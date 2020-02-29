package com.shimizukenta.secssimulator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import com.shimizukenta.secs.SecsCommunicatableStateChangeListener;
import com.shimizukenta.secs.SecsCommunicator;
import com.shimizukenta.secs.SecsException;
import com.shimizukenta.secs.SecsLog;
import com.shimizukenta.secs.SecsLogListener;
import com.shimizukenta.secs.SecsMessage;
import com.shimizukenta.secs.SecsMessageReceiveListener;
import com.shimizukenta.secs.SecsWaitReplyMessageException;
import com.shimizukenta.secs.hsmsss.HsmsSsCommunicator;
import com.shimizukenta.secs.secs2.Secs2;
import com.shimizukenta.secs.sml.SmlMessage;
import com.shimizukenta.secssimulator.macro.MacroExecutor;
import com.shimizukenta.secssimulator.macro.MacroFileReader;
import com.shimizukenta.secssimulator.macro.MacroReport;
import com.shimizukenta.secssimulator.macro.MacroReportListener;
import com.shimizukenta.secssimulator.macro.MacroRequest;

public abstract class AbstractSecsSimulator implements SecsSimulator {

	private final AbstractSecsSimulatorConfig config;
	private SecsCommunicator secsComm;
	private TcpIpAdapter tcpipAdapter;
	private Thread thLogging;
	private Thread thMacro;
	private final MacroExecutor macroExecutor;
	
	public AbstractSecsSimulator(AbstractSecsSimulatorConfig config) {
		this.config = config;
		this.secsComm = null;
		this.tcpipAdapter = null;
		this.thLogging = null;
		this.thMacro = null;
		
		this.macroExecutor = new MacroExecutor(this);
		this.addSecsMessageReceiveListener(this.macroExecutor::receive);
	}
	
	private Optional<SecsCommunicator> getCommunicator() {
		synchronized ( this ) {
			return secsComm == null ? Optional.empty() : Optional.of(secsComm);
		}
	}
	
	
	/* state-changed-listener */
	private final Collection<SecsCommunicatableStateChangeListener> commStateChangedListenrs = new CopyOnWriteArrayList<>();
	
	protected boolean addSecsCommunicatableStateChangeListener(SecsCommunicatableStateChangeListener lstnr) {
		boolean f = commStateChangedListenrs.add(lstnr);
		getCommunicator().ifPresent(comm -> {comm.addSecsCommunicatableStateChangeListener(lstnr);});
		return f;
	}
	
	protected boolean removeSecsCommunicatableStateChangeListener(SecsCommunicatableStateChangeListener lstnr) {
		boolean f = commStateChangedListenrs.remove(lstnr);
		getCommunicator().ifPresent(comm -> {comm.removeSecsCommunicatableStateChangeListener(lstnr);});
		return f;
	}
	
	
	/* receive-message-listener */
	private final Collection<SecsMessageReceiveListener> recvMsgListeners = new CopyOnWriteArrayList<>();
	
	protected boolean addSecsMessageReceiveListener(SecsMessageReceiveListener lstnr) {
		boolean f = recvMsgListeners.add(lstnr);
		getCommunicator().ifPresent(comm -> {comm.addSecsMessageReceiveListener(lstnr);});
		return f;
	}
	
	protected boolean removeSecsMessageReceiveListener(SecsMessageReceiveListener lstnr) {
		boolean f = recvMsgListeners.remove(lstnr);
		getCommunicator().ifPresent(comm -> {comm.removeSecsMessageReceiveListener(lstnr);});
		return f;
	}
	
	
	/* log-listener */
	private final Collection<SecsLogListener> logListeners = new CopyOnWriteArrayList<>();
	
	protected boolean addSecsLogListener(SecsLogListener lstnr) {
		boolean f = logListeners.add(lstnr);
		getCommunicator().ifPresent(comm -> {comm.addSecsLogListener(lstnr);});
		return f;
	}
	
