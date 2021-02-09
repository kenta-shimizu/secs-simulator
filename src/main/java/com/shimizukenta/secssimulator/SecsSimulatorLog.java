package com.shimizukenta.secssimulator;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * SecsSimulatorLog contains subject, timestamp, detail-information.
 * 
 * <p>
 * To get subject, {@link #subject()}.<br />
 * To get {@link LocalDateTime} timestamp, {@link #timestamp()}.<br />
 * To get Pretty-Print-value-information, {@link #optionalValueString()}.<br />
 * To get detail-information, {@link #value()}.<br />
 * </p>
 * 
 * @author kenta-shimizu
 *
 */
public interface SecsSimulatorLog extends Comparable<SecsSimulatorLog> {
	
	/**
	 * Returns subject.
	 * 
	 * @return subject
	 */
	public String subject();
	
	/**
	 * Returns timestamp.
	 * 
	 * @return timestamp
	 */
	public LocalDateTime timestamp();
	
	/**
	 * Returns Optional-value if has value.
	 * 
	 * @return Optional-value if has value
	 */
	public Optional<Object> value();
	
	/**
	 * Returns Pretty-Print-String if has value.
	 * 
	 * @return Pretty-Print-String if has value
	 */
	public Optional<String> optionalValueString();
	
}
