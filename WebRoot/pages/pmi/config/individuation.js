var editor_content = null;
var images = new Array();

$(document).ready(function(e) {
	//var c=  new LEx.Control.Calendar(function(){
	//	c.setup({id:"CTIME"});
	//});
	
	//upload_url: "{{PageContext.ContextPath}}bsp/uploadify?action=upload&path=site/",
	LEx.Control.SWFUpload({
		id: "file_upload",
		text: '上传图片',
		custom_settings : {
			id : "ATTACH"
		},
		upload_success_handler:uploadSuccess,
		type:"pic",
		file_size_limit:"2 MB"
	});

	LEx.Control.SWFUpload({
		id: "js_file_upload",
		text: '上传图片',
		custom_settings : {
		},
		upload_success_handler:uploadJsSuccess,
		type:"pic",
		file_size_limit:"2 MB"
	});
	
	LEx.Control.editor("CONTENT",editorFun);
	
	function editorFun(obj){
		editor_content = obj;
		initContent();
	}
	
	
	
});


function uploadSuccess(file, serverData) {
	$(".uploadTipe").hide();
	var data = LEx.decode(serverData);
	$("#ATTACH").val(data.path);
	
	var img = "<span style='padding-right: 15px;'><img src='" + data.path + "' height='33px'/><img src='{{PageContext.ContextPath}}public/images/erase.png' width='15px' style='cursor: pointer;' title='删除' onclick='deleteTitleImg()' /></span>";
	$("#title_img").html(img);
}

function deleteTitleImg() {
	$("#ATTACH").val("");
	$("#title_img").html("");
}

function uploadJsSuccess(file, serverData) {
	$(".uploadTipe").hide();
	var data = LEx.decode(serverData);
	var id = null;
	var path = data.path;
	var ei = path.indexOf(".");
	var si = path.lastIndexOf("/",ei);
	if(ei > -1 && si > -1){
		id = path.substring(si+1,ei);
	}
	var img = "<span id='" + id +"' style='padding-right: 15px;'><img src='" + data.path + "' height='33px'/><img src='{{PageContext.ContextPath}}public/images/erase.png' width='15px' style='cursor: pointer;' title='删除' onclick='deleteImg(\""+id+"\")' /></span>";
	$("#js_imgs").append(img);
	
	var obj = new Object();
	obj.id = id;
	obj.url = path;
	images.push(obj);
}

function deleteImg(id) {
	for(var i=0;i<images.length;i++) {
		var obj = images[i];
		if(obj.id == id) {
			images.splice(i,1);
			$("#" + id).remove();
			break;
		}
	}
}

function initContent() {
	
	var name = LEx.urldata.name;
	var type = LEx.urldata.type;
	if(type && type=="system") {
		$("#save_btn").hide();
	}else{
		$("#save_btn").show();
	}
	//当是站点管理员进来时
	if(!name || name.length <=0) {
		var contextPath = "{{PageContext.ContextPath}}";
		name = contextPath.substring(1,contextPath.length -1);
	}
	
	// 定义command对象
	var command = new LEx.Command("app.pmi.config.cmd.WebsiteCmd");
	command.setParameter("name", name);
	var ret = command.execute("getIndividuationConfig");
	if(!command.error){
		$("#CNAME").val(ret.data.title);
		var layout = ret.data.layout;
		$("#LAYOUT").val(layout);
		layoutChange(layout);
		var style = ret.data.style;
		$("#STYLE").val(style);
		$("#custom").val(ret.data.custom);
		if("custom" == style) {
			$("#_custom").show();
		}else {
			$("#_custom").hide();
		}
		$("#ATTACH").val(ret.data.titleImg);
		if(ret.data.titleImg){
			var ai = "<span style='padding-right: 15px;'><img src='" + ret.data.titleImg + "' height='33px'/><img src='{{PageContext.ContextPath}}public/images/erase.png' width='15px' style='cursor: pointer;' title='删除' onclick='deleteTitleImg()' /></span>";
			$("#title_img").html(ai);
		}
		var jsImgs = ret.data.jsImgs;
		if(jsImgs) {
			images = new Array();
			for(var i=0;i<jsImgs.length;i++) {
				var path = jsImgs[i];
				
				var ei = path.indexOf(".");
				var si = path.lastIndexOf("/",ei);
				if(ei > -1 && si > -1){
					id = path.substring(si+1,ei);
				}
				var obj = new Object();
				obj.id = id;
				obj.url = path;
				images.push(obj);
				
				var img = "<span id='" + id +"' style='padding-right: 15px;'><img src='" + path + "' height='33px'/><img src='{{PageContext.ContextPath}}public/images/erase.png' width='15px' style='cursor: pointer;' title='删除' onclick='deleteImg(\""+id+"\")' /></span>";
				$("#js_imgs").append(img);
			}
		}
		
		editor_content.html(ret.data.fdrk);
		//$("#CONTENT").html("快圣诞节贺卡收到货咖啡收到货");
	}else{
		LEx.dialog({
			title : "系统提示",
			content : command.error,
			icon : 'error',
			lock : true
		});
	}	
	
}
//站点管理员调用
function save2(){
	if(save()) {
		LEx.dialog.tips("站点配置保存成功");
	}
}

function save() {
	var name = LEx.urldata.name;
	
	//当是站点管理员进来时
	if(!name || name.length <=0) {
		var contextPath = "{{PageContext.ContextPath}}";
		name = contextPath.substring(1,contextPath.length -1);
	}
	var titleImg = $("#ATTACH").val();
	var layout = $("#LAYOUT").val();
	var style = $("#STYLE").val();
	var custom = "";
	if(style == "custom") {
		custom = $("#custom").val();
	}
	var jsImgs = new Array();
	for(var i=0;i<images.length;i++) {
		jsImgs.push(images[i].url);
	}
	var fdrk = editor_content.html();
	var command = new LEx.Command("app.pmi.config.cmd.WebsiteCmd");
	command.setParameter("name",name);
	command.setParameter("layout",layout);
	command.setParameter("style",style);
	command.setParameter("custom",custom);
    command.setParameter("titleImg",titleImg);
    command.setParameter("jsImgs",jsImgs);
    command.setParameter('fdrk',fdrk);
    ret = command.execute("saveIndividuationConfig");
    //结果处理
    if (ret.state == 1) {
        return true;
    } else {
        LEx.dialog({ title: "系统提示", content: ret.message, icon: 'error' ,lock:true});
    }
    return false;
}

function styleChange(value) {
	if(value == "custom") {
		$("#_custom").show();
	}else {
		$("#_custom").hide();
	}
}

function layoutChange(value) {
	var options = "";
	if(value == "one") {
		options = '<option value="default">默认</option><option value="red">红的</option><option value="green">绿的</option><option value="custom">自定义</option>';
	}else if(value == "two"){
		options = '<option value="default">默认</option><option value="red">红的</option><option value="blue">蓝的</option><option value="custom">自定义</option>';
	}else {
		options = '<option value="default">默认</option><option value="red">红的</option><option value="green">绿的</option><option value="custom">自定义</option>';
	}
	$("#STYLE").html(options);
}
