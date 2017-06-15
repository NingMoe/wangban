function init() {
	
		onQuery(); // 执行默认的查询方法
}
function onQuery() {
	// 定义command对象
	var command = new LEx.Command("app.pmi.config.cmd.WebsiteCmd");
	var ret = command.execute("getWebsiteConfig");
	if(!command.error){
		$('#_website').html(LEx.processDOMTemplate('securityTemplate',ret));
		$(".fwltj").each(function(){
			$(this).html(getSecurity($(this).attr("id")));
		});
	}else{
		LEx.dialog({
			title : "系统提示",
			content : command.error,
			icon : 'error',
			lock : true
		});
	}	
}

function Querymrfwl() {
	// 定义command对象
	var visittime=$(".key").val();
    debugger;
	var command = new LEx.Command("app.pmi.config.cmd.WebsiteCmd");
	var ret = command.execute("getWebsiteConfig");
	if(!command.error){
		$('#_website').html(LEx.processDOMTemplate('securityTemplate',ret));
		$(".mrfwltj").each(function(){
			$(this).html(getSecurity($(this).attr("id")));
		});
	}else{
		LEx.dialog({
			title : "系统提示",
			content : command.error,
			icon : 'error',
			lock : true
		});
	}
	
}
function getSecurity(name) {
	// 定义command对象
	var visittime=$(".key").val();
	var command = new LEx.Command("app.pmi.config.cmd.WebsiteCmd");
	command.setParameter("name", name);
	var ret = command.execute("getWebsiteConfig");
	if(!command.error){
		if(ret.data){
			if(ret.data.length>0){
				if(visittime==""){
				return setFwlHall(ret.data[0].webRegion);
				}else{
					return searchFwlHall(ret.data[0].webRegion);
					}
			}else{
				return "--";
			}
		}else{
			return "--";
		}
	}else{
		return "--";
	}
}
function setFwlHall(region){
  	var command = new LEx.Command("app.icity.project.ProjectIndexCmd");	
	command.setParameter("region_code", region);
  	var ret = command.execute("getTrafficStatisticsHallNow");
  	if(!command.error){
  		if(ret.total>0){
  		  	return ret.data[0].COUNT;
  		}else{
  			return "--";
  		}
  	}else{
  		return "";
  	}
  }

  	function searchFwlHall(region){
  		var visittime=$(".key").val();
  	  	var command = new LEx.Command("app.icity.project.ProjectIndexCmd");	
  		command.setParameter("region_code", region);
  		command.setParameter("visittime",visittime)
  	  	var ret = command.execute("getTrafficStatisticsHallNow");
  	  	if(!command.error){
  	  		if(ret.total>0){
  	  			if(visittime!=""){
  	  		  	return ret.data[0].COUNT;
  	  			}else{
  	  				return "--";
  	  			}
  	  		}else{
  	  			return "--";
  	  		}
  	  	}else{
  	  		return "";
  	  	}
  	  }


function savesite() {
	
	var pArr = $('table .on');
	var params = [];
	$.each(pArr,function(i,n){
		var param = {};
		param.id = $(n).attr("iname");
		param.pid = $(n).find('input[name=pid]').val();
		param.title = $(n).find('input[name=title]').val();
		param.name = $(n).find('input[name=name]').val();
		param.domain = $(n).find('textarea[name=domain]').val();
		params.push(param);
	});
	if (params.length == 0) {
		LEx.dialog.tips("没有配置需要保存!");
		return false;
	}

	var command = new LEx.Command("app.pmi.config.cmd.WebsiteCmd");
	command.setParameter("params", params);
	var ret = command.execute("saveWebsiteConfig");
	if(!command.error){
		LEx.dialog.tips("站点修改成功");
		onQuery();
	}else{
		LEx.dialog({
			title : "系统提示",
			content : command.error,
			icon : 'error',
			lock : true
		});
	}	
}

