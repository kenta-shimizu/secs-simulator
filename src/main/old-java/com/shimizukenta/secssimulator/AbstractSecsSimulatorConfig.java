package com.shimizukenta.secssimulator;

import java.io.Serializable;
import java.net.SocketAddress;

import com.shimizukenta.secs.BooleanProperty;
import com.shimizukenta.secs.Property;
import com.shimizukenta.secs.hsmsss.HsmsSsCommunicatorConfig;
import com.shimizukenta.secs.secs1ontcpip.Secs1OnTcpIpCommunicatorConfig;

public abstract class AbstractSecsSimulatorConfig implements Serializable {
	
	private static final long serialVersionUID = 7451456701694504127L;
	
	private final BooleanProperty autoReply = BooleanProperty.newInstance(false);
	private final BooleanProperty autoReplySxF0 = BooleanProperty.newInstance(false);
	private final BooleanProperty autoReplyS9Fy = BooleanProperty.newInstance(false);
	private final Property<SecsSimulatorProtocol> protocol = Property.newInstance(SecsSimulatorProtocol.HSMS_SS_PASSIVE);
	private final BooleanProperty autoOpen = BooleanProperty.newInstance(false);
	
	private final HsmsSsCommunicatorConfig hsmsSsCommConfig = new HsmsSsCommunicatorConfig();
	private final Secs1OnTcpIpCommunicatorConfig secs1OnTcpIpCommConfig = new Secs1OnTcpIpCommunicatorConfig();
	private final Secs1OnTcpIpCommunicatorConfig secs1OnTcpIpRecvCommConfig = new Secs1OnTcpIpCommunicatorConfig();
	private final Property<SocketAddress> secs1AdapterSocketAddress = Property.newInstance(null);
	
	public AbstractSecsSimulatorConfig() {
		/* Nothing */
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
	
	public Property<SecsSimulatorProtocol> protocol() {
		return protocol;
	}
	
	public BooleanProperty autoOpen() {
		return autoOpen;
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
	
}
