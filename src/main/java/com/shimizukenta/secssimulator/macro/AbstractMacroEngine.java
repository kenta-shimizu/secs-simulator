package com.shimizukenta.secssimulator.macro;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.shimizukenta.secs.PropertyChangeListener;

public abstract class AbstractMacroEngine implements MacroEngine {
	
	private final ExecutorService execServ = Executors.newCachedThreadPool(r -> {
		Thread th = new Thread(r);
		th.setDaemon(true);
		return th;
	});
	
	public AbstractMacroEngine() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void close() throws IOException {
		try {
			execServ.shutdown();
			if ( ! execServ.awaitTermination(1L, TimeUnit.MILLISECONDS) ) {
				execServ.shutdownNow();
				if ( ! execServ.awaitTermination(10L, TimeUnit.SECONDS) ) {
					throw new IOException("ExecutorService#shutdown failed");
				}
			}
		}
		catch ( InterruptedException ignore ) {
		}
	}

	@Override
	public Optional<MacroRecipe> start(MacroRecipe recipe) throws InterruptedException {
		
		// TODO Auto-generated method stub
		
		return Optional.empty();
	}

	@Override
	public Optional<MacroRecipe> stop() throws InterruptedException {
		
		// TODO Auto-generated method stub
		return Optional.empty();
	}
	
	@Override
	public boolean addStateChangeListener(PropertyChangeListener<? super MacroRecipe> l) {
		
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeStateChangeListener(PropertyChangeListener<? super MacroRecipe> l) {
		// TODO Auto-generated method stub
		return false;
	}

}
