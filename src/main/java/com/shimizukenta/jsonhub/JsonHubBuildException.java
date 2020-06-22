package com.shimizukenta.jsonhub;

public class JsonHubBuildException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7294512897813785687L;
	
	public JsonHubBuildException() {
		super();
	}

	public JsonHubBuildException(String message) {
		super(message);
	}

	public JsonHubBuildException(Throwable cause) {
		super(cause);
	}

	public JsonHubBuildException(String message, Throwable cause) {
		super(message, cause);
	}

}
