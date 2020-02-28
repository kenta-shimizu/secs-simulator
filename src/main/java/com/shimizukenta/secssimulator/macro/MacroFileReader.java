package com.shimizukenta.secssimulator.macro;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MacroFileReader {
	
	public static final String[] commentOuts = new String[]{"#", "//"};
	
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
		
		final List<MacroRequest> ll = new ArrayList<>();
		
		try (
				BufferedReader br = Files.newBufferedReader(path, StandardCharsets.US_ASCII);
				) {
			
			LOOP:
			for (int lineNumber = 1; ; ++lineNumber) {
				
				String line = br.readLine();
				
				if ( line == null ) {
					break;
				}
				
				line = line.trim();
				
				if ( line.isEmpty() ) {
					continue;
				}
				
				for ( String c : commentOuts ) {
					if ( line.startsWith(c) ) {
						continue LOOP;
					}
				}
				
				ll.add(MacroCommand.getRequest(line, lineNumber));
			}
		}
		
		return ll;
	}

}
