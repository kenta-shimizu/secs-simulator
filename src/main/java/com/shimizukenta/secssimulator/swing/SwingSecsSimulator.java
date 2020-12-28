package com.shimizukenta.secssimulator.swing;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;

import com.shimizukenta.jsonhub.JsonHubParseException;
import com.shimizukenta.secs.SecsCommunicator;
import com.shimizukenta.secs.SecsMessage;
import com.shimizukenta.secs.sml.SmlMessage;
import com.shimizukenta.secs.sml.SmlParseException;
import com.shimizukenta.secssimulator.SecsSimulatorException;
import com.shimizukenta.secssimulator.gui.AbstractGuiSecsSimulator;
import com.shimizukenta.secssimulator.macro.MacroRecipe;

public class SwingSecsSimulator extends AbstractGuiSecsSimulator {
	
	private final ExecutorService execServ = Executors.newCachedThreadPool(r -> {
		Thread th = new Thread(r);
		th.setDaemon(true);
		return th;
	});
	
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
		
		this.config.protocol().addChangeListener(protocol -> {
			execServ.execute(() -> {
				this.closeCommunicator();
			});
		});
	}
	
	
	@Override
	public void quitApplication() {
		
		try {
			execServ.shutdown();
			if ( ! execServ.awaitTermination(1L, TimeUnit.MILLISECONDS) ) {
				execServ.shutdownNow();
				if ( ! execServ.awaitTermination(10L, TimeUnit.SECONDS) ) {
					/* giveup */
				}
			}
		}
		catch ( InterruptedException giveup ) {
		}
		
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
		catch ( IOException e ) {
			putFailure(e);
		}
		return null;
	}
	
	@Override
	public void closeCommunicator() {
		try {
			super.closeCommunicator();
		}
		catch ( IOException e ) {
			putFailure(e);
		}
	}
	
	@Override
	public Optional<SecsMessage> send(SmlMessage sm) throws InterruptedException {
		try {
			return super.send(sm);
		}
		catch ( SecsSimulatorException e ) {
			putFailure(e);
		}
		return Optional.empty();
	}
	
	public void asyncSend(SmlMessage sm) {
		execServ.execute(() -> {
			try {
				send(sm);
			}
			catch ( InterruptedException ignore ) {
			}
		});
	}
	
	public void asyncLinktest() {
		execServ.execute(() -> {
			try {
				linktest();
			}
			catch ( InterruptedException ignore ) {
			}
		});
	}
	
	protected SwingSecsSimulatorConfig config() {
		return config;
	}
	
	private void showMainFrame() {
		this.frame.setVisible(true);
	}
	
	
	protected boolean showSetConfigDialog() {
		return this.frame.showSetConfigDialog();
	}
	
	protected boolean showLoadConfigDialog() {
		return this.frame.showLoadConfigDialog();
	}
	
	protected void showSaveConfigDialog() {
		this.frame.showSaveConfigDialog();
	}
	
	protected void showLoggingDialog() {
		this.frame.showLoggingDialog();
	}
	
	protected void showAddSmlDialog() {
		this.frame.showAddSmlDialog();
	}
	
	protected void showSmlEditorFrame() {
		this.frame.showSmlEditorFrame();
	}
	
	protected Optional<SmlMessage> showLoadSmlDialog() throws SmlParseException, IOException {
		return this.frame.showLoadSmlFileDialog();
	}
	
	protected Optional<Path> showSaveSmlDialog() {
		return this.frame.showSaveSmlFileDialog();
	}
	
	protected void showAddMacroRecipeDiralog() {
		this.frame.showAddMacroRecipeDiralog();
	}
	
	protected void showMacroFrame() {
		this.frame.showMacroFrame();
	}
	
	protected void showSmlMessage(SmlMessage sm) {
		this.frame.showSmlMessage(sm);
	}
	
	protected void showMacroRecipeMessage(MacroRecipe recipe) {
		this.frame.showMacroRecipeMessage(recipe);
	}
	
	protected void putFailure(Throwable t) {
		this.frame.putFailure(t);
	}
	
	public static void main(String[] args) {
		
		try {
			
			final SwingSecsSimulatorConfig config = new SwingSecsSimulatorConfig();
			
			boolean configLoaded = false;
			
			{
				final Map<String, List<String>> map = new HashMap<>();
				
				for ( int i = 0, m = args.length; i < m; i += 2 ) {
					String key = args[i].toLowerCase();
					map.computeIfAbsent(key, k -> new ArrayList<>()).add(args[i + 1]);
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
				
				simm.showMainFrame();
				
				if ( configLoaded ) {
					
					simm.frame.showViewFrame();
					simm.frame.showControlFrame();
					
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
					
					simm.frame.showEntryDialog();
					
					simm.frame.showViewFrame();
					simm.frame.showControlFrame();
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
