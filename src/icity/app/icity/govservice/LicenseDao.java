package app.icity.govservice;

import io.netty.util.CharsetUtil;
import iop.http.BASE64Encoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import sun.misc.BASE64Decoder;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.icity.project.ProjectIndexDao;
import app.icity.sync.UploadUtil;
import app.uc.UserDao;
import app.util.RestUtil;

import com.alibaba.fastjson.JSONException;
import com.icore.http.util.HttpUtil;
import com.icore.util.DaoFactory;
import com.icore.util.SecurityConfig;
import com.icore.util.StringUtil;
import com.icore.util.Tools;
import com.inspur.StateType;
import com.inspur.base.BaseJdbcDao;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;
import com.inspur.util.PathUtil;
import com.inspur.util.db.DbHelper;

import core.util.CommonUtils_api;

public class LicenseDao extends BaseJdbcDao {
	private LicenseDao() {
		this.setDataSourceName("icityDataSource");
	}

	public static LicenseDao getInstance() {
		return DaoFactory.getDao(LicenseDao.class.getName());
	}

	private static Logger log = LoggerFactory.getLogger(LicenseDao.class);

	/**
	 * 从自己的证照系统下载证照文件到本地 重庆需求 licenseNo 是 String 证照编号 accessToken 是 String
	 * 令牌代码(qd)
	 */
	public DataSet downloadLicense(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			ParameterSet m_pSet = new ParameterSet();
			m_pSet.put("sblsh", (String) pSet.get("sblsh"));
			DataSet m_ds = ProjectIndexDao.getInstance().BusinessNoticeQuery(
					m_pSet);
			JSONArray license = new JSONArray();
			if (m_ds.getTotal() > 0) {
				license = "".equals(m_ds.getRecord(0).get("LICENSE")) ? null
						: m_ds.getRecord(0).getJSONArray("LICENSE");
			}
			int m_len = license != null ? license.size() : 0;
			ds.setTotal(m_len);
			if (m_len > 0) {
				// 循环调接口
				for (int i = 0; i < m_len; i++) {
					JSONArray data = new JSONArray();
					String licenseNo = license.getJSONObject(i).getString(
							"LicenesCode");
					String url = HttpUtil.formatUrl(SecurityConfig
							.getString("LicenseUrl")
							+ "/main/licence/getLicenceInfoAndPicture");
					url += "?licenseNo="
							+ URLEncoder.encode(licenseNo, "utf-8")
							+ "&accessToken="
							+ SecurityConfig.getString("LicenseAccessToken");
					Object obj = RestUtil.getData(url);
					JSONObject json = JSONObject.fromObject(obj);
					if ("200".equals(json.getString("statusCode"))
							&& "SUCCESS".equals(json.getString("retCode"))) {
						JSONArray attachment;
						attachment = json.getJSONArray("attachment");
						int att_len = attachment.size();
						for (int k = 0; k < att_len; k++) {
							JSONObject att_data = new JSONObject();
							String fileName = (String) attachment
									.getJSONObject(k).getString("fileName")
									+ "."
									+ (String) attachment.getJSONObject(k)
											.getString("fileType");
							// String path =
							// app.icity.ServiceCmd.class.getResource("").getPath().split("WEB-INF")[0]+
							// "/file/upload/" + fileName;
							String path = PathUtil.getWebPath() + "file"
									+ File.separator + "upload"
									+ File.separator + fileName;
							File file = new File(path);
							if (!file.exists()) {
								byte[] Getresult = new BASE64Decoder()
										.decodeBuffer((String) attachment
												.getJSONObject(k).getString(
														"fileContent"));
								FileOutputStream fos = null;
								try {
									fos = new FileOutputStream(path);
									fos.write(Getresult);
								} catch (RuntimeException ex) {
									ex.printStackTrace();
								} finally {
									if (fos != null) {
										fos.close();
									}
								}
							}
							att_data.put("fileName", fileName);
							data.add(att_data);
						}
						license.getJSONObject(i).put("fileNames", data);
					}
				}
			}
			ds.setRawData(license);
			ds.setState(StateType.SUCCESS);
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
			e.printStackTrace();
		}
		return ds;
	}

	/**
	 * 查询证照列表（根据证件类型和证件号码） certificateType 是 String 证件类型（参数为对应类型的字典值）
	 * certificateNo 是 String 证件号码 accessToken 是 String 令牌代码
	 */
	public DataSet getLicenceListByCertificateTypeAndCertificateNo(
			ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String id = (String) pSet.get("ucid");
			String title = pSet.get("title") != null ? (String) pSet
					.get("title") : "";
			DataSet m_ds = UserDao.getInstance().getListExt(id);
			String certificateType = "";
			String certificateNo = "";
			if (m_ds.getTotal() > 0) {
				certificateType = m_ds.getRecord(0).getString("CARD_TYPE");
				certificateNo = m_ds.getRecord(0).getString("CARD_NO");
			}

			if (!"".equals(certificateType) && !"".equals(certificateNo)) {
				String url = HttpUtil
						.formatUrl(SecurityConfig.getString("LicenseUrl")
								+ "/main/licence/getLicenceListByCertificateTypeAndCertificateNo");
				url += "?certificateType=" + certificateType
						+ "&certificateNo=" + certificateNo + "&accessToken="
						+ SecurityConfig.getString("LicenseAccessToken");
				Object obj = RestUtil.getData(url);
				JSONObject json = JSONObject.fromObject(obj);
				if ("200".equals(json.getString("statusCode"))
						&& "SUCCESS".equals(json.getString("retCode"))) {
					JSONArray licenseArray;
					licenseArray = json.getJSONArray("licenseArray");

					JSONArray resultArray = new JSONArray();
					if (title.length() > 0) {
						for (int i = 0; i < licenseArray.size(); i++) {
							String name = (String) licenseArray
									.getJSONObject(i).get("licenseName");
							if (name.indexOf(title) >= 0) {
								resultArray.add(licenseArray.get(i));
							}
						}
						ds.setRawData(resultArray);

					} else {
						ds.setRawData(licenseArray);
					}
					ds.setState(StateType.SUCCESS);
					ds.setTotal(licenseArray.size());

				} else {
					ds.setState(StateType.FAILT);
					ds.setTotal(0);
					ds.setMessage(json.getJSONObject("errors").getString(
							"message"));
				}
			} else {
				ds.setState(StateType.FAILT);
				ds.setTotal(0);
				ds.setMessage("证件信息错误！");
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
			e.printStackTrace();
		}
		return ds;
	}

	/**
	 * 外网打证。 licenseNo 是 String 证照编号 accessToken 是 String 令牌代码(qd)
	 */
	public DataSet getLicencePictureFile(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			JSONArray data = new JSONArray();
			JSONObject reobj = new JSONObject();
			String licenseNo = (String) pSet.get("licenseNo");
			// licenseNo =
			// "440002_440003-440000-440003-XK-001-01-20160728090154-1_鲁A0084_2";
			String url = HttpUtil.formatUrl(SecurityConfig
					.getString("LicenseUrl")
					+ "/main/licence/getLicenceInfoAndPicture");
			url += "?licenseNo=" + URLEncoder.encode(licenseNo, "utf-8")
					+ "&accessToken="
					+ SecurityConfig.getString("LicenseAccessToken");
			Object obj = RestUtil.getData(url);
			JSONObject json = JSONObject.fromObject(obj);
			if ("200".equals(json.getString("statusCode"))
					&& "SUCCESS".equals(json.getString("retCode"))) {
				JSONObject metaData = json.getJSONObject("metaData");
				JSONArray attachment = json.getJSONArray("attachment");
				JSONObject surfaceData = json.getJSONObject("surfaceData");// 证照照面信息
				int att_len = attachment.size();
				ds.setTotal(att_len);
				for (int k = 0; k < att_len; k++) {
					JSONObject att_data = new JSONObject();
					String fileName = (String) attachment.getJSONObject(k)
							.getString("fileName").trim()
							+ "."
							+ (String) attachment.getJSONObject(k)
									.getString("fileType").trim();
					String path = PathUtil.getWebPath() + "file"
							+ File.separator + "upload" + File.separator
							+ fileName;
					File file = new File(path);
					if (!file.exists()) {
						byte[] Getresult = new BASE64Decoder()
								.decodeBuffer((String) attachment
										.getJSONObject(k)
										.getString("fileContent").trim());
						FileOutputStream fos = null;
						try {
							fos = new FileOutputStream(path);
							fos.write(Getresult);
						} catch (RuntimeException ex) {
							ex.printStackTrace();
						} finally {
							if (fos != null) {
								fos.close();
							}
						}
					}
					att_data.put("fileName", fileName);
					data.add(att_data);
				}
				reobj.put("attachment", data);
				reobj.put("metaData", metaData);
				reobj.put("surfaceData", surfaceData);
			}
			ds.setRawData(reobj);
			ds.setState(StateType.SUCCESS);
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
			e.printStackTrace();
		}
		return ds;
	}

	/**
	 * 根据证照编号、持证者类型，证照类型编号查询证照信息 by:yanhao 参数： { “licenseNumber”: ”照面编号”,
	 * “holderType”:”持证者类型”, “licenseTypeCode”:”证照模板编号” }
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getLicenseInfoByMutliRequirement(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			JSONArray licenses = new JSONArray();
			String queryMap = pSet.get("queryMap").toString();
			String url = HttpUtil.formatUrl(SecurityConfig
					.getString("LicenseUrl")
					+ "/main/licence/getLicenseInfoByMutliRequirement");
			url += "?queryMap=" + URLEncoder.encode(queryMap, "utf-8")
					+ "&accessToken="
					+ SecurityConfig.getString("LicenseAccessToken");
			Object obj = RestUtil.getData(url);
			JSONObject json = JSONObject.fromObject(obj);
			if ("200".equals(json.getString("statusCode"))
					&& "SUCCESS".equals(json.getString("retCode"))) {
				licenses = json.getJSONArray("license");
				for (int i = 0; i < licenses.size(); i++) {
					JSONArray attachment = ((JSONObject) licenses.get(i))
							.getJSONArray("attachment");
					int att_len = attachment.size();
					ds.setTotal(att_len);
					for (int k = 0; k < att_len; k++) {
						String fileName = (String) attachment.getJSONObject(k)
								.getString("fileName").trim()
								+ ""
								+ (String) attachment.getJSONObject(k)
										.getString("fileType").trim();
						String path = PathUtil.getWebPath() + "file"
								+ File.separator + "upload" + File.separator
								+ fileName;
						File file = new File(path);
						if (!file.exists()) {
							byte[] Getresult = new BASE64Decoder()
									.decodeBuffer((String) attachment
											.getJSONObject(k)
											.getString("fileContent").trim());
							FileOutputStream fos = null;
							try {
								fos = new FileOutputStream(path);
								fos.write(Getresult);
							} catch (RuntimeException ex) {
								ex.printStackTrace();
							} finally {
								if (fos != null) {
									fos.close();
								}
							}
						}
					}

				}
			}
			ds.setRawData(licenses);
			ds.setState(StateType.SUCCESS);
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
			e.printStackTrace();
		}
		return ds;
	}

	/**
	 * 根据照面编号获得证照信息
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getLicenceInfoByNumber(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String licenseNumber = pSet.get("licenseNumber") != null ? (String) pSet
					.get("licenseNumber") : "";
  	    	licenseNumber = java.net.URLEncoder.encode(licenseNumber, "UTF-8");
			String url = HttpUtil.formatUrl(SecurityConfig
					.getString("LicenseUrl")
					+ "/main/licence/getLicenceInfoByNumber");
			url += "?licenseNumber=" + licenseNumber 
					+ "&accessToken="
					+ SecurityConfig.getString("LicenseAccessToken");
			Object obj = RestUtil.getData(url);
			JSONObject json = JSONObject.fromObject(obj);
			// json =
			// JSONObject.fromObject("{\"retCode\":\"SUCCESS\",\"metaData\":{\"licenseNo\":\"0020_0020-500000-0020-ZS-20-20160927200255-1_test001_1\",\"licenseTypeCode\":\"0020-500000-0020-ZS-20-20160927200255-1\",\"licenseTypeName\":\"001\",\"deptOrganizeCode\":\"0020\",\"deptName\":\"市发展改革委\",\"certificateDate\":\"\",\"validPeriodBegin\":\"2016-10-19\",\"validPeriodEnd\":\"2017-10-19\",\"warningDate\":\"\",\"warningDateEnd\":\"\",\"holderType\":\"organization\",\"holderName\":\"测试\",\"certificateType\":\"18\",\"certificateNo\":\"123\"},\"surfaceData\":{\"编号\":\"test001\",\"二维码\":\"http://23.99.127.114:8136/icity/external/zzxx?licenseNum=test001&receiveNum=\"},\"surfaceDataPicture\":{},\"state\":1,\"statusCode\":200}");
			// json =
			// JSONObject.fromObject("{\"retCode\":\"SUCCESS\",\"metaData\":{\"licenseNo\":\"0020_0020-500000-0020-XK-675-20160926104456-1_wb001_1\",\"licenseTypeCode\":\"0020-500000-0020-XK-675-20160926104456-1\",\"licenseTypeName\":\"测试证照\",\"deptOrganizeCode\":\"0020\",\"deptName\":\"市发展改革委\",\"certificateDate\":\"\",\"validPeriodBegin\":\"2016-10-24\",\"validPeriodEnd\":\"2017-10-24\",\"warningDate\":\"\",\"warningDateEnd\":\"\",\"holderType\":\"natural\",\"holderName\":\"测试\",\"certificateType\":\"18\",\"certificateNo\":\"123\"},\"surfaceData\":{\"证照编号\":\"wb001\",\"颁发单位\":\"ceshi\",\"图片\":{\"content\":\"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\",\"type\":\"png\"},\"二维码\":\"http://23.99.127.114:8136/icity/external/zzxx?licenseNum=wb001&receiveNum=\",\"时间\":\"2016-10-24\"},\"surfaceDataPicture\":{\"图片\":\"picture\"},\"state\":1,\"statusCode\":200}");
			JSONObject jsonObj = new JSONObject();
			if ("200".equals(json.getString("statusCode"))
					&& "SUCCESS".equals(json.getString("retCode"))) {
				JSONObject metaData = json.getJSONObject("metaData");
				jsonObj.put("metaData", metaData);
				JSONObject surfaceData = json.getJSONObject("surfaceData");
				JSONObject surfaceDataPicture = json
						.getJSONObject("surfaceDataPicture");
				Iterator<Entry<String, JSONObject>> it = surfaceDataPicture
						.entrySet().iterator();
				JSONObject picData = new JSONObject();
				while (it.hasNext()) {
					String key = it.next().getKey();
					fileDownload(JSONObject.fromObject(surfaceData.get(key)),
							key);
					picData.put(key, key + "."
							+ JSONObject.fromObject(surfaceData.get(key)).getString("type"));
					surfaceData.remove(key);
				}
				jsonObj.put("surfaceData", surfaceData);
				jsonObj.put("picData", picData);
				ds.setRawData(jsonObj);
				ds.setState(StateType.SUCCESS);
			} else {
				ds.setState(StateType.FAILT);
				ds.setMessage("error:getLicenceInfoByNumber");
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
			e.printStackTrace();
		}
		return ds;
	}

	/**
	 * 根据申办流水号调用审批接口返回本条业务下的证照列表
	 * 
	 * @param pSet
	 *            (重庆审批接口)
	 * @return
	 */
	public DataSet getLicenceListBySblsh(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String licenseNumber = (String) pSet.get("sblsh");
			String url = HttpUtil.formatUrl(SecurityConfig
					.getString("approval_url") + "/getLicenseInfoList");
			url += "?receiveNumber=" + licenseNumber + "&systemCode=TYSP";
			String obj = RestUtil.getData(url);
			JSONObject json = JSONObject.fromObject(obj);
			if ("200".equals(json.getString("code"))) {
				JSONArray info = json.getJSONArray("info");
				ds.setRawData(info);
				ds.setState(StateType.SUCCESS);
			} else {
				ds.setState(StateType.FAILT);
				ds.setMessage("error:tysp_getLicenseInfoList");
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
			e.printStackTrace();
		}
		return ds;
	}

	// 现在图片或二维码
	private void fileDownload(JSONObject obj, String name) throws IOException {
		String fileName = name + "." + obj.get("type");
		String path = PathUtil.getWebPath() + "file" + File.separator
				+ "upload" + File.separator + fileName;
		File file = new File(path);
		if (!file.exists()) {
			byte[] Getresult = new BASE64Decoder().decodeBuffer((String) obj
					.getString("content").trim());
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(path);
				fos.write(Getresult);
			} catch (RuntimeException ex) {
				ex.printStackTrace();
			} finally {
				if (fos != null) {
					fos.close();
				}
			}
		}
	}

	// 按照超期时间重新排序
	public JSONArray warningDateSortJSONArray(JSONArray licenseArray) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date now = null;
		try {
			now = df.parse(df.format(new Date()));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		JSONArray sortedJsonArray = new JSONArray();
		List<JSONObject> jsonValues = new ArrayList<JSONObject>();
		for (int i = 0; i < licenseArray.size(); i++) {
			Date wdate = null;
			try {

				if (!licenseArray.getJSONObject(i).getString("warningDateEnd")
						.equals("")) {
					wdate = df.parse(licenseArray.getJSONObject(i).getString(
							"warningDateEnd"));
					if (compareDate(wdate, now) >= 0) {
						jsonValues.add(licenseArray.getJSONObject(i));
					}

				}
			} catch (ParseException e) {
				e.printStackTrace();
			}

		}
		Collections.sort(jsonValues, new Comparator<JSONObject>() {
			// You can change "Name" with "ID" if you want to sort by ID
			private static final String KEY_NAME = "warningDateEnd";

			@Override
			public int compare(JSONObject a, JSONObject b) {
				String valA = "";
				String valB = "";

				try {
					valA = (String) a.get(KEY_NAME);
					valB = (String) b.get(KEY_NAME);
				} catch (JSONException e) {
					// do something
				}

				return valA.compareTo(valB);
				// if you want to change the sort order, simply use the
				// following:
				// return -valA.compareTo(valB);
			}
		});

		for (int i = 0; i < jsonValues.size(); i++) {
			sortedJsonArray.add(jsonValues.get(i));
		}

		return sortedJsonArray;
	}

	// 按照年检时间重新排序
	public JSONArray validPeriodEndSortJSONArray(JSONArray licenseArray) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date now = null;
		try {
			now = df.parse(df.format(new Date()));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		JSONArray sortedJsonArray = new JSONArray();
		List<JSONObject> jsonValues = new ArrayList<JSONObject>();
		for (int i = 0; i < licenseArray.size(); i++) {
			Date wdate = null;
			try {

				if (!licenseArray.getJSONObject(i).getString("validPeriodEnd")
						.equals("")) {
					wdate = df.parse(licenseArray.getJSONObject(i).getString(
							"validPeriodEnd"));
					if (compareDate(now, wdate) > 0) {
						jsonValues.add(licenseArray.getJSONObject(i));
					}

				}
			} catch (ParseException e) {
				e.printStackTrace();
			}

		}
		Collections.sort(jsonValues, new Comparator<JSONObject>() {
			// You can change "Name" with "ID" if you want to sort by ID
			private static final String KEY_NAME = "validPeriodEnd";

			@Override
			public int compare(JSONObject a, JSONObject b) {
				String valA = "";
				String valB = "";

				try {
					valA = (String) a.get(KEY_NAME);
					valB = (String) b.get(KEY_NAME);
				} catch (JSONException e) {
					// do something
				}

				return valA.compareTo(valB);
				// if you want to change the sort order, simply use the
				// following:
				// return -valA.compareTo(valB);
			}
		});

		for (int i = 0; i < jsonValues.size(); i++) {
			sortedJsonArray.add(jsonValues.get(i));
		}

		return sortedJsonArray;
	}

	/**
	 * 查询证照列表（根据部门编码） deptcode 是 String 部门编号 title 是 String 查询内容 page 是 int 查询页数
	 * limit 是 int 每页条数 accessToken 是 String 令牌代码  subjectId 是String 证照主题id
	 */
	public DataSet getLicenceListByDeptCode(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String deptCode = (String) pSet.get("deptCode") != null ? (String) pSet
					.get("deptCode") : "";
			String subjectId = (String) pSet.get("subjectId") != null ? (String) pSet
					.get("subjectId") : "";
			String title = pSet.get("title") != null ? (String) pSet
					.get("title") : "";
			int limit = (Integer) pSet.get("limit");
			int page = Integer.parseInt(pSet.get("page") + "");

			title = java.net.URLEncoder.encode(title, "UTF-8");
			if (true) {
				String url = HttpUtil.formatUrl(SecurityConfig
						.getString("LicenseUrl")
						+ "/main/licence/getLicenceTypeListByPagination");
				url += "?accessToken="
						+ SecurityConfig.getString("LicenseAccessToken")
						+ "&isPagination=true" + "&page=" + page + "&rows="
						+ limit;
				if (!"".equals(deptCode)) {
					url += "&orgCode=" + deptCode;
				}
				if (!"".equals(title)) {
					url += "&licenseTypeName=" + title;
				}
				if (!"".equals(subjectId)) {
					url += "&subjectId=" + subjectId;
				}

				Object obj = RestUtil.getData(url);
				JSONObject json = JSONObject.fromObject(obj);
				if ("200".equals(json.getString("statusCode"))
						&& json.containsKey("cell")) {
					JSONArray licenseArray;
					try {
						licenseArray = json.getJSONArray("cell");
						ds.setRawData(licenseArray);
						ds.setState(StateType.SUCCESS);
						ds.setTotal(json.getInt("total"));
					} catch (Exception e) {
						ds.setState(StateType.FAILT);
						ds.setMessage(e.toString());
						e.printStackTrace();
					}
				} else {
					ds.setState(StateType.FAILT);
					ds.setTotal(0);
					ds.setMessage(json.getString("errors"));
				}
			} else {
				ds.setState(StateType.FAILT);
				ds.setTotal(0);
				ds.setMessage("未获取到证件信息！");
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
			e.printStackTrace();
		}
		return ds;
	}

	/**
	 * 查询证照照面信息（证照模板编码） id 是 String 证照类型码 accessToken 是 String 令牌代码
	 */
	public DataSet getLicenceByLicenceTypeCode(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {

			String id = (String) pSet.get("id");
			String url = HttpUtil.formatUrl(SecurityConfig
					.getString("LicenseUrl")
					+ "/main/licence/getLicenceSurfaceStruct");
			url += "?Access_Token="
					+ SecurityConfig.getString("LicenseAccessToken")
					+ "&licencetype_code=" + id;

			Object obj = RestUtil.getData(url);
			JSONObject json = JSONObject.fromObject(obj);
			if ("200".equals(json.getString("statusCode"))) {
				ds.setRawData(json.getJSONObject("licencedata"));
				ds.setState(StateType.SUCCESS);
				ds.setMessage(json.getString("ret_code"));
			} else {
				ds.setState(StateType.FAILT);
				ds.setTotal(0);
				ds.setMessage(json.getString("errors"));
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
			e.printStackTrace();
		}
		return ds;
	}

	/**
	 * 查询证照照面信息（证照模板编码）
	 * 
	 */
	public DataSet submitLicence(ParameterSet pSet) {

		DataSet ds = new DataSet();
		try {
			JSONObject surfaceData = ((JSONObject) pSet.getParameter("surface"));
			JSONObject metaData = ((JSONObject) pSet.getParameter("metaData"));
			String type = (String) pSet.getParameter("type");
			String attachment = (String) pSet.getParameter("attachment");
			String surfacePicture = (String) pSet
					.getParameter("surfacePicture");
			String attachmentfilename = (String) pSet
					.getParameter("attachmentFilename");
			String surfacePicturefilename = (String) pSet
					.getParameter("surfacePictureFilename");
			JSONObject attachmentJson = new JSONObject();
			JSONArray attachmentJsonarray = new JSONArray();
			// 处理附件
			if (attachment != null && !attachment.equals("")) {
				String[] upfile = attachment.split(";");
				String[] filename = attachmentfilename.split(";");
				for (int i = 0; i < upfile.length; i++) {
					try {
						String base64 = fileToBase64(upfile[i], filename[i]);
						attachmentJson.put(
								"fileName",
								filename[i].substring(0,
										filename[i].indexOf(".")));
						attachmentJson
								.put("fileType", filename[i]
										.substring(filename[i].indexOf(".")));
						attachmentJson.put("fileContent", base64);
						attachmentJson.put("beCopy", 1);
						attachmentJson.put("sequence", i + 1);
						attachmentJsonarray.add(attachmentJson);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			// 处理照面上的图片
			if (surfacePicture != null && !surfacePicture.equals("")) {
				String[] upfile = surfacePicture.split(";");
				String[] filename = surfacePicturefilename.split(";");
				for (int i = 0; i < upfile.length; i++) {
					try {
						String base64 = fileToBase64(upfile[i], filename[i]);
						String pfiletag = (String) pSet
								.getParameter("pfiletag");
						surfaceData.put(pfiletag, base64);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			String url = HttpUtil.formatUrl(SecurityConfig
					.getString("LicenseUrl")
					+ "/main/licence/submitLicenceDataByRule");
			JSONObject json1 = new JSONObject();
			if (metaData != null) {
				json1.put("metaData", metaData.toString());
			}
			if (surfaceData != null) {
				json1.put("surfaceData", surfaceData.toString());
			}
			json1.put("attachment", attachmentJsonarray);
			json1.put("accessToken",
					SecurityConfig.getString("LicenseAccessToken"));
			json1.put("forceChecked", "qiqihaer".equals(SecurityConfig
					.getString("AppId")) ? "true" : "false");
			json1.put("type", (String) pSet.getParameter("type"));
			if (type.equals("renew") || type.equals("change")) {
				json1.put("oldData",
						((JSONObject) pSet.getParameter("oldData")).toString());
			}
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost method = new HttpPost(url);
			StringEntity entity = new StringEntity(json1.toString(), "utf-8");
			entity.setContentEncoding("UTF-8");
			entity.setContentType("application/json");
			method.setEntity(entity);
			String obj;
			JSONObject json;
			String retCode;

			HttpResponse result = httpClient.execute(method);
			obj = EntityUtils.toString(result.getEntity());
			json = JSONObject.fromObject(obj);
			retCode = json.getString("retCode");

			if (retCode.equals("FAILED")) {
				ds.setState(StateType.FAILT);
				ds.setMessage(json.getJSONObject("errors").getString("message"));
				ds.setData(json.toString().getBytes(CharsetUtil.UTF_8));
			} else if (retCode.equals("SUCCESS")) {
				ds.setState(StateType.SUCCESS);
				ds.setMessage(retCode);
				ds.setData(json.toString().getBytes(CharsetUtil.UTF_8));
			}
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("证照录入异常！");
			ds.setTotal(0);
		}
		return ds;
	}

	/**
	 * 查询未审核和审核未通过的证照列表（根据证件类型和证件号码） certificateType 是 String 证件类型（参数为对应类型的字典值）
	 * certificateNo 是 String 证件号码 accessToken 是 String 令牌代码
	 */
	public DataSet getPreLicenceListByCertificate(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String state = (String) pSet.get("state") != null ? (String) pSet
					.get("state") : "";
			String id = (String) pSet.get("id");
			String title = pSet.get("title") != null ? (String) pSet
					.get("title") : "";
			DataSet m_ds = UserDao.getInstance().getListExt(id);
			String certificateType = "";
			String certificateNo = "";
			if (m_ds.getTotal() > 0) {
				certificateType = m_ds.getRecord(0).getString("CARD_TYPE");
				certificateNo = m_ds.getRecord(0).getString("CARD_NO");
			}

			if (!"".equals(certificateType) && !"".equals(certificateNo)) {
				String url = HttpUtil.formatUrl(SecurityConfig
						.getString("LicenseUrl")
						+ "/main/licence/getPreLicenceListByCertificate");
				url += "?certificateType=" + certificateType
						+ "&certificateNo=" + certificateNo + "&accessToken="
						+ SecurityConfig.getString("LicenseAccessToken");
				if (!"".equals(state)) {
					url += "&state=" + state;
				}
				Object obj = RestUtil.getData(url);
				JSONObject json = JSONObject.fromObject(obj);
				if ("200".equals(json.getString("statusCode"))
						&& "SUCCESS".equals(json.getString("retCode"))) {
					JSONArray licenseArray;
					licenseArray = json.getJSONArray("licenseArray");
					
					JSONArray resultArray = new JSONArray();
					if (title.length() > 0) {
						for (int i = 0; i < licenseArray.size(); i++) {
							String name = (String) licenseArray
									.getJSONObject(i).get("licenseName");
							if (name.indexOf(title) >= 0) {
								resultArray.add(licenseArray.get(i));
							}
						}
						ds.setRawData(resultArray);

					}else {
						ds.setRawData(licenseArray);
					}
					ds.setState(StateType.SUCCESS);
					ds.setTotal(licenseArray.size());
				} else {
					ds.setState(StateType.FAILT);
					ds.setTotal(0);
					ds.setMessage(json.getString("errors"));
				}
			} else {
				ds.setState(StateType.FAILT);
				ds.setTotal(0);
				ds.setMessage("证件信息错误！");
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
			e.printStackTrace();
		}
		return ds;
	}

	/**
	 * 查询证照照面信息（证照模板编码） id 是 String 证照类型码 accessToken 是 String 令牌代码
	 */
	public DataSet getPreLicenceByTemleteNo(ParameterSet pSet) {

		DataSet ds = new DataSet();
		try {

			String id = (String) pSet.get("id");
			String url = HttpUtil.formatUrl(SecurityConfig
					.getString("LicenseUrl")
					+ "/main/licence/getPreLicenceByTemleteNo");
			url += "?accessToken="
					+ SecurityConfig.getString("LicenseAccessToken")
					+ "&templeteNo=" + id;

			Object obj = RestUtil.getData(url);
			JSONObject json = JSONObject.fromObject(obj);
			if ("200".equals(json.getString("statusCode"))) {
				ds.setRawData(json);
				ds.setState(StateType.SUCCESS);
				ds.setMessage(json.getString("retCode"));
			} else {
				ds.setState(StateType.FAILT);
				ds.setTotal(0);
				ds.setMessage(json.getString("errors"));
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
			e.printStackTrace();
		}
		return ds;
	}

	/**
	 * 提交政务服务网提供的委托授权信息 *
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet submitAgentAuthorityInfo(ParameterSet pSet) {
		String ucid = (String) pSet.get("UCID");
		String licenseNo = (String) pSet.get("licenseNo");
		String licenseName = (String) pSet.get("licenseName");
		String authorizerName = (String) pSet.get("authorizerName");
		String authorizerNo = (String) pSet.get("authorizerNo");
		String approveitemCode = (String) pSet.get("approveitemCode");
		String approveitemName = (String) pSet.get("approveitemName");
		String authorizeeName = (String) pSet.get("authorizeeName");
		String authorizeeNo = (String) pSet.get("authorizeeNo");
		String validTime = (String) pSet.get("validTime");
		String authorizeeucid = (String) pSet.get("authorizeeucid");
		String authorizeeaccount = (String) pSet.get("authorizeeaccount");
		String id = Tools.getUUID32();
		String sql = "insert into LICENSE_AGENTAUTHORITYINFO t (ID,LICENSENO,LICENSENAME,AUTHORIZERNAME,"
				+ "AUTHORIZERNO,APPROVEITEMCODE,APPROVEITEMNAME,AUTHORIZEENAME,AUTHORIZEENO,VALIDTIME,AGENTTIME,"
				+ "RETURNID,STATUS,AUTHORIZERUCID,AUTHORIZEEUCID,AUTHORIZEEACCOUNT)"
				+ " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		DataSet ds = new DataSet();
		Timestamp d = null;
		Timestamp d_valid = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String startTime = sdf.format(new Date());
			d = CommonUtils_api.getInstance().parseStringToTimestamp(startTime,
					CommonUtils_api.YYYY_MM_DD_HH_mm_SS);
			d_valid = CommonUtils_api.getInstance().parseStringToTimestamp(
					validTime, CommonUtils_api.YYYY_MM_DD_HH_mm_SS);
			String select_sql = "Select * from LICENSE_AGENTAUTHORITYINFO t where t.status in ('0','1') and t.LICENSENO=? and t.AUTHORIZERUCID=? and t.AUTHORIZEEUCID=? and t.VALIDTIME>?";
			DataSet select_ds = this.executeDataset(select_sql, new Object[] {
					licenseNo, ucid, authorizeeucid, d });
			if (select_ds.getTotal() > 0) {
				ds.setState(StateType.FAILT);
				ds.setMessage("已提交授权请求或已授权");
			} else {
				int i = this.executeUpdate(sql, new Object[] { id, licenseNo,
						licenseName, authorizerName, authorizerNo,
						approveitemCode, approveitemName, authorizeeName,
						authorizeeNo, d_valid, d, "", "-1", ucid,
						authorizeeucid, authorizeeaccount });
				if (i > 0) {
					JSONArray authorityInfo = new JSONArray();
					JSONObject jo_authorityInfo = new JSONObject();
					jo_authorityInfo.put("id", id);
					jo_authorityInfo.put("licenseNo", licenseNo);
					jo_authorityInfo.put("licenseName", licenseName);
					jo_authorityInfo.put("authorizerName", authorizerName);
					jo_authorityInfo.put("authorizerNo", authorizerNo);
					jo_authorityInfo.put("approveitemCode", approveitemCode);
					jo_authorityInfo.put("approveitemName", approveitemName);
					jo_authorityInfo.put("authorizeeName", authorizeeName);
					jo_authorityInfo.put("authorizeeNo", authorizeeNo);
					jo_authorityInfo.put("validTime", validTime);
					authorityInfo.add(jo_authorityInfo);
					// 以下为调用接口
					String url = HttpUtil.formatUrl(SecurityConfig
							.getString("LicenseUrl")
							+ "/main/licence/submitAgentAuthorityInfo");
					JSONObject m_data = new JSONObject();
					m_data.put("accessToken",
							SecurityConfig.getString("LicenseAccessToken"));
					m_data.put("authorityInfo", authorityInfo.toString());
					try {
						JSONObject receive;
						Object ret = RestUtil.postJson(url, m_data);
						receive = JSONObject.fromObject(URLDecoder.decode(
								ret.toString(), "utf-8"));
						if ("200".equals(receive.getString("statusCode"))
								&& "SUCCESS".equals(receive
										.getString("ret_code"))) {
							try {
								String status = "0";//-1提交失败0提交成功1授权成功2授权失败
								sql = "update LICENSE_AGENTAUTHORITYINFO t set t.STATUS = ?,t.RETURNID=? where t.ID=?";
								JSONArray ids = JSONArray.fromObject(receive
										.getString("ids"));
								int len = ids.size();
								if (len > 0) {
									int j = this
											.executeUpdate(
													sql,
													new Object[] {
															status,
															ids.getJSONObject(0)
																	.getString(
																			"returnId"),
															ids.getJSONObject(0)
																	.getString(
																			"giveId") });
									if (j > 0) {
										ds.setState(StateType.SUCCESS);
									} else {
										ds.setState(StateType.FAILT);
										ds.setMessage("本地状态更新失败");
									}
								} else {
									ds.setState(StateType.FAILT);
									ds.setMessage("返回结果异常:" + ids.toString());
								}
							} catch (Exception e) {
								e.printStackTrace();
								ds.setState(StateType.FAILT);
								ds.setMessage("本地状态更新失败");
							}
						} else {
							ds.setState(StateType.FAILT);
							ds.setMessage("接口端写入失败");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					ds.setState(StateType.FAILT);
					ds.setMessage("写入数据库失败！");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("操作失败！");
		}
		return ds;
	}

	/**
	 * 获取授权列表
	 * 
	 * @param pSet
	 * @return
	 */
	public DataSet getAgentAuthorityInfoList(ParameterSet pSet) {
		String ucid = (String) pSet.get("ucid");
		int start = pSet.getPageStart();
		int limit = pSet.getPageLimit();
		DataSet ds = new DataSet();
		Timestamp d = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String startTime = sdf.format(new Date());
			d = CommonUtils_api.getInstance().parseStringToTimestamp(startTime,
					CommonUtils_api.YYYY_MM_DD_HH_mm_SS);
			String select_sql = "Select * from LICENSE_AGENTAUTHORITYINFO t where t.AUTHORIZERUCID=? and t.VALIDTIME>?";
			ds = this.executeDataset(select_sql, start, limit, new Object[] {
					ucid, d });
		} catch (Exception e) {
			e.printStackTrace();
			ds.setState(StateType.FAILT);
			ds.setMessage("操作失败！");
		}
		return ds;
	}

	// 比较时间
	public int compareDate(Date dt1, Date dt2) {
		if (dt1.getTime() > dt2.getTime()) {
			return 1;
		} else if (dt1.getTime() < dt2.getTime()) {
			return -1;
		} else {// 相等
			return 0;
		}
	}

	public DataSet getLicenceListByCertificateTypeAndCertificateNoWarn(
			ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String id = (String) pSet.get("ucid");
			DataSet m_ds = UserDao.getInstance().getListExt(id);
			String certificateType = "";
			String certificateNo = "";
			if (m_ds.getTotal() > 0) {
				certificateType = m_ds.getRecord(0).getString("CARD_TYPE");
				certificateNo = m_ds.getRecord(0).getString("CARD_NO");
			}

			if (!"".equals(certificateType) && !"".equals(certificateNo)) {
				String url = HttpUtil
						.formatUrl(SecurityConfig.getString("LicenseUrl")
								+ "/main/licence/getLicenceListByCertificateTypeAndCertificateNo");
				url += "?certificateType=" + certificateType
						+ "&certificateNo=" + certificateNo + "&accessToken="
						+ SecurityConfig.getString("LicenseAccessToken");
				Object obj = RestUtil.getData(url);
				JSONObject json = JSONObject.fromObject(obj);
				if ("200".equals(json.getString("statusCode"))
						&& "SUCCESS".equals(json.getString("retCode"))) {
					JSONArray licenseArray;
					licenseArray = json.getJSONArray("licenseArray");

					// 组装过期提醒证照列表
					JSONArray warnList = warningDateSortJSONArray(licenseArray);
					// 组装年检提醒证照
					JSONArray validPeriodEndList = validPeriodEndSortJSONArray(licenseArray);
					// 重新组装返回的jsonarray
					JSONArray result = new JSONArray();
					result.add(warnList);
					result.add(validPeriodEndList);
					ds.setRawData(result);
					ds.setState(StateType.SUCCESS);
					ds.setTotal(licenseArray.size());
				} else {
					ds.setState(StateType.FAILT);
					ds.setTotal(0);
					ds.setMessage(json.getString("errors"));
				}
			} else {
				ds.setState(StateType.FAILT);
				ds.setTotal(0);
				ds.setMessage("证件信息错误！");
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
			e.printStackTrace();
		}
		return ds;
	}

	public DataSet getLicenceData(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try {
			String Licence_NO = (String) pSet.get("licenseNo");
			String Licence_number = (String) pSet.get("licenceNumber");
			String DeptCode = (String) pSet.get("deptCode");
			String licencetype_code = (String) pSet.get("licenceTypeCode");

			String url = HttpUtil.formatUrl(SecurityConfig
					.getString("LicenseUrl") + "/main/licence/getLicenceData");
			url += "?licenseNo=" + URLEncoder.encode(Licence_NO, "utf-8")
					+ "&Licence_number=" + Licence_number + "&DeptCode="
					+ DeptCode + "&licencetype_code=" + licencetype_code
					+ "&Access_Token="
					+ SecurityConfig.getString("LicenseAccessToken");
			Object obj = RestUtil.getData(url);
			JSONObject json = JSONObject.fromObject(obj);
			if ("200".equals(json.getString("statusCode"))
					&& "SUCCESS".equals(json.getString("ret_code"))) {
				ds.setRawData(json);
				ds.setState(StateType.SUCCESS);
			}
		} catch (Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
			e.printStackTrace();
		}
		return ds;
	}

	/**
	 * 网盘下载到服务器dir = propath + "file" + File.separator + "upload" +
	 * File.separator;
	 * 
	 * @param fileName
	 * @param doc_id
	 * @param fileType
	 * @throws Exception
	 */
	public String fileToBase64(String docid, String name) {
		String base64 = "";
		String url = SecurityConfig.getString("NetDiskDownloadAddress") + docid;
		String propath = PathUtil.getWebPath();
		String dir = propath + "file" + File.separator + "upload"
				+ File.separator;
		File filecheck = new File(dir + name);
		if (filecheck.exists()) {
			boolean result = filecheck.delete();
			if (!result) {
				log.error("删除文件失败！");
			}
		}
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);
		HttpResponse httpResponse;
		try {
			httpResponse = client.execute(httpget);
			HttpEntity entity = httpResponse.getEntity();
			InputStream is = entity.getContent();
			File directory = new File(dir);
			if (!directory.exists()) {
				boolean result = directory.mkdirs();// 创建目录
				if (!result) {
					log.error("创建目录失败！");
				}
			}
			FileOutputStream fileout = new FileOutputStream(dir + name);
			try {
				byte[] buffer = new byte[50];
				int ch = 0;
				while ((ch = is.read(buffer)) != -1) {
					fileout.write(buffer, 0, ch);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				fileout.close();
				is.close();
			}
			FileInputStream input2 = new FileInputStream(dir + name);
			try {
				BASE64Encoder encoder = null;
				byte[] files2 = new byte[input2.available()];
				while (true) {
					int i = input2.read(files2);
					if (i == -1)
						break;
				}
				base64 = encoder.encode(files2);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				input2.close();
			}

		} catch (ClientProtocolException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return base64;
	}
	public DataSet getLicenseSubjectList(ParameterSet pSet) {
		DataSet ds = new DataSet();
		try{
			String url = HttpUtil.formatUrl(SecurityConfig
					.getString("LicenseUrl")
					+ "/main/licence/getLicenseSubjectList");
			url += "?accessToken="
					+ SecurityConfig.getString("LicenseAccessToken");
			Object obj = RestUtil.getData(url);
			JSONObject json = JSONObject.fromObject(obj);
			if ("200".equals(json.getString("statusCode"))) {
				ds.setRawData(json);
				ds.setState(StateType.SUCCESS);
			}
		}catch(Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
			e.printStackTrace();
		}
		return ds;
	}
	/**
	 * 查询证照委托授权信息
	 * @param pSet
	 * @return
	 */
	public DataSet getLicenseAgentAuthorityState(ParameterSet pSet){
		DataSet ds = new DataSet();
		try{
			String url = HttpUtil.formatUrl(SecurityConfig
					.getString("LicenseUrl")
					+ "/main/licence/getLicenseAgentAuthorityState");
			url += "?accessToken="
					+ SecurityConfig.getString("LicenseAccessToken")+"&licenseNo="+URLEncoder.encode((String)pSet.get("licenseNo"), "utf-8");
			Object obj = RestUtil.getData(url);
			JSONObject json = JSONObject.fromObject(obj);
			if ("200".equals(json.getString("statusCode"))) {
				JSONArray agentAuthority = json.getJSONArray("agentAuthority");
				int len = agentAuthority.size();
				for(int i=0;i<len;i++){
					String returnId = agentAuthority.getJSONObject(i).getString("id");
					String state = agentAuthority.getJSONObject(i).getString("state");
					if(!StringUtil.isNotEmpty(returnId)){
						System.out.println("证照授权业务流水号为空！");
						continue;
					}
					if(!StringUtil.isNotEmpty(state)){						
						System.out.println("证照授权业务状态为空！");						
						continue;						
					}
					String select_sql = "Select * from LICENSE_AGENTAUTHORITYINFO t where t.returnid=?";
					DataSet select_ds = this.executeDataset(select_sql, new Object[] { returnId });
					if (select_ds.getTotal() > 0) {							
						String status = "0";//-1提交失败0提交成功1授权成功2授权失败
						if("checked".equals(state)){
							status = "1";
						}else if("uncheck".equals(state)){
							status = "0";
						}else{
							status = "2";
						}
						String sql = "update LICENSE_AGENTAUTHORITYINFO t set t.STATUS = ? where t.RETURNID=? ";
						int k = this.executeUpdate(sql, new Object[] { status,returnId });						
						if(k>0){								
							System.out.println("业务【"+returnId+"】状态"+state+"更新成功！");	
						}else{
							System.out.println("业务【"+returnId+"】状态"+state+"更新失败！");	
						}
					}else{
						ds.setState(StateType.FAILT);
						System.out.println("业务【"+returnId+"】不存在！");		
					}
				}					
				ds.setRawData(json.getJSONArray("agentAuthority").toString());
				ds.setState(StateType.SUCCESS);
			}else{
				ds.setState(StateType.FAILT);
				ds.setMessage(json.getString("errors"));
			}
		}catch(Exception e) {
			ds.setState(StateType.FAILT);
			ds.setMessage(e.toString());
			e.printStackTrace();
		}
		return ds;
	}
}