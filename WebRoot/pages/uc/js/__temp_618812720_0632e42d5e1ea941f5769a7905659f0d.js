/*
	bjca 内部函数 js脚本
 */
/////////////////////全局变量/////////////////////////////
var gBJCA_Progress = 0;// 当前进度
var BJCA_READY = 0; // 准备就绪
var BJCA_PLAIN_SIGN = 1; // 对投标明文签名
var BJCA_PLAIN_ENC = 2; // 对投标明文加密
var BJCA_CIPHER_SIGN = 3; // 对投标密文签名
var BJCA_OK = 0;
var BJCA_ERR = 1;

var gBJCA_CertID = "";

var gBJCA_Steps = 0;// 当前投标操作进行的步骤
var gBJCA_BuyEnvelopeUrl = "";// 投标信封url地址

var gBJCA_bizid = 0;// 业务id
var gBJCA_starttime = "";// 投标开始时间
var gBJCA_endtime = "";// 投标截止时间
var gBJCA_opentime = "";// 开标时间
var gBJCA_sellerismust = "";// 是否是必须投标人参与解密
var gBJCA_m = 0;// 门限算法，最大分割份数
var gBJCA_n = 0;// 门限算法，恢复需要的最小份数
var gBJCA_k = 0;// 必须的个数
var gBJCA_opener_id = new Array();// 开标人编号
var gBJCA_opener_cert = new Array();// 开标人证书
var gBJCA_opener_ismust = new Array();// 是否是必须参与解密的开标人，YES/NO

var gBJCA_Seg = new Array();// 分割的门限片段，前k个必选
var gBJCA_subkey_value = new Array();// 和opener顺序相对应的子密钥（明文）,投标人放在最后一个。
var gBJCA_ssubkey_value = new Array();// 和opener顺序相对应的子密钥（密文）
var gBJCA_key = ""; // 原始密钥
var gBJCA_AllFiles = new Array();// 全部文件
var gBJCA_AllEncFiles = new Array();// 全部密文文件，
var gBJCA_AllEncFilesSize = new Array();// 全部密文文件大小
var gBJCA_AllEncStrings = new Array();// 全部密文字符串
var gBJCA_EncSignValue = "";// 明文文件总签名的密文
var gBJCA_BidEnvelop = "";// 投标信封
var gBJCA_TotalSign = "";// 最后的签名
var gBJCA_TotalTsReq = ""; // 最后的时间戳请求
var gBJCA_buyenvelopsign = "";// 投标人对投标时取得的招标信封的签名
var gBJCA_ssellercert = "";// 加密后的投标人签名证书
var gBJCA_sellerid = "";// 投标人id
var gBJCA_ssellerid;// 加密后的投标人id
var gBJCA_skey = "";// 加密后的密钥
var gBJCA_bidts = "";// 投标时间戳

// ///////////////////全局变量结束/////////////////////////////

// ///////////////////////错误码定义/////////////////////
var gBJCA_LastErr = 0;// 错误码
var Err_NoErr = 0;
var Err_NotNTOS = 1; // 操作系统为98以下
var Err_IEVersion = 2; // 浏览器版本低于6.0
var Err_CSP = 3; // 3：CSP不支持该算法
var Err_XMLDOM = 4; // 4：XML分析器不支持
var Err_OcxNotExit = 5; // 控件未安装或没有此方法
var Err_FileNotExist = 6; // 文件不存在
var Err_StringIsEmpty = 7; // 空字符串
var Err_CertNotVali = 8; // 证书无效
var Err_VerifySign = 9; // 验证签名失败
var Err_EncFileErr = 10; // 文件加密错误
var Err_DecFileErr = 11; // 文件解密错误
var Err_ReadBuyEnvelop = 12; // 解析招标信封出错
var Err_BidSecertSegment = 13;// 拆分并加密密钥出错
var Err_FilesAndStringsSign = 14; // 对投标文件签名失败
var Err_ReadBuyEnvelopHttp = 15;// 获取招标信封出错
var Err_SignErr = 16;// 投标文件签名错误
var Err_EncErr = 17;// 投标文件加密错误
var Err_GenBidEnvelop = 18;// 产生投标信封错误
var Err_SetBidTS = 19;// 设置投标信封时间戳出错
var Err_GenRandKey = 20;// 产生随机密钥出错
var Err_StringNotExist = 21;// 该字符串不存在
var Err_DecBidEnvelopeSubKey = 22;// 解密投标信封子密钥出错
var Err_HTTPGetBidEnvelope = 23;// 读取投标信封URL出错
var Err_ExportUserCert = 24;// 导出证书出错或证书标识不正确
var Err_ParameterErr = 25;// 参数错误

/* 获得浏览器的版本,如果是非IE浏览器则返回0，否则返回IE版本 */
function GetBrowserVersion() {
	var UAversion = navigator.appVersion;
	if (UAversion.indexOf("MSIE") != -1) {
		var IEmajorStart = UAversion.indexOf("MSIE") + 5;
		UAversion = UAversion.substring(IEmajorStart, UAversion.length);
		var IEmajorEnd = UAversion.indexOf(";");
		UAversion = UAversion.substring(0, IEmajorEnd);
		return UAversion;
	} else
		return 0;
}
/*
 * 是否支持微软XML DOM
 * 
 */
function IsSupportMSXMLDOM() {
	try {
		var xmlobj = new ActiveXObject("Microsoft.XMLDOM");
		if (xmlobj != null)
			return true;
		else
			return false;
	} catch (e) {
		return false;
	}
}
/*
 * 检查控件是否支持当前操作系统和IE版本 返回值： int rv。0：表示支持。 1：操作系统为98以下 2：浏览器版本低于6.0 3：CSP不支持该算法
 * 4：XML分析器不支持 100：其他未知原因
 */
function iCheckSupport() {
	var ret;
	try {
		ret = bjcactrl.CheckSupport();
	} catch (e) {
		ret == Err_OcxNotExit;
	}
	if (ret == Err_NotNTOS) {
		gBJCA_LastErr = Err_NotNTOS;
		return gBJCA_LastErr;
	}
	var BrowserVersion = GetBrowserVersion();
	if (BrowserVersion < 6.0) {
		gBJCA_LastErr = Err_IEVersion;
		return gBJCA_LastErr;
	}
	if (!IsSupportMSXMLDOM()) {
		gBJCA_LastErr = Err_XMLDOM;
		return gBJCA_LastErr;
	}
	gBJCA_LastErr = Err_NoErr;
	return gBJCA_LastErr;
}
/*
 * 根据当前控件的对称算法获取随机密钥
 */
function GenRandKey() {
	try {
		var ret = bjcactrl.GetCureEncryptMethod();
		if (ret == "AES-256")
			return bjcactrl.GenRandom(32);
		else if (ret == "DES")
			return bjcactrl.GenRandom(8);
		else if (ret == "T-DES")
			return bjcactrl.GenRandom(24);
		else if (ret == "AES-128")
			return bjcactrl.GenRandom(16);
		else if (ret == "AES-192")
			return bjcactrl.GenRandom(24);
		else
			return bjcactrl.GenRandom(32);
	} catch (e) {
		return "";
	}
}

var xmlhttp;
/*
 * 从Http获得招标信封
 */
function GetBuyEnvelopHttp() {
	try {
		xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
		if (xmlhttp != null) {
			xmlhttp.onreadystatechange = state_Change;
			xmlhttp.open("GET", gBJCA_BuyEnvelopeUrl, true);
			xmlhttp.send(null)
			return true;
		} else
			return false;
	} catch (e) {
		return false;
	}
}
function state_Change() {
	// if xmlhttp shows "loaded"
	if (xmlhttp.readyState == 4) {
		// if "OK"
		if (xmlhttp.status == 200)// HTTP GET OK
		{
			gBJCA_Progress = 2;
			var responseText = xmlhttp.responseText;
			// 解密招标文件
			var BuyEnvelopXml = bjcactrl.DecBuyEnvelopXml(responseText);
			if (!ReadBuyEnvelopXml(gBJCA_CertID, BuyEnvelopXml)) {
				gBJCA_LastErr = Err_ReadBuyEnvelop;
				gBJCA_Steps = BJCA_READY;
				iAllWorkDone(BJCA_ERR);
				return;
			}
			if (!iBidSecertSegment(gBJCA_CertID)) {
				gBJCA_LastErr = Err_ReadBuyEnvelop;
				gBJCA_Steps = BJCA_READY;
				iAllWorkDone(BJCA_ERR);
			} else {
				gBJCA_Progress = 3;

			}

			if (!iFilesAndStringsSign(gBJCA_CertID)) {
				gBJCA_LastErr = Err_FilesAndStringsSign;
				gBJCA_Steps = BJCA_READY;
				iAllWorkDone(BJCA_ERR);
			} else {
				;// alert("iFilesAndStringsSign OK");
			}
		} else {
			// 出错
			gBJCA_LastErr = Err_ReadBuyEnvelopHttp;
			gBJCA_Steps = BJCA_READY;
			iAllWorkDone(BJCA_ERR);
			return;
		}
	} else {
		return;
	}
}

/*
 * 读取投标信封,获取各种参数，包括投标时间、门限参数、开标人等 并对投标信封签名
 */
function ReadBuyEnvelopXml(CertId, BuyEnvelopXml) {
	try {
		var xmlobj = new ActiveXObject("Microsoft.XMLDOM");
		if (!xmlobj) {
			gBJCA_LastErr = Err_XMLDOM;
			return false;
		}
		xmlobj.async = false;
		xmlobj.loadXML(BuyEnvelopXml);
		var Count;
		var i;
		// 获得header信息
		var headernodes = xmlobj.getElementsByTagName("buyenvelop/header");
		gBJCA_bizid = headernodes[0].getElementsByTagName("bizid")[0].text;
		gBJCA_starttime = headernodes[0].getElementsByTagName("starttime")[0].text;
		gBJCA_endtime = headernodes[0].getElementsByTagName("endtime")[0].text;
		gBJCA_opentime = headernodes[0].getElementsByTagName("opentime")[0].text;
		gBJCA_sellerismust = headernodes[0].getElementsByTagName("sellerismust")[0].text;
		if (gBJCA_sellerismust == "YES")
			gBJCA_k = 1;
		else
			gBJCA_k = 0;
		// 获得m ，n
		gBJCA_m = xmlobj.getElementsByTagName("buyenvelop/body/m")[0].text;
		gBJCA_n = xmlobj.getElementsByTagName("buyenvelop/body/n")[0].text;

		// 获得openers信息
		var openersnodes = xmlobj.getElementsByTagName("buyenvelop/body/openers/opener");
		Count = openersnodes.length;
		for (i = 0; i < Count; i++) {
			var myid = openersnodes[i].getElementsByTagName("id")[0].text;
			gBJCA_opener_id[i] = myid;
			var cert = openersnodes[i].getElementsByTagName("cert")[0].text;
			gBJCA_opener_cert[i] = cert;
			var ismust = openersnodes[i].getElementsByTagName("ismust")[0].text;
			gBJCA_opener_ismust[i] = ismust;
			if (ismust == "YES")
				gBJCA_k++;
		}
		gBJCA_buyenvelopsign = bjcactrl.SignedData(CertId, xmlobj.xml);
	}// end try
	catch (e) {
		// alert(e.message);
		return false;
	}
	return true;
}
/*
 * 采用门限算法分割投标密钥，并使用投标证书加密。
 */
