package com.shimizukenta.secssimulator.cli;

import java.io.IOException;

import com.shimizukenta.jsonhub.JsonHub;
import com.shimizukenta.secs.sml.SmlParseException;
import com.shimizukenta.secssimulator.AbstractSecsSimulatorConfig;
import com.shimizukenta.secssimulator.macro.MacroRecipeParseException;

public class CliSecsSimulatorConfig extends AbstractSecsSimulatorConfig {
	
	private static final long serialVersionUID = -4642182855193964949L;

	public CliSecsSimulatorConfig() {
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
