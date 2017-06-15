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
