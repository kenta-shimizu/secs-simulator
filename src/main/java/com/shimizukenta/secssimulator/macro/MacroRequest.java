package com.shimizukenta.secssimulator.macro;

import java.util.Optional;

public class MacroRequest {
	
	private final MacroCommand command;
	private final String[] options;
	
	public MacroRequest(MacroCommand command, String[] options) {
		this.command = command;
		this.options = options;
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
	
}
