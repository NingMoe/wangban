package hlj_qqheNew.app.icity.project;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import app.icity.project.ProjectQueryDao;

import com.alibaba.dubbo.common.json.JSONObject;
import com.icore.util.DaoFactory;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.Tools;
import com.inspur.util.db.SqlCreator;

import core.util.CommonUtils;

public class ZmhdTsDao extends BaseJdbcDao{
	  private static Log _log = LogFactory.getLog(ZmhdTsDao.class);
	    protected static String icityDataSource = "icityDataSource";
	    protected static String ipfDataSource = "ipfDataSource";
	    protected static String icpDataSource = "icpDataSource";
	    private static ZmhdTsDao _instance = null;

	    public static ZmhdTsDao getInstance() {
	    	return (ZmhdTsDao)DaoFactory.getDao(ZmhdTsDao.class.getName());
	    }
	    public ZmhdTsDao() {
	        this.setDataSourceName(icityDataSource);
	    }
	
	
	
	
	
	public DataSet insert(ParameterSet pSet) {
		DataSet ds = new DataSet();
		String id="";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		id = sdf.format(new Date());
		String depart = (String) pSet.getParameter("depart_name");
		String[] s = depart.split(",");
		String depart_name=s[0];
		String depart_code=s[1];
		String type = (String) pSet.getParameter("type");	
		String name = (String) pSet.getParameter("name");
		String title = (String) pSet.getParameter("title");
		String content = (String) pSet.getParameter("content");
		String email = (String) pSet.getParameter("email");
		String phone = (String) pSet.getParameter("phone");
		String user_id = (String) pSet.getParameter("user_id");
		String region_id = (String)pSet.getParameter("region_id");
		String sql="insert into guestbook(id,depart_name,type,phone,email,username,content,write_date,title,user_id,region_id,depart_id) values ('"+id+"','"+depart_name+"','"+type+"','"+phone+"','"+email+"','"+name+"','"+content+"'," + "sysdate" + ",'"+title+"','"+user_id+"','"+region_id+"','"+depart_code+"' )";
		int i = this.executeUpdate(sql, null, icityDataSource);
        if (i == 0) {
            ds.setState(StateType.FAILT);
            ds.setMessage("数据库操作失败！");
        } else {
            ds.setState(StateType.SUCCESS);
        }
        return ds;
    }
	
	
	
	//留言评论发布
	public DataSet PublishMessage(ParameterSet pSet) { 
		DataSet ds = new DataSet();
        String sbrxm=(String) pSet.getParameter("sbrxm");
        String sblsh=(String) pSet.getParameter("sblsh");
		String sql="select sxbm from business_index where sblsh='"+sblsh+"' and sqrmc='"+sbrxm+"' and state='99' ";
		int i = this.executeUpdate(sql, null, icityDataSource);
        if (i == 0) {
            ds.setState(StateType.FAILT);
            ds.setMessage("数据库操作失败！");
            return ds;
        } else {
        	String sql1="select a.SBLSH,a.SXBM,a.SXMC,a.SQRMC,a.SBXMMC,a.SLSJ,to_char(a.SBSJ,'yyyy-mm-dd hh24:mi:ss') SBSJ,a.SJDWDM,a.SJDW,a.STATE,a.SQRZJHM from business_index a where sblsh='"+sblsh+"' ";
        	DataSet result1 = this.executeDataset(sql1, null, icityDataSource);
        	
        	//String sql2="select serial_number,star_level  from star_level_evaluation  where serial_number='"+sblsh+"' ";
        	//DataSet result2 = this.executeDataset(sql2, null, icityDataSource);        	
        	
        	        	
            ds.setState(StateType.SUCCESS);
            return result1;
        }
		
	}
	
	//
	public DataSet check_pj(ParameterSet pSet) {
		DataSet ds = new DataSet();
        String sblsh=(String) pSet.getParameter("sblsh");
        String sql="select y.star_level,y.EVALUATE_CONTENT,y.bslc,y.bsxl,y.bstd,a.SBLSH,a.SXBM,a.SXMC,a.SQRMC,a.SBXMMC,a.SLSJ,a.SBSJ,a.SJDWDM,a.SJDW,a.STATE,a.SQRZJHM from business_index a,star_level_evaluation y where a.sblsh=y.serial_number and y.serial_number='"+sblsh+"' ";
        int i = this.executeUpdate(sql, null, icityDataSource);
        if (i == 0) {
        	
        	return ds; 
        } else {
        	DataSet result1 = this.executeDataset(sql, null, icityDataSource);
        	return result1;
        }
       
	}
	
