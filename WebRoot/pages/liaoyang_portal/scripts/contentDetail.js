$(function(){	
	var id = LEx.urldata.id;
	onQuery(id);
});

function onQuery(id){
	var cmd = new LEx.Command("app.icity.govservice.GovProjectCmd");	
	cmd.setParameter("id", id);
	var ret =  cmd.execute("getContentDetail");
	if(!ret.error){
		$("#data").html(LEx.processDOMTemplate('template',ret));
	}else{
		LEx.alert(ret.error);
	}
}

function parseDate(val){
	if(LEx.isNotNull(val)){
		return LEx.util.Format.formatDate(val);
	}else{
		return '';
	}
}
function shareWebSite(url){
	window.open(url+'?title='+encodeURIComponent(location.href)+'&url='+encodeURIComponent(location.href),"_blank");
}
//发布内容格式为<p>时，打印会出现错乱现象，此处将<p>替换为<div>
function formartp(val){
	val = val.replace(new RegExp(/(<p)/g),'<div');
	val = val.replace(new RegExp(/(<\/p>)/g),'<\/div>');
	return val;
}
function printArea(){
	$("#bdshare").hide();
	$("#content").printArea();
}