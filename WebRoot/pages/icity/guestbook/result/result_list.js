	var dg1;//定义一个datagrid对象
	
	//页面初始化位置	
	$(document).ready(function(){
		//查询控件初始化
		initNavigation();
		
		arrPurview = LEx.BspCmd.mypurview();
		var dept = LEx.userInfo.deptId;
		if(arrPurview.superrole){
			dept = BASE_DEPT();
		}
		$("#dept").attr("code",dept);
		if(arrPurview.superrole){
			LEx.Control.Depart({id:"dept",type:"radio",rootId:BASE_DEPT()});
		}else{
			LEx.Control.Depart({id:"dept",type:"radio",rootId:dept});
		}
		
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
					var n_checks = rowData.CHECKS;
					if(!isNotNull(n_checks)){//高度
						n_checks = 0;
				   	}
					var n_url = LEx.webPath+"admin?m=icity/guestbook/result/result_detail.html&id="+rowData.ID+"&checks="+n_checks+"&status="+n_status;
					checkDialog({title: "详细信息", url: n_url,width:900,height:400,checks:n_checks,status:n_status});
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
		if(subCaption == "咨询"){
			command.setParameter("TYPE@=",2);
		}else if(subCaption == "投诉"){
			command.setParameter("TYPE@=",3);
		}else if(subCaption == "建议"){
			command.setParameter("TYPE@=",0);
		}else if(subCaption == "意见"){
			command.setParameter("TYPE@=",5);
		}
		
		var type = $("#type").val();
		if(LEx.isNotNull(type)){
			command.setParameter("TYPE@=",type);
		}
		
		var title = $("#title").val();
		if(LEx.isNotNull(title)){
			command.setParameter("TITLE@like",title);
		}
		
		command.setParameter("CHECKS@=",1);
		command.setParameter("dept", $("#dept").attr("code"));
		command.setParameter("start",dg1.start());
		command.setParameter("limit",dg1.limit());
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
	
	//咨询类型的状态转换
	function formatType(val,row){
        var type={"0":"建议","1":"表扬","2":"咨询","3":"投诉","4":"其他","5":"意见","10":"联系我们-意见建议","11":"联系我们-纠错"}
        return type[val];
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
	    }    
	    obj.button = [{ name: '关闭'}];		
		LEx.dialog(obj);
	}
