package com.shimizukenta.secssimulator.macro;

import java.nio.file.Path;

public class MacroReport {
	
	public static final String COMPLETED = "Macro completed";
	public static final String FAILED = "Macro failed";
	public static final String CANCELLED = "Macro cancelled";
	public static final String LINE_STARTED = "Macro-Line started";
	public static final String LINE_FINISHED = "Macro-Line finished";
	
	private boolean done;
	private Throwable t;
	private final Path path;
	private MacroRequest request;
	private boolean requestFinished;
	
	private MacroReport(Path path) {
		this.path = path;
		this.done = false;
		this.t = null;
		this.request = null;
		this.requestFinished = false;
	}
	
	public boolean isDone() {
		return done;
	}
	
	public boolean isCancelled() {
		return (t != null) && (t instanceof InterruptedException);
	}
	
	public Throwable getCause() {
		return t;
	}
	
	public MacroRequest request() {
		return request;
	}
	
	public boolean requestFinished() {
		return requestFinished;
	}
	
	private static final String BR = System.lineSeparator();
	
	@Override
	public String toString() {
		
		String fn = path.getFileName().toString();
		
		if ( isDone() ) {
			
			if ( getCause() == null ) {
				
				return COMPLETED + ", " + fn;
				
			} else {
				
				if ( isCancelled() ) {
					
					return CANCELLED + ", " + fn;
					
				} else {
					
					return FAILED + ", " + fn + BR + t.toString();
				}
			}
			
		} else {
			
			if ( requestFinished() ) {
				
				return LINE_FINISHED + ", " + fn + BR + request;
				
			} else {
				
				return LINE_STARTED + ", " + fn + BR + request;
			}
		}
	}
	
	public static MacroReport completed(Path path) {
		MacroReport r = new MacroReport(path);
		r.done = true;
		return r;
	}
	
	public static MacroReport interrupted(Path path, InterruptedException e) {
		MacroReport r = new MacroReport(path);
		r.done = true;
		r.t = e;
		return r;
	}
	
	public static MacroReport failed(Path path, Throwable t) {
		MacroReport r = new MacroReport(path);
		r.done = true;
		r.t = t;
		return r;
	}
	
	public static MacroReport requestStarted(Path path, MacroRequest request) {
		MacroReport r = new MacroReport(path);
		r.request = request;
		r.requestFinished = false;
		return r;
	}
	
	public static MacroReport requestFinished(Path path, MacroRequest request) {
		MacroReport r = new MacroReport(path);
		r.request = request;
		r.requestFinished = true;
		return r;
	}
	
}
