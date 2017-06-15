function init() {
		onQuery(); // 执行默认的查询方法
}


function formatIndex(i){
	return parseInt(i)+1;
}
function onQuery() {
	// 定义command对象
	var command = new LEx.Command("app.pmi.config.cmd.PluginCmd");
	var ret = command.execute("getList");
	if(!command.error){
		$('#plugin').html(LEx.processDOMTemplate('pluginTemplate',ret));
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
		$('#tr_'+xh).find('input').attr('disabled','disabled');
		$('#tr_'+xh).find('input').addClass('disabled_textarea');
		$('#tr_'+xh).find('input').val('');
	}else{
		$('#tr_'+xh).addClass('on');
		$(obj).addClass('selectedOn');
		$('#tr_'+xh).find('input').removeClass('disabled_textarea');
		$('#tr_'+xh).find('input').removeAttr('disabled');
	}
}
function save(){
	var pArr = $('table .on');
	var params = [];
	$.each(pArr,function(i,n){
		var param = {};
		param.id = $(n).find('input[id="id"]').val();
		params.push(param);
	});
	var command = new LEx.Command("app.pmi.config.cmd.PluginCmd");
	command.setParameter("param", params);
	var ret = command.execute("save");
	if(!command.error){
		LEx.dialog.tips("插件配置修改成功!");
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