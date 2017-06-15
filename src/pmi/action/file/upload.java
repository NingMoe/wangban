package action.file;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;

import com.icore.http.HttpException;
import com.icore.http.HttpResponseStatus;
import com.icore.util.SecurityConfig;
import com.inspur.base.BaseAction;

public class upload extends BaseAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected boolean IsVerify() {
		return false;
	}
	
	public boolean handler(Map<String,Object> data) throws HttpException{
		//如果没有配置DownloadServiceUrl，则退出走静态文件
		String downloadUrl = SecurityConfig.getInstance().getString("DownloadServiceUrl","");
		if(downloadUrl.length()<3){
			throw new HttpException(HttpResponseStatus.CONTINUE);
		}else{
			//走代理地址
			String path = this.getUrl();
			String url = downloadUrl + path;

			HttpClient client = new HttpClient();
			HttpMethod method = new GetMethod(url);
			String filename = path.substring(path.lastIndexOf("/")+1);
			try {

				//设置超时
				client.getHttpConnectionManager().getParams().setConnectionTimeout(60000);
				client.getHttpConnectionManager().getParams().setSoTimeout(60000);
				
				client.executeMethod(method);
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				if (method.getStatusCode() == HttpStatus.SC_OK) {
					InputStream reader = method.getResponseBodyAsStream();
					
					byte[] b = new byte[1024];
					int n;
					while ((n = reader.read(b)) != -1) {
						bos.write(b, 0, n);
					}
					reader.close();
					bos.close();
				}
				//如果找不到文件则抛出404错误
				if(bos.toByteArray().length>0){
					this.write(bos.toByteArray());
	
					//如果是图片则直接显示，如果是其他文件则下载
					Pattern wup = Pattern.compile(".+\\.(jpg|gif|png|jpeg|bmp)$", Pattern.CASE_INSENSITIVE);
					Matcher fm = wup.matcher(path.toLowerCase());
					
					if(!fm.find()){
						this.setContentType("application");
						this.setHeader("Content-Disposition","attachment; filename="+filename);
					}else{
						this.setContentType("image/jpeg");
					}
				}else{
					this.getHttpAdapter().setStatus(HttpResponseStatus.NOT_FOUND);
					String fileName = path.substring(path.lastIndexOf("/")+1,path.lastIndexOf("."));
					this.sendRedirect("not_found_"+fileName);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				method.releaseConnection();
			}
		}
		return false;
	}

}
