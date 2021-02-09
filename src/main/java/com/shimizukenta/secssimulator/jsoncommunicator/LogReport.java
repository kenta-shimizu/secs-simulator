package com.shimizukenta.secssimulator.jsoncommunicator;

import java.time.format.DateTimeFormatter;

import com.shimizukenta.secs.SecsMessage;
import com.shimizukenta.secssimulator.SecsSimulatorLog;

public class LogReport {
	
	public final String subject;
	public final String timestamp;
	public final String value;
	
	private LogReport(String subject, String timestamp, String value) {
		this.subject = subject;
		this.timestamp = timestamp;
		this.value = value;
	}
	
	private static DateTimeFormatter DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
	
	public static LogReport from(SecsSimulatorLog log) {
		
		final String subject = log.subject();
		final String timestamp = log.timestamp().format(DATETIME);
		
		Object v = log.value().orElse(null);
		
		if ( v == null ) {
			
			return new LogReport(subject, timestamp, null);
			
		} else {
			
			if ( v instanceof SecsMessage ) {
				
				return new LogReport(
						subject,
						timestamp,
						((SecsMessage)v).toJson()
						);
				
			} else {
				
				return new LogReport(
						subject,
						timestamp,
						log.optionalValueString().orElse(null)
						);
			}
		}
		
	}
	
}
