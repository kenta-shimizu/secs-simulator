package com.shimizukenta.secssimulator;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import com.shimizukenta.jsonhub.JsonHub;
import com.shimizukenta.jsonhub.JsonHubBuilder;
import com.shimizukenta.jsonhub.JsonHubParseException;
import com.shimizukenta.jsonhub.JsonObjectPair;
import com.shimizukenta.secs.BooleanProperty;
import com.shimizukenta.secs.Property;
import com.shimizukenta.secs.ReadOnlyTimeProperty;
import com.shimizukenta.secs.SecsTimeout;
import com.shimizukenta.secs.hsmsss.HsmsSsCommunicatorConfig;
import com.shimizukenta.secs.hsmsss.HsmsSsProtocol;
import com.shimizukenta.secs.secs1ontcpip.Secs1OnTcpIpCommunicatorConfig;
import com.shimizukenta.secs.sml.SmlParseException;
import com.shimizukenta.secssimulator.macro.MacroRecipeParseException;

public abstract class AbstractSecsSimulatorConfig implements Serializable {
	
	private static final long serialVersionUID = 7451456701694504127L;
	
	private static final SecsSimulatorProtocol defaultSecsSimulatorProtocol = SecsSimulatorProtocol.HSMS_SS_PASSIVE;
	private static final HsmsSsCommunicatorConfig defautlHsmsSsCommunicatorConfig = new HsmsSsCommunicatorConfig();
	private static final Secs1OnTcpIpCommunicatorConfig defaultSecs1OnTcpIpCommunicatorConfig = new Secs1OnTcpIpCommunicatorConfig();
	private static final boolean defaultAutoReply = true;
	private static final boolean defaultAutoReplySxF0 = false;
	private static final boolean defaultAutoReplyS9Fy = false;
	private static final boolean defaultAutoOpen = false;
	private static final Path defaultAutoLogging = null;
	
	private final BooleanProperty autoReply = BooleanProperty.newInstance(defaultAutoReply);
	private final BooleanProperty autoReplySxF0 = BooleanProperty.newInstance(defaultAutoReplySxF0);
	private final BooleanProperty autoReplyS9Fy = BooleanProperty.newInstance(defaultAutoReplyS9Fy);
	
	private final Property<SecsSimulatorProtocol> protocol = Property.newInstance(defaultSecsSimulatorProtocol);
	
	private final HsmsSsCommunicatorConfig hsmsSsCommConfig = new HsmsSsCommunicatorConfig();
	private final Secs1OnTcpIpCommunicatorConfig secs1OnTcpIpCommConfig = new Secs1OnTcpIpCommunicatorConfig();
	private final Secs1OnTcpIpCommunicatorConfig secs1OnTcpIpRecvCommConfig = new Secs1OnTcpIpCommunicatorConfig();
	private final Property<SocketAddress> secs1AdapterSocketAddress = Property.newInstance(null);
	
	private final SmlAliasPairPool smlPool = new SmlAliasPairPool();
	private final MacroRecipePairPool macroPool = new MacroRecipePairPool();
	
	private final BooleanProperty autoOpen = BooleanProperty.newInstance(defaultAutoOpen);
	private Path autoLogging;
	
	public AbstractSecsSimulatorConfig() {
		this.autoLogging = defaultAutoLogging;
	}
	
	public void initialize() {
		synchronized ( this ) {
			
			this.autoReply.set(defaultAutoReply);
			this.autoReplySxF0.set(defaultAutoReplySxF0);
			this.autoReplyS9Fy.set(defaultAutoReplyS9Fy);
			
			this.protocol(defaultSecsSimulatorProtocol);
			
			this.deviceId(defautlHsmsSsCommunicatorConfig.deviceId().intValue());
			this.isEquip(defautlHsmsSsCommunicatorConfig.isEquip().booleanValue());
			this.isMaster(defaultSecs1OnTcpIpCommunicatorConfig.isMaster().booleanValue());
			
			{
				SecsTimeout t = defautlHsmsSsCommunicatorConfig.timeout();
				this.timeoutT1(t.t1().getSeconds());
				this.timeoutT2(t.t2().getSeconds());
				this.timeoutT3(t.t3().getSeconds());
				this.timeoutT4(t.t4().getSeconds());
				this.timeoutT5(t.t5().getSeconds());
				this.timeoutT6(t.t6().getSeconds());
				this.timeoutT7(t.t7().getSeconds());
				this.timeoutT8(t.t8().getSeconds());
			}
			
			this.retry(defaultSecs1OnTcpIpCommunicatorConfig.retry().intValue());
			
			{
				ReadOnlyTimeProperty t = defautlHsmsSsCommunicatorConfig.linktest();
				if ( t.gtZero() ) {
					this.linktest(t.getSeconds());
				} else {
					this.notLinktest();
				}
			}
			
			this.smlPool.clear();
			this.macroPool.clear();
			
			this.autoOpen.set(defaultAutoOpen);
			this.autoLogging(defaultAutoLogging);
		}
	}
	
