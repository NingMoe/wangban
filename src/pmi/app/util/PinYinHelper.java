package app.util;

import java.io.UnsupportedEncodingException;

public class PinYinHelper {
	/**
	 * 存放国标一级汉字不同读音的起始区位码
	 */
	static final int[] secPosValueList = { 1601, 1637, 1833, 2078, 2274, 2302,
			2433, 2594, 2787, 3106, 3212, 3472, 3635, 3722, 3730, 3858, 4027,
			4086, 4390, 4558, 4684, 4925, 5249, 9999 };

	/**
	 * 存放国标一级汉字不同读音的起始区位码对应读音
	 */
	static final char[] firstLetter = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
			'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'w', 'x',
			'y', 'z' };

	/**
	 * 获取一个字符串的拼音码
	 * 
	 * @param oriStr
	 * @return
	 */
	public static String getFirstLetter(String oriStr) {
		return getFirstLetter(oriStr, 0);
	}

	/**
	 * 获取一个字符串的拼音码
	 * 
	 * @param oriStr
	 * @param scale
	 * 返回的拼音字头的位数
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static String getFirstLetter(String oriStr, int scale){
		String str = oriStr.toLowerCase();
		StringBuffer buffer = new StringBuffer();
		char ch;
		char[] temp;
		try {
			for (int i = 0; i < str.length(); i++) {
				// 依次处理str中每个字符
				ch = str.charAt(i);
				temp = new char[] { ch };
				byte[] uniCode = new String(temp).getBytes("GB2312");
				int bt = uniCode[0]&0xff;
				if (0<=bt && bt<=255) {
					// 非汉字
					buffer.append(temp);
				} else {
					buffer.append(convert(uniCode));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (buffer.toString().length() < scale || scale == 0) {
			return buffer.toString().toUpperCase();
		} else {
			return buffer.toString().substring(0, scale).toUpperCase();
		}
	}

	/**
	 * 获取一个汉字的拼音首字母
	 * 
	 * @param bytes
	 * @return
	 */
	static char convert(byte[] bytes) {
		char result = '-';
		int secPosValue = 0;
		int i;
		for (i = 0; i < bytes.length; i++) {
			bytes[i] -= 160;
		}
		secPosValue = bytes[0] * 100 + bytes[1];
		for (i = 0; i < 23; i++) {
			if (secPosValue >= secPosValueList[i]
					&& secPosValue < secPosValueList[i + 1]) {
				result = firstLetter[i];
				break;
			}
		}
		return result;
	}
	
	public static void main(String[] args) {
		System.out.println(PinYinHelper.getFirstLetter("中俄战争"));
		//System.getProperties().list(System.out); 
	}
	
}
