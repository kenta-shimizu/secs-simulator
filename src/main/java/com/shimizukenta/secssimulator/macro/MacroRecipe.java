package com.shimizukenta.secssimulator.macro;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * This interface is implements of Macro-recipe, alias-name, Macro-tasks, instance builder.
 * 
 * @author kenta-shimizu
 *
 */
public interface MacroRecipe {

	/**
	 * Returns alias.
	 * 
	 * @return alias
	 */
	public String alias();

	/**
	 * Returns list of tasks.
	 * 
	 * @return list of tasks
	 */
	public List<MacroTask> tasks();
	
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
		return new AbstractMacroRecipe(alias, MacroTaskBuilder.getInstance().build(path)) {
			private static final long serialVersionUID = 7951783215566673295L;
		};
	}
	
	/**
	 * Crate Macro-Recipe instance from file-path.
	 * 
	 * @param path
	 * @return Macro-Recipe instance
	 * @throws MacroRecipeParseException
	 * @throws IOException
	 */
	public static MacroRecipe fromFile(Path path) throws MacroRecipeParseException, IOException {
		final String SmlExtension = ".json";
		String alias = path.getFileName().toString();
		if ( alias.endsWith(SmlExtension) ) {
			alias = alias.substring(0, alias.length() - SmlExtension.length());
		}
		return fromFile(alias, path);
	}

}