package com.shimizukenta.secssimulator;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.shimizukenta.secs.InterruptableRunnable;
import com.shimizukenta.secs.SecsCommunicator;
import com.shimizukenta.secs.SecsLog;
import com.shimizukenta.secs.sml.SmlMessage;
import com.shimizukenta.secs.sml.SmlParseException;

public abstract class AbstractSecsSimulatorEngine {
	
	private ExecutorService execServ ;
	
	public AbstractSecsSimulatorEngine() {
		this.execServ = null;
	}
	
	protected ExecutorService executorService() {
		
		synchronized  ( this ) {
			
			if ( this.execServ == null ) {
				
				execServ = Executors.newCachedThreadPool(r -> {
					Thread th = new Thread(r);
					th.setDaemon(true);
					return th;
				});
			}
			
			return this.execServ;
		}
	}
	
	protected void executeLoopTask(InterruptableRunnable r) {
		executorService().execute(() -> {
			try {
				for ( ;; ) {
					r.run();
				}
			}
			catch ( InterruptedException ignore ) {
			}
		});
	}
	
	protected <T> T executeInvokeAny(Collection<? extends Callable<T>> tasks)
			throws InterruptedException, ExecutionException {
		return executorService().invokeAny(tasks);
	}
	
	protected <T> T executeInvokeAny(Callable<T> task)
			throws InterruptedException, ExecutionException {
		return executeInvokeAny(Arrays.asList(task));
	}
	
	protected <T> T executeInvokeAny(Callable<T> task1, Callable<T> task2)
			throws InterruptedException, ExecutionException {
		return executeInvokeAny(Arrays.asList(task1, task2));
	}
	
	protected <T> T executeInvokeAny(Callable<T> task1, Callable<T> task2, Callable<T> task3)
			throws InterruptedException, ExecutionException {
		return executeInvokeAny(Arrays.asList(task1, task2, task3));
	}
	
	abstract protected SecsCommunicator openCommunicator() throws IOException;
	abstract protected void closeCommunicator() throws IOException;
	abstract protected void waitUntilCommunicatable() throws InterruptedException;
	
	abstract protected SmlMessage parseSml(CharSequence sml) throws SmlParseException;
	
	abstract protected void notifyLog(SecsLog log);
	
	protected void notifyLog(Throwable t) {
		notifyLog(new SecsLog(t));
	}
	
}
