package action.bsp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.lang.StringUtils;
import org.tanukisoftware.wrapper.jmx.WrapperManager;

import com.icore.core.ThreadPoolManager;
import com.icore.file.FileManager;
import com.icore.util.Tools.JSON_TYPE;
import com.icore.util.security.MD5FileUtil;
import com.inspur.StateType;
import com.inspur.base.BaseAction;
import com.inspur.bean.DataSet;
import com.inspur.util.PathUtil;
import com.inspur.util.SecurityConfig;
import com.inspur.util.Tools;

import core.util.HttpClientUtil;

public class updater  extends BaseAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected boolean IsVerify() {
		return false;
	}
	
	
	public boolean handler(Map<String,Object> data) throws IOException{
		this.getHttpAdapter().setVm("/src.vm");
		
		String server_token = SecurityConfig.getInstance().getString("SuperAdmin","@WSX1qaz~!@#$%^&*");
		String action=this.getParameter("action");
		String step = this.getParameter("step");
		Map<String,String> postData =this.getPostData();
		data.put("PostData", postData);
		String updaterPassword = postData.get("updater_password");
		if(StringUtils.isEmpty(updaterPassword)){
			updaterPassword = this.getParameter("updater_password");
		}
		if(StringUtils.isNotEmpty(action)){
			if(!server_token.equalsIgnoreCase(updaterPassword)){
				this.sendRedirect("updater?err=1001");
				return false;
			}
			if("downloadZip".equalsIgnoreCase(action)){
				//从服务器段下载zip文件
				//压缩申请的文件并且下载""
				String paths = (String)(this.getPostData().get("paths"));
				if(!StringUtils.isNotEmpty(paths)){
					this.write("请传递paths参数");
					return false;
				}
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				try{
					FileManager.compressZipFile(bos, paths);
					//如果找不到文件则抛出404错误
					if(bos.toByteArray().length>0){
						this.write(bos.toByteArray());
					}else{
						throw new Exception("文件内容为空");
					}
					this.setContentType("application");
					this.setHeader("Content-Disposition","attachment; filename=updater"+Tools.formatDate(new Date(), "yyyyMMddHHmmss")+".zip");
				}
				catch(Exception e){
					this.write("下载文件失败");
				}finally{
					try {
						bos.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				return false;
			}
			else if("uploadZip".equalsIgnoreCase(action)){
				Map<String, File> m = this.getFileData();
				String file = PathUtil.getWebPath()+"updater.zip";
				Iterator<String> itFile = m.keySet().iterator();
				if(itFile.hasNext()){
					FileManager.copyFile(m.get(itFile.next()), file);
				}
				return true;
			}else if("sync".equalsIgnoreCase(action)){
				//从远程下载文件，或者将文件提交到远程
				String paths = (String)postData.get("updater_paths");
				String updaterType=(String)postData.get("updater_type");
				String updaterUrl = (String)postData.get("updater_url");
				String remoteUpdaterPassword =  (String)postData.get("remote_updater_password");
				if("1".equalsIgnoreCase(updaterType)){
					
					String url="";
					if(updaterUrl.endsWith("/")){
						url = updaterUrl+"bsp/updater?action=downloadZip";
					}
					else{
						url = updaterUrl+"/bsp/updater?action=downloadZip";
					}		
					HttpClient client = new HttpClient();
					PostMethod uploadData = new PostMethod(url);
					NameValuePair simcard = new NameValuePair("paths",paths);
					NameValuePair rup = new NameValuePair("updater_password",remoteUpdaterPassword);
					uploadData.setRequestBody(new NameValuePair[]{simcard,rup});
					FileOutputStream fos = null;
					try {
						//设置超时
						client.getHttpConnectionManager().getParams().setConnectionTimeout(60000);
						client.getHttpConnectionManager().getParams().setSoTimeout(120000);
						
						client.executeMethod((HttpMethod) uploadData);
						ByteArrayOutputStream bos = new ByteArrayOutputStream();
						if (uploadData.getStatusCode() == HttpStatus.SC_OK) {
							InputStream reader = uploadData.getResponseBodyAsStream();
							byte[] b = new byte[1024];
							int n;
							while ((n = reader.read(b)) != -1) {
								bos.write(b, 0, n);
							}
							reader.close();
							bos.close();
						}
						String file=PathUtil.getWebPath()+"updater.zip";
					    fos = new FileOutputStream (new File(file));
					    bos.writeTo(fos);
					
					} catch (IOException e) {
						this.sendRedirect("updater?err=1002");
						return false;
					} finally {
						if(fos!=null){
							fos.close();
						}
						uploadData.releaseConnection();
					}
				}else{
					String url=null;
					if(updaterUrl.endsWith("/")){
						url = updaterUrl+"bsp/updater?action=uploadZip&updater_password="+remoteUpdaterPassword;
					}
					else{
						url = updaterUrl+"/bsp/updater?action=uploadZip&updater_password="+remoteUpdaterPassword;
					}
					
					//压缩需要打包的所有路径
					try{
						ByteArrayOutputStream tempBos=new ByteArrayOutputStream();
						try {
							FileManager.compressZipFile(tempBos, paths);
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							throw e1;
						}
						String tempPath = PathUtil.getFileRoot()+"temp"+File.separator+"updater_"+Tools.formatDate(new Date(), "yyyyMMddHHmmss")+".zip";
						File tempFile = new File(tempPath);
						FileOutputStream tempFos = null;
						try {
							tempFos = new FileOutputStream (tempFile);
							tempBos.writeTo(tempFos);
						} catch (FileNotFoundException e1) {
							// TODO Auto-generated catch block
							throw e1;
						} catch (IOException e) {
							// TODO Auto-generated catch block
							throw e;
						}finally{
							tempBos.close();
							if(tempFos != null){
								tempFos.close();
							}
						}
						//将临时的压缩文件推送到远程服务器上
						HttpClient client = new HttpClient();
						PostMethod postMethod = new PostMethod(url);
						List<FilePart> parts = new ArrayList<FilePart>();
						FilePart fp=null;
						try {
							fp = new FilePart("updater", tempFile);
						} catch (FileNotFoundException e1) {
							// TODO Auto-generated catch block
							throw e1;
						}
						parts.add(fp);
						Part[] p = parts.toArray(new Part[parts.size()]);
						postMethod.setRequestEntity(new MultipartRequestEntity(p, postMethod.getParams()));
						try {
							//设置超时
							client.getHttpConnectionManager().getParams().setConnectionTimeout(60000);
							client.getHttpConnectionManager().getParams().setSoTimeout(60000);
							client.executeMethod(postMethod);
							
							if (postMethod.getStatusCode() == HttpStatus.SC_OK) {
								
							}else{
								throw new Exception("与远程服务器同步数据失败！");
							}
						} catch (HttpException e) {
							// TODO Auto-generated catch block
							throw e;
						} catch (IOException e) {
							// TODO Auto-generated catch block
							throw e;
						}
						finally{
							tempFile.delete();
						}
					}
					catch(Exception ex1){
						this.sendRedirect("updater?err=1002");
						return false;
					}
				}
				return true;
			}
			else if("fileVersion".equalsIgnoreCase(action)){
				//后去本程序文件版本
		    	JSONArray ja = getProjectFile(new File(PathUtil.getWebPath()));
		        DataSet ds = new DataSet();
		        ds.setRawData( ja);
		        this.write(ds.toJson());
		        return false;
			}
			else if("compare".equalsIgnoreCase(action)){
				//本地文件与远程文件进行版本比较
				JSONArray localJA = getProjectFile(new File(PathUtil.getWebPath()));
				//获取网络上的资源
				HttpClientUtil hc = new HttpClientUtil();
				String updaterUrl=(String)postData.get("updater_url");
				String updaterType=(String) postData.get("updater_type");
				String remote_updater_password=(String) postData.get("remote_updater_password");
				String c=null;
				if(updaterUrl.endsWith("/")){
					c=hc.getResult(updaterUrl+"bsp/updater?action=fileVersion", "updater_password="+remote_updater_password,true);
				}
				else{
					c=hc.getResult(updaterUrl+"/bsp/updater?action=fileVersion", "updater_password="+remote_updater_password,true);
				}
				
				DataSet reDS = new DataSet();
				
				if(StringUtils.isNotEmpty(c) && Tools.getJSONType(c).equals(JSON_TYPE.JSON_TYPE_OBJECT)){
				   	JSONObject netJO = JSONObject.fromObject(c);
				   	if(netJO.getInt("state")==1){
				   		JSONArray netJA = netJO.getJSONArray("data");
					   	JSONArray ja = null;
					   	try{
					   		if("1".equalsIgnoreCase(updaterType)){
					   			ja = compareData(localJA, netJA);
					   		}else if("2".equalsIgnoreCase(updaterType)){
					   			ja = compareData(netJA, localJA);
					   		}
					   	}
					   	catch(Exception e){
					   		e.printStackTrace();
					   	}
					   	reDS.setRawData(ja);
				   	}else{
				   		reDS.setState(StateType.FAILT);
				   		String errMsg = netJO.getString("message");
				   		if(StringUtils.isEmpty(errMsg)){
				   			errMsg = "从远程地址中获取资源失败，可能远程已报错！";
				   		}
				   		reDS.setMessage(errMsg);
				   	}
				}else{
					reDS.setState(StateType.FAILT);
			   		reDS.setMessage("从远程地址中获取资源失败，可能为网络不能互通。");
				}
			   	this.write(reDS.toJson());
			   	return false;
			}
			
		}
		
		
		if(StringUtils.isNotEmpty(step)){
			if(!server_token.equalsIgnoreCase(updaterPassword)){
				this.sendRedirect("updater?err=1001");
				return false;
			}
			if(step.equals("4")){
				String restartType = postData.get("restart_type");
				String updaterType = postData.get("updater_type");
				if("2".equalsIgnoreCase(updaterType)){
					HttpClientUtil hc = new HttpClientUtil();
					String updaterUrl=(String)postData.get("updater_url");
					String remote_updater_password=(String) postData.get("remote_updater_password");
					String c=null;
//					if(updaterUrl.endsWith("/")){
//						c=hc.getResult(updaterUrl+"bsp/updater?step=4", "updaterType=1&restart_type="+restartType+"&updater_password="+remote_updater_password,true);
//					}
//					else{
//						c=hc.getResult(updaterUrl+"bsp/updater?step=4", "updaterType=1&restart_type="+restartType+"&updater_password="+remote_updater_password,true);
//					}
					c=hc.getResult(updaterUrl+"bsp/updater?step=4", "updaterType=1&restart_type="+restartType+"&updater_password="+remote_updater_password,true);
					if(StringUtils.isEmpty(c)){
						data.put("error", "远程重启服务器失败，可能服务器之间网络繁忙！");
					}
					return true;
				}
				if(restartType.equalsIgnoreCase("2")){
					Thread th = new Thread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Tools.sleep(1000);
							Tools.isStop(true);
							while(true){
								Iterator<String> itPools = (Iterator<String>) ThreadPoolManager.getInstanceName();
								Boolean hasTask = false;
								while(itPools.hasNext()){
									String poolName = itPools.next();
									if(ThreadPoolManager.getInstance(poolName).getWaitTaskNumber() >0){
										hasTask =true;
										break;
									}
								}
								if(!hasTask){
									WrapperManager wm = new WrapperManager();
									wm.restart();
								}
								Tools.sleep(1000);
							}
						}
					});
					th.start();
				}
				else{
					Thread th = new Thread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Tools.sleep(1000);
							WrapperManager wm = new WrapperManager();
							wm.restart();
						}
					});
					th.start();
				}
			}
			return true;
		}
		return true;
	}
	
	
	private JSONArray compareData(JSONArray localData,JSONArray netData){
		//全局结果集
		JSONArray gja = new JSONArray();
		if(netData != null && netData.size()>0){
			for(int i=0;i<netData.size();i++){
				JSONObject netJO = netData.getJSONObject(i);
				String nk = netJO.getString("NAME");
				JSONObject localJO = findJson(localData, nk);
				//如果本地与服务器之间存在相同文件、目录，则可能存在为服务器端有修改或者没有变化
				if(localJO != null){
					//表示为目录
					if(localJO.getInt("DIR")==1){
						JSONArray lja = localJO.getJSONArray("CHILDREN");
						JSONArray nja = netJO.getJSONArray("CHILDREN");
						JSONArray cja = compareData(lja,nja);
						if(cja.size()>0){
							JSONObject tjo = new JSONObject();
							tjo.put("NAME", localJO.getString("NAME"));
							tjo.put("PATH", localJO.getString("PATH"));
							tjo.put("DIR", 1);
							tjo.put("CHILDREN", cja);
							gja.add(tjo);
						}
					}else{//为文件时候
						//比较文件属性是否一致，如果一致则表示为无变法，否则为修改
						JSONObject tjo = new JSONObject();
						tjo.put("NAME", localJO.getString("NAME"));
						tjo.put("PATH", localJO.getString("PATH"));
						tjo.put("DIR", 0);
						if(netJO.getString("MD5").equalsIgnoreCase(localJO.getString("MD5"))){
						}else{
							tjo.put("TYPE", "MODIFY");
							gja.add(tjo);
						}
						
					}
				}else{
					//本地不存在服务器文件，则本地标志为服务器端已新增
					//表示为目录
					if(netJO.getInt("DIR")==1){
						JSONArray nja = netJO.getJSONArray("CHILDREN");
						JSONArray cja = compareData(null,nja);
						if(cja.size()>0){
							JSONObject tjo = new JSONObject();
							tjo.put("NAME", netJO.getString("NAME"));
							tjo.put("PATH", netJO.getString("PATH"));
							tjo.put("DIR", 1);
							tjo.put("CHILDREN", cja);
							gja.add(tjo);
						}
					}else{//为文件时候
						//比较文件属性是否一致，如果一致则表示为无变法，否则为修改
						JSONObject tjo = new JSONObject();
						tjo.put("NAME", netJO.getString("NAME"));
						tjo.put("PATH", netJO.getString("PATH"));
						tjo.put("DIR", 0);
						tjo.put("TYPE", "ADD");
						gja.add(tjo);
					}
				}
			}
		}
		if(localData != null && localData.size()>0){
			//如果本地有，而服务器没有，则本地标志为服务器端已删除
			for(int li=0;li<localData.size();li++){
				JSONObject ljo = localData.getJSONObject(li);
				JSONObject jo = findJson(netData, ljo.getString("NAME"));
				if(jo == null || jo.size()==0){
					//gja.add(ljo);
				}
			}
		}
		return gja;
	}
	private JSONObject findJson(JSONArray localJA,String name){
		if(localJA == null){
			return null;
		}
		for(int i=0;i<localJA.size();i++){
			JSONObject jo = localJA.getJSONObject(i);
			if(jo !=null){
				if(name.equalsIgnoreCase(jo.getString("NAME"))){
					return jo;
				}
			}
		}
		return null;
	}
	private JSONArray getProjectFile(File file) throws IOException {
		JSONArray ja = new JSONArray();
        if (file.exists()) {
            if (file.isDirectory()) {
                for (File subFile : file.listFiles()) {
                	if(!subFile.getCanonicalPath().equalsIgnoreCase(getFilePath()) && subFile.getName().indexOf(".svn")==-1 && subFile.getName().indexOf(".DS_Store")==-1){
                		if(subFile.isDirectory()){
                			JSONObject tjo = new JSONObject();
                    		tjo.put("NAME", subFile.getName());
                    		tjo.put("PATH", formatWebPath(subFile));
                    		tjo.put("DIR", 1);
                    		JSONArray child = getProjectFile(subFile);
                    		if(child.size()>0){
	                    		tjo.put("CHILDREN", child);
	                    		ja.add(tjo);
                    		}
                		}
                		else{
                			if(subFile.length()>1024*1024*2){
                        		JSONObject tjo = new JSONObject();
                        		tjo.put("NAME", subFile.getName());
                        		tjo.put("PATH", formatWebPath(subFile));
                        		tjo.put("DIR", 0);
                        		tjo.put("MD5", "MAX_FILE_SIZE_"+subFile.length());
                        		ja.add(tjo);
                        	}
                        	else{
                        		JSONObject tjo = new JSONObject();
                        		tjo.put("NAME", subFile.getName());
                        		tjo.put("PATH", formatWebPath(subFile));
                        		tjo.put("DIR", 0);
                        		tjo.put("MD5", "MAX_FILE_SIZE_"+MD5FileUtil.getFileMD5String(subFile));
                        		ja.add(tjo);
                        	}
                		}
                		
                	}
                }
            } else if (file.isFile() &&  file.getName().indexOf(".svn")==-1 && file.getName().indexOf(".DS_Store")==-1) {
            	if(file.length()>1024*1024*2){
            		JSONObject tjo = new JSONObject();
            		tjo.put("NAME", file.getName());
            		tjo.put("PATH", formatWebPath(file));
            		tjo.put("DIR", 0);
            		tjo.put("MD5", "MAX_FILE_SIZE_"+file.length());
            		ja.add(tjo);
            	}
            	else{
            		JSONObject tjo = new JSONObject();
            		tjo.put("NAME", file.getName());
            		tjo.put("PATH", formatWebPath(file));
            		tjo.put("DIR", 0);
            		tjo.put("MD5", "MAX_FILE_SIZE_"+MD5FileUtil.getFileMD5String(file));
            		ja.add(tjo);
            	}
            }
        }
        return ja;
    }
	private String webPath = null;
	private String getWebPath() throws IOException{
		if(webPath == null){
			File f = new File(PathUtil.getWebPath());
			webPath= f.getCanonicalPath();
		}
		return webPath;
	}
	private String formatWebPath(File f) throws IOException{
		return StringUtils.replace(f.getCanonicalPath(), getWebPath(), "");
	}
	private String filePath = null;
	public String getFilePath() throws IOException{
		if(filePath == null){
			File f = new File(PathUtil.getFileRoot());
			filePath= f.getCanonicalPath();
		}
		return filePath;
	}
}
