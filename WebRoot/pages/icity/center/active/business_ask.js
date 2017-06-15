var table = null;
function contentHandler(){
	if(_calendar != null){
		_calendar.setup({
			id : "ctime_s"
		});
		_calendar.setup({
			id : "ctime_e"
		});
	}else{
		_calendar = new LEx.Control.Calendar(function(){
			_calendar.setup({
				id : "ctime_s"
			});
			_calendar.setup({
				id : "ctime_e"
			});
		});
	}
	var tabObj = {};
	tabObj.id = "menu";
	LEx.Control.Tab(tabObj);
	// 1.实例化分页table控件：table的id，模版的id
	table = new LEx.Control.Table("ba_list", "template");
	onQuery();
}

$("#submit").click(function() {
	onQuery();
	$("table input[type=checkbox]").attr("checked", false);
});

$("#delete").click(function() {
	onDel();
	$("table input[type=checkbox]").attr("checked", false);
});

function detail(id) {
	var n_url = LEx.webPath+"admin?m=icity/center/active/business_ask_info.html&id="+id;	
	LEx.dialog({
		id : "zxxq",
		title : "咨询详情",
		url : n_url,
		width : 900,
		height : 500,
		lock : true,
		button : [ {
			name : "保存",
			callback : function() {
				var iframe = this.iframe.contentWindow;
				if (!iframe.document.body) {
					LEx.alert('内容还没加载完毕呢');
					return false;
				}
				if (iframe.eval("postData()")) {
					eval("onQuery()");
					return true;
				} else {
					return false;
				}
			},
			focus : true
		}, {
			name : '关闭'
		}]
	});
}

// 分页
function onQuery() {
	if (LEx.isNotNull(LEx.userInfo.userId)) {
		var cmd = new LEx.Command("app.icity.center.active.BusinessAskCmd");
		table.limit(15);
		cmd.setParameter("start", table.start());		
		cmd.setParameter("limit", table.limit());
		cmd.setParameter("g.type@=", "2");
		cmd.setParameter("g.user_Id@=", LEx.userInfo.uid+"");
		var sxname=$("#name").val();
		if (LEx.isNotNull(sxname)) {
			cmd.setParameter("g.SXMC@like", $("#name").val());
		}
		/*获取申办时间1*/
		   var ctime_s = $('#ctime_s').val();
		   if(LEx.isNotNull(ctime_s)){
			   cmd.setParameter("g. WRITE_DATE@>=@Date", ctime_s +" 00:00:00");
		   }
		   /*获取申办时间2*/
		   var ctime_e = $('#ctime_e').val();
		   if(LEx.isNotNull(ctime_e)){
			   cmd.setParameter("g. WRITE_DATE@<=@Date", ctime_e +" 23:59:59");
		   }
		var content=$("#title").val();
		if (LEx.isNotNull(content)) {
			cmd.setParameter("g.content@like", content);
		}
		var ret = cmd.execute("getBusinessAskList");
		if (!ret.error) {
			table.toBody(ret);
			table.toPageBar("divList", "table", ret.total, "onQuery()");
		} else {
			LEx.alert(ret.error);
		}
	} else {
		LEx.alert("当前无用户登录，请先登录！");
	}
}

function selectAll() {
	if ($("#checkall").attr("checked") == "checked") {
		$("table input[type=checkbox]").attr("checked", true);
	} else {
		$("table input[type=checkbox]").attr("checked", false);
	}
}

function select(id){
	if($("#"+id).attr("checked")=="checked"){
		$("#"+id).attr("checked", true);
	}else{
		$("#"+id).attr("checked", false);
	}
}

// 删除事件
function onDel() {
	var command = new LEx.Command("app.icity.center.active.BusinessAskCmd");
	var pvalue = [];
	$('#bodys input:checked').each(function() {
		pvalue.push($(this).attr("id"));
	});
	command.setParameter("ids", pvalue.join(","));
	var ret = command.execute("delete");
	if (!ret.error) {
		onQuery();
		LEx.alert("删除成功！");
	} else {
		LEx.alert(ret.error);
	}
}

function formatDate(obj) {
	return LEx.util.Format.formatDate(obj);
}

function formatIsFlag(obj) {
	if (obj == "1") {
		return "是";
	} else if (obj == "0") {
		return "否";
	} else {
		return "";
	}
}