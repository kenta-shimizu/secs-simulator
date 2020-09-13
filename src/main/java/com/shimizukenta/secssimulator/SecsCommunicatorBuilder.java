package com.shimizukenta.secssimulator;

import com.shimizukenta.secs.SecsCommunicator;
import com.shimizukenta.secs.hsmsss.HsmsSsCommunicator;
import com.shimizukenta.secs.secs1ontcpip.Secs1OnTcpIpCommunicator;

public class SecsCommunicatorBuilder {

	protected SecsCommunicatorBuilder() {
		/* Nothing */
	}
	
	private static class SingletonHolder {
		private static final SecsCommunicatorBuilder inst = new SecsCommunicatorBuilder();
	}
	
	public static SecsCommunicatorBuilder getInstance() {
		return SingletonHolder.inst;
	}
	
	public SecsCommunicator build(AbstractSecsSimulatorConfig config) {
		
		switch ( config.protocol().get() ) {
		case HSMS_SS_PASSIVE:
		case HSMS_SS_ACTIVE: {
			
			return HsmsSsCommunicator.newInstance(config.hsmsSsCommunicatorConfig());
			/* break */
		}
		case SECS1_ON_TCP_IP: {
			
			return Secs1OnTcpIpCommunicator.newInstance(config.secs1OnTcpIpCommunicatorConfig());
			/* break */
		}
		case SECS1_ON_TCP_IP_RECEIVER: {
			
			return Secs1OnTcpIpCommunicator.newInstance(config.secs1OnTcpIpReceiverCommunicatorConfig());
			/* break */
		}
		default: {
			
			throw new IllegalStateException("Unknown protocol");
		}
		}
	}
}
