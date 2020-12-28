package com.shimizukenta.secssimulator.macro;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.shimizukenta.jsonhub.JsonHub;
import com.shimizukenta.jsonhub.JsonHubParseException;
import com.shimizukenta.secs.sml.SmlMessage;
import com.shimizukenta.secs.sml.SmlParseException;
import com.shimizukenta.secssimulator.SecsSimulatorException;
import com.shimizukenta.secssimulator.extendsml.ExtendSmlMessageParser;

/**
 * Macro-Tasks builder, Singleton-pattern.
 * 
 * @author kenta-shimizu
 *
 */
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
				
				String s = jht.getOrDefault("command").optionalString()
						.orElseThrow(() -> new MacroRecipeParseException("\"command\" not setted"));
				
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
			String msg = e.getMessage();
			if ( msg == null ) {
				throw new MacroRecipeParseException(e.getClass().getSimpleName(), e);
			} else {
				throw new MacroRecipeParseException(msg, e);
			}
		}
	}
	
	protected MacroTask buildOpen(JsonHub jh) throws MacroRecipeParseException {
		
		return new MacroTask() {

			@Override
			public void execute(AbstractMacroWorker worker)
					throws InterruptedException, Exception {
				
				worker.simulator().openCommunicator();
				worker.simulator().waitUntilCommunicatable();
			}
			
			@Override
			public String toString() {
				return "Open-Communicator";
			}
		};
	}
	
	protected MacroTask buildClose(JsonHub jh) throws MacroRecipeParseException {
		return new MacroTask() {

			@Override
			public void execute(AbstractMacroWorker worker)
					throws InterruptedException, Exception {
				
				worker.simulator().closeCommunicator();
			}
			
			@Override
			public String toString() {
				return "Close-Communicator";
			}
		};
	}
	
	protected MacroTask buildSendSmlAlias(JsonHub jh) throws MacroRecipeParseException {
		
		String op = jh.getOrDefault("option").optionalString()
				.orElseThrow(() -> new MacroRecipeParseException("option is not string"));
		
		return new MacroTask() {
			
			@Override
			public void execute(AbstractMacroWorker worker)
					throws InterruptedException, Exception {
				
				SmlMessage sml = worker.simulator().optionalSmlAlias(op)
						.orElseThrow(() -> new SecsSimulatorException("Sml-Alias \"" + op + "\"not found"));
				
				worker.simulator().send(sml);
			}
			
			@Override
			public String toString() {
				return "Send-SML-Alias \"" + op + "\"";
			}
		};
	}
	
	protected MacroTask buildSendSmlDirect(JsonHub jh) throws MacroRecipeParseException {
		
		try {
			String op = jh.getOrDefault("option").optionalString()
					.orElseThrow(() -> new MacroRecipeParseException("option is not string"));
			
			final SmlMessage sml = ExtendSmlMessageParser.getInstance().parse(op);
			
			return new MacroTask() {

				@Override
				public void execute(AbstractMacroWorker worker)
						throws InterruptedException, Exception {
					
					worker.simulator().send(sml);
				}
				
				@Override
				public String toString() {
					return "Send-SML-Direct \"S" + sml.getStream() + "F" + sml.getFunction()+ "\"";
				}
			};
		}
		catch ( SmlParseException e ) {
			String msg = e.getMessage();
			if ( msg == null ) {
				throw new MacroRecipeParseException(e.getClass().getSimpleName(), e);
			} else {
				throw new MacroRecipeParseException(msg, e);
			}
		}
	}
	
	protected static final String GROUP_STREAM = "STREAM";
	protected static final String GROUP_FUNCTION = "FUNCTION";
	protected static final String pregSxFy = "[Ss](?<" + GROUP_STREAM + ">[0-9]{1,3})[Ff](?<" + GROUP_FUNCTION + ">[0-9]{1,3})";
	
	protected static final Pattern ptnSxFy = Pattern.compile("^" + pregSxFy + "$");
	
	protected MacroTask buildWaitSxFy(JsonHub jh) throws MacroRecipeParseException {
		
		String op = jh.getOrDefault("option").optionalString()
				.orElseThrow(() -> new MacroRecipeParseException("option is not string"));
		
		Matcher m = ptnSxFy.matcher(op);
		
		if ( ! m.matches() ) {
			throw new MacroRecipeParseException("\"" + op + "\" not match SxFy");
		}
		
		final int x = Integer.parseInt(m.group(GROUP_STREAM));
		final int y = Integer.parseInt(m.group(GROUP_FUNCTION));
		
		final Integer sxfy = Integer.valueOf((x * 256) + y);
		
		final float timeout = jh.getOrDefault("timeout").optionalNubmer()
				.map(Number::floatValue)
				.orElse(-1.0F);
		
		final long ms = (long)(timeout * 1000.0F);
		
		return new MacroTask() {
			
			@Override
			public void execute(AbstractMacroWorker worker)
					throws InterruptedException, Exception {
				
				final Future<Void> future = worker.executorService().submit(() -> {
					worker.lastRecvSxFy().waitUntil(sxfy);
					return null;
				});
				
				try {
					if ( ms > 0 ) {
						future.get(ms, TimeUnit.MILLISECONDS);
					} else {
						future.get();
					}
				}
				catch ( ExecutionException e ) {
					Throwable t = e.getCause();
					
					if ( t instanceof Error ) {
						throw (Error)t;
					}
					
					if ( t instanceof Exception ) {
						throw (Exception)t;
					}
				}
			}
			
			@Override
			public String toString() {
				
				StringBuilder sb = new StringBuilder("Wait-S")
						.append(x)
						.append("F")
						.append(y);
				
				if ( ms > 0L ) {
					sb.append(", timeout ")
					.append(timeout)
					.append(" sec.");
				}
				
				return sb.toString();
			}
		};
	}
	
	protected MacroTask buildSleep(JsonHub jh) throws MacroRecipeParseException {
		
		final float v = jh.getOrDefault("timeout").optionalNubmer()
				.orElseThrow(() -> new MacroRecipeParseException("option is not number"))
				.floatValue();
		
		final long ms = (long)(v * 1000.0F);
		
		return new MacroTask() {

			@Override
			public void execute(AbstractMacroWorker worker)
					throws InterruptedException, Exception {
				
				if ( ms > 0 ) {
					TimeUnit.MILLISECONDS.sleep(ms);
				}
			}
			
			@Override
			public String toString() {
				return "Sleep " + v + " sec.";
			}
		};
	}
}
