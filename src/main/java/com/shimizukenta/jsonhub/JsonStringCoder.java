package com.shimizukenta.jsonhub;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * This class is JSON-String to escape or unescape.
 * 
 * @author kenta-shimizu
 *
 */
public class JsonStringCoder {

	private JsonStringCoder() {
		/* Nothing */
	}
	
	private static class SingletonHolder {
		private static final JsonStringCoder inst = new JsonStringCoder();
	}
	
	/**
	 * Returns coder instance.
	 * 
	 * <p>
	 * This class is Singleton-pattern.
	 * </p>
	 * 
	 * @return coder instance
	 */
	public static JsonStringCoder getInstance() {
		return SingletonHolder.inst;
	}
	
	
	protected static final byte BACKSLASH = 0x5C;	/* \ */
	protected static final byte UNICODE = 0x75;	/* u */
	
	protected enum EscapeSets {
		
		BS(0x08, 0x62),	/* b */
		HT(0x09, 0x74),	/* t */
		LF(0x0A, 0x6E),	/* n */
		FF(0x0C, 0x66),	/* f */
		CR(0x0D, 0x72),	/* r */
		QUOT(0x22, 0x22),	/* " */
		SLASH(0x2F, 0x2F),	/* / */
		BSLASH(BACKSLASH, BACKSLASH), /* \ */
		;
		
		private byte a;
		private byte b;
		
		private EscapeSets(int a, int b) {
			this.a = (byte)a;
			this.b = (byte)b;
		}
		
		public static Byte escape(byte b) {
			
			for ( EscapeSets x : values() ) {
				if ( x.a == b ) {
					return Byte.valueOf(x.b);
				}
			}
			
			return null;
		}
		
		public static Byte unescape(byte b) {
			
			for ( EscapeSets x : values() ) {
				if ( x.b == b ) {
					return Byte.valueOf(x.a);
				}
			}
			
			return null;
		}
	}
	
	/**
	 * Retruns escaped JSON-String.
	 * 
	 * <p>
	 * Not Accept {@code null}.<br />
	 * </p>
	 * 
	 * @param cs unescaped-JSON-Stirng
	 * @return escaped-JSON-String
	 */
	public String escape(CharSequence cs) {
		
		String v = cs.toString();
		
		try (
				ByteArrayOutputStream strm = new ByteArrayOutputStream();
				) {
			
			byte[] bb = v.getBytes(StandardCharsets.UTF_8);
			
			for (byte b : bb) {
				
				Byte x = EscapeSets.escape(b);
				
				if ( x == null ) {
					
					strm.write(b);
					
				} else {
					
					strm.write(BACKSLASH);
					strm.write(x.byteValue());
				}
			}
			
			return new String(strm.toByteArray(), StandardCharsets.UTF_8);
		}
		catch ( IOException notHappen ) {
			throw new RuntimeException(notHappen);
		}
	}
	
	/**
	 * Returns unescaped JSON-String.
	 * 
	 * <p>
	 * Not Accept {@code null}.<br />
	 * </p>
	 * 
	 * @param cs escaped-JSON-String
	 * @return unescaped-JSON-String
	 */
	public String unescape(CharSequence cs) {
		
		String v = cs.toString();
		
		try (
				ByteArrayOutputStream strm = new ByteArrayOutputStream();
				) {
			
			byte[] bb = v.getBytes(StandardCharsets.UTF_8);
			
			for (int i = 0, len = bb.length; i < len; ++i) {
				
				byte b = bb[i];
				
				if ( b == BACKSLASH ) {
					
					++i;
					
					byte b2 = bb[i];
					
					if ( b2 == UNICODE ) {
						
						byte[] xx = unescapeUnicode(new byte[]{bb[i + 1], bb[i + 2], bb[i + 3], bb[i + 4]});
						
						strm.write(xx);
						
						i += 4;
						
					} else {
						
						Byte x = EscapeSets.unescape(b2);
						
						if ( x != null ) {
							strm.write(x.byteValue());
						}
					}
					
				} else {
					
					strm.write(b);
				}
			}
			
			return new String(strm.toByteArray(), StandardCharsets.UTF_8);
		}
		catch ( IOException notHappen ) {
			throw new RuntimeException(notHappen);
		}
		catch ( IndexOutOfBoundsException e ) {
			throw new JsonHubIndexOutOfBoundsException("unescape failed \"" + cs.toString() + "\"");
		}
	}
	
	private static byte[] unescapeUnicode(byte[] bb) {
		
		String s1 = new String(new byte[]{bb[0], bb[1]}, StandardCharsets.UTF_8);
		String s2 = new String(new byte[]{bb[2], bb[3]}, StandardCharsets.UTF_8);
		
		byte b = decodeXXtoByte(s1);
		
		if ( b == 0x00 ) {
			
			return new byte[] {decodeXXtoByte(s2)};
			
		} else {
			
			return new byte[] {b, decodeXXtoByte(s2)};
		}
	}
	
	private static byte decodeXXtoByte(String s) {
		
		try {
			return Byte.parseByte(s, 16);
		}
		catch ( NumberFormatException e ) {
			throw new JsonHubNumberFormatException(s);
		}
	}
	
}
