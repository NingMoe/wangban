var bltable = null;
function contentHandler(){
	//1.实例化分页table控件：table的id，模版的id
	bltable = new LEx.Control.Table("div_business_list","template_business_list");
	bltable.limit(10);
	onQuery(); 
}
var name = "";
var title = "";
$(function(){
	$("#submit").click(function(){
		title = $("#title").val();
		onQuery();
	});
	$("#reset").click(function(){
		$("#title").val("");
		title = "";
		onQuery();
	});
});
function onQuery(){
	var start = bltable.start();
	var limit=bltable.limit();
	var cmd = new LEx.Command("app.icity.project.WdwpCmd");
	cmd.setParameter("start",start);
	cmd.setParameter("title",title);
	cmd.setParameter("limit", limit);	
	cmd.setParameter("ucid","{{UserInfo.uid}}");
	var ret =  cmd.execute("getAttachList");
	if(ret.state=="1"){
		//2.将数据传递给table对象
		bltable.toBody(ret);
		//3.加载分页控件：分页div的id，Table的实例化对象名称（字符串）,总数量，查询方法（字符串）
		bltable.toPageBar("epager","bltable",ret.total,"onQuery()");
		$("#div_business_list").html(LEx.processDOMTemplate("template_business_list",ret));
		//cmd.execute("getAttachAndPower");
	}else{		
		LEx.alert(ret.error);
	}
} 
//下载附件
function onDownload(path,name){
    var opera = "downloadagent4wp&path="+path+"&name="+encodeURIComponent(name); 
   	window.open(LEx.webPath + "bsp/uploadify?action="+opera);
	return false;
}

function delete_disk(doc_id){
	var cmd = new LEx.Command("app.icity.project.WdwpCmd");
		cmd.setParameter("ucid","{{UserInfo.uid}}");
		cmd.setParameter("doc_id",doc_id);
		cmd.setParameter("disk_id","");
		cmd.setParameter("folderids","0");
		var ret =  cmd.execute("deleteNetDiskDoc");
		if(!ret.error){				
			if(ret.state==1){
				LEx.alert("删除成功");
				onQuery();
			}				
		}else{
			LEx.alert(ret.error);
		}
  }
function formatDate(obj) {
	return LEx.util.Format.formatDate(obj);
}
var _sblsh="";
function BusinessDocumentDetail(sblsh){
	_sblsh = sblsh;
	$("#businessDocument").hide();
	$("#businessDocumentDetail").show();
	bltable = new LEx.Control.Table("div_item_list","template_item_list");
	bltable.limit(10);
	searchRelatedItem();
}
function searchRelatedItem(){
	var start = bltable.start();
	var limit=bltable.limit();
	var cmd = new LEx.Command("app.icity.project.WdwpCmd");
	cmd.setParameter("start",start);
	cmd.setParameter("limit", limit);
	cmd.setParameter("sblsh",_sblsh);
	var ret =  cmd.execute("getRelatedItemAttach");
	if(ret.state=="1"){
		if(ret.total=="0"){
			$("#null_sblsh").show();
		}else{
			$("#null_sblsh").hide();
		}
		bltable.toBody(ret);
		bltable.toPageBar("epager_item","bltable",ret.total,"searchRelatedItem()");
		$("#div_item_list").html(LEx.processDOMTemplate("template_item_list",ret));
	}else{		
		LEx.alert(ret.error);
	}
}