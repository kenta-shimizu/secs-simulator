package com.shimizukenta.jsoncommunicator;

import java.net.SocketAddress;

/**
 * This interface is implementation of Server-Bind-Status.
 * 
 * <p>
 * To get bind Socket-Address, {@link #socketAddress()}.<br />
 * To check binded, {@link #isBinded()}.<br />
 * To check closed, {@link #isClosed()}.<br />
 * </p>
 * 
 * @author kenta-shimizu
 *
 */
public interface JsonCommunicatorServerBindLog extends JsonCommunicatorLog {
	
	/**
	 * Returns bind Socket-Address.
	 * 
	 * @return bind Socket-Address
	 */
	public SocketAddress socketAddress();
	
	/**
	 * Returns {@code true} if binded.
	 * 
	 * @return {@code true} if binded
	 */
	public boolean isBinded();
	
	/**
	 * Returns {@code true} if closed.
	 * 
	 * @return {@code true} if closed
	 */
	public boolean isClosed();
	
}
