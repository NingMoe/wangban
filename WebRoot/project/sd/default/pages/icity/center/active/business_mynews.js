var bltable = null;
function contentHandler(status){
	//1.实例化分页bltable控件：bltable的id，模版的id
	bltable = new LEx.Control.Table("div_business_list","template_business_list");
	bltable.limit(8);	
	onQuery();
}
//分页
function onQuery(){
	var cmd = new LEx.Command("app.icity.ServiceCmd");
	var ret =  cmd.execute("initMynews");
	if(!ret.error){//document.write(LEx.encode(ret));	
		//2.将数据传递给bltable对象
		bltable.toBody(ret);
		//3.加载分页控件：分页div的id，bltable的实例化对象名称（字符串）,总数量，查询方法（字符串）
		bltable.toPageBar("epager","bltable",ret.total,"onQuery()");
		$("#div_business_list").html(LEx.processDOMTemplate("template_business_list",ret));
		$(".NeedFormatState").each(function(){
			var status=$(this).attr("state");
			var bState = LEx.icityBusiness.formatStatus(status);
			$(this).html(bState);
			
			var href = "&nbsp;&nbsp;<a class='btn btn-info btn-sm'href='https://auth.alipay.com/login/index.htm' target='_blank'>缴费</a>";
			var demonstrate_mode = '{{ConfigInfo.DEMONSTRATE_MODE}}';
			if(bState == "办理中"){
				if(demonstrate_mode == '1'){
					$(this).html(bState+href);
				}else{
					$(this).html(bState);
				}
			}else{
				$(this).html(bState);
			}
		});
	}else{
		LEx.alert(ret.error);
	}
}

/**
 * 办事攻略
*/

function Mystrategy(sblsh,sxbm,sxid,sxmc){
	$("#businessList").hide();
	$("#businessDetail").show();
	$("#businessDetail").load(LEx.webPath+"src?m=icity/center/active/business_strategyinfo.html&sblsh="+sblsh+"&sxbm="+sxbm+"&sxid="+sxid+"&sxmc="+sxmc, function() {});
}
function MystrategyinfoList(){
	$("#businessList").show();
	$("#businessDetail").hide();
}
//编辑办事攻略：bid业务流水号，pid事项编码
function EditWiki(bid,pid){
	if(!LEx.isNotNull(bid)){
		LEx.alert("没有指定业务流水号，编辑办事攻略失败！");
	}
	if(!LEx.isNotNull(pid)){
		LEx.alert("没有指定事项编码，编辑办事攻略失败！");
	}
	var cmd = new LEx.Command("app.icity.wiki.BusinessWikiCmd");
	cmd.setParameter("WT.SBLSH", bid);
	cmd.setParameter("WT.USERID", LEx.userInfo.uid.toString());
	var ret = cmd.execute("getBusinessWiki");
	if(!ret.error){
		//LEx.alert(LEx.encode(ret));
		if(ret.total>0){
			var id = ret.data.ID;
			var n_url = LEx.webPath+"admin?m=icity/center/active/business_wiki_info.html&id="+id;
			LEx.dialog({
				id:'wiki_edit',
				title:"办事攻略",
				url:n_url,
				width:900,
				height:500,
				lock:true,
				button:[{
				    name: "保存",
				    callback: function() {
				        var iframe = this.iframe.contentWindow;
				        if (!iframe.document.body) {
				            LEx.alert('内容还没加载完毕呢');
				            return false;
				        }
				        if(iframe.eval("postData()")){
				        	eval("onQuery()");
				        	return true;
				        }else{
				        	return false;
				        }
				    },
				    focus: true
			   },
			   {
				   name: '关闭'
			   }]
			}); 
		}else{
			var n_url = LEx.webPath+"admin?m=icity/center/active/business_wiki_add.html&bid="+bid+"&pid="+pid;
			operationDialog({title: "办事攻略", url: n_url,width:900,height:400});
		}
		
	}else{
		LEx.alert(ret.error);
	}
}