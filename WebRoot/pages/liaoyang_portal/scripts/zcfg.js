var cname="";
var region_code = getSecurityValue("WebRegion");
$(function(){	
	onQuery(1,"法律法规",3,1);
	onQuery(1,"政策文件",3,1);
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
		$("#flfg").show();
		$("#zcwj").hide();
		$("#position").html("法律法规");
		onQuery(1,"法律法规",10,1);
	}else if(i==1){
		$("#flfg").hide();
		$("#zcwj").show();
		$("#position").html("政策文件");
		onQuery(1,"政策文件",10,1);
	}
}

function getMore(str){
	window.open("contentList?cname="+str);
}