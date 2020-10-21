package com.shimizukenta.jsoncommunicator;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

/**
 * This class is JsonCommunicator Log, includes Subject, Timestamp, Detail-Information.
 * 
 * <p>
 * To get Subject, {@link #subject()}.<br />
 * To get Timestamp, {@link #timestamp()}.<br />
 * To get defail-information, {@link #timestamp()}.<br />
 * </p>
 * <p>
 * This log instance get from {@link JsonCommunicator#addLogListener(JsonCommunicatorLogListener)}.<br />
 * </p>
 * <p>
 * Instances of this class are immutable.<br />
 * </p>
 * 
 * @author kenta-shimizu
 *
 */
public class JsonCommunicatorLog implements Serializable {
	
	private static final long serialVersionUID = 3168528785591981508L;
	
	private static final String BR = System.lineSeparator();
	private static final String SPACE = " ";
	private static DateTimeFormatter DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
	
	
	private final String subject;
	private final LocalDateTime timestamp;
	private final Object value;
	
	public JsonCommunicatorLog(CharSequence subject, LocalDateTime timestamp, Object value) {
		this.subject = Objects.requireNonNull(subject).toString();
		this.timestamp = Objects.requireNonNull(timestamp);
		this.value = value;
	}
	
	public JsonCommunicatorLog(CharSequence subject, Object value) {
		this(subject, LocalDateTime.now(), value);
	}
	
	public JsonCommunicatorLog(CharSequence subject) {
		this(subject, null);
	}
	
	public JsonCommunicatorLog(Throwable t) {
		this(createThrowableSubject(t), t);
	}
	
	/**
	 * Returns Log-subject.
	 * 
	 * @return Log-subject
	 */
	public String subject() {
		return subject;
	}
	
	/**
	 * Returns Log-timestamp.
	 * 
	 * @return Log-timestamp
	 */
	public LocalDateTime timestamp() {
		return timestamp;
	}
	
	/**
	 * Returns Log-defail-information.
	 * 
	 * @return value if exist, and {@code Optional.empty()} otherwise
	 */
	public Optional<Object> value() {
		return value == null ? Optional.empty() : Optional.of(value);
	}
	
	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder()
				.append(toStringTimestamp())
				.append(SPACE)
				.append(subject());

		String v = toStringValue();
		if ( ! v.isEmpty() ) {
			sb.append(BR).append(v);
		}
		
		return sb.toString();
	}
	
	protected String toStringTimestamp() {
		return timestamp().format(DATETIME);
	}
	
	protected String toStringValue() {
		
		return value().map(o -> {
			
			if ( o instanceof Throwable ) {
				
				try (
						StringWriter sw = new StringWriter();
						) {
					
					try (
							PrintWriter pw = new PrintWriter(sw);
							) {
						
						((Throwable) o).printStackTrace(pw);
						pw.flush();
						
						return sw.toString();
					}
				}
				catch ( IOException e ) {
					return e.toString();
				}
				
			} else {
				
				return o.toString();
			}
			
		})
		.orElse("");
	}
	
	public static String createThrowableSubject(Throwable t) {
		return Objects.requireNonNull(t).getClass().getSimpleName();
	}
	
}
