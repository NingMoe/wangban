var bltable = null;

function contentHandler(status){
	if(_calendar != null){
		_calendar.setup({id:"APPLYTIME_S"});
		_calendar.setup({id:"APPLYTIME_E"});
	}else{
		_calendar= new LEx.Control.Calendar(function(){
			_calendar.setup({id:"APPLYTIME_S"});
			_calendar.setup({id:"APPLYTIME_E"});
		});
	}
	
	LEx.icityBusiness.initSelect("select_status");
	
	//1.实例化分页bltable控件：bltable的id，模版的id
	bltable = new LEx.Control.Table("bl_list","template_business_enter");
	bltable.limit(5);
	if(status != undefined){
		var categoryId = LEx.icityBusiness.getCategoryidByStatus(status);
		$('#select_status option[value="'+categoryId+'"]').attr("selected",'selected');
	}
	onQuery();
}

//分页
function onQuery(){
	var cmd = new LEx.Command("app.icity.project.ProjectIndexCmd");
	cmd.setParameter("start", bltable.start());
	cmd.setParameter("limit", bltable.limit());
	cmd.setParameter("ucid",""+LEx.userInfo.uid);
	var sbbh = $('#sbbh').val();
	if(LEx.isNotNull(sbbh)){
		cmd.setParameter("FIRST_GRADE_CODE@like",sbbh);
   }
   var applytime_s = $('#APPLYTIME_S').val();
   if(LEx.isNotNull(applytime_s)){
	   cmd.setParameter("APPLY_TIME@>=@Date",applytime_s+" 00:00:00");
   }
   var applytime_e = $('#APPLYTIME_E').val();
   if(LEx.isNotNull(applytime_e)){
	   cmd.setParameter("APPLY_TIME@<=@Date",applytime_e+" 23:59:59");
   }
   var business_status = $('#select_status option:selected').attr("code");
   if(LEx.isNotNull(business_status)){
	   cmd.setParameter("CURRENT_STATE@IN",business_status);
   }
   cmd.setParameter("sort"," APPLY_TIME DESC");
	var ret =  cmd.execute("getBusinessEnterList");
	if(!ret.error){	
		//2.将数据传递给bltable对象
		bltable.toBody(ret);
		//3.加载分页控件：分页div的id，bltable的实例化对象名称（字符串）,总数量，查询方法（字符串）
		bltable.toPageBar("raiderpager_business_enter","bltable",ret.total,"onQuery()");
		$("#bldata").html(LEx.processDOMTemplate("template_business_enter",ret));
		$(".NeedFormatState").each(function(){
			var status=$(this).attr("state");
			var bState = LEx.icityBusiness.formatStatus(status);
			$(this).html(bState);
//			var bState ="";
//			if(status=="00"){
//				bState ="暂存";
//			}else if(status=="01"){
//				bState ="提交";
//			}else if(status=="02"){
//				bState ="预审通过";
//			}
//            else if(status=="99"){
//            	bState ="办结";
//			}else{
//				bState = "办理中";
//			}
//			$(this).html(bState);
		});
	}else{
		LEx.alert(ret.error);
	}
}
function submit(id){
	$("#"+id).submit();
}
ChargeStandard=function(id){
		var n_url = LEx.webPath+"admin?m=icity/center/business/business_charge.html&id="+id;
			LEx.dialog({
				id:'',
				title:"收费标准",
				url:n_url,
				width:800,
				height:400,
				lock:true
			}); 
}

BusinessDetail=function(id){
//	var command = new LEx.Command("app.icity.sync.PowerBaseInfoSyncCmd");
//	command.setParameter("sbbh",id);
 //   command.execute("updatebaseinfo");
    
  //  if(!command.error){
   // }
	/*
	 * type 0:业务列表      1：企业设立        2：工程建设 
	 */
	var n_url = LEx.webPath+"admin?m=icity/center/business/businessenterdetail.html&id="+id+"&type=1";
		LEx.dialog({
			id:'',
			title:"业务详情",
			url:n_url,
			width:890,
			height:400,
			lock:true
		}); 
}





function fomatterDate(val){
	if(LEx.isNotNull(val)){		
		//return LEx.util.Format.formatDate(val,"yyyy-MM-dd hh:mm:ss");
		if(LEx.util.Format.formatDate(val,"HH")>=13&&LEx.util.Format.formatDate(val,"HH")<=23){
		    return LEx.util.Format.formatDate(val,"yyyy-MM-dd hh:mm:ss")+"下午";	
		}else{
			return LEx.util.Format.formatDate(val,"yyyy-MM-dd hh:mm:ss")+"上午";
		}
		    
		
	}else{
		return '-';
	}
}

/**
 * 格式化提交方式
 */
