$(document).ready(function(){
	//检查登陆状态
	checkLogin();	
	//判断是否绑定邮箱操作
	if(LEx.urldata.action){
		var action=LEx.urldata.action;
		if(action=="bind"){
			if(LEx.urldata.result&&LEx.urldata.result=="1"){
				$(".show_tab").load(LEx.webPath+"src?m=uc/activateEmailSuccess.html");
			}else{
				$(".show_tab").load(LEx.webPath+"src?m=uc/activateEmailFailure.html");
			}
		}
	}else{
		//点击链接加载对应页面
		$(".business_tab").click(function(){
			var tag = $(this).attr("tag");
			showTab(tag);		
		});

		var tagUrl = LEx.urldata.tagUrl;
			showTab(tagUrl);
	}
	
	//默认加载页面
	$(".show_tab").load(LEx.webPath+"src?m=uc/account.html");
});

//检查登陆状态,用户id为空跳转到登陆页面
function checkLogin(){
	var uid=$("#userInfo").val();	
	if(!LEx.isNotNull(uid)){		
		window.location.href=LEx.webPath+"uc/login";
	}
}

function isAccountActive(){
	var cmd = new LEx.Command("app.uc.TwebBusiCmd");
	var ret = cmd.execute("isAccountActive");
	if(ret.state == 1 && ret.total>0){
		return true;
	}else{
		return false;
	}
}

function showTab(tag){
	var tagUrl = LEx.urldata.tagUrl;
	var region = $("#region").val();
	if(tag=="secBindMailW"){
	var userId=$("#userInfo").val();
	var data={};
	data["uid"]=userId;
	$(".show_tab").load(LEx.webPath+"app/uc/user?action=rebind",data);
	}else{			
	$(".show_tab").load(LEx.webPath+"src?m=uc/"+tag+".html");			
	}
}
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
