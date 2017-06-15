function init() {
		onQuery(); // 执行默认的查询方法
}



function onQuery() {
	// 定义command对象
	var command = new LEx.Command("app.pmi.config.cmd.ConfigCmd");
	var filePath = LEx.urldata.filePath;
	command.setParameter("filePath", filePath);
	var ret = command.execute("getSecurityList");
	
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

function cmd(obj,xh){
	var type = $(obj).attr('class');
	$('#tr_'+xh).removeClass();
	$(obj).removeClass();
	if(type == 'selectedOn'){
		$('#tr_'+xh).addClass('off');
		$(obj).addClass('selectedOff');
		$('#tr_'+xh).find('textarea').attr('disabled','disabled');
		$('#tr_'+xh).find('textarea').addClass('disabled_textarea');
		$('#tr_'+xh).find('textarea').val('');
	}else{
		$('#tr_'+xh).addClass('on');
		$(obj).addClass('selectedOn');
		$('#tr_'+xh).find('textarea').removeClass('disabled_textarea');
		$('#tr_'+xh).find('textarea').removeAttr('disabled');
	}
}

function viewAll(obj){
	var div = $(obj).children().get(0);
	LEx.dialog.tips($(div).html());
}

function save(){
	var filePath = LEx.urldata.filePath;
	var pArr = $('table .on');
	var params = [];
	$.each(pArr,function(i,n){
		var param = {};
		param.id = $(n).find('input[id="id"]').val();
		var value = $(n).find('textarea[id="value"]').val();
		if(LEx.isNotNull(value)){
			param.value = $(n).find('textarea[id="value"]').val();
			params.push(param);
		}
	});
	if (params.length == 0) {
		LEx.dialog.tips("没有配置需要保存!");
		return false;
	}
	var command = new LEx.Command("app.pmi.config.cmd.ConfigCmd");
	command.setParameter("param", params);
	command.setParameter("filePath", filePath);
	var ret = command.execute("securitySave");
	if(ret.state=='1'){
		LEx.dialog.tips("security配置修改成功!");
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