function iBidSecertSegment(CertId) {
	try {
		var m = gBJCA_m;
		var n = gBJCA_n;
		m++;
		n++;
		var strSeg = bjcactrl.SecertSegment(gBJCA_key, m, n, gBJCA_k);
		if (strSeg == "")
			return false;
		var i;
		var j = 0;
		while (1)// 把以“&&&”链接的密钥片段，保存在数组Seg，前k个为必选
		{
			i = strSeg.indexOf("&&&");
			if (i <= 0) {
				break;
			}
			var subSeg = strSeg.substring(0, i);
			gBJCA_Seg[j] = subSeg;
			j++;
			var len = strSeg.length;
			strSeg = strSeg.substring(i + 3, len);
		}// end while(1)

		// 前K个片段分配给ismust=YES的证书
		var k_index = 0;
		j = 0;
		for (i = 0; i < gBJCA_opener_ismust.length; i++) {
			if (gBJCA_opener_ismust[i] == "YES") {
				gBJCA_subkey_value[i] = gBJCA_Seg[k_index];
				k_index++;
			} else {
				gBJCA_subkey_value[i] = gBJCA_Seg[gBJCA_k + j];
				j++;
			}
		}// end for
		if (gBJCA_sellerismust == "YES")
			gBJCA_subkey_value[i] = gBJCA_Seg[k_index];
		else
			gBJCA_subkey_value[i] = gBJCA_Seg[gBJCA_k + j];
		// 密钥分配完毕,使用公钥加密

		for (i = 0; i < gBJCA_opener_cert.length; i++) {
			// 对密钥片段，打包一个数字信封
			var RandKey = GenRandKey();
			if (RandKey == "")
				return false;
			var encdata = bjcactrl.EncryptData(RandKey, gBJCA_subkey_value[i]);
			if (encdata == "")
				return false;
			var enckey = bjcactrl.PubKeyEnc(gBJCA_opener_cert[i], RandKey);
			if (enckey == "")
				return false;
			var ssubkey = enckey + "&&&" + encdata;
			gBJCA_ssubkey_value[i] = ssubkey;

		}
		var encCert = bjcactrl.ExportExChangeUserCert(CertId);// 获得加密证书
		var RandKey = GenRandKey();
		var encdata = bjcactrl.EncryptData(RandKey, gBJCA_subkey_value[i]);
		var enckey = bjcactrl.PubKeyEnc(encCert, RandKey);
		var ssubkey = enckey + "&&&" + encdata;
		gBJCA_ssubkey_value[i] = ssubkey;
		// ////////////全部子密钥加密完毕///////////
		var signcert = bjcactrl.ExportUserCert(CertId);
		gBJCA_ssellercert = bjcactrl.EncryptData(gBJCA_key, signcert);// 加密
		// 投标人签名证书

		gBJCA_sellerid = CertId;// eboy 暂时使用Certid 作为gBJCA_sellerid
		gBJCA_ssellerid = bjcactrl.EncryptData(gBJCA_key, gBJCA_sellerid);// 加密投标人id
		gBJCA_skey = bjcactrl.PubKeyEnc(encCert, gBJCA_key);// 加密后的密钥
	} catch (e) {
		// alert(e);
		return false;
	}
	return true;
}
/*
 * 对投标文件和数据签名
 */
function iFilesAndStringsSign(CertId) {
	gBJCA_Steps = BJCA_PLAIN_SIGN;// 开始对明文签名
	if (!bjcactrl.SignUpdate(CertId)) {
		gBJCA_Steps = BJCA_READY;
		return false;
	} else
		return true;
}

/*
 * 产生投标信封
 */
function iGenBidEnvelop() {
	try {
		var i;
		var xmlobj = new ActiveXObject("Microsoft.XMLDOM");
		if (!xmlobj)
			return "";
		xmlobj.async = false;
		// var p = xmlobj.createProcessingInstruction("xml","version='1.0'");
		// xmlobj.insertBefore(p,xmlobj.childNodes(0));
		xmlobj.loadXML("<?xml version=\"1.0\" encoding=\"GB2312\" ?>");
		var bidenvelop_el = xmlobj.createElement('bidenvelop');
		xmlobj.appendChild(bidenvelop_el);
		// 添加header节点
		var header_el = xmlobj.createElement('header');
		var bizid_el = xmlobj.createElement('bizid');// 业务id
		bizid_el.text = gBJCA_bizid;
		var buyenvelopurl_el = xmlobj.createElement('buyenvelopurl');// 投标时取得的招标信封的URL
		buyenvelopurl_el.text = gBJCA_BuyEnvelopeUrl;
		var buyenvelopsign_el = xmlobj.createElement('buyenvelopsign');// 投标人对投标时取得的招标信封的签名
		buyenvelopsign_el.text = gBJCA_buyenvelopsign;
		var ssellercert_el = xmlobj.createElement('ssellercert');// 加密后的投标人签名证书
		ssellercert_el.text = gBJCA_ssellercert;
		var ssellerid_el = xmlobj.createElement('ssellerid');// 加密后的投标人签名证书
		ssellerid_el.text = gBJCA_sellerid;
		var skey_el = xmlobj.createElement('skey');// 加密后的密钥
		skey_el.text = gBJCA_skey;

		header_el.appendChild(bizid_el);
		header_el.appendChild(buyenvelopurl_el);
		header_el.appendChild(buyenvelopsign_el);
		header_el.appendChild(ssellercert_el);
		header_el.appendChild(ssellerid_el);
		header_el.appendChild(skey_el);
		bidenvelop_el.appendChild(header_el);

		// 添加body节点
		var body_el = xmlobj.createElement('body');
		bidenvelop_el.appendChild(body_el);
		// 创建ssubkeys节点
		var ssubkeys_el = xmlobj.createElement('ssubkeys');
		for (i = 0; i < gBJCA_ssubkey_value.length - 1; i++) {
			var ssubkey_el = xmlobj.createElement('ssubkey');
			var no_el = xmlobj.createElement('no');
			var no_test = i + 1;
			no_el.text = no_test;
			var value_el = xmlobj.createElement('value');
			value_el.text = gBJCA_ssubkey_value[i];
			var ismust_el = xmlobj.createElement('ismust');
			ismust_el.text = gBJCA_opener_ismust[i];
			var is4seller_el = xmlobj.createElement('is4seller');
			is4seller_el.text = "NO";
			ssubkey_el.appendChild(no_el);
			ssubkey_el.appendChild(value_el);
			ssubkey_el.appendChild(ismust_el);
			ssubkey_el.appendChild(is4seller_el);
			ssubkeys_el.appendChild(ssubkey_el);
		}
		var ssubkey_el = xmlobj.createElement('ssubkey');
		var no_el = xmlobj.createElement('no');
		var no_test = i + 1;
		no_el.text = no_test;
		var value_el = xmlobj.createElement('value');
		value_el.text = gBJCA_ssubkey_value[i];
		var ismust_el = xmlobj.createElement('ismust');
		ismust_el.text = gBJCA_sellerismust;
		var is4seller_el = xmlobj.createElement('is4seller');
		is4seller_el.text = "YES";
		ssubkey_el.appendChild(no_el);
		ssubkey_el.appendChild(value_el);
		ssubkey_el.appendChild(ismust_el);
		ssubkey_el.appendChild(is4seller_el);
		ssubkeys_el.appendChild(ssubkey_el);

		body_el.appendChild(ssubkeys_el);
		// 添加bid节点
		var bid_el = xmlobj.createElement('bid');
		body_el.appendChild(bid_el);
		var formquantity_el = xmlobj.createElement('formquantity');
		formquantity_el.text = gBJCA_AllEncStrings.length;
		bid_el.appendChild(formquantity_el);
		var filequantity_el = xmlobj.createElement('filequantity');
		filequantity_el.text = gBJCA_AllEncFiles.length;
		bid_el.appendChild(filequantity_el);
		var files_el = xmlobj.createElement('files');
		bid_el.appendChild(files_el);
		for (i = 0; i < gBJCA_AllEncFiles.length; i++) {
			var file_el = xmlobj.createElement('file');
			files_el.appendChild(file_el);
			var id_el = xmlobj.createElement('id');

			// change by zry at 2007.12.17
			// var id_text = i+1;
			var id_text = iGetFileNameFromPath(gBJCA_AllEncFiles[i]);
			id_el.text = id_text;
			file_el.appendChild(id_el);

			var signno_el = xmlobj.createElement('signno');
			signno_el.text = i + 1;
			file_el.appendChild(signno_el);
			var name_el = xmlobj.createElement('name');
			name_el.text = gBJCA_AllFiles[i];
			file_el.appendChild(name_el);

			var digest_el = xmlobj.createElement('digest');
			digest_el.text = "";
			file_el.appendChild(digest_el);
			var remark_el = xmlobj.createElement('remark');
			remark_el.text = "";
			file_el.appendChild(remark_el);
		}
		var forms_el = xmlobj.createElement('forms');
		bid_el.appendChild(forms_el);
		for (i = 0; i < gBJCA_AllEncStrings.length; i++) {
			var form_el = xmlobj.createElement('form');
			forms_el.appendChild(form_el);
			var id_el = xmlobj.createElement('id');
			var id_text = i + 1;
			id_el.text = id_text;
			form_el.appendChild(id_el);
			var signno_el = xmlobj.createElement('signno');
			signno_el.text = id_text;
			form_el.appendChild(signno_el);
			var digest_el = xmlobj.createElement('digest');
			digest_el.text = "";
			form_el.appendChild(digest_el);
			var remark_el = xmlobj.createElement('remark');
			remark_el.text = "";
			form_el.appendChild(remark_el);
		}
		var bidsign_el = xmlobj.createElement('bidsign');
		bidsign_el.text = gBJCA_EncSignValue;
		bid_el.appendChild(bidsign_el);
		return xmlobj.xml;

	} catch (e) {
		// alert("GenBidEnvelop Error:"+e.message);
		return "";
	}

}

