package com.shimizukenta.jsoncommunicator;

import java.net.SocketAddress;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractJsonCommunicatorConnectionLog extends AbstractJsonCommunicatorLog
		implements JsonCommunicatorConnectionLog {
	
	private static final long serialVersionUID = 1913547284849074358L;
	
	private final SocketAddress local;
	private final SocketAddress remote;
	private final boolean isConnecting;
	
	private String cacheToValueString;
	
	public AbstractJsonCommunicatorConnectionLog(CharSequence subject, LocalDateTime timestamp, SocketAddress local, SocketAddress remote, boolean isConnecting) {
		super(subject, timestamp);
		this.local = local;
		this.remote = remote;
		this.isConnecting = isConnecting;
		this.cacheToValueString = null;
	}

	public AbstractJsonCommunicatorConnectionLog(CharSequence subject, SocketAddress local, SocketAddress remote, boolean isConnecting) {
		super(subject);
		this.local = local;
		this.remote = remote;
		this.isConnecting = isConnecting;
		this.cacheToValueString = null;
	}
	
	@Override
	public SocketAddress local() {
		return this.local;
	}

	@Override
	public SocketAddress remote() {
		return this.remote;
	}

	@Override
	public boolean isConnecting() {
		return this.isConnecting;
	}
	
	@Override
	public Optional<String> optionalValueString() {
		synchronized ( this ) {
			if ( this.cacheToValueString == null ) {
				final List<String> ll = new ArrayList<>();
				
				if ( this.local != null ) {
					ll.add("local:" + local.toString());
				}
				
				if ( this.remote != null ) {
					ll.add("remote:" + remote.toString());
				}
				
				ll.add("connecting:" + isConnecting);
				
				this.cacheToValueString = ll.stream()
						.collect(Collectors.joining(", "));
			}
			
			return Optional.of(this.cacheToValueString);
		}
	}
	
}
