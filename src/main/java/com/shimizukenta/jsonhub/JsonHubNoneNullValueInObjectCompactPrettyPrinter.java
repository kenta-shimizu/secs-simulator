package com.shimizukenta.jsonhub;

import java.util.Collection;
import java.util.stream.Collectors;

public class JsonHubNoneNullValueInObjectCompactPrettyPrinter extends JsonHubCompactPrettyPrinter {

	private JsonHubNoneNullValueInObjectCompactPrettyPrinter() {
		super();
	}
	
	private static class SingletonHolder {
		private static JsonHubNoneNullValueInObjectCompactPrettyPrinter inst = new JsonHubNoneNullValueInObjectCompactPrettyPrinter();
	}
	
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
