package com.shimizukenta.secssimulator.cli;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import com.shimizukenta.jsonhub.JsonHub;
import com.shimizukenta.secssimulator.AbstractSecsSimulatorConfig;
import com.shimizukenta.secssimulator.SecsSimulatorProtocol;

public class CliSecsSimulatorConfig extends AbstractSecsSimulatorConfig {

	private static final long serialVersionUID = -4642182855193964949L;
	
	private static final String PROTOCOL_SECS1 = "secs1";
	private static final String PROTOCOL_SECS1_ON_TCP_IP = "secs1-on-tcp-ip";
	private static final String PROTOCOL_SECS1_ON_TCP_IP_RECEIVER = "secs1-on-tcp-ip-receiver";
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
	
	
	private void protocol(String value) {
		
		if ( sameKey(value, PROTOCOL_HSMS_SS_PASSIVE) ) {
			
			protocol(SecsSimulatorProtocol.HSMS_SS_PASSIVE);
			
		} else  if ( sameKey(value, PROTOCOL_HSMS_SS_ACTIVE) ) {
			
			protocol(SecsSimulatorProtocol.HSMS_SS_ACTIVE);
			
		} else if ( sameKey(value, PROTOCOL_SECS1_ON_TCP_IP, PROTOCOL_SECS1) ) {
			
			protocol(SecsSimulatorProtocol.SECS1_ON_TCP_IP);
			
		} else if ( sameKey(value, PROTOCOL_SECS1_ON_TCP_IP_RECEIVER) ) {
			
			protocol(SecsSimulatorProtocol.SECS1_ON_TCP_IP_RECEIVER);
		}
	}
	
	public void socketAddress(SocketAddress addr) {
		synchronized ( this ) {
			hsmsSsCommunicatorConfig().socketAddress(addr);
			secs1OnTcpIpCommunicatorConfig().socketAddress(addr);
			secs1AdapterSocketAddress(addr);
		}
	}
	
	private void socketAddress(String value) {
		String[] ss = value.split(":");
		int port = Integer.parseInt(ss[1]);
		socketAddress(new InetSocketAddress(ss[0], port));
	}
	
	public void deviceId(int value) {
		synchronized ( this ) {
			hsmsSsCommunicatorConfig().deviceId(value);
			secs1OnTcpIpCommunicatorConfig().deviceId(value);
			secs1OnTcpIpReceiverCommunicatorConfig().deviceId(value);
		}
	}
	
	public void sessionId(int value) {
		synchronized ( this ) {
			hsmsSsCommunicatorConfig().sessionId(value);
		}
	}
	
	public void isEquip(boolean f) {
		synchronized ( this ) {
			hsmsSsCommunicatorConfig().isEquip(f);
			secs1OnTcpIpCommunicatorConfig().isEquip(f);
			secs1OnTcpIpReceiverCommunicatorConfig().isEquip(f);
		}
	}
	
	public void isMaster(boolean f) {
		synchronized ( this ) {
			secs1OnTcpIpCommunicatorConfig().isMaster(f);
			secs1OnTcpIpReceiverCommunicatorConfig().isMaster(f);
		}
	}
	
	public void timeoutT1(float value) {
		synchronized ( this ) {
			hsmsSsCommunicatorConfig().timeout().t1(value);
			secs1OnTcpIpCommunicatorConfig().timeout().t1(value);
			secs1OnTcpIpReceiverCommunicatorConfig().timeout().t1(value);
		}
	}
	
	public void timeoutT2(float value) {
		synchronized ( this ) {
			hsmsSsCommunicatorConfig().timeout().t2(value);
			secs1OnTcpIpCommunicatorConfig().timeout().t2(value);
			secs1OnTcpIpReceiverCommunicatorConfig().timeout().t2(value);
		}
	}
	
	public void timeoutT3(float value) {
		synchronized ( this ) {
			hsmsSsCommunicatorConfig().timeout().t3(value);
			secs1OnTcpIpCommunicatorConfig().timeout().t3(value);
			secs1OnTcpIpReceiverCommunicatorConfig().timeout().t3(value);
		}
	}
	
