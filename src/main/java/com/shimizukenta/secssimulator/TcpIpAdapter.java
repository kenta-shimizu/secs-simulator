package com.shimizukenta.secssimulator;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class TcpIpAdapter implements Closeable {
	
	private final Inner a;
	private final Inner b;
	
	private boolean opened;
	private boolean closed;
	
	public TcpIpAdapter(SocketAddress a, SocketAddress b) {
		this.a = new Inner(a);
		this.b = new Inner(b);
		this.opened = false;
		this.closed = false;
	}
	
	public SocketAddress socketAddressA() throws IOException {
		return getSocketAddress(a);
	}
	
	public SocketAddress socketAddressB() throws IOException {
		return getSocketAddress(b);
	}
	
	private SocketAddress getSocketAddress(Inner i) throws IOException {
		if ( i.server == null ) {
			return i.addr;
		} else {
			return i.server.getLocalAddress();
		}
	}
	
	public void open() throws IOException {
		
		synchronized (this) {
			if ( this.closed ) {
				throw new IOException("Already closed");
			}
			
			if ( this.opened ) {
				throw new IOException("Already opened");
			}
			
			this.opened = true;
		}
		
		this.a.open(this.b);
		this.b.open(this.a);
	}

	@Override
	public void close() throws IOException {
		
		synchronized ( this ) {
			
			if ( this.closed ) {
				return;
			}
			
			this.closed = true;
		}
		
		IOException ioExcept = null;
		
		try {
			this.a.close();
		}
		catch ( IOException e ) {
			ioExcept = e;
		}
		
		try {
			this.b.close();
		}
		catch ( IOException e ) {
			ioExcept = e;
		}
		
		if ( ioExcept != null ) {
			throw ioExcept;
		}
	}
	
	public boolean addThrowableListener(ThrowableListener l) {
		boolean aa = a.thLstnrs.add(l);
		boolean bb = b.thLstnrs.add(l);
		return aa && bb;
	}
	
	public boolean removeThrowableListener(ThrowableListener l) {
		boolean aa = a.thLstnrs.remove(l);
		boolean bb = b.thLstnrs.remove(l);
		return aa && bb;
	}
	
	public static TcpIpAdapter open(SocketAddress a, SocketAddress b) throws IOException {
		
		final TcpIpAdapter inst = new TcpIpAdapter(a, b);
		
		try {
			inst.open();
		}
		catch ( IOException e ) {
			
			try {
				inst.close();
			}
			catch ( IOException giveup ) {
			}
			
			throw e;
		}
		
		return inst;
	}
	
	public static void main(String[] args) {
		
		try (
				TcpIpAdapter adapter = new TcpIpAdapter(
						parseSocketAddress(args[0]),
						parseSocketAddress(args[1]));
				) {
			
			adapter.addThrowableListener((addr, t) -> {
				System.out.println("Throw from SocketAddress: " + addr.toString());
				t.printStackTrace();
			});
			
			adapter.open();
			
			synchronized ( TcpIpAdapter.class ) {
				TcpIpAdapter.class.wait();
			}
		}
		catch ( InterruptedException ignore ) {
		}
		catch ( Throwable t ) {
			t.printStackTrace();
		}
	}
	
	private static SocketAddress parseSocketAddress(CharSequence cs) {
		String[] ss = Objects.requireNonNull(cs).toString().split(":", 2);
		int port = Integer.parseInt(ss[1].trim());
		return new InetSocketAddress(ss[0].trim(), port);
	}
	
	public static interface ThrowableListener {
		public void throwed(SocketAddress addr, Throwable t);
	}
	
	private class Inner implements Closeable {
		
		private final SocketAddress addr;
		
		private AsynchronousServerSocketChannel server;
		private final Collection<AsynchronousSocketChannel> channels = new CopyOnWriteArrayList<>();
		
		private boolean closed;
		
		public Inner(SocketAddress socketAddress) {
			this.addr = socketAddress;
			this.server = null;
			this.closed = false;
		}
		
		public void open(Inner another) throws IOException {
			
			server = AsynchronousServerSocketChannel.open();
			server.setOption(StandardSocketOptions.SO_REUSEADDR, true);
			server.bind(addr);
			
			server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {

				@Override
				public void completed(AsynchronousSocketChannel channel, Void attachment) {
					
					server.accept(null, this);
					
					try {
						channels.add(channel);
						
						ByteBuffer buffer = ByteBuffer.allocate(1024);
						
						for ( ;; ) {
							
							((Buffer)buffer).clear();
							
							Future<Integer> f = channel.read(buffer);
							
							try {
								int r = f.get().intValue();
								
								if ( r < 0 ) {
									break;
								}
								
								((Buffer)buffer).flip();
								
								byte[] bs = new byte[buffer.remaining()];
								buffer.get(bs);
								
								another.put(bs);
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
						
						synchronized ( this ) {
							
							if ( ! closed ) {
								putThrowable(e.getCause());
							}
						}
					}
					finally {
						
						channels.remove(channel);
						
						closeChannel(channel);
					}
				}

				@Override
				public void failed(Throwable t, Void attachment) {
					
					synchronized ( this ) {
						
						if ( ! closed ) {
							putThrowable(t);
						}
					}
				}
			});
		}
		
		public void close() throws IOException {
			
			synchronized ( this ) {
				
				if ( closed ) {
					return ;
				}
				
				closed = true;
			}
			
			if ( server != null ) {
				server.close();
			}
			
			channels.forEach(this::closeChannel);
			
		}
		
		private void closeChannel(AsynchronousSocketChannel channel) {
			
			try {
				channel.shutdownOutput();
			}
			catch ( IOException giveup ) {
			}
			
			try {
				channel.close();
			}
			catch ( IOException giveup ) {
			}
		}
		
		public void put(byte[] bs) throws InterruptedException {
			
			for ( AsynchronousSocketChannel channel : channels ) {
				
				ByteBuffer buffer = ByteBuffer.allocate(bs.length);
				buffer.put(bs);
				((Buffer)buffer).flip();
				
				try {
					while ( buffer.hasRemaining() ) {
						
						Future<Integer> f = channel.write(buffer);
						
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
				catch ( ExecutionException e ) {
					putThrowable(e.getCause());
				}
			}
		}
		
		private Collection<ThrowableListener> thLstnrs = new CopyOnWriteArrayList<>();
		
		private void putThrowable(Throwable t) {
			thLstnrs.forEach(l -> {
				l.throwed(addr, t);
			});
		}
	}

}
