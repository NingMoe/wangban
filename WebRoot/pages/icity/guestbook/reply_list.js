var dg1;// 定义一个datagrid对象

// 页面初始化位置
function init() {
	// 查询控件初始化
	initNavigation();

	arrPurview = LEx.BspCmd.mypurview();
	var dept = LEx.userInfo.deptId;
	if (arrPurview.superrole || arrPurview.item_sp_role || arrPurview.item_fb_role || arrPurview.item_jxw_role) {
		dept = BASE_DEPT();
	}
	$("#dept").attr("code", dept);
	if (arrPurview.superrole || arrPurview.item_sp_role || arrPurview.item_fb_role || arrPurview.item_jxw_role) {
		LEx.Control.Depart({
			id : "dept",
			type : "radio",
			rootId : BASE_DEPT()
		});
	} else {
		LEx.Control.Depart({
			id : "dept",
			type : "radio",
			rootId : dept
		});
	}

	// 加载datagrid控件，并将对象映射给datagrid对象
	new LEx.Control.datagrid('datagrid1', function(obj) {

		dg1 = obj; // 对象映射
		dg1.toPageBar(onQuery); // 初始化分页事件
		// 后台交互相关格式化
		dg1.addFormatter([]);

		// 添加事件
		dg1.addEvent({
			onDblClickRow : function(rowIndex, rowData) {
				var n_checks = rowData.CHECKS;
				if (!isNotNull(n_checks)) {// 高度
					n_checks = 0;
				}
				var n_url = LEx.webPath + "admin?m=icity/guestbook/reply_detail.html&id=" + rowData.ID + "&checks=" + n_checks;
				checkDialog({
					title : "详细信息",
					url : n_url,
					width : 900,
					height : 400,
					checks : n_checks
				},rowData);
			}
		});
		onQuery(); // 执行默认的查询方法
	});
}

var subCaption = "所有";

// 查询处理函数
function onQuery() {
	// 定义command对象
	var command = new LEx.Command("app.icity.guestbook.ReplyCmd");
	// 参数设置
	if (subCaption == "审核未通过") {
		command.setParameter("CHECKS@=", 2);
	} else if (subCaption == "未审核") {
		command.setParameter("CHECKS@=", 0);
	} else if (subCaption == "审核通过") {
		command.setParameter("CHECKS@=", 1);
	}
	var title = $("#title").val();
	if (LEx.isNotNull(title)) {
		command.setParameter("TITLE@like", title);
	}

	command.setParameter("dept", $("#dept").attr("code"));
	command.setParameter("start", dg1.start());
	command.setParameter("limit", dg1.limit());
	command.execute("getList");
	if (!command.error) {
		var ret = command.returns;// 读取后台查询结果数据
		dg1.loadData(ret);
	} else {
		LEx.dialog.tips(command.error);// 打印错误信息
	}
}
function changeCss(obj) {
	$('div[class*=ntab][class*=on]').removeClass("on");
	$(obj).addClass("on");
	onQuery();
}

// 时间格式，如果不为空，按照空间格式显示时间，如果为空，显示“—”
function formatDate(val, row) {
	if (isNotNull(val)) {
		return LEx.util.Format.formatDate(val);
	} else {
		return "—";
	}
}

// 是否公开的状态转换
function formatFlag(val, row) {
	if (val == "1") {
		return "是";
	} else if (val == "0") {
		return "否";
	} else {
		return "";
	}
}

// 审核是否通过的状态转换
function formatCheckFlag(val, row) {
	if (val == "1") {
		return "审核通过";
	} else if (val == "2") {
		return "审核不通过";
	} else if (val == "0") {
		return "未审核";
	}
}

// 咨询类型的状态转换
function formatType(val, row) {
	if (val == "0") {
		return "建议";
	} else if (val == "1") {
		return "表扬";
	} else if (val == "2") {
		return "咨询";
	} else if (val == "3") {
		return "投诉";
	} else if (val == "4") {
		return "其他";
	} else if (val == "5") {
		return "意见";
	}
}

// 删除事件
function onDel() {
	var datas = dg1.getChecked();
	var n_command = new LEx.Command("app.icity.guestbook.ReplyCmd");
	var pvalue = [];
	for (var i = 0; i < datas.length; i++) {
		pvalue.push(datas[i].ID);
	}
	n_command.setParameter("ids", pvalue.join(","));
	deleteDialog({
		data : datas,
		command : n_command
	});
}

function checkDialog(obj,rowData) {
	if (!isNotNull(obj.title)) {
		obj.title = "操作";
	}
	if (!isNotNull(obj.width)) {// 宽度
		obj.width = 800;
	}
	if (!isNotNull(obj.height)) {// 高度
		obj.height = 280;
	}
	if (!isNotNull(obj.button)) {// 如果页面未定义button，采用默认
		if (!isNotNull(obj.qmName)) {// 回调执行方法
			obj.qmName = "onQuery()";
		}
		if (!isNotNull(obj.pmName1)) {// 弹出框执行具体操作的方法
			obj.pmName1 = "postData1()";
		}
		if (!isNotNull(obj.pmName2)) {// 弹出框执行具体操作的方法
			obj.pmName2 = "postData2()";
		}
	}
	if(rowData.CHECKS == "0"){
		obj.button = [ {
			name : "通过",
			callback : function() {
				var iframe = this.iframe.contentWindow;
				if (!iframe.document.body) {
					LEx.alert('内容还没加载完毕呢');
					return false;
				}
				if (iframe.eval(obj.pmName1)) {
					eval(obj.qmName);
					return true;
				} else {
					return false;
				}
			},
			focus : true
		}, {
			name : "不通过",
			callback : function() {
				var iframe = this.iframe.contentWindow;
				if (!iframe.document.body) {
					LEx.alert('内容还没加载完毕呢');
					return false;
				}
				if (iframe.eval(obj.pmName2)) {
					eval(obj.qmName);
					return true;
				} else {
					return false;
				}
			}
		}, {
			name : '关闭'
		} ];
	}
	else{
		obj.button = [ {
			name:'关闭'
		}];
	}
	LEx.dialog(obj);
}
