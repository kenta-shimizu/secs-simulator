package com.shimizukenta.secssimulator.macro;

import com.shimizukenta.secssimulator.SecsSimulator;

public interface MacroTask {
	public void execute(SecsSimulator simm) throws InterruptedException, Exception;
}
