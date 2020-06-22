package com.shimizukenta.jsonhub;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

public interface JsonHub extends Iterable<JsonHub> {
	
	/**
	 * available if type is ARRAY or OBJECT
	 * if type is OBJECT, value is JsonHub.
	 * 
	 * @throws JsonHubUnsupportedOperationException
	 */
	@Override
	public Iterator<JsonHub> iterator();
	
	/**
	 * available if type is ARRAY or OBJECT
	 * if type is OBJECT, value is JsonHub.
	 * 
	 * @throws JsonHubUnsupportedOperationException
	 */
	@Override
	public Spliterator<JsonHub> spliterator();
	
	/**
	 * available if type is OBJECT or ARRAY.<br />
	 * if type is OBJECT, value is JsonHub.
	 * 
	 * @param Consumer<JsonHub>
	 * @throws JsonHubUnsupportedOperationException
	 */
	@Override
	public void forEach(Consumer<? super JsonHub> action);
	
	/**
	 * available if type is OBJECT or ARRAY.<br />
	 * if type is ARRAY, NAME is null.
	 * 
	 * @param BiConsumer<JsonString, JsonHub>
	 * @throws JsonHubUnsupportedOperationException
	 */
	public void forEach(BiConsumer<? super JsonString, ? super JsonHub> action);
	
	
	/**
	 * 
	 * @return JsonHubType
	 */
	public JsonHubType type();
	
	/**
	 * available if type is OBJECT or ARRAY.<br />
	 * if type is OBJECT, value is JsonHub.
	 * 
	 * @return Array values stream
	 * @throws JsonHubUnsupportedOperationException
	 */
	public Stream<JsonHub> stream();
	
	/**
	 * available if type is OBJECT
	 * 
	 * @return names
	 * @throws JsonHubUnsupportedOperationException
	 */
	public Set<JsonString> keySet();
	
	/**
	 * available if type is OBJECT or ARRAY
	 * 
	 * @return values
	 * @throws JsonHubUnsupportedOperationException
	 */
	public List<JsonHub> values();
	
	/**
	 * available if type is ARRAY
	 *  
	 * @param index
	 * @return value
	 * @throws JsonHubUnsupportedOperationException
	 */
	public JsonHub get(int index);
	
	/**
	 * available if type is OBJECT
	 * 
	 * @param name
	 * @return true if has name
	 * @throws JsonHubUnsupportedOperationException
	 */
	public boolean containsKey(CharSequence name);
	
	/**
	 * available if type is OBJECT
	 * 
	 * @param name
	 * @return value. null if not has name.
	 * @throws JsonHubUnsupportedOperationException
	 */
	public JsonHub get(CharSequence name);
	
	/**
	 * available if type is OBJECT
	 * 
	 * @param name
	 * @return emptyObject() if not exist
	 * @throws JsonHubUnsupportedOperationException
	 */
	public JsonHub getOrDefault(CharSequence name);
	
	/**
	 * available if type is OBJECT
	 * 
	 * @param name
	 * @param defaultValue
	 * @return defaultValue if not exist
	 * @throws JsonHubUnsupportedOperationException
	 */
	public JsonHub getOrDefault(CharSequence name, JsonHub defaultValue);
	
	/**
	 * available if type is OBJECT-chains
	 * 
	 * @param names
	 * @return value
	 * @throws JsonHubUnsupportedOperationException
	 */
	public JsonHub get(String... names);
	
	/**
	 * available if STRING or ARRAY or OBJECT
	 * 
	 * @return length
	 * @throws JsonHubUnsupportedOperationException
	 */
	public int length();
	
	/**
	 * available if STRING or ARRAY or OBJECT
	 * 
	 * @return true if empty
	 * @throws JsonHubUnsupportedOperationException
	 */
	public boolean isEmpty();
	
	/**
	 * 
	 * @return true if type is NULL
	 */
	public boolean isNull();
	
