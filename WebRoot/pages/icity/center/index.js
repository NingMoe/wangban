var _calendar= null;
function init(){
		_calendar= new LEx.Control.Calendar(function(){
		initHandler();
	});	
	$('#activateUserForm [tag="NAME"]').val();
	var cmd = new LEx.Command("app.uc.UserCmd");
	var ret=cmd.execute("showUserInfo");
	if (ret.state==1) {
		if(ret.data){	
			if(ret.data.PHOTO_URI !=""){
				$("#userface").attr("src",LEx.webPath+ret.data.PHOTO_URI);
			}
		}		
	} else {
		errorDialog("系统提示", "error：" + cmd.error);
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

//检查登录状态,用户id为空跳转到登录页面
/*function checkLogin(){
	var uid=$("#userInfo").val();	
	if(!LEx.isNotNull(uid)){		
		window.location.href=LEx.webPath+"uc/login";
	}
}*/


function initHandler(){
	$(".business_tab").click(function(){
		var folder = $(this).attr("folder");
		var tag = $(this).attr("tag");	
		$(".show_tab").load(LEx.webPath+"src?m=icity/center/"+folder+"/"+tag+".html", function() {
			contentHandler();
		});
	});
	
	var tag = LEx.urldata.tag;
	var folder = LEx.urldata.folder;

	if(tag){
		$(".show_tab").load(LEx.webPath+"src?m=icity/center/"+folder+"/"+tag+".html", function() {
			contentHandler();
		});
	} else{
		$(".show_tab").load(LEx.webPath+"src?m=icity/center/business/business_list.html", function() {
			contentHandler();
		});
	}
	
	
	//获取推荐应用
	//onQueryRecommendApp();
}
function onQueryRecommendApp(){
	if(LEx.userInfo != undefined){
		var cmd = new LEx.Command("icity.center.index.CenterCmd");
		var obj = {"nums":"6"};
		obj.uid = LEx.userInfo.id;
		cmd.setParameter("param",obj);
		cmd.setParameter("method","application/get_promoted_applist");
		cmd.afterExecute = function(){
			if(!cmd.error){
				var ret = cmd.returns;
				$('#RecommendAppData').html(LEx.processDOMTemplate("RecommendApptemplate",ret));
			}else{
				LEx.dialog.tips(ret.message,2);
			}
		}
		cmd.execute("getRecommendApp",false);
	}
}
