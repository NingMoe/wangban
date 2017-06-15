package app.db.bean;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.verify.web.WebVerifyClient;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.inspur.util.PathUtil;
import com.inspur.util.Tools;





public class ProxyInfo {
	
	
	
	private static final String proxyDir = PathUtil.getWebPath()+"public"+File.separator+"proxy"+File.separator;
	
	private Map<String,ProxyBean> items = new HashMap<String, ProxyBean>();
	private static Map<String,Long> lastUpdateTime = new HashMap<String,Long>();
	private static ProxyInfo _instance = null;
	
	private String getValue(Element e,String tag,String defValue){
		Attribute a = e.attribute(tag);
    	if(a != null){
    		return a.getValue();
    	}else{
    		return defValue;
    	}
	}
	
	
	private ProxyInfo() throws java.lang.Exception{
		File pDir = new File(proxyDir);
		File[] files = pDir.listFiles(new FileFilter() {
			   public boolean accept(File file) {
				   String fileName = file.getName();
				   if(fileName.substring(fileName.lastIndexOf(".")).equalsIgnoreCase(".xml")){
					   return true;
				   }
				   return false;
			   }
			  });
		items.clear();
		lastUpdateTime.clear();
		for(int fi=0;fi<files.length;fi++){
			try{
				lastUpdateTime.put(files[fi].getName(), files[fi].lastModified());
				SAXReader saxReader = new SAXReader();
		        Document document = saxReader.read(new InputStreamReader(new FileInputStream(files[fi]), "utf-8"));
//		        System.out.println(document.asXML());
		        //获取默认数据源
		        String strDefaultDataSource = "dataSource";
		        Element defDataSourceE = (Element)document.selectSingleNode("/root/defaultDataSource");
		        if(defDataSourceE != null){
		        	strDefaultDataSource = defDataSourceE.getText();
		        }
		        
		        //遍历所有proxy信息
		        List<Element> listE = document.selectNodes("/root/list/proxy");
		        for(int li=0;li<listE.size();li++){
		        	Element e = listE.get(li);
		        	String name = getValue(e,"name","").toUpperCase();
		        	String title = getValue(e,"title","");
		        	String ds =  getValue(e,"dataSource",strDefaultDataSource);
		        	if(StringUtils.isNotEmpty(name)){
		        		ProxyBean pBean = new ProxyBean();
		        		pBean.setDataSource(ds);
		        		pBean.setName(name);
		        		pBean.setTitle(title);
		        		//获取sql信息
		        		Node sqlNode = e.selectSingleNode("sql");
		        		if(sqlNode != null){
		        			String sql = sqlNode.getText();
		        			if(StringUtils.isNotEmpty(sql)){
		        				pBean.setSql(sql.trim());
		        			}
		        		}
		        		//获取other信息
		        		Node otherNode = e.selectSingleNode("other");
		        		if(otherNode != null){
		        			String other = otherNode.getText();
		        			if(StringUtils.isNotEmpty(other)){
		        				pBean.setOther(other.trim());
		        			}
		        		}
		        		//遍历所有parameters信息
	        			List<Element> listP = e.selectNodes("parameters/parameter");
		        		List<ParameterBean> pBeanList = new ArrayList<ParameterBean>();
		        		for(int lpi = 0;lpi<listP.size();lpi++){
		        			Element pE = listP.get(lpi);
		        			ParameterBean pb = new ParameterBean();
		        			pb.setDef(getValue(pE,"def",""));
		        			pb.setMust(getValue(pE,"must","false").equalsIgnoreCase("true"));
		        			pb.setName(getValue(pE,"name",""));
		        			pb.setType(getValue(pE,"type",""));
		        			pBeanList.add(pb);
		        		}
	        			pBean.setParameters(pBeanList);
	        			
	        			//遍历所有constants信息
	        			listP = e.selectNodes("constants/constant");
		        		pBeanList = new ArrayList<ParameterBean>();
		        		for(int lpi = 0;lpi<listP.size();lpi++){
		        			Element pE = listP.get(lpi);
		        			ParameterBean pb = new ParameterBean();
		        			pb.setDef(getValue(pE,"def",""));
		        			pb.setMust(getValue(pE,"must","false").equalsIgnoreCase("true"));
		        			pb.setName(getValue(pE,"name",""));
		        			pb.setType(getValue(pE,"type",""));
		        			pBeanList.add(pb);
		        		}
	        			pBean.setConstants(pBeanList);
		 	        	items.put(name, pBean);
		        	}
		        }
			}
			catch(Exception ex){
				System.out.println("文件："+files[fi].getName()+"配置错误：");
				ex.printStackTrace();
				throw ex;
			}
        }
	}
	public static ProxyInfo getInstance() throws Exception{
		if(!Tools.productModel()){
			synchronized (ProxyInfo.class){
				File f = new File(proxyDir);
				if(f.exists() && f.isDirectory()){
					File[] files = f.listFiles();
					for(int i=0;i<files.length;i++){
						if(!lastUpdateTime.containsKey(files[i].getName())){
							_instance = null;
							break;
						}
						long l = lastUpdateTime.get(files[i].getName());
						if(l == 0){
							_instance = null;
							break;
						}
						if(files[i].lastModified()>l){
							_instance = null;
							break;
						}
					}
				}
			}
		}
		if (_instance == null) {
			synchronized (ProxyInfo.class) {
				if (_instance == null) {
					try {
						_instance = new ProxyInfo();
					} catch (MalformedURLException e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					} catch (Exception e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
						throw new Exception("proxy文件配置错误，请检查！");
					}
				}
			}
		}
		return _instance;
	}
	
	/**************************************************************/
	/*                        公共方法                                                                            */
	/**************************************************************/
	public void clear(){
		synchronized (ProxyInfo.class){
			if (_instance == null){
				try {
					_instance = new ProxyInfo();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	public ProxyBean get(String name){
		return items.get(name);
	}
	public static void main(String[] args) throws Exception{
		ProxyInfo.getInstance().clear();
		System.out.println("OK");
	}
}
