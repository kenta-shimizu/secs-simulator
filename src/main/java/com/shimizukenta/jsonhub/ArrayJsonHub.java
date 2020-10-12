package com.shimizukenta.jsonhub;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * This class is implements of JSON value ARRAY.
 * 
 * <p>
 * Instances of this class are immutable.
 * </p>
 * 
 * @author kenta-shimizu
 *
 */
public class ArrayJsonHub extends AbstractJsonHub {
	
	private static final long serialVersionUID = -2012262422136607091L;
	
	private final List<JsonHub> v;
	private String toJsonCache;
	private String toJsonExcludedNullValueInObjectCache;
	
	protected ArrayJsonHub(List<? extends JsonHub> v) {
		super();
		
		this.v = new ArrayList<>(Objects.requireNonNull(v));
		this.toJsonCache = null;
		this.toJsonExcludedNullValueInObjectCache = null;
	}
	
	@Override
	public Iterator<JsonHub> iterator() {
		return v.iterator();
	}
	
	@Override
	public Spliterator<JsonHub> spliterator() {
		return v.spliterator();
	}
	
	@Override
	public void forEach(Consumer<? super JsonHub> action) {
		v.forEach(action);
	}
	
	@Override
	public void forEach(BiConsumer<? super JsonString, ? super JsonHub> action) {
		v.forEach(x -> {
			action.accept(null, x);
		});
	}
	
	@Override
	public JsonHubType type() {
		return JsonHubType.ARRAY;
	}
	
	@Override
	public Stream<JsonHub> stream() {
		return v.stream();
	}
	
	@Override
	public List<JsonHub> values() {
		return Collections.unmodifiableList(v);
	}
	
	@Override
	public JsonHub get(int index) {
		
		try {
			return v.get(index);
		}
		catch ( IndexOutOfBoundsException e ) {
			throw new JsonHubIndexOutOfBoundsException("get: " + index);
		}
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
		if ((o != null) && (o instanceof ArrayJsonHub)) {
			return ((ArrayJsonHub) o).toJson().equals(toJson());
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return toJson().hashCode();
	}
	
}
