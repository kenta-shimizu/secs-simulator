package com.shimizukenta.secssimulator.extendsml;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import com.shimizukenta.secs.secs2.Secs2Item;

public class Secs2Int8AutoNumber extends AbstractSecs2AutoNumber {
	
	private static final long serialVersionUID = 7458627781412030462L;
	
	public Secs2Int8AutoNumber() {
		super();
	}

	private static final Secs2Item secs2Item = Secs2Item.INT8;
	
	@Override
	public Secs2Item secs2Item() {
		return secs2Item;
	}
	
	@Override
	protected byte[] createNumberBytes() {
		
		final long n = incrementAndGet();
		
		ByteBuffer bb = ByteBuffer.allocate(8);
		bb.putLong(n);
		((Buffer)bb).flip();
		
		byte[] bs = new byte[8];
		bb.get(bs);
		return bs;
	}
	
	@Override
	public String toString() {
		return "<I8AUTO [1] >";
	}
	
	@Override
	protected String toJsonValue() {
		return String.valueOf(getNumber().longValue());
	}
	
}
