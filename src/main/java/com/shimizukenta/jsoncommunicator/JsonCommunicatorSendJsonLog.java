package com.shimizukenta.jsoncommunicator;

import java.net.SocketAddress;
import java.util.List;

/**
 * This interface is implementation of Sended JSON information.
 * 
 * <p>
 * To get sended Socket-Address list, {@link #remotes()}.<br />
 * To get sended JSON, {@link #json()}.<br />
 * </p>
 * 
 * @author kenta-shimizu
 *
 */
public interface JsonCommunicatorSendJsonLog extends JsonCommunicatorLog {
	
	/**
	 * Returns Sended Socket-Address list.
	 * 
	 * @return Sended Socket-Address list
	 */
	public List<SocketAddress> remotes();
	
	/**
	 * Returns Sended JSON.
	 * 
	 * @return Sended JSON
	 */
	public String json();
	
}
