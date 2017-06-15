package app.util;

import java.io.*;
import java.net.*;
import java.security.*;
import java.security.cert.*;
import java.util.Map;

import javax.net.ssl.*;

public class HttpClientUtil {
	myX509TrustManager xtm = new myX509TrustManager();
	myHostnameVerifier hnv = new myHostnameVerifier();

	public HttpClientUtil() {
		SSLContext sslContext = null;
		try {
			sslContext = SSLContext.getInstance("TLS");
			X509TrustManager[] xtmArray = new X509TrustManager[] { xtm };
			sslContext.init(null, xtmArray, new java.security.SecureRandom());
		} catch (GeneralSecurityException gse) {
		}
		if (sslContext != null) {
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
		}
		HttpsURLConnection.setDefaultHostnameVerifier(hnv);
	}

	public String inputStreamToString(InputStream is, String charSet) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(is, charSet));
		StringBuffer buffer = new StringBuffer();
		String line = "";
		while ((line = in.readLine()) != null) {
			buffer.append(line);
			buffer.append("\r\n");
		}
		in.close();
		return buffer.toString();
	}

	public String getResult(String url, String param) {
		return getResult(url, param, false);
	}

	public String getResult(String url, Boolean postType) {
		return getResult(url, "", postType);
	}

	public String getResult(String url, String param, Boolean postType) {
		String content = "";
		HttpURLConnection connection = null;
		OutputStreamWriter outer = null;
		try {
			URL restURL = null;
			try {
				restURL = new URL(url);
			} catch (Exception me) {
				me.printStackTrace();
			}

			if (restURL != null) {
				connection = (HttpURLConnection) restURL.openConnection();
				connection.setConnectTimeout(1000 * 100);
				connection.setReadTimeout(1000 * 150);

				connection.setDoOutput(true);
				if (postType) {
					connection.setRequestMethod("POST");
				}
				connection.setRequestProperty("Content-Type", "application/json");
				if ((param != null) && (param.length() > 1)) {
					connection.setRequestMethod("POST");
					connection.setDoOutput(true);
					outer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
					outer.write(param);
					outer.flush();
					outer.close();
				}
				InputStream ips = connection.getInputStream();
				content = inputStreamToString(ips, "UTF-8");
				ips.close();

				connection.disconnect();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (outer != null) {
				try {
					outer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (connection != null) {
				connection.disconnect();
			}
		}
		return content;
	}

	public String httpInvoke(String url, String methodm, String data) throws IOException {
		Map invokeResult = null;
		StringBuilder response = new StringBuilder();
		URL httpurl = new URL(url);
		HttpURLConnection hc = (HttpURLConnection) httpurl.openConnection();
		String Method = methodm.toUpperCase();
		hc.setRequestMethod(Method);
		hc.setDoInput(true);
		if ("POST".equals(Method)) {
			hc.setDoOutput(true);
			if (data != null) {
				hc.setRequestProperty("Content-Length", String.valueOf(data.length()));
			}
		}
		hc.setRequestProperty("Content-Type", "application/json");
		hc.setRequestProperty("Charset", "UTF-8");

		hc.connect();
		if ("POST".equals(Method)) {
			OutputStream ops = hc.getOutputStream();
			byte[] buff;
			if (data != null) {
				buff = data.getBytes("UTF-8");
				ops.write(buff);
			}
			ops.flush();
			ops.close();
		}
		int code = hc.getResponseCode();
		if (code == 200) {
			InputStream ins = null;
			InputStreamReader isr = null;
			try {
				ins = hc.getInputStream();
				isr = new InputStreamReader(ins, "UTF-8");
				char[] cbuf = new char[1024];
				int i = isr.read(cbuf);
				while (i > 0) {
					response.append(new String(cbuf, 0, i));
					i = isr.read(cbuf);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(isr!=null){
					isr.close();
				}
				if(ins!=null){
					ins.close();
				}
			}
		} else {
			InputStream ins = null;
			InputStreamReader isr = null;
			try {
				ins = hc.getErrorStream();
				isr = new InputStreamReader(ins, "UTF-8");
				char[] cbuf = new char[1024];
				int i = isr.read(cbuf);
				while (i > 0) {
					response.append(new String(cbuf, 0, i));
					i = isr.read(cbuf);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (isr != null) {
						isr.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					if (ins != null) {
						ins.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		hc.disconnect();
		return response.toString();
	}
}

class myX509TrustManager implements X509TrustManager {
	public myX509TrustManager() {
	}

	public void checkClientTrusted(X509Certificate[] chain, String authType) {
	}

	public void checkServerTrusted(X509Certificate[] chain, String authType) {
		// System.out.println("cert: " + chain[0].toString() + ", authType: " +
		// authType);
	}

	public X509Certificate[] getAcceptedIssuers() {
		return new X509Certificate[0];
	}
}

class myHostnameVerifier implements HostnameVerifier {
	public myHostnameVerifier() {
	}

	public boolean verify(String hostname, SSLSession session) {
		// System.out.println("hostname: " + hostname);
		return true;
	}
}
