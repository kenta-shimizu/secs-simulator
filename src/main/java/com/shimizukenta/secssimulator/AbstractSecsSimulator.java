package com.shimizukenta.secssimulator;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import com.shimizukenta.secs.BooleanProperty;
import com.shimizukenta.secs.PropertyChangeListener;
import com.shimizukenta.secs.ReadOnlyProperty;
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
import com.shimizukenta.secs.sml.SmlParseException;
import com.shimizukenta.secssimulator.extendsml.ExtendSmlMessageParser;
import com.shimizukenta.secssimulator.logging.AbstractLoggingEngine;
import com.shimizukenta.secssimulator.logging.LoggingEngine;
import com.shimizukenta.secssimulator.macro.AbstractMacroEngine;
import com.shimizukenta.secssimulator.macro.MacroEngine;
import com.shimizukenta.secssimulator.macro.MacroRecipe;

public abstract class AbstractSecsSimulator implements SecsSimulator {
	
	private final LoggingEngine loggingEngine = new AbstractLoggingEngine() {};
	private final MacroEngine macroEngine = new AbstractMacroEngine() {};
	
	private final AbstractSecsSimulatorConfig config;
	
	private SecsCommunicator secsComm;
	private TcpIpAdapter tcpipAdapter;
	
	public AbstractSecsSimulator(AbstractSecsSimulatorConfig config) {
		this.config = config;
		this.secsComm = null;
		this.tcpipAdapter = null;
		
		this.addSecsLogListener(this.loggingEngine::putLog);
	}
	
	@Override
	public boolean saveConfig(Path path) throws IOException {
		return config.save(path);
	}
	
	@Override
	public boolean loadConfig(Path path) throws IOException {
		return config.load(path);
	}
	
	private Optional<SecsCommunicator> getCommunicator() {
		synchronized ( this ) {
			return secsComm == null ? Optional.empty() : Optional.of(secsComm);
		}
	}
	
	/* state-changed-listener */
	private final BooleanProperty communicateState = BooleanProperty.newInstance(false);
	
	protected boolean addSecsCommunicatableStateChangeListener(SecsCommunicatableStateChangeListener lstnr) {
		return communicateState.addChangeListener(lstnr::changed);
	}
	
	protected boolean removeSecsCommunicatableStateChangeListener(SecsCommunicatableStateChangeListener lstnr) {
		return communicateState.removeChangeListener(lstnr::changed);
	}
	
	protected void waitUntilCommunicatable() throws InterruptedException {
		communicateState.waitUntilTrue();
	}
	
	
	/* receive-message-listener */
	private final Collection<SecsMessageReceiveListener> recvMsgListeners = new CopyOnWriteArrayList<>();
	
	protected boolean addSecsMessageReceiveListener(SecsMessageReceiveListener lstnr) {
		return recvMsgListeners.add(lstnr);
	}
	
	protected boolean removeSecsMessageReceiveListener(SecsMessageReceiveListener lstnr) {
		return recvMsgListeners.remove(lstnr);
	}
	
	
	/* log-listener */
	private final Collection<SecsLogListener> logListeners = new CopyOnWriteArrayList<>();
	
	protected boolean addSecsLogListener(SecsLogListener lstnr) {
		return logListeners.add(lstnr);
	}
	
	protected boolean removeSecsLogListener(SecsLogListener lstnr) {
		return logListeners.remove(lstnr);
	}
	
	protected void notifyLog(SecsLog log) {
		SecsLog simpleLog = toSimpleThrowableLog(log);
		logListeners.forEach(l -> {
			l.received(simpleLog);
		});
	}
	
	private static SecsLog toSimpleThrowableLog(SecsLog log) {
		Object o = log.value().orElse(null);
		if ((o != null) && (o instanceof Throwable)) {
			String s = o.getClass().getName();
			return new SecsLog(log.subject(), log.timestamp(), s);
		}
		return log;
	}
	
	
	private final Collection<SecsMessage> waitPrimaryMsgs = new ArrayList<>();
	
