package com.shimizukenta.secssimulator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

import com.shimizukenta.jsoncommunicator.JsonCommunicatorLog;
import com.shimizukenta.secs.SecsLog;

/**
 * SecsLog contains subject, timestamp, detail-information.
 * 
 * <p>
 * To get subject, {@link #subject()}<br />
 * To get {@link LocalDateTime} timestamp, {@link #timestamp()}<br />
 * To get detail-information, {@link #value()}<br />
 * </p>
 * <p>
 * {@link #toString()} is overrided to pretty-printing.<br />
 * </p>
 * <p>
 * Instances of this class are immutable.<br />
 * </p>
 * 
 * @author kenta-shimizu
 *
 */
public class SecsSimulatorLog {
	
	private final String subject;
	private final LocalDateTime timestamp;
	private final Object value;
	
	public SecsSimulatorLog(CharSequence subject, LocalDateTime timestamp, Object value) {
		this.subject = Objects.requireNonNull(subject).toString();
		this.timestamp = Objects.requireNonNull(timestamp);
		this.value = value;
	}
	
	public SecsSimulatorLog(CharSequence subject) {
		this(subject, LocalDateTime.now(), null);
	}
	
	public SecsSimulatorLog(CharSequence subject, Object value) {
		this(subject, LocalDateTime.now(), value);
	}
	
	public SecsSimulatorLog(Throwable t) {
		this("Throwable", LocalDateTime.now(), t);
	}
	
	public static SecsSimulatorLog from(SecsLog log) {
		return new SecsSimulatorLog(log.subject(), log.timestamp(), log.value().orElse(null));
	}
	
	public static SecsSimulatorLog from(JsonCommunicatorLog log) {
		return new SecsSimulatorLog(log.subject(), log.timestamp(), log.value().orElse(null));
	}
	
	/**
	 * Returns subject.
	 * 
	 * @return subject
	 */
	public String subject() {
		return subject;
	}
	
	/**
	 * Returns timestamp.
	 * 
	 * @return timestamp
	 */
	public LocalDateTime timestamp() {
		return timestamp;
	}
	
	/**
	 * Returns Optional-value if has value.
	 * 
	 * @return Optional-value if has value
	 */
	public Optional<Object> value() {
		return value == null ? Optional.empty() : Optional.of(value);
	}
	
	private static final String BR = System.lineSeparator();
	private static final String SPACE = "\t";
	private static DateTimeFormatter DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
	
	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder(toStringTimestamp())
				.append(SPACE);
		
		if ( value == null ) {
			
			sb.append(subject);
			
		} else {
			
			if ( value instanceof Throwable ) {
				
				Throwable t = (Throwable)value;
				
				sb.append("Error")
				.append(BR)
				.append(t.getClass().getSimpleName());
				
				String msg = t.getMessage();
				if ( msg != null && ! msg.isEmpty() ) {
					sb.append(": ").append(msg);
				}
				
			} else {
				
				sb.append(subject)
				.append(BR)
				.append(value);
			}
		}
		
		return sb.toString();
	}
	
	private String toStringTimestamp() {
		return timestamp.format(DATETIME);
	}
	
}
