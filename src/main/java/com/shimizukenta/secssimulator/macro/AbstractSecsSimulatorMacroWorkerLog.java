package com.shimizukenta.secssimulator.macro;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import com.shimizukenta.secssimulator.AbstractSecsSimulatorLog;

public abstract class AbstractSecsSimulatorMacroWorkerLog extends AbstractSecsSimulatorLog {
	
	private static final long serialVersionUID = -2593746552094606327L;
	
	private final MacroWorker worker;
	private final LocalDateTime timestamp;
	
	private String cacheSubject;
	private String cacheToValueString;
	
	public AbstractSecsSimulatorMacroWorkerLog(MacroWorker worker) {
		super();
		this.worker = Objects.requireNonNull(worker);
		this.timestamp = LocalDateTime.now();
		this.cacheSubject = null;
		this.cacheToValueString = null;
	}
	
	private static final String commonStateChangedSubject = "Macro worker state changed";
	
	@Override
	public String subject() {
		
		synchronized ( this ) {
			
			if ( this.cacheSubject == null ) {
				
				if ( this.worker.failed() ) {
					
					this.cacheSubject = "Macro worker " + this.worker.toString();
					
				} else {
					
					this.cacheSubject = commonStateChangedSubject;
				}
			}
			
			return this.cacheSubject;
		}
	}
	
	@Override
	public LocalDateTime timestamp() {
		return this.timestamp;
	}
	
	@Override
	public Optional<Object> value() {
		return Optional.of(this.worker);
	}
	
	@Override
	public Optional<String> optionalValueString() {
		
		synchronized ( this ) {
			
			if ( this.cacheToValueString == null ) {
				
				if ( this.worker.failed() ) {
					
					this.cacheToValueString = this.worker.failedException()
							.map(e -> e.toString())
							.orElse(null);
					
				} else {
					
					this.cacheToValueString = this.worker.toString();
				}
			}
			
			return this.cacheToValueString == null ? Optional.empty() : Optional.of(this.cacheToValueString);
		}
	}
	
}
