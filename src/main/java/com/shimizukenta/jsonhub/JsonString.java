package com.shimizukenta.jsonhub;

import java.io.Serializable;
import java.util.Objects;

public class JsonString implements Serializable {
	
	private static final long serialVersionUID = -2222040816285239082L;
	
	private String escaped;
	private String unescaped;
	
	protected JsonString() {
		escaped = null;
		unescaped = null;
	}
	
	public static JsonString escaped(CharSequence escaped) {
		JsonString inst = new JsonString();
		inst.escaped = Objects.requireNonNull(escaped, "JsonString nonNull \"escaped\"").toString();
		return inst;
	}
	
	public static JsonString unescaped(CharSequence unescaped) {
		JsonString inst = new JsonString();
		inst.unescaped = Objects.requireNonNull(unescaped, "JsonString nonNull \"unescaped\"").toString();
		return inst;
	}
	
	public String escaped() {
		
		synchronized ( this ) {
			
			if ( escaped == null ) {
				escaped = JsonStringCoder.getInstance().escape(unescaped);
			}
			
			return escaped;
		}
	}
	
	public String unescaped() {
		
		synchronized ( this ) {
			
			if ( unescaped == null ) {
				unescaped = JsonStringCoder.getInstance().unescape(escaped);
			}
			
			return unescaped;
		}
	}
	
	/**
	 * 
	 * @return unescaped().length()
	 */
	public int length() {
		return unescaped().length();
	}
	
	/**
	 * 
	 * @retur nunescaped().isEmpty()
	 */
	public boolean isEmpty() {
		return unescaped().isEmpty();
	}
	
	/**
	 *  alias of unescaped()
	 */
	@Override
	public String toString() {
		return unescaped();
	}
	
	@Override
	public boolean equals(Object o) {
		if ((o != null) && (o instanceof JsonString)) {
			return o.toString().equals(toString());
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
}