	protected boolean removeSecsLogListener(SecsLogListener lstnr) {
		boolean f = logListeners.remove(lstnr);
		getCommunicator().ifPresent(comm -> {comm.removeSecsLogListener(lstnr);});
		return f;
	}
	
	
	private final Collection<SecsMessage> waitPrimaryMsgs = new ArrayList<>();
	
	@Override
	public SecsCommunicator openCommunicator() throws IOException {
		
		synchronized ( this ) {
			
			closeCommunicator();
			
			final SecsCommunicator comm = SecsCommunicatorBuilder.getInstance().build(config);
			
			comm.addSecsMessageReceiveListener(primaryMsg -> {
				
				if ( config.autoReply() ) {
					
					final SmlMessage sml = autoReply(primaryMsg).orElse(null);
					
					if ( sml != null ) {
						
						try {
							send(primaryMsg, sml);
						}
						catch (InterruptedException | SecsSimulatorException ignore) {
						}
						
						return;
					}
				}
				
				if (config.autoReplySxF0()) {
					
					final LocalSecsMessage reply = autoReplySxF0(primaryMsg).orElse(null);
					
					if ( reply != null ) {
						
						try {
							send(primaryMsg, reply);
						}
						catch (InterruptedException | SecsSimulatorException ignore) {
						}
					}
				}
				
				if (config.autoReplyS9Fy()) {
					
					final LocalSecsMessage s9fy = autoReplyS9Fy(primaryMsg).orElse(null);
					
					if ( s9fy != null ) {
						
						try {
							send(s9fy);
						}
						catch (InterruptedException | SecsSimulatorException ignore) {
						}
					}
				}
				
				if ( primaryMsg.wbit() ) {
					synchronized ( waitPrimaryMsgs ) {
						waitPrimaryMsgs.add(primaryMsg);
					}
				}
			});
			
			recvMsgListeners.forEach(comm::addSecsMessageReceiveListener);
			commStateChangedListenrs.forEach(comm::addSecsCommunicatableStateChangeListener);
			logListeners.forEach(comm::addSecsLogListener);
			
			if ( config.protocol() == SecsSimulatorProtocol.SECS1_ON_TCP_IP_RECEIVER ) {
				
				SocketAddress s = new InetSocketAddress("127.0.0.1", 0);
				tcpipAdapter = TcpIpAdapter.open(config.secs1AdapterSocketAddress(), s);
				
				SocketAddress bx = tcpipAdapter.socketAddressB();
				config.secs1OnTcpIpReceiverCommunicatorConfig().socketAddress(bx);
			}
			
			comm.open();
			
			this.secsComm = comm;
			
			return comm;
		}
	}
	
	@Override
	public void closeCommunicator() throws IOException {
		
		synchronized ( this ) {
			
			IOException ioExcept = null;
			
			if ( secsComm != null ) {
				try {
					secsComm.close();
				}
				catch (IOException e) {
					ioExcept = e;
				}
				finally {
					secsComm = null;
				}
			}
			
			if ( tcpipAdapter != null ) {
				try {
					tcpipAdapter.close();
				}
				catch ( IOException e ) {
					ioExcept = e;
				}
				finally {
					tcpipAdapter = null;
				}
			}
			
			if ( ioExcept != null ) {
				throw ioExcept;
			}
		}
	}

	@Override
	public void quitApplication() {
		try {
			closeCommunicator();
			stopLogging();
			stopMacro();
		}
		catch (IOException giveup) {
		}
	}

	@Override
	public void protocol(SecsSimulatorProtocol protocol) {
		this.config.protocol(protocol);
	}

	@Override
	public SecsSimulatorProtocol protocol() {
		return this.config.protocol();
	}
	
