package com.shimizukenta.jsonhub;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class is implements of JSON value OBJECT.
 * 
 * <p>
 * Instances of this class are immutable.
 * </p>
 * 
 * @author kenta-shimizu
 *
 */
public class ObjectJsonHub extends AbstractJsonHub {
	
	private static final long serialVersionUID = 6707336078321312442L;
	
	private final Collection<JsonObjectPair> v;
	private String toJsonCache;
	private String toJsonExcludedNullValueInObjectCache;
	
	protected ObjectJsonHub(Collection<? extends JsonObjectPair> v) {
		super();
		
		this.v = new ArrayList<>(Objects.requireNonNull(v));
		this.toJsonCache = null;
		this.toJsonExcludedNullValueInObjectCache = null;
	}
	
	@Override
	public Iterator<JsonHub> iterator() {
		return stream().iterator();
	}
	
	@Override
	public Spliterator<JsonHub> spliterator() {
		return stream().spliterator();
	}
	
	@Override
	public void forEach(Consumer<? super JsonHub> action) {
		stream().forEach(action);
	}
	
	@Override
	public void forEach(BiConsumer<? super JsonString, ? super JsonHub> action) {
		v.forEach(x -> {
			action.accept(x.name(), x.value());
		});
	}
	
	
	@Override
	public JsonHubType type() {
		return JsonHubType.OBJECT;
	}
	
	@Override
	public Stream<JsonHub> stream() {
		return v.stream().map(x -> x.value());
	}
	
	@Override
	public Set<JsonString> keySet() {
		return v.stream().map(x -> x.name()).collect(Collectors.toSet());
	}
	
	protected Collection<JsonObjectPair> objectPairs() {
		return Collections.unmodifiableCollection(v);
	}
	
	@Override
	public List<JsonHub> values() {
		return stream().collect(Collectors.toList());
	}
	
	@Override
	public boolean containsKey(CharSequence name) {
		String s = name.toString();
		return v.stream()
				.map(x -> x.name().unescaped())
				.anyMatch(x ->  x.equals(s));
	}
	
	@Override
	public JsonHub get(CharSequence name) {
		return getOrDefault(name, null);
	}
	
	@Override
	public JsonHub get(String... names) {
		return get(new LinkedList<>(Arrays.asList(names)));
	}
	
	private ObjectJsonHub get(LinkedList<String> ll) {
		
		if ( ll.isEmpty() ) {
			
			return this;
			
		} else {
			
			String s = ll.removeFirst();
			
			JsonHub x = get(s);
			
			if ( x == null ) {
				return null;
			}
			
			return ((ObjectJsonHub)x).get(ll);
		}
	}
	
	@Override
	public JsonHub getOrDefault(CharSequence name) {
		return getOrDefault(name, JsonHub.getBuilder().emptyObject());
	}
	
	@Override
	public JsonHub getOrDefault(CharSequence name, JsonHub defaultValue) {
		String s = name.toString();
		return v.stream()
				.filter(x -> x.name().unescaped().equals(s))
				.findFirst()
				.map(x -> x.value())
				.orElse(defaultValue);
	}
	
	@Override
	public int length() {
		return v.size();
	}
	
	@Override
	public boolean isEmpty() {
		return v.isEmpty();
	}
	
	@Override
	public String toJson() {
		return toJsonCache();
	}
	
	@Override
	public void toJson(Writer writer) throws IOException {
		writer.write(toJsonCache());
	}
	
	@Override
	public String toJsonExcludedNullValueInObject() {
		return toJsonExcludedNullValueInObjectCache();
	}
	
	@Override
	public void toJsonExcludedNullValueInObject(Writer writer) throws IOException {
		writer.write(toJsonExcludedNullValueInObjectCache());
	}
	
	private String toJsonCache() {
		synchronized ( this ) {
			if ( toJsonCache == null ) {
				toJsonCache = super.toJson();
			}
			
			return toJsonCache;
		}
	}
	
	private String toJsonExcludedNullValueInObjectCache() {
		synchronized ( this ) {
			if ( toJsonExcludedNullValueInObjectCache == null ) {
				toJsonExcludedNullValueInObjectCache = super.toJsonExcludedNullValueInObject();
			}
			
			return toJsonExcludedNullValueInObjectCache;
		}
	}
	
	@Override
	public String toString() {
		return toJson();
	}
	
	@Override
	public boolean equals(Object o) {
		if ((o != null) && (o instanceof ObjectJsonHub)) {
			return ((ObjectJsonHub) o).toJson().equals(toJson());
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return toJson().hashCode();
	}
	
}
