function addsite() {
	var AppHall = $("#AppHall").val();
	var WebRegion = $("#WebRegion").val();
	var WebRank = $("#WebRank").val();
	var name = $("#name").val();
	var domain = $("#domain").val();
	var PAGE_MODE = $("#PAGE_MODE").val();
	var IS_SHOW = $("#IS_SHOW").val();
	if(!WebRegion || WebRegion.length <=0) {
		LEx.dialog({
			title : "系统提示",
			content : "分厅区划不能为空!",
			icon : 'error',
			lock : true
		});
		return false;
	}
	if(!name || name.length <=0) {
		LEx.dialog({
			title : "系统提示",
			content : "上下文路径不能为空!",
			icon : 'error',
			lock : true
		});
		return false;
	}
	var pid = LEx.urldata.pid;
	var command = new LEx.Command("app.pmi.config.cmd.WebsiteCmd");
	command.setParameter("title", AppHall);
	command.setParameter("name", name);
	command.setParameter("domain", domain);
	command.setParameter("pid", pid);
	command.setParameter("WebRegion", WebRegion);
	command.setParameter("WebRank", WebRank);
	command.setParameter("PAGE_MODE", PAGE_MODE);
	command.setParameter("IS_SHOW", IS_SHOW);

	var ret = command.execute("addWebsiteConfig");
	if(ret.state=="1"){
		/*var obj = new Object();
		obj.title = AppHall;
		obj.name = name;
		obj.domain = domain;
		obj.pname = pname;*/
		return true;
	}else {
		LEx.dialog({
			title : "系统提示",
			content : ret.message,
			icon : 'error',
			lock : true
		});
		return false;
	}
}