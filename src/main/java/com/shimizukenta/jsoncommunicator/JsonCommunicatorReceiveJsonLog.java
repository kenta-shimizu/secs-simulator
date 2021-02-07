package com.shimizukenta.jsoncommunicator;

import java.net.SocketAddress;

/**
 * This interface is implementation of Receive-JSON information.
 * 
 * <p>
 * To get Local-Socket-Address, {@link #local()}.<br />
 * To get Remote-Socket-Address, {@link #remote()}.<br />
 * To get received JSON, {@link #json()}.<br />
 * </p>
 * 
 * @author kenta-shimizu
 *
 */
public interface JsonCommunicatorReceiveJsonLog extends JsonCommunicatorLog {
	
	/**
	 * Returns Local-Socket-Address.
	 * 
	 * @return Local-Socket-Address if setted, {@code null} otherwise
	 */
	public SocketAddress local();
	
	/**
	 * Returns Remote-Socket-Address.
	 * 
	 * @return Remote-Socket-Address if setted, {@code null} otherwise
	 */
	public SocketAddress remote();
	
	/**
	 * Returns received JSON.
	 * 
	 * @return received JSON
	 */
	public String json();
	
}
