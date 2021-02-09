package com.shimizukenta.secssimulator;

import java.time.LocalDateTime;
import java.util.Optional;

import com.shimizukenta.secs.AbstractSecsMessage;
import com.shimizukenta.secs.SecsLog;
import com.shimizukenta.secs.SecsSendedMessageLog;
import com.shimizukenta.secs.SecsThrowableLog;

public abstract class AbstractSecsSimulatorSecsCommunicatorLog extends AbstractSecsSimulatorLog {
	
	private static final long serialVersionUID = 4433669945995898055L;
	
	private final SecsLog log;
	
	private String cacheSubject;
	private String cacheToValueString;
	
	public AbstractSecsSimulatorSecsCommunicatorLog(SecsLog log) {
		super();
		this.log = log;
		this.cacheSubject = null;
		this.cacheToValueString = null;
	}
	
	@Override
	public String subject() {
		synchronized ( this ) {
			if ( this.cacheSubject == null ) {
				
				if ( log instanceof SecsThrowableLog ) {
					
					this.cacheSubject = log.subjectHeader() + "SECS-Communicator Error";
					
				} else {
					
					this.cacheSubject = log.subjectHeader() + log.subject();
				}
			}
			
			return this.cacheSubject;
		}
	}
	
	@Override
	public LocalDateTime timestamp() {
		return this.log.timestamp();
	}
	
	@Override
	public Optional<Object> value() {
		return this.log.value();
	}
	
	@Override
	public Optional<String> optionalValueString() {
		synchronized ( this ) {
			if ( this.cacheToValueString == null ) {
				
				if ( log instanceof SecsThrowableLog ) {
					
					this.cacheToValueString = ((SecsThrowableLog) log).getCause().toString();
					
				} else if ( log instanceof SecsSendedMessageLog ) {
					
					this.cacheToValueString = log.value()
							.map(o -> {
								if ( o instanceof AbstractSecsMessage ) {
									return ((AbstractSecsMessage)o).toHeaderBytesString();
								}
								return null;
							})
							.orElse(null);
					
				} else {
					
					this.cacheToValueString = log.optionalValueString().orElse(null);
				}
			}
			
			return this.cacheToValueString == null ? Optional.empty() : Optional.of(this.cacheToValueString);
		}
	}
	
}
