package dnsrelay;

public class Convertint {
	/**
	 * Byte to Integer
	 */
	public static int byte2Int(byte[] array, int start)
	{
		return array[start] & 0xFF;
	}

	/**
	 * Integer to Byte
	 * @param value Integer
	 * @param array Byte array
	 * @param start Start index in array
	 * @return Offset
	 */
	public static int int2Byte(int value, byte[] array, int start) {
		final int length = 4;
		byte loop;
		for (int i = start; i < start + length; i++) {
			int offSet = length - (i - start) -1;
			loop = (byte) (value >> (8 * offSet)) ;
			array[i] = loop;
		}
		return length;
	}

	/**
	 * Integer to Byte
	 * @param value Integer
	 * @return byte[]
	 */
	public static byte[] int2Byte(int value) {
		byte[] array = new byte[4];
		int2Byte(value, array, 0);
		return array;
	}

}