	public void timeoutT4(float value) {
		synchronized ( this ) {
			hsmsSsCommunicatorConfig().timeout().t4(value);
			secs1OnTcpIpCommunicatorConfig().timeout().t4(value);
			secs1OnTcpIpReceiverCommunicatorConfig().timeout().t4(value);
		}
	}
	
	public void timeoutT5(float value) {
		synchronized ( this ) {
			hsmsSsCommunicatorConfig().timeout().t5(value);
			secs1OnTcpIpCommunicatorConfig().timeout().t5(value);
			secs1OnTcpIpReceiverCommunicatorConfig().timeout().t5(value);
		}
	}
	
	public void timeoutT6(float value) {
		synchronized ( this ) {
			hsmsSsCommunicatorConfig().timeout().t6(value);
			secs1OnTcpIpCommunicatorConfig().timeout().t6(value);
			secs1OnTcpIpReceiverCommunicatorConfig().timeout().t6(value);
		}
	}
	
	public void timeoutT7(float value) {
		synchronized ( this ) {
			hsmsSsCommunicatorConfig().timeout().t7(value);
			secs1OnTcpIpCommunicatorConfig().timeout().t7(value);
			secs1OnTcpIpReceiverCommunicatorConfig().timeout().t7(value);
		}
	}
	
	public void timeoutT8(float value) {
		synchronized ( this ) {
			hsmsSsCommunicatorConfig().timeout().t8(value);
			secs1OnTcpIpCommunicatorConfig().timeout().t8(value);
			secs1OnTcpIpReceiverCommunicatorConfig().timeout().t8(value);
		}
	}
	
	public void retry(int value) {
		synchronized ( this ) {
			secs1OnTcpIpCommunicatorConfig().retry(value);
			secs1OnTcpIpReceiverCommunicatorConfig().retry(value);
		}
	}
	
	public void linktest(float value) {
		synchronized ( this ) {
			hsmsSsCommunicatorConfig().linktest(value);
		}
	}
	
	public void rebindIfPassive(float value) {
		synchronized ( this ) {
			hsmsSsCommunicatorConfig().rebindIfPassive(value);
		}
	}
	
	
	/*
	 *******************************
	 * JsonPattern
	 *******************************
	{
		"communicator": {
			"protocol": "protorol-string",
			"socketAddress": "socket-address-string",
			"deviceId": number,
			"sessionId": number,
			"isEquip": boolean,
			"isMaster": boolean,
			"timeout": {
				"t1": number,
				"t2": number,
				"t3": number,
				"t4": number,
				"t5": number,
				"t6": number,
				"t7": number,
				"t8": number
			},
			"retry": number,
			"linktest": number,
			"rebindIfPassive": number
		},
		"autoReply": true,
		"autoReplyS9Fy": false,
		"autoReplySxF0": false,
		"smlFiles": [
			"/path/to/sxfy.sml",
			...
		],
		"smlDirectories": [
			"/path/to/directory",
			...
		],
		"logging": "/path/to/log.log",
		"macro": "path/to/macro.macro",
		"autoOpen": false
	}
	 *******************************
	 */
	
