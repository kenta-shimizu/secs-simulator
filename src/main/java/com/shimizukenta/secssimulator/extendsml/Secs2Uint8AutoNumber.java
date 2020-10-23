package com.shimizukenta.secssimulator.extendsml;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import com.shimizukenta.secs.secs2.Secs2Item;

public class Secs2Uint8AutoNumber extends AbstractSecs2AutoNumber {
	
	private static final long serialVersionUID = 4755532658281274089L;
	
	public Secs2Uint8AutoNumber() {
		super();
	}
	
	private static final Secs2Item secs2Item = Secs2Item.UINT8;
	
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
		return "<U8AUTO [1] >";
	}
	
	@Override
	protected String toJsonValue() {
		return Long.toUnsignedString(getNumber().longValue());
	}
	
}
