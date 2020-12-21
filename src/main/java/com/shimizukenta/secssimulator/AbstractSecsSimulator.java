package com.shimizukenta.secssimulator;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import com.shimizukenta.secs.BooleanProperty;
import com.shimizukenta.secs.PropertyChangeListener;
import com.shimizukenta.secs.ReadOnlyProperty;
import com.shimizukenta.secs.SecsCommunicatableStateChangeListener;
import com.shimizukenta.secs.SecsCommunicator;
import com.shimizukenta.secs.SecsException;
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
import com.shimizukenta.secssimulator.macro.MacroRecipeParseException;
import com.shimizukenta.secssimulator.macro.MacroWorker;

public abstract class AbstractSecsSimulator implements SecsSimulator {
	
	private final LoggingEngine loggingEngine;
	private final MacroEngine macroEngine;
	
	private final AbstractSecsSimulatorConfig config;
	
	private SecsCommunicator secsComm;
	private TcpIpAdapter tcpipAdapter;
	
	public AbstractSecsSimulator(AbstractSecsSimulatorConfig config) {
		this.config = config;
		this.secsComm = null;
		this.tcpipAdapter = null;
		
		this.loggingEngine = createLoggingEngine();
		this.macroEngine = createMacroEngine();
		
		this.addLogListener(this.loggingEngine::putLog);
	}
	
	/**
	 * Prototype pattern, LoggingEngine builder.
	 * 
	 * @return LoggingEngine
	 */
	protected LoggingEngine createLoggingEngine() {
		return new AbstractLoggingEngine() {};
	}
	
