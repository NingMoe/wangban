package km.app.icity.webservice.entity;
/**
 * 
 * ClassName: TransactionMaterial <br/>
 * date: 2015年10月14日 上午10:57:44 <br/>
 * @author lw
 */
public class TransactionMaterial {
	private String tranId;//办件编号
	private String materialName;//材料名称
	private String materialId;//材料编号
	private String materialType;//提交材料类型（多种类型用半角逗号分隔）
	private String materialNumber;//提交数量
	private String licenseNumber;//许可证件号码
	private String appMaterialFile;//电子材料文件
	private String trueFileName;//真实文件名
	public String getTranId() {
		return tranId;
	}
	public void setTranId(String tranId) {
		this.tranId = tranId;
	}
	public String getMaterialId() {
		return materialId;
	}
	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}
	public String getMaterialName() {
		return materialName;
	}
	public void setMaterialName(String materialName) {
		this.materialName = materialName;
	}
	public String getMaterialType() {
		return materialType;
	}
	public void setMaterialType(String materialType) {
		this.materialType = materialType;
	}
	public String getMaterialNumber() {
		return materialNumber;
	}
	public void setMaterialNumber(String materialNumber) {
		this.materialNumber = materialNumber;
	}
	public String getLicenseNumber() {
		return licenseNumber;
	}
	public void setLicenseNumber(String licenseNumber) {
		this.licenseNumber = licenseNumber;
	}
	public String getAppMaterialFile() {
		return appMaterialFile;
	}
	public void setAppMaterialFile(String appMaterialFile) {
		this.appMaterialFile = appMaterialFile;
	}
	public String getTrueFileName() {
		return trueFileName;
	}
	public void setTrueFileName(String trueFileName) {
		this.trueFileName = trueFileName;
	}
	
}