/*
 * 某个线程任务完成
 */
function WorkDone() {
	if (gBJCA_Steps == BJCA_PLAIN_SIGN)// 对投标明文签名完成
	{
		gBJCA_TotalTsReq = bjcactrl.GetTsReq();
		var sign = bjcactrl.GetTotalSign();// 获得明文投标文件的总签名
		if ((sign == "") || (gBJCA_TotalTsReq == "")) {
			gBJCA_LastErr = Err_SignErr;
			iAllWorkDone(BJCA_ERR);
			return;
		}
		gBJCA_EncSignValue = bjcactrl.EncryptData(gBJCA_key, sign);// 加密签名值
		if (gBJCA_EncSignValue == "") {
			gBJCA_LastErr = Err_EncErr;
			iAllWorkDone(BJCA_ERR);
			return;
		}
		// 启动线程开始加密投标文件
		gBJCA_Steps = BJCA_PLAIN_ENC;
		if (!bjcactrl.EncUpdate(gBJCA_key)) {
			gBJCA_Steps = BJCA_READY;
			gBJCA_LastErr = Err_EncErr;
			iAllWorkDone(BJCA_ERR);
			return;
		}
		return;
	}// end if BJCA_PLAIN_SIGN
	if (gBJCA_Steps == BJCA_PLAIN_ENC)// 对投标明文加密完成
	{
		var i;
		var count;
		// 获取全部密文文件
		count = bjcactrl.GetFileEncCount();
		for (i = 0; i < count; i++) {
			gBJCA_AllEncFiles[i] = bjcactrl.GetFileEncPath(i);
			gBJCA_AllEncFilesSize[i] = bjcactrl.GetFileEncSize(i);

		}
		count = bjcactrl.GetEncStringCount();
		for (i = 0; i < count; i++) {
			gBJCA_AllEncStrings[i] = bjcactrl.GetEncString(i);

		}
		// 打包投标信封
		gBJCA_BidEnvelop = iGenBidEnvelop();
		if (gBJCA_BidEnvelop == "") {
			gBJCA_Steps = BJCA_READY;
			gBJCA_LastErr = Err_GenBidEnvelop;
			iAllWorkDone(BJCA_ERR);
			return;
		}
		bjcactrl.Clear();
		// 对密文文件和数据以及投标信封计算签名和时间戳
		// Add File
		for (i = 0; i < gBJCA_AllEncFiles.length; i++) {
			bjcactrl.AddFile(gBJCA_AllEncFiles[i]);
		}
		// AddString
		for (i = 0; i < gBJCA_AllEncStrings.length; i++) {
			bjcactrl.AddString(gBJCA_AllEncStrings[i]);
		}
		gBJCA_Steps = BJCA_CIPHER_SIGN;
		if (!bjcactrl.SignUpdate(gBJCA_CertID)) {
			gBJCA_LastErr = Err_SignErr;
			gBJCA_Steps = BJCA_READY;
			iAllWorkDone(BJCA_ERR);
			return;
		}
		return;
	}// end if BJCA_PLAIN_ENC
	if (gBJCA_Steps == BJCA_CIPHER_SIGN)// 完成对投标密文的签名
	{

		var sign = bjcactrl.GetTotalSign();
		if (sign == "") {
			gBJCA_LastErr = Err_SignErr;
			gBJCA_Steps = BJCA_READY;
			iAllWorkDone(BJCA_ERR);
		}
		gBJCA_TotalSign = sign;
		iAllWorkDone(BJCA_OK);// 投标完成
		return;
	}
	return;
}
/*
 * 报告任务完成。
 */
function iAllWorkDone(ret) {
	if (ret == BJCA_ERR) {
		iClear();
	} else
		gBJCA_LastErr = Err_NoErr;
	gBJCA_Progress = 0;
	gBJCA_AllFiles = new Array();
	bjcactrl.AllWorkDone();
}
/*
 * 设置投标时间戳
 */
function iSetBidTS(ts) {
	// 把ts添加到gBJCA_BidEnvelop
	try {
		var xmlobj = new ActiveXObject("Microsoft.XMLDOM");
		if (!xmlobj) {
			gBJCA_LastErr = Err_XMLDOM;
			return false;
		}
		xmlobj.async = false;
		xmlobj.loadXML(gBJCA_BidEnvelop);
		var header_el = xmlobj.getElementsByTagName("bidenvelop/header");
		var bidts_el = xmlobj.createElement('bidts');
		bidts_el.text = ts;
		header_el[0].appendChild(bidts_el);
		var body_el = xmlobj.getElementsByTagName("bidenvelop/body");
		var xmltosign = header_el[0].xml + body_el[0].xml;
		var signvalue = bjcactrl.SignedData(gBJCA_CertID, xmltosign);
		var bidenvelop_el = xmlobj.getElementsByTagName("bidenvelop");

		// 添加 footer 节点
		var footer_el = xmlobj.createElement('footer');
		bidenvelop_el[0].appendChild(footer_el);
		var signdata_el = xmlobj.createElement('signdata');
		signdata_el.text = signvalue;
		footer_el.appendChild(signdata_el);
		var signcert_el = xmlobj.createElement('signcert');
		signcert_el.text = "";
		footer_el.appendChild(signcert_el);
		gBJCA_BidEnvelop = xmlobj.xml;
	} catch (e) {
		gBJCA_LastErr = Err_SetBidTS;
		return false;
	}
	gBJCA_LastErr = Err_NoErr;
	return true;
}

/*
 * 设置招标信封URL。 参数：输入参数，BSTR BuyEnvelopeUrl：招标信封的URL。（绝对路径） 返回值：int rv。0：成功，其他：失败。
 */
function iSetBuyEnvelopeUrl(BuyEnvelopeUrl) {
	gBJCA_BuyEnvelopeUrl = BuyEnvelopeUrl;// 只是保存起来，Updata时才使用
	return 0;
}
/*
 * 设置投标人ID
 */
function iSetSellerid(Sellerid) {
	gBJCA_sellerid = Sellerid;
}
function iGetFileNameFromPath(filepath) {
	var i;
	var ret;
	i = filepath.lastIndexOf("\\");
	if (i == -1) {
		ret = filepath;
		return ret;
	}
	ret = filepath.substring(i + 1, filepath.length);
	// ret = bjcactrl.GB2312ToUTF8(ret);//为防止有汉字，把数据utf-8编码
	return ret;
}
/*
 * 添加待处理的投标文件 BSTR FilePath：文件路径 返回值：int rv。0：成功，其他：失败。
 */
function iAddFile(FilePath) {
	try {
		if (bjcactrl.AddFile(FilePath)) {
			var i = gBJCA_AllFiles.length;
			gBJCA_AllFiles[i] = iGetFileNameFromPath(FilePath);
			return 0;
		} else
			return Err_FileNotExist;
	} catch (e) {
		return Err_OcxNotExit;
	}
}

/*
 * 添加投标待处理的字符串 BSTR sIndata：字符串 返回值：int rv。0：成功，其他：失败。
 */
function iAddString(sIndata) {
	try {
		if (bjcactrl.AddString(sIndata))
			return 0;
		else
			return Err_StringIsEmpty;
	} catch (e) {
		return Err_OcxNotExit;
	}
}
/*
 * 清除文件和字符串
 */
function iClear() {
	try {
		bjcactrl.Clear()
	} catch (e) {
		return;
	}
	// 也需要清空全局数组,即重新new一个新的数组
	gBJCA_opener_id = new Array();
	gBJCA_opener_cert = new Array();
	gBJCA_opener_ismust = new Array();
	gBJCA_Seg = new Array();
	gBJCA_subkey_value = new Array();
	gBJCA_ssubkey_value = new Array();
	gBJCA_AllFiles = new Array();
	gBJCA_AllEncFiles = new Array();
	gBJCA_AllEncFilesSize = new Array();
	gBJCA_AllEncStrings = new Array();
	gBJCA_Steps = 0;
}
/*
 * 执行投标前加密签名处理. 最关键的函数。 处理流程： 1. 产生投标密钥KEY1 根据控件设置的对称算法类型，产生对应长度的随机数作为密钥。 2.
 * 拆分KEY1 使用门限算法m+1、n+1门限拆分KEY1，拆分时指定必选个数。 3.加密密钥片段 使用开标者证书加密密钥片段。 4. 加密并签名
 * 对所以表单数据和文件逐个加密，并计算总的签名。最后把签名值也加密。文件签名后保存在临时目录下。 5. 打包投标信封
 * 投标信封xml包括密钥片段的密文、文件签名值的密文、文件的顺序等信息 6.数字签名和时间戳请求
 * 对全部投标密文文件、密文表单、投标信封计算签名和时间戳请求。 7.并在全部过程中设置当前进度。
 * 8.处理完成后控件内部保存当前全部状态，包括标书文件的顺序、路径、大小;总签名值、时间戳请求、投标信封等。以备后续函数获取。
 */
function iUpdate(CertID) {
	// 把之前的状态清空
	gBJCA_opener_id = new Array();
	gBJCA_opener_cert = new Array();
	gBJCA_opener_ismust = new Array();
	gBJCA_Seg = new Array();
	gBJCA_subkey_value = new Array();
	gBJCA_ssubkey_value = new Array();
	gBJCA_AllEncFiles = new Array();
	gBJCA_AllEncFilesSize = new Array();
	gBJCA_AllEncStrings = new Array();
	gBJCA_Steps = 0;

	gBJCA_LastErr = Err_NoErr;
	gBJCA_CertID = CertID;
	gBJCA_key = GenRandKey();// 产生投标密钥
	if (gBJCA_key == "") {
		gBJCA_LastErr = Err_GenRandKey;
		gBJCA_Progress = 0;
		return Err_GenRandKey;
	} else
		gBJCA_Progress = 1;
	if (!GetBuyEnvelopHttp()) {
		gBJCA_LastErr = Err_ReadBuyEnvelopHttp;
		return 1;
	} else {
		gBJCA_Progress = 0;
		gBJCA_LastErr = Err_NoErr;
		return 0;
	}
}
/*
 * 获得当前操作处理的进度 返回值：int rv。取值范围为0-100。
 */
