package com.shimizukenta.jsonhub;

public class JsonHubParseException extends RuntimeException {

	private static final long serialVersionUID = -715521949711541261L;
	
	public JsonHubParseException() {
		super();
	}

	public JsonHubParseException(String message) {
		super(message);
	}

	public JsonHubParseException(Throwable cause) {
		super(cause);
	}

	public JsonHubParseException(String message, Throwable cause) {
		super(message, cause);
	}

}
