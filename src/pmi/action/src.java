package action;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import net.asfun.jangod.base.ResourceManager;

import org.apache.commons.lang.StringUtils;

import com.icore.http.HttpResponseStatus;
import com.inspur.base.BaseAction;
import com.inspur.http.util.UrlHelper;
import com.inspur.util.DaoFactory;

public class src extends BaseAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String vmPath =UrlHelper.getRealPath("pages"+File.separator);
	protected boolean IsVerify() {
		return false;
	}
	public boolean handler(Map<String,Object> data) throws Exception {
		String m = this.getParameter("m");
		if(StringUtils.isNotEmpty(m)){
			m= StringUtils.replace(m, "..","");
			
			int lIndex = m.lastIndexOf(".htm");
			String jsName = "";
			if(lIndex>-1){
				String curFileName= m.substring(0,lIndex);
				String className = curFileName.replace("/", ".");
				if(className.indexOf(".")==0){
					className = className.substring(1);
				}
				this.getPageContext().setHtmlName(m);
				//**.js是否存在（可以将此信息加入缓存，减少每次判断）
				try{
					String curFileJs = curFileName + ".js";
					ResourceManager.getPagesName(curFileJs);
					this.getPageContext().setJsName(curFileJs);
				}
				catch(IOException jsIO){
					
				}
				BaseAction ba = (BaseAction)DaoFactory.getDao("action."+className);
				if(ba != null){
					ba.setPageContext(this.getPageContext());
					if(!ba.init(this.getHttpAdapter()) || !ba.handler(data)){
						return false;
					}
				}
			}
			else{
				this.getHttpAdapter().setStatus(HttpResponseStatus.NOT_FOUND);
				return false;
			}
		}
		else{
			this.getHttpAdapter().setStatus(HttpResponseStatus.NOT_FOUND);
			return false;
		}
		return true;
	}
}