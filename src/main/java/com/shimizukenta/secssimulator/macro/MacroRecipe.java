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
	
	public MacroRecipe(CharSequence alias, List<? extends MacroTask> tasks) {
		this.alias = Objects.requireNonNull(alias).toString();
		this.tasks = Collections.unmodifiableList(tasks);
	}
	
	/**
	 * Crate Macro-Recipe instance from file-path.
	 * 
	 * @param alias
	 * @param path
	 * @return Macro-Recipe instance
	 * @throws MacroRecipeParseException
	 * @throws IOException
	 */
	public static MacroRecipe fromFile(CharSequence alias, Path path) throws MacroRecipeParseException, IOException {
		return new MacroRecipe(alias, MacroTaskBuilder.getInstance().build(path));
	}
	
	private static final String SmlExtension = ".macro";
	
	/**
	 * Crate Macro-Recipe instance from file-path.
	 * 
	 * @param path
	 * @return Macro-Recipe instance
	 * @throws MacroRecipeParseException
	 * @throws IOException
	 */
	public static MacroRecipe fromFile(Path path) throws MacroRecipeParseException, IOException {
		String alias = path.getFileName().toString();
		if ( alias.endsWith(SmlExtension) ) {
			alias = alias.substring(0, alias.length() - SmlExtension.length());
		}
		return fromFile(alias, path);
	}
	
	/**
	 * Returns alias.
	 * 
	 * @return alias
	 */
	public String alias() {
		return this.alias;
	}
	
	/**
	 * Returns list of tasks.
	 * 
	 * @return list of tasks
	 */
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
