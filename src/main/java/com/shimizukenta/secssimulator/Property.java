package com.shimizukenta.secssimulator;

public interface Property<T> {
	public void set(T v);
	public T get();
	public boolean addChangedListener(PropertyChangeListener<? super T> l);
	public boolean removeChangedListener(PropertyChangeListener<? super T> l);
	public void waitUntil(T v) throws InterruptedException;
	public void waitUntilNot(T v) throws InterruptedException;
}
