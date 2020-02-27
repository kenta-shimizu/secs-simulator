package com.shimizukenta.secssimulator.macro;

import java.util.Optional;

public class MacroRequest {
	
	private final MacroCommand command;
	private final String[] options;
	private final int lineNumber;
	
	public MacroRequest(MacroCommand command, String[] options, int lineNumber) {
		this.command = command;
		this.options = options;
		this.lineNumber = lineNumber;
	}
	
	public MacroRequest(MacroCommand command, String[] options) {
		this(command, options, -1);
	}
	
	public MacroCommand command() {
		return command;
	}
	
	public String[] options() {
		return options;
	}
	
	public Optional<String> option(int index) {
		return (index >= 0 && index < options.length) ? Optional.of(options[index]) : Optional.empty();
	}
	
	public int lineNumber() {
		return lineNumber;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("line: ")
		.append(lineNumber)
		.append(", ")
		.append(command);
		
		//TODO
		//optoins
		
		
		return sb.toString();
	}
}
