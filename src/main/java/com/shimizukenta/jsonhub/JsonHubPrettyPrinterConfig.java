package com.shimizukenta.jsonhub;

import java.io.Serializable;

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
	
	public JsonHubPrettyPrinterConfig() {
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
	
	public static JsonHubPrettyPrinterConfig defaultConfig() {
		return new JsonHubPrettyPrinterConfig();
	}
	
	public void noneNullInObject(boolean f) {
		synchronized ( this ) {
			this.noneNullValueInObject = f;
		}
	}
	
	public boolean noneNullValueInObject() {
		synchronized ( this ) {
			return this.noneNullValueInObject;
		}
	}
	
	public void indent(CharSequence cs) {
		synchronized ( this ) {
			this.indent = cs.toString();
		}
	}
	
	public String indent() {
		synchronized ( this ) {
			return this.indent;
		}
	}
	
	public void lineSeparator(CharSequence cs) {
		synchronized ( this ) {
			this.lineSeparator = cs.toString();
		}
	}
	
	public String lineSeparator() {
		synchronized ( this ) {
			return this.lineSeparator;
		}
	}
	
	public void prefixValueSeparator(CharSequence cs) {
		synchronized ( this ) {
			this.prefixValueSeparator = cs.toString();
		}
	}
	
	public String prefixValueSeparator() {
		synchronized ( this ) {
			return this.prefixValueSeparator;
		}
	}
	
	public void suffixValueSeparator(CharSequence cs) {
		synchronized ( this ) {
			this.suffixValueSeparator = cs.toString();
		}
	}
	
	public String suffixValueSeparator() {
		synchronized ( this ) {
			return this.suffixValueSeparator;
		}
	}
	
	public void prefixNameSeparator(CharSequence cs) {
		synchronized ( this ) {
			this.prefixNameSeparator = cs.toString();
		}
	}
	
	public String prefixNameSeparator() {
		synchronized ( this ) {
			return this.prefixNameSeparator;
		}
	}
	
	public void suffixNameSeparator(CharSequence cs) {
		synchronized ( this ) {
			this.suffixNameSeparator = cs.toString();
		}
	}
	
	public String suffixNameSeparator() {
		synchronized ( this ) {
			return this.suffixNameSeparator;
		}
	}
	
	public void lineSeparateBeforeValueSeparator(boolean f) {
		synchronized ( this ) {
			this.lineSeparateBeforeValueSeparator = f;
		}
	}
	
	public boolean lineSeparateBeforeValueSeparator() {
		synchronized ( this ) {
			return this.lineSeparateBeforeValueSeparator;
		}
	}
	
	public void lineSeparateAfterValueSeparator(boolean f) {
		synchronized ( this ) {
			this.lineSeparateAfterValueSeparator = f;
		}
	}
	
	public boolean lineSeparateAfterValueSeparator() {
		synchronized ( this ) {
			return this.lineSeparateAfterValueSeparator;
		}
	}
	
	public void lineSeparateIfBlank(boolean f) {
		synchronized ( this ) {
			this.lineSeparateIfBlank = f;
		}
	}
	
	public boolean lineSeparateIfBlank() {
		synchronized ( this ) {
			return this.lineSeparateIfBlank;
		}
	}
	
}
