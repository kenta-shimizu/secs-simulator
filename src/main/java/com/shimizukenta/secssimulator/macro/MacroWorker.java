package com.shimizukenta.secssimulator.macro;

import java.util.Optional;
import java.util.concurrent.Future;

import com.shimizukenta.secs.PropertyChangeListener;

public interface MacroWorker extends Future<Void> {
	
	/**
	 * Returns {@code true} if macro failed.
	 * 
	 * @return {@code true} if macro failed
	 */
	public boolean failed();
	
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
