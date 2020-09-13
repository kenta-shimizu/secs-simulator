package com.shimizukenta.secssimulator;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import com.shimizukenta.secs.SecsCommunicator;
import com.shimizukenta.secs.SecsLog;
import com.shimizukenta.secs.sml.SmlMessage;
import com.shimizukenta.secs.sml.SmlParseException;

public abstract class AbstractSecsSimulatorEngine implements Closeable {
	
	private final AbstractSecsSimulator engine;
	private boolean opened;
	private boolean closed;
	
	public AbstractSecsSimulatorEngine(AbstractSecsSimulator engine) {
		this.engine = engine;
		this.opened = false;
		this.closed = false;
	}
	
	public void open() throws IOException {
		
		synchronized ( this ) {
			if ( this.closed ) {
				throw new IOException("Already closed");
			}
			
			if ( this.opened ) {
				throw new IOException("Already opened");
			}
			
			this.opened = true;
		}
	}
	
	public void close() throws IOException {
		synchronized ( this ) {
			if ( this.closed ) {
				return;
			}
			
			this.closed = true;
		}
	}
	
	public boolean isOpen() {
		synchronized ( this ) {
			return this.opened && ! this.closed;
		}
	}
	
	public boolean isClosed() {
		synchronized ( this ) {
			return this.closed;
		}
	}
	
	protected ExecutorService executorService() {
		return engine.executorService();
	}
	
	protected static Runnable createLoopTask(InterruptableRunnable r) {
		return AbstractSecsSimulator.createLoopTask(r);
	}
	
	protected void executeLoopTask(InterruptableRunnable r) {
		engine.executeLoopTask(r);
	}
	
	protected <T> T executeInvokeAny(Collection<? extends Callable<T>> tasks)
			throws InterruptedException, ExecutionException {
		return engine.executeInvokeAny(tasks);
	}
	
	protected <T> T executeInvokeAny(Callable<T> task)
			throws InterruptedException, ExecutionException {
		return engine.executeInvokeAny(task);
	}
	
	protected <T> T executeInvokeAny(Callable<T> task1, Callable<T> task2)
			throws InterruptedException, ExecutionException {
		return engine.executeInvokeAny(task1, task2);
	}
	
	protected <T> T executeInvokeAny(Callable<T> task1, Callable<T> task2, Callable<T> task3)
			throws InterruptedException, ExecutionException {
		return engine.executeInvokeAny(task1, task2, task3);
	}
	
	protected SecsCommunicator openCommunicator() throws IOException {
		return engine.openCommunicator();
	}
	
	protected void closeCommunicator() throws IOException {
		engine.closeCommunicator();
	}
	
	protected SmlMessage parseSml(CharSequence sml) throws SmlParseException {
		return engine.parseSml(sml);
	}

	protected void waitUntilCommunicatable() throws InterruptedException {
		engine.waitUntilCommunicatable();
	}
	
	protected void notifyLog(SecsLog log) {
		engine.notifyLog(log);
	}
	
	protected void notifyLog(Throwable t) {
		engine.notifyLog(new SecsLog(t));
	}
}
