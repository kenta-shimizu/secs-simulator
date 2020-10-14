package com.shimizukenta.jsonhub;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class is parser, from JSON-String to JsonHub instance.
 * 
 * <p>
 * To get parser instance, {@link #getInstance()}.<br />
 * To parse from JSON-String to JsonHub, {@link #parse(CharSequence)}
 * or {@link #parse(Reader)}.<br />
 * </p>
 * 
 * @author kenta-shimizu
 *
 */
public class JsonHubJsonParser {

	private JsonHubJsonParser() {
		/* Nothing */
	}
	
	private static class SingletonHolder {
		private static final JsonHubJsonParser inst = new JsonHubJsonParser();
	}

	/**
	 * Returns Parser instance.
	 * 
	 * <p>
	 * This class is Singleton-pattern.<br />
	 * </p>
	 * 
	 * @return
	 */
	public static JsonHubJsonParser getInstance() {
		return SingletonHolder.inst;
	}
	
	/**
	 * Returns JsonHub instance parsing from JSON-String.
	 * 
	 * <p>
	 * Not accept {@code null}.<br />
	 * </p>
	 * 
	 * @param cs JSON-String
	 * @return parsed JsonHub
	 * @throws JsonHubParseException if parse failed
	 */
	public AbstractJsonHub parse(CharSequence cs) {
		
		try {
			
			String s = cs.toString();
			
			if ( s.trim().isEmpty() ) {
				throw new JsonHubParseException("JSON is empty");
			}
			
			return fromJson(s);
		}
		catch ( JsonHubIndexOutOfBoundsException | JsonHubNumberFormatException e ) {
			throw new JsonHubParseException(e);
		}
	}
	
	/**
	 * Returns parsed JsonHub instance from reader includes JSON-String.
	 * 
	 * @param reader includes JSON-String
	 * @return parsed JsonHub
	 * @throws JsonHubParseException if parse failed
	 * @throws IOException
	 */
	public AbstractJsonHub parse(Reader reader) throws IOException {
		
		try (
				CharArrayWriter writer = new CharArrayWriter();
				) {
			
			for ( ;; ) {
				
				int r = reader.read();
				
				if ( r < 0 ) {
					break;
				}
				
				writer.write(r);
			}
			
			return parse(writer.toString());
		}
	}
	
	
	private static class SeekCharResult {
		
		private final char c;
		private final int index;
		
		private SeekCharResult(char c, int index) {
			this.c = c;
			this.index = index;
		}
	}
	
	private static class SeekValueResult {
		
		private final AbstractJsonHub value;
		private final int endIndex;
		
		private SeekValueResult(AbstractJsonHub v, int index) {
			this.value = v;
			this.endIndex = index;
		}
	}
	
	
	private static AbstractJsonHub fromJson(String str) {
		
		SeekCharResult r = seekNextChar(str, 0);
		
		SeekValueResult vr;
		
		if ( JsonStructuralChar.QUOT.match(r.c) ) {
			
			vr = fromJsonStringValue(str, r.index);
			
		} else if ( JsonStructuralChar.ARRAY_BIGIN.match(r.c) ) {
			
			vr = fromJsonArrayValue(str, r.index);
			
		} else if ( JsonStructuralChar.OBJECT_BIGIN.match(r.c) ) {
			
			vr = fromJsonObjectValue(str, r.index);
			
		} else {
			
			vr = fromJsonNotStructuralValue(str, r.index);
			
			if ( vr.endIndex < 0 ) {
				
				return vr.value;
				
			} else {
				
				throw new JsonHubParseException("Value is not Single \"" + str + "\"");
			}
		}
		
		if ( seekNextChar(str, vr.endIndex).index < 0 ) {
			
			return vr.value;
			
		} else {
			
			throw new JsonHubParseException("Value is not Single \"" + str + "\"");
		}
	}
	
	private static SeekValueResult fromJsonNotStructuralValue(String str, int fromIndex) {
		
		SeekCharResult r = seekNextEndDelimiter(str, fromIndex);
		
		String s;
		
		if ( r.index < 0 ) {
			
			if ( fromIndex >= 0 ) {
				
				s = str.substring(fromIndex).trim();
			
			} else {
				
				throw new JsonHubIndexOutOfBoundsException();
			}
			
		} else {
			
			s = str.substring(fromIndex, r.index).trim();
		}
		
		if ( JsonLiteral.NULL.match(s) ) {
			
			return new SeekValueResult(
					JsonHubBuilder.getInstance().nullValue()
					, r.index);
			
		} else if ( JsonLiteral.TRUE.match(s) ) {
			
			return new SeekValueResult(
					JsonHubBuilder.getInstance().trueValue()
					, r.index);
			
		} else if ( JsonLiteral.FALSE.match(s) ) {
			
			return new SeekValueResult(
					JsonHubBuilder.getInstance().falseValue()
					, r.index);
			
		} else {
			
			return new SeekValueResult(
					fromJsonNumberValue(s)
					, r.index);
		}
	}
	
	private static JsonString fromJsonString(String str) {
		String s = str.trim();
		return JsonString.escaped(s.substring(1, (s.length() - 1)));
	}
	
	private static SeekValueResult fromJsonStringValue(String str, int fromIndex) {
		
		int endIndex = seekEndIndexOfString(str, fromIndex);
		
		return new SeekValueResult(
				JsonHubBuilder.getInstance().string(
						fromJsonString(str.substring(fromIndex, endIndex)))
				, endIndex);
	}
	
	private static SeekValueResult fromJsonArrayValue(String str, int fromIndex) {
		
		final List<AbstractJsonHub> ll = new ArrayList<>();
		
		boolean first = true;
		
		for (int i = (fromIndex + 1), len = str.length(); i < len;) {
			
			{
				SeekCharResult r  = seekNextChar(str, i);
				
				if ( first ) {
					
					first = false;
					
					if ( JsonStructuralChar.ARRAY_END.match(r.c) ) {
						
						return new SeekValueResult(
								JsonHubBuilder.getInstance().array(ll)
								, r.index + 1);
						
					}
				}
				
				if ( JsonStructuralChar.QUOT.match(r.c) ) {
					
					SeekValueResult vr = fromJsonStringValue(str, r.index);
					ll.add(vr.value);
					i = vr.endIndex;
					
				} else if ( JsonStructuralChar.ARRAY_BIGIN.match(r.c) ) {
					
					SeekValueResult vr = fromJsonArrayValue(str, r.index);
					ll.add(vr.value);
					i = vr.endIndex;
					
				} else if ( JsonStructuralChar.OBJECT_BIGIN.match(r.c) ) {
					
					SeekValueResult vr = fromJsonObjectValue(str, r.index);
					ll.add(vr.value);
					i = vr.endIndex;
					
				} else if (JsonStructuralChar.SEPARATOR_NAME.match(r.c) || JsonStructuralChar.SEPARATOR_VALUE.match(r.c)) {
					
					throw new JsonHubParseException("Value is empty \"" + str + "\"");
					
				} else {
					
					SeekValueResult vr = fromJsonNotStructuralValue(str, r.index);
					
					if ( vr.endIndex < 0 ) {
						
						throw new JsonHubParseException("Not found end-of-value. index: " + r.index + " \"" + str + "\"");
						
					} else {
						
						ll.add(vr.value);
						i = vr.endIndex;
					}
				}
			}
			
			{
				SeekCharResult r  = seekNextChar(str, i);
				
				if ( JsonStructuralChar.SEPARATOR_VALUE.match(r.c) ) {
					
					i = r.index + 1;
					
				} else if ( JsonStructuralChar.ARRAY_END.match(r.c) ) {
					
					return new SeekValueResult(
							JsonHubBuilder.getInstance().array(ll)
							, r.index + 1);
					
				} else {
					
					throw new JsonHubParseException("Not found end-of-value. index: " + r.index + " \"" + str + "\"");
				}
			}
		}
		
		throw new JsonHubParseException("Not found end-of-ARRAY. fromIndex: " + fromIndex + " \"" + str + "\"");
	}
	
	private static SeekValueResult fromJsonObjectValue(String str, int fromIndex) {
		
		final JsonHubBuilder jhb = JsonHubBuilder.getInstance();
		
		final Collection<JsonObjectPair> pairs = new ArrayList<>();
		
		boolean first = true;
		
		for (int i = (fromIndex + 1), len = str.length(); i < len;) {
			
			if ( first ) {
				
				first = false;
				
				SeekCharResult r  = seekNextChar(str, i);
				
				if ( JsonStructuralChar.OBJECT_END.match(r.c) ) {
					
					return new SeekValueResult(
							jhb.object(pairs)
							, r.index + 1);
				}
				
				i = r.index;
			}
			
			int nameStartIndex = seekIndexOfNextQuot(str, i);
			int nameEndIndex = seekEndIndexOfString(str, nameStartIndex);
			
			JsonString js = fromJsonString(str.substring(nameStartIndex, nameEndIndex));
			
			i = seekIndexOfNextColon(str, nameEndIndex) + 1;
			
			{
				SeekCharResult r  = seekNextChar(str, i);
				
				if ( JsonStructuralChar.QUOT.match(r.c) ) {
					
					SeekValueResult vr = fromJsonStringValue(str, r.index);
					pairs.add(jhb.pair(js, vr.value));
					i = vr.endIndex;
					
				} else if ( JsonStructuralChar.ARRAY_BIGIN.match(r.c) ) {
					
					SeekValueResult vr = fromJsonArrayValue(str, r.index);
					pairs.add(jhb.pair(js, vr.value));
					i = vr.endIndex;
					
				} else if ( JsonStructuralChar.OBJECT_BIGIN.match(r.c) ) {
					
					SeekValueResult vr = fromJsonObjectValue(str, r.index);
					pairs.add(jhb.pair(js, vr.value));
					i = vr.endIndex;
					
				} else if (JsonStructuralChar.SEPARATOR_NAME.match(r.c) || JsonStructuralChar.SEPARATOR_VALUE.match(r.c)) {
					
					throw new JsonHubParseException("Value is empty. index: " + r.index + " \"" + str + "\"");
					
				} else {
					
					SeekValueResult vr = fromJsonNotStructuralValue(str, r.index);
					
					if ( vr.endIndex < 0 ) {
						
						throw new JsonHubParseException("Not found end-of-value. index: " + r.index + " \"" + str + "\"");
						
					} else {
						
						pairs.add(jhb.pair(js, vr.value));
						i = vr.endIndex;
					}
				}
			}
			
			{
				SeekCharResult r  = seekNextChar(str, i);
				
				if ( JsonStructuralChar.SEPARATOR_VALUE.match(r.c) ) {
					
					i = r.index + 1;
					
				} else if ( JsonStructuralChar.OBJECT_END.match(r.c) ) {
					
					return new SeekValueResult(
							jhb.object(pairs)
							, r.index + 1);
					
				} else {
					
					throw new JsonHubParseException("Not found end-of-value. index: " + r.index + " \"" + str + "\"");
				}
			}
		}
		
		throw new JsonHubParseException("Not found end-of-OBJECT. fromIndex: " + fromIndex + " \"" + str + "\"");
	}
	
	private static NumberJsonHub fromJsonNumberValue(String str) {
		return JsonHubBuilder.getInstance().number(str);
	}
	
	private static int seekIndexOfNextQuot(String str, int fromIndex) {
		SeekCharResult r = seekNextChar(str, fromIndex);
		if ((r.index >= 0) && JsonStructuralChar.QUOT.match(r.c) ) {
			return r.index;
		} else {
			throw new JsonHubParseException("Not found Quot. fromIndex: " + fromIndex + " \"" + str + "\"");
		}
	}
	
	private static int seekIndexOfNextColon(String str, int fromIndex) {
		SeekCharResult r = seekNextChar(str, fromIndex);
		if ((r.index >= 0) && JsonStructuralChar.SEPARATOR_NAME.match(r.c)) {
			return r.index;
		} else {
			throw new JsonHubParseException("Not found \":\" fromIndex: " + fromIndex + " \"" + str + "\"");
		}
	}
	
	private static int seekEndIndexOfString(String str, int fromIndex) {
		
		for (int i = (fromIndex + 1), len = str.length(); i < len; ++i) {
			
			char c = str.charAt(i);
			
			if ( JsonStructuralChar.ESCAPE.match(c) ) {
				++i;
				continue;
			}
			
			if ( JsonStructuralChar.QUOT.match(c) ) {
				return i + 1;
			}
		}
		
		throw new JsonHubParseException("Not found end-of-STRING. fromIndex: " + fromIndex + " \"" + str + "\"");
	}
	
	
	private static final char C_WS_MAX = 0x0020;
	
	private static SeekCharResult seekNextChar(String str, int fromIndex) {
		
		if ( fromIndex >= 0 ) {
			
			for (int i = fromIndex, len = str.length(); i < len; ++i) {
				
				char c = str.charAt(i);
				
				if ( c > C_WS_MAX ) {
					return new SeekCharResult(c, i);
				}
			}
		}
		
		return new SeekCharResult(C_WS_MAX, -1);
	}
	
	private static final JsonStructuralChar[] delimiters = new JsonStructuralChar[]{
			JsonStructuralChar.SEPARATOR_VALUE,
			JsonStructuralChar.ARRAY_END,
			JsonStructuralChar.OBJECT_END
	};
	
	private static SeekCharResult seekNextEndDelimiter(String str, int fromIndex) {
		
		if ( fromIndex >= 0 ) {
			
			for (int i = fromIndex, len = str.length(); i < len; ++i) {
				
				char c = str.charAt(i);
				
				for (JsonStructuralChar d : delimiters ) {
					
					if ( d.match(c) ) {
						return new SeekCharResult(c, i);
					}
				}
			}
		}
		
		return new SeekCharResult(C_WS_MAX, -1);
	}
	
}
