var cname="";
var region_code = getSecurityValue("WebRegion");
$(function(){	
	onQuery(1,"信息公开目录",3,1);
	onQuery(1,"信息公开指南",3,1);
	onQuery(1,"依申请公开",3,1);
	onQuery(1,"公开年度报告",3,1);
	onQuery(1,"规划计划",3,1);
	onQuery(1,"财政信息",3,1);
	onQuery(1,"人事信息",3,1);
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
	switch(i){
	case 0:
		$("#xxgkml").show();
		$("#xxgkzn").hide();
		$("#ysqgk").hide();
		$("#gkndbg").hide();
		$("#ghjh").hide();
		$("#czhxx").hide();
		$("#rsxx").hide();
		$("#position").html("信息公开目录");
		onQuery(1,"信息公开目录",10,1);
		break;
	case 1:
		$("#xxgkml").hide();
		$("#xxgkzn").show();
		$("#ysqgk").hide();
		$("#gkndbg").hide();
		$("#ghjh").hide();
		$("#czhxx").hide();
		$("#rsxx").hide();
		$("#position").html("信息公开指南");
		onQuery(1,"信息公开指南",10,1);
		break;
	case 2:
		$("#xxgkml").hide();
		$("#xxgkzn").hide();
		$("#ysqgk").show();
		$("#gkndbg").hide();
		$("#ghjh").hide();
		$("#czhxx").hide();
		$("#rsxx").hide();
		$("#position").html("依申请公开");
		onQuery(1,"依申请公开",10,1);
		break;
	case 3:
		$("#xxgkml").hide();
		$("#xxgkzn").hide();
		$("#ysqgk").hide();
		$("#gkndbg").show();
		$("#ghjh").hide();
		$("#czhxx").hide();
		$("#rsxx").hide();
		$("#position").html("公开年度报告");
		onQuery(1,"公开年度报告",10,1);
		break;
	case 4:
		$("#xxgkml").hide();
		$("#xxgkzn").hide();
		$("#ysqgk").hide();
		$("#gkndbg").hide();
		$("#ghjh").show();
		$("#czhxx").hide();
		$("#rsxx").hide();
		$("#position").html("规划计划");
		onQuery(1,"规划计划",10,1);
		break;
	case 5:
		$("#xxgkml").hide();
		$("#xxgkzn").hide();
		$("#ysqgk").hide();
		$("#gkndbg").hide();
		$("#ghjh").hide();
		$("#czhxx").show();
		$("#rsxx").hide();
		$("#position").html("财政信息");
		onQuery(1,"财政信息",10,1);
		break;
	case 6:
		$("#xxgkml").hide();
		$("#xxgkzn").hide();
		$("#ysqgk").hide();
		$("#gkndbg").hide();
		$("#ghjh").hide();
		$("#czhxx").hide();
		$("#rsxx").show();
		$("#position").html("人事信息");
		onQuery(1,"人事信息",10,1);
		break;
	}
}

function getMore(str){
	window.open("contentList?cname="+str);
}