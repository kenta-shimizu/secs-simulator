package com.shimizukenta.secssimulator.cli;

import java.util.Arrays;
import java.util.Objects;
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
		return Arrays.copyOf(options, options.length);
	}
	
	public Optional<String> option(int index) {
		return (index >= 0 && index < options.length) ? Optional.of(options[index]) : Optional.empty();
	}
	
	private static final String[] emptyArray = new String[0];
	private static final CliRequest undefined = new CliRequest(CliCommand.UNDEFINED, emptyArray);
	
	public static CliRequest get(CharSequence line) {
		
		String s = Objects.requireNonNull(line).toString().trim();
		
		if ( s.isEmpty() ) {
			return undefined;
		}
		
		CliCommand command = CliCommand.get((s.split("\\s+", 2))[0]);
		
		if ( command.split() > 1 ) {
			String[] ops = s.split("\\s+", command.split());
			return new CliRequest(command, Arrays.copyOfRange(ops, 1, ops.length));
		} else {
			return new CliRequest(command, emptyArray);
		}
	}
	
}
