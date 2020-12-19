package com.shimizukenta.secssimulator.macro;

import com.shimizukenta.secs.ReadOnlyProperty;
import com.shimizukenta.secssimulator.AbstractSecsSimulator;

public interface MacroTask {
	public void execute(AbstractSecsSimulator simm, ReadOnlyProperty<Integer> lastRecvSxFy) throws InterruptedException, Exception;
}
