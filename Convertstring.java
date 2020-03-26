package dnsrelay;

public class Convertstring{

	public static String byte2String(byte [] b, int start, int length) {
		return String.valueOf(byte2Char(b, start, length));
	}

	/**
	 * 字符串转换为字节
	 * @param s 字符串
	 * @param b 字节
	 * @param start 字节中起点
	 * @return 字节中的偏移量
	 */
	public static int string2Byte(String s, byte [] b, int start)
	{
		if (s == null) {
			return 0;
		}
		else if (s.length() <= 0) {
			return 0;
		}
		else {
			char [] c = s.toCharArray();
			return char2Byte(c, b, start, c.length);
		}
	}
public static char[] byte2Char(byte [] b, int start, int length) {
		char [] c = new char[length];
		for(int i = 0; i < length; i++) {
			c[i] = (char) b[start + i];
		}
		return c;
	}

	/**
	 * 字符串转换为字节
	 * @param c 字符串
	 * @param b 字节
	 * @param start 字节中起点
	 * @param length 字符串长度
	 * @return 字节中的偏移量
	 */
	public static int char2Byte(char[] c, byte [] b, int start, int length) {
		for(int i = 0; i < length; i++) {
			b[start + i] = (byte) c[i];
		}
		return length;
	}

	/**
	 * 字符串转换为字节
	 * @param b 字节
	 * @param start 起点
	 * @param length 长度
	 * @return 字符串
	 */
	
}