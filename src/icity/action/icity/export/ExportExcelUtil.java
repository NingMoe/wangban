package action.icity.export;

import app.util.CommonUtils_api;

import com.icore.http.HttpResponseStatus;
import com.icore.http.util.HttpUtil;
import com.icore.util.SecurityConfig;
import com.icore.util.StringUtil;
import com.inspur.base.BaseAction;
import com.inspur.util.PathUtil;
import com.inspur.util.Tools;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeUtility;

import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import freemarker.template.Configuration;
import freemarker.template.Template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import sun.misc.BASE64Encoder;
import core.util.HttpClientUtil;

public class ExportExcelUtil extends BaseAction {
	
	private static final long serialVersionUID = -6972768054672869532L;
	private static Logger logger = LoggerFactory.getLogger(ExportExcelUtil.class);

	public void exportDetail(String itemId,String type){
		String root = PathUtil.getWebPath();// D:/myWorkspace/b/InspurICity/WebRoot/
		String dir = "file/upload/";
		Configuration configuration = new Configuration();
		configuration.setDefaultEncoding("utf-8");
		configuration.setClassForTemplateLoading(this.getClass(),"/action/icity/export/template");
		FileOutputStream  fos = null;
		Writer out = null;
		try {
			Date date = new Date();
			CommonUtils_api _commonUtils_api = CommonUtils_api.getInstance();
			String datetime = _commonUtils_api.parseDateToString(date,"yyyy年MM月dd日");
			Map<String, Object> dataMap = new HashMap<String, Object>();
			dataMap.put("datetime", datetime);
			// 查询事项信息
			String url = HttpUtil.formatUrl(SecurityConfig.getString("ItemSynchronizationUrl")+ "/getAllItemInfoByItemID?itemId=" + itemId);
			HttpClientUtil client = new HttpClientUtil();
			JSONObject item = JSONObject.fromObject(client.getResult(url, ""));
			JSONArray itemInfo = item.getJSONArray("ItemInfo");// 基本信息
			JSONObject itemBasicInfo = itemInfo.getJSONObject(0);
			item.put("itemBasicInfo", itemBasicInfo);
			JSONArray materiallist = item.getJSONArray("material");// 申请材料
			// JSONArray documentlist = item.getJSONArray("document");//标准文书
			JSONArray chargelist = item.getJSONArray("charge");// 收费标准
			JSONArray legalbasis = item.getJSONArray("legalbasis");// 法律依据
			JSONArray outmaplist = item.getJSONArray("outmap");// 办理流程
			JSONArray conditionlist = item.getJSONArray("condition");// 受理条件
			JSONArray windowlist = item.getJSONArray("window");// 办事地址
			JSONArray handlingprocess = item.getJSONArray("handlingprocess");// 办理流程
			JSONArray complainlist = null;
			try{	
				complainlist = item.getJSONArray("complain");// 法律救济（投诉）"law"
			}catch(Exception e){
				complainlist = new JSONArray();
			}
			JSONArray image = new JSONArray();//流程图展示
			for (int i = 0; i < outmaplist.size(); i++){
				String imgname = (String) JSONObject.fromObject(
						outmaplist.get(i)).get("FILE_NAME");
				String img1 = getImageStr(root + dir + imgname);
				image.add(img1);

			}
			dataMap.put("image", image);
			dataMap.put("code", itemBasicInfo.getString("CODE"));
			dataMap.put("name", itemBasicInfo.getString("NAME"));
			String strType = "{\"XK\":\"行政许可\",\"QR\":\"行政确认\",\"BA\":\"行政备案\",\"JC\":\"监督检查\",\"BM\":\"公共服务\",\"QT\":\"其他类权力\",\"ZY\":\"行政征用\",\"JF\":\"行政给付\",\"CJ\":\"行政裁决\",\"SP\":\"非行政许可\",\"CF\":\"行政处罚\",\"QZ\":\"行政强制\",\"ZS\":\"行政征收\",\"JL\":\"行政奖励\",\"FW\":\"行政服务\"}";
			JSONObject objType = JSONObject.fromObject(strType);
			dataMap.put("type", objType.getString(itemBasicInfo.getString("TYPE")));// 需加载字典项
			dataMap.put("oragin", itemBasicInfo.getString("ORG_NAME"));
			dataMap.put("region", itemBasicInfo.getString("REGION_NAME"));
			// 申请主体
			dataMap.put("agent", itemBasicInfo.getString("APPLY_ENTITY"));
			// 受理范围
			dataMap.put("shoulifanwei", "1".equals(itemBasicInfo.getString("IS_PUBLIC")) ? "公开" : "不公开");
			// 办事地址
			dataMap.put("windowlist", windowlist);
			// 受理条件
			dataMap.put("conditionlist", conditionlist);
			// 申请材料
			dataMap.put("materiallist", materiallist);	
			// 法定时限
			dataMap.put("law_time", itemBasicInfo.getString("LAW_TIME"));
			// 承诺时限
			dataMap.put("agree_time", itemBasicInfo.getString("AGREE_TIME"));
			// 受理时限
			dataMap.put("accept_time", itemBasicInfo.getString("ACCEPT_TIME"));
			// 收费
			dataMap.put("shoufeilist", chargelist);
			// 法律依据
			dataMap.put("legalbasis", legalbasis);
			// 办理流程
			dataMap.put("handlingprocess", handlingprocess);
			// 外部流程图
			dataMap.put("outmaplist", outmaplist);
			// 法律救济（投诉）
			dataMap.put("complainlist", complainlist);
			// guide为服务指南 handbook为业务手册
			if("guide".equals(type)){
				String nname = itemBasicInfo.getString("NAME") + "服务指南.doc";
				File outFile = new File(root + dir + nname);
				Template t = configuration.getTemplate("bszn.xml", "utf-8");
				fos = new FileOutputStream(outFile);
				out = new BufferedWriter(new OutputStreamWriter(fos, "utf-8"), 10240);
				t.process(dataMap, out);
			}else if("guidepl".equals(type)){//盘龙区办事指南
				String nname = itemBasicInfo.getString("NAME") + "服务指南.doc";
				File outFile = new File(root + dir + nname);
				Template t = configuration.getTemplate("bsznpl.xml", "utf-8");
				fos = new FileOutputStream(outFile);
				out = new BufferedWriter(new OutputStreamWriter(fos, "utf-8"), 10240);
				t.process(dataMap, out);
			}else{
				String nname = itemBasicInfo.getString("NAME") + "业务手册.doc";
				File outFile = new File(root + dir + nname);
				Template t = configuration.getTemplate("ywsc.xml", "utf-8");
				fos = new FileOutputStream(outFile);
				out = new BufferedWriter(new OutputStreamWriter(fos, "utf-8"), 10240);
				t.process(dataMap, out);
			}
		} catch (Exception e) {
			logger.info("下载失败："+e.getMessage());
		} finally {
			if(out!=null){
				try {
					out.close();
				} catch (IOException e) {
					logger.info(e.getMessage());
				}
			}
			if(fos!=null){
				try {
					fos.close();
				} catch (IOException e) {
					logger.info(e.getMessage());
				}
			}
			
		}
	}
 
