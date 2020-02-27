package com.shimizukenta.secssimulator.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.shimizukenta.secs.SecsCommunicator;
import com.shimizukenta.secs.sml.SmlMessage;
import com.shimizukenta.secs.sml.SmlParseException;
import com.shimizukenta.secssimulator.AbstractSecsSimulator;
import com.shimizukenta.secssimulator.SecsSimulatorException;
import com.shimizukenta.secssimulator.SecsSimulatorSmlEntryFailedException;
import com.shimizukenta.secssimulator.macro.MacroReport;

public class CliSecsSimulator extends AbstractSecsSimulator implements Runnable {
	
	public static void main(String[] args) {
		
		try {
			CliSecsSimulatorConfig config = CliSecsSimulatorConfig.get(args);
			
			echo("Simulator started");
			
			new CliSecsSimulator(config).run();
		}
		catch ( Throwable t ) {
			echo(t);
		}
		
		echo("Simulator finished");
	}
	
	private final ExecutorService execServ = Executors.newCachedThreadPool(r -> {
		Thread th = new Thread(r);
		th.setDaemon(true);
		return th;
	});
	
	private final CliSecsSimulatorConfig config;
	private Path pwd;
	private boolean logging;
	
	public CliSecsSimulator(CliSecsSimulatorConfig config) {
		super(config);
		this.config = config;
		this.pwd = Paths.get(".").normalize();
		this.logging = false;
	}
	
	@Override
	public void run() {
		
		try {
			
			this.addSecsLogListener(log -> {echo(toSimpleThrowableLog(log));});
			this.addSecsLogListener(log -> {
				try {
					putLog(toSimpleThrowableLog(log));
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
						
						CliRequest request = CliCommand.getRequest(br.readLine());
						
						switch ( request.command() ) {
						case MANUAL: {
							
							
							//TODO
							
							break;
						}
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
								
								asyncSend(sm);
							}
							break;
						}
						case SEND_DIRECT: {
							
							String s = request.option(0).orElse(null);
							
							if ( s != null ) {
								
								try {
									SmlMessage sm = parseSml(s);
									asyncSend(sm);
								}
								catch (SmlParseException e) {
									echo(e);
								}
							}
							break;
						}
						case LINKTEST: {
							
							asyncLinktest();
							break;
						}
						case LOG: {
							
							Path path = request.option(0).map(pwd::resolve).orElse(null);
							
							if ( path == null ) {
								stopLogging();
							} else {
								startLogging(path);
							}
							break;
						}
						case MACRO: {
							
							Path path = request.option(0).map(pwd::resolve).orElse(null);
							
							if ( path == null ) {
								stopMacro();
							} else {
								startMacro(path);
							}
							break;
						}
						case AUTO_REPLY: {
							
							boolean f = request.option(0).map(Boolean::parseBoolean).orElse(! config.autoReply());
							config.autoReply(f);
							echo("auto-reply: " + config.autoReply());
							break;
						}
						default: {
							/* Nothing */
						}
						}
					}
				}
			}
		}
		catch ( IOException | SecsSimulatorException e ) {
			echo(e);
		}
		finally {
			
			try {
				execServ.shutdown();
				if ( ! execServ.awaitTermination(1L, TimeUnit.MILLISECONDS) ) {
					execServ.shutdownNow();
					if ( ! execServ.awaitTermination(5L, TimeUnit.SECONDS) ) {
						echo("ExecutorService#shutdown failed");
					}
				}
			}
			catch ( InterruptedException giveup ) {
			}
			
			quitApplication();
		}
	}
	
	@Override
	public SecsCommunicator openCommunicator() throws IOException {
		echo("Try-open-communicator");
		return super.openCommunicator();
	}
	
	private void asyncSend(SmlMessage sm) {
		execServ.execute(() -> {
			try {
				send(sm);
			}
			catch ( InterruptedException ignore ) {
			}
			catch ( SecsSimulatorException e ) {
				echo(e);
			}
		});
	}
	
	private void asyncLinktest() {
		execServ.execute(() -> {
			try {
				linktest();
			}
			catch ( InterruptedException ignore ) {
			}
		});
	}
	
	
	private final Object syncLog = new Object();
	
	@Override
	public void startLogging(Path path) {
		
		stopLogging();
		
		logQueue.clear();

		execServ.execute(() -> {
			
			Collection<Callable<Object>> tasks = Arrays.asList(
					() -> {
						loggingTask(path).run();
						echo("Stop-Logging");
						return syncLog;
					},
					() -> {
						synchronized ( syncLog ) {
							syncLog.wait();
						}
						return syncLog;
					});
			
			try {
				synchronized ( this ) {
					logging = true;
				}
				
				execServ.invokeAny(tasks);
			}
			catch ( InterruptedException ignore ) {
			}
			catch ( ExecutionException e ) {
				echo(e.getCause());
			}
			finally {
				synchronized ( this ) {
					logging = false;
				}
			}
		});
		
		echo("Start-Logging: " + path.toString());
	}
	
	@Override
	public void stopLogging() {
		synchronized ( syncLog ) {
			syncLog.notifyAll();
		}
	}
	
	@Override
	protected void putLog(Object o) throws InterruptedException {
		synchronized ( this ) {
			if ( this.logging ) {
				logQueue.put(o);
			}
		}
	}
	
	
	private final Object syncMacro = new Object();
	
	@Override
	public void startMacro(Path path) {
		
		stopMacro();
		
		execServ.execute(() -> {
			
			Collection<Callable<Object>> tasks = Arrays.asList(
					() -> {
						macroTask(path).run();
						return syncMacro;
					},
					() -> {
						synchronized ( syncMacro ) {
							syncMacro.wait();
						}
						return syncMacro;
					});
			
			try {
				execServ.invokeAny(tasks);
			}
			catch ( InterruptedException ignore ) {
			}
			catch ( ExecutionException e ) {
				echo(e);
			}
		});
		
		echo("Start-Macro: " + path.toString());
	}
	
	@Override
	public void stopMacro() {
		synchronized ( syncMacro ) {
			syncMacro.notifyAll();
		}
	}
	
	@Override
	protected void macroReport(MacroReport result) {
		echo(result);
	}
	
	
	@Override
	public void quitApplication() {
		try {
			closeCommunicator();
		}
		catch (IOException giveup) {
		}
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
