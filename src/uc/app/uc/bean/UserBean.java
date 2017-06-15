package app.uc.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import net.sf.json.JSONObject;

public class UserBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private long id;
	private String account;
	private String name;
	private String email;
	private String phone;
	private String password;
	private String type;
	private String status;
	private String card_type;
	private String card_no;
	private String org_name;
	private String org_no;
	private String org_boss_type;
	private String org_boss_no;
	private String org_boss_name;
	private String ca_id;	//CA_ID
	private String pubkey;	//公钥
	private String parent_id;
	private Timestamp creation_time;
	private Timestamp last_modification_time;
	private String version_code;
	private String address;
	private String userIdCode;
	private String access_token;
	private String question;
	private String answer;
	private String regionId;
	private String isInuse;
	
	public UserBean() {
		super();
	}
	
	public UserBean(long id, String account, String name, String email,String phone, String password, String type, String status,
			String cardType, String cardNo, String orgName, String orgNo,String orgBossType, String orgBossNo, String orgBossName,
			String caId,String pubkey,String parentId, Timestamp creationTime,Timestamp lastModificationTime, String versionCode,String address,String userIdCode,String regionId,String isInuse) {
		super();
		this.id = id;
		this.account = account;
		this.name = name;
		this.email = email;
		this.phone = phone;
		this.password = password;
		this.type=type;
		this.status=status;
		this.card_type = cardType;
		this.card_no = cardNo;
		this.org_name = orgName;
		this.org_no = orgNo;
		this.org_boss_type = orgBossType;
		this.org_boss_no = orgBossNo;
		this.org_boss_name = orgBossName;
		this.ca_id = caId;
		this.pubkey=pubkey;
		this.parent_id = parentId;
		this.creation_time = creationTime;
		this.last_modification_time = lastModificationTime;
		this.version_code = versionCode;
		this.address = address;
		this.userIdCode = userIdCode;
		this.regionId = regionId;
		this.isInuse = isInuse;
	}
	
	public UserBean(JSONObject json) {
		super();
		this.id = json.getLong("ID");
		this.account = json.getString("ACCOUNT");
		this.name = json.getString("NAME");
		this.email = json.getString("EMAIL");
		this.phone = json.getString("PHONE");
		this.password = json.getString("PASSWORD");
		this.type=json.getString("TYPE");
		this.status=json.getString("STATUS");	
		this.card_type = json.getString("CARD_TYPE");
		this.card_no = json.getString("CARD_NO");
		this.org_name = json.getString("ORG_NAME");
		this.org_no = json.getString("ORG_NO");
		this.org_boss_type = json.getString("ORG_BOSS_TYPE");
		this.org_boss_no = json.getString("ORG_BOSS_NO");
		this.org_boss_name = json.getString("ORG_BOSS_NAME");
		this.ca_id = json.getString("CA_ID");
		this.pubkey=json.getString("PUBKEY");
		this.parent_id = json.getString("PARENT_ID");
		this.version_code = json.getString("VERSION_CODE");
		this.address = json.getString("ADDRESS");
		this.userIdCode = json.getString("USERIDCODE");
		if(json.containsKey("ACCESS_TOKEN")) this.access_token = json.getString("ACCESS_TOKEN");
		this.question = json.getString("QUESTION");
		this.answer = json.getString("ANSWER");
		this.regionId = json.getString("REGION_ID");
		this.isInuse = json.getString("IS_INUSE");
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getCard_type() {
		return card_type;
	}

	public void setCard_type(String cardType) {
		card_type = cardType;
	}

	public String getCard_no() {
		return card_no;
	}

	public void setCard_no(String cardNo) {
		card_no = cardNo;
	}

	public String getOrg_name() {
		return org_name;
	}

	public void setOrg_name(String orgName) {
		org_name = orgName;
	}

	public String getOrg_no() {
		return org_no;
	}

	public void setOrg_no(String orgNo) {
		org_no = orgNo;
	}

	public String getOrg_boss_type() {
		return org_boss_type;
	}

	public void setOrg_boss_type(String orgBossType) {
		org_boss_type = orgBossType;
	}

	public String getOrg_boss_no() {
		return org_boss_no;
	}

	public void setOrg_boss_no(String orgBossNo) {
		org_boss_no = orgBossNo;
	}

	public String getOrg_boss_name() {
		return org_boss_name;
	}

	public void setOrg_boss_name(String orgBossName) {
		org_boss_name = orgBossName;
	}

	public String getCa_id() {
		return ca_id;
	}

	public void setCa_id(String caId) {
		ca_id = caId;
	}

	public String getPubkey() {
		return pubkey;
	}

	public void setPubkey(String pubkey) {
		this.pubkey = pubkey;
	}
	
	public String getParent_id() {
		return parent_id;
	}

	public void setParent_id(String parentId) {
		parent_id = parentId;
	}

	public Date getCreation_time() {
		return creation_time;
	}

	public void setCreation_time(Timestamp creationTime) {
		creation_time = creationTime;
	}

	public Date getLast_modification_time() {
		return last_modification_time;
	}

	public void setLast_modification_time(Timestamp lastModificationTime) {
		last_modification_time = lastModificationTime;
	}

	public String getVersion_code() {
		return version_code;
	}

	public void setVersion_code(String versionCode) {
		version_code = versionCode;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getUserIdCode() {
		return userIdCode;
	}

	public void setUserIdCode(String userIdCode) {
		this.userIdCode = userIdCode;
	}

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getRegionId() {
		return regionId;
	}

	public void setRegionId(String regionId) {
		this.regionId = regionId;
	}

	public String getIsInuse() {
		return isInuse;
	}

	public void setIsInuse(String isInuse) {
		this.isInuse = isInuse;
	}	
	
}
