$(document).ready(function() {

	//回调失败的信息
	var callbackMessage = LEx.urldata.callbackMessage;
	if(LEx.isNotNull(callbackMessage)){
		callbackMessage = decodeURIComponent(callbackMessage);
		errorDialog("系统提示",callbackMessage);
	}
	// 切换登陆方式
	var caAuth = $("#caAuth").val();
	if (caAuth == "1") {
		$(".form-tab li").click(function() {
			$(".form-tab li.on").removeClass("on");
			$(this).addClass("on");
			$(".form-body .tab").hide();
			$("#" + $(this).attr("tag")).show();
		});
	}

	var uuid = LEx.urldata.uuid;
	if(LEx.isNotNull(uuid)){
		$(".ps_info").show();
		$("#sso_dl").hide();
	}

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

	// enter事件
	document.getElementById('verify').onkeydown = function(event) {
		var e = event || window.event || arguments.callee.caller.arguments[0];
		if (e && e.keyCode == 13) { // enter 键
			okSubmit();
		}
	};

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

	initPlaceHolders();// 显示提示内容
});

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
			var type = $(".form-tab li.on").attr("tag");// 登陆类型
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

// 提交登陆信息后
var ajaxAfter = {
	success : function(data) {
		var obj = eval('(' + data + ')');
		if (obj.state == 1) {
			var nextGo = obj.data.nextGo;
			location.href = nextGo;
		} else {
			errorDialog("系统提示", obj.message);
			$(".btn-smb").removeClass("btn-smb-ing").removeAttr("disabled");
			$("#password").val("");
			var type = $(".form-tab li.on").attr("tag");// 登陆类型
			if (type == "yhdl") {
				showVerifyCode(obj);
			}
		}
	},
	error : function(data) {
		$(".btn-smb").removeClass("btn-smb-ing").removeAttr("disabled");
	}
};

function okSubmit() {
	var type = $(".form-tab li.on").attr("tag");// 登陆类型
	if (type == "cadl") {
		// 获取服务器验证信息
		$.ajax({
			type : "get",
			url : LEx.webPath + "app/uc/user?action=getUkey",
			success : function(data) {
				var obj = eval('(' + data + ')');
				// ukey方式验证时从后台获取签名，随机数，证书
				var strServerSignedData = null;
				var strServerRan = null;
				var strServerCert = null;
				var strContainerName = $("#userList").val();
				if (obj.state == 1) {
					strServerSignedData = obj.data.strSignedData;
					strServerRan = obj.data.strRandom;
					strServerCert = obj.data.strServerCert;
					$("#UserSignedData").val(strServerSignedData);
					$("#UserCert").val(strServerRan);
					$("#ContainerName").val(strContainerName);
				} else {
					errorDialog("系统提示", obj.message);
					return false;
				}

				// 调用js验证，验证通过后跳到后台action
				if (SZG_Login(caLoginForm, strContainerName, strServerRan)) {
					$(".btn-smb").addClass("btn-smb-ing").attr("disabled", "disabled");
					$("#caLoginForm").ajaxSubmit(ajaxAfter);
				} else {
					return false;
				}
			},
			error : function(data) {
				errorDialog("系统提示", "未获取到CA服务器验证信息！");
			}
		});
	} else {
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
			errorDialog("系统提示", "请输入登陆用户名！");
			$("#account").focus();
			return false;
		}

		if ($("#password").hasClass("input_validation-failed")) {
			errorDialog("系统提示", "请输入登陆密码！");
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

		// 验证通过，调action登陆
		var password = $("#password").val();
		$("#pwd").val(toMD5Str(password));
		$("#verify_code").val($("#verify").val());
		$(".btn-smb").addClass("btn-smb-ing").attr("disabled", "disabled");
		$("#yhLoginForm").ajaxSubmit(ajaxAfter);
	}
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

function gotoRegister() {
	if (typeof signupUrl == "undefined")
		return;
	var uuid = LEx.urldata.uuid;
	if(LEx.isNotNull(uuid)){
		signupUrl += "?uuid="+uuid + "&goto="+encodeURIComponent(LEx.urldata.goto);
	}
	location.href = signupUrl;
}

function errorDialog(t, c) {
	LEx.dialog({
		title : t,
		content : c,
		icon : 'error',
		lock : true
	});
}

// 读出CA证书列表
function setCaList(strListID) {
	var i;
	var strOption;
	var strName;
	var strUniqueID;
	$("#" + strListID).html("");
	var strTemp = SZG_GetUserList();
	while (true) {
		i = strTemp.indexOf("&&&");
		if (i <= 0) {
			break;
		}
		strOption = strTemp.substring(0, i);
		strName = strOption.substring(0, strOption.indexOf("||"));
		strUniqueID = strOption.substring(strOption.indexOf("||") + 2, strOption.length);
		//获取用户签名证书
		var ucert=SZG_ExportUserCert(strUniqueID);
		//获取证书唯一标识，证书绑定环节使用，可在插入证书后得到用户唯一标识同系统用户绑定
		var oid=SZG_GetUserInfoByOid(ucert,"2.16.156.112548");
		//判断证书中有无证书唯一标识，没有的进行过滤
		if(oid!=""){
			var objItem = "<option value=" + strUniqueID + ">" + strName + "</option>";
			$("#" + strListID).append(objItem);
		}
		len = strTemp.length;
		strTemp = strTemp.substring(i + 3, len);
	}
}

function ssoLogin(){
	var url = window.location.href;
	if(url.indexOf("http://")==0){
	        url = url.replace("http://","");
	        url = url.substr(0,url.indexOf("/"));        
	}else{
	        url = url.substr(0,url.indexOf("/"));
	}
	url = "http://"+url;
	var callback_url = url+LEx.webPath+"app/uc/login?action=callback";

	var loginUrl = $("#wsbsDomain").val();
	
	loginUrl = loginUrl + "uc/login?callback="+encodeURIComponent(callback_url);

	var gotoUrl = LEx.urldata.goto;
	if(LEx.isNotNull(gotoUrl)){
		loginUrl += "&goto=" + encodeURIComponent(gotoUrl);
	}
	window.location.href=loginUrl;
}

