package app.util;

import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

import app.util.PinYinHelper;

public class PinYinCmd extends BaseQueryCommand {
	public DataSet getPinyin(ParameterSet pset){
		DataSet ds = new DataSet();
		String chinese = (String)pset.getParameter("chinese");
		String pinyin = PinYinHelper.getFirstLetter(chinese);
		ds.setData(pinyin.getBytes());
		ds.setState(StateType.SUCCESS);
		return ds;
	}
}
