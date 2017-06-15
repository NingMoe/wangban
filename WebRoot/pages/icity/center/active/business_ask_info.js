var editor;
function init() {
	var id = LEx.urldata.id;
	if(isNotNull(id)){
		var command = new LEx.Command("app.icity.center.active.BusinessAskCmd");
		command.setParameter("g.id@=", id);
		var ret = command.execute("getBusinessAsk");
		if (ret.state == 1) {
			if (ret.data.length > 0) {
				var d = ret.data[0];
				if (d.DEAL_DATE){
					$("#TITLE").attr("tag","TITLE");
					$("#CONTENT").attr("tag","CONTENT");
				}else{
					$("#TITLE").html('<input tag="TITLE" id="ask_title" class="input" style="width:552px;" value="" />');
					$("#CONTENT").html('<textarea rows="6" cols="7" id="ask_content" style="width:99%;height:300px;font-size:14px" tag="CONTENT"></textarea>');
					LEx.Control.editor("ask_content", editorFun);
				}
				LEx.form.set("form", d);
				$("#WARN").html(formatWarn(d.WARN));
				if (isNotNull(d.DEAL_DATE)) {
					$("#OPEN").val(formatFlag(d.OPEN));
				}
				$("#SXMC").html(LEx.util.Format.formatDate(d.SXMC));
				$("#WRITE_DATE").html(LEx.util.Format.formatDate(d.WRITE_DATE));
				$("#DEAL_DATE").html(LEx.util.Format.formatDate(d.DEAL_DATE));
				$("#REPLY").html(formatDetailFlag(d.STATUS));
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
		content=$("#CONTENT").text();
		if (!isNotNull(content)) {
			LEx.alert("咨询内容不能为空！");
			return false;
		}	
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
		command.setParameter("CONTENT", encodeURIComponent(content));
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
function formatDetailFlag(val) {
	if (val == '0') {
		return "未办理";
	} else if (val == '1') {
		return "已回复";
	}else if (val == '3') {
		return "审核通过";
	}else if (val == '4') {
		return "审核不通过";
	}else {
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
