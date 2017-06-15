package app.pmi.config.cmd;

import app.pmi.config.cmd.WebsiteCmd;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import app.pmi.config.ISecurity;
import app.pmi.config.JobHandle;

import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.PathUtil;
import com.inspur.util.SecurityConfig;
import com.inspur.util.Tools;
import com.inspur.util.db.DbHelper;

public class ConfigCmd extends BaseQueryCommand {
	private static Log _log = LogFactory.getLog(ConfigCmd.class);
	private static final String DISPLAY_NONE = "none";
	private static final String ENABLE_FALSE = "false";

	public DataSet getList(ParameterSet pset) {
		DataSet ds = new DataSet();
		JSONArray array = new JSONArray();
		Document jobDoc = JobHandle.getInstance().getDoc(ISecurity.JOB_FILE);
		Document registerJobDoc = JobHandle.getInstance().getDoc(ISecurity.REGISTERJOB_FILE);
		if (registerJobDoc == null) {
			registerJobDoc = DocumentHelper.createDocument();
		}
		Element registerJobRoot = registerJobDoc.getRootElement();
		Element jroot = jobDoc.getRootElement();
		List<Element> jList = jroot.elements();
		for (Element ele : jList) {
			JSONObject json = new JSONObject();
			String id = ele.attributeValue("id");
			json.put("ID", id);
			json.put("NAME", ele.elementText("name"));
			json.put("ACTION", ele.elementText("action"));
			json.put("METHOD", ele.elementText("method"));
			json.put("DEFAUT_CRON", ele.elementText("defaut_cron"));
			json.put("DESCRIBE", ele.elementText("describe"));
			json.put("ISOPEN", "0");
			json.put("CRON", "");
			if (registerJobRoot != null) {
				List<Element> rList = registerJobRoot.elements();
				for (Element rele : rList) {
					if (rele.attributeValue("id").equals(id)) {
						json.put("ISOPEN", "1");
						json.put("CRON", rele.attributeValue("cron"));
						break;
					}
				}
			}
			array.add(json);
		}
		ds.setData(Tools.stringToBytes(array.toString()));
		return ds;
	}

