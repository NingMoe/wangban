package app.pmi.config.cmd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import app.pmi.config.ISecurity;
import app.pmi.config.JobHandle;

import com.icore.plugin.PluginFactory;
import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.Tools;

public class PluginCmd extends BaseQueryCommand {

	private static final String DISPLAY_NONE = "none";
	private static final String ENABLE_FALSE = "false";

	public DataSet getList(ParameterSet pset) {
		DataSet ds = new DataSet();
		JSONArray array = new JSONArray();
		Document pluginDoc = JobHandle.getInstance().getDoc(ISecurity.PLUGIN_FILE);
		Document registerPluginDoc = JobHandle.getInstance().getDoc(ISecurity.REGISTERPLUGIN_FILE);
		if (registerPluginDoc == null) {
			registerPluginDoc = DocumentHelper.createDocument();
		}
		Element registerPluginRoot = registerPluginDoc.getRootElement();
		Element jroot = pluginDoc.getRootElement();
		List<Element> jList = jroot.elements();
		for (Element ele : jList) {
			JSONObject json = new JSONObject();
			String id = ele.attributeValue("id");
			json.put("ID", id);
			json.put("NAME", ele.attributeValue("name"));
			if (registerPluginRoot != null) {
				List<Element> rList = registerPluginRoot.elements();
				for (Element rele : rList) {
					if (rele.attributeValue("id").equals(id)) {
						json.put("ISOPEN", "1");
						break;
					}
				}
			}
			array.add(json);
		}
		ds.setData(Tools.stringToBytes(array.toString()));
		return ds;
	}

	public DataSet save(ParameterSet pset) {
		DataSet ds = new DataSet();
		JSONArray array = (JSONArray) pset.getParameter("param");
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement("plugins");
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(new File(ISecurity.REGISTERPLUGIN_FILE));
			for (int i = 0, n = array.size(); i < n; i++) {
				JSONObject json = array.getJSONObject(i);
				Element e = root.addElement("plugin");
				e.addAttribute("id", json.getString("id"));
			}
			fos.write(Tools.stringToBytes(doc.asXML()));
			PluginFactory.getInstance().reset();
		} catch (FileNotFoundException e) {
			ds.setState(StateType.FAILT);
			ds.setMessage("保存配置失败");
			e.printStackTrace();
		} catch (IOException e) {
			ds.setState(StateType.FAILT);
			ds.setMessage("保存配置失败");
			e.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return ds;
	}
}
