package com.shimizukenta.secssimulator;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import com.shimizukenta.secs.SecsCommunicator;
import com.shimizukenta.secs.SecsMessage;
import com.shimizukenta.secs.sml.SmlMessage;

public interface SecsSimulator {
	
	/**
	 * Save config to path-file.
	 * 
	 * @param path
	 * @return {@code true} if save success
	 * @throws IOException
	 */
	public boolean saveConfig(Path path) throws IOException;
	
	/**
	 * Load config from path-file.
	 * 
	 * @param path
	 * @return {@code true} if load success
	 * @throws IOException
	 */
	public boolean loadConfig(Path path) throws IOException;
	
	/**
	 * Open communicator.
	 * 
	 * @return opened-communicator
	 * @throws IOException
	 */
	public SecsCommunicator openCommunicator() throws IOException;
	
	/**
	 * Close communicator.
	 * 
	 * @throws IOException
	 */
	public void closeCommunicator() throws IOException;
	
	/**
	 * Quit application.
	 * 
	 */
	public void quitApplication();
	
	/**
	 * Communicator protocol setter.
	 * 
	 * <p>
	 * Not accept {@code null}
	 * </p>
	 * 
	 * @param protocol
	 */
	public void protocol(SecsSimulatorProtocol protocol);
	
	/**
	 * Returns communicator protocol.
	 * 
	 * @return communicator-protocol
	 */
	public SecsSimulatorProtocol protocol();
	
	/**
	 * Send SML-Message and receive Reply Message.
	 * 
	 * <p>
	 * Blocking-method<br />
	 * wait until received reply-message if exist
	 * </p>
	 * 
	 * @param sml
	 * @return Reply-Message if exist
	 * @throws SecsSimulatorException
	 * @throws InterruptedException
	 */
	public Optional<SecsMessage> send(SmlMessage sml) throws SecsSimulatorException, InterruptedException;
	
	/**
	 * Send Reply-Message.
	 * 
	 * <p>
	 * Blocking-method<br />
	 * </p>
	 * 
	 * @param primaryMsg
	 * @param replySml
	 * @return {@code Optional.empty()}
	 * @throws SecsSimulatorException
	 * @throws InterruptedException
	 */
	public Optional<SecsMessage> send(SecsMessage primaryMsg, SmlMessage replySml) throws SecsSimulatorException, InterruptedException;
	
	/**
	 * Linktest.
	 * 
	 * <p>
	 * Blocking-method<br />
	 * wait until linktest.rsp
	 * </p>
	 * 
	 * @return true if linktest success
	 * @throws InterruptedException
	 */
	public boolean linktest() throws InterruptedException;
	
	
//	/**
//	 * Return sorted SML-Aliases list.
//	 * 
//	 * @return sorted SML-Aliases list
//	 */
//	public List<String> smlAliases();
//	
//	/**
//	 * Returns SML if exist, {@code Optional.empty()} otherwise.
//	 * 
//	 * @param alias
//	 * @return SmlMessage if exist
//	 */
//	public Optional<SmlMessage> sml(CharSequence alias);
	
	/**
	 * Add SML
	 * 
	 * @param alias of SML
	 * @param sml
	 * @return true if add success
	 */
	public boolean addSml(CharSequence alias, SmlMessage sml);
	
	/**
	 * Remove SML
	 * 
	 * @param alias of SML
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
	
	
	
}
