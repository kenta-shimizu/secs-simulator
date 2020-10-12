package com.shimizukenta.jsonhub;

import java.io.IOException;
import java.io.Writer;

/**
 * This class is implements of JSON literal null.
 * 
 * <p>
 * Instances of this class are immutable.
 * </p>
 * 
 * @author kenta-shimizu
 *
 */
public class NullJsonHub extends AbstractJsonHub {

	private static final long serialVersionUID = 4404413772323952148L;

	protected NullJsonHub() {
		super();
	}
	
	@Override
	public JsonHubType type() {
		return JsonHubType.NULL;
	}
	
	@Override
	public String toJson() {
		return JsonLiteral.NULL.toString();
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
		return JsonLiteral.NULL.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		return (o != null) && (o instanceof NullJsonHub);
	}
	
	@Override
	public int hashCode() {
		return type().hashCode();
	}

}
