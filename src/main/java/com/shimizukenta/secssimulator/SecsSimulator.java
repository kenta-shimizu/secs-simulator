package com.shimizukenta.secssimulator;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import com.shimizukenta.secs.SecsMessage;
import com.shimizukenta.secs.sml.SmlMessage;

public interface SecsSimulator {
	
	public void openCommunicator() throws IOException;
	public void closeCommunicator() throws IOException;
	public void quitApplication();
	
	
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
	
	
	/* SMl */
	public boolean addSml(CharSequence alias, SmlMessage sml);
	public boolean removeSml(CharSequence alias);
	
	/* SML-chnaged-listeners */
	public boolean addSmlAliasListChangedListener(SmlAliasListChangedListener l);
	public boolean removeSmlAliasListChangedListener(SmlAliasListChangedListener l);
	
	/* Logging */
	public void startLogging(Path path) throws IOException;
	public void stopLogging();
	
	
	/* Macros */
	public void startMacro(Path path);
	public void stopMacro();
	
}
