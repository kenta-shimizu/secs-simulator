package com.shimizukenta.jsoncommunicator;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * This interface is JsonCommunicator Log, includes Subject, Timestamp, Detail-Information.
 * 
 * <p>
 * To get Subject, {@link #subject()}.<br />
 * To get Timestamp, {@link #timestamp()}.<br />
 * To get defail-information, {@link #value()}.<br />
 * To get defail-information-String, {@link #optionalValueString()}.<br />
 * </p>
 * 
 * @author kenta-shimizu
 *
 */
public interface JsonCommunicatorLog {
	
	/**
	 * Returns Log-subject.
	 * 
	 * @return Log-subject
	 */
	public String subject();
	
	/**
	 * Returns Log-timestamp.
	 * 
	 * @return Log-timestamp
	 */
	public LocalDateTime timestamp();
	
	/**
	 * Returns Log-defail-information.
	 * 
	 * @return value if exist, and {@code Optional.empty()} otherwise
	 */
	public Optional<Object> value();
	
	/**
	 * Returns subject-header.
	 * 
	 * @return subject-header
	 */
	public String subjectHeader();
	
	/**
	 * Returns Log-defail-information-String
	 * 
	 * @return value if exist, and {@code Optional.empty()} otherwise
	 */
	public Optional<String> optionalValueString();
	
}
