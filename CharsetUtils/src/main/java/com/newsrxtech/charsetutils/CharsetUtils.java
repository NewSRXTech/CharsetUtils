package com.newsrxtech.charsetutils;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.text.translate.AggregateTranslator;
import org.apache.commons.lang3.text.translate.CharSequenceTranslator;
import org.apache.commons.lang3.text.translate.EntityArrays;
import org.apache.commons.lang3.text.translate.LookupTranslator;
import org.apache.commons.lang3.text.translate.NumericEntityEscaper;

import com.ibm.icu.text.Transliterator;
import com.newsrxtech.charsetutils.log.Log;

public class CharsetUtils {

	protected static Logger log = Log.getLogger(CharsetUtils.class.getName());

	static {
		log.setLevel(Level.WARNING);
	}

	public static Level getLogLevel() {
		return log.getLevel();
	}

	public static void setLogLevel(Level newLevel) {
		log.setLevel(newLevel);
	}

	/**
	 * Lowest common charset for Latin based orthographies.
	 */
	public static final Charset US_ASCII = StandardCharsets.US_ASCII;
	/**
	 * Charset as used in US Microsoft Windows.
	 */
	public static final Charset CP1252 = Charset.forName("CP1252");
	/**
	 * Charset as used in Western Europe Microsoft Windows.
	 */
	public static final Charset ISO_8859_1 = StandardCharsets.ISO_8859_1;

	protected static final String ANY_LATIN = "Latin-ASCII";
	protected static final String ANY_ASCII = "Any-Latin; Latin-ASCII";
	protected static Transliterator asLatin = Transliterator.getInstance(ANY_LATIN);
	protected static Transliterator asAscii = Transliterator.getInstance(ANY_ASCII);

	/**
	 * 
	 * @param unicode_text
	 * @param htmlFallback
	 * @return String mutilated as a best fit into Windows ANSI bytes.
	 */
	public static byte[] asCp1252Bytes(String unicode_text, boolean htmlFallback) {
		return asCharsetBytes(unicode_text, CP1252, htmlFallback);
	}

	/**
	 * Best effort to re-encode string to US Windows CP1252. This is the default
	 * encoding for US Microsoft System.<br/>
	 * <strong>This encoding is not identical to ISO-8859-1!</strong>
	 * 
	 * @param unicode_text
	 * @return String mutilated as a best fit to Windows ANSI CP1252. 
	 */
	public static String asCp1252SafeString(String unicode_text, boolean htmlFallback) {
		return asCharsetSafeString(unicode_text, CP1252, htmlFallback);
	}

	/**
	 * 
	 * @param unicode_text
	 * @param htmlFallback
	 * @return String mutilated as a best fit into Western European ISO8859-1 bytes.
	 */
	public static byte[] asIso8859_Bytes(String unicode_text, boolean htmlFallback) {
		return asCharsetBytes(unicode_text, ISO_8859_1, htmlFallback);
	}

	/**
	 * Best effort to re-encode string to West European ISO_8859_1. <br/>
	 * <strong>This encoding is not identical to US Windows CP1252!</strong>
	 * 
	 * @param unicode_text
	 * @return String mutilated as a best fit into a Western European String.
	 */
	public static String asIso8859_SafeString(String unicode_text, boolean htmlFallback) {
		return asCharsetSafeString(unicode_text, ISO_8859_1, htmlFallback);
	}

	/**
	 * 
	 * @param unicode_text
	 * @param htmlFallback
	 * @return String mutilated as a best fit into 7-bit range ASCII bytes.
	 */
	public static byte[] asAsciiBytes(String unicode_text, boolean htmlFallback) {
		return asCharsetBytes(unicode_text, US_ASCII, htmlFallback);
	}

	/**
	 * Best effort to re-encode string to US ASCII.
	 * 
	 * @param unicode_text
	 * @return String mutilated as a best fit into a 7-bit ASCII String.
	 */
	public static String asAsciiSafeString(String unicode_text, boolean htmlFallback) {
		return asCharsetSafeString(unicode_text, US_ASCII, htmlFallback);
	}

	/**
	 * 
	 * @param unicode_text
	 * @param charset
	 * @param htmlFallback
	 * @return String mutilated as a best fit into the specified charset bytes.
	 */
	public static byte[] asCharsetBytes(String unicode_text, Charset charset, boolean htmlFallback) {
		return asCharsetSafeString(unicode_text, charset, htmlFallback).getBytes(charset);
	}

	/**
	 * Best effort to re-encode string to specified {@linkplain Charset} via
	 * ICU4J any-latin transliterator. <br/>
	 * Only works well for charsets that are LATIN/US-ASCII based.
	 * 
	 * @param unicode_text
	 * @return String mutilated as a best fit into a charset compatible String.
	 */
	public static String asCharsetSafeString(String unicode_text, Charset charset, boolean htmlFallback) {
		if (unicode_text == null) {
			return null;
		}
		CharsetEncoder encoder = charset.newEncoder();
		StringBuilder sb = new StringBuilder(unicode_text.length());
		//String[] split = unicode_text.split("");
		//for (String letter : split) {
		unicode_text.codePoints().forEach(cp->{
			String letter = new String(Character.toChars(cp));
			if (!encoder.canEncode(letter)) {
				String asLatinLetter = asLatin.transliterate(letter);
				if (encoder.canEncode(asLatinLetter)) {
					sb.append(asLatinLetter);
					return;
				}
				String asAsciiLetter = asAscii.transliterate(letter);
				if (encoder.canEncode(asAsciiLetter)){
					sb.append(asAsciiLetter);
					return;
				}
				if (htmlFallback) {
					String htmlEscaped = ESCAPE_EXTENDED_FALLBACK.translate(asAsciiLetter);
					if (!alreadyLogged.contains(asAsciiLetter)) {
						log.info("ESCAPE_EXTENDED_FALLBACK: '" + asAsciiLetter + "' => " + htmlEscaped);
						alreadyLogged.add(asAsciiLetter);
					}
					sb.append(htmlEscaped);
					return;
				}
			}
			sb.append(letter);
		});
		return sb.toString();
	}

	protected final static Set<String> alreadyLogged = new HashSet<>();

	protected static final CharSequenceTranslator ESCAPE_EXTENDED_FALLBACK = new AggregateTranslator(
			new LookupTranslator(EntityArrays.HTML40_EXTENDED_ESCAPE()), new NumericEntityEscaper());

}
