package com.shimizukenta.secssimulator.extendsml;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import com.shimizukenta.secs.secs2.Secs2Item;

public class Secs2Int4AutoNumber extends AbstractSecs2AutoNumber {
	
	private static final long serialVersionUID = -7326580608711750861L;
	
	public Secs2Int4AutoNumber() {
		super();
	}

	private static final Secs2Item secs2Item = Secs2Item.INT4;
	
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
		return "<I4AUTO [1] >";
	}
	
	@Override
	protected String toJsonValue() {
		return String.valueOf(getNumber().intValue());
	}
	
}
