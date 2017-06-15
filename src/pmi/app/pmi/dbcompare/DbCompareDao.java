package app.pmi.dbcompare;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.icore.util.PathUtil;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.util.Tools;
import com.inspur.util.db.DBSource;
import com.inspur.util.db.DbHelper;

import core.util.CommonUtils;

/**
 * 数据库比较器
 * 
 * @author XiongZhiwen
 */
public abstract class DbCompareDao extends BaseJdbcDao {
	protected HashMap<String, String> dbMap = new HashMap<String, String>(); // 数据源配置信息
	protected HashMap<String, String> dbToXmlMap = new HashMap<String, String>(); // 数据库字段类型映射到xml字段类型
	protected HashMap<String, String> xmlToDbMap = new HashMap<String, String>(); // xml字段类型映射到数据库字段类型

	/**
	 * 公共构造函数
	 * 
	 * @param dbMap
	 *            数据源配置信息
	 * @param conn
	 *            数据库连接
	 */
	public DbCompareDao(HashMap<String, String> dbMap) {
		this.dbMap = dbMap;

		// 初始化数据库字段类型与xml字段类型映射关系
		String ysPath = PathUtil.getWebPath() + "public/db/ys.xml";
		Document document = DbCompareTool.readDoc(ysPath);
		if (document != null && dbMap != null) {
			List<?> list = document.selectNodes("/root/bean[@dbType='" + dbMap.get("dbType") + "']/item");
			for (int i = 0; i < list.size(); i++) {
				Element element = (Element) list.get(i);
				String xml = element.attributeValue("xml"); // xml字段类型
				String db = element.attributeValue("db"); // 数据库字段类型
				String defdb = element.attributeValue("defdb"); // xml对应数据库字段类型
				if (StringUtils.isNotEmpty(db)) {
					String[] dbArr = db.split(",");
					for (int j = 0; j < dbArr.length; j++) {
						dbToXmlMap.put(dbArr[j], xml);
					}
				}
				if (StringUtils.isNotEmpty(xml)) {
					xmlToDbMap.put(xml, defdb);
				}
			}
		}
	}

