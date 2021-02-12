package com.shimizukenta.jsoncommunicator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public abstract class AbstractJsonCommunicator<T> implements JsonCommunicator<T> {
	
	protected static final byte DELIMITER = (byte)0x0;
	
	private final ExecutorService execServ = Executors.newCachedThreadPool(r -> {
		Thread th = new Thread(r);
		th.setDaemon(true);
		return th;
	});
	
	private final JsonCommunicatorConfig config;
	
	private boolean opened;
	private boolean closed;
	
	public AbstractJsonCommunicator(JsonCommunicatorConfig config) {
		this.config = config;
		this.opened = false;
		this.closed = false;
	}
	
	protected ExecutorService executorService() {
		return this.execServ;
	}
	
	@Override
	public boolean isOpen() {
		synchronized ( this ) {
			return this.opened && ! this.closed;
		}
	}
	
	@Override
	public boolean isClosed() {
		synchronized ( this ) {
			return this.closed;
		}
	}
	
	@Override
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
		
		execServ.execute(this.createRecvJsonTask());
		execServ.execute(this.createRecvPojoTask());
		execServ.execute(this.createLogTask());
		
		config.binds().forEach(addr -> {
			execServ.execute(createLoopTask(() -> {
				this.openBind(addr);
				long t = (long)(config.rebindSeconds() * 1000.0F);
				if ( t > 0 ) {
					TimeUnit.MILLISECONDS.sleep(t);
				}
			}));
		});
		
		config.connects().forEach(addr -> {
			execServ.execute(createLoopTask(() -> {
				this.openConnect(addr);
				long t = (long)(config.reconnectSeconds() * 1000.0F);
				if ( t > 0 ) {
					TimeUnit.MILLISECONDS.sleep(t);
				}
			}));
		});
	}
	
	@Override
	public void close() throws IOException {
		
		synchronized ( this ) {
			if ( this.closed ) {
				return;
			}
			
			this.closed = true;
		}
		
		try {
			execServ.shutdownNow();
			if ( ! execServ.awaitTermination(5L, TimeUnit.SECONDS) ) {
				throw new IOException("ExecutorService#shutdown failed");
			}
		}
		catch ( InterruptedException giveup ) {
		}
	}
	
	private Collection<AsynchronousSocketChannel> channels = new CopyOnWriteArrayList<>();
	
	private void openBind(SocketAddress addr) throws InterruptedException {
		
		try (
				AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open();
				) {
			
			notifyLog(SimpleJsonCommunicatorServerBindLog.tryBind(addr));
			
			server.bind(addr);
			
			notifyLog(SimpleJsonCommunicatorServerBindLog.binded(addr));
			
			server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
				
				@Override
				public void completed(AsynchronousSocketChannel channel, Void attachment) {
					
					server.accept(null, this);
					
					SocketAddress local = null;
					SocketAddress remote = null;
					
					try {
						channels.add(channel);
						
						local = channel.getLocalAddress();
						remote = channel.getRemoteAddress();
						
						notifyLog(SimpleJsonCommunicatorConnectionLog.accepted(local, remote));
						
						stateChanged(channel, JsonCommunicatorConnectionState.CONNECTED);
						
						reading(channel);
					}
					catch ( IOException giveup ) {
					}
					catch ( InterruptedException ignore ) {
					}
					finally {
						
						channels.remove(channel);
						
						try {
							channel.shutdownOutput();
						}
						catch ( IOException giveup ) {
						}
						
						try {
							channel.close();
						}
						catch ( IOException e ) {
							notifyLog(e);
						}
						
						stateChanged(channel, JsonCommunicatorConnectionState.NOT_CONNECTED);
						
						notifyLog(SimpleJsonCommunicatorConnectionLog.closed(local, remote));
					}
				}
				
				@Override
				public void failed(Throwable t, Void attachment) {
					
					if ( ! (t instanceof ClosedChannelException) ) {
						notifyLog(t);
					}
					
					synchronized ( server ) {
						server.notifyAll();
					}
				}
			});
			
			synchronized ( server ) {
				server.wait();
			}
		}
		catch ( IOException e ) {
			notifyLog(e);
		}
		finally {
			notifyLog(SimpleJsonCommunicatorServerBindLog.closed(addr));
		}
	}
	
	private void openConnect(SocketAddress addr) throws InterruptedException {
		
		try (
				AsynchronousSocketChannel channel = AsynchronousSocketChannel.open();
				) {
			
			notifyLog(SimpleJsonCommunicatorConnectionLog.tryConnect(addr));
			
			channel.connect(addr, null, new CompletionHandler<Void, Void>() {

				@Override
				public void completed(Void result, Void attachment) {
					
					SocketAddress local = null;
					SocketAddress remote = null;
					
					try {
						channels.add(channel);
						
						stateChanged(channel, JsonCommunicatorConnectionState.CONNECTED);
						
						local = channel.getLocalAddress();
						remote = channel.getRemoteAddress();
						
						notifyLog(SimpleJsonCommunicatorConnectionLog.connected(local, remote));
						
						reading(channel);
					}
					catch ( IOException giveup ) {
					}
					catch ( InterruptedException ignore ) {
					}
					finally {
						
						channels.remove(channel);
						
						try {
							channel.shutdownOutput();
						}
						catch ( IOException giveup ) {
						}
						
						stateChanged(channel, JsonCommunicatorConnectionState.NOT_CONNECTED);
						
						notifyLog(SimpleJsonCommunicatorConnectionLog.closed(local, remote));

						synchronized ( channel ) {
							channel.notifyAll();
						}
					}
				}
				
				@Override
				public void failed(Throwable t, Void attachment) {
					
					if ( ! (t instanceof ClosedChannelException) ) {
						notifyLog(t);
					}
					
					synchronized ( channel ) {
						channel.notifyAll();
					}
				}
			});
			
			synchronized ( channel ) {
				channel.wait();
			}
		}
		catch ( IOException e ) {
			notifyLog(e);
		}
	}
	
	private void reading(AsynchronousSocketChannel channel) throws InterruptedException {
		
		final Collection<Callable<Void>> tasks = Arrays.asList(createReadingTask(channel));
		
		try {
			execServ.invokeAny(tasks);
		}
		catch ( ExecutionException e ) {
			
			Throwable t = e.getCause();
			
			if ( t instanceof RuntimeException ) {
				throw (RuntimeException)t;
			}
			
			notifyLog(t);
		}
	}
	
	private Callable<Void> createReadingTask(AsynchronousSocketChannel channel) {
		
		return new Callable<Void>() {
			
			@Override
			public Void call() throws Exception {
				
				try (
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						) {
					
					final ByteBuffer buffer = ByteBuffer.allocate(1024);
					
					for ( ;; ) {
						
						((Buffer)buffer).clear();
						
						final Future<Integer> f = channel.read(buffer);
						
						try {
							int r = f.get().intValue();
							
							if ( r < 0 ) {
								break;
							}
							
							((Buffer)buffer).flip();
							
							while ( buffer.hasRemaining() ) {
								
								byte b = buffer.get();
								
								if ( b == DELIMITER ) {
									
									byte[] bs = baos.toByteArray();
									putReceivedBytes(channel, bs);
									baos.reset();
									
								} else {
									
									baos.write(b);
								}
							}
						}
						catch ( InterruptedException e ) {
							f.cancel(true);
							throw e;
						}
					}
				}
				catch ( InterruptedException ignore ) {
				}
				catch ( ExecutionException e ) {
					
					Throwable t = e.getCause();
					
					if ( ! (t instanceof ClosedChannelException) ) {
						if ( t instanceof Exception ) {
							throw (Exception)t;
						}
					}
				}
				
				return null;
			}
		};
	}
	
	abstract protected void putReceivedBytes(AsynchronousSocketChannel channel, byte[] bs);
	
	private final Object syncSend = new Object();
	
	@Override
	public void send(CharSequence json) throws InterruptedException, IOException {
		synchronized ( syncSend ) {
			send(channels, json);
		}
	}
	
	@Override
	public void send(Object pojo) throws InterruptedException, IOException, JsonCommunicatorParseException {
		synchronized ( syncSend ) {
			send(channels, pojo);
		}
	}
	
	@Override
	public void send(AsynchronousSocketChannel channel, CharSequence json) throws InterruptedException, IOException {
		synchronized ( syncSend ) {
			send(Arrays.asList(Objects.requireNonNull(channel)), json);
		}
	}
	
	@Override
	public void send(AsynchronousSocketChannel channel, Object pojo) throws InterruptedException, IOException, JsonCommunicatorParseException {
		synchronized ( syncSend ) {
			send(Arrays.asList(Objects.requireNonNull(channel)), pojo);
		}
	}
	
	protected void send(Collection<AsynchronousSocketChannel> channels, CharSequence json) throws InterruptedException, IOException {
		byte[] bs = Objects.requireNonNull(json).toString().getBytes(StandardCharsets.UTF_8);
		send(channels, bs, json);
	}
	
	protected void send(Collection<AsynchronousSocketChannel> channels, Object pojo) throws InterruptedException, IOException, JsonCommunicatorParseException {
		byte[] bs = createBytesFromPojo(pojo);
		send(channels, bs, pojo);
	}
	
	protected void send(Collection<AsynchronousSocketChannel> channels, byte[] bs, Object toLog) throws InterruptedException, IOException {
		
		final Collection<Callable<AsynchronousSocketChannel>> tasks = channels.stream()
				.map(ch -> createSendTask(ch, bs))
				.collect(Collectors.toList());
		
		final List<Future<AsynchronousSocketChannel>> results = execServ.invokeAll(tasks);
		
		IOException ioExcept = null;
		
		List<SocketAddress> toAddrs = new ArrayList<>();
		
		for ( Future<AsynchronousSocketChannel> f : results ) {
			
			try {
				AsynchronousSocketChannel ch = f.get();
				toAddrs.add(ch.getRemoteAddress());
			}
			catch ( IOException e ) {
				notifyLog(e);
			}
			catch ( ExecutionException e ) {
				
				Throwable t = e.getCause();
				
				if ( t instanceof RuntimeException ) {
					throw (RuntimeException)t;
				}
				
				notifyLog(t);
				
				if ( t instanceof IOException ) {
					ioExcept = (IOException)t;
				}
			}
		}
		
		notifySendJsonLog(toLog.toString(), toAddrs);
		
		if ( ioExcept != null ) {
			throw ioExcept;
		}
	}
	
	private Callable<AsynchronousSocketChannel> createSendTask(AsynchronousSocketChannel channel, byte[] bs) {
		
		return new Callable<AsynchronousSocketChannel>() {
			
			@Override
			public AsynchronousSocketChannel call() throws Exception {
				
				final ByteBuffer buffer = ByteBuffer.allocate(bs.length + 1);
				buffer.put(bs);
				buffer.put(DELIMITER);
				((Buffer)buffer).flip();
				
				try {
					while ( buffer.hasRemaining() ) {
						
						final Future<Integer> f = channel.write(buffer);
						
						try {
							int w = f.get().intValue();
							
							if ( w <= 0 ) {
								break;
							}
						}
						catch ( InterruptedException e ) {
							f.cancel(true);
							throw e;
						}
					}
				}
				catch ( InterruptedException ignore ) {
				}
				catch ( ExecutionException e ) {
					
					Throwable t = e.getCause();
					
					if (! (t instanceof ClosedChannelException) ) {
						if ( t instanceof Exception ) {
							throw (Exception)t;
						}
					}
				}
				
				return channel;
			}
		};
	}
	
	private static final String subjectSendJson = "Sended-JSON";
	
	private void notifySendJsonLog(CharSequence json, List<SocketAddress> toAddrs) {
		
		notifyLog(new AbstractJsonCommunicatorSendJsonLog(subjectSendJson, json, toAddrs) {

			private static final long serialVersionUID = -1598027910284008481L;
		});
	}
	
	abstract protected byte[] createBytesFromPojo(Object pojo) throws JsonCommunicatorParseException;
	
	
	private final Collection<JsonCommunicatorJsonReceiveListener> recvJsonLstnrs = new CopyOnWriteArrayList<>();
	
	@Override
	public boolean addJsonReceiveListener(JsonCommunicatorJsonReceiveListener l) {
		return recvJsonLstnrs.add(Objects.requireNonNull(l));
	}
	
	@Override
	public boolean removeJsonReceiveListener(JsonCommunicatorJsonReceiveListener l) {
		return recvJsonLstnrs.remove(Objects.requireNonNull(l));
	}
	
	private final Collection<JsonCommunicatorJsonReceiveBiListener> recvJsonBiLstnrs = new CopyOnWriteArrayList<>();
	
	@Override
	public boolean addJsonReceiveListener(JsonCommunicatorJsonReceiveBiListener l) {
		return recvJsonBiLstnrs.add(Objects.requireNonNull(l));
	}
	
	@Override
	public boolean removeJsonReceiveListener(JsonCommunicatorJsonReceiveBiListener l) {
		return recvJsonBiLstnrs.remove(Objects.requireNonNull(l));
	}
	
	private class RecvJsonPack {
		
		private final AsynchronousSocketChannel channel;
		private final String json;
		
		private RecvJsonPack(AsynchronousSocketChannel channel, String json) {
			this.channel = channel;
			this.json = json;
		}
	}
	
	private final BlockingQueue<RecvJsonPack> recvJsonPackQueue = new LinkedBlockingQueue<>();
	
	private Runnable createRecvJsonTask() {
		return createLoopTask(() -> {
			final RecvJsonPack p = recvJsonPackQueue.take();
			recvJsonLstnrs.forEach(l -> {
				l.received(p.json);
			});
			recvJsonBiLstnrs.forEach(l -> {
				l.received(p.channel, p.json);
			});
		});
	}
	
	protected final boolean offerRecvJsonPackQueue(RecvJsonPack p) {
		return recvJsonPackQueue.offer(p);
	}
	
	protected void receiveJson(AsynchronousSocketChannel channel, String json) {
		RecvJsonPack p = new RecvJsonPack(channel, json);
		offerRecvJsonPackQueue(p);
		notifyReceiveJsonLog(p);
	}
	
	private static final String subjectReceiveJson = "Receive-JSON";
	
	private void notifyReceiveJsonLog(RecvJsonPack p) {
		
		try {
			SocketAddress local = p.channel.getLocalAddress();
			SocketAddress remote = p.channel.getRemoteAddress();
			
			notifyLog(new AbstractJsonCommunicatorReceiveJsonLog(subjectReceiveJson, p.json, local, remote) {
				
				private static final long serialVersionUID = 4856578022712191825L;
			});
		}
		catch ( IOException giveup ) {
		}
	}
	
	private final Collection<JsonCommunicatorPojoReceiveListener<? super T>> recvPojoLstnrs = new CopyOnWriteArrayList<>();
	
	public boolean addPojoReceiveListener(JsonCommunicatorPojoReceiveListener<? super T> l) {
		return recvPojoLstnrs.add(Objects.requireNonNull(l));
	}
	
	public boolean removePojoReceiveListener(JsonCommunicatorPojoReceiveListener<? super T> l) {
		return recvPojoLstnrs.remove(Objects.requireNonNull(l));
	}
	
	private final Collection<JsonCommunicatorPojoReceiveBiListener<? super T>> recvPojoBiLstnrs = new CopyOnWriteArrayList<>();
	
	public boolean addPojoReceiveListener(JsonCommunicatorPojoReceiveBiListener<? super T> l) {
		return recvPojoBiLstnrs.add(Objects.requireNonNull(l));
	}
	
	public boolean removePojoReceiveListener(JsonCommunicatorPojoReceiveBiListener<? super T> l) {
		return recvPojoBiLstnrs.remove(Objects.requireNonNull(l));
	}
	
	private class RecvPojoPack {
		private final AsynchronousSocketChannel channel;
		private final T pojo;
		private RecvPojoPack(AsynchronousSocketChannel channel, T pojo) {
			this.channel = channel;
			this.pojo = pojo;
		}
	}
	
	private final BlockingQueue<RecvPojoPack> recvPojoPackQueue = new LinkedBlockingQueue<>();
	
	private Runnable createRecvPojoTask() {
		return createLoopTask(() -> {
			final RecvPojoPack p = recvPojoPackQueue.take();
			recvPojoLstnrs.forEach(l -> {
				l.received(p.pojo);
			});
			recvPojoBiLstnrs.forEach(l -> {
				l.received(p.channel, p.pojo);
			});
		});
	}
	
	protected final boolean offerRecvPojoPackQueue(RecvPojoPack p) {
		return recvPojoPackQueue.offer(p);
	}
	
	protected void receivePojo(AsynchronousSocketChannel channel, T pojo) {
		RecvPojoPack p = new RecvPojoPack(channel, pojo);
		offerRecvPojoPackQueue(p);
	}
	
	private final Collection<JsonCommunicatorConnectionStateChangeListener> stateChangedLstnrs = new CopyOnWriteArrayList<>();
	
	@Override
	public boolean addConnectionStateChangeListener(JsonCommunicatorConnectionStateChangeListener l) {
		return stateChangedLstnrs.add(Objects.requireNonNull(l));
	}
	
	@Override
	public boolean removeConnectionStateChangeListener(JsonCommunicatorConnectionStateChangeListener l) {
		return stateChangedLstnrs.remove(Objects.requireNonNull(l));
	}
	
	private final Collection<JsonCommunicatorConnectionStateChangeBiListener> stateChangedBiLstnrs = new CopyOnWriteArrayList<>();
	
	@Override
	public boolean addConnectionStateChangeListener(JsonCommunicatorConnectionStateChangeBiListener l) {
		return stateChangedBiLstnrs.add(Objects.requireNonNull(l));
	}
	
	@Override
	public boolean removeConnectionStateChangeListener(JsonCommunicatorConnectionStateChangeBiListener l) {
		return stateChangedBiLstnrs.remove(Objects.requireNonNull(l));
	}
	
	protected void stateChanged(AsynchronousSocketChannel channel, JsonCommunicatorConnectionState state) {
		stateChangedLstnrs.forEach(l -> {
			l.changed(state);
		});
		stateChangedBiLstnrs.forEach(l -> {
			l.changed(channel, state);
		});
	}
	
	private final Collection<JsonCommunicatorLogListener> logLstnrs = new CopyOnWriteArrayList<>();
	
	public boolean addLogListener(JsonCommunicatorLogListener l) {
		return logLstnrs.add(Objects.requireNonNull(l));
	}
	
	public boolean removeLogListener(JsonCommunicatorLogListener l) {
		return logLstnrs.remove(Objects.requireNonNull(l));
	}
	
	private final BlockingQueue<JsonCommunicatorLog> logQueue = new LinkedBlockingQueue<>();
	
	private Runnable createLogTask() {
		
		return new Runnable() {

			@Override
			public void run() {
				
				try {
					for ( ;; ) {
						final JsonCommunicatorLog log = logQueue.take();
						logLstnrs.forEach(l -> {
							l.received(log);
						});
					}
				}
				catch ( InterruptedException ignore ) {
				}
				
				try {
					for ( ;; ) {
						final JsonCommunicatorLog log = logQueue.poll(100L, TimeUnit.MILLISECONDS);
						if ( log == null ) {
							break;
						}
						logLstnrs.forEach(l -> {
							l.received(log);
						});
					}
				}
				catch ( InterruptedException ignore ) {
				}
			}
		};
	}
	
	protected final boolean offerLogQueue(AbstractJsonCommunicatorLog log) {
		return logQueue.offer(log);
	}
	
	protected void notifyLog(AbstractJsonCommunicatorLog log) {
		config.logSubjectHeader().ifPresent(log::subjectHeader);
		offerLogQueue(log);
	}
	
	protected void notifyLog(Throwable cause) {
		notifyLog(new AbstractJsonCommunicatorThrowableLog(cause) {

			private static final long serialVersionUID = -4710635971309923125L;
		});
	}
	
	
	protected static interface InterruptableRunnable {
		public void run() throws InterruptedException;
	}
	
	protected static Runnable createLoopTask(InterruptableRunnable r) {
		
		return new Runnable() {
			
			@Override
			public void run() {
				try {
					for ( ;; ) {
						r.run();
					}
				}
				catch (InterruptedException ignore) {
				}
			}
		};
	}

}
