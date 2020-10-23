package com.shimizukenta.secssimulator.extendsml;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import com.shimizukenta.secs.secs2.Secs2Item;

public class Secs2Uint4AutoNumber extends AbstractSecs2AutoNumber {
	
	private static final long serialVersionUID = -3961383539177789023L;
	
	public Secs2Uint4AutoNumber() {
		super();
	}
	
	private static final Secs2Item secs2Item = Secs2Item.UINT4;
	
	@Override
	public Secs2Item secs2Item() {
		return secs2Item;
	}
	
	@Override
	protected byte[] createNumberBytes() {
		
		final int n = (int)(incrementAndGet());
		
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.putInt(n);
		((Buffer)bb).flip();
		
		byte[] bs = new byte[4];
		bb.get(bs);
		return bs;
	}
	
	@Override
	public String toString() {
		return "<U4AUTO [1] >";
	}
	
	@Override
	protected String toJsonValue() {
		return Integer.toUnsignedString(getNumber().intValue());
	}
	
}
