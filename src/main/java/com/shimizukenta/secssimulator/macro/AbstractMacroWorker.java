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

import com.shimizukenta.secs.Property;
import com.shimizukenta.secs.PropertyChangeListener;
import com.shimizukenta.secssimulator.AbstractSecsSimulator;

public abstract class AbstractMacroWorker implements MacroWorker {
	
	private final Object sync = new Object();
	private final Object abortTask = new Object();
	
	private final int id;
	private final MacroRecipe recipe;
	private final AbstractSecsSimulator simm;
	private final ExecutorService execServ;
	
	private boolean cancelled;
	private boolean done;
	private boolean failed;
	private int step;
	private Property<Integer> lastRecvSxFy = Property.newInstance(Integer.valueOf(-1));
	
	public AbstractMacroWorker(int id, MacroRecipe recipe,
			AbstractSecsSimulator simm,
			ExecutorService execServ) {
		
		this.id = id;
		this.recipe = recipe;
		this.simm = simm;
		this.execServ = execServ;
		this.cancelled = false;
		this.done = false;
		this.failed = false;
		this.step = -1;
		
		simm.addSecsCommunicatableStateChangeListener(f -> {
			this.lastRecvSxFy.set(Integer.valueOf(-1));
		});
		
		simm.addSecsMessageReceiveListener(msg -> {
			int strm = msg.getStream();
			if ( strm >= 0) {
				int func = msg.getFunction();
				this.lastRecvSxFy.set(Integer.valueOf((strm << 8) + func));
			}
		});
	}
	
	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		synchronized ( sync ) {
			synchronized ( this.abortTask ) {
				if ( ! cancelled && ! done ) {
					this.cancelled = true;
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
			return this.cancelled;
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
						
						int m = recipe.tasks().size();
						
						for ( ;; ) {
							
							synchronized ( sync ) {
								this.step += 1;
								if ( this.step >= m ) {
									break;
								}
							}
							notifyStateChanged(this);
							
							recipe.tasks().get(this.step).execute(simm, lastRecvSxFy);
						}
					}
					catch ( InterruptedException ignore ) {
					}
					catch ( Exception e ) {
						synchronized ( sync ) {
							this.failed = true;
						}
					}
					finally {
						
						synchronized ( sync ) {
							this.done = true;
						}
						notifyStateChanged(this);
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
	public boolean failed() {
		synchronized ( sync ) {
			return this.failed;
		}
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
			return this.step + 1;
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
		if ( other != null && (other instanceof AbstractMacroWorker)) {
			return ((AbstractMacroWorker)other).id == id;
		}
		return false;
	}
	
	@Override
	public String toString() {
		synchronized ( sync ) {
			StringBuilder sb = new StringBuilder("ID: ")
					.append(id())
					.append(", \"")
					.append(recipe().alias())
					.append("\", ");
			
			if ( isCancelled() ) {
				
				sb.append("cancelled");
				
			} else if ( failed() ) {
				
				sb.append("failed, ")
				.append(step())
				.append("/")
				.append(this.taskCount());
				
			} else if ( isDone() ) {
				
				sb.append("completed");
				
			} else {
				
				int s = step();
				
				if ( s > 0 ) {
					
					sb.append(s)
					.append("/")
					.append(this.taskCount());
					 
				} else {
					
					sb.append("yet");
				}
			}
			
			return sb.toString();
		}
	}
}
