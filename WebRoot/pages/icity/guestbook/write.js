var _calendar = null;
var depts=null;		//部门数组
var region="";		//行政区划代码

// 验证数据合法性及数据提交
function init() {
	region=$("#webRegion").val();
	
	_calendar = new LEx.Control.Calendar(function() {
		contentHandler();
	});
	goIndex(1);

	// 如果url中有信件类型参数，则类型下拉框显示指定值并变为不可选
	var letter_type = LEx.urldata.type;
	if (LEx.isNotNull(letter_type)) {
		$("#type").attr("value", letter_type);
	}

	$("#submit").bind("click", function() {
		postData();
	});
}

function goIndex(index) {
	var fp = {};
	fp.id = "flowStep";
	fp.list=[{"title":"选择单位","href" : LEx.webPath + "icity/guestbook/choose"},{"title" : "填写内容"},{"title" : "提交成功"}];
	// 当前索引
	fp.curIndex = index;
	LEx.Control.flowStep(fp);
}

//显示错误提示
function errorDialog(content){
	var option={title : '系统提示',content:content,icon:'error',fixed:true,lock:true,button:[{name:"确定",focus:true}]};
	LEx.dialog(option);
}

//保存信件
function postData(){
	var name = $("#username").val();
	var email = $("#useremail").val();
	var phone = $("#userphone").val();
	var address = $("#useraddress").val();
	var title = $("#title").val();
	var type = $("#type").val();
	var content = $("#content").val();
	var busi_id = LEx.urldata.busi_id;
	
	//检查数据
	var message="";
	if (!LEx.isNotNull(name)) {
		message="请填写您的姓名！";
		errorDialog(message);
		return false;
	}
	if (name.length >= 20) {
		message="姓名过长,请输入正确的姓名！";
		errorDialog(message);
		return false;
	}
	if (!LEx.isNotNull(phone)) {
		message="请填写您的电话！";
		errorDialog(message);
		return false;
	}
	if (phone.length != 11) {
		message="请填写正确的手机号码！";
		errorDialog(message);
		return false;
	}
	var isEmail = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/;
	if (!isEmail.test(email)) {
		message="邮箱地址为空或填写错误，请填写正确的邮箱地址！";
		errorDialog(message);
		return false;
	}
	if (!LEx.isNotNull(title)) {
		message="主题不能为空！";
		errorDialog(message);
		return false;
	}
	if (!LEx.isNotNull(content)) {
		message="内容不能为空！";
		errorDialog(message);
		return false;
	}

	var BadWords = /你妹|狗日|他妈的|他妈|操蛋|fuck|Fuck|FUCK|共产党|习近平/;
	if (BadWords.test(content) || BadWords.test(title) || BadWords.test(address)) {
		message="注意和谐,-_-!";
		errorDialog(message);
		return false;
	}
	
	var depart_id = LEx.urldata.depart;
	var depart_name = LEx.BspCmd.deptName(depart_id);	//根据部门id获取部门名称
	var inputCode = $("#validCode").val();

	var cmd_ins = new LEx.Command("app.icity.guestbook.WriteCmd");
	cmd_ins.setParameter("DEPART_ID", depart_id);
	cmd_ins.setParameter("DEPART_NAME", depart_name);
	cmd_ins.setParameter("USERNAME", LEx.htmlEncode(name));
	cmd_ins.setParameter("PHONE", phone);
	cmd_ins.setParameter("EMAIL", email);
	cmd_ins.setParameter("ADDRESS", LEx.htmlEncode(address));
	cmd_ins.setParameter("TITLE", LEx.htmlEncode(title));
	cmd_ins.setParameter("TYPE", type);
	cmd_ins.setParameter("CONTENT", LEx.htmlEncode(content));
	cmd_ins.setParameter("BUSI_ID", busi_id);
	cmd_ins.setParameter("VerifyCode", inputCode);
	
	var ret = cmd_ins.execute("insert");
	if (ret.error) {
		LEx.dialog.tips(ret.error);		
		changeimg();	// 验证码输入错误则更换验证码
		return false;
	} else {
		var id = ret.data.id;
		location.href = LEx.webPath+"icity/guestbook/information?id="+id;
	}
}

// 清除填写内容
function resetQuery() {
	$("input:visible,select,textarea").val("");
}

// 更换验证码
function changeimg() {
	$("#code").attr("src",LEx.webPath+"bsp/verifyCode?time=" + new Date().getTime());
}