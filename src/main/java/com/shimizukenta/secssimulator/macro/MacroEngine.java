package com.shimizukenta.secssimulator.macro;

import java.io.Closeable;
import java.util.Optional;

import com.shimizukenta.secs.PropertyChangeListener;

public interface MacroEngine extends Closeable {
	
	public Optional<MacroRecipe> start(MacroRecipe recipe) throws InterruptedException;
	public Optional<MacroRecipe> stop() throws InterruptedException;
	
	public boolean addStateChangeListener(PropertyChangeListener<? super MacroRecipe> l);
	public boolean removeStateChangeListener(PropertyChangeListener<? super MacroRecipe> l);
	
}
