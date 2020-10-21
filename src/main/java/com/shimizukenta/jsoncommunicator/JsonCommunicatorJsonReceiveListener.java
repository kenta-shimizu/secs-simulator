package com.shimizukenta.jsoncommunicator;

import java.util.EventListener;

public interface JsonCommunicatorJsonReceiveListener extends EventListener {
	public void received(String json);
}
