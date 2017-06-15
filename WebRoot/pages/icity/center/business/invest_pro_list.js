var bltable = null;

$(function(){
	var intervalId = setInterval(function(){
		$(".payinfo1").toggle("slow");			
	},"1500");
});
function contentHandler(status){
	if(_calendar != null){
		_calendar.setup({id:"APPLYTIME_S"});
		_calendar.setup({id:"APPLYTIME_E"});
	}else{
		_calendar= new LEx.Control.Calendar(function(){
			_calendar.setup({id:"APPLYTIME_S"});
			_calendar.setup({id:"APPLYTIME_E"});
		});
	}
	
	LEx.icityBusiness.initSelect("select_status");
	
	bltable = new LEx.Control.Table("bl_list","template_business_list");
	bltable.limit(5);
	onQuery();
}

//分页
function onQuery(){
	var cmd = new LEx.Command("app.icity.pro.ProCmd");
	cmd.setParameter("start", bltable.start());
	cmd.setParameter("limit", bltable.limit());
	cmd.setParameter("uuid",""+LEx.userInfo.uid);
	
	var sblsh = $("#SBLSH").val();
	if(LEx.isNotNull(sblsh)){
		cmd.setParameter("SBLSH@like",sblsh);
	}
   var applytime_s = $('#APPLYTIME_S').val();
   if(LEx.isNotNull(applytime_s)){
	   cmd.setParameter("SBSJ@>=@Date",applytime_s+" 00:00:00");
   }
   var applytime_e = $('#APPLYTIME_E').val();
   if(LEx.isNotNull(applytime_s)&&LEx.isNotNull(applytime_e)){
	   if(applytime_s>applytime_e){
		   LEx.alert("截止日期不允许小于开始时间");
		   return;
	   }  
   }
   if(LEx.isNotNull(applytime_e)){
	   cmd.setParameter("SBSJ@<=@Date",applytime_e+" 23:59:59");
   }
   cmd.setParameter("sort"," SBSJ DESC");
   var ret = cmd.execute("getProList");
   if(!ret.error){			
		//2.将数据传递给bltable对象
		bltable.toBody(ret);
		//3.加载分页控件：分页div的id，bltable的实例化对象名称（字符串）,总数量，查询方法（字符串）
		bltable.toPageBar("raiderpager_business_list","bltable",ret.total,"onQuery()");
		$("#bldata").html(LEx.processDOMTemplate("template_business_list",ret));
	}else{
		LEx.alert(ret.error);
	}
}

function formatState(val){
	if(val=="01"){
		return "未赋码";
	}else if(val=="99"){
		return "赋码通过";
	}else if(val=="03"){
		return "赋码未通过";
	}
}

function formatHandleState(val){
	
	if(val=="1"){
		return "草稿";
	}else if(val=="2"){
		return "收件";
	}else if(val=="3"){
		return "预受理";
	}else if(val=="4"){
		return "预受理退回";
	}else if(val=="5"){
		return "受理";
	}else if(val=="6"){
		return "补齐补正";
	}else if(val=="7"){
		return "不予受理";
	}else if(val=="8"){
		return "在办";
	}else if(val=="9"){
		return "挂起";
	}else if(val=="10"){
		return "办结";
	}else if(val=="11"){
		return "转报办结";
	}else if(val=="12"){
		return "作废办结";
	}else if(val=="13"){
		return "退件";
	}else if(val=="14"){
		return "不予行政许可";
	}else if(val=="99"){
		return "预审批";
	}else if(val=="100"){
		return "豁免";
	}else if(val=="101"){
		return "备案";
	}
}

function ProBusinessDetail(seqid,code){
	$("#ProBusinessDetail").show();
	$("#businessList_main").hide();
	$("#ProBusinessDetail").load(LEx.webPath+"src?m=icity/center/business/business_pro_detail.html&id="+seqid+"&code="+code, function() {});
}
function ProBusinessList(){
	$("#ProBusinessDetail").hide();
	$("#businessList_main").show();
}

//联合审批
function lhsp(projectId){
	var cmd = new LEx.Command("app.icity.pro.ProCmd");
	cmd.setParameter("projectId",projectId);
	var ret = cmd.execute("geUnionBizByProjectId");
}

function showXmbpList(seqid,code,type){
	$("#XmbpList").show();
	$("#businessList_main").hide();
	$("#XmbpList").load(LEx.webPath+"src?m=icity/center/business/xmbp_list.html&id="+seqid+"&code="+code+"&type="+type, function() {initDict();onQuery1();});
}
function xmbpBack(){
	$("#XmbpList").hide();
	$("#businessList_main").show();
}
function showLhspList(seqid,code){
	$("#LhspList").show();
	$("#businessList_main").hide();
	$("#LhspList").load(LEx.webPath+"src?m=icity/center/business/lhsp_list.html&id="+seqid+"&code="+code, function() {onQuery2();});
}
function lhspBack(){
	$("#LhspList").hide();
	$("#businessList_main").show();
}

//项目关联
function xmgl(){
	var n_url = LEx.webPath+"admin?m=icity/center/business/xmgl.html";
	LEx.dialog({
		id: "", title: "项目关联",
		url: n_url,
		width: 500, height: 280,
		lock:true,
		button: [
		         {
		        	name: '确定',
		        	callback: function() {
		        		var iframe = this.iframe.contentWindow;
		        		if (!iframe.document.body) {
		        			LEx.alert('内容还没加载完毕呢');
		        			return false;
		        		}		        		
		        	 	if(iframe.checkdata()){
		        	 		LEx.alert("项目关联成功！");
		        	 	}else{
		        	 		LEx.alert("项目关联失败！");
		        	 	}
		         	},
		         	focus: true
		         },
		         {
		        	 name: '关闭'
		         }
		         ]
	});
}

function showInform(seqid){
	$("#InformList").show();
	$("#businessList_main").hide();
	$("#InformList").load(LEx.webPath+"src?m=icity/center/business/informlist.html&id="+seqid, function() {querylist();});
}
function informBack(){
	$("#InformList").hide();
	$("#businessList_main").show();
}