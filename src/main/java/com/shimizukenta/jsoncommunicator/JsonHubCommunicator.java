package com.shimizukenta.jsoncommunicator;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Collection;
import java.util.function.BiConsumer;

import com.shimizukenta.jsonhub.JsonHub;
import com.shimizukenta.jsonhub.JsonHubParseException;

public class JsonHubCommunicator<T> extends AbstractJsonCommunicator<T> {
	
	private final BiConsumer<AsynchronousSocketChannel, JsonHub> biconsumer;
	
	protected JsonHubCommunicator(JsonCommunicatorConfig config) {
		super(config);
		
		this.biconsumer = (channel, jh) -> {
			/* Nothing */
		};
	}
	
	protected JsonHubCommunicator(JsonCommunicatorConfig config, Class<T> classOfT) {
		super(config);
		
		this.biconsumer = (channel, jh) -> {
			try {
				receivePojo(channel, jh.toPojo(classOfT));
			}
			catch ( JsonHubParseException e ) {
				notifyLog(e);
			}
		};
	}

	@Override
	protected void putReceivedBytes(AsynchronousSocketChannel channel, byte[] bs) {
		
		try {
			JsonHub jh = JsonHub.fromBytes(bs);
			receiveJson(channel, jh.toJson());
			biconsumer.accept(channel, jh);
		}
		catch ( JsonHubParseException e ) {
			notifyLog(e);
		}
	}
	
	@Override
	protected byte[] createBytesFromPojo(Object pojo) throws JsonCommunicatorParseException {
		try {
			return JsonHub.fromPojo(pojo).getBytesExcludedNullValueInObject();
		}
		catch ( JsonHubParseException e ) {
			throw new JsonCommunicatorParseException(e);
		}
	}
	
	@Override
	protected void send(Collection<AsynchronousSocketChannel> channels, Object pojo) throws InterruptedException, IOException, JsonCommunicatorParseException {
		try {
			JsonHub jh = JsonHub.fromPojo(pojo);
			byte[] bs = jh.getBytesExcludedNullValueInObject();
			String json = jh.toJsonExcludedNullValueInObject();
			send(channels, bs, json);
		}
		catch ( JsonHubParseException e ) {
			throw new JsonCommunicatorParseException(e);
		}
	}


}
