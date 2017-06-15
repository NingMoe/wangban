//var type = "11"; // 聊城用户注册方式,11表示个人用户密码注册，12表示个人ukey注册，21表示单位用户密码注册，22表示单位ukey注册
var code="";
function readOnly(name, value) {
	$("[name=" + name + "]").attr("readonly", "readonly").addClass("readonly").removeAttr("reg").unbind("blur");
	if (LEx.isNotNull(value)) {
		$("[name=" + name + "]").val(value);
	}
}

function initCodeMap() {
	var option = "";
	for ( var o in CODE_MAP_SOURCE) {
		var obj = CODE_MAP_SOURCE[o];
		if (obj.DICT_CODE == "user_sfzjlx") {
			option += '<option value="' + obj.ITEM_CODE + '" title="' + obj.ITEM_VALUE + '">' + obj.ITEM_VALUE + '</option>';
		}
	}

	$("#register_form_per #CARD_TYPE,#register_form_org #CARD_TYPE,#ORG_BOSS_TYPE").html(option);
	$("#register_form_per #CARD_TYPE,#register_form_org #CARD_TYPE,#ORG_BOSS_TYPE").val("10");
	$("#register_form_per #CARD_TYPE,#register_form_org #CARD_TYPE,#ORG_BOSS_TYPE").hide();
}

