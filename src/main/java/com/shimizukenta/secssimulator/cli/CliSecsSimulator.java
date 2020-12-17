package com.shimizukenta.secssimulator.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.shimizukenta.secs.sml.SmlMessage;
import com.shimizukenta.secs.sml.SmlParseException;
import com.shimizukenta.secssimulator.AbstractSecsSimulator;
import com.shimizukenta.secssimulator.AbstractSecsSimulatorConfig;
import com.shimizukenta.secssimulator.SecsSimulatorException;
import com.shimizukenta.secssimulator.SecsSimulatorProtocol;
import com.shimizukenta.secssimulator.SmlAliasPair;

public class CliSecsSimulator extends AbstractSecsSimulator {
	
	private final ExecutorService execServ = Executors.newCachedThreadPool(r -> {
		Thread th = new Thread(r);
		th.setDaemon(true);
		return th;
	});
	
	private final AbstractSecsSimulatorConfig config;
	private Path pwd;
	
	private CliSecsSimulator(AbstractSecsSimulatorConfig config) {
		super(config);
		this.config = config;
		this.pwd = Paths.get(".").toAbsolutePath().normalize();
	}
	
	@Override
	public void quitApplication() throws IOException {
		
		IOException ioExcept = null;
		
		try {
			execServ.shutdown();
			if ( ! execServ.awaitTermination(1L, TimeUnit.MILLISECONDS) ) {
				execServ.shutdownNow();
				if ( ! execServ.awaitTermination(10L, TimeUnit.SECONDS) ) {
					ioExcept = new IOException("ExecutorService#shutdown failed");
				}
			}
		}
		catch ( InterruptedException giveup ) {
		}
		
		try {
			super.quitApplication();
		}
		catch ( IOException e ) {
			ioExcept = e;
		}
		
		if ( ioExcept != null ) {
			throw ioExcept;
		}
	}
	
	private Optional<Path> loadConfig(String v) {
		synchronized ( this ) {
			try {
				Path p = this.pwd.resolve(v);
				if ( this.loadConfig(p) ) {
					return Optional.of(p.normalize());
				}
			}
			catch ( IOException | InvalidPathException e ) {
			}
			return Optional.empty();
		}
	}
	
	private Optional<Path> saveConfig(String v) {
		synchronized ( this ) {
			try {
				Path p = this.pwd.resolve(v);
				if ( this.saveConfig(p) ) {
					return Optional.of(p.normalize());
				}
			}
			catch ( IOException | InvalidPathException e ) {
			}
			return Optional.empty();
		}
	}
	
	private List<String> status() {
		synchronized ( this ) {
			return Arrays.asList(
					"Protocol: " + config.protocol().get(),
					"SocketAddress: " + config.hsmsSsCommunicatorConfig().socketAddress().get(),
					"Device-ID: " + config.hsmsSsCommunicatorConfig().deviceId().intValue(),
					"IS-EQUIP: " + config.hsmsSsCommunicatorConfig().isEquip().booleanValue(),
					"IS-MASTER: " + config.secs1OnTcpIpCommunicatorConfig().isMaster().booleanValue(),
					"Auto-Reply: " + config.autoReply().booleanValue(),
					"Auto-Reply-S9Fy: " + config.autoReplyS9Fy().booleanValue(),
					"Auto-Reply-SxF0: " + config.autoReplySxF0().booleanValue(),
					"Logging: " + this.loggingProperty().get()
					);
		}
	}
	
	private Optional<String> addSml(String v) {
		synchronized ( this ) {
			try {
				SmlAliasPair pair = SmlAliasPair.fromFile(this.pwd.resolve(v));
				if ( config.smlAliasPairPool().add(pair) ) {
					return Optional.of(pair.alias());
				}
			}
			catch ( InvalidPathException | IOException | SmlParseException giveup ) {
			}
			return Optional.empty();
		}
	}
	
	private Path presentWorkingDirectory() {
		synchronized ( this ) {
			return this.pwd;
		}
	}
	
	private Path changeWorkingDirectory(String path) {
		synchronized ( this ) {
			try {
				Path p = this.pwd.resolve(path);
				if ( Files.isDirectory(p) ) {
					this.pwd = p.normalize();
				}
			}
			catch ( InvalidPathException ignore ) {
			}
			return this.pwd;
		}
	}
	
