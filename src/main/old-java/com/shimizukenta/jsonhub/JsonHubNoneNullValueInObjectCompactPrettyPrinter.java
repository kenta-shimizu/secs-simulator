package com.shimizukenta.jsonhub;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * This class provides Compact-JSON-pretty-printer, no line-separate, no space
 * and exclude null-value-pair in Object.
 * 
 * @author kenta-shimizu
 *
 */
public class JsonHubNoneNullValueInObjectCompactPrettyPrinter extends JsonHubCompactPrettyPrinter {

	protected JsonHubNoneNullValueInObjectCompactPrettyPrinter() {
		super();
	}
	
	private static class SingletonHolder {
		private static JsonHubNoneNullValueInObjectCompactPrettyPrinter inst = new JsonHubNoneNullValueInObjectCompactPrettyPrinter();
	}
	
	/**
	 * Returns Compact and exclude-null-value-pair-in-Object JSON pretty-printer instance.
	 * 
	 * <p>
	 * This class is Singleton-pattern.<br />
	 * </p>
	 * 
	 * @return Compact and exclude-null-value-pair-in-Object JSON pretty-printer instance
	 */
	public static JsonHubNoneNullValueInObjectCompactPrettyPrinter getInstance() {
		return SingletonHolder.inst;
	}
	
	@Override
	protected Collection<JsonObjectPair> objectPairs(ObjectJsonHub ojh) {
		return ojh.objectPairs().stream()
				.filter(pair -> pair.value().nonNull())
				.collect(Collectors.toList());
	}
	
}