	public BooleanProperty autoReply() {
		return autoReply;
	}
	
	public BooleanProperty autoReplySxF0() {
		return autoReplySxF0;
	}
	
	public BooleanProperty autoReplyS9Fy() {
		return autoReplyS9Fy;
	}
	
	public BooleanProperty autoOpen() {
		return autoOpen;
	}
	
	public Optional<Path> autoLogging() {
		synchronized ( this ) {
			return autoLogging == null ? Optional.empty() : Optional.of(autoLogging);
		}
	}
	
	public void autoLogging(Path logPath) {
		synchronized ( this ) {
			this.autoLogging = logPath;
		}
	}
	
	public HsmsSsCommunicatorConfig hsmsSsCommunicatorConfig() {
		return hsmsSsCommConfig;
	}
	
	public Secs1OnTcpIpCommunicatorConfig secs1OnTcpIpCommunicatorConfig() {
		return secs1OnTcpIpCommConfig;
	}
	
	public Secs1OnTcpIpCommunicatorConfig secs1OnTcpIpReceiverCommunicatorConfig() {
		return secs1OnTcpIpRecvCommConfig;
	}
	
	public Property<SocketAddress> secs1AdapterSocketAddress() {
		return secs1AdapterSocketAddress;
	}
	
	
	public Property<SecsSimulatorProtocol> protocol() {
		return protocol;
	}
	
	public void protocol(SecsSimulatorProtocol protocol) {
		synchronized ( this ) {
			this.protocol.set(protocol);
			if ( protocol == SecsSimulatorProtocol.HSMS_SS_PASSIVE ) {
				this.hsmsSsCommunicatorConfig().protocol(HsmsSsProtocol.PASSIVE);
			} else if ( protocol == SecsSimulatorProtocol.HSMS_SS_ACTIVE ) { 
				this.hsmsSsCommunicatorConfig().protocol(HsmsSsProtocol.ACTIVE);
			}
		}
	}
	
	
	public void deviceId(int id) {
		synchronized ( this ) {
			this.hsmsSsCommConfig.deviceId(id);
			this.secs1OnTcpIpCommConfig.deviceId(id);
			this.secs1OnTcpIpRecvCommConfig.deviceId(id);
		}
	}
	
	public void socketAddress(CharSequence socketAddressString) {
		socketAddress(parseSocketAddress(socketAddressString));
	}
	
	public void socketAddress(SocketAddress socketAddress) {
		synchronized ( this ) {
			this.hsmsSsCommConfig.socketAddress(socketAddress);
			this.secs1OnTcpIpCommConfig.socketAddress(socketAddress);
			this.secs1AdapterSocketAddress.set(socketAddress);
		}
	}
	
	public void isEquip(boolean f) {
		synchronized ( this ) {
			this.hsmsSsCommConfig.isEquip(f);
			this.secs1OnTcpIpCommConfig.isEquip(f);
			this.secs1OnTcpIpRecvCommConfig.isEquip(f);
		}
	}
	
	public void isMaster(boolean f) {
		synchronized ( this ) {
			this.secs1OnTcpIpCommConfig.isMaster(f);
			this.secs1OnTcpIpRecvCommConfig.isMaster(f);
		}
	}
	
	public void timeoutT1(float v) {
		synchronized ( this ) {
			this.hsmsSsCommConfig.timeout().t1(v);
			this.secs1OnTcpIpCommConfig.timeout().t1(v);
			this.secs1OnTcpIpRecvCommConfig.timeout().t1(v);
		}
	}
	
