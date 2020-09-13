package com.shimizukenta.secssimulator.log;

import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;

import com.shimizukenta.secssimulator.AbstractSecsSimulator;
import com.shimizukenta.secssimulator.AbstractSecsSimulatorEngine;
import com.shimizukenta.secssimulator.BooleanProperty;

public class LoggerEngine extends AbstractSecsSimulatorEngine {
	
	
	private final BooleanProperty logging = new BooleanProperty(false);
	
	public LoggerEngine(AbstractSecsSimulator engine) {
		super(engine);
	}
	
	public BooleanProperty logging() {
		return logging;
	}
	
	private final BlockingQueue<Object> queue = new LinkedBlockingQueue<>();
	
	public void putLog(Object o) {
		if ( logging.booleanValue() ) {
			queue.offer(o);
		}
	}
	
	private final Object syncLogging = new Object();
	
	public void start(Path path) {
		synchronized ( this ) {
			stop();
			
			executorService().execute(() -> {
				
				Collection<Callable<Void>> tasks = Arrays.asList(
						() -> {
							try {
								synchronized ( syncLogging ) {
									syncLogging.wait();
								}
							}
							catch ( InterruptedException ignore ) {
							}
							
							return null;
						},
						() -> {
							try {
								try (
										BufferedWriter bw = Files.newBufferedWriter(
												path,
												StandardCharsets.UTF_8,
												StandardOpenOption.WRITE,
												StandardOpenOption.CREATE,
												StandardOpenOption.APPEND);
										) {
									
									Object o = queue.take();
									
									bw.write(o.toString());
									bw.newLine();
									bw.newLine();
									
									bw.flush();
								}
							}
							catch ( InterruptedException ignore ) {
							}
							
							return null;
						});
				
				try {
					queue.clear();
					logging.set(true);
					executorService().invokeAny(tasks);
				}
				catch ( ExecutionException e ) {
					notifyLog(e.getCause());
				}
				catch ( InterruptedException ignore ) {
				}
				finally {
					logging.set(false);
				}
			});
		}
	}
	
	public void stop() {
		synchronized ( syncLogging ) {
			syncLogging.notifyAll();
		}
	}
	
}
