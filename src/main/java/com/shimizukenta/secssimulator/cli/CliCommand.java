package com.shimizukenta.secssimulator.cli;

import java.util.Arrays;

public enum CliCommand {
	
	UNDEFINED(-1, "UNDEFINED"),
	
	MANUAL(2, CliCommandManual.MANUAL, "man", "?"),
	
	QUIT(1, CliCommandManual.QUIT, "quit", "exit"),
	OPEN(1, CliCommandManual.OPEN, "open"),
	CLOSE(1, CliCommandManual.CLOSE, "close"),
	
	LOAD(2, CliCommandManual.LOAD,"load", "load-config"),
	SAVE(2, CliCommandManual.SAVE, "save", "save-config"),
	STATUS(1, CliCommandManual.STATUS, "status"),
	
	SEND_SML(2, CliCommandManual.SEND_SML,
			"send", "ss", "sendsml", "send-sml"),
	
//	SEND_DIRECT(2, CliCommandManual.SEND_DIRECT,
//			"senddirect", "sd", "send-direct"),
	
	LINKTEST(1, CliCommandManual.LINKTEST, "linktest"),
	
	LIST_SML(1, CliCommandManual.LIST_SML, "list", "list-sml"),
	SHOW_SML(2, CliCommandManual.SHOW_SML, "show", "show-sml"),
	ADD_SML(2, CliCommandManual.ADD_SML, "addsml", "add-sml"),
	REMOVE_SML(2, CliCommandManual.REMOVE_SML, "removesml", "remove-sml"),
	
	PWD(1, CliCommandManual.PWD, "pwd"),
	CD(2, CliCommandManual.CD, "cd"),
	LS(2, CliCommandManual.LS, "ls"),
	MKDIR(2, CliCommandManual.MKDIR, "mkdir"),
	
	LOG(2, CliCommandManual.LOG, "log"),
	
	AUTO_REPLY(2, CliCommandManual.AUTO_REPLY, "autoreply", "auto-reply"),
	AUTO_REPLY_S9Fy(2, CliCommandManual.AUTO_REPLY_S9Fy,
			"autoreplys9fy", "auto-reply-s9fy", "autos9fy", "auto-s9fy"),
	AUTO_REPLY_SxF0(2, CliCommandManual.AUTO_REPLY_SxF0,
			"autoreplysxf0", "auto-reply-sxf0", "autosxf0", "auto-sxf0"),
	
	MACRO(2, CliCommandManual.MACRO, "macro"),
	LIST_MACRO(1, CliCommandManual.MACROS, "macros"),
	ADD_MACRO(2, CliCommandManual.ADD_MACRO,
			"addmacro", "add-macro"),
	REMOVE_MACRO(2, CliCommandManual.REMOVE_MACRO,
			"removemacro", "remove-macro"),
	
	;
	
	private final int split;
	private CliCommandManual manual;
	private final String[] commands;
	
	private CliCommand(int split, CliCommandManual manual, String... commands) {
		this.split = split;
		this.manual = manual;
		this.commands = commands;
	}
	
	private CliCommand(int split, String... commands) {
		this(split, null, commands);
	}
	
	public int split() {
		return this.split;
	}
	
	public CliCommandManual manual() {
		return this.manual;
	}
	
	public String[] commands() {
		return Arrays.copyOf(this.commands, this.commands.length);
	}
	
	public static CliCommand get(CharSequence command) {
		final String s = command.toString();
		for ( CliCommand v : values() ) {
			if ( v != UNDEFINED ) {
				for ( String c : v.commands ) {
					if ( c.equalsIgnoreCase(s) ) {
						return v;
					}
				}
			}
		}
		return UNDEFINED;
	}
	
}
