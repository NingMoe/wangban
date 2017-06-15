package app.icity.sync;
/**
 * 文件上传公用类
 * @author wangwei
 * @date 2014-10-28
 * @version 1.0
 * @description 
 */
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import action.onlineapply.applyinfo;

import com.icore.util.SecurityConfig;
import com.icore.util.StringUtil;
import com.inspur.util.PathUtil;


@SuppressWarnings("deprecation")
public class UploadUtil {


	private static final String CHARSET = HTTP.UTF_8;
	private static HttpClient customerHttpClient;
	private static Log _log = LogFactory.getLog(applyinfo.class);
	//2014-12-10 09:02:11 KL private static final String TAG = "UploadUtil";

	public UploadUtil() {

	}

	/**
	 * 初始化httpclient
	 * 
	 * @return
	 */
	public static synchronized HttpClient getHttpClient() {
		if (null == customerHttpClient) {
			HttpParams params = new BasicHttpParams();
			// 设置一些基本参数
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, CHARSET);
			HttpProtocolParams.setUseExpectContinue(params, true);
			HttpProtocolParams
					.setUserAgent(
							params,
							"Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83) "
									+ "AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");
			// 超时设置
			/* 从连接池中取连接的超时时间 */
			ConnManagerParams.setTimeout(params, 10000);
			/* 连接超时 */
			HttpConnectionParams.setConnectionTimeout(params, 20000);
			/* 请求超时 */
			HttpConnectionParams.setSoTimeout(params, 20000);

			// 设置我们的HttpClient支持HTTP和HTTPS两种模式
			SchemeRegistry schReg = new SchemeRegistry();
			schReg.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			schReg.register(new Scheme("https", SSLSocketFactory
					.getSocketFactory(), 443));

			// 使用线程安全的连接管理来创建HttpClient
			ClientConnectionManager conMgr = new ThreadSafeClientConnManager(
					params, schReg);
			customerHttpClient = new DefaultHttpClient(conMgr, params);
		}
		return customerHttpClient;
	}

	/**
	 * httpclient+MultipartEntity方式上传
	 * 
	 * @param params
	 * @param pathToOurFile
	 * @param urlServer
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static String post(final Map<String, String> params,
			final ArrayList<String> fileURL_list, final String urlServer) {

		if (fileURL_list == null ) { // 如果文件列表中没有数据，返回null
			return null;
		}
		int list_size = fileURL_list.size();
		if (list_size<=0) {
			return null;
		}
		ExecutorService executorService =null;
		try {
			// 上传队列
			executorService = Executors.newFixedThreadPool(list_size);
			for ( int i = 0; i < list_size; i++) {
				//2014-12-10 09:00:50 KL  final String fileUrl = fileURL_list.get(i);
				Future<?> a = executorService.submit(new Runnable() {
					public void run() {
						//2014-12-10 09:00:58 KL String result = startUploadService(params,fileUrl,urlServer);
//						System.out.println(result);
//						sendMessage(NetThread.UPLOAD_FILES, DiskService.changeToObject(result), handler);
					}
				});
			}
			executorService.shutdown();
		} catch (Exception e) {
			System.out.println("UploadUtils-->post:"+e.getMessage());
		}finally{
				
		}
		return "测试";
	}

	/**
	 * 向服务器发送文件
	 * 
	 * @param file_map
	 * @param file_url
	 * @param server_url
	 * @return
	 */
	public static String startUploadService(Map<String, String> params,
			String file_url, String server_url) {
		try {
			// 开启上传队列
			File file = new File(file_url);
			HttpClient httpclient = new DefaultHttpClient();
			// 设置通信协议版本
			httpclient.getParams().setParameter(
					CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
			// 文件参数设置
			HttpPost httppost = new HttpPost(server_url);
			MultipartEntity mpEntity = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE, null, Charset.forName(CHARSET));
			if (params != null && !params.isEmpty()) {
				// 编码参数
				for (Map.Entry<String, String> k : params.entrySet()) {
					StringBody valueBody = new StringBody(k.getValue(),Charset.forName(CHARSET));
					mpEntity.addPart(k.getKey(), valueBody);
				}
			}
			ContentBody cbFile = new FileBody(file);
			mpEntity.addPart("file", cbFile);
			httppost.setEntity(mpEntity);
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity resEntity = response.getEntity();

			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				return "{\"code\":\"4000\", \"msg\":\"URL请求失败\", \"result\":\'"
						+ response.getStatusLine().toString() + "\'}";
			}
			String result = (resEntity == null) ? null : EntityUtils.toString(resEntity,
					CHARSET);
			return result;
		} catch (UnsupportedEncodingException e) {
			System.out.println( e.getMessage());
			return "{\"code\":\"4002\", \"msg\":\"URL请求失败\", \"result\":\'UnsupportedEncodingException:"
					+ e.getMessage() + "\'}";
		} catch (ClientProtocolException e) {
			System.out.println(e.getMessage());
			return "{\"code\":\"4003\", \"msg\":\"URL请求失败\", \"result\":\'ClientProtocolException:"
					+ e.getMessage() + "\'}";
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return "{\"code\":\"4001\", \"msg\":\"URL请求失败\", \"result\":\'IOException:"
					+ e.getMessage() + "\'}";
		}
	}
	
	//网盘下载图片代码
	public static void downloadFile(String fileName,String doc_id,String fileType)
	throws Exception {
		String url = SecurityConfig.getString("NetDiskDownloadAddress")+doc_id;
		String propath =PathUtil.getWebPath();
		String dir="";
		//图片新闻的图片、附件的下载地址
		if(fileType.equals("1")||fileType.equals("2")){
		dir= propath+"file"+File.separator +"upload"+File.separator ;
		}
		//正文图片的下载地址
		if(fileType.equals("3")){
			String webSiteContexPath=SecurityConfig.getString("webSiteContexPath");
			dir= propath+"static"+File.separator+"data"+File.separator;
			if(StringUtil.isNotEmpty(webSiteContexPath)){
				dir= propath+webSiteContexPath+File.separator+"static"+File.separator+"data"+File.separator;
			}
		}
		File filecheck = new File(dir+fileName);
		if(!filecheck.exists()){
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);
		HttpResponse httpResponse = client.execute(httpget);
		HttpEntity entity = httpResponse.getEntity();
		InputStream is = entity.getContent();
		File directory = new File(dir);
		if(!directory.exists()){
		directory.mkdirs();//创建目录
		}
		FileOutputStream fileout = new FileOutputStream(dir + fileName);
		try {
		byte[] buffer=new byte[50];
		int ch = 0;
		while ((ch = is.read(buffer)) != -1) {					   
		    fileout.write(buffer, 0, ch);
		}
		}  catch (Exception e) {
		e.printStackTrace();
		}finally{
		fileout.close();
		is.close();
     }   
		}
		}
	
}


