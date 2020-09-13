package com.shimizukenta.secssimulator.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.shimizukenta.secs.sml.SmlMessage;
import com.shimizukenta.secs.sml.SmlParseException;
import com.shimizukenta.secssimulator.AbstractSecsSimulator;
import com.shimizukenta.secssimulator.BooleanProperty;
import com.shimizukenta.secssimulator.SecsSimulatorException;
import com.shimizukenta.secssimulator.SecsSimulatorSmlEntryFailedException;

public class CliSecsSimulator extends AbstractSecsSimulator {
	
	public static void main(String[] args) {
		
		try {
			CliSecsSimulatorConfig config = CliSecsSimulatorConfig.get(args);
			
			try (
					CliSecsSimulator simm = new CliSecsSimulator(config);
					) {
				
				simm.open();
				
				echo("Simulator started");
				
				try (
						InputStreamReader isr = new InputStreamReader(System.in);
						) {
					
					try (
							BufferedReader br = new BufferedReader(isr);
							) {
						
						for ( ;; ) {
							
							String line = br.readLine();
							
							if ( line == null ) {
								break;
							}
							
							if ( ! line.trim().isEmpty() ) {
								CliRequest request = CliCommand.getRequest(line);
								if ( simm.request(request) ) {
									break;
								}
							}
						}
					}
				}
			}
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
	public void open() throws IOException {
		
		super.open();
		
		this.addSecsLogListener(log -> {
			echo(toSimpleThrowableLog(log));
		});
		
		this.addSecsLogListener(log -> {
			notifyLog(toSimpleThrowableLog(log));
		});
		
		this.addMacroReportListener(r -> {echo(r);});
		
		try {
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
		}
		catch ( SecsSimulatorException e ) {
			echo(e);
		}
		
		try {
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
		}
		catch ( SecsSimulatorException e ) {
			echo(e);
		}
		
		
		final BooleanProperty started = new BooleanProperty(false);
		
		config.autoReply().addChangedListener(f -> {
			if ( started.booleanValue() ) {
				echo("auto-reply: " + f);
			}
		});
		
		config.autoReplySxF0().addChangedListener(f -> {
			if ( started.booleanValue() ) {
				echo("auto-reply-SxF0: " + f);
			}
		});
		
		config.autoReplyS9Fy().addChangedListener(f -> {
			if ( started.booleanValue() ) {
				echo("auto-reply-S9Fy: " + f);
			}
		});
		
		started.set(Boolean.TRUE);
		
		
		config.logging().ifPresent(path -> {
			try {
				this.startLogging(path);
			}
			catch ( IOException e ) {
				notifyLog(e);
			}
		});
		
		if ( config.autoOpen().booleanValue() ) {
			openCommunicator();
		}
		
	}
	
	/**
	 * 
	 * @param request
	 * @return true if quit
	 */
	public boolean request(CliRequest request) {
		
		try {
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
				return true;
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
				
				boolean f = request.option(0).map(Boolean::parseBoolean).orElse(! config.autoReply().get());
				config.autoReply().set(f);
				break;
			}
			case AUTO_REPLY_S9Fy: {
				
				boolean f = request.option(0).map(Boolean::parseBoolean).orElse(! config.autoReplyS9Fy().get());
				config.autoReplyS9Fy().set(f);
				break;
			}
			case AUTO_REPLY_SxF0: {
				
				boolean f = request.option(0).map(Boolean::parseBoolean).orElse(! config.autoReplySxF0().get());
				config.autoReplySxF0().set(f);
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
		
		return false;
	}
	
	
	private void asyncSend(SmlMessage sm) {
		executorService().execute(() -> {
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
		executorService().execute(() -> {
			try {
				linktest();
			}
			catch ( InterruptedException ignore ) {
			}
		});
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
