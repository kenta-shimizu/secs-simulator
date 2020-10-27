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
	 * Link-test.
	 * 
	 * <p>
	 * Blocking-method<br />
	 * wait until linktest.rsp
	 * </p>
	 * 
	 * @return true if link-test success
	 * @throws InterruptedException
	 */
	public boolean linktest() throws InterruptedException;
	
	/**
	 * Return SML-Aliases set.
	 * 
	 * @return SML-Aliases set
	 */
	public Set<String> smlAliases();
	
	/**
	 * Returns SML if exist, {@code Optional.empty()} otherwise.
	 * 
	 * @param alias
	 * @return SmlMessage if exist
	 */
	public Optional<SmlMessage> sml(CharSequence alias);
	
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
	
	
//	/**
//	 * Start Macro.
//	 * 
//	 * @param path
//	 */
//	public void startMacro(Path path);
//	
//	/**
//	 * Stop Macro.
//	 */
//	public void stopMacro();
	
	
	
}
