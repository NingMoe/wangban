var cname="";
var region_code=getSecurityValue("WebRegion");
$(function(){	
	onQuery(1,"图文信息",5,1);

	onQuery(1,"工作动态",5,2);
	onQuery(1,"公示公告",5,2);
	onQueryZcfg(1,"政策法规",6,1);
	onQuery(1,"经验交流",6,1);
	
	//办件公示
	getBusinessList();
	
	//办件统计
	getAcceptQuantity();
	
});
function onQueryZcfg(start,cnamea,lim,lon){
	cnamea = "法律法规";	
	var cmd = new LEx.Command("app.icity.govservice.GovProjectCmd");		
	cmd.setParameter("content.rid@=", region_code);
    cmd.setParameter("CHECKS@=", '1');
    cmd.setParameter("workTime", '1');
    cmd.setParameter("sort"," CTIME DESC");
	cmd.setParameter("start", (start-1)*lim);
	cmd.setParameter("limit", lim);
	cmd.setParameter("channel.name@=", cnamea);	
	var retflfg = cmd.execute("getList");
	cnamea = "政策文件";
	var cmd1 = new LEx.Command("app.icity.govservice.GovProjectCmd");		
	cmd1.setParameter("content.rid@=", region_code);
    cmd1.setParameter("CHECKS@=", '1');
    cmd1.setParameter("workTime", '1');
    cmd1.setParameter("sort"," CTIME DESC");
	cmd1.setParameter("start", (start-1)*lim);
	cmd1.setParameter("limit", lim);
	cmd1.setParameter("channel.name@=", cnamea);	
	var retzcwj = cmd1.execute("getList");
	var retflfgLen = retflfg.total;
	var retzcwjLen = retzcwj.total;
	var ret={};
	if(retflfg.state=="1"){
		ret.state="1";
		var total = 0;
		var data=[];
		if(retflfgLen>0){
			if(retflfgLen>3){
				for(var i=0;i<3;i++){
					data.push(retflfg.data[i]);
				}
			}else{
				for(var i=0;i<retflfgLen;i++){
					data.push(retflfg.data[i]);
				}
			}			
		}
		if(retzcwj.state=="1"){			
			if(retzcwjLen>0){
				if(retzcwjLen>3){
					for(var i=0;i<3;i++){
						data.push(retzcwj.data[i]);
					}
				}else{
					for(var i=0;i<retzcwjLen;i++){
						data.push(retzcwj.data[i]);
					}
				}	
			}			
		}
		ret.data = data;
		$("#政策法规").html(LEx.processDOMTemplate('MattersListTemplate1',ret));
	}
}
//onQuery(页码,栏目名称,显示条数,每条长度)
function onQuery(start,cnamea,lim,lon){
	var cmd = new LEx.Command("app.icity.govservice.GovProjectCmd");
	if(cnamea=="图文信息"){
		cmd.setParameter("channel.name@=",'图文信息');
		cmd.setParameter("isDtyw",'1');
		cmd.setParameter("PicModel",'1');
	}else{
		cmd.setParameter("channel.name@=", cnamea);
	}
	cmd.setParameter("content.rid@=", region_code);
    cmd.setParameter("CHECKS@=", '1');
    cmd.setParameter("workTime", '1');
    cmd.setParameter("sort"," CTIME DESC");
	cmd.setParameter("start", (start-1)*lim);
	cmd.setParameter("limit", lim);
	var ret = cmd.execute("getList");
	if(!ret.error){
		if(cnamea=="图文信息"){
			$('#tpc').html(LEx.processDOMTemplate('MattersListTemplatep',ret));
			$('#p1').html(LEx.processDOMTemplate('MattersListTemplatep1',ret));
			
			var len = ret.data.length;
			if(len>0){
				for(var i =0;i<len;i++){
					if(i==0){
						$("#head_title").append("<h1 class='text-center'><a href='contentDetail?id="+ret.data[i].ID+"' class='text-danger' target='_blank'><strong>"+ret.data[i].NAME+"</strong></a></h1>");
					}
				}
			}
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

function getBusinessList(){
	var cmd = new LEx.Command("app.icity.project.ProjectBusinessStatCmd");
	var ret = cmd.execute("getBusinessList");
	if (!ret.error) {
		$('#businessList').html(LEx.processDOMTemplate('businessTemplate', ret));
	} else {
		LEx.alert(ret.error);
	}
}

function getAcceptQuantity(){
	var cmd = new LEx.Command("app.icity.ServiceCmd");
	cmd.setParameter("region_code", region_code);
	var ret = cmd.execute("getAcceptQuantity");
	if (!ret.error) {
		$("#accept").html(ret.data[0].accept);
		$("#completed").html(ret.data[0].completed);
		$("#handle").html(ret.data[0].handle);
		$("#preMonthAccept").html(ret.data[0].preMonthAccept);
		$("#preMonthCompleted").html(ret.data[0].preMonthCompleted);
		$("#monthAccept").html(ret.data[0].monthAccept);
		$("#monthCompleted").html(ret.data[0].monthCompleted);
		$("#yesterdayAccept").html(ret.data[0].yesterdayAccept);
		$("#yesterdayCompleted").html(ret.data[0].yesterdayCompleted);
	} else {
		LEx.alert(ret.error);
	}
}

function formateDate(val){
	if(val==null){
		return "--";
	}else{
		var dt = new Date(val.time);
		return dt.getFullYear()+dt.getMonth()+1+"-"+dt.getDate();
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

function parseLength(val,len) {
	if (LEx.isNotNull(val)&&val.length>len) {
		return val.substring(0,len)+"...";
	} else {
		return val;
	}
}

function getMore(str){
	window.open("contentList?cname="+str);
}

function queryBusiness(){
	window.open("http://60.18.184.20/icity/icity/result");
}