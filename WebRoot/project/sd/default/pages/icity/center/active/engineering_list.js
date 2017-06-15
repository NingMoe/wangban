var bltable = null;
function contentHandler(){
	//1.实例化分页table控件：table的id，模版的id
	bltable = new LEx.Control.Table("div_business_list","template_business_list");
	bltable.limit(8);
	onQuery(); 
}

$("#submit").click(function(){
	 onQuery();
	 $("table input[type=checkbox]").attr("checked",false);
});

$("#delete").click(function(){
	 onDel();
	 $("table input[type=checkbox]").attr("checked",false);
});
//分页
function onQuery(){
	if(LEx.isNotNull(LEx.userInfo.userId)){
		var cmd = new LEx.Command("app.icity.engineering.EngineeringCmd");
		cmd.setParameter("start", bltable.start());
		cmd.setParameter("limit", bltable.limit());
		cmd.setParameter("ucid",LEx.userInfo.uid+"");
		var id=$("#id").val();
		if (LEx.isNotNull(id)) {
			cmd.setParameter("t.PROJECT_ID@=", id);
		}
		var content=$("#title").val();
		if (LEx.isNotNull(content)) {
			cmd.setParameter("t.PROJECT_NAME@like", content);
		}
		var ctime_s=$("#ctime_s").val();
		if (LEx.isNotNull(ctime_s)) {
			cmd.setParameter("t.CREATE_TIME@>@Date", ctime_s+" 00:00:00");
		}
		var ctime_e=$("#ctime_e").val();
		if (LEx.isNotNull(ctime_e)) {
			cmd.setParameter("t.CREATE_TIME@<@Date", ctime_e+" 23:59:59");
		}				
		var ret =  cmd.execute("getProject");
		if(!ret.error){
			//2.将数据传递给table对象
			bltable.toBody(ret);
			//3.加载分页控件：分页div的id，Table的实例化对象名称（字符串）,总数量，查询方法（字符串）
			bltable.toPageBar("epager","bltable",ret.total,"onQuery()");
			$("#div_business_list").html(LEx.processDOMTemplate("template_business_list",ret));			
		}else{
			LEx.alert(ret.error);
		}	
	}else{
		LEx.alert("当前无用户登录，请先登录！");
	}
}

function selectAll(){
	 if($("#checkall").attr("checked")=="checked"){
		 $("table input[type=checkbox]").attr("checked",true);
	 }else{
		 $("table input[type=checkbox]").attr("checked",false);
	 }
}

function select(id){
	if($("#"+id).attr("checked")=="checked"){
		$("#"+id).attr("checked", true);
	}else{
		$("#"+id).attr("checked", false);
	}
}

//删除事件
function onDel(){
	var command = new LEx.Command("app.icity.engineering.EngineeringCmd");
	var pvalue = [];
	$('#bodys input:checked').each(function(){ 
		pvalue.push($(this).attr("id"));
	});
	command.setParameter("ids",pvalue.join(","));
	var ret =  command.execute("delProject");
	if(!ret.error){	
		onQuery();
		LEx.alert("删除成功！");
	}else{
		LEx.alert(ret.error);
	}
}


function formatDate(obj){
	return LEx.util.Format.formatDate(obj,"yyyy-MM-dd HH:mm:ss");
}


function FormatIsFlag(obj){
	if(obj=="1"){
		return "是";
	}else if(obj=="0"){
		return "否";
	}else{
		return "";
	}
}
EngineeringList=function(){
	$("#EngineeringList").show();
	$("#businessEngineeringinfo").hide();
};
Engineeringinfo=function(id){
	$("#EngineeringList").hide();
	$("#businessEngineeringinfo").show();
	$("#businessEngineeringinfo").load(LEx.webPath+"src?m=icity/center/active/engineering_listinfo.html&id="+id, function() {});
};