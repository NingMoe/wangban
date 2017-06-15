/*******************************************************************************************************************************
 ************************************************************************************************************************
 **************************************************GDCA KEY OPERATION****************************************************
 ****************************************************************************************************************************
 *******************************************************************************************************************************/

 //��ǩ����
var  GDCA_LBL_CONFIG=   1;
var  GDCA_LBL_DATA=     2;
var  GDCA_LBL_EXDATA=   3;
var  GDCA_LBL_SIGNKEY=  4;
var  GDCA_LBL_ENCKEY=   5;
var  GDCA_LBL_SIGNCERT= 7;
var  GDCA_LBL_ENCCERT=  8;
var  GDCA_LBL_CACERT=   9;
//��ǩ��дģʽ��ֻ��MODE_RD ֻдMODE_WR ��дMODE_RW
var GDCA_LBLMODE_RD = 1;
var GDCA_LBLMODE_WR = 2;
var GDCA_LBLMODE_RW = 3;
 
//�豸��ʶ
var Wellhope_OldKey  = 0;
var Wellhope_Key     = 1;
var GDCA_Key         = 3;
var GDCA_Machine_Key = 4;
var MH_Key           = 5;
//֤������
var EncCert          = 1;
var SignCert         = 2;
var ROOTCA_CERT      = 3;
var CA_CERT          = 4;


//����֤��
var LBL_ROOTCACERT="ROOTCA_CERT";
var LBL_CACERT="CA_CERT";
var LBL_USERCERT="LAB_USERCERT";
var LBL_USERCERT_SIG="LAB_USERCERT_SIG";
var LBL_USERCERT_TEMP="LAB_USERCERT_TEMP";
var LBL_USERCERT_ENC="LAB_USERCERT_ENC";
var LBL_USERCERT_SIG_SN="LAB_USERCERT_SIG_SN";
var LBL_USERCERT_ENC_SN="LAB_USERCERT_ENC_SN";
 //ԭ��֤�鱸��
var LBL_USERCERT_ENC_BAK="LAB_USERCERT_ENC_BAK";
var LBL_USERCERT_SIG_BAK="LAB_USERCERT_SIG_BAK";

var LBL_DISAID="LAB_DISAID";
var LBL_USERID="LAB_USERID";


var GDCA_GET_CERT_INFO              = 1;
var GDCA_GET_CERT_VERSION           = 2;
var GDCA_GET_CERT_SERIAL            = 3;
var GDCA_GET_CERT_SIGNATURE_ALGO    = 4;
var GDCA_GET_CERT_ISSUER            = 5;
var GDCA_GET_CERT_VALID_TIME        = 6;
var GDCA_GET_CERT_SUBJECT           = 7;
var GDCA_GET_CERT_PUBLIC_KEY        = 8;
var GDCA_GET_CERT_EXTENSIONS        = 9;


var GDCA_MODE_ECB =1;
var GDCA_MODE_CBC =2;
var GDCA_MODE_CFB =3;
var GDCA_MODE_OFB =4;

/*****************************************************
*  �������ƣ�replaceChars;
*  �������ܣ��滻�ַ����е���Ӧ�ַ���;
*******************************************************/
function replaceChars(entry,orgStr,replaceStr)
{
	temp = "" + entry;
	while (temp.indexOf(orgStr)>-1)
	{
		pos= temp.indexOf(orgStr);
		temp = "" + (temp.substring(0, pos) + replaceStr + temp.substring((pos + orgStr.length), temp.length));
	}

	return temp;
}

function ReadFile(i_Com,filename)
{
   var FileData="";
   var flag;
   var curStatus;

   FileData=i_Com.ReadFromFile(filename);
   return FileData;
}


function WriteFile(i_Com,filename,inData)
{
   var flag;
   var curStatus;

   flag=i_Com.WriteToFile("",inData);
   curStatus=i_Com.CurStatus;
   if(flag)
   {
    alert("�����ļ��ɹ�!");
   }
   else
   {
    alert("�ļ���������쳣����!������룺"+curStatus);
   }
   return flag;
}

//***************************��ʼ��һ��*********************************************
function GDCASetDevice(i_Com,deviceType)
{
	var rtn,errorCode;

	rtn = i_Com.GDCA_SetDeviceType(deviceType);
  	errorCode = i_Com.GetError();

        return errorCode;
}

function GDCAInitialize(i_Com)
{
	var rtn,errorCode;

	rtn = i_Com.GDCA_Initialize();
  	errorCode = i_Com.GetError();

        return errorCode;
}

function WellhopeOldKeyInit(i_Com)
{
	var ret=0;

        ret=i_Com.OpkiInit();
        if (ret!=0)
	{
           alert("�ͻ��˳�ʹ��ʧ��:"+ret);
	   return ret;
	}
	 return ret;
}

function WellhopeNewKeyInit(i_Com)
{
	var ret;

        ret = i_Com.AtvInit();
  	if(ret != 0)
	 {
	  alert("�ͻ��˿ؼ���ʼ��ʧ�ܣ�");
	  return ret;
	 }

   	return ret;
}

function GDCAKeyInit(i_Com,KeyType)
{
    	var ret;

    //      alert("KeyType="+KeyType);
          ret=GDCASetDevice(i_Com,KeyType);
          if(ret!=0)
          {
            alert("�����豸���ͳ���"+ret);
            return ret;
          }
         //��ʼ���ؼ�
         ret=GDCAInitialize(i_Com);
         if(ret!=0)
         {
            alert("��ʼ���ؼ�����"+ret);
            return ret;
         }

        return ret;
}

function ActiveXInit(i_Com0,i_Com1,i_Com2,KeyType)
{
	var ret,DeviceType;


	DeviceType = i_Com2.GDCA_GetDevicType();
	ret = DeviceType;
	switch(parseInt(DeviceType))
        {
         case -1  : alert("�����֤��Ӳ�����ʣ�"+ret);
          			KeyType[0] = DeviceType;
					break;
		 case -2  : alert("ע������,�뵼����ȷ��ע����ļ�:"+ret);
					KeyType[0] = DeviceType;
					break;
         case -3  : alert("��������USB�豸:"+ret);
                    KeyType[0] = DeviceType;
         			break;
         case 3  : ret = WellhopeNewKeyInit(i_Com1);
                    KeyType[0] = 1;
                    break;
         case 2  : ret = WellhopeOldKeyInit(i_Com0);
	  				KeyType[0] = 0;
                    break;
         default   : ret = GDCAKeyInit(i_Com2,DeviceType);
                    KeyType[0] = 2;
                    break;
        }

        return ret;
}

//***************************��ʼ��END*********************************************
//***************************�ͷ����һ��******************************************
function WellhopeOldKeyEnd(i_Com)
{
	var ret;

	ret = i_Com.OpkiEnd();
	if(ret != 0)
	{
		alert("OpkiEnd Error:"+ret);
	}
	return ret;
}

function WellhopeNewKeyEnd(i_Com)
{
	var ret;

	ret = i_Com.AtvEnd();
	if(ret != 0)
	 {
	   alert("AtvEnd Error:"+ret);
	 }
	 return ret;
}

function GDCAFinalize(i_Com)
{
	var rtn,errorCode;
	rtn = i_Com.GDCA_Finalize();
  	errorCode = i_Com.GetError();
        return errorCode;
}

function GDCAKeyEnd(i_Com)
{
	var ret;

         //�ͷ���Դ
         ret=GDCAFinalize(i_Com);
         if(ret!=0)
                 	alert("Finalize Error:"+ret);
         return ret;
}


