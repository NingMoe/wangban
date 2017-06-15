/**
 * <p>
 * 与satisfaction.html用户满意度评价网页相对应的js文件
 * </p>
 * 
 * @time 2015-07-28
 * @author chendeming
 */

$(function() {
	
	//初始化信息
	LEx.form.init();
	
	//获取受理部门
	//getDeptName();

	$("button[op]").click(function(event) {
		var op = $(this).attr("op");
		if (op == "submit") {
			submit();
		} else {
			window.close();
		}
	});
});

function queryDetail() {
	
	if ($("#SBLSH").val() == "" || $("#SBLSH").val() == "请输入受理编号") {
		LEx.dialog.tips("请正确填写查询条件", 1);
		return false;
	}
	
	var command = new LEx.Command("app.icity.project.ProjectIndexCmd");
	command.setParameter("spsxcxmm", $("#SPSXCXMM").val());
	command.setParameter("sblsh", $("#SBLSH").val());
	var ret = command.execute("BusinessProgressQueryByPassword");
	if (ret.state == 1) {
		
//		$("input[tag=SBLSH]").attr("readonly", "readonly");
//		$("input[tag=LXRXM]").attr("readonly", "readonly");
//		$("input[tag=LXRSJ]").attr("readonly", "readonly");
		
//		$("input[tag=APPLYNAME]").attr("readonly", "readonly");
//		$("input[tag=ITEM_NAME]").attr("readonly", "readonly");
//		$("input[tag=DEPT_NAME]").attr("readonly", "readonly");
		
		// 页面数据绑定
		var obj = LEx.form.set("base-message", ret.data);
		
		$("#isExistSx").val("0");
		
	} else {
		LEx.dialog.tips("没有找到对应的业务信息", 1);
		
		$("#isExistSx").val("1");
		
		$("input[tag=APPLYNAME]").val("");
		$("input[tag=ITEM_NAME]").val("");
		$("input[tag=DEPT_NAME]").val("");
		
//		$("input[tag=APPLYNAME]").removeAttr("readonly");
//		$("input[tag=ITEM_NAME]").removeAttr("readonly");
//		$("input[tag=DEPT_NAME]").removeAttr("readonly");
	}
	
	queryStarLevel($("#SBLSH").val());
}

function queryStarLevel(sblsh) {
	
	var command = new LEx.Command("app.icity.interactive.satisfaction.SatisfactionEvaluationCmd");
	command.setParameter("sblsh", sblsh);
	command.execute("queryStarLevel");
	
	//返回操作
	if( !command.error ){
		
		var ret = command.returns;
		if(ret.total>0){
			
			//提交时判断是否已评价过（只能评价一次）
			$("#isExistStar").val("1");
			
			var level = ret.data[0].STAR_LEVEL;
			
			var content = ret.data[0].EVALUATE_CONTENT;
			
			$("#"+level).click();
			
			$("#EVALUATE_CONTENT").val(content);
			
			$("#EVALUATE_CONTENT").attr("readonly","readonly");
			
			if(1 == $("#isExistStar").val()){
				LEx.alert("您已评价过，请勿重复评价！");
				return;
			}
			
			//$("#submit").attr("disabled",true);
		}else{
			
			$("#isExistStar").val("0");
			
			$(".current-rating").removeClass();
			$("#stars1-tips").html("");
			$("#stars1-input").val("");
			
			$("#EVALUATE_CONTENT").val("");
			$("#EVALUATE_CONTENT").removeAttr("readonly");
			
			//$("#submit").attr("disabled",false);
		}
	}else{
		LEx.alert(ret.error);
	}
}

/**
 * <p>
 * 评价提交操作
 * </p>
 */
function submit() {
	
	var isExistSx = $("#isExistSx").val();
	if(isExistSx == "0"){
		//if ($("input[tag=APPLYNAME]").val() == "" || $("input[tag=ITEM_NAME]").val() == "" ) {
		if ($("input[tag=APPLYNAME]").val() == "" || $("input[tag=ITEM_NAME]").val() == "" || $("input[tag=DEPT_NAME]").val() == "") {
			LEx.alert("请先进行查询！");
			return;
		}
	}else if(isExistSx == "1"){
		LEx.alert("没有找到对应的业务信息！");
		return;
	}
	
	var isExistStar = $("#isExistStar").val();
	if(isExistStar == "1"){
		LEx.alert("您已评价过，请勿重复评价！");
		return;
	}
	
	if ($("#stars1-input").val() == "") {
		LEx.alert("未进行星级评价！");
		return;
	}
	
	var STATE_CODE = $("input[tag=STATE_CODE]").val();
	if(STATE_CODE != "02" && STATE_CODE != "96" && STATE_CODE != "97" && STATE_CODE != "98" && STATE_CODE != "99"){
		
		LEx.alert("该件还未办结，请办结后进行评价！");
		return;
	}
	
	var cmd = new LEx.Command("app.icity.interactive.satisfaction.SatisfactionEvaluationCmd");
	var container = "base-message";
	var obj = LEx.form.get(container);
	$.each(obj, function(k, v) {
		cmd.setParameter(k, v);
	});
	 cmd.setParameter("EVALUATE_TYPE","1");//评价类型，1 办件评价
	cmd.setParameter("STAR_LEVEL", $("#stars1-input").val());
	cmd.execute("insertNewEvaluation");
	
	// 返回操作
	if (!cmd.error) {
		// 成功提交，显示一个提示对话框

		LEx.alert('评价提交成功');
		
		/*
		 * $(container+" input,textarea").each(function(){
		 * $(this).removeClass("input_validation-failed"); $(this).val("");});
		 * //结果显示 $(".bs-modal-result").modal('show'); $("span#id").html(id); var
		 * secs = 60; for(var i=secs;i>=0;i--) { window.setTimeout("doUpdate(" + i +
		 * ")", (secs-i) * 1000); }
		 */
	} else {
		// errorDialog("系统提示信息", ret.error);
		var ret = cmd.returns;
		LEx.alert(ret.error);
	}
}

function getDeptName() {
	var region_code = getSecurityValue("WebRegion");
	var command = new LEx.Command("app.icity.ServiceCmd");
	command.setParameter("region_code", region_code);
	var ret = command.execute("getDeptList");
	if (!command.error) {
		var len = ret.data.organ.length;
		// var dept = ret.data.region;//下级行政区划
		var dept = ret.data.organ;// 下级部门
		for ( var i = 0; i < len; i++) {
			if (ret.data.organ[i].TYPE_NAME == "机关") {
				$("#SJDW").append(
						"<option value='" + dept[i].SHORT_NAME + "'>"
								+ formatLen(dept[i].SHORT_NAME, 0, 20)
								+ "</option>");
			}
		}
	} else {
		alert(command.error);// 打印错误信息
	}
}