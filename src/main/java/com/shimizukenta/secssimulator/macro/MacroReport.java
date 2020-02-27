package com.shimizukenta.secssimulator.macro;

import java.nio.file.Path;

public class MacroReport {
	
	public static final String COMPLETED = "macro completed";
	public static final String FAILED = "macro failed";
	public static final String INTERRUPT = "macro interrupted";
	public static final String LINE_FINISHED = "macro-line finihsed";
	
	private final boolean completed;
	private final Throwable t;
	private final Path path;
	private final MacroRequest request;
	
	private MacroReport(boolean completed, Path path) {
		this.completed = completed;
		this.path = path;
		this.t = null;
		this.request = null;
	}
	
	private MacroReport(Path path, Throwable t) {
		this.completed = true;
		this.path = path;
		this.t = t;
		this.request = null;
	}
	
	private MacroReport(Path path, MacroRequest request) {
		this.completed = false;
		this.path = path;
		this.t = null;
		this.request = request;
	}
	
	private static final String BR = System.lineSeparator();
	
	public boolean success() {
		return completed;
	}
	
	public Throwable cause() {
		return t;
	}
	
	public MacroRequest request() {
		return request;
	}
	
	@Override
	public String toString() {
		
		if ( completed ) {
			
			if ( t == null ) {
				
				return COMPLETED + ", " + path.getFileName().toString();
				
			} else {
				
				if ( t instanceof InterruptedException ) {
					
					return INTERRUPT + ", " + path.getFileName().toString();
					
				} else {
					
					return FAILED + ", " + path.getFileName().toString() + BR + t.toString();
				}
			}
			
		} else {
			
			return LINE_FINISHED + ", " + request;
		}
	}
	
	public static MacroReport completed(Path path) {
		return new MacroReport(true, path);
	}
	
	public static MacroReport requestFinished(Path path, MacroRequest request) {
		return new MacroReport(path, request);
	}
	
	public static MacroReport interrupted(InterruptedException e, Path path) {
		return new MacroReport(path, e);
	}
	
	public static MacroReport failed(Throwable t, Path path) {
		return new MacroReport(path, t);
	}
	
}
