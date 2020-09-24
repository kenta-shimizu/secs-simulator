package com.shimizukenta.secssimulator;

import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.shimizukenta.secs.SecsCommunicator;
import com.shimizukenta.secs.SecsMessage;
import com.shimizukenta.secs.sml.SmlMessage;
import com.shimizukenta.secs.sml.SmlParseException;

public interface SecsSimulator extends Closeable {
	
	public void open() throws IOException;
	
	/**
	 * is open.
	 * 
	 * @return true if open.
	 */
	public boolean isOpen();
	
	/**
	 * is closed.
	 * 
	 * @return true if closed.
	 */
	public boolean isClosed();
	
	public SecsCommunicator openCommunicator() throws IOException;
	public void closeCommunicator() throws IOException;
	public void quitApplication();
	
	public void protocol(SecsSimulatorProtocol protocol);
	public SecsSimulatorProtocol protocol();
	
	/**
	 * Blocking-mechod<br />
	 * wait until reply-message if exist
	 * 
	 * @param sml
	 * @return 
	 * @throws SecsSimulatorException
	 * @throws InterruptedException
	 */
	public Optional<SecsMessage> send(SmlMessage sml) throws SecsSimulatorException, InterruptedException;
	
	/**
	 * Blocking-method<br />
	 * 
	 * @param primaryMsg
	 * @param replySml
	 * @return Optional.empty()
	 * @throws SecsSimulatorException
	 * @throws InterruptedException
	 */
	public Optional<SecsMessage> send(SecsMessage primaryMsg, SmlMessage replySml) throws SecsSimulatorException, InterruptedException;
	
	/**
	 * Blocking-method<br />
	 * wait until linktest.rsp
	 * 
	 * @return true if success
	 * @throws InterruptedException
	 */
	public boolean linktest() throws InterruptedException;
	
	
	/* SMLs */
	public Set<String> smlAliases();
	public List<String> sortedSmlAliases();
	public Optional<SmlMessage> sml(CharSequence alias);
	
	public boolean addSml(CharSequence alias, SmlMessage sml);
	public boolean removeSml(CharSequence alias);
	
	public boolean addSmlAliasListChangedListener(SmlAliasListChangeListener l);
	public boolean removeSmlAliasListChangedListener(SmlAliasListChangeListener l);
	
	
	
	public static final String SmlExtension = "sml";
	
	default public boolean addSmlFile(Path path) throws IOException, SmlParseException {
		
		try (
				Stream<String> lines = Files.lines(path, StandardCharsets.US_ASCII);
				) {
			
			String sml = lines.collect(Collectors.joining(" "));
			SmlMessage sm = parseSml(sml);
			
			String alias = path.getFileName().toString();
			
			String ext = "." + SmlExtension;
			
			if ( alias.toLowerCase().endsWith(ext) ) {
				alias = alias.substring(0, alias.length() - ext.length());
			}
			
			return addSml(alias, sm);
		}
		catch ( SmlParseException e ) {
			throw new SmlParseException(path.getFileName().toString(), e);
		}
		
	}
	
	default public boolean addSmlFiles(Path directory) throws IOException, SmlParseException {
		
		try (
				DirectoryStream<Path> paths = Files.newDirectoryStream(
						directory
						, path -> {
							return path.toString().toLowerCase().endsWith("." + SmlExtension);
						});
				) {
			
			for ( Path path : paths ) {
				if ( ! addSmlFile(path) ) {
					return false;
				}
			}
			
			return true;
		}
	}
	
	
	/* Logging */
	public void startLogging(Path path) throws IOException;
	public void stopLogging();
	
	
	/* Macros */
	public void startMacro(Path path);
	public void stopMacro();
	
}