	/**
	 * 
	 * @return true if type is not NULL
	 */
	public boolean nonNull();
	
	/**
	 * 
	 * @return true if type is TRUE
	 */
	public boolean isTrue();
	
	/**
	 * 
	 * @return true if type is FALSE
	 */
	public boolean isFalse();
	
	/**
	 * 
	 * @return true is type is STRING
	 */
	public boolean isString();
	
	/**
	 * 
	 * @return true if type is NUMBER
	 */
	public boolean isNumber();
	
	/**
	 * 
	 * @return true if type is ARRAY
	 */
	public boolean isArray();
	
	/**
	 * 
	 * @return true if type is OBJECT
	 */
	public boolean isObject();
	
	/**
	 * 
	 * @return Optional has value if type is TRUE or FALSE
	 */
	public Optional<Boolean> optionalBoolean();
	
	/**
	 * 
	 * @return Optional has value if type is NUMBER
	 */
	public OptionalInt optionalInt();
	
	/**
	 * 
	 * @return Optional has value if type is NUMBER
	 */
	public OptionalLong optionalLong();
	
	/**
	 * 
	 * @return Optional has value if type is NUMBER
	 */
	public OptionalDouble optionalDouble();
	
	/**
	 * 
	 * @return Optional has value if type is STRING
	 */
	public Optional<String> optionalString();
	
	/**
	 * 
	 * @return Optional has value if type is NUMBER
	 */
	public Optional<Number> optionalNubmer();
	
	/**
	 * available if type is TRUE or FALSE
	 * 
	 * @return boolean
	 * @throws JsonHubUnsupportedOperationException
	 */
	public boolean booleanValue();
	
	/**
	 * available if type is NUMBER
	 * 
	 * @return value
	 * @throws JsonHubUnsupportedOperationException
	 */
	public int intValue();
	
	/**
	 * available if type is NUMBER
	 * 
	 * @return value
	 * @throws JsonHubUnsupportedOperationException
	 */
	public long longValue();
	
	/**
	 * available if type is NUMBER
	 * 
	 * @return value
	 * @throws JsonHubUnsupportedOperationException
	 */
	public double doubleValue();
	
	
	/* builders */
	
	public static JsonHubBuilder getBuilder() {
		return JsonHubBuilder.getInstance();
	}
	
	/**
	 * parse to JsonHub
	 * 
	 * @param json
	 * @return JsonHub
	 * @throws JsonHubParseException
	 */
	public static JsonHub fromJson(CharSequence json) {
		return JsonHubJsonParser.getInstance().parse(json);
	}
	
	/**
	 * parse to JaonHub
	 * 
	 * @param reader
	 * @return JsonHub
	 * @throws IOException
	 * @throws JsonHubParseException
	 */
	public static JsonHub fromJson(Reader reader) throws IOException {
		return JsonHubJsonParser.getInstance().parse(reader);
	}
	
	/**
	 * parse to compact-JSON-String
	 * 
	 * @return json
	 */
	public String toJson();
	
	/**
	 * compact-JSON-String to writer
	 * 
	 * @param writer
	 * @throws IOException
	 */
	public void toJson(Writer writer) throws IOException;
	
	/**
	 * parse to compact-JSON-String exclued null value in Object;
	 * 
	 * @return json of excluded null value in Object.
	 */
	public String toJsonExcludedNullValueInObject();
	
	/**
	 * parse to compact-JSON-String exclued null value in Object;
	 * 
	 * @param writer
	 * @throws IOException
	 */
	public void toJsonExcludedNullValueInObject(Writer writer) throws IOException;
	
