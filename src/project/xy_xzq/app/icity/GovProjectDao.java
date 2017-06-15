package xy_xzq.app.icity;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import app.icity.ServiceCmd;
import app.util.RestUtil;

import com.icore.http.util.HttpUtil;
import com.icore.util.DaoFactory;
import com.icore.util.SecurityConfig;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

public class GovProjectDao extends BaseJdbcDao {
	private GovProjectDao() {
		this.setDataSourceName("icityDataSource");
	}

	public static GovProjectDao getInstance() {
		return DaoFactory.getDao(GovProjectDao.class.getName());
	}

	
	public DataSet getRdbs(ParameterSet pSet) {
		String sql ="select count(*),sxmc,sxbm,sjdw,sjdwdm,SXID from BUSINESS_INDEX t group by sxmc,sxbm,sjdw,sjdwdm,SXID order by count(*) desc ";
		return this.executeDataset(sql, null);
	
	}
	
	public DataSet getChannelList(ParameterSet pSet) {
		String sql ="select * from PUB_CHANNEL t order by ctime ";
		return this.executeDataset(sql, null);
	
	}
	
	public DataSet getContentList(ParameterSet pSet) {
		String title = (String)pSet.getParameter("searchName");
		String channelId = (String)pSet.getParameter("channelId");
		int start = (Integer)pSet.getParameter("page");
		int limit = (Integer)pSet.getParameter("limit");
		String sql ="select p.NAME,p.ID,p.CID,u.name uname,p.DEPT_NAME,p.DEPT_ID,to_char(p.ctime,'yyyy-mm-dd') ctime from PUB_CONTENT p,pub_channel u  where p.cid=u.id ";
		if(title.length()>0){
			sql+= " and p.name like '%"+title+"%' ";
		}
		if(channelId.length()>0){
			sql+= " and p.cid = '"+channelId+"' ";
		}
		sql+=" order by p.ctime ";
		return this.executeDataset(sql, start, limit);
	
	}
	public DataSet getContenttolList(ParameterSet pSet) {
		String UNAME = (String)pSet.getParameter("uname");
		String sql ="select p.NAME,p.ID,p.CID,u.name uname,p.DEPT_NAME,p.DEPT_ID,to_char(p.ctime,'yyyy-mm-dd') ctime from PUB_CONTENT p,pub_channel u  where p.cid=u.id and u.name='"+UNAME+"' and ROWNUM<=8 ";
		sql+=" order by p.ctime ";
		return this.executeDataset(sql, null);
	
	}
	public DataSet getContenttplList(ParameterSet pSet) {
		String sql ="select t.id,t.name,a.docid,a.name as docname,t.ctime from PUB_CONTENT t left join ATTACH a on  t.id=a.conid   where a.type='1' and ROWNUM<=3   order by t.ctime desc ";
		return this.executeDataset(sql, null);
	
	}
	
	public DataSet getContent(ParameterSet pSet) {
		String id = (String)pSet.getParameter("id");
		String sql ="select name,content,to_char(ctime,'yyyy-mm-dd') ctime from PUB_content t where id='"+id+"' order by ctime ";
		return this.executeDataset(sql, null);
	
	}
	
