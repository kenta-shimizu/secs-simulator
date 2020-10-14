package com.shimizukenta.jsoncommunicator;

public class JsonCommunicatorParseException extends Exception {

	private static final long serialVersionUID = 3456798094241076629L;
	
	public JsonCommunicatorParseException() {
		super();
	}

	public JsonCommunicatorParseException(String message) {
		super(message);
	}

	public JsonCommunicatorParseException(Throwable cause) {
		super(cause);
	}

	public JsonCommunicatorParseException(String message, Throwable cause) {
		super(message, cause);
	}

}
