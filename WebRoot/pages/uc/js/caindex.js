function MM_swapImgRestore() { //v3.0
	  var i,x,a=document.MM_sr; for(i=0;a&&i<a.length&&(x=a[i])&&x.oSrc;i++) x.src=x.oSrc;
	}

	function MM_preloadImages() { //v3.0
	  var d=document; if(d.images){ if(!d.MM_p) d.MM_p=new Array();
	    var i,j=d.MM_p.length,a=MM_preloadImages.arguments; for(i=0; i<a.length; i++)
	    if (a[i].indexOf("#")!=0){ d.MM_p[j]=new Image; d.MM_p[j++].src=a[i];}}
	}

	function MM_swapImage() { //v3.0
	  var i,j=0,x,a=MM_swapImage.arguments; document.MM_sr=new Array; for(i=0;i<(a.length-2);i+=3)
	   if ((x=MM_findObj(a[i]))!=null){document.MM_sr[j++]=x; if(!x.oSrc) x.oSrc=x.src; x.src=a[i+2];}
	}

	function GDCA_Finalize(i_Com)
	{
		try
		{
			var errorCode;               //COM控件返回的错误代码
			i_Com.GDCA_Finalize();       //释放资源
		  	errorCode = i_Com.GetError();//检查函数是否成功运行，在函数运行结束后调用
		  	if (errorCode != 0)
	        	alert("释放接口资源失败！");
	  	}
	  	catch(objError)
	  	{
	  		alert("GDCA3.0接口资源释放出错！");
	  	}
	    return errorCode;
	}

	function GDCA_Logout(i_Com)
	{
		var ret,errorCode;
		
		try
		{
			ret = i_Com.GDCA_Logout();      //退出登录
			errorCode = i_Com.GetError();	//检查函数是否成功运行，在函数运行结束后调用
			if(errorCode != 0)              //退出登录失败
				alert("退出登录不成功" );
		}
		catch(objError)
		{
			alert("GDCA3.0退出登录出错！ ");
		}
		finally
		{
			GDCAFinalize(i_Com);            //释放资源
		}
		return ret;
	}


	function calogin()
	{
		var GDCACom ;                       //GDCACOM控件
		var DeviceType;                     //密码设备类型
		var errorCode = -1;                 //COM控件返回的错误代码
		var ReadOutSignCert;                //读出的用户签名证书
		var ReadOutEncCert;                 //读出的用户加密证书
		var ReadOutKeyIdEnCode;             //读出的唯一标识符（未解码）
		var ReadOutKeyId;                   //读出的唯一标识符
		var randomData;                 //服务器端传来的随机数
		var Base64Data;                 //BASE64编码后的数据，中间值，作为加密或签名的传入参数
		var ClientSignData;   			//客户端签名值
		var ret;                        //返回值
		var hashAlgo="GDCA_ALGO_SHA1";  //签名算法
		var outData = new Array;        //出参，用于保存客户端签名值
		var user_pin = form1.user_pin.value;//密码设备PIN码
		var user_sign_cert;
		var trustid;

		//加载控件
		try
	  	{
	  		//Atl_com.Gdca为GDCAUSBkey的COM接口
	     	GDCACom = new ActiveXObject("Atl_com.Gdca");
	     	//alert("3.0控件创建成功！");
	  	}
	  	catch(objError)   
	  	{
	  		alert("GDCA3.0控件加载失败！.   error: "   +   objError.description);
		 	//ReadCert();
		 	return false;
	  	}

	  	//初始化控件
		try
		{
			DeviceType = GDCACom.GDCA_GetDevicType();   //获取客户端密码设备设备类型
			
			switch(parseInt(DeviceType))
	    	{
	    		case -1:alert("请插入证书硬件介质(USBKey):"+ DeviceType);
						break;
				case -2:alert("注册表错误,请导入正确的注册表文件: "+ DeviceType);
						break;
				case -3:alert("有其他的USB设备: "+ DeviceType);
	         			break;
				default:
						//设置密码设备类型，具体设置详见gdca_device.ini的配置信息，gdca_device.ini在安装包中有
						GDCACom.GDCA_SetDeviceType(DeviceType);
	  					errorCode = GDCACom.GetError();	//检查函数是否成功运行，在函数运行结束后调用
					    if(errorCode != 0)
					    {
					    	alert("设置密码设备类型出错");
					        return errorCode;
					    }

					    //初始化接口所需要的全局资源（内存、信号量等），如果初始化成功，返回值为0
					    GDCACom.GDCA_Initialize();				    
	  					errorCode = GDCACom.GetError(); //检查函数是否成功运行，在函数运行结束后调用
					    if(errorCode != 0)
					    {
					    	alert("初始化控件出错");
					        return errorCode;
					    }
	                	break;
			}
		}
		catch(objError)   
	  	{   
	     	alert("GDCA3.0控件初始化失败！");
	     	GDCA_Finalize(GDCACom);         /*释放密码设备接口资源*/
		 	return false;
	  	}
		try
		{
			GDCACom.GDCA_Login(2,user_pin); //客户端密码设备登录
			errorCode = GDCACom.GetError();	//检查函数是否成功运行，在函数运行结束后调用
			if(errorCode != 0)
			{
	          	GDCA_Finalize(GDCACom); //释放GDCA_Initialize中分配的全局资源
				return false;
			}
		}
		catch(objError)
		{
			alert("用户登录出错！");
			GDCA_Finalize(GDCACom);         /*释放密码设备接口资源*/
		 	return false;
		}
	 
		//读取用户证书
		try
		{
			ReadOutSignCert = GDCACom.GDCA_ReadLabel("LAB_USERCERT_SIG",7);
			errorCode = GDCACom.GetError(); //检查函数是否成功运行，在函数运行结束后调用
			//证书读取失败
	        if(errorCode != 0)           
	        {
	        	alert("读取用户签名证书失败：" + errorCode);
	         	//退出登录
		  		GDCA_Logout(GDCACom);
				return false;
		 	}
		}
		catch(objError)
		{
			alert("读取用户签名证书出错！.   error: "   +   objError.description);
			GDCA_Finalize(GDCACom);          /*释放密码设备接口资源*/
		 	return false;
		}
		
		try
		{
			//读取用户加密证书，保存在ReadOutEncCert中
			ReadOutEncCert = GDCACom.GDCA_ReadLabel("LAB_USERCERT_ENC",8);
			errorCode = GDCACom.GetError();  //检查函数是否成功运行，在函数运行结束后调用
	        if(errorCode != 0)           
	        {
	        	alert("读取用户加密证书失败：" + errorCode);
	         	//退出登录
		  		GDCA_Logout(GDCACom);
				return false;
		 	}
		}
		catch(objError)
		{
			alert("读取用户加密证书出错！.   error: "   +   objError.description);
			GDCA_Finalize(GDCACom);         /*释放密码设备接口资源*/
		 	return false;
		}

		//判断证书有效期
		try
		{
			errorCode = GDCAGetCerTime(GDCACom,2,ReadOutSignCert);
			if(errorCode != 0)
	   		{
				alert("判断证书是否过期失败：" + errorCode);
				return errorCode;
	   		}
		}
		catch(objError)
		{
			alert("判断证书是否过期出错！.   error: "   +   objError.description);
			GDCA_Finalize(GDCACom);         释放密码设备接口资源
		 	return false;
		}
	//将JS的参数保存在form里传递给服务器端
		form1.user_sign_cert.value = ReadOutSignCert; //用户签名证书
		form1.user_enc_cert.value = ReadOutEncCert;   //用户加密证书
		form1.KeyType.value = 2;                      //KEY类型 
		form1.user_pin.focus();
	   	caSumit();
	}
	function caSumit(){
		user_sign_cert = $("#user_sign_cert").val();
		var user_enc_cert  = $("#user_enc_cert").val();
		var KeyType = $("#KeyType").val();
		var user_pin = $("#user_pin").val();
		var cmd = new LEx.Command("app.calogin.caLoginCmd");
		cmd.setParameter("USER_SIGN_CERT",user_sign_cert);
		cmd.setParameter("USER_ENC_CERT",user_enc_cert);
		cmd.setParameter("KeyType",KeyType);
		cmd.setParameter("USER_PIN", user_pin);
		var ret = cmd.execute("caLoginStep1");
		randomData = ret.data.RANDOMDATA;
		UserPin = ret.data.USER_PIN;
		user_sign_cert = ret.data.USER_SIGN_CERT; 
		if (randomData =="" )
		{
	        alert("未得到服务器端传来的随机数!");
	    	return -1;
		}
		if (ret.state == 1) {
			caStep2();
			return true;
		} else {
			errorDialog("系统提示", "登录失败：" + ret.message);
		}
		return false;
	}
	
	function caStep2(){
		//对收集的数据进行BASE64位编码
		try
		{
			Base64Data = GDCACom.GDCA_Base64Encode(randomData);
	   		ret = GDCACom.GetError();       //检查函数是否成功运行，在函数运行结束后调用
	   		if(ret != 0)
	   		{	
	   			alert("BASE64编码失败：" + ret);
	   			GDCA_Logout(GDCACom);       //退出登录，释放资源
	    	}
	    }
	    catch(objError)
	    {
	    	alert("BASE64编码出错！.   error:"   +   objError.description);
			GDCA_Finalize(GDCACom);         /*释放密码设备接口资源*/
	 		return false;
	    }
	    
	    //使用用户签名证书UserSignCert，对传入的BASE64位已编码数据Base64Data进行数字签名
	        	try
	        	{
	    	    	ClientSignData = GDCACom.GDCA_OpkiSignData("LAB_USERCERT_SIG",4,user_sign_cert,Base64Data,32772,0);
	    	    	ret = GDCACom.GetError();       //检查函数是否成功运行，在函数运行结束后调用
	    	    	if(ret != 0)
	    	  		{
	    				alert("客户端数字签名失败：" + ret);
	    				GDCA_Logout(GDCACom);       //退出登录，释放资源
	    			}
	    		}
	    		catch(objError)
	    		{
	    			alert("客户端数字加密出错！.   error:"   +   objError.description);
	    			GDCA_Finalize(GDCACom);         /*释放密码设备接口资源*/
	    	 		return false;
	    		}
	    		
	    		//alert("对数据的签名值" + SignOutData);
	    		if(ClientSignData == "")
	    		{
	    			alert("客户端签名失败，请检查!");
	    		    return;
	    		}
	    		var cmd = new LEx.Command("app.calogin.caLoginCmd");
	    		cmd.setParameter("RandomData",randomData);
	    		cmd.setParameter("user_sign_data",ClientSignData);
	    		cmd.setParameter("USER_PIN", UserPin);
	    		cmd.setParameter("user_sign_cert",user_sign_cert)
	    		var ret = cmd.execute("caLoginStep2");
	    		$("#pubkey").val(ret.data.TRUSTID);
	    		doSumit("form1");
	}