
package com.kinsec;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.icore.util.DaoFactory;
import com.icore.util.SecurityConfig;
import com.inspur.StateType;
import com.inspur.bean.DataSet;
import com.inspur.bean.ParameterSet;

import bl.app.icity.govservice.GovProjectDao;

public class PKISvrTcpJavaClt{
	static {
		System.loadLibrary("PKISvrTcpJavaClt");
	}

	 private PKISvrTcpJavaClt(){
	  }	
	 private PKISvrTcpJavaClt(String svrURL){
			_strURL = svrURL;
			_strLastError = "";
			_nHandle = 0;
			_strLastSignCertByP1 = "";
			_strLastSignCertByVerifyP7 = "";
			_strLastSrcDataByVerifyP7 = "";
		}
		public static PKISvrTcpJavaClt _instance=null;
		private static Log _log = LogFactory.getLog(PKISvrTcpJavaClt.class);

		public static PKISvrTcpJavaClt getInstance(){
			return DaoFactory.getDao(PKISvrTcpJavaClt.class.getName());
		}

	/**
	 * 获取最后一次调用的错误信息
	 * @return 错误信息
	 */
	public String KT_GetLastError(){
		return _strLastError;
	};
	
	/**
	 * 初始化库,支持多线程安全
	 * @return void
	 */
	public static native void InitilizeCrypto();
	public static void KT_Initilize()
	{
		InitilizeCrypto();
	}

	/**
	 * 多线程安全释放
	 * @return void
	 */
	public static native void FinalizeCrypto();
	public static void KT_Finalize()
	{
		FinalizeCrypto();
	}

	// 0 获取一个新的实例
	public static PKISvrTcpJavaClt KT_GetInstance(String serverURL){
		return new PKISvrTcpJavaClt(serverURL);
	}

	// 1 连接PKI服务器
	public native boolean ConnectPKISvr();
	public boolean KT_OpenPKISvr(){
		return ConnectPKISvr();
	}
	
	// 2 关闭PKI服务器
	public native void ClosePKISvr();
	public void KT_ClosePKISvr(){
		ClosePKISvr();
	}
	
	// 3 产生随机数
	public native String GenRandom( int nLen );
	public String KT_GenRandom( int nLen ){
		return GenRandom( nLen );
	}

	// 4 Raw签名(PKCS1格式)
	public native String P1_SignData( String strSrc );
	public String KT_P1SignData( String strSrc ){
		return P1_SignData( strSrc );
	}

	// 5 Raw验证(PKCS1格式)
	public native boolean P1_VerifySignData( String strSrc, String strB64Cert, String strB64Sign );
	public boolean KT_VerifyP1SignData( String strSrc, String strB64Cert, String strB64Sign ){
		return P1_VerifySignData( strSrc, strB64Cert, strB64Sign );
	}

	// 6 对文件Raw签名(Sha1摘要)
	public native String P1_SignFileBySha1( String strInFile );
	public String KT_P1SignFileBySha1( String strInFile ){
		return P1_SignFileBySha1( strInFile );
	}

	// 7 对文件Raw签名进行验证(签名使用Sha1摘要)
	public native boolean P1_VerifySignFileBySha1( String strInFile, String strB64Cert, String strB64SignValue );
	public boolean KT_P1VerifySignFileBySha1( String strInFile, String strB64Cert, String strB64SignValue ){
		return P1_VerifySignFileBySha1( strInFile, strB64Cert, strB64SignValue );
	}

	// 8 获取上一个Raw签名成功的签名公钥证书
	public String KT_GetLastP1SignCert(){
		return _strLastSignCertByP1;
	}

	// 9 Attach/dettach签名(PKCS7格式)
	public native String P7_SignData( String strSrc, int nAttachFlag );
	public String KT_P7SignData( String strSrc, int nAttachFlag ) {
		return P7_SignData( strSrc, nAttachFlag );
	}

	// 10 Attach/dettach验证(PKCS7格式)
	public native boolean P7_VerifySignData( String strB64SignValue, String strSrc );
	public boolean KT_P7VerifySignData( String strB64SignValue, String strSrc ){
		return P7_VerifySignData( strB64SignValue, strSrc );
	}

