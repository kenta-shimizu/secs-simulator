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
import com.shimizukenta.secssimulator.AbstractSecsSimulatorThrowableLog;
import com.shimizukenta.secssimulator.SecsSimulatorException;
import com.shimizukenta.secssimulator.SecsSimulatorProtocol;
import com.shimizukenta.secssimulator.macro.AbstractSecsSimulatorMacroWorkerLog;
import com.shimizukenta.secssimulator.macro.MacroRecipe;
import com.shimizukenta.secssimulator.macro.MacroRecipeParseException;
import com.shimizukenta.secssimulator.macro.MacroWorker;

public class CliSecsSimulator extends AbstractSecsSimulator {
	
	private final ExecutorService execServ = Executors.newCachedThreadPool(r -> {
		Thread th = new Thread(r);
		th.setDaemon(true);
		return th;
	});
	
	private final CliSecsSimulatorConfig config;
	private Path pwd;
	
	private CliSecsSimulator(CliSecsSimulatorConfig config) {
		super(config);
		this.config = config;
		this.pwd = Paths.get(".").toAbsolutePath().normalize();
	}
	
	@Override
	public void quitApplication() {
		
		try {
			execServ.shutdown();
			if ( ! execServ.awaitTermination(1L, TimeUnit.MILLISECONDS) ) {
				execServ.shutdownNow();
				if ( ! execServ.awaitTermination(10L, TimeUnit.SECONDS) ) {
					/* giveup */
				}
			}
		}
		catch ( InterruptedException giveup ) {
		}
		
		try {
			super.quitApplication();
		}
		catch ( IOException giveup ) {
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
			
			SecsSimulatorProtocol protocol = config.protocol().get();
			
			List<String> ll = new ArrayList<>();
			
			ll.addAll(Arrays.asList(
					"Protocol: " + protocol,
					"SocketAddress: " + config.hsmsSsCommunicatorConfig().socketAddress().get(),
					"Device-ID: " + config.hsmsSsCommunicatorConfig().deviceId().intValue(),
					"IS-EQUIP: " + config.hsmsSsCommunicatorConfig().isEquip().booleanValue()
					));
			
			if ( protocol.isSecs1() ) {
				ll.add("IS-MASTER: " + config.secs1OnTcpIpCommunicatorConfig().isMaster().booleanValue());
			}
			
			ll.addAll(Arrays.asList(
					"Auto-Reply: " + config.autoReply().booleanValue(),
					"Auto-Reply-S9Fy: " + config.autoReplyS9Fy().booleanValue(),
					"Auto-Reply-SxF0: " + config.autoReplySxF0().booleanValue(),
					"Logging: " + this.loggingProperty().get()
					));
			
			return Collections.unmodifiableList(ll);
		}
	}
	
	private Optional<String> addSml(String v) {
		synchronized ( this ) {
			try {
				Path path = this.pwd.resolve(v);
				return super.addSml(path).stream().findFirst();
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
	
	private Optional<MacroWorker> startMacro(String v) {
		try {
			this.stopMacro();
			MacroRecipe r = this.optionalMacroRecipeAlias(v).orElse(null);
			if ( r != null ) {
				return super.startMacro(r);
			}
		}
		catch ( InterruptedException giveup ) {
		}
		return Optional.empty();
	}
	
	@Override
	public List<MacroWorker> stopMacro() {
		try {
			return super.stopMacro();
		}
		catch ( InterruptedException ignore ) {
		}
		return Collections.emptyList();
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
	
	private Optional<MacroRecipe> addMacroRecipe(String path) {
		try {
			Path p = this.pwd.resolve(path);
			return super.addMacroRecipe(p).stream().findFirst();
		}
		catch ( InvalidPathException | MacroRecipeParseException | IOException giveup ) {
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
			final CliSecsSimulatorConfig config = new CliSecsSimulatorConfig();
			
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
			
			simm.addLogListener(log -> {echo(log);});
			
			simm.addMacroWorkerStateChangeListener(w -> {
				
				echo(new AbstractSecsSimulatorMacroWorkerLog(w) {
					
					private static final long serialVersionUID = -3905142429402218208L;
				});
			});
			
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
									simm.optionalSmlAlias(alias).ifPresent(sm -> {
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
									simm.optionalSmlAlias(v).ifPresent(sm -> {
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
										echo("Logging-stop: " + path);
									});
								} else {
									simm.startLogging(v).ifPresent(path -> {
										echo("Logging-start: " + path);
									});
								}
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
							case MACRO: {
								String v = req.option(0).orElse(null);
								if ( v == null ) {
									simm.stopMacro();
								} else {
									simm.startMacro(v);
								}
								break;
							}
							case LIST_MACRO: {
								echo(simm.macroRecipeAliases());
								break;
							}
							case SHOW_MACRO: {
								req.option(0).ifPresent(v -> {
									simm.optionalMacroRecipeAlias(v).ifPresent(r -> {
										echo(r);
									});
								});
								break;
							}
							case ADD_MACRO: {
								req.option(0).ifPresent(path -> {
									simm.addMacroRecipe(path).ifPresent(r -> {
										echo("Add-Macro-Recipe: " + r.alias());
									});
								});
								break;
							}
							case REMOVE_MACRO: {
								req.option(0).ifPresent(path -> {
									simm.removeMacroRecipe(path).ifPresent(r -> {
										echo("Remove-Macro-Recipe: " + r.alias());
									});
								});
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
				simm.quitApplication();
			}
		}
		catch ( Throwable t ) {
			echo(t);
		}
		
		echo("Simulator finished");
	}
	
	private static interface InnerEnterConfig {
		public boolean enter(BufferedReader br, AbstractSecsSimulatorConfig config) throws IOException;
	}
	
	private static void enterConfig(BufferedReader br, AbstractSecsSimulatorConfig config) throws IOException {
		
		List<InnerEnterConfig> ll = Arrays.asList(
				CliSecsSimulator::enterProtocolConfig,
				CliSecsSimulator::enterSocketAddressConfig,
				CliSecsSimulator::enterDeviceIdConfig,
				CliSecsSimulator::enterIsEquipConfig,
				CliSecsSimulator::enterIsMasterConfig
				);
		
		for ( InnerEnterConfig x : ll ) {
			for ( ;; ) {
				if ( x.enter(br, config) ) {
					break;
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
		
		if ( config.protocol().get().isSecs1() ) {
			
			System.out.print("Enter is-Master ? (1: yes, 2: no): ");
			
			String v = br.readLine().trim();
			
			if ( v.equals("1") ) {
				config.isMaster(true);
				return true;
			} else if ( v.equals("2") ) {
				config.isMaster(false);
				return true;
			}
			
			return false;
			
		} else {
			
			return true;
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
				
				System.out.println(new AbstractSecsSimulatorThrowableLog((Throwable)o) {
					
					private static final long serialVersionUID = -3456444578844843433L;
				});
				
			} else {
				
				System.out.println(o);
			}
			
			System.out.println();
		}
	}
	
	private static final String BR = System.lineSeparator();
	
	private static void echo(List<? extends Object> oo) {
		echo(oo.stream().map(Object::toString).collect(Collectors.joining(BR)));
	}
	
}
