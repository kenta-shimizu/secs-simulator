package com.shimizukenta.secssimulator;

import java.io.Serializable;
import java.util.Objects;

import com.shimizukenta.secs.sml.SmlMessage;

public class SmlAlias implements Comparable<SmlAlias>, Serializable{
	
	private static final long serialVersionUID = -9104717537245402313L;
	
	private final String alias;
	private final SmlMessage sm;
	
	public SmlAlias(CharSequence alias, SmlMessage sm) {
		this.alias = Objects.requireNonNull(alias).toString();
		this.sm = Objects.requireNonNull(sm);
	}
	
	public String alias() {
		return alias;
	}
	
	public SmlMessage smlMessage() {
		return sm;
	}
	
	@Override
	public int hashCode() {
		return alias.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if ((o != null) && (o instanceof SmlAlias)) {
			return ((SmlAlias)o).alias.equals(alias);
		} else {
			return false;
		}
	}
	
	private Integer smNum() {
		return Integer.valueOf((sm.getStream() << 8) | sm.getFunction());
	}
	
	@Override
	public int compareTo(SmlAlias ref) {
		
		Integer v = smNum();
		Integer refv = ref.smNum();
		
		if ( v.equals(refv) ) {
			
			return alias.compareTo(ref.alias);
			
		} else {
			
			return v.compareTo(refv);
		}
	}
	
}
