var bltable = null;
function contentHandler(){
	//1.实例化分页bltable控件：bltable的id，模版的id
	bltable = new LEx.Control.Table("div_business_list","template_business_list");
	bltable.limit(8);
	onQuery();
}
//分页
function onQuery(){
	var cmd = new LEx.Command("app.icity.project.ProjectIndexCmd");
	cmd.setParameter("start", bltable.start());
	//默认显示 最近一周办结的件和正在办理的件
	//处理最近一周的时间
	var now = new Date();
	var m_date = new Date(now.getTime() - 7 * 24 * 3600 * 1000);
	var m_year = m_date.getFullYear();
	var m_month = m_date.getMonth() + 1;
	if(m_month<10){
		m_month="0"+m_month;
	}
	var m_day = m_date.getDate();
	if(m_day<10){
		m_day = "0"+m_day;
	}
	var date = new Date();
	var month = date.getMonth() + 1;
	if(month<10){
		month="0"+month;
	}
	var day = date.getDate();
	if(day<10){
		day="0"+day;
	}
	var bjsj_s = m_year+"-"+m_month+"-"+m_day+" 00:00:00";
	cmd.setParameter("bjsj_s", bjsj_s);//办结开始时间
	cmd.setParameter("limit", bltable.limit());
	cmd.setParameter("t.ucid",""+LEx.userInfo.uid);
	var sbxmmc = $('#sbxmmc').val();
	if(LEx.isNotNull(sbxmmc)){
		cmd.setParameter("t.SBXMMC@like",sbxmmc);
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
   var business_status = $('#select_status option:selected').attr("code");
   if(business_status== undefined||business_status=="all"){//查询全部
	   business_status ="02,13,96,97,98,99";
   }
   if(LEx.isNotNull(business_status)){
	   cmd.setParameter("STATE@IN",business_status);
   }
   	cmd.setParameter("sort"," SBSJ DESC");
	var ret =  cmd.execute("getBusinessComplaint");
	if(!ret.error){
		if(ret.total>0){
			//2.将数据传递给bltable对象
			bltable.toBody(ret);
			//3.加载分页控件：分页div的id，bltable的实例化对象名称（字符串）,总数量，查询方法（字符串）
			bltable.toPageBar("epager","bltable",ret.total,"onQuery()");
			$("#div_business_list").html(LEx.processDOMTemplate("template_business_list",ret));
			$(".NeedFormatState").each(function(){
				var status=$(this).attr("state");
				var bState = LEx.icityBusiness.formatStatus(status);
				$(this).html(bState);
			});
			$("#project-none").hide();
		}else{
			$("#project-none").show();
		}
	}else{
		LEx.alert(ret.error);
	}
}
/**
 * 业务详情
 */
function detail(id){
	var n_url=LEx.webPath+"icity/center/business/detail?BID="+id;
	operationDialog({title: "业务详情",url: n_url,width:920,height: 500});
}
function complain(a,b,c,d){
	document.location="{{PageContext.ContextPath}}icity/complain?sblsh="+a+"&sbxmmc="+encodeURIComponent(b)+"&sxid="+c+"&sxmc="+encodeURIComponent(d);
}
function formatDate(obj){
	return LEx.util.Format.formatDate(obj);
}
