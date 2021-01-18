package com.shimizukenta.secssimulator.swing;

import java.awt.Color;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import com.shimizukenta.jsonhub.JsonHub;
import com.shimizukenta.jsonhub.JsonHubBuilder;
import com.shimizukenta.secs.sml.SmlParseException;
import com.shimizukenta.secssimulator.gui.AbstractGuiSecsSimulatorConfig;
import com.shimizukenta.secssimulator.macro.MacroRecipeParseException;

public class SwingSecsSimulatorConfig extends AbstractGuiSecsSimulatorConfig {
	
	private static final long serialVersionUID = -528323152735952588L;
	
	private static final SocketAddress defaultSocketAddress = new InetSocketAddress("127.0.0.1", 5000);
	private static final int defaultViewerSize = 500;
	private static final boolean defaultOverrideIsEquip = true;
	
	private int viewerSize;
	
	public SwingSecsSimulatorConfig() {
		super();
		
		this.socketAddress(defaultSocketAddress);
		this.viewerSize = defaultViewerSize;
		this.isEquip(defaultOverrideIsEquip);
	}
	
	@Override
	public void initialize() {
		super.initialize();
		
		this.viewerSize = defaultViewerSize;
		this.isEquip(defaultOverrideIsEquip);
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
	
	private static final Color defaultDarkAreaForeGroundColor = Color.WHITE;
	private static final Color defaultDarkAreaBackGroundColor = Color.BLACK;
	private static final Color defaultDarkPanelBackGroundColor = Color.BLACK;
	private static final Color defaultDarkDesktopBackGroundColor = Color.BLACK;
	private static final Color communicatingColor = Color.GREEN;
	
	public Color communicatingColor() {
		return communicatingColor;
	}
	
	public Color defaultDarkAreaForeGroundColor() {
		return defaultDarkAreaForeGroundColor;
	}
	
	public Color defaultDarkAreaBackGroundColor() {
		return defaultDarkAreaBackGroundColor;
	}
	
	public Color defaultDarkPanelBackGroundColor() {
		return defaultDarkPanelBackGroundColor;
	}
	
	public Color defaultDarkDesktopBackGroundColor() {
		return defaultDarkDesktopBackGroundColor;
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
				jhb.pair("dark", this.darkMode().booleanValue()),
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