	// 11 获取上一个Attach/dettach验证成功时的签名公钥证书
	public String KT_GetLastP7VerifySignCert(){
		return _strLastSignCertByVerifyP7;
	}

	// 12获取上一个Attach/dettach验证成功时的源文数据
	public String KT_GetLastP7VerifySrcData(){
		return _strLastSrcDataByVerifyP7;
	}

	// 13 根据传入公钥证书判断证书USBKEY类型
	// 1代表CA新系统颁发的企业证书KEY -- 简称新企业证书KEY
    // 2代表CA新系统颁发的个人证书KEY
    // 3代表CA新系统颁发的编码证书KEY
    // 4代表CA旧系统颁发的企业证书KEY -- 简称旧企业证书KEY
    // 小于0 取错误信息LastErrorString
	public native int GetCertKeyType( String strB64Cert );
	public int KT_GetCertKeyType( String strB64Cert ){
		return GetCertKeyType( strB64Cert );
	}
	
	// 14 获取传入证书的CN
	public native String GetCertCN( String strB64Cert );
	public String KT_GetCertCN( String strB64Cert ){
		return GetCertCN( strB64Cert );
	}

	// 15 获取传入证书的SN
	public native String GetCertSN( String strB64Cert );
	public String KT_GetCertSN( String strB64Cert ){
		return GetCertSN( strB64Cert );
	}

	// 16 获取传入证书的DN
	public native String GetCertDN( String strB64Cert );
	public String KT_GetCertDN( String strB64Cert ){
		return GetCertDN( strB64Cert );
	}

	// 17 获取传入证书的OU
	public native String GetCertOU( String strB64Cert );
	public String KT_GetCertOU( String strB64Cert ){
		return GetCertOU( strB64Cert );
	}

	// 18 获取传入证书的唯一标识码
	public native String GetCertUniqueID( String strB64Cert );
	public String KT_GetCertUniqueID( String strB64Cert ){
		return GetCertUniqueID( strB64Cert );
	}

	// 19 获取传入证书的注册号(企业证书)
	public native String GetUserRegisterNumber( String strB64Cert );
	public String KT_GetUserRegisterNumber( String strB64Cert ){
		return GetUserRegisterNumber( strB64Cert );
	}

	// 20 获取传入证书的组织结构代码(企业证书)
	public native String GetUserOrganizationNumber( String strB64Cert );
	public String KT_GetUserOrganizationNumber( String strB64Cert ){
		return GetUserOrganizationNumber( strB64Cert );
	}
	
	// 21 获取传入证书的身份证号(个人证书)
	public native String GetUserIdentificationNumber( String strB64Cert );
	public String KT_GetUserIdentificationNumber( String strB64Cert ){
		return GetUserIdentificationNumber( strB64Cert );
	}

	// 22 PKCS7方式对文件签名
	public native String P7_SignFile( String strPathFile, int nAttachFlag );
	public String KT_P7SignFile( String strPathFile, int nAttachFlag ) {
		return P7_SignFile( strPathFile, nAttachFlag );
	}

	// 23 PKCS7方式对文件验证
	public native boolean P7_VerifySignFile( String strSignPathFile, String strSrcPathFile );
	public boolean KT_P7VerifySignFile( String strSignPathFile, String strSrcPathFile ) {
		return P7_VerifySignFile( strSignPathFile, strSrcPathFile );
	}

	// 24 自定义格式对文件进行PKCS7数字签名
	public native boolean P7_SignKtfFile( String strInPathFile, String strOutPathFile );
	public boolean KT_P7SignKtfFile( String strInPathFile, String strOutPathFile ) {
		return P7_SignKtfFile( strInPathFile, strOutPathFile );
	}

	// 25 对自定义格式的PKCS7数字签名文件进行验证
	public native boolean P7_VerifyKtfFile( String strInPathFile, String strOutPathFile );
	public boolean KT_P7VerifyKtfFile( String strInPathFile, String strOutPathFile ) {
		return P7_VerifyKtfFile( strInPathFile, strOutPathFile );
	}

