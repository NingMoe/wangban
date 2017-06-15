var account = "";// 登录用户名
var firstTimer = null, secondTimer = null; // 第一步图形验证码计时器，第二步图形验证码计时器

$(document).ready(function() {
	var step = LEx.urldata.step;
	if (step == "first") {
		$("#getpwd_form_first").show();

		// 点击下一步
		$("#next_btn").click(function() {
			getPwdSecInfo();
		});

		// 清空填写的账号信息
		$("#cancel_btn").click(function() {
			$("#getpwd_form_first input").val("");
		});

		// 验证码enter事件
		document.getElementById("checknum").onkeydown = function(event) {
			var e = event || window.event || arguments.callee.caller.arguments[0];
			if (e && e.keyCode == 13) {// enter 键
				getPwdSecInfo();
			}
		};

		// 每隔三分钟刷新一次验证码
		firstTimer = setInterval("changeVcode('first')", 180000);
	} else {
		$("#getpwd_form_second").show();

		// 初始化可供选择的密保方式
		getPwdMethodInfo();

		// 重设密码
		$("#submit_btn").click(function() {
			resetPass();
		});

		// 清空填写的密保信息
		$("#reset_btn").click(function() {
			$("#getpwd_form_second select").val("");
			$("#getpwd_form_second input").val("");
		});
	}
});

// 获取密码找回方式
function getPwdSecInfo() {
	// 验证登录用户名是否输入
	if (!validateObj($("#login_name"))) {
		return false;
	}

	// 验证校验码是否输入
	if (!validateObj($("#checknum"))) {
		return false;
	}

	var loginName = $("#login_name").val();// 登录用户名
	var verifyCode = $("#checknum").val();// 验证码
	var step = LEx.urldata.step;// 找回密码步骤

	// 传到后台数据
	var data = {
		loginName : loginName,
		verifyCode : verifyCode,
		step : step
	};

	// 校验登录用户名
	$.ajax({
		type : "POST",
		url : LEx.webPath + "app/uc/user?action=getPwdSec",
		data : data,
		dataType : "json",
		success : function(obj) {
			if (obj.state == 1) {
				clearInterval(firstTimer);
				location.href = LEx.webPath + "uc/getPwd?step=second";
			} else {
				errorDialog("系统提示", "找回密码失败：" + obj.message);
				changeVcode("first");
			}
		},
		error : function() {
			errorDialog("系统提示", "数据发送失败！");
		}
	});
}

// 根据密保方式带出不同界面
function getPwdMethodInfo() {
	// 获取密保信息
	$.ajax({
		type : "GET",
		url : LEx.webPath + "app/uc/user?action=getPwdMethod",
		success : function(data) {
			var obj = eval("(" + data + ")");
			if (obj.state == "1") {
				var data = obj.data;
				account = data.ACCOUNT;
				if (validType == "sms"&&data.PHONE) {
					$("#pwdMethod").append("<option value=\"phone\" tag=\"phoneDiv\">通过绑定手机找回</option>");
				}
				if (validType == "mail"&&data.EMAIL) {
					$("#pwdMethod").append("<option value=\"email\" tag=\"emailDiv\">通过绑定邮箱找回</option>");
				}
				if (data.ASK) {
					$("#pwdMethod").append("<option value=\"ask\" tag=\"askDiv\">通过密保问题找回</option>");
				}

				$("#pwdMethod").change(function() {
					var divtag = $(this).children("option[selected]").attr("tag");
					if($(this).children().length==1){
						divtag=$("#pwdMethod option:first").attr("tag");
					}
					$("#emailDiv,#phoneDiv,#askDiv").hide();
					$("#" + divtag).show();
					if (divtag == "emailDiv") {
						$("#ymail").html(data.EMAIL);
						bindReg("emailDiv");
						bindValid("EMAIL");
					} else if (divtag == "phoneDiv") {
						$("#yphone").html(data.PHONE);
						bindReg("phoneDiv");
						bindValid("PHONE");
					} else if (divtag = "askDiv") {
						secondTimer = window.setInterval("changeVcode('second')", 180000);
						$("#yask").html(data.ASK);
						bindReg("askDiv");
					}
				});
				// 默认选中第一个密保方式
				$("#pwdMethod option:first").trigger("change");
			} else {
				location.href = LEx.webPath + "uc/getPwd?step=first";
			}
		},
		error : function() {
			errorDialog("系统提示", "数据发送失败！");
		}
	});
}

