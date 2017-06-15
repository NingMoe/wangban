$(document).ready(function() {
	$.ajax({
		type : "get",
		url : LEx.webPath + "app/uc/login?action=fail",
		success : function(data) {
			var obj = eval('(' + data + ')');
			showVerifyCode(obj);
		},
		error : function() {
			$("#verify_code_div").hide();
		}
	});

	// enter事件
	if(document.getElementById('password')){
		document.getElementById('password').onkeydown = function(event) {
			var e = event || window.event || arguments.callee.caller.arguments[0];
			if (e && e.keyCode == 13) {
				if ($("#verify_code_div").css("display") == "none") {
					okSubmit();
				} else if ($("#verify_code_div").css("display") == "block") {
					document.getElementById('verify').focus();
				}
			}
		};
	}

	// enter事件
	if(document.getElementById('verify')){
		document.getElementById('verify').onkeydown = function(event) {
			var e = event || window.event || arguments.callee.caller.arguments[0];
			if (e && e.keyCode == 13) { // enter 键
				okSubmit();
			}
		};
	}

	// 设置上次的登录用户名
	var account = LEx.cookie.get("account");
	if (LEx.isNotNull(account)) {
		$("#account").val(account);
		$("#remember_me").attr("checked", "checked");
	}

	$("[reg]").change(function() {
		var obj = $(this);
		validate(obj);
	});

	$("#remember_me").click(function() {
		if ($(this).is(":checked")) {
			LEx.cookie.set("account", $("#account").val());
		} else {
			LEx.cookie.set("account", "");
		}
	});

	//initPlaceHolders();// 显示提示内容
});

function geturldata(){
 	var url = location.href;
 	var data = url.split("?")[1];
 	if(!data){
 		return {};
 	}
 	data = data.split("&");
 	var datastr = "{";
 	for(var i=0;i<data.length;i++){
 		var temp = data[i].split("=");
 		if(!temp[1]){continue;}
 		datastr += '"'+temp[0]+'":"'+temp[1].replace(/#$/,'').replace(new RegExp("\"", "g"),"\\\"")+'",';
 	}
 	if(datastr.length>2){
 		datastr = datastr.substring(0,datastr.length-1);
 	}
 	datastr += '}';
 	return eval('('+datastr+')');
}
function gotoRegister(){
	window.location.href=LEx.webPath+"uc/register?goto="+encodeURIComponent(window.location.href);
}
function changeVerify() {
	var url = LEx.webPath + "bsp/verifyCode?time=" + new Date().getTime();
	$("#verifyImg").attr("src", url);
}

function showVerifyCode(obj) {
	if (obj && obj.data && obj.data.login_fail_time) {
		var loginFailTime = parseInt(obj.data.login_fail_time);
		if (loginFailTime > 3) {
			$("#verify_code_div").show();
			changeVerify();
			setTimeout("changeVerify()", 180000);
		} else {
			$("#verify_code_div").hide();
		}
	}
}

// 提交ajax登录请求
var ajaxInit = {
	success : function(data) {
		var obj = eval('(' + data + ')');
		if (obj.state == 0) {
			errorDialog("系统提示", obj.message);
			$(".btn-smb").removeClass("btn-smb-ing").removeAttr("disabled");
			$("#password").val("");
			var type = $(".form-tab li.on").attr("tag");// 登录类型
			if (type == "yhdl") {
				showVerifyCode(obj);
			}
		} else {
			var nextGo = decodeURIComponent(obj.data.nextGo);
			location.href = nextGo;
		}
	},
	error : function(e) {
		$(".btn-smb").removeClass("btn-smb-ing").removeAttr("disabled");
	}
};

// 提交登录信息后
function doSumit(formId){
	
	var formData = LEx.form.get(formId);
	$.ajax({
		type : "post",
		url : LEx.webPath + "app/uc/login?action=login",
		data:formData,  
		success : function(data) {
			var obj = eval('(' + data + ')');
			if (obj.state == 1) { 
				var tickId = obj.data.tickId;
				var nextGo = obj.data.nextGo;
				location.href = nextGo;
			} else {
				errorDialog("系统提示", obj.message);
				$(".btn-smb").removeClass("btn-smb-ing").removeAttr("disabled");
				$("#password").val("");
				var type = $(".form-tab li.on").attr("tag");// 登录类型
				if (type == "yhdl") {
					showVerifyCode(obj);
				}
			}
		},
		error : function(data) {
			$(".btn-smb").removeClass("btn-smb-ing").removeAttr("disabled");
		}
	});
	
}
function grSubmit() {
		$("[reg]").each(function() {
			validate($(this));
		});

		if ($("#account").hasClass("input_validation-failed")) {
			errorDialog("系统提示", "请输入登录用户名！");
			$("#account").focus();
			return false;
		}

		if ($("#password").hasClass("input_validation-failed")) {
			errorDialog("系统提示", "请输入登录密码！");
			$("#password").focus();
			return false;
		}

		// 其他错误
		$(".input_validation-failed").each(function() {
			errorDialog("系统提示", $(this).attr("tips"));
			return false;
		});

		if ($(".input_validation-failed").length > 0) {
			return false;
		}

		// 验证通过，调action登录
		var password = $("#password").val();
		$("#pwd").val(toMD5Str(password));
		$("#verify_code").val($("#verify").val());
		$(".btn-smb").addClass("btn-smb-ing").attr("disabled", "disabled");
		doSumit("grform");
	
}
function okSubmit() {
	var type = $(".form-tab li.on").attr("tag");// 登录类型
		// 表单验证
		if ($("#remember_me").is(":checked")) {
			LEx.cookie.set("account", $("#account").val());
		} else {
			LEx.cookie.set("account", "");
		}
		// 表单验证
		if (!$("#verify_code_div").is(":hidden")) {
			var verify_code = $("#verify").val();
			if (verify_code == null || verify_code.length != 4) {
				errorDialog("系统提示", "请输入4位校验码！");
				return false;
			}
		}

		$("[reg]").each(function() {
			validate($(this));
		});

		if ($("#account").hasClass("input_validation-failed")) {
			errorDialog("系统提示", "请输入登录用户名！");
			$("#account").focus();
			return false;
		}

		if ($("#password").hasClass("input_validation-failed")) {
			errorDialog("系统提示", "请输入登录密码！");
			$("#password").focus();
			return false;
		}

		// 其他错误
			$(".input_validation-failed").each(function() {
				if($(this).parent().attr("id")=="yhLoginForm"){
					errorDialog("系统提示",$(this).attr("tips"));
					return false;
				}
			});
			if($(".input_validation-failed").length > 0) {
				if($(this).parent().attr("id")=="yhLoginForm"){
				    return false;
				}
		    }
		// 验证通过，调action登录
		var password = $("#password").val();
		$("#pwd").val(toMD5Str(password));
		$("#verify_code").val($("#verify").val());
		$(".btn-smb").addClass("btn-smb-ing").attr("disabled", "disabled");
		doSumit("yhLoginForm");
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
