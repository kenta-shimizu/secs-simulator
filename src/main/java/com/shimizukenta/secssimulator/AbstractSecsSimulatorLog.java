package com.shimizukenta.secssimulator;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;

public abstract class AbstractSecsSimulatorLog implements SecsSimulatorLog, Serializable {
	
	private static final long serialVersionUID = -4591234469751869011L;
	
	private String cacheToString;
	
	public AbstractSecsSimulatorLog() {
		this.cacheToString = null;
	}
	
	
	
	private static final String BR = System.lineSeparator();
	private static final String SPACE = "  ";
	private static DateTimeFormatter DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
	
	@Override
	public String toString() {
		
		synchronized ( this ) {
			
			if ( this.cacheToString == null ) {
				
				final StringBuilder sb = new StringBuilder(this.toTimestampString())
						.append(SPACE)
						.append(this.subject());
				
				this.optionalValueString().ifPresent(v -> {
					sb.append(BR)
					.append(v);
				});
				
				this.cacheToString = sb.toString();
			}
			
			return this.cacheToString;
		}
	}
	
	public  String toTimestampString() {
		return this.timestamp().format(DATETIME);
	}
	
	@Override
	public int compareTo(SecsSimulatorLog other) {
		return this.timestamp().compareTo(other.timestamp());
	}
	
}
