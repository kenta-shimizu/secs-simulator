package com.shimizukenta.jsonhub;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Arrays;
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

/**
 * This abstract class is implementation of JsonHub.
 * 
 * @author kenta-shimizu
 * 
 */
abstract public class AbstractJsonHub implements JsonHub, Serializable {
	
	private static final long serialVersionUID = -8854276210327173340L;
	
	private byte[] toBytesCache;
	private byte[] toBytesExcludeNullValueInObjectCache;
	
	public AbstractJsonHub() {
		toBytesCache = null;
		toBytesExcludeNullValueInObjectCache = null;
	}
	
	private byte[] toBytesCache() {
		
		synchronized ( this ) {
			
			if ( toBytesCache == null ) {
				toBytesCache = toJson().getBytes(StandardCharsets.UTF_8);;
			}
			
			return toBytesCache;
		}
	}
	
	private byte[] toBytesExcludeNullValueInObjectCache() {
		
		synchronized ( this ) {
			
			if ( toBytesExcludeNullValueInObjectCache == null ) {
				toBytesExcludeNullValueInObjectCache = toJsonExcludedNullValueInObject().getBytes(StandardCharsets.UTF_8);
			}
			
			return toBytesExcludeNullValueInObjectCache;
		}
	}
	
	@Override
	public Iterator<JsonHub> iterator() {
		throw new JsonHubUnsupportedOperationException(type() + " not support #iterator");
	}
	
	@Override
	public Spliterator<JsonHub> spliterator() {
		throw new JsonHubUnsupportedOperationException(type() + " not support #spliterator");
	}
	
	@Override
	public void forEach(Consumer<? super JsonHub> action) {
		throw new JsonHubUnsupportedOperationException(type() + " not support #forEach");
	}
	
	@Override
	public void forEach(BiConsumer<? super JsonString, ? super JsonHub> action) {
		throw new JsonHubUnsupportedOperationException(type() + " not support #forEach");
	}
	
	@Override
	public Stream<JsonHub> stream() {
		throw new JsonHubUnsupportedOperationException(type() + " not support #stream");
	}
	
	@Override
	public Set<JsonString> keySet() {
		throw new JsonHubUnsupportedOperationException(type() + " not support #keySet");
	}
	
	@Override
	public List<JsonHub> values() {
		throw new JsonHubUnsupportedOperationException(type() + " not support #values");
	}
	
	@Override
	public JsonHub get(int index) {
		throw new JsonHubUnsupportedOperationException(type() + " not support #get(" + index + ")");
	}
	
	@Override
	public boolean containsKey(CharSequence name) {
		throw new JsonHubUnsupportedOperationException(type() + " not support #containsKey(\"" + name + "\")");
	}
	
	@Override
	public JsonHub get(CharSequence name) {
		throw new JsonHubUnsupportedOperationException(type() + "not support #get(\"" + name + "\")");
	}
	
	@Override
	public JsonHub getOrDefault(CharSequence name) {
		throw new JsonHubUnsupportedOperationException(type() + "not support #getOrDefault");
	}
	
	@Override
	public JsonHub getOrDefault(CharSequence name, JsonHub defaultValue) {
		throw new JsonHubUnsupportedOperationException(type() + "not support #getOrDefault");
	}
	
	@Override
	public JsonHub get(String... names) {
		throw new JsonHubUnsupportedOperationException(type() + "not support #get(\"name\"...)");
	}
	
	@Override
	public int length() {
		throw new JsonHubUnsupportedOperationException(type() + " not support #length");
	}
	
	@Override
	public boolean isEmpty() {
		throw new JsonHubUnsupportedOperationException(type() + " not support #isEmpty");
	}
	
	@Override
	public boolean isNull() {
		return type() == JsonHubType.NULL;
	}
	
	@Override
	public boolean nonNull() {
		return ! isNull();
	}
	
	@Override
	public boolean isTrue() {
		return type() == JsonHubType.TRUE;
	}
	
	@Override
	public boolean isFalse() {
		return type() == JsonHubType.FALSE;
	}
	
	@Override
	public boolean isString() {
		return type() == JsonHubType.STRING;
	}
	
	@Override
	public boolean isNumber() {
		return type() == JsonHubType.NUMBER;
	}
	
	@Override
	public boolean isArray() {
		return type() == JsonHubType.ARRAY;
	}
	
	@Override
	public boolean isObject() {
		return type() == JsonHubType.OBJECT;
	}
	
	@Override
	public Optional<Boolean> optionalBoolean() {
		return Optional.empty();
	}
	