function ActiveXEnd(i_Com,KeyType)
{
	var ret;

	switch(parseInt(KeyType))
        {
 	 case 0  :  ret = WellhopeOldKeyEnd(i_Com);
                    break;
         case 1  :  ret = WellhopeNewKeyEnd(i_Com);
                    break;
         default :  ret = GDCAKeyEnd(i_Com);
        }
        return ret;
}
//****************************************�ͷ����һ��END****************************************
//******************************��½һ��*******************************************************
function WellhopeOldKeyLogin(i_Com,userpin)
  {
  	var ret;
       // 1ΪUSERPIN��0ΪSOPIN
       ret=i_Com.OpkiLogin(1,userpin);
        if (ret!=0)
	{
           alert("�������PIN�벻��ȷ������:"+ret);
	   WellhopeOldKeyLogout(i_Com);
	   WellhopeOldKeyEnd(i_Com);
	   return ret;
	 }

       return ret;
  }

function WellhopeNewKeyLogin(i_Com,userpin)
{
 	var ret;

 	flag=i_Com.Login(1,userpin);
    	if (!flag)
       	{
	    alert("�������PIN�벻��ȷ������:"+ret);
	    WellhopeNewKeyEnd(i_Com);
	    return -2021;
	}else  ret = 0;
	return ret;
}

function GDCAKeyLogin(i_Com,userpin)
{
	var ret,errorCode;

	ret = i_Com.GDCA_Login(2,userpin);
	errorCode = i_Com.GetError();
	if(errorCode != 0)
	{
         alert("��½ʧ�ܣ�"+ret);
          //�ͷ���Դ
         ret=GDCAFinalize(i_Com);
	}
	return errorCode;
}

function GDCALogin(i_Com,keytype,userpin)
{
  	var ret;
	switch(parseInt(keytype))
        {
         case 2   : ret = GDCAKeyLogin(i_Com,userpin);
                    break;
         case 1  : ret = WellhopeNewKeyLogin(i_Com,userpin);
                    break;
         case 0  : ret = WellhopeOldKeyLogin(i_Com,userpin);
                    break;
        }
        return ret;
}
//*******************************��½һ��end****************************************
//******************************�ǳ�һ��******************************************
function WellhopeOldKeyLogout(i_Com)
{
	var ret;

	ret = i_Com.OpkiLogout();
        if(ret != 0)
        {
        	alert("�˳���½ʧ�ܣ�"+ret);
		WellhopeOldKeyEnd(i_Com);
		return ret;
        }
       return ret;
}

function WellhopeNewKeyLogout(i_Com)
{
       var ret;

       ret = i_Com.Logout();
       if(!ret)
       {
 	 alert("�˳���¼���ɹ�:"+ret);
 	 WellhopeNewKeyEnd(i_Com);
 	 return ret;
	}else ret = 0;
       return ret;
}

function GDCAKeyLogout(i_Com)
{
	var ret,errorCode;

	ret = i_Com.GDCA_Logout();
	errorCode = i_Com.GetError();
	if(errorCode != 0)
	{
	  alert("�˳���¼���ɹ�:"+ret);
  	  GDCAKeyEnd(i_Com);
	}
	return ret;
}

function GDCALogout(i_Com,keytype)
{
  	var ret;

	switch(parseInt(keytype))
        {
         case 2   : ret = GDCAKeyLogout(i_Com);
                    break;
         case 1  : ret = WellhopeNewKeyLogout(i_Com);
                    break;
         case 0  : ret = WellhopeOldKeyLogout(i_Com);
                    break;
        }
        return ret;
}
//******************************�ǳ�һ��END******************************************
//******************************�޸�PIN��һ��******************************************
function ChangePinWellhopeOldKey(i_Com,oldpin,newpin)
{
	var ret;

        ret=i_Com.OpkiModifyPin(1,oldpin,newpin);
	if(ret!=0)
	{
	    alert("�޸�pin��ʧ��"+ret);
            WellhopeOldKeyLogout(i_Com);
            WellhopeOldKeyEnd(i_Com);
	    return ret;
	}
	return ret;
}

function ChangePinWellhopeNewKey(i_Com,oldpin,newpin)
{
	var ret=0;

	ret=i_Com.ChangeLoginPin(LBL_USERCERT,oldpin,newpin);
        if(!ret)
        {
        alert("�޸�PIN�벻�ɹ� ");
        WellhopeNewKeyLogout(i_Com);
        WellhopeNewKeyEnd(i_Com);
        return -1;
        }
        return ret;
}

function ChangePinGDCAKey(i_Com,oldpin,newpin)
{
	var ret;

	ret = i_Com.GDCA_ChangePin(2,oldpin,newpin);
	if(ret != 0)
	{
	alert("�޸�PIN�벻�ɹ�"+ret);
	GDCAKeyLogout(i_Com);
	GDCAKeyEnd(i_Com);
	}
       return ret;
}

function GDCAChangePin(i_Com,KeyType,oldpin,newpin)
{
	var ret;

	switch(parseInt(KeyType))
        {
 	 case 0  :   ret = ChangePinWellhopeOldKey(i_Com,oldpin,newpin);
 	             break;
 	 case 1  :   ret = ChangePinWellhopeNewKey(i_Com,oldpin,newpin);
 	             break;
	 default :   ret = ChangePinGDCAKey(i_Com,oldpin,newpin)
 	 }

 	 return ret;
}
//**************************************�޸�PIN��END********************************************
//*******************************Base64����************************************************
function WellhopeOldKeyB64Encode(i_Com,inData,outData)
{
    var ret;

    ret = i_Com.OpkiB64Encode(inData,inData.length);
    if(ret!=0)
     {
  	    alert("BASE64����ʧ�ܣ�"+ret);
	    WellhopeOldKeyLogout(i_Com);
            WellhopeOldKeyEnd(i_Com);
	    return ret;
    }else
    	{
    	outData[0] = i_Com.outData;
    	i_Com.CleanOutData();
	}
    return ret;
}

function WellhopeNewKeyB64Encode(i_Com,inData,outData)
{
    var ret=0;

   outData[0] = i_Com.Base64Encode(inData);
   if(outData[0]=="")
    {
	ret = -2001;
	alert("BASE64����ʧ�ܣ�"+ret);
	WellhopeNewKeyLogout(i_Com);
        WellhopeNewKeyEnd(i_Com);
	return ret;
   }
   return ret;
}

function GDCAKeyB64Encode(i_Com,inData,outData)
{
   var ret=0;

   outData[0] = i_Com.GDCA_Base64Encode(inData);
   ret = i_Com.GetError();
   if(ret != 0)
   {
   	alert("BASE64����ʧ�ܣ�"+ret);
   	GDCAKeyLogout(i_Com);
	GDCAKeyEnd(i_Com);
        return ret;
     }
   return ret;
}

function GDCABase64Encode(i_Com,KeyType,inData,outData)
{
	var ret=0;

        switch(parseInt(KeyType))
        {
 	 case 0  :  ret = WellhopeOldKeyB64Encode(i_Com,inData,outData);
   	            break;
 	 case 1  :  ret = WellhopeNewKeyB64Encode(i_Com,inData,outData);
		    break;
  	 default :  ret = GDCAKeyB64Encode(i_Com,inData,outData);
 	}

	return ret;
}
//*******************************Base64����END************************************************
//*******************************Base64����*************************************************
function WellhopeOldKeyB64Decode(i_Com,inData,outData)
{
	var ret=0;

	ret = i_Com.OpkiB64Decode(inData);
 	if(ret != 0)
 	  {
 	      alert("Base64����ʧ�ܣ�"+ret);
 	      WellhopeOldKeyLogout(i_Com);
              WellhopeOldKeyEnd(i_Com);
	      return ret;
 	  }else
 	  	{
		    outData[0] = i_Com.outData;
   	            i_Com.CleanOutData();
   	        }

   	 return ret;
}

function WellhopeNewKeyB64Decode(i_Com,inData,outData)
{
	var ret=0;

	outData[0] = i_Com.Base64Decode(inData);
	if(outData[0] == "")
	{
	ret = -2002;
  	alert("����ʧ�ܣ�"+ret);
	WellhopeNewKeyLogout(i_Com);
        WellhopeNewKeyEnd(i_Com);
	return ret;
	}

	return ret;
}

