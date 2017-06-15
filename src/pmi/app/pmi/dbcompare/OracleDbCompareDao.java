package app.pmi.dbcompare;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.inspur.StateType;
import com.inspur.bean.DataSet;
import com.inspur.util.Tools;
import com.inspur.util.db.DBSource;
import com.inspur.util.db.DbHelper;

public class OracleDbCompareDao extends DbCompareDao {
	private static HashSet<String> format = new HashSet<String>(); // 创建或更新表时不需要typeFormat

	static {
		format.add("date");
		format.add("datetime");
		format.add("clob");
	}

	public OracleDbCompareDao(HashMap<String, String> dbMap) {
		super(dbMap);
	}

	@Override
	protected HashMap<String, String> getTableDesc() {
		HashMap<String, String> ret = new HashMap<String, String>();
		Connection conn = null;
		try {
			conn = DBSource.getConnection(dbMap.get("dsName"));
			String sql = "select table_name,comments from user_tab_comments where table_name not like 'BIN$%'";
			DataSet ds = DbHelper.query(sql, null, conn);
			if (ds != null) {
				JSONArray dsArray = ds.getJAData();
				for (int j = 0; j < dsArray.size(); j++) {
					JSONObject jo = (JSONObject) dsArray.get(j);
					if (jo != null) {
						ret.put(jo.getString("TABLE_NAME").toUpperCase(), jo.getString("COMMENTS"));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException se) {
					se.printStackTrace();
				}
			}
		}
		return ret;
	}

	@Override
	protected HashMap<String, HashMap<String, String>> getColumnDesc(String tableName) {
		HashMap<String, HashMap<String, String>> ret = new HashMap<String, HashMap<String, String>>();
		Connection conn = null;
		try {
			conn = DBSource.getConnection(dbMap.get("dsName"));
			String sql = "select table_name,column_name,comments from user_col_comments where table_name not like 'BIN$%'";
			if(StringUtils.isNotEmpty(tableName)){
				sql+=" and table_name='"+tableName+"'";
			}
			DataSet ds = DbHelper.query(sql, null, conn);
			if (ds != null) {
				JSONArray dsArray = ds.getJAData();
				for (int j = 0; j < dsArray.size(); j++) {
					JSONObject jo = (JSONObject) dsArray.get(j);
					String table_name = jo.getString("TABLE_NAME").toUpperCase();
					if (ret.containsKey(table_name)) {
						HashMap<String, String> columnMap = ret.get(table_name);
						columnMap.put(jo.getString("COLUMN_NAME").toUpperCase(), jo.getString("COMMENTS"));
					} else {
						HashMap<String, String> columnMap = new HashMap<String, String>();
						columnMap.put(jo.getString("COLUMN_NAME").toUpperCase(), jo.getString("COMMENTS"));
						ret.put(jo.getString("TABLE_NAME").toUpperCase(), columnMap);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException se) {
					se.printStackTrace();
				}
			}
		}
		return ret;
	}

	@Override
	protected DataSet createTable(String tableName) {
		DataSet ds = new DataSet();
		try {
			Document document = DbCompareDao.readXml(dbMap.get("name"), tableName);
			if (document != null) {
				Element root = (Element) document.selectSingleNode("/root");
				List<?> itemList = document.selectNodes("/root/bean/item");

				StringBuffer sqlBuffer = new StringBuffer(); // 创建表语句中字段部分
				StringBuffer keyBuffer = new StringBuffer(); // 创建表语句中创建主键部分
				StringBuffer descBuffer = new StringBuffer();// 表及表中字段增加描述信息

				sqlBuffer.append("create table ").append(tableName).append("(");
				String desc = root.attributeValue("desc"); // 表名描述信息
				if (StringUtils.isNotEmpty(desc)) {
					descBuffer.append("comment on table ").append(tableName).append(" is '").append(desc).append("';");
				}

				for (int i = 0; i < itemList.size(); i++) {
					Element itemElement = (Element) itemList.get(i);
					String name = itemElement.attributeValue("name");
					String iskey = itemElement.attributeValue("iskey");
					String title = itemElement.attributeValue("title");
					String must = itemElement.attributeValue("must");
					String type = itemElement.attributeValue("type");
					String typeFormat = itemElement.attributeValue("typeFormat");

					if ("true".equals(iskey)) {
						keyBuffer.append(name).append(",");
					}
					if (format.contains(type)) {
						sqlBuffer.append(name).append(" ").append(xmlToDbMap.get(type));
					} else {
						sqlBuffer.append(name).append(" ").append(xmlToDbMap.get(type)).append("(").append(typeFormat).append(")");
					}
					if ("true".equals(must)) {
						sqlBuffer.append(" not null,");
					} else {
						sqlBuffer.append(",");
					}
					if (StringUtils.isNotEmpty(title)) {
						descBuffer.append("comment on column ").append(tableName).append(".").append(name).append(" is '").append(title).append("';");
					}
				}
				if (keyBuffer.length() > 0) {
					sqlBuffer.append("constraint ").append(tableName).append("_PK primary key(").append(keyBuffer.deleteCharAt(keyBuffer.length() - 1)).append("));");
				} else {
					sqlBuffer.deleteCharAt(sqlBuffer.length() - 1).append(");");
				}
				if (descBuffer.length() > 0) {
					sqlBuffer.append(descBuffer);
				}
				ds.setData(Tools.stringToBytes(sqlBuffer.toString()));
			} else {
				ds.setState(StateType.FAILT);
				ds.setMessage("读取" + tableName + "导出文件失败！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
		}
		return ds;
	}

	@Override
	protected DataSet modifyTable(String tableName) {
		DataSet ds = new DataSet();
		try {
			StringBuffer sqlBuffer = new StringBuffer(); // 创建表语句中字段部分
			StringBuffer insertBuffer = new StringBuffer(); // 添加字段语句
			StringBuffer updateBuffer = new StringBuffer(); // 修改字段语句
			StringBuffer descBuffer = new StringBuffer();// 表及表中字段增加描述信息

			HashMap<String, HashMap<String, String>> columnXmlMap = this.getDefine(tableName, "xml");
			HashMap<String, HashMap<String, String>> columnSjkMap = this.getDefine(tableName, "sjk");

			Iterator<Entry<String, HashMap<String, String>>> xmlit = columnXmlMap.entrySet().iterator();
			while (xmlit.hasNext()) {
				Entry<String, HashMap<String, String>> iterator = xmlit.next();
				String name = iterator.getKey();
				HashMap<String, String> item = iterator.getValue();
				String title = item.get("title");
				String must = item.get("must");
				String type = item.get("type");
				String typeFormat = item.get("typeFormat");

				if (columnSjkMap.containsKey(name)) {
					if (format.contains(type)) {
						updateBuffer.append(name).append(" ").append(xmlToDbMap.get(type));
					} else {
						updateBuffer.append(name).append(" ").append(xmlToDbMap.get(type)).append("(").append(typeFormat).append(")");
					}

					if ("true".equals(must)) {
						HashMap<String, String> colMap = columnSjkMap.get(name);
						String sjkMust = "1".equals(colMap.get("NULLABLE")) ? "false" : "true";
						if (!"true".equals(sjkMust)) {
							updateBuffer.append(" not null");
						}
					}
					updateBuffer.append(",");
				} else {
					if (format.contains(type)) {
						insertBuffer.append(name).append(" ").append(xmlToDbMap.get(type));
					} else {
						insertBuffer.append(name).append(" ").append(xmlToDbMap.get(type)).append("(").append(typeFormat).append(")");
					}
					if ("true".equals(must)) {
						insertBuffer.append(" not null");
					}
					insertBuffer.append(",");
				}
				if (StringUtils.isNotEmpty(title)) {
					descBuffer.append("comment on column ").append(tableName).append(".").append(name).append(" is '").append(title).append("';");
				}
			}
			if (insertBuffer.length() > 0) {
				sqlBuffer.append("alter table ").append(tableName).append(" add(").append(insertBuffer.deleteCharAt(insertBuffer.length() - 1)).append(");");
			}
			if (updateBuffer.length() > 0) {
				sqlBuffer.append("alter table ").append(tableName).append(" modify(").append(updateBuffer.deleteCharAt(updateBuffer.length() - 1)).append(");");
			}
			if (descBuffer.length() > 0) {
				sqlBuffer.append(descBuffer);
			}
			ds.setData(Tools.stringToBytes(sqlBuffer.toString()));
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
		}
		return ds;
	}

	@Override
	protected String formatStrToDate(String str) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("to_date('").append(str).append("','yyyy-MM-dd hh24:mi:ss')");
		String ret = buffer.toString();
		return ret;
	}
}
