package hlj_qqheNew.app.icity.project;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import net.sf.json.JSONObject;

import com.icore.util.DaoFactory;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.SecurityConfig;

public class ZxyyDao extends BaseJdbcDao{
	    protected static String yysqlDataSource = "yysqlDataSource";
	    protected static String NewEcGapsqlDataSource = "NewEcGapsqlDataSource";
	    protected static String mssqlDataSource = "mssqlDataSource";
	    private HashMap<String, Object> Msgmap = new HashMap<String, Object>();

	    public static ZxyyDao getInstance() {
	    	return DaoFactory.getDao(ZxyyDao.class.getName());
	    }
	    public ZxyyDao() {
	        this.setDataSourceName(yysqlDataSource);
	    }
	    
	    private static String vcode = "1";
		public static void createRandomVcode() {
			// 验证码
			vcode = "";
			for (int i = 0; i < 6; i++) {
				vcode = vcode + (int) (Math.random() * 9);
			}

		}

			
		public DataSet getQueryDept(ParameterSet pSet) {
			DataSet ds = new DataSet();
			String sql="SELECT distinct OrgName as ywname FROM YYTimeConfig where OrgName is not NULL order by OrgName ";
			try {
				 ds = this.executeDataset(sql, null, yysqlDataSource);
				 ds.setState(StateType.SUCCESS);
			} catch (Exception e) {
				ds.setState(StateType.FAILT);
				ds.setMessage(e.getMessage());
				e.printStackTrace();
			}
            return ds;
		}
		
		public DataSet getQueryDeptTime(ParameterSet pSet) {
			DataSet ds = new DataSet();
			String orgName = pSet.getParameter("orgName").toString();
			String type = pSet.getParameter("type").toString();
			String sql="select yystime,yyetime,yymax,ywno,ywname from yytimeconfig where isenbled='是' and orgname='"+orgName+"' and ywname like '%"+type+"%' ";
			try {
				 ds = this.executeDataset(sql, null, yysqlDataSource);
				 ds.setState(StateType.SUCCESS);
			} catch (Exception e) {
				ds.setState(StateType.FAILT);
				ds.setMessage(e.getMessage());
				e.printStackTrace();
			}
            return ds;
		}
		
		public DataSet getQueryDeptDate(ParameterSet pSet) {
			DataSet ds = new DataSet();
			String sql="select  top 5  *  from AttendanceCalendar t where iswork='1' and tdate>=replace(convert(varchar,getdate(),111),'/','-') order by  tdate ";
			try {
				 ds = this.executeDataset(sql, null, NewEcGapsqlDataSource);
				 ds.setState(StateType.SUCCESS);
			} catch (Exception e) {
				ds.setState(StateType.FAILT);
				ds.setMessage(e.getMessage());
				e.printStackTrace();
			}
            return ds;
		}
		