function GDCAKeyB64Decode(i_Com,inData,outData)
{
   var ret=0;
   //alert("inData="+inData);
   outData[0] = i_Com.GDCA_Base64Decode(inData);
   ret = i_Com.GetError();
   if(ret != 0)
   {
   	alert("����ʧ�ܣ�"+ret);
   	GDCAKeyLogout(i_Com);
	GDCAKeyEnd(i_Com);
        return ret;
     }
   return ret;
}

function GDCABase64Decode(i_Com,KeyType,inData,outData)
{
	var ret=0;

// 	alert("inData="+inData);
	switch(parseInt(KeyType))
        {
 	 case 0  :  ret = WellhopeOldKeyB64Decode(i_Com,inData,outData);
 	            break;
 	 case 1  :  ret = WellhopeNewKeyB64Decode(i_Com,inData,outData);
		    break;
         default :  ret = GDCAKeyB64Decode(i_Com,inData,outData);
	}

	return ret;
}
//***********************************Base64����END***********************************
//***********************************���������**************************************
function WellhopeOldKeyGenRandom(i_Com,size,outData)
{
	var ret;

	ret=i_Com.OpkiGenerateRandom(size);
	if(ret!=0)
	{
	    alert("���������ʧ�ܣ�"+ret);
	    WellhopeOldKeyLogout(i_Com);
            WellhopeOldKeyEnd(i_Com);
	    return ret;
 	}else
 	  	{
		    outData[0] = i_Com.outData;
   	            i_Com.CleanOutData();
   	        }

   	 return ret;
}

function WellhopeNewKeyGenRandom(i_Com,size,outData)
{
	var ret=0;

	outData[0] = i_Com.GenRandom(size);
	if(outData[0] == "")
	{
	ret = -2003;
  	alert("���������ʧ�ܣ�"+ret);
	WellhopeNewKeyLogout(i_Com);
        WellhopeNewKeyEnd(i_Com);
	return ret;
	}

	return ret;
}

function GDCAKeyGenRandom(i_Com,size,outData)
{
	var ret;

        outData[0] = i_Com.GDCA_OpkiGenerateRandom(size);
	ret = i_Com.GetError();
	if(ret != 0)
	{
	alert("���������ʧ�ܣ�"+ret);
   	GDCAKeyLogout(i_Com);
	GDCAKeyEnd(i_Com);
        return ret;
       }

       return ret;
}

function GDCAGenRandom(i_Com,KeyType,size,outData)
{
	var ret=0;

	switch(parseInt(KeyType))
        {
	 case  0 :   ret = WellhopeOldKeyGenRandom(i_Com,size,outData)
		     break;
	 case  1 :   ret = WellhopeNewKeyGenRandom(i_Com,size,outData);
  	             break;
	 default :   ret = GDCAKeyGenRandom(i_Com,size,outData);
	}

	return ret;
}

//***********************************���������END***********************************
//***********************************��ϣ�㷨****************************************
function WellhopeOldKeyHashData(i_Com,HashAlgo,inData,outData)
{
	var ret;

	ret = i_Com.OpkiHashData(HashAlgo,inData,inData.length);
        if(ret != 0)
        {
            alert("��ϣ����ʧ�ܣ�"+ret);
            WellhopeOldKeyLogout(i_Com);
            WellhopeOldKeyEnd(i_Com);
	    return ret;
 	}else
 	  	{
		    outData[0] = i_Com.outData;
   	            i_Com.CleanOutData();
   	        }

   	return ret;
}

function WellhopeNewKeyHashData(i_Com,HashAlgo,inData,outData)
{
     var ret=0;

     outData[0]=i_Com.HashData(HashAlgo,inData);
     if(outData[0] == "")
	{
	ret = -2004;
  	alert("��ϣ����ʧ�ܣ�"+ret);
	WellhopeNewKeyLogout(i_Com);
        WellhopeNewKeyEnd(i_Com);
	return ret;
	}

	return ret;
}

function GDCAOpkiHash(i_Com,hashAlgo,hashData,outData)
{
	var base64plain = "";
	var ret,hashedData = "";

	base64plain = i_Com.GDCA_Base64Encode(hashData);
	ret = i_Com.GetError();
	if(ret != 0)
	 {
	     	GDCAKeyLogout(i_Com);
	        GDCAKeyEnd(i_Com);
	     	return ret;
         }
	hashedData = i_Com.GDCA_OpkiHashData(hashAlgo,base64plain);
	ret = i_Com.GetError();
	if(ret != 0)
	 {
	     	alert("��ϣ����ʧ�ܣ�"+ret);
	     	GDCAKeyLogout(i_Com);
		GDCAKeyEnd(i_Com);
	     	return ret;
         }

	outData[0] = hashedData;
	return ret;
}

function GDCAKeyHashData(i_Com,HashAlgo,inData,outData)
{
    var ret;

    ret = GDCAOpkiHash(i_Com,HashAlgo,inData,outData);
    return ret;
}

function GDCAGenHashData(i_Com,KeyType,HashAlgo,inData,outData)
{
	var ret=0;
	var Algo;

        if(HashAlgo == "GDCA_ALGO_SHA1")
              Algo=32772;
         else if(HashAlgo == "GDCA_ALGO_MD5")
                     Algo=32771;

       switch(parseInt(KeyType))
        {
	 case  0 : ret = WellhopeOldKeyHashData(i_Com,Algo,inData,outData);
	 	   break;
	 case  1 : ret = WellhopeNewKeyHashData(i_Com,Algo,inData,outData);
	    	   break;
	 default : ret = GDCAKeyHashData(i_Com,Algo,inData,outData);

	}
	return ret;
}
//***********************************��ϣ�㷨END****************************************
//**********************************�ԳƼ���********************************************
function WellhopeOldKeySymm(i_Com,isEncryption,i_algo,keyString,inData,outData)
{
	var ret;

	ret=i_Com.OpkiRWKCryptData(isEncryption,i_algo,keyString,inData,inData.length);

	if(ret != 0)
        {
            alert("�Գ��������"+ret);
            WellhopeOldKeyLogout(i_Com);
            WellhopeOldKeyEnd(i_Com);
	    return ret;
 	}else
 	  	{
		    outData[0] = i_Com.outData;
   	            i_Com.CleanOutData();
   	        }
      return ret;
}

function WellhopeNewKeySymmEncrypt(i_Com,i_algo,keyString,inData,outData)
{
	var ret=0;

   	outData[0] = i_Com.SymmEncrypt(i_algo,keyString,inData);
	if(outData[0]=="")
	{
	ret = -2005;
 	alert("�ԳƼ��ܲ��ɹ�:"+ret);
	WellhopeNewKeyLogout(i_Com);
        WellhopeNewKeyEnd(i_Com);
	return ret;
	}

	return ret;
}

function GDCACreateSymmKeyObj(i_Com,isEncryption,i_algo,keyString)
{
	var SymmKeyObj ;
        var base64iv="";
        var ret;
        var Algo = 101;

//long GDCA_CreateSymmKeyObj(long  algorithmType,long  encOrDec,BSTR  key, BSTR  iv,long   cryptoMode)
//ECBģʽ encOrDec = 1 Ϊ����
	SymmKeyObj = i_Com.GDCA_CreateSymmKeyObj(Algo,isEncryption,keyString,base64iv,GDCA_MODE_ECB);
	ret = i_Com.GetError();
	if (ret != 0 || SymmKeyObj < 1)
	{
		alert("CreateSymmKeyObj Error:"+ret);
		GDCAKeyLogout(i_Com);
		GDCAKeyEnd(i_Com);
	     	return ret;
	}

	return SymmKeyObj;
}

