/**
 * <p>与satisfaction.html用户满意度评价网页相对应的js文件</p>
 * @time 2015-07-28
 * @author chendeming
 */

//输入验证

//提交操作
function contentHandler(){
	//初始化所有提交信息
	LEx.form.init();
	$("input[tag=SBLSH]").attr("readonly","readonly");
	$("input[tag=SBXMMC]").attr("readonly","readonly");
	$("input[tag=LXRXM]").attr("readonly","readonly");
	$("input[tag=LXRSJ]").attr("readonly","readonly");
	$("input[tag=SQRMC]").attr("readonly","readonly");
	$("input[tag=SXMC]").attr("readonly","readonly");
	$("input[tag=SJDW]").attr("readonly","readonly");
	//getDeptName();
	initQuery();
	$.fn.raty.defaults.path = '{{PageContext.JsPath}}raty_master/lib/images/';
	$('#star_level').raty();
	$("button[op]").click(function(event) {
		//验证div的class为zl-thumbnails-ggfu-2中信息
		var op=$(this).attr("op");
		if(op=="submit"){
			//页面数据验证
			if(LEx.form.validate(container)){
				submit(container);
			}else{
				LEx.dialog.tips("请正确填写必填项",1.5);
			}
		}else if(op=="select"){
			//查询业务信息和申报人信息
			var container="input[tag='SBLSH']";
			if($(container).val()==''||$(container).val()==undefined){
				//如果页面上受理编号为空
				alert('请输入查询编号');
				return ;
				container = "input[tag='SPSXCXMM']";
				if($(container).val()==''||$(container).val()==undefined){
					alert('请输入查询密码');
					return ;
				}
			}
			//查询编号不为空，开始执行查询
			select();
			
	}else{
		window.close();
		}
	});
	
	$("select[tag=SBSXLB]").change(function(){
		$("input[tag=SBLSH]").val($("select[tag=SBSXLB]").find("option:selected").val());
		select();
	});
}
function initQuery(){	
	var cmd = new LEx.Command("app.icity.interactive.satisfaction.SatisfactionEvaluationCmd");	
	cmd.execute("getSbsxlist");
	if( !cmd.error ){
		var ret = cmd.returns;
		if(ret.total>0){
			var len = ret.data.length;
			for(var i =0;i<len;i++){
				$("select[tag=SBSXLB]").append("<option value='"+ret.data[i].SBLSH+"'>"+ret.data[i].SBXMMC+"</option>");
			}
		}
	}else{
		LEx.alert(ret.error);
	}
}
/**
 * <p>根据受理编号查询相关的事项信息与申报人信息并将之返回界面</p>
 * */
function select(){
	//调用cmd查询事项信息与申报人信息
	var cmd = new LEx.Command("app.icity.interactive.satisfaction.SatisfactionEvaluationDao");
	var container="input[tag='SBLSH']";
	cmd.setParameter("SBLSH",$(container).val());
	cmd.execute("getBasicEvaluationMessage");
	//返回操作
	if( !cmd.error ){
		//获取返回值
		var ret = cmd.returns;
		if(ret.total>0){
			/*$("input[tag=SBLSH]").attr("readonly","readonly");*/
		}
		//页面数据绑定
		var obj=LEx.form.set("base-message",ret.data[0]);
	}else{
		LEx.alert(ret.error);
	}
}

/**
 * <p>评价提交操作</p>
 * */
function submit(container){
	
	if($("#SJDW").val()== ""){
		LEx.alert("请选择受理部门！");
		return;
	}
	
	if($('#star_level').raty('score')==undefined){
		LEx.alert("未评分！");
		return;
	}
	
	if($("input[tag=SBLSH]").val()==""||$("input[tag=SXMC]").val()==""){
		LEx.alert("业务不存在");
		return;
	}
	//调用cmd
    var cmd = new LEx.Command("app.icity.interactive.satisfaction.SatisfactionEvaluationCmd");
    //设置传入cmd的参数
    var container = "base-message";
    var obj = LEx.form.get(container);
    $.each(obj,function(k,v){
    	cmd.setParameter(k,v);
    });
    cmd.setParameter("type","1");
    cmd.setParameter("STAR_LEVEL",$('#star_level').raty('score'));
    //执行插入
    cmd.execute("insertNewEvaluation");
	//返回操作
	if( !cmd.error ){
		//成功提交，显示一个提示对话框
		success();
	}else{
		var ret = cmd.returns;
		LEx.alert(ret.error);
	}
}

/**
 * <p>成功跳转</p>
 * */
function success(){	
	LEx.alert('评价提交成功');
	$("select[tag=SBSXLB]").empty();
	$(".form-control").val("");
	$('#star_level').raty();
	$("select[tag=SBSXLB]").append("<option selected=\"selected\" value=\"\">请选择要评价的项目</option>");
	initQuery();
}

function getDeptName(){
	var region_code=getSecurityValue("WebRegion");
	var command = new LEx.Command("app.icity.ServiceCmd");
	command.setParameter("region_code",region_code);
	var ret = command.execute("getDeptList");
	if(!command.error){
		var len = ret.data.organ.length;
		var dept = ret.data.organ;//下级部门
		for(var i=0;i<len;i++){
			if(ret.data.organ[i].TYPE_NAME=="机关"){
				$("#SJDW").append("<option value='"+dept[i].SHORT_NAME+"'>"+formatLen(dept[i].SHORT_NAME,0,20)+"</option>");
			}
		}
	}else{
		alert(command.error);//打印错误信息
	}
}