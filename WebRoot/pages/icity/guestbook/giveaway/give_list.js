	var dg1;//定义一个datagrid对象
	
	//页面初始化位置	
	$(document).ready(function(){
		//查询控件初始化
		initNavigation();
		
		
		arrPurview = LEx.BspCmd.mypurview();
		
		//加载datagrid控件，并将对象映射给datagrid对象
		new LEx.Control.datagrid('datagrid1',function(obj){
			
			dg1 = obj;					//对象映射
			dg1.toPageBar(onQuery);		//初始化分页事件
			//后台交互相关格式化
			dg1.addFormatter([]);
			
			//添加事件
			dg1.addEvent({
				onDblClickRow:function(rowIndex, rowData){
					var n_status = rowData.STATUS;
					var n_give_status= rowData.GIVE_STATUS;
					if(!isNotNull(n_status)){
						n_status = 0;
				   	}
					var n_url = LEx.webPath+"admin?m=icity/guestbook/giveaway/give_detail.html&id="+rowData.ID+"&status="+n_status+"&give_status="+n_give_status;
					checkDialog({title: "详细信息", url: n_url,width:900,height:400,status:n_status,give_status:n_give_status});
				}
			});
			onQuery();					//执行默认的查询方法
		});
	});
	
	var subCaption = "所有";
	
	//查询处理函数
	function onQuery(){
		//定义command对象
		var command=new LEx.Command("app.icity.guestbook.ReplyCmd");
		//参数设置
		if(subCaption == "已分发"){
			command.setParameter("GIVE_STATUS@=",1);
		}else if(subCaption == "未分发"){
			command.setParameter("GIVE_STATUS@=",0);
		}
		var title = $("#title").val();
		if(LEx.isNotNull(title)){
			command.setParameter("TITLE@like",title);
		}
		
		var type = $("#type").val();
		if(LEx.isNotNull(type)){
			command.setParameter("TYPE@=",type);
		}
		
		command.setParameter("TYPE@in", '0,3,5,10,11');
		command.setParameter("STATUS@=", '0');
		command.setParameter("start",dg1.start());
		command.setParameter("limit",dg1.limit());
		command.setParameter("REGION_ID",LEx.userInfo.regionId);
		command.execute("getList");
		if(!command.error){
			var ret = command.returns;//读取后台查询结果数据
			dg1.loadData(ret);
			
		}else{
			alert(command.error);//打印错误信息
		}
	}
	function changeCss(obj){
		$('div[class*=ntab][class*=on]').removeClass("on");
		$(obj).addClass("on");
		onQuery();
	}
	
	
	
	//分发事件
	function onNew(){
		var datas = dg1.getChecked(); 
		if(datas.length==0){
			LEx.dialog.tips('请选择需要分发的数据!');
			return;
		}
		var rootId = LEx.userInfo.deptId;
		if(arrPurview.superrole){
			rootId=BASE_DEPT();
		}
		LEx.dialog({
    		title:'选择分发部门',
           	url:LEx.webPath+"admin?m=icity/guestbook/giveaway/deptTree.html&rootId="+rootId,
            fixed: true,
            width:350,
            height:450,
            lock: true,
            button: [{
    		    name: "确定",
    		    callback: function() {
    		    	var iframe = this.iframe.contentWindow;
    		    	if (!iframe.document.body) {
			          LEx.alert('内容还没加载完毕呢');
			          return false;
			}

			var result = iframe.postData();
			if(!$.isEmptyObject(result)){
				var dept_id=result.dept_id;
				var dept_name=result.dept_name;
			}
			
			LEx.dialog({
				content: '是否要分发至【'+dept_name+'】处理？',
				ok: function () {
					var command = new LEx.Command("app.icity.guestbook.ReplyCmd");
					var pvalue = [];				
					for(var i=0;i<datas.length;i++){
						pvalue.push(datas[i].ID);	
					}
					command.setParameter("ids",pvalue.join(","));
					command.setParameter("dept_id",dept_id);
					command.setParameter("dept_name",dept_name);
					var ret = command.execute("giveaway");
					if(ret.state == 1){
						//initDeptTree();
						onQuery();
					}else{
						LEx.dialog({title:"系统提示",content:ret.message,icon: 'error',lock:true});
					}
				},
				icon:"question",
				cancelVal: '关闭',
				cancel: true
			}); 
			
    		    },
    		    focus: true
    	   }]
       });
		/*
		LEx.dialog({
			content: '是否要分发至【'+dept_name+'】处理？',
			ok: function () {
				var command = new LEx.Command("app.icity.guestbook.ReplyCmd");
				var pvalue = [];				
				for(var i=0;i<datas.length;i++){
					pvalue.push(datas[i].ID);	
				}
				command.setParameter("ids",pvalue.join(","));
				command.setParameter("dept_id",dept_id);
				command.setParameter("dept_name",dept_name);
				var ret = command.execute("giveaway");
				if(ret.state == 1){
					//initDeptTree();
					onQuery();
				}else{
					LEx.dialog({title:"系统提示",content:ret.message,icon: 'error',lock:true});
				}
			},
			icon:"question",
			cancelVal: '关闭',
			cancel: true
		}); 
		*/
	}
	
	//删除事件
	function onDel(){
		var datas = dg1.getChecked(); 
		var n_command = new LEx.Command("app.icity.guestbook.ReplyCmd");
		var pvalue = [];
		for(var i=0;i<datas.length;i++){
			pvalue.push(datas[i].ID);	
		}
		n_command.setParameter("ids",pvalue.join(","));
		deleteDialog({data: datas,command: n_command});
	}
	
	
	function checkDialog(obj){
		if(!isNotNull(obj.title)){
	   		obj.title = "操作";
	   	}
		if(!isNotNull(obj.width)){//宽度
	   		obj.width = 800;
	   	}
	   	if(!isNotNull(obj.height)){//高度
	   		obj.height = 280;
	   	} 	
	    if(!isNotNull(obj.button)){//如果页面未定义button，采用默认
	    	if(!isNotNull(obj.qmName)){//回调执行方法
	    		obj.qmName = "onQuery()";
	    	}
	    	if(!isNotNull(obj.pmName1)){//弹出框执行具体操作的方法
	    		obj.pmName1 = "postData1()";
	    	}
	    	/*
	    	if(!isNotNull(obj.pmName2)){//弹出框执行具体操作的方法
	    		obj.pmName2 = "postData2()";
	    	}
	    	*/
	    }
	    if(obj.status=="1"||obj.give_status=="1"){
	    	obj.button = [{ name: '关闭'}];
	    }else{
	    	obj.button = [             
	    	{
			    name: "回复",
			    callback: function() {
			        var iframe = this.iframe.contentWindow;
			        if (!iframe.document.body) {
			            LEx.alert('内容还没加载完毕呢');
			            return false;
			        }
			        if(iframe.eval(obj.pmName1)){
			        	eval(obj.qmName);
			        	return true;
			        }else{
			        	return false;
			        }
			    },
			    focus: true
		   },
		   /*
		   {
			    name: "不通过",
			    callback: function() {
			        var iframe = this.iframe.contentWindow;
			        if (!iframe.document.body) {
			            LEx.alert('内容还没加载完毕呢');
			            return false;
			        }
			        if(iframe.eval(obj.pmName2)){
			        	eval(obj.qmName);
			        	return true;
			        }else{
			        	return false;
			        }
			    }
		   },
		   */
		   {
			   name: '关闭'
		   }];
	    }
	    
		LEx.dialog(obj);
	}
	
	//时间格式，如果不为空，按照空间格式显示时间，如果为空，显示“—”
	function formatDate(val,row){
		if(isNotNull(val)){
			return LEx.util.Format.formatDate(val,"yyyy-MM-dd HH:mm:ss");
		}else{
			return "—" ;
		}
	}
    
	//是否公开的状态转换
	function formatFlag(val,row){
		if(val=="1"){
			return "是";
		}else if(val == "0"){
			return "否";
		}else{
			return "";
		}
	}
	
	//审核是否通过的状态转换
	function formatCheckFlag(val,row){
		if(val=="1"){
			return "审核通过";
		}else if(val == "2"){
			return "审核不通过";
		}else if(val=="0"){
			return "未审核";
		}
	}
	
	function formatGiveStatus(val,row){
		if(val=="1"){
			return "已分发";
		}else if(val == "0"){
			return "未分发";
		}
	}
	
	function formatGiveDeptName(val,row){
		if(isNotNull(val)){
			return val;
		}else{
			return "——";
		}
	}
	
	function formatDeptName(val,row){
		if(isNotNull(val)){
			return val;
		}else{
			return "无部门";
		}
	}
	
	//咨询类型的状态转换
	function formatType(val,row){
		var type={"0":"建议","1":"表扬","2":"咨询","3":"投诉","4":"其他","5":"意见","10":"联系我们-意见建议","11":"联系我们-纠错"}
		
		return type[val];
		if(val=="0"){
			return "建议";
		}else if(val == "1"){
			return "表扬";
		}else if(val=="2"){
			return "咨询";
		}else if(val=="3"){
			return "投诉";
		}else if(val=="4"){
			return "其他";
		}else if(val=="5"){
			return "意见";
		}
	}