function GDCADestroySymmKeyObj(i_Com,SymmKeyObj)
{
	var ret;

        ret = i_Com.GDCA_DestroySymmKeyObj(SymmKeyObj);
	if (ret != 0)
	{
		GDCAKeyLogout(i_Com);
		GDCAKeyEnd(i_Com);
		alert("DestroySymmKeyObj Error:"+ret);
		return ret;
	}
	return ret;
}

function OpkiSymmCryptData(i_Com,isEncryption,symmAlgoType,symmKey,inData,outData)
{
	var base64plain,base64Key;
	var CryptedMsg = "";

	if (isEncryption == 1)
	{
		ret = GDCAKeyB64Encode(i_Com,inData,outData);
		base64plain = outData[0];
		if (ret != 0)
		{
			alert("�ԳƼ��ܣ����ݱ������:"+ret);
			return ret;
		}
	}
	else
	{
		base64plain = inData;
	}

	ret = GDCAKeyB64Encode(i_Com,symmKey,outData);
	base64Key = outData[0];
	if (ret != 0)
	{
		alert("�Գ���Կ�������:"+ret);
		return ret;
	}
	CryptedMsg = i_Com.GDCA_OpkiSymmCryptData(isEncryption,symmAlgoType,base64Key,base64plain);
	ret = i_Com.GetError();
	if (ret != 0)
	{
		alert("�Գ��������"+ret);
		GDCAKeyLogout(i_Com);
		GDCAKeyEnd(i_Com);
		return ret;
	}


	outData[0] = CryptedMsg;

	return ret;
}

function GDCAKeySymmEncrypt(i_Com,i_algo,keyString,inData,outData)
{
	var ret1,ret;
	var SymmKeyObj = 0;

	//����
	ret = OpkiSymmCryptData(i_Com,1,i_algo,keyString,inData,outData);


/*
        SymmKeyObj = GDCACreateSymmKeyObj(i_Com,1,i_algo,keyString);
	if (SymmKeyObj < 1)
	{
          return SymmKeyObj;
	}
	var CryptTxt = i_Com.GDCA_SymmEncrypt(SymmKeyObj,inData);
	ret = i_Com.GetError();
	outData[0] = CryptTxt;

	ret1 = GDCADestroySymmKeyObj(i_Com,SymmKeyObj);
	if (ret1 != 0)
	{
		return ret1;
	}
	//alert(CryptTxt);
	if (ret != 0 || CryptTxt == "")
	{

		alert("SymmEncrypt Error:"+ret);

		GDCAKeyLogout(i_Com);
		GDCAKeyEnd(i_Com);
		return ret;
	}
*/
       return ret;
}

function GDCASymmEncrypt(i_Com,KeyType,keyString,algorithmType,inData,outData)
{
	var ret=0;
	var Algo;

        if(algorithmType == "GDCA_ALGO_3DES")
              Algo=26115;
         else if(HashAlgo == "GDCA_ALGO_RC4")
                     Algo=26625;
                 else if(HashAlgo == "GDCA_ALGO_SSF33")
                           Algo=9;
                     else {
                     		alert("�㷨���ʹ���");
                     	        return -2006;
                     	   }
       switch(parseInt(KeyType))
        {
	 case  0 : ret = WellhopeOldKeySymm(i_Com,1,1,keyString,inData,outData);
	 	   break;
	 case  1 : ret = WellhopeNewKeySymmEncrypt(i_Com,Algo,keyString,inData,outData)
	    	   break;
	 default : ret = GDCAKeySymmEncrypt(i_Com,Algo,keyString,inData,outData);
	}
	return ret;
}

//**********************************�ԳƼ���END********************************************
//**********************************�Գƽ���***********************************************
function WellhopeNewKeySymmDecrypt(i_Com,i_algo,keyString,inData,outData)
{
	var ret=0;

   	outData[0] = i_Com.SymmDecrypt(i_algo,keyString,inData);
	if(outData[0]=="")
	{
	ret = -2005;
 	alert("�Գƽ��ܲ��ɹ�:"+ret);
	WellhopeNewKeyLogout(i_Com);
        WellhopeNewKeyEnd(i_Com);
	return ret;
	}

	return ret;
}

function GDCAKeySymmDecrypt(i_Com,i_algo,keyString,inData,outData)
{
	var ret1,ret;
	var SymmKeyObj = 0;
//0:����
	ret = OpkiSymmCryptData(i_Com,0,i_algo,keyString,inData,outData);
/*
        SymmKeyObj = GDCACreateSymmKeyObj(i_Com,0,i_algo,keyString);
	if (SymmKeyObj < 1)
	{
          return SymmKeyObj;
	}

	var CryptTxt = i_Com.GDCA_SymmDecrypt(SymmKeyObj,inData);
	ret = i_Com.GetError();
	outData[0] = CryptTxt;

	ret1 = GDCADestroySymmKeyObj(i_Com,SymmKeyObj);
	if (ret1 != 0)
	{
		return ret1;
	}
	//alert(CryptTxt);
	if (ret != 0 || CryptTxt == "")
	{
		alert("SymmEncrypt Error:"+ret);
		GDCAKeyLogout(i_Com);
		GDCAKeyEnd(i_Com);
		return ret;
	}
*/
       return ret;
}


function GDCASymmDecrypt(i_Com,KeyType,keyString,algorithmType,inData,outData)
{
	var ret=0;
	var Algo;

        if(algorithmType == "GDCA_ALGO_3DES")
              Algo=26115;
         else if(HashAlgo == "GDCA_ALGO_RC4")
                     Algo=26625;
                 else if(HashAlgo == "GDCA_ALGO_SSF33")
                           Algo=9;
                     else {
                     		alert("�㷨���ʹ���");
                     	        return -2007;
                     	   }
       switch(parseInt(KeyType))
        {
	 case  0 : ret = WellhopeOldKeySymm(i_Com,0,1,keyString,inData,outData);
	 	   break;
	 case  1 : ret = WellhopeNewKeySymmDecrypt(i_Com,Algo,keyString,inData,outData)
	    	   break;
	 default : ret = GDCAKeySymmDecrypt(i_Com,Algo,keyString,inData,outData)

	}
	return ret;
}

//**********************************�Գƽ���END********************************************
//*********************************�����ŷ����******************************************
function WellhopeOldKeySealEnvelope(i_Com,EncrytCert,i_algo,inData,outData)
{
	var ret;

	ret=i_Com.OpkiSealEnvelope(EncrytCert,1,inData,inData.length);
        if(ret!=0)
            {
                alert("�����ŷ���ܳ���:"+ret);
            	WellhopeOldKeyLogout(i_Com);
    		WellhopeOldKeyEnd(i_Com);
                return ret;
            }else
  	{
	    outData[0] = i_Com.outData;
            i_Com.CleanOutData();
        }
      return ret;
}

function WellhopeNewKeySealEnvelope(i_Com,EncrytCert,i_algo,inData,outData)
{
	var ret=0;

        outData[0] = i_Com.SealEnvelope(EncrytCert,i_algo,inData);
        if(outData[0] == "")
        {
        ret = -2009;
        alert("�����ŷ���ܳ���:"+ret);
	WellhopeNewKeyLogout(i_Com);
        WellhopeNewKeyEnd(i_Com);
	return ret;
        }

	return ret;
}

function GDCAKeySealEnvelope(i_Com,EncrytCert,i_algo,inData,outData)
{
        var ret;
	var Base64Data = new Array;

	ret = GDCAKeyB64Encode(i_Com,inData,Base64Data);
        if(ret != 0)
     	{
     		alert("�������:"+ret);
     		GDCAKeyLogout(i_Com);
		GDCAKeyEnd(i_Com);
     		return ret;
   	}
       outData[0] = i_Com.GDCA_OpkiSealEnvelope(EncrytCert,Base64Data[0],i_algo);
       ret = i_Com.GetError();
       if(ret != 0)
  	{
	alert("���ܳ���"+ret);
	GDCAKeyLogout(i_Com);
	GDCAKeyEnd(i_Com);
      	return ret;
	}

	return ret;
}

