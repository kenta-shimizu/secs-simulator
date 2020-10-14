package com.shimizukenta.secssimulator.extendsml;

import com.shimizukenta.secs.sml.SmlDataItemParser;
import com.shimizukenta.secs.sml.SmlMessageParser;

public class ExtendSmlMessageParser extends SmlMessageParser {

	protected ExtendSmlMessageParser() {
		super();
	}
	
	private static class SingletonHolder {
		private static ExtendSmlMessageParser inst = new ExtendSmlMessageParser();
	}
	
	public static ExtendSmlMessageParser getInstance() {
		return SingletonHolder.inst;
	}
	
	@Override
	protected SmlDataItemParser getSmlSecs2Parser() {
		return ExtendSmlDataItemParser.getInstance();
	}
}
