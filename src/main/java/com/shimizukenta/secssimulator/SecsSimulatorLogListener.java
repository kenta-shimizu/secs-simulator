package com.shimizukenta.secssimulator;

import java.util.EventListener;

public interface SecsSimulatorLogListener extends EventListener {
	public void received(SecsSimulatorLog log);
}
