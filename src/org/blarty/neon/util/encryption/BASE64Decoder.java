
package org.jini.projects.neon.util.encryption;


/**
 * Decodes a base64 String buffer into it's original byte array sequence
 
 */
public class BASE64Decoder {

	private static final int EIGHT_BIT_MASK = 0xFF;

	public byte[] decodeBuffer(String data) {
		StringWrapper wrapper = new StringWrapper(data);

		int byteArrayLength = wrapper.getUsefulLength() * 3 / 4;
		byte result[] = new byte[byteArrayLength];

		int byteTriplet = 0;
		int byteIndex = 0;

		while (byteIndex + 2 < byteArrayLength) {
			byteTriplet = mapCharToInt(wrapper.getNextUsefulChar());
			byteTriplet <<= 6;
			byteTriplet |= mapCharToInt(wrapper.getNextUsefulChar());
			byteTriplet <<= 6;
			byteTriplet |= mapCharToInt(wrapper.getNextUsefulChar());
			byteTriplet <<= 6;
			byteTriplet |= mapCharToInt(wrapper.getNextUsefulChar());

			result[byteIndex + 2] = (byte) (byteTriplet & EIGHT_BIT_MASK);
			byteTriplet >>= 8;
			result[byteIndex + 1] = (byte) (byteTriplet & EIGHT_BIT_MASK);
			byteTriplet >>= 8;
			result[byteIndex] = (byte) (byteTriplet & EIGHT_BIT_MASK);
			byteIndex += 3;
		}
		if (byteIndex == byteArrayLength - 1) {
			byteTriplet = mapCharToInt(wrapper.getNextUsefulChar());
			byteTriplet <<= 6;
			byteTriplet |= mapCharToInt(wrapper.getNextUsefulChar());
			byteTriplet >>= 4;
			result[byteIndex] = (byte) (byteTriplet & EIGHT_BIT_MASK);
		}
		if (byteIndex == byteArrayLength - 2) {
			byteTriplet = mapCharToInt(wrapper.getNextUsefulChar());
			byteTriplet <<= 6;
			byteTriplet |= mapCharToInt(wrapper.getNextUsefulChar());
			byteTriplet <<= 6;
			byteTriplet |= mapCharToInt(wrapper.getNextUsefulChar());

			byteTriplet >>= 2;
			result[byteIndex + 1] = (byte) (byteTriplet & EIGHT_BIT_MASK);
			byteTriplet >>= 8;
			result[byteIndex] = (byte) (byteTriplet & EIGHT_BIT_MASK);

		}
		return result;
	}

	private int mapCharToInt(char c) {
		if (c >= 'A' && c <= 'Z') {
			return c - 'A';
		}
		if (c >= 'a' && c <= 'z') {
			return (c - 'a') + BASE64Encoder.LOWER_CASE_A_VALUE;
		}
		if (c >= '0' && c <= '9') {

			return (c - '0') + BASE64Encoder.ZERO_VALUE;
		}
		if (c == '+') {
			return BASE64Encoder.PLUS_VALUE;

		}
		if (c == '/') {
			return BASE64Encoder.SLASH_VALUE;
		}
		throw new IllegalArgumentException(
			c + " is not a valid Base64 character");

	}

	/**
	 * Provides a class that extracts Base64 values for characters
	 * @author calum.mackay
	 *
	 */
	private class StringWrapper {
		private String mString;

		private int mIndex = 0;

		private int mUsefulLength;

		private boolean isUsefulChar(char c) {
			return (c >= 'A' && c <= 'Z')
				|| (c >= 'a' && c <= 'z')
				|| (c >= '0' && c <= '9')
				|| (c == '+')
				|| (c == '/');
		}

		public StringWrapper(String s) {
			mString = s;
			mUsefulLength = 0;
			int length = mString.length();
			for (int i = 0; i < length; i++) {
				if (isUsefulChar(mString.charAt(i))) {
					mUsefulLength++;
				}
			}
		}

		public int getUsefulLength() {
			return mUsefulLength;
		}

		public char getNextUsefulChar() {
			char result = '_';
			while (!isUsefulChar(result))
				result = mString.charAt(mIndex++);
			return result;
		}
	}
}
