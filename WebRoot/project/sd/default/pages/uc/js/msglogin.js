$(document).ready(function() {
	
	LEx.form.init();
	initPlaceHolders();// 显示提示内容
	bindValid("register_form_per", "PHONE");
});

function okSubmit() {
	
		$("[reg]").each(function() {
			validate($(this));
		});

		// 其他错误
		$(".input_validation-failed").each(function() {
			errorDialog("系统提示", $(this).attr("tips"));
			return false;
		});

		if ($(".input_validation-failed").length > 0) {
			return false;
		}

		// 验证通过，调action登录
		doSumit("register_form_per");
}

//提交登录信息后
function doSumit(formId){
	var formData = LEx.form.get(formId,"tag");
	
	$.ajax({
		type : "post",
		url : LEx.webPath + "app/uc/msglogin?action=login",
		data:formData,  
		success : function(data) {
			var obj = eval('(' + data + ')');
			if (obj.state == 1) { 
				location.href = LEx.webPath;
			} else {
				errorDialog("系统提示", obj.message);
			}
		},
		error : function(data) {
		}
	});
	
}

function initPlaceHolders() {
	if ('placeholder' in document.createElement('input')) { // 如果浏览器原生支持placeholder
		return;
	}
	var target = function(e) {
		var e = e || window.event;
		return e.target || e.srcElement;
	};
	var _getEmptyHintEl = function(el) {
		var hintEl = el.hintEl;
		return hintEl && g(hintEl);
	};
	var blurFn = function(e) {
		var el = target(e);
		if (!el || el.tagName != 'INPUT' && el.tagName != 'TEXTAREA')
			return;// IE下，onfocusin会在div等元素触发
		var emptyHintEl = el.__emptyHintEl;
		if (emptyHintEl) {
			if (el.value)
				emptyHintEl.style.display = 'none';
			else
				emptyHintEl.style.display = '';
		}
	};
	var focusFn = function(e) {
		var el = target(e);
		if (!el || el.tagName != 'INPUT' && el.tagName != 'TEXTAREA')
			return;// IE下，onfocusin会在div等元素触发
		var emptyHintEl = el.__emptyHintEl;
		if (emptyHintEl) {
			emptyHintEl.style.display = 'none';
		}
	};
	if (document.addEventListener) {// ie
		document.addEventListener('focus', focusFn, true);
		document.addEventListener('blur', blurFn, true);
	} else {
		document.attachEvent('onfocusin', focusFn);
		document.attachEvent('onfocusout', blurFn);
	}

	var elss = [ document.getElementsByTagName('input'), document.getElementsByTagName('textarea') ];
	for (var n = 0; n < 2; n++) {
		var els = elss[n];
		for (var i = 0; i < els.length; i++) {
			var el = els[i];
			var placeholder = el.getAttribute('placeholder'), emptyHintEl = el.__emptyHintEl;
			if (placeholder && !emptyHintEl) {
				emptyHintEl = document.createElement('span');
				emptyHintEl.innerHTML = placeholder;
				emptyHintEl.className = 'emptyhint';
				emptyHintEl.onclick = function(el) {
					return function() {
						try {
							el.focus();
						} catch (ex) {
						}
					}
				}(el);
				if (el.value)
					emptyHintEl.style.display = 'none';
				el.parentNode.insertBefore(emptyHintEl, el);
				el.__emptyHintEl = emptyHintEl;
			}
		}
	}
}

function validate(obj) {
	var value = obj.val();
	var reg = new RegExp(obj.attr("reg"), "g");
	if (reg.test(value)) {
		obj.removeClass("input_validation-failed").removeAttr("title");
	} else {
		obj.addClass("input_validation-failed").attr("title", obj.attr("tips"));
	}
}

function errorDialog(t, c) {
	LEx.dialog({
		title : t,
		content : c,
		icon : 'error',
		lock : true
	});
}


//发送验证码后倒计时，为PHONE绑定发送短信，为EMAIL绑定发送邮件
function waitValid(id, type, time) {
	if (time > 0) {
		time--;
		$("#" + id + " [name=verify_btn_text]").html(time + "秒后重新发送");
		setTimeout(function() {
			waitValid(id, type, time);
		}, 1000);
	} else {
		bindValid(id, type);// 重新绑定发送短信验证码事件
		$("#" + id + " [name=verify_btn]").attr("disabled",false);
		if (type == "PHONE") {
			$("#" + id + " [name=verify_btn_text]").html("获取短信验证码");
		} else {
			$("#" + id + " [name=verify_btn_text]").html("通过邮箱获取验证码");
		}
		return;
	}
}

//绑定验证码发送功能，为PHONE绑定发送短信
function bindValid(id, type) {
	$("#" + id + " [name=verify_btn]").unbind("click").bind("click", function() {
		var receive = $("#" + id + " [name=" + type + "]").val();
		if (LEx.isNotNull(receive)) {
			if ($("#" + id + " [name=" + type + "]").hasClass("input_validation-failed")) {
				if (type == "PHONE") {
					errorDialog("系统提示", "手机号未通过系统校验");
				} else {
					errorDialog("系统提示", "邮箱地址未通过系统校验");
				}
			} else {
				if (type == "PHONE") {
					if(checkPhone(receive)){
						sendValid(id, "PHONE", receive);// 手机号码通过校验时才发送短信
					}
				} else {
					sendValid(id, "EMAIL", receive);// 邮箱地址通过校验时才发送邮件
				}
			}
		} else {
			if (type == "PHONE") {
				errorDialog("系统提示", "请输入手机号码");
			} else {
				errorDialog("系统提示", "请输入邮箱地址");
			}
			return false;
		}
	});
}

function check_phone(o){
	checkPhone($(o).val());
}

function checkPhone(phone){
	var cmd = new LEx.Command("app.uc.UserCmd");
	cmd.setParameter("phone",phone);
	var ret = cmd.execute("getList");
	if(ret.state == '1'){
		if(ret.total>0){
			if(ret.data[0].IS_PHONEBIND == "0"){
				errorDialog("系统提示", "手机号码尚未绑定，请前往用户中心进行绑定！");
				return false;
			}else{
				return true;
			}
		}else{
			errorDialog("系统提示", "该手机号码未注册！");
			return false;
		}
	}else{
		errorDialog("系统提示", "检验手机号码失败，请稍后再试！");
		return false;
	}
}

//发送验证码
function sendValid(id, type, receive) {
	var cmd = new LEx.Command("app.uc.UserCmd");
	var ret = null;
	if (type == "PHONE") {
		
		cmd.setParameter("PHONE", receive);
		cmd.setParameter("MESSAGE", "您此次登录政务服务网的短信验证码为：");// 对应security.properties中主键
		ret = cmd.execute("sendMessage4Login");
	} else {
		cmd.setParameter("mail", receive);
		ret = cmd.execute("registerValidEmail");
	}
	if (ret.state == 1) {
		$("#" + id + " [name=verify_btn]").unbind("click").attr("disabled","disabled").css("cursor", "default");
		waitValid(id, type, 60);// 重新发送短信倒数计时
		return true;
	} else {
		errorDialog("系统提示", ret.message);
		return false;
	}
}
