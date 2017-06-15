var cname="";
$(function(){	
	//	onQuery(页码,栏目名称,显示条数,每条长度（1为20,2为16）)
	onQuery(1,"图片新闻",5,1);

	onQuery(1,"动态要闻",7,1);
	onQuery(1,"改革创新",7,1);
	onQuery(1,"专题专栏",7,1);
		
	onQuery(1,"通知",7,2);
	onQuery(1,"公告",7,2);
	onQuery(1,"公示",7,2);
	
	onQuery(1,"简报",6,1);
	onQuery(1,"通报",6,1);
	onQuery(1,"调查研究",6,1);
	onQuery(1,"会议纪要",6,1);

	onQuery(1,"工作交流",6,1);
	onQuery(1,"外地经验",6,1);
	onQuery(1,"数据分析",6,1);
	
	//onQuery(1,"业务处室动态信息",7,1);
	//onQuery(2,"业务处室动态信息",7,1);
	
	onQuery(1,"内设机构动态信息",6,1);
	
	
	onQuery(1,"县区",5,3);
	onQuery(1,"乡镇",5,3);
	onQuery(1,"社区",5,3);
	onQuery(1,"功能区",5,3);
	
	onQuery(1,"窗口动态",6,2);
    
    tj("day");
    tj("month");
    tj("year");
    tj("all");
    biddingdata();
    
    onQueryTips();
	setTimeout("codefans()",30000);
//办件统计结束
});
//招投标数据统计
function biddingdata(){
	if('{{ConfigInfo.WebRegion}}'=="330901000000"){
		$("#bidding_zs").show();
		$("#bidding").hide();
		$("#biddingdata").attr("src","http://www.zsztb.gov.cn/zsztbweb/Ztbinfo/jyqt.aspx");
	}else if('{{ConfigInfo.WebRegion}}'=="330903000000"){
		$("#bidding_zs").hide();
		$("#bidding").show();
		$("#biddingdata_pt").attr("src","http://www.ptztb.com/list.asp?classid=18");
	}
}
//办件统计函数
function tj(t){

	var options1={};
    var str1 = {};//{data:[]};
    
    var colors = Highcharts.getOptions().colors,
        categoriesToday = ['预审','咨询','受理','即办','承诺','补办','挂起','办结','超期','提前'];
    options1 = {
        chart: { renderTo: t,type: 'column', height: '183' },
        title: { text: false },
        subtitle: { text: false },
        legend:{ enabled: false },
        credits:{ enabled:false },
        exporting: { enabled: false },
        plotOptions: {
            column: {
                dataLabels: {
                    enabled: true,
                    color: [],
                    style: {
                        fontWeight: 'bold',
                        fontSize: '12px'
                    },
                    formatter: function() {
                        return this.y;
                    }
                }
            }
        },
        tooltip: {
            formatter: function() {
                var point = this.point,
                    s = this.x +':<b>'+ this.y + '件';
                return s;
            }
        },
        
        yAxis: {
            title: { text: false }
        },
        xAxis: {
            categories: categoriesToday
        },
        series: []
    };
    
    
  	//办件统计
   	var command = new LEx.Command("app.icity.ServiceCmd");;
   	command.setParameter("type",t);
   	command.setParameter("islogin","true");
   	var json = command.execute("getBjtjCacheZs");
   	var colors = Highcharts.getOptions().colors;
   	if(json==''||json==null){
   		var s = {y: 1,color: colors[1]};
   		str1 = {	data:[{y: 1,color: colors[1]},
   					      {y: 1,color: colors[2]},
   					      {y: 1,color: colors[3]},
   					      {y: 1,color: colors[4]},
   					      {y: 1,color: colors[5]},
   					      {y: 1,color: colors[6]},
   					      {y: 1,color: colors[7]},
   					      {y: 1,color: colors[8]},
   					      {y: 1,color: colors[9]},
   					      {y: 1,color: colors[10]}]
   			    };
   	}else{
   		str1 = {	data:[{y: json[0].INTERNET,color: colors[1]},
   					      {y: json[0].CONSULT,color: colors[2]},
   					      {y: json[0].ACCEPT,color: colors[3]},
   					      {y: json[0].JIBAN,color: colors[4]},
   					      {y: json[0].CHENGNUO,color: colors[5]},
   					      {y: json[0].CORRECTION,color: colors[6]},
   					      {y: json[0].SUSPEND,color: colors[7]},
   					      {y: json[0].COMPLETE,color: colors[8]},
   					      {y: json[0].OVERTIME,color: colors[9]},
   					      {y: json[0].EARLYFINISH,color: colors[10]}]
   			    };
   	}	
  /* 	str1.data.push(json[0].INTERNET);
   	str1.data.push(json[0].CONSULT);
   	str1.data.push(json[0].ACCEPT);
   	str1.data.push(json[0].JIBAN);
   	str1.data.push(json[0].CHENGNUO);
   	str1.data.push(json[0].CORRECTION);
   	str1.data.push(json[0].SUSPEND);
   	str1.data.push(json[0].COMPLETE);
   	str1.data.push(json[0].OVERTIME);
   	str1.data.push(json[0].EARLYFINISH);*/
	 
   	options1.series.push(str1);
    new Highcharts.Chart(options1);	
	
}
function onQuery(start,cnamea,lim,lon){
	
	var cmd = new LEx.Command("app.icity.govservice.GovProjectCmd");
	if(cnamea=="县区"||cnamea=="乡镇"||cnamea=="社区"||cnamea=="功能区"||cnamea=="图片新闻"){
		cmd.setParameter("channel.name@=",'动态要闻');
		cmd.setParameter("isDtyw",'1');
	}else{
	cmd.setParameter("channel.name@=", cnamea);
	}
	
	if(cnamea=="县区"){
		cmd.setParameter("mod(content.rid,1000000)@=",0);
	    cmd.setParameter("mod(content.rid,10000000)@!=",0);
	    cmd.setParameter("content.rid@!=",'330901000000');
	    cmd.setParameter("content.rid not@in",'000901000000,000902000000,000903000000,000904000000,000905000000');
	}else if(cnamea=="乡镇"){
		cmd.setParameter("mod(content.rid,1000)@=",0);
	    cmd.setParameter("mod(content.rid,1000000)@!=",0);
		//cmd.setParameter("ridlike", '3309');
	}else if(cnamea=="功能区"){
	    cmd.setParameter("content.rid@like",'00090');
		cmd.setParameter("mod(content.rid,1000000)@=",0);
		//cmd.setParameter("ridlike", '3309');
	}else if(cnamea=="社区"){
	    cmd.setParameter("mod(content.rid,1000)@!=",0);
		//cmd.setParameter("ridlike", '3309');
	}else{
	   cmd.setParameter("content.rid@=", '{{UserInfo.regionId}}');
	   //cmd.setParameter("content.rid@=", '330901000000');
	}
	
    //默认只显示通过审核的数据
	if(cnamea=="图片新闻"){
	    cmd.setParameter("PicModel",'1');
	}
    cmd.setParameter("CHECKS@=", '1');
    if(cnamea=="动态要闻"||cnamea=="改革创新"||cnamea=="专题专栏"||cnamea=="通知"||cnamea=="公告"||cnamea=="公示"||cnamea=="窗口动态"||cnamea=="县区"||cnamea=="乡镇"||cnamea=="社区"||cnamea=="功能区"||cnamea=="简报"||cnamea=="通报"||cnamea=="会议纪要"){
	    cmd.setParameter("sort"," CTIME DESC");
	}else{
		cmd.setParameter("sort"," ID DESC");
	}
	cmd.setParameter("start", (start-1)*lim);
	cmd.setParameter("limit", lim);
	var ret = cmd.execute("getList");

	if(!ret.error){
		if(cnamea=="动态要闻"){
			$("#动态要闻").html(LEx.processDOMTemplate('MattersListTemplate1',ret));
		}else if(cnamea=="图片新闻"){
			$('#tpc').html(LEx.processDOMTemplate('MattersListTemplatep',ret));
			$('#p1').html(LEx.processDOMTemplate('MattersListTemplatep1',ret));
		}else if(cnamea=="业务处室动态信息"&&start=="2"){
			$('#业务处室动态信息2').html(LEx.processDOMTemplate('MattersListTemplate1_ywcs',ret));
		}else if(cnamea=="业务处室动态信息"&&start=="1"){
			$('#业务处室动态信息').html(LEx.processDOMTemplate('MattersListTemplate1_ywcs',ret));
		}else if(cnamea=="窗口动态"){
			$('#窗口动态').html(LEx.processDOMTemplate('MattersListTemplate1_ckdt',ret));
		}else{
			if(lon==1){
     		$("#"+cnamea).html(LEx.processDOMTemplate('MattersListTemplate1',ret));
			}else if(lon==2){
	     		$("#"+cnamea).html(LEx.processDOMTemplate('MattersListTemplate2',ret));
			}else{
	     		$("#"+cnamea).html(LEx.processDOMTemplate('MattersListTemplate3',ret));
			}
		}	
	}else{
		LEx.alert(ret.error);
	}
}