	//我的评价
	public DataSet getMypj(ParameterSet pSet) {

		String sql="select a.sxmc,a.sblsh,b.star_level,b.EVALUATE_CONTENT from STAR_LEVEL_EVALUATION b,BUSINESS_INDEX a where a.XZQHDM ='"+pSet.remove("XZQHDM")+"' and a.SBLSH = b.SERIAL_NUMBER";
		int start = pSet.getPageStart();
		int limit = pSet.getPageLimit();
		if (start == -1 || limit == -1) {
			sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
			return this.executeDataset(sql, null);
		} else {
			sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
			return this.executeDataset(sql, start, limit, null);
		}
       
	}
	//我的评价详情
	public DataSet getMypjxx(ParameterSet pSet) {
		
		String sql = "select a.sxmc,b.star_level,b.EVALUATE_CONTENT from STAR_LEVEL_EVALUATION b,BUSINESS_INDEX a where a.XZQHDM ='"+pSet.remove("XZQHDM")+"' and a.SBLSH = b.SERIAL_NUMBER and a.SBLSH = '"+pSet.remove("SBLSH")+"'";
		return this.executeDataset(sql, null);
	}
	
	//
	public DataSet check_pj_ok(ParameterSet pSet) {
		DataSet ds = new DataSet();
        String sblsh=(String) pSet.getParameter("sblsh");
        String sql="select serial_number,star_level  from star_level_evaluation  where serial_number='"+sblsh+"' ";
        int i = this.executeUpdate(sql, null, icityDataSource);
        if (i == 0) {
            ds.setState(StateType.FAILT);
            ds.setMessage("数据库操作失败！");
            return ds;
        } else {
        	DataSet result = this.executeDataset(sql, null, icityDataSource);
            ds.setState(StateType.SUCCESS);
            return result;
        }

	}
	
	
	//留言 评价
	public DataSet insert_pl(ParameterSet pSet) {
		DataSet ds = new DataSet();
//		String sblsh = (String) pSet.getParameter("serial_number");
//		String sql = "select * from STAR_LEVEL_EVALUATION t where t.serial_number = '"
//				+ sblsh + "'";
//		ds = this.executeDataset(sql, null);
//		if (ds.getTotal() > 0) {
//			ds.setState(StateType.FAILT);
//			ds.setMessage("每笔业务只能评议一次！");
//			return ds;
//		}

		String sql = "insert into STAR_LEVEL_EVALUATION(ID, SERVICE_CODE, BSR_NAME, BSR_IDENTITY_NUMBER,SERIAL_NUMBER, STAR_LEVEL, EVALUATE_CONTENT, NOTES, CREATOR_DATE) values(?,?,?,?,?,?,?,?,?)";
		int i = this.executeUpdate(sql, new Object[] {
				Tools.getUUID32(),
				(String) pSet.getParameter("sxbm"),
				//(String) pSet.getParameter("service_org_id"),
				(String) pSet.getParameter("sbrxm"),
				(String) pSet.getParameter("sqrzjhm"),
				(String) pSet.getParameter("sblsh"),
				Integer.parseInt(pSet.getParameter("py").toString()),
				(String) pSet.getParameter("py"),
				"",
				//pSet.getRemoteAddr(),
				//(String) pSet.getParameter("client_type"),
				//pSet.getParameter("creator_id"),
				CommonUtils.getInstance().parseDateToTimeStamp(new Date(),
						CommonUtils.YYYY_MM_DD_HH_mm_SS) });
		if (i == 0) {
			ds.setState(StateType.FAILT);
			ds.setMessage("数据库操作失败！");
		} else {
			ds.setState(StateType.SUCCESS);
		}
		return ds;
	}
	