function GDCASealEnvelope(i_Com,KeyType,EncrytCert,EncryptAlgo,inData,outData)
{
	var ret=0,Algo,EncrytData;

	if(EncryptAlgo == "GDCA_ALGO_3DES")
              Algo=26115;
        else if(EncryptAlgo == "GDCA_ALGO_RC4")
                     Algo=26625;
              else if(EncryptAlgo == "GDCA_ALGO_SSF33")
                         Algo=9;
                    else
                    	{
				alert("�㷨���ʹ���");
                     	        return -2008;
                    	}

 	switch(parseInt(KeyType))
        {
	 case  0 :  ret = WellhopeOldKeySealEnvelope(i_Com,EncrytCert,Algo,inData,outData);
	 	    break;
	 case  1 :  ret = WellhopeNewKeySealEnvelope(i_Com,EncrytCert,Algo,inData,outData);
                    break;
	 default :  ret = GDCAKeySealEnvelope(i_Com,EncrytCert,Algo,inData,outData);
	}

	return ret;
}

//*********************************�����ŷ�ӽ���END******************************************
//*********************************�����ŷ����******************************************
function WellhopeOldKeyOpenEnvelope(i_Com,UserPin,inData,outData)
{
	var ret;

	if(UserPin == "" || UserPin.length < 0)
	 		{
			 alert("�û�PIN���󣬲���Ϊ��");
			 ret = -2011;
			 WellhopeOldKeyLogout(i_Com);
    	    		 WellhopeOldKeyEnd(i_Com);
			 return ret;
			}
//����˽Կ�ı�ǩ������˽Կ�ı��������������ŷ�����
	ret=i_Com.OpkiOpenEnvelope(LBL_USERCERT,UserPin,inData);
	if(ret!=0)
	{
	    alert("�����ŷ���ܳ���:"+ret);
            WellhopeOldKeyLogout(i_Com);
    	    WellhopeOldKeyEnd(i_Com);
	    return ret;
	}else
	{
		outData[0] = i_Com.outData;
                i_Com.CleanOutData();
	}

      return ret;
}

function WellhopeNewKeyOpenEnvelope(i_Com,inData,outData)
{
	var ret=0;

        outData[0] = i_Com.OpenEnvelope(inData);
        if(outData[0] == "")
        {
        ret = -2010;
        alert("�����ŷ���ܳ���:"+ret);
	WellhopeNewKeyLogout(i_Com);
        WellhopeNewKeyEnd(i_Com);
	return ret;
        }

	return ret;
}

function GDCAKeyOpenEnvelope(i_Com,inData,outData)
{
        var ret=0;
        var middleText1="";
        //alert("GDCAKeyOpenEnvelope=indata="+inData);
        middleText1 = i_Com.GDCA_OpkiOpenEnvelope(LBL_USERCERT_ENC,GDCA_LBL_ENCKEY,inData);
        if(outData[0] == "")
     	{
     		alert("�����ŷ���ܳ���:"+ret);
     		GDCAKeyLogout(i_Com);
		GDCAKeyEnd(i_Com);
     		return ret;
   	}
       //alert("____________________222"+middleText1);
       ret = GDCAKeyB64Decode(i_Com,middleText1,outData);
       if(ret != 0)
       {
       alert("�������Ĵ���"+ret);
	return ret;
	}
	return ret;
}

function GDCAOpenEnvelope(i_Com,KeyType,UserPin,inData,outData)
{
	var ret=0;

    //alert("______________"+inData);
 	switch(parseInt(KeyType))
        {
	 case  0 :  ret = WellhopeOldKeyOpenEnvelope(i_Com,UserPin,inData,outData);
	 	    break;
	 case  1 :  ret = WellhopeNewKeyOpenEnvelope(i_Com,inData,outData);
                    break;
	 default :  ret = GDCAKeyOpenEnvelope(i_Com,inData,outData);
	}

	return ret;
}
//*********************************�����ŷ����END******************************************
//*********************************����ǩ��*************************************************
function WellhopeOldKeySignData(i_Com,UserPin,inData,outData)
{
	var ret;

	if(UserPin == "" || UserPin.length < 0)
	{
	 alert("�û�PIN���󣬲���Ϊ��");
	 ret = -2012;
	 WellhopeOldKeyLogout(i_Com);
    	 WellhopeOldKeyEnd(i_Com);
	 return ret;
	}
	ret=i_Com.OpkiSignData(LBL_USERCERT,UserPin,inData,inData.length);
        if(ret!=0)
         {
	    alert("ǩ��ʧ�ܣ�����!"+ret);
	    WellhopeOldKeyLogout(i_Com);
    	    WellhopeOldKeyEnd(i_Com);
	    return ret;
	 }else
		{
		     outData[0] = i_Com.outData;
		     i_Com.CleanOutData();
		}

      return ret;
}

function WellhopeNewKeySignData(i_Com,hashAlgo,inData,outData)
{
	var ret=0;

        outData[0] = i_Com.SignData(inData,hashAlgo,0);
        if(outData[0] == "")
        {
        WellhopeNewKeyLogout(i_Com);
        WellhopeNewKeyEnd(i_Com);
        ret=-2013;
        alert("ǩ��ʧ�ܣ�����!"+ret);
        return ret;
        }

	return ret;
}

function GDCAKeySignData(i_Com,ClientSignCert,Algo,inData,outData)
{
        var ret;
	var Base64Data = new Array;

	ret = GDCAKeyB64Encode(i_Com,inData,Base64Data);
        if(ret != 0)
     	{
     		alert("�������:"+ret);
     		GDCAKeyLogout(i_Com);
		GDCAKeyEnd(i_Com);
     		return ret;
   	}

        outData[0] = i_Com.GDCA_OpkiSignData(LBL_USERCERT_SIG,GDCA_LBL_SIGNKEY,ClientSignCert,Base64Data[0],Algo,0);
        ret = i_Com.GetError();
	if(ret != 0)
	{
	     	alert("����ǩ������:"+ret);
     		GDCAKeyLogout(i_Com);
		GDCAKeyEnd(i_Com);
	     	return ret;
	}

	return ret;
}

function GDCASignData(i_Com,KeyType,ClientSignCert,UserPin,inData,hashAlgo,outData)
{
	var ret=0,Algo,ClientSignData;
	var Base64OutData = new Array;

	if(hashAlgo == "GDCA_ALGO_SHA1")
              Algo=32772;
         else if(hashAlgo == "GDCA_ALGO_MD5")
                     Algo=32771;


	switch(parseInt(KeyType))
        {
	 case  0  :  ret = WellhopeOldKeySignData(i_Com,UserPin,inData,outData);
		     break;
	case  1  :   ret = WellhopeNewKeySignData(i_Com,Algo,inData,outData);
           	     break;
	default :    ret = GDCAKeySignData(i_Com,ClientSignCert,Algo,inData,outData);
	}

       return ret;
}
//*********************************����ǩ��END*************************************************
//*********************************��֤ǩ��****************************************************
function WellhopeOldKeyVerifySignData(i_Com,ClientSignCert,originalData,signedData)
{
	var ret=0;

        ret = i_Com.OpkiVerifyData(ClientSignCert,originalData,0,signedData);
    	if(ret!=0)
       	{
	alert("��֤ǩ��ʧ�ܣ�����:"+ret);
	WellhopeOldKeyLogout(i_Com);
    	WellhopeOldKeyEnd(i_Com);
	return ret;
	}

      return ret;
}

function WellhopeNewKeyVerifySignData(i_Com,ClientSignCert,Algo,originalData,signedData)
{
	var ret=0;

        ret = i_Com.VerifySign(ClientSignCert,originalData,signedData,Algo,0);
	if(!ret)
	{
	ret = -2014;
	alert("�ͻ�����֤ǩ��ʧ�ܣ�����!:"+ret);
	WellhopeNewKeyLogout(i_Com);
        WellhopeNewKeyEnd(i_Com);
        return ret;
	}else ret =0;

	return ret;
}

