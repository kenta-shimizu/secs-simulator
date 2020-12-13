package com.shimizukenta.secssimulator.cli;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.shimizukenta.secssimulator.AbstractSecsSimulator;
import com.shimizukenta.secssimulator.AbstractSecsSimulatorConfig;
import com.shimizukenta.secssimulator.SecsSimulatorProtocol;

public class CliSecsSimulator extends AbstractSecsSimulator {
	
	public static void main(String[] args) {
		
		try {
			echo("Simulator started");
			
			final AbstractSecsSimulatorConfig config = new AbstractSecsSimulatorConfig() {
				private static final long serialVersionUID = 5301436919812579702L;
			};
			
			boolean configLoaded = false;
			
			for ( int i = 0, m = args.length; i < m; i += 2 ) {
				String key = args[i];
				String v = args[i + 1];
				if ( key.equalsIgnoreCase("--config") ) {
					if ( config.load(Paths.get(v)) ) {
						configLoaded = true;
					}
				}
			}
			
			final CliSecsSimulator simm = new CliSecsSimulator(config);
			
			simm.addSecsLogListener(log -> {echo(log);});
			
			try {
				
				try (
						InputStreamReader isr = new InputStreamReader(System.in);
						) {
					
					try (
							BufferedReader br = new BufferedReader(isr);
							) {
						
						if ( ! configLoaded ) {
							
							{
								System.out.println("Choose prorocol");
								System.out.println("1: HSMS-SS-PASSIVE, 2: HSMS-SS-ACTIVE");
								System.out.println("3: SECS1-ON-TCP/IP, 4: SECS1-ON-TCP/IP-RECEIVER");
								System.out.print(": ");
								
								String v = br.readLine();
								if ( v.equals("1") ) {
									config.protocol(SecsSimulatorProtocol.HSMS_SS_PASSIVE);
								} else if ( v.equals("2") ) {
									config.protocol(SecsSimulatorProtocol.HSMS_SS_ACTIVE);
								} else if ( v.equals("3") ) {
									config.protocol(SecsSimulatorProtocol.SECS1_ON_TCP_IP);
								} else if ( v.equals("4") ) {
									config.protocol(SecsSimulatorProtocol.SECS1_ON_TCP_IP_RECEIVER);
								}
							}
							{
								System.out.print("Enter SocketAddress: ");
								String v = br.readLine();
								config.socketAddress(v);
							}
							{
								System.out.print("Enter Device-ID: ");
								String v = br.readLine();
								config.deviceId(Integer.parseInt(v));
							}
						}
						
						for ( ;; ) {
							
							String line = br.readLine();
							
							if ( line == null ) {
								break;
							}
							
							line = line.trim();
							
							if ( line.isEmpty() ) {
								continue;
							}
							
							if ( line.equalsIgnoreCase("quit") ) {
								break;
							}
							
							if ( line.equalsIgnoreCase("open") ) {
								simm.openCommunicator();
							}
							
							if ( line.equalsIgnoreCase("close") ) {
								simm.closeCommunicator();
							}
						}
					}
				}
			}
			finally {
				simm.quitApplication();
			}
		}
		catch ( Throwable t ) {
			echo(t);
		}
		
		echo("Simulator finished");
	}
	
	
	private Path pwd;
	
	public CliSecsSimulator(AbstractSecsSimulatorConfig config) {
		super(config);
		this.pwd = Paths.get(".").normalize();
	}
	
	
//	private void asyncSend(SmlMessage sm) {
//		executorService().execute(() -> {
//			try {
//				send(sm);
//			}
//			catch ( InterruptedException ignore ) {
//			}
//			catch ( SecsSimulatorException e ) {
//				echo(e);
//			}
//		});
//	}
//	
//	private void asyncLinktest() {
//		executorService().execute(() -> {
//			try {
//				linktest();
//			}
//			catch ( InterruptedException ignore ) {
//			}
//		});
//	}
	
	private static final Object syncEcho = new Object();
	
	private static void echo(Object o) {
		synchronized ( syncEcho ) {
			if ( o instanceof Throwable ) {
				((Throwable) o).printStackTrace();
			} else {
				System.out.println(o);
			}
			System.out.println();
		}
	}
}
