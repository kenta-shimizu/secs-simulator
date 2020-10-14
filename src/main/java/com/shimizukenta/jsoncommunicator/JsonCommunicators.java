package com.shimizukenta.jsoncommunicator;

import java.io.IOException;
import java.net.SocketAddress;

/**
 * This class is communicator instance builder.
 * 
 * <p>
 * To get client instance,
 * {@link #createClient(SocketAddress)}
 * or {@link #createClient(SocketAddress, Class)}.<br />
 * To get server instance,
 * {@link #createServer(SocketAddress)}
 * or {@link #createServer(SocketAddress, Class)}.<br />
 * To get complex instance,
 * {@link #newInstance(JsonCommunicatorConfig)}
 * or {@link #newInstance(JsonCommunicatorConfig, Class)}.<br />
 * </p>
 * <p>
 * To create client instance and open,
 * {@link #openClient(SocketAddress)}
 * or {@link #openClient(SocketAddress, Class)}.<br />
 * To create server instance and open,
 * {@link #openServer(SocketAddress)}
 * or {@link #openServer(SocketAddress, Class)}.<Br />
 * To create complex instance and open,
 * {@link #open(JsonCommunicatorConfig)}
 * or {@link #open(JsonCommunicatorConfig, Class)}.<br />
 * </p>
 * 
 * @author kenta-shimizu
 *
 */
public final class JsonCommunicators {

	private JsonCommunicators() {
		/* Nothing */
	}
	
	/**
	 * Create JsonCommunicator instance for Server.
	 * 
	 * @param addr binding SocketAddress
	 * @return JsonCommunicator<?> instance
	 */
	public static JsonCommunicator<?> createServer(SocketAddress addr) {
		JsonCommunicatorConfig config = new JsonCommunicatorConfig();
		config.addBind(addr);
		return newInstance(config);
	}
	
	/**
	 * Create JsonCommunicator instance for Server,
	 * this instance can parse to class of T POJO.
	 * 
	 * @param <T>
	 * @param addr binding SocketAddress
	 * @param classOfT to parsing POJO
	 * @return JsonCommunicator<T> instance
	 */
	public static <T> JsonCommunicator<T> createServer(SocketAddress addr, Class<T> classOfT) {
		JsonCommunicatorConfig config = new JsonCommunicatorConfig();
		config.addBind(addr);
		return newInstance(config, classOfT);
	}
	
	/**
	 * Create and open JsonCommunicator instance for Server.
	 * 
	 * @param addr binding SocketAddress
	 * @return JsonCommunicator<?> instance
	 * @throws IOException
	 */
	public static JsonCommunicator<?> openServer(SocketAddress addr) throws IOException {
		final JsonCommunicator<?> inst = createServer(addr);
		tryOpen(inst);
		return inst;
	}
	
	/**
	 * Create and open JsonCommunicator instance for Server,
	 * this instance can parse to class of T POJO.
	 * 
	 * @param <T>
	 * @param addr binding SocketAddress
	 * @param classOfT to parsing POJO
	 * @return JsonCommunicator<T> instance
	 * @throws IOException
	 */
	public static <T> JsonCommunicator<T> openServer(SocketAddress addr, Class<T> classOfT) throws IOException {
		final JsonCommunicator<T> inst = createServer(addr, classOfT);
		tryOpen(inst);
		return inst;
	}
	
	/**
	 * Create JsonCommunicator instance for Client.
	 * 
	 * @param addr connecting SocketAddress
	 * @return JsonCommunicator<?> instance
	 */
	public static JsonCommunicator<?> createClient(SocketAddress addr) {
		JsonCommunicatorConfig config = new JsonCommunicatorConfig();
		config.addConnect(addr);
		return newInstance(config);
	}
	
	/**
	 * Create JsonCommunicator instance for Client,
	 * this instance can parse to class of T POJO.
	 * 
	 * @param <T>
	 * @param addr connecting SocketAddress
	 * @param classOfT to parsing POJO
	 * @return JsonCommunicator<T> instance
	 */
	public static <T> JsonCommunicator<T> createClient(SocketAddress addr, Class<T> classOfT) {
		JsonCommunicatorConfig config = new JsonCommunicatorConfig();
		config.addConnect(addr);
		return newInstance(config, classOfT);
	}
	
	/**
	 * Create and open JsonCommunicator instance for Client.
	 * 
	 * @param addr connecting SocketAddress
	 * @return JsonCommunicator<?> instance
	 * @throws IOException
	 */
	public static JsonCommunicator<?> openClient(SocketAddress addr) throws IOException {
		final JsonCommunicator<?> inst = createClient(addr);
		tryOpen(inst);
		return inst;
	}
	
	/**
	 * Create and open JsonCommunicator instance for Client,
	 * this instance can parse to class of T POJO.
	 * 
	 * @param <T>
	 * @param addr connecting SocketAddress
	 * @param classOfT to parsing POJO
	 * @return JsonCommunicator<T> instance
	 * @throws IOException
	 */
	public static <T> JsonCommunicator<T> openClient(SocketAddress addr, Class<T> classOfT) throws IOException {
		final JsonCommunicator<T> inst = createClient(addr, classOfT);
		tryOpen(inst);
		return inst;
	}
	
	/**
	 * Create JsonCommunicator by JsonCommunicatorConfig.
	 * 
	 * @param config
	 * @return JsonCommunicator<?> instance
	 */
	public static JsonCommunicator<?> newInstance(JsonCommunicatorConfig config) {
		return new JsonHubCommunicator<Object>(config);
	}
	
	/**
	 * Create JsonCommunicator by JsonCommunicatorConfig,
	 * this instance can parse to class of T POJO.
	 * 
	 * @param <T>
	 * @param config
	 * @param classOfT to parsing POJO
	 * @return JsonCommunicator<T> instance
	 */
	public static <T> JsonCommunicator<T> newInstance(JsonCommunicatorConfig config, Class<T> classOfT) {
		return new JsonHubCommunicator<T>(config, classOfT);
	}
	
	/**
	 * Create and open JsonCommunicator by JsonCommunicatorConfig.
	 * 
	 * @param config
	 * @return JsonCommunicator<?> instance
	 * @throws IOException
	 */
	public static JsonCommunicator<?> open(JsonCommunicatorConfig config) throws IOException {
		final JsonCommunicator<?> inst = newInstance(config);
		tryOpen(inst);
		return inst;
	}
	
	/**
	 * Create and open JsonCommunicator by JsonCommunicatorConfig,
	 * this instance can parse to class of T POJO.
	 * 
	 * @param <T>
	 * @param config
	 * @param classOfT to parsing POJO
	 * @return JsonCommunicator<T> instance
	 * @throws IOException
	 */
	public static <T> JsonCommunicator<T> open(JsonCommunicatorConfig config, Class<T> classOfT) throws IOException {
		final JsonCommunicator<T> inst = newInstance(config, classOfT);
		tryOpen(inst);
		return inst;
	}
	
	private static void tryOpen(JsonCommunicator<?> comm) throws IOException {
		try {
			comm.open();
		}
		catch ( IOException e ) {
			try {
				comm.close();
			}
			catch ( IOException giveup ) {
			}
			throw e;
		}
	}

}
