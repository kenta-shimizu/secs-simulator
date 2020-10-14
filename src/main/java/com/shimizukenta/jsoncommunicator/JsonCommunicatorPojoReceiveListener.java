package com.shimizukenta.jsoncommunicator;

import java.util.EventListener;

public interface JsonCommunicatorPojoReceiveListener<T> extends EventListener {
	public void received(T pojo);
}
