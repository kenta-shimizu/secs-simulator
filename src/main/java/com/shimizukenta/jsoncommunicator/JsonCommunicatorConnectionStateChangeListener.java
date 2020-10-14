package com.shimizukenta.jsoncommunicator;

import java.util.EventListener;

public interface JsonCommunicatorConnectionStateChangeListener extends EventListener {
	public void changed(JsonCommunicatorConnectionState state);
}
