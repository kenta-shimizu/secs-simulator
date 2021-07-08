package com.shimizukenta.secssimulator.extendsml;

import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.shimizukenta.secs.secs2.AbstractSecs2;
import com.shimizukenta.secs.secs2.Secs2BuildException;
import com.shimizukenta.secs.secs2.Secs2BytesPackBuilder;
import com.shimizukenta.secs.secs2.Secs2Exception;
import com.shimizukenta.secs.secs2.Secs2IndexOutOfBoundsException;

public abstract class AbstractSecs2AutoNumber extends AbstractSecs2 {
	
	private static final long serialVersionUID = 5803001528341990499L;
	
	private static final AtomicLong autoNumber = new AtomicLong(0L);
	
	public AbstractSecs2AutoNumber() {
		super();
	}
	
	@Override
	public int size() {
		return 1;
	}
	
	@Override
	protected String toStringValue() {
		return "";
	}
	
	abstract protected byte[] createNumberBytes();
	
	@Override
	protected void putBytesPack(Secs2BytesPackBuilder builder) throws Secs2BuildException {
		this.putHeadAndBodyBytesToBytesPack(builder, createNumberBytes());
	}
	
	protected Number getNumber() {
		return autoNumber;
	}
	
	protected long incrementAndGet() {
		return autoNumber.incrementAndGet();
	}
	
	@Override
	protected int getInt(int index) throws Secs2Exception {
		if ( index == 0 ) {
			return getNumber().intValue();
		}
		throw new Secs2IndexOutOfBoundsException();
	}
	
	@Override
	protected long getLong(int index) throws Secs2Exception {
		if ( index == 0 ) {
			return getNumber().longValue();
		}
		throw new Secs2IndexOutOfBoundsException();
	}
	
	@Override
	protected float getFloat(int index) throws Secs2Exception {
		if ( index == 0 ) {
			return getNumber().floatValue();
		}
		throw new Secs2IndexOutOfBoundsException();
	}
	
	@Override
	protected double getDouble(int index) throws Secs2Exception {
		if ( index == 0 ) {
			return getNumber().doubleValue();
		}
		throw new Secs2IndexOutOfBoundsException();
	}
	
	@Override
	protected BigInteger getBigInteger(int index) throws Secs2Exception {
		if ( index == 0 ) {
			return BigInteger.valueOf(getNumber().longValue());
		}
		throw new Secs2IndexOutOfBoundsException();
	}
	
}
