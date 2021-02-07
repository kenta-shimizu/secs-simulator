package com.shimizukenta.jsoncommunicator;

import java.net.SocketAddress;

public final class SimpleJsonCommunicatorConnectionLog extends AbstractJsonCommunicatorConnectionLog {
	
	private static final long serialVersionUID = 1616168998888886181L;
	
	private SimpleJsonCommunicatorConnectionLog(CharSequence subject, SocketAddress local, SocketAddress remote, boolean isConnecting) {
		super(subject, local, remote, isConnecting);
	}
	
	private static final String commonAccepted = "Accepted";
	
	public static SimpleJsonCommunicatorConnectionLog accepted(SocketAddress local, SocketAddress remote) {
		return new SimpleJsonCommunicatorConnectionLog(commonAccepted, local, remote, true);
	}
	
	private static final String commonTryConnect = "Try-Connect";
	
	public static SimpleJsonCommunicatorConnectionLog tryConnect(SocketAddress remote) {
		return new SimpleJsonCommunicatorConnectionLog(commonTryConnect, null, remote, false);
	}
	
	private static final String commonConnected = "Connected";
	
	public static SimpleJsonCommunicatorConnectionLog connected(SocketAddress local, SocketAddress remote) {
		return new SimpleJsonCommunicatorConnectionLog(commonConnected, local, remote, true);
	}
	
	private static final String commonClosed = "Channel-closed";
	
	public static SimpleJsonCommunicatorConnectionLog closed(SocketAddress local, SocketAddress remote) {
		return new SimpleJsonCommunicatorConnectionLog(commonClosed, local, remote, false);
	}
	
}
