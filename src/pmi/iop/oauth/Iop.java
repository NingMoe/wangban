package iop.oauth;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Map;

import iop.oauth.http.HttpClient;

public class Iop implements java.io.Serializable {

	private static final long serialVersionUID = 4282616848978535016L;

	public HttpClient client = new HttpClient();

	public void setToken(String token) {
		client.setToken(token);
	}

	/**
	 * 加密算法
	 */
	public static String md5(String plainText) {
		if (plainText == null)
			plainText = "";
		byte[] temp = plainText.getBytes();
		MessageDigest md;
		// 返回结果
		StringBuffer buffer = new StringBuffer();
		try {
			// 进行MD5散列
			md = MessageDigest.getInstance("md5");
			md.update(temp);
			temp = md.digest();
			// 将散列的结果转换为Hex字符串
			int i = 0;
			for (int offset = 0; offset < temp.length; offset++) {
				i = temp[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buffer.append("0");
				buffer.append(Integer.toHexString(i));
			}
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
		// 返回
		return buffer.toString();
	}

	public String sig(Map parameter, String secret) {
		StringBuilder sb = new StringBuilder();
		if (parameter != null) {
			Object[] keys = parameter.keySet().toArray();
			Arrays.sort(keys);
			String key;
			for (int i = 0; i < keys.length; i++) {
				key = (String) keys[i];
				sb.append(key).append("=").append(parameter.get(key));
			}
		}
		String reqCode = md5(sb.toString());
		if (secret == null) {
			return null;
		}
		String toSign = md5(reqCode + secret);
		return toSign;
	}
}