package com.shimizukenta.secssimulator.macro;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class AbstractMacroRecipe implements Serializable, MacroRecipe {
	
	private static final long serialVersionUID = -8587183078820360557L;
	
	private final String alias;
	private final List<MacroTask> tasks;
	
	public AbstractMacroRecipe(CharSequence alias, List<? extends MacroTask> tasks) {
		this.alias = Objects.requireNonNull(alias).toString();
		this.tasks = Collections.unmodifiableList(tasks);
	}
	
	@Override
	public String alias() {
		return this.alias;
	}
	
	@Override
	public List<MacroTask> tasks() {
		return tasks;
	}
	
	@Override
	public int hashCode() {
		return alias().hashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if ((other != null) && (other instanceof MacroRecipe)) {
			return ((MacroRecipe)other).alias().equals(alias());
		}
		return false;
	}
	
	private static final String BR = System.lineSeparator();
	
	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder("Alias: \"")
				.append(alias)
				.append("\", TaskCount: ")
				.append(tasks.size());
		
		int n = 0;
		for ( MacroTask task : tasks ) {
			++ n;
			
			sb.append(BR)
			.append(n)
			.append(": ")
			.append(task);
		}
		
		return sb.toString();
	}
}