	//满意度评价
	//留言 评价
		public DataSet insert_pl_dq(ParameterSet pSet) {
			DataSet ds = new DataSet();
			
			String sql = "insert into STAR_LEVEL_EVALUATION(ID, SERVICE_CODE, BSR_NAME, BSR_IDENTITY_NUMBER,SERIAL_NUMBER, STAR_LEVEL, EVALUATE_CONTENT, CREATOR_DATE,bslc,bsxl,bstd,is_pj) values(?,?,?,?,?,?,?,?,?,?,?,?)";
			int i = this.executeUpdate(sql, new Object[] {
					Tools.getUUID32(),
					(String) pSet.getParameter("sxbm"),
					(String) pSet.getParameter("sbrxm"),
					(String) pSet.getParameter("sqrzjhm"),
					(String) pSet.getParameter("sblsh"),
					Integer.parseInt(pSet.getParameter("myd").toString()),
					(String) pSet.getParameter("pjnr"),
					CommonUtils.getInstance().parseDateToTimeStamp(new Date(),
							CommonUtils.YYYY_MM_DD_HH_mm_SS),
					(String) pSet.getParameter("lc"),
					(String) pSet.getParameter("xl"),
					(String) pSet.getParameter("td"),
					1
							});
			if (i == 0) {
				ds.setState(StateType.FAILT);
				ds.setMessage("数据库操作失败！");
			} else {
				ds.setState(StateType.SUCCESS);
			}
			return ds;
		}
	
	
    //常见问题
	public DataSet getProjectInfo(ParameterSet pSet) {
		String sql="select rownum as num,a.id,a.name,substr(a.name,0,35) short_name,to_char(a.ctime,'yyyy-mm-dd') as time,a.summary,a.source,a.submit_status,to_char(a.valid_date_start,'yyyy-mm-dd'),to_char(a.valid_date_end,'yyyy-mm-dd') from PUB_CONTENT a,pub_channel b WHERE Checks='1' and  a.CID=b.ID(+) and b.name='常见问题' and a.rid='"+pSet.remove("XZQHDM")+"'";
		
		int start = pSet.getPageStart();
		int limit = pSet.getPageLimit();
		if (start == -1 || limit == -1) {
			sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
			return this.executeDataset(sql, null);
		} else {
			sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
			return this.executeDataset(sql, start, limit, null);
		}

		
		
	}
	
	//查询问题详情
	public DataSet getName(ParameterSet pSet) {
        DataSet ds=new DataSet();
		String id=(String) pSet.getParameter("id");
		String sql="select name,summary,content from pub_content WHERE id='"+id+"' ";
        int i = this.executeUpdate(sql, null, icityDataSource);
        if (i == 0) {
            ds.setState(StateType.FAILT);
            ds.setMessage("数据库操作失败！");
            return ds;
        } else {
        	DataSet result = this.executeDataset(sql, null, icityDataSource);
            ds.setState(StateType.SUCCESS);
            return result;
        }		
	}
	

	//我的投诉
		public DataSet getMyTs(ParameterSet pSet) {
			String sql="select id,title,to_char(write_date,'yyyy-mm-dd') as write_date,deal_result,substr(trim(deal_result),0,46) as deal_result_short from GUESTBOOK where type='3' and region_id ='"+pSet.remove("XZQHDM")+"'";
			int start = pSet.getPageStart();
			int limit = pSet.getPageLimit();
			if (start == -1 || limit == -1) {
				sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
				return this.executeDataset(sql, null);
			} else {
				sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
				return this.executeDataset(sql, start, limit, null);
			}
		}
		
		//我的咨询
			public DataSet getMyZx(ParameterSet pSet) {

				String sql="select id,title,to_char(write_date,'yyyy-mm-dd') as write_date,deal_result,substr(trim(deal_result),0,46) as deal_result_short from GUESTBOOK where type='2' and region_id ='"+pSet.remove("XZQHDM")+"'";
				int start = pSet.getPageStart();
				int limit = pSet.getPageLimit();
				if (start == -1 || limit == -1) {
					sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
					return this.executeDataset(sql, null);
				} else {
					sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
					return this.executeDataset(sql, start, limit, null);
				}
			}
			//我的咨询
			public DataSet getMyQz(ParameterSet pSet) {
				String sql="select id,title,content,substr(trim(content),0,46) short_content,to_char(write_date,'yyyy-mm-dd') createDate,deal_result,substr(trim(deal_result),0,46) deal_result_short,type from guestbook where 1=1 and type='12' ";
				//String user_id=(String) pSet.getParameter("user_id");
				int start = pSet.getPageStart();
				int limit = pSet.getPageLimit();
				if (start == -1 || limit == -1) {
					sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
					return this.executeDataset(sql, null);
				} else {
					sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
					return this.executeDataset(sql, start, limit, null);
				}	
			}
			
