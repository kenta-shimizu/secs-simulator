package com.shimizukenta.secssimulator.extendsml;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.shimizukenta.secs.secs2.Secs2Ascii;
import com.shimizukenta.secs.secs2.Secs2BuildException;
import com.shimizukenta.secs.secs2.Secs2ByteBuffersBuilder;
import com.shimizukenta.secs.secs2.Secs2Exception;

public class Secs2Now extends Secs2Ascii {

	private static final long serialVersionUID = 9064194857259167313L;
	
	private final int size;
	
	protected Secs2Now(int size) {
		super("");
		this.size = size;
	}

	@Override
	public int size() {
		return size;
	}
	
	private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
	
	private String now() {
		String s = LocalDateTime.now().format(dtf);
		
		if ( size == 12 ) {
			return s.substring(2, 14);
		} else if ( size == 16 ){
			return s.substring(0, 16);
		} else {
			return "";
		}
	}
	
	@Override
	protected void putByteBuffers(Secs2ByteBuffersBuilder buffers) throws Secs2BuildException {
		byte[] bs = now().getBytes(StandardCharsets.US_ASCII);
		putHeaderBytesToByteBuffers(buffers, bs.length);
		buffers.put(bs);
	}
	
	@Override
	public String getAscii() throws Secs2Exception {
		return now();
	}
	
	@Override
	protected String toJsonValue() {
		return "\"" + now() + "\"";
	}
	
	@Override
	public String toString() {
		return "<NOW [" + size() + "] >";
	}
	
	@Override
	protected String toStringValue() {
		return "\"" + now() + "\"";
	}


}
