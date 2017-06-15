package hlj_qqheNew.app.icity.project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import com.icore.util.DaoFactory;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.SecurityConfig;

public class LoginByCardDao extends BaseJdbcDao{
	    protected static String icityDataSource = "icityDataSource";
	    protected static String ipfDataSource = "ipfDataSource";
	    protected static String icpDataSource = "icpDataSource";

	    public static LoginByCardDao getInstance() {
	    	return DaoFactory.getDao(LoginByCardDao.class.getName());
	    }
	    public LoginByCardDao() {
	        this.setDataSourceName(icityDataSource);
	    }
	    
	    /*
	     * 获取身份证阅读器中读到的信息
	     */
	    public String[] getPersonInfo(String ipadd){
	    	String CardFolder = SecurityConfig.getString("CardFolder");
	    	String filePath = CardFolder+ipadd+"/wz.txt";
	    	//清空扫描信息
	    	DeleteFolder(filePath.substring(0,filePath.length()-7));
	    	String[] PInfo = {"","0"};
	    	int j=10;
	    	//递归等待身份证信息扫描
	    	for(int i=0;i<j;i++){
	    		if(PInfo[1].equals("0")){
	    			try {   
		    	    	PInfo = getFile(filePath);
		    	    	Thread.currentThread().sleep(500);
		    	    	continue;
	    	    	}catch(Exception e){
	    	    		e.printStackTrace();
	    	    	} 
	    		}else{
	    			break;
	    		}
	    	}
	    	
	    	return PInfo;
	    }
	    
	    public String[] getFile(String filePath){
	    	String[] PInfo = {"","0"};//读取到的信息
	    	try {
                String encoding="GBK";
                File file=new File(filePath);
                if(file.isFile() && file.exists()){ //判断文件是否存在
                    InputStreamReader read = new InputStreamReader(
                    new FileInputStream(file),encoding);//考虑到编码格式
                    BufferedReader bufferedReader = new BufferedReader(read);
                    String lineTxt = null;
                    while((lineTxt = bufferedReader.readLine()) != null){
                    	PInfo[0] += lineTxt;
                    }
                    PInfo[1] ="1";
                    System.out.println("wz.txt内容>>>>>>>>>>>"+PInfo[0]);
                    read.close();
                    DeleteFolder(filePath.substring(0,filePath.length()-7));
		        }else{
		        	PInfo[0] = "找不到身份证扫描的文件";
		        	PInfo[1] = "0";
		        }
	        } catch (Exception e) {
	        	PInfo[0] = "读取文件内容出错";
	        	PInfo[1] = "0";
	            e.printStackTrace();
	        }
	    	return PInfo;
	    }
	    
	    /*
	     * 查询身份证对应的帐号、密码
	     */
		public DataSet queryCard(ParameterSet pSet) {
			String ipadd = pSet.getRemoteAddr();
	        DataSet ds = new  DataSet();
	        String[] PInfo = getPersonInfo(ipadd);
	        if(PInfo[1].equals("1")){
	        	String[] card_no = PInfo[0].split(",");
				String sql=" select account,password from uc_user where card_no='"+card_no[5]+"' ";
		        int i = this.executeUpdate(sql, null, icityDataSource);
		        if (i == 0) {
		        	System.out.println("没有注册帐号>>>>>>>>>>>>>>");
		        	ds=this.executeDataset(sql, null, icityDataSource);
		            ds.setState(StateType.FAILT);
		            ds.setMessage("没有对应的账户信息");
		        } else {
		        	System.out.println("返回登录>>>>>>>>>>>>>>>>");
		        	ds=this.executeDataset(sql, null, icityDataSource);
		            ds.setState(StateType.SUCCESS);
		        }	
	        }else{
	        	ds.setState(StateType.FAILT);
	        	ds.setMessage(PInfo[0]);	
	        }
			return ds;
		}
		
		/** 
		 *  根据路径删除指定的目录或文件，无论存在与否 
		 *@param sPath  要删除的目录或文件 
		 *@return 删除成功返回 true，否则返回 false。 
		 */  
		public boolean DeleteFolder(String sPath) {  
			boolean flag = false;  
		    File file = new File(sPath);  
		    // 判断目录或文件是否存在  
		    if (!file.exists()) {  // 不存在返回 false  
		        return flag;  
		    } else {  
		        // 判断是否为文件  
		        if (file.isFile()) {  // 为文件时调用删除文件方法  
		            return deleteFile(sPath);  
		        } else {  // 为目录时调用删除目录方法  
		            return deleteDirectory(sPath);  
		        }  
		    }  
		}  
		
		/** 
		 * 删除目录（文件夹）以及目录下的文件 
		 * @param   sPath 被删除目录的文件路径 
		 * @return  目录删除成功返回true，否则返回false 
		 */  
		public boolean deleteDirectory(String sPath) {  
		    //如果sPath不以文件分隔符结尾，自动添加文件分隔符  
		    if (!sPath.endsWith(File.separator)) {  
		        sPath = sPath + File.separator;  
		    }  
		    File dirFile = new File(sPath);  
		    //如果dir对应的文件不存在，或者不是一个目录，则退出  
		    if (!dirFile.exists() || !dirFile.isDirectory()) {  
		        return false;  
		    }  
		    boolean flag = true;  
		    //删除文件夹下的所有文件(包括子目录)  
		    File[] files = dirFile.listFiles();  
		    for (int i = 0; i < files.length; i++) {  
		        //删除子文件  
		        if (files[i].isFile()) {  
		            flag = deleteFile(files[i].getAbsolutePath());  
		            if (!flag) break;  
		        } //删除子目录  
		        else {  
		            flag = deleteDirectory(files[i].getAbsolutePath());  
		            if (!flag) break;  
		        }  
		    }  
		    if (!flag) return false;  
		    //删除当前目录  
		    if (dirFile.delete()) {  
		        return true;  
		    } else {  
		        return false;  
		    }  
		}  
		
		/** 
		 * 删除单个文件 
		 * @param   sPath    被删除文件的文件名 
		 * @return 单个文件删除成功返回true，否则返回false 
		 */  
		public boolean deleteFile(String sPath) {  
		    boolean flag = false;  
		    File file = new File(sPath);  
		    // 路径为文件且不为空则进行删除  
		    if (file.isFile() && file.exists()) {  
		        file.delete();  
		        flag = true;  
		    }  
		    return flag;  
		} 

}
