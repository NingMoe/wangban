$(document).ready(function() {
	var body = $("body");
	$("#verify-dialog-mail").dialog({
	      autoOpen : false,
		  bgiframe : true,
		  title : "找回密码失败",
		  modal : true,
		  buttons : {
		      '确定':function() {
		    		$(this).dialog('close');
		    	}
		  }
	});
	
	//去掉重选找回方式的按钮
	var cancelReChoose = function(){
		var e = $("body").find("div[name='re_choose']");
		if(e[0]){
			e.remove();
		}
	}
	//清除填写的信息
	var bindClear = function(){
		body.find("input[name='checknum_mail']").val("");
	};
	//提交
	var submit = function(){
		var login_name = body.find("input[name='login_name']").val();
		var submit_url = body.find("input[name='doGetByMail_url']").val()+"&user="+login_name;
		window.location.href  = submit_url;
		return;
	};
	
	$("#submit_btn_mail").click(function() {
		submit();
	});
	$("#cancle_btn_mail").click(function() {
		bindClear();
	});
	
	
});
