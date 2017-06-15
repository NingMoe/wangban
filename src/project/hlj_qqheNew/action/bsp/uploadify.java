package hlj_qqheNew.action.bsp;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import app.icity.sync.UploadUtil;

import com.icore.http.HttpResponseStatus;
import com.icore.util.StringUtil;
import com.inspur.base.BaseAction;
import com.inspur.http.util.UrlHelper;
import com.inspur.util.PathUtil;
import com.inspur.util.SecurityConfig;
import com.inspur.util.Tools;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeUtility;

public class uploadify extends BaseAction {
	protected boolean IsVerify() {
		return false;
	}
	
	protected static Log _log = LogFactory.getLog(uploadify.class);
	private String __uid__ = "";
	
	public boolean handler(Map<String, Object> data) {
		String action=this.getParameter("action");
		String uid = this.getParameter("uid");
		if("download".equals(action)){
			download();
		}else if("downloadNew".equals(action)){
			downloadNew();
		}else if("downloadagent".equals(action)){
			downloadagent();
		}else if("uploadagent".equals(action)){
			uploadagent();
		}else if("uploadagent4wp_cyfw".equals(action)){
			__uid__ = this.getParameter("ucid");
			uploadagent4wp_cyfw();
		}else if("uploadagent4wp".equals(action)){
			if("".equals(uid)){
				uploadagent4wp();
			}else{
				uploadagent4wp(uid);
			}
			
		}else{
			upload();
		}
		return false;	
	}
	