	private Optional<SecsMessage> waitPrimaryMessage(SmlMessage sml) {
		
		synchronized ( waitPrimaryMsgs ) {
			
			int strm = sml.getStream();
			int func = sml.getFunction();
			
			for ( SecsMessage m : waitPrimaryMsgs ) {
				
				if ((m.getStream() == strm) && ((m.getFunction() + 1) == func)) {
					
					waitPrimaryMsgs.remove(m);
					
					return Optional.of(m);
				}
			}
			
			return Optional.empty();
		}
	}
	
	@Override
	public Optional<SecsMessage> send(SmlMessage sml) throws SecsSimulatorException, InterruptedException {
		
		SecsMessage primaryMsg = waitPrimaryMessage(sml).orElse(null);
		
		if (primaryMsg != null) {
			return send(primaryMsg, sml);
		}
		
		try {
			return getCommunicator().orElseThrow(SecsSimulatorNotOpenException::new).send(sml);
		}
		catch ( SecsWaitReplyMessageException e ) {
			
			if ( config.autoReplyS9Fy() ) {
				
				SecsMessage m = e.secsMessage().orElse(null);
				
				if ( m != null ) {
					
					try {
						send(new LocalSecsMessage(9, 9, false, Secs2.binary(m.header10Bytes())));
					}
					catch (SecsSimulatorException giveup) {
					}
				}
			}
			
			throw new SecsSimulatorWaitReplyException(e);
		}
		catch ( SecsException e ) {
			throw new SecsSimulatorSendException(e);
		}
	}

	@Override
	public Optional<SecsMessage> send(SecsMessage primaryMsg, SmlMessage replySml) throws SecsSimulatorException, InterruptedException {
		try {
			return getCommunicator().orElseThrow(SecsSimulatorNotOpenException::new).send(primaryMsg, replySml);
		}
		catch ( SecsException e ) {
			throw new SecsSimulatorSendException(e);
		}
	}
	
	private Optional<SecsMessage> send(LocalSecsMessage msg) throws SecsSimulatorException, InterruptedException {
		
		try {
			return getCommunicator().orElseThrow(SecsSimulatorNotOpenException::new)
					.send(msg.strm
							, msg.func
							, msg.wbit
							, msg.secs2);
		}
		catch ( SecsWaitReplyMessageException e ) {
			throw new SecsSimulatorWaitReplyException(e);
		}
		catch ( SecsException e ) {
			throw new SecsSimulatorSendException(e);
		}
	}
	
	private Optional<SecsMessage> send(SecsMessage primaryMsg, LocalSecsMessage reply) throws SecsSimulatorException, InterruptedException {
		
		try {
			return getCommunicator().orElseThrow(SecsSimulatorNotOpenException::new)
					.send(primaryMsg
							, reply.strm
							, reply.func
							, reply.wbit
							, reply.secs2);
		}
		catch ( SecsException e ) {
			throw new SecsSimulatorSendException(e);
		}
	}
	
	@Override
	public boolean linktest() throws InterruptedException {
		SecsCommunicator comm = getCommunicator().orElse(null);
		if ((comm != null) && (comm instanceof HsmsSsCommunicator)) {
			return ((HsmsSsCommunicator)comm).linktest();
		}
		return false;
	}
	
	
	private class SmlAlias {
		
		private final String alias;
		private final SmlMessage sm;
		
		private SmlAlias(CharSequence alias, SmlMessage sm) {
			this.alias = Objects.requireNonNull(alias).toString();
			this.sm = Objects.requireNonNull(sm);
		}
		
		@Override
		public int hashCode() {
			return alias.hashCode();
		}
		
		@Override
		public boolean equals(Object o) {
			if ((o != null) && (o instanceof SmlAlias)) {
				return ((SmlAlias)o).alias.equals(alias);
			} else {
				return false;
			}
		}
	}
	
	private final Set<SmlAlias> smls = new CopyOnWriteArraySet<>();
	
	@Override
	public Set<String> smlAliases() {
		return smls.stream().map(x -> x.alias).collect(Collectors.toSet());
	}
	
