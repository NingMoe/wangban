//对所有控件校验
function checkValidate(id){
	var f = true;
	if(id==""||id==null){
		$("#"+id+" [reg]").each(function(){
			if(!validateObj($(this),id)){
				f=false;
			}		
		});
	}else{
		$("#"+id+" [reg]").each(function(){
			if(!validateObj($(this),id)){
				f=false;
			}		
		});
	}	
	return f;
}

//对单个控件校验
function validateObj(obj,id){
	var t=null;
	if(id==""||id==null){
		t=$(".form-tip span[for="+obj.attr("id")+"]");
	}else{
		t = $("#"+id+" .form-tip span[for="+obj.attr("id")+"]");
	}	
	if(!validate(obj)){
		t.attr("class","invalid").html(obj.attr("tip"));
		return false;
	}else{
		var func = obj.attr("func");
		if(func == undefined){
			t.attr("class","valid").html("");			
 		}else{
 			try{
		 		var s = eval(func);
		 		var re = s.call(s,obj);
		 		if(!re){
			 		return false;
		 		}
		 	}catch(e){
		 		alert("请求的验证函数错误。");
		 	}
 		}
	}
	return true;
}

//错误提示
function errorDialog(t,c){
	LEx.dialog({
		title : t,
		content : c,
		icon : 'error',
		lock : true
	});
}