package com.shimizukenta.secssimulator.extendsml;

import com.shimizukenta.secs.sml.SmlDataItemParser;
import com.shimizukenta.secs.sml.SmlParseException;

public class ExtendSmlDataItemParser extends SmlDataItemParser {

	protected ExtendSmlDataItemParser() {
		super();
	}
	
	private static class SingletonHolder {
		private static ExtendSmlDataItemParser inst = new ExtendSmlDataItemParser();
	}
	
	public static ExtendSmlDataItemParser getInstance() {
		return SingletonHolder.inst;
	}
	
	private static final String itemNow = "NOW";
	
	private static final String itemU4Auto = "U4AUTO";
	private static final String itemU8Auto = "U8AUTO";
	private static final String itemI4Auto = "I4AUTO";
	private static final String itemI8Auto = "I8AUTO";
	
	@Override
	protected SeekValueResult parseExtend(String str, int fromIndex, String secs2ItemString, int size)
			throws SmlParseException {
		
		if ( secs2ItemString.equals(itemNow) ) {
			return parseNow(str, fromIndex, size);
		}
		
		if ( secs2ItemString.equals(itemU4Auto) ) {
			return parseU4Auto(str, fromIndex, size);
		}
		
		if ( secs2ItemString.equals(itemU8Auto) ) {
			return parseU8Auto(str, fromIndex, size);
		}
		
		if ( secs2ItemString.equals(itemI4Auto) ) {
			return parseI4Auto(str, fromIndex, size);
		}
		
		if ( secs2ItemString.equals(itemI8Auto) ) {
			return parseI8Auto(str, fromIndex, size);
		}
		
		
		/* HOOK   */
		/* Others */
		
		
		throw new SmlParseException("UNKNOWN SECS2ITEM type: " + secs2ItemString);
	}
	
	private SeekValueResult parseNow(String str, int fromIndex, int size)
			throws SmlParseException {
		
		SeekCharResult r = this.seekAngleBranketEnd(str, fromIndex);
		return seekValueResult(Secs2Now.now(size), r.index + 1);
	}
	
	private SeekValueResult parseU4Auto(String str, int fromIndex, int size)
			throws SmlParseException {
		
		SeekCharResult r = this.seekAngleBranketEnd(str, fromIndex);
		return seekValueResult(new Secs2Uint4AutoNumber(), r.index + 1);
	}
	
	private SeekValueResult parseU8Auto(String str, int fromIndex, int size)
			throws SmlParseException {
		
		SeekCharResult r = this.seekAngleBranketEnd(str, fromIndex);
		return seekValueResult(new Secs2Uint8AutoNumber(), r.index + 1);
	}
	
	private SeekValueResult parseI4Auto(String str, int fromIndex, int size)
			throws SmlParseException {
		
		SeekCharResult r = this.seekAngleBranketEnd(str, fromIndex);
		return seekValueResult(new Secs2Int4AutoNumber(), r.index + 1);
	}
	
	private SeekValueResult parseI8Auto(String str, int fromIndex, int size)
			throws SmlParseException {
		
		SeekCharResult r = this.seekAngleBranketEnd(str, fromIndex);
		return seekValueResult(new Secs2Int8AutoNumber(), r.index + 1);
	}
	
}
