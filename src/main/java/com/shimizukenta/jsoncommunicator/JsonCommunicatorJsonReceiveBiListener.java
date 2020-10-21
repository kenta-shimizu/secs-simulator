package com.shimizukenta.jsoncommunicator;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.EventListener;

public interface JsonCommunicatorJsonReceiveBiListener extends EventListener {
	public void received(AsynchronousSocketChannel channel, String json);
}
