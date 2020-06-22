package com.shimizukenta.jsonhub;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class JsonHubBuilder {

	private JsonHubBuilder() {
		/* Nothing */
	}
	
	private static class SingletonHolder {
		private static final JsonHubBuilder inst = new JsonHubBuilder();
		
		private static final JsonString emptyString = JsonString.escaped("");
		private static final NullJsonHub nullValue = new NullJsonHub();
		private static final TrueJsonHub trueValue = new TrueJsonHub();
		private static final FalseJsonHub falseValue = new FalseJsonHub();
		private static final ArrayJsonHub emptyArrayValue = new ArrayJsonHub(Collections.emptyList());
		private static final ObjectJsonHub emptyObjectValue = new ObjectJsonHub(Collections.emptyList());
		private static final StringJsonHub emptyStringValue = new StringJsonHub(emptyString);
	}
	
	public static JsonHubBuilder getInstance() {
		return SingletonHolder.inst;
	}
	
	/**
	 * 
	 * @return NullJsonHub
	 */
	public NullJsonHub nullValue() {
		return SingletonHolder.nullValue;
	}
	
	/**
	 * 
	 * @return TrueJsonHub
	 */
	public TrueJsonHub trueValue() {
		return SingletonHolder.trueValue;
	}
	
	/**
	 * 
	 * @return FalseJsonHub
	 */
	public FalseJsonHub falseValue() {
		return SingletonHolder.falseValue;
	}
	
	public NumberJsonHub build(int v) {
		return number(v);
	}
	
	public NumberJsonHub build(long v) {
		return number(v);
	}
	
	public NumberJsonHub build(float v) {
		return number(v);
	}
	
	public NumberJsonHub build(double v) {
		return number(v);
	}
	
	public AbstractJsonHub build(boolean v) {
		if ( v ) {
			return trueValue();
		} else {
			return falseValue();
		}
	}
	
	/**
	 * 
	 * @param v
	 * @return NULL, TRUE, FALSE, STRING, NUMBER
	 */
	public JsonHub build(Object v) {
		
		if ( v == null ) {
			
			return nullValue();
			
		} else {
			
			if ( v instanceof Boolean ) {
				build(((Boolean) v).booleanValue());
			}
			
			if ( v instanceof CharSequence ) {
				return string((CharSequence)v);
			}
			
			if ( v instanceof JsonString ) {
				return string((JsonString)v);
			}
			
			if ( v instanceof Number ) {
				return number((Number)v);
			}
			
			throw new JsonHubBuildException("build failed \"" + v.toString() + "\"");
		}
	}
	
	
	/**
	 * 
	 * @param cs
	 * @return NumberJsonHub
	 */
	public NumberJsonHub number(CharSequence cs) {
		return new NumberJsonHub(cs);
	}
	
	public NumberJsonHub number(Number n) {
		return new NumberJsonHub(n);
	}
	
	public NumberJsonHub number(int n) {
		return number(Integer.valueOf(n));
	}
	
	public NumberJsonHub number(long n) {
		return number(Long.valueOf(n));
	}
	
	public NumberJsonHub number(float n) {
		return number(Float.valueOf(n));
	}
	
	public NumberJsonHub number(double n) {
		return number(Double.valueOf(n));
	}
	
	
	/**
	 * 
	 * @param v
	 * @return STRING
	 */
	public StringJsonHub string(CharSequence v) {
		if ( Objects.requireNonNull(v).toString().isEmpty() ) {
			return SingletonHolder.emptyStringValue;
		} else {
			return string(JsonString.unescaped(v));
		}
	}
	
	/**
	 * 
	 * @param v
	 * @return STRING
	 */
	public StringJsonHub string(JsonString v) {
		return new StringJsonHub(v);
	}
	
	/**
	 * 
	 * @return empty ARRAY
	 */
	public ArrayJsonHub array() {
		return emptyArray();
	}
	
	/**
	 * 
	 * @param v
	 * @return ARRAY
	 */
	public ArrayJsonHub array(JsonHub... v) {
		return new ArrayJsonHub(Arrays.asList(v));
	}
	
	/**
	 * 
	 * @param v
	 * @return ARRAY
	 */
	public ArrayJsonHub array(List<? extends JsonHub> v) {
		if ( v.isEmpty() ) {
			return emptyArray();
		} else {
			return new ArrayJsonHub(v);
		}
	}
	
	public ArrayJsonHub emptyArray() {
		return SingletonHolder.emptyArrayValue;
	}
	
	
	/**
	 * 
	 * @return empty OBJECT
	 */
	public ObjectJsonHub object() {
		return emptyObject();
	}
	
	/**
	 * 
	 * @param v
	 * @return OBJECT
	 */
	public ObjectJsonHub object(JsonObjectPair... v) {
		return object(Arrays.asList(v));
	}

	/**
	 * 
	 * @param v
	 * @return OBJECT
	 */
	public ObjectJsonHub object(Collection<? extends JsonObjectPair> v) {
		if ( Objects.requireNonNull(v).isEmpty() ) {
			return emptyObject();
		} else {
			return new ObjectJsonHub(v);
		}
	}
	
	/**
	 * 
	 * @param v
	 * @return OBJECT
	 */
	public ObjectJsonHub object(Map<? extends JsonString, ? extends JsonHub> v) {
		
		final Collection<JsonObjectPair> pairs = new ArrayList<>();
		
		Objects.requireNonNull(v).forEach((name, value) -> {
			pairs.add(pair(name, value));
		});
			
		return object(pairs);
	}
	
	public ObjectJsonHub emptyObject() {
		return SingletonHolder.emptyObjectValue;
	}
	
	/**
	 * 
	 * @param name
	 * @param value
	 * @return JsonObjectPair
	 * @throws JsonHubBuildException
	 */
	public JsonObjectPair pair(JsonString name, Object v) {
		if ((v != null) && (v instanceof JsonHub)) {
			return new JsonObjectPair(name, (JsonHub)v);
		} else {
			return new JsonObjectPair(name, build(v));
		}
	}
	
	public JsonObjectPair pair(JsonString name, int v) {
		return new JsonObjectPair(name, build(v));
	}
	
	public JsonObjectPair pair(JsonString name, long v) {
		return new JsonObjectPair(name, build(v));
	}
	
	public JsonObjectPair pair(JsonString name, float v) {
		return new JsonObjectPair(name, build(v));
	}
	
	public JsonObjectPair pair(JsonString name, double v) {
		return new JsonObjectPair(name, build(v));
	}
	
	public JsonObjectPair pair(JsonString name, boolean v) {
		return new JsonObjectPair(name, build(v));
	}
	
	/**
	 * 
	 * @param name
	 * @param v
	 * @return JsonObjectPair
	 * @throws JsonHubBuildException
	 */
	public JsonObjectPair pair(CharSequence name, Object v) {
		return pair(JsonString.unescaped(name), v);
	}
	
	public JsonObjectPair pair(CharSequence name, int v) {
		return pair(JsonString.unescaped(name), v);
	}
	
	public JsonObjectPair pair(CharSequence name, long v) {
		return pair(JsonString.unescaped(name), v);
	}
	
	public JsonObjectPair pair(CharSequence name, float v) {
		return pair(JsonString.unescaped(name), v);
	}
	
	public JsonObjectPair pair(CharSequence name, double v) {
		return pair(JsonString.unescaped(name), v);
	}
	
	public JsonObjectPair pair(CharSequence name, boolean v) {
		return pair(JsonString.unescaped(name), v);
	}
	
	
	/**
	 * 
	 * @param json
	 * @return JsonHub
	 * @throws JsonHubParseException
	 */
	public JsonHub fromJson(CharSequence v) {
		return JsonHubJsonParser.getInstance().parse(v);
	}
	
	/**
	 * 
	 * @param reader
	 * @return JsonHub
	 * @throws IOException
	 * @throws JsonHubParseException
	 */
	public JsonHub fromJson(Reader reader) throws IOException {
		return JsonHubJsonParser.getInstance().parse(reader);
	}
	
}
