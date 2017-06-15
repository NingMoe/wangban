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

//错误提示
function errorDialog(t,c){
	LEx.dialog({
		title : t,
		content : c,
		icon : 'error',
		lock : true
	});
}