var table = null;
function contentHandler(){
	if(_calendar != null){
		_calendar.setup({id:"pubtime_s"});
		_calendar.setup({id:"pubtime_e"});
	}else{
		_calendar= new LEx.Control.Calendar(function(){
			_calendar.setup({id:"pubtime_s"});
			_calendar.setup({id:"pubtime_e"});
		});
	}
	var tabObj = {};
	tabObj.id = "menu";
	LEx.Control.Tab(tabObj);
	//1.实例化分页table控件：table的id，模版的id
	table = new LEx.Control.Table("bw_list","template");
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
	var n_url = LEx.webPath+"admin?m=icity/center/active/business_wiki_info.html&id="+id;
	LEx.dialog({
		id:'',
		title:"咨询详情",
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
	
	
	/*Dialog({title: "办事攻略信息详情", url: n_url,width:900});*/
}



//分页
function onQuery(){
	if(LEx.isNotNull(LEx.userInfo.userId)){
		var cmd = new LEx.Command("app.icity.wiki.BusinessWikiCmd");
		cmd.setParameter("start", table.start());
		cmd.setParameter("limit", table.limit());
		cmd.setParameter("WT.USERID", LEx.userInfo.uid.toString);
		if(!($("#pname").val()==""||$("#pname").val()==null)){
			cmd.setParameter("PI.NAME@LIKE", $("#pname").val());
		}
		if(!($("#name").val()==""||$("#name").val()==null)){
			cmd.setParameter("WT.NAME@LIKE", $("#name").val());
		}
		if(!($("#pubtime_s").val()==""||$("#pubtime_s").val()==null)){
			cmd.setParameter("WT.CREATETIME@>=@Date",$("#pubtime_s").val()+" 00:00:00");
		}
		if(!($("#pubtime_e").val()==""||$("#pubtime_e").val()==null)){
			cmd.setParameter("WT.CREATETIME@<=@Date",$("#pubtime_e").val()+" 23:59:59");
		}
		var ret =  cmd.execute("getBusinessWikiList");
		if(!ret.error){		
			//LEx.alert(LEx.encode(ret));
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



//删除事件
function onDel(){
	var command = new LEx.Command("app.icity.wiki.BusinessWikiCmd");
	var pvalue = [];
	$('#bodys input:checked').each(function(){ 
		pvalue.push($(this).attr("tag"));
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


function formatIsFlag(obj){
	if(obj=="1"){
		return "是";
	}else if(obj=="0"){
		return "否";
	}else{
		return " ";
	}
}

function formatCheck(obj){
	if(obj=="1"){
		return "通过";
	}else if(obj=="2"){
		return "不通过";
	}else{
		return "未审核 ";
	}
}


