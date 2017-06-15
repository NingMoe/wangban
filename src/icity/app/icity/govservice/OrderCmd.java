package app.icity.govservice;

import java.io.IOException;
import java.util.Map;

import org.jdom.JDOMException;

import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

public class OrderCmd extends BaseQueryCommand {
	
	public DataSet getOrder(){
		return OrderDao.getInstance().getOrder();
	}
	
	public DataSet getOrderXMLDoc() throws IOException, JDOMException{
		return OrderDao.getInstance().getOrderXMLDoc();
	}
	public DataSet getOrderFeedback(){
		return OrderDao.getInstance().getOrderFeedback();
	}
	public DataSet getQueryOrder(){
		return OrderDao.getInstance().getQueryOrder();
	}
	
	public DataSet getQueryXMLDoc() throws IOException, JDOMException{
		return OrderDao.getInstance().getQueryXMLDoc();
	}
}
