package com.shimizukenta.jsoncommunicator;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.Optional;

public abstract class AbstractJsonCommunicatorThrowableLog extends AbstractJsonCommunicatorLog
		implements JsonCommunicatorThrowableLog {
	
	private static final long serialVersionUID = -8341237192440492621L;
	
	private static final String commonSubject = "Throwable";
	private final Throwable cause;
	
	private String cacheSubject;
	private String cacheToValueString;

	public AbstractJsonCommunicatorThrowableLog(Throwable cause, LocalDateTime timestamp) {
		super(commonSubject, timestamp, cause);
		this.cause = cause;
		this.cacheSubject = null;
		this.cacheToValueString = null;
	}

	public AbstractJsonCommunicatorThrowableLog(Throwable cause) {
		super(commonSubject, cause);
		this.cause = cause;
		this.cacheSubject = null;
		this.cacheToValueString = null;
	}

	@Override
	public Throwable getCause() {
		return this.cause;
	}
	
	@Override
	public String subject() {
		synchronized ( this ) {
			if ( this.cacheSubject == null ) {
				this.cacheSubject = this.cause.toString();
			}
			
			return this.cacheSubject;
		}
	}
	
	@Override
	public Optional<String> optionalValueString() {
		synchronized ( this ) {
			if ( this.cacheToValueString == null ) {
				
				try (
						StringWriter sw = new StringWriter();
						) {
					
					try (
							PrintWriter pw = new PrintWriter(sw);
							) {
						
						this.cause.printStackTrace(pw);
						pw.flush();
						
						this.cacheToValueString = sw.toString();
					}
				}
				catch ( IOException giveup ) {
					this.cacheToValueString = "";
				}
			}
			
			if ( this.cacheToValueString.isEmpty() ) {
				return Optional.empty();
			} else {
				return Optional.of(this.cacheToValueString);
			}
		}
	}
	
}