	public void timeoutT2(float v) {
		synchronized ( this ) {
			this.hsmsSsCommConfig.timeout().t2(v);
			this.secs1OnTcpIpCommConfig.timeout().t2(v);
			this.secs1OnTcpIpRecvCommConfig.timeout().t2(v);
		}
	}
	
	public void timeoutT3(float v) {
		synchronized ( this ) {
			this.hsmsSsCommConfig.timeout().t3(v);
			this.secs1OnTcpIpCommConfig.timeout().t3(v);
			this.secs1OnTcpIpRecvCommConfig.timeout().t3(v);
		}
	}
	
	public void timeoutT4(float v) {
		synchronized ( this ) {
			this.hsmsSsCommConfig.timeout().t4(v);
			this.secs1OnTcpIpCommConfig.timeout().t4(v);
			this.secs1OnTcpIpRecvCommConfig.timeout().t4(v);
		}
	}
	
	public void timeoutT5(float v) {
		synchronized ( this ) {
			this.hsmsSsCommConfig.timeout().t5(v);
			this.secs1OnTcpIpCommConfig.timeout().t5(v);
			this.secs1OnTcpIpRecvCommConfig.timeout().t5(v);
		}
	}
	
	public void timeoutT6(float v) {
		synchronized ( this ) {
			this.hsmsSsCommConfig.timeout().t6(v);
			this.secs1OnTcpIpCommConfig.timeout().t6(v);
			this.secs1OnTcpIpRecvCommConfig.timeout().t6(v);
		}
	}
	
	public void timeoutT7(float v) {
		synchronized ( this ) {
			this.hsmsSsCommConfig.timeout().t7(v);
			this.secs1OnTcpIpCommConfig.timeout().t7(v);
			this.secs1OnTcpIpRecvCommConfig.timeout().t7(v);
		}
	}
	
	public void timeoutT8(float v) {
		synchronized ( this ) {
			this.hsmsSsCommConfig.timeout().t8(v);
			this.secs1OnTcpIpCommConfig.timeout().t8(v);
			this.secs1OnTcpIpRecvCommConfig.timeout().t8(v);
		}
	}
	
	public void retry(int retryCount) {
		synchronized ( this ) {
			this.secs1OnTcpIpCommConfig.retry(retryCount);
			this.secs1OnTcpIpRecvCommConfig.retry(retryCount);
		}
	}
	
	public void linktest(float v) {
		synchronized ( this ) {
			this.hsmsSsCommConfig.linktest(v);
		}
	}
	
	public void notLinktest() {
		synchronized ( this ) {
			this.hsmsSsCommConfig.notLinktest();
		}
	}
	
	public SmlAliasPairPool smlAliasPairPool() {
		return smlPool;
	}
	
	public MacroRecipePairPool macroRecipePairPool() {
		return macroPool;
	}
	
	/**
	 * Save config to path-file.
	 * 
	 * @param path
	 * @throws IOException
	 */
	public boolean save(Path path) throws IOException {
		synchronized ( this ) {
			getJsonHub().prettyPrint(path);
			return true;
		}
	}
	
	/**
	 * Load config from path-file.
	 * 
	 * @param path
	 * @throws IOException
	 * @throws JsonHubParseException
	 */
	public boolean load(Path path) throws IOException, JsonHubParseException {
		synchronized ( this ) {
			try {
				this.initialize();
				this.setByJson(JsonHub.fromFile(path));
			}
			catch ( SmlParseException e) {
				throw new IOException(e);
			}
			catch ( MacroRecipeParseException e ) {
				throw new IOException(e);
			}
			return true;
		}
	}
	
	/*
	 * {
	 *   "communicator": {
	 *     "protocol": "protocol-name",
	 *     "socketAddress": "aaa.bbb.ccc.ddd:n",
	 *     "deviceId": 10,
	 *     "isEquip": true,
	 *     "isMaster": true,
	 *     "timeout": {
	 *       "t1":  1.0F,
	 *       "t2": 15.0F,
	 *       "t3": 45.0F,
	 *       "t4": 45.0F,
	 *       "t5": 10.0F,
	 *       "t6":  5.0F,
	 *       "t7": 10.0F,
	 *       "t8":  6.0F
	 *     }
	 *     "retry": 3,
	 *     "linktest": 120.0F
	 *   },
	 *   
	 *   "autoReply": true,
	 *   "autoReplyS9Fy": false,
	 *   "autoReplySxF0": false,
	 *   
	 *   "smlFiles": [
	 *     {
	 *       "path": "/path/to/file.sml",
	 *       "alias": "alias-of-file"
	 *     },
	 *     ...
	 *   ],
	 *   
	 *   "autoOpen": false
	 * }
	 */
	