	public DataSet getSecurityList(ParameterSet pset) {
		String fp = (String) pset.getParameter("filePath");
		String filePath = PathUtil.getWebPath() + "/conf/" + fp;
		DataSet ds = new DataSet();
		InputStream is = null;
		InputStreamReader isr = null;
		try {
			JSONArray array = new JSONArray();
			Document securityDoc = JobHandle.getInstance().getDoc(ISecurity.SECURITY_FILE);
			Properties registerProperties = new Properties();
			File f = new File(filePath);
			if (f.isFile()) {
				is = new FileInputStream(f);
				isr = new InputStreamReader(is, "UTF-8");
				registerProperties.load(isr);
				// registerProperties.load(is);
			}
			Element jroot = securityDoc.getRootElement();
			List<Element> jList = jroot.elements();
			for (Element ele : jList) {
				List<Element> params = ele.elements();
				JSONObject group = new JSONObject();
				String display = ele.attributeValue("display");
				if (!DISPLAY_NONE.equals(display)) {
					// group.put("DISPLAY", display);
					group.put("ID", ele.attributeValue("groupId"));
					group.put("NAME", ele.attributeValue("groupName"));
					group.put("DEFAULT_VALUE", "");
					group.put("DESCRIBE", "");
					group.put("ISREGISTER", "");
					group.put("VALUE", "");
					group.put("TYPE", "GROUP");
					array.add(group);
					for (Element param : params) {
						JSONObject json = new JSONObject();
						// 隐藏
						String ed = param.attributeValue("display");
						// 启用
						String enable = param.attributeValue("enable");
						if (!DISPLAY_NONE.equals(ed) && !ENABLE_FALSE.equals(enable)) {
							String id = param.attributeValue("id");
							String visible = param.elementText("visible");
							if (StringUtils.isNotEmpty(visible) && "0".equals(visible)) {
								continue;
							}
							json.put("ID", id);
							json.put("DISPLAY", display);
							String defaultValue = param.elementText("default-value");
							json.put("DEFAULT_VALUE", defaultValue);
							json.put("DESCRIBE", param.elementText("describe"));
							json.put("ISREGISTER", "0");
							json.put("VALUE", "");
							json.put("TYPE", "PARAM");
							String value = registerProperties.getProperty(id);
							if (StringUtils.isNotEmpty(value)) {
								json.put("ISREGISTER", "1");
								json.put("VALUE", value);
							}
							array.add(json);
						}
					}
				}
			}
			ds.setData(array.toString().getBytes("UTF-8"));
			// ds.setData(Tools.stringToBytes(array.toString()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if(isr!=null){
					isr.close();
				}
				if(is!=null){
					is.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ds;
	}

	public DataSet getSecurityListxx(ParameterSet pset) {
		String fp = (String) pset.getParameter("filePath");
		String gid = (String) pset.getParameter("gid");
		String filePath = PathUtil.getWebPath() + "/conf/" + fp;
		DataSet ds = new DataSet();
		InputStreamReader isr = null;
		FileInputStream fis = null;
		try {
			JSONArray array = new JSONArray();
			Document securityDoc = JobHandle.getInstance().getDoc(ISecurity.SECURITY_FILE);
			Properties registerProperties = new Properties();
			File f = new File(filePath);
			if (f.isFile()) {
				fis = new FileInputStream(f);
				isr = new InputStreamReader(fis, "UTF-8");
				registerProperties.load(isr);
				// registerProperties.load(new InputStreamReader(is, "UTF-8"));
				// registerProperties.load(is);
			}
			Element jroot = securityDoc.getRootElement();
			List<Element> jList = jroot.elements();
			for (Element ele : jList) {
				List<Element> params = ele.elements();
				JSONObject group = new JSONObject();
				String display = ele.attributeValue("display");
				if (!DISPLAY_NONE.equals(display) && ele.attributeValue("groupId").equals(gid)) {
					// group.put("DISPLAY", display);
					group.put("ID", ele.attributeValue("groupId"));
					group.put("NAME", ele.attributeValue("groupName"));
					group.put("DEFAULT_VALUE", "");
					group.put("DESCRIBE", "");
					group.put("ISREGISTER", "");
					group.put("VALUE", "");
					group.put("TYPE", "GROUP");
					array.add(group);
					for (Element param : params) {
						JSONObject json = new JSONObject();
						// 隐藏
						String ed = param.attributeValue("display");
						// 启用
						String enable = param.attributeValue("enable");
						if (!DISPLAY_NONE.equals(ed) && !ENABLE_FALSE.equals(enable)) {
							String id = param.attributeValue("id");
							String visible = param.elementText("visible");
							if (StringUtils.isNotEmpty(visible) && "0".equals(visible)) {
								continue;
							}
							json.put("ID", id);
							json.put("DISPLAY", display);
							String defaultValue = param.elementText("default-value");
							json.put("DEFAULT_VALUE", defaultValue);
							json.put("DESCRIBE", param.elementText("describe"));
							json.put("ISREGISTER", "0");
							json.put("VALUE", "");
							json.put("TYPE", "PARAM");
							String value = registerProperties.getProperty(id);
							if (StringUtils.isNotEmpty(value)) {
								json.put("ISREGISTER", "1");
								json.put("VALUE", value);
							}
							array.add(json);
						}
					}
				}
			}
			// ds.setData(array.toString().getBytes("UTF-8"));
			ds.setData(Tools.stringToBytes(array.toString()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if(isr!=null){
					isr.close();
				}
				if(fis!=null){
					fis.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ds;
	}

	public DataSet save(ParameterSet pset) {
		DataSet ds = new DataSet();
		JSONArray array = (JSONArray) pset.getParameter("param");
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement("quartz");
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(new File(ISecurity.REGISTERJOB_FILE));
			for (int i = 0, n = array.size(); i < n; i++) {
				JSONObject json = array.getJSONObject(i);
				Element e = root.addElement("job");
				e.addAttribute("id", json.getString("id"));
				if (StringUtils.isNotEmpty(json.getString("cron"))) {
					e.addAttribute("cron", json.getString("cron"));
				}
			}
			fos.write(Tools.stringToBytes(doc.asXML()));
			JobHandle.getInstance().restart();
		} catch (FileNotFoundException e) {
			ds.setState(StateType.FAILT);
			ds.setMessage("保存配置失败");
			e.printStackTrace();
		} catch (IOException e) {
			ds.setState(StateType.FAILT);
			ds.setMessage("保存配置失败");
			e.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return ds;
	}

	public DataSet securitySave(ParameterSet pset) {
		String fp = (String) pset.getParameter("filePath");
		String filePath = PathUtil.getWebPath() + "/conf/" + fp;
		DataSet ds = new DataSet();
		JSONArray array = JSONArray.fromObject(pset.getParameter("param"));
		Properties propertise = new Properties();
		Properties individuationProp = new Properties();
		InputStream in = null;
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		try {
			in = new BufferedInputStream(new FileInputStream(filePath));
			FileInputStream fis = null;
			InputStreamReader isr = null;
			try {
				fis = new FileInputStream(new File(filePath));
				isr = new InputStreamReader(fis, "UTF-8");
				individuationProp.load(isr);
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				try {
					if(fis!=null){
						fis.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					if(isr!=null){
						isr.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// individuationProp.load(new InputStreamReader(in, "UTF-8"));
			// 站点个性化配置的几个属性 需要保存下来
			String titleImg = individuationProp.getProperty("TITLE_IMG");
			if (StringUtils.isNotEmpty(titleImg)) {
				propertise.setProperty("TITLE_IMG", titleImg);
			}
			String jsImgs = individuationProp.getProperty("PRESENTATION_IMG");
			if (StringUtils.isNotEmpty(jsImgs)) {
				propertise.setProperty("PRESENTATION_IMG", jsImgs);
			}
			String entranceFile = individuationProp.getProperty("ENTRANCE_FILE");
			if (StringUtils.isNotEmpty(entranceFile)) {
				propertise.setProperty("ENTRANCE_FILE", entranceFile);
			}
			String layout = individuationProp.getProperty("LAYOUT");
			if (StringUtils.isNotEmpty(layout)) {
				propertise.setProperty("LAYOUT", layout);
			}
			String style = individuationProp.getProperty("STYLE");
			if (StringUtils.isNotEmpty(style)) {
				propertise.setProperty("STYLE", style);
			}

			File f = new File(filePath);
			if (!f.isFile()) {
				f.createNewFile();
			}
			fos = new FileOutputStream(f);
			Enumeration<?> enu = individuationProp.propertyNames();
			while (enu.hasMoreElements()) {
				Object key = enu.nextElement();
				propertise.setProperty((String) key, individuationProp.getProperty((String) key));
			}
			for (int i = 0, n = array.size(); i < n; i++) {
				JSONObject json = array.getJSONObject(i);
				if (!(json.getString("id").equals("SXSL_URL")
						|| json.getString("id").equals("PROVINCE_EXCHANGE_ACCOUNT"))) {
					propertise.setProperty(json.getString("id"), json.getString("value"));
				}
			}
			osw = new OutputStreamWriter(fos, "UTF-8");
			propertise.store(osw, "");
		} catch (FileNotFoundException e) {
			ds.setState(StateType.FAILT);
			ds.setMessage("保存配置失败");
			e.printStackTrace();
		} catch (IOException e) {
			ds.setState(StateType.FAILT);
			ds.setMessage("保存配置失败");
			e.printStackTrace();
		} finally {
			try {
				if(osw!=null){
					osw.close();
				}
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return ds;
	}

	public DataSet getConfigInfo(ParameterSet pset) {
		DataSet ds = new DataSet();
		String key = (String) pset.getParameter("key");
		ds.setData(SecurityConfig.getString(key).getBytes());
		return ds;
	}

	/**
	 * 初始化数据库
	 * 
	 * @param pset
	 * @return
	 */
	public DataSet initHandlerData(ParameterSet pset) {
		Connection conn = DbHelper.getConnection("icityDataSource");
		DataSet ds = new DataSet();
		try {
			String sql = "select table_name from user_tables order by table_name";
			ds = DbHelper.query(sql, new Object[] {}, conn);
			JSONArray tbs;
			if (ds.getState() == 1) {
				tbs = JSONArray.fromObject(ds.getRawData());
				for (int i = 0; i < ds.getTotal(); i++) {
					String table_name = tbs.getJSONObject(i).getString("TABLE_NAME");
					if (!("POWER_CATALOG".equalsIgnoreCase(table_name) || "PUB_ID".equalsIgnoreCase(table_name)||"PUB_DICT_ITEM".equalsIgnoreCase(table_name))) {
						sql = "delete from " + table_name;
						int j = DbHelper.update(sql, new Object[] {}, conn);
						if (j > 0) {
							// _log.info("表["+table_name+"]初始化成功！");
						} else {
							// _log.info("表["+table_name+"]初始化失败！");
						}
					} else if ("PUB_DICT_ITEM".equals(table_name)) {
						sql = "delete from " + table_name +" where dict_code!='user_sfzjlx'";
						int j = DbHelper.update(sql, new Object[] {}, conn);
						if (j > 0) {
							// _log.info("表["+table_name+"]初始化成功！");
						} else {
							// _log.info("表["+table_name+"]初始化失败！");
						}
					} else if ("PUB_ID".equals(table_name)) {
						sql = "update pub_id set value='80000' where ID ='usercenter'";
						int j = DbHelper.update(sql, new Object[] {}, conn);
						sql = "update pub_id set value='0' where ID !='usercenter'";
						int k = DbHelper.update(sql, new Object[] {}, conn);
						if (j > 0) {
							// _log.info("表["+table_name+"]初始化成功！");
						} else {
							// _log.info("表["+table_name+"]初始化失败！");
						}
					}
				}
			}
			ds.setState(StateType.SUCCESS);
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		} finally {
			if (conn != null)
				try {
					conn.setAutoCommit(true);
				} catch (SQLException e) {
					e.printStackTrace();
					ds.setState(StateType.FAILT);
					ds.setMessage(e.toString());
				}
			DbHelper.closeConnection(conn);
		}
		return ds;
	}

	/**
	 * 初始化站点配置文件
	 * 
	 * @param pset
	 * @return
	 */
	public DataSet initHandlerFile(ParameterSet pset) {
		DataSet ds = new DataSet();
		try {
			WebsiteCmd websitecmd = new WebsiteCmd();
			ds = websitecmd.getWebsiteConfig(pset);
			JSONArray tbs;
			if (ds.getState() == 1) {
				tbs = JSONArray.fromObject(ds.getJAData());
				for (int i = 0; i < tbs.size(); i++) {
					String name = tbs.getJSONObject(i).getString("name");
					ParameterSet pSet = new ParameterSet();
					pSet.put("name", name);
					ds = websitecmd.deleteWebsite(pSet);
					if (ds.getState() == 1) {
						// 暂时屏蔽
						// _log.info("站点["+tbs.getJSONObject(i).getString("title")+"]初始化成功！");
					} else {
						// 暂时屏蔽
						// _log.info("站点["+tbs.getJSONObject(i).getString("title")+"]初始化失败！");
					}
				}
			}
			ds = websitecmd.getWebsiteConfig(pset);
			if (ds.getState() == 1) {
				tbs = JSONArray.fromObject(ds.getJAData());
				if (tbs.size() > 0) {
					initHandlerFile(pset);
				}
			}
			ds.setState(StateType.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		} finally {

		}
		return ds;
	}

}
