package app.pmi;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.icore.handler.AbstractHandler;
import com.icore.http.HttpException;
import com.icore.http.adapter.HttpAdapter;
import com.inspur.util.SecurityConfig;
import com.inspur.util.URLEncode;

public class UniformAuthenHandler extends AbstractHandler {
	private static final Pattern aPattern = Pattern.compile(SecurityConfig.getString("UniformAuthenHandler_Undo","^.*/(a|((c|(command)|(bsp)|(forum))/.*))$"), Pattern.CASE_INSENSITIVE);

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

	@Override
	/**
	 * 业务逻辑处理，返回值表示：true为：直接结束，false：为继续交给下一个Handler处理
	 */
	public Boolean doHandle(HttpAdapter httpAdapter) throws HttpException {
		//http://127.0.0.1/icity/uc/index?tokenId=AQIC5wM2LY4SfcxevBbX4icnChPsBhECnpK0mx1WS1_3n2U.*AAJTSQACMDEAAlNLABQtMjU1NjczNDEwMzE2MzAzNTQwMw..*&random=YzU2ZGJhZDEwMTQyNGZhNDg1YWM5MmEzYWFiYjVmYzg%3D
		// TODO Auto-generated method stub
		String url = httpAdapter.getUri();
		if(!needToDo(url)) return false;
		//需要处理
		Map<String, String> paramMap = httpAdapter.getParameterMap();
		String tokenid = paramMap.get("tokenId");
		paramMap.remove("tokenId");
		String random = paramMap.get("random");
		paramMap.remove("random");
		String contextPath = httpAdapter.getContextPath();
		String tempUrl = contextPath + "/app/uc/login?action=callback&tokenid="+ tokenid+"&random="+random;
		tempUrl += "&goto=" + URLEncode.encodeURL(contextPath + httpAdapter.getUrl() + fixParams(paramMap));
		httpAdapter.sendRedirect(tempUrl);
		return true;
	}
	
	private boolean needToDo(String url){
		//是否包含tokenId&random 
		boolean bl = false;
		// TODO Auto-generated method stub
		if(url.indexOf("tokenId=") > -1 && url.indexOf("random=") > -1){
			bl = true;
		}
		return bl;
	}
	
	private String fixParams(Map<String, String> paramMap){
		StringBuffer sb = new StringBuffer(); 
		if(paramMap.size() > 0) sb.append("?");
		for(Map.Entry<String, String> entry : paramMap.entrySet()){
			sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
		}
		return sb.toString();
	}
}
