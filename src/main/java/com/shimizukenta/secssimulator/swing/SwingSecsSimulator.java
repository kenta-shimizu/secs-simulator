package com.shimizukenta.secssimulator.swing;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;

import com.shimizukenta.secssimulator.gui.AbstractGuiSecsSimulator;

public class SwingSecsSimulator extends AbstractGuiSecsSimulator {
	
	private final SwingSecsSimulatorConfig config;
	private final SwingCockpitFrame cockpit;
	
	public SwingSecsSimulator(SwingSecsSimulatorConfig config) {
		super(config);
		
		this.config = config;
		
		this.cockpit = new SwingCockpitFrame(this);
		
		this.cockpit.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.cockpit.addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent ev) {
				cockpit.setVisible(false);
				notifyApplicationQuit();
			}
		});
		
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
		
		this.addMacroWorkerStateChangeListener(w -> {
			
			//TODO
		});
		
	}
	
	@Override
	public void quitApplication() {
		
		this.cockpit.dispose();
		
		try {
			super.quitApplication();
		}
		catch (IOException giveup) {
		}
	}
	
	protected SwingSecsSimulatorConfig config() {
		return config;
	}
	
	private void showWindow() {
		this.cockpit.setVisible(true);
	}
	
	public static void main(String[] args) {
		
		try {
			
			final SwingSecsSimulatorConfig config = new SwingSecsSimulatorConfig();
			
			boolean configLoaded = false;
			
			{
				final Map<String, List<String>> map = new HashMap<>();
				
				for ( int i = 0, m = args.length; i < m; i += 2 ) {
					map.computeIfAbsent(args[i], k -> new ArrayList<>()).add(args[i + 1]);
				}
				
				for ( String v : map.getOrDefault("--config", Collections.emptyList()) ) {
					if ( config.load(Paths.get(v)) ) {
						configLoaded = true;
					}
				}
				
				for ( String v : map.getOrDefault("--auto-open", Collections.emptyList()) ) {
					config.autoOpen().set(Boolean.parseBoolean(v));
				}
				
				for ( String v : map.getOrDefault("--auto-logging", Collections.emptyList()) ) {
					config.autoLogging(Paths.get(v));
				}
			}

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
			
			e.printStackTrace();
			/* giveup */
		}
	}

}
