package com.shimizukenta.secssimulator.swing;

import java.io.IOException;

import com.shimizukenta.jsonhub.JsonHub;
import com.shimizukenta.secs.sml.SmlParseException;
import com.shimizukenta.secssimulator.gui.AbstractGuiSecsSimulatorConfig;
import com.shimizukenta.secssimulator.macro.MacroRecipeParseException;

public class SwingSecsSimulatorConfig extends AbstractGuiSecsSimulatorConfig {
	
	private static final long serialVersionUID = -528323152735952588L;
	
	public SwingSecsSimulatorConfig() {
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