	/**
	 * read JSON file and parse to JsonHub
	 * 
	 * @param JSON-file-path
	 * @return JsonHub
	 * @throws IOException
	 * @throws JsonHubParseException
	 */
	public static JsonHub fromFile(Path path) throws IOException {
		
		try (
				BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8);
				){
			
			return fromJson(br);
		}
	}
	
	/**
	 * write to file
	 * 
	 * @param file-path
	 * @throws IOException
	 */
	public void writeFile(Path path) throws IOException;
	
	/**
	 * write to file
	 * 
	 * @param path
	 * @param options
	 * @throws IOException
	 */
	public void writeFile(Path path, OpenOption... options) throws IOException;
	
	
	/**
	 * 
	 * @return Pretty-Print-JSON
	 */
	public String prettyPrint();
	
	/**
	 * 
	 * @param config
	 * @return Pretty-Print-JSON with config format
	 */
	public String prettyPrint(JsonHubPrettyPrinterConfig config);
	
	/**
	 * write Pretty-Print-JSON to writer
	 * 
	 * @param writer
	 * @throws IOException
	 */
	public void prettyPrint(Writer writer) throws IOException;
	
	/**
	 * write Pretty-Print-JSON to writer with config format
	 * 
	 * @param writer
	 * @param config
	 * @throws IOException
	 */
	public void prettyPrint(Writer writer, JsonHubPrettyPrinterConfig config) throws IOException;
	
	/**
	 * write Pretty-Print-JSON to File
	 * 
	 * @param path
	 * @throws IOException
	 */
	public void prettyPrint(Path path) throws IOException;
	
	/**
	 * write Pretty-Print-JSON to File
	 * 
	 * @param path
	 * @param options
	 * @throws IOException
	 */
	public void prettyPrint(Path path, OpenOption... options) throws IOException;
	
	/**
	 * write Pretty-Print-JSON to File with config format
	 * 
	 * @param path
	 * @param config
	 * @throws IOException
	 */
	public void prettyPrint(Path path, JsonHubPrettyPrinterConfig config) throws IOException;
	
	/**
	 * write Pretty-Print-JSON to File with config format
	 * 
	 * @param path
	 * @param config
	 * @param options
	 * @throws IOException
	 */
	public void prettyPrint(Path path, JsonHubPrettyPrinterConfig config, OpenOption... options) throws IOException;
	
	
	/**
	 * 
	 * @param pojo
	 * @return
	 * @throws JsonHubParseException
	 */
	public static JsonHub fromPojo(Object pojo) {
		return JsonHubFromPojoParser.getInstance().fromPojo(pojo);
	}
	
	/**
	 * 
	 * @param <T>
	 * @param classOfT
	 * @return Pojo
	 * @throws JsonHubParseException
	 */
	public <T> T toPojo(Class<T> classOfT);
	
	/**
	 * 
	 * @return UTF-8 encorded bytes
	 */
	public byte[] getBytes();
	
	/**
	 * write UTF-8 encorded bytes to OutputStream
	 * 
	 * @param OutputSteam
	 * @throws IOException
	 */
	public void writeBytes(OutputStream strm) throws IOException;
	
	/**
	 * 
	 * @return UTF-8 encorded bytes excluded null value in Object.
	 */
	public byte[] getBytesExcludedNullValueInObject();
	
	/**
	 * write UTF-8 encorded bytes exclued null value in Object to OutputStream
	 * 
	 * @param strm
	 * @throws IOException
	 */
	public void writeBytesExcludedNullValueInObject(OutputStream strm) throws IOException;
	
	/**
	 * 
	 * @param bytes
	 * @return JsonHub
	 * @throws JsonHubParseException
	 */
	public static JsonHub fromBytes(byte[] bs) {
		return fromJson(new String(bs, StandardCharsets.UTF_8));
	}
	
	/**
	 * 
	 * @param strm
	 * @return JsonHub
	 * @throws IOException
	 * @throws JsonHubParseException
	 */
	public static JsonHub fromBytes(InputStream strm) throws IOException {
		
		try (
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				){
			
			for ( ;; ) {
				
				int r = strm.read();
				
				if ( r < 0 ) {
					break;
				}
				
				os.write(r);
			}
			
			return fromBytes(os.toByteArray());
		}
	}

}
