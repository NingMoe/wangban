/**
 * 文档下载
 */
(function($){
	$(document).ready(function() {
		request();
	});
	function request(secret) {
		$.ajax({
			type : "POST",
			dataType:"json",
			data : { doc_id : doc_id,secret : secret},
			url : CONST.SERVICE_URL + "/index.php?r=/DocPreview/getExtractCode",
			success : function(data) {
				$("#sct_btn").click(function() {
					clickSct(data);
				});
				if(data.share_type==2&&data.extract_code!=data.secret){
					$("#download").css({background :"gray",cursor:"default",color:"#fff",border:"gray"});
				}
				if(data.share_type==1||data.share_type==4||(data.secret&&data.extract_code==data.secret)){
					$("#download").click(function() {
						DownLoad.link(data);
					});
				}
				if(data.share_type==2&&data.extract_code!=data.secret){
					$("#sub").css({display:"block"});
				}
				else if(data.secret&&data.extract_code==data.secret){
					$("#sub").css({display:"none"});
					if(data.status==2){
						$("#flash").css({display:"block"});
					}else{
						$("#ss").css({display:"block"});
					}
				}
				else{
					if(data.status==2){
						$("#flash").css({display:"block"});
					}else{
						$("#ss").css({display:"block"});
					}
				}
			}
		});
	}
	function clickSct(data) {
		var secret = document.getElementById('secret').value;
		$("#error").css({display:"none"});
		if(secret==data.extract_code){
			request(secret);
			$("#download").css({"background-color":"","cursor":"hand"});
			$("#download").hover(
					  function () {
					    $(this).css({"background-color":"","cursor":"hand","color":"#f49807"});
					  },
					  function () {
						  $(this).css({"background-color":"","color":"#fff"});
					  }
					);
		}else{
			$("#error").css({display:"block"});
		}
		document.getElementById('secret').value='';
	}
	function getDownLoadURL(id,status,uid){
		var str = "/doc?doc_id=" + id+"&"+"share_status="+status+"&"+"uid="+uid;
		return CONST.UPLOAD_URL + str;
	}
	var DownLoad={};
	DownLoad.link=function() {
		if(uid){
			var dlurl=getDownLoadURL(doc_id,share_status,uid);
		    $("#download").attr({href:dlurl});
		}
		else{
			showLogin();
			/* 2013-03-18 弹出登录，使用默认的showLogin方法
			$("#flash").css({display:"none"});*
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
		}
	}
})(jQuery);

