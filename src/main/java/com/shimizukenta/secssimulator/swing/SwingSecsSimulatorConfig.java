package com.shimizukenta.secssimulator.swing;

import java.awt.Color;
import java.io.IOException;

import com.shimizukenta.jsonhub.JsonHub;
import com.shimizukenta.jsonhub.JsonHubBuilder;
import com.shimizukenta.secs.sml.SmlParseException;
import com.shimizukenta.secssimulator.gui.AbstractGuiSecsSimulatorConfig;
import com.shimizukenta.secssimulator.macro.MacroRecipeParseException;

public class SwingSecsSimulatorConfig extends AbstractGuiSecsSimulatorConfig {
	
	private static final long serialVersionUID = -528323152735952588L;
	
	private static final int defaultViewerSize = 500;
	
	private int viewerSize;
	
	public SwingSecsSimulatorConfig() {
		super();
		
		this.viewerSize = defaultViewerSize;
	}
	
	@Override
	public void initialize() {
		super.initialize();
		
		this.viewerSize = defaultViewerSize;
	}
	
	public int viewerSize() {
		synchronized ( this ) {
			return this.viewerSize;
		}
	}
	
	public void viewerSize(int size) {
		synchronized ( this ) {
			this.viewerSize = size;
		}
	}
	
	private final Color communicatingColor = Color.GREEN;
	
	public Color communicatingColor() {
		return communicatingColor;
	}
	
	@Override
	public JsonHub getJsonHub() {
		return super.getJsonHub();
	}
	
	@Override
	protected JsonHub getGuiJsonHub() {
		final JsonHubBuilder jhb = JsonHub.getBuilder();
		
		return jhb.object(
				jhb.pair("screen", this.getGuiScreenJsonHub()),
				jhb.pair("dark", this.darkMode()),
				jhb.pair("swing", this.getGuiSwingJsonHub())
				);
	}
	
	protected JsonHub getGuiSwingJsonHub() {
		final JsonHubBuilder jhb = JsonHub.getBuilder();
		
		return jhb.object(
				jhb.pair("viewer", this.getGuiSwingViewerJsonHub())
				);
	}
	
	protected JsonHub getGuiSwingViewerJsonHub() {
		final JsonHubBuilder jhb = JsonHub.getBuilder();
		
		return jhb.object(
				jhb.pair("size", this.viewerSize())
				);
	}
	
	@Override
	public void setByJson(JsonHub jh)
			throws SmlParseException, MacroRecipeParseException, IOException{
		
		super.setByJson(jh);
	}
	
	protected void setGuiByJson(JsonHub jh) {
		setGuiScreenByJson(jh.getOrDefault("screen"));
		jh.getOrDefault("dark").optionalBoolean().ifPresent(this::darkMode);
		setGuiSwingByJson(jh.getOrDefault("swing"));
	}
	
	protected void setGuiSwingByJson(JsonHub jh) {
		setGuiSwingViewerByJson(jh.getOrDefault("viewer"));
	}
	
	protected void setGuiSwingViewerByJson(JsonHub jh) {
		jh.getOrDefault("size").optionalInt().ifPresent(this::viewerSize);
	}
	
}
