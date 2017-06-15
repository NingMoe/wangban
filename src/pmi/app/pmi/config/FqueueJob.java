package app.pmi.config;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


import com.google.code.fqueue.FQueue;
import com.icore.log.Logger;
import com.inspur.base.BaseQueryCommand;
import com.inspur.util.Command;
import com.inspur.util.PathUtil;
import com.inspur.util.Tools;
import core.queue.FqueueUtil;
import core.queue.QueueObj;
import core.util.CommonUtils;

public class FqueueJob extends BaseQueryCommand {
	private static Logger log = Logger.getLogger(FqueueJob.class);
	private final String writeType="file";//写入方式，用来判断将日志写入文件还是写入数据库
	private final int maxnum=100;//每次批量写入日志文件的数目
	private FileOutputStream fos=null;//文件输出流
	private BufferedOutputStream buff=null;//缓存输出流

	public void execute(){
		try {
			FQueue queue = FqueueUtil.getInstance().getfQueue();
			List<QueueObj> perList=new ArrayList<QueueObj>();//每次存入的日志文件			
			int pernum=0;//当前批次已存入日志文件数目
			while (queue != null && queue.size() > 0 && pernum < maxnum) {
				QueueObj obj = FqueueUtil.getInstance().poll();
				if (obj != null) {					
					perList.add(obj);
					pernum++;
				}
				//队列为空，或当前批次已存满
				if(queue.size()==0||pernum==maxnum){
					if("file".equals(writeType)){
						this.writeFile(perList);					
					}else{
						this.writeData(perList);
					}
					perList.clear();
					pernum=0;
				}
			}			
		} catch (Exception e) {
			log.error("任务执行异常：" + Tools.formatDate(new Date(), CommonUtils.YYYY_MM_DD_HH_mm_SS));
			e.printStackTrace();
		}finally{			
			try {
				if(buff!=null){
					buff.close();
				}
				if(fos!=null){
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}
	}
		
	//写入每批日志
	private void writeFile(List<QueueObj> list){
		try{
			StringBuffer exchange=new StringBuffer();//交换日志
			StringBuffer statistics=new StringBuffer();//交换日志
			
			if(list!=null){
				for(int i=0;i<list.size();i++){			
					QueueObj obj=list.get(i);
					if(obj!=null){
						String type = obj.getType();
						byte[] content = obj.getContent();
						JSONObject jlog=Tools.bytesToJson(content);						
						if(jlog!=null){
							if("EXCHANGERESTLOG".equals(type)){							
								exchange.append("登录用户名:"+jlog.getString("loginName")+",");
								exchange.append("部门ID:"+jlog.getString("dept")+",");
								exchange.append("类名:"+jlog.getString("cclass")+",");
								exchange.append("方法名:"+jlog.getString("method")+",");
								exchange.append("\r\n参数列表:"+jlog.getString("param")+",\r\n");
								exchange.append("是否成功:"+("1".equals(jlog.getString("issuccess"))?"是":"否")+",");
								exchange.append("描述:"+jlog.getString("describe")+",");
								exchange.append("创建时间:"+jlog.getString("ctime")+",");
								exchange.append("登录密码:"+jlog.getString("key")+"\r\n\r\n");
							}else if("IDSPSTATISTICS".equals(type)){
								statistics.append("行动:"+jlog.getString("action")+",");
								statistics.append("方法模式:"+jlog.getString("method")+",");
								statistics.append("消费者:"+jlog.getString("consumer")+",");
								statistics.append("提供者:"+jlog.getString("provider")+",");
								statistics.append("创建时间:"+jlog.getString("createdDate")+",");
								statistics.append("最后时间:"+jlog.getString("lastDate")+",");
								statistics.append("成功:"+((jlog.getInt("success")==1)?"是":"否")+",");
								statistics.append("失败:"+((jlog.getInt("failure")==1)?"是":"否")+",");
								statistics.append("总共使用时间:"+jlog.getInt("totalusetime")+",");
								statistics.append("最大使用时间:"+jlog.getInt("maxusetime")+",");
								statistics.append("最小使用时间:"+jlog.getInt("minusetime")+"\r\n\r\n");
							}
						}
					}						
				}
				//写入接口日志
				String exchangeRestLog=exchange.toString();
				if(exchangeRestLog.length()>0){
					String exchangeType="exchange_rest_log";
					this.write(exchangeRestLog,exchangeType);
				}				
				//写入操作日志
				String idspStatistics=statistics.toString();
				if(idspStatistics.length()>0){
					String idspType="idsp_statistics";
					this.write(idspStatistics,idspType);
				}				
			}
		}catch(Exception e){
			e.printStackTrace();
		}		
	}
	
	private void write(String log,String type) {		
		try {
			String filePath=PathUtil.getFileRoot() +"/logs/";
			File dir=new File(filePath);
			if(!dir.exists()){
				dir.mkdirs();
			}
			
			String now=Tools.formatDate(new Date(), "_yyyy_MM_dd");
			String fileName=type+now+".log";
			fileName=filePath+"/"+fileName;
			File file=new File(fileName);
			if(!file.exists()){
				file.createNewFile();
			}
			if(fos==null){
				fos=new FileOutputStream(file, true);				
			}
			if(buff==null){
				buff=new BufferedOutputStream(fos);
			}
			buff.write(Tools.stringToBytes(log));
			buff.flush();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void writeData(List<QueueObj> list){
		JSONArray exchange=new JSONArray();//接口日志
		JSONArray statistics=new JSONArray();//操作日志
		try{			
			if(list!=null){
				for(int i=0;i<list.size();i++){
					QueueObj obj=list.get(i);
					if(obj!=null){
						String type = obj.getType();
						byte[] content = obj.getContent();
						JSONObject jlog=Tools.bytesToJson(content);	
						if(jlog!=null){
							if("EXCHANGERESTLOG".equals(type)){							
								exchange.add(jlog);
							}else if("IDSPSTATISTICS".equals(type)){
								statistics.add(jlog);
							}
						}
					}
				}
			}
					
			//写入接口日志
			if(exchange.size()>0){
				Command ecmd = new Command("job.cmd.LoggerCmd");
				ecmd.setParameter("writeobj", exchange);
				ecmd.execute("insertRestLog");
			}
			
			//写入操作日志
			if(statistics.size()>0){
				Command scmd = new Command("job.cmd.LoggerCmd");
				scmd.setParameter("writeobj", statistics);
				scmd.execute("insertStatistics");
			}
		}catch(Exception e){
			e.printStackTrace();
		}	
	}
}
