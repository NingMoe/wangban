var editor;
function init() {
	var id = LEx.urldata.id;
	if (isNotNull(id)) {
		var command = new LEx.Command("app.icity.center.active.BusinessComplaintCmd");
		command.setParameter("g.id@=", id);
		var ret = command.execute("getBusinessComplaint");
		if (ret.state == 1) {
			if (ret.data.length > 0) {
				var d = ret.data[0];
				debugger;
				if(isNotNull(d.STATUS)){
					if(d.STATUS!="3"){
						var trs = $("tr[class='hid']");  
						for(i = 0; i < trs.length; i++){   
						    trs[i].style.display = "none"; //这里获取的trs[i]是DOM对象而不是jQuery对象，因此不能直接使用hide()方法  
						} 	
						
					}
				}
				if (d.DEAL_DATE){
					$("#TITLE").attr("tag","TITLE");
					$("#CONTENT").attr("tag","CONTENT");
				}else{
					$("#TITLE").html('<input tag="TITLE" id="complaint_title" class="input" style="width:552px;" value="" />');
					$("#CONTENT").html('<textarea rows="6" cols="7" id="complaint_content" style="width:99%;height:300px;font-size:14px" tag="CONTENT"></textarea>');
					LEx.Control.editor("complaint_content", editorFun);
				}
				LEx.form.set("form", d);
				if (isNotNull(d.DEAL_DATE)) {
					$("#OPEN").val(formatFlag(d.OPEN));
				}
				$("#WRITE_DATE").html(LEx.util.Format.formatDate(d.WRITE_DATE));
				$("#DEAL_DATE").html(LEx.util.Format.formatDate(d.DEAL_DATE));
	
			
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
	var command = new LEx.Command("app.icity.center.active.BusinessComplaintCmd");
	var title = $("input[tag=TITLE]").val();
	var dealDate=$("#DEAL_DATE").html();
	var content="";
	if(isNotNull(dealDate)){
		content = $("#CONTENT").text();
		if (!isNotNull(content)) {
			LEx.alert("投诉内容不能为空！");
			return false;
		}			
	}else{
		content=editor.html();
		if (!isNotNull(content)) {
			LEx.alert("投诉内容不能为空！");
			return false;
		}			
	}
	var id = LEx.urldata.id;
	if (isNotNull(id)) {
		// 如果有主键则修改
		if (!isNotNull(title)) {
			title = $("#TITLE").text();
			if (!isNotNull(title)) {
			LEx.alert("投诉标题不能为空！");
			return false;
			}
		}	
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

function formatCheckFlag(val) {
	if (val == '1') {
		return "审核通过";
	} else if (val == '2') {
		return "审核不通过";
	} else {
		return "未审核";
	}
}
