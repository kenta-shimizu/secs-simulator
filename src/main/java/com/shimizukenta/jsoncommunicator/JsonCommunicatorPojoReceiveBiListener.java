package com.shimizukenta.jsoncommunicator;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.EventListener;

public interface JsonCommunicatorPojoReceiveBiListener<T> extends EventListener {
	public void received(AsynchronousSocketChannel channel, T pojo);
}