			//我的投诉详细展示
			public DataSet getMytsXx(ParameterSet pSet) {
		        DataSet ds=new DataSet();
				String id=(String) pSet.getParameter("id");
				String sql="select id,title,content,to_char(write_date,'yyyy-mm-dd') createDate,deal_result,substr(trim(deal_result),0,50) deal_result_short,type from guestbook where 1=1 and type='3' and id='"+id+"' ";
		        int i = this.executeUpdate(sql, null, icityDataSource);
		        if (i == 0) {
		            ds.setState(StateType.FAILT);
		            ds.setMessage("数据库操作失败！");
		            return ds;
		        } else {
		        	DataSet result = this.executeDataset(sql, null, icityDataSource);
		            ds.setState(StateType.SUCCESS);
		            return result;
		        }		
			}
			
			
			
			//我的咨询详细展示
			public DataSet getMyzxXx(ParameterSet pSet) {
		        DataSet ds=new DataSet();
				String id=(String) pSet.getParameter("id");
				String sql="select id,title,content,to_char(write_date,'yyyy-mm-dd') createDate,deal_result,substr(trim(deal_result),0,50) deal_result_short,type from guestbook where 1=1 and type='2' and id='"+id+"' ";
		        int i = this.executeUpdate(sql, null, icityDataSource);
		        if (i == 0) {
		            ds.setState(StateType.FAILT);
		            ds.setMessage("数据库操作失败！");
		            return ds;
		        } else {
		        	DataSet result = this.executeDataset(sql, null, icityDataSource);
		            ds.setState(StateType.SUCCESS);
		            return result;
		        }		
			}
			//我的求助详细展示
			public DataSet getMyqzXx(ParameterSet pSet) {
		        DataSet ds=new DataSet();
				String id=(String) pSet.getParameter("id");
				String sql="select id,title,content,to_char(write_date,'yyyy-mm-dd') createDate,deal_result,substr(trim(deal_result),0,50) deal_result_short,type from guestbook where 1=1 and type='12' and id='"+id+"' ";
		        int i = this.executeUpdate(sql, null, icityDataSource);
		        if (i == 0) {
		            ds.setState(StateType.FAILT);
		            ds.setMessage("数据库操作失败！");
		            return ds;
		        } else {
		        	DataSet result = this.executeDataset(sql, null, icityDataSource);
		            ds.setState(StateType.SUCCESS);
		            return result;
		        }		
			}
			
			
			//我的咨询详细展示
			public DataSet getMyzxXx_dq(ParameterSet pSet) {
		        DataSet ds=new DataSet();
				String sql="select id,title,substr(title,0,18) as short_title,content,to_char(write_date,'yyyy-mm-dd') createDate,deal_result,substr(trim(deal_result),0,18) deal_result_short,type from guestbook where 1=1 and type='2' and deal_result is not null and rownum<3 and write_date is not null order by write_date desc ";
		        int i = this.executeUpdate(sql, null, icityDataSource);
		        if (i == 0) {
		            ds.setState(StateType.FAILT);
		            ds.setMessage("数据库操作失败！");
		            return ds;
		        } else {
		        	DataSet result = this.executeDataset(sql, null, icityDataSource);
		            ds.setState(StateType.SUCCESS);
		            return result;
		        }		
			}
			//首页在线咨询标题展示
			public DataSet getIndexzxZx_dq(ParameterSet pSet) {
				String sql="select id,title,content,to_char(write_date,'MM-dd') createDate,deal_result,substr(trim(deal_result),0,46) deal_result_short,type from guestbook where 1=1 and type='2' ";
				//String user_id=(String) pSet.getParameter("user_id");
				int start = 0;
				int limit = 5;
				 sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
				 return this.executeDataset(sql, start, limit, null);
			//	 return this.executeDataset(sql,null);
			}

}
