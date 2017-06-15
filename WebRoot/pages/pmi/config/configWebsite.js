function init() {
		onQuery(); // 执行默认的查询方法
}

function onQuery() {
	// 定义command对象
	var command = new LEx.Command("app.pmi.config.cmd.WebsiteCmd");
	var ret = command.execute("getWebsiteConfig");
	if(!command.error){
		$('#_website').html(LEx.processDOMTemplate('securityTemplate',ret));
	}else{
		LEx.dialog({
			title : "系统提示",
			content : command.error,
			icon : 'error',
			lock : true
		});
	}	
}


function savesite() {
	var pArr = $('table .on');
	var params = [];
	$.each(pArr,function(i,n){
		var param = {};
		param.pname = $(n).find('input[name=pname]').val();
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
		LEx.dialog.tips("站点修改成功!");
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
				LEx.dialog.tips("站点删除成功!");
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
	var url = LEx.webPath+'ipmi?m=pmi/config/editsite.html&name=' + name;
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
						    LEx.alert('内容还没加载完毕!');
						    return false;
						}
						var result = iframe.editsite();
						if(result){
							onQuery();
							//提示添加成功
							LEx.dialog.tips("站点修改成功!");
						}else{
							return false;
						}
					    },
					    focus: true
					}]
	});
	
	
	
}

function addsite(pname,level) {
	var url = LEx.webPath+'ipmi?m=pmi/config/addsite.html&pname=' + pname;
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
						    LEx.alert('内容还没加载完毕!');
						    return false;
						}
						var result = iframe.addsite();
						if(result){
							onQuery();
							//提示添加成功
							LEx.dialog.tips("站点添加成功!");
						}else{
							return false;
						}
					    },
					    focus: true
					}]
	});
	
	
	
}

function editProp(filePath){
	var url = LEx.webPath+'ipmi?m=pmi/config/security.html&filePath=' + filePath;
	LEx.dialog({
		title: "站点属性",
		url: url,
		width: 1000, height: 600,
		button: [
		         ]
	});
}

function configSite(name) {
	var url = LEx.webPath+'ipmi?m=pmi/config/individuation.html&type=system&name=' + name;
	LEx.dialog({
		title: "配置站点",
		url: url,
		width: 1000, height: 600,
		button:[
				{
					    name: '保存',
					    callback: function() {
						var iframe = this.iframe.contentWindow;
						if (!iframe.document.body) {
						    LEx.alert('内容还没加载完毕!');
						    return false;
						}
						var result = iframe.save();
						if(result){
							onQuery();
							//提示添加成功
							LEx.dialog.tips("站点配置成功!");
						}else{
							return false;
						}
					    },
					    focus: true
					}]
	});
}