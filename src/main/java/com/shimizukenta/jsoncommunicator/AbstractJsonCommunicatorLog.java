package com.shimizukenta.jsoncommunicator;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractJsonCommunicatorLog implements JsonCommunicatorLog, Serializable {
	
	private static final long serialVersionUID = 3168528785591981508L;
	
	private final String subject;
	private final LocalDateTime timestamp;
	private final Object value;
	
	protected String subjectHeader;
	
	private String cacheToString;
	
	public AbstractJsonCommunicatorLog(CharSequence subject, LocalDateTime timestamp, Object value) {
		this.subject = Objects.requireNonNull(subject).toString();
		this.timestamp = Objects.requireNonNull(timestamp);
		this.value = value;
		
		this.subjectHeader = "";
		
		this.cacheToString = null;
	}
	
	public AbstractJsonCommunicatorLog(CharSequence subject, Object value) {
		this(subject, LocalDateTime.now(), value);
	}
	
	public AbstractJsonCommunicatorLog(CharSequence subject, LocalDateTime timestamp) {
		this(subject, timestamp, null);
	}
	
	public AbstractJsonCommunicatorLog(CharSequence subject) {
		this(subject, LocalDateTime.now(), null);
	}
	
	@Override
	public String subject() {
		return subject;
	}
	
	@Override
	public LocalDateTime timestamp() {
		return timestamp;
	}
	
	@Override
	public Optional<Object> value() {
		return value == null ? Optional.empty() : Optional.of(value);
	}
	
	@Override
	public Optional<String> optionalValueString() {
		return value().map(Object::toString);
	}
	
	protected void subjectHeader(CharSequence header) {
		synchronized ( this ) {
			this.subjectHeader = Objects.requireNonNull(header).toString();
			this.cacheToString = null;
		}
	}
	
	@Override
	public String subjectHeader() {
		synchronized ( this ) {
			return this.subjectHeader;
		}
	}
	
	
	private static final String BR = System.lineSeparator();
	private static final String SPACE = "  ";
	private static DateTimeFormatter DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
	
	@Override
	public String toString() {
		
		synchronized ( this ) {
			
			if ( this.cacheToString == null ) {
				
				StringBuilder sb = new StringBuilder()
						.append(this.toTimestampString())
						.append(SPACE)
						.append(this.subjectHeader())
						.append(this.subject());
				
				optionalValueString().ifPresent(v -> {
					sb.append(BR).append(v);
				});
				
				this.cacheToString = sb.toString();
			}
			
			return this.cacheToString;
		}
	}
	
	protected String toTimestampString() {
		return timestamp().format(DATETIME);
	}
	
	
}
