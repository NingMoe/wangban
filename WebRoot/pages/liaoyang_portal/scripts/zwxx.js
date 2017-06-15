var cname="";
var region_code = getSecurityValue("WebRegion");
$(function(){	
	onQuery(1,"图文信息",3,1);
	onQuery(1,"工作动态",3,1);
	onQuery(1,"党建信息",3,1);
	
});

//onQuery(页码,栏目名称,显示条数,每条长度)
function onQuery(start,cnamea,lim,lon){
	var cmd = new LEx.Command("app.icity.govservice.GovProjectCmd");
	cmd.setParameter("channel.name@=", cnamea);
	cmd.setParameter("content.rid@=", region_code);
    cmd.setParameter("CHECKS@=", '1');
    cmd.setParameter("workTime", '1');
    cmd.setParameter("sort"," CTIME DESC");
	//cmd.setParameter("sort"," ID DESC");
	cmd.setParameter("start", (start-1)*lim);
	cmd.setParameter("limit", lim);
	var ret = cmd.execute("getList");
	if(!ret.error){
		$("#"+cnamea).html(LEx.processDOMTemplate('MattersListTemplate1',ret));
	}else{
		LEx.alert(ret.error);
	}
}

function show(i){
	if(i==0){
		$("#tpxw").show();
		$("#gzdt").hide();
		$("#djxx").hide();
		$("#position").html("图文信息");
		onQuery(1,"图文信息",10,1);
	}else if(i==1){
		$("#tpxw").hide();
		$("#gzdt").show();
		$("#djxx").hide();
		$("#position").html("工作动态");
		onQuery(1,"工作动态",10,1);
	}else{
		$("#tpxw").hide();
		$("#gzdt").hide();
		$("#djxx").show();
		$("#position").html("党建信息");
		onQuery(1,"党建信息",10,1);
	}
}

function getMore(str){
	window.open("contentList?cname="+str);
}