package at.fhooe.drugme.utils;

import android.util.Base64;

/**
 * @author S1210455002
 * 
 */
public class Converter {
	
	static final String HEXES = "0123456789ABCDEF";

	/**
	 * converts byte array to hex string
	 * 
	 * @param raw
	 * @return
	 */
	public static String getHex(byte[] raw) {
		if (raw == null) {
			return null;
		}
		final StringBuilder hex = new StringBuilder(2 * raw.length);
		for (final byte b : raw) {
			hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(
					HEXES.charAt((b & 0x0F)));
		}
		return hex.toString();
	}

	/**
	 * 
	 * @param input
	 * @return
	 */
	public static byte[] hexStringToByteArray(String input) {
		int len = input.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(input.charAt(i), 16) << 4) + Character
					.digit(input.charAt(i + 1), 16));
		}
		return data;
	}

	/**
	 * 
	 * @param first
	 * @param second
	 * @return
	 */
	public static byte[] concatArray(byte[] first, byte[] second) {
		byte[] result = new byte[first.length + second.length];
		System.arraycopy(first, 0, result, 0, first.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

	/**
	 * 
	 * @param input
	 * @return
	 */
	public static String base64Encode(byte[] input) {
		return new String(Base64.encode(input, 0));
	}

	/**
	 * 
	 * @param encoded
	 * @return
	 */
	public static String base64Decode(byte[] encoded) {
		return new String(Base64.decode(encoded, 0));
	}

	/**
	 * 
	 * @param input
	 * @return
	 */
	public static String base64Encode(String input) {
		return new String(Base64.encode(input.getBytes(), 0));
	}

	/**
	 * 
	 * @param input
	 * @return
	 */
	public static String base64Decode(String input) {
		return new String(Base64.decode(input, 0));
	}
	
	/**
	 * 
	 * @param input
	 * @return
	 */
	public static byte[] base64DecodeToBytes(String input){
		return Base64.decode(input, 0);
	}
	
	/**
	 * 
	 * @param input
	 * @return
	 */
	public static byte[] base64EcodeToBytes(byte[] input){
		return Base64.encode(input, 0);
	}

}
