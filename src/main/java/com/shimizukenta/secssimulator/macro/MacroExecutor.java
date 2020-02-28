package com.shimizukenta.secssimulator.macro;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.shimizukenta.secs.SecsCommunicatableStateChangeListener;
import com.shimizukenta.secs.SecsCommunicator;
import com.shimizukenta.secs.SecsMessage;
import com.shimizukenta.secssimulator.SecsSimulator;

public class MacroExecutor {
	
	public static final long defaultSleepMilliSec = 1000L;
	
	private final ExecutorService execServ = Executors.newCachedThreadPool(r -> {
		Thread th = new Thread(r);
		th.setDaemon(true);
		return th;
	});
	
	private final SecsSimulator parent;
	
	public MacroExecutor(SecsSimulator parent) {
		this.parent = parent;
	}
	
	public void execute(MacroRequest request)
			throws InterruptedException, MacroException {
		
		switch ( request.command() ) {
		case OPEN: {
			
			try {
				openCommunicator();
			}
			catch ( IOException e ) {
				throw new MacroException(e);
			}
			break;
		}
		case CLOSE: {
			
			try {
				parent.closeCommunicator();
			}
			catch ( IOException e ) {
				throw new MacroException(e);
			}
			break;
		}
		case SEND_SML: {
			
			//TODO
			
			break;
		}
		case SEND_DIRECT: {
			
			//TODO
			
			break;
		}
		case WAIT: {
			
			//TODO
			
			break;
		}
		case SLEEP: {
			
			try {
				long v = request.option(0)
						.map(s -> {
							return (long)(Float.parseFloat(s) * 1000.0F);
						})
						.orElse(defaultSleepMilliSec);
				
				TimeUnit.MILLISECONDS.sleep(v);
			}
			catch ( NumberFormatException e ) {
				throw new MacroException(e);
			}
			break;
		}
		default: {
			/* Nothing */
		}
		}
	}
	
	public void shutdown() throws InterruptedException {
		
		execServ.shutdown();
		if ( execServ.awaitTermination(1L, TimeUnit.MILLISECONDS) ) {
			return;
		}
		
		execServ.shutdownNow();
		if ( execServ.awaitTermination(5L, TimeUnit.SECONDS) ) {
			return;
		}
	}
	
	public void receive(SecsMessage recvMsg) {
		
		//TODO
	}
	
	private void waitMessage(int strm, int func)
			throws InterruptedException, MacroException {
		
		//TODO
	}
	
	private void openCommunicator() throws InterruptedException, IOException {
		
		final SecsCommunicator comm = parent.openCommunicator();
		
		final SecsCommunicatableStateChangeListener lstnr = communicated -> {
			synchronized ( this ) {
				if ( communicated ) {
					this.notifyAll();
				}
			}
		};
		
		try {
			synchronized ( this ) {
				comm.addSecsCommunicatableStateChangeListener(lstnr);
				this.wait();
			}
		}
		finally {
			comm.removeSecsCommunicatableStateChangeListener(lstnr);
		}
	}
	
}
