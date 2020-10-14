package com.shimizukenta.jsonhub;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;


/**
 * This class provides Compact-JSON-pretty-printer, no line-separate and no space.
 * 
 * @author kenta-shimizu
 *
 */
public class JsonHubCompactPrettyPrinter extends AbstractJsonHubPrettyPrinter {
	
	protected JsonHubCompactPrettyPrinter() {
		super(JsonHubPrettyPrinterConfig.defaultConfig());
	}
	
	private static class SingletonHolder {
		private static final JsonHubCompactPrettyPrinter inst = new JsonHubCompactPrettyPrinter();
	}
	
	/**
	 * Returns Compact-JSON-pretty-printer instance.
	 * 
	 * <p>
	 * This class is Singleton-pattern.<br />
	 * </p>
	 * 
	 * @return Compact-JSON-pretty-printer instance
	 */
	public static JsonHubCompactPrettyPrinter getInstance() {
		return SingletonHolder.inst;
	}
	
	@Override
	public void print(JsonHub v, Writer writer) throws IOException {
		
		synchronized ( this ) {
			_print(v, writer);
		}
	}
	
	private void _print(JsonHub v, Writer writer) throws IOException {
		
		switch ( v.type() ) {
		case NULL:
		case TRUE:
		case FALSE:
		case STRING:
		case NUMBER: {
			
			v.toJson(writer);
			break;
		}
		case ARRAY: {
			
			writer.write(JsonStructuralChar.ARRAY_BIGIN.str());
			
			boolean f = false;
			
			for (JsonHub jh : v.values()) {
				
				if ( f ) {
					writer.write(JsonStructuralChar.SEPARATOR_VALUE.str());
				} else {
					f = true;
				}
				
				_print(jh, writer);
			}
			
			writer.write(JsonStructuralChar.ARRAY_END.str());
			break;
		}
		case OBJECT: {
			
			writer.write(JsonStructuralChar.OBJECT_BIGIN.str());
			
			Collection<JsonObjectPair> pairs = objectPairs((ObjectJsonHub)v);
			
			boolean f = false;
			
			for ( JsonObjectPair pair : pairs ) {
				
				if ( f ) {
					writer.write(JsonStructuralChar.SEPARATOR_VALUE.str());
				} else {
					f = true;
				}
				
				writer.write(JsonStructuralChar.QUOT.str());
				writer.write(pair.name().escaped());
				writer.write(JsonStructuralChar.QUOT.str());
				writer.write(JsonStructuralChar.SEPARATOR_NAME.str());
				
				_print(pair.value(), writer);
			}
			
			writer.write(JsonStructuralChar.OBJECT_END.str());
			break;
		}
		}
	}
	
	protected Collection<JsonObjectPair> objectPairs(ObjectJsonHub ojh) {
		return ojh.objectPairs();
	}
	
}
