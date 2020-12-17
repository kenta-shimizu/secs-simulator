package com.shimizukenta.secssimulator.logging;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import com.shimizukenta.secs.PropertyChangeListener;
import com.shimizukenta.secs.ReadOnlyProperty;

public interface LoggingEngine extends Closeable {
	
	/**
	 * Start logging.
	 * 
	 * @param path of log-file
	 * @return Optional has value if logging-start success
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public Optional<Path> start(Path path) throws IOException, InterruptedException;
	
	/**
	 * Stop logging.
	 * 
	 * @return Optional has value if logging-stop success
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public Optional<Path> stop() throws IOException, InterruptedException;
	
	/**
	 * Add logging-state-change listener.
	 * 
	 * <p>
	 * Notify path if logging started.<br />
	 * Notify {@code null} if logging stopped.<br />
	 * </p>
	 * 
	 * @param l
	 * @return {@code true} if add success
	 */
	public boolean addStateChangeListener(PropertyChangeListener<? super Path> l);
	
	/**
	 * Remove logging-state-change listener.
	 * 
	 * @param l
	 * @return {@code true} if remove success.
	 */
	public boolean removeStateChangeListener(PropertyChangeListener<? super Path> l);
	
	/**
	 * Put log to engine.
	 * 
	 * @param log
	 */
	public void putLog(Object log);
	
	/**
	 * Returns Logging-property
	 * 
	 * @return Logging-property
	 */
	public ReadOnlyProperty<Path> loggingProperty();
	
}
