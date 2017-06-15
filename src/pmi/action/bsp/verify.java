package action.bsp;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.file.UPDownLoadFile;
import org.apache.commons.lang.builder.verify.web.WebVerifyClient;




import com.icore.http.adapter.HttpAdapter;
import com.inspur.base.BaseAction;
import com.inspur.http.util.UrlHelper;
import com.inspur.util.PathUtil;

public class verify extends BaseAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected boolean IsVerify() {
		return false;
	}
	private static WebVerifyClient verifyClient;
	private static WebVerifyClient getVerifyClient() {
		if (verifyClient == null){
			synchronized (verify.class) {
				if (verifyClient == null){
					verifyClient = new WebVerifyClient();
				}
			}
		}
		return verifyClient;
	}
	public boolean handler(Map<String,Object> data){
		String type = this.getParameter("type");
		if(StringUtils.isNotEmpty(type)){
			if(type.equalsIgnoreCase("download")){
				
				try {
					getVerifyClient().verifyClient();
					getVerifyClient().downLoadOfStream(this.getHttpAdapter());
				} catch (IOException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
			}else if(type.equalsIgnoreCase("upload")){
				Map<String,File> fileMap = this.getFileData();
				Iterator<Entry<String, File>> it = fileMap.entrySet().iterator();
				while(it.hasNext()){
					Entry<String, File> iterator = it.next();
					//String key = iterator.getKey();
					FileInputStream is = null;
					try {
						is = new FileInputStream(iterator.getValue());
						UPDownLoadFile.copy(is, PathUtil.getWebInfPath() + "license");
					} catch (IOException e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}
					finally{
						if(is != null)
						{
							try {
								is.close();
							} catch (IOException e) {
								// TODO 自动生成的 catch 块
								e.printStackTrace();
							}
							is = null;
						}
					}
				}
				this.sendRedirect(UrlHelper.index(this.getHttpAdapter()));
			}else{
				goDefault(this.getHttpAdapter());
			}
		}else{
			goDefault(this.getHttpAdapter());
		}
		return false;
	}
	private void goDefault(HttpAdapter httpAdapter){
		try {
			getVerifyClient().updownLoad(httpAdapter,null,null,"ICITY网办运行支撑平台","山东浪潮齐鲁软件有限公司");
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
}
