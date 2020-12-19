package com.shimizukenta.secssimulator;

import java.util.EventListener;

public interface ThrowableCatchListener extends EventListener {
	public void catched(Throwable t);
}
