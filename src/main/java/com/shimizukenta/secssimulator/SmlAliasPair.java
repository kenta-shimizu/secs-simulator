package com.shimizukenta.secssimulator;

import java.util.Objects;

import com.shimizukenta.secs.sml.SmlMessage;

/**
 * This class is implements of SML-Message and alias getter.
 * 
 * <p>
 * Instances of this class are immutable.<br />
 * </p>
 * 
 * @author kenta-shimizu
 *
 */
public class SmlAliasPair {
	
	private final String alias;
	private final SmlMessage sml;
	
	public SmlAliasPair(CharSequence alias, SmlMessage sm) {
		this.alias = Objects.requireNonNull(alias).toString();
		this.sml = Objects.requireNonNull(sm);
	}
	
	/**
	 * Returns alias.
	 * 
	 * @return alias
	 */
	public String alias() {
		return this.alias;
	}
	
	/**
	 * Return SML-Message.
	 * 
	 * @return SML-Message
	 */
	public SmlMessage sml() {
		return this.sml;
	}
	
	@Override
	public int hashCode() {
		return this.alias.hashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if ((other != null) && (other instanceof SmlAliasPair)) {
			return ((SmlAliasPair)other).alias().equals(alias());
		}
		return false;
	}
	
}
