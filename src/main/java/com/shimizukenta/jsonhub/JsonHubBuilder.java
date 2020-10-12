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

/**
 * This class is implements of building JsonHub instance.
 * 
 * <p>
 * This class is used in {@link JsonHubJsonParser}, {@link JsonHubFromPojoParser}.<br />
 * </p>
 * <p>
 * To build NullJsonHub instance, {@link #nullValue()}, {@link #build(Object)}.<br />
 * To build TrueJsonHub instance, {@link #trueValue()}, {@link #build(Object)}.<br />
 * To build FalseJsonHub instance, {@link #falseValue()}, {@link #build(Object)}.<br />
 * To build StringJsonHub instance, {@link #string(CharSequence)}, {@link #string(JsonString)}, {@link #build(Object)}.<br />
 * To build NumberJsonHub instance, {@link #number(int)}, {@link #number(long)}, {@link #number(float)}, {@link #number(double)},
 * {@link #number(Number)}, {@link #number(CharSequence)},
 * {@link #build(int)}, {@link #build(long)}, {@link #build(float)}, {@link #build(double)}, {@link #build(Object)}.<br />
 * To build ArrayJsonHub instance, {@link #array()}, {@link #array(JsonHub...)}, {@link #array(List)}, {@link #emptyArray()}.<br />
 * To build ObjectJsonHub instance, {@link #object()}, {@link #object(Collection)}, {@link #object(JsonObjectPair...)}, {@link #object(Map)}, {@link #emptyObject()}.<br />
 * </p>
 * <p>
 * To build JsonObjectPair instance, {@link #pair(CharSequence, boolean)},
 * {@link #pair(CharSequence, int)}, {@link #pair(CharSequence, long)},
 * {@link #pair(CharSequence, float)}, {@link #pair(CharSequence, double)},
 * {@link #pair(CharSequence, Object)},
 * {@link #pair(JsonString, boolean)},
 * {@link #pair(JsonString, int)}, {@link #pair(JsonString, long)},
 * {@link #pair(JsonString, float)}, {@link #pair(JsonString, double)},
 * {@link #pair(JsonString, Object)}.<br />
 * </p>
 * 
 * @author kenta-shimizu
 *
 */
public class JsonHubBuilder {
	
