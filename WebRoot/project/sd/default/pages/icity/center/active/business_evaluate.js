var bltable = null;
var isCurrentPage=false;//是否查询当前页
var currentStart=0;
var total=0;
var evaluate_status = "0";//0待评价 1 已评价
$.fn.raty.defaults.path = '{{PageContext.JsPath}}raty_master/lib/images/';
function contentHandler(){
	//1.实例化分页bltable控件：bltable的id，模版的id
	if(evaluate_status=="1"){
		bltable = new LEx.Control.Table("div_business_list","template_have_evaluate_list");
	}else{
		bltable = new LEx.Control.Table("div_business_list","template_evaluate_list");
	}
	bltable.limit(8);
	onQuery();
}
//分页
function onQuery(){
	if(!isCurrentPage){
		currentStart = bltable.start();//记录本次查询的页
	}
	var cmd = new LEx.Command("app.icity.project.ProjectIndexCmd");
	if(isCurrentPage&&total!=currentStart+1){//删除成功，星级评价成功返回列表，需要查询当前页
		cmd.setParameter("start", currentStart);
	}else{
		cmd.setParameter("start", bltable.start());
	}
	cmd.setParameter("limit", bltable.limit());
	cmd.setParameter("ucid",""+LEx.userInfo.uid);
	var sbxmmc = $('#sbxmmc').val();
	if(LEx.isNotNull(sbxmmc)){
		cmd.setParameter("SBXMMC@like",sbxmmc);
   }	
	if(LEx.isNotNull(LEx.urldata.SBLSH)){
		cmd.setParameter("SBLSH@=",LEx.urldata.SBLSH);
		LEx.urldata.SBLSH = null;		
   }
   var applytime_s = $('#APPLYTIME_S').val();
   if(LEx.isNotNull(applytime_s)){
	   cmd.setParameter("SBSJ@>=@Date",applytime_s+" 00:00:00");
   }
   var applytime_e = $('#APPLYTIME_E').val();
   if(LEx.isNotNull(applytime_s)&&LEx.isNotNull(applytime_e)){
	   if(applytime_s>applytime_e){
		   LEx.alert("截止日期不允许小于开始时间");
	   }  
   }
   if(LEx.isNotNull(applytime_e)){
	   cmd.setParameter("SBSJ@<=@Date",applytime_e+" 23:59:59");
   }
   cmd.setParameter("EVALUATE_STATUS",evaluate_status);//0 待评价 1已评价
   cmd.setParameter("STATE@IN","99");//准予许可的件
   if(evaluate_status=="1"){
	   cmd.setParameter("sort"," l.CREATOR_DATE DESC");
   }else{
	   cmd.setParameter("sort"," SBSJ DESC"); 
   }
	var ret =  cmd.execute("getBusinessIndexList");
	if(!ret.error){
		isCurrentPage=false;
		total=ret.total;
		//2.将数据传递给bltable对象
		bltable.toBody(ret);
		//3.加载分页控件：分页div的id，bltable的实例化对象名称（字符串）,总数量，查询方法（字符串）
		bltable.toPageBar("epager","bltable",ret.total,"onQuery()");
		if(evaluate_status=="1"){//已评价
			$("#div_business_list").html(LEx.processDOMTemplate("template_have_evaluate_list",ret));	
		}else{
			$("#div_business_list").html(LEx.processDOMTemplate("template_evaluate_list",ret));	
		}
		$(".NeedFormatState").each(function(){
			var status=$(this).attr("state");
			var bState = LEx.icityBusiness.formatStatus(status);
			$(this).html(bState);
		});
	}else{
		LEx.alert(ret.error);
	}
	$(".star_level_data").raty({ 
		readOnly: true,
		score:  function() {
			return $(this).attr('data-score');
		} ,
		hints: ['非常不满意', '不满意', '基本满意', '满意  ', '非常满意'],
	});
}
function changeState(state){
	evaluate_status = state;
	contentHandler();
}
/**
 * 业务详情
 */
function detail(id){
	var n_url=LEx.webPath+"icity/center/business/detail?BID="+id;
	operationDialog({title: "业务详情",url: n_url,width:920,height: 500});
}

