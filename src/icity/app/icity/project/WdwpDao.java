package app.icity.project;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import com.alibaba.fastjson.JSONArray;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
//import com.alibaba.fastjson.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.icore.http.util.HttpUtil;
import com.icore.util.DaoFactory;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.dzzw.oauth.util.Base64.OutputStream;
import com.inspur.hsf.service.rpc.bootstrap.ServiceConfig;
import com.inspur.hsf.service.rpc.bootstrap.ServiceReference;
import com.inspur.util.PathUtil;
import com.inspur.util.SecurityConfig;
import com.inspur.util.Tools;
import com.inspur.util.db.DbHelper;

import action.bsp.uploadify;
import app.icity.sync.PowerBaseInfoQZCmd;
import app.icity.sync.UploadUtil;
import app.uc.GetUserMapDao;
import app.util.RestUtil;

public class WdwpDao extends BaseJdbcDao {

	protected static Log _log = LogFactory.getLog(WdwpDao.class);
	protected static String icityDataSource = "icityDataSource";

	public WdwpDao() {
		this.setDataSourceName(icityDataSource);
	}

	public static WdwpDao getInstance() {
		return (WdwpDao) DaoFactory.getDao(WdwpDao.class.getName());
	}

	public DataSet getNetDiskList(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String ucid = (String) pSet.getParameter("ucid");
			String strStart = pSet.getParameter("page").toString();
			String strLimit = pSet.getParameter("limit").toString();
			JSONObject json = new JSONObject();

			String diskHome = HttpUtil.formatUrl(SecurityConfig
					.getString("NetDiskAddress"));			
			if (StringUtils.isNotBlank(diskHome)) {
				diskHome = diskHome.substring(7, diskHome.lastIndexOf(":"));
			}
			ServiceConfig address = new ServiceConfig();
			address.setProtocol("netty");
			address.setHost(diskHome);
			address.setPort(1651);
			address.setServiceName("com.inspur.dc.doc.api.IDocDiskDomain");
			ServiceReference docDiskDomain = new ServiceReference(address);
			try {
				Object result = docDiskDomain.invoke("getDocDiskByUid",
						new Object[] { ucid });
				List<Map<String, Object>> list = (List<Map<String, Object>>) result;
				String disk_id = list.get(0).get("disk_id").toString();
				Map<String, String> map = new HashMap<String, String>();
				Map<String, String> limit = new HashMap<String, String>();
				map.put("uid", ucid);
				map.put("pid", "0");
				map.put("disk_id", disk_id);
				limit.put("_pstart", strStart);
				limit.put("_psize", strLimit);
				ServiceConfig doc_address = new ServiceConfig();
				doc_address.setProtocol("netty");
				doc_address.setHost(diskHome);
				doc_address.setPort(1651);
				doc_address
						.setServiceName("com.inspur.dc.doc.api.IDocInfoDomain");
				ServiceReference docDiskDomain_doc = new ServiceReference(
						doc_address);
				Object docResult = docDiskDomain_doc.invoke("listByFolderId",
						new Object[] { map, limit });
				List<Map<String, Object>> docList = (List<Map<String, Object>>) docResult;

				String downurl = HttpUtil.formatUrl(SecurityConfig
						.getString("NetDiskDownloadAddress"));
				// 获取文件的总数total
				limit.put("_pstart", "0");
				limit.put("_psize", "10000");
				Object docTotal = docDiskDomain_doc.invoke("listByFolderId",
						new Object[] { map, limit });
				List<Map<String, Object>> dList = (List<Map<String, Object>>) docTotal;
				int total = dList.size();
				json.put("net", downurl);
				json.put("doc", docList);
				ds.setData(Tools.jsonToBytes(json));
				ds.setTotal(total);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}
	public DataSet deleteNetDiskDoc(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			ds = delBusinessAttach(pSet);
			//将从网盘删除的代码屏蔽
			/*String ucid = (String) pSet.getParameter("ucid");
			int doc_id = (Integer) pSet.getParameter("doc_id");
			String fileids = String.valueOf(doc_id);
			String disk_id = String.valueOf(pSet.getParameter("disk_id"));
			Map map = new HashMap();
			map.put("uid", ucid);
			map.put("disk_id", disk_id);
			if (fileids != null && !"".equals(fileids))
				map.put("fileids", fileids);			
			String diskHome = HttpUtil.formatUrl(SecurityConfig
					.getString("NetDiskAddress"));
			if (StringUtils.isNotBlank(diskHome)) {
				diskHome = diskHome.substring(7, diskHome.lastIndexOf(":"));
			}
			ServiceConfig address = new ServiceConfig();
			address.setProtocol("netty");
			address.setHost(diskHome);
			address.setPort(1651);
			address.setServiceName("com.inspur.dc.doc.api.IDocInfoDomain");
			ServiceReference docDiskDomain = new ServiceReference(address);
			// 逻辑删除
			// Object result = docDiskDomain.invoke("logicDeleteFilesById",new
			// Object[]{map});
			// 物理删除
			Object result = docDiskDomain.invoke("completelyDeleteById",
					new Object[] { map });
			int deleteResult = Integer.parseInt(result.toString());
			System.out.println(result);			
			if (deleteResult == 1) {
				ds.setState(StateType.SUCCESS);
				ds.setMessage("删除成功！");			
			} else {
				ds.setState(StateType.FAILT);
				ds.setMessage("数据库操作失败！");
			}*/
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return ds;
	}

	/**
	 * 从附件表删除附件
	 * @param pSet
	 * @return
	 */
	public DataSet delBusinessAttach(ParameterSet pSet){
		DataSet ds = new DataSet();		
		try{
			String ucid = (String)pSet.get("ucid");
			String docid = (Integer)pSet.getParameter("doc_id")+"";
			String sb = "delete from business_attach where ucid=? and docid=? ";
			int i = this.executeUpdate(sb, new Object[]{
					ucid,
					docid
			},icityDataSource);
			if(i>0){
				ds.setState(StateType.SUCCESS);
				ds.setMessage("数据库删除成功！");	
			}else{
				ds.setState(StateType.FAILT);
				ds.setMessage("数据库删除失败！");	
			}
		}catch(Exception e){
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		}
		return ds;
	}
	
	/**
	 * 查询资料关联事项
	 * @param pSet
	 * @return
	 */
	public DataSet getRelatedItemAttach(ParameterSet pSet){
		DataSet ds = new DataSet();		
		try{
			String sblsh = (String)pSet.get("sblsh");
			int start = pSet.getPageStart();//Integer.parseInt(pSet.getParameter("start").toString());
			int limit = pSet.getPageLimit();//Integer.parseInt(pSet.getParameter("limit").toString());
			//去除最后一个逗号
			if(sblsh != null && !"".equals(sblsh)){
				sblsh = sblsh.substring(0,sblsh.length()-1);
				sblsh="'"+sblsh.replaceAll(",", "','")+"'";
				String sql = "select t.sxmc,t.sxbm from business_index t where t.sblsh in ("+sblsh+") group by t.sxmc,t.sxbm";
				ds = this.executeDataset(sql,start, limit,new Object[]{});
			}else{
				ds.setState(StateType.FAILT);
				ds.setMessage("申办流水号为空！");
			}
		}catch(Exception e){
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		}
		return ds;
	}
	/**
	 * 根据上传文件获取与其相似文件
	 * @return
	 */
	public DataSet getSimilarFile(ParameterSet pSet){
		DataSet ds = new DataSet();		
		try{
			//调用省级接口查询省级资料库根据上传文件获取与其相似文件
			if ("1".equals(SecurityConfig.getString("useBusinessAttachInterface"))) {
				PowerBaseInfoQZCmd powerBaseInfoQZCmd = new PowerBaseInfoQZCmd();
				JSONObject js = powerBaseInfoQZCmd.getSimilarFile(pSet);
				JSONArray data = js.getJSONArray("js");
				ds.setTotal(data.size());
				ds.setRawData(data);
				ds.setState(StateType.SUCCESS);
			}else {
				String uid = (String)pSet.get("UID");
				String name = (String)pSet.get("name"); 
				String sb = "select * from business_attach where ucid=? ";
				       sb += " and name like '%"+name+"%'";
				ds = this.executeDataset(sb, new Object[]{
						uid
				});
			}
		}catch(Exception e){
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		}
		return ds;
	}
	
	/**
	 * 如果使用的资料库的文件首先判断本地资料库是否存在此文件，
	 * 有则返回此文件，无则写入此文件同时存入本地网盘
	 * 
	 * @return
	 */
	public DataSet getBdBusinessAttach(ParameterSet pSet){
		DataSet ds = new DataSet();		
		try{
			String ucid = (String) pSet.get("ucid");
			String tyId = (String) pSet.get("tyid");
			String sb = "select * from business_attach where ucid=? and tyid = ?";
			ds = this.executeDataset(sb, new Object[]{
					ucid,
					tyId
			});
			
			if(ds.getTotal()>0){
				ds.setState(StateType.SUCCESS);
				ds.setMessage("该附件已存储！");
			}else{
				PowerBaseInfoQZCmd powerBaseInfoQZCmd = new PowerBaseInfoQZCmd();
				JSONObject jso = powerBaseInfoQZCmd.getFileByTyid(tyId);
				JSONArray jsa = jso.getJSONArray("js");
				JSONObject js = JSONObject.fromObject(jsa.get(0));
				
			//获取省资料库接口返回的信息
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
			String date = df.format(new Date());// new Date()为获取当前系统时间
			String type = (String)js.get("TYPE");
			String state = "1";
			String docid = (String) js.get("DOCID");
			String downloadurl = (String) js.get("downloadurl");
			String name = (String) js.get("NAME");
			String key = (String) js.get("NAME");
			//http://localhost:8080/icity/bsp/uploadify?action=downloadagent4wp&path=126079&name=%E4%B8%8A%E4%BC%A0.txt
			String nameurl = java.net.URLEncoder.encode(name,"UTF-8");
			downloadurl = downloadurl + "?action=downloadagent4wp&path="+docid+"&name="+nameurl;
			Map<String, String> map = new HashMap<String, String>();
			map.put("action", "downloadagent4wp");
			map.put("path", docid);
			map.put("name", name);
			InputStream in = RestUtil.postDataInput(downloadurl, map);
			//将他地网盘下载的文件暂存在temp目录下，名称为省资料库的源名称
			String fileUri = PathUtil.getTempPath() + key;
			File File = new File(fileUri);
			FileOutputStream out = new FileOutputStream(File);
			byte[] b = new byte[1024];
			int n;
			while ((n = in.read(b)) != -1) {
				out.write(b, 0, n);
			}
			//将下载的文件传值本地网盘
			Map<String, String> params = new HashMap<String, String>();
			params.put("type", "doc");
			params.put("folder_name", "//");
			params.put("uid", ucid);
			String uploadUrl = SecurityConfig.getString("NetDiskAddress");
			String scc = UploadUtil.startUploadService(params, fileUri,
					uploadUrl);
			JSONObject o = JSONObject.fromObject(scc);
			String docwid = o.getString("docid");
			String id = Tools.getUUID32();
			String sql = "insert into business_attach (id,tyid,type,sblsh,url,name,yname,docid,ucid,uploadtime,remark,state) " +
			"values (?,?,?,?,?,?,?,?,?,?,?,?)";
			int i = this.executeUpdate(sql,new Object[]{
						id,
						tyId,
						type,
						"",
						docwid,
						name,
						key,
						docwid,
						ucid,
						date,
						"",
						state
				});
				if(i>0){
					//省资料库记录本地存放信息
					pSet.put("WebRegion", SecurityConfig.getString("WebRegion"));
					powerBaseInfoQZCmd.setWebRegions(pSet);
					File.delete();
					String sq = "select * from business_attach where id = ?";
					ds = this.executeDataset(sq, new Object[]{
							id
					});
					ds.setState(StateType.SUCCESS);
				}else{
					ds.setState(StateType.FAILT);
				}	
			}
			
		}catch(Exception e){
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		}
		return ds;
	}

	/**
	 * 对上传文件重命名
	 * @return
	 */
	public DataSet getFileRename(ParameterSet pSet){
		DataSet ds = new DataSet();		
		try{
			String uid = (String)pSet.get("UID");
			String name = (String)pSet.get("name"); 
			String sb = "select 1 from business_attach where ucid=? and name= ?";
			ds = this.executeDataset(sb, new Object[]{
					uid,
					name
			});
			int total = ds.getTotal();
			if(total>0){
				name = "("+(total)+")"+name;
			}
			ds.setState(StateType.SUCCESS);
			ds.setRawData(name);
		}catch(Exception e){
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		}
		return ds;
	}
	
	/**
	 * 上传到网盘的材料写入数据库
	 * @param pSet
	 * ID	VARCHAR2(50)	N			ID主键
	 * TYPE	VARCHAR2(10)	Y			0在线办理提交1业务中心提交
	 * SBLSH	CLOB	Y			业务关联申办流水号集合
	 * NAME	VARCHAR2(50)	Y			附件名称（在服务器上的名称）
	 * YNAME	VARCHAR2(100)	Y			上传源文件名称
	 * DOCID	VARCHAR2(10)	Y			网盘文件ID
	 * UCID	VARCHAR2(10)	Y			用户ID
	 * UPLOADTIME	VARCHAR2(50)	Y			上传日期
	 * REMARK	VARCHAR2(500)	Y			备注
	 * STATE	VARCHAR2(1)	Y			0可删除1不可删除
	 *
	 * @return
	 */
	public DataSet setBusinessAttach(ParameterSet pSet){
		DataSet ds = new DataSet();		
		try{
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
			String date = df.format(new Date());// new Date()为获取当前系统时间
			String type = (String)pSet.get("TYPE");
			String state = (String)pSet.get("STATE");
			String uid = (String)pSet.get("UID");
			JSONObject jo = JSONObject.fromObject(pSet.get("obj"));
			//调用省级接口将上传到网盘的材料信息写入省数据库
			if ("1".equals(SecurityConfig.getString("useBusinessAttachInterface"))) {
				pSet.put("WebRegion", SecurityConfig.getString("WebRegion"));
				PowerBaseInfoQZCmd powerBaseInfoQZCmd = new PowerBaseInfoQZCmd();
				DataSet dst = powerBaseInfoQZCmd.setBusinessAttach(pSet);
				System.out.print("dst:"+dst.toString());
				pSet.put("tyId", dst.getRawData().toString());
				setBdBusinessAttach(pSet);
			}else {
				String sb = "select 1 from business_attach where ucid=? and docid = ?";
				ds = this.executeDataset(sb, new Object[]{
						uid,
						jo.getJSONObject("scc").getString("docid")
				});
				if(ds.getTotal()>0){
					ds.setState(StateType.SUCCESS);
					ds.setMessage("该附件已存储！");
				}else{
					pSet.put("name", jo.getString("name"));
					DataSet newobj = getFileRename(pSet);
					jo.put("yname", newobj.getRawData().toString());
					String sql = "insert into business_attach (id,type,sblsh,url,name,yname,docid,ucid,uploadtime,remark,state) " +
					"values (?,?,?,?,?,?,?,?,?,?,?)";
					int i = this.executeUpdate(sql,new Object[]{
							Tools.getUUID32(),
							type,
							"",
							jo.getString("url"),
							jo.getString("name"),
							jo.get("yname"),
							jo.getJSONObject("scc").getString("docid"),
							uid,
							date,
							"",
							state
					});
					if(i>0){
						ds.setState(StateType.SUCCESS);
					}else{
						ds.setState(StateType.FAILT);
					}				
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		}
		return ds;
	}
	//将资料库历史资料推送到省数据库
	public DataSet sendData() {
		DataSet ds = new DataSet();
		try {
			for(int n=0;n<400;n++){
			String sql = "select * from business_attach where tyid is null and  ucid in (select user_id from uc_user_map) and rownum<=200";
			ds = this.executeDataset(sql, new Object[]{
			});
			JSONArray dsAll = ds.getJAData();
			JSONObject obj;
			//存放数据的js串，接口中的data参数
			JSONObject data = new JSONObject();
			for (int i = 0; i < ds.getTotal(); i++) {
				try {
				obj = JSONObject.fromObject(dsAll.get(i));
				String ucid = obj.getString("UCID");
				String uuid = GetUserMapDao.getInstance().GetMapid(ucid).getRecord(0).getString("USER_ID_MAP");
				data.put("date", obj.get("UPLOADTIME"));
				data.put("type", obj.get("TYPE"));
				data.put("state", obj.get("STATE"));
				data.put("uuid", uuid);
				data.put("WebRegion", SecurityConfig.getString("WebRegion"));
				data.put("url", obj.get("URL"));
				data.put("name", obj.get("NAME"));
				data.put("yname", obj.get("YNAME"));
				data.put("docid", obj.get("DOCID"));
				try {
				String url = HttpUtil.formatUrl(SecurityConfig
						.getString("BusinessAttachUrl")+"/sendData");
				Map<String, String> map = new HashMap<String, String>();
				map.put("data", data.toString());
				Object ret = RestUtil.postData(url, map);
				JSONObject json = JSONObject.fromObject(ret);
				if (json.getString("state").equals("1")) {
					String tyid = json.getString("tyId");
					String sq = "update business_attach set tyid=? where id=?";
					int j = this.executeUpdate(sq,new Object[]{
							tyid,obj.get("ID")
					});
					if(j>0){
						ds.setState(StateType.SUCCESS);
					}else{
						ds.setState(StateType.FAILT);
					}	
				}else {
					ds.setRawData(json);
					ds.setState(StateType.FAILT);
					ds.setMessage("资料提交失败sendData！");
				}
			}catch (Exception e) {
				e.printStackTrace();
				ds.setState(StateType.FAILT);
				ds.setMessage("资料提交失败submitSL！");
				continue;
			}
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
			} //循环结束
			System.err.println("内层循环一次结束，二百条数据过去");
			}
		} catch (Exception e){
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		}
		return ds;
	}
	
	//调用省级接口将写入省资料库的信息写入本地数据库
	public DataSet setBdBusinessAttach(ParameterSet pSet){
		DataSet ds = new DataSet();		
		try{
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
			String date = df.format(new Date());// new Date()为获取当前系统时间
			String type = (String)pSet.get("TYPE");
			String state = (String)pSet.get("STATE");
			String uid = (String)pSet.get("UID");
			JSONObject jo = JSONObject.fromObject(pSet.get("obj"));
			String tyId = (String) pSet.get("tyId");
			String sb = "select 1 from business_attach where ucid=? and tyid = ?";
			ds = this.executeDataset(sb, new Object[]{
					uid,
					tyId
			});
			if(ds.getTotal()>0){
				ds.setState(StateType.SUCCESS);
				ds.setMessage("该附件已存储！");
			}else{
				pSet.put("name", jo.getString("name"));
				DataSet newobj = getFileRename(pSet);
				jo.put("yname", newobj.getRawData().toString());
				String sql = "insert into business_attach (id,tyid,type,sblsh,url,name,yname,docid,ucid,uploadtime,remark,state) " +
				"values (?,?,?,?,?,?,?,?,?,?,?,?)";
				int i = this.executeUpdate(sql,new Object[]{
						Tools.getUUID32(),
						tyId,
						type,
						"",
						jo.getString("url"),
						jo.getString("name"),
						jo.get("yname"),
						jo.getJSONObject("scc").getString("docid"),
						uid,
						date,
						"",
						state
				});
				if(i>0){
					ds.setState(StateType.SUCCESS);
				}else{
					ds.setState(StateType.FAILT);
				}				
			}
			
		}catch(Exception e){
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		}
		return ds;
	}
	
	
	
	
	/**
	 * 从附件表获取当前用户下的材料列表 在线办理调用
	 * @param pSet
	 * @return
	 */
	public DataSet getAttachList(ParameterSet pSet){
		DataSet ds = new DataSet();
		int start = Integer.parseInt(pSet.getParameter("start").toString());
		int limit = Integer.parseInt(pSet.getParameter("limit").toString());
		String ucid = (String)pSet.get("ucid");
		String title = (String)pSet.get("title");
		try{
			//调用省级接口查询省级资料库获取当前用户下的资料
			if ("1".equals(SecurityConfig.getString("useBusinessAttachInterface"))) {
				PowerBaseInfoQZCmd powerBaseInfoQZCmd = new PowerBaseInfoQZCmd();
				DataSet dst = powerBaseInfoQZCmd.getAttachList(pSet);
				ds.setTotal(dst.getTotal());
				ds.setRawData(dst.getRawData());
			}else {
				String sb = "select * from business_attach where ucid=? ";
				if (StringUtils.isNotEmpty(title)) {
					sb += " and YNAME like '%"+title+"%'";
				}
				sb +=" order by uploadtime desc,id";
				ds = this.executeDataset(sb, start, limit, new Object[]{ucid});	
			}
		}catch(Exception e){
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		}
		return ds;
	}
	public DataSet setEvaluationAttach(ParameterSet pSet){
		DataSet ds = new DataSet();		
		try{
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
			String date = df.format(new Date());// new Date()为获取当前系统时间
			String state = (String)pSet.get("STATE");
			String uid = (String)pSet.get("UID");
			String sblsh = (String)pSet.get("SBLSH");
			JSONObject jo = JSONObject.fromObject(pSet.get("obj"));
			String sb = "select 1 from evaluation_attach where ucid=? and docid = ?";
			ds = this.executeDataset(sb, new Object[]{
					uid,
					jo.getJSONObject("scc").getString("docid")
			});
			if(ds.getTotal()>0){
				ds.setState(StateType.SUCCESS);
				ds.setMessage("该附件已存储！");
			}else{
				String sql = "insert into evaluation_attach (id,sblsh,name,yname,docid,ucid,uploadtime,remark,state) " +
				"values (?,?,?,?,?,?,?,?,?)";
				int i = this.executeUpdate(sql,new Object[]{
						Tools.getUUID32(),
						sblsh,
						jo.getString("url"),
						jo.getString("name"),
						jo.getJSONObject("scc").getString("docid"),
						uid,
						date,
						"",
						state
				});
				if(i>0){
					ds.setState(StateType.SUCCESS);
				}else{
					ds.setState(StateType.FAILT);
				}				
			}			
		}catch(Exception e){
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
		}
		return ds;
	}
}
