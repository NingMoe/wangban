package hlj_qqheNew.app.icity.project;


import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.bean.UserInfo;
import com.icore.util.SecurityConfig;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>用户满意度评价服务接口</p>
 */
public class SatisfactionEvaluationCmd extends BaseQueryCommand {

    private Logger logger = LoggerFactory.getLogger(SatisfactionEvaluationCmd.class);

    private final static String APPROVAL_URL = SecurityConfig.getString("SatisfactionEvaluation_URL", "");

    public DataSet getBasicEvaluationMessage(ParameterSet pSet) {
        return SatisfactionEvaluationDao.getInstance().getBasicEvaluationMessage(pSet);
    }

    public DataSet insertNewEvaluation(ParameterSet pSet) {
        //获取客户端信息
        UserInfo userInfo = this.getUserInfo(pSet);
        if (null != userInfo) {
            pSet.setParameter("CreatorId", userInfo.getUid() + "");
        } else {
            pSet.setParameter("CreatorId", "");
        }
        //type为1时，是获取business_index中的业务进行评价
        if ("1".equals(pSet.get("type"))) {
            return SatisfactionEvaluationDao.getInstance().insertNewEvaluation1(pSet);
        }
        return SatisfactionEvaluationDao.getInstance().insertNewEvaluation(pSet);
    }

    public DataSet queryStarLevel(ParameterSet pSet) {
        return SatisfactionEvaluationDao.getInstance().queryStarLevel(pSet);
    }

    public DataSet getSbsxlist(ParameterSet pSet) {
        UserInfo userInfo = this.getUserInfo(pSet);
        if (null != userInfo) {
            pSet.setParameter("CreatorId", userInfo.getUid());
        } else {
            pSet.setParameter("CreatorId", "");
        }
        return SatisfactionEvaluationDao.getInstance().getSbsxlist(pSet);
    }

    /**
     * 办件评价
     *
     * @param pSet
     * @return
     */
    public Map<String, Object> getBjpjList(ParameterSet pSet) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            String serviceOrgId = (String)pSet.get("SERVICE_ORG_ID");

            // organ id 哪里获取
            String getURL = APPROVAL_URL + "/web/senate/querycount";

            if(StringUtils.isNotBlank(serviceOrgId)) {
                getURL += "?id=";
                getURL += serviceOrgId;
            }

            URL getUrl = new URL(getURL);
            connection = (HttpURLConnection) getUrl.openConnection();
            connection.connect();
            reader = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(),"UTF-8"));

            String lines;
            StringBuilder sb = new StringBuilder();
            while ((lines = reader.readLine()) != null) {
                sb.append(lines);
            }
            JSONArray jsonArr = JSONArray.fromObject(sb.toString());
            reader.close();
            // 断开连接
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("data", jsonArr);
            return result;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        } finally {
        	try {
        		if(reader!=null){
        			reader.close();
        		}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public DataSet getDeptEvaluation(ParameterSet pSet) {
        return SatisfactionEvaluationDao.getInstance().getBjpjList(pSet);
    }
    
    public DataSet getDeptEvaluationAll(ParameterSet pSet) {
        return SatisfactionEvaluationDao.getInstance().getBjpjListAll(pSet);
    }
    
    public DataSet QueryCount(ParameterSet pSet) {
        return SatisfactionEvaluationDao.getInstance().QueryCount(pSet);
    }

    public DataSet insertDeptEvaluation(ParameterSet pSet) {
        //获取客户端信息
        UserInfo userInfo = this.getUserInfo(pSet);
        if (null != userInfo) {
            pSet.setParameter("CreatorId", userInfo.getUid() + "");
            pSet.setParameter("BSR_NAME", userInfo.getUserName() + "");
        } else {
            pSet.setParameter("CreatorId", "");
            pSet.setParameter("BSR_NAME", "");
        }
        return SatisfactionEvaluationDao.getInstance().insertDeptEvaluation(pSet);
    }
    
    public DataSet insertEvaluation(ParameterSet pSet) {
        //获取客户端信息
        UserInfo userInfo = this.getUserInfo(pSet);
        if (null != userInfo) {
            pSet.setParameter("CreatorId", userInfo.getUid() + "");
        } else {
            pSet.setParameter("CreatorId", "");
        }
        return SatisfactionEvaluationDao.getInstance().insertEvaluation(pSet);
    }
    //插入办事指南评价
    public DataSet insertGuideEvaluation(ParameterSet pSet) {
    	//获取客户端信息
    	UserInfo userInfo = this.getUserInfo(pSet);
    	if (null != userInfo) {
    		pSet.setParameter("CreatorId", userInfo.getUid() + "");
    	} else {
    		pSet.setParameter("CreatorId", "");
    	}
    	return SatisfactionEvaluationDao.getInstance().insertGuideEvaluation(pSet);
    }
    
    public DataSet getPjPercent(ParameterSet pSet) {
        return SatisfactionEvaluationDao.getInstance().getPjPercent(pSet);
    }
    
    public DataSet getBjPjList(ParameterSet pSet) {
        return SatisfactionEvaluationDao.getInstance().getBjPjList(pSet);
    }
    
    public DataSet getBjAndZnPjPercent(ParameterSet pSet) {
        return SatisfactionEvaluationDao.getInstance().getBjAndZnPjPercent(pSet);
    }
}