	/**
	 * 上传服务代理，解决swfupload跨域问题
	 */
	private void uploadagent(){
		String uploadUrl = SecurityConfig.getString("UploadServiceUrl");
		//如果没有配置上传服务地址，则直接上传到本地
		if(org.apache.commons.lang.StringUtils.isEmpty(uploadUrl)){
			upload();
		}else{
			Map<String, File> m = this.getFileData();
			try {
				String result =doPost(uploadUrl,"UTF-8",m);
				this.write(result);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 上传服务代理，解决swfupload跨域问题
	 */
	private void uploadagent4wp(String uid){
		String uploadUrl = SecurityConfig.getString("NetDiskAddress");
		//如果没有配置上传服务地址，则直接上传到本地
		if(org.apache.commons.lang.StringUtils.isEmpty(uploadUrl)){
			upload();
		}else{
			Map<String, File> m = this.getFileData();
			Iterator<String> it = m.keySet().iterator();
			//真实文件名
			String fileName=this.getPostData().get("Filename");
			String _fileName = fileName;
			while (it.hasNext()) {
				String key = it.next();
				File file = m.get(key);
				fileName =file.getParent() + File.separator +fileName;	
				moveFile(file, fileName);
			}
			try {
				Map<String,String> params = new HashMap<String, String>();
			//	params.put("uid", SecurityConfig.getString("NetDiskUid"));
				params.put("uid", uid);
				params.put("type", "doc");
				params.put("folder_name", "//");
				String scc=UploadUtil.startUploadService(params, fileName, uploadUrl);
				JSONObject jo = new JSONObject();
				fileName = StringUtils.replace(fileName, "\\", "/");					
				jo.put("path", fileName);
				jo.put("scc", JSONObject.fromObject(scc));	
				jo.put("url", fileName);
				jo.put("error", 0);
				jo.put("name", _fileName);

				this.write(jo.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	private void uploadagent4wp()
	  {
	    String uploadUrl = SecurityConfig.getString("NetDiskAddress");

	    if (org.apache.commons.lang.StringUtils.isEmpty(uploadUrl)) {
	      upload();
	    } else {
	      Map m = getFileData();
	      Iterator it = m.keySet().iterator();

	      String fileName = (String)getPostData().get("Filename");
	      String _fileName = fileName;
	      while (it.hasNext()) {
	        String key = (String)it.next();
	        File file = (File)m.get(key);
	        fileName = file.getParent() + File.separator + fileName;
	        moveFile(file, fileName);
	      }
	      try {
	        Map params = new HashMap();
	        params.put("uid", SecurityConfig.getString("NetDiskUid"));
	        params.put("type", "doc");
	        params.put("folder_name", "//");
	        String scc = UploadUtil.startUploadService(params, fileName, uploadUrl);
	        JSONObject jo = new JSONObject();
	        fileName = org.springframework.util.StringUtils.replace(fileName, "\\", "/");
	        jo.put("path", fileName);
	        jo.put("scc", JSONObject.fromObject(scc));
	        jo.put("url", fileName);
	        jo.put("error", Integer.valueOf(0));
	        jo.put("name", _fileName);

	        write(jo.toString());
	      } catch (Exception e) {
	        e.printStackTrace();
	      }
	    }
	  }

	/**
	 * 下载服务代理
	 */
	private void downloadagent(){
		String downloadUrl = SecurityConfig.getString("DownloadServiceUrl");
		//如果没有配置下载服务地址，则直接上传到本地
		if(org.apache.commons.lang.StringUtils.isEmpty(downloadUrl)){
			download();
		}else{
			String path = this.getParameter("path");
			String url = downloadUrl + this.getParameter("path");

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
	}
	
	/**
	 * 模拟post提交，将附件同步到附件服务器中
	 * @param url
	 * @param charset
	 * @param files
	 * @return
	 */
	public String doPost(String url,String charset,Map<String, File> files) {
		StringBuffer response = new StringBuffer();
		HttpClient client = new HttpClient();
		PostMethod method = new PostMethod(url);
		try {
			Iterator<String> it = files.keySet().iterator();
			List<FilePart> parts=new ArrayList<FilePart>();
			
			List<File> tempFiles = new ArrayList<File>();
			while (it.hasNext()) {
				String key = it.next();
				File file = files.get(key);
				
				/*重新命名*/
				String tempFileName = file.getName();
				String fileType = tempFileName.substring(tempFileName.lastIndexOf('.'));
				String newPath = file.getParent() + File.separator + Tools.getUUID32() + fileType;
				moveFile(file,newPath);
				
				File newFile = new File(newPath);
				tempFiles.add(newFile);
				FilePart fp = new FilePart(key, newFile);
				fp.setCharSet("GBK");
				parts.add(fp);
			}
			Part[] p = parts.toArray(new Part[parts.size()]);
			method.setRequestEntity(new MultipartRequestEntity(p, method.getParams()));
			//设置超时
			client.getHttpConnectionManager().getParams().setConnectionTimeout(60000);
			client.getHttpConnectionManager().getParams().setSoTimeout(60000);
			
			client.executeMethod(method);
			if (method.getStatusCode() == HttpStatus.SC_OK) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(method.getResponseBodyAsStream(),
								charset));
				String line;
				while ((line = reader.readLine()) != null) {
					response.append(line);
				}
				reader.close();
			}

			/*删除临时文件*/
			for (File f : tempFiles) {
				f.delete();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			method.releaseConnection();
		}
		return response.toString();
	}
	
	private void upload(){		
		Map<String, File> m = this.getFileData();
		String type = this.getParameter("type");
		if(!StringUtil.isNotEmpty(type)){
			type=this.getPostData().get("type");
		}
		String path = this.getParameter("path");	
		Iterator<String> it = m.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			File file = m.get(key);		
			if(type==null){
				type = "local";
			}
			if (type!=null&&"local".equals(type)) {
				try {
					if (path == null) {
						path = "";
					}
					String fileName = getFilePath(path);
					String tempFileName = file.getName();
					String fileType = tempFileName.substring(tempFileName.lastIndexOf('.'));
					fileName = fileName + Tools.getUUID32() + fileType;
					String realPath = UrlHelper.getRealPath(fileName);

					moveFile(file, realPath);

					JSONObject jo = new JSONObject();
					fileName = StringUtils.replace(fileName, "\\", "/");					
					jo.put("path", fileName);
					jo.put("photo_id", fileName);	
					jo.put("url", fileName);
					jo.put("error", 0);
					this.write(jo.toString());
					
				} catch (Exception e) {
					e.printStackTrace();
				}				
			}
		}
	}
	
	private String getFilePath(String path) {
		path = StringUtils.trimTrailingCharacter(path, '/');
		if (path != null && path.length() > 0) {
			return "file" + File.separator + "upload" + File.separator + path + File.separator;
		}
		return "file" + File.separator + "upload" + File.separator;
	}
	
	private void moveFile(File oldFile, String newPath) {
		FileInputStream inStream=null;
		FileOutputStream fs=null;
		try {			
			if (oldFile.exists()) {
				// 文件存在时
				inStream= new FileInputStream(oldFile);
				File newFile = new File(newPath);
				if (!newFile.exists()) {
					newFile.createNewFile();
				}
				
				int byteread = 0;
				byte[] buffer = new byte[1024];
				fs= new FileOutputStream(newFile);				
				while ((byteread = inStream.read(buffer)) != -1) {
					fs.write(buffer, 0, byteread);
				}
				
				inStream.close();
				fs.close();	
				oldFile.delete();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void downloadNew() {
		String path = this.getParameter("path");
		String name = this.getParameter("name");
		Boolean download = false;
		if ("yes".equals(this.getParameter("download"))) {
			download = true;
		} else if ("no".equals(this.getParameter("download"))) {
			download = false;
		}
		try {
			if (StringUtil.isNotEmpty(path)) {
				path=path.substring(path.lastIndexOf("/")+1);
				// 附件的物理地址,为防止串改物理路径，将文件下载地址限制在file/upload/文件夹下
				String filepath = PathUtil.getWebPath() + getFilePath("") + path;
				File file = new File(filepath);

				// 如果找不到文件则抛出404错误
				if (file.exists()) {
					this.write(Tools.getFile(file));

					// 如果是图片则直接显示，如果是其他文件则下载
					/*Pattern wup = Pattern.compile(
							".+\\.(jpg|gif|png|jpeg|bmp)$",
							Pattern.CASE_INSENSITIVE);
					Matcher fm = wup.matcher(path.toLowerCase());*/

					if (download) {
						this.setContentType("application");
						String fileName = file.getName();
						if (StringUtil.isNotEmpty(name)) {
							fileName = name;
						}
						fileName =fileNameDecode(fileName);
						this.setHeader("Content-Disposition",
								"attachment;" + fileName);
					} else {
						this.setContentType("application/pdf");
						String fileName = file.getName();
						if (StringUtil.isNotEmpty(name)) {
							fileName = name;
						}
						fileName =fileNameDecode(fileName);
						if (fileName.contains("pdf")) {
							this.setContentType("application/pdf");
						} else {
							this.setContentType("image/jpeg");
						}
						this.setHeader("Content-Disposition",
								"inline;" + fileName);
					}
				} else {
					this.getHttpAdapter().sendError(
							HttpResponseStatus.NOT_FOUND, "");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void download() {
		String path = this.getParameter("path");
		String name = this.getParameter("name");
		Boolean download = false;
		if ("yes".equals(this.getParameter("download"))) {
			download = true;
		} else if ("no".equals(this.getParameter("download"))) {
			download = false;
		}
		try {
			if (StringUtil.isNotEmpty(path)) {
				path=path.substring(path.lastIndexOf("/")+1);
				// 附件的物理地址,为防止串改物理路径，将文件下载地址限制在file/upload/文件夹下
				String filepath = PathUtil.getWebPath() + getFilePath("") + path;
				File file = new File(filepath);

				// 如果找不到文件则抛出404错误
				if (file.exists()) {
					this.write(Tools.getFile(file));

					// 如果是图片则直接显示，如果是其他文件则下载
					/*Pattern wup = Pattern.compile(
							".+\\.(jpg|gif|png|jpeg|bmp)$",
							Pattern.CASE_INSENSITIVE);
					Matcher fm = wup.matcher(path.toLowerCase());*/

					if (download) {
						this.setContentType("application");
						String fileName = file.getName();
						if (StringUtil.isNotEmpty(name)) {
							fileName = name;
						}
						fileName =fileNameDecode(fileName);
						this.setHeader("Content-Disposition",
								"attachment;" + fileName);
					} else {
						this.setContentType("application/pdf");
						String fileName = file.getName();
						if (StringUtil.isNotEmpty(name)) {
							fileName = name;
						}
						fileName =fileNameDecode(fileName);
						if (fileName.contains("pdf")) {
							this.setContentType("application/pdf");
						} else {
							this.setContentType("image/jpeg");
						}
						this.setHeader("Content-Disposition",
								"inline;" + fileName);
					}
				} else {
					this.getHttpAdapter().sendError(
							HttpResponseStatus.NOT_FOUND, "");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	private String fileNameDecode(String fileName)
			throws UnsupportedEncodingException {
		String userAgent = this.getHttpAdapter().getHeader("User-Agent");
		String def = URLEncoder.encode(fileName, "UTF8");
		String rtn = "filename=" + def;
		if (userAgent != null) {
			userAgent = userAgent.toLowerCase();
			// IE浏览器，只能采用URLEncoder编码
			if (userAgent.indexOf("msie") != -1) {
			}
			// Opera浏览器只能采用filename*
			else if (userAgent.indexOf("opera") != -1) {
				rtn = "filename*=UTF-8''" + def;
			}
			// Safari浏览器，只能采用ISO编码的中文输出
			else if (userAgent.indexOf("safari") != -1) {
				rtn = "filename="
						+ new String(fileName.getBytes("UTF-8"), "ISO8859-1");
			}
			// Chrome浏览器，只能采用MimeUtility编码或ISO编码的中文输出
			else if (userAgent.indexOf("applewebkit") != -1) {
				def = MimeUtility.encodeText(fileName, "UTF8", "B");
				rtn = "filename=" + def;
			}
			// FireFox浏览器，可以使用MimeUtility或filename*或ISO编码的中文输出
			else if (userAgent.indexOf("mozilla") != -1) {
				rtn = "filename*=UTF-8''" + def;
			}
		}
		return rtn;
	}
	
	
	/**
	 * 创业服务上传
	 */
	private void uploadagent4wp_cyfw() {
		String uploadUrl = SecurityConfig.getString("NetDiskAddress");
		// 如果没有配置上传服务地址，则直接上传到本地
		if (org.apache.commons.lang.StringUtils.isEmpty(uploadUrl)) {
			upload();
		} else {
			JSONArray r = new JSONArray();
			Map<String, File> m = this.getFileData();
			Iterator<Entry<String, File>> it = m.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, File> iterator = it.next();// 真实文件名，在IE下会带着路径信息
				String key = iterator.getKey();
				File file = iterator.getValue();
				String fileType = key.substring(key.lastIndexOf('.'));
				try {
					// 将临时文件转换为真实文件名，然后再上传到网盘,File.separator在linux部署环境下为/
					key = key.substring(key.lastIndexOf(File.separator) + 1);
					key = key.substring(key.lastIndexOf("\\") + 1);
					String fileUri = PathUtil.getTempPath() + key;
					moveFile(file, fileUri);
					file = new File(fileUri);
					Map<String, String> params = new HashMap<String, String>();
					if (!"".equals(__uid__) && null != __uid__) {
						params.put("uid", __uid__);
					} else {
						params.put("uid",
								SecurityConfig.getString("NetDiskUid"));
					}
					params.put("type", "doc");
					params.put("folder_name", "//");
					String scc = UploadUtil.startUploadService(params, fileUri,
							uploadUrl);
					JSONObject jo = new JSONObject();
					JSONObject o = JSONObject.fromObject(scc);
					String docid = o.getString("docid");
					file.delete();
					// String file_name=Tools.getUUID32()+ fileType;
					// fileUri=
					// UrlHelper.getRealPath(getFilePath("")+file_name);
					// moveFile(file, fileUri);
					jo.put("path", docid);
					jo.put("scc", o);
					jo.put("url", docid);
					jo.put("error", 0);
					jo.put("name", key);
					r.add(jo);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			this.write(r.toString());
		}
	}
}
