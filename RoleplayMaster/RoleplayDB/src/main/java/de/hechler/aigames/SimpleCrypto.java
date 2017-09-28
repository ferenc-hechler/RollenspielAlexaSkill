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
package de.hechler.aigames;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * Usage:
 * <pre>
 * String crypto = SimpleCrypto.encrypt(masterpassword, cleartext)
 * ...
 * String cleartext = SimpleCrypto.decrypt(masterpassword, crypto)
 * </pre>
 * @author ferenc.hechler
 */
public class SimpleCrypto {

        public static String encrypt(String seed, String cleartext) {
                byte[] rawKey = getRawKey(seed);
                byte[] result = encrypt(rawKey, cleartext.getBytes(StandardCharsets.UTF_8));
                return toHex(result);
        }
        
        public static String decrypt(String seed, String encrypted) {
                byte[] rawKey = getRawKey(seed);
                byte[] enc = toByte(encrypted);
                byte[] result = decrypt(rawKey, enc);
                return new String(result, StandardCharsets.UTF_8);
        }

        private static byte[] getRawKey(String seedString) {
			try {
				byte[] seed = ("fe-"+seedString+"-ri").getBytes(StandardCharsets.UTF_8);
	        	// Create MD5 Hash
	            MessageDigest digest;
				digest = java.security.MessageDigest.getInstance("MD5");
	            digest.update(seed);
	            byte messageDigest[] = digest.digest();
	            byte[] result = new byte[16];
	            System.arraycopy(messageDigest, 0, result, 0, 16);
	            return result;
			} catch (NoSuchAlgorithmException e) {
				throw new RuntimeException(e.toString(), e);
			}
        }

        
        private static byte[] encrypt(byte[] raw, byte[] clear) {
			try {
	            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
	                Cipher cipher = Cipher.getInstance("AES");
	            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
	            byte[] encrypted = cipher.doFinal(clear);
	                return encrypted;
			} catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
				throw new RuntimeException(e.toString(), e);
			}
        }

        private static byte[] decrypt(byte[] raw, byte[] encrypted) {
			try {
	            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
	                Cipher cipher = Cipher.getInstance("AES");
	            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
	            byte[] decrypted = cipher.doFinal(encrypted);
	                return decrypted;
			} catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
				throw new RuntimeException(e.toString(), e);
			}
        }

        public static String toHex(String txt) {
                return toHex(txt.getBytes(StandardCharsets.UTF_8));
        }
        public static String fromHex(String hex) {
                return new String(toByte(hex), StandardCharsets.UTF_8);
        }
        
        public static byte[] toByte(String hexString) {
                int len = hexString.length()/2;
                byte[] result = new byte[len];
                for (int i = 0; i < len; i++)
                        result[i] = Integer.valueOf(hexString.substring(2*i, 2*i+2), 16).byteValue();
                return result;
        }

        public static String toHex(byte[] buf) {
                if (buf == null)
                        return "";
                StringBuffer result = new StringBuffer(2*buf.length);
                for (int i = 0; i < buf.length; i++) {
                        appendHex(result, buf[i]);
                }
                return result.toString();
        }
        private final static String HEX = "0123456789ABCDEF";
        private static void appendHex(StringBuffer sb, byte b) {
                sb.append(HEX.charAt((b>>4)&0x0f)).append(HEX.charAt(b&0x0f));
        }
        
}