function GDCAKeyVerifySignData(i_Com,ClientSignCert,Algo,originalData,signedData)
{
        var ret;
	var Base64Data = new Array;

	ret = GDCAKeyB64Encode(i_Com,originalData,Base64Data);
        if(ret != 0)
     	{
     		alert("�������:"+ret);
     		GDCAKeyLogout(i_Com);
		GDCAKeyEnd(i_Com);
     		return ret;
   	}

        ret = i_Com.GDCA_OpkiVerifyData(ClientSignCert,Base64Data[0],signedData,Algo,0);
        errorCode = i_Com.GetError();
        if(errorCode != 0)
	 {
	  	alert("�ͻ�����֤ǩ��ʧ�ܣ�����:"+errorCode);
	  	GDCAKeyLogout(i_Com);
		GDCAKeyEnd(i_Com);
     		return ret;
	 }
	else ret = 0;

	return ret;
}

function GDCAVerifySignData(i_Com,KeyType,ClientSignCert,originalData,signedData,hashAlgo)
{
	var ret,Algo,inData;
        var outData = new Array;

	if(hashAlgo == "GDCA_ALGO_SHA1")
              Algo=32772;
         else if(hashAlgo == "GDCA_ALGO_MD5")
                     Algo=32771;
              else  Algo=0;

	switch(parseInt(KeyType))
        {
	 case  0 :  ret = WellhopeOldKeyVerifySignData(i_Com,ClientSignCert,originalData,signedData);
		    break;
	 case 1  :  ret = WellhopeNewKeyVerifySignData(i_Com,ClientSignCert,Algo,originalData,signedData);
	 	    break;
	 default :  ret = GDCAKeyVerifySignData(i_Com,ClientSignCert,Algo,originalData,signedData);
	 	    break;
	}

	return ret;
}
//*********************************��֤ǩ��END*****************************************************
//*********************************��ȡ֤��********************************************************
function WhOldKeyReadCert(i_Com,CertType,CertData)
{
	var ret=0;

	if(CertType != 3)
        {
        	ret=i_Com.OpkiReadLabel(LBL_USERCERT,1);
        }else if(CertType==3)
        	{
        	  ret=i_Com.OpkiReadLabel(LBL_ROOTCACERT,1);
        	}
        if(ret!=0)
        {
	alert("ȡ֤��ʧ�ܣ�����:"+ret);
	WellhopeOldKeyLogout(i_Com);
    	WellhopeOldKeyEnd(i_Com);
	return ret;
	}else
		{
	          CertData[0] = i_Com.outData;
   	          var tDataLen=i_Com.outDataLen;
   	          i_Com.CleanOutData();
		}

      return ret;
}

function WhNewKeyReadCert(i_Com,CertType,CertData)
{
	var ret;

          CertData[0] = i_Com.GetCert(CertType);
	  if(CertData[0] == "")
	  {
	   ret = -2015;
	   var curStatus=i_Com.CurStatus;
	   alert("��ȡ֤�����"+curStatus);
	   GDCAKeyLogout(i_Com);
	   GDCAKeyEnd(i_Com);
           return ret;
	  }else
	  	ret = 0;

       return ret;
}

function GDCAReadLabel(i_Com,labelName,labelType,outData)
{
	var ret;
	var ReadOutData;

      //  alert("labelName="+labelName +"  labelType="+labelType);
	ReadOutData = i_Com.GDCA_ReadLabel(labelName,labelType);
	ret = i_Com.GetError();
        if(ret != 0)
         {
	  	GDCAKeyLogout(i_Com);
		GDCAKeyEnd(i_Com);
	     	return ret;
	 }
   //     ret = GDCABase64Decode(i_Com,KeyType,out_data[0],outData);
//        alert("GDCABase64Decode ret ="+ret);
	outData[0] = ReadOutData;

  	return ret;
}

function GDCAKeyGetCert(i_Com,CertType,CertData)
{
	var ret;

//        alert("CertType="+CertType);
	switch(parseInt(CertType))
        {
         case 1  :  //		alert("������֤��");
		    ret = GDCAReadLabel(i_Com,LBL_USERCERT_ENC,GDCA_LBL_ENCCERT,CertData);
		    if(ret !=0)
		    {
		    	alert("������֤�����"+ret);
		    	return ret;
		    }
		    break;
         case 2  :  //		alert("��ǩ��֤��");
		    ret = GDCAReadLabel(i_Com,LBL_USERCERT_SIG,GDCA_LBL_SIGNCERT,CertData);
		    if(ret !=0)
		    {
		    	alert("��ǩ��֤�����"+ret);
		    	return ret;
		    }
		    break;
	 case 3  :  //	alert("��CA֤��")
		    ret = GDCAReadLabel(i_Com,LBL_CACERT,GDCA_LBL_CACERT,CertData);
		    if(ret !=0)
		    {
		    	alert("��CA֤�����"+ret);
		    	return ret;
		    }
		  //   alert("LBL_CACERT="+CertData[0]);
		    break;
	default  :  alert("�޴�֤������");
	            ret = -2016;
	            return ret;

  	}
            //       alert("CertData1="+CertData[0]);
	return 	ret;
  }

function GDCAReadCert(i_Com,KeyType,CertType,CertData)
{
	var ret;

  //    alert("KeyType="+KeyType);
	//alert("CertType="+CertType);
        switch(parseInt(KeyType))
        {
         case 0  : ret = WhOldKeyReadCert(i_Com,CertType,CertData);
         	   break;
         case 1  : if(CertType!=3 && CertType!=4)
        	   {
                     ret = WhNewKeyReadCert(i_Com,CertType,CertData);
	 	   }else  ret = 0;
	 	   break;
	 default : ret = GDCAKeyGetCert(i_Com,CertType,CertData);
         }
//	 alert("CertData2="+CertData[0]);
	 return ret;
}

//*********************************��ȡ֤��END********************************************************
//*********************************��ȡ֤����Ϣ*******************************************************
function WhOldKeyGetCertInfo(i_Com,CertData,outData)
{
	var ret,CertInfo="";

	ret=i_Com.OpkiGetCertInfo(CertData);
	if(ret!=0)
	{
	    alert("��ȡ֤����Ϣ���󣡣���");
	    WellhopeOldKeyLogout(i_Com);
    	    WellhopeOldKeyEnd(i_Com);
	    return ret;
	}

	//����OpkiGetCertInfo ��ȡ֤����Ϣ
        var serialnum="<serialnum>"+i_Com.SserialNumber+"</serialnum>";
        var issuer="<issuer><dn_c>"+i_Com.SIdnc+"</dn_c><dn_s>"+i_Com.SIdns+"</dn_s><dn_l>"+i_Com.SIdnl+"</dn_l><dn_o>"+i_Com.SIdno+"</dn_o><dn_ou>"+i_Com.SIdnou+"</dn_ou><dn_cn1>"+i_Com.SIdncn1+"</dn_cn1><dn_cn2>"+i_Com.SIdncn2+"</dn_cn2><dn_cn3>"+i_Com.SIdncn3+"<dn_cn3></issuer>";
        var validity="<validity><notbefore>"+i_Com.SbeginTime+"</notbefore><notafter>"+i_Com.SendTime+"</notafter></validity>";
        var subject="<subject><dn_c>CN</dn_c><dn_s>"+i_Com.SSdns+"</dn_s><dn_l>"+i_Com.SSdnl+"</dn_l><dn_o>"+i_Com.SSdno+"</dn_o><dn_ou>"+i_Com.SSdnou+"</dn_ou><dn_cn1>"+i_Com.SIdncn1+"</dn_cn1><dn_cn2>"+i_Com.SIdncn2+"</dn_cn2><dn_cn3>"+i_Com.SIdncn3+"<dn_cn3></subject>";

        CertInfo = "<certinfo><version>3</version>"+serialnum+"<algorithm>SHA1RSA</algorithm>"+issuer+validity+subject+"</certinfo>";
        outData[0] = CertInfo;

        return ret;
}