$(document).ready(function() {
	// 初始化选项
	/*initCodeMap();*/
	$(function(){
		$('#PASSWORD').passwordStrength();
	});
	// 注册
	$("#btn-submit_per").click(function() {
		doRegister("register_form_per");
	});

	$("#btn-submit_org").click(function() {
		doRegister("register_form_org");
	});


	$(".form-type input").click(function() {
		$(".form-type input.btn-org-hover").removeClass("btn-org-hover");
		$(this).addClass("btn-org-hover");
		$(".form_contain_DIV").hide();
		$(".form_contain_DIV").removeClass("curr_contain");
		var tag = $(this).attr("tag");
		$("#" + tag).addClass("curr_contain").show();
		if (tag == "form_per_contain") {
				type = "11";// 个人用户密码注册方式
		} else {
				type = "21";// 单位用户密码注册方式
		}
		$("#" + tag + " [name=TYPE]").val(type);
	});
	
	//聊城 法人注册 时选择企业法人和非企业法人时改变type
	$("#TYPE").change(function() {
		type = $("#TYPE").val();
		var formID=$(this).attr("name");
		//add by chenyushu
		if(type=="21"){//代表企业
		
			$(".qiye").show();
			$(".jigou").hide();
		
		}else if(type=="31"){//代表机构
			
			$(".qiye").hide();
			$(".jigou").show();
		}
	
		//end by chenyushu
		
	});
	
	// 检验普通项的数据
	$("[reg]").bind("blur", function() {
		var c = $(".form-contain .curr_contain");
		var t = $(".form-tip span[for=" + $(this).attr("id") + "]", c);
		var isRequired = true;//是否必填  不是必填的字段，可以为空
		var name = $(this).attr("name");
		if(name=="ADDRESS"|name=="EMAIL"){
			if($.trim($(this).val())==""){
				isRequired = false;
			}
		}
		if(name=="PHONE"){
			var p_flag = $("#isRequired_phone").html();
			if(p_flag==undefined){
				isRequired = false;
			}
		}
		if (isRequired == true&&!validate($(this))) {
			t.html($(this).attr("tip")).attr("class", "invalid");
			$(this).attr("style","border-color:red;")
		} else {
			var func = $(this).attr("func");
			if(isRequired == false){//非必填项为空时不检查
				t.html("").attr("class", "valid");
				$(this).attr("style","border-color:#cccccc;")
			}else if (func == undefined) {
				t.html("").attr("class", "valid");
				$(this).attr("style","border-color:#cccccc;")
			} else {
				try {
					var s = eval(func);
					re = s.call(s, $(this));
					if (re) {
						t.html("").attr("class", "valid");
						$(this).attr("style","border-color:#cccccc;")
					}
				} catch (e) {
					alert("请求的验证函数错误。");
				}
			}
		}
	});
	// 绑定短信，邮箱发送事件
	var validType = $("#validType").val();
	if (validType == "sms") {
		bindValid("register_form_per", "PHONE");
		bindValid("register_form_org", "PHONE");
	} else if (validType == "mail") {
		bindValid("register_form_per", "EMAIL");
		bindValid("register_form_org", "EMAIL");
	}
	
	//打开页面显示对应标签页
	if(LEx.isNotNull(LEx.urldata.tag)){
		$("#btn-"+LEx.urldata.tag).click();
	}
	
	$("#btn-clear").click(function(){
	    $("#ACCOUNT").val("");
	    $("#ACCOUNT").attr("style","border-color:#cccccc;");
	    $("span[for='ACCOUNT']").html("");
	    
	    $("#PASSWORD").val("");
	    $("#PASSWORD").attr("style","border-color:#cccccc;");
	    $("span[for='PASSWORD']").html("");
	    
	    $("#CONFIRM_PASSWORD").val("");
	    $("#CONFIRM_PASSWORD").attr("style","border-color:#cccccc;");
	    $("span[for='CONFIRM_PASSWORD']").html("");
	    
	    $("#ORG_NO").val("");
	    $("#ORG_NO").attr("style","border-color:#cccccc;");
	    $("span[for='ORG_NO']").html("");
	    
	    $("#ORG_NAME").val("");
	    $("#ORG_NAME").attr("style","border-color:#cccccc;");
	    $("span[for='ORG_NAME']").html("");
	    
	    $("#ORG_BOSS_NAME").val("");
	    $("#ORG_BOSS_NAME").attr("style","border-color:#cccccc;");
	    $("span[for='ORG_BOSS_NAME']").html("");
	    
	    $("#ORG_BOSS_NO").val("");
	    $("#ORG_BOSS_NO").attr("style","border-color:#cccccc;");
	    $("span[for='ORG_BOSS_NO']").html("");
	    
	    $("#CARD_NAME").val("");
	    $("#CARD_NAME").attr("style","border-color:#cccccc;");
	    $("span[for='CARD_NAME']").html("");
	    
	    $("#CARD_NO").val("");
	    $("#CARD_NO").attr("style","border-color:#cccccc;");
	    $("span[for='CARD_NO']").html("");
	    
	    $("#ADDRESS").val("");
	    $("#ADDRESS").attr("style","border-color:#cccccc;");
	    $("span[for='ADDRESS']").html("");
	    
	    $("#EMAIL").val("");
	    $("#EMAIL").attr("style","border-color:#cccccc;");
	    $("span[for='EMAIL']").html("");
	    
	    $("#PHONE").val("");
	    $("#PHONE").attr("style","border-color:#cccccc;");
	    $("span[for='PHONE']").html("");
	    
	    $("#VCODE").val("");
	});
});

// 绑定验证码发送功能，为PHONE绑定发送短信，为EMAIL绑定发送邮件
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

function checkPhone(phone){
	var cmd = new LEx.Command("app.uc.UserCmd");
	cmd.setParameter("phone",phone);
	var ret = cmd.execute("getList");
	if(ret.state == '1'){
		if(ret.total>0){
			errorDialog("系统提示", "该手机已注册！");
			return false;
		}else{
			return true;
		}
	}else{
		errorDialog("系统提示", "检验手机号码失败，请稍后再试！");
		return false;
	}
}

function checkCARD(card_no){
	var cmd = new LEx.Command("app.uc.UserCmd");
	cmd.setParameter("CARD_NO",card_no);
	var ret = cmd.execute("getList");
	if(ret.state == '1'){
		if(ret.total>0){
			return false;
		}else{
			return true;
		}
	}else{
		return false;
	}
}

