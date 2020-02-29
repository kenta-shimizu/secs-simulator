package com.shimizukenta.secssimulator;

import java.io.Serializable;
import java.net.SocketAddress;

import com.shimizukenta.secs.hsmsss.HsmsSsCommunicatorConfig;
import com.shimizukenta.secs.hsmsss.HsmsSsProtocol;
import com.shimizukenta.secs.secs1ontcpip.Secs1OnTcpIpCommunicatorConfig;

public abstract class AbstractSecsSimulatorConfig implements Serializable {
	
	private static final long serialVersionUID = 2514470591604580145L;
	
	private boolean autoReply;
	private boolean autoReplySxF0;
	private boolean autoReplyS9Fy;
	private SecsSimulatorProtocol protocol;
	private final HsmsSsCommunicatorConfig hsmsSsCommConfig = new HsmsSsCommunicatorConfig();
	private final Secs1OnTcpIpCommunicatorConfig secs1OnTcpIpCommConfig = new Secs1OnTcpIpCommunicatorConfig();
	private final Secs1OnTcpIpCommunicatorConfig secs1OnTcpIpRecvCommConfig = new Secs1OnTcpIpCommunicatorConfig();
	private SocketAddress secs1AdapterSocketAddress;
	
	public AbstractSecsSimulatorConfig() {
		autoReply = true;
		autoReplySxF0 = false;
		autoReplyS9Fy = false;
		protocol = SecsSimulatorProtocol.HSMS_SS_PASSIVE;
		secs1AdapterSocketAddress = null;
	}
	
	public void autoReply(boolean f) {
		synchronized ( this ) {
			this.autoReply = f;
		}
	}
	
	public boolean autoReply() {
		synchronized ( this ) {
			return autoReply;
		}
	}
	
	public void autoReplySxF0(boolean f) {
		synchronized ( this ) {
			autoReplySxF0 = f;
		}
	}
	
	public boolean autoReplySxF0() {
		synchronized ( this ) {
			return autoReplySxF0;
		}
	}
	
	public void autoReplyS9Fy(boolean f) {
		synchronized ( this ) {
			autoReplyS9Fy = f;
		}
	}
	
	public boolean autoReplyS9Fy() {
		synchronized ( this ) {
			return autoReplyS9Fy;
		}
	}
	
	public SecsSimulatorProtocol protocol() {
		synchronized ( this ) {
			return protocol;
		}
	}
	
	public void protocol(SecsSimulatorProtocol protocol) {
		synchronized ( this ) {
			this.protocol = protocol;
			
			switch ( protocol ) {
			case HSMS_SS_PASSIVE: {
				hsmsSsCommConfig.protocol(HsmsSsProtocol.PASSIVE);
				break;
			}
			case HSMS_SS_ACTIVE: {
				hsmsSsCommConfig.protocol(HsmsSsProtocol.ACTIVE);
				break;
			}
			default: {
				/* nothing */
			}
			}
		}
	}
	
	public HsmsSsCommunicatorConfig hsmsSsCommunicatorConfig() {
		synchronized ( this ) {
			return hsmsSsCommConfig;
		}
	}
	
	public Secs1OnTcpIpCommunicatorConfig secs1OnTcpIpCommunicatorConfig() {
		synchronized ( this ) {
			return secs1OnTcpIpCommConfig;
		}
	}
	
	public Secs1OnTcpIpCommunicatorConfig secs1OnTcpIpReceiverCommunicatorConfig() {
		synchronized ( this ) {
			return secs1OnTcpIpRecvCommConfig;
		}
	}
	
	public void secs1AdapterSocketAddress(SocketAddress addr) {
		synchronized ( this ) {
			secs1AdapterSocketAddress = addr;
		}
	}
	
	public SocketAddress secs1AdapterSocketAddress() {
		synchronized ( this ) {
			return secs1AdapterSocketAddress;
		}
	}
}
