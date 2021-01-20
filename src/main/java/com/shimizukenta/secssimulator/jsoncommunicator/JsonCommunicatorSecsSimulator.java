package com.shimizukenta.secssimulator.jsoncommunicator;

import java.io.Closeable;
import java.io.IOException;

import com.shimizukenta.jsoncommunicator.JsonCommunicator;
import com.shimizukenta.jsoncommunicator.JsonCommunicators;
import com.shimizukenta.secssimulator.AbstractSecsSimulator;

public class JsonCommunicatorSecsSimulator extends AbstractSecsSimulator implements Closeable {
	
	private final JsonCommunicatorSecsSimulatorConfig config;
	private final JsonCommunicator<RequestJson> jsonComm;
	
	public JsonCommunicatorSecsSimulator(JsonCommunicatorSecsSimulatorConfig config) {
		super(config);
		this.config = config;
		this.jsonComm = JsonCommunicators.newInstance(config.jsonCommunicator(), RequestJson.class);
	}
	
	public void open() throws IOException {
		
		this.jsonComm.addPojoReceiveListener(request -> {
			
			//TODO
			
		});
		
		this.jsonComm.open();
		
		//TODO
		
	}
	
	@Override
	public void close() throws IOException {
		this.jsonComm.close();
	}
	
	public static void main(String[] args) {
		
		try {
			final JsonCommunicatorSecsSimulatorConfig config = JsonCommunicatorSecsSimulatorConfig.get(args);
			
			try (
					JsonCommunicatorSecsSimulator inst = new JsonCommunicatorSecsSimulator(config);
					) {
				
				inst.open();
				
				synchronized ( JsonCommunicatorSecsSimulator.class ) {
					JsonCommunicatorSecsSimulator.class.wait();
				}
			}
		}
		catch ( Throwable t ) {
			t.printStackTrace();
		}
	}

}
