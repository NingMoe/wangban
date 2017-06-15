(function(){
	//检查登录状态
	checkLogin();
	
	//提交表单
	$("#submit_btn").click(function(){
		changePwd();
	});	
	
	//重置密码
	$("#clear_btn").click(function(){
		$("#oldPassWord").val("");
		$("#newPassWord").val("");
		$("#cfmPassWord").val("");		
	});
	
	//检验普通项的数据
	$("[reg]").bind("blur",function(){
		var obj=$(this);
		validateObj(obj);
	});
})();

function changePwd(){
	if(!checkValidate("change-form")){
		alert("请按要求进行填写！");
		return false;
	}
	var cmd = new LEx.Command("app.uc.UserCmd");
	var userId=$("#userInfo").val();//用户id	
	cmd.setParameter("id",userId);
	var oldPassWord=toMD5Str($("#oldPassWord").val());//原密码
	cmd.setParameter("password",oldPassWord);
	var newPassWord=$("#newPassWord").val();//新密码
	cmd.setParameter("newPassWord",toMD5Str(newPassWord));
	var ret = cmd.execute("changePwd");
	if (ret.state == 1) {
		LEx.dialog({title : "系统提示",content : "修改密码成功！", icon: 'succeed' ,lock : true});
		return true;
	} else {
		errorDialog("系统提示","修改密码失败："+ret.message);
	}
	return false;
}
function check_confirm_password(obj){
	var t = $(".form-tip span[for="+obj.attr("id")+"]");
	if(obj.val()!=$("#newPassWord").val()){
		t.html("新密码与确认密码不一致");
		return false;
	}
	t.html("");
	return true;
}

function check_new_password(obj){
	var t = $(".form-tip span[for="+obj.attr("id")+"]");
	if(obj.val()==$("#oldPassWord").val()){
		t.html("新密码与当前密码一致");
		return false;
	}
	t.html("");
	return true;
}







