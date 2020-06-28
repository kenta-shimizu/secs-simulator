package com.shimizukenta.secssimulator.gui.swing;

import java.util.Optional;

public class CliRequest {
	
	private final CliCommand command;
	private final String[] options;
	
	public CliRequest(CliCommand command, String[] options) {
		this.command = command;
		this.options = options;
	}
	
	public CliCommand command() {
		return command;
	}
	
	public String[] options() {
		return options;
	}
	
	public Optional<String> option(int index) {
		return (index >= 0 && index < options.length) ? Optional.of(options[index]) : Optional.empty();
	}
}