	// 26 对数据自定义格式进行数字信封可签名(nCipher值为1时只作信封;值为2时信封并签名)
	public native byte[] P7_CipherKinsecData( byte[] indata, String strDecipherCert, int nCipher );
	public byte[] KT_P7CipherKinsecData( byte[] indata, String strDecipherCert, int nCipher )
	{
		return P7_CipherKinsecData( indata, strDecipherCert, nCipher );
	}

	// 27 对自定义格式的数据进行数字信封解密并验证
	public native byte[] P7_DecipherKinsecData( byte[] indata );
	public byte[] KT_P7DecipherKinsecData( byte[] indata )
	{
		return P7_DecipherKinsecData( indata );
	}

	// 28 对文件自定义格式进行数字信封并签名(nCipher值为1时只作信封;值为2时信封并签名)
	public native boolean P7_CipherKinsecFile( String strInPathFile, String strDeCipherCert, int nCipher, String strOutPathFile );
	public boolean KT_P7CipherKinsecFile( String strInPathFile, String strDeCipherCert, int nCipher, String strOutPathFile )
	{
		return P7_CipherKinsecFile( strInPathFile, strDeCipherCert, nCipher, strOutPathFile );
	}

	// 29 对自定义格式的数字信封文件进行解密并验证
	public native boolean P7_DecipherKinsecFile( String strInPathFile, String strOutPathFile );
	public boolean KT_P7DecipherKinsecFile( String strInPathFile, String strOutPathFile )
	{
		return P7_DecipherKinsecFile( strInPathFile, strOutPathFile );
	}

	// 30 获取上一个解密数字信封并验证通过的签名公钥证书
	public String KT_GetLastP7DecipherSignCert(){
		return _strLastSignCertByDecipherP7;
	}

	private String _strURL;
	private String _strLastSignCertByP1;
	private String _strLastSignCertByVerifyP7;
	private String _strLastSrcDataByVerifyP7;
	private String _strLastSignCertByDecipherP7;
	private String _strLastError;
	private long _nHandle;

