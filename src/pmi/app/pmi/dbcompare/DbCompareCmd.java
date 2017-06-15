package app.pmi.dbcompare;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import net.sf.json.JSONArray;

import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.PathUtil;
import com.inspur.util.Tools;

public class DbCompareCmd extends BaseQueryCommand {
	private static HashMap<String, HashMap<String, String>> dbInfoMap = new HashMap<String, HashMap<String, String>>(); // 数据源配置信息
	private static HashMap<HashMap<String, String>, DbCompareDao> dbInstances = new HashMap<HashMap<String, String>, DbCompareDao>(); // 保存已初始化类

	static {
		try {
			String filePath = PathUtil.getWebPath() + "public/db/db.xml";
			Document document = DbCompareTool.readDoc(filePath);
			if(document!=null){
				List<?> list = document.selectNodes("/root/list/db");
				for (int i = 0; i < list.size(); i++) {
					HashMap<String, String> dbMap = new HashMap<String, String>();
					Element element = (Element) list.get(i);
					String elename = element.attributeValue("name");
					dbMap.put("name", elename);
					dbMap.put("dsName", element.attributeValue("dsName"));
					dbMap.put("schem", element.attributeValue("schem"));
					dbMap.put("dbType", element.attributeValue("dbType"));
					dbMap.put("class", element.attributeValue("class"));
					dbInfoMap.put(elename, dbMap);
				}
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 初始化后台类
	 * 
	 * @param dbMap
	 *            数据源配置信息
	 */
	private static DbCompareDao getDbCompareDao(HashMap<String, String> dbMap) {
		DbCompareDao instance = null;
		try {
			instance = dbInstances.get(dbMap);
			if (instance == null) {
				synchronized (DbCompareDao.class) {
					Class<?> clazz = Class.forName(dbMap.get("class"));
					Class<?>[] param = new Class[] { HashMap.class };
					Constructor<?> structor = clazz.getConstructor(param);
					Object[] args = new Object[] { dbMap };
					instance = (DbCompareDao) structor.newInstance(args);
					dbInstances.put(dbMap, instance);
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return instance;
	}

	/**
	 * 导出所有表结构到xml文件
	 * 
	 * @param pSet
	 *            参数集
	 */
	public DataSet initAllTableXml(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			StringBuffer message = new StringBuffer();
			String dataSource = (String) pSet.getParameter("dataSource");
			String[] dsName = dataSource.split(",");
			for (int i = 0; i < dsName.length; i++) {
				HashMap<String, String> dbMap = dbInfoMap.get(dsName[i]);
				DbCompareDao dao = DbCompareCmd.getDbCompareDao(dbMap);
				ds = dao.initAllTableXml();
				if (ds.getState() == StateType.FAILT) {
					message.append(ds.getMessage()).append("\n");
				}
			}
			if (message.length() > 0) {
				ds.setState(StateType.FAILT);
				ds.setMessage(message.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
		}
		return ds;
	}

	/**
	 * 导入所有表结构到数据库
	 * 
	 * @param pSet
	 *            参数集
	 */
	public DataSet initAllTableSjk(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			StringBuffer sqlbuff = new StringBuffer();
			String dataSource = (String) pSet.getParameter("dataSource");
			String[] dsName = dataSource.split(",");
			for (int i = 0; i < dsName.length; i++) {
				HashMap<String, String> dbMap = dbInfoMap.get(dsName[i]);
				DbCompareDao dao = DbCompareCmd.getDbCompareDao(dbMap);
				ds = dao.initAllTableSjk();
				if (ds.getState() == StateType.SUCCESS) {
					sqlbuff.append(Tools.bytesToString(ds.getData()));
				}
			}
			if (sqlbuff.length() > 0) {
				ds.setData(Tools.stringToBytes(sqlbuff.toString()));
			} else {
				ds.setState(StateType.FAILT);
			}
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
		}
		return ds;
	}

	/**
	 * 将选中表导出为xml文件
	 * 
	 * @param pSet
	 *            参数集
	 */
	public DataSet initSelectTableXml(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String dsName = (String) pSet.getParameter("dsName");
			JSONArray tableArray = (JSONArray) pSet.getParameter("tableArray");
			HashMap<String, String> dbMap = dbInfoMap.get(dsName);
			DbCompareDao dao = DbCompareCmd.getDbCompareDao(dbMap);
			ds = dao.initSelectTableXml(tableArray);
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("导出文件失败！");
		}
		return ds;
	}

	/**
	 * 将选中表导入到数据库
	 * 
	 * @param pSet
	 *            参数集
	 */
	public DataSet initSelectTableSjk(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String dsName = (String) pSet.getParameter("dsName");
			JSONArray tableArray = (JSONArray) pSet.getParameter("tableArray");
			HashMap<String, String> dbMap = dbInfoMap.get(dsName);
			DbCompareDao dao = DbCompareCmd.getDbCompareDao(dbMap);
			ds = dao.initSelectTableSjk(tableArray);
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
		}
		return ds;
	}

	/**
	 * 将选中数据导出到报文
	 * 
	 * @param pSet
	 *            参数集
	 */
	public DataSet initSelectDataXml(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String dsName = (String) pSet.getParameter("dsName");
			String tableName = (String) pSet.getParameter("tableName");
			JSONArray rowArray = (JSONArray) pSet.getParameter("rowArray");
			HashMap<String, String> dbMap = dbInfoMap.get(dsName);
			DbCompareDao dao = DbCompareCmd.getDbCompareDao(dbMap);
			ds = dao.initSelectDataXml(tableName, rowArray);
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
		}
		return ds;
	}

	/**
	 * 将选中数据导入到数据库
	 * 
	 * @param pSet
	 *            参数集
	 */
	public DataSet initSelectDataSjk(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String dsName = (String) pSet.getParameter("dsName");
			String tableName = (String) pSet.getParameter("tableName");
			JSONArray rowArray = (JSONArray) pSet.getParameter("rowArray");
			HashMap<String, String> dbMap = dbInfoMap.get(dsName);
			DbCompareDao dao = DbCompareCmd.getDbCompareDao(dbMap);
			ds = dao.initSelectDataSjk(tableName, rowArray);
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
		}
		return ds;
	}

	/**
	 * 比较数据源中所有表结构和表数据信息
	 * 
	 * @param pSet
	 *            参数集
	 */
	public DataSet compareTableInfo(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			boolean diff = "1".equals((String) pSet.getParameter("diff")) ? true : false;
			String dsName = (String) pSet.getParameter("dsName");
			HashMap<String, String> dbMap = dbInfoMap.get(dsName);
			DbCompareDao dao = DbCompareCmd.getDbCompareDao(dbMap);
			ds = dao.compareTableInfo(diff);
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
		}
		return ds;
	}

	/**
	 * 比较表结构信息
	 * 
	 * @param pSet
	 *            参数集
	 */
	public DataSet compareTableDefine(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			boolean diff = "1".equals((String) pSet.getParameter("diff")) ? true : false;
			String dsName = (String) pSet.getParameter("dsName");
			String tableName = (String) pSet.getParameter("tableName");
			HashMap<String, String> dbMap = dbInfoMap.get(dsName);
			DbCompareDao dao = DbCompareCmd.getDbCompareDao(dbMap);
			ds = dao.compareTableDefine(tableName, diff);
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
		}
		return ds;
	}

