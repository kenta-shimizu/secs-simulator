package com.shimizukenta.secssimulator.macro;

import java.util.Optional;
import java.util.concurrent.Future;

import com.shimizukenta.secs.PropertyChangeListener;

/**
 * This interface is Macro-worker, provices worker id, step, recipe, status.
 * 
 * @author kenta-shimizu
 *
 */
public interface MacroWorker extends Future<Void> {
	
	/**
	 * Returns {@code true} if macro failed.
	 * 
	 * @return {@code true} if macro failed
	 */
	public boolean failed();
	
	/**
	 * Return Optional has Exception if failed.
	 * 
	 * @return Optional has Exception if failed.
	 */
	public Optional<Exception> failedException();
	
	/**
	 * Returns id.
	 * 
	 * @return id
	 */
	public int id();
	
	/**
	 * Returns recipe.
	 * 
	 * @return retipe
	 */
	public MacroRecipe recipe();
	
	/**
	 * Returns step of tasks.
	 * 
	 * @return step, 0 if not started
	 */
	public int step();
	
	/**
	 * Returns tasks size.
	 * 
	 * @return tasks size
	 */
	public int taskCount();
	
	/**
	 * Returns present-task.
	 * 
	 * @return present-task
	 */
	public Optional<MacroTask> presentTask();
	
	/**
	 * Add listener.
	 * 
	 * @param l
	 * @return {@code true} if add success
	 */
	public boolean addStateChangeListener(PropertyChangeListener<? super MacroWorker> l);
	
	/**
	 * Remove listener.
	 * 
	 * @param l
	 * @return {@code true} if remove success
	 */
	public boolean removeStateChangeListener(PropertyChangeListener<? super MacroWorker> l);
	
}
