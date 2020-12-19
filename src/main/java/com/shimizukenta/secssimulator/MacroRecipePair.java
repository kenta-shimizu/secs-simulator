package com.shimizukenta.secssimulator;

import java.io.Serializable;
import java.nio.file.Path;

import com.shimizukenta.secssimulator.macro.MacroRecipe;

public class MacroRecipePair implements Serializable {
	
	private static final long serialVersionUID = 1184096113903881701L;
	
	private final MacroRecipe recipe;
	private final Path path;
	
	public MacroRecipePair(MacroRecipe recipe, Path path) {
		this.recipe = recipe;
		this.path = path;
	}
	
	public MacroRecipe recipe() {
		return this.recipe;
	}
	
	public Path path() {
		return this.path;
	}
	
}
