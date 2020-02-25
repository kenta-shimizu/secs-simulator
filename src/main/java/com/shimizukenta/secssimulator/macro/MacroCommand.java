package com.shimizukenta.secssimulator.macro;

import java.util.Collection;
import java.util.HashSet;

public enum MacroCommand {
	
	UNDEFINED(1, "undefined"),
	
	OPEN(2, "open"),
	CLOSE(1, "close"),
	
	SEND(2, "send"),
	WAIT(2, "wait"),
	
	SLEEP(2, "sleep"),
	
	;
	
	private int split;
	private final Collection<String> commands = new HashSet<>();
	
	private MacroCommand(int split, String... commands) {
		this.split = split;
		for ( String cmd : commands) {
			this.commands.add(cmd);
		}
	}
	
	public static MacroCommand get(CharSequence cs) {
		String s = cs.toString();
		for ( MacroCommand cmd : values() ) {
			for ( String cc : cmd.commands ) {
				if ( cc.equalsIgnoreCase(s) ) {
					return cmd;
				}
			}
		}
		return UNDEFINED;
	}
	
	
}
