package com.shimizukenta.jsoncommunicator;

import java.net.SocketAddress;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractJsonCommunicatorServerBindLog extends AbstractJsonCommunicatorLog
		implements JsonCommunicatorServerBindLog {
	
	private static final long serialVersionUID = 8155368190934740307L;
	
	private final SocketAddress addr;
	private final boolean isBinded;
	private final boolean isClosed;
	
	private String cacheValueString;
	
	public AbstractJsonCommunicatorServerBindLog(CharSequence subject, LocalDateTime timestamp, SocketAddress addr, boolean isBinded, boolean isClosed) {
		super(subject, timestamp);
		this.addr = addr;
		this.isBinded = isBinded;
		this.isClosed = isClosed;
		this.cacheValueString = null;
	}

	public AbstractJsonCommunicatorServerBindLog(CharSequence subject, SocketAddress addr, boolean isBinded, boolean isClosed) {
		super(subject);
		this.addr = addr;
		this.isBinded = isBinded;
		this.isClosed = isClosed;
		this.cacheValueString = null;
	}

	@Override
	public SocketAddress socketAddress() {
		return this.addr;
	}
	
	@Override
	public boolean isBinded() {
		return this.isBinded;
	}
	
	@Override
	public boolean isClosed() {
		return this.isClosed;
	}
	
	@Override
	public Optional<String> optionalValueString() {
		synchronized ( this ) {
			if ( this.cacheValueString == null ) {
				
				List<String> ll = new ArrayList<>();
				
				if ( this.addr != null ) {
					ll.add(this.addr.toString());
				}
				
				if ( this.isClosed ) {
					ll.add("closed");
				} else {
					ll.add("binded:" + isBinded);
				}
				
				this.cacheValueString = ll.stream()
						.collect(Collectors.joining(", "));
			}
			
			return Optional.of(this.cacheValueString);
		}
	}
	
}