// 发送验证码后倒计时，为PHONE绑定发送短信，为EMAIL绑定发送邮件
function waitValid(id, type, time) {
	if (time > 0) {
		time--;
		$("#" + id + " [name=verify_btn_text]").html(time + "秒后可以重新发送");
		setTimeout(function() {
			waitValid(id, type, time);
		}, 1000);
	} else {
		bindValid(id, type);// 重新绑定发送短信验证码事件
		$("#" + id + " [name=verify_btn]").removeClass("opt_btn_gray");
		if (type == "PHONE") {
			$("#" + id + " [name=verify_btn_text]").html("点击获取短信验证码");
		} else {
			$("#" + id + " [name=verify_btn_text]").html("通过邮箱获取验证码");
		}
		return;
	}
}

// 发送验证码
function sendValid(id, type, receive) {
	var cmd = new LEx.Command("app.uc.UserCmd");
	var ret = null;
	if (type == "PHONE") {
		
		cmd.setParameter("PHONE", receive);
		cmd.setParameter("SMS", "RegisterSms");// 对应security.properties中主键
		ret = cmd.execute("sendMessage");
		code=ret.route;
		
	} else {
		cmd.setParameter("mail", receive);
		ret = cmd.execute("registerValidEmail");
	}
	if (ret.state == 1) {
		$("#" + id + " [name=verify_btn]").unbind("click").addClass("opt_btn_gray").css("cursor", "default");
		waitValid(id, type, 60);// 重新发送短信倒数计时
		return true;
	} else {
		errorDialog("系统提示", ret.message);
		return false;
	}
}

function check_confirm_password(obj) {
	var c = $(".form-contain .curr_contain");
	var t = $(".form-tip span[for=" + obj.attr("id") + "]", c);
	if (obj.val() != $("#PASSWORD", c).val()) {
		t.html("两次输入的密码不一致");
		t.attr("class", "invalid");
		$(obj).parent().find("input").attr("style","border-color:red;")
		return false;
	}
	return true;
}

function check_Exists(obj) {
	var c = $(".form-contain .curr_contain");
	var t = $(".form-tip span[for=" + obj.attr("id") + "]", c);
	if (checkExists(obj.attr("tag"), obj.val())) {
		t.html("该信息已注册");
		t.attr("class", "invalid");
		$(obj).parent().find("input").attr("style","border-color:red;")
		return false;
	}
	return true;
}

// 身份证件类型正则表达式
var zjlxReg = {
	10 : "(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}[X|x]$)",
	14 : "^(H|h|M|m)\\d{8}\\d{2}$",
	16 : "^[a-zA-Z]{1,2}\\d{6}\\(\\d{1}\\)$",
	17 : "^\\d{7}\\(\\d\\)$",
	18 : "^[a-zA-Z]\\d{9}$"
}

//同时检测身份证存在和身份证格式
function check_card(obj){
	var c1 = $(".form-contain .curr_contain");
	var t1 = $(".form-tip span[for=" + obj.attr("id") + "]", c1);
	if (checkExists(obj.attr("tag"), obj.val())) {
		t1.html("该信息已注册");
		t1.attr("class", "invalid");
		$(obj).parent().find("input").attr("style","border-color:red;")
		return false;
	}
	var c = $(".form-contain .curr_contain");
	var t = $(".form-tip span[for=" + obj.attr("id") +"]",c);
	var reg;
	var f = obj.attr("for");
	var zjlx = $("#" + f,c).val();
	if(zjlx == 10){
		if(!cidInfo(obj.val())){
			t.attr("class", "invalid").html("证件输入不合法");
			$(obj).parent().find("input").attr("style","border-color:red;")
			return false;
		}
		return true;
	}
	
	// 如果存在正则表达式，必须符合格式，否则可以随意填
	if (zjlxReg[zjlx]) {
		reg = new RegExp(zjlxReg[zjlx]);
		if (reg.test(obj.val()) === false) {
			t.attr("class", "invalid").html("证件输入不合法");
			$(obj).parent().find("input").attr("style","border-color:red;")
			return false;
		} else {
			t.attr("class", "valid").html("");
			return true;
		}
	} else {
		t.attr("class", "valid").html("");
		return true;
	}
}

