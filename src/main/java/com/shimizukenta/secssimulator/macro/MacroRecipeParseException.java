package com.shimizukenta.secssimulator.macro;

public class MacroRecipeParseException extends MacroException {
	
	private static final long serialVersionUID = -4098453488444848388L;
	
	public MacroRecipeParseException() {
		super();
	}
	
	public MacroRecipeParseException(String message) {
		super(message);
	}

	public MacroRecipeParseException(Throwable cause) {
		super(cause);
	}

	public MacroRecipeParseException(String message, Throwable cause) {
		super(message, cause);
	}

}
