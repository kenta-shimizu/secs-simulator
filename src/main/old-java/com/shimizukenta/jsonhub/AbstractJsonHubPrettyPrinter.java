package com.shimizukenta.jsonhub;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This abstract class is super implementation class of JsoHubPrettyPrinter.
 * 
 * @author kenta-shimizu
 *
 */
public abstract class AbstractJsonHubPrettyPrinter implements JsonHubPrettyPrinter {
	
	private JsonHubPrettyPrinterConfig config;
	
	protected AbstractJsonHubPrettyPrinter(JsonHubPrettyPrinterConfig config) {
		this.config = config;
	}
	
	private static class SingletonHolder {
		private static final AbstractJsonHubPrettyPrinter defaultPrinter = new AbstractJsonHubPrettyPrinter(JsonHubPrettyPrinterConfig.defaultConfig()) {};
	}
	
	/**
	 * Returns AbstractJsonHubPrettyPrinter default instance.
	 * 
	 * <p>
	 * This class is Singleton-pattern.
	 * </p>
	 * 
	 * @return default-pretty-printer instance
	 */
	public static AbstractJsonHubPrettyPrinter getDefaultPrinter() {
		return SingletonHolder.defaultPrinter;
	}
	
	/**
	 * Returns custom-pretty-printer instance.
	 * 
	 * @param config
	 * @return customized-pretty-printer instance
	 */
	public static AbstractJsonHubPrettyPrinter newPrinter(JsonHubPrettyPrinterConfig config) {
		return new AbstractJsonHubPrettyPrinter(config) {};
	}
	
	@Override
	public void print(JsonHub v, Writer writer) throws IOException {
		print(v, writer, 0);
	}
	
	@Override
	public void print(JsonHub v, Path path) throws IOException {
		
		try (
				BufferedWriter bw = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
				) {
			
			print(v, bw);
		}
	}
	
	@Override
	public void print(JsonHub v, Path path, OpenOption... options) throws IOException {
		
		try (
				BufferedWriter bw = Files.newBufferedWriter(path, StandardCharsets.UTF_8, options);
				) {
			
			print(v, bw);
		}
	}
	
	@Override
	public String print(JsonHub v) {
		
		synchronized ( this ) {
			
			try (
					StringWriter sw = new StringWriter();
					) {
				
				print(v, sw);
				return sw.toString();
			}
			catch ( IOException notHappen ) {
				throw new RuntimeException(notHappen);
			}
		}
	}
	
	private void print(JsonHub v, Writer writer, int level) throws IOException {
		
		switch ( v.type() ) {
		case NULL:
		case TRUE:
		case FALSE:
		case STRING:
		case NUMBER: {
			
			v.toJson(writer);
			break;
		}
		case ARRAY: {
			
			writer.write(JsonStructuralChar.ARRAY_BIGIN.str());
			
			if ( v.isEmpty() ) {
				
				writeLineSeparatorIfBlank(writer, level);
				
			} else {
				
				int deepLevel = level + 1;
				
				writeLineSeparator(writer);
				writeIndent(writer, deepLevel);
				
				boolean f = false;
				
				for (JsonHub jh : v.values()) {
					
					if ( f ) {
						writeValueSeparator(writer, deepLevel);
					} else {
						f = true;
					}
					
					print(jh, writer, deepLevel);
				}
				
				writeLineSeparator(writer);
				writeIndent(writer, level);
			}
			
			writer.write(JsonStructuralChar.ARRAY_END.str());
			
			break;
		}
		case OBJECT: {
			
			writer.write(JsonStructuralChar.OBJECT_BIGIN.str());
			
			final List<JsonObjectPair> pairs;
			
			if ( config.noneNullValueInObject() ) {
				
				pairs = ((ObjectJsonHub)v).objectPairs().stream()
						.filter(p -> p.value().nonNull())
						.collect(Collectors.toList());
				
			} else {
				
				pairs = ((ObjectJsonHub)v).objectPairs().stream()
						.collect(Collectors.toList());
			}
			
			if ( pairs.isEmpty() ) {
				
				writeLineSeparatorIfBlank(writer, level);
				
			} else {
				
				int deepLevel = level + 1;
				
				writeLineSeparator(writer);
				writeIndent(writer, deepLevel);
				
				boolean f = false;
				
				for ( JsonObjectPair pair : pairs ) {
					
					if ( f ) {
						writeValueSeparator(writer, deepLevel);
					} else {
						f = true;
					}
					
					writeObjectName(writer, pair);
					writeNameSeparator(writer);
					
					print(pair.value(), writer, deepLevel);
				}
				
				writeLineSeparator(writer);
				writeIndent(writer, level);
			}
			
			writer.write(JsonStructuralChar.OBJECT_END.str());
			
			break;
		}
		}
	}
	
	private void writeIndent(Writer writer, int level) throws IOException {
		for (int i = 0; i < level; ++i) {
			writer.write(config.indent());
		}
	}
	
	private void writeLineSeparator(Writer writer) throws IOException {
		writer.write(config.lineSeparator());
	}
	
	private void writeLineSeparatorIfBlank(Writer writer, int level) throws IOException {
		if ( config.lineSeparateIfBlank() ) {
			writeLineSeparator(writer);
			writeIndent(writer, level);
		}
	}
	
	private void writeValueSeparator(Writer writer, int level) throws IOException {
		
		if ( config.lineSeparateBeforeValueSeparator() ) {
			writeLineSeparator(writer);
			writeIndent(writer, level);
		}
		
		writer.write(config.prefixValueSeparator());
		writer.write(JsonStructuralChar.SEPARATOR_VALUE.str());
		writer.write(config.suffixValueSeparator());
		
		if ( config.lineSeparateAfterValueSeparator() ) {
			writeLineSeparator(writer);
			writeIndent(writer, level);
		}
	}
	
	private void writeNameSeparator(Writer writer) throws IOException {
		writer.write(config.prefixNameSeparator());
		writer.write(JsonStructuralChar.SEPARATOR_NAME.str());
		writer.write(config.suffixNameSeparator());
	}
	
	private void writeObjectName(Writer writer, JsonObjectPair pair) throws IOException {
		writer.write(JsonStructuralChar.QUOT.str());
		writer.write(pair.name().escaped());
		writer.write(JsonStructuralChar.QUOT.str());
	}
	
}
