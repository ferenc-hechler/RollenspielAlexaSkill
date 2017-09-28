/**
 * Diese Datei ist Teil des Alexa Skills Rollenspiel Soloabenteuer.
 * Copyright (C) 2016-2017 Ferenc Hechler (github@fh.anderemails.de)
 *
 * Der Alexa Skills Rollenspiel Soloabenteuer ist Freie Software: 
 * Sie koennen es unter den Bedingungen
 * der GNU General Public License, wie von der Free Software Foundation,
 * Version 3 der Lizenz oder (nach Ihrer Wahl) jeder spaeteren
 * veroeffentlichten Version, weiterverbreiten und/oder modifizieren.
 *
 * Der Alexa Skills Rollenspiel Soloabenteuer wird in der Hoffnung, 
 * dass es nuetzlich sein wird, aber
 * OHNE JEDE GEWAEHRLEISTUNG, bereitgestellt; sogar ohne die implizite
 * Gewaehrleistung der MARKTFAEHIGKEIT oder EIGNUNG FUER EINEN BESTIMMTEN ZWECK.
 * Siehe die GNU General Public License fuer weitere Details.
 * 
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.hechler.soloroleplay.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

public class TextUtil {

	private final static Logger logger = Logger.getLogger(TextUtil.class.getName());

	public final static String UML_AE = "\u00c4";
	public final static String UML_ae = "\u00e4";
	public final static String UML_OE = "\u00d6";
	public final static String UML_oe = "\u00f6";
	public final static String UML_UE = "\u00dc";
	public final static String UML_ue = "\u00fc";
	public final static String UML_sz = "\u00df";
	
	public final static String UML_AEOEUESZ = UML_AE + UML_OE + UML_UE + UML_sz; 
	public final static String UML_aeoeueAEOEUESZ = UML_ae + UML_oe + UML_ue + UML_AE + UML_OE + UML_UE + UML_sz; 
	
    public final static String endl = System.getProperty("line.separator");

	public static void addNotNull(StringBuilder result, String addText) {
		if (addText != null) {
			if (result.length() > 0) {
				result.append(" ");
			}
			result.append(addText);
		}
	}

	

	public static String readResource(String resource) {
		if (resource == null) {
			return null;
		}
		return readFully(Thread.currentThread().getContextClassLoader().getResourceAsStream(resource));
	}

	public static String readResource(String resource, Charset charset) {
		if (resource == null) {
			return null;
		}
		return readFully(Thread.currentThread().getContextClassLoader().getResourceAsStream(resource), charset);
	}

	public static String readFully(File file) {
		try {
			if (file == null) {
				return null;
			}
			return readFully(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static String readFully(File file, Charset charset) {
		try {
			if (file == null) {
				return null;
			}
			return readFully(new FileInputStream(file), charset);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static String readFully(InputStream ins) {
		if (ins == null) {
			return null;
		}
		return readFully(new InputStreamReader(ins));
	}

	public static String readFully(InputStream ins, Charset charset) {
		if (ins == null) {
			return null;
		}
		return readFully(new InputStreamReader(ins, charset));
	}

	public static String readFully(InputStreamReader in) {
		if (in == null) {
			return null;
		}
		try {
			StringWriter result = new StringWriter();
			char[] cbuf = new char[4096];
			int cnt;
			while ((cnt=in.read(cbuf)) > 0) {
				result.write(cbuf, 0, cnt);
			}
			return result.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		finally {
			try {
				in.close();
			} catch (IOException ignore) {}
		}
	}



	public static String makeEndl(String text) {
		String result = text.replace("\r\n", "\n").replace("\r", "\n").replace("\n", endl);
//		result = result.replace("\r", "\\r").replace("\n", "\\n");
		return result;
	}



	public static void normalizeForCompare(List<String> words) {
		for (int i=0; i<words.size(); i++) {
			String word = words.get(i);
			word = normalizeForCompare(word);
			words.set(i, word);
		}
	}

	public static String normalizeForCompare(String word) {
		return word.toUpperCase(Locale.GERMAN).replaceAll("[^A-Z"+TextUtil.UML_AE+TextUtil.UML_OE+TextUtil.UML_UE+TextUtil.UML_sz+"0-9]+", "");
	}

	public static String handleMinus(String text) {
		if (text == null) {
			return null;
		}
		return text.replaceAll("\\s*-\\s*(\\d)", " minus $1");
	}

	public static String encodeRest(String text) {
		return text.replace('\'', '"');
	}

	public static String escUml(String text) {
		return text.replace("ä", UML_ae).replace("ö", UML_oe).replace("ü", UML_ue).replace("Ä", UML_AE).replace("Ö", UML_OE).replace("Ü", UML_UE).replace("ß", UML_sz);
	}

	public static String normalizeAnswer(String answer) {
		if (answer == null) {
			return null;
		}
		String result = answer.toUpperCase(Locale.GERMAN).replaceAll("\\s+", " ").replaceAll("[^A-Z "+TextUtil.UML_AE+TextUtil.UML_OE+TextUtil.UML_UE+TextUtil.UML_sz+"]+", "").trim();
		return result;
	}



	public static void logChars(String prefix, String word) {
		logger.info(prefix+" '"+word+"': "+dump(word));
	}


	public static String escapeHtml(String text) {
		if (text == null) {
			return "NULL";
		}
		return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("'", "&apos;").replace("\"", "&quot;");
	}

	public static String dump(String word) {
		StringBuilder result = new StringBuilder();
		if (word == null) {
			return "<null>";
		}
		result.append("[");
		String seperator = "";
		for (int n=0; n<word.length(); n++) {
			char c = word.charAt(n);
			result.append(seperator).append(Integer.toHexString(c));
			seperator = ", ";
		}
		result.append("]");
		return result.toString();
	}



	private static String SPACES = "                                        ";
	
	public static String spaces(int n) {
		if (n<=0) {
			return "";
		}
		if (n < SPACES.length()) {
			return SPACES.substring(0, n);
		}
		while (SPACES.length() < n) {
			SPACES = SPACES + SPACES;
		}
		return SPACES.substring(0, n);
	}
	

	public static String padL(String txt, int len) {
		if (txt == null) {
			return spaces(len);
		}
		if (txt.length() >= len) {
			return txt;
		}
		return spaces(len-txt.length()) + txt;
	}
	
	public static String padR(String txt, int len) {
		if (txt == null) {
			return spaces(len);
		}
		if (txt.length() >= len) {
			return txt;
		}
		return txt+spaces(len-txt.length());
	}
	
	public static String padNum(int n, int len) {
		return padL(Integer.toString(n), len);
	}


	private final static String RX_PHONEM = "PHONEM\\(([^=]+)[=]([^\\)]+)\\)";
	
	public static String makeReadable(String text) {
		if (text == null) {
			return null;
		}
		return text.replaceAll(RX_PHONEM, "$1");
	}



	/**
	 * convert character position (0-based) to line number (1-based).
	 * @param text
	 * @param pos
	 * @return
	 */
	public static int pos2line(String text, int pos) {
		if ((text == null) || (pos < 0) || (pos >= text.length())) {
			return -1;
		}
		int currentPos = 0;
		int result = 1;
		int nextPos = text.indexOf('\n', currentPos);
		while ((nextPos != -1) && (nextPos < pos)) {
			result += 1;
			currentPos = nextPos+1;
			nextPos = text.indexOf('\n', currentPos);
		}
		return result;
	}

	/**
	 * convert line number (1-based) to character position (0-based).  
	 * @param text
	 * @param line
	 * @return
	 */
	public static int line2pos(String text, int line) {
		if (line < 1) {
			return -1;
		}
		int result = 0;
		int currentLine = 1;
		while ((result != -1) && (currentLine < line)) {
			currentLine++;
			result = text.indexOf('\n', result)+1;
		}
		if (result >= text.length()) {
			return -1;
		}
		return result;
	}



	public static String escQuot(String result) {
		if (result == null) {
			return null;
		}
		return result.replace("\"", "&quot;");
	}
	
	public static String unescQuot(String result) {
		if (result == null) {
			return null;
		}
		return result.replace("&quot;", "\"");
	}



	public static String shorten(String responseString, int len) {
		if (responseString == null) {
			return null;
		}
		if (responseString.length() <= len) {
			return responseString;
		}
		if (len < 5) {
			return responseString.substring(1, len);
		}
		int half = len / 2;
		return responseString.substring(0, half) + ".." + responseString.substring(responseString.length()-len+half+2);
	}
	

}
