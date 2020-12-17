package com.shimizukenta.secssimulator.logging;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.shimizukenta.secs.Property;
import com.shimizukenta.secs.PropertyChangeListener;
import com.shimizukenta.secs.ReadOnlyProperty;

public abstract class AbstractLoggingEngine implements LoggingEngine {
	
	private final ExecutorService execServ = Executors.newCachedThreadPool(r -> {
		Thread th = new Thread(r);
		th.setDaemon(true);
		return th;
	});
	
	private final Property<Path> lastPath = Property.newInstance(null);
	private final Object sync = new Object();
	private final BlockingQueue<Object> logQueue = new LinkedBlockingQueue<>();
	
	private boolean closed;
	private BufferedWriter bw;
	
	public AbstractLoggingEngine() {
		this.closed = false;
		this.bw = null;
		
		execServ.execute(() -> {
			
			final List<Object> oo = new ArrayList<>();
			
			try {
				for ( ;; ) {
					oo.add(logQueue.take());
					
					for ( ;; ) {
						Object o = logQueue.poll();
						if ( o == null ) {
							break;
						} else {
							oo.add(o);
						}
					}
					
					synchronized ( sync ) {
						if ( this.bw != null ) {
							try {
								for ( Object o : oo ) {
									this.bw.write(o.toString());
									this.bw.newLine();
									this.bw.newLine();
								}
								
								bw.flush();
							}
							catch ( IOException e ) {
								
								//TODO
							}
						}
					}
					
					oo.clear();
				}
			}
			catch ( InterruptedException ignore ) {
			}
		});
	}
	
	@Override
	public void close() throws IOException {
		
		synchronized ( sync ) {
			
			if ( this.closed ) {
				return;
			}
			
			this.closed = true;
			
			IOException ioExcept = null;
			
			try {
				execServ.shutdown();
				if ( ! execServ.awaitTermination(1L, TimeUnit.MILLISECONDS) ) {
					execServ.shutdownNow();
					if ( ! execServ.awaitTermination(10L, TimeUnit.SECONDS) ) {
						ioExcept = new IOException("ExecutorService#shutdown failed");
					}
				}
			}
			catch ( InterruptedException ignore ) {
			}
			
			try {
				stop();
			}
			catch ( IOException e ) {
				ioExcept = e;
			}
			catch ( InterruptedException ignore ) {
			}
			
			if ( ioExcept != null ) {
				throw ioExcept;
			}
		}
	}
	
	@Override
	public Optional<Path> start(Path path) throws IOException, InterruptedException {
		
		synchronized ( sync ) {
			
			if ( this.closed ) {
				throw new IOException("Already closed");
			}
			
			stop();
			
			logQueue.clear();
			
			this.bw = Files.newBufferedWriter(
					path,
					StandardCharsets.UTF_8,
					StandardOpenOption.WRITE,
					StandardOpenOption.CREATE,
					StandardOpenOption.APPEND);
			
			this.lastPath.set(path.normalize());
			
			return Optional.of(this.lastPath.get());
		}
	}
	
	@Override
	public Optional<Path> stop() throws IOException, InterruptedException {
		synchronized ( sync ) {
			if ( this.bw != null ) {
				try {
					this.bw.close();
					Path p = this.lastPath.get();
					if ( p != null ) {
						return Optional.of(p);
					}
				}
				finally {
					this.bw = null;
					this.lastPath.set(null);
				}
			}
			return Optional.empty();
		}
	}
	
	@Override
	public void putLog(Object log) {
		if ( this.lastPath.get() != null ) {
			logQueue.offer(log);
		}
	}
	
	@Override
	public boolean addStateChangeListener(PropertyChangeListener<? super Path> l) {
		return this.lastPath.addChangeListener(l);
	}
	
	@Override
	public boolean removeStateChangeListener(PropertyChangeListener<? super Path> l) {
		return this.lastPath.removeChangeListener(l);
	}
	
	@Override
	public ReadOnlyProperty<Path> loggingProperty() {
		return this.lastPath;
	}
	
}
