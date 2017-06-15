package app.pmi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Properties;

import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;

import com.icore.http.util.HttpUtil;
import com.icore.util.DaoFactory;
import com.icore.util.conf.DataSourceConfig;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.PathUtil;
import com.inspur.util.SecurityConfig;
import com.inspur.util.Tools;

public class BspDao extends BaseJdbcDao {
	protected BspDao() {
		this.setDataSourceName("icityDataSource");
	}

	public static BspDao getInstance() {
		return DaoFactory.getDao(BspDao.class.getName());
	}
	public String getSecurityItem(String item){		
		String filePath =  HttpUtil.formatUrl(PathUtil.getWebPath()+"/conf/security.properties") ;		
		String value="";
		FileInputStream fis = null;
		InputStreamReader isr = null;
		try {
			Properties registerProperties = new Properties();
			File f = new File(filePath);
			if (f.isFile()) {
				try {
					fis = new FileInputStream(f);
					isr = new InputStreamReader(fis,"UTF-8");
					registerProperties.load(isr);
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					if(isr!=null){
						isr.close();
					}
					if(fis!=null){
						fis.close();
					}
				}
			}
			value = registerProperties.getProperty(item);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return value;
	}


	public DataSet getKeyValueArrays(ParameterSet pset) {
		DataSet ds = new DataSet();
		JSONArray array = (JSONArray) pset.get("param");
		net.sf.json.JSONObject data = new net.sf.json.JSONObject();
		for (int i = 0, n = array.size(); i < n; i++) {
			ParameterSet p = new ParameterSet();
			net.sf.json.JSONObject json = array.getJSONObject(i);
			p.setParameter("serviceName", json.getString("serviceName"));
			p.setParameter("dataType", json.getString("dataType"));
			p.setParameter("ids", json.getString("ids"));
			data.put(json.getString("field"), this.getValues(p).getJAData());
		}
		ds.setData(Tools.jsonToBytes(data));
		return ds;
	}

	public DataSet getValues(ParameterSet pSet) {
		String serviceName = (String) pSet.getParameter("serviceName");
		String dataType = (String) pSet.getParameter("dataType");
		String ids = (String) pSet.getParameter("ids");
		Map map = SecurityConfig.getMap("CodeMap");
		String sql = (String) map.get(serviceName);
		DataSet ds;
		if (ids != null && ids.length() > 0) {
			String condition = (String) map.get(serviceName + "_condition");
			if (condition != null) {
				sql += condition;
				String[] arrIds = ids.split(",");
				StringBuffer stringbuffer = new StringBuffer("'");
				int i = arrIds.length;
				for (int j = 0; j < i; j++) {
					stringbuffer.append(arrIds[j]);
					if (j < i - 1)
						stringbuffer.append("','");
				}
				stringbuffer.append("'");
				sql = sql.replace("☆", stringbuffer);
			}
		}
		if (map.get(serviceName + "_other") != null && !"".equals(dataType)) {
			sql += (String) map.get(serviceName + "_other");
		}
		if ("".equals(dataType))
			ds = executeDataset(sql, null);
		else
			ds = executeDataset(sql, new Object[] { dataType });

		return ds;
	}

	/**
	 * 获取数据库
	 * 
	 * @param datasSource
	 * @return
	 */
	public String getDbName(String datasSource) {
		String dbName = "";
		  dbName = DataSourceConfig.getString("jdbc." + datasSource + ".name");
		  if (StringUtils.isEmpty(dbName)) {
			  dbName = DataSourceConfig.getString("jdbc." + datasSource + ".user");
		  }
		return dbName;
	}
}
