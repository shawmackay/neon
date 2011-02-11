
package org.jini.projects.neon.util.encryption;

import java.util.Arrays;


/**
 * @author calum.mackay
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class BASE64Encoder {
	final static int LOWER_CASE_A_VALUE = 26;

	final static int ZERO_VALUE = 52;

	final static int PLUS_VALUE = 62;

	final static int SLASH_VALUE = 63;

	private final static int SIX_BIT_MASK = 63;

	private int convertUnsignedByteToInt(byte b) {
		if (b >= 0)
			return (int) b;
		return 256 + b;
	}

	public String encode(byte[] data) {
		int charCount = ((data.length * 4) / 3) + 4;

		StringBuffer result = new StringBuffer((charCount * 77) / 76);
		int byteArrayLength = data.length;
		int byteArrayIndex = 0;
		int byteTriplet = 0;

		while (byteArrayIndex < byteArrayLength - 2) {
			byteTriplet = convertUnsignedByteToInt(data[byteArrayIndex++]);
			byteTriplet <<= 8;
			byteTriplet |= convertUnsignedByteToInt(data[byteArrayIndex++]);
			byteTriplet <<= 8;
			byteTriplet |= convertUnsignedByteToInt(data[byteArrayIndex++]);

			byte b4 = (byte) (SIX_BIT_MASK & byteTriplet);
			byteTriplet >>= 6;
			byte b3 = (byte) (SIX_BIT_MASK & byteTriplet);
			byteTriplet >>= 6;
			byte b2 = (byte) (SIX_BIT_MASK & byteTriplet);
			byteTriplet >>= 6;
			byte b1 = (byte) (SIX_BIT_MASK & byteTriplet);

			result.append(mapByteToChar(b1));
			result.append(mapByteToChar(b2));
			result.append(mapByteToChar(b3));
			result.append(mapByteToChar(b4));

			if (byteArrayIndex % 57 == 0) {
				result.append("\n");
			}
		}
		if (byteArrayIndex == byteArrayLength - 1) {
			byteTriplet = convertUnsignedByteToInt(data[byteArrayIndex++]);

			byteTriplet <<= 4;

			byte b2 = (byte) (SIX_BIT_MASK & byteTriplet);
			byteTriplet >>= 6;
			byte b1 = (byte) (SIX_BIT_MASK & byteTriplet);
			result.append(mapByteToChar(b1));
			result.append(mapByteToChar(b2));

			result.append("==");
		}

		if (byteArrayIndex == byteArrayLength - 2) {
			byteTriplet = convertUnsignedByteToInt(data[byteArrayIndex++]);
			byteTriplet <<= 8;
			byteTriplet |= convertUnsignedByteToInt(data[byteArrayIndex++]);
			byteTriplet <<= 2;
			byte b3 = (byte) (SIX_BIT_MASK & byteTriplet);
			byteTriplet >>= 6;

			byte b2 = (byte) (SIX_BIT_MASK & byteTriplet);
			byteTriplet >>= 6;
			byte b1 = (byte) (SIX_BIT_MASK & byteTriplet);
			result.append(mapByteToChar(b1));
			result.append(mapByteToChar(b2));
			result.append(mapByteToChar(b3));
			result.append("=");
		}

		return result.toString();
	}

	private char mapByteToChar(byte b) {
		if (b < LOWER_CASE_A_VALUE) {
			return (char) ('A' + b);
		}

		if (b < ZERO_VALUE) {
			return (char) ('a' + (b - LOWER_CASE_A_VALUE));
		}
		if (b < PLUS_VALUE) {

			return (char) ('0' + (b - ZERO_VALUE));
		}
		if (b == PLUS_VALUE)
			return '+';
		if (b == SLASH_VALUE)
			return '/';

		throw new IllegalArgumentException(
			"Byte " + new Integer(b) + " is not a valid Base64 value");
	}

	public static void main(String args[]) throws Exception {
		BASE64Encoder encoder = new BASE64Encoder();

		BASE64Decoder decoder = new BASE64Decoder();

		String encoded = encoder.encode("abcdefghijkl".getBytes());
		System.out.println("Encoded: " + encoded);
		System.out.println(new String(decoder.decodeBuffer(encoded)));

		for (int j = 0; j < 10; j++) {
			byte test[] = new byte[(int) (100000 * Math.random())];
			for (int i = 0; i < test.length; i++) {
				test[i] = (byte) (256 * Math.random());
			}
			String string = encoder.encode(test);

			byte result[] = decoder.decodeBuffer(string);

			if (!Arrays.equals(test, result) || test.length != result.length) {
				System.out.println("ARRAY SET " + j + " DO NOT MATCH!!!!");
			} else {
				System.out.print("\rArray set " + j + " matched");
			}
		}
	}
}
