package com.shimizukenta.secssimulator;

public class BooleanProperty extends AbstractProperty<Boolean> {
	
	private static final long serialVersionUID = 7773335206303418212L;
	
	public BooleanProperty(boolean initial) {
		super(Boolean.valueOf(initial));
	}
	
	public boolean booleanValue() {
		return get().booleanValue();
	}
	
	public void waitUntilTrue() throws InterruptedException {
		waitUntil(Boolean.TRUE);
	}
	
	public void waitUntilFalse() throws InterruptedException {
		waitUntil(Boolean.FALSE);
	}
}