	protected JsonHubBuilder() {
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
	
	/**
	 * Returns Builder instance.
	 * 
	 * <p>
	 * This class is Singleton-pattern.
	 * </p>
	 * 
	 * @return builder instance
	 */
	public static JsonHubBuilder getInstance() {
		return SingletonHolder.inst;
	}
	
	/**
	 * Returns NullJsonHub instance.
	 * 
	 * <p>
	 * This instance is Singleton-pattern.<br />
	 * Returned instances are all the same.<br />
	 * </p>
	 * 
	 * @return NullJsonHub instance
	 */
	public NullJsonHub nullValue() {
		return SingletonHolder.nullValue;
	}
	
	/**
	 * Returns TrueJsonHub instance.
	 * 
	 * <p>
	 * This instance is Singleton-pattern.<br />
	 * Returned instances are all the same.<br />
	 * </p>
	 * 
	 * @return TrueJsonHub instance
	 */
	public TrueJsonHub trueValue() {
		return SingletonHolder.trueValue;
	}
	
	/**
	 * Returns FalseJsonHub instance.
	 * 
	 * <p>
	 * This instance is Singleton-pattern.<br />
	 * Returned instances are all the same.<br />
	 * </p>
	 * 
	 * @return FalseJsonHub instance
	 */
	public FalseJsonHub falseValue() {
		return SingletonHolder.falseValue;
	}
	
	/**
	 * Returns NumberJsonHub instance of int-value.
	 * 
	 * @param v
	 * @return NumberJsonHub instance of int-value
	 */
	public NumberJsonHub build(int v) {
		return number(v);
	}
	
	/**
	 * Returns NumberJsonHub instance of long-value.
	 * 
	 * @param v
	 * @return NumberJsonHub instance of long-value
	 */
	public NumberJsonHub build(long v) {
		return number(v);
	}
	
	/**
	 * Returns NumberJsonHub instance of float-value.
	 * 
	 * @param v
	 * @return NumberJsonHub instance of float-value
	 */
	public NumberJsonHub build(float v) {
		return number(v);
	}
	
	/**
	 * Returns NumberJsonHub instance of double-value.
	 * 
	 * @param v
	 * @return NumberJsonHub instance of double-value
	 */
	public NumberJsonHub build(double v) {
		return number(v);
	}
	
	/**
	 * Returns TrueJsonHub or FalseJsonHub instance.
	 * 
	 * @param v
	 * @return TrueJsonHub instance if true, and FalseJsonHub instance otherwise
	 */
	public AbstractJsonHub build(boolean v) {
		if ( v ) {
			return trueValue();
		} else {
			return falseValue();
		}
	}
	
	/**
	 * Returns AbstractJsonHub instance (NULL, TRUE, FALSE, STRING or NUMBER).
	 * 
	 * <p>
	 * If {@code v == null}, return NullJsonHub instance.<br />
	 * If {@code v instanceof Boolean}, return TrueJsonHub or FalseJsonHub instance.<br />
	 * If {@code v instanceof CharSequence}, return StringJsonHub instance.<br />
	 * If {@code v instanceof JsonString}, return StringJsonHub instance.<br />
	 * If {@code v instanceof Number}, return NumberJsonHub instance.<br />
	 * </p>
	 * 
	 * @param v
	 * @return AbstractJsonHub (NULL, TRUE, FALSE, STRING or NUMBER)
	 * @throws JsonHubBuildException if type is unsupported
	 */
	public AbstractJsonHub build(Object v) {
		
		if ( v == null ) {
			
			return nullValue();
			
		} else {
			
			if ( v instanceof Boolean ) {
				return build(((Boolean) v).booleanValue());
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
	 * Returns NumberJsonHub instance from JSON-String.
	 * 
	 * <p>
	 * Not accept {@code null}.<br />
	 * </p>
	 * 
	 * @param cs Number-String
	 * @return NumberJsonHub
	 * @throws JsonHubNumberFormatException
	 */
	public NumberJsonHub number(CharSequence cs) {
		return new NumberJsonHub(cs);
	}
	
	/**
	 * Returns NumberJsonHub instance.
	 * 
	 * <p>
	 * Not accept {@code null}.<br />
	 * </p>
	 * 
	 * @param n
	 * @return NumberJsonHub instance
	 */
	public NumberJsonHub number(Number n) {
		return new NumberJsonHub(n);
	}
	
	/**
	 * Returns NumberJsonHub instance of int-value.
	 * 
	 * @param n
	 * @return NumberJsonHub instance of int-value
	 */
	public NumberJsonHub number(int n) {
		return number(Integer.valueOf(n));
	}
	
	/**
	 * Returns NumberJsonHub instance of long-value.
	 * 
	 * @param n
	 * @return NumberJsonHub instance of long-value
	 */
	public NumberJsonHub number(long n) {
		return number(Long.valueOf(n));
	}
	
	/**
	 * Returns NumberJsonHub instance of float-value.
	 * 
	 * @param n
	 * @return NumberJsonHub instance of float-value
	 */
	public NumberJsonHub number(float n) {
		return number(Float.valueOf(n));
	}
	
	/**
	 * Returns NumberJsonHub instance of double-value.
	 * 
	 * @param n
	 * @return NumberJsonHub instance of double-value
	 */
	public NumberJsonHub number(double n) {
		return number(Double.valueOf(n));
	}
	
	
	/**
	 * Returns StringJsonHub from JSON-String.
	 * 
	 * <p>
	 * Not accept {@code null}.<br />
	 * </p>
	 * 
	 * @param v
	 * @return StringJsonHub
	 */
	public StringJsonHub string(CharSequence v) {
		if ( Objects.requireNonNull(v).toString().isEmpty() ) {
			return SingletonHolder.emptyStringValue;
		} else {
			return string(JsonString.unescaped(v));
		}
	}
	
	/**
	 * Returns StringJsonHub from JsonString.
	 * 
	 * <p>
	 * Not accept {@code null}.<br />
	 * </p>
	 * 
	 * @param v
	 * @return StringJsonHub
	 */
	public StringJsonHub string(JsonString v) {
		return new StringJsonHub(v);
	}
	
	/**
	 * Returns ArrayJsonHub instance, values is empty.
	 * 
	 * <p>
	 * This instance is Singleton-pattern.<br />
	 * Returned instances are all the same.<br />
	 * </p>
	 * 
	 * @return empty-ArrayJsonHub instance
	 */
	public ArrayJsonHub array() {
		return emptyArray();
	}
	
	/**
	 * Returns ArrayJsonHub instance.
	 * 
	 * @param values
	 * @return ArrayJsonHub instance
	 */
	public ArrayJsonHub array(JsonHub... values) {
		return new ArrayJsonHub(Arrays.asList(values));
	}
	
	/**
	 * Returns ArrayJsonHub instance.
	 * 
	 * @param values
	 * @return ArrayJsonHub instance
	 */
	public ArrayJsonHub array(List<? extends JsonHub> values) {
		if ( Objects.requireNonNull(values).isEmpty() ) {
			return emptyArray();
		} else {
			return new ArrayJsonHub(values);
		}
	}
	
	/**
	 * Returns ArrayJsonHub instance, values is empty.
	 * 
	 * <p>
	 * This instance is Singleton-pattern.<br />
	 * Returned instances are all the same.<br />
	 * </p>
	 * 
	 * @return empty-ArrayJsonHub instance
	 */
	public ArrayJsonHub emptyArray() {
		return SingletonHolder.emptyArrayValue;
	}
	
	
	/**
	 * Returns ObjectJsonHub instance, Object pairs is empty.
	 * 
	 * <p>
	 * This instance is Singleton-pattern.<br />
	 * Returned instances are all the same.<br />
	 * </p>
	 * 
	 * @return empty-ObjectJsonHub instance
	 */
	public ObjectJsonHub object() {
		return emptyObject();
	}
	
	/**
	 * Returns ObjectJsonHub instance.
	 * 
	 * @param pairs
	 * @return ObjectJsonHub instance
	 */
	public ObjectJsonHub object(JsonObjectPair... pairs) {
		return object(Arrays.asList(pairs));
	}

	/**
	 * Returns ObjectJsonHub instance.
	 * 
	 * @param pairs
	 * @return ObjectJsonHub instance
	 */
	public ObjectJsonHub object(Collection<? extends JsonObjectPair> pairs) {
		if ( Objects.requireNonNull(pairs).isEmpty() ) {
			return emptyObject();
		} else {
			return new ObjectJsonHub(pairs);
		}
	}
	
	/**
	 * Returns ObjectJsonHub instance.
	 * 
	 * @param map
	 * @return ObjectJsonHub instance
	 */
	public ObjectJsonHub object(Map<? extends JsonString, ? extends JsonHub> map) {
		
		final Collection<JsonObjectPair> pairs = new ArrayList<>();
		
		Objects.requireNonNull(map).forEach((name, value) -> {
			pairs.add(pair(name, value));
		});
			
		return object(pairs);
	}
	
	/**
	 * Returns ObjectJsonHub instance, Object pairs is empty.
	 * 
	 * <p>
	 * This instance is Singleton-pattern.<br />
	 * Returned instances are all the same.<br />
	 * </p>
	 * 
	 * @return empty-ObjectJsonHub instance
	 */
	public ObjectJsonHub emptyObject() {
		return SingletonHolder.emptyObjectValue;
	}
	
	/**
	 * Returns JsonObjectPair instance.
	 * 
	 * @param name
	 * @param value
	 * @return JsonObjectPair
	 * @throws JsonHubBuildException if unsupported Object type
	 */
	public JsonObjectPair pair(JsonString name, Object value) {
		if ((value != null) && (value instanceof JsonHub)) {
			return new JsonObjectPair(name, (JsonHub)value);
		} else {
			return new JsonObjectPair(name, build(value));
		}
	}
	
	/**
	 * Returns JsonObjectPair instance.
	 * 
	 * @param name
	 * @param value
	 * @return JsonObjectPair
	 */
	public JsonObjectPair pair(JsonString name, int value) {
		return new JsonObjectPair(name, number(value));
	}
	
	/**
	 * Returns JsonObjectPair instance.
	 * 
	 * @param name
	 * @param value
	 * @return JsonObjectPair
	 */
	public JsonObjectPair pair(JsonString name, long value) {
		return new JsonObjectPair(name, number(value));
	}
	
	/**
	 * Returns JsonObjectPair instance.
	 * 
	 * @param name
	 * @param value
	 * @return JsonObjectPair
	 */
	public JsonObjectPair pair(JsonString name, float value) {
		return new JsonObjectPair(name, number(value));
	}
	
	/**
	 * Returns JsonObjectPair instance.
	 * 
	 * @param name
	 * @param value
	 * @return JsonObjectPair
	 */
	public JsonObjectPair pair(JsonString name, double value) {
		return new JsonObjectPair(name, number(value));
	}
	
	/**
	 * Returns JsonObjectPair instance.
	 * 
	 * @param name
	 * @param value
	 * @return JsonObjectPair
	 */
	public JsonObjectPair pair(JsonString name, boolean value) {
		return new JsonObjectPair(name, build(value));
	}
	
	/**
	 * Returns JsonObjectPair instance.
	 * 
	 * @param name
	 * @param value
	 * @return JsonObjectPair
	 * @throws JsonHubBuildException if unsupported Object type
	 */
	public JsonObjectPair pair(CharSequence name, Object value) {
		return pair(JsonString.unescaped(name), value);
	}
	
	/**
	 * Returns JsonObjectPair instance.
	 * 
	 * @param name
	 * @param value
	 * @return JsonObjectPair
	 */
	public JsonObjectPair pair(CharSequence name, int value) {
		return pair(JsonString.unescaped(name), value);
	}
	
	/**
	 * Returns JsonObjectPair instance.
	 * 
	 * @param name
	 * @param value
	 * @return JsonObjectPair
	 */
	public JsonObjectPair pair(CharSequence name, long value) {
		return pair(JsonString.unescaped(name), value);
	}
	
	/**
	 * Returns JsonObjectPair instance.
	 * 
	 * @param name
	 * @param value
	 * @return JsonObjectPair
	 */
	public JsonObjectPair pair(CharSequence name, float value) {
		return pair(JsonString.unescaped(name), value);
	}
	
	/**
	 * Returns JsonObjectPair instance.
	 * 
	 * @param name
	 * @param value
	 * @return JsonObjectPair
	 */
	public JsonObjectPair pair(CharSequence name, double value) {
		return pair(JsonString.unescaped(name), value);
	}
	
	/**
	 * Returns JsonObjectPair instance.
	 * 
	 * @param name
	 * @param value
	 * @return JsonObjectPair
	 */
	public JsonObjectPair pair(CharSequence name, boolean value) {
		return pair(JsonString.unescaped(name), value);
	}
	
	/**
	 * Returns parsed JsonHub instance from JSON-String.
	 * 
	 * <p>
	 * Not accept {@code null}.<br />
	 * </p>
	 * 
	 * @param v 
	 * @return parsed JsonHub instance
	 * @throws JsonHubParseException if parse failed
	 */
	public JsonHub fromJson(CharSequence v) {
		return JsonHubJsonParser.getInstance().parse(v);
	}
	
	/**
	 * Returns parsed JsonHub instance from Reader includes JSON-String.
	 * 
	 * @param reader includes JSON-String
	 * @return parsed JsonHub instance
	 * @throws IOException
	 * @throws JsonHubParseException if parse failed
	 */
	public JsonHub fromJson(Reader reader) throws IOException {
		return JsonHubJsonParser.getInstance().parse(reader);
	}
	
}
