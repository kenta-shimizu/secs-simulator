package com.shimizukenta.secssimulator;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;

import com.shimizukenta.secssimulator.macro.MacroRecipe;
import com.shimizukenta.secssimulator.macro.MacroRecipeParseException;

public class MacroRecipePair implements Serializable {
	
	private static final long serialVersionUID = 1184096113903881701L;
	
	private final MacroRecipe recipe;
	private Path path;
	
	public MacroRecipePair(MacroRecipe recipe, Path path) {
		this.recipe = recipe;
		this.path = path;
	}
	
	public MacroRecipe recipe() {
		return this.recipe;
	}
	
	public Path path() {
		synchronized ( this ) {
			return this.path;
		}
	}
	
	public void path(Path path) {
		synchronized ( this ) {
			this.path = path;
		}
	}
	
	@Override
	public int hashCode() {
		return this.recipe.alias().hashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other != null && (other instanceof MacroRecipePair) ) {
			return recipe().alias().equals(((MacroRecipePair)other).recipe().alias());
		}
		return false;
	}
	
	public static MacroRecipePair fromFile(CharSequence alias, Path path) throws MacroRecipeParseException, IOException {
		return new MacroRecipePair(MacroRecipe.fromFile(alias, path), path);
	}
	
	public static MacroRecipePair fromFile(Path path) throws MacroRecipeParseException, IOException {
		return new MacroRecipePair(MacroRecipe.fromFile(path), path);
	}
	
}