	private List<Path> listDirectory(String path) {
		synchronized ( this ) {
			try {
				Path p = this.pwd.resolve(path);
				try (
						Stream<Path> pp = Files.list(p);
						) {
					return pp.map(Path::getFileName)
							.collect(Collectors.toList());
				}
			}
			catch ( IOException | InvalidPathException giveup ) {
			}
			return Collections.emptyList();
		}
	}
	
	private Optional<Path> makeDirectory(String dir) {
		synchronized ( this ) {
			try {
				Path p = Paths.get(dir);
				Path newDir = this.pwd.resolve(p);
				Path result = Files.createDirectories(newDir);
				return Optional.of(result.normalize());
			}
			catch ( IOException | InvalidPathException giveup ) {
			}
			return Optional.empty();
		}
	}
	
	private void asyncSend(SmlMessage sm) {
		execServ.execute(() -> {
			try {
				send(sm);
			}
			catch ( SecsSimulatorException | InterruptedException idgnore ) {
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
	
	private Optional<Path> startLogging(String v) {
		synchronized ( this ) {
			try {
				return this.startLogging(this.pwd.resolve(v));
			}
			catch ( InvalidPathException giveup ) {
			}
			return Optional.empty();
		}
	}
	
	@Override
	public Optional<Path> startLogging(Path path) {
		synchronized ( this ) {
			try {
				return super.startLogging(path);
			}
			catch ( IOException | InterruptedException giveup ) {
			}
			return Optional.empty();
		}
	}
	
	@Override
	public Optional<Path> stopLogging() {
		try {
			return super.stopLogging();
		}
		catch (IOException | InterruptedException giveup) {
		}
		return Optional.empty();
	}
	
	private void autoReply(String v) {
		if ( v == null ) {
			config.autoReply().set(! config.autoReply().booleanValue());
		} else {
			config.autoReply().set(Boolean.parseBoolean(v.trim()));
		}
	}
	
	private void autoReplyS9Fy(String v) {
		if ( v == null ) {
			config.autoReplyS9Fy().set(! config.autoReplyS9Fy().booleanValue());
		} else {
			config.autoReplyS9Fy().set(Boolean.parseBoolean(v.trim()));
		}
	}
	
	private void autoReplySxF0(String v) {
		if ( v == null ) {
			config.autoReplySxF0().set(! config.autoReplySxF0().booleanValue());
		} else {
			config.autoReplySxF0().set(Boolean.parseBoolean(v.trim()));
		}
	}
	
	public static void main(String[] args) {
		
		try {
			final AbstractSecsSimulatorConfig config = new AbstractSecsSimulatorConfig() {
				private static final long serialVersionUID = 5301436919812579702L;
			};
			
			boolean configLoaded = false;
			
			{
				final Map<String, List<String>> map = new HashMap<>();
				
				for ( int i = 0, m = args.length; i < m; i += 2 ) {
					map.computeIfAbsent(args[i], k -> new ArrayList<>()).add(args[i + 1]);
				}
				
				for ( String v : map.getOrDefault("--config", Collections.emptyList()) ) {
					if ( config.load(Paths.get(v)) ) {
						configLoaded = true;
					}
				}
				
				for ( String v : map.getOrDefault("--auto-open", Collections.emptyList()) ) {
					config.autoOpen().set(Boolean.parseBoolean(v));
				}
				
				for ( String v : map.getOrDefault("--auto-logging", Collections.emptyList()) ) {
					config.autoLogging(Paths.get(v));
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
							enterConfig(br, config);
						}
						
						echo("Simulator started");
						
						
						simm.config.autoLogging().ifPresent(v -> {
							simm.startLogging(v).ifPresent(path -> {
								echo("Logging start: " + path);
							});
						});
						
						if ( simm.config.autoOpen().booleanValue() ) {
							try {
								simm.openCommunicator();
							}
							catch ( IOException e ) {
								echo(e);
							}
						}
						
						LOOP:
						for ( ;; ) {
							
							String line = br.readLine();
							
							if ( line == null ) {
								break;
							}
							
							CliRequest req = CliRequest.get(line);
							
							switch ( req.command() ) {
							case MANUAL: {
								String v = req.option(0).orElse(null);
								if ( v == null ) {
									echoManual();
								} else {
									echoManual(v);
								}
								break;
							}
							case QUIT: {
								break LOOP;
							}
							case OPEN: {
								try {
									simm.openCommunicator();
								}
								catch ( IOException e ) {
									echo(e);
								}
								break;
							}
							case CLOSE: {
								try {
									simm.closeCommunicator();
								}
								catch ( IOException e ) {
									echo(e);
								}
								break;
							}
							case LOAD: {
								req.option(0).ifPresent(v -> {
									simm.loadConfig(v).ifPresent(sp -> {
										echo("loaded: " + sp);
										try {
											simm.closeCommunicator();
										}
										catch ( IOException e ) {
											echo(e);
										}
									});
								});
								break;
							}
							case SAVE: {
								req.option(0).ifPresent(v -> {
									simm.saveConfig(v).ifPresent(sp -> {
										echo("saved: " + sp);
									});
								});
								break;
							}
							case STATUS: {
								echo(simm.status());
								break;
							}
							case LIST_SML: {
								echo(simm.smlAliases());
								break;
							}
							case SHOW_SML: {
								req.option(0).ifPresent(alias -> {
									simm.optionalAlias(alias).ifPresent(sm -> {
										echo(sm);
									});
								});
								break;
							}
							case ADD_SML: {
								req.option(0).ifPresent(v -> {
									simm.addSml(v).ifPresent(r -> {
										echo("Add-SML: " + r);
									});
								});
								break;
							}
							case REMOVE_SML: {
								req.option(0).ifPresent(alias -> {
									if ( simm.removeSml(alias) ) {
										echo("Remove-SML: " + alias);
									}
								});
								break;
							}
							case SEND_SML: {
								req.option(0).ifPresent(v -> {
									simm.optionalAlias(v).ifPresent(sm -> {
										simm.asyncSend(sm);
									});
								});
								break;
							}
							case LINKTEST: {
								simm.asyncLinktest();
								break;
							}
							case PWD: {
								echo("PWD: " + simm.presentWorkingDirectory());
								break;
							}
							case CD: {
								req.option(0).ifPresent(path -> {
									echo("PWD: " + simm.changeWorkingDirectory(path));
								});
								break;
							}
							case LS: {
								echo(simm.listDirectory(req.option(0).orElse(".")));
								break;
							}
							case MKDIR: {
								req.option(0).ifPresent(dir -> {
									simm.makeDirectory(dir).ifPresent(newDir -> {
										echo("MKDIR: " + newDir);
									});
								});
								break;
							}
							case LOG: {
								String v = req.option(0).orElse(null);
								if ( v == null ) {
									simm.stopLogging().ifPresent(path -> {
										echo("Logging stop: " + path);
									});
								} else {
									simm.startLogging(v).ifPresent(path -> {
										echo("Logging start: " + path);
									});
								}
								break;
							}
							case MACRO: {
								
								//TODO
								
								break;
							}
							case AUTO_REPLY: {
								simm.autoReply(req.option(0).orElse(null));
								break;
							}
							case AUTO_REPLY_S9Fy: {
								simm.autoReplyS9Fy(req.option(0).orElse(null));
								break;
							}
							case AUTO_REPLY_SxF0: {
								simm.autoReplySxF0(req.option(0).orElse(null));
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
			finally {
				
				try {
					simm.quitApplication();
				}
				catch (IOException e ) {
					echo(e);
				}
			}
		}
		catch ( Throwable t ) {
			echo(t);
		}
		
		echo("Simulator finished");
	}
	
	private static void enterConfig(BufferedReader br, AbstractSecsSimulatorConfig config) throws IOException {
		
		for ( ;; ) {
			if ( enterProtocolConfig(br, config) ) {
				break;
			}
		}
		
		for ( ;; ) {
			if ( enterSocketAddressConfig(br, config) ) {
				break;
			}
		}
		
		for ( ;; ) {
			if ( enterDeviceIdConfig(br, config) ) {
				break;
			}
		}
		
		for ( ;; ) {
			if ( enterIsEquipConfig(br, config) ) {
				break;
			}
		}
		
		{
			SecsSimulatorProtocol p = config.protocol().get();
			if ( p == SecsSimulatorProtocol.SECS1_ON_TCP_IP
					|| p == SecsSimulatorProtocol.SECS1_ON_TCP_IP_RECEIVER) {
				
				for ( ;; ) {
					if ( enterIsMasterConfig(br, config) ) {
						break;
					}
				}
			}
		}
	}
	
	private static boolean enterProtocolConfig(BufferedReader br, AbstractSecsSimulatorConfig config) throws IOException {
		
		System.out.println("Choose protocol");
		System.out.println("(1: HSMS-SS-PASSIVE, 2: HSMS-SS-ACTIVE");
		System.out.println(" 3: SECS1-ON-TCP/IP, 4: SECS1-ON-TCP/IP-RECEIVER)");
		System.out.print(": ");
		
		String v = br.readLine().trim();
		
		if ( v.equals("1") ) {
			config.protocol(SecsSimulatorProtocol.HSMS_SS_PASSIVE);
			return true;
		} else if ( v.equals("2") ) {
			config.protocol(SecsSimulatorProtocol.HSMS_SS_ACTIVE);
			return true;
		} else if ( v.equals("3") ) {
			config.protocol(SecsSimulatorProtocol.SECS1_ON_TCP_IP);
			return true;
		} else if ( v.equals("4") ) {
			config.protocol(SecsSimulatorProtocol.SECS1_ON_TCP_IP_RECEIVER);
			return true;
		} else {
			return false;
		}
	}
	
	private static boolean enterSocketAddressConfig(BufferedReader br, AbstractSecsSimulatorConfig config) throws IOException {
		try {
			System.out.println("Enter SocketAddress (Format is \"aaa.bbb.ccc.ddd:nnnnn\")");
			System.out.print(": ");
			
			String v = br.readLine().trim();
			
			config.socketAddress(v);
			return true;
		}
		catch ( RuntimeException giveup ) {
		}
		return false;
	}
	
	private static boolean enterDeviceIdConfig(BufferedReader br, AbstractSecsSimulatorConfig config) throws IOException {
		try {
			System.out.print("Enter Device-ID: ");
			
			String v = br.readLine().trim();
			
			int id = Integer.parseInt(v);
			if ( id > 0 && id < 65536 ) {
				config.deviceId(id);
				return true;
			}
		}
		catch ( NumberFormatException giveup ) {
		}
		return false;
	}
	
	private static boolean enterIsEquipConfig(BufferedReader br, AbstractSecsSimulatorConfig config) throws IOException {
		System.out.print("Enter (1: Equip, 2: Host): ");
		
		String v = br.readLine().trim();
		
		if ( v.equals("1") ) {
			config.isEquip(true);
			return true;
		} else if ( v.equals("2") ) {
			config.isEquip(false);
			return true;
		} else {
			return false;
		}
	}
	
	private static boolean enterIsMasterConfig(BufferedReader br, AbstractSecsSimulatorConfig config) throws IOException {
		
		System.out.print("Enter is-Master ? (1: yes, 2: no): ");
		
		String v = br.readLine().trim();
		
		if ( v.equals("1") ) {
			config.isMaster(true);
			return true;
		} else if ( v.equals("2") ) {
			config.isMaster(false);
			return true;
		} else {
			return false;
		}
	}
	
	private static void echoManual() {
		List<String> ss = Stream.of(CliCommand.values())
				.filter(c -> Objects.nonNull(c.manual()))
				.filter(c -> c.commands().length > 0)
				.map(c -> {
					return commandArrayString(c) + " " + c.manual().description();
				})
				.collect(Collectors.toList());
		echo(ss);
	}
	
	private static void echoManual(String v) {
		CliCommand c = CliCommand.get(v);
		CliCommandManual m = CliCommand.get(v).manual();
		if ( m != null ) {
			List<String> ss = new ArrayList<>();
			ss.add(m.description());
			ss.add(commandArrayString(c));
			for ( String s : m.details() ) {
				ss.add(s);
			}
			echo(ss);
		}
	}
	
	private static String commandArrayString(CliCommand c) {
		return Stream.of(c.commands())
				.collect(Collectors.joining("\", \"", "[ \"", "\" ]"));
	}
	
	private static final Object syncEcho = new Object();
	
	private static void echo(Object o) {
		synchronized ( syncEcho ) {
			if ( o instanceof Throwable ) {
				((Throwable) o).printStackTrace();
			} else {
				System.out.println(o);
			}
		}
	}
	
	private static final String BR = System.lineSeparator();
	
	private static void echo(List<? extends Object> oo) {
		echo(oo.stream().map(Object::toString).collect(Collectors.joining(BR)));
	}
	
}
