package com.shimizukenta.secssimulator.macro;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MacroRecipe implements Serializable {
	
	private static final long serialVersionUID = -8587183078820360557L;
	
	private final String alias;
	private final List<MacroTask> tasks;
	
	public MacroRecipe(String alias, List<MacroTask> tasks) {
		this.alias = Objects.requireNonNull(alias);
		this.tasks = Collections.unmodifiableList(tasks);
	}
	
	public static MacroRecipe fromFile(CharSequence alias, Path path) throws MacroRecipeParseException, IOException {
		
		//TODO
		
		return null;
	}
	
	public static MacroRecipe fromFile(Path path) throws MacroRecipeParseException, IOException {
		
		//TODO
		
		return null;
	}
	
	public String alias() {
		return this.alias;
	}
	
	public List<MacroTask> tasks() {
		return tasks;
	}
	
	@Override
	public int hashCode() {
		return alias.hashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other != null && (other instanceof MacroRecipe)) {
			return ((MacroRecipe)other).alias.equals(alias);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "Alias: \"" + alias + "\", TaskSCount: " + tasks.size();
	}
}
