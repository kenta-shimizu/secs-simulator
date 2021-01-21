package com.shimizukenta.secssimulator.jsoncommunicator;

public enum JsonCommunicatorRequestCommand {
	
	UNDEFINED("undefined"),
	
	QUIT("quit"),
	REBOOT("reboot"),
	
	OPEN("open"),
	CLOSE("close"),
	
	;
	
	private final String[] commands;
	
	private JsonCommunicatorRequestCommand(String... commands) {
		this.commands = commands;
	}
	
	public static JsonCommunicatorRequestCommand get(CharSequence cs) {
		
		if ( cs != null ) {
			String s = cs.toString();
			for ( JsonCommunicatorRequestCommand v : values() ) {
				for ( String c : v.commands ) {
					if ( c.equalsIgnoreCase(s) ) {
						return v;
					}
				}
			}
		}
		
		return UNDEFINED;
	}
	
}