function WhNewKeyGetCertInfo(i_Com,CertData,outData)
{
	var ret;

	  outData[0] = i_Com.GetCertInfo(CertData);
	  if(outData[0] == "")
	  {
	   ret = -2018;
	   alert("��ȡ֤����Ϣ����"+ret);
	   GDCAKeyLogout(i_Com);
	   GDCAKeyEnd(i_Com);
           return ret;
	  }else
	  	ret = 0;

       return ret;
}

function GDCAKeyGetCertInfo(i_Com,CertData,outData)
{
	var errorCode;
	var InfoValue;


        outData[0] = i_Com.GDCA_GetCertificateInfo(CertData,GDCA_GET_CERT_INFO);
	errorCode = i_Com.GetError();
	if(errorCode != 0)
	{
	    GDCAKeyLogout(i_Com);
	    GDCAKeyEnd(i_Com);
            return errorCode;
	}

	return errorCode;
}
function GDCAGetCertInfo(i_Com,KeyType,CertData,outData)
{
	var ret;

  //    alert("KeyType="+KeyType);
	//alert("CertType="+CertType);

        switch(parseInt(KeyType))
        {
         case 0  : ret = WhOldKeyGetCertInfo(i_Com,CertData,outData);
         	   break;
         case 1  : ret = WhNewKeyGetCertInfo(i_Com,CertData,outData);
	 	   break;
	 default : ret = GDCAKeyGetCertInfo(i_Com,CertData,outData);
         }

//	 alert("CertData2="+CertData[0]);
	 return ret;
}

//*********************************��ȡ֤����ϢEND*******************************************************
//*********************************��֤֤��**************************************************************
function WhOldKeyCheckCert(i_Com,inCertData,trustCertData,certsChainData,crlData)
{
	var ret;

//��������֤֤�����Ч�ԡ�
//		long CPkiComCtrl::OpkiCheckCert(
//			LPCTSTR inCertData, 		//������Ĵ���֤��֤��
//			LPCTSTR trustCertData, 		//�������������֤������֤��
//			LPCTSTR certsChainData, 	//���������֤��֤��·��
//			LPCTSTR crlData			//�������������֤�ĺ�����
//			)
	ret = i_Com.OpkiCheckCert(inCertData,trustCertData,certsChainData,crlData);
        if(ret!=0)
        	  {
	                 alert("�ͻ��˶Է����֤�鲻���Σ�����:"+ret);
	                 WellhopeOldKeyLogout(i_Com);
    	    		 WellhopeOldKeyEnd(i_Com);
	                 return ret;
	            }
	return ret;
}

function WhNewKeyCheckCert(i_Com,inCertData)
{
   var flag=false,ret;

   flag=i_Com.CheckCert(inCertData);
   if(!flag)
   {
             ret = -2017;
             //  alert("OCSP��������");
            alert("�ͻ��˶Է����֤�鲻���Σ�����:"+ret);
	    GDCAKeyLogout(i_Com);
	    GDCAKeyEnd(i_Com);
            return flag;
      }else ret = 0;
   return ret;
}

function GDCAKeyCheckCert(i_Com,inCertData,caCert,crlData)
{
    var ret;
    alert("GDCAKeyCheckCert Start");
    ret = i_Com.GDCA_VerifyCert(inCertData,caCert);
    if(ret != 0)
	{
	    GDCAKeyLogout(i_Com);
	    GDCAKeyEnd(i_Com);
            alert("�û�֤��Ƿ���"+ret);
            return ret;
	}
    alert("GDCA_VerifyCert end");
    ret = i_Com.GDCA_VerifyCrl(crlData,caCert);
    if(ret != 0)
	{
	    GDCAKeyLogout(i_Com);
	    GDCAKeyEnd(i_Com);
            alert("�����б�Ƿ���"+ret);
            return ret;
	}
    ret = i_Com.GDCA_CheckCertByCrl(inCertData,crlData);
    if(ret != 0)
	{
	    GDCAKeyLogout(i_Com);
	    GDCAKeyEnd(i_Com);
            alert("��֤���ѱ�������"+ret);
            return ret;
	}
    return ret;
}

function GDCACheckCert(i_Com,KeyType,inCertData,UserRootCert,crlData)
{
        var ret;
	switch(parseInt(KeyType))
        {
	case  0 : ret = WhOldKeyCheckCert(i_Com,inCertData,UserRootCert,"","");
	          break;
	case  1 : //ret = WhNewKeyCheckCert(i_Com,inCertData);
	          ret = 0;
	          break;
	default : //ret = GDCAKeyCheckCert(i_Com,inCertData,UserRootCert,crlData);
	          ret = 0;
	 }
        return ret;
}
//*********************************��֤֤��END**************************************************************
//*************************************��ȡӲ������ID*******************************************************
function WhOldKeyGetDisaid(i_Com,outData)
{
	var ret;

	ret=i_Com.OpkiReadLabel0(LBL_DISAID,3);
    	if(ret!=0)
    	{
	  alert("��ȡDISAID ����"+ret);
	  WellhopeOldKeyLogout(i_Com);
    	  WellhopeOldKeyEnd(i_Com);
	  return ret;
	}else
	{
        	outData[0] = i_Com.outData;
        	i_Com.CleanOutData();
	}

	return ret;
}
/*
function WhNewKeyGetDisaid(i_Com,outData)
{
 var ret;

 outData[0] = i_Com.GetKeyID();
 if(outData[0]=="")
 {
  ret = i_Com.CurStatus;
  alert("��ȡ֤����Ϣ����"+ret);
  return ret;
 }
   return ret;
}
*/
function WhNewKeyGetDisaid(i_Com,outData,ClientCert)
{
 var ret;

 var ClientCertInfo = i_Com.GetCertInfo(ClientCert);
 if(ClientCertInfo == "")
 {
  ret = i_Com.CurStatus;
  alert("��ȡ֤����Ϣ����"+ret);
  return ret;
 }else ret =0;

 var indx1 = ClientCertInfo.indexOf("<serialnum>");
 var indx2 = ClientCertInfo.indexOf("</serialnum>");

  outData[0] = ClientCertInfo.substring(indx1+11,indx2);

   return ret;
}

function GDCAKeyGetDisaid(i_Com,outData)
{
	var ret;
	var KeyID = new Array;

	ret = GDCAReadLabel(i_Com,LBL_DISAID,GDCA_LBL_EXDATA,KeyID);
	if(ret != 0)
        {
             alert("��ȡKeyIDʧ�ܣ�"+ret);
             return ret;
        }
        ret  = GDCAKeyB64Decode(i_Com,KeyID[0],outData);
        if(ret != 0)
        {
             alert("����KeyIDʧ�ܣ�"+ret);
             return ret;
        }
        return ret;
}

function GDCAGetKeyID(i_Com,KeyType,KeyID,CertData)
{
	var ret=0;

	switch(parseInt(KeyType))
        {
         case 0 : ret = WhOldKeyGetDisaid(i_Com,KeyID);
                  break;
         case 1 : ret = WhNewKeyGetDisaid(i_Com,KeyID,CertData);
                  break;

	default : ret = GDCAKeyGetDisaid(i_Com,KeyID);
        }
	return ret;
}
//*************************************��ȡӲ������ID END*******************************************************
//************************************��д�ļ�******************************************************************
function WhOldKeyReadFile(i_Com,filename1,outData)
{
	var ret;
//        alert("ReadFile Start");
	ret=i_Com.ReadFile(filename1);
//	alert("ReadFile end");
    	if(ret!=0)
    	{
	  alert("��ȡ�ļ�����"+ret);
	 // WellhopeOldKeyLogout(i_Com);
    	 // WellhopeOldKeyEnd(i_Com);
	  return ret;
	}else
	{
        	outData[0] = i_Com.outData;
        	i_Com.CleanOutData();
	}

	return ret;
}

