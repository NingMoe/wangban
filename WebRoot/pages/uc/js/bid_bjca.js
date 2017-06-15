/*
检查控件是否支持当前操作系统和IE版本
返回值：
int rv。0：表示支持。
1：操作系统为98以下
2：浏览器版本低于6.0
3：CSP不支持该算法
4：XML分析器不支持
100：其他未知原因
*/
function SZG_CheckSupport()
{
	 return iCheckSupport();
}

/*
设置招标信封URL。
参数：输入参数，BSTR BuyEnvelopeUrl：招标信封的URL。（绝对路径）
返回值：int rv。0：成功，其他：失败。
*/
function SZG_SetBuyEnvelopeUrl(BuyEnvelopeUrl)
{
	return iSetBuyEnvelopeUrl(BuyEnvelopeUrl);
}
/*
	设置投标人ID
*/
function SZG_SetSellerid(Sellerid)
{
	return iSetSellerid(Sellerid);
}

/*
	添加待处理的投标文件
	BSTR FilePath：文件路径
	返回值：int rv。0：成功，其他：失败。
*/
function SZG_AddFile(FilePath)
{
	return iAddFile(FilePath);
}

/*
	添加投标待处理的字符串
	BSTR sIndata：字符串
	返回值：int rv。0：成功，其他：失败。
*/
function SZG_AddString(sIndata)
{
	return iAddString(sIndata);
}
/*
	清除文件和字符串
*/
function SZG_Clear()
{
	return iClear();
}
/*
	执行投标前加密签名处理
	
*/
function SZG_Update(CertID)
{
	return iUpdate(CertID);
}
/*
	设置投标时间戳
*/
function SZG_SetBidTS(ts)
{
	return iSetBidTS(ts);
}
/*
获得当前操作处理的进度
返回值：int rv。取值范围为0-100。
*/
function SZG_GetProgress()
{
	return igetProgress();
}

/*
	 获得投标总签名
	 BSTR rv。签名值。 出错返回空值。
*/
function SZG_GetTotalSign()
{
 	
	return iGetTotalSign();
}
/*
	 获得投标时间戳请求
	 BSTR rv。时间戳请求。 出错返回空值。
*/
function SZG_GetTsReq()
{
	return iGetTsReq();
}


 
/*
	获取投标信封
	返回值：BSTR rv。投标信封。出错返回空值。
*/
function SZG_BidGetBidEnvelope()
{
	 
	return iGetBidEnvelope();
}
/*
	解密投标信封，解密出其中的一个密钥片段
	输入：BSTR BidEnvelope投标信封的URL。
	输入：BSTR CertID，证书标识。
	返回值：BSTR rv。某一个密钥片段的明文。出错返回空值。
	rv格式：
	<subkey>
  <no>子密钥1的顺序号（从1开始）</no> 
  <originalvalue>解密之前的子密钥(Base64)（注：与投标信封对应）</originalvalue> 
  <value>解密之后的子密钥(Base64)</value> 
  <issuccess>YES</issuccess> 
  <ismust>是否是必须参与解密的子密钥 （YES/NO）</ismust> 
  <is4seller>是否是投标人的子密钥 （YES/NO）</is4seller> 
  </subkey>  
*/
function SZG_DecSubKey(BidEnvelope,CertID)
{
	return iDecSubKey(BidEnvelope,CertID);
}
/*
	取得当前已安装证书的用户列表
	返回：	BSTR			ret		用户列表字符串，格式：(用户名1||证书惟一标识1&&&用户名2||证书惟一标识&&&…)
*/
function SZG_GetUserList()
{
	return igetUserList();
}

/*
	根据证书惟一标识，获取Base64编码的证书字符串
	参数：输入参数，BSTR CertID：证书惟一标识
	返回值：BSTR rv：证书字符串。出错返回空值。
*/
function SZG_ExportUserCert(CertID)
{
	return iexportUserCert(CertID);
}
/*
	根据证书惟一标识，获取Base64编码的交换证书字符串
	参数：输入参数，BSTR CertID：证书惟一标识
	返回值：BSTR rv：证书字符串。出错返回空值。
*/
function SZG_ExportExChangeUserCert(CertID)
{
	return iExportExChangeUserCert(CertID);
}

