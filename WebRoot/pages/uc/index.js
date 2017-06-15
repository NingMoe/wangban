$(document).ready(function(){
	//检查登录状态
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
			var folder = $(this).attr("folder");
			if(folder!="business"){
				showTab(tag);
			}			
		});
	}
	
	var tag = LEx.urldata.tag;
	//默认加载页面
	if(LEx.isNotNull(tag)){
		$(".show_tab").load(LEx.webPath+"src?m=uc/"+tag+".html");
	} else{
		$(".show_tab").load(LEx.webPath+"src?m=uc/account.html");
	}
	var cmd = new LEx.Command("app.uc.UserCmd");
	var ret=cmd.execute("showUserInfo");
	if (ret.state==1) {
		if(ret.data){	
			if(ret.data.PHOTO_URI != ""){
				$("#userface1").attr("src",LEx.webPath+ret.data.PHOTO_URI);
			}
		}		
	} else {
		errorDialog("系统提示", "error：" + cmd.error);
	}	
});

//检查登录状态,用户id为空跳转到登录页面
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