package com.shimizukenta.jsonhub;

import java.util.Objects;

/**
 * JSON Literals.
 * 
 * @author kenta-shimizu
 *
 */
public enum JsonLiteral {
	
	NULL("null"),
	TRUE("true"),
	FALSE("false"),
	;
	
	private final String s;
	private JsonLiteral(String s) {
		this.s = s;
	}
	
	public boolean match(CharSequence cs) {
		return Objects.requireNonNull(cs).toString().equals(s);
	}
	
	@Override
	public String toString() {
		return s;
	}
}
