(function(){
	//检查登录状态
	checkLogin();	
	var userId=$("#userInfo").val();
	if(LEx.isNotNull(userId)){
		var cmd = new LEx.Command("app.uc.UserCmd");
		cmd.setParameter("id@=",userId);
	    var ret = cmd.execute("getList");
	    if (ret.state == 1) {
	        if (ret.data.length > 0) {
	        	var question=ret.data[0].QUESTION;
	        	if(LEx.isNotNull(question)){
	        		 $("#oldQue").html(question);
	        		 $("#oldAnswer").attr("reg","^.+$").attr("tip","请输入旧密保答案");
	        	}else{
	        		$("#oldmb").html("尚未设置密保!");
	        		$("#oldAnswer").attr("disabled","ture");
	        	}
	        }
	    }else{
	        LEx.dialog({ title: "系统提示", content: ret.message, icon: 'error' ,lock:true});
	    }
	    
	    //提交表单
		$("#submit_btn").click(function(){
			bindAsk();
		});	
		
		//清除信息
		$("#cancle_btn").click(function() {
			$("#oldAnswer").val("");
			$("#secAsk_select").val("");
			$("#selfQue").val("");
			$("#newAnswer").val("");
			$("#password").val("");
		});
	
		$("#secAsk_select").change(function(){
			var tag=$(this).children("option[selected]").attr("tag");
			if(tag=="other"){
				$("#selfquestion").show();
				$("#selfQue").attr("reg","^.+$").attr("tip","请输入自定义密保问题").val("");			
				$("#newAnswer").attr("reg","^.{1,20}$").attr("tip","请输入自定义密保问题");
			}else{				
				$("#selfquestion").hide();
			}
			bindBlur();
		});
	
		bindBlur();//根据正则表达式校验表格内容，动态提示
	}
})();

function bindBlur(){
	//检验普通项的数据
	$("[reg]").bind("blur",function(){
		var obj=$(this);
		validateObj(obj);
	});
}

function bindAsk(){
	if(!checkValidate()){
		return false;
	}
	var userid=$("#userInfo").val();//用户id
	var pass=toMD5Str($("#password").val());//登录密码
	var oldAnswer=$("#oldAnswer").val();//原密保问题
	var secAsk=$("#secAsk_select").val();//新密保问题
	var newAnswer=$("#newAnswer").val();//新密保答案	
	var cmd = new LEx.Command("app.uc.UserCmd");
	cmd.setParameter("id",userid);	
	cmd.setParameter("password",pass);	
	cmd.setParameter("oldanswer",oldAnswer);
	if(secAsk=="other"){
		cmd.setParameter("question",$("#selfQue").val());
	}else{
		cmd.setParameter("question",secAsk);
	}	
	cmd.setParameter("answer",newAnswer);	
	var ret = cmd.execute("changeAsk");
	if (ret.state == 1) {
		LEx.dialog({title : "系统提示",content : "修改密保问题成功", icon: 'succeed' ,lock : true});
		return true;
	} else {
		errorDialog("系统提示","修改密保问题失败："+ret.message);
	}
	return false;
}


