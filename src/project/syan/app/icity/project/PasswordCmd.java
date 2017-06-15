package syan.app.icity.project;

import com.inspur.StateType;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

public class PasswordCmd {
	public DataSet resetPwd(ParameterSet pSet) {
		DataSet ds = new DataSet();
		
		String Account  = (String)pSet.getParameter("account");
		String Name= (String)pSet.getParameter("name");
		String Card_No= (String)pSet.getParameter("card_no");
		
		try {
			DataSet UserDS = PasswordDao.getInstance().getUser(Account, Name, Card_No);
			if(UserDS.getTotal() == 1){
				int SuccCount = PasswordDao.getInstance().resetPwd(UserDS.getRecord(0).getString("ID"));
				if(SuccCount == 1){
					ds.setState(StateType.SUCCESS);
					ds.setMessage("重置成功！");
				}else{
					throw new Exception("重置失败！");
				}
			}else{
				throw new Exception("查询用户失败！");
			}
		}catch(Exception e){
			ds.setState(StateType.FAILT);
			ds.setMessage(e.getMessage());
			e.printStackTrace();
		}
		
		return ds;
	}
}
