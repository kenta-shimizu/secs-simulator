package com.shimizukenta.secssimulator.gui;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

import com.shimizukenta.secssimulator.AbstractSecsSimulator;

public abstract class AbstractGuiSecsSimulator extends AbstractSecsSimulator {

	public AbstractGuiSecsSimulator(AbstractGuiSecsSimulatorConfig config) {
		super(config);
	}
	
	private final Collection<ApplicationQuitListener> quitListeners = new CopyOnWriteArrayList<>();
	
	protected boolean addApplicationQuitListener(ApplicationQuitListener l) {
		return quitListeners.add(l);
	}
	
	protected boolean removeApplicationQuitListener(ApplicationQuitListener l) {
		return quitListeners.remove(l);
	}
	
	protected void notifyApplicationQuit() {
		quitListeners.forEach(l -> {
			l.quit(this);
		});
	}
}