	public DataSet getUKInfo(ParameterSet pSet) 
	{	
		DataSet ds = new DataSet();
		//整个工程只调用一次
		KT_Initilize();
		String strSvrUrl = SecurityConfig.getString("PKIstrSvrUrl");//"120.35.10.241:101";
		String strSrc = "123";   //原文
		
		PKISvrTcpJavaClt clt = KT_GetInstance( strSvrUrl );
		
        String strB64Cert = (String)pSet.get("zs");//"MIIElTCCBDqgAwIBAgIKLRAAAAAAAAVm7DAKBggqgRzPVQGDdTBEMQswCQYDVQQGEwJDTjENMAsGA1UECgwEQkpDQTENMAsGA1UECwwEQkpDQTEXMBUGA1UEAwwOQmVpamluZyBTTTIgQ0EwHhcNMTUwOTE2MTYwMDAwWhcNMTYxMjE3MTU1OTU5WjCBlTETMBEGA1UEKQwKMDAwMDAwMDAtMDEjMCEGA1UEAwwa5LyB5Lia5rWL6K+V6K+B5LmmKOa1i+ivlSkxDTALBgNVBAoMBEJKQ0ExGzAZBgNVBAoMEuS8geS4mua1i+ivleivgeS5pjEPMA0GA1UEBwwG56aP5bu6MQ8wDQYDVQQIDAbnpo/lu7oxCzAJBgNVBAYMAkNOMFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEW5D+pemgJdLc59i2XcDTo5xz9eGKwzp/sy6fOFHG6bB9ovCKSIboo17I5T4JyzY/94yzH14wqmI+/0TCui5NSaOCAsAwggK8MB8GA1UdIwQYMBaAFB/mz9SPxSIql0opihXnFsmSNMS2MB0GA1UdDgQWBBRe/eaz/ZbFlCI6DMVbMdlNg+BuCDALBgNVHQ8EBAMCBsAwgZsGA1UdHwSBkzCBkDBfoF2gW6RZMFcxCzAJBgNVBAYTAkNOMQ0wCwYDVQQKDARCSkNBMQ0wCwYDVQQLDARCSkNBMRcwFQYDVQQDDA5CZWlqaW5nIFNNMiBDQTERMA8GA1UEAxMIY2EyMWNybDIwLaAroCmGJ2h0dHA6Ly9jcmwuYmpjYS5vcmcuY24vY3JsL2NhMjFjcmwyLmNybDAcBgoqgRyG7zICAQEBBA4MDEpKMDAwMDAwMDAtMDBgBggrBgEFBQcBAQRUMFIwIwYIKwYBBQUHMAGGF09DU1A6Ly9vY3NwLmJqY2Eub3JnLmNuMCsGCCsGAQUFBzAChh9odHRwOi8vY3JsLmJqY2Eub3JnLmNuL2NhaXNzdWVyMGwGA1UdIARlMGMwMAYDVR0gMCkwJwYIKwYBBQUHAgEWGyBodHRwOi8vd3d3LmJqY2Eub3JnLmNuL2NwczAvBgNVHSAwKDAmBggrBgEFBQcCARYaaHR0cDovL3d3dy5iamNhLm9yZy5jbi9jcHMwEQYJYIZIAYb4QgEBBAQDAgD/MBoGCiqBHIbvMgIBAQgEDAwKMDAwMDAwMDAtMDAcBgoqgRyG7zICAQICBA4MDEpKMDAwMDAwMDAtMDAfBgoqgRyG7zICAQEOBBEMDzk5OTAwMDEwMDA4ODQyODAcBgoqgRyG7zICAQEEBA4MDEpKMDAwMDAwMDAtMDAlBgoqgRyG7zICAQEXBBcMFTlAMjE1MDA5SkowMDAwMDAwMDAtMDAYBggqgRzQFAQBBAQMDAowMDAwMDAwMC0wMBQGCiqBHIbvMgIBAR4EBgwEMTI3NjAKBggqgRzPVQGDdQNJADBGAiEA+lOpbMxc+SVdnYtu54oh+eYF8yzAjDMCcsVdNd+mp24CIQDlWo9HAZfpeaWNS/zB38IX9yPFAVma3WsueO/S9b2nqA==";
		String strB64Sign = (String)pSet.get("fhz");//"MEUCIDKdMu2e3J0AtvSI/pCK8iKyVfyNKntd4LIr4BDDXqbzAiEAtixjFOcszxH6tsA/Tir7qqb6gBm7rHynDgFRqDC2rds=";   //签名结果
		
		//验证
		boolean bRes = clt.KT_OpenPKISvr();
		if( !bRes ){
			System.out.println( "验证签名失败："+clt.KT_GetLastError() );
			//整个工程只释放一次	
			KT_Finalize();
			ds.setState(StateType.FAILT);
			return ds;
		}
		bRes = clt.KT_VerifyP1SignData(strSrc, strB64Cert, strB64Sign);
		if( !bRes ){
			System.out.println( "验证签名失败："+clt.KT_GetLastError() );
			//整个工程只释放一次	
			KT_Finalize();
			ds.setState(StateType.FAILT);
			return ds;
		}else{
		   System.out.println("验证签名成功：证书通用名为"+clt.KT_GetCertCN(strB64Cert));
		   System.out.println("              企业组织机构代码为"+clt.KT_GetUserOrganizationNumber(strB64Cert));
           System.out.println("              个人身份证号吗为"+clt.KT_GetUserIdentificationNumber(strB64Cert));
           JSONArray ja = new JSONArray();
           JSONObject jo = new JSONObject();
           jo.put("zstym", clt.KT_GetCertCN(strB64Cert));
           jo.put("zzjgdm", clt.KT_GetUserOrganizationNumber(strB64Cert));
           jo.put("grsfzh", clt.KT_GetUserIdentificationNumber(strB64Cert));
           ja.add(jo);
           ds.setRawData(ja);
           ds.setState(StateType.SUCCESS);
		}
		//整个工程只释放一次	
		KT_Finalize();
		clt.KT_ClosePKISvr();
		return ds;
	}
}
