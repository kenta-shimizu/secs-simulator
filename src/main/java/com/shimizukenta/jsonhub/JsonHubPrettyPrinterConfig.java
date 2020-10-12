package com.shimizukenta.jsonhub;

import java.io.Serializable;

/**
 * This class is implements of Pretty-Print-Configuration.
 * 
 * <p>
 * Used in {@link JsonHubPrettyPrinter#newPrinter(JsonHubPrettyPrinterConfig)}.<br />
 * </p>
 * 
 * @author kenta-shimizu
 *
 */
public class JsonHubPrettyPrinterConfig implements Serializable {
	
	private static final long serialVersionUID = 5214359701545372403L;
	
	private static final boolean defaultNoneNullValueInObject = false;
	private static final String defaultIndent = "  ";
	private static final String defaultLineSeparator = System.lineSeparator();
	private static final String defaultPrefixValueSeparator = "";
	private static final String defaultSuffixValueSeparator = "";
	private static final String defaultPrefixNameSeparator = "";
	private static final String defaultSuffixNameSeparator = " ";
	private static final boolean defaultLineSeparateBeforeValueSeparator = false;
	private static final boolean defaultLineSeparateAfterValueSeparator = true;
	private static final boolean defaultLineSeparateIfBlank = false;
	
	private boolean noneNullValueInObject;
	private String indent;
	private String lineSeparator;
	private String prefixValueSeparator;
	private String suffixValueSeparator;
	private String prefixNameSeparator;
	private String suffixNameSeparator;
	private boolean lineSeparateBeforeValueSeparator;
	private boolean lineSeparateAfterValueSeparator;
	private boolean lineSeparateIfBlank;
	
	protected JsonHubPrettyPrinterConfig() {
		this.noneNullValueInObject = defaultNoneNullValueInObject;
		this.indent = defaultIndent;
		this.lineSeparator = defaultLineSeparator;
		this.prefixValueSeparator = defaultPrefixValueSeparator;
		this.suffixValueSeparator = defaultSuffixValueSeparator;
		this.prefixNameSeparator = defaultPrefixNameSeparator;
		this.suffixNameSeparator = defaultSuffixNameSeparator;
		this.lineSeparateBeforeValueSeparator = defaultLineSeparateBeforeValueSeparator;
		this.lineSeparateAfterValueSeparator = defaultLineSeparateAfterValueSeparator;
		this.lineSeparateIfBlank = defaultLineSeparateIfBlank;
	}
	
	/**
	 * Returns default-JsonHubPrettyPrinterConfig.
	 * 
	 * <p>
	 * Config from this instance.<br />
	 * </p>
	 * 
	 * @return default-JsonHubPrettyPrinterConfig
	 */
	public static JsonHubPrettyPrinterConfig defaultConfig() {
		return new JsonHubPrettyPrinterConfig();
	}
	
	/**
	 * Setter of excluding null-value-pair in OBJECT.
	 * 
	 * @param f set {@code true} if exclude null-value-pair in OBJECT
	 */
	public void noneNullInObject(boolean f) {
		synchronized ( this ) {
			this.noneNullValueInObject = f;
		}
	}
	
	/**
	 * Excluding null-value-pair in OBJECT getter.
	 * 
	 * @return true if excluding null-value-pair in OBJECT
	 */
	public boolean noneNullValueInObject() {
		synchronized ( this ) {
			return this.noneNullValueInObject;
		}
	}
	
	/**
	 * Indent setter.
	 * 
	 * <p>
	 * Not accept {@code null}.<br />
	 * </p>
	 * 
	 * @param cs Indent String
	 */
	public void indent(CharSequence cs) {
		synchronized ( this ) {
			this.indent = cs.toString();
		}
	}
	
	/**
	 * Indent getter.
	 * 
	 * @return Indent String
	 */
	public String indent() {
		synchronized ( this ) {
			return this.indent;
		}
	}
	
	/**
	 * Line Separator setter.
	 * 
	 * <p>
	 * Not accept {@code null}.<br />
	 * </p>
	 * 
	 * @param cs Line-Searator-String
	 */
	public void lineSeparator(CharSequence cs) {
		synchronized ( this ) {
			this.lineSeparator = cs.toString();
		}
	}
	
	/**
	 * Line Separator getter.
	 * 
	 * @return Line-Separator-String
	 */
	public String lineSeparator() {
		synchronized ( this ) {
			return this.lineSeparator;
		}
	}
	