//定义一个当前事项评价需要保存的数据
var SBLSH="";// 受理编号
function evaluateShow(sblsh,sbxmmc){
	SBLSH=sblsh;// 受理编号
	$("#bjmc").html(sbxmmc);//办件名称
	$("#evaluate").show();
	$("#divcontent").hide();
	$(".evaluatelabel").show();
	$("#businessList").hide();
	$(".level_info").html("");
	$('.star').raty({
		  click: function(score, evt) {
			var id = this.id;
			var level_5 = "<label class='text-success'>非常满意 </label>"; 
			var level_4 = "<label class='text-primary'>满意</label>";
			var level_3 ="<label class='text-info'>基本满意</label>";
			var level_2 ="<label class='text-warning'>不满意</label>";
			var level_1 = "<label class='text-danger'>非常不满意 </label>";
			if(score==5){
				$("#"+id+"_info").html(level_5);
			}else if(score==4){
				$("#"+id+"_info").html(level_4);
			}else if(score==3){
				$("#"+id+"_info").html(level_3);
			}else if(score==2){
				$("#"+id+"_info").html(level_2);
			}else if(score==1){
				$("#"+id+"_info").html(level_1);
			}
		  },
		  hints: ['非常不满意', '不满意', '基本满意', '满意  ', '非常满意']
		});
}
function hideshow(){
	$("#evaluate").hide();
	$("#businessList").show();
}
//查看 评价
function lookEvaluate(sbxmmc,star,quality,time,major,content){
	$("#bjmc").html(sbxmmc);//办件名称
	$("#divcontent").html(content);//评价内容
	$("#divcontent").show();
	$("#star_level").attr('data-score',star);
	$("#quality_star_level").attr('data-score',quality);
	$("#major_star_level").attr('data-score',time);
	$("#time_star_level").attr('data-score',major);
	$("#evaluate").show();
	$("#businessList").hide();
	$(".evaluatelabel").hide();
	$(".star").raty({ 
		readOnly: true,
		score:  function() {
			var id =this.id;
			var score = $(this).attr('data-score');
			var level_5 = "<label class='text-success'>非常满意 </label>"; 
			var level_4 = "<label class='text-primary'>满意</label>";
			var level_3 ="<label class='text-info'>基本满意</label>";
			var level_2 ="<label class='text-warning'>不满意</label>";
			var level_1 = "<label class='text-danger'>非常不满意 </label>";
			if(score>4){
				$("#"+id+"_info").html(level_5);
			}else if(score>3){
				$("#"+id+"_info").html(level_4);
			}else if(score>2){
				$("#"+id+"_info").html(level_3);
			}else if(score>1){
				$("#"+id+"_info").html(level_2);
			}else if(score>0){
				$("#"+id+"_info").html(level_1);
			}else{
				$("#"+id+"_info").html("");
			}
			return score;
		} ,
		hints: ['非常不满意', '不满意', '基本满意', '满意  ', '非常满意']
	});
}
//评价件
function evaluateProject(){
	var STAR_LEVAL = $('#star_level').raty('score');
	var QUALITY_STAR_LEVEL = $('#quality_star_level').raty('score');
	var TIME_STAR_LEVEL = $('#time_star_level').raty('score');
	var MAJOR_STAR_LEVEL = $('#major_star_level').raty('score');
	if(STAR_LEVAL==undefined){
		LEx.alert('未进行办件总体评价！');
		return;
	}
	if(QUALITY_STAR_LEVEL==undefined){
		LEx.alert('未进行服务质量评价！');
		return;	
	}
	if(TIME_STAR_LEVEL==undefined){
		LEx.alert('未进行办件时间评价！');
		return;
	}
	if(MAJOR_STAR_LEVEL==undefined){
		LEx.alert('未进行业务专业评价！');
		return;
	}
	var EVALUATE_CONTENT = $("#EVALUATE_CONTENT").val();
	if (EVALUATE_CONTENT == undefined||EVALUATE_CONTENT=="") {
		LEx.alert('未填写评价内容！');
		return;
	}
	var cmd = new LEx.Command(
			"app.icity.interactive.satisfaction.SatisfactionEvaluationCmd");
	cmd.setParameter("type", "1");
	cmd.setParameter("STAR_LEVEL", STAR_LEVAL);
	cmd.setParameter("QUALITY_STAR_LEVEL", QUALITY_STAR_LEVEL);
	cmd.setParameter("TIME_STAR_LEVEL", TIME_STAR_LEVEL);
	cmd.setParameter("MAJOR_STAR_LEVEL", MAJOR_STAR_LEVEL);
	cmd.setParameter("EVALUATE_CONTENT", EVALUATE_CONTENT);
	cmd.setParameter("SBLSH", SBLSH);
	cmd.setParameter("EVALUATE_TYPE", "1");//办件评价
	var ret = cmd.execute("insertNewEvaluation");
	// 返回操作
	if (ret.state=='1') {
		isCurrentPage=true;
		onQuery();
		LEx.alert('评价成功！');
		hideshow();
	} else {
		LEx.alert("评价失败！"+ret.message);
	}
}
function formatDate(obj){
	return LEx.util.Format.formatDate(obj);
}