		public DataSet submitYy(ParameterSet pSet) {
			DataSet ds = new DataSet();
			String stime = pSet.getParameter("stime").toString();
		    String etime = pSet.getParameter("etime").toString();
		    String ywname = pSet.getParameter("ywname").toString();
		    String ywno = pSet.getParameter("ywno").toString();
		    String sedate = pSet.getParameter("sedate").toString();
		    String deptName = pSet.getParameter("deptName").toString();
		    String phone = pSet.getParameter("phone").toString();
		    String icard = pSet.getParameter("icard").toString();
		    String message = pSet.getParameter("message").toString();
		    //对日期是当天的预约进行时间判断  防止下午预约上午
		    try{
		    	int c = compareDateTime(sedate+" "+stime+":00");
			    if(c==1){
			    	boolean flage = isTrue(message,phone);
				    if(flage){
					    //检查可预约最大值
						String selectMax = "select yymax from yytimeconfig  where YYSTIME='"+stime+"'"+" and YYETIME= '"+etime+"'"+" and YWNO='"+ywno+"'";
						DataSet dsMax = this.executeDataset(selectMax, null,yysqlDataSource);
						JSONObject ojMax = dsMax.toJSONObject().getJSONArray("data").getJSONObject(0); 
						String maxNum =  ojMax.getString("YYMAX"); 
						int intMaxNum = Integer.parseInt(maxNum);
						//检查当前预约数
						String selectNum = "select count(*) as yynum from yyyw  where YYTIMEMIN='"+stime+"'"+" and YYTIMEMAX='"+etime+"'"+" and YYYWNO='"+ywno+"' and YYDATE= '"+sedate+"'";
						DataSet dsNum = this.executeDataset(selectNum, null,yysqlDataSource);
						JSONObject ojNum = dsNum.toJSONObject().getJSONArray("data").getJSONObject(0); 
						String Num =  ojNum.getString("YYNUM"); 
						int intNum = Integer.parseInt(Num);
						//检查该号码是否已预约
						String selectWhether="select * from yyyw  where YYTIMEMIN ='"+stime+"'"+" and YYTIMEMAX='"+etime+"'" +" and YYYWNO='"+ywno+"' and YYDATE='"+sedate+"'"+" and YYPERIDNO='"+icard+"'";
						DataSet dsWhether=this.executeDataset(selectWhether, null,yysqlDataSource);
						int WhetherNum=dsWhether.getTotal();
						//是否超过最大可预约数
						if(intMaxNum>intNum){
							//是否已预约
							if(WhetherNum==0){
								String insertsql="insert into yyyw(YYDATE,YYTIMEMIN,YYTIMEMAX,YYYWNO,YYYW,YYPERIDNO,YYPERNAME,CREATEDATE,DEPTNAME,PHONE)values('"+
										sedate+"','"+stime+"','"+etime+"','"+ywno+"','"+ywname+"','"+icard+"','',"+"getdate()"+",'"+deptName+"','"+phone+"')";
								int k = this.executeUpdate(insertsql, null,yysqlDataSource);
								//预约是否成功
								if(k==0){
									ds.setState(StateType.FAILT);
									ds.setMessage("数据库操作失败！");
								}else {
									ds.setState(StateType.SUCCESS);
									ds.setMessage("预约成功！请准时到现场取号，办理相关业务。");
								}
							}else{
								ds.setState(StateType.FAILT);
								ds.setMessage("您在当前时间段内，已预约过此事项。请勿重复预约");
							}
						}else{
							ds.setState(StateType.FAILT);
							ds.setMessage("当前时间段预约数已满");
						}
		            }else{
		            	ds.setState(StateType.FAILT);
						ds.setMessage("短信验证码验证失败");
				    }
			    }else{
			    	ds.setState(StateType.FAILT);
					ds.setMessage("预约失败！您的预约时间不符。");
			    }
		    }catch(Exception e){
		    	ds.setState(StateType.FAILT);
				ds.setMessage("预约失败！"+e.getMessage());
				e.printStackTrace();
		    }
            return ds;
		}
		
		public DataSet sendPhoneCode(ParameterSet pSet) {
			Date date = new Date();
			DataSet ds = new DataSet();
			String isOn = SecurityConfig.getString("messageAudit");
			if(isOn.equals("on")){
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				DateFormat format2 = new SimpleDateFormat("HH:mm:ss");
				String time = format.format(date);
				String time1 = format1.format(date);
				String time2 = format2.format(date);
				ZxyyDao.createRandomVcode();
				String mescode = vcode;
				String phone = (String) pSet.getParameter("phone");
				Msgmap.remove(phone);
				Msgmap.put(phone, mescode);
				String sql = "insert into InBox(mbno,Msg,ArriveDate,ArriveDateTime,ArriveTime) values ('"+ phone+ "','您的短信验证码为:"+ mescode+ "','"+ time+ "','"+ time1+ "','" + time2 + "');";
				int i = this.executeUpdate(sql, null, mssqlDataSource);
				if (i == 0) {
					ds.setState(StateType.FAILT);
					ds.setMessage("验证码发送失败！");
				} else {
					ds.setState(StateType.SUCCESS);
					ds.setMessage("验证码已发送！");
				}
			}else{
				ds.setState(StateType.SUCCESS);
				ds.setMessage("短信验证功能已关闭！请继续操作。");
			}
			return ds;
		}
		
		// 比对验证码
		public boolean isTrue(String message,String phone) {
			String isOn = SecurityConfig.getString("Msgcode");
			if (isOn.equals("on")) {
				return true;
			}
			vcode = (String)Msgmap.get(phone);
			if (message.equals(vcode)) {
				Msgmap.remove(phone);
				return true;
			} else {
				return false;
			}
		}
		
		public int compareDateTime(String yyDT) {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    	String now = dateFormat.format(new Date());
			try {
				Date dateTimeyyDT = dateFormat.parse(yyDT);
				Date dateTimenow = dateFormat.parse(now);
		    	int compareToBefore = dateTimeyyDT.compareTo(dateTimenow); 
		    	return compareToBefore;
			} catch (ParseException e) {
				e.printStackTrace();
				return -1;
			}
		}

}
