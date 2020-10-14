package com.shimizukenta.jsonhub;

import java.io.IOException;
import java.io.Writer;
import java.util.Optional;

/**
 * This class is implements of JSON literal FALSE.
 * 
 * <p>
 * Instances of this class are immutable.
 * </p>
 * 
 * @author kenta-shimizu
 *
 */
public class FalseJsonHub extends AbstractJsonHub {

	private static final long serialVersionUID = 3644504159696333609L;

	protected FalseJsonHub() {
		super();
	}
	
	@Override
	public JsonHubType type() {
		return JsonHubType.FALSE;
	}
	
	@Override
	public Optional<Boolean> optionalBoolean() {
		return Optional.of(Boolean.FALSE);
	}
	
	@Override
	public String toJson() {
		return JsonLiteral.FALSE.toString();
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
		return Boolean.FALSE.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		return (o != null) && (o instanceof FalseJsonHub);
	}
	
	@Override
	public int hashCode() {
		return type().hashCode();
	}
	
}
