package dnsrelay;

public class Convertshort{
	public static short byte2Short(byte[] array, int start)
	{
		final int length = 2;
		short result = 0;

		byte loop;
		for (int i = start; i < start + length; i++) {
			loop = array[i];
			int offSet = length - (i - start) -1; //(i - start);
			result += (loop & 0xFF) << (8 * offSet);
		}

		return result;
	}

	/**
	 * Short to Byte
	 * @param value Short
	 * @param array Byte array
	 * @param start Start index in array
	 * @return Offset
	 */
	public static int short2Byte(short value, byte[] array, int start) {
		final int length = 2;
		byte loop;
		for (int i = start; i < start + length; i++) {
			int offSet = length - (i - start) -1;
			loop = (byte) ((byte) (value >> (8 * offSet)) & 0xFF);
			array[i] = loop;
		}
		return length;
	}

	/**
	 * Short to Byte
	 * @param value Short
	 * @return Byte[]
	 */
	public static byte[] short2Byte(short value) {
		byte[] array = new byte[2];
		short2Byte(value, array, 0);
		return array;
	}
}