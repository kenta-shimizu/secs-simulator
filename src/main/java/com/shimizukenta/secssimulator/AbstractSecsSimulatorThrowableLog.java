package com.shimizukenta.secssimulator;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractSecsSimulatorThrowableLog extends AbstractSecsSimulatorLog {
	
	private static final long serialVersionUID = -5639490002950471333L;
	
	private final Throwable cause;
	private final LocalDateTime timestamp;
	private String cacheToValueString;
	
	public AbstractSecsSimulatorThrowableLog(Throwable cause) {
		super();
		this.cause = Objects.requireNonNull(cause);
		this.timestamp = LocalDateTime.now();
		this.cacheToValueString = null;
	}
	
	private static final String commonSubject = "SECS-Simulator Error";
	@Override
	public String subject() {
		return commonSubject;
	}
	
	@Override
	public LocalDateTime timestamp() {
		return this.timestamp;
	}

	@Override
	public Optional<Object> value() {
		return Optional.of(cause);
	}

	@Override
	public Optional<String> optionalValueString() {
		synchronized ( this ) {
			if ( this.cacheToValueString == null ) {
				
				StringBuilder sb = new StringBuilder()
						.append(this.cause.getClass().getSimpleName());
				String msg = this.cause.getMessage();
				if ( msg != null ) {
					sb.append(": ").append(msg);
				}
				this.cacheToValueString = sb.toString();
			}
			
			return Optional.of(this.cacheToValueString);
		}
	}

}
