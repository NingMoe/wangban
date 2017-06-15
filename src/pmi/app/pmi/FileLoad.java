package app.pmi;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.PathUtil;
import com.inspur.util.Tools;

public class FileLoad extends BaseQueryCommand{
	
	public static Map<String,String> FILE_TYPE_URL = new HashMap<String, String>();
	
	public static Set<String> FILE_TYPE_PIC = new HashSet<String>();
	
	static{
		//特殊类型
		FILE_TYPE_URL.put("doc", "public/images/img/docx.png");
		FILE_TYPE_URL.put("docx", "public/images/img/docx.png");
		FILE_TYPE_URL.put("ppt", "public/images/img/pptx.png");
		FILE_TYPE_URL.put("pptx", "public/images/img/pptx.png");
		FILE_TYPE_URL.put("xls", "public/images/img/xlsx.png");
		FILE_TYPE_URL.put("xlsx", "public/images/img/xlsx.png");
		FILE_TYPE_URL.put("folder", "public/images/img/folder.png");
		FILE_TYPE_URL.put("rar", "public/images/img/rar.png");
		FILE_TYPE_URL.put("zip", "public/images/img/zip.png");
		FILE_TYPE_URL.put("txt", "public/images/img/text.png");
		FILE_TYPE_URL.put("pdf", "public/images/img/pdf.png");
		//其他类型使用通用图片显示
		FILE_TYPE_URL.put("common", "public/images/img/common.png");
		//图片格式
		FILE_TYPE_PIC.add("bmp");
		FILE_TYPE_PIC.add("gif");
		FILE_TYPE_PIC.add("jpg");
		FILE_TYPE_PIC.add("png");
		FILE_TYPE_PIC.add("jpeg");
		FILE_TYPE_PIC.add("tiff");
		FILE_TYPE_PIC.add("psd");
		FILE_TYPE_PIC.add("svg");
		//音乐
		FILE_TYPE_URL.put("mp3", "public/images/img/music.png");
		FILE_TYPE_URL.put("wma", "public/images/img/music.png");
		FILE_TYPE_URL.put("ogg", "public/images/img/music.png");
		FILE_TYPE_URL.put("ape", "public/images/img/music.png");
		FILE_TYPE_URL.put("midi", "public/images/img/music.png");
		//动画
		FILE_TYPE_URL.put("fla", "public/images/img/fla.png");
		FILE_TYPE_URL.put("swf", "public/images/img/fla.png");
		//视频
		FILE_TYPE_URL.put("3gp", "public/images/img/video.png");
		FILE_TYPE_URL.put("asf", "public/images/img/video.png");
		FILE_TYPE_URL.put("avi", "public/images/img/video.png");
		FILE_TYPE_URL.put("mkv", "public/images/img/video.png");
		FILE_TYPE_URL.put("mov", "public/images/img/video.png");
		FILE_TYPE_URL.put("mp4", "public/images/img/video.png");
		FILE_TYPE_URL.put("mpeg", "public/images/img/video.png");
		FILE_TYPE_URL.put("rmvb", "public/images/img/video.png");
	}
	public DataSet picturesLoad(ParameterSet pset){
		DataSet ds = new DataSet();
		String path = (String)pset.getParameter("path");
		String type = (String)pset.getParameter("type");
		Set<String> typeSet = null;
		if (StringUtils.isNotEmpty(type)) {
			typeSet = new HashSet<String>();
			String[] types = type.split(",");
			for (int i = 0,n=types.length; i < n; i++) {
				typeSet.add(types[i].toLowerCase());
			}
		}
		String root = PathUtil.getWebPath();
		File file = new File(root+path);
		JSONArray pics = new JSONArray();
		//判断指定的路径是文件还是目录
		if (file.isFile()) {
			if (!isEndWithStr(file)) {
				JSONObject json = new JSONObject();
				String name = file.getName();
				String file_type = name.substring(name.lastIndexOf(".")+1).toLowerCase();;
				//判断是否只需要指定的格式文件
				if (typeSet != null) {
					if (typeSet.contains(file_type)) {
						json = getFileJson(file);
						pics.add(json);
						ds.setTotal(1);
					}
				}else{
					json = getFileJson(file);
					pics.add(json);
					ds.setTotal(1);
				}
			}
		}else if(file.isDirectory()){
			File[] files = file.listFiles();
			int size = 0;
			for (File f : files) {
				if (!isEndWithStr(f)) {
					size++;
					JSONObject json = null;
					String file_type = "";
					if (typeSet != null) {
						typeSet.add("folder");
						if (f.isDirectory()) {
							file_type = "folder";
						}else{
							String name = f.getName();
							file_type = name.substring(name.lastIndexOf(".")+1).toLowerCase();
						}
						if (typeSet.contains(file_type)) {
							json = getFileJson(f);
							pics.add(json);
						}
					}else{
						json = getFileJson(f);
						pics.add(json);
					}
				}
			}
			ds.setTotal(size);
		}
		ds.setData(Tools.stringToBytes(pics.toString()));
		return ds;
	}
	
	/**
	 * 
	 * @param file
	 * @return
	 */
	public JSONObject getFileJson(File file){
		JSONObject json = new JSONObject();
		json.put("ID", Tools.getUUID32());
		json.put("PATH", file.getPath().replaceAll("\\\\", "/").replaceAll(PathUtil.getWebPath(), ""));
		if (!file.isDirectory()) {
			String name = file.getName();
			String file_type = name.substring(name.lastIndexOf(".")+1).toLowerCase();
			json.put("TYPE", file_type);
			json.put("NAME", name);
			if (FILE_TYPE_PIC.contains(file_type)) {
				json.put("TYPE_URL", file.getPath().replaceAll("\\\\", "/").replaceAll(PathUtil.getWebPath(), ""));
			}else{
				if (FILE_TYPE_URL.keySet().contains(file_type)) {
					json.put("TYPE_URL", FILE_TYPE_URL.get(file_type));
				}else{
					json.put("TYPE_URL", FILE_TYPE_URL.get("common"));
				}
			}
		}else{
			json.put("TYPE", "folder");
			json.put("NAME", file.getName());
			json.put("TYPE_URL", FILE_TYPE_URL.get("folder"));
		}
		return json;
	}
	
	/**
	 * 是否以指定后缀
	 * @param file
	 * @return
	 */
	public boolean isEndWithStr(File file){
		String path = file.getPath().toLowerCase();
		if (path.endsWith("/.svn".toLowerCase()) || path.endsWith("\\.svn".toLowerCase())) {
			return true;
		}
		return false;
	}
	public static void main(String[] args) {
		String afa = "fsfsaf\\.svn";
		System.out.println(afa.endsWith("\\.svn"));
	}
}
