var bltable1 = null;
var id = "{{PageContext.Parameter.id}}";
var code = "{{PageContext.Parameter.code}}";
var type = "{{PageContext.Parameter.type}}";

$(function(){
	var intervalId = setInterval(function(){
		$(".payinfo1").toggle("slow");			
	},"1500");
	
	if(_calendar != null){
		_calendar.setup({id:"APPLYTIME_S1"});
		_calendar.setup({id:"APPLYTIME_E1"});
	}else{
		_calendar= new LEx.Control.Calendar(function(){
			_calendar.setup({id:"APPLYTIME_S1"});
			_calendar.setup({id:"APPLYTIME_E1"});
		});
	}
});

function onQuery1(){
	var cmd = new LEx.Command("app.icity.pro.ProCmd");
	cmd.setParameter("projectId",id);
	cmd.setParameter("itemName",$("#itemName").val());
	cmd.setParameter("stage",$("#stage").val());
	var applytime_s = $('#APPLYTIME_S1').val();
	var applytime_e = $('#APPLYTIME_E1').val();
	if(LEx.isNotNull(applytime_s)&&LEx.isNotNull(applytime_e)){
	   if(applytime_s>applytime_e){
		   LEx.alert("截止日期不允许小于开始时间");
		   return;
	   }  
	}
	cmd.setParameter("startTime",applytime_s);
	cmd.setParameter("endTime",applytime_e);
	var ret = cmd.execute("getBussinessByProjectId");
	if(!cmd.error){
		bltable1 = new LEx.Control.Table("bl_list1","template_business_list1");
		bltable1.toBody(ret);
		$("#bldata1").html(LEx.processDOMTemplate("template_business_list1",ret));
	}else{
		LEx.alert(cmd.error);
	}
}

BusinessDetail=function(id,itemId){
    
	var n_url = LEx.webPath+"admin?m=icity/center/business/business_detail.html&id="+id+"&type=0&sxid="+itemId;
		LEx.dialog({
			id:'',
			title:"业务详情",
			url:n_url,
			width:890,
			height:400,
			lock:true
		}); 
};

/**
 * 业务详情
 */
function detail(id){
	var n_url=LEx.webPath+"icity/center/business/detail?BID="+id;
	operationDialog({title: "业务详情",url: n_url,width:920,height: 500});
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

//初始化字典
function initDict(){
	
	//事项所属阶段
	var TZSPSSJD=[];
	//审批事项所属阶段
	var TZXMSPLXMJD=[];
	//核准所属阶段
	var TZXMHZLXMJD=[];
	//备案所属阶段
	var TZXMBALXMJD=[];
	
	var command = new LEx.Command("app.icity.pro.ProCmd");
	command.setParameter("dictCode", "TZXMSPLXMJD,TZXMHZLXMJD,TZXMBALXMJD");
	var ret = command.execute("getDictInfo");
	if(!command.error){		
		TZXMSPLXMJD = ret.data.info[0].TZXMSPLXMJD;
		TZXMHZLXMJD = ret.data.info[0].TZXMHZLXMJD;
		TZXMBALXMJD = ret.data.info[0].TZXMBALXMJD;
	}
	
	if(type == "A00001"){
		TZSPSSJD = TZXMSPLXMJD;
	}else if(type == "A00002"){
		TZSPSSJD = TZXMHZLXMJD;
	}else if(type == "A00003"){
		TZSPSSJD = TZXMBALXMJD;
	}
	$("#stage").append("<option value=''>请选择</option>");
	for(var i=0;i<TZSPSSJD.length;i++){
		$("#stage").append("<option value='"+TZSPSSJD[i].code+"'>"+TZSPSSJD[i].name+"</option>");
	}
}

function showXmbpDetail(bizId,itemId){
	
	var n_url = LEx.webPath+"admin?m=icity/center/business/xmbp_detail.html&bizId="+bizId+"&itemId="+itemId;
	LEx.dialog({
		id:'',
		title:"业务详情",
		url:n_url,
		width:890,
		height:400,
		lock:true
	}); 
}