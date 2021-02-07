package com.shimizukenta.jsoncommunicator;

import java.net.SocketAddress;

/**
 * This interface is implementation of Connecting Local and Remote Socket-Address.
 * 
 * <p>
 * To get Local-Socket-Address, {@link #local()}.<br />
 * TO get Remote-Socket-Address, {@link #remote()}.<br />
 * To get is Connecting, {@link #isConnecting()}.<br />
 * </p>
 * 
 * @author kenta-shimizu
 *
 */
public interface JsonCommunicatorConnectionLog extends JsonCommunicatorLog {
	
	/**
	 * Return Local-Socket-Address.
	 * 
	 * @return Local-Socket-Address if setted, {@code null} otherwise.
	 */
	public SocketAddress local();
	
	/**
	 * Return Remote-Socket-Address.
	 * 
	 * @return Remote-Socket-Address if setted, {@code null} otherwise.
	 */
	public SocketAddress remote();
	
	/**
	 * Returns {@code true} if connecting.
	 * 
	 * @return {@code true} if connecting.
	 */
	public boolean isConnecting();
	
}
