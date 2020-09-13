package com.shimizukenta.secssimulator;

import java.util.Objects;

public enum SecsSimulatorProtocol {
	
	UNDEFINED(false, ""),
	
	HSMS_SS_PASSIVE(true, "hsms-ss-passive"),
	HSMS_SS_ACTIVE(true, "hsms-ss-active"),
	SECS1_ON_TCP_IP(false, "secs1-on-tcp-ip"),
	SECS1_ON_TCP_IP_RECEIVER(false, "secs1-on-tcp-ip-receiver"),
	
	;
	
	private final boolean linktest;
	private final String optionName;
	
	private SecsSimulatorProtocol(boolean linktest, String optionName) {
		this.linktest = linktest;
		this.optionName = optionName;
	}
	
	public boolean linktestable() {
		return linktest;
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