	@Override
	public OptionalInt optionalInt() {
		return OptionalInt.empty();
	}
	
	@Override
	public OptionalLong optionalLong() {
		return OptionalLong.empty();
	}
	
	@Override
	public OptionalDouble optionalDouble() {
		return OptionalDouble.empty();
	}
	
	@Override
	public Optional<String> optionalString() {
		return Optional.empty();
	}
	
	@Override
	public Optional<Number> optionalNubmer() {
		return Optional.empty();
	}
	
	@Override
	public boolean booleanValue() {
		return optionalBoolean().orElseThrow(() -> new JsonHubUnsupportedOperationException(type() + " not support #booleanValue"));
	}
	
	@Override
	public int intValue() {
		return optionalInt().orElseThrow(() -> new JsonHubUnsupportedOperationException(type() + " not support #intValue"));
	}
	
	@Override
	public long longValue() {
		return optionalLong().orElseThrow(() -> new JsonHubUnsupportedOperationException(type() + " not support #longValue"));
	}
	
	@Override
	public double doubleValue() {
		return optionalDouble().orElseThrow(() -> new JsonHubUnsupportedOperationException(type() + " not support #doubleValue"));
	}
	
	@Override
	public String toJson() {
		return JsonHubPrettyPrinter.getCompactPrinter().print(this);
	}
	
	@Override
	public void toJson(Writer writer) throws IOException {
		JsonHubPrettyPrinter.getCompactPrinter().print(this, writer);
	}
	
	@Override
	public String toJsonExcludedNullValueInObject() {
		return JsonHubPrettyPrinter.getNoneNullValueInObjectCompactPrinter().print(this);
	}
	
	@Override
	public void toJsonExcludedNullValueInObject(Writer writer) throws IOException {
		JsonHubPrettyPrinter.getNoneNullValueInObjectCompactPrinter().print(this, writer);
	}
	
	@Override
	public void writeFile(Path path) throws IOException {
		
		try (
				BufferedWriter bw = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
				) {
			
			toJson(bw);
		}
	}
	
	@Override
	public void writeFile(Path path, OpenOption... options) throws IOException {
		
		try (
				BufferedWriter bw = Files.newBufferedWriter(path, StandardCharsets.UTF_8, options);
				) {
			
			toJson(bw);
		}
	}
	
	@Override
	public String prettyPrint() {
		return JsonHubPrettyPrinter.getDefaultPrinter().print(this);
	}
	
	@Override
	public String prettyPrint(JsonHubPrettyPrinterConfig config) {
		return JsonHubPrettyPrinter.newPrinter(config).print(this);
	}
	
	@Override
	public void prettyPrint(Writer writer) throws IOException {
		JsonHubPrettyPrinter.getDefaultPrinter().print(this, writer);
	}
	
	@Override
	public void prettyPrint(Writer writer, JsonHubPrettyPrinterConfig config) throws IOException {
		JsonHubPrettyPrinter.newPrinter(config).print(this, writer);
	}
	
	@Override
	public void prettyPrint(Path path) throws IOException {
		JsonHubPrettyPrinter.getDefaultPrinter().print(this, path);
	}
	
	@Override
	public void prettyPrint(Path path, OpenOption... options) throws IOException {
		JsonHubPrettyPrinter.getDefaultPrinter().print(this, path, options);
	}
	
	@Override
	public void prettyPrint(Path path, JsonHubPrettyPrinterConfig config) throws IOException {
		JsonHubPrettyPrinter.newPrinter(config).print(this, path);
	}
	
	@Override
	public void prettyPrint(Path path, JsonHubPrettyPrinterConfig config, OpenOption... options) throws IOException {
		JsonHubPrettyPrinter.newPrinter(config).print(this, path, options);
	}
	
	@Override
	public <T> T toPojo(Class<T> classOfT) {
		return JsonHubToPojoParser.getInstance().parse(this, classOfT);
	}
	
	@Override
	public byte[] getBytes() {
		byte[] bs = toBytesCache();
		return Arrays.copyOf(bs, bs.length);
	}
	
	@Override
	public void writeBytes(OutputStream strm) throws IOException {
		strm.write(toBytesCache());
	}
	
	@Override
	public byte[] getBytesExcludedNullValueInObject() {
		byte[] bs = this.toBytesExcludeNullValueInObjectCache();
		return Arrays.copyOf(bs, bs.length);
	}
	
	@Override
	public void writeBytesExcludedNullValueInObject(OutputStream strm) throws IOException {
		strm.write(toBytesExcludeNullValueInObjectCache());
	}
	
}
