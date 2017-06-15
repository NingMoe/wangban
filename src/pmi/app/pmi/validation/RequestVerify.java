package app.pmi.validation;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.axis.utils.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;

import api.impl.HlverifyImpl;

import com.icore.util.DaoFactory;
import com.icore.util.StringUtil;

/**
 * Title:请求合法性验证
 * <p>Description:</p>
 * Copyright (c) CQLIVING 2016
 * @author yuwu on 2016年7月19日
 */
public class RequestVerify implements Serializable {
	private static final long serialVersionUID = -7100551250737300577L;
	
	// 字符编码格式 目前支持 gbk 或 utf-8
	public static String input_charset = "utf-8";
	// 签名方式 不需修改
	public static String sign_type = "MD5";
	// 存储签名后的摘要信息参数名
	public static String SIGN_NAME = "hashSign";
	// 存储时间戳的参数名，该值是时间的毫秒数
	public static String SIGN_TIME = "time";
	/**
	 * Title:将请求参数转成map,如果数组值里面有多个，只取第一个元素
	 * @author yuwu on 2016年7月19日
	 * @param reqeustParamMap
	 * @return
	 */
	private static Map<String,String> convertparamMap(Map<String, String[]> reqeustParamMap){
		Map<String,String> returnMap = new HashMap<String, String>();
		if(null != reqeustParamMap){
			Set<String> keySet = reqeustParamMap.keySet();
			for(Object keyObject : keySet.toArray()){
				String[] valueArr = reqeustParamMap.get(keyObject);
				returnMap.put((String)keyObject, valueArr[0]);
			}
		}
		return returnMap;
	}
	
	/**
     * 验证消息是否是政务云APP发出的合法消息
     * @param params 通知返回来的参数数组
     * @param privateKey 私钥
     * @return 验证结果
     */
	private static boolean verify2(Map<String, String> params, String privateKey) {
	    String sign = StringUtils.isEmpty(params.get(SIGN_NAME)) ? "":params.get(SIGN_NAME);
//	    boolean isSign = getSignVeryfy(params, sign);
	    boolean isSign = getSignVeryfy(params, sign, privateKey);

        //String sWord = "isSign=" + isSign + "\n 返回回来的参数：" + RequestVerify.createLinkString(params);
        return isSign;
    }
    
	/***
	 * Title:验证请求来源是否合法，验证请求是否过期
	 * @author yuwu on 2016年7月20日
	 * @param reqeustParamMap 请求参数request.getParameterMap()
	 * @param validSecond 请求有效期，单位：秒，如果不需要验证有效期，则设置为0
	 * @param privateKey 私钥
	 * @return
	 */
	public static Response<Void> verify(Map<String, String[]> reqeustParamMap, String privateKey) {
//    	return verify(reqeustParamMap, 0);
    	return verify(reqeustParamMap, 0, privateKey);
    }
	
    public static Response<Void> verify(Map<String, String[]> reqeustParamMap, int validSecond, String privateKey) {
    	Map<String, String> params = RequestVerify.convertparamMap(reqeustParamMap);
    	
    	Response<Void> response = Response.newInstance();
//    	boolean isSign = RequestVerify.verify2(params);
    	boolean isSign = RequestVerify.verify2(params, privateKey);
    	if(!isSign){
    		response.setCode(Response.BUSINESS_ERROR);
    		response.setMessage("请求参数不合法，请从原请求处重新发送请求");
    		return response;
    	}
    	//大于0才去验证
    	if(validSecond > 0){
    		//获取请求时间
    		String timeStr = params.get(RequestVerify.SIGN_TIME);
    		Long time = StringUtils.isEmpty(timeStr) ? 0L : Long.parseLong(timeStr);
    		Long now = System.currentTimeMillis();
    		//判断请求是否在有效期内
    		boolean isValid = Math.abs(now - time) > validSecond*1000;
    		if(isValid){
    			response.setCode(Response.BUSINESS_ERROR);
        		response.setMessage("请求已失效，请检查当前设备系统时间，或请从原请求处重新发送请求");
        		return response;
    		}
    	}
	    return response;
    }
    
    /**
     * 根据反馈回来的信息，生成签名结果
     * @param Params 通知返回来的参数数组
     * @param sign 比对的签名结果
     * @param privateKey 私钥
     * @return 生成的签名结果
     */
	private static boolean getSignVeryfy(Map<String, String> Params, String sign, String privateKey) {
    	//过滤空值、sign_name参数
    	Map<String, String> sParaNew = RequestVerify.paraFilter(Params);
        //获取待签名字符串
        String preSignStr = RequestVerify.createLinkString(sParaNew);
        System.out.println("待签名字符串:" + preSignStr);
        //获得签名验证结果
//        boolean isSign = RequestVerify.verify(preSignStr, sign, RequestVerify.key, RequestVerify.input_charset);
        boolean isSign = RequestVerify.verify(preSignStr, sign, privateKey, RequestVerify.input_charset);
        return isSign;
    }
	
	/** 
     * 除去数组中的签名参数
     * @param sArray 签名参数组
     * @return 去掉签名参数后的新签名参数组
     */
    public static Map<String, String> paraFilter(Map<String, String> sArray) {

        Map<String, String> result = new HashMap<String, String>();
        if (sArray == null || sArray.size() <= 0) {
            return result;
        }
        for (String key : sArray.keySet()) {
            String value = sArray.get(key);
            if (key.equalsIgnoreCase(SIGN_NAME)) {
                continue;
            }
            result.put(key, value);
        }
        return result;
    }
    
