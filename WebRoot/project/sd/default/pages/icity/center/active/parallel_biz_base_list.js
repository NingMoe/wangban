var bltable = null;
function contentHandler(){
	//1.实例化分页table控件：table的id，模版的id
	bltable = new LEx.Control.Table("div_business_list","template_business_list");
	bltable.limit(8);
	onQuery(); 
}

$("#submit").click(function(){
	 onQuery();
	 //$("table input[type=checkbox]").attr("checked",false);
});

$("#delete").click(function(){
	 onDel();
	 //$("table input[type=checkbox]").attr("checked",false);
});
$("#reset").click(function(){
	$("#id").val("");
	$("#title").val("");
	$("#ctime_s").val("");
	$("#ctime_e").val("");
	onQuery();
	//$("table input[type=checkbox]").attr("checked",false);
});
//分页
function onQuery(){
	if(LEx.isNotNull(LEx.userInfo.userId)){
		var cmd = new LEx.Command("app.icity.engineering.EngineeringCmd");
		cmd.setParameter("start", bltable.start());
		cmd.setParameter("limit", bltable.limit());
		var BIZ_ID=$("#id").val();
		if (LEx.isNotNull(BIZ_ID)) {
			cmd.setParameter("t.BIZ_ID@=", BIZ_ID);
		}
		var apply_subject=$("#title").val();
		if (LEx.isNotNull(apply_subject)) {
			cmd.setParameter("t.apply_subject@like", apply_subject);
		}
		var ctime_s=$("#ctime_s").val();
		if (LEx.isNotNull(ctime_s)) {
			cmd.setParameter("t.apply_time@>@Date", ctime_s+" 00:00:00");
		}
		var ctime_e=$("#ctime_e").val();
		if (LEx.isNotNull(ctime_e)) {
			cmd.setParameter("t.apply_time@<@Date", ctime_e+" 23:59:59");
		}				
		var ret =  cmd.execute("getEnginBaseList");
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
	return LEx.util.Format.formatDate(obj);
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
ParallelBizBase=function(){
	$("#parallel_biz_base_list").show();
	$("#parallel_biz_base_listinfo").hide();
};
ParallelBizBaseInfo=function(id){
	$("#parallel_biz_base_list").hide();
	$("#parallel_biz_base_listinfo").show();
	$("#parallel_biz_base_listinfo").load(LEx.webPath+"src?m=icity/center/active/parallel_biz_base_listinfo.html&id="+id, function() {});
};