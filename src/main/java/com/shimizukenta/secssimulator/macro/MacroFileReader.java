package com.shimizukenta.secssimulator.macro;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class MacroFileReader {

	private MacroFileReader() {
		/* Nothing */
	}
	
	private static class SingletonHolder {
		private static final MacroFileReader inst = new MacroFileReader();
	}
	
	public static MacroFileReader getInstance() {
		return SingletonHolder.inst;
	}
	
	public List<MacroRequest> lines(Path path) throws IOException {
		
		//TODO
		
		return Collections.emptyList();
	}

}