	protected JsonHub getJsonHub() {
		
		final JsonHubBuilder jhb = JsonHub.getBuilder();
		
		return jhb.object(
				jhb.pair("communicator", getCommunicatorJsonHub()),
				jhb.pair("autoReply", this.autoReply().booleanValue()),
				jhb.pair("autoReplyS9Fy", this.autoReplyS9Fy().booleanValue()),
				jhb.pair("autoReplySxF0", this.autoReplySxF0().booleanValue()),
				jhb.pair("smlFiles", this.smlAliasPairPool().getJsonHub()),
				jhb.pair("macroRecipeFiles", this.macroRecipePairPool().getJsonHub()),
				jhb.pair("autoOpen", this.autoOpen().booleanValue()),
				jhb.pair("autoLogging", this.autoLogging().map(Path::normalize).map(Path::toString).orElse(null))
				);
	}
	
	protected JsonHub getCommunicatorJsonHub() {
		
		final JsonHubBuilder jhb = JsonHub.getBuilder();
		
		final List<JsonObjectPair> pairs = new ArrayList<>();
		
		pairs.add(jhb.pair("protocol", protocol().get().optionName()));
		
		{
			SocketAddress addr = this.hsmsSsCommunicatorConfig().socketAddress().get();
			if ( addr instanceof InetSocketAddress ) {
				InetSocketAddress iaddr = (InetSocketAddress)addr;
				int port = iaddr.getPort();
				String v = iaddr.getAddress().getHostAddress() + ":" + port;
				pairs.add(jhb.pair("socketAddress", v));
			}
		}
		
		pairs.add(jhb.pair("deviceId", this.hsmsSsCommunicatorConfig().deviceId().intValue()));
		pairs.add(jhb.pair("isEquip", this.hsmsSsCommunicatorConfig().isEquip().booleanValue()));
		pairs.add(jhb.pair("isMaster", this.secs1OnTcpIpCommunicatorConfig().isMaster().booleanValue()));
		
		{
			SecsTimeout timeout = this.hsmsSsCommunicatorConfig().timeout();
			
			JsonHub jht = jhb.object(
					jhb.pair("t1", timeout.t1().getSeconds()),
					jhb.pair("t2", timeout.t2().getSeconds()),
					jhb.pair("t3", timeout.t3().getSeconds()),
					jhb.pair("t4", timeout.t4().getSeconds()),
					jhb.pair("t5", timeout.t5().getSeconds()),
					jhb.pair("t6", timeout.t6().getSeconds()),
					jhb.pair("t7", timeout.t7().getSeconds()),
					jhb.pair("t8", timeout.t8().getSeconds())
					);
			
			pairs.add(jhb.pair("timeout", jht));
		}
		
		pairs.add(jhb.pair("retry", this.secs1OnTcpIpCommunicatorConfig().retry().intValue()));
		pairs.add(jhb.pair("linktest", this.hsmsSsCommunicatorConfig().linktest().getSeconds()));
		
		return jhb.object(pairs);
	}
	
	protected void setByJson(JsonHub jh) throws SmlParseException, MacroRecipeParseException, IOException {
		
		setCommunicatorByJson(jh.getOrDefault("communicator"));
		
		jh.getOrDefault("autoReply").optionalBoolean().ifPresent(this.autoReply::set);
		jh.getOrDefault("autoReplyS9Fy").optionalBoolean().ifPresent(this.autoReplyS9Fy::set);
		jh.getOrDefault("autoReplySxF0").optionalBoolean().ifPresent(this.autoReplySxF0::set);
		
		setSmlAliasPairs(jh.getOrDefault("smlFiles"));
		setMacroRecipePairs(jh.getOrDefault("macroRecipeFiles"));
		
		jh.getOrDefault("autoOpen").optionalBoolean().ifPresent(this.autoOpen::set);
		jh.getOrDefault("autoLogging").optionalString().map(Paths::get).ifPresent(this::autoLogging);
	}
	