	private String getImageStr(String imgFile) {
		// String imgFile = "d:/test.jpg";
		InputStream in = null;
		byte[] data = null;
		try {
			in = new FileInputStream(imgFile);
			data = new byte[in.available()];

			int len=data.length;
			int readBytes=0;

			while (readBytes < len) {
				int read = in.read(data);
				if (read == -1) {  //判断是不是读到了数据流的末尾 ，防止出现死循环。
					break;
				}
				readBytes += read;
			}
			in.close();
		} catch (IOException e) {
			try {
				if(null!=in){ in.close();}
			} catch (IOException e1) {

			}
		}
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(data);
	}
	public static byte[] readInputStream(InputStream inputStream)
			throws IOException {
		byte[] buffer = new byte[1024];
		int len = 0;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		while ((len = inputStream.read(buffer)) != -1) {
			bos.write(buffer, 0, len);
		}
		bos.close();
		return bos.toByteArray();
	}

	@Override
	public boolean handler(Map<String, Object> arg0) throws Exception {
		String type = this.getParameter("type");
		logger.info("进入下载："+type+"[==]"+this.getParameter("itemId"));
		exportDetail(this.getParameter("itemId"),type);
		String name = this.getParameter("name");
		try {
			if (StringUtil.isNotEmpty(name)) {
				if("guide".equals(type)||"guidepl".equals(type)){
					name = name + "服务指南.doc";
				}else if("handbook".equals(type)){
					name = name + "业务手册.doc";
				}
				String filepath = PathUtil.getWebPath() + getFilePath("") + name;
				File file = new File(filepath);
				// 如果找不到文件则抛出404错误
				if (file.exists()) {
					this.write(Tools.getFile(file));

					this.setContentType("application/octet-stream");
					String fileName = file.getName();
					if (StringUtil.isNotEmpty(name)) {
						fileName = name;
					}
					fileName =fileNameDecode(fileName);
					this.setHeader("Content-Disposition",
							"attachment;" + fileName);
				} else {
					this.getHttpAdapter().sendError(
							HttpResponseStatus.NOT_FOUND, "");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	
	private String fileNameDecode(String fileName)throws UnsupportedEncodingException {
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
	private String getFilePath(String path) {
		path = StringUtils.trimTrailingCharacter(path, '/');
		if (path != null && path.length() > 0) {
			return "file" + File.separator + "upload" + File.separator + path
					+ File.separator;
		}
		return "file" + File.separator + "upload" + File.separator;
	}

}
