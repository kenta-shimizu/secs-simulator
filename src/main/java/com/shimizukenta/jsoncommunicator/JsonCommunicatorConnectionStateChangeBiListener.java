package com.shimizukenta.jsoncommunicator;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.EventListener;

public interface JsonCommunicatorConnectionStateChangeBiListener extends EventListener {
	public void changed(AsynchronousSocketChannel channel, JsonCommunicatorConnectionState state);
}