function checkCardNo(obj) {
	var c = $(".form-contain .curr_contain");
	var t = $(".form-tip span[for=" + obj.attr("id") + "]", c);
	var reg;
	var f = obj.attr("for");
	var zjlx = $("#" + f, c).val();
	if(zjlx==10){
		if(!cidInfo(obj.val())){
			t.attr("class", "invalid").html("证件输入不合法");
			$(obj).parent().find("input").attr("style","border-color:red;")
			return false;
		}
		return true;
	}

	// 如果存在正则表达式，必须符合格式，否则可以随意填
	if (zjlxReg[zjlx]) {
		reg = new RegExp(zjlxReg[zjlx]);
		if (reg.test(obj.val()) === false) {
			t.attr("class", "invalid").html("证件输入不合法");
			$(obj).parent().find("input").attr("style","border-color:red;")
			return false;
		} else {
			t.attr("class", "valid").html("");
			return true;
		}
	} else {
		t.attr("class", "valid").html("");
		return true;
	}
}

function checkExists(field, value) {
	if(field=="ORG_NO"){
		return true;
	}
	var cmd = new LEx.Command("app.uc.UserCmd");
	cmd.setParameter(field, value);
	ret = cmd.execute("getList");
	if (ret.state == 1) {
		if (ret.data.length > 0) {
			return true;
		} else {
			return false;
		}
	} else {
		errorDialog("系统提示", "调用服务出错：" + ret.message);
	}
	return true;
}

function checkValidate() {
	var c = $(".form-contain .curr_contain");
	var f = true;
	$("[reg]", c).each(function() {
		var t = $(".form-tip span[for=" + $(this).attr("id") + "]", c);
		var isRequired = true;//是否必填  不是必填的字段，可以为空
		var name = $(this).attr("name");
		if(name=="ADDRESS"|name=="EMAIL"){
			if($.trim($(this).val())==""){
				isRequired = false;
			}
		}
		if(name=="PHONE"){
			var p_flag = $("#isRequired_phone").html();
			if(p_flag==undefined){
				isRequired = false;
			}
		}
		if (isRequired==true&&!validate($(this))) {
			t.html($(this).attr("tip"));
			t.attr("class", "invalid");
			f = false;
		} else {
			var func = $(this).attr("func");
			if (isRequired==false||func == undefined) {
				t.html("");
				t.attr("class", "valid");
			} else {
				try {
					var s = eval(func);
					var re = s.call(s, $(this));
					if (!re) {
						f = false;
					}
				} catch (e) {
					alert("请求的验证函数错误。");
				}
			}
		}
		
	});
	
	return f;
}

function doRegister(id){
	if (!checkValidate()) {
		
		errorDialog("系统提示", "请填写正确的信息！");
		return false;
	}else{
		/*if(code!=$("#VCODE").val()){
			
			alert("验证码输入有误！");
			return false;
		}*/
	}

	if(type=="11"){
	
		var card = $("#CARD_NO").val();
		if(!checkCARD(card)){
			errorDialog("系统提示", "该证件号已经注册。");
			return false;
		}

	}
		register(id);
}