function igetProgress() {

	var iProgress;
	if (gBJCA_Steps == BJCA_READY)// 准备就绪
	{
		iProgress = gBJCA_Progress;
	} else if (gBJCA_Steps == BJCA_PLAIN_SIGN)// 正在第一步签名
	{
		iProgress = bjcactrl.GetProgress() / 4 + gBJCA_Progress;
	} else if (gBJCA_Steps == BJCA_PLAIN_ENC)// 正在加密
	{
		iProgress = bjcactrl.GetProgress() / 2 + gBJCA_Progress + 25;
	} else if (gBJCA_Steps == BJCA_CIPHER_SIGN)// 正在第二步签名
	{
		iProgress = bjcactrl.GetProgress() / 4 + gBJCA_Progress + 75;
	} else
		iProgress = 0;
	if (iProgress > 99)
		iProgress = 99;
	return parseInt(iProgress);
}

/*
 * 获得投标密文文件路径 参数：int file_index，密文文件索引, 和AddFile的顺序对应,从0开始
 */
function iGetFileEncPath(file_index) {
	try {
		var ret = gBJCA_AllEncFiles[file_index];
		gBJCA_LastErr = Err_NoErr;
		return ret;
	} catch (e) {
		gBJCA_LastErr = Err_FileNotExist;
		return "";
	}
}
/*
 * 获得投标密文文件大小 参数：int file_index，密文文件索引, 和AddFile的顺序对应,从0开始
 */
function iGetFileEncSize(file_index) {
	try {
		var ret = gBJCA_AllEncFilesSize[file_index];
		gBJCA_LastErr = Err_NoErr;
		return ret;
	} catch (e) {
		gBJCA_LastErr = Err_StringNotExist;
		return 0;
	}
}
/*
 * 获得投标总签名 BSTR rv。签名值。 出错返回空值。
 */
function iGetTotalSign() {
	if (gBJCA_TotalSign == "") {
		gBJCA_LastErr = Err_StringNotExist;
	} else {
		gBJCA_LastErr = Err_NoErr;
	}
	return gBJCA_TotalSign;
}
/*
 * 获得投标时间戳请求 BSTR rv。时间戳请求。 出错返回空值。
 */
function iGetTsReq() {
	if (gBJCA_TotalTsReq == "") {
		gBJCA_LastErr = Err_StringNotExist;
	} else {
		gBJCA_LastErr = Err_NoErr;
	}
	return gBJCA_TotalTsReq;
}

/*
 * 获得投标数据的密文 参数：int string_index，密文文件索引, 和AddString的顺序对应,从0开始
 */
function iGetEncString(string_index) {
	try {
		var ret = gBJCA_AllEncStrings[string_index];
		gBJCA_LastErr = Err_NoErr;
		return ret;
	} catch (e) {
		gBJCA_LastErr = Err_StringNotExist;
		return "";
	}
}

/*
 * 获取投标信封 返回值：BSTR rv。投标信封。出错返回空值。
 */
function iGetBidEnvelope() {
	if (gBJCA_BidEnvelop == "") {
		gBJCA_LastErr = Err_StringNotExist;
	} else {
		gBJCA_LastErr = Err_NoErr;
	}
	var ret = "<?xml version=\"1.0\" encoding=\"GB2312\" ?>" + gBJCA_BidEnvelop;
	return ret;
}
/*
 * 解密投标信封的分割密钥片段 返回： <subkey> <no>子密钥1的顺序号（从1开始）</no>
 * <originalvalue>解密之前的子密钥(Base64)（注：与投标信封对应）</originalvalue>
 * <value>解密之后的子密钥(Base64)</value> <issuccess>YES</issuccess>
 * <ismust>是否是必须参与解密的子密钥 （YES/NO）</ismust> <is4seller>是否是投标人的子密钥 （YES/NO）</is4seller>
 * </subkey>
 * 
 */
function DecBidEnvelopeSubKey(BidEnvelopeXmlObj, CertID) {
	try {
		var xmlobj = BidEnvelopeXmlObj;
		var subkeys = xmlobj.getElementsByTagName("bidenvelop/body/ssubkeys/ssubkey");
		var i;
		for (i = 0; i < subkeys.length; i++) {
			var no_text = subkeys[i].getElementsByTagName("no")[0].text;
			var value_text = subkeys[i].getElementsByTagName("value")[0].text;
			var ismust_text = subkeys[i].getElementsByTagName("ismust")[0].text;
			var is4seller_text = subkeys[i].getElementsByTagName("is4seller")[0].text;
			var j;
			j = value_text.indexOf("&&&");
			if (j == -1)
				continue;
			var skey = value_text.substring(0, j);
			var cipherdata = value_text.substring(j + 3, value_text.length);
			// 解密value_text
			var key = bjcactrl.PriKeyDec(CertID, skey);
			if (key == "")
				continue;

			var subkey = bjcactrl.DecryptData(key, cipherdata);
			if (subkey == "")
				continue;
			else {
				// 解密成功，组返回数据
				var ret = "<subkey><no>";
				ret += no_text;
				ret += "</no><originalvalue>";
				value_text = skey + "&amp;&amp;&amp;" + cipherdata;
				ret += value_text;
				ret += "</originalvalue><value>";
				ret += subkey;
				ret += "</value><issuccess>YES</issuccess><ismust>";
				ret += ismust_text;
				ret += "</ismust><is4seller>"
				ret += is4seller_text;
				ret += "</is4seller></subkey>"
				gBJCA_LastErr = Err_NoErr;
				return ret;
			}
		}
	} catch (e) {
		gBJCA_LastErr = Err_DecBidEnvelopeSubKey;
		return "";
	}
}
/*
 * 通过http读取投标信封。
 */
var xmlhttp1;
function HTTPGetBidEnvelope(BidEnvelopeUrl, CertID) {
	try {
		xmlhttp1 = new ActiveXObject("Microsoft.XMLHTTP");
		if (xmlhttp1 != null) {
			// xmlhttp1.onreadystatechange=BidEnvelopeState_Change;
			xmlhttp1.open("GET", BidEnvelopeUrl, false);
			xmlhttp1.send(null);
			if (xmlhttp1.status == 200) {

				var responseXML = xmlhttp1.responseXML;
				var ret = DecBidEnvelopeSubKey(responseXML, CertID);
				return ret;
			} else {
				gBJCA_LastErr = Err_HTTPGetBidEnvelope;
				return "";
			}
		} else {
			gBJCA_LastErr = Err_HTTPGetBidEnvelope;
			return "";
		}
	} catch (e) {
		gBJCA_LastErr = Err_HTTPGetBidEnvelope;
		return "";
	}
}

/*
 * 解密投标信封，解密出其中的一个密钥片段 输入：BSTR BidEnvelope投标信封的URL。 输入：BSTR CertID，证书标识。
 * 返回值：BSTR rv。某一个密钥片段的明文。出错返回空值。 <subkey> <no>子密钥1的顺序号（从1开始）</no>
 * <originalvalue>解密之前的子密钥(Base64)（注：与投标信封对应）</originalvalue>
 * <value>解密之后的子密钥(Base64)</value> <issuccess>YES</issuccess>
 * <ismust>是否是必须参与解密的子密钥 （YES/NO）</ismust> <is4seller>是否是投标人的子密钥 （YES/NO）</is4seller>
 * </subkey>
 */
function iDecSubKey(BidEnvelope, CertID) {

	bjcactrl.SignedData(CertID, "针对飞天key的bug，解密时不自动弹出口令输入框");
	return HTTPGetBidEnvelope(BidEnvelope, CertID);

}

/*
 * 取得当前已安装证书的用户列表 返回： BSTR ret 用户列表字符串，格式：(用户名1||证书惟一标识1&&&用户名2||证书惟一标识&&&…)
 */
function igetUserList() {
	try {
		return bjcactrl.GetUserList();
	} catch (e) {
		return "";
	}
}

/*
 * 根据证书惟一标识，获取Base64编码的证书字符串 参数：输入参数，BSTR CertID：证书惟一标识 返回值：BSTR
 * rv：证书字符串。出错返回空值。
 */
function iexportUserCert(CertID) {
	var ret = bjcactrl.ExportUserCert(CertID);
	if (ret == "") {
		gBJCA_LastErr = Err_ExportUserCert;
	} else
		gBJCA_LastErr = Err_NoErr;
	return ret;
}
/*
 * 根据证书惟一标识，获取Base64编码的交换证书字符串 参数：输入参数，BSTR CertID：证书惟一标识 返回值：BSTR
 * rv：证书字符串。出错返回空值。
 */
function iExportExChangeUserCert(CertID) {
	var ret = bjcactrl.ExportExChangeUserCert(CertID);
	if (ret == "") {
		gBJCA_LastErr = Err_ExportUserCert;
	} else
		gBJCA_LastErr = Err_NoErr;
	return ret;
}

/*
 * 功能：获取证书信息 参数： 输入参数 ：BSTR sCert：Base64编码的证书 输入参数：int typ：获取信息的类型 根据type获得证书信息
 * Type的值主要有： TYPE 意义 1 证书版本 2 证书序列号 4 证书发放者国家名 5 证书发放者组织名 6 证书发放者部门名 7 证书发放者省州名
 * 8 证书发放者通用名 9 证书发放者城市名 10 证书发放者EMAIL地址 11 证书有效期起始 12 证书有效期截止 13 用户国家名 14 用户组织名
 * 15 用户部门名 16 用户省州名 17 用户通用名 18 用户城市名 19 用户EMAIL地址 返回： BSTR ret 证书信息。出错返回空值。
 */
function igetUserInfo(sCert, typ) {
	if ((typ < 1) || (typ > 19)) {
		gBJCA_LastErr = Err_ParameterErr;
		return "";
	}
	var ret = bjcactrl.GetUserInfo(sCert, typ);
	return ret;
}

/*
 * 根据OID获取证书私有扩展项信息 参数： 输入参数 ：BSTR sCert：Base64编码的证书 输入参数：BSTR
 * oid：私有扩展对象ID，比如“1.2.18.21.88.2” 返回： BSTR ret:：证书OID对应的值。出错返回空值。
 */
function igetUserInfoByOid(sCert, oid) {
	return bjcactrl.GetUserInfoByOid(sCert, oid);
}
/*
 * 功能：验证证书有效性 参数： 输入参数 ：BSTR sCert：Base64编码的证书 返回： int rv 0：验证成功 其他：验证失败
 * 
 */
function iValidateCert(sCert) {
	if (!bjcactrl.ValidateCert(sCert))
		return Err_CertNotVali;
	else
		return 0;
}
/*
 * 功能：数字签名 参数： 输入参数 ：BSTR CertID：证书标识 输入参数：BSTR InData ：签名原文 返回： BSTR ret
 * 签名结果。出错返回空值。
 */