    /** 
     * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     * @param params 需要排序并参与字符拼接的参数组
     * @return 拼接后字符串
     */
    public static String createLinkString(Map<String, String> params) {

        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        StringBuilder prestr = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
            value = value == null ? "":value;
            if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
            	prestr.append(key).append("=").append(value);
            } else {
            	prestr.append(key).append("=").append(value).append("&");
            }
        }
        return prestr.toString();
    }
    
    /**
     * 签名字符串
     * @param text 需要签名的字符串
     * @param sign 签名结果
     * @param key 密钥
     * @param input_charset 编码格式
     * @return 签名结果
     */
    public static boolean verify(String text, String sign, String key, String input_charset) {
    	text = text + key;
    	if(StringUtil.isEmpty(sign)) {
    		return false;
    	}
    	String mysign = DigestUtils.md5Hex(getContentBytes(text, input_charset)).toLowerCase();
    	System.out.println("签名后的hash值：" + mysign + " 传入的sign "+ sign.toLowerCase());
    	if(mysign.equals(sign.toLowerCase())) {
    		return true;
    	}else {
    		return false;
    	}
    }
    
    /**
     * @param content
     * @param charset
     * @return
     * @throws SignatureException
     * @throws UnsupportedEncodingException 
     */
    private static byte[] getContentBytes(String content, String charset) {
        if (charset == null || "".equals(charset)) {
            return content.getBytes();
        }
        try {
            return content.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("MD5签名过程中出现错误,指定的编码集不对,您目前指定的编码集是:" + charset);
        }
    }
    
    /**
     * Title:签名
     * @author yuwu on 2016年12月7日
     * @param params
     * @param privateKey
     * @return
     */
    private static Map<String, String> sign(Map<String, String> params, String privateKey) {
    	//加上当前时间参数
    	params.put(SIGN_TIME, String.valueOf(System.currentTimeMillis()));
    	//params.put(SIGN_TIME, "1473778397696");
    	//过滤空值、sign_name参数
    	Map<String, String> sParaNew = RequestVerify.paraFilter(params);
    	 
        //获取待签名字符串
        String preSignStr = RequestVerify.createLinkString(sParaNew);
        System.out.println("待签名字符串:" + preSignStr);
        
        //待签名字符串拼上双方约定的私钥
        preSignStr = preSignStr + privateKey;
        //获取签名结果
        String mysign = DigestUtils.md5Hex(getContentBytes(preSignStr, input_charset)).toLowerCase();
    	System.out.println("签名后的hash值：" + mysign);
    	//加上签名字符串
    	params.put(SIGN_NAME, mysign);
        return params;
    }
    /**
     * 验证签名
     * @return
     */
    public boolean verifythesignature(Map<String, String> params){
    	//双方约定的签名字符串
    	String privateKey = "cl213xkj0ji1r4f653869xt1yyymj5mptgs123";    	
    	/*************验签验签验签************/
    	//需要验证的请求签名
    	//Map<String, String> params = new HashMap<String,String>();
    	//params.put("appId", "18");
    	//params.put("hashSign", "aeca0becc75c11f194147684f12efbf2");
    	//params.put("phone", "18996770076");
    	//params.put("sessionId", "b6d926acbc434295a08290192f92ebc6");
    	//params.put("time", "1473778397696");
    	//params.put("type", "2");
    	//验签，返回true或false
    	//System.out.println(RequestVerify.verify2(params, privateKey));
    	return RequestVerify.verify2(params, privateKey);
    	/*************验签验签验签************/
    }
    /**
     * 签名
     * @return
     */
    public Map<String, String> thesignature(Map<String, String> request){
    	String privateKey = "cl213xkj0ji1r4f653869xt1yyymj5mptgs123";    
    	/*************签名签名签名************/
    	//request这里面是原始请求参数，请求完了后会在request里面加hashSign和time两个值
    	/*Map<String, String> request = new HashMap<String,String>();
    	request.put("appId", "18");
    	request.put("phone", "18996770076");
    	request.put("sessionId", "b6d926acbc434295a08290192f92ebc6");
    	request.put("type", "2");*/
    	
    	//如果要验证请将sign方法里面的//params.put(SIGN_TIME, "1473778397696");注释打开
    	//签名，返回签名后的请求参数，在原request里面加hashSign和time两个值
    	request = sign(request,privateKey);
    	/*************签名签名签名************/
    	//System.out.println(request);
    	return request;
    }
    public static RequestVerify getInstance() {
		return DaoFactory.getDao(RequestVerify.class.getName());
	}

   /* public static void main(String[] args) {
    	//双方约定的签名字符串
    	String privateKey = "cl213xkj0ji1r4f653869xt1yyymj5mptgs123";    	
    	*//*************验签验签验签************//*
    	//需要验证的请求签名
    	Map<String, String> params = new HashMap<String,String>();
    	params.put("appId", "18");
    	params.put("hashSign", "aeca0becc75c11f194147684f12efbf2");
    	params.put("phone", "18996770076");
    	params.put("sessionId", "b6d926acbc434295a08290192f92ebc6");
    	params.put("time", "1473778397696");
    	params.put("type", "2");
    	//验签，返回true或false
    	System.out.println(RequestVerify.verify2(params, privateKey));
    	*//*************验签验签验签************//*
    	
    	*//*************签名签名签名************//*
    	//request这里面是原始请求参数，请求完了后会在request里面加hashSign和time两个值
    	Map<String, String> request = new HashMap<String,String>();
    	request.put("appId", "18");
    	request.put("phone", "18996770076");
    	request.put("sessionId", "b6d926acbc434295a08290192f92ebc6");
    	request.put("type", "2");
    	
    	//如果要验证请将sign方法里面的//params.put(SIGN_TIME, "1473778397696");注释打开
    	//签名，返回签名后的请求参数，在原request里面加hashSign和time两个值
    	request = sign(request,privateKey);
    	*//*************签名签名签名************//*
    	System.out.println(request);
    }*/
}
