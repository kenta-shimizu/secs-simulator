package com.shimizukenta.secssimulator.macro;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.shimizukenta.jsonhub.JsonHub;
import com.shimizukenta.jsonhub.JsonHubParseException;
import com.shimizukenta.secssimulator.AbstractSecsSimulator;

public class MacroTaskBuilder {

	protected MacroTaskBuilder() {
		/* Nothing */
	}
	
	private static class SingletonHolder {
		private static final MacroTaskBuilder inst = new MacroTaskBuilder();
	}
	
	public static MacroTaskBuilder getInstance() {
		return SingletonHolder.inst;
	}
	
	public List<MacroTask> build(Path path) throws MacroRecipeParseException, IOException {
		
		try {
			JsonHub jh = JsonHub.fromFile(path);
			
			List<MacroTask> tasks = new ArrayList<>();
			
			for ( JsonHub jht : jh.getOrDefault("tasks") ) {
				
				String s = jht.getOrDefault("command").optionalString().orElse(null);
				
				if ( s == null ) {
					throw new MacroRecipeParseException("\"command\" not setted");
				}
				
				MacroCommand cmd = MacroCommand.get(s);
				
				switch ( cmd ) {
				case OPEN: {
					tasks.add(buildOpen(jht));
					break;
				}
				case CLOSE: {
					tasks.add(buildClose(jht));
					break;
				}
				case SEND_SML_ALIAS: {
					tasks.add(buildSendSmlAlias(jht));
					break;
				}
				case SEND_SML_DIRECT: {
					tasks.add(buildSendSmlDirect(jht));
					break;
				}
				case WAIT_SxFy: {
					tasks.add(buildWaitSxFy(jht));
					break;
				}
				case SLEEP: {
					tasks.add(buildSleep(jht));
					break;
				}
				default: {
					throw new MacroRecipeParseException("\"" + s + "\" not undefined");
				}
				}
			}
			
			return tasks;
		}
		catch ( JsonHubParseException e ) {
			throw new MacroRecipeParseException(e);
		}
	}
	
	protected MacroTask buildOpen(JsonHub jh) throws MacroRecipeParseException {
		return (simm, lastRecvSxFy) -> {
			simm.openCommunicator();
			simm.waitUntilCommunicatable();
		};
	}
	
	protected MacroTask buildClose(JsonHub jh) throws MacroRecipeParseException {
		return (simm, lastRecvSxFy) -> {
			simm.closeCommunicator();
		};
	}
	
	protected MacroTask buildSendSmlAlias(JsonHub jh) throws MacroRecipeParseException {
		return (simm, lastRecvSxFy) -> {
			
			//TODO
		};
	}
	
	protected MacroTask buildSendSmlDirect(JsonHub jh) throws MacroRecipeParseException {
		return (simm, lastRecvSxFy) -> {
			
			//TODO
		};
	}
	
	protected MacroTask buildWaitSxFy(JsonHub jh) throws MacroRecipeParseException {
		return (simm, lastRecvSxFy) -> {
			
			//TODO
		};
	}
	
	protected MacroTask buildSleep(JsonHub jh) throws MacroRecipeParseException {
		return (simm, lastRecvSxFy) -> {
			
			//TODO
		};
	}
}
