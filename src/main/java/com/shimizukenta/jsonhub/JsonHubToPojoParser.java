package com.shimizukenta.jsonhub;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is parser, from JsonHub instance to POJO(Plain-Old-Java-Object).
 * 
 * <p>
 * To get parser instance, {@link #getInstance()}.<br />
 * To parse, {@link #parse(JsonHub, Class)}.<br />
 * </p>
 * <p>
 * To POJO Conditions.<br />
 * <ul>
 * <li>Class has {@code public new()} (arguments is 0)</li>
 * <li>Field is {@code public}</li>
 * <li>Field is <i>not</i> {@code static}</li>
 * <li>Field is <i>not</i> {@code final}</li>
 * </ul>
 * </p>
 * 
 * @author kenta-shimizu
 *
 */
public class JsonHubToPojoParser {
	
	protected JsonHubToPojoParser() {
		/* Nothing */
	}
	
	private static class SingletonHolder {
		private static final JsonHubToPojoParser inst = new JsonHubToPojoParser();
	}
	
	/**
	 * Returns parser instance.
	 * 
	 * <p>
	 * This class is Singleton-pattern.<br />
	 * </p>
	 * 
	 * @return JsonHubToPojoParser instance
	 */
	public static JsonHubToPojoParser getInstance() {
		return SingletonHolder.inst;
	}
	
	/**
	 * Returns parsed POJO of classOfT from JsonHub instance.
	 * 
	 * <p>
	 * To POJO Conditions.<br />
	 * <ul>
	 * <li>Class has {@code public new()} (arguments is 0)</li>
	 * <li>Field is {@code public}</li>
	 * <li>Field is <i>not</i> {@code static}</li>
	 * <li>Field is <i>not</i> {@code final}</li>
	 * </ul>
	 * </p>
	 * 
	 * @param <T>
	 * @param jh JsonHub instance
	 * @param classOfT
	 * @return parsed POJO
	 * @throws JsonHubParseException if parse failed
	 */
	public <T> T parse(JsonHub jh, Class<T> classOfT) {
		
		try {
			return toTopLevelPojo(jh, classOfT);
		}
		catch (ReflectiveOperationException | ClassCastException | IllegalArgumentException e) {
			throw new JsonHubParseException(e);
		}
	}
	
	private static <T> T toTopLevelPojo(JsonHub jh, Class<T> classOfT)
		throws ReflectiveOperationException {
		
		switch ( jh.type() ) {
		case NULL: {
			
			return null;
			/* break */
		}
		case TRUE:
		case FALSE: {
			
			Boolean f = jh.optionalBoolean().get();
			return classOfT.cast(f);
			/* break; */
		}
		case STRING: {
			
			return classOfT.cast(jh.toString());
			/* break */
		}
		case NUMBER: {
			
			Number n = toNumberPojo(
					jh.optionalNubmer().get()
					, classOfT);
			
			return classOfT.cast(n);
			/* break */
		}
		case ARRAY : {
			
			if ( classOfT.isArray() ) {
				
				return toArrayPojo(jh, classOfT);
				
			} else {
				
				throw new JsonHubUnsupportedParseException("Top level \"" + classOfT.toGenericString() + "\" is not support");
			}
			
			/* break; */
		}
		case OBJECT: {
			
			return toObjectPojo(jh, classOfT);
			/* break */
		}
		default: {
			return null;
		}
		}
	}
	
	private static <T> T toObjectPojo(JsonHub jh, Class<T> classOfT)
			throws ReflectiveOperationException {
		
		final T inst = classOfT.getDeclaredConstructor().newInstance();
		
		for ( JsonObjectPair pair : ((ObjectJsonHub)jh).objectPairs() ) {
			
			try {
				
				String name = pair.name().unescaped();
				
				Field field = classOfT.getField(name);
				
				field.setAccessible(true);
				
				{
					int iMod = field.getModifiers();
					
					if ( ! Modifier.isPublic(iMod) ) {
						continue;
					}
					
					if ( Modifier.isStatic(iMod) ) {
						continue;
					}
					
					if ( Modifier.isFinal(iMod) ) {
						continue;
					}
				}
				
				JsonHub v = pair.value();
				
				switch ( v.type() ) {
				case NULL: {
					
					field.set(inst, null);
					break;
				}
				case TRUE:
				case FALSE: {
					
					field.set(inst, v.optionalBoolean().get());
					break;
				}
				case STRING: {
					
					field.set(inst, v.toString());
					break;
				}
				case NUMBER: {
					
					Number n = v.optionalNubmer().get();
					field.set(inst, toNumberPojo(n, field.getType()));
					break;
				}
				case ARRAY: {
					
					Type type = field.getGenericType();
					
					if ( type instanceof Class<?> ) {
						
						field.set(inst, toArrayPojo(v, (Class<?>)type));
						
					} else {
						
						field.set(inst, toUtilListPojo(v, type));
					}
					
					break;
				}
				case OBJECT: {
					
					field.set(inst, toObjectPojo(v, field.getType()));
					break;
				}
				}
			}
			catch ( NoSuchFieldException ignore ) {
			}
		}
		
		return inst;
	}
	
	private static <T> T toArrayPojo(JsonHub jh, Class<T> classOfT)
			throws ReflectiveOperationException {
		
		if ( ! classOfT.isArray() ) {
			throw new JsonHubUnsupportedParseException("Cannot create a generic Array");
		}

		Class<?> compClass = classOfT.getComponentType();
		
		int len = jh.length();
		
		Object array = Array.newInstance(compClass, len);
		
		for ( int i = 0; i < len; ++i ) {
			
			JsonHub v = jh.get(i);
			
			switch ( v.type() ) {
			case NULL: {
				
				Array.set(array, i, null);
				break;
			}
			case TRUE:
			case FALSE: {
				
				Array.set(array, i, v.optionalBoolean().get());
				break;
			}
			case STRING: {
				
				Array.set(array, i, v.toString());
				break;
			}
			case NUMBER: {
				
				Number n = v.optionalNubmer().get();
				Array.set(array, i, toNumberPojo(n, compClass));
				break;
			}
			case ARRAY: {
				
				Array.set(array, i, toArrayPojo(v, compClass));
				break;
			}
			case OBJECT: {
				
				Array.set(array, i, toObjectPojo(v, compClass));
				break;
			}
			}
		}
		
		return classOfT.cast(array);
	}
	
	private static Object toUtilListPojo(JsonHub jh, Type type)
			throws ReflectiveOperationException {
		
		if ( ! ( type instanceof ParameterizedType) ) {
			throw new JsonHubUnsupportedParseException("\"" + type.toString() + "\" is not support");
		}
		
		if ( ! jh.isArray() ) {
			throw new JsonHubUnsupportedParseException("\"" +type.toString() + "\" is not support");
		}
		
		Type ptype = ((ParameterizedType)type).getActualTypeArguments()[0];
		
		if ( ptype instanceof Class<?> ) {
			
			return toUtilListPojo(jh, (Class<?>)ptype);
			
		} else {
			
			List<Object> ll = new ArrayList<>();
			
			for ( JsonHub v : jh.values() ) {
				ll.add(toUtilListPojo(v, ptype));
			}
			
			return ll;
		}
	}
	
	private static <T> List<T> toUtilListPojo(JsonHub jh, Class<T> classOfT)
			throws ReflectiveOperationException {
		
		List<T> inst = new ArrayList<>();
		
		for ( JsonHub v : jh ) {
			
			switch ( v.type() ) {
			case NULL: {
				
				inst.add(null);
				break;
			}
			case TRUE:
			case FALSE: {
				
				inst.add(classOfT.cast(v.optionalBoolean().get()));
				break;
			}
			case STRING: {
				
				inst.add(classOfT.cast(v.toString()));
				break;
			}
			case NUMBER: {
				
				Number n = v.optionalNubmer().get();
				inst.add(classOfT.cast(toNumberPojo(n, classOfT)));
				break;
			}
			case ARRAY: {
				
				inst.add(toArrayPojo(v, classOfT));
				break;
			}
			case OBJECT: {
				
				inst.add(toObjectPojo(v, classOfT));
				break;
			}
			}
		}
		
		return inst;
	}
	
	private static <T> Number toNumberPojo(Number n, Class<?> classOfT) {
		
		if ( classOfT == byte.class || classOfT == Byte.class ) {
			return Byte.valueOf(n.byteValue());
		}
		
		if ( classOfT == short.class || classOfT == Short.class ) {
			return Short.valueOf(n.shortValue());
		}
		
		if ( classOfT == int.class || classOfT == Integer.class ) {
			return Integer.valueOf(n.intValue());
		}
		
		if ( classOfT == long.class || classOfT == Long.class ) {
			return Long.valueOf(n.longValue());
		}
		
		if ( classOfT == float.class || classOfT == Float.class ) {
			return Float.valueOf(n.floatValue());
		}
		
		if ( classOfT == double.class || classOfT == Double.class ) {
			return Double.valueOf(n.doubleValue());
		}
		
		throw new JsonHubParseException("toNumberPojo cast failed \"" + classOfT.toString() + "\"");
	}
	
}