	@Override
	public SecsCommunicator openCommunicator() throws IOException {
		
		synchronized ( this ) {
			
			closeCommunicator();
			
			final SecsCommunicator comm = SecsCommunicatorBuilder.getInstance().build(config);
			
			comm.addSecsCommunicatableStateChangeListener(communicateState::set);
			
			comm.addSecsMessageReceiveListener(primaryMsg -> {
				
				int strm = primaryMsg.getStream();
				int func = primaryMsg.getFunction();
				
				if ( equalsDeviceId(primaryMsg) ) {
					
					
				}

				if ( config.autoReply().booleanValue() ) {
					
					final SmlMessage sml = config.smlAliasPairPool().optionalOnlyOneStreamFunction(strm, func).orElse(null);
					
					if ( sml != null ) {
						
						try {
							send(primaryMsg, sml);
						}
						catch (InterruptedException | SecsSimulatorException ignore) {
						}
						
						return;
					}
				}
				
				if ( config.autoReplySxF0().booleanValue() ) {
					
					final LocalSecsMessage reply = autoReplySxF0(primaryMsg).orElse(null);
					
					if ( reply != null ) {
						
						try {
							send(primaryMsg, reply);
						}
						catch (InterruptedException | SecsSimulatorException ignore) {
						}
					}
				}
				
				if ( config.autoReplyS9Fy().booleanValue() ) {
					
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
			
			comm.addSecsMessageReceiveListener(msg -> {
				this.recvMsgListeners.forEach(l -> {
					l.received(msg);
				});
			});
			
			comm.addSecsLogListener(this::notifyLog);
			
			if ( config.protocol().get() == SecsSimulatorProtocol.SECS1_ON_TCP_IP_RECEIVER ) {
				
				SocketAddress s = new InetSocketAddress("127.0.0.1", 0);
				tcpipAdapter = TcpIpAdapter.open(config.secs1AdapterSocketAddress().get(), s);
				
				SocketAddress bx = tcpipAdapter.socketAddressB();
				config.secs1OnTcpIpReceiverCommunicatorConfig().socketAddress(bx);
				
				tcpipAdapter.addThrowableListener((sock, t) -> {
					/* Nothing */
				});
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
					notifyLog(new SecsLog("Communicator closed"));
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
	public void quitApplication() throws IOException {
		
		IOException ioExcept = null;
		
		try {
			closeCommunicator();
		}
		catch (IOException e) {
			ioExcept = e;
		}
		
		try {
			loggingEngine.close();
		}
		catch ( IOException e ) {
			ioExcept = e;
		}
		
		try {
			macroEngine.close();
		}
		catch ( IOException e ) {
			ioExcept = e;
		}
		
		if (ioExcept != null) {
			throw ioExcept;
		}
	}

	@Override
	public void protocol(SecsSimulatorProtocol protocol) {
		this.config.protocol().set(protocol);
	}

	@Override
	public SecsSimulatorProtocol protocol() {
		return this.config.protocol().get();
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
			
			if ( config.autoReplyS9Fy().get() ) {
				
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
	
	protected SmlMessage parseSml(CharSequence sml) throws SmlParseException {
		return ExtendSmlMessageParser.getInstance().parse(sml);
	}
	
	@Override
	public boolean addSml(CharSequence alias, SmlMessage sml) {
		return config.smlAliasPairPool().add(alias, sml);
	}
	
	@Override
	public boolean removeSml(CharSequence alias) {
		return config.smlAliasPairPool().remove(alias);
	}
	
	protected boolean addSmlPairs(Collection<? extends SmlAliasPair> pairs) {
		return config.smlAliasPairPool().addAll(pairs);
	}
	
	protected List<String> smlAliases() {
		return config.smlAliasPairPool().aliases();
	}
	
	protected Optional<SmlMessage> optionalAlias(CharSequence alias) {
		return config.smlAliasPairPool().optionalAlias(alias);
	}
	
	protected boolean addSmlAliasesChangeListener(PropertyChangeListener<? super Collection<? extends SmlAliasPair>> l) {
		return config.smlAliasPairPool().addChangeListener(l);
	}
	
	protected boolean removeSmlAliasesChangeListener(PropertyChangeListener<? super Collection<? extends SmlAliasPair>> l) {
		return config.smlAliasPairPool().removeChangeListener(l);
	}
	
	private Optional<LocalSecsMessage> autoReplySxF0(SecsMessage primary) {
		
		int strm = primary.getStream();
		
		if ( strm < 0 ) {
			return Optional.empty();
		}
		
		if ( ! equalsDeviceId(primary) ) {
			return Optional.empty();
		}
		
		int func = primary.getFunction();
		boolean wbit = primary.wbit();
		
		if ( wbit ) {
			
			if ( ! config.smlAliasPairPool().hasReplyMessages(strm, func) ) {
				
				if ( config.smlAliasPairPool().hasReplyMessages(strm) ) {
					
					return Optional.of(new LocalSecsMessage(strm, 0, false, Secs2.empty()));
					
				} else {
					
					return Optional.of(new LocalSecsMessage(0, 0, false, Secs2.empty()));
				}
			}
		}
		
		return Optional.empty();
	}
	
	private Optional<LocalSecsMessage> autoReplyS9Fy(SecsMessage primary) {
		
		int strm = primary.getStream();
		
		if ( strm < 0 ) {
			return Optional.empty();
		}
		
		if ( ! equalsDeviceId(primary) ) {
			return Optional.of(new LocalSecsMessage(9, 1, false, Secs2.binary(primary.header10Bytes())));
		}
		
		int func = primary.getFunction();
		boolean wbit = primary.wbit();
		
		if ( wbit ) {
			
			if ( ! config.smlAliasPairPool().hasReplyMessages(strm, func) ) {
				
				if ( config.smlAliasPairPool().hasReplyMessages(strm) ) {
					
					return Optional.of(new LocalSecsMessage(9, 3, false, Secs2.binary(primary.header10Bytes())));
					
				} else {
					
					return Optional.of(new LocalSecsMessage(9, 5, false, Secs2.binary(primary.header10Bytes())));
				}
			}
		}
		
		return Optional.empty();
	}
	
	private boolean equalsDeviceId(SecsMessage msg) {
		return getCommunicator().filter(comm -> comm.deviceId() == msg.deviceId()).isPresent();
	}
	
	
	/* Logging */
	@Override
	public Optional<Path> startLogging(Path path) throws IOException, InterruptedException {
		return this.loggingEngine.start(path);
	}

	@Override
	public Optional<Path> stopLogging() throws IOException, InterruptedException {
		return this.loggingEngine.stop();
	}
	
	protected ReadOnlyProperty<Path> loggingProperty() {
		return this.loggingEngine.loggingProperty();
	}
	
	
	/* Macros */
	@Override
	public Optional<MacroRecipe> startMacro(MacroRecipe recipe) throws InterruptedException {
		return this.macroEngine.start(recipe);
	}
	
	@Override
	public Optional<MacroRecipe> stopMacro() throws InterruptedException {
		return this.macroEngine.stop();
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
