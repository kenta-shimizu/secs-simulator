package com.shimizukenta.secssimulator.macro;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.shimizukenta.secs.PropertyChangeListener;
import com.shimizukenta.secssimulator.SecsSimulator;

public abstract class AbstractMacroWorker implements MacroWorker {
	
	private final Object sync = new Object();
	private final Object abortTask = new Object();
	
	private final int id;
	private final MacroRecipe recipe;
	private final SecsSimulator simm;
	private final ExecutorService execServ;
	
	private boolean canceled;
	private boolean done;
	private int step;
	
	public AbstractMacroWorker(int id, MacroRecipe recipe,
			SecsSimulator simm,
			ExecutorService execServ) {
		
		this.id = id;
		this.recipe = recipe;
		this.simm = simm;
		this.execServ = execServ;
		this.canceled = false;
		this.done = false;
		this.step = -1;
	}
	
	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		synchronized ( sync ) {
			synchronized ( this.abortTask ) {
				if ( ! canceled && ! done ) {
					this.canceled = true;
					this.abortTask.notifyAll();
					return true;
				} else {
					return false;
				}
			}
		}
	}
	
	@Override
	public boolean isCancelled() {
		synchronized ( sync ) {
			return this.canceled;
		}
	}
	
	@Override
	public boolean isDone() {
		synchronized ( sync ) {
			return this.done;
		}
	}
	
	@Override
	public Void get() throws InterruptedException, ExecutionException {
		return execServ.invokeAny(createTasks());
	}
	
	@Override
	public Void get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		return execServ.invokeAny(createTasks(), timeout, unit);
	}
	
	private Collection<Callable<Void>> createTasks() {
		
		return Arrays.asList(
				() -> {
					try {
						
						synchronized ( sync ) {
							this.step = 0;
						}
						notifyStateChanged(this);
						
						for ( MacroTask task : recipe.tasks() ) {
							task.execute(simm);
							
							synchronized ( sync ) {
								this.step += 1;
							}
							notifyStateChanged(this);
						}
						
						synchronized ( sync ) {
							this.done = true;
						}
						notifyStateChanged(this);
					}
					catch ( InterruptedException ignore ) {
					}
					return null;
				},
				() -> {
					
					try {
						synchronized ( this.abortTask ) {
							this.abortTask.wait();
						}
						notifyStateChanged(this);
					}
					catch ( InterruptedException ignore ) {
					}
					return null;
				});
	}
	
	@Override
	public int id() {
		return this.id;
	}
	
	@Override
	public MacroRecipe recipe() {
		return recipe;
	}
	
	@Override
	public int step() {
		synchronized ( sync ) {
			return this.step;
		}
	}
	
	@Override
	public int taskCount() {
		return this.recipe.tasks().size();
	}
	
	@Override
	public Optional<MacroTask> presentTask() {
		synchronized ( sync ) {
			if ( this.step >= 0 && this.step < taskCount() ) {
				return Optional.of(this.recipe.tasks().get(this.step));
			} else {
				return Optional.empty();
			}
		}
	}
	
	private final Collection<PropertyChangeListener<? super MacroWorker>> listeners = new CopyOnWriteArrayList<>();
	
	@Override
	public boolean addStateChangeListener(PropertyChangeListener<? super MacroWorker> l) {
		synchronized ( listeners ) {
			boolean f = listeners.add(l);
			if ( f ) {
				l.changed(this);
			}
			return f;
		}
	}
	
	@Override
	public boolean removeStateChangeListener(PropertyChangeListener<? super MacroWorker> l) {
		synchronized ( listeners ) {
			return listeners.remove(l);
		}
	}
	
	protected void notifyStateChanged(MacroWorker w) {
		synchronized ( listeners ) {
			listeners.forEach(l -> {l.changed(w);});
		}
	}
	
	@Override
	public int hashCode() {
		return id;
	}
	
	@Override
	public boolean equals(Object other) {
		
		//TODO
		
		return false;
	}
	
	@Override
	public String toString() {
		
		//TODO
		
		return "";
	}
}
