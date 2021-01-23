package com.shimizukenta.secssimulator.jsoncommunicator;

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
	
	public static LogReport from(SecsSimulatorLog log) {
		
		Object o = log.value().orElse(null);
		
		if ( o == null ) {
			
			return new LogReport(log.subject(), log.toTimestampString(), null);
			
		} else {
			
			if ( o instanceof Throwable ) {
				
				final StringBuilder sb = new StringBuilder();
				
				Throwable t = (Throwable)o;
				
				sb.append(t.getClass().getSimpleName());
				
				{
					String msg = t.getMessage();
					if ( msg != null && ! msg.isEmpty() ) {
						sb.append(": ").append(msg);
					}
				}
				
				return new LogReport("Error", log.toTimestampString(), sb.toString());
				
			} else if ( o instanceof SecsMessage ) {
				
				SecsMessage sm = (SecsMessage)o;
				return new LogReport(log.subject(), log.toTimestampString(), sm.toJson());
				
			} else {
				
				return new LogReport(log.subject(), log.toTimestampString(), o.toString());
			}
		}
	}
	
}
