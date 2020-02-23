package com.shimizukenta.secssimulator.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.shimizukenta.secs.SecsCommunicator;
import com.shimizukenta.secs.sml.SmlMessage;
import com.shimizukenta.secs.sml.SmlParseException;
import com.shimizukenta.secssimulator.AbstractSecsSimulator;
import com.shimizukenta.secssimulator.SecsSimulatorException;
import com.shimizukenta.secssimulator.SecsSimulatorSmlEntryFailedException;

public class CliSecsSimulator extends AbstractSecsSimulator implements Runnable {
	
	public static void main(String[] args) {
		
		try {
			CliSecsSimulatorConfig config = CliSecsSimulatorConfig.get(args);
			
			echo("Simulator started.");
			
			new CliSecsSimulator(config).run();
		}
		catch ( Throwable t ) {
			echo(t);
		}
		
		echo("Simulator finished");
	}
	
	private final CliSecsSimulatorConfig config;
	private Path pwd;
	
	public CliSecsSimulator(CliSecsSimulatorConfig config) {
		super(config);
		this.config = config;
		this.pwd = Paths.get(".").normalize();
	}
	
	@Override
	public void run() {
		
		try {
			
			this.addSecsLogListener(log -> {echo(log);});
			this.addSecsLogListener(log -> {
				try {
					putLog(log);
				}
				catch ( InterruptedException ignore ) {
				}
			});
			
			for ( Path path : config.smlFiles() ) {
				try {
					if ( ! addSmlFile(path) ) {
						throw new SecsSimulatorSmlEntryFailedException(path);
					}
				}
				catch ( SmlParseException e ) {
					throw new SecsSimulatorSmlEntryFailedException(path, e);
				}
			}
			
			for ( Path path : config.smlDirectories() ) {
				try {
					if ( ! addSmlFiles(path) )  {
						throw new SecsSimulatorSmlEntryFailedException(path);
					}
				}
				catch ( SmlParseException e ) {
					throw new SecsSimulatorSmlEntryFailedException(path, e);
				}
			}
			
			config.logging().ifPresent(this::startLogging);
			
			if ( config.autoOpen() ) {
				openCommunicator();
			}
			
			try (
					InputStreamReader isr = new InputStreamReader(System.in);
					) {
				
				try (
						BufferedReader br = new BufferedReader(isr);
						) {
					
					for ( ;; ) {
						
						try {
							
							CliRequest request = CliCommand.getRequest(br.readLine());
							
							switch ( request.command() ) {
							case QUIT: {
								echo("Quiting...");
								return;
								/* break; */
							}
							case OPEN: {
								openCommunicator();
								break;
							}
							case CLOSE: {
								closeCommunicator();
								echo("Closed-communicator");
								break;
							}
							case PWD: {
								synchronized ( this ) {
									echo("PWD: " + this.pwd.toAbsolutePath());
								}
								break;
							}
							case CD: {
								synchronized ( this ) {
									request.option(0)
									.map(Paths::get)
									.ifPresent(path -> {
										this.pwd = pwd.resolve(path).normalize();
									});
								}
								break;
							}
							case LS: {
								
								synchronized ( this ) {
									
									try (
											DirectoryStream<Path> paths = Files.newDirectoryStream(pwd);
											) {
										
										StringBuilder sb = new StringBuilder();
										
										for ( Path p : paths ) {
											sb.append(p.normalize().toAbsolutePath())
											.append(System.lineSeparator());
										}
										
										echo(sb);
									}
								}
								break;
							}
							case LIST_SML: {
								
								Set<String> smls = smlAliases();
								
								if ( smls.isEmpty() ) {
									
									echo("SMLs not entry");
									
								} else {
									
									String s = smls.stream()
											.sorted()
											.collect(Collectors.joining(System.lineSeparator()));
									echo(s);
								}
								break;
							}
							case SHOW_SML: {
								
								Optional<String> alias = request.option(0);
								String s = alias
										.flatMap(this::sml)
										.map(x -> x.toString())
										.orElse("");
								
								if ( s.isEmpty() ) {
									
									echo("SML-Alias not found \"" + alias.orElse("") + "\"");
									
								} else {
									
									echo(s);
								}
								break;
							}
							case SEND_SML: {
								
								Optional<String> alias = request.option(0);
								SmlMessage sm = alias.flatMap(this::sml).orElse(null);
								
								if ( sm == null ) {
									
									echo("SML-Alias not found \"" + alias.orElse("") + "\"");
									
								} else {
									
									send(sm);
								}
								break;
							}
							case SEND_DIRECT: {
								
								String s = request.option(0).orElse(null);
								
								if ( s != null ) {
									
									try {
										SmlMessage sm = parseSml(s);
										send(sm);
									}
									catch (SmlParseException e) {
										echo(e);
									}
								}
								break;
							}
							case LINKTEST: {
								
								this.linktest();
								break;
							}
							case LOG: {
								
								Path path = request.option(0).map(pwd::resolve).orElse(null);
								
								if ( path == null ) {
									stopLogging();
									echo("Stop-Logging.");
								} else {
									startLogging(path);
								}
								break;
							}
							case MACRO: {
								
								Path path = request.option(0).map(pwd::resolve).orElse(null);
								
								if ( path == null ) {
									stopMacro();
									echo("Stop-Macro.");
								} else {
									startMacro(path);
								}
								break;
							}
							default: {
								/* Nothing */
							}
							}
						}
						catch ( SecsSimulatorException e ) {
							echo(e);
						}
					}
				}
			}
		}
		catch ( InterruptedException ignore ) {
		}
		catch ( IOException | SecsSimulatorException e ) {
			echo(e);
		}
		finally {
			quitApplication();
		}
	}
	
	@Override
	public SecsCommunicator openCommunicator() throws IOException {
		echo("Try-open-communicator");
		return super.openCommunicator();
	}
	
	@Override
	public void startLogging(Path path) {
		try {
			super.startLogging(path);
			echo("Start-Logging: " + path.toString());
		}
		catch (IOException nothappened) {
		}
	}
	
	@Override
	public void startMacro(Path path) {
		echo("Start-Macro: " + path.toString());
		super.startMacro(path);
	}
	
	
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
