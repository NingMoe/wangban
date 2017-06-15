function init() {
		onQuery(); // 执行默认的查询方法
}



function onQuery() {
	// 定义command对象
	var command = new LEx.Command("app.pmi.config.cmd.WebsiteCmd");
	var name = LEx.urldata.name;
	command.setParameter("name", name);
	var ret = command.execute("getWebsiteConfig");
	if(!command.error){
		$('#security').html(LEx.processDOMTemplate('securityTemplate',ret));
	}else{
		LEx.dialog({
			title : "系统提示",
			content : command.error,
			icon : 'error',
			lock : true
		});
	}	
}

function editsite(){
	var oldname = LEx.urldata.name;
	var name = $("#name").val();
	if(!name || name.length <=0) {
		LEx.dialog({
			title : "系统提示",
			content : "上下文路径不能为空!",
			icon : 'error',
			lock : true
		});
		return false;
	}
	
	var command = new LEx.Command("app.pmi.config.cmd.WebsiteCmd");
	command.setParameter("oldname", oldname);
	command.setParameter("title", $("#AppHall").val());
	command.setParameter("name", name);
	command.setParameter("domain", $("#domain").val());
	command.setParameter("WebRegion", $("#WebRegion").val());
	command.setParameter("WebRank", $("#WebRank").val());	
	command.setParameter("PAGE_MODE", $("#PAGE_MODE").val());
	command.setParameter("IS_SHOW", $("#IS_SHOW").val());

	var ret = command.execute("setWebsiteConfig");
	
	if(ret.state=="1"){
		return true;
	}else{
		LEx.dialog({
			title : "系统提示",
			content : ret.message,
			icon : 'error',
			lock : true
		});
		return false;
	}	
}