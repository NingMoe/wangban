/**
 * @title 网上办事页
 * @author: Xiongzhiwen
 * @version: 2.0
 * @date: 2013-08-29
 * @description: 网上办事首页改造
 */
var code=LEx.urldata.code;//事项编码

function init(){
	var tabObj = {};
	tabObj.id = "tab";//tab
	LEx.Control.Tab(tabObj);
	$(".tab").click(function(){
		var c = $(this).attr("tag");
		var length = $("#"+c).html().length;
		if(length==0){
			$("#"+c).load(LEx.webPath+"src?m=icity/project/"+c+".html", function() {
				contentHandler();
			});
		}
	});
	onQuery();
}

function onQuery(){
	initTop();
	if(typeof(onBottom) != "undefined")	onBottom();
}

//初始化第一屏内容
function initTop(){
	$(".jqzoom").jqueryzoom({
		xzoom:410,//放大图显示范围
		yzoom:330,//放大图显示范围
		offset:10,
		position:"right",
		preload:1,
		lens:1
	});
}

//for循环中获取行号
function getRowNum(index){
	return parseInt(index)+1;
}

//删除浏览历史信息
function deleteHistory(){
	//清空cookie
	LEx.cookie.set("project_history","");
	if(LEx.userInfo!=null&&LEx.userInfo!=""){
		var cmd = new LEx.Command("app.icity.project.ProjectIndexCmd");
		var uid = LEx.userInfo.userId;
		cmd.setParameter("uid@=", uid);
		var ret =  cmd.execute("deleteHistory");
		if(ret.state==1){
			LEx.dialog.tips("删除浏览历史信息成功！");
		}else{
			LEx.dialog.tips("删除浏览历史信息失败！");
		}
	}
	$("#guide-right-history").css("height","30px");
	$("#guide-right-history").html("");
}

//添加收藏[已登录则持久化到数据库中，未登录则添加到收藏夹中]
function addFaveriot(o){
	if(LEx.userInfo==null||LEx.userInfo=="null"){
		try{
			var url=window.location.href;
			window.external.addFavorite(url, '{{ConfigInfo.AppTitle}}');
		}catch (e){
	    	try{
	          window.sidebar.addPanel("{{ConfigInfo.AppTitle}}", url, "");
	        }catch(e){
	        	LEx.dialog.tips("请使用Ctrl+D进行添加");
	        }
		}
	}else{
		var cmd = new LEx.Command("app.icity.favorite.ProjectFavoriteCmd");
		cmd.setParameter("SXID", $(o).attr("item_id"));
		cmd.setParameter("SXBM", $(o).attr("item_code"));
		cmd.setParameter("SXMC", $(o).attr("item_name"));
		var ret = cmd.execute("addFaveriot");
		if(!ret.error&&ret.state == 1){
			LEx.dialog.tips("收藏成功！");
		}else{
			LEx.dialog.tips(ret.error);
		}
	}
}

//推荐给好友
function recomand(name){
	window.location="mailto:?subject="+name+"&body="+window.location.href;
}

//我要纠错
function correctError(id){
	LEx.dialog({
		title:"我要纠错",
	    content: '<textarea id="crrect-error-input" cols="80" rows="10" style="height:100px;"></textarea>',
	    fixed: true,
	    padding: 0,
	    id: 'crrect-error-1',
	    okVal: '确定',
	    ok: function () {
	    	var input = document.getElementById('crrect-error-input');
	    	if (input.value == null || input.value =="") {
	    		LEx.dialog({
		           	content: '请输入纠错内容!',
		            icon: 'error',
		            fixed: true,
		            lock: true,
		        });
	    		return false;
	        } else {
	        	input.select();
		        input.focus();
		    	var cmd = new LEx.Command("app.icity.correct.ProjectCorrectCmd");
				cmd.setParameter("SXBM", code);
		    	cmd.setParameter("TITLE",input.value);
		    	cmd.setParameter("PUBLISHER",LEx.userInfo.uid.toString());
				var ret = cmd.execute("insert");
				if(ret.state==1){
					 LEx.dialog({
				        	title:'系统提示',
			            	content: '谢谢您的参与！',
			                icon: 'succeed',
			                fixed: true,
			                lock: true,
			            });
				}else{
					LEx.dialog({
			        	title:'系统提示',
		            	content: '系统保存失败,请重试！',
		                icon: 'error',
		                fixed: true,
		                lock: true,
		            });
				}

	        };
	    },
	    cancel: true
	});
}

function CheckData(id){
	LEx.dialog({
		title:"网上办理提示",
	    content: '<div Style="height:25px;">表单提交前，请不要关闭此验证窗口。</div><div Style="height:25px;">表单提交后，请根据你提交反馈的情况点击下面的按钮。</div>',
	    fixed: true,
	    padding: 0,
	    lock: true,
	    width:310,
		height:100,
	    button: [{
		    name: "完成提交",
		    callback: function() {
		    	//调用rest服务验证数据
		    	$.get(LEx.webPath+"center/CheckSubmitData",{id:code},function(data,status){
		    		location.href=LEx.webPath+"center/index";
		    	});
		    },
		    focus: true
	   },
	   {
		    name: "提交出现问题",
		    callback: function() {
		    	return true;
		    }
	   }]
	});
}

//获取满意度百分比
function getEvalueNum(data,high,middle,low){
	if(undefined==data||"undefined"==data || isNaN(parseInt(data)) || isNaN(parseInt(high)) || isNaN(parseInt(middle)) || isNaN(parseInt(low))){
		high=1;middle=1;low=1;data=1;
	}
	var sum = parseInt(high)+parseInt(middle)+parseInt(low);
	var a = (parseInt(data)/sum)*100;
	return a.toFixed(0);
}


