package xy.action.bsp;

import io.netty.util.CharsetUtil;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

public class uploadify extends BaseAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected boolean IsVerify() {
		return false;
	}

	protected static Log _log = LogFactory.getLog(uploadify.class);
	private String __uid__ = "";
	private String province = "0";

	public boolean handler(Map<String, Object> data) {
		String action = this.getParameter("action");
		if ("download".equals(action)) {
			download();
		} else if ("uploadagent4wp".equals(action)) {
			__uid__ = this.getParameter("ucid");
			uploadagent4wp();
		} else if ("downloadagent4wp".equals(action)) {
			province = this.getParameter("province");
			downloadagent();
		} else if ("delete".equals(action)) {
			deleteFile();
		} else {
			upload();
		}
		return false;
	}


	/**
	 * 上传服务代理，解决swfupload跨域问题
	 */
	private void uploadagent4wp() {
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
				String key = iterator.getKey();// 真实文件名，在IE下会带着路径信息
				File file = iterator.getValue();
				String fileType = key.substring(key.lastIndexOf('.'));
				try {
					// 将临时文件转换为真实文件名，然后再上传到网盘
					if (key.indexOf(File.separator) > -1) {
						key = key.substring(key.lastIndexOf(File.separator) + 1);
					}
					String fileUri = PathUtil.getTempPath() + key;
					moveFile(file, fileUri);
					file = new File(fileUri);
					Map<String, String> params = new HashMap<String, String>();
					if (!"".equals(__uid__) && null != __uid__) {
						params.put("uid", __uid__);
					} else {
						params.put("uid", SecurityConfig.getString("NetDiskUid"));
					}
					params.put("type", "doc");
					params.put("folder_name", "//");
					String scc = UploadUtil.startUploadService(params, fileUri, uploadUrl);
					JSONObject jo = new JSONObject();
					JSONObject o = JSONObject.fromObject(scc);
					String file_name = Tools.getUUID32() + fileType;
					fileUri = UrlHelper.getRealPath(getFilePath("") + file_name);
					moveFile(file, fileUri);
					jo.put("path", file_name);
					jo.put("scc", o);
					jo.put("url", file_name);
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

	/**
	 * 下载服务代理
	 */
	private void downloadagent() {
		String downloadUrl = SecurityConfig.getString("NetDiskDownloadAddress");
		if ("1".equals(province)) {
			downloadUrl = SecurityConfig.getString("NetDiskDownloadAddress-jxxy-province");
		}
		// 如果没有配置下载服务地址，则直接上传到本地
		if (org.apache.commons.lang.StringUtils.isEmpty(downloadUrl)) {
			download();
		} else {
			String docid = this.getParameter("path");
			String name = this.getParameter("name");
			String url = downloadUrl + docid;
			String fileName = docid;// 下载文件名默认用docid
			if (StringUtil.isNotEmpty(name)) {
				fileName = name;
			}
			fileName = new String(fileName.getBytes(CharsetUtil.UTF_8), CharsetUtil.ISO_8859_1);
			HttpClient client = new HttpClient();
			HttpMethod method = new GetMethod(url);
			try {

				// 设置超时
				client.getHttpConnectionManager().getParams().setConnectionTimeout(60000);
				client.getHttpConnectionManager().getParams().setSoTimeout(60000);
				method.getParams().setContentCharset("UTF-8");
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
				// 如果找不到文件则抛出404错误
				if (bos.toByteArray().length > 0) {
					this.write(bos.toByteArray());
					this.setContentType("application");
					this.setHeader("Content-Disposition", "attachment; filename=" + fileName);
				} else {
					this.getHttpAdapter().sendError(HttpResponseStatus.NOT_FOUND, "");
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
	 * 
	 * @param url
	 * @param charset
	 * @param files
	 * @return
	 */
	public String doPost(String url, String charset, Map<String, File> files) {
		StringBuffer response = new StringBuffer();
		HttpClient client = new HttpClient();
		PostMethod method = new PostMethod(url);
		BufferedReader reader = null;
		try {
			Iterator<Entry<String, File>> it = files.entrySet().iterator();
			List<FilePart> parts = new ArrayList<FilePart>();

			List<File> tempFiles = new ArrayList<File>();
			while (it.hasNext()) {
				Entry<String, File> iterator = it.next();
				String key = iterator.getKey();
				File file = iterator.getValue();

				/* 重新命名 */
				String tempFileName = file.getName();
				String fileType = tempFileName.substring(tempFileName.lastIndexOf('.'));
				String newPath = file.getParent() + File.separator + Tools.getUUID32() + fileType;
				moveFile(file, newPath);

				File newFile = new File(newPath);
				tempFiles.add(newFile);
				FilePart fp = new FilePart(key, newFile);
				fp.setCharSet("GBK");
				parts.add(fp);
			}
			Part[] p = parts.toArray(new Part[parts.size()]);
			method.setRequestEntity(new MultipartRequestEntity(p, method.getParams()));
			// 设置超时
			client.getHttpConnectionManager().getParams().setConnectionTimeout(60000);
			client.getHttpConnectionManager().getParams().setSoTimeout(60000);

			client.executeMethod(method);
			if (method.getStatusCode() == HttpStatus.SC_OK) {
				reader = new BufferedReader(
						new InputStreamReader(method.getResponseBodyAsStream(), charset));
				String line;
				while ((line = reader.readLine()) != null) {
					response.append(line);
				}
			}

			/* 删除临时文件 */
			for (File f : tempFiles) {
				f.delete();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(reader!=null){
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			method.releaseConnection();
		}
		return response.toString();
	}

	private void upload() {
		Map<String, File> m = this.getFileData();
		String type = this.getParameter("type");
		if (!StringUtil.isNotEmpty(type)) {
			type = this.getPostData().get("type");
		}
		String path = this.getParameter("path");
		Iterator<Entry<String, File>> it = m.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, File> iterator = it.next();
			// String key = iterator.getKey();
			File file = iterator.getValue();
			if (type == null) {
				type = "local";
			}
			if ("local".equals(type)) {
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
		FileInputStream inStream = null;
		FileOutputStream fs = null;
		try {
			if (oldFile.exists()) {
				// 文件存在时
				inStream = new FileInputStream(oldFile);
				File newFile = new File(newPath);
				if (!newFile.exists()) {
					newFile.createNewFile();
				}

				int byteread = 0;
				byte[] buffer = new byte[1024];
				fs = new FileOutputStream(newFile);
				while ((byteread = inStream.read(buffer)) != -1) {
					fs.write(buffer, 0, byteread);
				}

				oldFile.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(inStream!=null){
					inStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if(fs!=null){
					fs.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void download() {
		String path = this.getParameter("path");
		String name = this.getParameter("name");
		try {
			if (path != null && path.length() > 0) {
				// 附件的物理地址,为防止串改物理路径，将文件下载地址限制在file/upload/文件夹下
				String filepath = PathUtil.getWebPath() + "/" + getFilePath("") + "/" + path;
				File file = new File(filepath);

				// 如果找不到文件则抛出404错误
				if (file.exists()) {
					this.write(Tools.getFile(file));

					// 如果是图片则直接显示，如果是其他文件则下载
					Pattern wup = Pattern.compile(".+\\.(jpg|gif|png|jpeg|bmp)$", Pattern.CASE_INSENSITIVE);
					Matcher fm = wup.matcher(path.toLowerCase());

					if (!fm.find()) {
						this.setContentType("application");
						String fileName = file.getName();
						if (StringUtil.isNotEmpty(name)) {
							fileName = name;
						}
						fileName = new String(fileName.getBytes(CharsetUtil.UTF_8), CharsetUtil.ISO_8859_1);
						this.setHeader("Content-Disposition", "attachment; filename=" + fileName);
					} else {
						this.setContentType("image/jpeg");
					}
				} else {
					this.getHttpAdapter().sendError(HttpResponseStatus.NOT_FOUND, "");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void deleteFile() {
		String path = this.getParameter("path");
		try {
			if (path != null && path.length() > 0) {
				// 附件的物理地址,为防止串改物理路径，将文件删除地址限制在file/upload/文件夹下
				String filepath = PathUtil.getWebPath() + "/" + getFilePath("") + "/" + path;
				File file = new File(filepath);
				// 如果找不到文件则抛出404错误
				if (file.exists()) {
					file.delete();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