function register(id) {
	var pass = $("#DES_password").val();
	var obj = LEx.form.get(id);
	//add by cys，for:添加用户名密码，过滤条件
	if(obj.ACCOUNT==""){
		alert("用户名不为空");
		$("#ACCOUNT").focus();
		return false;
	}
	if(obj.PASSWORD==""){	
		alert("密码不为空");
		$("#PASSWORD").focus();
		return false;
	}
	if(obj.CONFIRM_PASSWORD==""){
		
		alert("确认密码不为空");
		$("#CONFIRM_PASSWORD").focus();
		return false;
	}
	if(obj.PASSWORD.length<6){
	    alert("密码至少大于等于6位"); 
	    $("#PASSWORD").focus();
	    return false;
	}
	var reg = /^[0-9a-zA-Z]+$/;
	if(!reg.test(obj.PASSWORD)){
	    alert("密码只能由数字和字母组成");
	    $("#PASSWORD").focus();
	    return false;

	}
	if(obj.CONFIRM_PASSWORD!=obj.PASSWORD){
		alert("两次输入密码不一致");
		  $("#CONFIRM_PASSWORD").focus();
		return false;
	
	}
	obj.PASSWORD = toMD5Str(obj.PASSWORD);
	//md5加密的基础上des加密
	obj.PASSWORD_DES = strEnc(obj.PASSWORD,pass,"","");
	//des解密方式     var b = strDec(obj.PASSWORD_DES,"inspur","","");
	obj.CONFIRM_PASSWORD = toMD5Str(obj.CONFIRM_PASSWORD);
	obj.PHONE_DES = strEnc(obj.PHONE,pass,"","");
	if (type == "21" || type == "22" || type == "31") {
		var card_name = $("#CARD_NAME").val();
		if (LEx.isNotNull(card_name)) {
			obj.NAME = card_name;
		}
	}
	// ukey方式
	if(type == "12" || type == "22"){
		obj.UserSignedData = $("#UserSignedData").val();
		obj.RANDOM = $("#RANDOM").val();
		obj.UserCert = $("#UserCert").val();
		obj.ContainerName = $("#ContainerName").val();
	}
	obj.ACCOUNT_DES = strEnc(obj.ACCOUNT,pass,"","");
	// 通过data传递数据
	var data = {
		ACCOUNT : "",
		ACCOUNT_DES : "",
		NAME : "",
		PHONE : "",
		PHONE_DES : "",
		EMAIL : "",
		PASSWORD : "",
		PASSWORD_DES : "",
		TYPE : "",
		STATUS : "",
		CARD_TYPE : "",
		CARD_NO : "",
		ORG_NAME : "",
		ORG_NO : "",
		ORG_BOSS_TYPE : "",
		ORG_BOSS_NO : "",
		ORG_BOSS_NAME : "",
		PARENT_ID : "",
		ContainerName : "",
		UserCert : "",
		UserSignedData : "",
		RANDOM : "",
		ADDRESS : ""
	};
	$.extend(data, obj);
	// 将注册成功后需跳转url传到后台
	if (validType == "mail") {
		var url = window.location.href;
		if (url.indexOf("http://") == 0) {
			url = url.replace("http://", "");
		}
		url = url.substr(0, url.indexOf("/"));
		data.url = "http://" + url + LEx.webPath;
	}

	// 正式请求服务
	$.ajax({
		type : "POST",
		url : LEx.webPath + "app/uc/user?action=register",
		data : data,
		dataType : "json",
		success : function(obj) {
			if (obj.state == 1) {
				LEx.dialog({
					title : "系统提示",
					content : "注册成功，<span id='totalSecond'>10</span>秒后将自动跳转",
					icon : 'succeed',
					lock : true,
					time: 10,
					close : function() {
						loginAndRedirect(data,obj);
					}
				});

				var secs = 10;   
			          for(var i=secs;i>=0;i--) {   
			             window.setTimeout("doUpdate(" + i + ",'" + nextGoUrl + "')", (secs-i) * 1000);   
			          }

			} else {
				errorDialog("系统提示", "注册失败：" + obj.message);
				if (!LEx.isNotNull(validType)) {
					changeVerify(); // 图形验证码方式更换验证码
				}
			}
		},
		error : function() {
			errorDialog("系统提示", "注册信息发送失败！");
		}
	});
}