// 重置密码
function resetPass() {

	var method = $("#pwdMethod").val();
	var divtag = $("#pwdMethod").children("option[selected]").attr("tag");
	if (!checkValidate(divtag)) {
		errorDialog("系统提示", "请填写正确的信息！");
		return false;
	}

	// 将新密码信息放到数据库
	var password = "", vcode = "", answer = "";
	if (method == "email") {
		password = $("#newPassWordEmail").val();
		vcode = $("#MailCode").val();
	} else if (method == "phone") {
		password = $("#newPassWordPhone").val();
		vcode = $("#MessageCode").val();
	} else if (method == "ask") {
		answer = $("#yanswer").val();
		password = $("#newPassWordAsk").val();
		vcode = $("#VCODE").val();
	}

	if (LEx.isNotNull(password)) {
		var data = {
			method : method,
			account : account,
			newpass : toMD5Str(password),
			vcode : vcode,
			answer : answer
		};
		
		// 重置密码
		$.ajax({
			type : "POST",
			url : LEx.webPath + "app/uc/user?action=resetPass",
			data : data,
			dataType : "json",
			success : function(obj) {
				if (obj.state == 1) {
					LEx.dialog({
						title : "系统提示",
						content : "重置密码成功",
						icon : 'succeed',
						lock : true,
						close : function() {
							// 跳转到登录页面
							location.href = LEx.webPath + "uc/login";
						}
					});
					return true;
				} else {
					errorDialog("系统提示", "重置密码失败：" + obj.message);
				}
			},
			error : function() {
				errorDialog("系统提示", "数据发送失败！");
			}
		});
	} else {
		errorDialog("系统提示", "新密码不能为空！");
	}
}

// 绑定校验函数
function bindReg(id) {
	$("[reg]").unbind("blur");
	$("#" + id + " [reg]").bind("blur", function() {
		var obj = $(this);
		validateObj(obj);
	});
}

// 绑定发送验证码事件
function bindValid(type) {
	var cid = "";
	if (type == "PHONE") {
		cid = "verify_btn_sms";
	} else {
		cid = "verify_btn_mail";
	}
	$("#" + cid).unbind("click").bind("click", function() {
		sendValid(type);
	});
}

// 发送验证码
function sendValid(type) {
	var cmd = new LEx.Command("app.uc.UserCmd");
	cmd.setParameter("account", account);
	cmd.setParameter("CHANNEL", "resetpwd");
	if (type == "PHONE") {
		cmd.setParameter("type", "sms");
	} else {
		cmd.setParameter("type", "mail");
	}
	var ret = cmd.execute("resetPassCode");
	if (ret.state == 1) {
		var cid = "";
		if (type == "PHONE") {
			cid = "verify_btn_sms";
		} else {
			cid = "verify_btn_mail";
		}
		$("#" + cid).unbind("click").addClass("opt_btn_gray").css("cursor", "default");
		waitValid(type, 60);// 重新发送验证码倒计时
		return true;
	} else {
		errorDialog("系统提示", ret.message);
		return false;
	}
}

function waitValid(type, time) {
	var cid = "";
	if (type == "PHONE") {
		cid = "verify_btn_sms";
	} else {
		cid = "verify_btn_mail";
	}

	if (time > 0) {
		time--;
		if (type == "PHONE") {
			$("#verify_text_sms").html(time + "秒后重新发送");
		} else {
			$("#verify_text_mail").html(time + "秒后重新发送");
		}

		setTimeout(function() {
			waitValid(type, time);
		}, 1000);
	} else {
		bindValid(type);
		$("#" + cid).removeClass("opt_btn_gray");
		if (type == "PHONE") {
			$("#verify_text_sms").html("免费获取短信验证码");
		} else {
			$("#verify_text_mail").html("通过邮箱获取验证码");
		}
		return;
	}
}

// 刷新第一步验证码
function changeVcode(step) {
	var url = LEx.webPath + "bsp/verifyCode?time=" + new Date().getTime();
	if (step == "first") {
		$("#verifyImgFirst").attr("src", url);
	} else {
		$("#verifyImgSecond").attr("src", url);
	}
}