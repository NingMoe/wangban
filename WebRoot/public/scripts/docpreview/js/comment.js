$(document).ready(function() {
	showAllComments();
	getCommentpage();
	$("#submit_btn").click(function() {
		clickSubmit();
		clickReset();
	});
	$("#reset_btn").click(function() {
		clickReset();
	});
});
function showAllComments(pagecount) {
	if(!pagecount){
		pagecount=1;
	}
	$.ajax({
		type : "POST",
		dataType:"json",
		data : { doc_id : doc_id,page : pagecount},
		url : CONST.SERVICE_URL + "/index.php?r=/DocPreview/getComments",
		success : getCommentsComplete,
		error:function(){
			alert("ss");
		}
	});
}
function getCommentsComplete(data) {
	var comments=$("#comments");
	comments.empty();
	var ops = [];
	for( var key in data){
		var obj=data[key];
		if(!obj.uid||obj.uid=="null"){
			ops.push('<div class="comment"><a href="#"><img src="',CONST.RESOURCE_URL,'/css/docview/nophoto.jpg"></a>'+
					'<div class="cont1"><p>匿名网友(',obj.ip,')&nbsp;&nbsp;&nbsp;<i>',obj.cmt_time,'</i>&nbsp;&nbsp;说：</p>'+
					'<div class="cont2">', obj.content,'</div></div></div>');
		}
		else{
			ops.push('<div class="comment"><a href="#"><img src="',CONST.RESOURCE_URL,'/css/docview/nophoto.jpg"></a>'+
					'<div class="cont1"><p><a href="#">',obj.nick_name,'</a>&nbsp;&nbsp;&nbsp;<i>',obj.cmt_time,'</i>&nbsp;&nbsp;说：'+
					'</p><div class="cont2">', obj.content,'</div></div></div>'); 
		}
	}
	comments.html(ops.join(""));
}
function getCommentpage() {
	$.ajax({
		type : "POST",
		dataType:"json",
		data : { doc_id : doc_id},
		url : CONST.SERVICE_URL + "/index.php?r=/DocPreview/getCmtPage",
		success : function(data) {
			showCommentPage(data,1);
		}
	});
}
function showCommentPage(data,pagecount) {
	page=data.page;
	commentcount=data.count;
	if(!pagecount){
		pagecount=1;
	}
	var comments=$("#commentpage");
	comments.empty();
	var ops = [];
	var pages='';
	if(page>1){
		if(pagecount>1){
			var beforepage=pagecount-1;
			pages+='<a title="首页" href="javascript:void(0)" ><span class="page" id="'+1+'" ><span>首页</span></span></a>';
			pages+='<a title="上一页" href="javascript:void(0)"><span class="page" id="'+beforepage+'"><span><上一页</span></span></a>';
		}
		if(pagecount==1){
			pages+='<span class="page" id="'+1+'" ><span>'+1+'</span></span>';
		}else{
			pages+='<a title="'+1+'" href="javascript:void(0)"><span class="page" id="'+1+'" ><span>'+1+'</span></span></a>';
		}
		if(pagecount>=5){
			pages+='<span class="commentcount"><span>...</span></span>';
			for(i=pagecount-2;i<pagecount;i++){
				pages+='<a title="'+i+'" href="javascript:void(0)"><span class="page" id="'+i+'" ><span>'+i+'</span></span></a>';
			}
		}else{
			for(i=2;i<pagecount;i++){
				pages+='<a  title="'+i+'"href="javascript:void(0)"><span class="page" id="'+i+'" ><span>'+i+'</span></span></a>';
			}
		}
		if(pagecount!=1&&pagecount!=page){
			pages+='<span class="page" id="'+pagecount+'" ><span>'+pagecount+'</span></span>';
		}
		if(page-pagecount>=4){
			for(i=pagecount+1;i<=pagecount+2;i++){
				pages+='<a title="'+i+'" href="javascript:void(0)"><span class="page" id="'+i+'" ><span>'+i+'</span></span></a>';
			}
			pages+='<span class="commentcount"><span>...</span></span>';
		}else{
			for(i=pagecount+1;i<page;i++){
				pages+='<a title="'+i+'" href="javascript:void(0)"><span class="page" id="'+i+'" ><span>'+i+'</span></span></a>';
			}
		}
		if(pagecount==page){
			pages+='<span class="page" id="'+page+'" ><span>'+page+'</span></span>';
		}else{
			pages+='<a title="'+page+'" href="javascript:void(0)"><span class="page" id="'+page+'" ><span>'+page+'</span></span></a>';
		}
		if(pagecount<page){
			var nextpage=pagecount+1;
			pages+='<a title="下一页" href="javascript:void(0)"><span class="page" id="'+nextpage+'"><span>下一页></span></span></a>';
			pages+='<a title="末页" href="javascript:void(0)"><span class="page" id="'+page+'" ><span>末页</span></span></a>';
		}
		pages+='<span class="commentcount"><span>共有'+commentcount+'篇评论</span></span>';
	}
	ops.push('<div class="commentpage">',pages,'</div>');
	comments.html(ops.join(""));
	$(".page").click(function() {
		var pagecountnew=parseInt($(this).attr("id"));
		showAllComments(pagecountnew);
		showCommentPage(data,pagecountnew);
	});
}
function clickSubmit() {
	var content = document.getElementById('content').value;
	if(!content||content==""){
		alert("评论不能为空！");
		return false;
	}
	$.ajax({
		type : "POST",
		dataType:"json",
		data : {doc_id : doc_id,content : content,uid : uid},
		url : CONST.SERVICE_URL + "/index.php?r=/DocPreview/addComment",
		success : function() {
			showAllComments();
			getCommentpage();
		}
	});
}
function clickReset() {
	document.getElementById('content').value='';
}