//页面倒数计时跳转
 function doUpdate(num,nextGoUrl){   
           document.getElementById("totalSecond").innerHTML = num ;   
           if (num =='0'){
                window.location=nextGoUrl;
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

// 刷新图形验证码
function changeVerify() {
	var url = LEx.webPath + "bsp/verifyCode?time=" + new Date().getTime();
	$("[name=verifyImg]").attr("src", url);
}

//身份证验证
var aCity = { 11: "北京", 12: "天津", 13: "河北", 14: "山西", 15: "内蒙古", 21: "辽宁", 22: "吉林", 23: "黑龙江 ", 31: "上海", 32: "江苏", 33: "浙江", 34: "安徽", 35: "福建", 36: "江西", 37: "山东", 41: "河南", 42: "湖北 ", 43: "湖南", 44: "广东", 45: "广西", 46: "海南", 50: "重庆", 51: "四川", 52: "贵州", 53: "云南", 54: "西藏 ", 61: "陕西", 62: "甘肃", 63: "青海", 64: "宁夏", 65: "新疆", 71: "台湾", 81: "香港", 82: "澳门", 91: "国外 " }

function cidInfo(sId) {
    var iSum = 0;
    var info = "";
    var iCount = sId.length;

    if (!(/(^\d{15}$)|(^\d{17}([0-9]|X)$)/.test(sId))) {
    	errorDialog("系统提示",'输入的身份证号长度不对，或者号码不符合规定！\n15位号码应全为数字，18位号码末位可以为数字或X。');
        return false;
    }
    sId = sId.replace(/x$/i, "a");
    if (aCity[parseInt(sId.substr(0, 2))] == null) { errorDialog("系统提示","非法地区"); return false; }
    sBirthday = sId.substr(6, 4) + "-" + Number(sId.substr(10, 2)) + "-" + Number(sId.substr(12, 2));
    var d = new Date(sBirthday.replace(/-/g, "/"))
    if (sBirthday != (d.getFullYear() + "-" + (d.getMonth() + 1) + "-" + d.getDate())) {errorDialog("系统提示","非法生日"); return false; }
    if (iCount == 18) {
        for (var i = 17; i >= 0; i--) iSum += (Math.pow(2, i) % 11) * parseInt(sId.charAt(17 - i), 11)
        if (iSum % 11 != 1) { errorDialog("系统提示","证件输入不合法"); return false; }
    }
    return true;
}

function loginAndRedirect(formData,obj) {
			var nextGoUrl = $("#goto").val();
			if(obj.total>0 && LEx.isNotNull(obj.data.nextGo)){
				nextGoUrl = obj.data.nextGo;
			}
			if(!LEx.isNotNull(nextGoUrl)){
				nextGoUrl=LEx.webPath+"uinfo";
			}
			window.location.href =nextGoUrl;
}
$.fn.passwordStrength = function(options){
	return this.each(function(){
		 v = $(this).keyup(function(){
			if( typeof el == "undefined" )
				this.el = $(this);
			var s = getPasswordStrength (this.value);
			$("#progress-bar").removeClass().addClass("progress-bar-"+s.className).width(s.width+"%");
		});		
	});
	function getPasswordStrength(pwd){
		/** 强度规则
		 + ------------------------------------------------------- +
		 1) 任何少于6个字符的组合，弱；任何字符数的同类字符组合，弱；
		 2) 任何字符数的两类字符组合，中；
		 3) 12位字符数以下的三类或四类字符组合，强；
		 4) 12位字符数以上的三类或四类字符组合，非常好。
		 + ------------------------------------------------------- +
		**/
		 var strength = 0;
		 if (pwd.length < 6) return strength;
		 if (/\d/.test(pwd)) strength++; //数字
		 if (/[a-z]/.test(pwd)) strength++; //小写
		 if (/[A-Z]/.test(pwd)) strength++; //大写  
		 if (/\W/.test(pwd)) strength++; //特殊字符
		 switch (strength)
		 {
		  case 1:
		   return {width:25,className:'danger'};
		   break;
		  case 2:
		   return {width:50,className:'warning'};
		  case 3:
		  case 4:
		   return pwd.length < 12 ? {width:75,className:'info'} : {width:100,className:'success'};
		   break;
		 }
}};
