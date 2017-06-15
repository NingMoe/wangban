package app.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.icore.util.DaoFactory;

public class UserAgentTool {
		
	public static UserAgentTool getInstance() {
		return DaoFactory.getDao(UserAgentTool.class.getName());
	}
	
	/*
	 百度浏览器	Google Chrome	IE 8.0	Firefox	   搜狗高速  	IE 9.0	猎豹浏览器	QQ浏览器 	其他
	IE 6.0	IE 10.0	傲游浏览器	2345浏览器	世界之窗   	Safari	360浏览器	淘宝浏览器	IE 7.0	枫树浏览器     */

	private final static String IE11="MSIE 11.0";    
	private final static String IE10="MSIE 10.0";    
	private final static String IE9="MSIE 9.0";    
    private final static String IE8="MSIE 8.0";    
    private final static String IE7="MSIE 7.0";    
    private final static String IE6="MSIE 6.0";    
    private final static String MAXTHON="Maxthon";    
    private final static String QQ="QQBrowser";    
    private final static String GREEN="GreenBrowser";    
    private final static String SE360="360SE";    
    private final static String FIREFOX="Firefox";    
    private final static String OPERA="Opera";    
    private final static String CHROME="Chrome";    
    private final static String SAFARI="Safari";    
    private final static String OTHER="其它";  
    
    private final static String desIE11="IE 11.0";    
    private final static String desIE10="IE 10.0";    
    private final static String desIE9="IE 9.0";    
    private final static String desIE8="IE 8.0";    
    private final static String desIE7="IE 7.0";    
    private final static String desIE6="IE 6.0";    
    private final static String desMAXTHON="傲游浏览器";    
    private final static String desQQ="QQ浏览器";    
    private final static String desGREEN="Green Browser";    
    private final static String desSE360="360安全浏览器";    
    private final static String desFIREFOX="火狐浏览器";    
    private final static String desOPERA="Opera浏览器";    
    private final static String desCHROME="谷歌浏览器";    
    private final static String desSAFARI="Safari浏览器";  
    
	private final static String LINUX="Linux";    
	private final static String WINDOWS2000="Windows NT 5.0";    
	private final static String WINDOWSXPSP3="Windows NT 5.1";    
	private final static String WINDOWSVISTA="Windows; U; Windows NT 6.";    
    private final static String WINDOWS7="Windows NT 6.1; WOW64;";    
    private final static String WINDOWS8="Windows NT 6.2; WOW64";    
    private final static String WINDOWS8_1="Windows NT 6.3; WOW64";    
    private final static String MACOS="Mac OS";    
    private final static String ANDROID ="Android";    
    private final static String IPHONE ="iPhone";    
    private final static String IPAD ="iPad";    
    
    private final static String desLINUX="Linux";    
	private final static String desWINDOWS2000="Windows 2000";    
	private final static String desWINDOWSXPSP3="Windows XP";    
	private final static String desWINDOWSVISTA="Windows Vista";    
    private final static String desWINDOWS7="Windows 7";    
    private final static String desWINDOWS8="Windows 8";    
    private final static String desWINDOWS8_1="Windows 8.1";    
    private final static String desMACOS="Mac OS";    
    private final static String desANDROID ="Android";    
    private final static String desIPHONE ="iPhone";    
    private final static String desIPAD ="iPad";  
        
    public boolean regex(String regex,String str){    
        Pattern p =Pattern.compile(regex,Pattern.MULTILINE);    
        Matcher m=p.matcher(str);    
        return m.find();    
    }    
        
    public String checkBrowse(String userAgent){    
        if(regex(OPERA, userAgent))return desOPERA;    
        if(regex(CHROME, userAgent))return desCHROME;    
        if(regex(FIREFOX, userAgent))return desFIREFOX;    
        if(regex(SAFARI, userAgent))return desSAFARI;    
        if(regex(SE360, userAgent))return desSE360;    
        if(regex(GREEN,userAgent))return desGREEN;    
        if(regex(QQ,userAgent))return desQQ;    
        if(regex(MAXTHON, userAgent))return desMAXTHON;    
        if(regex(IE11,userAgent))return desIE11;    
        if(regex(IE10,userAgent))return desIE10;    
        if(regex(IE9,userAgent))return desIE9;    
        if(regex(IE8,userAgent))return desIE8;    
        if(regex(IE7,userAgent))return desIE7;    
        if(regex(IE6,userAgent))return desIE6;    
        return OTHER;    
    } 
    
    public String checkOs(String userAgent){    
    	if(regex(LINUX, userAgent))return desLINUX;    
    	if(regex(WINDOWS2000, userAgent))return desWINDOWS2000;    
    	if(regex(WINDOWSXPSP3, userAgent))return desWINDOWSXPSP3;    
    	if(regex(WINDOWSVISTA, userAgent))return desWINDOWSVISTA;    
    	if(regex(WINDOWS7, userAgent))return desWINDOWS7;    
    	if(regex(WINDOWS8,userAgent))return desWINDOWS8;    
    	if(regex(WINDOWS8_1,userAgent))return desWINDOWS8_1;    
    	if(regex(MACOS,userAgent))return desMACOS;    
    	if(regex(ANDROID,userAgent))return desANDROID;    
    	if(regex(IPHONE,userAgent))return desIPHONE;    
    	if(regex(IPAD,userAgent))return desIPAD;    
    	return OTHER;    
    }    
    
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(UserAgentTool.getInstance().checkBrowse("Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.5 Safari/537.36"));   
		System.out.println(UserAgentTool.getInstance().checkOs("Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36 SE 2.X MetaSr 1.0"));   
	}
}