/*
功能：获取证书信息
参数：	
输入参数 ：BSTR sCert：Base64编码的证书
输入参数：int typ：获取信息的类型	
根据type获得证书信息
Type的值主要有：
						TYPE	意义
						1		证书版本
						2		证书序列号
						4		证书发放者国家名
						5		证书发放者组织名
						6		证书发放者部门名
						7		证书发放者省州名
						8		证书发放者通用名
						9		证书发放者城市名
						10	证书发放者EMAIL地址
						11	证书有效期起始
						12	证书有效期截止
						13	用户国家名
						14	用户组织名
						15	用户部门名
						16	用户省州名
						17	用户通用名
						18	用户城市名
						19	用户EMAIL地址
返回：	BSTR			ret		 证书信息。出错返回空值。
*/
function SZG_GetUserInfo(sCert,typ)
{
	return igetUserInfo(sCert,typ);
}
/*
	根据OID获取证书私有扩展项信息
	参数：	
	输入参数 ：BSTR sCert：Base64编码的证书
	输入参数：BSTR oid：私有扩展对象ID，比如“1.2.18.21.88.2”
	返回：	BSTR			ret:：证书OID对应的值。出错返回空值。
*/
function SZG_GetUserInfoByOid(sCert,oid)
{
	var uniqid = igetUserInfoByOid(sCert,oid);
	var head = uniqid.charCodeAt(0);
	if(head==12){
		uniqid = uniqid.substring(2,uniqid.length);
	}
	return uniqid;
}
/*
功能：验证证书有效性
参数：	
输入参数 ：BSTR sCert：Base64编码的证书
返回：	int rv
0：验证成功
其他：验证失败

*/
function SZG_ValidateCert(sCert)
{
	return ivalidateCert(sCert);
}

/*
功能：数字签名
参数：	
输入参数 ：BSTR CertID：证书标识
输入参数：BSTR  InData	：签名原文
返回：	BSTR			ret		签名结果。出错返回空值。
*/
function SZG_SignedData(CertID,InData)
{
	return iSignedData(CertID,InData);
}

/*
	功能：验证数字签名
	参数：	
	输入参数 ：BSTR Cert：签名者证书
	输入参数：BSTR  InData	：签名原文
	输入参数：BSTR  SignValue：签名值
	返回：	0：验证成功
	其他验证失败	
*/
function SZG_VerifySignedData(Cert,InData,SignValue)
{
	return iVerifySignedData(Cert,InData,SignValue);
}

/*
功能：对文件数字签名
参数：	
输入参数 ：BSTR CertID：证书标识
输入参数：BSTR  InFile	：签名原文文件路径
返回：	BSTR			ret		签名结果。出错返回空值。
*/
function SZG_SignedFile(CertID,InFile)
{
	return iSignedFile(CertID,InFile);
}
/*
功能：验证文件数字签名
参数：	
输入参数 ：BSTR Cert：签名者证书
输入参数：BSTR  InFile	：签名原文文件路径
输入参数：BSTR  SignValue：签名值
返回：	0：验证成功
其他验证失败。	
*/
function SZG_VerifySignedFile(Cert,InFile,SignValue)
{
	return iVerifySignedFile(Cert,InFile,SignValue);
}
/*
功能：产生随机数
参数：	
输入参数 ：int RanddomLen：待产生的随机数长度（bytes，字节长度）
返回：	BSTR rv：随机数值（Base64编码后的）
*/
function SZG_GenRandom(RanddomLen)
{
	return iGenRandom(RanddomLen);
}

/*
功能：门限算法，拆分秘密。
参数：输入参数：BSTR sSecert：待拆分的秘密（Base64编码后的）
	输入参数：int m：	秘密分割份额
	输入参数，int n：秘密恢复最小份额
	输入参数：int k：设置为恢复秘密时的必选片段的个数。（此参数可选，不设置标识任意n份片段均可恢复秘密）。
返回值：BSTR rv：拆分后的密码（Base64编码）。共m份，以&&&相连。例如“1111111111111&&&222222222&&&33333333&&&444444444&&&”。出错返回空值。
若有必选片段，则前k份为必选。

*/
function SZG_SecertSegment(Secert,m,n,k)
{
	return iSecertSegment(Secert,m,n,k);
}
/*
功能：使用对称算法加密数据
参数：输入参数：BSTR sKey，加密密钥
	输入参数：BSTR sIndata，待加密的明文
返回值：BSTR rv：加密后的密文。出错返回空值。
*/
function SZG_EncryptData(sKey,sIndata)
{
	return iEncryptData(sKey,sIndata);
}
/*
功能：使用对称算法解密数据
参数：输入参数：BSTR sKey，加密密钥
	输入参数：BSTR sIndata，待解密的密文
返回值：BSTR rv：解密后的明文。出错返回空值。
*/
function SZG_DecryptData(sKey,sIndata)
{
	return iDecryptData(sKey,sIndata);
}
/*
功能：使用对称算法加密数据
参数：输入参数：BSTR sKey，加密密钥
	输入参数：BSTR InFile，待加密的明文文件路径
	输入参数：BSTR OutFile，密文文件保存路径
返回值：int rv：0：成功，其他失败。
*/
function SZG_EncryptFile(sKey,InFile,OutFile)
{
	return iEncryptFile(sKey,InFile,OutFile);
}
/*
功能：使用对称算法解密数据
参数：
	输入参数：BSTR sKey，解密密钥
	输入参数：BSTR InFile，待解密的密文文件路径
	输入参数：BSTR OutFile，明文文件保存路径
返回值：int rv：0：成功，其他失败。

*/
function SZG_DecryptFile(sKey,InFile,OutFile)
{
	return iDecryptFile(sKey,InFile,OutFile);
}
/*
功能：获得操作错误码
参数： 
输入参数 无
返回值：int rv：错误码
*/
function SZG_BidGetLastError()
{
	return iGetLastError();
}
/*
功能：获得操作错误信息
参数： 
输入参数 ：int ErrCode：错误码
返回值：BSTR rv：错误信息。
*/
function SZG_BidGetLastErrorMsg(ErrCode)
{
	return iGetLastErrorMsg(ErrCode);
}
 /*
	获得投标密文文件路径
	参数：int i，密文文件索引,
	和AddFile的顺序对应,从0开始
*/
function SZG_BidGetFileEncPath(i)
{
	 
	var path = iGetFileEncPath(i);
	return path;
}
/*
	 获得投标密文文件大小
	 参数：int file_index，密文文件索引,
	 和AddFile的顺序对应,从0开始
*/
function SZG_BidGetFileEncSize(i)
{
	return iGetFileEncSize(i);
}
/*
	获得投标数据的密文
	参数：int i，密文文件索引,
	 和AddString的顺序对应,从0开始
*/

