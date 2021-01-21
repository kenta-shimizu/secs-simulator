package com.shimizukenta.secssimulator.jsoncommunicator;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;

import com.shimizukenta.jsoncommunicator.JsonCommunicator;
import com.shimizukenta.jsoncommunicator.JsonCommunicatorConnectionState;
import com.shimizukenta.jsoncommunicator.JsonCommunicatorParseException;
import com.shimizukenta.jsoncommunicator.JsonCommunicators;
import com.shimizukenta.jsonhub.JsonHub;
import com.shimizukenta.jsonhub.JsonHubParseException;
import com.shimizukenta.secssimulator.AbstractSecsSimulator;

public class JsonCommunicatorSecsSimulator extends AbstractSecsSimulator implements Closeable {
	
	private final JsonCommunicatorSecsSimulatorConfig config;
	private final JsonCommunicator<?> jsonComm;
	
	private boolean opened;
	private boolean closed;
	
	private boolean rebootCommunicator;
	private JsonCommunicatorReportJson reportCache;
	
	public JsonCommunicatorSecsSimulator(JsonCommunicatorSecsSimulatorConfig config) {
		super(config);
		this.config = config;
		this.jsonComm = JsonCommunicators.newInstance(config.jsonCommunicator());
		this.opened = false;
		this.closed = false;
		this.rebootCommunicator = false;
		this.reportCache = new JsonCommunicatorReportJson();
	}
	
	public void open() throws IOException {
		
		synchronized ( this ) {
			
			if ( this.closed ) {
				throw new IOException("Already closed");
			}
			
			if ( this.opened ) {
				throw new IOException("Already opened");
			}
			
			this.opened = true;
		}
		
		this.jsonComm.addLogListener(this::echo);
		
		this.jsonComm.addJsonReceiveListener((channel, json) -> {
			try {
				receiveJson(channel, JsonHub.fromJson(json));
			}
			catch ( JsonHubParseException | JsonCommunicatorParseException | IOException e ) {
				echo(e);
			}
		});
		
		this.jsonComm.addConnectionStateChangeListener((channel, state) -> {
			if ( state == JsonCommunicatorConnectionState.CONNECTED ) {
				try {
					jsonComm.send(channel, reportCache);
				}
				catch ( InterruptedException ignore ) {
				}
				catch ( JsonCommunicatorParseException | IOException e ) {
					echo(e);
				}
			}
		});
		
		this.jsonComm.open();
		
		this.addSecsCommunicatableStateChangeListener(communicating -> {
			
			synchronized ( this ) {
				
				this.reportCache.communicating = Boolean.valueOf(communicating);
				
				JsonCommunicatorReportJson report = new JsonCommunicatorReportJson();
				report.communicating = Boolean.valueOf(communicating);
				
				//TODO
			}
		});
		
	}
	
	@Override
	public void close() throws IOException {
		
		synchronized ( this ) {
			
			if ( this.closed ) {
				return;
			}
			
			this.closed = true;
		}
		
		this.jsonComm.close();
	}
	
	
	private final Object syncWait = new Object();
	
	/**
	 * Open communicator and waiting until received quit/reboot request..
	 * 
	 * <p>
	 * Blocking method.<br/>
	 * Returns {@code true} if received Reboot request.<br />
	 * </p>
	 * 
	 * @return {@code true} if receive Reboot request
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private boolean openAndWait() throws IOException, InterruptedException {
		
		this.open();
		
		synchronized ( this.syncWait ) {
			this.syncWait.wait();
		}
		
		return this.rebootCommunicator;
	}
	
	private void notifyQuitCommunicator() {
		synchronized ( this.syncWait ) {
			this.rebootCommunicator = false;
			this.syncWait.notifyAll();
		}
	}
	
	private void notifyRebootCommunicator() {
		synchronized ( this.syncWait ) {
			this.rebootCommunicator = true;
			this.syncWait.notifyAll();
		}
	}
	
	private void receiveJson(AsynchronousSocketChannel channel, JsonHub jh)
			throws JsonHubParseException, JsonCommunicatorParseException, IOException {
		
		JsonCommunicatorRequestCommand reqCmd = JsonCommunicatorRequestCommand.get(
				jh.getOrDefault("request").optionalString().orElse(null));
		
		switch ( reqCmd ) {
		case QUIT: {
			notifyQuitCommunicator();
			break;
		}
		case REBOOT: {
			notifyRebootCommunicator();
			break;
		}
		case OPEN: {
			this.openCommunicator();
			break;
		}
		case CLOSE: {
			this.closeCommunicator();
			break;
		}
		default: {
			/* Nothing */
		}
		}
	}
	
	private void echo(Object o) {
		if ( config.isEcho() ) {
			staticEcho(o);
		}
	}
	
	
	public static void main(String[] args) {
		
		try {
			
			final JsonCommunicatorSecsSimulatorConfig config = JsonCommunicatorSecsSimulatorConfig.get(args);
			
			try {
				for ( ;; ) {
					
					try (
							JsonCommunicatorSecsSimulator inst = new JsonCommunicatorSecsSimulator(config);
							) {
						
						boolean reboot = inst.openAndWait();
						
						if ( ! reboot ) {
							break;
						}
					}
				}
			}
			catch ( InterruptedException ignore ) {
			}
			catch ( Throwable t ) {
			}
		}
		catch ( Throwable t ) {
			staticEcho(t);
		}
	}
	
	private static final Object syncStaticEcho = new Object();
	
	private static void staticEcho(Object o) {
		synchronized ( syncStaticEcho ) {
			if ( o instanceof Throwable ) {
				((Throwable) o).printStackTrace();
			} else {
				System.out.println(o);
			}
			System.out.println();
		}
	}
	
}
