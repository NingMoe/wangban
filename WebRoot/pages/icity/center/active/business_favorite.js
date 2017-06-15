var table = null;
function contentHandler(){
	var tabObj = {};
	tabObj.id = "menu";
	LEx.Control.Tab(tabObj);
	//1.实例化分页table控件：table的id，模版的id
	table = new LEx.Control.Table("bf_list","template");
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
			table.limit(0);
			cmd.setParameter("SXMC@LIKE", $("#name").val());
		}
		cmd.setParameter("start", table.start());
		table.limit(15);
		cmd.setParameter("limit", table.limit());
		var ret =  cmd.execute("getBusinessFavoriteList");
		
		if(!ret.error){		
			//LEx.alert(LEx.encode(ret.data));
			//2.将数据传递给table对象
			table.toBody(ret);
			//3.加载分页控件：分页div的id，Table的实例化对象名称（字符串）,总数量，查询方法（字符串）
			table.toPageBar("divList","table",ret.total,"onQuery()");
			/* $("#divList").html(LEx.processDOMTemplate("template",ProjectItem)); */
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
		content:'备注内容:<br/><textarea id="remark-area" style="width:452px;height:100px;" rows="10">'+remark+'</textarea><br/>',
		fixed:true,
		padding:0,
		id:'remark_1',
		okVal: '确定',
	    ok: function () {
	    	var input = document.getElementById('remark-area').value;
	    	if (input!=null&&input.length>0) {
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
	        	LEx.dialog({
	        		title:'系统提示',
		           	content: '请输入备注!',
		            icon: 'error',
		            fixed: true,
		            lock: true,
		            time: 1.5
		        });
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

function getProject(id){
	var cmd = new LEx.Command("app.icity.ServiceCmd");	
	cmd.setParameter("id", id);
	var ret =  cmd.execute("getPermission");
	if(!ret.error){
		var itemInfo =ret.data.itemBasicInfo;
		if(itemInfo.IS_ONLINE=='1'){
			var html = "<a class='btn btn-xs btn-link btn-primary' onaddress='"+itemInfo.ONLINE_ADDRESS+"' id='"+itemInfo.ID+"' onclick='sp(this);' >在线申报</a>";
			html = html+"<a class='btn btn-xs btn-link' href='{{PageContext.ContextPath}}icity/project/guide?id="+itemInfo.ID+"' target='_blank'>办事指南</a>";
			return html;
		}else{
			var html = "<a class='btn btn-xs btn-link btn-default disabled'>在线申报</a>";
			html = html+"<a class='btn btn-xs btn-link' href='{{PageContext.ContextPath}}icity/project/guide?id="+itemInfo.ID+"' target='_blank'>办事指南</a>";
			return html;
		}
	}else{
		LEx.alert(ret.error);
	}
}
function sp(obj){
    var id = obj.getAttribute("id");
    if(obj.getAttribute("onaddress")==""||obj.getAttribute("onaddress")=="undefined"){
	    window.open("{{PageContext.ContextPath}}onlineapply/applyinfo?itemId="+id,"_blank"); 
    }else{
    	window.open(obj.getAttribute("onaddress")); 
    }
}