function SZG_BidGetEncString(i)
{
	return iGetEncString(i);
}

/*
	获取投标文件个数
*/
function SZG_BidGetFileEncCount()
{	 
	 return iBidGetFileEncCount();
}
/*
	获取投标表单字符串个数
*/
function SZG_BidGetEncStringCount()
{	 
	 return iBidGetEncStringCount();
}


/*
	功能：登录函数  
	参数：	
	输入参数：strFormName	：表单名称
	输入参数 ：BSTR CertID：证书标识
	输入参数：strServerRan	：随机数
	返回：	0：验证成功
	其他验证失败	
*/

function SZG_Login(strFormName,strContainerName,strServerRan) 

{
	var ret;
	var objForm = eval(strFormName);

	if (objForm == null) 
	{
		alert("Form Error");
		return false;
	}
	if (strContainerName == null || strContainerName == "") 
	{
		alert("请先选择需要登录的证书");
		return false;
	}
	
	//Add a hidden item ...
	var strSignItem = "<input type=\"hidden\" name=\"UserSignedData\" value=\"\">";
	if (objForm.UserSignedData == null) 
	{
		objForm.insertAdjacentHTML("BeforeEnd",strSignItem);
	}
	var strCertItem = "<input type=\"hidden\" name=\"UserCert\" value=\"\">";
	if (objForm.UserCert == null) 
	{
		objForm.insertAdjacentHTML("BeforeEnd",strCertItem);
	}
	var strContainerItem = "<input type=\"hidden\" name=\"ContainerName\" value=\"\">";
	if (objForm.ContainerName == null) 
	{
		objForm.insertAdjacentHTML("BeforeEnd",strContainerItem);
	}
		
	  //读取用户证书
	  var strUserCert= SZG_ExportUserCert(strContainerName);//获得加密证书
	    
	  if (strUserCert=="")
	  {
		alert("读取用户证书失败");
		return false;
	  }
			 
	  //对随机数进行签名
	  var strClientSignedData = SZG_SignedData(strContainerName,strServerRan);
	  
	  if (strClientSignedData=="")
	  {
		alert("签名失败");
		return false;
	  }
	
	 objForm.UserSignedData.value = strClientSignedData;
	 objForm.UserCert.value = strUserCert;
	 objForm.ContainerName.value = strContainerName;

	return true;
}

/*
pkcs1 格式公钥加密
*/
function  SZG_PubKeyEnc(Cert,Indata)
{
	return iPubKeyEnc(Cert,Indata);
}
/*
pkcs1 格式私钥解密
*/
function SZG_PriKeyDec(CertID,Indata)
{
	return iPriKeyDec(CertID,Indata);
}

/*
xml Sign
*/
function SZG_SignedDataXML(CertID,Indata)
{
	return isignedDataXML(CertID,Indata);
}

/*
verifySignedDataXML
*/
function SZG_VerifySignedDataXML(Indata)
{
	return iverifySignedDataXML(Indata);
}



/*
getXMLSignatureInfo
infotype:
1:XML原文
2:摘要
3:签名值
4:签名证书
5:摘要算法
6:签名算法

*/
function SZG_GetXMLSignatureInfo(Indata,infotype)
{
	return igetXMLSignatureInfo(Indata,infotype);
}


