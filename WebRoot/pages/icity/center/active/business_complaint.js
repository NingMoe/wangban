var table = null;
function contentHandler(){
	if(_calendar != null){
		_calendar.setup({id:"ctime_s"});
		_calendar.setup({id:"ctime_e"});
	}else{
		_calendar= new LEx.Control.Calendar(function(){
			_calendar.setup({id:"ctime_s"});
			_calendar.setup({id:"ctime_e"});
		});
	}
	var tabObj = {};
	tabObj.id = "menu";
	LEx.Control.Tab(tabObj);
	//1.实例化分页table控件：table的id，模版的id
	table = new LEx.Control.Table("bc_list","template");
	onQuery(); 
}

$("#submit").click(function(){
	 onQuery();
	 $("table input[type=checkbox]").attr("checked",false);
});

$("#delete").click(function(){
	 onDel();
	 $("table input[type=checkbox]").attr("checked",false);
});

function detail(id){
	var n_url = LEx.webPath+"admin?m=icity/center/active/business_complaint_info.html&id="+id;	
	LEx.dialog({
		id:'tsxxxq',
		title:"投诉信息详情",
		url:n_url,
		width:900,
		height:500,
		lock:true,
		button:[{
		    name: "保存",
		    callback: function() {
		        var iframe = this.iframe.contentWindow;
		        if (!iframe.document.body) {
		            LEx.alert('内容还没加载完毕呢');
		            return false;
		        }
		        if(iframe.eval("postData()")){
		        	eval("onQuery()");
		        	return true;
		        }else{
		        	return false;
		        }
		    },
		    focus: true
	   },
	   {
		   name: '关闭'
	   }]
	});
}



//分页
function onQuery(){
	if(LEx.isNotNull(LEx.userInfo.userId)){
		var cmd = new LEx.Command("app.icity.center.active.BusinessComplaintCmd");
		cmd.setParameter("start", table.start());
		cmd.setParameter("limit", table.limit());
		cmd.setParameter("g.type", "3");
		cmd.setParameter("user_id",LEx.userInfo.uid+"");
		var id=$("#id").val();
		if (LEx.isNotNull(id)) {
			cmd.setParameter("g.id@=", id);
		}
		var content=$("#title").val();
		if (LEx.isNotNull(content)) {
			cmd.setParameter("g.content@like", content);
		}
		var ctime_s=$("#ctime_s").val();
		if (LEx.isNotNull(ctime_s)) {
			cmd.setParameter("g.write_date@>@Date", ctime_s);
		}
		var ctime_e=$("#ctime_e").val();
		if (LEx.isNotNull(ctime_e)) {
			cmd.setParameter("g.write_date@<@Date", ctime_e);
		}				
		var ret =  cmd.execute("getBusinessComplaintList");
		if(!ret.error){		//alert(LEx.encode(ret));
			//2.将数据传递给table对象
			table.toBody(ret);
			//3.加载分页控件：分页div的id，Table的实例化对象名称（字符串）,总数量，查询方法（字符串）
			table.toPageBar("divList","table",ret.total,"onQuery()");
			/* $("#divList").html(LEx.processDOMTemplate("template",ProjectItem)); */
		}else{
			LEx.alert(ret.error);
		}	
	}else{
		LEx.alert("当前无用户登录，请先登录！");
	}
}

function selectAll(){
	 if($("#checkall").attr("checked")=="checked"){
		 $("table input[type=checkbox]").attr("checked",true);
	 }else{
		 $("table input[type=checkbox]").attr("checked",false);
	 }
}

function select(id){
	if($("#"+id).attr("checked")=="checked"){
		$("#"+id).attr("checked", true);
	}else{
		$("#"+id).attr("checked", false);
	}
}

//删除事件
function onDel(){
	var command = new LEx.Command("app.icity.center.active.BusinessComplaintCmd");
	var pvalue = [];
	$('#bodys input:checked').each(function(){ 
		pvalue.push($(this).attr("id"));
	});
	command.setParameter("ids",pvalue.join(","));
	var ret =  command.execute("delete");
	if(!ret.error){	
		onQuery();
		LEx.alert("删除成功！");
	}else{
		LEx.alert(ret.error);
	}
}


function formatDate(obj){
	return LEx.util.Format.formatDate(obj);
}


function FormatIsFlag(obj){
	if(obj=="1"){
		return "是";
	}else if(obj=="0"){
		return "否";
	}else{
		return "";
	}
}