function iSignedData(CertID, InData) {
	return bjcactrl.SignedData(CertID, InData);
}

/*
 * 功能：验证数字签名 参数： 输入参数 ：BSTR Cert：签名者证书 输入参数：BSTR InData ：签名原文 输入参数：BSTR
 * SignValue：签名值 返回： 0：验证成功 其他验证失败
 */
function iVerifySignedData(Cert, InData, SignValue) {
	if (!bjcactrl.VerifySignedData(Cert, InData, SignValue)) {
		return Err_VerifySign;
	} else
		return 0;
}

/*
 * 功能：对文件数字签名 参数： 输入参数 ：BSTR CertID：证书标识 输入参数：BSTR InFile ：签名原文文件路径 返回： BSTR ret
 * 签名结果。出错返回空值。
 */
function iSignedFile(CertID, InData) {
	return bjcactrl.SignedFile(CertID, InData);
}
/*
 * 功能：验证文件数字签名 参数： 输入参数 ：BSTR Cert：签名者证书 输入参数：BSTR InFile ：签名原文文件路径 输入参数：BSTR
 * SignValue：签名值 返回： 0：验证成功 其他验证失败。
 */
function iVerifySignedFile(Cert, InFile, SignValue) {
	if (!bjcactrl.VerifySignedFile(Cert, InFile, SignValue)) {
		return Err_VerifySign;
	} else
		return 0;
}
/*
 * 功能：产生随机数 参数： 输入参数 ：int RanddomLen：待产生的随机数长度（bytes，字节长度） 返回： BSTR
 * rv：随机数值（Base64编码后的）
 */
function iGenRandom(RanddomLen) {
	return bjcactrl.GenRandom(RanddomLen);
}

/*
 * 功能：门限算法，拆分秘密。 参数：输入参数：BSTR sSecert：待拆分的秘密（Base64编码后的） 输入参数：int m： 秘密分割份额
 * 输入参数，int n：秘密恢复最小份额 输入参数：int k：设置为恢复秘密时的必选片段的个数。（此参数可选，不设置标识任意n份片段均可恢复秘密）。
 * 返回值：BSTR
 * rv：拆分后的密码（Base64编码）。共m份，以&&&相连。例如“1111111111111&&&222222222&&&33333333&&&444444444&&&”。出错返回空值。
 * 若有必选片段，则前k份为必选。
 * 
 */
function iSecertSegment(Secert, m, n, k) {
	return bjcactrl.SecertSegment(Secert, m, n, k);
}

/*
 * 功能：使用对称算法加密数据 参数：输入参数：BSTR sKey，加密密钥 输入参数：BSTR sIndata，待加密的明文 返回值：BSTR
 * rv：加密后的密文。出错返回空值。
 */
function iEncryptData(sKey, sIndata) {
	return bjcactrl.EncryptData(sKey, sIndata);
}

/*
 * 功能：使用对称算法解密数据 参数：输入参数：BSTR sKey，加密密钥 输入参数：BSTR sIndata，待解密的密文 返回值：BSTR
 * rv：解密后的明文。出错返回空值。
 */
function iDecryptData(sKey, sIndata) {
	return bjcactrl.DecryptData(sKey, sIndata);
}
/*
 * 功能：使用对称算法加密数据 参数：输入参数：BSTR sKey，加密密钥 输入参数：BSTR InFile，待加密的明文文件路径 输入参数：BSTR
 * OutFile，密文文件保存路径 返回值：int rv：0：成功，其他失败。
 */
function iEncryptFile(sKey, InFile, OutFile) {
	if (!bjcactrl.EncryptFile(sKey, InFile, OutFile)) {
		return Err_EncFileErr;
	} else
		return 0;
}
/*
 * 功能：使用对称算法解密数据 参数： 输入参数：BSTR sKey，解密密钥 输入参数：BSTR InFile，待解密的密文文件路径 输入参数：BSTR
 * OutFile，明文文件保存路径 返回值：int rv：0：成功，其他失败。
 * 
 */
function iDecryptFile(sKey, InFile, OutFile) {
	if (!bjcactrl.DecryptFile(sKey, InFile, OutFile)) {
		return Err_DecFileErr;
	} else
		return 0;
}
/*
 * 功能：获得操作错误码 参数： 输入参数 无 返回值：int rv：错误码
 */
function iGetLastError() {
	return gBJCA_LastErr;
}
/*
 * 功能：获得操作错误信息 参数： 输入参数 ：int ErrCode：错误码 返回值：BSTR rv：错误信息。
 */
function iGetLastErrorMsg(ErrCode) {
	switch (ErrCode) {
	case Err_NoErr:
		return "正确";
	case Err_NotNTOS:
		return "操作系统为98以下";
	case Err_IEVersion:
		return "浏览器版本低于6.0";
	case Err_CSP:
		return "CSP不支持该算法";
	case Err_XMLDOM:
		return "XML分析器不支持";
	case Err_OcxNotExit:
		return "控件未安装或没有此方法";
	case Err_FileNotExist:
		return "文件不存在";
	case Err_StringIsEmpty:
		return "空字符串";
	case Err_CertNotVali:
		return "证书无效";
	case Err_VerifySign:
		return "验证签名失败";
	case Err_EncFileErr:
		return "文件加密错误";
	case Err_DecFileErr:
		return "文件解密错误";
	case Err_ReadBuyEnvelop:
		return "解析招标信封出错";
	case Err_BidSecertSegment:
		return "拆分并加密密钥出错";
	case Err_FilesAndStringsSign:
		return "对投标文件签名失败";
	case Err_ReadBuyEnvelopHttp:
		return "获取招标信封出错";
	case Err_SignErr:
		return "投标文件签名错误";
	case Err_EncErr:
		return "投标文件加密错误";
	case Err_GenBidEnvelop:
		return "产生投标信封错误";
	case Err_SetBidTS:
		return "设置投标信封时间戳出错";
	case Err_GenRandKey:
		return "产生随机密钥出错";
	case Err_StringNotExist:
		return "该字符串不存在";
	case Err_DecBidEnvelopeSubKey:
		return "解密投标信封子密钥出错";
	case Err_HTTPGetBidEnvelope:
		return "读取投标信封URL出错";
	case Err_ExportUserCert:
		return "导出证书出错或证书标识不正确";
	case Err_ParameterErr:
		return "参数错误";
	default:
		return "";
	}
	return "";
}

/*
 * 获取投标文件个数
 */
function iBidGetFileEncCount() {
	try {
		var ret = gBJCA_AllEncFiles.length;
		return ret;
	} catch (e) {
		return 0;
	}
}
/*
 * 获取投标表单字符串个数
 */
function iBidGetEncStringCount() {
	try {
		var ret = gBJCA_AllEncStrings.length;
		return ret;
	} catch (e) {
		return 0;
	}
}
/*
 * pkcs1 格式公钥加密
 */
function iPubKeyEnc(Cert, Indata) {
	try {
		var ret = bjcactrl.PubKeyEnc(Cert, Indata);
		return ret;
	} catch (e) {
		return "";
	}
}

/*
 * pkcs1 格式私钥解密
 */
function iPriKeyDec(CertID, Indata) {
	try {
		var ret = bjcactrl.PriKeyDec(CertID, Indata);
		return ret;
	} catch (e) {
		return "";
	}
}

/*
 * xml Sign
 */
function isignedDataXML(CertID, Indata) {
	try {
		var ret = bjcactrl.signedDataXML(CertID, Indata);
		return ret;
	} catch (e) {
		return "";
	}
}
/*
 * verifySignedDataXML
 */
function iverifySignedDataXML(Indata) {
	try {
		var ret = bjcactrl.verifySignedDataXML(Indata);
		return ret;
	} catch (e) {
		return false;
	}
}

/*
 * getXMLSignatureInfo infotype: 1:XML原文 2:摘要 3:签名值 4:签名证书 5:摘要算法 6:签名算法
 * 
 */
function igetXMLSignatureInfo(Indata, infotype) {
	try {
		var ret = bjcactrl.getXMLSignatureInfo(Indata, infotype);
		return ret;
	} catch (e) {
		return "";
	}
}

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
	功能：登陆函数  
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
		alert("请先选择需要登陆的证书");
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



/*
 * 不支持中文
 * b64 = base64encode(data);
 * data = base64decode(b64);
 */


var base64EncodeChars = [
    "A", "B", "C", "D", "E", "F", "G", "H",
    "I", "J", "K", "L", "M", "N", "O", "P",
    "Q", "R", "S", "T", "U", "V", "W", "X",
    "Y", "Z", "a", "b", "c", "d", "e", "f",
    "g", "h", "i", "j", "k", "l", "m", "n",
    "o", "p", "q", "r", "s", "t", "u", "v",
    "w", "x", "y", "z", "0", "1", "2", "3",
    "4", "5", "6", "7", "8", "9", "+", "/"
];

var base64DecodeChars = [
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63,
    52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1,
    -1,  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14,
    15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1,
    -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
    41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1
];

function base64encode(str) {
    var out, i, j, len;
    var c1, c2, c3;

    len = str.length;
    i = j = 0;
    out = [];
    while (i < len) {
        c1 = str.charCodeAt(i++) & 0xff;
        if (i == len)
        {
            out[j++] = base64EncodeChars[c1 >> 2];
            out[j++] = base64EncodeChars[(c1 & 0x3) << 4];
            out[j++] = "==";
            break;
        }
        c2 = str.charCodeAt(i++) & 0xff;
        if (i == len)
        {
            out[j++] = base64EncodeChars[c1 >> 2];
            out[j++] = base64EncodeChars[((c1 & 0x03) << 4) | ((c2 & 0xf0) >> 4)];
            out[j++] = base64EncodeChars[(c2 & 0x0f) << 2];
            out[j++] = "=";
            break;
        }
        c3 = str.charCodeAt(i++) & 0xff;
        out[j++] = base64EncodeChars[c1 >> 2];
        out[j++] = base64EncodeChars[((c1 & 0x03) << 4) | ((c2 & 0xf0) >> 4)];
        out[j++] = base64EncodeChars[((c2 & 0x0f) << 2) | ((c3 & 0xc0) >> 6)];
        out[j++] = base64EncodeChars[c3 & 0x3f];
    }
    return out.join('');
}

