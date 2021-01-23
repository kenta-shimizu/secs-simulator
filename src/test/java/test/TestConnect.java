package test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

import com.shimizukenta.jsoncommunicator.JsonCommunicator;
import com.shimizukenta.jsoncommunicator.JsonCommunicators;

public class TestConnect {

	public TestConnect() {
		/* Nothing */
	}

	public static void main(String[] args) {
		
		final SocketAddress addr = new InetSocketAddress("127.0.0.1", 10000);
		
		System.out.println("start");
		
		try (
				JsonCommunicator<?> jsonComm = JsonCommunicators.createClient(addr);
				) {
			
			jsonComm.addJsonReceiveListener(json -> {
				System.out.println(json);
			});
			
			jsonComm.open();
			
			TimeUnit.SECONDS.sleep(1L);
			
			jsonComm.send("{\"request\":\"open\"}");
			
			TimeUnit.SECONDS.sleep(3L);
			
			jsonComm.send("{\"request\":\"close\"}");
			
			TimeUnit.SECONDS.sleep(3L);
			
			jsonComm.send("{\"request\":\"reboot\"}");
			
			TimeUnit.SECONDS.sleep(2L);
		}
		catch ( InterruptedException ignore ) {
		}
		catch ( IOException e ) {
			e.printStackTrace();
		}
		
		System.out.println("end");
	}

}
