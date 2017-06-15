(function(){
	//检查登录状态
	checkLogin();
	var userId=$("#userInfo").val();
	if(LEx.isNotNull(userId)){
		var cmd = new LEx.Command("app.uc.UserCmd");
		cmd.setParameter("id",userId);
	    var ret = cmd.execute("getHidePhone");
	    if (ret.state == 1) {
	    	if(ret.data){
        		$("#OLDMOBILE").html(ret.data);
        	}else{
        		$("#oldPhone").hide();
        	}
	    }else{
	        LEx.dialog({ title: "系统提示", content: ret.message, icon: 'error' ,lock:true});
	    }
	    
	    //提交表单
		$("#submit_btn").click(function(){
			bindPhone();
		});	
		
		//清除信息
		$("#cancle_btn").click(function() {
			$("#PHONE").val("");
			$("#VCODE").val("");
			$("#PASSWORD").val("");
		});
		
		//检验普通项的数据
		$("[reg]").bind("blur",function(){
			var obj=$(this);
			validateObj(obj);
		});
		
		//绑定短信发送功能
		bindMessage();
	}
})();

//发送短信验证码事件
function bindMessage(){
	$("#verify_btn").unbind("click").bind("click",function(){
		var phone=$("#PHONE").val();
		if(isNotNull(phone)){
			if($("#PHONE").hasClass("input_validation-failed")){
				errorDialog("系统提示","手机号未通过系统校验");
			}else{
				sendMessage(phone);
			}
		}else{
			errorDialog("系统提示","请输入手机号码");
			return false;  
		}
	});	
}

function waitSend(time){
	if(time>0){
		time--;
		$("#verify_btn_text").html(time+"秒后重新发送");	
		setTimeout(function(){waitSend(time);},1000);	
	}else{
		bindMessage();//重新绑定发送短信验证码事件		
		$("#verify_btn").removeClass("opt_btn_gray");
		$("#verify_btn_text").html("获取短信验证码");
		$("verifyOK").html("请输入短信验证码").css("color","gray");
		return;
	}
}

//发送短信验证码
function sendMessage(phone){
	var cmd = new LEx.Command("app.uc.UserCmd");	
	cmd.setParameter("PHONE",phone);
	cmd.setParameter("SMS","BindSms");
	var ret = cmd.execute("sendMessage");
	if (ret.state == 1){
		$("#verify_btn").attr("send","1");//标志已成功发送短信验证码		
		$("#verify_btn").unbind("click").addClass("opt_btn_gray").css("cursor","default");
		$("verifyOK").html("已发送手机验证码，请查收并输入").css("color","red");
		waitSend(60);//重新发送短信倒数计时	
		return true;
	}else{
		$("#verify_btn").attr("send","0");//标志未成功发送短信验证码
		errorDialog("系统提示",ret.message);
		return false;
	}
}

function bindPhone(){
	if(!checkValidate()){
		return false;
	}
	var send=$("#verify_btn").attr("send");
	if(send!="1"){
		errorDialog("系统提示","还未获取短信验证码！");
		return false;
	}
	var cmd = new LEx.Command("app.uc.UserCmd");
	var userId=$("#userInfo").val();;//用户id	
	cmd.setParameter("id",userId);
	cmd.setParameter("phone",$("#PHONE").val());//密保手机号
	cmd.setParameter("vcode",$("#VCODE").val());//短信验证码
	var password=toMD5Str($("#PASSWORD").val());//确认登录密码
	cmd.setParameter("password",password);
	var ret = cmd.execute("changePhone");
	if (ret.state == 1) {
		LEx.dialog({title : "系统提示",content : "绑定新手机号成功", icon: 'succeed' ,lock : true});
		return true;
	} else {
		errorDialog("系统提示","绑定新手机号失败："+ret.message);
	}
	return false;
}