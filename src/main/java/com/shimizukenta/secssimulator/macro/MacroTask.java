package com.shimizukenta.secssimulator.macro;

public interface MacroTask {
	public void execute(AbstractMacroWorker worker) throws InterruptedException, Exception;
}
