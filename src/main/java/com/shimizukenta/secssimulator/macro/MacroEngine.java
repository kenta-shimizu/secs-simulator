package com.shimizukenta.secssimulator.macro;

import java.io.Closeable;
import java.util.List;
import java.util.Optional;

import com.shimizukenta.secs.PropertyChangeListener;

/**
 * This interface is Macro-executor, start/stop, add/remove listener.
 * 
 * @author kenta-shimizu
 *
 */
public interface MacroEngine extends Closeable {
	
	/**
	 * Start Macro.
	 * 
	 * @param recipe
	 * @return Optional has value if start success, otherwise {@code Optional.empty()}
	 * @throws InterruptedException
	 */
	public Optional<MacroWorker> start(MacroRecipe recipe) throws InterruptedException;
	
	/**
	 * Stop Macro.
	 * 
	 * @param worker
	 * @return Optional has value if stop success, otherwise {@code Optional.empty()}
	 * @throws InterruptedException
	 */
	public Optional<MacroWorker> stop(MacroWorker worker) throws InterruptedException;
	
	/**
	 * Stop Macro.
	 * 
	 * @param workerId
	 * @return Optional has value if stop success, otherwise {@code Optional.empty()}
	 * @throws InterruptedException
	 */
	public Optional<MacroWorker> stop(int workerId) throws InterruptedException;
	
	/**
	 * Stop all Macro.
	 * 
	 * @return List of stop success macros
	 * @throws InterruptedException
	 */
	public List<MacroWorker> stop() throws InterruptedException;
	
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
