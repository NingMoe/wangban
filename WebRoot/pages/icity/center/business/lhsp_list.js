var bltable2 = null;
var id = "{{PageContext.Parameter.id}}";
var code = "{{PageContext.Parameter.code}}";

$(function(){
	var intervalId = setInterval(function(){
		$(".payinfo1").toggle("slow");			
	},"1500");
	
	if(_calendar != null){
		_calendar.setup({id:"APPLYTIME_S2"});
		_calendar.setup({id:"APPLYTIME_E2"});
	}else{
		_calendar= new LEx.Control.Calendar(function(){
			_calendar.setup({id:"APPLYTIME_S2"});
			_calendar.setup({id:"APPLYTIME_E2"});
		});
	}
});

function onQuery2(){
	var cmd = new LEx.Command("app.icity.pro.ProCmd");
	cmd.setParameter("projectId",id);
	var applytime_s = $('#APPLYTIME_S2').val();
	var applytime_e = $('#APPLYTIME_E2').val();
	if(LEx.isNotNull(applytime_s)&&LEx.isNotNull(applytime_e)){
	   if(applytime_s>applytime_e){
		   LEx.alert("截止日期不允许小于开始时间");
		   return;
	   }  
	}
	cmd.setParameter("startTime",applytime_s);
	cmd.setParameter("endTime",applytime_e);
	var ret = cmd.execute("geUnionBizByProjectId");
	if(!cmd.error){
		bltable2 = new LEx.Control.Table("bl_list2","template_business_list2");
		bltable2.toBody(ret);
		$("#bldata2").html(LEx.processDOMTemplate("template_business_list2",ret));
	}else{
		LEx.alert(cmd.error);
	}
}

function formateType(val){
	if(val == "A00001"){
		return "审批类";
	}else if(val == "A00001"){
		return "核准类";
	}else if(val == "A00001"){
		return "备案类";
	}
}
function formateSource(val){
	if(val == "1"){
		return "内网申报";
	}else if(val == "2"){
		return "外网申报";
	}
}
function formateUnionState(val){
	if(val == "1"){
		return "联审收件";
	}else{
		return "";
	}
}

//联审状态
function applymaterial(bizId){
	var n_url = LEx.webPath+"admin?m=icity/center/business/unionmateriallist.html&bizId="+bizId;
	LEx.dialog({
		id: "", title: "事项列表",
		url: n_url,
		width: 900, height: 420,
		lock:true,
		button: [
		         {
		        	name: '提交',
		        	callback: function() {
		        		var iframe = this.iframe.contentWindow;
		        		if (!iframe.document.body) {
		        			LEx.alert('内容还没加载完毕呢');
		        			return false;
		        		}		        		
		        	 	if(iframe.checkdata()){
		        	 		LEx.alert("提交成功！");
		        	 	}else{
		        	 		LEx.alert("提交失败！");
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

//业务详情
function showdetail(bizId){
	var n_url = LEx.webPath+"admin?m=icity/center/business/uniondetail.html&bizId="+bizId;
	LEx.dialog({
		id: "", title: "业务详情",
		url: n_url,
		width: 900, height: 420,
		lock:true
	});
}
