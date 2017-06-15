package app.icity.interactive;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.icore.util.SecurityConfig;
import com.icore.util.Tools;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.UserInfo;
import com.inspur.util.DaoFactory;
import com.inspur.util.db.SqlCreator;

public class OnlineSurveyDao extends BaseJdbcDao {
	protected static Log _log = LogFactory.getLog(OnlineSurveyDao.class);
	protected static String icityDataSource = "icityDataSource";
	protected static String mssqlDataSource = "mssqlDataSource";
	public static final SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public OnlineSurveyDao() {
		this.setDataSourceName(icityDataSource);
	}

	public static OnlineSurveyDao getInstance() {
		return (OnlineSurveyDao)DaoFactory.getDao(OnlineSurveyDao.class.getName());
	}

	
	/**
	 * 舟山市问卷调查列表
	 * @author liuyq
	 * @param pset
	 * @return
	 * @since 2013-8-28
	 */
	public DataSet getSurveyList(ParameterSet pset) {
		
		String region = SecurityConfig.getString("WebRegion");
		
		String sql = "select ID,NAME,START_TIME,END_TIME,STATE from que_survey where state = '01' and region = ?";
		sql = SqlCreator.getSimpleQuerySql(pset, sql, this.getDataSource());
		Object[] params = new Object[]{region};
		int start = pset.getPageStart();
		int limit = pset.getPageLimit();
		return this.executeDataset(sql, start, limit, params);
	}

	public DataSet getSurveyByID(ParameterSet pSet) {
		String sql = "select * from que_survey where 1=1 ";
		sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
		return this.executeDataset(sql);
	}

