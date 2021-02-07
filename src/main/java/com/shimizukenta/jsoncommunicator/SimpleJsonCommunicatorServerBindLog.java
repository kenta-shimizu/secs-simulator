package com.shimizukenta.jsoncommunicator;

import java.net.SocketAddress;

public final class SimpleJsonCommunicatorServerBindLog extends AbstractJsonCommunicatorServerBindLog {
	
	private static final long serialVersionUID = -123468483221227445L;
	
	private SimpleJsonCommunicatorServerBindLog(CharSequence subject, SocketAddress addr, boolean isBinded, boolean isClosed) {
		super(subject, addr, isBinded, isClosed);
	}
	
	private static final String commonTryBind = "server-Try-Bind";
	
	public static SimpleJsonCommunicatorServerBindLog tryBind(SocketAddress addr) {
		return new SimpleJsonCommunicatorServerBindLog(commonTryBind, addr, false, false);
	}
	
	private static final String commonBinded = "Server-Binded";
	
	public static SimpleJsonCommunicatorServerBindLog binded(SocketAddress addr) {
		return new SimpleJsonCommunicatorServerBindLog(commonBinded, addr, true, false);
	}
	
	private static final String commonClosed = "Server-Closed";
	
	public static SimpleJsonCommunicatorServerBindLog closed(SocketAddress addr) {
		return new SimpleJsonCommunicatorServerBindLog(commonClosed, addr, false, true);
	}
	
}
