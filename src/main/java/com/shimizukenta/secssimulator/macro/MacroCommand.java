package com.shimizukenta.secssimulator.macro;

public enum MacroCommand {
	
	UNDEFINED("undefined"),
	
	OPEN("open"),
	CLOSE("close"),
	
	SEND_SML_ALIAS("send-sml-alias", "sendsmlalias"),
	SEND_SML_DIRECT("send-sml-direct", "sendsmldirect"),
	
	WAIT("wait-sxfy", "wait", "waitsxfy"),
	
	SLEEP("sleep"),
	
	;
	
	private final String[] commands;
	
	private MacroCommand(String... commands) {
		this.commands = commands;
	}
	
	public static MacroCommand get(CharSequence command) {
		if ( command != null ) {
			String s = command.toString().trim();
			for ( MacroCommand c : values() ) {
				for ( String a : c.commands ) {
					if ( s.equalsIgnoreCase(a) ) {
						return c;
					}
				}
			}
		}
		return UNDEFINED;
	}
	
}