function base64decode(str) {
    var c1, c2, c3, c4;
    var i, j, len, out;

    len = str.length;
    i = j = 0;
    out = [];
    while (i < len) {
        /* c1 */
        do {
            c1 = base64DecodeChars[str.charCodeAt(i++) & 0xff];
        } while (i < len && c1 == -1);
        if (c1 == -1) break;

        /* c2 */
        do {
            c2 = base64DecodeChars[str.charCodeAt(i++) & 0xff];
        } while (i < len && c2 == -1);
        if (c2 == -1) break;

        out[j++] = String.fromCharCode((c1 << 2) | ((c2 & 0x30) >> 4));

        /* c3 */
        do {
            c3 = str.charCodeAt(i++) & 0xff;
            if (c3 == 61) return out.join('');
            c3 = base64DecodeChars[c3];
        } while (i < len && c3 == -1);
        if (c3 == -1) break;

        out[j++] = String.fromCharCode(((c2 & 0x0f) << 4) | ((c3 & 0x3c) >> 2));

        /* c4 */
        do {
            c4 = str.charCodeAt(i++) & 0xff;
            if (c4 == 61) return out.join('');
            c4 = base64DecodeChars[c4];
        } while (i < len && c4 == -1);
        if (c4 == -1) break;
        out[j++] = String.fromCharCode(((c3 & 0x03) << 6) | c4);
    }
    return out.join('');
}
/*!
 * jQuery Form Plugin
 * version: 2.95 (30-JAN-2012)
 * @requires jQuery v1.3.2 or later
 *
 * Examples and documentation at: http://malsup.com/jquery/form/
 * Dual licensed under the MIT and GPL licenses:
 *	http://www.opensource.org/licenses/mit-license.php
 *	http://www.gnu.org/licenses/gpl.html
 */
