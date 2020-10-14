package com.shimizukenta.jsonhub;

public class JsonHubUnsupportedParseException extends JsonHubParseException {

	private static final long serialVersionUID = 2351099713814673110L;

	public JsonHubUnsupportedParseException() {
		super();
	}

	public JsonHubUnsupportedParseException(String message) {
		super(message);
	}

	public JsonHubUnsupportedParseException(Throwable cause) {
		super(cause);
	}

	public JsonHubUnsupportedParseException(String message, Throwable cause) {
		super(message, cause);
	}

}