function onQueryTips() {
	var cmd = new LEx.Command("app.icity.govservice.GovProjectCmd");
	cmd.setParameter("channel.name@=", '廉政格言');
	cmd.setParameter("content.rid@=", '{{ConfigInfo.WebRegion}}');
	cmd.setParameter("start", 0);
	cmd.setParameter("limit", 10000);
	var ret = cmd.execute("getList4Lzgy");
	if (!ret.error) {
		$("#tzgg").html(LEx.processDOMTemplate('tips', ret));
	}
}

function formateDate(val){
	if(val==null){
		return "--";
	}else{
		var dt = new Date(val.time);
		return dt.getMonth()+1+"-"+dt.getDate();
	}
}

function getList(str){
	window.location.href="{{cp}}iportal/base-2?CNAME=" + str;
}

function getMore(st) {   
	 $("#"+st+" li").each(function () {                 
		 if ($(this).prop('outerHTML').indexOf("active") >= 0) {                     
		 	if ($(this).attr("tid") != undefined) {                        
		 		getList($(this).attr("tid"));                     
		 	}                
		 }            
	 	});        
	 } 

function getContent(str) {             
	window.open("base-2?id=" + str);       
}

function hideLzgy() {
	$('#lzgy').hide();
}

function codefans(){
	var box=document.getElementById("lzgy");
	box.style.display="none"; 
}