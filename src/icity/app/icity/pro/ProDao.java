package app.icity.pro;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import app.util.RestUtil;

import com.icore.http.util.HttpUtil;
import com.icore.util.DaoFactory;
import com.icore.util.SecurityConfig;
import com.icore.util.StringUtil;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.Tools;
import com.inspur.util.db.DbHelper;
import com.inspur.util.db.SqlCreator;

import core.util.CommonUtils;
import core.util.HttpClientUtil;

/**
 * 企业设立 lhy
 * 
 * @author lenovo
 * 
 */
public class ProDao extends BaseJdbcDao {

	private static Log _log = LogFactory.getLog(ProDao.class);

	private ProDao() {
		this.setDataSourceName("icityDataSource");
	}

	public static ProDao getInstance() {
		return DaoFactory.getDao(ProDao.class.getName());
	}

	/**
	 * 获取字典信息
	 * 
	 * @return
	 */
	public DataSet getDictInfo(ParameterSet pSet) {
		/*
		 * DataSet ds = new DataSet(); String d =
		 * "{\"state\":\"200\",\"error\":\"\",\"info\":[{\"TZSPXJBM\":[{\"name\":\"普陀区\",\"code\":\"310107\"}],\"TZXMHYFL\":[{\"name\":\"农业水利\",\"code\":\"A00001\"},{\"name\":\"农业\",\"code\":\"A0000101\"},{\"name\":\"水库\",\"code\":\"A0000102\"},{\"name\":\"其他水事工程\",\"code\":\"A0000103\"},{\"name\":\"能源\",\"code\":\"A00002\"},{\"name\":\"水电站\",\"code\":\"A0000201\"},{\"name\":\"抽水蓄能电站\",\"code\":\"A0000202\"},{\"name\":\"火电站\",\"code\":\"A0000203\"},{\"name\":\"热电站\",\"code\":\"A0000204\"},{\"name\":\"风电站\",\"code\":\"A0000205\"},{\"name\":\"核电站\",\"code\":\"A0000206\"},{\"name\":\"电网工程\",\"code\":\"A0000207\"},{\"name\":\"煤矿\",\"code\":\"A0000208\"},{\"name\":\"煤制燃料\",\"code\":\"A0000209\"},{\"name\":\"液化石油气接收、存储设施（不含油气田、炼油厂的配套项目）\",\"code\":\"A0000210\"},{\"name\":\"进口液化天然气接收、存储设施\",\"code\":\"A0000211\"},{\"name\":\"输油管网\",\"code\":\"A0000212\"},{\"name\":\"输气管网\",\"code\":\"A0000213\"},{\"name\":\"炼油\",\"code\":\"A0000214\"},{\"name\":\"变性燃料乙醇\",\"code\":\"A0000215\"},{\"name\":\"交通运输\",\"code\":\"A00003\"},{\"name\":\"新建铁路\",\"code\":\"A0000301\"},{\"name\":\"公路\",\"code\":\"A0000302\"},{\"name\":\"独立公（铁）路桥、隧道\",\"code\":\"A0000303\"},{\"name\":\"煤炭、矿石、油气专用泊位\",\"code\":\"A0000304\"},{\"name\":\"集装箱专用码头\",\"code\":\"A0000305\"},{\"name\":\"内河航运\",\"code\":\"A0000306\"},{\"name\":\"民航\",\"code\":\"A0000307\"},{\"name\":\"信息产业\",\"code\":\"A00004\"},{\"name\":\"电信\",\"code\":\"A0000401\"},{\"name\":\"原材料\",\"code\":\"A00005\"},{\"name\":\"稀土、铁矿、有色矿山开发\",\"code\":\"A0000501\"},{\"name\":\"石化\",\"code\":\"A0000502\"},{\"name\":\"化工\",\"code\":\"A0000503\"},{\"name\":\"稀土\",\"code\":\"A0000504\"},{\"name\":\"黄金\",\"code\":\"A0000505\"},{\"name\":\"机械制造\",\"code\":\"A00006\"},{\"name\":\"汽车\",\"code\":\"A0000601\"},{\"name\":\"轻工\",\"code\":\"A00007\"},{\"name\":\"烟草\",\"code\":\"A0000701\"},{\"name\":\"高新技术\",\"code\":\"A00008\"},{\"name\":\"民用航空航天\",\"code\":\"A0000801\"},{\"name\":\"城建\",\"code\":\"A00009\"},{\"name\":\"城市快速轨道交通项目\",\"code\":\"A0000901\"},{\"name\":\"城市道路桥梁、隧道\",\"code\":\"A0000902\"},{\"name\":\"其他城建项目\",\"code\":\"A0000903\"},{\"name\":\"社会事业\",\"code\":\"A00010\"},{\"name\":\"主题公园\",\"code\":\"A0001001\"},{\"name\":\"旅游\",\"code\":\"A0001002\"},{\"name\":\"其他社会事业项目\",\"code\":\"A0001003\"},{\"name\":\"外商投资\",\"code\":\"A00011\"},{\"name\":\"外商投资民航业项目\",\"code\":\"A000112\"},{\"name\":\"境外投资\",\"code\":\"A00012\"}],\"ZSTZXMFRZZLX\":[{\"name\":\"企业营业执照\",\"code\":\"0\"},{\"name\":\"企业法人\",\"code\":\"1\"},{\"name\":\"国家机关法人\",\"code\":\"7\"},{\"name\":\"事业单位法人\",\"code\":\"3\"},{\"name\":\"社会团体法人\",\"code\":\"5\"},{\"name\":\"其他\",\"code\":\"9\"}],\"TZSPSBM\":[{\"name\":\"舟山市\",\"code\":\"330900\"}],\"TZSPJSXZ\":[{\"name\":\"新建\",\"code\":\"0\"},{\"name\":\"扩建\",\"code\":\"1\"},{\"name\":\"改建\",\"code\":\"3\"},{\"name\":\"迁建\",\"code\":\"2\"}],\"TZSPXMLX\":[{\"name\":\"审批类项目\",\"code\":\"A00001\"},{\"name\":\"新建\",\"code\":\"0\"},{\"name\":\"核准类项目\",\"code\":\"A00002\"},{\"name\":\"备案类项目\",\"code\":\"A00003\"},{\"name\":\"敏感涉密项目\",\"code\":\"A00004\"}],\"TZSPSJBM\":[{\"name\":\"浙江省\",\"code\":\"330000\"}],\"TZSPSSJD\":[{\"name\":\"审批阶段\",\"code\":\"0\"},{\"name\":\"开工阶段\",\"code\":\"1\"},{\"name\":\"竣工验收\",\"code\":\"3\"}],\"TZXMLX\":[{\"name\":\"审批\",\"code\":\"A00001\"},{\"name\":\"核准\",\"code\":\"A00002\"},{\"name\":\"备案\",\"code\":\"A00003\"}]}]}"
		 * ; JSONObject dss = JSONObject.fromObject(d); ds.setRawData(dss);
		 * return ds;
		 */
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("proUrl")
				+ "/web/investProject/getDictInfo");
		String dictCode = (String) pSet.getParameter("dictCode");
		url += "?dictCode=" + dictCode;
		try {
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.getData(url));
			if ("200".equals(obj.getString("state"))) {
				ds.setRawData(obj);
				ds.setState(StateType.SUCCESS);
			} else {
				ds.setState(StateType.FAILT);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			_log.error("获取字典出错！");
		}
		return ds;
	}

	/**
	 * 获取事项信息
	 * 
	 * @return
	 */
	public DataSet getRule(ParameterSet pSet) {
		/*
		 * DataSet ds = new DataSet(); String d =
		 * "{\"state\":\"200\",\"error\":\"\",\"info\":[{\"itemList\":[],\"stage\":\"1\"},{\"itemList\":[{\"investType\":\"A00002\",\"regionCode\":\"370100000000\",\"limiteTime\":\"3\",\"itemName\":\"企业设立税务增收\",\"orgCode\":\"370100004\",\"regionName\":\"济南市\",\"orgName\":\"济南市国税局\",\"itemId\":\"505B084445E049F4AF5B316D8879B65E\",\"stage\":\"0\",\"itemCode\":\"370100-004-XK-002-03\"}],\"stage\":\"0\"}]}"
		 * ; JSONObject ddds = JSONObject.fromObject(d); ds.setRawData(ddds);
		 * return ds;
		 */
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("proUrl")
				+ "/web/investProject/getRule");
		String investType = (String) pSet.getParameter("investType");
		// String secondInvestType = (String)
		// pSet.getParameter("secondInvestType");
		String regionCode = (String) pSet.getParameter("regionCode");
		url += "?investType=" + investType + "&regionCode=" + regionCode;
		// GetMethod postMethod = new
		// GetMethod(url+"?investType="+investType+"&secondInvestType="+secondInvestType+"&regionCode="+regionCode);
		try {
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.getData(url));
			if ("200".equals(obj.getString("state"))) {
				ds.setRawData(obj);
				ds.setState(StateType.SUCCESS);
			} else {
				ds.setState(StateType.FAILT);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			_log.error("获取事项列表出错！");
		}
		return ds;
	}

	/**
	 * 提交项目信息
	 * 
	 * @return
	 */
	public DataSet submitProjectMessage(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("proUrl")
				+ "/web/investProject/submitProjectMessage");
		Map<String, String> data = new HashMap<String, String>();
		data.put("project", pSet.getParameter("project").toString());
		data.put("dcCompany", pSet.getParameter("dcCompany").toString());
		data.put("contectInformation", pSet.getParameter("contectInformation")
				.toString());
		try {
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.postData(url, data));
			if ("200".equals(obj.getString("state"))) {
				ds.setRawData(obj);
				ds.setState(StateType.SUCCESS);
			} else {
				ds.setState(StateType.FAILT);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			_log.error("注册失败！");
		}
		return ds;
	}

	/**
	 * 业务申请
	 * 
	 * @return
	 */
	public DataSet applyBusiness(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("proUrl")
				+ "/web/investProject/applyBusiness");
		Map<String, String> data = new HashMap<String, String>();
		data.put("investType", (String) pSet.getParameter("investType"));
		data.put("projectCode", (String) pSet.getParameter("projectCode"));
		data.put("itemList", pSet.getParameter("itemList").toString());
		try {
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.postData(url, data));
			if ("200".equals(obj.getString("state"))) {
				ds.setRawData(obj);
				ds.setState(StateType.SUCCESS);
			} else {
				ds.setState(StateType.FAILT);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			_log.error("提交失败！");
		}
		return ds;
	}

	/**
	 * 进度查询
	 * 
	 * @return
	 */
	public DataSet queryBusiness(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("proUrl")
				+ "/web/investProject/queryBusiness");
		String projectCode = (String) pSet.getParameter("projectCode");
		url += "?projectCode=" + projectCode;
		try {
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.getData(url));
			if ("200".equals(obj.getString("state"))) {
				ds.setRawData(obj);
				ds.setState(StateType.SUCCESS);
			} else {
				ds.setState(StateType.FAILT);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			_log.error("获取进度出错！");
		}
		return ds;
	}

	/**
	 * 获取事项基本信息
	 * 
	 * @return
	 */
	public DataSet getAllItemInfo(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig
				.getString("ItemSynchronizationUrl")
				+ "/getAllItemInfoByItemID");
		String itemId = (String) pSet.getParameter("itemId");
		try {
			url += "?itemId=" + itemId;
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.getData(url));
			JSONArray materials = obj.getJSONArray("material");
			// 过滤，只展示申报提交材料
			for (int i = 0; i < materials.size(); i++) {
				if (!materials.getJSONObject(i).get("BUSINESS_TYPE")
						.equals("Apply")) {
					materials.remove(i);
					i--;
				}
			}
			JSONObject jo = new JSONObject();
			jo.put("material", materials);
			ds.setData(Tools.jsonToBytes(jo));
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			_log.error("获取字典出错！");
		}
		return ds;
	}

	/**
	 * 获取办件公示
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet showBusiness(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("proUrl")
				+ "/web/investProject/showBusiness");
		Map<String, String> data = new HashMap<String, String>();
		String page = (String) pSet.get("page");
		String limit = (String) pSet.get("limit");
		data.put("page", page);
		data.put("limit", limit);
		data.put("regionCode", SecurityConfig.getString("WebRegion"));
		try {
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.postData(url, data));
			// if("200".equals(obj.getString("state"))){
			ds.setRawData(obj);
			ds.setState(StateType.SUCCESS);
			ds.setTotal(50);
			// }else{
			// ds.setState(StateType.FAILT);
			// }
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			_log.error("办件公示查询失败！");
		}
		return ds;
	}

	/**
	 * 获取投资项目行业目录permitItemCode
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getCatalogInfo(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("proUrl")
				+ "/web/investProject/getCatalogInfo");
		String regionCode = (String) pSet.getParameter("regionCode");
		String permitIndustry = (String) pSet.getParameter("permitIndustry");
		String projectType = (String) pSet.getParameter("projectType");
		url += "?regionCode=" + regionCode + "&permitIndustry="
				+ permitIndustry + "&projectType=" + projectType;
		try {
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.getData(url));
			if ("200".equals(obj.getString("state"))) {
				ds.setRawData(obj.get("info"));
				ds.setState(StateType.SUCCESS);
			} else {
				ds.setMessage(obj.getString("error"));
				ds.setState(StateType.FAILT);
			}
		} catch (Exception e) {
			ds.setMessage(e.toString());
			ds.setState(StateType.FAILT);
			_log.error("获取投资项目行业目录出错！");
		}
		return ds;
	}

	/**
	 * 获取项目列表
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getProjectList(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			JSONObject userInfo = (JSONObject) pSet.getParameter("userInfo");
			if (!userInfo.isEmpty()) {
				String sql = "select * from ipro_index t where t.sblsh is not null and uuid = ?";
				ds = this.executeDataset(sql,
						new Object[] { userInfo.getString("uid") });
			} else {
				String sql = "select * from ipro_index t where t.sblsh is not null";
				ds = this.executeDataset(sql);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
		}
		return ds;
	}

	/**
	 * 获取项目列表 调投资接口
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getTZProjectList(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("proUrl")
				+ "/web/investProject/getProjectList");
		String projectCode = (String) pSet.getParameter("projectCode");
		url += "?projectCode=" + projectCode;
		try {
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.getData(url));
			if ("200".equals(obj.getString("state"))) {
				ds.setRawData(obj.get("info"));
				ds.setState(StateType.SUCCESS);
			} else {
				ds.setMessage(obj.getString("error"));
				ds.setState(StateType.FAILT);
			}
		} catch (Exception e) {
			ds.setMessage(e.toString());
			ds.setState(StateType.FAILT);
			_log.error("获取投资项目列表出错！");
		}
		return ds;
	}

	/**
	 * 获取项目根据sblsh
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getContent(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String sblsh = (String) pSet.get("id");
			String sql = "select * from ipro_index t where t.seqid = ?";
			ds = this.executeDataset(sql, new Object[] { sblsh });
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
		}
		return ds;
	}

	/**
	 * 注册后存表
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet submitProjectMessageSave(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String sql = "insert into ipro_index t "
					+ "(SBLSH,SXBM,SXMC,UUID,SBSJ,CONTENT,UCNAME,REGION_CODE,REGION_NAME,"
					+ "PROJECTNAME,SEQID,PROJECT_TYPE,printcontent) values (?,?,?,?,sysdate,?,?,?,?,?,?,?,?)";
			int i = this.executeUpdate(
					sql,
					new Object[] { "", "", "", (String) pSet.get("uid"),
							pSet.get("content").toString(),
							(String) pSet.get("ucname"),
							SecurityConfig.getString("WebRegion"), "",
							(String) pSet.get("projectname"),
							(String) pSet.get("seqid"),
							(String) pSet.get("projecttype"),
							pSet.get("printcontent").toString() });
			if (i > 0) {
				ds.setState(StateType.SUCCESS);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
		}
		return ds;
	}

	/**
	 * 我的项目
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getUserJDCX(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection(this.getDataSourceName());

		try {

			String uuid = pSet.getParameter("uuid").toString();
			String regionCode = pSet.getParameter("regionCode").toString();

			int page = Integer.parseInt(pSet.getParameter("page").toString());
			int rows = Integer.parseInt(pSet.getParameter("limit").toString());
			String sqlEx = "";
			int cnt = 0;
			if (StringUtil.isNotEmpty(regionCode)) {
				sqlEx = "SELECT count(1) cnt FROM IPRO_INDEX WHERE uuid = ? AND REGION_CODE=?";
				try {
					DataSet result = DbHelper.query(sqlEx, new Object[] { uuid,
							regionCode }, conn);
					cnt = result.getRecord(0).getInt("CNT");
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					conn.close();
				}
			} else {
				sqlEx = "SELECT count(1) cnt FROM IPRO_INDEX WHERE uuid = ?";
				try {
					DataSet result = DbHelper.query(sqlEx,
							new Object[] { uuid }, conn);
					cnt = result.getRecord(0).getInt("CNT");
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					conn.close();
				}
			}

			conn = DbHelper.getConnection(this.getDataSourceName());
			if (StringUtil.isNotEmpty(regionCode)) {
				sqlEx = "select * from (select rownum rn,t.* from (SELECT  SBLSH, SXBM, "
						+ "SXMC, UUID, to_char(SBSJ, 'yyyy-mm-dd') SBSJ, CONTENT, PROJECTNAME,"
						+ "SEQID,PROJECT_TYPE FROM IPRO_INDEX i  where uuid =? and region_code=? order by sbsj desc) t) WHERE rn>? and rn<=?";
				try {
					DataSet result = DbHelper.query(sqlEx, new Object[] { uuid,
							regionCode, (page - 1) * rows, page * rows }, conn);
					ds = result;
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					conn.close();
				}
			} else {
				sqlEx = "select * from (select rownum rn,t.* from (SELECT  SBLSH, SXBM, "
						+ "SXMC, UUID, to_char(SBSJ, 'yyyy-mm-dd') SBSJ, CONTENT, PROJECTNAME,"
						+ "SEQID,PROJECT_TYPE FROM IPRO_INDEX i  where uuid =? order by sbsj desc) t) WHERE rn>? and rn<=?";
				try {
					DataSet result = DbHelper.query(sqlEx, new Object[] { uuid,
							(page - 1) * rows, page * rows }, conn);
					ds = result;
					System.err.println("ds==" + ds);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					conn.close();
				}
			}
			ds.setTotal(cnt);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}

	/**
	 * 我的项目
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getJDCX(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection(this.getDataSourceName());
		try {
			JSONObject userInfo = (JSONObject) pSet.getParameter("userInfo");
			int page = (Integer) pSet.getParameter("page");
			int rows = (Integer) pSet.getParameter("limit");

			String sqlEx = "SELECT count(1) cnt FROM IPRO_INDEX WHERE uuid = ? and sblsh is not null";
			int cnt = 0;
			try {
				DataSet result = DbHelper.query(sqlEx,
						new Object[] { userInfo.getString("uuid") }, conn);
				cnt = result.getRecord(0).getInt("CNT");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				conn.close();
			}
			conn = DbHelper.getConnection(this.getDataSourceName());
			sqlEx = "select * from (select rownum rn,t.* from (SELECT  SBLSH, SXBM, "
					+ "SXMC, UUID, to_char(SBSJ, 'yyyy-mm-dd') SBSJ, CONTENT, PROJECTNAME,"
					+ "SEQID,PROJECT_TYPE FROM IPRO_INDEX i  where uuid = ? and sblsh is not null  order by sbsj desc) t) WHERE rn>? and rn<=?";
			try {
				DataSet result = DbHelper.query(sqlEx,
						new Object[] { userInfo.getString("uuid"),
								(page - 1) * rows, page * rows }, conn);
				ds = result;
				System.err.println("ds==" + ds);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				conn.close();
			}
			ds.setTotal(cnt);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}

	/**
	 * 查询上传过报告的项目列表
	 * 
	 * @return
	 */
	public DataSet queryIproReport(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String uuid = (String) pSet.get("uid");
			String sql = "select t.*,to_char(t.inserttime,'yyyy-MM-dd') time from ipro_report t where t.projectcode=? and t.uuid = ?";
			ds = this.executeDataset(sql,
					new Object[] { (String) pSet.get("projectcode"), uuid });
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
		}
		return ds;
	}

	/**
	 * 报告上传
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet submitProjectProcess(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("proUrl")
				+ "/web/investProject/submitProjectProcess");
		Map<String, String> data = new HashMap<String, String>();
		String paramArray = pSet.get("paramArray").toString();
		data.put("paramArray", paramArray);
		try {
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.postData(url, data));
			if ("200".equals(obj.getString("state"))) {
				DataSet _ds = saveIproReport(pSet);
				if (_ds.getState() == 1) {
					ds.setState(StateType.SUCCESS);
				} else {
					ds.setState(StateType.FAILT);
				}
			} else {
				ds.setState(StateType.FAILT);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			_log.error("提交失败！");
		}
		return ds;
	}

	/**
	 * 报告上传后本地保存
	 * 
	 * @param pSet
	 *            ID VARCHAR2(50) N id PROJECTCODE VARCHAR2(50) Y
	 *            项目编码对应ipro_index PROJECTSTATUSID VARCHAR2(10) Y
	 *            项目状态（字典属性）0在建1竣工2撤销 PROJECTSTATUSNAME VARCHAR2(50) Y
	 *            项目状态名称（字典名称） NODENAME VARCHAR2(50) Y 节点名称（字典名称） METAIL CLOB Y
	 *            附件 INSERTTIME DATE Y 时间 UUID VARCHAR2(50) Y 用户id NODEID
	 *            VARCHAR2(50) Y 节点代码（字典名称）
	 * @return
	 */
	public DataSet saveIproReport(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			JSONArray ja = JSONArray.fromObject(pSet.get("paramArray"));
			String PROJECTCODE = ja.getJSONObject(0).getString("projectCode");
			String PROJECTSTATUSID = ja.getJSONObject(0).getString(
					"projectStatusId");
			String PROJECTSTATUSNAME = ja.getJSONObject(0).getString(
					"projectStatusName");
			String NODEID = ja.getJSONObject(0).getString("nodeId");
			String NODENAME = ja.getJSONObject(0).getString("nodeName");
			String UUID = (String) pSet.get("uid");
			String PROJECTNAME = (String) pSet.get("PROJECTNAME");
			String sql = "insert into ipro_report t "
					+ "(ID,PROJECTCODE,PROJECTSTATUSID,PROJECTSTATUSNAME,NODEID,NODENAME,UUID,METAIL,INSERTTIME,PROJECTNAME) "
					+ "values (?,?,?,?,?,?,?,?,sysdate,?)";
			int i = this.executeUpdate(sql, new Object[] { Tools.getUUID32(),
					PROJECTCODE, PROJECTSTATUSID, PROJECTSTATUSNAME, NODEID,
					NODENAME, UUID, ja.toString(), PROJECTNAME });
			if (i > 0) {
				ds.setState(StateType.SUCCESS);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
		}
		return ds;
	}

	// 无偿代理的表单提交
	public DataSet submitWCDL(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("proUrl")
				+ "/web/investProject/submitAgent");
		Map<String, String> data = new HashMap<String, String>();
		data.put("projectCode", pSet.get("projectCode").toString());
		data.put("formId", pSet.get("formId").toString());
		data.put("dataId", pSet.get("dataId").toString());
		try {
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.postData(url, data));
			if ("200".equals(obj.getString("state"))) {
				ds.setState(StateType.SUCCESS);
			} else {
				ds.setState(StateType.FAILT);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			_log.error("提交失败！");
		}
		return ds;
	}

	// 咨询服务的表单提交
	public DataSet submitConsult(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("proUrl")
				+ "/web/investProject/submitOrgConsult");
		Map<String, String> data = new HashMap<String, String>();
		String consultSubject = pSet.get("consultSubject").toString();
		String projectCode = pSet.get("projectCode").toString();
		String formId = pSet.get("formId").toString();
		String dataId = pSet.get("dataId").toString();
		String investType = pSet.get("investType").toString();

		JSONObject jsonBumen = (JSONObject) pSet.get("orgArray");
		String orgName = jsonBumen.get("buMen").toString();
		String orgCode = jsonBumen.get("orgCode").toString();
		String[] orgNames = orgName.split(",");
		String[] orgCodes = orgCode.split(",");
		JSONArray orgArray = new JSONArray();
		for (int i = 0; i < orgNames.length; i++) {
			JSONObject orgArrays1 = new JSONObject();
			orgArrays1.put("orgCode", orgCodes[i]);
			orgArrays1.put("orgName", orgNames[i]);
			orgArray.add(orgArrays1);
		}
		String projectName = pSet.get("projectName").toString();
		String constructPer = pSet.get("constructPer").toString();
		String investment = pSet.get("investment").toString();
		String placeAreaDetail = pSet.get("placeAreaDetail").toString();
		String projectContent = pSet.get("projectContent").toString();
		String lerepNo = pSet.get("lerepNo").toString();
		String projectManagerName = pSet.get("projectManagerName").toString();
		String projectManagerPhone = pSet.get("projectManagerPhone").toString();
		String linkMan = pSet.get("linkMan").toString();
		String linkPhone = pSet.get("linkPhone").toString();
		String Content = pSet.get("Content").toString();
		JSONObject param = new JSONObject();
		param.put("consultSubject", consultSubject);
		param.put("projectCode", projectCode);
		param.put("formId", formId);
		param.put("dataId", dataId);
		param.put("investType", investType);
		param.put("orgArray", orgArray);
		param.put("projectName", projectName);
		param.put("constructPer", constructPer);
		param.put("investment", investment);
		param.put("placeAreaDetail", placeAreaDetail);
		param.put("projectContent", projectContent);
		param.put("lerepNo", lerepNo);
		param.put("projectManagerName", projectManagerName);
		param.put("projectManagerPhone", projectManagerPhone);
		param.put("linkMan", linkMan);
		param.put("linkPhone", linkPhone);
		param.put("Content", Content);
		data.put("paramJson", param.toString());

		try {
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.postData(url, data));
			if ("200".equals(obj.getString("state"))) {
				ds.setState(StateType.SUCCESS);
				// 存入icity库
				projectMessageSave(pSet, obj.getString("consultId"),
						projectCode);
			} else {
				ds.setState(StateType.FAILT);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			_log.error("提交失败！");
		}

		return ds;
	}

	// 咨询服务提交时插入gustbook表
	public DataSet projectMessageSave(ParameterSet pSet, String consultId,
			String projectCode) {
		DataSet ds = new DataSet();
		try {
			String sql = "INSERT INTO GUESTBOOK "
					+ "(ID, TYPE, TITLE, CONTENT,BUSI_ID,REGION_ID,WRITE_DATE,PROJECTCODE) "
					+ "VALUES(?,?,?,?,?,?,?,?)";
			String id = Tools.getNewID("mutual");
			int i = this.executeUpdate(
					sql,
					new Object[] {
							id, // id生成器生成
							"4",
							(String) pSet.get("consultSubject"),
							(String) pSet.get("Content"),
							consultId,
							(String) SecurityConfig.getString("WebRegion"),
							CommonUtils.getInstance().parseStringToTimeStamp(
									Tools.formatDate(new Date(),
											CommonUtils.YYYY_MM_DD_HH_mm_SS),
									CommonUtils.YYYY_MM_DD_HH_mm_SS),
							projectCode });
			if (i > 0) {
				ds.setState(StateType.SUCCESS);
			} else {
				ds.setState(StateType.FAILT);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
		}
		return ds;
	}

	/**
	 * 根据赋码获取项目信息
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getInfoByProjectCode(ParameterSet pSet) {
		String projectId = (String) pSet.get("projectId");

		if ("null".equals(projectId) || projectId == null) {
			projectId = "";
		}

		String projectCode = pSet.get("projectCode").toString();
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("proUrl")
				+ "/web/investProject/getInfoByProjectCode")
				+ "?projectCode=" + projectCode + "&projectId=" + projectId;
		HttpClientUtil client = new HttpClientUtil();
		try {

			JSONObject obj;
			String urla = client.getResult(url, "");
			obj = JSONObject.fromObject(urla);
			if ("200".equals(obj.getString("state"))) {

				ds.setRawData(obj);
			} else {
				ds.setState(StateType.FAILT);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			_log.error("获取项目信息失败！");
		}

		return ds;

	}

	/**
	 * 获取事项组信息（投资项目-联合审批）
	 * 
	 * @return
	 */
	public DataSet queryItemGroup(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("proUrl")
				+ "/web/investProject/queryItemGroup");
		String investType = (String) pSet.getParameter("investType");
		String regionCode = (String) pSet.getParameter("regionCode");
		url += "?investType=" + investType + "&regionCode=" + regionCode;
		try {
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.getData(url));
			if ("200".equals(obj.getString("state"))) {
				ds.setRawData(obj);
				ds.setState(StateType.SUCCESS);
			} else {
				ds.setState(StateType.FAILT);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			_log.error("获取事项列表出错！");
		}
		return ds;
	}

	/**
	 * 我的项目列表
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getMyProjectList(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection(this.getDataSourceName());

		String projectCode = (String) pSet.getParameter("projectCode");
		String projectName = (String) pSet.getParameter("projectName");
		String investType = (String) pSet.getParameter("investType");

		JSONObject userInfo = JSONObject.fromObject(pSet
				.getParameter("userInfo"));

		String sql = "SELECT SBLSH,UUID,PROJECTNAME,PROJECT_TYPE FROM IPRO_INDEX where uuid = ? and SBLSH IS NOT NULL ";

		if (!"".equals(projectCode)) {
			sql += "and SBLSH like '%" + projectCode + "%' ";
		}
		if (!"".equals(projectName)) {
			sql += "and PROJECTNAME like '%" + projectName + "%' ";
		}
		if (!"".equals(investType) && investType != null
				&& !"null".equals(investType)) {
			sql += "and PROJECT_TYPE = '" + investType + "' ";
		}

		try {
			DataSet result = DbHelper.query(sql,
					new Object[] { userInfo.getString("uuid") }, conn);
			ds = result;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return ds;
	}

	/**
	 * 联合审批提交申请
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet applyUnionBusiness(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("proUrl")
				+ "/web/investProject/applyUnionBusiness");
		Map<String, String> data = new HashMap<String, String>();
		String investType = pSet.get("investType").toString();
		String projectCode = pSet.get("projectCode").toString();
		String itemGroupId = pSet.get("itemGroupId").toString();
		String groupName = pSet.get("groupName").toString();
		String orgCode = pSet.get("orgCode").toString();
		String orgName = pSet.get("orgName").toString();
		String formId = pSet.get("formId").toString();
		String dataId = pSet.get("dataId").toString();
		data.put("investType", investType);
		data.put("projectCode", projectCode);
		data.put("itemGroupId", itemGroupId);
		data.put("groupName", groupName);
		data.put("orgCode", orgCode);
		data.put("orgName", orgName);
		data.put("formId", formId);
		data.put("dataId", dataId);
		try {
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.postData(url, data));
			if ("200".equals(obj.getString("state"))) {

				pSet.setParameter("bizId", obj.get("bizId"));

				DataSet _ds = saveUnionBusiness(pSet);
				if (_ds.getState() == 1) {
					ds.setState(StateType.SUCCESS);
				} else {
					ds.setState(StateType.FAILT);
				}
			} else {
				ds.setState(StateType.FAILT);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			_log.error("提交失败！");
		}
		return ds;
	}

	public DataSet saveUnionBusiness(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {

			String investType = pSet.get("investType").toString();
			String projectCode = pSet.get("projectCode").toString();
			String itemGroupId = pSet.get("itemGroupId").toString();
			String groupName = pSet.get("groupName").toString();
			String orgCode = pSet.get("orgCode").toString();
			String orgName = pSet.get("orgName").toString();
			String bizId = pSet.get("bizId").toString();
			String uid = (String) pSet.get("uid");
			String sql = "insert into ipro_union_index t "
					+ "(ID,BIZID,USERID,PROJECTCODE,INVESTTYPE,ITEMGROUPID,CREATETIME,GROUPNAME,ORGCODE,ORGNAME) "
					+ "values (?,?,?,?,?,?,sysdate,?,?,?)";
			int i = this.executeUpdate(sql, new Object[] { Tools.getUUID32(),
					bizId, uid, projectCode, investType, itemGroupId,
					groupName, orgCode, orgName });
			if (i > 0) {
				ds.setState(StateType.SUCCESS);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
		}
		return ds;
	}

	/**
	 * 获取事项组列表
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getItemGroupList(ParameterSet pSet) {
		DataSet ds = new DataSet();
		Connection conn = DbHelper.getConnection(this.getDataSourceName());

		String projectCode = (String) pSet.getParameter("projectCode");

		String sql = "SELECT BIZID,ITEMGROUPID,GROUPNAME,ORGNAME FROM IPRO_UNION_INDEX where PROJECTCODE = ? order by createtime desc";

		try {
			DataSet result = DbHelper.query(sql, new Object[] { projectCode },
					conn);
			ds = result;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return ds;
	}

	/**
	 * 联审业务回复结果查询
	 * 
	 * @return
	 */
	public DataSet queryUnionIsHandle(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("proUrl")
				+ "/web/investProject/queryUnionIsHandle");
		String bizId = (String) pSet.getParameter("bizId");
		url += "?bizId=" + bizId;
		try {
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.getData(url));
			if ("200".equals(obj.getString("state"))) {
				ds.setRawData(obj);
				ds.setState(StateType.SUCCESS);
			} else {
				ds.setState(StateType.FAILT);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			_log.error("获取回复结果出错！");
		}
		return ds;
	}

	/**
	 * 联审业务材料提交
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet submitUnionCailiao(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("proUrl")
				+ "/web/investProject/submitUnionCailiao");
		Map<String, String> data = new HashMap<String, String>();
		String bizId = pSet.get("bizId").toString();
		String itemList = pSet.get("itemList").toString();
		data.put("bizId", bizId);
		data.put("itemList", itemList);
		try {
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.postData(url, data));
			if ("200".equals(obj.getString("state"))) {
				ds.setState(StateType.SUCCESS);
			} else {
				ds.setState(StateType.FAILT);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			_log.error("提交失败！");
		}
		return ds;
	}

	/**
	 * 投资项目分类统计
	 * 
	 * @return
	 */
	public DataSet getProjectCount(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("proUrl")
				+ "/web/investProject/getProjectCount");
		String regionCode = (String) pSet.getParameter("regionCode");
		url += "?regionCode=" + regionCode;
		try {
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.getData(url));
			if ("200".equals(obj.getString("state"))) {
				ds.setRawData(obj);
				ds.setState(StateType.SUCCESS);
			} else {
				ds.setState(StateType.FAILT);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			_log.error("获取投资项目分类统计失败！");
		}
		return ds;
	}

	/**
	 * 获取投资项目列表-业务中心
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getProList(ParameterSet pSet) {

		DataSet ds = new DataSet();
		String sql = "SELECT SBLSH,UUID,TO_CHAR(SBSJ,'YYYY-MM-DD hh24:mi:ss') SBSJ,PROJECTNAME,PROJECT_TYPE,SEQID FROM IPRO_INDEX where 1=1 ";

		sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
		int start = pSet.getPageStart();
		int limit = pSet.getPageLimit();
		if (start == -1 || limit == -1) {
			ds = this.executeDataset(sql, null, this.getDataSourceName());
		} else {
			ds = this.executeDataset(sql, start, limit, null,
					this.getDataSourceName());
		}

		int total = ds.getTotal();

		// SEQID拼串
		StringBuffer seqids = new StringBuffer();
			JSONArray array = ds.getJAData();
			if (array != null && array.size() > 0) {
				for (int i = 0; i < array.size(); i++) {
					JSONObject jo = (JSONObject) array.get(i);
					if (jo != null) {
						seqids.append(jo.get("SEQID")).append(",");
					}
				}
			}

		String url = HttpUtil.formatUrl(SecurityConfig.getString("proUrl")
				+ "/web/investProject/getProjectListByProjectId");
		Map<String, String> data = new HashMap<String, String>();
		data.put("projectId", seqids.toString());
		try {
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.postData(url, data));

			ds = new DataSet();
			ds.setTotal(total);
			if ("200".equals(obj.getString("state"))) {
				ds.setRawData(obj.get("projectArray"));
				ds.setState(StateType.SUCCESS);
			} else {
				ds.setState(StateType.FAILT);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			_log.error("获取项目列表失败！");
		}

		return ds;
	}

	/**
	 * 根据项目ID查询单体业务
	 * 
	 * @return
	 */
	public DataSet getBussinessByProjectId(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("proUrl")
				+ "/web/investProject/getBussinessByProjectId");
		Map<String, String> data = new HashMap<String, String>();
		String projectId = (String) pSet.getParameter("projectId");
		String itemName = (String) pSet.getParameter("itemName");
		String stage = (String) pSet.getParameter("stage");
		String startTime = (String) pSet.getParameter("startTime");
		String endTime = (String) pSet.getParameter("endTime");

		data.put("projectId", projectId);
		data.put("stage", stage);
		data.put("startTime", startTime);
		data.put("endTime", endTime);
		data.put("itemName", itemName);
		try {
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.postData(url, data));
			if ("200".equals(obj.getString("state"))) {
				ds.setRawData(obj.get("bizArray"));
				ds.setState(StateType.SUCCESS);
			} else {
				ds.setMessage(obj.getString("error"));
				ds.setState(StateType.FAILT);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			_log.error("查询单体业务失败！");
		}

		return ds;
	}

	/**
	 * 根据项目ID查询联合业务
	 * 
	 * @return
	 */
	public DataSet geUnionBizByProjectId(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("proUrl")
				+ "/web/investProject/geUnionBizByProjectId");
		String projectId = (String) pSet.getParameter("projectId");
		String startTime = (String) pSet.getParameter("startTime");
		String endTime = (String) pSet.getParameter("endTime");
		url += "?projectId=" + projectId + "&startTime=" + startTime
				+ "&endTime=" + endTime;
		try {
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.getData(url));
			if ("200".equals(obj.getString("state"))) {
				ds.setRawData(obj.get("bizArray"));
				ds.setState(StateType.SUCCESS);
			} else {
				ds.setState(StateType.FAILT);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			_log.error("查询单体业务失败！");
		}
		return ds;
	}

	/**
	 * 项目代理信息
	 * 
	 * @return
	 */
	public DataSet getProjectAgent(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("proUrl")
				+ "/web/investProject/getProjectAgent");
		String projectId = (String) pSet.getParameter("projectId");
		url += "?projectId=" + projectId;
		try {
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.getData(url));
			if ("200".equals(obj.getString("state"))) {
				ds.setRawData(obj);
				ds.setState(StateType.SUCCESS);
			} else {
				ds.setState(StateType.FAILT);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			_log.error("获取代理信息失败！");
		}
		return ds;
	}

	/**
	 * 联合踏勘记录
	 * 
	 * @return
	 */
	public DataSet getReconnaissance(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("proUrl")
				+ "/web/investProject/getReconnaissance");
		String projectId = (String) pSet.getParameter("projectId");
		url += "?projectId=" + projectId;
		try {
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.getData(url));
			if ("200".equals(obj.getString("state"))) {
				ds.setRawData(obj);
				ds.setState(StateType.SUCCESS);
			} else {
				ds.setState(StateType.FAILT);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			_log.error("获取联合踏勘记录失败！");
		}
		return ds;
	}

	/**
	 * 联合踏勘通知反馈信息
	 * 
	 * @return
	 */
	public DataSet getFeedback(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("proUrl")
				+ "/web/investProject/getFeedback");
		String reconId = (String) pSet.getParameter("reconId");
		url += "?reconId=" + reconId;
		try {
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.getData(url));
			if ("200".equals(obj.getString("state"))) {
				ds.setRawData(obj);
				ds.setState(StateType.SUCCESS);
			} else {
				ds.setState(StateType.FAILT);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			_log.error("获取联合踏勘通知反馈信息失败！");
		}
		return ds;
	}

	/**
	 * 联合踏勘意见反馈信息
	 * 
	 * @return
	 */
	public DataSet getFeedbackOpinion(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("proUrl")
				+ "/web/investProject/getFeedbackOpinion");
		String reconId = (String) pSet.getParameter("reconId");
		url += "?reconId=" + reconId;
		try {
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.getData(url));
			if ("200".equals(obj.getString("state"))) {
				ds.setRawData(obj);
				ds.setState(StateType.SUCCESS);
			} else {
				ds.setState(StateType.FAILT);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			_log.error("获取联合踏勘意见反馈信息失败！");
		}
		return ds;
	}

	/**
	 * 联合踏勘会议纪要
	 * 
	 * @return
	 */
	public DataSet getSummary(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("proUrl")
				+ "/web/investProject/getSummary");
		String reconId = (String) pSet.getParameter("reconId");
		url += "?reconId=" + reconId;
		try {
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.getData(url));
			if ("200".equals(obj.getString("state"))) {
				ds.setRawData(obj);
				ds.setState(StateType.SUCCESS);
			} else {
				ds.setState(StateType.FAILT);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			_log.error("获取联合踏勘会议纪要失败！");
		}
		return ds;
	}

	/**
	 * 项目报告查询
	 * 
	 * @return
	 */
	public DataSet queryProjectReport(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("proUrl")
				+ "/web/investProject/queryProjectReport");
		String projectId = (String) pSet.getParameter("projectId");
		url += "?projectId=" + projectId;
		try {
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.getData(url));
			if ("200".equals(obj.getString("state"))) {
				ds.setRawData(obj);
				ds.setState(StateType.SUCCESS);
			} else {
				ds.setState(StateType.FAILT);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
		}
		return ds;
	}

	/**
	 * 踏勘通知信息
	 * 
	 * @return
	 */
	public DataSet getReconInfoById(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("proUrl")
				+ "/web/investProject/getReconInfoById");
		String reconId = (String) pSet.getParameter("reconId");
		url += "?reconId=" + reconId;
		try {
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.getData(url));
			if ("200".equals(obj.getString("state"))) {
				ds.setRawData(obj);
				ds.setState(StateType.SUCCESS);
			} else {
				ds.setState(StateType.FAILT);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			_log.error("踏勘通知信息查询失败！");
		}
		return ds;
	}

	/**
	 * 获取材料信息
	 * 
	 * @return
	 */
	public DataSet getFileByBizId(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("proUrl")
				+ "/web/investProject/getFileByBizId");
		String bizId = (String) pSet.getParameter("bizId");
		url += "?bizId=" + bizId;
		try {
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.getData(url));
			if ("200".equals(obj.getString("state"))) {
				ds.setRawData(obj);
				ds.setState(StateType.SUCCESS);
			} else {
				ds.setState(StateType.FAILT);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			_log.error("获取材料信息失败！");
		}
		return ds;
	}

	/**
	 * 咨询结果
	 * 
	 * @return
	 */
	public DataSet queryConsultResult(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("proUrl")
				+ "/web/investProject/queryConsultResult");
		String id = (String) pSet.getParameter("id");
		url += "?consultId=" + id;
		try {
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.getData(url));
			if ("200".equals(obj.getString("state"))) {
				ds.setRawData(obj);
				ds.setState(StateType.SUCCESS);
			} else {
				ds.setState(StateType.FAILT);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
		}
		return ds;
	}

	/**
	 * 获取项目
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getProject(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {

			String uuid = pSet.getParameter("UUID").toString();
			String sblsh = pSet.getParameter("SBLSH").toString();

			String sql = "select * from ipro_index t where t.sblsh = ? and uuid = ?";
			ds = this.executeDataset(sql, new Object[] { sblsh, uuid });
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
		}
		return ds;
	}

	/**
	 * 项目关联插入
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet insertIproIndex(ParameterSet pSet) {
		DataSet ds = new DataSet();

		JSONObject jsonObject = JSONObject.fromObject(pSet.get("data"));

		Object projectInfo = jsonObject.get("projectInfo");
		String sblsh = ((JSONObject) projectInfo).get("projectCode").toString();
		String seqid = ((JSONObject) projectInfo).get("projectId").toString();
		String regionName = ((JSONObject) projectInfo).get("regionName")
				.toString();
		String regionCode = ((JSONObject) projectInfo).get("regionCode")
				.toString();
		String projectName = ((JSONObject) projectInfo).get("projectName")
				.toString();
		String projectType = ((JSONObject) projectInfo).get("projectType")
				.toString();
		String uuid = pSet.getParameter("uuid").toString();
		String ucname = pSet.getParameter("ucname").toString();

		// SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		// try {
		// Date d = dateFormat.parse(((JSONObject)
		// projectInfo).get("applyDate").toString());
		// } catch (ParseException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }

		try {
			String sql = "insert into ipro_index t "
					+ "(SBLSH,SXBM,SXMC,UUID,SBSJ,CONTENT,UCNAME,REGION_CODE,REGION_NAME,"
					+ "PROJECTNAME,SEQID,PROJECT_TYPE) values (?,?,?,?,sysdate,?,?,?,?,?,?,?)";
			int i = this.executeUpdate(sql, new Object[] { sblsh, "", "", uuid,
					"", ucname, regionCode, regionName, projectName, seqid,
					projectType });
			if (i > 0) {
				ds.setState(StateType.SUCCESS);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
		}
		return ds;
	}

	/**
	 * 获取一次性告知
	 * 
	 * @return
	 */
	public DataSet queryOnceByProjectId(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("proUrl")
				+ "/web/investProject/queryOnceByProjectId");
		String projectId = (String) pSet.getParameter("projectId");
		url += "?projectId=" + projectId;
		try {
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.getData(url));
			if ("200".equals(obj.getString("state"))) {
				ds.setRawData(obj);
				ds.setState(StateType.SUCCESS);
			} else {
				ds.setState(StateType.FAILT);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			_log.error("获取一次性告知失败！");
		}
		return ds;
	}
	/**
	 * 事项组办事指南
	 * @param pSet
	 * @return
	 */
	public DataSet getServiceGuideById(ParameterSet pSet){
		DataSet ds = new DataSet();
		String url = HttpUtil.formatUrl(SecurityConfig.getString("proUrl")
				+ "/web/investProject/getServiceGuideById");
		String itemGroupId = (String) pSet.getParameter("itemGroupId");
		url += "?groupId=" + itemGroupId;
		try {
			JSONObject obj;
			obj = JSONObject.fromObject(RestUtil.getData(url));
			if ("200".equals(obj.getString("state"))) {
				ds.setRawData(obj);
				ds.setState(StateType.SUCCESS);
			} else {
				ds.setState(StateType.FAILT);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			_log.error("获取事项组办事指南失败！");
		}
		return ds;
	}
}