function  fomatterSBFS(val){
	 if(val=='1'||!LEx.isNotNull(val)){
		 return '网上提交';
	 }else{
		 return '窗口提交';		 
	 }
	
}

/**
 * 计算应办结日期
 * m:申请日期 n：承诺日期
 * */
function cal(m,n){
	if(LEx.isNotNull(m)){
		var millions = m.time + n*24*60*60*1000;
		var date = {time:millions};
		return LEx.util.Format.formatDate(date,"yyyy-MM-dd HH:mm:ss");
	}else{
		return '-';
	}
	
}

function del(type,ids){
	var pvalue = [];
	if(type == 'delAll'){
		pvalue = getAll();
	}else if(type == 'delSingle'){
		pvalue.push(ids);
		
	}
	var cmd = new LEx.Command("app.icity.project.ProjectIndexCmd");
	var datas = pvalue.join(",");
	cmd.setParameter("ids",datas);
	deleteDialog({data: datas,command: cmd,pmName: "deleteBusiness"});
}





//备注
function beRemark(id,remark){
	LEx.dialog({
		title:'我要备忘',
		content:'<textarea id="remark-area" style="margin-top:2px;width:452px;height:120px;" rows="10">'+remark+'</textarea><br/>',
		fixed:true,
		padding:0,
		id:'remark_1',
		okVal: '确定',
	    ok: function () {
	    	var input = document.getElementById('remark-area').value;
	    	if (input!=null&&input.length>0) {
	    		var command = new LEx.Command('app.icity.project.ProjectIndexCmd');
	    		command.setParameter('sbbh',id);
	    		command.setParameter('REMARK',LEx.htmlEncode(input));
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



/**
 * 批量出证
 */
function certificateConfirm(){
	var checkArr = $('#data input:checked');
	var pvalue = [];
	var flag = true;
	$.each(checkArr,function(i,n){
		if (flag) {
			if($(n).attr('n_status') != 7){
				LEx.alert('不能确认未出证的业务!');
				flag = false;
			}
			pvalue.push($(n).val());
		}
	});
	if(flag) {
		var cmd = new LEx.Command("app.icity.project.ProjectIndexCmd");
		cmd.setParameter("ids",pvalue.join(","));
		var ret = cmd.execute("certificateConfirm");
		if(!ret.error){		
			onQuery();
		}else{
			LEx.alert(ret.error);
		}
	}
	return false;
}






/**
 * 批量删除
 */
function getAll(){
	var checkArr = $('#data input:checked');
	var pvalue = [];
	$.each(checkArr,function(i,n){
		pvalue.push($(n).val());
	});
	return pvalue;
}

/**
 * 全选
*/

function selectAll(obj){
	var checkArr;
	if($(obj).attr('checked') == 'checked'){
		checkArr =  $('#data input[checked!=checked]');
		$.each(checkArr,function(i,n){
			$(n).attr('checked','checked');
		});
	}else{
		checkArr =  $('#data input:checked');
		$.each(checkArr,function(i,n){
			$(n).removeAttr('checked');
		});
	}
}



/**
 * 办事攻略
*/

function raiders(id,bid,pid){
	var n_url = LEx.webPath+"admin?m=icity/center/business/raiders.html&id="+id+"&bid="+bid+"&pid="+pid;
	operationDialog({title: "办事攻略", url: n_url,width:600,height: 400,pmName:"postData()"});
}

/**
 * 办事评价
*/

function evaluate(id){
	var n_url = LEx.webPath+"center/evaluate/evaluate?BID="+id;
	operationDialog({title: "办事攻略", url: n_url,width:920,height: 400});
}

/**
 * 业务详情
 */
function detail(id){
	var n_url=LEx.webPath+"icity/center/business/detail?BID="+id;
	operationDialog({title: "业务详情",url: n_url,width:920,height: 500});
}

function detail_2(id){
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
		    	$.get(LEx.webPath+"icity/center/CheckSubmitData",{id:id},function(data,status){
		    		alert("status:" + status + "   Data Loaded: " + data);
		    		location.href=LEx.webPath+"center/index";
		    	});
		    	
		    	
		    },
		    focus: true
	   },
	   {
		    name: "提交出现问题",
		    callback: function() {
		       
		    }
	   }]
	});
	var n_url="http://203.175.130.241:8089/login/BusinessView.aspx?close=true&sbbh="+id;
	window.open(n_url);
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
	cmd.setParameter("WT.sbbh", bid);
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
//撤回
function chehui(obj){//alert(obj);
	var cmd = new LEx.Command("app.icity.project.ProjectIndexCmd");
	cmd.setParameter("SBLSH", obj);	
	cmd.setParameter("STATE", '00');	

	var ret =  cmd.execute("updateBusinessIndex");
	if(!ret.error){	
		onQuery();
	}else{
		LEx.alert(ret.error);
	}
}