function deleteSite(name,hasChilds) {
	var width = 200;
	var value = "确定删除吗？";
	if(hasChilds) {
		value = "该站点存在子站点，删除后子站点将成为顶级站点，" + value;
		width = 400;
	}
	LEx.dialog({
		title : "系统提示",
		lock : true,
		content:value,
		width:width,
		height:50,
		ok :function () {
			var command = new LEx.Command("app.pmi.config.cmd.WebsiteCmd");
			command.setParameter("name", name);
			var ret = command.execute("deleteWebsite");
			if(!command.error){
				LEx.dialog.tips("站点删除成功");
				onQuery();
			}else{
				LEx.dialog({
					title : "系统提示",
					content : command.error,
					icon : 'error',
					lock : true
				});
			}	
		},
		cancel :function () {
		}
	});
	
}

function editSite(name) {
	var url = LEx.webPath+'pmi/config/editsite?name=' + name;
	LEx.dialog({
		title : "修改站点",
		url : url,
		lock : true,
		width:800,
		height:400,
		button:[
				{
					    name: '保存',
					    callback: function() {
						var iframe = this.iframe.contentWindow;
						if (!iframe.document.body) {
						    LEx.alert('内容还没加载完毕呢');
						    return false;
						}
						var result = iframe.editsite();
						if(result){
							onQuery();
							//提示添加成功
							LEx.dialog.tips("站点修改成功");
						}else{
							return false;
						}
					    },
					    focus: true
					}]
	});
	
	
	
}

function addsite(pid,level) {
	var url = LEx.webPath+'pmi/config/addsite?pid=' + pid;
	LEx.dialog({
		title : "添加站点",
		url : url,
		lock : true,
		width:800,
		height:400,
		button:[
				{
					    name: '保存',
					    callback: function() {
						var iframe = this.iframe.contentWindow;
						if (!iframe.document.body) {
						    LEx.alert('内容还没加载完毕呢');
						    return false;
						}
						var result = iframe.addsite();
						if(result){
							onQuery();
							//提示添加成功
							LEx.dialog.tips("站点添加成功");
						}else{
							return false;
						}
					    },
					    focus: true
					}]
	});
	
	
	
}

function editProp(filePath){
	var url = LEx.webPath+'pmi/config/security?filePath=' + filePath;
	LEx.dialog({
		title: "站点属性",
		url: url,
		width: 1000, height: 600,
		button: [
		         ]
	});
}

function configSite(name) {
	var url = LEx.webPath+'pmi/config/individuation?type=system&name=' + name;
	LEx.dialog({
		title: "配置站点",
		url: url,
		width: 1000, 
		height: 600,
		button:[
				{
					    name: '保存',
					    callback: function() {
						var iframe = this.iframe.contentWindow;
						if (!iframe.document.body) {
						    LEx.alert('内容还没加载完毕呢');
						    return false;
						}
						var result = iframe.save();
						if(result){
							onQuery();
							//提示添加成功
							LEx.dialog.tips("站点配置成功");
						}else{
							return false;
						}
					    },
					    focus: true
					}]
	});
}
function goMenu(name){
	var url = "/"+name+'/pmi/config/menuList?websiteType='+$("#PAGE_MODE").val();
	LEx.dialog({
		title: "栏目管理",
		url: url,
		width:1000,
		height:400,
		button:[
			{
				name: '保存',
				callback: function() {
					var iframe = this.iframe.contentWindow;
					if (!iframe.document.body) {
						LEx.alert('内容还没加载完毕呢');
						return false;
					}
					var result = iframe.save();
					if(result){
						onQuery();
						//提示添加成功
						LEx.dialog.tips("站点配置成功");
					}else{
						return false;
					}
				},
				focus: true
			}]
	});
}

function goWebSite(name){
	var url = LEx.webPath+'pmi/config/individuation?type=system&name=' + name;
	LEx.dialog({
		title : "站点配置",
		url : url,
		lock : true,
		width:1000,
		height:400,
		button:[
			{
				name: '保存',
				callback: function() {
					var iframe = this.iframe.contentWindow;
					if (!iframe.document.body) {
						LEx.alert('内容还没加载完毕呢');
						return false;
					}
					var result = iframe.editsite();
					if(result){
						onQuery();
						//提示添加成功
						LEx.dialog.tips("站点修改成功");
					}else{
						return false;
					}
				},
				focus: true
			}]
	});
}