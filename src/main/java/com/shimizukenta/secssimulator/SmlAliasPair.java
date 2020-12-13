package com.shimizukenta.secssimulator;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.shimizukenta.secs.sml.SmlMessage;
import com.shimizukenta.secs.sml.SmlParseException;
import com.shimizukenta.secssimulator.extendsml.ExtendSmlMessageParser;

/**
 * This class is implements of SML-Message and alias getter.
 * 
 * <p>
 * Instances of this class are immutable.<br />
 * </p>
 * 
 * @author kenta-shimizu
 *
 */
public class SmlAliasPair implements Comparable<SmlAliasPair>, Serializable {
	
	private static final long serialVersionUID = 3476711070674520185L;
	
	private final String alias;
	private final SmlMessage sml;
	private final Integer sfnum;
	private final Path path;
	
	public SmlAliasPair(CharSequence alias, SmlMessage sm, Path path) {
		this.alias = Objects.requireNonNull(alias).toString();
		if ( this.alias.isEmpty() ) {
			throw new IllegalArgumentException("require not empty-alias-string");
		}
		this.sml = Objects.requireNonNull(sm);
		this.sfnum = Integer.valueOf((sm.getStream() << 8) | sm.getFunction());
		this.path = path;
	}
	
	private static final ExtendSmlMessageParser parser = ExtendSmlMessageParser.getInstance();
	
	/**
	 * Create SmlAliasPair instance.
	 * 
	 * @param alias
	 * @param path
	 * @return SmlAliasPair instance
	 * @throws SmlParseException
	 * @throws IOException
	 */
	public static SmlAliasPair fromFile(CharSequence alias, Path path) throws SmlParseException, IOException {
		
		try (
				Stream<String> lines = Files.lines(path, StandardCharsets.UTF_8);
				) {
			
			String ss = lines.collect(Collectors.joining());
			return new SmlAliasPair(alias, parser.parse(ss), path.toAbsolutePath());
		}
	}
	
	private static final String SmlExtension = ".sml";
	
	/**
	 * Create CmlAliasPair instance.
	 * 
	 * @param path
	 * @return SmlAliasPair instance
	 * @throws SmlParseException
	 * @throws IOException
	 */
	public static SmlAliasPair fromFile(Path path) throws SmlParseException, IOException {
		String alias = path.getFileName().toString();
		if ( alias.endsWith(SmlExtension) ) {
			alias = alias.substring(0, alias.length() - SmlExtension.length());
		}
		return fromFile(alias, path);
	}
	
	/**
	 * Returns alias.
	 * 
	 * @return alias
	 */
	public String alias() {
		return this.alias;
	}
	
	/**
	 * Return SML-Message.
	 * 
	 * @return SML-Message
	 */
	public SmlMessage sml() {
		return this.sml;
	}
	
	/**
	 * Returns SML-file-path.
	 * 
	 * @return SML-file-path
	 */
	public Path path() {
		synchronized ( this ) {
			return this.path;
		}
	}
	
	@Override
	public int hashCode() {
		return this.alias.hashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if ((other != null) && (other instanceof SmlAliasPair)) {
			return ((SmlAliasPair)other).alias().equals(alias());
		}
		return false;
	}
	
	@Override
	public int compareTo(SmlAliasPair o) {
		if ( this.sfnum.equals(o.sfnum) ) {
			return this.alias.compareTo(o.alias);
		} else {
			return this.sfnum.compareTo(o.sfnum);
		}
	}
	
}
