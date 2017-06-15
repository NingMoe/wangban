package syan.app.icity.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import com.icore.util.DaoFactory;

import sun.net.www.protocol.http.HttpURLConnection;

public class URLServers {
	
	public static URLServers getInstance() {
		return DaoFactory.getDao(URLServers.class.getName());
	}

	public String callURLPost(String pURL,String param) throws IOException {

		StringBuffer result = new StringBuffer();
		try {
			URL url = new URL(pURL);
			byte[] entity =param.getBytes("UTF-8");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("Content-Type", "text/xml;charset=utf-8");
			conn.setConnectTimeout(10000);
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true); 
			conn.setRequestProperty("User-Agent", "directclient");
			conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");//用于指导实体数据的内容类型  
		    conn.setRequestProperty("Content-Length",String.valueOf(entity.length));//entity为要传输的数据格式为  title=hello&time=20//可以对该数据编码  
		    //传递参数 方式1
		    OutputStream outStream=conn.getOutputStream();
		    outStream.write(entity);
		    outStream.flush();
		    outStream.close();
		    //传递参数 方式2
		   /* OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
			String str = String.format(Locale.CHINA, param);
			out.write(str);
			out.flush();
			out.close();*/
		   // 接收数据
			InputStream is = conn.getInputStream();
			InputStreamReader isr = new InputStreamReader(is,"UTF-8");
			BufferedReader in = new BufferedReader(isr);
			String line = null;
			while ((line = in.readLine()) != null) {
				result.append(line);
			}
			if(isr!=null){
				in.close();
				isr.close();
			}
			if (is != null) {
				is.close();
			}
			if (conn != null) {
				conn.disconnect();
			}
		}catch (MalformedURLException e) {
			e.printStackTrace();
			throw new MalformedURLException("服务网络不稳定，请稍后再试！");
		}catch (IOException e) {
			e.printStackTrace();
			throw new IOException("服务网络不稳定，请稍后再试！");
		}
		return result.toString();
	}
	
	
	
}
