package com.shimizukenta.jsoncommunicator;

import java.io.Serializable;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * This class is JsonCommunicator config setter/getter.
 * 
 * <p>
 * To add binding SocketAddress, {@link #addBind(SocketAddress)}.<br />
 * To add connecting SocketAddress, {@link #addConnect(SocketAddress)}.<br />
 * </p>
 * <p>
 * To set rebind seconds, {@link #rebindSeconds(float)}.<br />
 * To set connect seconds, {@link #reconnectSeconds(float)}.<br />
 * </p>
 * 
 * @author kenta-shimizu
 *
 */
public class JsonCommunicatorConfig implements Serializable {
	
	private static final long serialVersionUID = 5336238572932335370L;
	
	public static final float REBIND_SECONDS = 10.0F;
	public static final float RECONNECT_SECONDS = 10.0F;
	
	private final Set<SocketAddress> binds = new CopyOnWriteArraySet<>();
	private final Set<SocketAddress> connects = new CopyOnWriteArraySet<>();
	private float rebindSeconds;
	private float reconnectSeconds;
	private String logSubjectHeader;
	
	public JsonCommunicatorConfig() {
		this.rebindSeconds = REBIND_SECONDS;
		this.reconnectSeconds = RECONNECT_SECONDS;
		this.logSubjectHeader = "";
	}
	
	/**
	 * Returns bind SocketAddress set.
	 * 
	 * <p>
	 * This set is UnmodifiableSet.<br />
	 * </p>
	 * 
	 * @return bind SocketAddress set
	 */
	public Set<SocketAddress> binds() {
		return Collections.unmodifiableSet(binds);
	}
	
	/**
	 * Returns connects SocketAddress set.
	 * 
	 * <p>
	 * This set is UnmodifiableSet.<br />
	 * </p>
	 * 
	 * @return connects SocketAddress set
	 */
	public Set<SocketAddress> connects() {
		return Collections.unmodifiableSet(connects);
	}
	
	/**
	 * Returns rebind seconds.
	 * 
	 * @return rebind seconds
	 */
	public float rebindSeconds() {
		synchronized ( this ) {
			return rebindSeconds;
		}
	}
	
	/**
	 * Returns reconnect seconds.
	 * 
	 * @return reconnect seconds
	 */
	public float reconnectSeconds() {
		synchronized ( this ) {
			return reconnectSeconds;
		}
	}
	
	/**
	 * Add binding SocketAddress.
	 * 
	 * <p>
	 * Not accept {@code null}.<br />
	 * </p>
	 * 
	 * @param addr
	 * @return {@code true} if add success
	 */
	public boolean addBind(SocketAddress addr) {
		return binds.add(Objects.requireNonNull(addr));
	}
	
	/**
	 * Remove binding SocketAddress.
	 * 
	 * <p>
	 * Not accept {@code null}.<br />
	 * </p>
	 * 
	 * @param addr
	 * @return {@code true} if remove success.
	 */
	public boolean removeBind(SocketAddress addr) {
		return binds.remove(Objects.requireNonNull(addr));
	}
	
	/**
	 * Add connecting SocketAddress.
	 * 
	 * <p>
	 * Not accept {@code null}.<br />
	 * </p>
	 * 
	 * @param addr
	 * @return {@code true} if add success
	 */
	public boolean addConnect(SocketAddress addr) {
		return connects.add(Objects.requireNonNull(addr));
	}
	
	/**
	 * Remove connecting SocketAddress.
	 * 
	 * <p>
	 * Not accept {@code null}.<br />
	 * </p>
	 * 
	 * @param addr
	 * @return {@code true} if remove success
	 */
	public boolean removeConnect(SocketAddress addr) {
		return connects.remove(Objects.requireNonNull(addr));
	}
	
	/**
	 * Rebind seconds Setter.
	 * 
	 * @param v seconds
	 */
	public void rebindSeconds(float v) {
		synchronized ( this ) {
			this.rebindSeconds = v;
		}
	}
	
	/**
	 * Reconnect seconds Setter.
	 * 
	 * @param v seconds
	 */
	public void reconnectSeconds(float v) {
		synchronized ( this ) {
			this.reconnectSeconds = v;
		}
	}
	
	/**
	 * Log-Subject-Header Setter.
	 * 
	 * <p>
	 * Not accept {@code null}.<br />
	 * </p>
	 * 
	 * @param cs
	 */
	public void logSubjectHeader(CharSequence cs) {
		synchronized ( this ) {
			this.logSubjectHeader = Objects.requireNonNull(cs).toString();
		}
	}
	
	/**
	 * Returns Log-Subject-Header.
	 * 
	 * @return value if exist, and {@code Optional.empty()} otherwise
	 */
	public Optional<String> logSubjectHeader() {
		synchronized ( this ) {
			return this.logSubjectHeader.isEmpty() ? Optional.empty() : Optional.of(this.logSubjectHeader);
		}
	}
	
}
