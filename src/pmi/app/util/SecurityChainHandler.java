package app.util;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.icore.handler.AbstractHandler;
import com.icore.http.HttpException;
import com.icore.http.HttpResponseStatus;
import com.icore.http.adapter.HttpAdapter;
import com.icore.util.PathUtil;
import com.icore.util.SecurityConfig;

public class SecurityChainHandler extends AbstractHandler {
	
	private static final Pattern aPattern = Pattern.compile(SecurityConfig.getString("SecurityChainHandler_Undo","^.*/(a|((c|(command)|(bsp)|(forum))/.*))$"), Pattern.CASE_INSENSITIVE);

	private static final String securityChainConfigName = SecurityConfig.getString("securityChainConfigName","securityChain.xml");
	private static String regex = "";
	private static Pattern pattern = null;
	
	static{
		try {
			initConfig();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
		}
	}
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return this.getClass().getName();
	}

	@Override
	public Boolean isHandler(String url) {
		Matcher m = aPattern.matcher(url);
		if(m.find()){
			return false;
		}
		return true;
	}

	/**
	 * 业务逻辑处理，返回值表示：true为：直接结束，false：为继续交给下一个Handler处理
	 */
	@Override
	public Boolean doHandle(HttpAdapter httpAdapter) throws HttpException {
		String host = httpAdapter.getHeader("Host");
		if(pattern == null || !pattern.matcher(host).matches()){
			addRegex(host);
			pattern = Pattern.compile(regex);
		}
		
		String referer = httpAdapter.getHeader("Referer");
		if(StringUtils.isNotBlank(referer)){
			if(!pattern.matcher(referer).matches()){
				httpAdapter.sendError(HttpResponseStatus.BAD_REQUEST,"");
				return true;
			}
		}
		
		return false;
	}
	
	private static String getSecurityChainPath(){
		return PathUtil.getConfigPath() + securityChainConfigName;
	}
	
	private static String change2rex(String temp){
		temp = temp.replace(".", "\\.");
		return "(^.*" + temp + ".*$)";
	}
	
	private static void initConfig() throws DocumentException, MalformedURLException{
		
		String configPath = getSecurityChainPath();
		File file = new File(configPath);
		if(file.exists()){
			Document doc = new SAXReader().read(file);
			Element rootElement = doc.getRootElement();
			Element chainList = rootElement.element("chainList");
			if(chainList != null){
				Iterator<Element> chainIt = chainList.elementIterator();
				while(chainIt.hasNext()){
					Element chain = chainIt.next();
					Element urlElement = chain.element("url");
					if(urlElement != null){
						String temp = urlElement.getText();
						addRegex(temp);
					}
				}
			}
		}
	}

	private static void addRegex(String str){
		if(StringUtils.isNotBlank(str)){
			if(StringUtils.isNotBlank(regex)){
				regex += "|" + change2rex(str);
			}else{ 
				regex = change2rex(str);
			}
		}
	}
}
