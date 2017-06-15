package syan.app.icity.common;

import com.icore.util.DaoFactory;
import com.inspur.bean.ParameterSet;

public class ParameterSetExtension {

	public static ParameterSetExtension getInstance() {
		return DaoFactory.getDao(ParameterSetExtension.class.getName());
	}

	public String get(ParameterSet pSet, String Param) {
		String ParamStr = String.valueOf(pSet.getParameter(Param));
		return ParamStr.equals("null") ? "" : ParamStr;
	}
}
