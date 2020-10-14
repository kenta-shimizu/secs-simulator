package com.shimizukenta.jsonhub;

import java.io.Serializable;
import java.util.Objects;

/**
 * This class is implements of escape/unescape JSON-String.
 * 
 * @author kenta-shimizu
 *
 */
public class JsonString implements Serializable {
	
	private static final long serialVersionUID = -2222040816285239082L;
	
	private String escaped;
	private String unescaped;
	
	protected JsonString() {
		escaped = null;
		unescaped = null;
	}
	
	/**
	 * Returns JsonString instance from escaped-String.
	 * 
	 * <p>
	 * Not accept {@code null}.<br />
	 * </p>
	 * 
	 * @param escaped String
	 * @return JsonString instance
	 */
	public static JsonString escaped(CharSequence escaped) {
		JsonString inst = new JsonString();
		inst.escaped = Objects.requireNonNull(escaped, "JsonString nonNull \"escaped\"").toString();
		return inst;
	}
	
	/**
	 * Returns JsonString instance from unescaped-String.
	 * 
	 * <p>
	 * Not accept {@code null}.<br />
	 * </p>
	 * 
	 * @param unescaped String
	 * @return JsonString instance
	 */
	public static JsonString unescaped(CharSequence unescaped) {
		JsonString inst = new JsonString();
		inst.unescaped = Objects.requireNonNull(unescaped, "JsonString nonNull \"unescaped\"").toString();
		return inst;
	}
	
	/**
	 * Returns escaped-String.
	 * 
	 * @return escaped-String
	 */
	public String escaped() {
		
		synchronized ( this ) {
			
			if ( escaped == null ) {
				escaped = JsonStringCoder.getInstance().escape(unescaped);
			}
			
			return escaped;
		}
	}
	
	/**
	 * Returns unescaped String.
	 * 
	 * @return unescaped-String
	 */
	public String unescaped() {
		
		synchronized ( this ) {
			
			if ( unescaped == null ) {
				unescaped = JsonStringCoder.getInstance().unescape(escaped);
			}
			
			return unescaped;
		}
	}
	
	/**
	 * Returns {@code unescaped().length()}.
	 * 
	 * @return {@code unescaped().length()}
	 */
	public int length() {
		return unescaped().length();
	}
	
	/**
	 * Returns {@code unescaped().isEmpty()}.
	 * 
	 * @return {@code unescaped().isEmpty()}
	 */
	public boolean isEmpty() {
		return unescaped().isEmpty();
	}
	
	/**
	 * Return {@code unescaped()}.
	 * 
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
