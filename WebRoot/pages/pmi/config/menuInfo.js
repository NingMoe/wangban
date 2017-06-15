function init(){
	$("#tagType").change(function(){
		var t = $(this).val();
		if(t =="url"){
			$(".portalTag").hide();
			$(".urlTag").show();
			$("input[tag='TITLE']").attr("reg","");
		}else if(t =="portal"){
			$(".urlTag").hide();
			$(".portalTag").show();
			$("input[tag='TITLE']").attr("reg","^.+$");
		}
	});
	LEx.form.init();
	var pid = LEx.urldata.pid;
	if(LEx.isNotNull(pid)){
		$("input[tag='PARENT_ID']").val(pid);
	}
}
function postData() {
	if(!LEx.form.validate()){
		LEx.dialog({ title: "操作提示", content: "您填写的数据有误，请检查红色提示框！", icon: 'error' ,lock:true});
		return false;
	}
	var data = LEx.form.get("form");
	var id =LEx.urldata.id;
	var method = "";
	if(LEx.isNotNull(id)){
		data.ID = data;
		method = "updateWebSiteMenu";
	}else{
		data.ID = Guid.newGuid();
		method = "insertWebSiteMenu";
	}
	var websiteId = LEx.urldata.websiteId;
	if(LEx.isNotNull(websiteId)){
		data.WEBSITE_ID = websiteId;
	}
	data.SORT_ORDER = "101";
	var cmd = new LEx.Command("app.pmi.PortalCmd");
	//将表单内容设置到command对象中
	for(var property in data){
		cmd.setParameter(property,data[property]);
	}
	var ret = cmd.execute(method);
	if(ret.state=="1"){
		return true;
	}else{
		alert("提交数据失败，错误原因为:<br/>"+ret.message);
		return false;
	}
}