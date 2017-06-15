/**
 * 增加收藏
 */
(function($){
	$(document).ready(function() {
		Favorite.readyfav();
		$("#collect").click(function() {
			Favorite.clickfav();
		});
	});
	var Favorite={};
	Favorite.readyfav=function() {
		if (count == 0||!uid) {
			$("#collect").text("加入收藏");
		} else {
			$("#collect").text("已收藏");
			$("#collect").css({background :"gray",cursor:"default",color:"#fff",border:"0" });
		};
	}
	Favorite.clickfav=function() {
		if(uid){
			$.ajax({
				type : "POST",
				url : CONST.SERVICE_URL+"/index.php?r=/DocPreview/addFav",
				data : {
					'doc_id' : doc_id
				},
				success : function() {
					$("#collect").text("已收藏");
					$("#collect").css({background :"gray",cursor:"default",color:"#fff",border:"0" });
				}
			});
		}
		else{
			showLogin();
			/* 2013-03-18 弹出登录，使用默认的showLogin方法
			$("#flash").css({display:"none"});
			function showLogin(url){
				if(url==null){
					url = window.location.href;
				}
				$("#redirectUrl").val(url);
				$("#loginBox").clone(true).dialog({
			        width: "370",
			        title: "登录或注册",
			        modal: true,
			        close:function(){
			        	$("#flash").css({display:"block"});
			        }
				});
				
			}
			*/
			/*window.gotoLink = function(url){
				if(true){
					showLogin(url);
				}else{
					this.location.href=url;
				}
			}*/
		}
	}
})(jQuery);

