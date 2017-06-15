package app.icity.govservice;

import com.inspur.base.BaseQueryCommand;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

public class LicenseCmd extends BaseQueryCommand{ 

	/**
	 * 从自己的证照系统下载证照文件到本地 重庆需求
	 * licenseNo	是	String	证照编号
	 * accessToken	是	String	令牌代码(qd)
	 */
	public DataSet downloadLicense(ParameterSet pSet){
		return LicenseDao.getInstance().downloadLicense(pSet);
	}
	/**
	 * 查询证照列表（根据证件类型和证件号码）
	 * certificateType	是	String	证件类型（参数为对应类型的字典值）
	 *  certificateNo	是	String	证件号码
	  * accessToken	是	String	令牌代码
	 */
	public DataSet getLicenceListByCertificateTypeAndCertificateNo(ParameterSet pSet){
		return LicenseDao.getInstance().getLicenceListByCertificateTypeAndCertificateNo(pSet);
	}
	/**
	 * 外网打证   
	 * licenseNo	是	String	证照编号
	 * accessToken	是	String	令牌代码(qd)
	 */
	public DataSet getLicencePictureFile(ParameterSet pSet){
		return LicenseDao.getInstance().getLicencePictureFile(pSet);
	}
	/**
	 * 根据证照编号、持证者类型，证照类型编号查询证照信息 
	 * by:yanhao
	 * 参数：
	 * { “licenseNumber”: ”照面编号”,  
	 *	“holderType”:”持证者类型”,  
	 *	“licenseTypeCode”:”证照模板编号”
	 *	}
	 * @param pSet
	 * @return
	 */
	public DataSet getLicenseInfoByMutliRequirement(ParameterSet pSet){
		return LicenseDao.getInstance().getLicenseInfoByMutliRequirement(pSet);
	}
	
	/**
	 * 根据证照编号 获取证照信息及其照面信息
	 * Licence_NO	String	证照编号
	 * Licence_number String	证照编号，照面上的编号
	 * DeptCode	String	颁发单位机构代码
	 * licencetype_code String	证照类型编码
	 * Access_Token String	令牌代码
	 *
	 * @param pSet
	 * @return
	 */
	public DataSet getLicenceData(ParameterSet pSet){
		return LicenseDao.getInstance().getLicenceData(pSet);
	}
	/**
	 * 根据照面编号获得证照信息
	 * 提供证照展示页面
	 * @param pSet
	 * @return
	 */
	public DataSet getLicenceInfoByNumber(ParameterSet pSet){
		return LicenseDao.getInstance().getLicenceInfoByNumber(pSet);
	}
	/**
	 * 根据申办流水号获取证照列表
	 * @param pSet
	 * @return
	 */
	public DataSet getLicenceListBySblsh(ParameterSet pSet){
		return LicenseDao.getInstance().getLicenceListBySblsh(pSet);
	}
	
	/**
	 * 根部门编码获取证照列表
	 * @param pSet
	 * @return
	 */
	public DataSet getLicenceListByDeptCode(ParameterSet pSet){
		return LicenseDao.getInstance().getLicenceListByDeptCode(pSet);
	}
	
	/**
	 * 根据证照模板代码获取证照照面信息
	 * @param pSet
	 * @return
	 */
	public DataSet getLicenceByLicenceTypeCode(ParameterSet pSet){
		return LicenseDao.getInstance().getLicenceByLicenceTypeCode(pSet);
	}
	
	/**
	 * 提交录入的证照信息
	 * @param pSet
	 * @return
	 */
	public DataSet submitLicence(ParameterSet pSet){
		return LicenseDao.getInstance().submitLicence(pSet);
	}
	
	/**
	 * 查询未审核和审核未通过的证照列表（根据证件类型和证件号码）
	 * certificateType	是	String	证件类型（参数为对应类型的字典值）
	 *  certificateNo	是	String	证件号码
	 *  accessToken	是	String	令牌代码
	 */
	public DataSet getPreLicenceListByCertificate(ParameterSet pSet){
		return LicenseDao.getInstance().getPreLicenceListByCertificate(pSet);
	}
	
	/**
	 * 根据证照临时编码获取证照照面数据
	 * certificateType	是	String	证件类型（参数为对应类型的字典值）
	 *  certificateNo	是	String	证件号码
	 * accessToken	是	String	令牌代码
	 */
	public DataSet getPreLicenceByTemleteNo(ParameterSet pSet){
		return LicenseDao.getInstance().getPreLicenceByTemleteNo(pSet);
	}
	/**
	 * 提交政务服务网提供的委托授权信息
	 * @param pSet
	 * @return
	 */
	public DataSet submitAgentAuthorityInfo(ParameterSet pSet){
		return LicenseDao.getInstance().submitAgentAuthorityInfo(pSet);
	}
	/**
	 * 获取授权列表
	 * @param pSet
	 * @return
	 */
	public DataSet getAgentAuthorityInfoList(ParameterSet pSet){
		return LicenseDao.getInstance().getAgentAuthorityInfoList(pSet);
	}
	
	public DataSet getLicenceListByCertificateTypeAndCertificateNoWarn(ParameterSet pSet){
		return LicenseDao.getInstance().getLicenceListByCertificateTypeAndCertificateNoWarn(pSet);
	}
	/**
	 * 查询证照主题列表
	 * 
	 */
	public DataSet getLicenseSubjectList(ParameterSet pSet){
		return LicenseDao.getInstance().getLicenseSubjectList(pSet);
	}
	/**
	 * 查询证照委托授权信息
	 * @param pSet
	 * @return
	 */
	public DataSet getLicenseAgentAuthorityState(ParameterSet pSet){
		return LicenseDao.getInstance().getLicenseAgentAuthorityState(pSet);
	}
}