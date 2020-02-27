package com.shimizukenta.secssimulator.cli;

import java.util.Arrays;
import java.util.Objects;

public enum CliCommand {
	
	UNDEFINED(-1, "UNDEFINED"),
	
	MANUAL(2, "MAN", "?"),
	
	OPEN(1, "open"),
	CLOSE(1, "close"),
	QUIT(1, "quit", "exit"),
	
	SEND_SML(2, "send", "ss", "sendsml", "send-sml"),
	SEND_DIRECT(2, "senddirect", "sd", "send-direct"),
	LINKTEST(1, "linktest"),
	
	LIST_SML(1, "list"),
	SHOW_SML(2, "show"),
	ADD_SML(2, "addfile", "add-file"),
	ADD_SMLS(2, "addfiles", "add-files"),
	
	PWD(1, "pwd"),
	CD(2, "cd"),
	LS(1, "ls"),
	
	LOG(2, "log"),
	MACRO(2, "macro"),
	AUTO_REPLY(2, "autoreply", "auto-reply"),
	
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
			
			String[] ss = requestLine.toString().trim().split("\\s+", cmd.split);
			
			ss = Arrays.copyOfRange(ss, 1, ss.length);
			
			return new CliRequest(cmd, ss);
			
		} else {
			
			return new CliRequest(cmd, new String[0]);
		}
	}
}
