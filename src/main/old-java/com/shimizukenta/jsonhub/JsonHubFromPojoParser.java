package com.shimizukenta.jsonhub;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class is parser, from POJO (Plain-Old-Java-Object) to JsonHub instance.
 * 
 * <p>
 * To get parser instance, {@link #getInstance()}.<br />
 * To parse, {@link #parse(Object)}.<br />
 * </p>
 * <p>
 * From POJO Conditions.<br />
 * <ul>
 * <li>Field is {@code public}</li>
 * <li>Field is <i>not</i> {@code static}</li>
 * </ul>
 * </p>
 * 
 * @author kenta-shimizu
 *
 */
public class JsonHubFromPojoParser {
	
	protected final JsonHubBuilder jhb = JsonHubBuilder.getInstance();
	
	protected JsonHubFromPojoParser() {
		/* Nothing */
	}
	
	private static class SingletonHolder {
		private static final JsonHubFromPojoParser inst = new JsonHubFromPojoParser();
	}
	
	/**
	 * Returns parser instance.
	 * 
	 * <p>
	 * This class is Singleton-pattern.
	 * </p>
	 * 
	 * @return JsonHubFromPojoParser instance
	 */
	public static JsonHubFromPojoParser getInstance() {
		return SingletonHolder.inst;
	}
	
	/**
	 * Parse from POJO to JsonHub instance.
	 * 
	 * <p>
	 * From POJO Conditions.<br />
	 * <ul>
	 * <li>Field is {@code public}</li>
	 * <li>Field is <i>not</i> {@code static}</li>
	 * </ul>
	 * </p>
	 * <p>
	 * Not accept {@code null}.<br />
	 * </p>
	 * 
	 * @param pojo (Plain-Old-Java-Object)
	 * @return AbstractJsonHub instance
	 * @throws JsonHubParseException if parse failed
	 */
	public AbstractJsonHub parse(Object pojo) {
		try {
			return fromObjectPojo(pojo);
		}
		catch ( IllegalArgumentException | IllegalAccessException | ClassCastException e ) {
			throw new JsonHubParseException(e);
		}
	}
	
	/**
	 * Parse from POJO to JsonHub instance.
	 * 
	 * @param pojo (Plain-Old-Java-Object)
	 * @return AbstractJsonHub instance
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws ClassCastException
	 */
	protected AbstractJsonHub fromObjectPojo(Object pojo)
			throws IllegalArgumentException, IllegalAccessException, ClassCastException {
		
		if ( pojo == null ) {
			
			return jhb.nullValue();
		
		} else if ( pojo.getClass().isArray() ) {
			
			final List<JsonHub> ll = new ArrayList<>();
			
			int len = Array.getLength(pojo);
			
			for ( int i = 0 ; i < len; ++i ) {
				ll.add(fromObjectPojo(Array.get(pojo, i)));
			}
			
			return jhb.array(ll);
			
		} else if ( pojo instanceof Boolean ) {
			
			return jhb.build(((Boolean)pojo).booleanValue());
			
		} else if ( pojo instanceof CharSequence ) {
			
			return jhb.string((CharSequence)pojo);
			
		} else if ( pojo instanceof Number ) {
			
			return jhb.number((Number)pojo);
			
		} else if ( pojo instanceof List<?> ) {
			
			List<?> pojoList = (List<?>)pojo;
			
			final List<JsonHub> ll = new ArrayList<>();
			
			for ( Object p : pojoList ) {
				ll.add(fromObjectPojo(p));
			}
			
			return jhb.array(ll);
			
		} else if ( pojo instanceof Map<?, ?> ) {
			
			final List<JsonObjectPair> pairs = new ArrayList<>();
			
			Map<?, ?> mm = (Map<?, ?>)pojo;
			
			for ( Object key : mm.keySet() ) {
				
				pairs.add(
						jhb.pair(
								key.toString()
								, fromObjectPojo(mm.get(key))));
			}
			
			return jhb.object(pairs);
		}
		
		final List<JsonObjectPair> pairs = new ArrayList<>();
		
		for ( Field field : pojo.getClass().getFields() ) {
			
			field.setAccessible(true);
			
			int iMod = field.getModifiers();
			
			if ( ! Modifier.isPublic(iMod) ) {
				continue;
			}

			if ( Modifier.isStatic(iMod) ) {
				continue;
			}
			
			pairs.add(
					jhb.pair(
							field.getName()
							, fromObjectPojo(field.get(pojo))));
		}
		
		return jhb.object(pairs);
	}
	
}
