package com.shimizukenta.secssimulator.macro;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;

import com.shimizukenta.secs.BooleanProperty;
import com.shimizukenta.secs.ReadOnlyBooleanProperty;
import com.shimizukenta.secssimulator.AbstractSecsSimulator;
import com.shimizukenta.secssimulator.AbstractSecsSimulatorEngine;

public class MacroEngine extends AbstractSecsSimulatorEngine {
	
	private final BooleanProperty processing = BooleanProperty.newInstance(false);
	
	private final MacroExecutor executor;
	
	public MacroEngine(AbstractSecsSimulator engine) {
		super(engine);
		this.executor = new MacroExecutor(engine);
	}
	
	private final Object syncMacroAbort = new Object();
	
	public void start(Path path) {
		
		stop();
		
		executorService().execute(() -> {
			
			try {
				processing.waitUntilFalse();
				
				final Callable<Void> abortTask = () -> {
					try {
						synchronized ( syncMacroAbort ) {
							syncMacroAbort.wait();
						}
					}
					catch ( InterruptedException ignore ) {
					}
					
					return null;
				};
				
				final Callable<Void> execTask = () -> {
					
					try {
						List<MacroRequest> requests = MacroFileReader.getInstance().lines(path);
						
						processing.set(true);
						
						for ( MacroRequest r : requests ) {
							
							if ( r.command() != null ) {
								report(MacroReport.requestStarted(path, r));
								executor.execute(r);
								report(MacroReport.requestFinished(path, r));
							}
						}
						
						report(MacroReport.completed(path));
					}
					catch ( IOException e ) {
						report(MacroReport.failed(path, e));
					}
					catch ( InterruptedException ignore ) {
					}
					
					return null;
				};
				
				executeInvokeAny(abortTask, execTask);
			}
			catch ( InterruptedException ignore ) {
			}
			catch ( ExecutionException e ) {
				
				Throwable t = e.getCause();
				
				if ( t instanceof RuntimeException ) {
					throw (RuntimeException)t;
				}
				
				if ( t instanceof Error ) {
					throw (Error)t;
				}
				
				notifyLog(t);
			}
			finally {
				processing.set(false);
			}
		});
	}
	
	public void stop() {
		synchronized ( syncMacroAbort ) {
			syncMacroAbort.notifyAll();
		}
	}
	
	public ReadOnlyBooleanProperty processing() {
		return processing;
	}
	
	private final Collection<MacroReportListener> reportListeners = new CopyOnWriteArrayList<>();
	
	public boolean addMacroReportListener(MacroReportListener l) {
		return reportListeners.add(l);
	}
	
	public boolean removeMacroReportListener(MacroReportListener l) {
		return reportListeners.remove(l);
	}
	
	private void report(MacroReport r) {
		reportListeners.forEach(l -> {
			l.report(r);
		});
	}
	
}
