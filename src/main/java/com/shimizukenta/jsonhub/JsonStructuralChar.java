package com.shimizukenta.jsonhub;

import java.util.Objects;

/**
 * JSON structural characters.
 * 
 * @author kenta-shimizu
 *
 */
public enum JsonStructuralChar {
	
	ESCAPE("\\"),
	QUOT("\""),
	OBJECT_BIGIN("{"),
	OBJECT_END("}"),
	ARRAY_BIGIN("["),
	ARRAY_END("]"),
	SEPARATOR_VALUE(","),
	SEPARATOR_NAME(":"),
	;
	
	private final String str;
	private final char chr;
	
	private JsonStructuralChar(String s) {
		this.str = s;
		this.chr = s.charAt(0);
	}
	
	public String str() {
		return str;
	}
	
	public boolean match(CharSequence cs) {
		return Objects.requireNonNull(cs).toString().equals(str);
	}
	
	public boolean match(char c) {
		return chr == c;
	}
}
