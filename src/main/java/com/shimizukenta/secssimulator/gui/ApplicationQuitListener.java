package com.shimizukenta.secssimulator.gui;

import java.util.EventListener;

public interface ApplicationQuitListener extends EventListener {
	public void quit(AbstractGuiSecsSimulator simulator);
}