;(function($) {

/*
	Usage Note:
	-----------
	Do not use both ajaxSubmit and ajaxForm on the same form.  These
	functions are intended to be exclusive.  Use ajaxSubmit if you want
	to bind your own submit handler to the form.  For example,

	$(document).ready(function() {
		$('#myForm').bind('submit', function(e) {
			e.preventDefault(); // <-- important
			$(this).ajaxSubmit({
				target: '#output'
			});
		});
	});

	Use ajaxForm when you want the plugin to manage all the event binding
	for you.  For example,

	$(document).ready(function() {
		$('#myForm').ajaxForm({
			target: '#output'
		});
	});

	When using ajaxForm, the ajaxSubmit function will be invoked for you
	at the appropriate time.
*/

/**
 * ajaxSubmit() provides a mechanism for immediately submitting
 * an HTML form using AJAX.
 */
$.fn.ajaxSubmit = function(options) {
	// fast fail if nothing selected (http://dev.jquery.com/ticket/2752)
	if (!this.length) {
		log('ajaxSubmit: skipping submit process - no element selected');
		return this;
	}
	
	var method, action, url, $form = this;

	if (typeof options == 'function') {
		options = { success: options };
	}

	method = this.attr('method');
	action = this.attr('action');
	url = (typeof action === 'string') ? $.trim(action) : '';
	url = url || window.location.href || '';
	if (url) {
		// clean url (don't include hash vaue)
		url = (url.match(/^([^#]+)/)||[])[1];
	}

	options = $.extend(true, {
		url:  url,
		success: $.ajaxSettings.success,
		type: method || 'GET',
		iframeSrc: /^https/i.test(window.location.href || '') ? 'javascript:false' : 'about:blank'
	}, options);

	// hook for manipulating the form data before it is extracted;
	// convenient for use with rich editors like tinyMCE or FCKEditor
	var veto = {};
	this.trigger('form-pre-serialize', [this, options, veto]);
	if (veto.veto) {
		log('ajaxSubmit: submit vetoed via form-pre-serialize trigger');
		return this;
	}

	// provide opportunity to alter form data before it is serialized
	if (options.beforeSerialize && options.beforeSerialize(this, options) === false) {
		log('ajaxSubmit: submit aborted via beforeSerialize callback');
		return this;
	}

	var traditional = options.traditional;
	if ( traditional === undefined ) {
		traditional = $.ajaxSettings.traditional;
	}
	
	var qx,n,v,a = this.formToArray(options.semantic);
	if (options.data) {
		options.extraData = options.data;
		qx = $.param(options.data, traditional);
	}

	// give pre-submit callback an opportunity to abort the submit
	if (options.beforeSubmit && options.beforeSubmit(a, this, options) === false) {
		log('ajaxSubmit: submit aborted via beforeSubmit callback');
		return this;
	}

	// fire vetoable 'validate' event
	this.trigger('form-submit-validate', [a, this, options, veto]);
	if (veto.veto) {
		log('ajaxSubmit: submit vetoed via form-submit-validate trigger');
		return this;
	}

	var q = $.param(a, traditional);
	if (qx) {
		q = ( q ? (q + '&' + qx) : qx );
	}	
	if (options.type.toUpperCase() == 'GET') {
		options.url += (options.url.indexOf('?') >= 0 ? '&' : '?') + q;
		options.data = null;  // data is null for 'get'
	}
	else {
		options.data = q; // data is the query string for 'post'
	}

	var callbacks = [];
	if (options.resetForm) {
		callbacks.push(function() { $form.resetForm(); });
	}
	if (options.clearForm) {
		callbacks.push(function() { $form.clearForm(options.includeHidden); });
	}

	// perform a load on the target only if dataType is not provided
	if (!options.dataType && options.target) {
		var oldSuccess = options.success || function(){};
		callbacks.push(function(data) {
			var fn = options.replaceTarget ? 'replaceWith' : 'html';
			$(options.target)[fn](data).each(oldSuccess, arguments);
		});
	}
	else if (options.success) {
		callbacks.push(options.success);
	}

	options.success = function(data, status, xhr) { // jQuery 1.4+ passes xhr as 3rd arg
		var context = options.context || options;	// jQuery 1.4+ supports scope context 
		for (var i=0, max=callbacks.length; i < max; i++) {
			callbacks[i].apply(context, [data, status, xhr || $form, $form]);
		}
	};

	// are there files to upload?
	var fileInputs = $('input:file:enabled[value]', this); // [value] (issue #113)
	var hasFileInputs = fileInputs.length > 0;
	var mp = 'multipart/form-data';
	var multipart = ($form.attr('enctype') == mp || $form.attr('encoding') == mp);

	var fileAPI = !!(hasFileInputs && fileInputs.get(0).files && window.FormData);
	log("fileAPI :" + fileAPI);
	var shouldUseFrame = (hasFileInputs || multipart) && !fileAPI;

	// options.iframe allows user to force iframe mode
	// 06-NOV-09: now defaulting to iframe mode if file input is detected
	if (options.iframe !== false && (options.iframe || shouldUseFrame)) {
		// hack to fix Safari hang (thanks to Tim Molendijk for this)
		// see:  http://groups.google.com/group/jquery-dev/browse_thread/thread/36395b7ab510dd5d
		if (options.closeKeepAlive) {
			$.get(options.closeKeepAlive, function() {
				fileUploadIframe(a);
			});
		}
  		else {
			fileUploadIframe(a);
  		}
	}
	else if ((hasFileInputs || multipart) && fileAPI) {
		options.progress = options.progress || $.noop;
		fileUploadXhr(a);
	}
	else {
		$.ajax(options);
	}

	 // fire 'notify' event
	 this.trigger('form-submit-notify', [this, options]);
	 return this;

	 // XMLHttpRequest Level 2 file uploads (big hat tip to francois2metz)
	function fileUploadXhr(a) {
		var formdata = new FormData();

		for (var i=0; i < a.length; i++) {
			if (a[i].type == 'file')
				continue;
			formdata.append(a[i].name, a[i].value);
		}

		$form.find('input:file:enabled').each(function(){
			var name = $(this).attr('name'), files = this.files;
			if (name) {
				for (var i=0; i < files.length; i++)
					formdata.append(name, files[i]);
			}
		});

		if (options.extraData) {
			for (var k in options.extraData)
				formdata.append(k, options.extraData[k])
		}

		options.data = null;

		var s = $.extend(true, {}, $.ajaxSettings, options, {
			contentType: false,
			processData: false,
			cache: false,
			type: 'POST'
		});

      //s.context = s.context || s;

      s.data = null;
      var beforeSend = s.beforeSend;
      s.beforeSend = function(xhr, o) {
          o.data = formdata;
          if(xhr.upload) { // unfortunately, jQuery doesn't expose this prop (http://bugs.jquery.com/ticket/10190)
              xhr.upload.onprogress = function(event) {
                  o.progress(event.position, event.total);
              };
          }
          if(beforeSend)
              beforeSend.call(o, xhr, options);
      };
      $.ajax(s);
   }

	// private function for handling file uploads (hat tip to YAHOO!)
	function fileUploadIframe(a) {
		var form = $form[0], el, i, s, g, id, $io, io, xhr, sub, n, timedOut, timeoutHandle;
		var useProp = !!$.fn.prop;

		if (a) {
			if ( useProp ) {
				// ensure that every serialized input is still enabled
				for (i=0; i < a.length; i++) {
					el = $(form[a[i].name]);
					el.prop('disabled', false);
				}
			} else {
				for (i=0; i < a.length; i++) {
					el = $(form[a[i].name]);
					el.removeAttr('disabled');
				}
			};
		}

		if ($(':input[name=submit],:input[id=submit]', form).length) {
			// if there is an input with a name or id of 'submit' then we won't be
			// able to invoke the submit fn on the form (at least not x-browser)
			alert('Error: Form elements must not have name or id of "submit".');
			return;
		}
		
		s = $.extend(true, {}, $.ajaxSettings, options);
		s.context = s.context || s;
		id = 'jqFormIO' + (new Date().getTime());
		if (s.iframeTarget) {
			$io = $(s.iframeTarget);
			n = $io.attr('name');
			if (n == null)
			 	$io.attr('name', id);
			else
				id = n;
		}
		else {
			$io = $('<iframe name="' + id + '" src="'+ s.iframeSrc +'" />');
			$io.css({ position: 'absolute', top: '-1000px', left: '-1000px' });
		}
		io = $io[0];


		xhr = { // mock object
			aborted: 0,
			responseText: null,
			responseXML: null,
			status: 0,
			statusText: 'n/a',
			getAllResponseHeaders: function() {},
			getResponseHeader: function() {},
			setRequestHeader: function() {},
			abort: function(status) {
				var e = (status === 'timeout' ? 'timeout' : 'aborted');
				log('aborting upload... ' + e);
				this.aborted = 1;
				$io.attr('src', s.iframeSrc); // abort op in progress
				xhr.error = e;
				s.error && s.error.call(s.context, xhr, e, status);
				g && $.event.trigger("ajaxError", [xhr, s, e]);
				s.complete && s.complete.call(s.context, xhr, e);
			}
		};

		g = s.global;
		// trigger ajax global events so that activity/block indicators work like normal
		if (g && ! $.active++) {
			$.event.trigger("ajaxStart");
		}
		if (g) {
			$.event.trigger("ajaxSend", [xhr, s]);
		}

		if (s.beforeSend && s.beforeSend.call(s.context, xhr, s) === false) {
			if (s.global) {
				$.active--;
			}
			return;
		}
		if (xhr.aborted) {
			return;
		}

		// add submitting element to data if we know it
		sub = form.clk;
		if (sub) {
			n = sub.name;
			if (n && !sub.disabled) {
				s.extraData = s.extraData || {};
				s.extraData[n] = sub.value;
				if (sub.type == "image") {
					s.extraData[n+'.x'] = form.clk_x;
					s.extraData[n+'.y'] = form.clk_y;
				}
			}
		}
		
		var CLIENT_TIMEOUT_ABORT = 1;
		var SERVER_ABORT = 2;

		function getDoc(frame) {
			var doc = frame.contentWindow ? frame.contentWindow.document : frame.contentDocument ? frame.contentDocument : frame.document;
			return doc;
		}
		
		// Rails CSRF hack (thanks to Yvan Barthelemy)
		var csrf_token = $('meta[name=csrf-token]').attr('content');
		var csrf_param = $('meta[name=csrf-param]').attr('content');
		if (csrf_param && csrf_token) {
			s.extraData = s.extraData || {};
			s.extraData[csrf_param] = csrf_token;
		}

		// take a breath so that pending repaints get some cpu time before the upload starts
		function doSubmit() {
			// make sure form attrs are set
			var t = $form.attr('target'), a = $form.attr('action');

			// update form attrs in IE friendly way
			form.setAttribute('target',id);
			if (!method) {
				form.setAttribute('method', 'POST');
			}
			if (a != s.url) {
				form.setAttribute('action', s.url);
			}

			// ie borks in some cases when setting encoding
			if (! s.skipEncodingOverride && (!method || /post/i.test(method))) {
				$form.attr({
					encoding: 'multipart/form-data',
					enctype:  'multipart/form-data'
				});
			}

			// support timout
			if (s.timeout) {
				timeoutHandle = setTimeout(function() { timedOut = true; cb(CLIENT_TIMEOUT_ABORT); }, s.timeout);
			}
			
			// look for server aborts
			function checkState() {
				try {
					var state = getDoc(io).readyState;
					log('state = ' + state);
					if (state.toLowerCase() == 'uninitialized')
						setTimeout(checkState,50);
				}
				catch(e) {
					log('Server abort: ' , e, ' (', e.name, ')');
					cb(SERVER_ABORT);
					timeoutHandle && clearTimeout(timeoutHandle);
					timeoutHandle = undefined;
				}
			}

			// add "extra" data to form if provided in options
			var extraInputs = [];
			try {
				if (s.extraData) {
					for (var n in s.extraData) {
						extraInputs.push(
							$('<input type="hidden" name="'+n+'">').attr('value',s.extraData[n])
								.appendTo(form)[0]);
					}
				}

				if (!s.iframeTarget) {
					// add iframe to doc and submit the form
					$io.appendTo('body');
					io.attachEvent ? io.attachEvent('onload', cb) : io.addEventListener('load', cb, false);
				}
				setTimeout(checkState,15);
				form.submit();
			}
			finally {
				// reset attrs and remove "extra" input elements
				form.setAttribute('action',a);
				if(t) {
					form.setAttribute('target', t);
				} else {
					$form.removeAttr('target');
				}
				$(extraInputs).remove();
			}
		}

		if (s.forceSync) {
			doSubmit();
		}
		else {
			setTimeout(doSubmit, 10); // this lets dom updates render
		}

		var data, doc, domCheckCount = 50, callbackProcessed;

		function cb(e) {
			if (xhr.aborted || callbackProcessed) {
				return;
			}
			try {
				doc = getDoc(io);
			}
			catch(ex) {
				log('cannot access response document: ', ex);
				e = SERVER_ABORT;
			}
			if (e === CLIENT_TIMEOUT_ABORT && xhr) {
				xhr.abort('timeout');
				return;
			}
			else if (e == SERVER_ABORT && xhr) {
				xhr.abort('server abort');
				return;
			}

			if (!doc || doc.location.href == s.iframeSrc) {
				// response not received yet
				if (!timedOut)
					return;
			}
			io.detachEvent ? io.detachEvent('onload', cb) : io.removeEventListener('load', cb, false);

			var status = 'success', errMsg;
			try {
				if (timedOut) {
					throw 'timeout';
				}

				var isXml = s.dataType == 'xml' || doc.XMLDocument || $.isXMLDoc(doc);
				log('isXml='+isXml);
				if (!isXml && window.opera && (doc.body == null || doc.body.innerHTML == '')) {
					if (--domCheckCount) {
						// in some browsers (Opera) the iframe DOM is not always traversable when
						// the onload callback fires, so we loop a bit to accommodate
						log('requeing onLoad callback, DOM not available');
						setTimeout(cb, 250);
						return;
					}
					// let this fall through because server response could be an empty document
					//log('Could not access iframe DOM after mutiple tries.');
					//throw 'DOMException: not available';
				}

				//log('response detected');
				var docRoot = doc.body ? doc.body : doc.documentElement;
				xhr.responseText = docRoot ? docRoot.innerHTML : null;
				xhr.responseXML = doc.XMLDocument ? doc.XMLDocument : doc;
				if (isXml)
					s.dataType = 'xml';
				xhr.getResponseHeader = function(header){
					var headers = {'content-type': s.dataType};
					return headers[header];
				};
				// support for XHR 'status' & 'statusText' emulation :
				if (docRoot) {
					xhr.status = Number( docRoot.getAttribute('status') ) || xhr.status;
					xhr.statusText = docRoot.getAttribute('statusText') || xhr.statusText;
				}

				var dt = (s.dataType || '').toLowerCase();
				var scr = /(json|script|text)/.test(dt);
				if (scr || s.textarea) {
					// see if user embedded response in textarea
					var ta = doc.getElementsByTagName('textarea')[0];
					if (ta) {
						xhr.responseText = ta.value;
						// support for XHR 'status' & 'statusText' emulation :
						xhr.status = Number( ta.getAttribute('status') ) || xhr.status;
						xhr.statusText = ta.getAttribute('statusText') || xhr.statusText;
					}
					else if (scr) {
						// account for browsers injecting pre around json response
						var pre = doc.getElementsByTagName('pre')[0];
						var b = doc.getElementsByTagName('body')[0];
						if (pre) {
							xhr.responseText = pre.textContent ? pre.textContent : pre.innerText;
						}
						else if (b) {
							xhr.responseText = b.textContent ? b.textContent : b.innerText;
						}
					}
				}
				else if (dt == 'xml' && !xhr.responseXML && xhr.responseText != null) {
					xhr.responseXML = toXml(xhr.responseText);
				}

				try {
					data = httpData(xhr, dt, s);
				}
				catch (e) {
					status = 'parsererror';
					xhr.error = errMsg = (e || status);
				}
			}
			catch (e) {
				log('error caught: ',e);
				status = 'error';
				xhr.error = errMsg = (e || status);
			}

			if (xhr.aborted) {
				log('upload aborted');
				status = null;
			}

			if (xhr.status) { // we've set xhr.status
				status = (xhr.status >= 200 && xhr.status < 300 || xhr.status === 304) ? 'success' : 'error';
			}

			// ordering of these callbacks/triggers is odd, but that's how $.ajax does it
			if (status === 'success') {
				s.success && s.success.call(s.context, data, 'success', xhr);
				g && $.event.trigger("ajaxSuccess", [xhr, s]);
			}
			else if (status) {
				if (errMsg == undefined)
					errMsg = xhr.statusText;
				s.error && s.error.call(s.context, xhr, status, errMsg);
				g && $.event.trigger("ajaxError", [xhr, s, errMsg]);
			}

			g && $.event.trigger("ajaxComplete", [xhr, s]);

			if (g && ! --$.active) {
				$.event.trigger("ajaxStop");
			}

			s.complete && s.complete.call(s.context, xhr, status);

			callbackProcessed = true;
			if (s.timeout)
				clearTimeout(timeoutHandle);

			// clean up
			setTimeout(function() {
				if (!s.iframeTarget)
					$io.remove();
				xhr.responseXML = null;
			}, 100);
		}

		var toXml = $.parseXML || function(s, doc) { // use parseXML if available (jQuery 1.5+)
			if (window.ActiveXObject) {
				doc = new ActiveXObject('Microsoft.XMLDOM');
				doc.async = 'false';
				doc.loadXML(s);
			}
			else {
				doc = (new DOMParser()).parseFromString(s, 'text/xml');
			}
			return (doc && doc.documentElement && doc.documentElement.nodeName != 'parsererror') ? doc : null;
		};
		var parseJSON = $.parseJSON || function(s) {
			return window['eval']('(' + s + ')');
		};

		var httpData = function( xhr, type, s ) { // mostly lifted from jq1.4.4

			var ct = xhr.getResponseHeader('content-type') || '',
				xml = type === 'xml' || !type && ct.indexOf('xml') >= 0,
				data = xml ? xhr.responseXML : xhr.responseText;

			if (xml && data.documentElement.nodeName === 'parsererror') {
				$.error && $.error('parsererror');
			}
			if (s && s.dataFilter) {
				data = s.dataFilter(data, type);
			}
			if (typeof data === 'string') {
				if (type === 'json' || !type && ct.indexOf('json') >= 0) {
					data = parseJSON(data);
				} else if (type === "script" || !type && ct.indexOf("javascript") >= 0) {
					$.globalEval(data);
				}
			}
			return data;
		};
	}
};

/**
 * ajaxForm() provides a mechanism for fully automating form submission.
 *
 * The advantages of using this method instead of ajaxSubmit() are:
 *
 * 1: This method will include coordinates for <input type="image" /> elements (if the element
 *	is used to submit the form).
 * 2. This method will include the submit element's name/value data (for the element that was
 *	used to submit the form).
 * 3. This method binds the submit() method to the form for you.
 *
 * The options argument for ajaxForm works exactly as it does for ajaxSubmit.  ajaxForm merely
 * passes the options argument along after properly binding events for submit elements and
 * the form itself.
 */
$.fn.ajaxForm = function(options) {
	// in jQuery 1.3+ we can fix mistakes with the ready state
	if (this.length === 0) {
		var o = { s: this.selector, c: this.context };
		if (!$.isReady && o.s) {
			log('DOM not ready, queuing ajaxForm');
			$(function() {
				$(o.s,o.c).ajaxForm(options);
			});
			return this;
		}
		// is your DOM ready?  http://docs.jquery.com/Tutorials:Introducing_$(document).ready()
		log('terminating; zero elements found by selector' + ($.isReady ? '' : ' (DOM not ready)'));
		return this;
	}

	return this.ajaxFormUnbind().bind('submit.form-plugin', function(e) {
		if (!e.isDefaultPrevented()) { // if event has been canceled, don't proceed
			e.preventDefault();
			$(this).ajaxSubmit(options);
		}
	}).bind('click.form-plugin', function(e) {
		var target = e.target;
		var $el = $(target);
		if (!($el.is(":submit,input:image"))) {
			// is this a child element of the submit el?  (ex: a span within a button)
			var t = $el.closest(':submit');
			if (t.length == 0) {
				return;
			}
			target = t[0];
		}
		var form = this;
		form.clk = target;
		if (target.type == 'image') {
			if (e.offsetX != undefined) {
				form.clk_x = e.offsetX;
				form.clk_y = e.offsetY;
			} else if (typeof $.fn.offset == 'function') { // try to use dimensions plugin
				var offset = $el.offset();
				form.clk_x = e.pageX - offset.left;
				form.clk_y = e.pageY - offset.top;
			} else {
				form.clk_x = e.pageX - target.offsetLeft;
				form.clk_y = e.pageY - target.offsetTop;
			}
		}
		// clear form vars
		setTimeout(function() { form.clk = form.clk_x = form.clk_y = null; }, 100);
	});
};

// ajaxFormUnbind unbinds the event handlers that were bound by ajaxForm
$.fn.ajaxFormUnbind = function() {
	return this.unbind('submit.form-plugin click.form-plugin');
};

/**
 * formToArray() gathers form element data into an array of objects that can
 * be passed to any of the following ajax functions: $.get, $.post, or load.
 * Each object in the array has both a 'name' and 'value' property.  An example of
 * an array for a simple login form might be:
 *
 * [ { name: 'username', value: 'jresig' }, { name: 'password', value: 'secret' } ]
 *
 * It is this array that is passed to pre-submit callback functions provided to the
 * ajaxSubmit() and ajaxForm() methods.
 */
$.fn.formToArray = function(semantic) {
	var a = [];
	if (this.length === 0) {
		return a;
	}

	var form = this[0];
	var els = semantic ? form.getElementsByTagName('*') : form.elements;
	if (!els) {
		return a;
	}

	var i,j,n,v,el,max,jmax;
	for(i=0, max=els.length; i < max; i++) {
		el = els[i];
		n = el.name;
		if (!n) {
			continue;
		}

		if (semantic && form.clk && el.type == "image") {
			// handle image inputs on the fly when semantic == true
			if(!el.disabled && form.clk == el) {
				a.push({name: n, value: $(el).val(), type: el.type });
				a.push({name: n+'.x', value: form.clk_x}, {name: n+'.y', value: form.clk_y});
			}
			continue;
		}

		v = $.fieldValue(el, true);
		if (v && v.constructor == Array) {
			for(j=0, jmax=v.length; j < jmax; j++) {
				a.push({name: n, value: v[j]});
			}
		}
		else if (v !== null && typeof v != 'undefined') {
			a.push({name: n, value: v, type: el.type});
		}
	}

	if (!semantic && form.clk) {
		// input type=='image' are not found in elements array! handle it here
		var $input = $(form.clk), input = $input[0];
		n = input.name;
		if (n && !input.disabled && input.type == 'image') {
			a.push({name: n, value: $input.val()});
			a.push({name: n+'.x', value: form.clk_x}, {name: n+'.y', value: form.clk_y});
		}
	}
	return a;
};

/**
 * Serializes form data into a 'submittable' string. This method will return a string
 * in the format: name1=value1&amp;name2=value2
 */
$.fn.formSerialize = function(semantic) {
	//hand off to jQuery.param for proper encoding
	return $.param(this.formToArray(semantic));
};

/**
 * Serializes all field elements in the jQuery object into a query string.
 * This method will return a string in the format: name1=value1&amp;name2=value2
 */
$.fn.fieldSerialize = function(successful) {
	var a = [];
	this.each(function() {
		var n = this.name;
		if (!n) {
			return;
		}
		var v = $.fieldValue(this, successful);
		if (v && v.constructor == Array) {
			for (var i=0,max=v.length; i < max; i++) {
				a.push({name: n, value: v[i]});
			}
		}
		else if (v !== null && typeof v != 'undefined') {
			a.push({name: this.name, value: v});
		}
	});
	//hand off to jQuery.param for proper encoding
	return $.param(a);
};

/**
 * Returns the value(s) of the element in the matched set.  For example, consider the following form:
 *
 *  <form><fieldset>
 *	  <input name="A" type="text" />
 *	  <input name="A" type="text" />
 *	  <input name="B" type="checkbox" value="B1" />
 *	  <input name="B" type="checkbox" value="B2"/>
 *	  <input name="C" type="radio" value="C1" />
 *	  <input name="C" type="radio" value="C2" />
 *  </fieldset></form>
 *
 *  var v = $(':text').fieldValue();
 *  // if no values are entered into the text inputs
 *  v == ['','']
 *  // if values entered into the text inputs are 'foo' and 'bar'
 *  v == ['foo','bar']
 *
 *  var v = $(':checkbox').fieldValue();
 *  // if neither checkbox is checked
 *  v === undefined
 *  // if both checkboxes are checked
 *  v == ['B1', 'B2']
 *
 *  var v = $(':radio').fieldValue();
 *  // if neither radio is checked
 *  v === undefined
 *  // if first radio is checked
 *  v == ['C1']
 *
 * The successful argument controls whether or not the field element must be 'successful'
 * (per http://www.w3.org/TR/html4/interact/forms.html#successful-controls).
 * The default value of the successful argument is true.  If this value is false the value(s)
 * for each element is returned.
 *
 * Note: This method *always* returns an array.  If no valid value can be determined the
 *	array will be empty, otherwise it will contain one or more values.
 */
$.fn.fieldValue = function(successful) {
	for (var val=[], i=0, max=this.length; i < max; i++) {
		var el = this[i];
		var v = $.fieldValue(el, successful);
		if (v === null || typeof v == 'undefined' || (v.constructor == Array && !v.length)) {
			continue;
		}
		v.constructor == Array ? $.merge(val, v) : val.push(v);
	}
	return val;
};

/**
 * Returns the value of the field element.
 */
$.fieldValue = function(el, successful) {
	var n = el.name, t = el.type, tag = el.tagName.toLowerCase();
	if (successful === undefined) {
		successful = true;
	}

	if (successful && (!n || el.disabled || t == 'reset' || t == 'button' ||
		(t == 'checkbox' || t == 'radio') && !el.checked ||
		(t == 'submit' || t == 'image') && el.form && el.form.clk != el ||
		tag == 'select' && el.selectedIndex == -1)) {
			return null;
	}

	if (tag == 'select') {
		var index = el.selectedIndex;
		if (index < 0) {
			return null;
		}
		var a = [], ops = el.options;
		var one = (t == 'select-one');
		var max = (one ? index+1 : ops.length);
		for(var i=(one ? index : 0); i < max; i++) {
			var op = ops[i];
			if (op.selected) {
				var v = op.value;
				if (!v) { // extra pain for IE...
					v = (op.attributes && op.attributes['value'] && !(op.attributes['value'].specified)) ? op.text : op.value;
				}
				if (one) {
					return v;
				}
				a.push(v);
			}
		}
		return a;
	}
	return $(el).val();
};

/**
 * Clears the form data.  Takes the following actions on the form's input fields:
 *  - input text fields will have their 'value' property set to the empty string
 *  - select elements will have their 'selectedIndex' property set to -1
 *  - checkbox and radio inputs will have their 'checked' property set to false
 *  - inputs of type submit, button, reset, and hidden will *not* be effected
 *  - button elements will *not* be effected
 */
$.fn.clearForm = function(includeHidden) {
	return this.each(function() {
		$('input,select,textarea', this).clearFields(includeHidden);
	});
};

/**
 * Clears the selected form elements.
 */
$.fn.clearFields = $.fn.clearInputs = function(includeHidden) {
	var re = /^(?:color|date|datetime|email|month|number|password|range|search|tel|text|time|url|week)$/i; // 'hidden' is not in this list
	return this.each(function() {
		var t = this.type, tag = this.tagName.toLowerCase();
		if (re.test(t) || tag == 'textarea' || (includeHidden && /hidden/.test(t)) ) {
			this.value = '';
		}
		else if (t == 'checkbox' || t == 'radio') {
			this.checked = false;
		}
		else if (tag == 'select') {
			this.selectedIndex = -1;
		}
	});
};

/**
 * Resets the form data.  Causes all form elements to be reset to their original value.
 */
$.fn.resetForm = function() {
	return this.each(function() {
		// guard against an input with the name of 'reset'
		// note that IE reports the reset function as an 'object'
		if (typeof this.reset == 'function' || (typeof this.reset == 'object' && !this.reset.nodeType)) {
			this.reset();
		}
	});
};

/**
 * Enables or disables any matching elements.
 */
$.fn.enable = function(b) {
	if (b === undefined) {
		b = true;
	}
	return this.each(function() {
		this.disabled = !b;
	});
};

/**
 * Checks/unchecks any matching checkboxes or radio buttons and
 * selects/deselects and matching option elements.
 */
$.fn.selected = function(select) {
	if (select === undefined) {
		select = true;
	}
	return this.each(function() {
		var t = this.type;
		if (t == 'checkbox' || t == 'radio') {
			this.checked = select;
		}
		else if (this.tagName.toLowerCase() == 'option') {
			var $sel = $(this).parent('select');
			if (select && $sel[0] && $sel[0].type == 'select-one') {
				// deselect all other options
				$sel.find('option').selected(false);
			}
			this.selected = select;
		}
	});
};

// expose debug var
$.fn.ajaxSubmit.debug = false;

// helper fn for console logging
function log() {
	if (!$.fn.ajaxSubmit.debug) 
		return;
	var msg = '[jquery.form] ' + Array.prototype.join.call(arguments,'');
	if (window.console && window.console.log) {
		window.console.log(msg);
	}
	else if (window.opera && window.opera.postError) {
		window.opera.postError(msg);
	}
};

})(jQuery);

