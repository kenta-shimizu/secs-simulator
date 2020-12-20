package com.shimizukenta.secssimulator;

import java.util.Objects;

public enum SecsSimulatorProtocol {
	
	UNDEFINED(false, "", false, false),
	
	HSMS_SS_PASSIVE(true, "hsms-ss-passive", true, false),
	HSMS_SS_ACTIVE(true, "hsms-ss-active", true, false),
	SECS1_ON_TCP_IP(false, "secs1-on-tcp-ip", false, true),
	SECS1_ON_TCP_IP_RECEIVER(false, "secs1-on-tcp-ip-receiver", false, true),
	
	;
	
	private final String optionName;
	private final boolean isHsmsSs;
	private final boolean isSecs1;
	
	private SecsSimulatorProtocol(
			boolean linktest,
			String optionName,
			boolean isHsmsSs,
			boolean isSecs1) {
		
		this.optionName = optionName;
		this.isHsmsSs = isHsmsSs;
		this.isSecs1 = isSecs1;
	}
	
	
	public boolean isHsmsSs() {
		return isHsmsSs;
	}
	
	public boolean isSecs1() {
		return isSecs1;
	}
	
	public String optionName() {
		return optionName;
	}
	
	public static SecsSimulatorProtocol get(CharSequence cs) {
		String op = Objects.requireNonNull(cs).toString().trim();
		for ( SecsSimulatorProtocol p : values() ) {
			if ( p.optionName.equalsIgnoreCase(op) ) {
				return p;
			}
		}
		return UNDEFINED;
	}
	
}