function WhNewKeyReadFile(i_Com,filename1,outData)
{
	var ret;

	outData[0] = i_Com.ReadFromFile(filename1);
	if(outData[0] == "")
	{
	alert("�ļ���ȡʧ��!");
	return -2020;
	}else ret = 0;

	return ret;
}

function GDCAKeyReadFile(i_Com,filename1,outData)
{
	var ret=0;

        outData[0] = i_Com.GDCA_ReadFile(0,filename1,1);
        ret = i_Com.GetError();
        if(ret != 0)
        {
           alert("�ļ���ȡʧ��:"+ret);
           return ret;
        }
      return ret;
}

function GDCAReadFile(i_Com,KeyType,filename1,outData)
{
       var ret = 0;

       switch(parseInt(KeyType))
        {
         case 0 : ret = WhOldKeyReadFile(i_Com,filename1,outData);
                  break;
         case 1 : ret = WhNewKeyReadFile(i_Com,filename1,outData);
                  break;
	default : ret = GDCAKeyReadFile(i_Com,filename1,outData);
        }
	return ret;
}

function WhOldKeyWriteFile(i_Com,filename1,inData)
{
      var ret;

      ret = i_Com.WriteFile(filename1,inData,inData.length);
      if(ret != 0)
      {
      	alert("д�ļ�ʧ�ܣ�"+ret);
      	return ret;
	}

      return ret;
}

function WhNewKeyWriteFile(i_Com,filename1,inData)
{
      var ret;

      flag=i_Com.WriteToFile(filename1,inData);
      if(flag)
      {
       alert("�����ļ��ɹ�!");
      }
      else
      {
       alert("�ļ���������쳣����!�������");
      }

      return flag;
}

function GDCAKeyWriteFile(i_Com,filename1,inData)
{
	var ret;

        ret = i_Com.GDCA_WriteFile(filename1,0,inData);
        ret = i_Com.GetError();
        if(ret != 0)
        {
           alert("�����ļ�ʧ��:"+ret);
           return ret;
        }

      return ret;
}

function GDCAWriteFile(i_Com,KeyType,filename1,inData)
{
	var ret;

        switch(parseInt(KeyType))
        {
         case 0 : ret = WhOldKeyWriteFile(i_Com,filename1,inData);
                  break;
         case 1 : ret = WhNewKeyWriteFile(i_Com,filename1,inData);
                  break;
	default : ret = GDCAKeyWriteFile(i_Com,filename1,inData);
        }

	return ret;
}
//************************************��д�ļ�END**************************************************//
//��������������������������������������������������������������������������ȡ�û�֤�����Чʱ�������������������������������������������������������������������������������//
//��ȡ1.0key��֤����Чʱ��
function WhOldKeyGetTime(i_Com,KeyType,indata)
{
	
	var ret=0;
	ret=i_Com.OpkiGetCertInfo(indata);
	if(ret!=0)
	{
	    alert("��ȡ֤����Ϣ���󣡣���"+ret);
		ret=i_Com.OpkiLogout();
		ret=i_Com.OpkiEnd();
	    return -1;
	}
	//��ȡ֤��Ľ���ʱ��
	var CertEndTime=i_Com.SendTime;
	GDCACerEndDate(KeyType,CertEndTime);
	return ret;
}

//��ȡ2.0key��֤����Чʱ��
function WhNewKeyGetTime(i_Com,KeyType,signcert)
{
	
	var ret=0;
	var ClientCertInfo = i_Com.GetCertInfo(signcert);
	if(ClientCertInfo == "")
	{
		ret = i_Com.CurStatus;
		alert("��ȡ֤����Ϣ����"+ret);
		return -1;
	}

	var indx1 = ClientCertInfo.indexOf("<notafter>");
	var indx2 = ClientCertInfo.indexOf("</notafter>");
	var data = ClientCertInfo.substring(indx1+10,indx2);
	var CertEndTime=data;
	GDCACerEndDate(KeyType,CertEndTime);
	return ret;
}

//��ȡ3.0key��֤����Чʱ��
function GDCAKeyGetTime(i_Com,KeyType,signcert)
{
	
	var ret=0;
	var date=i_Com.GDCA_GetCertificateInfo(signcert,6);
	ret = i_Com.GetError();
	var CertEndTime = date.substring(15,23);
	GDCACerEndDate(KeyType,CertEndTime); 
	return ret;
}

//�ȽϿͻ���ϵͳʱ����û�֤�����Чʱ��
function GDCACerEndDate(KeyType,inData)
{
	var ret=0;
	var time=inData;
	var today = new Date();
	var sysday= new Date();
    var DisaEndDate = new Date();

//	alert("KeyType=== " + KeyType);
//	alert("֤�鵽��ʱ��=== " + time);
	//��1.0��֤������ж�
	if(KeyType==0)
	{
		DisaEndDate.setFullYear("20"+time.substring(0,2));
		//alert(time.substring(0,2));
		DisaEndDate.setMonth(time.substring(2,4)-1);
		//alert(time.substring(2,4));
		DisaEndDate.setDate(time.substring(4,6));
		//alert(time.substring(4,6));
	}
	//��2.0��֤������ж�
	if(KeyType==1)
	{
		DisaEndDate.setFullYear(time.substring(0,4));
		//alert(time.substring(0,4));
		DisaEndDate.setMonth(time.substring(5,7)-1);
		//alert(time.substring(5,7));
		DisaEndDate.setDate(time.substring(8,10));
		//alert(time.substring(8,10));
	}
	//��3.0��֤������ж�
	if(KeyType==2)
	{
		DisaEndDate.setFullYear(time.substring(0,4));
		//alert(time.substring(0,4));
	    DisaEndDate.setMonth(time.substring(4,6)-1);
		//alert(time.substring(4,6));
		DisaEndDate.setDate(time.substring(6,8));
		//alert(time.substring(6,8));
	}
//	alert("DisaEndDate===" + DisaEndDate.toLocaleString());
	
	sysday.setFullYear(today.getFullYear());
    sysday.setMonth(today.getMonth());
	sysday.setDate(today.getDate());
	
//	alert("sysday===" + sysday.toLocaleString());
	
	var tempmonth=sysday.getMonth()+1;
	var tempsysmonth=DisaEndDate.getMonth()+1;
	//alert("sys="+sysday.getFullYear()+tempmonth+sysday.getDate());
	//alert("DisaEndDate="+DisaEndDate.getFullYear()+tempsysmonth+DisaEndDate.getDate());

	//alert((DisaEndDate.getTime()-sysday.getTime())/(1000*60*60*24));
	var havetime=(DisaEndDate.getTime()-sysday.getTime())/(1000*60*60*24);
	//alert(havetime);
    if((((DisaEndDate.getTime()-sysday.getTime())/(1000*60*60*24))<30)&&(((DisaEndDate.getTime()-today.getTime())/(1000*60*60*24))>=0))
	{
		alert("����֤�黹��"+havetime+"�����");
		alert("�����û�֤�齫��"+time+"����");
		
	}
	if(((DisaEndDate.getTime()-sysday.getTime())/(1000*60*60*24))<0)
	{
		alert("�����û�֤���Ѿ�����");
		
	}
	
}

function GDCAGetCerTime(i_Com,KeyType,signcert)
{
	var ret;
	
	switch(parseInt(KeyType))
	{
		case 0 : ret = WhOldKeyGetTime(i_Com,KeyType,signcert);
                  break;
		case 1 : ret = WhNewKeyGetTime(i_Com,KeyType,signcert);
                  break;
		default : ret = GDCAKeyGetTime(i_Com,KeyType,signcert);
    }
	
	return ret;
}