	@Override
	public Optional<SmlMessage> sml(CharSequence alias) {
		if ( alias == null ) {
			return Optional.empty();
		} else {
			String s = alias.toString();
			return smls.stream().filter(x -> x.alias.equals(s)).map(x -> x.sm).findFirst();
		}
	}
	
	@Override
	public boolean addSml(CharSequence alias, SmlMessage sml) {
		return smls.add(new SmlAlias(alias, sml));
	}

	@Override
	public boolean removeSml(CharSequence alias) {
		return smls.removeIf(s -> alias.toString().equals(s.alias));
	}
	
	private Optional<SmlMessage> autoReply(SecsMessage primaryMsg) {
		return autoReply(primaryMsg.getStream(), primaryMsg.getFunction(), primaryMsg.wbit());
	}
	
	private Optional<SmlMessage> autoReply(int primaryStream, int primaryFunction, boolean primaryWbit) {
		
		if ( primaryWbit ) {
			
			List<SmlMessage> ss = autoReplys(primaryStream, primaryFunction);
			return (ss.size() == 1) ? Optional.of(ss.get(0)) : Optional.empty();
			
		} else {
			
			return Optional.empty();
		}
	}
	
	private List<SmlMessage> autoReplys(int primaryStream, int primaryFunction) {
		
		return smls.stream()
				.map(x -> x.sm)
				.filter(x -> ! x.wbit())
				.filter(s -> s.getStream() == primaryStream)
				.filter(x -> x.getFunction() == primaryFunction + 1)
				.filter(x -> (x.getFunction() % 2) == 0)
				.collect(Collectors.toList());
	}
	
	private List<SmlMessage> autoReplys(int primaryStream) {
		
		return smls.stream()
				.map(x -> x.sm)
				.filter(x -> ! x.wbit())
				.filter(s -> s.getStream() == primaryStream)
				.filter(x -> (x.getFunction() % 2) == 0)
				.collect(Collectors.toList());
	}
	
	private Optional<LocalSecsMessage> autoReplySxF0(SecsMessage primary) {
		
		if ( primary.getStream() < 0 ) {
			return Optional.empty();
		}
		
		if ( ! equalsDeviceId(primary) ) {
			return Optional.empty();
		}
		
		int strm = primary.getStream();
		int func = primary.getFunction();
		boolean wbit = primary.wbit();
		
		if ( wbit ) {
			
			if ( autoReplys(strm, func).isEmpty() ) {
				
				if ( autoReplys(strm).isEmpty() ) {
					return Optional.of(new LocalSecsMessage(0, 0, false, Secs2.empty()));
				}
				
				return Optional.of(new LocalSecsMessage(strm, 0, false, Secs2.empty()));
			}
		}
		
		return Optional.empty();
	}
	
	private Optional<LocalSecsMessage> autoReplyS9Fy(SecsMessage primary) {
		
		if ( primary.getStream() < 0 ) {
			return Optional.empty();
		}
		
		if ( ! equalsDeviceId(primary) ) {
			return Optional.of(new LocalSecsMessage(9, 1, false, Secs2.binary(primary.header10Bytes())));
		}
		
		int strm = primary.getStream();
		int func = primary.getFunction();
		boolean wbit = primary.wbit();
		
		if ( wbit ) {
			
			if ( autoReplys(strm, func).isEmpty() ) {
				
				if ( autoReplys(strm).isEmpty() ) {
					return Optional.of(new LocalSecsMessage(9, 3, false, Secs2.binary(primary.header10Bytes())));
				}
				
				return Optional.of(new LocalSecsMessage(9, 5, false, Secs2.binary(primary.header10Bytes())));
			}
		}
		
		return Optional.empty();
	}
	
	private boolean equalsDeviceId(SecsMessage msg) {
		return getCommunicator().filter(comm -> comm.deviceId() == msg.deviceId()).isPresent();
	}
	
	
	private static final Object commRefObj = new Object();
	
