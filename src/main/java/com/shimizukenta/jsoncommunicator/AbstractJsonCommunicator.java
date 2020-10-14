package com.shimizukenta.jsoncommunicator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
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
	protected static final String BR = System.lineSeparator();
	
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
			execServ.shutdown();
			if ( ! execServ.awaitTermination(1L, TimeUnit.MILLISECONDS) ) {
				execServ.shutdownNow();
				if ( ! execServ.awaitTermination(5L, TimeUnit.SECONDS) ) {
					throw new IOException("ExecutorService#shutdown failed");
				}
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
			
			server.bind(addr);
			
			notifyLog("server-binded", addr);
			
			server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {

				@Override
				public void completed(AsynchronousSocketChannel channel, Void attachment) {
					
					server.accept(null, this);
					
					final String channelStr = channel.toString();
					
					try {
						channels.add(channel);
						notifyLog("channel-accepted", channelStr);
						stateChanged(channel, JsonCommunicatorConnectionState.CONNECTED);
						
						reading(channel);
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
						
						notifyLog("channel-closed", channelStr);
						stateChanged(channel, JsonCommunicatorConnectionState.NOT_CONNECTED);
					}
				}

				@Override
				public void failed(Throwable t, Void attachment) {
					notifyLog(t);
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
			notifyLog("server-closed", addr);
		}
	}
	
	private void openConnect(SocketAddress addr) throws InterruptedException {
		
		try (
				AsynchronousSocketChannel channel = AsynchronousSocketChannel.open();
				) {
			
			notifyLog("try-connect", addr);
			
			channel.connect(addr, null, new CompletionHandler<Void, Void>() {

				@Override
				public void completed(Void result, Void attachment) {
					
					final String channelStr = channel.toString();
					
					try {
						channels.add(channel);
						notifyLog("channel-connected", channelStr);
						stateChanged(channel, JsonCommunicatorConnectionState.CONNECTED);
						
						reading(channel);
					}
					catch ( InterruptedException ignore ) {
					}
					finally {
						
						channels.remove(channel);
						notifyLog("channel-disconnected", channelStr);
						
						try {
							channel.shutdownOutput();
						}
						catch ( IOException giveup ) {
						}
						
						stateChanged(channel, JsonCommunicatorConnectionState.NOT_CONNECTED);
						
						synchronized ( channel ) {
							channel.notifyAll();
						}
					}
				}

				@Override
				public void failed(Throwable t, Void attachment) {
					notifyLog(t);
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
		
		final Collection<Callable<Object>> tasks = Arrays.asList(createReadingTask(channel));
		
		try {
			execServ.invokeAny(tasks);
		}
		catch ( ExecutionException e ) {
			notifyLog(e.getCause());
		}
	}
	
	private Callable<Object> createReadingTask(AsynchronousSocketChannel channel) {
		
		return new Callable<Object>() {
			
			@Override
			public Object call() throws Exception {
				
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
								return null;
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
						catch ( ExecutionException e ) {
							notifyLog(e.getCause());
							return null;
						}
						catch ( InterruptedException e ) {
							f.cancel(true);
							throw e;
						}
					}
				}
				catch ( IOException e ) {
					notifyLog(e);
				}
				catch ( InterruptedException ignore ) {
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
		
		List<String> toAddrs = new ArrayList<>();
		
		for ( Future<AsynchronousSocketChannel> f : results ) {
			
			try {
				AsynchronousSocketChannel ch = f.get();
				String toAddr = ch.getRemoteAddress().toString();
				toAddrs.add(toAddr);
			}
			catch ( IOException e ) {
				notifyLog(e);
			}
			catch ( ExecutionException e ) {
				Throwable t = e.getCause();
				notifyLog(t);
				if ( t instanceof IOException ) {
					ioExcept = (IOException)t;
				}
			}
		}
		
		String logValue = "to [" + toAddrs.stream().collect(Collectors.joining(", ")) + "]" + BR + String.valueOf(toLog);
		notifyLog("sended", logValue);
		
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
								return channel;
							}
						}
						catch ( InterruptedException e ) {
							f.cancel(true);
							throw e;
						}
						catch ( ExecutionException e ) {
							Throwable t = e.getCause();
							if (t instanceof Exception) {
								throw (Exception)t;
							} else {
								notifyLog(t);
								return channel;
							}
						}
					}
				}
				catch ( InterruptedException ignore ) {
				}
				
				return channel;
			}
		};
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
		
		@Override
		public String toString() {
			try {
				return "from " + channel.getRemoteAddress().toString() + BR + json;
			}
			catch ( IOException giveup) {
				return json;
			}
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
		notifyLog("receive", p);
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
		return createLoopTask(() -> {
			JsonCommunicatorLog log = logQueue.take();
			logLstnrs.forEach(l -> {
				l.received(log);
			});
		});
	}
	
	protected final boolean offerLogQueue(JsonCommunicatorLog log) {
		return logQueue.offer(log);
	}
	
	protected void notifyLog(JsonCommunicatorLog log) {
		offerLogQueue(new JsonCommunicatorLog(
				createLogSubject(log.subject()),
				log.timestamp(),
				log.value().orElse(null)));
	}
	
	protected void notifyLog(CharSequence subject) {
		offerLogQueue(new JsonCommunicatorLog(createLogSubject(subject)));
	}
	
	protected void notifyLog(CharSequence subject, Object value) {
		offerLogQueue(new JsonCommunicatorLog(
				createLogSubject(subject),
				value));
	}
	
	protected void notifyLog(Throwable t) {
		offerLogQueue(new JsonCommunicatorLog(
				createLogSubject(JsonCommunicatorLog.createThrowableSubject(t)),
				t));
	}
	
	private String createLogSubject(CharSequence subject) {
		if ( subject == null ) {
			return "No-subject";
		} else {
			String s = subject.toString();
			return config.logSubjectHeader()
					.map(h -> h + s)
					.orElse(s);
		}
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
