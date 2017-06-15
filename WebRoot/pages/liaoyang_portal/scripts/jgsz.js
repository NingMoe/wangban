var cname="";
var region_code = getSecurityValue("WebRegion");
$(function(){	
	onQuery(1,"局领导",3,1);
	onQuery(1,"内设机构",3,1);
	onQuery(1,"直属单位",3,1);
	
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
		$("#jld").show();
		$("#nsjg").hide();
		$("#zsdw").hide();
		$("#position").html("局领导");
		onQuery(1,"局领导",10,1);
	}else if(i==1){
		$("#jld").hide();
		$("#nsjg").show();
		$("#zsdw").hide();
		$("#position").html("内设机构");
		onQuery(1,"内设机构",10,1);
	}else{
		$("#jld").hide();
		$("#nsjg").hide();
		$("#zsdw").show();
		$("#position").html("直属单位");
		onQuery(1,"直属单位",10,1);
	}
}

function getMore(str){
	window.open("contentList?cname="+str);
}