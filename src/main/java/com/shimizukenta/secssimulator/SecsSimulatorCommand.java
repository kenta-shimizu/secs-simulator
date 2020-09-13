package com.shimizukenta.secssimulator;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum SecsSimulatorCommand {
	
	MANUAL("Show Commands, Show Manual with option"),
	
	QUIT("Quit application"),
	
	OPEN("Open communicator",
			"if already opened, close and reopen."),
	CLOSE("Close communicator"),
	
	PWD("Present working directory"),
	LS("List directory files"),
	CD("Change directory",
			"option is destination directory"),
	MKDIR("Make directory",
			"option is new directory name"),
	
	SIMM_SHOW_STATUS("Show Simulator status"),
	
	SIMM_SET_PROTOCOL("Set/Change Protocol",
			Stream.of(SecsSimulatorProtocol.values())
			.map(p -> "\"" + p.optionName() + "\"")
			.collect(Collectors.joining(", ")),
			"if already opened, close and reopen as changed Prorotol"),
	
	SIMM_SET_IPADDRESS("Set/Change IP-Address",
			"Set/Change Connect or Bind IP-Address",
			"Pattern is \"aaa.bbb.ccc.ddd:nnnnn\"",
			"if already opened, close and reopen as changed IP-Address"),
	
	SEND_SML("Send SML",
			"option is Alias-Name of SML"),
	SEND_DIRECT("Send Direct SML",
			"option is SML-String.",
			"Support 1 line only.",
			"sample: \"sd S1F16 <B 0x0>.\""),
	LINKTEST("Linktest",
			"Linktest if protocol is HSMS-SS."),
	
	LIST_SML("List SMLs",
			"List is SML entries"),
	SHOW_SML("Show SML",
			"option is Alias-Name of SML"),
	ADD_SML("Add SML",
			"option is path/to/file.sml"),
	ADD_SMLS("Add SMLs",
			"option is path/to/directory",
			"add all files of *.sml in directory"),
	REMOVE_SML("Remove SML",
			"option is Alias-Name of SML"),
	
	LOG("Logging start/stop",
			"if has option (path/to/file.log), logging start.",
			"if has no option, logging stop",
			"if already started, stop and restart."),
	
	MACRO("Macro start/stop",
			"if has option (path/to/file.macro), macro start.",
			"if has not option, macro stop.",
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
	
	;
	
	private String description;
	private String[] details;
	
	private SecsSimulatorCommand(String desc, String... lines) {
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
