package com.shimizukenta.secssimulator.cli;

import java.util.Arrays;

public enum CliCommandManual {
	
	MANUAL("Show Commands, Show Manual with option"),
	
	OPEN("Open communicator",
			"if already opened, close communicator and reopen."),
	CLOSE("Close communicator"),
	QUIT("Quit application"),
	
	LOAD("Load config",
			"option is path/from/config.json"),
	SAVE("Save config",
			"option is path/to/config.json"),
	STATUS("Show status"),
	
	PWD("Present working directory"),
	LS("List directory files"),
	CD("Change directory",
			"option is path/destination/directory"),
	MKDIR("Make directory",
			"option is path/of/new/directory"),
	
	SEND_SML("Send SML",
			"option is Alias-Name of SML"),
	SEND_DIRECT("Send Direct SML",
			"option is SML-String.",
			"Support 1 line only.",
			"sample: \"sd S1F16 <B 0x0>.\""),
	LINKTEST("Linktest",
			"Linktest if protocol is HSMS-SS"),
	
	LIST_SML("List SMLs",
			"List is added SMLs"),
	SHOW_SML("Show SML",
			"option is Alias-Name of SML"),
	ADD_SML("Add SML",
			"option is path/to/file.sml"),
	REMOVE_SML("Remove SML",
			"option is Alias-Name of SML"),
	
	LOG("Logging start/stop",
			"if has option (path/to/file.log), logging start.",
			"if has no option, logging stop",
			"if already started, stop and restart."),
	
	AUTO_REPLY("Switch Auto Reply",
			"if has option (true/false), set option value",
			"if has no option, toggle value"),
	AUTO_REPLY_S9Fy("Switch Auto Reply S9Fy",
			"if has option (true/false), set option value",
			"if has no option, toggle value"),
	AUTO_REPLY_SxF0("Switch Auto Reply SxF0",
			"if has option (true/false), set option value",
			"if has no option, toggle value"),
	
	MACRO("Macro start/stop",
			"if has option (Alias-name of Macro), macro start.",
			"if has not option, macro stop.",
			"if already started, stop and restart."),
	MACROS("List Macro recipes"),
	SHOW_MACRO("Show Macro recipe tasks",
			"option is Alias-Name of Macro recipe"),
	ADD_MACRO("Add Macro recipe",
			"option is path/of/macro-recipe.json"),
	REMOVE_MACRO("Remove Macro recipe",
			"option is Alias-Name of Macro recipe"),
	
	;
	
	private String description;
	private String[] details;
	
	private CliCommandManual(String desc, String... lines) {
		this.description = desc;
		this.details = lines;
	}
	
	public String description() {
		return description;
	}
	
	public String[] details() {
		return Arrays.copyOf(details, details.length);
	}
	
}
