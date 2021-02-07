package com.shimizukenta.jsoncommunicator;

/**
 * This interface implements Throwable-Log.
 * 
 * <p>
 * To get {@link Throwable}, {@link #getCause()}.<br />
 * </p>
 * 
 * @author kenta-shimizu
 *
 */
public interface JsonCommunicatorThrowableLog extends JsonCommunicatorLog {
	
	/**
	 * Returns Log cause {@link Throwable}.
	 * 
	 * @return log cause Throwable
	 */
	public Throwable getCause();
}
