package com.shimizukenta.secssimulator.cli;

public class CliRequest {
	
	private final CliCommand command;
	private final String[] params;
	
	public CliRequest(CliCommand command, String[] params) {
		this.command = command;
		this.params = params;
	}
	
	public CliCommand command() {
		return command;
	}
	
	public String[] params() {
		return params;
	}
}