	/**
	 * Prefix-String of value-Separator (",") setter.
	 * 
	 * <p>
	 * Not accept {@code null}
	 * </p>
	 * 
	 * @param cs Prefix-String of value-Separator
	 */
	public void prefixValueSeparator(CharSequence cs) {
		synchronized ( this ) {
			this.prefixValueSeparator = cs.toString();
		}
	}
	
	/**
	 * Prefix-String of value-Separator (",") getter.
	 * 
	 * @return Prefix-String of value-Separator
	 */
	public String prefixValueSeparator() {
		synchronized ( this ) {
			return this.prefixValueSeparator;
		}
	}
	
	/**
	 * Suffix-String of value-Separator (",") setter.
	 * 
	 * <p>
	 * Not accept {@code null}
	 * </p>
	 * 
	 * @param cs Suffix-String of value-Separator
	 */
	public void suffixValueSeparator(CharSequence cs) {
		synchronized ( this ) {
			this.suffixValueSeparator = cs.toString();
		}
	}
	
	/**
	 * Suffix-String of value-Separator (",") getter.
	 * 
	 * @return Suffix-String of value-Separator
	 */
	public String suffixValueSeparator() {
		synchronized ( this ) {
			return this.suffixValueSeparator;
		}
	}
	
	/**
	 * Prefix-String of name-Separator (":") setter.
	 * 
	 * <p>
	 * Not accept {@code null}
	 * </p>
	 * 
	 * @param cs Prefix-String of name-Separator
	 */
	public void prefixNameSeparator(CharSequence cs) {
		synchronized ( this ) {
			this.prefixNameSeparator = cs.toString();
		}
	}
	
	/**
	 * Prefix-String of name-Separator (":") getter.
	 * 
	 * @return Prefix-String of name-Separator
	 */
	public String prefixNameSeparator() {
		synchronized ( this ) {
			return this.prefixNameSeparator;
		}
	}
	
	/**
	 * Suffix-String of name-Separator (":") setter.
	 * 
	 * <p>
	 * Not accept {@code null}
	 * </p>
	 * 
	 * @param cs Suffix-String of name-Separator
	 */
	public void suffixNameSeparator(CharSequence cs) {
		synchronized ( this ) {
			this.suffixNameSeparator = cs.toString();
		}
	}
	
	/**
	 * Suffix-String of name-Separator (":") getter.
	 * 
	 * @return Suffix-String of name-Separator
	 */
	public String suffixNameSeparator() {
		synchronized ( this ) {
			return this.suffixNameSeparator;
		}
	}
	
	/**
	 * Setter of Line-Separate before value-Separator (",").
	 * 
	 * @param f set {@code true} if line-separate before value-separator
	 */
	public void lineSeparateBeforeValueSeparator(boolean f) {
		synchronized ( this ) {
			this.lineSeparateBeforeValueSeparator = f;
		}
	}
	
	/**
	 * Line-Separating before value-separator getter.
	 * 
	 * @return {@code true} if line-separating before value-separator
	 */
	public boolean lineSeparateBeforeValueSeparator() {
		synchronized ( this ) {
			return this.lineSeparateBeforeValueSeparator;
		}
	}
	
	/**
	 * Setter of Line-Separate after value-Separator (",").
	 * 
	 * @param f set {@code true} if line-separate after value-separator
	 */
	public void lineSeparateAfterValueSeparator(boolean f) {
		synchronized ( this ) {
			this.lineSeparateAfterValueSeparator = f;
		}
	}
	
	/**
	 * Line-Separating after value-separator getter.
	 * 
	 * @return {@code true} if line-separating after value-separator
	 */
	public boolean lineSeparateAfterValueSeparator() {
		synchronized ( this ) {
			return this.lineSeparateAfterValueSeparator;
		}
	}
	
	/**
	 * Setter of Line-Separate in blank ARRAY or OBJECT.
	 * 
	 * @param f set {@code true} if line-separate in blank ARRAY or OBJECT
	 */
	public void lineSeparateIfBlank(boolean f) {
		synchronized ( this ) {
			this.lineSeparateIfBlank = f;
		}
	}
	
	/**
	 * Line-Separate in blank ARRAY or OBJECT getter.
	 * 
	 * @return {@code true} if line-separate in blank ARRAY or OBJECT
	 */
	public boolean lineSeparateIfBlank() {
		synchronized ( this ) {
			return this.lineSeparateIfBlank;
		}
	}
	
}
