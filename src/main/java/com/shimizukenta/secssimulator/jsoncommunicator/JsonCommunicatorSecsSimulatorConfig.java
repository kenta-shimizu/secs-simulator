package com.shimizukenta.secssimulator.jsoncommunicator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.shimizukenta.jsoncommunicator.JsonCommunicatorConfig;
import com.shimizukenta.secssimulator.AbstractSecsSimulatorConfig;

public class JsonCommunicatorSecsSimulatorConfig extends AbstractSecsSimulatorConfig {
	
	private static final long serialVersionUID = 7511244441909307797L;
	
	private final JsonCommunicatorConfig jsonCommConfig = new JsonCommunicatorConfig();
	
	private boolean isEcho;
	
	public JsonCommunicatorSecsSimulatorConfig() {
		super();
		this.isEcho = false;
	}
	
	public JsonCommunicatorConfig jsonCommunicator() {
		return this.jsonCommConfig;
	}
	
	public void isEcho(boolean f) {
		synchronized ( this ) {
			this.isEcho = f;
		}
	}
	
	public boolean isEcho() {
		synchronized ( this ) {
			return this.isEcho;
		}
	}
	
	public static JsonCommunicatorSecsSimulatorConfig get(String[] args) {
		
		final JsonCommunicatorSecsSimulatorConfig config = new JsonCommunicatorSecsSimulatorConfig();
		
		final Map<String, List<String>> map = new HashMap<>();
		
		for ( int i = 0, m = args.length; i < m; i += 2 ) {
			
			String key = args[i];
			String v = args[i + 1];
			
			map.computeIfAbsent(key, k -> new ArrayList<>()).add(v);
		}
		
		for (String v : map.getOrDefault("--connect", Collections.emptyList())) {
			config.jsonCommunicator().addConnect(parseSocketAddress(v));
		}
		
		for (String v : map.getOrDefault("--bind", Collections.emptyList())) {
			config.jsonCommunicator().addBind(parseSocketAddress(v));
		}
		
		for ( String v : map.getOrDefault("--echo", Collections.emptyList())) {
			config.isEcho(Boolean.parseBoolean(v));
		}
		
		return config;
	}
	
}
