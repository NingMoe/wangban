(function(){
	//检查登录状态
	checkLogin();
	var userId=$("#userInfo").val();
	if(LEx.isNotNull(userId)){
		var cmd = new LEx.Command("app.uc.UserCmd");
		cmd.setParameter("id",userId);
	    var ret = cmd.execute("getEmailLast");
	    if (ret.state == 1) {
	        if (ret.data) {
	        	$("#YCOM").html(ret.data);       
	        }else{
	        	$("#YMAIL").val("").removeAttr("reg").removeAttr("tip");
	        	$("#oldMail").hide();
	        }
	    }else{
	        LEx.dialog({ title: "系统提示", content: ret.message, icon: 'error' ,lock:true});
	    }
	    
	    //提交表单
		$("#submit_btn").click(function(){
			bindMail();
		});	
		
		//清除信息
		$("#cancle_btn").click(function() {
			$("#MAIL").val("");
			$("#PASSWORD").val("");
		});
		
		//检验普通项的数据
		$("[reg]").bind("blur",function(){
			var obj=$(this);
			validateObj(obj);
		});
	}
})();

function bindMail(){
	if(!checkValidate()){
		return false;
	}
	var userId=$("#userInfo").val();//用户id	
	var password=toMD5Str($("#PASSWORD").val());//登录密码
	var oldEmail=$("#YMAIL").val()+$("#YCOM").html();//原密保邮箱
	var newEmail=$("#MAIL").val();//新密保邮箱	
	var cmd = new LEx.Command("app.uc.UserCmd");
	cmd.setParameter("id",userId);
	cmd.setParameter("password",password);
	cmd.setParameter("ymail",oldEmail);
	cmd.setParameter("mail",newEmail);	
	var url=window.location.href;
	if(url.indexOf("http://")==0){
        url=url.replace("http://","");       
    }
	url=url.substr(0,url.indexOf("/"));
    url = "http://"+url+LEx.webPath;
	cmd.setParameter("url",url);
	var ret = cmd.execute("rebindValidMail");
	if(ret.state==1){
		LEx.dialog({title : "系统提示",content : "邮件发送成功！", icon: 'succeed' ,lock : true});
		var data={};
		data["uid"]=userId;
		$(".show_tab").load(LEx.webPath+"app/uc/user?action=rebind",data);		
	}else{
		errorDialog("系统提示","发送邮件失败："+ret.message);
	}	
	return false;
}