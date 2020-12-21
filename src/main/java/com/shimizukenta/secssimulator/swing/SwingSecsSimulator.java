package com.shimizukenta.secssimulator.swing;

import java.io.IOException;

import com.shimizukenta.secssimulator.gui.AbstractGuiSecsSimulator;

public class SwingSecsSimulator extends AbstractGuiSecsSimulator {

	public SwingSecsSimulator(SwingSecsSimulatorConfig config) {
		super(config);
		
		this.addLogListener(log -> {
			
			//TODO
		});
		
		this.addSecsCommunicatableStateChangeListener(communicatable -> {
			
			//TODO
		});
		
		this.loggingProperty().addChangeListener(path -> {
			
			//TODO
		});
		
		this.addSmlAliasesChangeListener(pairs -> {
			
			//TODO
		});
		
		this.addMacroRecipeChangeListener(pairs -> {
			
			//TODO
		});
		
		this.addMacroWorkerStateChangedListener(w -> {
			
			//TODO
		});
		
	}
	
	@Override
	public void quitApplication() {
		
		//TODO
		//dispose window
		
		try {
			super.quitApplication();
		}
		catch (IOException giveup) {
		}
	}
	
	private void showWindow() {
		
		//TODO
	}
	
	public static void main(String[] args) {
		
		try {
			
			final SwingSecsSimulatorConfig config = new SwingSecsSimulatorConfig();
			
			final SwingSecsSimulator simm = new SwingSecsSimulator(config);
			
			try {
				simm.addApplicationQuitListener(app -> {
					synchronized ( SwingSecsSimulator.class ) {
						SwingSecsSimulator.class.notifyAll();
					}
				});
				
				simm.showWindow();
				
				synchronized ( SwingSecsSimulator.class ) {
					SwingSecsSimulator.class.wait();
				}
			}
			finally {
				simm.quitApplication();
			}
		}
		catch ( InterruptedException ignore ) {
		}
		catch ( Throwable e ) {
			/* giveup */
		}
	}

}
