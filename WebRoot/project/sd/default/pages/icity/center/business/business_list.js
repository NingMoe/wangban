var bltable = null;
var isCurrentPage=false;//是否查询当前页
var currentStart=0;
var total=0;
$.fn.raty.defaults.path = '{{PageContext.JsPath}}raty_master/lib/images/';
function contentHandler(status){	
	LEx.icityBusiness.initSelect("select_status");	
	//1.实例化分页bltable控件：bltable的id，模版的id
	bltable = new LEx.Control.Table("div_business_list","template_business_list");
	bltable.limit(8);
	if(status != undefined){
		var categoryId = LEx.icityBusiness.getCategoryidByStatus(status);
		$('#select_status option[value="'+categoryId+'"]').attr("selected",'selected');
	}
	if(LEx.urldata.BQBZ)
		$('#select_status').val("bqbz");
	if(LEx.urldata.KEY){
		var now = new Date();
		var m_date = new Date(now.getTime() - 7 * 24 * 3600 * 1000);
		var m_year = m_date.getFullYear();
		var m_month = m_date.getMonth() + 1;
		if(m_month<10){
			m_month="0"+m_month;
		}
		var m_day = m_date.getDate();
		if(m_day<10){
			m_day = "0"+m_day;
		}
		var date = new Date();
		var year = date.getFullYear();
		var month = date.getMonth() + 1;
		if(month<10){
			month="0"+month;
		}
		var day = date.getDate();
		if(day<10){
			day="0"+day;
		}
		$('#APPLYTIME_S').val(m_year+"-"+m_month+"-"+m_day);
		$('#APPLYTIME_E').val(year+"-"+month+"-"+day);
	}		
	onQuery();
}
//分页
function onQuery(){
	if(!isCurrentPage){
		currentStart = bltable.start();//记录本次查询的页
	}
	var cmd = new LEx.Command("app.icity.project.ProjectIndexCmd");
	if(isCurrentPage&&total!=currentStart+1){//删除成功，星级评价成功返回列表，需要查询当前页
		cmd.setParameter("start", currentStart);
	}else{
		cmd.setParameter("start", bltable.start());
	}
	cmd.setParameter("limit", bltable.limit());
	cmd.setParameter("ucid",""+LEx.userInfo.uid);
	var sbxmmc = $('#sbxmmc').val();
	if(LEx.isNotNull(sbxmmc)){
		cmd.setParameter("SBXMMC@like",sbxmmc);
   }	
	if(LEx.isNotNull(LEx.urldata.SBLSH)){
		cmd.setParameter("SBLSH@=",LEx.urldata.SBLSH);
		LEx.urldata.SBLSH = null;		
   }
   var applytime_s = $('#APPLYTIME_S').val();
   if(LEx.isNotNull(applytime_s)){
	   cmd.setParameter("SBSJ@>=@Date",applytime_s+" 00:00:00");
   }
   var applytime_e = $('#APPLYTIME_E').val();
   if(LEx.isNotNull(applytime_s)&&LEx.isNotNull(applytime_e)){
	   if(applytime_s>applytime_e){
		   LEx.alert("截止日期不允许小于开始时间");
	   }  
   }
   if(LEx.isNotNull(applytime_e)){
	   cmd.setParameter("SBSJ@<=@Date",applytime_e+" 23:59:59");
   }
   var business_status = $('#select_status option:selected').attr("code");
   if(LEx.isNotNull(business_status)){
	   cmd.setParameter("STATE@IN",business_status);
   }
   cmd.setParameter("sort"," SBSJ DESC");
	var ret =  cmd.execute("getBusinessIndexList");
	if(!ret.error){//document.write(LEx.encode(ret));
		isCurrentPage=false;
		total=ret.total;
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
	$(".star_level_data").raty({ 
		readOnly: true,
		score:  function() {
			return $(this).attr('data-score');
			} 
		});
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

satisfaction=function(id,itemId,sqrmc){
	var n_url = LEx.webPath+"admin?m=icity/center/business/satisfaction_detail.html&id="+id+"&type=0&sxid="+itemId+"&sqrmc="+sqrmc;
	 LEx.dialog({
	        id: "satisfaction", title: "星级测评",
	        url: n_url,
	        width: 370, height: 180,
	        button: [
				{
				    name: '提交',
				    callback: function() {
				        var iframe = this.iframe.contentWindow;
				        if (!iframe.document.body) {
				            LEx.alert('内容还没加载完毕呢');
				            return false;
				        }
				        if(iframe.submit()){
				        	onQuery();
				        	return true;
				        }else{
				        	return false;
				        }
				    },
				    focus: true
				},
				{
				    name: '关闭'
				}
			]
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
	    		command.setParameter('SBLSH',id);
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
	var n_url="http://203.175.130.241:8089/login/BusinessView.aspx?close=true&sblsh="+id;
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
//撤回
function chehui(obj){
	var cmd = new LEx.Command("app.icity.project.ProjectIndexCmd");
	cmd.setParameter("SBLSH", obj);	
	var ret =  cmd.execute("updateBusinessIndex");
	if(!ret.error){	
		LEx.alert("撤回成功！");
		onQuery();
	}else{
		LEx.alert(ret.error);
	}
}

function complain(a,b,c,d){
	document.location="{{PageContext.ContextPath}}icity/complain?sblsh="+a+"&sbxmmc="+encodeURIComponent(b)+"&sxid="+c+"&sxmc="+encodeURIComponent(d);
}
//删除
function deleteProject(sblsh){
	LEx.dialog({
        id: 'Confirm',
        title:'删除提示',
        fixed: true,
        lock: true,
        opacity: .1,
        content: "确定删除？",
        ok: function () {
        	var cmd = new LEx.Command("app.icity.project.ProjectIndexCmd");
        	cmd.setParameter("ids",sblsh);
        	var ret =  cmd.execute("deleteBusiness");
        	if(!ret.error){	
        		isCurrentPage=true;
        		onQuery();
        		LEx.alert("删除成功！");
        	}else{
        		LEx.alert("删除失败！");
        	}
        },
        cancel: function () {
            this.close();
        }
    });
}

function formatDate(obj) {
	return LEx.util.Format.formatDate(obj);
}
