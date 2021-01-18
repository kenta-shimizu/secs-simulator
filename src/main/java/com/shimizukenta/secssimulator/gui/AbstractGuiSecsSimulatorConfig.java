package com.shimizukenta.secssimulator.gui;

import java.io.IOException;
import java.nio.file.Path;

import com.shimizukenta.jsonhub.JsonHub;
import com.shimizukenta.jsonhub.JsonHubBuilder;
import com.shimizukenta.secs.BooleanProperty;
import com.shimizukenta.secs.sml.SmlParseException;
import com.shimizukenta.secssimulator.AbstractSecsSimulatorConfig;
import com.shimizukenta.secssimulator.macro.MacroRecipeParseException;

public abstract class AbstractGuiSecsSimulatorConfig extends AbstractSecsSimulatorConfig {
	
	private static final long serialVersionUID = -5329466667504451983L;
	
	private static final boolean defaultFullScreen = false;
	private static final int defaultScreenWidth = 960;
	private static final int defaultScreenHeight = 540;
	private static final boolean defaultDarkMode = false;
	
	private boolean fullScreen;
	private int screenWidth;
	private int screenHeight;
	private BooleanProperty darkMode = BooleanProperty.newInstance(defaultDarkMode);
	
	public AbstractGuiSecsSimulatorConfig() {
		super();
		
		this.fullScreen = defaultFullScreen;
		this.screenWidth = defaultScreenWidth;
		this.screenHeight = defaultScreenHeight;
	}
	
	public void initialize() {
		super.initialize();
		
		this.fullScreen(defaultFullScreen);
		this.screenWidth(defaultScreenWidth);
		this.screenHeight(defaultScreenHeight);
		this.darkMode(defaultDarkMode);
	}
	
	public boolean fullScreen() {
		synchronized ( this ) {
			return this.fullScreen;
		}
	}
	
	public void fullScreen(boolean f) {
		synchronized ( this ) {
			this.fullScreen = f;
		}
	}
	
	public int screenWidth() {
		synchronized ( this ) {
			return this.screenWidth;
		}
	}
	
	public void screenWidth(int width) {
		synchronized ( this ) {
			this.screenWidth = width;
		}
	}
	
	public int screenHeight() {
		synchronized ( this ) {
			return this.screenHeight;
		}
	}
	
	public void screenHeight(int height) {
		synchronized ( this ) {
			this.screenHeight = height;
		}
	}
	
	public BooleanProperty darkMode() {
		synchronized ( this ) {
			return this.darkMode;
		}
	}
	
	public void darkMode(boolean f) {
		synchronized ( this ) {
			this.darkMode.set(f);
		}
	}
	
	@Override
	public JsonHub getJsonHub() {
		final JsonHubBuilder jhb = JsonHub.getBuilder();
		
		return jhb.object(
				jhb.pair("communicator", getCommunicatorJsonHub()),
				jhb.pair("autoReply", this.autoReply().booleanValue()),
				jhb.pair("autoReplyS9Fy", this.autoReplyS9Fy().booleanValue()),
				jhb.pair("autoReplySxF0", this.autoReplySxF0().booleanValue()),
				jhb.pair("smlFiles", this.smlAliasPairPool().getJsonHub()),
				jhb.pair("macroRecipeFiles", this.macroRecipePairPool().getJsonHub()),
				jhb.pair("autoOpen", this.autoOpen().booleanValue()),
				jhb.pair("autoLogging", this.autoLogging().map(Path::normalize).map(Path::toString).orElse(null)),
				jhb.pair("gui", this.getGuiJsonHub())
				);
	}
	
	protected JsonHub getGuiJsonHub() {
		final JsonHubBuilder jhb = JsonHub.getBuilder();
		
		return jhb.object(
				jhb.pair("screen", this.getGuiScreenJsonHub()),
				jhb.pair("dark", this.darkMode().booleanValue())
				);
	}
	
	protected JsonHub getGuiScreenJsonHub() {
		final JsonHubBuilder jhb = JsonHub.getBuilder();
		
		return jhb.object(
				jhb.pair("full", this.fullScreen()),
				jhb.pair("width", this.screenWidth()),
				jhb.pair("height", this.screenHeight())
				);
	}
	
	@Override
	public void setByJson(JsonHub jh)
			 throws SmlParseException, MacroRecipeParseException, IOException{
		
		super.setByJson(jh);
		
		this.setGuiByJson(jh.getOrDefault("gui"));
	}
	
	protected void setGuiByJson(JsonHub jh) {
		setGuiScreenByJson(jh.getOrDefault("screen"));
		jh.getOrDefault("dark").optionalBoolean().ifPresent(this::darkMode);
	}
	
	protected void setGuiScreenByJson(JsonHub jh) {
		jh.getOrDefault("full").optionalBoolean().ifPresent(this::fullScreen);
		jh.getOrDefault("width").optionalInt().ifPresent(this::screenWidth);
		jh.getOrDefault("height").optionalInt().ifPresent(this::screenHeight);
	}
	
}
