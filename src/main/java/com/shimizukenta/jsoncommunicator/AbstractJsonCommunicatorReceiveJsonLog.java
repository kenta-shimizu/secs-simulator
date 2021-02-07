package com.shimizukenta.jsoncommunicator;

import java.net.SocketAddress;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractJsonCommunicatorReceiveJsonLog extends AbstractJsonCommunicatorLog
		implements JsonCommunicatorReceiveJsonLog {
	
	private static final long serialVersionUID = 2446352533461356962L;
	
	private final String json;
	private final SocketAddress local;
	private final SocketAddress remote;
	
	private String cacheValueString;
	
	public AbstractJsonCommunicatorReceiveJsonLog(CharSequence subject, LocalDateTime timestamp, CharSequence json, SocketAddress local, SocketAddress remote) {
		super(subject, timestamp);
		this.json = ((json == null) ? "" : json.toString());
		this.local = local;
		this.remote = remote;
		this.cacheValueString = null;
	}

	public AbstractJsonCommunicatorReceiveJsonLog(CharSequence subject, CharSequence json, SocketAddress local, SocketAddress remote) {
		super(subject);
		this.json = ((json == null) ? "" : json.toString());
		this.local = local;
		this.remote = remote;
		this.cacheValueString = null;
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
	public String json() {
		return this.json;
	}
	
	private static final String BR = System.lineSeparator();
	
	@Override
	public Optional<String> optionalValueString() {
		synchronized ( this ) {
			if ( this.cacheValueString == null ) {
				
				List<String> ll = new ArrayList<>();
				
				if ( this.local != null ) {
					ll.add("local:" + this.local.toString());
				}
				
				if ( this.remote != null ) {
					ll.add("remote:" + this.remote.toString());
				}
				
				this.cacheValueString = ll.stream().collect(Collectors.joining(", ", "{", "}")) + BR + json;
			}
			
			return Optional.of(this.cacheValueString);
		}
	}

}
