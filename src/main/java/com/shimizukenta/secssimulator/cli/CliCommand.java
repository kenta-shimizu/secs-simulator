package com.shimizukenta.secssimulator.cli;

import java.util.Arrays;
import java.util.Objects;

public enum CliCommand {
	
	UNDEFINED(-1, "UNDEFINED"),
	
	OPEN(1, "open"),
	CLOSE(1, "close"),
	QUIT(1, "quit"),
	
	SEND_SML(2, "ss", "sendsml", "send-sml"),
	SEND_DIRECT(2, "sd", "senddirect", "send-direct"),
	LINKTEST(1, "linktest"),
	
	PWD(2, "pwd"),
	CD(2, "cd"),
	
	LOG(2, "log"),
	
	;
	
	private final int split;
	private final String[] commands;
	
	private CliCommand(int split, String... commands) {
		this.split = split;
		this.commands = commands;
	}
	
	public static CliCommand get(CharSequence requestLine) {
		
		String s = Objects.requireNonNull(requestLine).toString().trim();
		
		for ( CliCommand v : values() ) {
			
			String c;
			if ( v.split > 1 ) {
				c = (s.split("\\s+", 2))[0];
			} else {
				c = s;
			}
			
			for (String a : v.commands ) {
				if ( a.equals(c) ) {
					return v;
				}
			}
		}
		
		return UNDEFINED;
	}
	
	public static CliRequest getRequest(CharSequence requestLine) {
		
		CliCommand cmd = get(requestLine);
		
		if ( cmd.split > 1 ) {
			
			String[] ss = requestLine.toString().trim().split("\\s+");
			return new CliRequest(cmd, Arrays.copyOfRange(ss, 1, cmd.split));
			
		} else {
			
			return new CliRequest(cmd, new String[0]);
		}
	}
}
