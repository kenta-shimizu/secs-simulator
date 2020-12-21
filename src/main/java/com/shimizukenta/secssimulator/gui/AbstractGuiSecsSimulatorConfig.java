package com.shimizukenta.secssimulator.gui;

import java.io.IOException;

import com.shimizukenta.jsonhub.JsonHub;
import com.shimizukenta.secs.sml.SmlParseException;
import com.shimizukenta.secssimulator.AbstractSecsSimulatorConfig;
import com.shimizukenta.secssimulator.macro.MacroRecipeParseException;

public abstract class AbstractGuiSecsSimulatorConfig extends AbstractSecsSimulatorConfig {
	
	private static final long serialVersionUID = -5329466667504451983L;

	public AbstractGuiSecsSimulatorConfig() {
		super();
	}
	
	@Override
	public void setByJson(JsonHub jh)
			 throws SmlParseException, MacroRecipeParseException, IOException{
		
		super.setByJson(jh);
	}
	
	@Override
	public JsonHub getJsonHub() {
		return super.getJsonHub();
	}
	
}
