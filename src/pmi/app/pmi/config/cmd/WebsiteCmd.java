package app.pmi.config.cmd;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import app.pmi.config.ISecurity;

import com.icore.util.SecurityConfig;
import com.icore.util.StringUtil;
import com.inspur.StateType;
import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.hsf.service.common.utils.StringUtils;
import com.inspur.util.PathUtil;
import com.inspur.util.Tools;

public class WebsiteCmd extends BaseQueryCommand {

	@SuppressWarnings("unchecked")
	public DataSet getWebsiteConfig(ParameterSet pSet) {
		DataSet ds = new DataSet();
		SAXReader sr = new SAXReader();// 获取读取方式
		Document doc;
		InputStream in = null;
		try {
			doc = sr.read(ISecurity.CUSTOM_FILE);
			// 读取xml文件，并且将数据全部存放到Document中
			Element root = doc.getRootElement();// 获取根节点
			List<Element> list = root.elements("virtualPath");// 根据根节点，找出
																// virtualPath
			if (list != null && list.size() > 0) {
				Element virtualPath = list.get(0);
				List<Element> items = virtualPath.elements("item");// 获取所有的item节点

				// 按照name查找站点
				String qn = (String) pSet.getParameter("name");
				List<WebSite> jsonArray = new ArrayList<WebSite>();
				// 查找一个站点
				if (StringUtils.isNotEmpty(qn)) {
					for (Element item : items) {
						Attribute na = item.attribute("name");
						String name = na.getStringValue();
						if (StringUtils.isNotEmpty(name)) {
							// 去掉最前面的 “/”
							name = name.substring(1);
						}
						if (qn.equals(name)) {
							String id = item.attributeValue("id");
							if (StringUtil.isEmpty(id)) {
								id = name;
							}
							String title = item.attributeValue("title");
							String domain = item.attributeValue("domain");
							String value = item.attributeValue("value");
							String pid = item.attributeValue("pid");
							String is_show = item.attributeValue("is_show");
							if (StringUtil.isEmpty(pid)) {
								pid = item.attributeValue("pname");
							}
							WebSite itemObj = new WebSite(id, pid, name, domain, value, title,is_show);

							// 读取属性文件
							String filePath = PathUtil.getWebPath() + "/conf/" + value;
							Properties propertise = new Properties();
							in = new BufferedInputStream(new FileInputStream(filePath));
							propertise.load(in);
							itemObj.setPAGE_MODE(propertise.getProperty("PAGE_MODE"));
							itemObj.setWebRank(propertise.getProperty("WebRank"));
							itemObj.setWebRegion(propertise.getProperty("WebRegion"));
							jsonArray.add(itemObj);
							break;
						}
					}
				} else {
					// 查找全部
					Map<String, WebSite> map = new HashMap<String, WebSite>();
					for (Element item : items) {
						Attribute na = item.attribute("name");
						String name = na.getStringValue();
						if (StringUtils.isNotEmpty(name)) {
							// 去掉最前面的 “/”
							name = name.substring(1);
						}
						String id = item.attributeValue("id");
						if (StringUtil.isEmpty(id)) {
							id = name;
						}
						String title = item.attributeValue("title");
						String domain = item.attributeValue("domain");
						String value = item.attributeValue("value");
						String pid = item.attributeValue("pid");
						String is_show = item.attributeValue("is_show");
						if (StringUtil.isEmpty(pid)) {
							pid = item.attributeValue("pname");
						}
						WebSite itemObj = new WebSite(id, pid, name, domain, value, title,is_show);
						// 读取属性文件
						String filePath = PathUtil.getWebPath() + "/conf/" + value;
						Properties propertise = new Properties();
						in = new BufferedInputStream(new FileInputStream(filePath));
						propertise.load(in);
						itemObj.setPAGE_MODE(propertise.getProperty("PAGE_MODE"));
						itemObj.setWebRank(propertise.getProperty("WebRank"));
						itemObj.setWebRegion(propertise.getProperty("WebRegion"));
						if (StringUtils.isNotEmpty(pid)) {
							WebSite parent = map.get(pid);
							if (parent != null) {
								List<WebSite> childs = parent.getChilds();
								if (childs == null) {
									childs = new ArrayList<WebSite>();
								}
								childs.add(itemObj);
								parent.setChilds(childs);
							}

						} else {
							jsonArray.add(itemObj);
						}
						map.put(id, itemObj);
					}
				}
				JSONArray array = JSONArray.fromObject(jsonArray);
				ds.setData(Tools.stringToBytes(array.toString()));

			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage("获取配置失败！");
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return ds;
	}

	public DataSet saveWebsiteConfig(ParameterSet pSet) {
		DataSet ds = new DataSet();
		SAXReader sr = null;
		XMLWriter writer = null;
		JSONArray array = (JSONArray) pSet.getParameter("params");
		try {
			sr = new SAXReader();// 获取读取方式
			Document doc = sr.read(ISecurity.CUSTOM_FILE);
			// 读取xml文件，并且将数据全部存放到Document中
			Element root = doc.getRootElement();// 获取根节点
			List<Element> list = root.elements("virtualPath");// 根据根节点，找出
																// virtualPath
			if (list != null && list.size() > 0) {
				Element virtualPath = list.get(0);
				Set<String> nameSet = new HashSet<String>();
				virtualPath.clearContent();
				for (int i = 0, n = array.size(); i < n; i++) {
					JSONObject json = array.getJSONObject(i);
					Element e = virtualPath.addElement("item");
					e.addAttribute("id", json.getString("id"));
					e.addAttribute("title", json.getString("title"));
					e.addAttribute("domain", json.getString("domain"));
					String name = "/" + json.getString("name");
					if (nameSet.contains(name)) {
						ds.setState(StateType.FAILT);
						ds.setMessage("虚拟路径【" + name + "】存在重复,请修改后保存");
						return ds;
					} else {
						nameSet.add(name);
					}
					String pid = json.getString("pid");
					// 如果不存在父节点 提升为最高级
					if (nameSet.contains("/" + pid)) {
						e.addAttribute("pid", "/" + pid);
					} else {
						e.addAttribute("pid", "");
					}
					e.addAttribute("name", name);
					String value = "security" + name + ".properties";
					e.addAttribute("value", value);
					// 修改属性文件
					editPropFile(value);
				}
				OutputFormat outFormat = OutputFormat.createPrettyPrint();
				outFormat.setEncoding("UTF-8");
				writer = new XMLWriter(new FileOutputStream(ISecurity.CUSTOM_FILE), outFormat);
				writer.write(doc); // 输出到文件
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage("获取配置失败！");
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return ds;
	}

	public DataSet setWebsiteConfig(ParameterSet pSet) {
		DataSet ds = new DataSet();
		SAXReader sr = null;
		XMLWriter writer = null;
		InputStream in = null;
		FileOutputStream fos = null;
		try {
			sr = new SAXReader();// 获取读取方式
			Document doc = sr.read(ISecurity.CUSTOM_FILE);
			// 读取xml文件，并且将数据全部存放到Document中
			Element root = doc.getRootElement();// 获取根节点
			List<Element> list = root.elements("virtualPath");// 根据根节点，找出
																// virtualPath
			if (list != null && list.size() > 0) {
				Element virtualPath = list.get(0);
				List<Element> items = virtualPath.elements("item");// 获取所有的item节点
				String oldname = (String) pSet.getParameter("oldname");
				String newName = (String) pSet.getParameter("name");
				if (StringUtils.isEmpty(oldname)) {
					ds.setState(StateType.FAILT);
					ds.setMessage("修改配置异常！");
					return ds;
				}

				for (int i = 0; i < items.size(); i++) {
					Element e = items.get(i);
					Attribute na = e.attribute("name");
					String name = na.getStringValue();
					if (StringUtils.isNotEmpty(name)) {
						// 去掉最前面的 “/”
						name = name.substring(1);
					}
					if(newName.equals(name)&&!newName.equals(oldname)){
						ds.setState(StateType.FAILT);
						ds.setMessage("上下文路径不能重复！");
						return ds;
					}

				}
				for (int j = 0; j < items.size(); j++) {
					Element e = items.get(j);
					Attribute na = e.attribute("name");
					String name = na.getStringValue();
					if (StringUtils.isNotEmpty(name)) {
						// 去掉最前面的 “/”
						name = name.substring(1);
					}
					if (oldname.equals(name)) {
						String title = (String) pSet.getParameter("title");
						String domain = (String) pSet.getParameter("domain");
						newName = "/" + newName;
						String WebRank = (String) pSet.getParameter("WebRank");
						String WebRegion = (String) pSet.getParameter("WebRegion");
						String PAGE_MODE = (String) pSet.getParameter("PAGE_MODE");
						String IS_SHOW = (String) pSet.getParameter("IS_SHOW");

						Attribute ta = e.attribute("title");
						ta.setValue(title);
						Attribute ia = e.attribute("is_show");
						if(ia==null){
							e.addAttribute("is_show", IS_SHOW);
						}else{
							ia.setValue(IS_SHOW);
						}
						Attribute da = e.attribute("domain");
						da.setValue(domain);
						na.setValue(newName);
						Attribute va = e.attribute("value");
						String propFilePath = va.getStringValue();
						String value = "security" + newName + ".properties";
						// 判断是否为原文件
						boolean flag = false;
						String oldfilePath = PathUtil.getWebPath() + "/conf/" + value;
						if (!propFilePath.equals(value)) {
							// 修改属性文件
							editPropFile(value);
							va.setValue(value);
							oldfilePath = PathUtil.getWebPath() + "/conf/" + propFilePath;
							flag = true;
						}

						// 设置属性值
						String filePath = PathUtil.getWebPath() + "/conf/" + value;
						Properties propertise = new Properties();
						in = new BufferedInputStream(new FileInputStream(oldfilePath));
						propertise.load(in);
						File f = new File(filePath);
						if (!f.isFile()) {
							f.createNewFile();
						}
						fos = new FileOutputStream(f);

						propertise.setProperty("AppHall", title);
						propertise.setProperty("WebRank", WebRank);
						propertise.setProperty("WebRegion", WebRegion);
						propertise.setProperty("PAGE_MODE", PAGE_MODE);

						propertise.store(fos, null);
						// 先close掉 才能删除文件
						in.close();
						in = null;
						if (flag) {
							// 原配置文件是否要删除？
							File file = new File(oldfilePath);
							if (file.exists()) {
								file.delete();
							}
						}
						break;
					}

				}

				OutputFormat outFormat = OutputFormat.createPrettyPrint();
				outFormat.setEncoding("UTF-8");
				writer = new XMLWriter(new FileOutputStream(ISecurity.CUSTOM_FILE), outFormat);
				writer.write(doc); // 输出到文件
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage("获取配置失败！");
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
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

	/**
	 * 删除一个站点
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet deleteWebsite(ParameterSet pSet) {
		DataSet ds = new DataSet();
		SAXReader sr = null;
		XMLWriter writer = null;
		try {
			sr = new SAXReader();// 获取读取方式
			Document doc = sr.read(ISecurity.CUSTOM_FILE);
			// 读取xml文件，并且将数据全部存放到Document中
			Element root = doc.getRootElement();// 获取根节点
			List<Element> list = root.elements("virtualPath");// 根据根节点，找出
																// virtualPath

			String delId = "";
			if (list != null && list.size() > 0) {
				Element virtualPath = list.get(0);
				List<Element> items = virtualPath.elements("item");// 获取所有的item节点
				String name = (String) pSet.getParameter("name");
				if (StringUtils.isEmpty(name)) {
					ds.setState(StateType.FAILT);
					ds.setMessage("参数异常！");
					return ds;
				}

				for (int i = 0; i < items.size(); i++) {
					Element e = items.get(i);
					Attribute na = e.attribute("name");
					String n = na.getStringValue();
					if (StringUtils.isNotEmpty(n)) {
						// 去掉最前面的 “/”
						n = n.substring(1);
					}
					if (name.equals(n)) {

						Attribute va = e.attribute("value");
						delId = e.attributeValue("id");
						if (StringUtil.isEmpty(delId)) {
							delId = n;
						}
						String propFilePath = va.getStringValue();

						String filePath = PathUtil.getWebPath() + "/conf/" + propFilePath;

						// 删除配置文件
						File file = new File(filePath);
						if (file.exists()) {
							file.delete();
						}
						virtualPath.remove(e);
						break;
					}
				}

				for (int i = 0; i < items.size(); i++) {
					Element e = items.get(i);
					String pid = e.attributeValue("pid");
					if (!StringUtil.isEmpty(pid) && pid.equals(delId)) {
						e.attribute("pid").setValue("");
					}
				}
				OutputFormat outFormat = OutputFormat.createPrettyPrint();
				outFormat.setEncoding("UTF-8");
				writer = new XMLWriter(new FileOutputStream(ISecurity.CUSTOM_FILE), outFormat);
				writer.write(doc); // 输出到文件
				SecurityConfig.clear();
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage("获取配置失败！");
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return ds;
	}

	private boolean editPropFile(String newPropFile) {
		boolean flag = false;
		String conf = PathUtil.getWebPath() + "/conf";
		File newFile = new File(conf, newPropFile);
		if (!newFile.exists()) {
			newFile.getParentFile().mkdirs();
			// getParentFile()返回File类型的父路径，mkdirs()创建所有的路径
			try {
				flag = newFile.createNewFile();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		return flag;
	}

	public DataSet addWebsiteConfig(ParameterSet pSet) {
		DataSet ds = new DataSet();
		SAXReader sr = null;
		XMLWriter writer = null;
		FileOutputStream fos = null;

		String id = (String) pSet.getParameter("id");
		if (StringUtil.isEmpty(id)) {
			id = Tools.getUUID32();
		}

		String name = (String) pSet.getParameter("name");
		String title = (String) pSet.getParameter("title");
		String domain = (String) pSet.getParameter("domain");

		String pid = (String) pSet.getParameter("pid");

		String WebRank = (String) pSet.getParameter("WebRank");
		String WebRegion = (String) pSet.getParameter("WebRegion");

		String PAGE_MODE = (String) pSet.getParameter("PAGE_MODE");
		String IS_SHOW = (String) pSet.getParameter("IS_SHOW");
		try {
			sr = new SAXReader();// 获取读取方式
			Document doc = sr.read(ISecurity.CUSTOM_FILE);
			if (StringUtils.isEmpty(name)) {
				ds.setState(StateType.FAILT);
				ds.setMessage("上下文路径不能为空！");
				return ds;
			}
			name = "/" + name;
			// 读取xml文件，并且将数据全部存放到Document中
			Element root = doc.getRootElement();// 获取根节点
			List<Element> list = root.elements("virtualPath");// 根据根节点，找出
																// virtualPath
			if (list != null && list.size() > 0) {
				Element virtualPath = list.get(0);
				List<Element> items = virtualPath.elements("item");// 获取所有的item节点

				// 判断站点的虚拟路径不能相同
				Iterator iter = items.iterator();
				// boolean hasParent = false;
				while (iter.hasNext()) {
					Element e = (Element) iter.next();
					Attribute attribute = e.attribute("name");
					if (attribute.getValue().equals(name)) {
						ds.setState(StateType.FAILT);
						ds.setMessage("上下文路径不能重复！");
						return ds;
					}
					/*
					 * if (attribute.getValue().equals("/" + pid)) { hasParent =
					 * true; }
					 */
				}

				Element e = virtualPath.addElement("item");
				e.addAttribute("id", id);
				e.addAttribute("title", title);
				e.addAttribute("domain", domain);
				e.addAttribute("is_show", IS_SHOW);
				e.addAttribute("name", name);
				String value = "security" + name + ".properties";
				e.addAttribute("value", value);
				// if(hasParent) {
				e.addAttribute("pid", pid);
				// }else {
				// e.addAttribute("pid", "");
				// }
				// 創建配置文件
				editPropFile(value);
				String filePath = PathUtil.getWebPath() + "/conf/" + value;
				Properties propertise = new Properties();
				File f = new File(filePath);
				if (!f.isFile()) {
					f.createNewFile();
				}
				fos = new FileOutputStream(f);
				propertise.setProperty("AppHall", title);
				propertise.setProperty("WebRank", WebRank);
				propertise.setProperty("WebRegion", WebRegion);
				propertise.setProperty("PAGE_MODE", PAGE_MODE);
				propertise.store(fos, null);

				OutputFormat outFormat = OutputFormat.createPrettyPrint();
				outFormat.setEncoding("UTF-8");
				writer = new XMLWriter(new FileOutputStream(ISecurity.CUSTOM_FILE), outFormat);

				writer.write(doc); // 输出到文件
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage("获取配置失败");
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
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

	/**
	 * 保存个性化配置信息
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet saveIndividuationConfig(ParameterSet pSet) {
		DataSet ds = new DataSet();
		SAXReader sr = null;
		XMLWriter writer = null;
		InputStream in = null;
		FileOutputStream fos = null;
		String siteName = (String) pSet.getParameter("name");
		String layout = (String) pSet.getParameter("layout");
		String style = (String) pSet.getParameter("style");
		String titleImg = (String) pSet.getParameter("titleImg");
		JSONArray array = (JSONArray) pSet.getParameter("jsImgs");
		String fdrk = (String) pSet.getParameter("fdrk");
		String custom = (String) pSet.getParameter("custom");
		try {
			sr = new SAXReader();// 获取读取方式
			Document doc = sr.read(ISecurity.CUSTOM_FILE);
			if (StringUtils.isEmpty(siteName)) {
				ds.setState(StateType.FAILT);
				ds.setMessage("上下文路径不能为空！");
				return ds;
			}
			// 读取xml文件，并且将数据全部存放到Document中
			Element root = doc.getRootElement();// 获取根节点
			List<Element> list = root.elements("virtualPath");// 根据根节点，找出
																// virtualPath
			if (list != null && list.size() > 0) {
				Element virtualPath = list.get(0);
				List<Element> items = virtualPath.elements("item");// 获取所有的item节点

				for (int i = 0; i < items.size(); i++) {
					Element e = items.get(i);
					Attribute na = e.attribute("name");
					String name = na.getStringValue();
					if (StringUtils.isNotEmpty(name)) {
						// 去掉最前面的 “/”
						name = name.substring(1);
					}
					if (siteName.equals(name)) {
						String value = "security/" + name + ".properties";
						String filePath = PathUtil.getWebPath() + "/conf/" + value;
						Properties propertise = new Properties();
						File f = new File(filePath);
						if (f.exists()) {
							// 获取原配置
							in = new BufferedInputStream(new FileInputStream(filePath));
							propertise.load(in);
							fos = new FileOutputStream(f);
							// 对个图片用","隔开
							StringBuilder js = new StringBuilder();
							for (int j = 0; j < array.size(); j++) {
								js.append(array.get(j)).append(",");
							}
							if (array.size() > 0) {
								// 去掉最后一个','
								js.deleteCharAt(js.length() - 1);
							}
							propertise.setProperty("TITLE_IMG", titleImg);
							propertise.setProperty("STYLE", style);
							propertise.setProperty("LAYOUT", layout);
							propertise.setProperty("PRESENTATION_IMG", js.toString());
							// 县级分厅入口的详细信息存入文件中，配置文件只标记文件地址
							if (StringUtils.isEmpty(fdrk)) {
								fdrk = "";
							}
							String webRegion = propertise.getProperty("WebRegion");
							String fp = "map/" + webRegion + ".html";
							String fdrkFilePath = PathUtil.getWebPath() + "/file/" + fp;
							String skin = PathUtil.getWebPath() + "/file/skin/" + webRegion + ".css";

							BufferedWriter fdrkOs = null;
							try {
								fdrkOs = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fdrkFilePath)));
								fdrkOs.write(fdrk);
								propertise.setProperty("ENTRANCE_FILE", fp);
								// 只有自定义的时候才修改自定义文件 这样能保存上次自定义的样式
								if ("custom".equals(style)) {
									fdrkOs = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(skin)));
									fdrkOs.write(custom);
								}
							} catch (Exception e1) {
							} finally {
								if (fdrkOs != null) {
									fdrkOs.close();
								}
							}

							propertise.store(fos, null);
							OutputFormat outFormat = OutputFormat.createPrettyPrint();
							outFormat.setEncoding("UTF-8");
							writer = new XMLWriter(new FileOutputStream(ISecurity.CUSTOM_FILE), outFormat);
							writer.write(doc); // 输出到文件
						} else {
							ds.setState(StateType.FAILT);
							ds.setMessage("获取配置文件失败！");
						}
					}
				}
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage("获取配置失败！");
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return ds;
	}

	/**
	 * 获取个性化数据
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getIndividuationConfig(ParameterSet pSet) {
		DataSet ds = new DataSet();
		SAXReader sr = new SAXReader();// 获取读取方式
		Document doc;
		InputStream in = null;
		try {
			doc = sr.read(ISecurity.CUSTOM_FILE);
			// 读取xml文件，并且将数据全部存放到Document中
			Element root = doc.getRootElement();// 获取根节点
			List<Element> list = root.elements("virtualPath");// 根据根节点，找出
																// virtualPath
			if (list != null && list.size() > 0) {
				Element virtualPath = list.get(0);
				List<Element> items = virtualPath.elements("item");// 获取所有的item节点

				String qn = (String) pSet.getParameter("name");
				// 查找一个站点
				if (StringUtils.isNotEmpty(qn)) {
					for (Element item : items) {
						Attribute na = item.attribute("name");
						String name = na.getStringValue();
						if (StringUtils.isNotEmpty(name)) {
							// 去掉最前面的 “/”
							name = name.substring(1);
						}
						if (qn.equals(name)) {
							String title = item.attributeValue("title");
							// String domain = item.attributeValue("domain");
							String value = item.attributeValue("value");
							// String pname = item.attributeValue("pname");
							JSONObject itemObj = new JSONObject();
							itemObj.put("title", title);

							// 读取属性文件
							String filePath = PathUtil.getWebPath() + "/conf/" + value;
							Properties propertise = new Properties();
							in = new BufferedInputStream(new FileInputStream(filePath));
							propertise.load(in);
							String layout = propertise.getProperty("LAYOUT");
							itemObj.put("layout", layout);
							String style = propertise.getProperty("STYLE");
							itemObj.put("style", style);
							String titleImg = propertise.getProperty("TITLE_IMG");
							itemObj.put("titleImg", titleImg);
							String jsImgs = propertise.getProperty("PRESENTATION_IMG");
							if (StringUtils.isNotEmpty(jsImgs)) {
								itemObj.put("jsImgs", jsImgs.split(","));
							}
							String fp = propertise.getProperty("ENTRANCE_FILE");
							String fdrkFilePath = PathUtil.getWebPath() + "/file/" + fp;

							BufferedReader fdrkOs = null;
							try {
								StringBuilder sb = new StringBuilder();
								fdrkOs = new BufferedReader(new InputStreamReader(new FileInputStream(fdrkFilePath)));
								String str = null;
								while ((str = fdrkOs.readLine()) != null) {
									sb.append(str);
								}
								itemObj.put("fdrk", sb.toString());

								String skin = PathUtil.getWebPath() + "/file/skin/"
										+ propertise.getProperty("WebRegion") + ".css";
								File sf = new File(skin);
								if (sf.exists()) {
									StringBuilder css = new StringBuilder();
									fdrkOs = new BufferedReader(new InputStreamReader(new FileInputStream(skin)));
									String s = null;
									while ((s = fdrkOs.readLine()) != null) {
										css.append(s).append("\r\n");
									}
									itemObj.put("custom", css.toString());
								}
							} catch (Exception e1) {
							} finally {
								if (fdrkOs != null) {
									fdrkOs.close();
								}
							}
							ds.setData(Tools.stringToBytes(itemObj.toString()));
							break;
						}
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("获取配置失败！");
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return ds;
	}
}
