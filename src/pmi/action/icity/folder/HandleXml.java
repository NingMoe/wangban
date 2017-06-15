package action.icity.folder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import com.inspur.base.BaseAction;
import com.inspur.util.PathUtil;
import com.inspur.util.SecurityConfig;

public class HandleXml extends BaseAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public boolean handler(Map<String, Object> data) {
		BufferedWriter bufferWritter = null;
		try {
			Map<String, String> map = this.getPostData();
			String xmlPath = map.get("xmlPath");
			xmlPath = xmlPath.replaceFirst(SecurityConfig.getString("VirtualPath"), "");
			String path = PathUtil.getWebPath();
			File file = new File(path + xmlPath);
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
			// true = append file
			FileWriter fileWritter = new FileWriter(file, false);
			bufferWritter = new BufferedWriter(fileWritter);
			bufferWritter.write(map.get("xml"));
			bufferWritter.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bufferWritter != null) {
				try {
					bufferWritter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return false;
		}
	}
}