	/**
	 * 导出所有表结构到xml文件
	 * 
	 * @return DataSet 数据集
	 */
	public DataSet initAllTableXml() {
		DataSet ds = new DataSet();
		try {
			final StringBuffer message = new StringBuffer();
			HashSet<String> tableNameSet = this.getTableName("sjk");
			final Object[] tableNameArr = tableNameSet.toArray();
			int maxsize = tableNameArr.length;
			RunThreadPool pool = new RunThreadPool(maxsize);
			ArrayList<ArrayList<Integer>> pos = pool.getPos();
			for (int i = 0; i < pos.size(); i++) {
				final ArrayList<Integer> list = pos.get(i);
				pool.add(new Runnable() {
					int start = list.get(0), end = list.get(1);

					@Override
					public void run() {
						for (int m = start; m < end + 1; m++) {
							String tableName = (String) tableNameArr[m];
							DataSet wds = writeDefineXml(tableName);
							if (wds.getState() == StateType.FAILT) {
								message.append(wds.getMessage()).append(";\n");
							}
						}
					}
				});
			}
			pool.start();
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
	 * @return DataSet 数据集
	 */
	public DataSet initAllTableSjk() {
		DataSet ds = new DataSet();
		try {
			final StringBuffer sqlbuff = new StringBuffer();
			HashSet<String> tableNameSet = this.getTableName("xml");
			final Object[] tableNameArr = tableNameSet.toArray();
			int maxsize = tableNameArr.length;
			RunThreadPool pool = new RunThreadPool(maxsize);
			ArrayList<ArrayList<Integer>> pos = pool.getPos();
			for (int i = 0; i < pos.size(); i++) {
				final ArrayList<Integer> list = pos.get(i);
				pool.add(new Runnable() {
					int start = list.get(0), end = list.get(1);

					@Override
					public void run() {
						for (int m = start; m < end + 1; m++) {
							String tableName = (String) tableNameArr[m];
							DataSet tds = writeDefineSjk(tableName);
							if (tds.getState() == StateType.SUCCESS) {
								sqlbuff.append(Tools.bytesToString(tds.getData()));
								tds = writeDataSjk(tableName, true, null);
								if (tds.getState() == StateType.SUCCESS) {
									sqlbuff.append(Tools.bytesToString(tds.getData()));
								}
							}
						}
					}
				});
			}
			pool.start();
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
	 * @param tableArray
	 *            由表名组成数组
	 */
	public DataSet initSelectTableXml(final JSONArray tableArray) {
		DataSet ds = new DataSet();
		try {
			final StringBuffer message = new StringBuffer();
			int maxsize = tableArray.size();
			RunThreadPool pool = new RunThreadPool(maxsize);
			ArrayList<ArrayList<Integer>> pos = pool.getPos();
			for (int i = 0; i < pos.size(); i++) {
				final ArrayList<Integer> list = pos.get(i);
				pool.add(new Runnable() {
					int start = list.get(0), end = list.get(1);

					@Override
					public void run() {
						for (int m = start; m < end + 1; m++) {
							String tableName = (String) tableArray.get(m);
							DataSet wds = writeDefineXml(tableName);
							if (wds.getState() == StateType.FAILT) {
								message.append(wds.getMessage()).append(";\n");
							}
						}
					}
				});
			}
			pool.start();
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
	 * 将选中表导入到数据库
	 * 
	 * @param tableArray
	 *            由表名组成数组
	 */
	public DataSet initSelectTableSjk(JSONArray tableArray) {
		DataSet ds = new DataSet();
		try {
			final StringBuffer sqlbuff = new StringBuffer();
			final Object[] tableNameArr = tableArray.toArray();
			int maxsize = tableNameArr.length;
			RunThreadPool pool = new RunThreadPool(maxsize);
			ArrayList<ArrayList<Integer>> pos = pool.getPos();
			for (int i = 0; i < pos.size(); i++) {
				final ArrayList<Integer> list = pos.get(i);
				pool.add(new Runnable() {
					int start = list.get(0), end = list.get(1);

					@Override
					public void run() {
						for (int m = start; m < end + 1; m++) {
							String tableName = (String) tableNameArr[m];
							DataSet tds = writeDefineSjk(tableName);
							if (tds.getState() == StateType.SUCCESS) {
								sqlbuff.append(Tools.bytesToString(tds.getData()));
								tds = writeDataSjk(tableName, true, null);
								if (tds.getState() == StateType.SUCCESS) {
									sqlbuff.append(Tools.bytesToString(tds.getData()));
								}
							}
						}
					}
				});
			}
			pool.start();
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
	 * 将选中数据导出到报文
	 * 
	 * @param tableName
	 *            表名
	 * @param rowArray
	 *            由主键组成索引对象
	 */
	public DataSet initSelectDataXml(String tableName, JSONArray rowArray) {
		DataSet ds = new DataSet();
		try {
			ds = this.writeDefineXml(tableName);
			if (ds.getState() == StateType.SUCCESS) {
				ds = this.writeDataXml(tableName, rowArray);
			}
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
	 * @param tableName
	 *            表名
	 * @param rowArray
	 *            由主键组成索引对象
	 */
	public DataSet initSelectDataSjk(String tableName, JSONArray rowArray) {
		DataSet ds = new DataSet();
		try {
			StringBuffer sqlbuff = new StringBuffer();
			ds = this.writeDefineSjk(tableName);
			if (ds.getState() == StateType.SUCCESS) {
				sqlbuff.append(Tools.bytesToString(ds.getData()));
				ds = this.writeDataSjk(tableName, false, rowArray);
				if (ds.getState() == StateType.SUCCESS) {
					sqlbuff.append(Tools.bytesToString(ds.getData()));
					ds.setData(Tools.stringToBytes(sqlbuff.toString()));
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
	 * 比较数据源中所有表结构信息
	 * 
	 * @param diffFlag
	 *            为1只返回比对差异数据，为0返回所有比对数据
	 */
	public DataSet compareTableInfo(final boolean diffFlag) {
		DataSet ds = new DataSet();
		try {
			final JSONArray ret = new JSONArray(); // 比较结果数组，对象类型，引用地址不可改变,存放内容可改变
			final HashSet<String> tableNameXml = this.getTableName("xml");
			final HashSet<String> tableNameSjk = this.getTableName("sjk");
			final HashMap<String, String> tableDesc = this.getTableDesc();
			Object[] tableNameArr = ArrayUtils.addAll(tableNameXml.toArray(), tableNameSjk.toArray());
			final List<String> tableNameList = new ArrayList<String>();
			for (int k = 0; k < tableNameArr.length; k++) {
				String tabName = (String) tableNameArr[k];
				if (!tableNameList.contains(tabName)) {
					tableNameList.add(tabName);
				}
			}
			int maxsize = tableNameList.size();
			RunThreadPool pool = new RunThreadPool(maxsize);
			ArrayList<ArrayList<Integer>> pos = pool.getPos();
			for (int i = 0; i < pos.size(); i++) {
				final ArrayList<Integer> list = pos.get(i);
				pool.add(new Runnable() {
					int start = list.get(0), end = list.get(1);

					@Override
					public void run() {
						for (int m = start; m < end + 1; m++) {
							String tableName = (String) tableNameList.get(m);
							JSONObject jg = new JSONObject();
							jg.put("DSNAME", dbMap.get("name"));
							jg.put("TABLE_NAME", tableName);
							jg.put("TABLE_DESC", tableDesc.get(tableName));
							if (tableNameXml.contains(tableName)) {
								jg.put("EXISTS", "1");
								if (tableNameSjk.contains(tableName)) {
									jg.put("SOURCE", "all");
									DataSet cds = compareTwoDefine(null, tableName, diffFlag, true);
									if (cds.getState() == StateType.FAILT) {
										jg.put("JG", "0");
										ret.add(jg);
									} else {
										cds = compareTwoData(null, tableName, diffFlag, 0, 0, true);
										if (cds.getState() == StateType.FAILT) {
											jg.put("JG", "0");
											ret.add(jg);
										} else {
											jg.put("JG", "1");
											if (!diffFlag) {
												ret.add(jg);
											}
										}
									}
								} else {
									jg.put("SOURCE", "xml");
									jg.put("JG", "0");
									ret.add(jg);
								}
							} else {
								jg.put("EXISTS", "0");
								jg.put("SOURCE", "sjk");
								jg.put("JG", "0");
								ret.add(jg);
							}
						}
					}
				});
			}
			pool.start();
			// 写入结果到数据集
			ds.setData(Tools.stringToBytes(ret.toString()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}

	/**
	 * 比较表结构定义信息
	 * 
	 * @param tableName
	 *            表名
	 * @param diffFlag
	 *            为1只返回比对差异数据，为0返回所有比对数据
	 */
	public DataSet compareTableDefine(String tableName, boolean diffFlag) {
		DataSet ds = new DataSet();
		try {
			JSONArray ret = new JSONArray();
			ds = this.compareTwoDefine(ret, tableName, diffFlag, false);
			ds.setData(Tools.stringToBytes(ret.toString()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}

	/**
	 * 比较表数据信息
	 * 
	 * @param String
	 *            表名
	 * @param diffFlag
	 *            为1只返回比对差异数据，为0返回所有比对数据
	 */
	public DataSet compareTableData(String tableName, boolean diffFlag, int start, int limit) {
		DataSet ds = new DataSet();
		try {
			JSONArray ret = new JSONArray();
			ds = this.compareTwoData(ret, tableName, diffFlag, start, limit, false);
			ds.setData(Tools.stringToBytes(ret.toString()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}

	protected DataSet executeSql(String sql) {
		DataSet ds = new DataSet();
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = DBSource.getConnection(dbMap.get("dsName"));
			conn.setAutoCommit(false);
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			String[] sqlarr = sql.split(";\n");
			for (int i = 0; i < sqlarr.length; i++) {
				if (StringUtils.isNotEmpty(sqlarr[i])) {
					stmt.addBatch(sqlarr[i]);
				}
			}
			stmt.executeBatch();
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				if (conn != null) {
					conn.rollback();
				}
			} catch (SQLException se) {
				se.printStackTrace();
			}
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			DbCompareTool.closeConn(conn);
		}
		return ds;
	}

	/*********************************************
	 * 本行以上为cmd调用方法，本行以下为调用方法的实现
	 *******************************************************/

	/**
	 * 获取表名对应中文名称
	 * 
	 * @return HashMap<String,String> 表名对应中文名称
	 */
	protected abstract HashMap<String, String> getTableDesc();

	/**
	 * 获取表名对应字段及字段中文名称
	 * 
	 * @param tableName
	 *            表名
	 * 
	 * @return HashMap<String,HashMap<String,String>> 表名对应字段及字段中文名称
	 */
	protected abstract HashMap<String, HashMap<String, String>> getColumnDesc(String tableName);

	/**
	 * 创建表结构
	 * 
	 * @param tableName
	 *            表名
	 */
	protected abstract DataSet createTable(String tableName);

	/**
	 * 修改表结构
	 * 
	 * @param tableName
	 *            表名
	 */
	protected abstract DataSet modifyTable(String tableName);

	/**
	 * 生成在数据库端进行日期转换格式
	 * 
	 * @param str
	 *            日期字符串格式
	 */
	protected abstract String formatStrToDate(String str);

	/**
	 * 导出表结构到xml文件
	 * 
	 * @param tableName
	 *            表名
	 * @return DataSet 数据集
	 */
	protected DataSet writeDefineXml(String tableName) {
		DataSet ds = new DataSet();
		try {
			boolean success = false;
			HashMap<String, String> tableDesc = this.getTableDesc();
			Document document = DocumentHelper.createDocument();
			Element root = document.addElement("root");
			root.addAttribute("desc", tableDesc.get(tableName));
			root.addAttribute("createTime", DbCompareTool.formatDateToStr(new Date()));
			Element bean = root.addElement("bean");
			HashSet<String> keySet = this.getKeySet(tableName, "sjk"); // 数据库主键
			HashMap<String, String> colDesc = this.getColumnDesc(tableName).get(tableName); // 字段名对应中文名称
			HashMap<String, HashMap<String, String>> defineMap = this.getDefine(tableName, "sjk"); // 表结构信息
			Iterator<Entry<String, HashMap<String, String>>> it = defineMap.entrySet().iterator();
			while (it.hasNext()) {
				Element item = bean.addElement("item");
				Entry<String, HashMap<String, String>> iterator = it.next();
				String colname = iterator.getKey();
				HashMap<String, String> fieldMap = iterator.getValue(); // 字段结构信息
				item.addAttribute("name", colname);
				item.addAttribute("iskey", keySet.contains(colname) ? "true" : "false");
				item.addAttribute("title", colDesc.get(colname));
				item.addAttribute("must", "1".equals(fieldMap.get("NULLABLE")) ? "false" : "true");
				item.addAttribute("type", dbToXmlMap.get(fieldMap.get("TYPE_NAME")));
				item.addAttribute("typeFormat", fieldMap.get("COLUMN_SIZE"));
			}
			success = DbCompareDao.writeXml(dbMap.get("name"), tableName, document);
			if (!success) {
				ds.setState(StateType.FAILT);
				ds.setMessage(tableName);
			}
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage(tableName);
		}
		return ds;
	}

	/**
	 * 导入表结构到数据库
	 * 
	 * @param tableName
	 *            表名
	 */
	protected DataSet writeDefineSjk(String tableName) {
		HashSet<String> tableNameSet = this.getTableName("sjk");
		if (tableNameSet.contains(tableName)) {
			return modifyTable(tableName);
		} else {
			return createTable(tableName);
		}
	}

	/**
	 * 导入表数据到数据库
	 * 
	 * @param tableName
	 *            表名
	 * @param allFlag
	 *            为true写入xml文件中所有数据。为false写入rowArray对应数据
	 * @param rowArray
	 *            由主键列组成json索引对象数组
	 */
	protected DataSet writeDataSjk(String tableName, boolean allFlag, JSONArray rowArray) {
		DataSet ds = new DataSet();
		try {
			Document document = DbCompareDao.readXml(dbMap.get("name"), tableName);
			if (document != null) {
				HashMap<String, String> tableDesc = this.getTableDesc();
				List<?> elements = document.selectNodes("/root/init/sql");
				if (elements.size() > 0) {
					HashSet<String> keySet = this.getKeySet(tableName, "xml");
					if (allFlag) {
						rowArray = DbCompareDao.getJsonArray(elements, keySet);
					}
					if (rowArray != null && rowArray.size() > 0) {
						HashMap<JSONObject, Integer> tabMapIndex = new HashMap<JSONObject, Integer>();
						if (tableDesc.containsKey(tableName)) {
							String tabSqlIndex = this.buildSqlIndex(tableName, rowArray);
							DataSet tabDs = this.getTable(tableName, tabSqlIndex, 0, 0);
							if (tabDs != null) {
								tabMapIndex = DbCompareDao.buildMapIndex(tabDs.getJAData(), keySet);// 根据主键建立数据索引用来判断更新还是插入
								ds = this.writeData(tableName, document, rowArray, tabMapIndex);
							}
						} else {
							ds = this.writeData(tableName, document, rowArray, tabMapIndex);
						}
					}
				}
			} else {
				ds.setState(StateType.FAILT);
				ds.setMessage("读取" + tableName + "导出文件失败，已回滚操作！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
		}
		return ds;
	}

	/**
	 * 获取由关键列组成索引对象数组
	 * 
	 * @param elements
	 *            xml文件中表数据
	 * @param keySet
	 *            主键集合
	 */
	protected static JSONArray getJsonArray(List<?> elements, HashSet<String> keySet) {
		JSONArray ret = new JSONArray();
		try {
			if (elements != null) {
				for (int i = 0; i < elements.size(); i++) {
					Element sqlElement = (Element) elements.get(i);
					if (sqlElement != null && keySet.size() > 0) {
						JSONObject jo = new JSONObject();
						Iterator<String> keyit = keySet.iterator();
						while (keyit.hasNext()) {
							String key = keyit.next();
							String value = sqlElement.elementText(key);
							jo.put(key, value);
						}
						ret.add(jo);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * 根据主键索引对象数组拼装数据库查询条件
	 * 
	 * @param rowArray
	 *            主键索引对象数组
	 */
	protected String buildSqlIndex(String tableName, JSONArray rowArray) {
		String ret = "";
		try {
			StringBuffer fieldStr = new StringBuffer(); // sql语句in查询条件前半部分
			StringBuffer dataStr = new StringBuffer(); // sql语句in查询条件后半部分
			if (rowArray != null && rowArray.size() > 0) {
				// 拼装sql语句in查询条件前半部分
				JSONObject jo = (JSONObject) rowArray.get(0);
				if (jo != null) {
					fieldStr.append("(");
					Iterator<?> keyit = jo.keys();
					while (keyit.hasNext()) {
						String key = (String) keyit.next(); // 不考虑主键为日期情况
						fieldStr.append(key).append(",");
					}
					if (fieldStr.length() > 0) {
						fieldStr.deleteCharAt(fieldStr.length() - 1);
					}
					fieldStr.append(")");
				}
				// 拼装sql语句in查询条件后半部分
				dataStr.append(" in (");
				for (int i = 0; i < rowArray.size(); i++) {
					StringBuffer data = new StringBuffer();
					JSONObject rowObj = (JSONObject) rowArray.get(i);
					Iterator<?> keyit = jo.keys();
					while (keyit.hasNext()) {
						String temp = rowObj.getString((String) keyit.next());
						data.append("'").append(temp).append("'").append(",");
					}
					if (data.length() > 0) {
						dataStr.append("(").append(data.deleteCharAt(data.length() - 1)).append("),");
					}
				}
				dataStr.deleteCharAt(dataStr.length() - 1).append(")");
			}
			ret = fieldStr.append(dataStr).toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * 根据主键和数据拼装数据快速索引
	 * 
	 * @param rowArray
	 *            主键索引对象数组
	 */
	protected static HashMap<JSONObject, Integer> buildMapIndex(JSONArray dataArray, HashSet<String> keySet) {
		HashMap<JSONObject, Integer> tabMapIndex = new HashMap<JSONObject, Integer>();
		try {
			if (dataArray != null) {
				for (int i = 0; i < dataArray.size(); i++) {
					JSONObject rowObj = (JSONObject) dataArray.get(i);
					if (rowObj != null) {
						// 存在关键列按关键列索引，否则按整行索引
						if (keySet.size() > 0) {
							JSONObject keyObj = new JSONObject();
							Iterator<String> keyit = keySet.iterator();
							while (keyit.hasNext()) {
								String key = keyit.next();
								keyObj.put(key, rowObj.get(key));
							}
							tabMapIndex.put(keyObj, i);
						} else {
							tabMapIndex.put(rowObj, i);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tabMapIndex;
	}

	/**
	 * 导入报文数据到数据库
	 * 
	 * @param tableName
	 *            表名
	 * @param document
	 *            xml报文对象
	 * @param rowArray
	 *            由主键列组成json索引对象数组
	 * @param tabMapIndex
	 *            由主键列组成索引对象
	 */
	protected DataSet writeData(String tableName, Document document, JSONArray rowArray,
			HashMap<JSONObject, Integer> tabMapIndex) {
		DataSet ds = new DataSet();
		try {
			StringBuffer sqlbuff = new StringBuffer(); // 示例语句

			if (document != null) {
				for (int i = 0; i < rowArray.size(); i++) {
					JSONObject indexObj = (JSONObject) rowArray.get(i); // 索引对象，用来查询xml数据，以及判断进行更新还是插入操作

					// 根据索引对象查找xml中数据
					StringBuffer tabXmlSearch = new StringBuffer();
					Iterator<?> it = indexObj.keySet().iterator();
					while (it.hasNext()) {
						if (tabXmlSearch.length() > 0) {
							tabXmlSearch.append(" and ");
						}
						String key = (String) it.next();
						tabXmlSearch.append("@").append(key).append("='").append(indexObj.get(key)).append("'");
					}
					List<?> sqlList = document.selectNodes("/root/init/sql[" + tabXmlSearch.toString() + "]");
					if (sqlList.size() > 0) {
						Element sqlElement = (Element) sqlList.get(0);

						HashSet<String> reqFormatSet = this.getReqFormatField(tableName, "xml"); // 需进行日期格式化字段
						List<?> list = sqlElement.elements();
						if (tabMapIndex.containsKey(indexObj)) {
							StringBuffer updatePrev = new StringBuffer();
							StringBuffer updateNext = new StringBuffer();
							for (int k = 0; k < list.size(); k++) {
								Element element = (Element) list.get(k);
								String colname = element.getName();
								String coltext = element.getTextTrim();
								updatePrev.append(colname).append("=");
								if (reqFormatSet.contains(colname)) {
									updatePrev.append(formatStrToDate(coltext));
								} else {
									updatePrev.append("'").append(coltext).append("'");
								}
								updatePrev.append(",");
							}
							Iterator<?> keyit = indexObj.keySet().iterator();
							while (keyit.hasNext()) {
								if (updateNext.length() > 0) {
									updateNext.append(" and ");
								}
								String key = (String) keyit.next();
								updateNext.append(key).append("=").append("'").append(indexObj.getString(key))
										.append("'");
							}
							if (updatePrev.length() > 0) {
								sqlbuff.append("update ").append(tableName).append(" set ")
										.append(updatePrev.deleteCharAt(updatePrev.length() - 1)).append(" where ")
										.append(updateNext).append(";");
							}
						} else {
							StringBuffer insertPrev = new StringBuffer();
							StringBuffer insertNext = new StringBuffer();
							for (int j = 0; j < list.size(); j++) {
								Element element = (Element) list.get(j);
								String colname = element.getName();
								String coltext = element.getTextTrim();
								insertPrev.append(colname).append(",");
								if (reqFormatSet.contains(colname)) {
									insertNext.append(formatStrToDate(coltext));
								} else {
									insertNext.append("'").append(coltext).append("'");
								}
								insertNext.append(",");
							}
							if (insertPrev.length() > 0) {
								sqlbuff.append("insert into ").append(tableName).append("(")
										.append(insertPrev.deleteCharAt(insertPrev.length() - 1)).append(") values (")
										.append(insertNext.deleteCharAt(insertNext.length() - 1)).append(");");
							}
						}
					}
				}
				if (sqlbuff.length() > 0) {
					ds.setData(Tools.stringToBytes(sqlbuff.toString()));
				} else {
					ds.setState(StateType.FAILT);
					ds.setMessage(tableName);
				}
			} else {
				ds.setState(StateType.FAILT);
				ds.setMessage(tableName);
			}
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
		}
		return ds;
	}

	/**
	 * 比较xml与数据库表结构信息
	 * 
	 * @param ret
	 *            引用对象，存放表结构比较信息
	 * @param tableName
	 *            表名
	 * @param diffFlag
	 *            为true存放所有比较结果，为false存在差异比较结果
	 * @param quick
	 *            为true进行快速比对，为false返回详细比较结果
	 */
	protected DataSet compareTwoDefine(final JSONArray ret, String tableName, final boolean diffFlag,
			final boolean quick) {
		final DataSet ds = new DataSet();
		try {
			final HashMap<String, HashMap<String, String>> defineMap = this.getDefine(tableName, "sjk"); // 数据库表结构定义信息
			final HashSet<String> keySet = this.getKeySet(tableName, "sjk"); // 数据库关键列信息
			final HashMap<String, String> colDesc = this.getColumnDesc(tableName).get(tableName); // 字段对应中文名称

			// 先以xml为基础进行比对，再输出数据库特有字段
			Document document = DbCompareDao.readXml(dbMap.get("name"), tableName);
			if (document != null) {
				final List<?> beans = document.selectNodes("/root/bean/item");

				int maxsize = beans.size();
				final RunThreadPool pool = new RunThreadPool(maxsize);
				ArrayList<ArrayList<Integer>> pos = pool.getPos();
				for (int i = 0; i < pos.size(); i++) {
					final ArrayList<Integer> list = pos.get(i);
					pool.add(new Runnable() {
						int start = list.get(0), end = list.get(1);

						@Override
						public void run() {
							for (int m = start; m < end + 1; m++) {

								JSONObject jg = new JSONObject(); // 存放比较结果

								Element element = (Element) beans.get(m);
								String xmlName = element.attributeValue("name"); // 字段名
								String xmlIsKey = element.attributeValue("iskey"); // 是否主键
								String xmlTitle = element.attributeValue("title"); // 字段描述
								String xmlMust = element.attributeValue("must"); // 是否必录
								String xmlType = element.attributeValue("type"); // 字段类型
								String xmlTypeFormat = element.attributeValue("typeFormat"); // 字段格式
								boolean hasdiff = false;

								// 数据库包含该字段，则进行结构比较，否则直接输出
								if (defineMap.containsKey(xmlName)) {
									HashMap<String, String> colMap = defineMap.get(xmlName);
									defineMap.remove(xmlName);
									jg.put("SOURCE", "all");

									// 存放字段名
									compareTwoCols(jg, "NAME", xmlName, xmlName, quick);

									// 比较是否主键
									String sjkIsKey = keySet.contains(xmlName) ? "true" : "false";
									if (compareTwoCols(jg, "ISKEY", xmlIsKey, sjkIsKey, quick)) {
										if (quick) {
											ds.setState(StateType.FAILT);
											pool.stop();
										}
										hasdiff = true;
									}

									// 比较字段中文名称
									String sjkTitle = colDesc.get(xmlName);
									if (compareTwoCols(jg, "TITLE", xmlTitle, sjkTitle, quick)) {
										if (quick) {
											ds.setState(StateType.FAILT);
											pool.stop();
										}
										hasdiff = true;
									}

									// 比较是否必录
									String sjkMust = "1".equals(colMap.get("NULLABLE")) ? "false" : "true";
									if (compareTwoCols(jg, "MUST", xmlMust, sjkMust, quick)) {
										if (quick) {
											ds.setState(StateType.FAILT);
											pool.stop();
										}
										hasdiff = true;
									}

									// 比较字段类型
									String sjkType = dbToXmlMap.get(colMap.get("TYPE_NAME"));
									if (compareTwoCols(jg, "TYPE", xmlType, sjkType, quick)) {
										if (quick) {
											ds.setState(StateType.FAILT);
											pool.stop();
										}
										hasdiff = true;
									}

									// 比较字段类型
									String sjkTypeFormat = colMap.get("COLUMN_SIZE");
									if (compareTwoCols(jg, "TYPEFORMAT", xmlTypeFormat, sjkTypeFormat, quick)) {
										if (quick) {
											ds.setState(StateType.FAILT);
											pool.stop();
										}
										hasdiff = true;
									}
								} else {
									if (quick) {
										ds.setState(StateType.FAILT);
										pool.stop();
									}
									jg.put("SOURCE", "xml");
									compareTwoCols(jg, "NAME", xmlName, null, quick);
									compareTwoCols(jg, "ISKEY", xmlIsKey, null, quick);
									compareTwoCols(jg, "TITLE", xmlTitle, null, quick);
									compareTwoCols(jg, "MUST", xmlMust, null, quick);
									compareTwoCols(jg, "TYPE", xmlType, null, quick);
									compareTwoCols(jg, "TYPEFORMAT", xmlTypeFormat, null, quick);
									hasdiff = true;
								}

								// 非快速比较时分两种情况，一种只展现差异结果，一种展现全部
								if (ret != null) {
									if (diffFlag) {
										if (hasdiff) {
											ret.add(jg);
										}
									} else {
										ret.add(jg);
									}
								}

							}
						}
					});
				}
				pool.start();
				if (quick && ds.getState() == StateType.FAILT) {
					return ds;
				}
			}

			// 返回数据库特有字段
			if (defineMap.size() > 0 && !"{}".equals(defineMap.toString())) {
				if (quick) {
					ds.setState(StateType.FAILT);
					return ds;
				} else {
					Iterator<Entry<String, HashMap<String, String>>> sjkit = defineMap.entrySet().iterator();
					while (sjkit.hasNext()) {
						JSONObject jg = new JSONObject();
						jg.put("SOURCE", "sjk");

						Entry<String, HashMap<String, String>> iterator = sjkit.next(); // 字段名
						String sjkName = iterator.getKey(); // 字段名
						HashMap<String, String> colMap = iterator.getValue(); // 字段结构信息
						String sjkIsKey = keySet.contains(sjkName) ? "true" : "false"; // 是否主键
						String sjkTitle = colDesc.get(sjkName); // 字段中文名称
						String sjkMust = "1".equals(colMap.get("NULLABLE")) ? "false" : "true"; // 是否必录
						String sjkType = dbToXmlMap.get(colMap.get("TYPE_NAME")); // 字段类型
						String sjkTypeFormat = colMap.get("COLUMN_SIZE"); // 字段格式

						compareTwoCols(jg, "NAME", null, sjkName, quick);
						compareTwoCols(jg, "ISKEY", null, sjkIsKey, quick);
						compareTwoCols(jg, "TITLE", null, sjkTitle, quick);
						compareTwoCols(jg, "MUST", null, sjkMust, quick);
						compareTwoCols(jg, "TYPE", null, sjkType, quick);
						compareTwoCols(jg, "TYPEFORMAT", null, sjkTypeFormat, quick);

						if (ret != null) {
							ret.add(jg);
						}
					}
				}
			}

			// 将比较结果写回数据集
			if (ret != null) {
				ds.setData(Tools.stringToBytes(ret.toString())); // 非快速比较，返回结果集
			}
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
		}
		return ds;
	}

	/**
	 * 以xml为基准，对数据进行比较
	 * 
	 * @param ret
	 *            引用对象，存放表结构比较信息
	 * @param tableName
	 *            表名
	 * @param diffFlag
	 *            为true存放所有比较结果，为false存在差异比较结果
	 * @param quick
	 *            为true进行快速比对，为false返回详细比较结果
	 */
	protected DataSet compareTwoData(final JSONArray ret, String tableName, final boolean diffFlag, int start,
			int limit, final boolean quick) {
		final DataSet ds = new DataSet();
		try {
			Document document = DbCompareDao.readXml(dbMap.get("name"), tableName);
			HashMap<String, String> tableDesc = this.getTableDesc();
			// 未导出文件时，以文件为基准进行数据比对，否则直接返回数据库数据
			if (document != null) {
				final List<?> elements = document.selectNodes("/root/init/sql");
				final JSONArray delArray = new JSONArray(); // 非快速比较方式时，用来保存xml已有数据，用来去重

				if (elements.size() > 0) {
					final HashSet<String> keySet = this.getKeySet(tableName, "xml"); // 关键列

					// 展现差异时，以xml为基准进行比对，展现全部时，获取全部数据
					DataSet tabDs = new DataSet();
					if (tableDesc.containsKey(tableName)) {
						if (diffFlag) {
							// 读xml数据，构建表主键
							JSONArray rowArray = new JSONArray();
							for (int i = 0; i < elements.size(); i++) {
								Element xmlElement = (Element) elements.get(i);
								if (xmlElement != null && keySet.size() > 0) {
									JSONObject indexObj = new JSONObject();
									Iterator<String> keyit = keySet.iterator();
									while (keyit.hasNext()) {
										String key = keyit.next();
										String value = xmlElement.elementText(key);
										indexObj.put(key, value);
									}
								}
							}

							// 建立数据索引
							String tabSqlIndex = this.buildSqlIndex(tableName, rowArray);
							tabDs = this.getTable(tableName, tabSqlIndex, 0, 0);
						} else {
							tabDs = this.getTable(tableName, null, 0, 0);
						}
					}

					final JSONArray dataArray = tabDs.getJAData(); // 与tabMapIndex结合，用来快速索引数据
					final HashMap<JSONObject, Integer> tabMapIndex = DbCompareDao.buildMapIndex(dataArray, keySet); // 与dataArray结合，快速索引数据

					// 开启多线程比较
					int maxsize = elements.size();
					final RunThreadPool pool = new RunThreadPool(maxsize);
					ArrayList<ArrayList<Integer>> pos = pool.getPos();
					for (int i = 0; i < pos.size(); i++) {
						final ArrayList<Integer> list = pos.get(i);
						pool.add(new Runnable() {
							int start = list.get(0), end = list.get(1);

							@Override
							public void run() {
								for (int m = start; m < end + 1; m++) {

									JSONObject jg = new JSONObject();

									// 索引数据
									JSONObject indexObj = new JSONObject();
									Element xmlRow = (Element) elements.get(m);
									if (xmlRow != null && keySet.size() > 0) {
										Iterator<String> keyit = keySet.iterator();
										while (keyit.hasNext()) {
											String key = keyit.next();
											String value = xmlRow.elementText(key);
											indexObj.put(key, value);
										}
									}

									// 多线程比较
									if (tabMapIndex.containsKey(indexObj)) {
										jg.put("SOURCE", "all");
										JSONObject sjkRow = (JSONObject) dataArray.get(tabMapIndex.get(indexObj));
										boolean diff = compareTwoRows(jg, xmlRow, sjkRow, quick);
										if (quick && diff) {
											ds.setState(StateType.FAILT);
											pool.stop();
										}
										if (ret != null) {
											if (diffFlag) {
												if (diff) {
													ret.add(jg);
												}
											} else {
												ret.add(jg);
											}
										}
										delArray.add(sjkRow);
									} else {
										if (quick) {
											ds.setState(StateType.FAILT);
											pool.stop();
										}
										jg.put("SOURCE", "xml");
										compareTwoRows(jg, xmlRow, null, quick);
										if (ret != null) {
											ret.add(jg);
										}
									}

								}
							}
						});
					}
					pool.start();
					if (quick && ds.getState() == StateType.FAILT) {
						return ds;
					}

					// 保存数据库特有数据
					if (!diffFlag && dataArray != null) {
						dataArray.removeAll(delArray);
						maxsize = dataArray.size();
						final RunThreadPool diffpool = new RunThreadPool(maxsize);
						ArrayList<ArrayList<Integer>> diffpos = diffpool.getPos();
						for (int i = 0; i < diffpos.size(); i++) {
							final ArrayList<Integer> list = diffpos.get(i);
							diffpool.add(new Runnable() {
								int start = list.get(0), end = list.get(1);

								@Override
								public void run() {
									for (int m = start; m < end + 1; m++) {
										JSONObject jg = new JSONObject();
										jg.put("SOURCE", "sjk");
										JSONObject sjkRow = (JSONObject) dataArray.get(m);
										compareTwoRows(jg, null, sjkRow, quick);
										if (ret != null) {
											ret.add(jg);
										}
									}
								}
							});
						}
						diffpool.start();
					}
				}
			} else {
				if (quick) {
					ds.setState(StateType.SUCCESS);
					return ds;
				}
				if (!diffFlag && tableDesc.containsKey(tableName)) {
					DataSet tabDs = this.getTable(tableName, null, start, limit);
					final JSONArray dataArray = tabDs.getJAData(); // 数组对象用来进行索引

					// 开启多线程比较
					int maxsize = dataArray.size();
					RunThreadPool pool = new RunThreadPool(maxsize);
					ArrayList<ArrayList<Integer>> pos = pool.getPos();
					for (int i = 0; i < pos.size(); i++) {
						final ArrayList<Integer> list = pos.get(i);
						pool.add(new Runnable() {
							int start = list.get(0), end = list.get(1);

							@Override
							public void run() {
								for (int m = start; m < end + 1; m++) {
									JSONObject jg = new JSONObject();
									jg.put("SOURCE", "sjk");
									JSONObject sjkRow = (JSONObject) dataArray.get(m);
									compareTwoRows(jg, null, sjkRow, quick);
									if (ret != null) {
										ret.add(jg);
									}
								}
							}
						});
					}
					pool.start();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
		}
		return ds;
	}

	/**
	 * 对xml文件与数据库相对应行进行比较
	 * 
	 * @param jg
	 *            行两两比较结果
	 * @param xmlrow
	 *            xml行
	 * @param sjkrow
	 *            数据库行
	 * @param quick
	 *            为true进行快速比较，只返回是否一致，为false则返回比较后结果
	 * @return 返回true表示报文与数据库行不一致，返回false则一致
	 */
	protected boolean compareTwoRows(JSONObject jg, Element xmlrow, JSONObject sjkrow, boolean quick) {
		boolean result = false;
		try {
			if (xmlrow != null) {
				List<?> elements = xmlrow.elements();
				if (elements.size() > 0) {
					for (int i = 0; i < elements.size(); i++) {
						Element colElement = (Element) elements.get(i);
						String colname = colElement.getName();
						String xmldata = null, sjkdata = null;
						xmldata = colElement.getText();
						if (sjkrow != null) {
							sjkdata = sjkrow.getString(colname);
						}
						if (this.compareTwoCols(jg, colname, xmldata, sjkdata, quick)) {
							if (quick) {
								return true;
							}
							result = true;
						}
					}
				}
			} else {
				if (quick) {
					return false;
				}
				Iterator<?> sjkit = sjkrow.keySet().iterator();
				while (sjkit.hasNext()) {
					String colname = (String) sjkit.next();
					String sjkdata = sjkrow.getString(colname);
					jg.put(colname + "JG", "1");
					jg.put(colname + "XML", "空");
					jg.put(colname + "SJK", sjkdata);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 对xml文件与数据库相对应字段进行比较
	 * 
	 * @param jg
	 *            字段两两比较结果
	 * @param colname
	 *            字段名
	 * @param xmldata
	 *            xml字段数据
	 * @param sjkdata
	 *            数据库字段数据
	 * @param quick
	 *            为true进行快速比较，只返回是否一致，为false则返回比较后结果
	 * @return boolean 为true表示存在字段差异，为false表示字段一致
	 */
	protected boolean compareTwoCols(JSONObject jg, String colname, String xmldata, String sjkdata, boolean quick) {
		boolean result = false;
		try {
			String xmlstr = StringUtils.isEmpty(xmldata) ? "空" : xmldata.trim();
			String sjkstr = StringUtils.isEmpty(sjkdata) ? "空" : sjkdata.trim();
			if (quick) {
				if (!sjkstr.equals(xmlstr)) {
					return true;
				}
			} else {
				String hasdiff = (sjkstr.equals(xmlstr)) ? "1" : "0";
				jg.put(colname + "JG", hasdiff);
				jg.put(colname + "XML", xmlstr);
				jg.put(colname + "SJK", sjkstr);
				if ("0".equals(hasdiff)) {
					result = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 获取需要进行日期格式化字段
	 * 
	 * @param tableName
	 *            表名
	 * @param type
	 *            为sjk表示获取数据库日期格式化字段，为xml表示获取xml日期格式化字段
	 */
	protected HashSet<String> getReqFormatField(String tableName, String type) {
		HashSet<String> ret = new HashSet<String>();
		try {
			HashMap<String, HashMap<String, String>> defineMap = null;
			if ("sjk".equals(type)) {
				defineMap = this.getDefine(tableName, "sjk");
				if (defineMap.size() > 0) {
					Iterator<Entry<String, HashMap<String, String>>> it = defineMap.entrySet().iterator();
					while (it.hasNext()) {
						Entry<String, HashMap<String, String>> iterator = it.next();
						String colname = iterator.getKey();
						String sjkTypeName = iterator.getValue().get("TYPE_NAME");
						String typeName = dbToXmlMap.get(sjkTypeName);
						if ("date".equals(typeName) || "datetime".equals(typeName)) {
							ret.add(colname);
						}
					}
				}
			} else if ("xml".equals(type)) {
				defineMap = this.getDefine(tableName, "xml");
				if (defineMap.size() > 0) {
					Iterator<Entry<String, HashMap<String, String>>> it = defineMap.entrySet().iterator();
					while (it.hasNext()) {
						Entry<String, HashMap<String, String>> iterator = it.next();
						String colname = iterator.getKey();
						String colvalue = iterator.getValue().get("type");
						if ("date".equals(colvalue) || "datetime".equals(colvalue)) {
							ret.add(colname);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * 导出表数据到报文
	 * 
	 * @param tableName
	 *            表名
	 * @param rowArray
	 *            由主键列组成索引对象数组
	 */
	protected DataSet writeDataXml(String tableName, JSONArray rowArray) {
		DataSet ds = new DataSet();
		try {
			boolean success = false;
			if (rowArray != null && rowArray.size() > 0) {
				HashSet<String> keySet = this.getKeySet(tableName, "sjk"); // 数据库主键
				Document document = DbCompareDao.readXml(dbMap.get("name"), tableName);
				if (document != null) {
					Element root = document.getRootElement();
					Element init = root.addElement("init");
					String tabSqlIndex = this.buildSqlIndex(tableName, rowArray);
					DataSet tabDs = this.getTable(tableName, tabSqlIndex, 0, 0);
					if (tabDs != null) {
						JSONArray dataArray = tabDs.getJAData();
						for (int i = 0; i < dataArray.size(); i++) {
							JSONObject rowObj = (JSONObject) dataArray.get(i);
							if (rowObj != null) {
								Element sql = init.addElement("sql");
								Iterator<?> colit = rowObj.keys();
								while (colit.hasNext()) {
									String colname = (String) colit.next();
									String colvalue = (String) rowObj.get(colname);
									if (keySet.contains(colname)) {
										sql.addAttribute(colname, colvalue);
									}
									Element element = sql.addElement(colname);
									element.setText(colvalue);
								}
							}
						}
					}
				}
				success = DbCompareDao.writeXml(dbMap.get("name"), tableName, document);
				if (!success) {
					ds.setState(StateType.FAILT);
					ds.setMessage(tableName);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage(tableName);
		}
		return ds;
	}

	/**
	 * 获取所有表名
	 * 
	 * @type 为sjk表示获取数据库中所有表名，为xml表示获取xml中所有表名
	 * @return HashSet<String> 所有表名集合
	 */
	public HashSet<String> getTableName(String type) {
		HashSet<String> tableNameSet = new HashSet<String>();
		HashMap<String, String> tableDesc = this.getTableDesc();
		if ("sjk".equals(type)) {
			if (tableDesc != null) {
				Iterator<String> it = tableDesc.keySet().iterator();
				while (it.hasNext()) {
					String tableName = it.next().toUpperCase();
					tableNameSet.add(tableName);
				}
			}
		} else if ("xml".equals(type)) {
			String dirPath = PathUtil.getWebPath() + "public/db/" + dbMap.get("name");
			File dir = new File(dirPath);
			if (dir.isDirectory()) {
				File[] files = dir.listFiles();
				for (int i = 0; i < files.length; i++) {
					String fileName = files[i].getName();
					if (fileName.indexOf(".") != -1) {
						String tableName = fileName.substring(0, fileName.lastIndexOf("."));
						tableNameSet.add(tableName);
					}
				}
			}
		}
		return tableNameSet;
	}

	/**
	 * 获取表主键信息
	 * 
	 * @param tableName
	 *            表名
	 * @param 为sjk表示获取数据库表主键信息
	 *            ，为xml表示获取xml表主键信息
	 * @return HashSet<String> 表主键集合
	 */
	protected HashSet<String> getKeySet(String tableName, String type) {
		HashSet<String> keySet = new HashSet<String>();
		if ("sjk".equals(type)) {
			Connection dconn = null;
			try {
				dconn = DBSource.getConnection(dbMap.get("dsName"));
				DatabaseMetaData dbmd = dconn.getMetaData();
				ResultSet keyrs = dbmd.getPrimaryKeys(null, dbMap.get("schem"), tableName);
				while (keyrs.next()) {
					keySet.add(keyrs.getString("COLUMN_NAME").toUpperCase());
				}
				keyrs.close();
			} catch (SQLException se) {
				se.printStackTrace();
			} finally {
				DbCompareTool.closeConn(dconn);
			}
		} else if ("xml".equals(type)) {
			Document document = DbCompareDao.readXml(dbMap.get("name"), tableName);
			if (document != null) {
				List<?> items = document.selectNodes("/root/bean/item[@iskey='true']");
				if (items.size() == 0) {
					items = document.selectNodes("/root/bean/item"); // 未设置主键时以所有字段作为索引主键
				}
				for (int i = 0; i < items.size(); i++) {
					Element element = (Element) items.get(i);
					if (element != null) {
						keySet.add(element.attributeValue("name"));
					}
				}
			}
		}
		return keySet;
	}

	/**
	 * 获取表结构信息
	 * 
	 * @param tableName
	 *            表名
	 * @param type
	 *            为sjk表示获取数据库表结构信息，为xml表示获取xml表结构信息
	 */
	protected HashMap<String, HashMap<String, String>> getDefine(String tableName, String type) {
		HashMap<String, HashMap<String, String>> defineMap = new HashMap<String, HashMap<String, String>>();
		if ("sjk".equals(type)) {
			Connection dconn = null;
			try {
				dconn = DBSource.getConnection(dbMap.get("dsName"));
				DatabaseMetaData dbmd = dconn.getMetaData();
				ResultSet rs = dbmd.getColumns(null, dbMap.get("schema"), tableName, null);
				while (rs.next()) {
					String columnName = rs.getString("COLUMN_NAME").toUpperCase();
					HashMap<String, String> fieldMap = new HashMap<String, String>(); // 字段对应字段结构信息
					ResultSetMetaData rsmd = rs.getMetaData();
					for (int i = 1; i <= rsmd.getColumnCount(); i++) {
						String key = rsmd.getColumnName(i).toUpperCase();
						String value = rs.getString(i);
						fieldMap.put(key, value);
					}
					defineMap.put(columnName, fieldMap);
				}
				rs.close();
			} catch (SQLException se) {
				se.printStackTrace();
			} finally {
				DbCompareTool.closeConn(dconn);
			}
		} else if ("xml".equals(type)) {
			Document document = DbCompareDao.readXml(dbMap.get("name"), tableName);
			if (document != null) {
				List<?> items = document.selectNodes("/root/bean/item");
				if (items.size() > 0) {
					for (int i = 0; i < items.size(); i++) {
						Element element = (Element) items.get(i);
						String itemname = element.attributeValue("name");
						List<?> attributes = element.attributes();
						HashMap<String, String> fieldMap = new HashMap<String, String>();
						if (attributes.size() > 0) {
							for (int j = 0; j < attributes.size(); j++) {
								Attribute attribute = (Attribute) attributes.get(j);
								fieldMap.put(attribute.getName(), attribute.getValue());
							}
						}
						defineMap.put(itemname, fieldMap);
					}
				}
			}
		}
		return defineMap;
	}

	/**
	 * 查询获取表数据
	 * 
	 * @param tableName
	 *            表名
	 * @param tabSqlIndex
	 *            附加查询条件
	 * @param start
	 *            起始位置
	 * @param limit
	 *            分页大小
	 */
	protected DataSet getTable(String tableName, String tabSqlIndex, int start, int limit) {
		DataSet ds = new DataSet();
		Connection dconn = null;
		try {
			StringBuffer sqlbuff = new StringBuffer();
			sqlbuff.append("select * from ").append(tableName);
			if (StringUtils.isNotEmpty(tabSqlIndex)) {
				sqlbuff.append(" where ").append(tabSqlIndex);
			}
			String sql = sqlbuff.toString();
			dconn = DBSource.getConnection(dbMap.get("dsName"));
			if (start > -1 && limit > -1) {
				ds = DbHelper.query(sql, start, limit, null, dconn);
			} else {
				ds = DbHelper.query(sql, null, dconn);
			}
			// 对日期字段进行格式化
			HashSet<String> reqFormatField = this.getReqFormatField(tableName, "sjk");
			if (ds != null) {
				JSONArray array = ds.getJAData();
				if (array != null) {
					for (int i = 0; i < array.size(); i++) {
						JSONObject jo = (JSONObject) array.get(i);
						if (jo != null) {
							Iterator<String> fit = reqFormatField.iterator();
							while (fit.hasNext()) {
								String key = fit.next();
								Object obj = jo.get(key);
								if (obj != null && obj instanceof JSONObject) {
									JSONObject datejo = (JSONObject) obj;
									String date = Tools.formatDate(new Date(datejo.getLong("time")),
											CommonUtils.YYYY_MM_DD_HH_mm_SS);
									jo.put(key, date);
								}
							}
						}
					}
					ds.setData(Tools.stringToBytes(array.toString()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbCompareTool.closeConn(dconn);
		}
		return ds;
	}

	/**
	 * 读取xml报文
	 * 
	 * @param dirName
	 *            目录名（对应数据源名称）
	 * @param tableName
	 *            数据表
	 * @return Document 返回Document对象
	 */
	protected static Document readXml(String dirName, String tableName) {
		String filePath = PathUtil.getWebPath() + "public/db/" + dirName + "/" + tableName + ".xml";
		return DbCompareTool.readDoc(filePath);
	}

	/**
	 * 导出xml报文
	 * 
	 * @param dirName
	 *            目录名（对应数据源名称）
	 * @param tableName
	 *            数据表
	 * @param boolean
	 *            true表示写入成功，false表示写入失败
	 */
	protected static boolean writeXml(String dirName, String tableName, Document document) {
		String dirPath = PathUtil.getWebPath() + "public/db/" + dirName;
		File dir = new File(dirPath);
		if (!dir.exists()) {
			dir.mkdirs(); // 如果目录不存在，则建立相应目录
		}
		String filePath = dirPath + "/" + tableName + ".xml";
		return DbCompareTool.writeDoc(filePath, document);
	}

	/**
	 * 内部类，用来执行多线程
	 * 
	 * @author AnthonyKing @
	 */
	protected static class RunThreadPool {
		private ThreadPoolExecutor threadPool; // 线程池
		private ArrayList<ArrayList<Integer>> pos = new ArrayList<ArrayList<Integer>>(); // json对象记录线程起始和终止坐标，根据ArrayList生成线程

		// 初始化时获取分线程数据下标
		public RunThreadPool(int maxsize) {
			int maxpool = 0;
			if (maxsize < 50) {
				maxpool = 10;
			} else if (maxsize >= 50 && maxsize < 100) {
				maxpool = 20;
			} else if (maxsize >= 100 && maxsize < 500) {
				maxpool = 30;
			} else {
				maxpool = 50;
			}
			// 初始化线程池
			threadPool = new ThreadPoolExecutor(maxpool * 2, maxpool * 4, 180, TimeUnit.SECONDS,
					new LinkedBlockingQueue<Runnable>(maxpool * 2));
			int persize = (maxsize % maxpool == 0) ? (maxsize / maxpool) : (maxsize / maxpool + 1);
			int perstart = 0, perend = 0; // 待处理数据下标位置
			for (int i = 0; i < maxpool; i++) {
				if (maxsize - perend - 1 < 0) {
					break;
				} else {
					ArrayList<Integer> list = new ArrayList<Integer>();
					perend = (perstart + persize) > maxsize ? (maxsize - 1) : (perstart + persize - 1);
					list.add(perstart);
					list.add(perend);
					perstart = perend + 1;
					pos.add(list);
				}
			}
		}

		// 返回分线程数据下标
		public ArrayList<ArrayList<Integer>> getPos() {
			return pos;
		}

		// 添加线程
		public void add(Runnable runnable) {
			if (!threadPool.isShutdown()) {
				threadPool.execute(runnable);
			}
		}

		// 主线程阻塞，不允许添加新线程，立即执行已添加子线程
		public void start() {
			threadPool.shutdown();
			while (threadPool.getPoolSize() != 0) {
				continue;
			}
		}

		// 立即终止所有线程
		public void stop() {
			threadPool.shutdownNow();
		}
	}
}