	protected void setCommunicatorByJson(JsonHub jh) {
		
		jh.getOrDefault("protocol").optionalString()
		.map(SecsSimulatorProtocol::get)
		.ifPresent(this::protocol);
		
		jh.getOrDefault("socketAddress").optionalString()
		.ifPresent(this::socketAddress);
		
		jh.getOrDefault("deviceId").optionalInt()
		.ifPresent(this::deviceId);
		
		jh.getOrDefault("isEquip").optionalBoolean()
		.ifPresent(this::isEquip);
		
		jh.getOrDefault("isMaster").optionalBoolean()
		.ifPresent(this::isMaster);
		
		{
			JsonHub jht = jh.getOrDefault("timeout");
			
			jht.getOrDefault("t1").optionalNubmer().map(Number::floatValue).ifPresent(this::timeoutT1);
			jht.getOrDefault("t2").optionalNubmer().map(Number::floatValue).ifPresent(this::timeoutT2);
			jht.getOrDefault("t3").optionalNubmer().map(Number::floatValue).ifPresent(this::timeoutT3);
			jht.getOrDefault("t4").optionalNubmer().map(Number::floatValue).ifPresent(this::timeoutT4);
			jht.getOrDefault("t5").optionalNubmer().map(Number::floatValue).ifPresent(this::timeoutT5);
			jht.getOrDefault("t6").optionalNubmer().map(Number::floatValue).ifPresent(this::timeoutT6);
			jht.getOrDefault("t7").optionalNubmer().map(Number::floatValue).ifPresent(this::timeoutT7);
			jht.getOrDefault("t8").optionalNubmer().map(Number::floatValue).ifPresent(this::timeoutT8);
		}
		
		jh.getOrDefault("retry").optionalInt()
		.ifPresent(this::retry);
		
		{
			float v = jh.getOrDefault("linktest").optionalNubmer()
					.map(Number::floatValue)
					.orElse(-1.0F);
			
			if ( v > 0.0F ) {
				this.linktest(v);
			} else {
				this.notLinktest();
			}
		}
	}
	
	protected void setSmlAliasPairs(JsonHub jh) throws SmlParseException, IOException {
		
		this.smlPool.clear();
		
		Collection<SmlAliasPair> pairs = new HashSet<>();
		
		for ( JsonHub jhp : jh ) {
			
			try {
				Path path = jhp.getOrDefault("path").optionalString().map(Paths::get).orElse(null);
				
				if ( path != null && Files.exists(path) ) {
					
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
						
						String alias = jhp.getOrDefault("alias").optionalString().orElse(null);
						
						if ( alias == null ) {
							
							pairs.add(SmlAliasPair.fromFile(path));
							
						} else {
							
							pairs.add(SmlAliasPair.fromFile(alias, path));
						}
					}
				}
			}
			catch ( InvalidPathException e ) {
			}
		}
		
		this.smlPool.addAll(pairs);
	}
	
	protected void setMacroRecipePairs(JsonHub jh) throws MacroRecipeParseException, IOException {
		
		this.macroPool.clear();
		
		Collection<MacroRecipePair> pairs = new HashSet<>();
		
		for ( JsonHub jhp : jh ) {
			
			try {
				
				Path path = jhp.getOrDefault("path").optionalString().map(Paths::get).orElse(null);
				
				if ( path != null && Files.exists(path) ) {
					
					if ( Files.isDirectory(path) ) {
						
						try (
								DirectoryStream<Path> macroRecipePaths = Files.newDirectoryStream(path, "*.json");
								) {
							
							for ( Path mrPath : macroRecipePaths ) {
								
								if ( ! Files.isDirectory(mrPath) ) {
									pairs.add(MacroRecipePair.fromFile(mrPath));
								}
							}
						}
						
					} else {
						
						String alias = jhp.getOrDefault("alias").optionalString().orElse(null);
						
						if ( alias == null ) {
							
							pairs.add(MacroRecipePair.fromFile(path));
							
						} else {
							
							pairs.add(MacroRecipePair.fromFile(alias, path));
						}
					}
				}
			}
			catch ( InvalidPathException e ) {
			}
		}
		
		this.macroPool.addAll(pairs);
	}
	
	protected static SocketAddress parseSocketAddress(CharSequence cs)
			throws NumberFormatException, IllegalArgumentException, IndexOutOfBoundsException {
		
		String[] ss = cs.toString().split(":", 2);
		int port = Integer.parseInt(ss[1]);
		return new InetSocketAddress(ss[0], port);
	}
}