	/**
	 * 保存外网提交的问卷答案
	 * @author liuyq
	 * @param pSet
	 * @param userInfo
	 * @return
	 */
	public DataSet saveAnswer(ParameterSet pSet, UserInfo userInfo) {
		//更新答案表
		String sql = "insert into QUE_ANSWER(ID,SURVEY_ID,SURVEY_NAME, USER_ID, USER_NAME, IP, PHONE, CONTENT, SUBMIT_TIME, EMAIL) VALUES(?,?,?,?,?,?,?,?,?,?)";
		String userId = "";
		String userName = "未登录用户";
		if ("xy".equals(SecurityConfig.getString("AppId"))){
		    userName = (String) pSet.get("userName");
		}else if(null != userInfo){
			userId = userInfo.getUserId();
			userName = userInfo.getUserName();
		}
		String answer_id = Tools.getUUID32();
		Object[] params = new Object[]{
				answer_id,
				pSet.get("ID"),
				pSet.get("NAME"),
				userId,
				userName,
				pSet.getRemoteAddr(),
				pSet.get("PHONE"),
				pSet.get("CONTENT").toString(),
				new Timestamp(Calendar.getInstance().getTimeInMillis()),
				pSet.get("EMAIL")
		};
		int result = this.executeUpdate(sql, params);
		DataSet ds = new DataSet();
		if(result == 1){
			ds.setState(new Byte("1"));
		}
		
		
		//更新统计结果表
		sql = "select * from QUE_STATISTICS WHERE SURVEY_ID = ?";
		DataSet list = this.executeDataset(sql,new Object[]{pSet.get("ID")});
		if(list.getTotal() > 0){      //第二次及以后更新数据表
			sql = "update QUE_STATISTICS SET CONTENT = ?";
			Object content = pSet.get("CONTENT");
			if(content instanceof JSONArray){
				JSONArray contentRes = (JSONArray)list.getJOData().get("CONTENT");
				JSONArray contentArr = (JSONArray)content;
				for(int i=0; i<contentArr.size(); i++){
					JSONObject jo = contentArr.getJSONObject(i);
					JSONObject joRes = contentRes.getJSONObject(i);
					int type = Integer.valueOf((String) jo.get("type"));
					if(type == 0 || type == 1){
						JSONArray options = jo.getJSONArray("option");
						float checkeds = 0f;
						for(int j=0; j<options.size(); j++){
							JSONObject option = options.getJSONObject(j);
							int checked = Integer.valueOf((String) option.get("checked"));
							if(checked == 0){
								int sum = Integer.valueOf( (String) joRes.getJSONArray("option").getJSONObject(j).get("sum") );
								joRes.getJSONArray("option").getJSONObject(j).put("sum", String.valueOf(sum+1) );
							}
							int sum = Integer.valueOf( (String) joRes.getJSONArray("option").getJSONObject(j).get("sum") );
							checkeds += sum;
						}
						options = joRes.getJSONArray("option");
						for(int j=0; j<options.size(); j++){
							JSONObject option = options.getJSONObject(j);
							int checked = Integer.valueOf((String) option.get("checked"));
							//if(checked == 0 && checkeds != 0){
								option.put("percent", String.valueOf(Integer.valueOf( options.getJSONObject(j).getString("sum") )/checkeds*100));
							//}
						}
					}else{
						Date date = new Date();
						String sql_que = "insert into QUE_ANSWER_OF_QUESTION(ID,SURVEY_ID,ANSWER_ID,QUESTION_ID,TITLE,ANSWER,CONTENT,CREATE_TIME) " +
								"values('"+Tools.getUUID32()+"','"+pSet.get("ID")+"','"+answer_id+"','"+jo.getString("id")+"','"+jo.getString("itemName")+"','"+jo.getString("answer")+"','"+jo.toString()+"',"+"systimestamp"+")";
						/*Object[] parameter = new Object[]{
								Tools.getUUID32(),
								pSet.get("ID"),
								answer_id,
								jo.getString("id"),
								jo.getString("itemName"),
								jo.getString("answer"),
								jo.toString(),
								"systimestamp"
								//date
							//	new Timestamp(Calendar.getInstance().getTimeInMillis())
						};*/
						Object[] parameter = new Object[]{};
						this.executeUpdate(sql_que, parameter);
					}
				}
				this.executeUpdate(sql, new Object[]{contentRes.toString()} );
			}
		}else{        //第一次插入
			sql = "insert into QUE_STATISTICS(ID, SURVEY_ID,CONTENT) VALUES(?,?,?)";
			Object content = pSet.get("CONTENT");
			if(content instanceof JSONArray){
				JSONArray contentArr = (JSONArray)content;
				for(int i=0; i<contentArr.size(); i++){
					JSONObject jo = contentArr.getJSONObject(i);
					int type = Integer.valueOf((String) jo.get("type"));
					if(type == 0 || type == 1){
						JSONArray options = jo.getJSONArray("option");
						float checkeds = 0f;
						for(int j=0; j<options.size(); j++){
							JSONObject option = options.getJSONObject(j);
							int checked = Integer.valueOf((String) option.get("checked"));
							if(checked == 0){
								checkeds += 1;
							}
						}
						for(int j=0; j<options.size(); j++){
							JSONObject option = options.getJSONObject(j);
							int checked = Integer.valueOf((String) option.get("checked"));
							if(checked == 0 && checkeds != 0){
								option.put("sum", "1");
								option.put("percent", String.valueOf( 1/checkeds*100 ));
							}else{
								option.put("sum", "0");
								option.put("percent", "0");
							}
						}
					}else{
						String sql_que = "insert into QUE_ANSWER_OF_QUESTION(ID,SURVEY_ID,ANSWER_ID,QUESTION_ID,TITLE,ANSWER,CONTENT,CREATE_TIME) " +
								"values('"+Tools.getUUID32()+"','"+pSet.get("ID")+"','"+answer_id+"','"+jo.getString("id")+"','"+jo.getString("itemName")+"','"+jo.getString("answer")+"','"+jo.toString()+"',"+"systimestamp"+")";
					//	String sql_que = "insert into QUE_ANSWER_OF_QUESTION(ID,SURVEY_ID,ANSWER_ID,QUESTION_ID,TITLE,ANSWER,CONTENT) values(?,?,?,?,?,?,?)";
						/*Object[] parameter = new Object[]{
								Tools.getUUID32(),
								pSet.get("ID"),
								answer_id,
								jo.getString("id"),
								jo.getString("itemName"),
								jo.getString("answer"),
								jo.toString()
						};*/
						Object[] parameter = new Object[]{};
						this.executeUpdate(sql_que, parameter);
					}
				}
			}
			this.executeUpdate(sql,new Object[]{Tools.getUUID32(),pSet.get("ID"),content.toString()});
		}
		return ds;
	}

	public DataSet getStatisticsByID(ParameterSet pSet) {
		String type = null;
		if(pSet.containsKey("type")){
			type = String.valueOf( pSet.get("type") );
			pSet.remove("type");
		}
		if("0".equals(type)){
			String sql = "select t1.content as CONTENT, t2.name as NAME from que_statistics t1 left join que_survey t2 on t1.survey_id = t2.id where 1=1 ";
			sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
			return this.executeDataset(sql);
		}else{
			String sql = "select t1.survey_id as SUEVEY_ID, T1.QUESTION_ID AS QUESTION_ID, TITLE, ANSWER, T2.USER_NAME, T2.SUBMIT_TIME from que_answer_of_question t1 left join que_answer t2 ON t1.answer_id = t2.id where 1=1 ";
			sql = SqlCreator.getSimpleQuerySql(pSet, sql, this.getDataSource());
			int start = pSet.getPageStart();
			int limit = pSet.getPageLimit();
			//sql=sql+"order by t1.create_time";
			return this.executeDataset(sql, start, limit);
		}
	}

	public DataSet validatePhone(ParameterSet pSet, UserInfo userInfo) {
		String sql = "select * from que_answer where survey_id = ? and phone=?";
		return this.executeDataset(sql, new Object[]{pSet.get("ID"), pSet.get("PHONE")});
	}
}
