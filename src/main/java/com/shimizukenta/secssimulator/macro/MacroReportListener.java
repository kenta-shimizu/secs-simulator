package com.shimizukenta.secssimulator.macro;

import java.util.EventListener;

public interface MacroReportListener extends EventListener {
	public void report(MacroReport report);
}