	public void setByJson(Path path) throws IOException {
		
		JsonHub jh = JsonHub.fromFile(path);
		
		{
			JsonHub comm = jh.getOrDefault("communicator");
			
			comm.getOrDefault("protocol").optionalString().ifPresent(this::protocol);
			comm.getOrDefault("socketAddress").optionalString().ifPresent(this::socketAddress);
			comm.getOrDefault("deviceId").optionalInt().ifPresent(this::deviceId);
			comm.getOrDefault("sessionId").optionalInt().ifPresent(this::sessionId);
			comm.getOrDefault("isEquip").optionalBoolean().ifPresent(this::isEquip);
			comm.getOrDefault("isMaster").optionalBoolean().ifPresent(this::isMaster);
			
			{
				JsonHub tt = comm.getOrDefault("timeout");
				
				tt.getOrDefault("t1").optionalNubmer().map(Number::floatValue).ifPresent(this::timeoutT1);
				tt.getOrDefault("t2").optionalNubmer().map(Number::floatValue).ifPresent(this::timeoutT2);
				tt.getOrDefault("t3").optionalNubmer().map(Number::floatValue).ifPresent(this::timeoutT3);
				tt.getOrDefault("t4").optionalNubmer().map(Number::floatValue).ifPresent(this::timeoutT4);
				tt.getOrDefault("t5").optionalNubmer().map(Number::floatValue).ifPresent(this::timeoutT5);
				tt.getOrDefault("t6").optionalNubmer().map(Number::floatValue).ifPresent(this::timeoutT6);
				tt.getOrDefault("t7").optionalNubmer().map(Number::floatValue).ifPresent(this::timeoutT7);
				tt.getOrDefault("t8").optionalNubmer().map(Number::floatValue).ifPresent(this::timeoutT8);
			}
			
			comm.getOrDefault("retry").optionalInt().ifPresent(this::retry);
			comm.getOrDefault("linktest").optionalNubmer().map(Number::floatValue).ifPresent(this::linktest);
			comm.getOrDefault("rebindIfPassive").optionalNubmer().map(Number::floatValue).ifPresent(this::rebindIfPassive);
		}
		
		jh.getOrDefault("autoReply").optionalBoolean().ifPresent(this::autoReply);
		jh.getOrDefault("autoReplyS9Fy").optionalBoolean().ifPresent(this::autoReplyS9Fy);
		jh.getOrDefault("autoReplySxF0").optionalBoolean().ifPresent(this::autoReplySxF0);
		
		jh.getOrDefault("smlFiles").forEach(x -> {
			x.optionalString().map(Paths::get).ifPresent(this::smlFile);
		});
		
		jh.getOrDefault("smlDirectories").forEach(x -> {
			x.optionalString().map(Paths::get).ifPresent(this::smlDirectory);
		});
		
		jh.getOrDefault("logging").optionalString().map(Paths::get).ifPresent(this::logging);
		jh.getOrDefault("macro").optionalString().map(Paths::get).ifPresent(this::macro);
		jh.getOrDefault("autoOpen").optionalBoolean().ifPresent(this::autoOpen);
	}
	
	public static CliSecsSimulatorConfig get(String[] args) throws IOException {
		
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
				conf.protocol(value);
				continue;
			}
			
			if ( sameKey(key, "--socket-address", "--socketaddress") ) {
				conf.socketAddress(value);
				continue;
			}
			
			if ( sameKey(key, "--devicd-id", "--devicdid") ) {
				conf.deviceId(Integer.parseInt(value));
				continue;
			}
			
			if ( sameKey(key, "--session-id", "--sessionid") ) {
				conf.sessionId(Integer.parseInt(value));
				continue;
			}
			
			if ( sameKey(key, "--equip") ) {
				conf.isEquip(Boolean.parseBoolean(value));
				continue;
			}
			
			if ( sameKey(key, "--master") ) {
				conf.isMaster(Boolean.parseBoolean(value));
				continue;
			}
			
			if ( sameKey(key, "--rebind") ) {
				conf.rebindIfPassive(Float.valueOf(value));
				continue;
			}
			
			if ( sameKey(key, "--link-test", "--linktest") ) {
				conf.linktest(Float.valueOf(value));
				continue;
			}
			
			if ( sameKey(key, "--sml", "--smlfile") ) {
				conf.smlFile(Paths.get(value));
				continue;
			}
			
			if ( sameKey(key, "--smls", "--smlfiles") ) {
				conf.smlDirectory(Paths.get(value));
				continue;
			}
			
			if ( sameKey(key, "--auto-reply", "--autoreply") ) {
				conf.autoReply(Boolean.parseBoolean(value));
				continue;
			}
			
			if ( sameKey(key, "--auto-reply-s9fy", "--autoreplys9fy") ) {
				conf.autoReplyS9Fy(Boolean.parseBoolean(value));
				continue;
			}
			
			if ( sameKey(key, "--auto-reply-sxf0", "--autoreplysxf0") ) {
				conf.autoReplySxF0(Boolean.parseBoolean(value));
				continue;
			}
			
			if ( sameKey(key, "--logging", "--log") ) {
				conf.logging(Paths.get(value));
			}
			
			if ( sameKey(key, "--macro") ) {
				conf.macro(Paths.get(value));
				continue;
			}
			
			if ( sameKey(key, "--auto-open", "--autoopen") ) {
				conf.autoOpen(Boolean.valueOf(value));
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
