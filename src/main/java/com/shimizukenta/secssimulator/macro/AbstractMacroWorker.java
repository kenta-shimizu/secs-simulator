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
import com.shimizukenta.secs.ReadOnlyProperty;
import com.shimizukenta.secssimulator.AbstractSecsSimulator;

public abstract class AbstractMacroWorker implements MacroWorker {
	
	private final Object abortTask = new Object();
	
	private final int id;
	private final MacroRecipe recipe;
	private final AbstractMacroEngine engine;
	
	private boolean cancelled;
	private boolean done;
	private boolean failed;
	private Exception failedException;
	private int step;
	private Property<Integer> lastRecvSxFy;
	
	public AbstractMacroWorker(int id, MacroRecipe recipe, AbstractMacroEngine engine) {
		
		this.id = id;
		this.recipe = recipe;
		this.engine = engine;
		this.cancelled = false;
		this.done = false;
		this.failed = false;
		this.failedException = null;
		this.step = -1;
		this.lastRecvSxFy = Property.newInstance(Integer.valueOf(-1));
		
		simulator().addSecsCommunicatableStateChangeListener(f -> {
			this.lastRecvSxFy.set(Integer.valueOf(-1));
		});
		
		simulator().addSecsMessageReceiveListener(msg -> {
			int strm = msg.getStream();
			if ( strm >= 0) {
				int func = msg.getFunction();
				this.lastRecvSxFy.set(Integer.valueOf((strm * 256) + func));
			}
		});
	}
	
	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		synchronized ( this ) {
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
		synchronized ( this ) {
			return this.cancelled;
		}
	}
	
	@Override
	public boolean isDone() {
		synchronized ( this ) {
			return this.done;
		}
	}
	
	@Override
	public Void get() throws InterruptedException, ExecutionException {
		return executorService().invokeAny(createTasks());
	}
	
	@Override
	public Void get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		return executorService().invokeAny(createTasks(), timeout, unit);
	}
	
	protected AbstractSecsSimulator simulator() {
		return engine.simulator();
	}
	
	protected ExecutorService executorService() {
		return engine.executorService();
	}
	
	protected ReadOnlyProperty<Integer> lastRecvSxFy() {
		return lastRecvSxFy;
	}
	
	private Collection<Callable<Void>> createTasks() {
		
		return Arrays.asList(
				() -> {
					
					try {
						int m = recipe.tasks().size();
						
						for ( ;; ) {
							
							synchronized ( this ) {
								this.step += 1;
								if ( this.step >= m ) {
									break;
								}
							}
							notifyStateChanged(this);
							
							this.presentTask().get().execute(this);
						}
					}
					catch ( InterruptedException ignore ) {
					}
					catch ( Exception e ) {
						synchronized ( this ) {
							this.failed = true;
							this.failedException = e;
						}
					}
					finally {
						
						synchronized ( this ) {
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
					}
					catch ( InterruptedException ignore ) {
					}
					
					return null;
				});
	}
	
	@Override
	public boolean failed() {
		synchronized ( this ) {
			return this.failed;
		}
	}
	
	@Override
	public Optional<Exception> failedException() {
		synchronized ( this ) {
			return failedException == null ? Optional.empty() : Optional.of(failedException);
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
		synchronized ( this ) {
			return this.step + 1;
		}
	}
	
	@Override
	public int taskCount() {
		return this.recipe.tasks().size();
	}
	
	@Override
	public Optional<MacroTask> presentTask() {
		synchronized ( this ) {
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
		if ( other != null && (other instanceof MacroWorker)) {
			return ((MacroWorker)other).id() == this.id();
		}
		return false;
	}
	
	@Override
	public int compareTo(MacroWorker other) {
		return Integer.valueOf(this.id()).compareTo(Integer.valueOf(other.id()));
	}
	
	@Override
	public String toString() {
		
		synchronized ( this ) {
			
			final StringBuilder sb = new StringBuilder("ID: ")
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
					
					this.presentTask().ifPresent(pt -> {
						
						sb.append(", ")
						.append(pt);
					});
					
				} else {
					
					sb.append("yet");
				}
			}
			
			return sb.toString();
		}
	}
	
}
