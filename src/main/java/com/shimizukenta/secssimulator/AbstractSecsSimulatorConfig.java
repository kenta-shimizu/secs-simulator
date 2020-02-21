package com.shimizukenta.secssimulator;

import java.io.Serializable;

import com.shimizukenta.secs.hsmsss.HsmsSsCommunicatorConfig;
import com.shimizukenta.secs.secs1ontcpip.Secs1OnTcpIpCommunicatorConfig;

public abstract class AbstractSecsSimulatorConfig implements Serializable {
	
	private static final long serialVersionUID = 2514470591604580145L;
	
	private SecsSimulatorProtocol protocol;
	private final HsmsSsCommunicatorConfig hsmsSsCommConfig = new HsmsSsCommunicatorConfig();
	private final Secs1OnTcpIpCommunicatorConfig secs1OnTcpIpCommConfig = new Secs1OnTcpIpCommunicatorConfig();
	
	public AbstractSecsSimulatorConfig() {
		protocol = SecsSimulatorProtocol.HSMS_SS_PASSIVE;
	}
	
	public HsmsSsCommunicatorConfig hsmsSsCommunicatorConfig() {
		return hsmsSsCommConfig;
	}
	
	public Secs1OnTcpIpCommunicatorConfig secs1OnTcpIpCommunicatorConfig() {
		return secs1OnTcpIpCommConfig;
	}
	
}
