var table = null;
var ontab = null
function contentHandler(){
	var tabObj = {};
	tabObj.id = "menu";
	LEx.Control.Tab(tabObj);
	if(ontab==null){
		//1.实例化分页table控件：table的id，模版的id
		table = new LEx.Control.Table("bem_list","template1");
		onQuery1(); 
	}
	
	$(".tab").click(function(){
		ontab = $(this).attr("tag");
		if(ontab=="evaluate_mine"){
			table = new LEx.Control.Table("bem_list","template1");
			onQuery1(); 
		}else if(ontab=="evaluate_tome"){
			table = new LEx.Control.Table("bet_list","template2");
			onQuery2(); 
		}
	});
	
}

//分页
function onQuery1(){
		var cmd = new LEx.Command("app.icity.center.active.BusinessEvaluateCmd");
		cmd.setParameter("start", table.start());
		cmd.setParameter("limit", table.limit());
		//cmd.setParameter("username",);     用户名称去不过来，暂不处理
		var ret =  cmd.execute("getMyEvaluateList");
		if(!ret.error){		
			//LEx.alert(LEx.encode(ret.data));
			//2.将数据传递给table对象
			table.toBody(ret);
			//3.加载分页控件：分页div的id，Table的实例化对象名称（字符串）,总数量，查询方法（字符串）
			table.toPageBar("bemdivList","table",ret.total,"onQuery1()");
			/* $("#divList").html(LEx.processDOMTemplate("template",ProjectItem)); */
		}else{
			LEx.alert(ret.error);
		}
}



function onQuery2(){
	var cmd = new LEx.Command("app.icity.center.active.BusinessEvaluateCmd");
	cmd.setParameter("start", table.start());
	table.limit(15)
	cmd.setParameter("limit", table.limit());
	var ret =  cmd.execute("getEvaluateToMeList");
	if(!ret.error){		
		//LEx.alert(LEx.encode(ret.data));
		//2.将数据传递给table对象
		table.toBody(ret);
		//3.加载分页控件：分页div的id，Table的实例化对象名称（字符串）,总数量，查询方法（字符串）
		table.toPageBar("betdivList","table",ret.total,"onQuery2()");
		/* $("#divList").html(LEx.processDOMTemplate("template",ProjectItem)); */
	}else{
		LEx.alert(ret.error);
	}
}


function formatDate(obj){
	return LEx.util.Format.formatDate(obj);
}



function formatClassFlag(obj){
	if(obj=="1"){
		return "好评";
	}else if(obj=="1"){
		return "中评";
	}else if(obj=="-1"){
		return "差评";
	}else{
		return "未评价";
	}
}


