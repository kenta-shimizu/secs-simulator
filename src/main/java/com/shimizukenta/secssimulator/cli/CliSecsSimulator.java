package com.shimizukenta.secssimulator.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.shimizukenta.secs.sml.SmlMessage;
import com.shimizukenta.secs.sml.SmlParseException;
import com.shimizukenta.secssimulator.AbstractSecsSimulator;
import com.shimizukenta.secssimulator.SecsSimulatorException;
import com.shimizukenta.secssimulator.SecsSimulatorSmlEntryFailedException;

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
	private boolean started;
	
	public CliSecsSimulator(CliSecsSimulatorConfig config) {
		super(config);
		this.config = config;
		this.pwd = Paths.get(".").normalize();
		this.logging = false;
		this.started = false;
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
			
			this.addMacroReportListener(r -> {echo(r);});
			
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
			
			config.addAutoReplyStateChangedListener(f -> {
				if ( started ) {
					echo("auto-reply: " + f);
				}
			});
			
			config.addAutoReplySxF0StateChangedListener(f -> {
				if ( started ) {
					echo("auto-reply-SxF0: " + f);
				}
			});
			
			config.addAutoReplyS9FyStateChangedListener(f -> {
				if ( started ) {
					echo("auto-reply-S9Fy: " + f);
				}
			});
			
			started = true;
			
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
							case MANUAL: {
								
								String cmd = request.option(0).orElse("");
								
								if ( cmd.isEmpty() ) {
									
									echo(CliCommand.getManuals());
									
								} else {
									
									echo(CliCommand.getDetailManual(cmd));
								}
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
									.map(pwd::resolve)
									.map(Path::normalize)
									.ifPresent(path -> {
										this.pwd = path;
									});
								}
								break;
							}
							case LS: {
								
								synchronized ( this ) {
									
									Path path = request.option(0).map(pwd::resolve).orElse(pwd);
									
									try (
											DirectoryStream<Path> paths = Files.newDirectoryStream(path);
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
							case MKDIR: {
								
								synchronized ( this ) {
									
									Path path = request.option(0)
											.map(pwd::resolve)
											.map(Path::normalize)
											.orElse(null);
									
									if ( path != null ) {
										Files.createDirectory(path);
									}
								}
								break;
							}
							case LIST_SML: {
								
								List<String> aliases = sortedSmlAliases();
								
								if ( aliases.isEmpty() ) {
									
									echo("SMLs not entry");
									
								} else {
									
									echo(aliases.stream().collect(Collectors.joining(System.lineSeparator())));
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
							case ADD_SML: {
								
								Path path = request.option(0).map(pwd::resolve).orElse(null);
								
								if ( path == null ) {
									echo("request option path/of/file.sml");
								} else {
									if ( ! addSmlFile(path) ) {
										echo("add-sml failed. \"" + path.normalize().toString() + "\"");
									}
								}
								break;
							}
							case ADD_SMLS: {
								
								Path path = request.option(0).map(pwd::resolve).orElse(pwd);
								if ( ! addSmlFiles(path) ) {
									echo("add-smls failed. \"" + path.normalize().toString() + "\"");
								}
								break;
							}
							case REMOVE_SML: {
								
								request.option(0).ifPresent(this::removeSml);
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
									SmlMessage sm = parseSml(s);
									asyncSend(sm);
								}
								break;
							}
							case LINKTEST: {
								
								asyncLinktest();
								break;
							}
							case LOG: {
								
								String ps = request.option(0).orElse(null);
								
								if ( ps == null ) {
									stopLogging();
								} else {
									startLogging(pwd.resolve(ps));
								}
								break;
							}
							case MACRO: {
								
								String ps = request.option(0).orElse(null);
								
								if ( ps == null ) {
									stopMacro();
								} else {
									startMacro(pwd.resolve(ps));
								}
								break;
							}
							case AUTO_REPLY: {
								
								boolean f = request.option(0).map(Boolean::parseBoolean).orElse(! config.autoReply());
								config.autoReply(f);
								break;
							}
							case AUTO_REPLY_S9Fy: {
								
								boolean f = request.option(0).map(Boolean::parseBoolean).orElse(! config.autoReplyS9Fy());
								config.autoReplyS9Fy(f);
								break;
							}
							case AUTO_REPLY_SxF0: {
								
								boolean f = request.option(0).map(Boolean::parseBoolean).orElse(! config.autoReplySxF0());
								config.autoReplySxF0(f);
								break;
							}
							default: {
								/* Nothing */
							}
							}
						}
						catch (InvalidPathException | IOException | SmlParseException e ) {
							
							echo(e);
						}
					}
				}
			}
		}
		catch ( Throwable t ) {
			echo(t);
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
	public void openCommunicator() throws IOException {
		echo("Try-open-communicator");
		super.openCommunicator();
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
						try {
							synchronized ( syncLog ) {
								syncLog.wait();
							}
						}
						catch ( InterruptedException ignore ) {
						}
						return syncLog;
					});
			
			try {
				synchronized ( syncLog ) {
					this.logging = true;
				}
				
				echo("Start-Logging: " + path.toString());
				
				execServ.invokeAny(tasks);
			}
			catch ( InterruptedException ignore ) {
			}
			catch ( ExecutionException e ) {
				echo(e.getCause());
			}
			finally {
				synchronized ( syncLog ) {
					this.logging = false;
				}
			}
		});
	}
	
	@Override
	public void stopLogging() {
		synchronized ( syncLog ) {
			syncLog.notifyAll();
		}
	}
	
	@Override
	protected void putLog(Object o) throws InterruptedException {
		synchronized ( syncLog ) {
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
						try {
							synchronized ( syncMacro ) {
								syncMacro.wait();
							}
						}
						catch ( InterruptedException ignore ) {
						}
						return syncMacro;
					});
			
			try {
				echo("Start-Macro: " + path.toString());
				execServ.invokeAny(tasks);
			}
			catch ( InterruptedException ignore ) {
			}
			catch ( ExecutionException e ) {
				echo(e);
			}
		});
	}
	
	@Override
	public void stopMacro() {
		synchronized ( syncMacro ) {
			syncMacro.notifyAll();
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
