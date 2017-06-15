var editor;
function init() {
	var id = LEx.urldata.id;
	if(isNotNull(id)){
		var command = new LEx.Command("app.icity.center.active.BusinessFsbsjCmd");
		command.setParameter("f.id@=", id);
		var ret = command.execute("getBusinessFsbsj");
		if (ret.state == 1) {
			if (ret.data.length > 0) {
				var d = ret.data[0];
				LEx.form.set("form", d);
				$("#DEPTNAME").html(d.YYDEPTNAME);
				$("#DEPTPHONE").html(d.DEPTPHONE);
				$("#YYDAY").html(d.YYDAY);
				$("#YYTIME").html(d.YYTIME);
				$("#YYCONTENT").html(d.YYCONTENT);
				$("#RECONTENT").html(d.RE_CONTENT);
				$("#REDAY").html(LEx.util.Format.formatDate(d.REDAY));
				
			}
		} else {
			LEx.dialog({
				title : "系统提示",
				content : ret.message,
				icon : 'error',
				lock : true
			});
		}
	}
}

function editorFun(obj) {
	editor = obj;
}

function postData() {
	// 创建Command对象
	var command = new LEx.Command("app.icity.center.active.BusinessAskCmd");
	var title = $("#TITLE").html();
	var dealDate=$("#DEAL_DATE").html();
	var content="";
	if(isNotNull(dealDate)){
		$("input[tag=CONTENT]").val();	
	}else{
		content=editor.html();
		if (!isNotNull(content)) {
			LEx.alert("咨询内容不能为空！");
			return false;
		}			
	}
	var open = $("#OPEN").val();	
	var id = LEx.urldata.id;
	if (isNotNull(id)) {
		// 如果有主键则修改
		command.setParameter("ID", id);
		command.setParameter("TITLE", title);
		command.setParameter("OPEN", open);
		command.setParameter("CONTENT", content);
		ret = command.execute("update");
	} else {
		LEx.dialog({
			title : "系统提示",
			content : "主键为空",
			icon : 'error',
			lock : true
		});
	}
	// 结果处理
	if (ret.state == 1) {
		return true;
	} else {
		LEx.dialog({
			title : "系统提示",
			content : ret.message,
			icon : 'error',
			lock : true
		});
	}
	return false;
}

function formatFlag(val) {
	if (val == '1') {
		return "是";
	} else if (val == '0') {
		return "否";
	} else {
		return "";
	}
}

function formatWarn(val){
	if (val == '1') {
		return "已超期";
	} else {
		return "未超期";
	}
}

function formatCheckFlag(val) {
	if (val == '1') {
		return "审核通过";
	} else if (val == '2') {
		return "审核不通过";
	} else {
		return "未审核";
	}
}
