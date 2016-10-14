package com.newsrxtech.charsetutils;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.text.translate.AggregateTranslator;
import org.apache.commons.lang3.text.translate.CharSequenceTranslator;
import org.apache.commons.lang3.text.translate.EntityArrays;
import org.apache.commons.lang3.text.translate.LookupTranslator;
import org.apache.commons.lang3.text.translate.NumericEntityEscaper;

import com.ibm.icu.text.Transliterator;

public class CharsetUtils {

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

	private static final String ANY_LATIN = "Latin-ASCII";
	private static final String ANY_ASCII = "Any-Latin; Latin-ASCII";
	private static Transliterator asLatin = Transliterator.getInstance(ANY_LATIN);
	private static Transliterator asAscii = Transliterator.getInstance(ANY_ASCII);

	public static byte[] asCp1252Bytes(String unicode_text, boolean htmlFallback) {
		return asCharsetBytes(unicode_text, CP1252, htmlFallback);
	}

	/**
	 * Best effort to re-encode string to US Windows CP1252. This is the default
	 * encoding for US Microsoft System.<br/>
	 * <strong>This encoding is not identical to ISO-8859-1!</strong>
	 * 
	 * @param unicode_text
	 * @return
	 */
	public static String asCp1252SafeString(String unicode_text, boolean htmlFallback) {
		return asCharsetSafeString(unicode_text, CP1252, htmlFallback);
	}

	public static byte[] asIso8859_1Bytes(String unicode_text, boolean htmlFallback) {
		return asCharsetBytes(unicode_text, ISO_8859_1, htmlFallback);
	}

	/**
	 * Best effort to re-encode string to West European ISO_8859_1. <br/>
	 * <strong>This encoding is not identical to US Windows CP1252!</strong>
	 * 
	 * @param unicode_text
	 * @return
	 */
	public static String asIso8859_1SafeString(String unicode_text, boolean htmlFallback) {
		return asCharsetSafeString(unicode_text, ISO_8859_1, htmlFallback);
	}
	
	public static byte[] asAsciiBytes(String unicode_text, boolean htmlFallback) {
		return asCharsetBytes(unicode_text, US_ASCII, htmlFallback);
	}

	/**
	 * Best effort to re-encode string to US ASCII.
	 * 
	 * @param unicode_text
	 * @return
	 */
	public static String asAsciiSafeString(String unicode_text, boolean htmlFallback) {
		return asCharsetSafeString(unicode_text, US_ASCII, htmlFallback);
	}
	
	public static byte[] asCharsetBytes(String unicode_text, Charset charset, boolean htmlFallback) {
		return asCharsetSafeString(unicode_text, charset, htmlFallback).getBytes(charset);
	}
	
	/**
	 * Best effort to re-encode string to specified {@linkplain Charset} via ICU4J any-latin transliterator. <br/>
	 * Only works well for charsets that are LATIN/US-ASCII based.
	 * 
	 * @param unicode_text
	 * @return
	 */
	public static String asCharsetSafeString(String unicode_text, Charset charset, boolean htmlFallback) {
		if (unicode_text == null) {
			return null;
		}
		CharsetEncoder encoder = charset.newEncoder();
		StringBuilder sb = new StringBuilder();
		String[] split = unicode_text.split("");
		for (String letter : split) {
			if (!encoder.canEncode(letter)) {
				String asLatinLetter = asLatin.transliterate(letter);
				if (encoder.canEncode(asLatinLetter)) {
					sb.append(asLatinLetter);
					continue;
				}
				String asAsciiLetter = asAscii.transliterate(letter);
				if (!encoder.canEncode(asAsciiLetter) && htmlFallback) {
					String replacement = ESCAPE_EXTENDED_FALLBACK.translate(asAsciiLetter);
					if (!alreadyLogged.contains(asAsciiLetter)) {
						System.err.print("ESCAPE_EXTENDED_FALLBACK: '" + asAsciiLetter + "' => ");
						System.err.println(replacement);
						alreadyLogged.add(asAsciiLetter);
					}
					asAsciiLetter=replacement;
				}
				sb.append(asAsciiLetter);
				continue;
			}
			sb.append(letter);
		}
		return sb.toString();
	}
	
	private final static Set<String> alreadyLogged = new HashSet<>();
	
	public static final CharSequenceTranslator ESCAPE_EXTENDED_FALLBACK = 
	        new AggregateTranslator(
	            new LookupTranslator(EntityArrays.HTML40_EXTENDED_ESCAPE()),
	            new NumericEntityEscaper()
	        );
	
}
