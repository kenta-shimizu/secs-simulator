package com.shimizukenta.secssimulator.cli;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import com.shimizukenta.secssimulator.AbstractSecsSimulatorConfig;
import com.shimizukenta.secssimulator.SecsSimulatorProtocol;

public class CliSecsSimulatorConfig extends AbstractSecsSimulatorConfig {

	private static final long serialVersionUID = -4642182855193964949L;
	
	private static final String PROTOCOL_SECS1 = "secs1";
	private static final String PROTOCOL_SECS1_ON_TCP_IP = "secs1-on-tcp-ip";
	private static final String PROTOCOL_HSMS_SS_PASSIVE = "hsms-ss-passive";
	private static final String PROTOCOL_HSMS_SS_ACTIVE = "hsms-ss-active";
	
	private Path log;
	private Path macro;
	private boolean autoOpen;
	private final Collection<Path> smlFiles = new HashSet<>();
	private final Collection<Path> smlDirs = new HashSet<>();
	
	public CliSecsSimulatorConfig() {
		super();
		
		log = null;
		macro = null;
		autoOpen = false;
	}
	
	public void logging(Path path) {
		synchronized ( this ) {
			this.log = path;
		}
	}
	
	public Optional<Path> logging() {
		synchronized ( this ) {
			return log == null ? Optional.empty() : Optional.of(log);
		}
	}
	
	public void macro(Path path) {
		synchronized ( this ) {
			this.macro = path;
		}
	}
	
	public Optional<Path> macro() {
		synchronized ( this ) {
			return macro == null ? Optional.empty() : Optional.of(macro);
		}
	}
	
	public void autoOpen(boolean f) {
		synchronized ( this ) {
			this.autoOpen = f;
		}
	}
	
	public boolean autoOpen() {
		synchronized ( this ) {
			return autoOpen;
		}
	}
	
	public void smlFile(Path path) {
		synchronized ( this ) {
			smlFiles.add(path);
		}
	}
	
	public Collection<Path> smlFiles() {
		synchronized ( this ) {
			return Collections.unmodifiableCollection(smlFiles);
		}
	}
	
	public void smlDirectory(Path path) {
		synchronized ( this ) {
			smlDirs.add(path);
		}
	}
	
	public Collection<Path> smlDirectories() {
		synchronized ( this ) {
			return Collections.unmodifiableCollection(smlDirs);
		}
	}
	
	public void setByJson(Path path) {
		
		//TODO
	}
	
	public static CliSecsSimulatorConfig get(String[] args) {
		
		final CliSecsSimulatorConfig conf = new CliSecsSimulatorConfig();
		
		for ( int i = 0, m = args.length; i < m; i += 2 ) {
			
			String key = args[i];
			String value = args[i + 1];
			
			if ( sameKey(key, "--config") ) {
				Path path = Paths.get(value);
				conf.setByJson(path);
				continue;
			}
			
			if ( sameKey(key, "--protocol") ) {
				
				if ( sameKey(value, PROTOCOL_HSMS_SS_PASSIVE) ) {
					conf.protocol(SecsSimulatorProtocol.HSMS_SS_PASSIVE);
					continue;
				}
				
				if ( sameKey(value, PROTOCOL_HSMS_SS_ACTIVE) ) {
					conf.protocol(SecsSimulatorProtocol.HSMS_SS_ACTIVE);
					continue;
				}
				
				if ( sameKey(value, PROTOCOL_SECS1_ON_TCP_IP, PROTOCOL_SECS1) ) {
					conf.protocol(SecsSimulatorProtocol.SECS1_ON_TCP_IP);
					continue;
				}
				
				continue;
			}
			
			if ( sameKey(key, "--socket-address", "--socketaddress") ) {
				String[] ss = value.split(":");
				int port = Integer.parseInt(ss[1]);
				SocketAddress addr = new InetSocketAddress(ss[0], port);
				conf.hsmsSsCommunicatorConfig().socketAddress(addr);
				conf.secs1OnTcpIpCommunicatorConfig().socketAddress(addr);
				continue;
			}
			
			if ( sameKey(key, "--devicd-id", "--devicdid") ) {
				int v = Integer.parseInt(value);
				conf.hsmsSsCommunicatorConfig().deviceId(v);
				conf.secs1OnTcpIpCommunicatorConfig().deviceId(v);
				continue;
			}
			
			if ( sameKey(key, "--session-id", "--sessionid") ) {
				int v = Integer.parseInt(value);
				conf.hsmsSsCommunicatorConfig().sessionId(v);
				continue;
			}
			
			if ( sameKey(key, "--equip") ) {
				boolean f = Boolean.parseBoolean(value);
				conf.hsmsSsCommunicatorConfig().isEquip(f);
				conf.secs1OnTcpIpCommunicatorConfig().isEquip(f);
				continue;
			}
			
			if ( sameKey(key, "--master") ) {
				boolean f = Boolean.parseBoolean(value);
				conf.secs1OnTcpIpCommunicatorConfig().isMaster(f);
				continue;
			}
			
			if ( sameKey(key, "--rebind") ) {
				float v = Float.valueOf(value);
				conf.hsmsSsCommunicatorConfig().rebindIfPassive(v);
				continue;
			}
			
			if ( sameKey(key, "--link-test", "--linktest") ) {
				float v = Float.valueOf(value);
				conf.hsmsSsCommunicatorConfig().linktest(v);
				continue;
			}
			
			if ( sameKey(key, "--sml", "--smlfile") ) {
				Path path = Paths.get(value);
				conf.smlFile(path);
				continue;
			}
			
			if ( sameKey(key, "--smls", "--smlfiles") ) {
				Path path = Paths.get(value);
				conf.smlDirectory(path);
				continue;
			}
			
			if ( sameKey(key, "--auto-reply", "--autoreply") ) {
				boolean f = Boolean.parseBoolean(value);
				conf.autoReply(f);
				continue;
			}
			
			if ( sameKey(key, "--logging", "--log") ) {
				Path path = Paths.get(value);
				conf.logging(path);
			}
			
			if ( sameKey(key, "--macro") ) {
				Path path = Paths.get(value);
				conf.macro(path);
				continue;
			}
			
			if ( sameKey(key, "--auto-open", "--autoopen") ) {
				boolean f = Boolean.valueOf(value);
				conf.autoOpen(f);
				continue;
			}
		}
		
		return conf;
	}
	
	private static boolean sameKey(String key, String... keys) {
		for ( String v : keys ) {
			if ( v.equalsIgnoreCase(key) ) {
				return true;
			}
		}
		return false;
	}

}
