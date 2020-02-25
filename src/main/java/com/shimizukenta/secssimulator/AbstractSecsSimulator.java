package com.shimizukenta.secssimulator;

import java.io.BufferedWriter;
import java.io.IOException;
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
import com.shimizukenta.secs.SecsLogListener;
import com.shimizukenta.secs.SecsMessage;
import com.shimizukenta.secs.SecsMessageReceiveListener;
import com.shimizukenta.secs.hsmsss.HsmsSsCommunicator;
import com.shimizukenta.secs.sml.SmlMessage;

public abstract class AbstractSecsSimulator implements SecsSimulator {

	private final AbstractSecsSimulatorConfig config;
	private SecsCommunicator secsComm;
	private Thread thLogging;
	private Thread thMacro;
	
	public AbstractSecsSimulator(AbstractSecsSimulatorConfig config) {
		this.config = config;
		this.secsComm = null;
		this.thLogging = null;
		this.thMacro = null;
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
				
				if ( primaryMsg.wbit() ) {
					synchronized ( waitPrimaryMsgs ) {
						waitPrimaryMsgs.add(primaryMsg);
					}
				}
			});
			
			recvMsgListeners.forEach(comm::addSecsMessageReceiveListener);
			commStateChangedListenrs.forEach(comm::addSecsCommunicatableStateChangeListener);
			logListeners.forEach(comm::addSecsLogListener);
			
			comm.open();
			
			this.secsComm = comm;
			
			return comm;
		}
	}
	
	@Override
	public void closeCommunicator() throws IOException {
		synchronized ( this ) {
			if ( secsComm != null ) {
				try {
					secsComm.close();
				}
				finally {
					secsComm = null;
				}
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
		
		if ( ! primaryWbit ) {
			return Optional.empty();
		}
		
		List<SmlMessage> ss = smls.stream()
				.map(x -> x.sm)
				.filter(x -> ! x.wbit())
				.filter(s -> s.getStream() == primaryStream)
				.filter(x -> x.getFunction() == primaryFunction + 1)
				.filter(x -> (x.getFunction() % 2) == 0)
				.collect(Collectors.toList());
		
		return (ss.size() == 1) ? Optional.of(ss.get(0)) : Optional.empty();
	}
	
	
	private final BlockingQueue<Object> logQueue = new LinkedBlockingQueue<>();
	
	@Override
	public void startLogging(Path path) throws IOException {
		synchronized ( logQueue ) {
			stopLogging();
			
			thLogging = new Thread(() -> {
				
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
					catch ( IOException e ) {
						putLog(e);
					}
				}
				catch ( InterruptedException ignore ) {
				}
			});
			
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
			
			thMacro = new Thread(() -> {
				
				//TODO
				//macro
			});
			
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
	
}
