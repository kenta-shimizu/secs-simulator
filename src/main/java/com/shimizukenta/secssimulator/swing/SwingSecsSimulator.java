package com.shimizukenta.secssimulator.swing;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.swing.JFrame;

import com.shimizukenta.jsonhub.JsonHubParseException;
import com.shimizukenta.secs.SecsCommunicator;
import com.shimizukenta.secs.SecsMessage;
import com.shimizukenta.secs.sml.SmlMessage;
import com.shimizukenta.secssimulator.SecsSimulatorException;
import com.shimizukenta.secssimulator.gui.AbstractGuiSecsSimulator;

public class SwingSecsSimulator extends AbstractGuiSecsSimulator {
	
	private final SwingSecsSimulatorConfig config;
	private final SwingMainFrame frame;
	
	public SwingSecsSimulator(SwingSecsSimulatorConfig config) {
		super(config);
		
		this.config = config;
		
		this.frame = new SwingMainFrame(this);
		
		this.frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.frame.addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent ev) {
				notifyApplicationQuit();
			}
		});
		
		this.addLogListener(frame::putMessageLog);
		this.addSecsCommunicatableStateChangeListener(frame::notifyCommunicateStateChanged);
		this.loggingProperty().addChangeListener(frame::notifyLoggingPropertyChanged);
		this.addSmlAliasesChangeListener(frame::notifySmlAliasesChanged);
		this.addMacroRecipeChangeListener(frame::notifyMacroRecipeChanged);
		this.addMacroWorkerStateChangeListener(frame::notifyMacroWorkerStateChanged);
	}
	
	@Override
	protected void notifyApplicationQuit() {
		super.notifyApplicationQuit();
	}
	
	@Override
	public void quitApplication() {
		
		this.frame.setVisible(false);
		this.frame.dispose();
		
		try {
			super.quitApplication();
		}
		catch (IOException giveup) {
		}
	}
	
	@Override
	public SecsCommunicator openCommunicator() {
		try {
			return super.openCommunicator();
		}
		catch ( IOException giveup ) {
		}
		return null;
	}
	
	@Override
	public void closeCommunicator() {
		try {
			super.closeCommunicator();
		}
		catch ( IOException giveup ) {
		}
	}
	
	@Override
	public Optional<SecsMessage> send(SmlMessage sm) throws InterruptedException {
		try {
			return super.send(sm);
		}
		catch ( SecsSimulatorException e ) {
		}
		return Optional.empty();
	}
	
	protected SwingSecsSimulatorConfig config() {
		return config;
	}
	
	private void showWindow() {
		this.frame.setVisible(true);
	}
	
	protected void showSendSmlDirectFrame() {
		this.frame.showSendSmlDirectFrame();
	}
	
	protected void showSml(SmlMessage sm) {
		this.frame.showSml(sm);
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
					try {
						if ( config.load(Paths.get(v)) ) {
							configLoaded = true;
						}
					}
					catch ( InvalidPathException | JsonHubParseException e ) {
						throw new IOException(e);
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
				
				if ( configLoaded ) {
					
					config.autoLogging().ifPresent(path -> {
						try {
							simm.startLogging(path);
						}
						catch (InterruptedException ignore) {
						}
						catch (IOException giveup) {
						}
					});
					
					if ( config.autoOpen().booleanValue() ) {
						simm.openCommunicator();
					}
					
				} else {
					
					//TODO
					//show-dialog.
				}
				
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
		catch ( Error e ) {
			e.printStackTrace();
			System.exit(10);
		}
		catch ( RuntimeException e ) {
			e.printStackTrace();
			System.exit(100);
		}
		catch ( Exception e ) {
			e.printStackTrace();
			System.exit(1000);
		}
		
		System.exit(0);
	}
	
}
