package com.shimizukenta.jsoncommunicator;

import java.net.SocketAddress;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractJsonCommunicatorSendJsonLog extends AbstractJsonCommunicatorLog
		implements JsonCommunicatorSendJsonLog {
	
	private static final long serialVersionUID = -8924276421579604853L;
	
	private final String json;
	private final List<SocketAddress> remotes;
	
	private String cacheValueString;
	
	public AbstractJsonCommunicatorSendJsonLog(CharSequence subject, LocalDateTime timestamp, CharSequence json, List<SocketAddress> remotes) {
		super(subject, timestamp);
		this.json = ((json == null) ? "" : json.toString());
		this.remotes = new ArrayList<>(remotes);
		this.cacheValueString = null;
	}

	public AbstractJsonCommunicatorSendJsonLog(CharSequence subject, CharSequence json, List<SocketAddress> remotes) {
		super(subject);
		this.json = ((json == null) ? "" : json.toString());
		this.remotes = new ArrayList<>(remotes);
		this.cacheValueString = null;
	}
	
	@Override
	public List<SocketAddress> remotes() {
		return Collections.unmodifiableList(remotes);
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
				
				String ss = remotes.stream()
						.map(s -> s.toString())
						.collect(Collectors.joining(", ", "[", "]"));
				
				this.cacheValueString = ss + BR + json;
			}
			
			return Optional.of(this.cacheValueString);
		}
	}
}