/*//校验规则初始化
$(document).ready(function() {
	
	
	
	var body = $("body");
	jQuery.validator.addMethod("birthday_num_rule", function(value, element) {
		 return this.optional(element) || /^((0[13456789]|1[0-2])(0[1-9]|[12][0-9]|3[01]))|(02(0[1-9]|1[0-9]|2[0-9]))$/.test(value);
    	},"请填写生日，例如0312");
	jQuery.validator.addMethod("last_phonenum_rule", function(value, element) {
		 return this.optional(element) || /^[0-9]{6}$/.test(value);
   		},"请填写手机号的后6位数字");
	$("#secAsk_form").validate({
		rules : {
			oldAnswer : {
				required : true,
				maxlength : function(){
								if($("#oldQue").length>0){
									var selectStr=$("#oldQue").text();
									if(selectStr.indexOf("生日")>0){
										return 4;
									}else if(selectStr.indexOf("位")>0){
										return 6;
									}
								}else{
										return 20;
									}
				},
			   birthday_num_rule :function(){
				   					if($("#oldQue").length>0){
										var selectStr=$("#oldQue").text();
										if(selectStr.indexOf("生日")>0){
											return true;
										}
									}
				},
			  last_phonenum_rule :function(){
								  if($("#oldQue").length>0){
										var selectStr=$("#oldQue").text();
										if(selectStr.indexOf("位")>0){
											return true;
										}
									}
				}
			},
			secAsk_select : {
				required : true
			},
			selfQue : {
				required : true				
			},
			newAnswer : {
				required : true,
				maxlength : function(){
								var selectStr=$("#secAsk_select").val();
								if(selectStr.indexOf("生日")>0){
									return 4;
								}else if(selectStr.indexOf("位")>0){
									return 6;
								}else{
									return 20;
								}
				},
				birthday_num_rule :function(){
										var selectStr=$("#secAsk_select").val();
										if(selectStr.indexOf("生日")>0){
											return true;
										}
				},
				last_phonenum_rule :function(){
										var selectStr=$("#secAsk_select").val();
										if(selectStr.indexOf("位")>0){
											return true;
										}
				}
			},	
			password : {
				required : true,
				password_rule : true 
			}
		},
		messages : {
			oldAnswer : {
				required : "请输入旧密保答案",
				maxlength : function(){
					if($("#oldQue").length>0){
						var selectStr=$("#oldQue").text();
						if(selectStr.indexOf("生日")>0){
							return "超过了4位，请填写生日，例如0312";
						}else if(selectStr.indexOf("位")>0){
							return "超过了6位，请填写后6位手机号";
						}
					}else{
							return "答案位数不可超过20";
						}
				}
			},
			secAsk_select : {
				required : "请输入新密保问题"
			},
			selfQue : {
				required : "请输入自定义密保问题"				
			},
			newAnswer : {
				required : "请输入新密保答案",
				maxlength : function(){
					var selectStr=$("#secAsk_select").val();
					if(selectStr.indexOf("生日")>0){
						return "超过了4位，请填写生日，例如0312";
					}else if(selectStr.indexOf("位")>0){
						return "超过了6位，请填写后6位手机号";
					}else{
						return "答案位数不可超过20";
					}
				}
			},	
			password : {
				required:"请输入密码",
				password_rule:"请按要求填写"
			}			
				
		}
	});
	
});

$(document).ready(function() {
	var body = $("body");

	$("#verify-dialog").dialog({
	      autoOpen : false,
		  bgiframe : true,
		  title : "提交失败",
		  modal : true,
		  buttons : {
		      '确定':function() {
		    		$(this).dialog('close');
		    	}
		  }
	});
	
	//清除填写的信息
	var bindClear = function(){
		var old = body.find("input[name='oldAnswer']");
		if(old[0]){
			old.val("");
		}
		body.find("select[name='secAsk_select']").val("");
		body.find("input[name='selfQue']").val("");
		body.find("input[name='newAnswer']").val("");
		body.find("input[name='password']").val("");
	};
	//提交
	var bindSave = function(){
		var oldAnswer = "";
		var old = body.find("input[name='oldAnswer']");
		if(old[0]){
			oldAnswer = old.val();
		}
		
		var newQue = body.find("select[name='secAsk_select']").val();
		if(  newQue == "others"){
			newQue =  body.find("input[name='selfQue']").val();
		}
		var newAnswer = body.find("input[name='newAnswer']").val();
		var password = toMD5Str(body.find("input[name='password']").val());
		
		var submit_url = body.find("input[name='submit_url']").val();
		var data = {
			oldAnswer : oldAnswer,
			newQue : newQue,
			newAnswer : newAnswer,
			password : password
		};
		$.ajax({
			type : "POST",
			url : submit_url,
			data : data,
			success : function(data) {
				switch(data){
					case "1" : 
						window.location.href =  body.find("input[name='askOK_url']").val();
						break;
					case "0" :
						$("#verify-dialog").find("div[name='verify-tips']")[0].firstChild.nodeValue = "服务器异常，绑定失败。请联系系统管理员。";
						$("#verify-dialog").dialog('open');
						break;
					case "-1" : 
						$("#verify-dialog").find("div[name='verify-tips']")[0].firstChild.nodeValue = "旧问题答案不匹配。请重新填写或重新获取。";
						$("#verify-dialog").dialog('open');
						break;
					case "-2" : 
						$("#verify-dialog").find("div[name='verify-tips']")[0].firstChild.nodeValue = "登录密码填写有误，请重新填写。";
						$("#verify-dialog").dialog('open');
						break;
				}
			},
			error : function(data) {
				alert("由于网络原因，修改密保问题失败。");
			}
		});
	};
	
	$("#submit_btn").click(function() {
		var validate = $("#secAsk_form").valid();
		if(!validate){
			return false;
		}
		bindSave();
	});
	$("#cancle_btn").click(function() {
		bindClear();
	});
	
	
});
$(document).ready(function() {
	var body = $("body");
	$("#verify_btn").on("click",function() {
		var validate = body.find("input[name='mobile']").valid();
		if(!validate){
			$("#verify-dialog").find("div[name='verify-tips']")[0].firstChild.nodeValue = "请先输入您要绑定的手机号。";
			$("#verify-dialog").dialog('open');
			return false;
		}
		var mobile = body.find("input[name='mobile']").val();
		//获取手机校验码
		var verifyPhone_url = body.find("input[name='verifyPhone_url']").val();
		var data = {mobile : mobile};
		$.ajax({
			type : "POST",
			url : verifyPhone_url,
			data : data,
			success : function(data) {
				body.find("input[name='vcode']").val(data);
			},
			error : function(data) {
				alert("由于网络原因，获取验证码失败。");
			}
		});
	});
});
*/