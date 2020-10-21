package com.shimizukenta.secssimulator.macro;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

public enum MacroCommand {
	
	UNDEFINED(1, "undefined"),
	
	OPEN(2, "open"),
	CLOSE(1, "close"),
	
	SEND_SML(2, "send", "send-sml", "sendsml"),
	SEND_DIRECT(2, "send-direct", "senddirect"),
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
	
	public static MacroCommand get(CharSequence requestLine) {
		
		String s = Objects.requireNonNull(requestLine).toString().trim();
		
		for ( MacroCommand v : values() ) {
			
			String c;
			if ( v.split > 1 ) {
				c = (s.split("\\s+", 2))[0];
			} else {
				c = s;
			}
			
			for (String a : v.commands ) {
				if ( a.equalsIgnoreCase(c) ) {
					return v;
				}
			}
		}
		
		return UNDEFINED;
	}
	
	private static final String[] emptyArray = new String[0];
	
	public static MacroRequest getRequest(CharSequence requestLine, int lineNumber) {
		
		MacroCommand cmd = get(requestLine);
		
		if ( cmd.split > 1 ) {
			
			String[] ss = requestLine.toString().trim().split("\\s+", cmd.split);
			
			ss = Arrays.copyOfRange(ss, 1, ss.length);
			
			return new MacroRequest(cmd, ss, lineNumber);
			
		} else {
			
			return new MacroRequest(cmd, emptyArray, lineNumber);
		}
	}
	
}