	public DataSet getPowerList(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String url = HttpUtil.formatUrl(SecurityConfig.getString("ItemSynchronizationUrl") + "/getItemListByPage");
			String per = SecurityConfig.getString("subject.person.code");
			if (StringUtils.isEmpty(per)) {
				per = "0";
			}
			String ent = SecurityConfig.getString("subject.legalPerson.code");
			if (StringUtils.isEmpty(ent)) {
				ent = "1";
			}
			String bmfw = SecurityConfig.getString("subject.publicService.code");
			if (StringUtils.isEmpty(bmfw)) {
				bmfw = "4";
			}
			Map<String, String> map=new HashMap<String, String>();
			StringBuilder whereValue = new StringBuilder();
			JSONArray paramValue = new JSONArray();
			String cat = (String) pSet.getParameter("cat");
			String classtype = (String) pSet.getParameter("ID");
			String deptid = (String) pSet.getParameter("deptid");
			String item_type = (String) pSet.getParameter("ITEM_TYPE");
			String bm_type = (String) pSet.getParameter("BM_TYPE");
			String region_code = (String) pSet.getParameter("region_code");
			String folder_code = (String) pSet.getParameter("folder_code");
			String SUIT_ONLINE = (String) pSet.getParameter("SUIT_ONLINE");
			String SearchName = (String) pSet.getParameter("SearchName");
			String pagemodel = (String) pSet.getParameter("pagemodel");
			String theme = (String) pSet.getParameter("theme");
			String idList = (String) pSet.getParameter("ID_LIST");
			if ("1".equals(SUIT_ONLINE)) {
				whereValue.append(" and is_online=? ");
				paramValue.add("1");
			}
			if (StringUtils.isNotEmpty(SearchName)) {
				whereValue.append(" and name like ? ");
				paramValue.add("%" + SearchName + "%");
			}

			if (StringUtils.isNotEmpty(bm_type)) {
				whereValue.append(" and type=? ");
				paramValue.add("BM");
				whereValue.append(" and sub_type=? ");
				paramValue.add(bm_type);
			}

			if (StringUtils.isNotEmpty(region_code)) {
				whereValue.append(" and region_code=? ");
				paramValue.add(region_code);
			}
			if (StringUtils.isNotEmpty(deptid)) {
				whereValue.append(" and org_code=? ");
				paramValue.add(deptid);
			}
			if (StringUtils.isNotEmpty(item_type)) {
				whereValue.append(" and type =? ");
				paramValue.add(item_type);
			}
			if (StringUtils.isNotEmpty(folder_code)) {
				whereValue.append(" and folder_code=? ");
				paramValue.add(folder_code);
			}
			if (("theme").equals(cat)) {
				whereValue.append(" and title_name like ? ");
				paramValue.add("%" + classtype + "%");
			}
			if (("person").equals(pagemodel)) {
				whereValue.append(" and service_object like ? ");
				paramValue.add("%"+per+"%");
			} else if (("ent").equals(pagemodel)) {
				whereValue.append(" and service_object like ? ");
				paramValue.add("%"+ent+"%");
			} else if (("bmfw").equals(pagemodel)) {
				whereValue.append(" and service_object like ? ");
				paramValue.add("%" + bmfw + "%");
			} 

			if (StringUtils.isNotEmpty(theme)) {
				whereValue.append(" and title_name like ? ");
				paramValue.add("%" + theme + "%");
			}

			pSet.put("region_code", SecurityConfig.getString("WebRegion"));
			DataSet dept = ServiceCmd.getInstance().getDeptList(pSet);
			JSONArray organs = dept.getJOData().getJSONArray("organ");
			if (organs.size() > 0) {
				int __count = 0;
				JSONObject organ = null;
				StringBuffer sqlBuff = new StringBuffer(" and org_code in ( ");
				String sql = "";
				for (int i = 0; i < organs.size(); i++) {
					organ = organs.getJSONObject(i);
					if ("1".equals(organ.get("IS_HALL"))) {
						__count++;
						sqlBuff.append("?,");
						paramValue.add(organ.get("CODE"));
					}
				}
				sql = sqlBuff.toString();
				if (__count > 0) {
					whereValue.append(sql.substring(0, sql.length() - 1) + ") ");
				}
			}

			// 按照事项id列表查询，不会参数化拼接in查询条件。 by yenan
			if (StringUtils.isNotEmpty(idList)) {
				String[] idList_ = idList.split(",");
				StringBuffer idwhereBuff = new StringBuffer(" and id in (");
				String idwhere = "";
				for (String item : idList_) {
					idwhereBuff.append("?,");
					paramValue.add(item);
				}
				idwhere = idwhereBuff.toString();
				idwhere = idwhere.substring(0, idwhere.length() - 1) + ") ";
				whereValue.append(idwhere);
			}
			if (StringUtils.isNotEmpty((String) pSet.getParameter("startTime"))) {
				map.put("startTime", pSet.getParameter("startTime").toString());
			}
			if (StringUtils.isNotEmpty((String) pSet.getParameter("endTime"))) {
				map.put("endTime", pSet.getParameter("endTime").toString());
			}
			map.put("page", pSet.getParameter("page").toString());
			map.put("rows", pSet.getParameter("limit").toString());
			map.put("whereValue", whereValue.toString());
			map.put("paramValue", paramValue.toString());
			map.put("order", "order by type desc,last_time desc");
			JSONObject obj = JSONObject.fromObject(RestUtil.postData(url, map));
			JSONArray pageList = obj.getJSONArray("pageList");
			JSONArray rows = new JSONArray();
			for (int i = 0; i < pageList.size(); i++) {
				JSONObject column = (JSONObject) pageList.get(i);
				rows.add(column.get("columns"));
			}
			ds.setRawData(rows);
			ds.setTotal(obj.getInt("totlaRow"));
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage("查询事项系统失败！");
		}
		// System.out.println("ds:"+ds);
		return ds;
	}

}