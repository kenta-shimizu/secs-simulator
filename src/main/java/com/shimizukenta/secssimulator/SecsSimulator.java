package com.shimizukenta.secssimulator;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;

import com.shimizukenta.secs.SecsCommunicator;
import com.shimizukenta.secs.SecsMessage;
import com.shimizukenta.secs.sml.SmlMessage;

public interface SecsSimulator {
	
	/**
	 * Open communicator
	 * 
	 * @return opened-communicator
	 * @throws IOException
	 */
	public SecsCommunicator openCommunicator() throws IOException;
	
	/**
	 * Close communicator
	 * 
	 * @throws IOException
	 */
	public void closeCommunicator() throws IOException;
	
	/**
	 * Quit program
	 */
	public void quitApplication();
	
	/**
	 * Communicator protocol setter
	 * 
	 * @param protocol
	 */
	public void protocol(SecsSimulatorProtocol protocol);
	
	/**
	 * communicator protocol getter
	 * 
	 * @return communicator-protocol
	 */
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
	
	/**
	 * Sml-Aliases getter
	 * 
	 * @return sml-aliases-set
	 */
	public Set<String> smlAliases();
	
	/**
	 * Sml  getter
	 * 
	 * @param alias
	 * @return SmlMessage if exist
	 */
	public Optional<SmlMessage> sml(CharSequence alias);
	
	/**
	 * Add Sml
	 * 
	 * @param alias
	 * @param sml
	 * @return true if add success
	 */
	public boolean addSml(CharSequence alias, SmlMessage sml);
	
	/**
	 * Remove Sml
	 * 
	 * @param alias
	 * @return true if remove success
	 */
	public boolean removeSml(CharSequence alias);
	
	/**
	 * Start logging.
	 * 
	 * @param path
	 * @throws IOException
	 */
	public void startLogging(Path path) throws IOException;
	
	/**
	 * Stop logging
	 */
	public void stopLogging();
	
	/**
	 * Start Macro.
	 * 
	 * @param path
	 */
	public void startMacro(Path path);
	
	/**
	 * Stop Macro.
	 */
	public void stopMacro();
	
	
//	public static final String SmlExtension = "sml";
//	
//	default public boolean addSmlFile(Path path) throws IOException, SmlParseException {
//		
//		try (
//				Stream<String> lines = Files.lines(path, StandardCharsets.US_ASCII);
//				) {
//			
//			String sml = lines.collect(Collectors.joining(" "));
//			SmlMessage sm = parseSml(sml);
//			
//			String alias = path.getFileName().toString();
//			
//			String ext = "." + SmlExtension;
//			
//			if ( alias.toLowerCase().endsWith(ext) ) {
//				alias = alias.substring(0, alias.length() - ext.length());
//			}
//			
//			return addSml(alias, sm);
//		}
//		catch ( SmlParseException e ) {
//			throw new SmlParseException(path.getFileName().toString(), e);
//		}
//		
//	}
//	
//	default public boolean addSmlFiles(Path directory) throws IOException, SmlParseException {
//		
//		try (
//				DirectoryStream<Path> paths = Files.newDirectoryStream(
//						directory
//						, path -> {
//							return path.toString().toLowerCase().endsWith("." + SmlExtension);
//						});
//				) {
//			
//			for ( Path path : paths ) {
//				if ( ! addSmlFile(path) ) {
//					return false;
//				}
//			}
//			
//			return true;
//		}
//	}
	
	
	
}
