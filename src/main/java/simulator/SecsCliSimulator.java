package simulator;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SecsCliSimulator implements Closeable {
	
	private final ExecutorService execServ = Executors.newCachedThreadPool(r -> {
		Thread th = new Thread(r);
		th.setDaemon(true);
		return th;
	});
	
	private final SecsCliSimulatorConfig config;
	
	
	public SecsCliSimulator(SecsCliSimulatorConfig config) {
		this.config = config;
	}
	
	public void open() throws IOException {
		
		//TODO
	}

	@Override
	public void close() throws IOException {
		
		
		// TODO Auto-generated method stub

	}
	
	public static SecsCliSimulator open(SecsCliSimulatorConfig config) throws IOException {
		
		final SecsCliSimulator inst = new SecsCliSimulator(config);
				
		try {
			inst.open();
		}
		catch ( IOException e ) {
			
			try {
				inst.close();
			}
			catch ( IOException giveup ) {
			}
			
			throw e;
		}
		
		return inst;
	}

	public static void main(String[] args) {
		
		try {
			final SecsCliSimulatorConfig config = SecsCliSimulatorConfig.parse(args);
			
			try (
					SecsCliSimulator inst = SecsCliSimulator.open(config);
					) {
				
				synchronized ( SecsCliSimulator.class ) {
					SecsCliSimulator.class.wait();
				}
			}
			catch ( InterruptedException ignore ) {
			}
			catch ( IOException e ) {
				e.printStackTrace();
			}
		}
		catch ( RuntimeException e ) {
			e.printStackTrace();
		}

	}

}
