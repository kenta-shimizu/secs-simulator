package com.shimizukenta.jsonhub;

/**
 * JSON value types.
 * 
 * @author kenta-shimizu
 *
 */
public enum JsonHubType {

	NULL,
	TRUE,
	FALSE,
	STRING,
	NUMBER,
	ARRAY,
	OBJECT,
	;
	
	private JsonHubType() {
		/* Nothing */
	}
	
}