	/**
	 * 比较表数据信息
	 * 
	 * @param pSet
	 *            参数集
	 */
	public DataSet compareTableData(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			boolean diff = "1".equals((String) pSet.getParameter("diff")) ? true : false;
			String dsName = (String) pSet.getParameter("dsName");
			String tableName = (String) pSet.getParameter("tableName");
			HashMap<String, String> dbMap = dbInfoMap.get(dsName);
			DbCompareDao dao = DbCompareCmd.getDbCompareDao(dbMap);
			int start = pSet.getPageStart();
			int limit = pSet.getPageLimit();
			if (start > 0 && limit > 0) {
				ds = dao.compareTableData(tableName, diff, start, limit);
			} else {
				ds = dao.compareTableData(tableName, diff, start, limit);
			}
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
		}
		return ds;
	}

	/**
	 * 执行sql语句
	 * 
	 * @param pSet
	 *            参数集
	 */
	public DataSet executeSql(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String sql = (String) pSet.getParameter("sql");
			String dsName = (String) pSet.getParameter("dsName");
			HashMap<String, String> dbMap = dbInfoMap.get(dsName);
			DbCompareDao dao = DbCompareCmd.getDbCompareDao(dbMap);
			ds = dao.executeSql(sql);
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
		}
		return ds;
	}
}
