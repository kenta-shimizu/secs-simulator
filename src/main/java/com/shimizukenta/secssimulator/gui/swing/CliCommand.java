package com.shimizukenta.secssimulator.gui.swing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public enum CliCommand {
	
	UNDEFINED(-1, "UNDEFINED"),
	
	MANUAL(2, CliCommandManual.MANUAL, "man", "?"),
	
	OPEN(1, CliCommandManual.OPEN, "open"),
	CLOSE(1, CliCommandManual.CLOSE, "close"),
	QUIT(1, CliCommandManual.QUIT, "quit", "exit"),
	
	SEND_SML(2, CliCommandManual.SEND_SML,
			"send", "ss", "sendsml", "send-sml"),
	SEND_DIRECT(2, CliCommandManual.SEND_DIRECT,
			"senddirect", "sd", "send-direct"),
	LINKTEST(1, CliCommandManual.LINKTEST, "linktest"),
	
	LIST_SML(1, CliCommandManual.LIST_SML, "list"),
	SHOW_SML(2, CliCommandManual.SHOW_SML, "show"),
	ADD_SML(2, CliCommandManual.ADD_SML, "addfile", "add-file"),
	ADD_SMLS(2, CliCommandManual.ADD_SMLS, "addfiles", "add-files"),
	REMOVE_SML(2, CliCommandManual.REMOVE_SML, "removesml", "remove-sml"),
	
	PWD(1, CliCommandManual.PWD, "pwd"),
	CD(2, CliCommandManual.CD, "cd"),
	LS(1, CliCommandManual.LS, "ls"),
	MKDIR(2, CliCommandManual.MKDIR, "mkdir"),
	
	LOG(2, CliCommandManual.LOG, "log"),
	MACRO(2, CliCommandManual.MACRO, "macro"),
	
	AUTO_REPLY(2, CliCommandManual.AUTO_REPLY, "autoreply", "auto-reply"),
	AUTO_REPLY_S9Fy(2, CliCommandManual.AUTO_REPLY_S9Fy,
			"autoreplys9fy", "auto-reply-s9fy", "autos9fy", "auto-s9fy"),
	AUTO_REPLY_SxF0(2, CliCommandManual.AUTO_REPLY_SxF0,
			"autoreplysxf0", "auto-reply-sxf0", "autosxf0", "auto-sxf0"),
	
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
	
	public static CliCommand get(CharSequence requestLine) {
		
		String s = Objects.requireNonNull(requestLine).toString().trim();
		
		for ( CliCommand v : values() ) {
			
			String c;
			if ( v.split > 1 ) {
				c = (s.split("\\s+", 2))[0];
			} else {
				c = s;
			}
			
			for (String a : v.commands ) {
				if ( a.equalsIgnoreCase(c) ) {
					return v;
				}
			}
		}
		
		return UNDEFINED;
	}
	
	private static final String[] emptyArray = new String[0];
	
	public static CliRequest getRequest(CharSequence requestLine) {
		
		CliCommand cmd = get(requestLine);
		
		if ( cmd.split > 1 ) {
			
			String[] ss = requestLine.toString().trim().split("\\s+", cmd.split);
			
			ss = Arrays.copyOfRange(ss, 1, ss.length);
			
			return new CliRequest(cmd, ss);
			
		} else {
			
			return new CliRequest(cmd, emptyArray);
		}
	}
	
	private static final String BR = System.lineSeparator();
	
	private static final String descLine(CliCommand cmd) {
		
		List<String> cmds = new ArrayList<>();
		
		for ( String s : cmd.commands ) {
			cmds.add(s);
		}
		
		return cmds.stream().collect(Collectors.joining(" | ", "[ ", " ]"))
				+ " " + cmd.manual.description();
	}
	
	public static String getManuals() {
		
		List<String> lines = new ArrayList<>();
		
		for ( CliCommand cmd : values() ) {
			if ( cmd.manual != null ) {
				lines.add(descLine(cmd));
			}
		}
		
		return lines.stream().collect(Collectors.joining(BR));
	}
	
	public static String getDetailManual(CharSequence command) {
		
		CliCommand cmd = get(command);
		
		if ( cmd.manual == null ) {
			return "\"" + command.toString() + "\" has no manual";
		}
		
		List<String> lines = new ArrayList<>();
		
		lines.add(descLine(cmd));
		
		for ( String detail : cmd.manual.details() ) {
			lines.add(detail);
		}
		
		return lines.stream().collect(Collectors.joining(BR));
	}
	
}
