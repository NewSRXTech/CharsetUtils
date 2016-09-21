package com.newsrxtech.charsetutils;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;

import com.ibm.icu.text.Transliterator;

public class CharsetUtils {

	private static final Charset CP1252 = Charset.forName("CP1252");
	private static final Charset ISO_8859_1 = StandardCharsets.ISO_8859_1;

	private static final String ANY_LATIN = "Latin-ASCII";
	private static final String ANY_ASCII = "Any-Latin; Latin-ASCII";
	private static Transliterator asLatin = Transliterator.getInstance(ANY_LATIN);
	private static Transliterator asAscii = Transliterator.getInstance(ANY_ASCII);

	public static byte[] asCp1252Bytes(String unicode_text) {
		if (unicode_text == null) {
			return null;
		}
		return asCp1252SafeString(unicode_text).getBytes(CP1252);
	}

	/**
	 * Best effort to re-encode string to US Windows CP1252. This is the default
	 * encoding for US Microsoft System.<br/>
	 * <strong>This encoding is not identical to ISO-8859-1!</strong>
	 * 
	 * @param unicode_text
	 * @return
	 */
	public static String asCp1252SafeString(String unicode_text) {
		if (unicode_text == null) {
			return null;
		}
		CharsetEncoder encoder = CP1252.newEncoder();
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
				switch (asAsciiLetter) {
				case "\u018f":
					asAsciiLetter = "E";
					break;
				case "\u0259":
					asAsciiLetter = "e";
					break;
				}
				if (!encoder.canEncode(asAsciiLetter)) {
					System.err.println("To Ascii FAIL: '" + asAsciiLetter + "'!");
				}
				sb.append(asAsciiLetter);
				continue;
			}
			sb.append(letter);
		}
		return sb.toString();
	}

	public static byte[] asIso8859_1Bytes(String unicode_text) {
		if (unicode_text == null) {
			return null;
		}
		return asIso8859_1SafeString(unicode_text).getBytes(ISO_8859_1);
	}

	/**
	 * Best effort to re-encode string to West European ISO_8859_1. <br/>
	 * <strong>This encoding is not identical to US Windows CP1252!</strong>
	 * 
	 * @param unicode_text
	 * @return
	 */
	public static String asIso8859_1SafeString(String unicode_text) {
		if (unicode_text == null) {
			return null;
		}
		CharsetEncoder encoder = ISO_8859_1.newEncoder();
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
				switch (asAsciiLetter) {
				case "\u018f":
					asAsciiLetter = "E";
					break;
				case "\u0259":
					asAsciiLetter = "e";
					break;
				}
				if (!encoder.canEncode(asAsciiLetter)) {
					System.err.println("To Ascii FAIL: '" + asAsciiLetter + "'!");
				}
				sb.append(asAsciiLetter);
				continue;
			}
			sb.append(letter);
		}
		return sb.toString();
	}
}
