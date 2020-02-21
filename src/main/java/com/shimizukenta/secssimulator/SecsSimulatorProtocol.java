package com.shimizukenta.secssimulator;

public enum SecsSimulatorProtocol {
	
	HSMS_SS_PASSIVE(true),
	HSMS_SS_ACTIVE(true),
	SECS1_ON_TCP_IP(true),
	
	;
	
	private final boolean linktest;
	
	private SecsSimulatorProtocol(boolean linktest) {
		this.linktest = linktest;
	}
	
	public boolean linktestable() {
		return linktest;
	}
	
}
