package com.shimizukenta.jsonhub;

import java.io.IOException;
import java.io.Writer;
import java.util.Optional;

/**
 * This class is implements of JSON literal TRUE.
 * 
 * <p>
 * Instances of this class are immutable.
 * </p>
 * 
 * @author kenta-shimizu
 *
 */
public class TrueJsonHub extends AbstractJsonHub {
	
	private static final long serialVersionUID = -788713372914256563L;
	
	protected TrueJsonHub() {
		super();
	}
	
	@Override
	public JsonHubType type() {
		return JsonHubType.TRUE;
	}
	
	@Override
	public Optional<Boolean> optionalBoolean() {
		return Optional.of(Boolean.TRUE);
	}
	
	@Override
	public String toJson() {
		return JsonLiteral.TRUE.toString();
	}
	
	@Override
	public void toJson(Writer writer) throws IOException {
		writer.write(toJson());
	}
	
	@Override
	public String toJsonExcludedNullValueInObject() {
		return toJson();
	}
	
	@Override
	public void toJsonExcludedNullValueInObject(Writer writer) throws IOException {
		toJson(writer);
	}
	
	@Override
	public String toString() {
		return Boolean.TRUE.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		return (o != null) && (o instanceof TrueJsonHub);
	}
	
	@Override
	public int hashCode() {
		return type().hashCode();
	}
	
}
