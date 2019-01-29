package tests;

import com.newsrxtech.charsetutils.CharsetUtils;

public class ManualTests {
	public static void main(String[] args) {
//		System.out.println("Available Transliterator IDs");
//		Enumeration<String> availableIDs = Transliterator.getAvailableIDs();
//		while (availableIDs.hasMoreElements()) {
//			System.out.println(" - "+availableIDs.nextElement());
//		}
		String text;
		text = "キャンパス";
		System.out.println(text + " => " +CharsetUtils.asCp1252SafeString(text, false));
		text = "Αλφαβητικός Κατάλογος";
		System.out.println(text + " => " +CharsetUtils.asCp1252SafeString(text, false));
		text = "биологическом";
		System.out.println(text + " => " +CharsetUtils.asCp1252SafeString(text, false));
		text = "x☀x☚x☜x⚌x";
		System.out.println(text + " => " +CharsetUtils.asCp1252SafeString(text, false));
		text = "←↑→↓↔↕↖↗↘↙↚";
		System.out.println(text + " => " +CharsetUtils.asCp1252SafeString(text, false));
		text = "←↑→↓↔↕↖↗↘↙↚";
		System.out.println(text + " => " +CharsetUtils.asCp1252SafeString(text, true));
	}
}
