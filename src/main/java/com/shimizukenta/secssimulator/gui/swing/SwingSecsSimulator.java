package com.shimizukenta.secssimulator.gui.swing;

import com.shimizukenta.secssimulator.gui.AbstractGuiSecsSimulator;
import com.shimizukenta.secssimulator.gui.AbstractGuiSecsSimulatorConfig;

public class SwingSecsSimulator extends AbstractGuiSecsSimulator {
	
	public static void main(String[] args) {
		
		try {
			SwingSecsSimulatorConfig config = SwingSecsSimulatorConfig.get(args);
			
			SwingSecsSimulator inst = new SwingSecsSimulator(config);
			
			
		}
		catch ( Throwable t ) {
			
		}
	}
	
	private final SwingSecsSimulatorConfig config;
	
	public SwingSecsSimulator(SwingSecsSimulatorConfig config) {
		super(config);
		this.config = config;
	}

}
