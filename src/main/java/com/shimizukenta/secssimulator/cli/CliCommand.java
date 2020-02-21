package com.shimizukenta.secssimulator.cli;

import java.util.Objects;

public enum CliCommand {
	
	UNDEFINED(-1, "UNDEFINED"),
	
	OPEN(0, "open"),
	
	;
	
	private final int split;
	private final String[] commands;
	
	private CliCommand(int split, String... commands) {
		this.split = split;
		this.commands = commands;
	}
	
	public static CliCommand get(CharSequence request) {
		
		String s = Objects.requireNonNull(request).toString().trim();
		
		//TODO
		
		return UNDEFINED;
	}
}