	protected SecsLog toSimpleThrowableLog(SecsLog log) {
		
		Object o = log.value().orElse(commRefObj);
		
		if (o instanceof Throwable) {
			String s = o.getClass().getName();
			return new SecsLog(log.subject(), log.timestamp(), s);
		}
		
		return log;
	}
	
	protected final BlockingQueue<Object> logQueue = new LinkedBlockingQueue<>();
	
	@Override
	public void startLogging(Path path) throws IOException {
		synchronized ( logQueue ) {
			stopLogging();
			thLogging = new Thread(loggingTask(path));
			thLogging.start();
		}
	}

	@Override
	public void stopLogging() {
		synchronized ( logQueue ) {
			if ( thLogging != null ) {
				thLogging.interrupt();
				logQueue.clear();
				thLogging = null;
			}
		}
	}
	
	protected Runnable loggingTask(Path path) {
		
		return new Runnable() {
			
			@Override
			public void run() {
				
				try {
					
					try (
							BufferedWriter bw = Files.newBufferedWriter(
									path
									, StandardCharsets.UTF_8
									, StandardOpenOption.WRITE
									, StandardOpenOption.CREATE
									, StandardOpenOption.APPEND);
							) {
						
						for ( ;; ) {
							
							Object o = logQueue.take();
							
							bw.write(o.toString());
							bw.newLine();
							bw.newLine();
							
							bw.flush();
						}
					}
					catch ( IOException giveup ) {
					}
				}
				catch ( InterruptedException ignore ) {
				}
			}
		};
	}
	
	protected void putLog(Object o) throws InterruptedException {
		synchronized ( logQueue ) {
			if ( thLogging != null ) {
				logQueue.put(o);
			}
		}
	}
	
	
	/* Macros */
	@Override
	public void startMacro(Path path) {
		synchronized ( this ) {
			stopMacro();
			thMacro = new Thread(macroTask(path));
			thMacro.start();
		}
	}
	
	@Override
	public void stopMacro() {
		synchronized ( this ) {
			if ( thMacro != null ) {
				thMacro.interrupt();
				thMacro = null;
			}
		}
	}
	
	protected Runnable macroTask(Path path) {
		
		return new Runnable() {
			
			@Override
			public void run() {
				
				try {
					
					List<MacroRequest> requests = MacroFileReader.getInstance().lines(path);
					
					for ( MacroRequest r : requests ) {
						
						if ( r.command() != null ) {
							macroReport(MacroReport.requestStarted(path, r));
							macroExecutor.execute(r);
							macroReport(MacroReport.requestFinished(path, r));
						}
					}
					
					macroReport(MacroReport.completed(path));
				}
				catch ( InterruptedException e ) {
					macroReport(MacroReport.interrupted(path, e));
				}
				catch (RuntimeException | Error e) {
					macroReport(MacroReport.failed(path, e));
					throw e;
				}
				catch (Exception e) {
					macroReport(MacroReport.failed(path, e));
				}
			}
		};
	}
	
	private final Collection<MacroReportListener> macroReportListeners = new CopyOnWriteArrayList<>();
	
	public boolean addMacroReportListener(MacroReportListener l) {
		return macroReportListeners.add(l);
	}
	
	public boolean removeMacroReportListener(MacroReportListener l) {
		return macroReportListeners.remove(l);
	}
	
	protected void macroReport(MacroReport r) {
		macroReportListeners.forEach(l -> {l.report(r);});
	}
	
	private class LocalSecsMessage {
		
		public int strm;
		public int func;
		public boolean wbit;
		public Secs2 secs2;
		
		public LocalSecsMessage(int strm, int func, boolean wbit, Secs2 secs2) {
			this.strm = strm;
			this.func = func;
			this.wbit = wbit;
			this.secs2 = secs2;
		}
	}
	
}