	/**
	 * Prototype pattern, MacroEngine builder.
	 * 
	 * @return MacroEngine
	 */
	protected MacroEngine createMacroEngine() {
		return new AbstractMacroEngine(this) {};
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
	
	public boolean addSecsCommunicatableStateChangeListener(SecsCommunicatableStateChangeListener lstnr) {
		return communicateState.addChangeListener(lstnr::changed);
	}
	
	public boolean removeSecsCommunicatableStateChangeListener(SecsCommunicatableStateChangeListener lstnr) {
		return communicateState.removeChangeListener(lstnr::changed);
	}
	
	@Override
	public void waitUntilCommunicatable() throws InterruptedException {
		communicateState.waitUntilTrue();
	}
	
	/* receive-message-listener */
	private final Collection<SecsMessageReceiveListener> recvMsgListeners = new CopyOnWriteArrayList<>();
	
	public boolean addSecsMessageReceiveListener(SecsMessageReceiveListener lstnr) {
		return recvMsgListeners.add(lstnr);
	}
	
	public boolean removeSecsMessageReceiveListener(SecsMessageReceiveListener lstnr) {
		return recvMsgListeners.remove(lstnr);
	}
	
	
	/* log-listener */
	private final Collection<SecsSimulatorLogListener> logListeners = new CopyOnWriteArrayList<>();
	
	protected boolean addLogListener(SecsSimulatorLogListener lstnr) {
		return logListeners.add(lstnr);
	}
	
	protected boolean removeLogListener(SecsSimulatorLogListener lstnr) {
		return logListeners.remove(lstnr);
	}
	
	protected void notifyLog(SecsSimulatorLog log) {
		logListeners.forEach(l -> {l.received(log);});
	}
	
	private final Collection<SecsMessage> waitPrimaryMsgs = new ArrayList<>();
	
	@Override
	public SecsCommunicator openCommunicator() throws IOException {
		
		synchronized ( this ) {
			
			closeCommunicator();
			
			synchronized ( waitPrimaryMsgs ) {
				waitPrimaryMsgs.clear();
			}
			
			final SecsCommunicator comm = SecsCommunicatorBuilder.getInstance().build(config);
			
			comm.addSecsCommunicatableStateChangeListener(communicateState::set);
			
			comm.addSecsMessageReceiveListener(primaryMsg -> {
				try {
					this.receivePrimaryMsg(primaryMsg);
				}
				catch ( InterruptedException ignore ) {
				}
			});
			
			comm.addSecsMessageReceiveListener(msg -> {
				this.recvMsgListeners.forEach(l -> {
					l.received(msg);
				});
			});
			
			comm.addSecsLogListener(log -> {
				notifyLog(SecsSimulatorLog.from(log));
			});
			
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
					notifyLog(new SecsSimulatorLog("Communicator closed"));
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
			return getCommunicator()
					.orElseThrow(SecsSimulatorNotOpenException::new)
					.send(sml);
		}
		catch ( SecsWaitReplyMessageException e ) {
			
			if ( config.autoReplyS9Fy().booleanValue() ) {
				
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
			return getCommunicator()
					.orElseThrow(SecsSimulatorNotOpenException::new)
					.send(primaryMsg, replySml);
		}
		catch ( SecsException e ) {
			throw new SecsSimulatorSendException(e);
		}
	}
	
	private Optional<SecsMessage> send(LocalSecsMessage msg) throws SecsSimulatorException, InterruptedException {
		
		try {
			return getCommunicator()
					.orElseThrow(SecsSimulatorNotOpenException::new)
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
			return getCommunicator()
					.orElseThrow(SecsSimulatorNotOpenException::new)
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
	

	public Set<String> addSml(Path path) throws SmlParseException, IOException {
		
		Set<SmlAliasPair> pairs = new HashSet<>();
		
		if ( ! Files.exists(path) ) {
			return Collections.emptySet();
		}
			
		if ( Files.isDirectory(path) ) {
			
			try (
					DirectoryStream<Path> smlPaths = Files.newDirectoryStream(path, "*.sml");
					) {
				
				for ( Path smlPath : smlPaths ) {
					
					if ( ! Files.isDirectory(smlPath) ) {
						
						pairs.add(SmlAliasPair.fromFile(smlPath));
					}
				}
			}
			
		} else {
			
			pairs.add(SmlAliasPair.fromFile(path));
		}
		
		boolean f = config.smlAliasPairPool().addAll(pairs);
		
		if ( f  ) {
			
			return pairs.stream().map(p -> p.alias()).collect(Collectors.toSet());
			
		} else {
			
			return Collections.emptySet();
		}
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
	
	public Optional<SmlMessage> optionalSmlAlias(CharSequence alias) {
		return config.smlAliasPairPool().optionalAlias(alias);
	}
	
	protected boolean addSmlAliasesChangeListener(PropertyChangeListener<? super Collection<? extends SmlAliasPair>> l) {
		return config.smlAliasPairPool().addChangeListener(l);
	}
	
	protected boolean removeSmlAliasesChangeListener(PropertyChangeListener<? super Collection<? extends SmlAliasPair>> l) {
		return config.smlAliasPairPool().removeChangeListener(l);
	}
	
	
	private boolean equalsDeviceId(SecsMessage msg) {
		return getCommunicator()
				.filter(comm -> comm.deviceId() == msg.deviceId())
				.isPresent();
	}
	
	private void receivePrimaryMsg(SecsMessage primaryMsg) throws InterruptedException {
		
		{
			/*** check S9F1 ***/
			
			LocalSecsMessage reply = autoReplyS9F1(primaryMsg).orElse(null);
			
			if ( reply != null ) {
				try {
					send(primaryMsg, reply);
				}
				catch (SecsSimulatorException ignore) {
				}
				
				return;
			}
		}
		
		{
			/*** Auto-reply ***/
			
			final SmlMessage reply = autoReply(primaryMsg).orElse(null);
				
			if ( reply != null ) {
				try {
					send(primaryMsg, reply);
				}
				catch (SecsSimulatorException ignore) {
				}
				
				return;
			}
		}
		
		boolean alreadyReply = false;
		
		{
			/*** Auto-reply SxF0 or S9F5 ***/
			
			final LocalSecsMessage reply = autoReplySxF0(primaryMsg).orElse(null);
			
			if ( reply != null ) {
				
				try {
					send(primaryMsg, reply);
				}
				catch (SecsSimulatorException ignore) {
				}
				
				alreadyReply = true;
			}
		}
		
		{
			/*** Auto-reply S9F3 or S9F5 ***/
			
			final LocalSecsMessage s9fy = autoReplyS9F3or5(primaryMsg).orElse(null);
			
			if ( s9fy != null ) {
				
				try {
					send(s9fy);
				}
				catch (SecsSimulatorException ignore) {
				}
			}
		}
		
		if (
				! alreadyReply
				&& primaryMsg.wbit()
				&& ((primaryMsg.getFunction() % 2) == 0)
				) {
			
			synchronized ( waitPrimaryMsgs ) {
				waitPrimaryMsgs.add(primaryMsg);
			}
		}
	}
	
	private Optional<SmlMessage> autoReply(SecsMessage primary) {
		
		if ( config.autoReply().booleanValue() ) {
			
			int strm = primary.getStream();
			int func = primary.getFunction();
			
			if (
					primary.wbit()
					&& ((func % 2) == 1)
					&& strm >= 0 && strm <= 127
					&& func >= 0 && func <= 255
					) {
				
			}
			return config.smlAliasPairPool()
					.optionalOnlyOneStreamFunction(strm, func + 1);
		}
		
		return Optional.empty();
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
		
		if ( primary.wbit() ) {
			
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
	
	private Optional<LocalSecsMessage> autoReplyS9F1(SecsMessage primary) {
		
		if ( config.autoReplyS9Fy().booleanValue() ) {
			
			if ( primary.getStream() >= 0 ) {
				
				if ( ! equalsDeviceId(primary) ) {
					return Optional.of(new LocalSecsMessage(9, 1, false, Secs2.binary(primary.header10Bytes())));
				}
			}
		}
		return Optional.empty();
	}
	
	private Optional<LocalSecsMessage> autoReplyS9F3or5(SecsMessage primary) {
		
		if ( config.autoReplyS9Fy().booleanValue() ) {
			
			int strm = primary.getStream();
			int func = primary.getFunction();
			
			if ( strm >= 0 ) {
				
				if ( ! config.smlAliasPairPool().hasReplyMessages(strm, func) ) {
					
					if ( config.smlAliasPairPool().hasReplyMessages(strm) ) {
						
						return Optional.of(new LocalSecsMessage(9, 5, false, Secs2.binary(primary.header10Bytes())));
						
					} else {
						
						return Optional.of(new LocalSecsMessage(9, 3, false, Secs2.binary(primary.header10Bytes())));
					}
				}
			}
		}
		
		return Optional.empty();
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
	public Optional<MacroWorker> startMacro(MacroRecipe recipe) throws InterruptedException {
		return this.macroEngine.start(recipe);
	}
	
	@Override
	public Optional<MacroWorker> stopMacro(MacroWorker worker) throws InterruptedException {
		return this.macroEngine.stop(worker);
	}
	
	@Override
	public Optional<MacroWorker> stopMacro(int workerId) throws InterruptedException {
		return this.macroEngine.stop(workerId);
	}
	
	@Override
	public List<MacroWorker>  stopMacro() throws InterruptedException {
		return this.macroEngine.stop();
	}
	
	@Override
	public List<String> macroRecipeAliases() {
		return this.config.macroRecipePairPool().aliases();
	}
	
	public Set<MacroRecipe> addMacroRecipe(Path path) throws MacroRecipeParseException , IOException {
		
		Set<MacroRecipePair> pairs = new HashSet<>();
		
		if ( ! Files.exists(path) ) {
			return Collections.emptySet();
		}
			
		if ( Files.isDirectory(path) ) {
			
			try (
					DirectoryStream<Path> mrPaths = Files.newDirectoryStream(path, "*.json");
					) {
				
				for ( Path mrPath : mrPaths ) {
					
					if ( ! Files.isDirectory(mrPath) ) {
						
						pairs.add(MacroRecipePair.fromFile(mrPath));
					}
				}
			}
			
		} else {
			
			pairs.add(MacroRecipePair.fromFile(path));
		}
		
		boolean f = config.macroRecipePairPool().addAll(pairs);
		
		if ( f  ) {
			
			return pairs.stream().map(p -> p.recipe()).collect(Collectors.toSet());
			
		} else {
			
			return Collections.emptySet();
		}
	}
	
	@Override
	public Optional<MacroRecipe> addMacroRecipe(MacroRecipe r) {
		MacroRecipePair pair = new MacroRecipePair(r, null);
		boolean f = config.macroRecipePairPool().add(pair);
		return f ? Optional.of(r) : Optional.empty();
	}
	
	@Override
	public Optional<MacroRecipe> removeMacroRecipe(CharSequence macroRecipeAlias) {
		MacroRecipe r = config.macroRecipePairPool().optionalAlias(macroRecipeAlias).orElse(null);
		if ( r != null ) {
			if ( config.macroRecipePairPool().remove(macroRecipeAlias) ) {
				return Optional.of(r);
			}
		}
		return Optional.empty();
	}
	
	protected Optional<MacroRecipe> optionalMacroRecipeAlias(CharSequence alias) {
		return this.config.macroRecipePairPool().optionalAlias(alias);
	}
	
	protected boolean addMacroRecipeChangeListener(PropertyChangeListener<? super Collection<? extends MacroRecipePair>> l) {
		return this.config.macroRecipePairPool().addChangeListener(l);
	}
	
	protected boolean removeMacroRecipeChangeListener(PropertyChangeListener<? super Collection<? extends MacroRecipePair>> l) {
		return this.config.macroRecipePairPool().removeChangeListener(l);
	}
	
	protected boolean addMacroWorkerStateChangeListener(PropertyChangeListener<? super MacroWorker> l) {
		return this.macroEngine.addStateChangeListener(l);
	}
	
	protected boolean removeMacroWorkerStateChangeListener(PropertyChangeListener<? super MacroWorker> l) {
		return this.macroEngine.removeStateChangeListener(l);
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
