package com.shimizukenta.secssimulator.macro;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.shimizukenta.secs.PropertyChangeListener;
import com.shimizukenta.secssimulator.AbstractSecsSimulator;

public abstract class AbstractMacroEngine implements MacroEngine {
	
	private final ExecutorService execServ = Executors.newCachedThreadPool(r -> {
		Thread th = new Thread(r);
		th.setDaemon(true);
		return th;
	});
	
	private final Collection<MacroWorker> workers = new ArrayList<>();
	
	private final Object sync = new Object();
	
	private final AbstractSecsSimulator simm;
	private boolean closed;
	
	public AbstractMacroEngine(AbstractSecsSimulator simm) {
		this.simm = simm;
		this.closed = false;
	}
	
	@Override
	public void close() throws IOException {
		
		synchronized ( sync ) {
			
			if ( this.closed ) {
				return ;
			}
			
			this.closed = true;
			
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
	}
	
	/**
	 * Prototype pattern, worker builder.
	 * 
	 * @param id
	 * @param recipe
	 * @return worker
	 */
	protected MacroWorker createWorker(int id, MacroRecipe recipe) {
		return new AbstractMacroWorker(id, recipe, simm, execServ) {};
	}
	
	private final AtomicInteger autoNumber = new AtomicInteger(0);
	
	@Override
	public Optional<MacroWorker> start(MacroRecipe recipe) throws InterruptedException {
		
		synchronized ( sync ) {
			if ( this.closed ) {
				return Optional.empty();
			}
		}
		
		final MacroWorker w = createWorker(autoNumber.incrementAndGet(), recipe);
		
		synchronized ( this.workers ) {
			this.workers.add(w);
		}
		
		w.addStateChangeListener(this::notifyStateChanged);
		
		execServ.execute(() -> {
			
			try {
				w.get();
			}
			catch ( InterruptedException ignore ) {
			}
			catch ( ExecutionException e ) {
				Throwable t = e.getCause();
				
				if ( t instanceof Error ) {
					throw (Error)t;
				}
				
				if ( t instanceof RuntimeException ) {
					throw (RuntimeException)t;
				}
				
			}
			finally {
				synchronized ( this.workers ) {
					this.workers.remove(w);
				}
			}
		});
		
		return Optional.of(w);
	}
	
	@Override
	public Optional<MacroWorker> stop(MacroWorker worker) throws InterruptedException {
		boolean f = worker.cancel(true);
		return f ? Optional.of(worker) : Optional.empty();
	}
	
	@Override
	public Optional<MacroWorker> stop(int workerId) throws InterruptedException {
		
		synchronized ( this ) {
			
			final Collection<MacroWorker> ww = new ArrayList<>();
			
			this.workers.stream()
			.filter(w -> w.id() == workerId)
			.forEach(w -> {
				if ( w.cancel(true) ) {
					ww.add(w);
				}
			});
			
			return ww.stream().findAny();
		}
	}
	
	@Override
	public List<MacroWorker> stop() throws InterruptedException {
		
		synchronized ( this ) {
			
			final List<MacroWorker> ww = new ArrayList<>();
			
			this.workers.forEach(w -> {
				if ( w.cancel(true) ) {
					ww.add(w);
				}
			});
			
			return Collections.unmodifiableList(ww);
		}
	}
	
	private final Collection<PropertyChangeListener<? super MacroWorker>> listeners = new CopyOnWriteArrayList<>();
	
	@Override
	public boolean addStateChangeListener(PropertyChangeListener<? super MacroWorker> l) {
		return listeners.add(l);
	}
	
	@Override
	public boolean removeStateChangeListener(PropertyChangeListener<? super MacroWorker> l) {
		return listeners.remove(l);
	}
	
	protected void notifyStateChanged(MacroWorker w) {
		listeners.forEach(l -> {l.changed(w);});
	}
	
}
