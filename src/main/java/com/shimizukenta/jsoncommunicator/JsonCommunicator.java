package com.shimizukenta.jsoncommunicator;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;

/**
 * This interface is implements of JsonCommunicate,
 * open/close, send/recieve JSON, parsing from JSON to POJO.
 * 
 * <p>
 * To start communicating, {@link #open()}.<br />
 * To stop communicating, {@link #close()}.<br />
 * </p>
 * <p>
 * To send JSON,
 * {@link #send(CharSequence)},
 * {@link #send(AsynchronousSocketChannel, CharSequence)}.<br />
 * To parse POJO and send JSON,
 * {@link #send(Object)},
 * {@link #send(AsynchronousSocketChannel, Object)}.<br />
 * </p>
 * <p>
 * To receive JSON,
 * {@link #addJsonReceiveListener(JsonCommunicatorJsonReceiveListener)},
 * {@link #addJsonReceiveListener(JsonCommunicatorJsonReceiveBiListener)}.<br />
 * To receive parsed POJO,
 * {@link #addPojoReceiveListener(JsonCommunicatorPojoReceiveListener)},
 * {@link #addPojoReceiveListener(JsonCommunicatorPojoReceiveBiListener)}.<br />
 * </p>
 * <p>
 * To get communicate-state-changed,
 * {@link #addConnectionStateChangeListener(JsonCommunicatorConnectionStateChangeListener)},
 * {@link #addConnectionStateChangeListener(JsonCommunicatorConnectionStateChangeBiListener)}.<br />
 * </p>
 * <p>
 * To receive Communicate Log,
 * {@link #addLogListener(JsonCommunicatorLogListener)}.<br />
 * </p>
 * 
 * @author kenta-shimizu
 *
 * @param <T>
 */
public interface JsonCommunicator<T> extends Closeable {
	
	/**
	 * Returns is-open.
	 * 
	 * @return {@code true} if opened and <i>not</i> closed
	 */
	public boolean isOpen();
	
	/**
	 * Returns is-closed.
	 * 
	 * @return {@code true} if closed
	 */
	public boolean isClosed();
	
	/**
	 * Start communicating.
	 * 
	 * @throws IOException
	 */
	public void open() throws IOException;
	
	/**
	 * Send JSON to all connected channels.<br />
	 * 
	 * @param json
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public void send(CharSequence json) throws InterruptedException, IOException;
	
	/**
	 * Send JSON parsed POJO to all connected channels.
	 * 
	 * @param pojo
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws JsonCommunicatorParseException if parse failed
	 */
	public void send(Object pojo) throws InterruptedException, IOException, JsonCommunicatorParseException;
	
	/**
	 * Send JSON to target channel.
	 * 
	 * @param channel
	 * @param json
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public void send(AsynchronousSocketChannel channel, CharSequence json) throws InterruptedException, IOException;
	
	/**
	 * Send JSON parsed POJO to target channel.
	 *  
	 * @param channel
	 * @param pojo
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws JsonCommunicatorParseException if parse failed
	 */
	public void send(AsynchronousSocketChannel channel, Object pojo) throws InterruptedException, IOException, JsonCommunicatorParseException;
	
	/**
	 * Add received JSON Listener.
	 * 
	 * @param l
	 * @return {@code true} if add success
	 */
	public boolean addJsonReceiveListener(JsonCommunicatorJsonReceiveListener l);
	
	/**
	 * Remove received JSON Listener.
	 * 
	 * @param l
	 * @return {@code true} if remove success
	 */
	public boolean removeJsonReceiveListener(JsonCommunicatorJsonReceiveListener l);
	
	/**
	 * Add received (Channel and JSON) Listener.
	 * 
	 * @param l
	 * @return {@code true} if add success
	 */
	public boolean addJsonReceiveListener(JsonCommunicatorJsonReceiveBiListener l);
	
	/**
	 * Remove received (Channel and JSON) Listener.
	 * 
	 * @param l
	 * @return {@code true} if remove success
	 */
	public boolean removeJsonReceiveListener(JsonCommunicatorJsonReceiveBiListener l);
	
	/**
	 * Add parsed POJO Listener.
	 * 
	 * @param l
	 * @return {@code true} if add success
	 */
	public boolean addPojoReceiveListener(JsonCommunicatorPojoReceiveListener<? super T> l);
	
	/**
	 * Remove parsed POJO Listener.
	 * 
	 * @param l
	 * @return {@code true} if remove success
	 */
	public boolean removePojoReceiveListener(JsonCommunicatorPojoReceiveListener<? super T> l);
	
	/**
	 * Add received (Channel and POJO) Listener.
	 * 
	 * @param l
	 * @return {@code true} if add success
	 */
	public boolean addPojoReceiveListener(JsonCommunicatorPojoReceiveBiListener<? super T> l);
	
	/**
	 * Remove received (Channel and POJO) Listener.
	 * 
	 * @param l
	 * @return {@code true} if remove success
	 */
	public boolean removePojoReceiveListener(JsonCommunicatorPojoReceiveBiListener<? super T> l);
	
	/**
	 * Add changed ConnectionState Listener.
	 * 
	 * <p>
	 * This listener is blocking method, Pass through quickly.<br />
	 * </p>
	 * 
	 * @param l
	 * @return {@code true} if add success.
	 */
	public boolean addConnectionStateChangeListener(JsonCommunicatorConnectionStateChangeListener l);
	
	/**
	 * Remove changed ConnectionState Listener.
	 * 
	 * @param l
	 * @return {@code true} if remove success
	 */
	public boolean removeConnectionStateChangeListener(JsonCommunicatorConnectionStateChangeListener l);
	
	/**
	 * Add changed (channel and ConnectionState) Listener.
	 * 
	 * <p>
	 * This listener is blocking method, Pass through quickly.<br />
	 * </p>
	 * 
	 * @param l
	 * @return {@code true} if add success.
	 */
	public boolean addConnectionStateChangeListener(JsonCommunicatorConnectionStateChangeBiListener l);
	
	/**
	 * Remove changed (channel and ConnectionState) Listener.
	 * 
	 * @param l
	 * @return {@code true} if remove success
	 */
	public boolean removeConnectionStateChangeListener(JsonCommunicatorConnectionStateChangeBiListener l);
	
	/**
	 * Add Communicate Log Listener.
	 * 
	 * @param l
	 * @return {@code true} if add success
	 */
	public boolean addLogListener(JsonCommunicatorLogListener l);
	
	/**
	 * Remove Communicate Log Listeenr.
	 * 
	 * @param l
	 * @return {@code true} if remove success
	 */
	public boolean removeLogListener(JsonCommunicatorLogListener l);

}
