var bltable = null;
function contentHandler(){
	//1.实例化分页table控件：table的id，模版的id
	bltable = new LEx.Control.Table("div_business_list","template_business_list");
	onQuery(); 
}

$("#submit").click(function(){
	 onQuery();
	 $("table input[type=checkbox]").attr("checked",false);
});

$("#delete").click(function(){
	 onDel();
	 $("table input[type=checkbox]").attr("checked",false);
});



//分页
function onQuery(){
		var cmd = new LEx.Command("app.icity.favorite.ProjectFavoriteCmd");
		if(!($("#name").val()==""||$("#name").val()==null)){
			bltable.limit(0);
			cmd.setParameter("SXMC@LIKE", $("#name").val());
		}
		cmd.setParameter("start", bltable.start());
		bltable.limit(10);
		cmd.setParameter("limit", bltable.limit());
		var ret =  cmd.execute("getBusinessFavoriteList");
		
		if(!ret.error){		
			//LEx.alert(LEx.encode(ret.data));
			//2.将数据传递给table对象
			bltable.toBody(ret);
			//3.加载分页控件：分页div的id，Table的实例化对象名称（字符串）,总数量，查询方法（字符串）
			bltable.toPageBar("epager","bltable",ret.total,"onQuery()");
			$("#div_business_list").html(LEx.processDOMTemplate("template_business_list",ret));
		}else{
			LEx.alert(ret.error);
		}
}



//删除事件
function onDel(){
	var command = new LEx.Command("app.icity.favorite.ProjectFavoriteCmd");
	var pvalue = [];
	$('#bodys input:checked').each(function(){ 
		pvalue.push($(this).attr("tag"));
	});
	command.setParameter("ids",pvalue.join(","));
	var ret =  command.execute("personaldelete");
	if(!ret.error){	
		onQuery();
		LEx.alert("删除收藏成功！");
	}else{
		LEx.alert(ret.error);
	}
}


function EditRemark(id,remark){
	LEx.dialog({
		title:'我要备注',
		width:'460px',
		content:'备注内容:<br/><textarea  id="remark-area" style="width:452px;height:100px;" rows="5">'+remark+'</textarea><br/><span style="margin-left: 2px;">（注：字数1~20）</span><span id="tip" style="color: red;margin-left: 10px;"></span>',
		fixed:true,
		padding:0,
		id:'remark_1',
		okVal: '确定',
	    ok: function () {
	    	var input = document.getElementById('remark-area').value;
	    	if (input!=null&&input.length>0) {
	    		if(input.length>=20){
	    			$("#tip").html("内容控制在20字以内！");
	    			return false;
	    		}
	    		var command = new LEx.Command('app.icity.favorite.ProjectFavoriteCmd');
	    		command.setParameter('ID',id);
	    		command.setParameter('REMARK',input);
	    		var ret = command.execute('updateRemark');
	    		if(ret.state == 1){
		    		 LEx.dialog({
				        	title:'系统提示',
			            	content: '备注更新成功！',
			                icon: 'succeed',
			                fixed: true,
			                lock: true,
			                time: 1.5
			         });
		    		 onQuery();
	    		}else{
	    			 LEx.dialog({
				        	title:'系统提示',
			            	content: '备注更新失败！',
			                icon: 'error',
			                fixed: true,
			                lock: true,
			                time: 1.5
			         });
	    		}
	        } else {
	        	$("#tip").html("请输入备注!");
	    		return false;
	        };
	    },
	    cancel: true
	});	
}






function selectAll(){
	 if($("#checkall").attr("checked")=="checked"){
		 $("table input[type=checkbox]").attr("checked",true);
	 }else{
		 $("table input[type=checkbox]").attr("checked",false);
	 }
}

function formatDate(obj){
	return LEx.util.Format.formatDate(obj);
}
