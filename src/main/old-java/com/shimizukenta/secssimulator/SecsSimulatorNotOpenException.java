package com.shimizukenta.secssimulator;

public class SecsSimulatorNotOpenException extends SecsSimulatorException {
	
	private static final long serialVersionUID = 3925011855208047400L;

	public SecsSimulatorNotOpenException() {
		super();
	}

	public SecsSimulatorNotOpenException(String message) {
		super(message);
	}

	public SecsSimulatorNotOpenException(Throwable cause) {
		super(cause);
	}

	public SecsSimulatorNotOpenException(String message, Throwable cause) {
		super(message, cause);
	}

}
