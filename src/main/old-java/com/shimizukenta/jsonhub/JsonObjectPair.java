package com.shimizukenta.jsonhub;

import java.io.Serializable;
import java.util.Objects;

/**
 * This class is implements of name and value pair in JSON-OBJECT.
 * 
 * @author kenta-shimizu
 *
 */
public class JsonObjectPair implements Serializable {
	
	private static final long serialVersionUID = 5500254554321018084L;
	
	private final JsonString name;
	private final JsonHub v;
	
	protected JsonObjectPair(JsonString name, JsonHub v) {
		this.name = Objects.requireNonNull(name, "JsonObjectPair nonNull \"name\"");
		this.v = Objects.requireNonNull(v, "JsonObjectPair nonNull \"value\"");
	}
	
	/**
	 * Returns name of pair.
	 * 
	 * @return name
	 */
	public JsonString name() {
		return name;
	}
	
	/**
	 * Returns value of pair.
	 * 
	 * @return value
	 */
	public JsonHub value() {
		return v;
	}
	
	@Override
	public boolean equals(Object o) {
		if ((o != null) && (o instanceof JsonObjectPair)) {
			return ((JsonObjectPair) o).name().equals(name());
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return name().hashCode();
	}
	
	@Override
	public String toString() {
		return "(\"" + name() + "\": " + value() + ")";
	}
}
