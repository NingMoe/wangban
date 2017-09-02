//弹出框
LEx.dialog = function(obj) {
    obj.background = '#ccc'; // 背景色
    obj.opacity = 0.5; // 透明度
    // 临时解决标题过长撑大整个层问题
    if (obj.title != null) {
        if (obj.width != null) {
            if (typeof (obj.width) == "number") {
                var len = obj.width / 14;
                if (len < obj.title.length) {
                    obj.title = obj.title.substring(0, len) + "...";
                }
            }
        }
    }
    if(obj.lock == null){
    	obj.lock = true;
    }
    var p=null,d=null;
    try {
    	p = (parent.$.dialog == null) ? plugin : parent.plugin;
	} 
    catch (e) {
	p=plugin;
	}
    try {
    	d = (parent.$.dialog == null) ? $.dialog : parent.$.dialog;
    	}
    	catch (e) {
    		 p.ready("dialog", function() {
     	    	d=$.dialog;
     	    	if (obj.url) {
     	            d.open(obj.url, obj);
     	        } else {
     	            d(obj);
     	        }
     	    });
    		return;
    	}
    	if(d==null){
    		 p.ready("dialog", function() {
      	    	d=$.dialog;
      	    	if (obj.url) {
      	            d.open(obj.url, obj);
      	        } else {
      	            d(obj);
      	        }
      	    });
    		 return;
    	}
    	if (obj.url) {
	            d.open(obj.url, obj);
	        } else {
	            d(obj);
	        }
   
};
/**
 * 确认
 * @param	{String}	消息内容
 * @param	{Function}	确定按钮回调函数
 * @param	{Function}	取消按钮回调函数
 */
LEx.dialog.confirm = function (content, yes, no) {
    return LEx.dialog({
        id: 'Confirm',
        icon: 'question',
        title:'系统提示',
        fixed: true,
        lock: true,
        opacity: .1,
        content: content,
        ok: function (here) {
            return yes.call(this, here);
        },
        cancel: function (here) {
            return no && no.call(this, here);
        }
    });
};

/**
 * 短暂提示
 * @param	{String}	提示内容
 * @param	{Number}	显示时间 (默认1.5秒)
 */
LEx.dialog.tips = function (content, time,fun) {
	var data = '<div style="padding: 0 1em;">' + content + '</div>';
	if(fun== undefined){
		fun = function(){};
	}
    return LEx.dialog({
        id: 'Tips',
        title: false,
        content:data,
        cancel: false,
        fixed: true,
        lock: false,
        close:fun,
        time:(time || 4)
    });
};

/**
 * 输入提示
 * @param	{String}	消息内容
 * @param	{Function}	确定按钮回调函数
 * @param	{Function}	取消按钮回调函数
 */
LEx.dialog.prompt = function (content, yes,no) {
	var input;
    return LEx.dialog({
        id: 'Prompt',
        icon: 'question',
        title:'系统提示',
        fixed: true,
        lock: true,
        opacity: .1,
        content: [
			'<div style="margin-bottom:5px;font-size:12px">',
				content,
			'</div>',
			'<div>',
				'<input value="',
				'" style="width:18em;padding:6px 4px" />',
			'</div>'
			].join(''),
		init: function () {
			input = this.DOM.content.find('input')[0];
			input.select();
			input.focus();
		},
        ok: function () {
            return yes.call(this,input.value);
        },
        cancel: function (here) {
            return no && no.call(this, here);
        }
    });
};

// 关闭窗口
LEx.close =function(id)
{
	try{
		var d = (parent.$.dialog == null) ? $.dialog : parent.$.dialog;
		var list =d.list;
		if(list){
			for (var i in list) {
				if(i == id){
					list[i].close();
					break;
				}
			}
		}
	}catch(e){
		alert(e.message);
	}
}


LEx.Control = {};
/*
 * 树形控件
 */
LEx.Control.Tree = function(containerId, setting, ret, fun) {
    plugin.ready("tree", function() {
        this.tree = $.fn.zTree.init($("#" + containerId), setting, ret);
        if (fun != undefined) {
            var s = eval(fun);
            s.call(s, this.tree);
        }
    });
}
LEx.Control.Tree.getZTreeObj = function(id) {
	if($.fn.zTree == undefined){
		alert("请在执行完LEx.Control.Tree的回调后再执行此方法");
		return;
	}
    return $.fn.zTree.getZTreeObj(id);
};

// 时间控件（id:对象id；max:可选的最大日期，默认为null，格式20120212；flag:是否显示当前时间，1显示，0不显示，默认0；formatString:格式化，默认格式%Y-%m-%d）
LEx.Control.Calendar = function(fun) {
    var obj = this;
    plugin.ready("calendar", function() {
        if (fun) {
            var s = eval(fun);
            s.call(s,obj);
        }
    });
};

LEx.Control.Calendar.prototype = {
    setup: function(data) {
        if (data == null) {
            data = {};
        }
        if (data.id) {
            if (data.inputField == null) {
                data.inputField = data.id;
            }
            if (data.trigger == null) {
                data.trigger = data.id;
            }
        }
        if (data.formatString == "" || data.formatString == null) data.formatString = "%Y-%m-%d";
        if (data.flag == "1") {
            var date = new Date();
            $("#" + data.id).attr("value", Calendar.printDate(date, data.formatString));
        }
        if (!data.onSelect) {
            data.onSelect = function() { this.hide(); };
        }
        if (!data.minuteStep) {
            data.minuteStep = 1;
        }
        if (!data.weekNumbers) {
            data.weekNumbers = true;
        }
        if (!data.showTime) {
            data.showTime = false;
        }
        // data.max = null;
        Calendar.setup(data);
    }
};
LEx.Control.Date=function(data){
	if(typeof data == "string"){
		var tmp = data;
		data = {};
		data.id = tmp;
	}
	
	if(data == null || data.id == null){
		alert("LEx.Control.Date中至少包含id参数，格式为：{id:'date'}");
		return;	
	}
	if(data.defTab == null){
		data.defTab = 0;	
	}
	if(data.enableTab == null){
		data.enableTab = true;	
	}
	if(data.enableClear == null){
		data.enableClear = true;	
	}
	$("#"+data.id).attr("readonly","readonly");
	var btns = [];
	btns.push({
					name: '确定',
					callback: function () {
						var iframe = this.iframe.contentWindow;
						if (!iframe.document.body) {
							alert('内容还没加载完毕呢');
							return false;
						}
						var o = iframe.getData();
						if(o.state == false){
							return false;	
						}
						$("#"+data.id).val(o.date);
						return true;
					},
					focus: true
				});
	if(data.enableClear != false){
			btns.push({
					name: '清空',
					callback: function () {
						$("#"+data.id).val("");
						return true;
					}
				});
	}
	$("#"+data.id).click(function(e) {
       	LEx.dialog({
			id:data.id,
			title:"选择时间范围",
			url:LEx.webPath+"admin?m=bsp/date.html&defTab="+data.defTab+"&enableTab="+data.enableTab,
			width:580,
			height:250,
			lock:true,
			button: btns
		});
    });
};

LEx.Control.Tab = function(data) {
    if (typeof data == "string") {
        var tmp = data;
        data = {};
        data.id = tmp;
        data.defTab = 0;
        data.enableTab = true;
        data.special_list = [];
    } else {
        if (data.defTab == null) {
            data.defTab = 0;
        }
        if (data.enableTab == null) {
            data.enableTab = true;
        }
        if (data.special_list == null) {
            data.special_list = [];
        }
    }
    $("#" + data.id + " a.tab").click(function() {
        var content_show = $(this).attr("tag");
        var special_flag = false;
        $.each(data.special_list,function(i,n){
            if(content_show == n){
                special_flag = true;
            }
        });
        if (data.enableTab == true || special_flag) {
            // switch all tabs off
            $("#" + data.id + " .active").removeClass("active");
            // switch this tab on
            $(this).addClass("active");
            // slide all content up
            $("#" + data.id + " .tabs-content").hide();
            // slide this content up
            // var content_show = $(this).attr("tag");
            $("#" + data.id + " #" + content_show).show();
        }
    });

    $("#" + data.id + " a.tab").each(function(index, element) {
        if (index == data.defTab) {
            $("#" + data.id + " .active").removeClass("active");
            $(this).addClass("active");
            $("#" + data.id + " .tabs-content").hide();
            // slide this content up
            var content_show = $(this).attr("tag");
            $("#" + data.id + " #" + content_show).show();
        }
    });
};


// 核心控件
LEx.LUI = {};
LEx.LUI.init = function(obj) {
    try {
        $.parser.parse(obj);
    }
    catch (e) {
        //alert("渲染页面出错："+e.message);
    }
}





/**
 * tableId:表格的Id templateId:模版的Id
 */
LEx.Control.Table  = function(tableId,templateId){
	this.tableId = tableId;
	this.templateId = templateId;
}
var pagenum=[];
LEx.Control.Table.prototype = {
	start:function(){// 页面开始位置
		var index = this.pageIndex();
		if(pagenum.length==1){
			index = this.pageIndex(1);
		}
		index = (index-1)*this.limit();
		if(index<0){
			index =0;
		}
		
		return index;
	},
	limit:function(i){// 获取/设置页面大小
		if(i != null)
		{
			this._limit = i;
		}
		if(this._limit == undefined){
			this._limit = 10;
		}
		return this._limit;
	},
	pageIndex:function(i){// 获取/设置页面索引号，从1开始	
		pagenum.push(1);
		if(i != null)
		{
			this._pageIndex = i;
		}
		if(this._pageIndex == undefined){
			this._pageIndex = 1;
		}		
		return this._pageIndex;
	},
	getHtml:function(data){
		return LEx.processDOMTemplate(this.templateId,data);
	},
	toBody:function(data){
		$("#"+this.tableId).find("tbody").html("");
		$("#"+this.tableId).find("tbody").append(LEx.processDOMTemplate(this.templateId,data));
		$("#"+this.tableId).find("tr:even").addClass("even");
		$("#"+this.tableId).attr("cellspacing","1");
		return true;
	},
	toFoot:function(data){
		$("#"+this.tableId).find("tfoot").html("");
		$("#"+this.tableId).find("tfoot").append(LEx.processDOMTemplate(this.templateId,data));
		return true;
	},
	toPageBar:function(targetId,tempId,total,fun,maxSize,className){
		$("#"+targetId).html(this.lygPagebar(tempId,total,fun,maxSize,className));
	},
	//lianyungang分页样式
	toLygPageBar:function(targetId,tempId,total,fun,maxSize,className){
		$("#"+targetId).html(this.lygPagebar(tempId,total,fun,maxSize,className));
	},
	toRegisterPageBar:function(targetId,box,bltable,total,funName,maxSize,className){
		$("#"+targetId).html(this.registerPageBar(total,maxSize,className));
		box.find(".page_btn").unbind('click').bind('click',function(){
			var pindex = $(this).attr("pageindex");
        	bltable.pageIndex(pindex);
            box.notify({type:funName,data:null});
        });
	},
	toKey:function(serviceName,containerId,dataType){
		if(dataType == null){dataType = "";}
		var keys = [];
		$("#"+containerId+" ."+serviceName+"_tmplate_depart_id_"+dataType).each(function(){
			keys.push($(this).attr("v"));
		})
		//去掉重复对象
		var ids = LEx.arrayUnique(keys).join(",");
		var arr = LEx.BspCmd.keyValues(serviceName,ids,dataType);
		$("#"+containerId+" ."+serviceName+"_tmplate_depart_id_"+dataType).each(function(){
			for(var i=0;i<arr.length;i++){
				if(arr[i].KEY == $(this).attr("v")){
					if(arr[i].VALUE!=null && arr[i].VALUE != 'null'){
						$(this).html(arr[i].VALUE);
						$(this).attr("title", arr[i].VALUE);
					}
				}
			}
		});
	},
	pagebar:function(id,total,fun,maxSize,className){
		if(total == undefined){
			total = 1;
		}
		var index = this.pageIndex();
		pagenum.length=0;
		var pSize = this.limit();
		// 样式名称
		if(className == null){
			className = "pages";
		}
		// 最大显示多少个分页
		if(maxSize == null){
			maxSize = 6;
		}
		var startPage = 1;// 开始页
		var totalPage = 1;
		if(total%pSize==0){
			totalPage = total/pSize;
		}else{
			totalPage = parseInt((total/pSize))+1;
		}
		if(totalPage<1){
			totalPage = 1;
		}
		if(index<1){
			index= 1;
		}
		if(index>totalPage){
			index = totalPage;
		}
		if(totalPage <2){
			return "";
		}
		var re = [];
		re.push("<div class=\""+className+"\">");
		re.push("<a class=\"a1\">"+total+"条</a>")
		
		// 结束页处理
		if(index<=(maxSize/2-1))
		{
			startPage =1;
		}
		else{
			startPage = Math.ceil(index- (maxSize/2-1));
		}
		if(startPage+maxSize>totalPage){
			startPage = totalPage-maxSize+1;
		}
		if(startPage<1){
			startPage = 1;
		}
		if(index>1){
			re.push("<a  href=\"javascript:void(0);\" onclick=\""+id+".pageIndex("+(index-1)+");"+fun+"\" class=\"a1\">«上一页</a>");
		}
		if(startPage>1){
			re.push("<a  href=\"javascript:void(0);\" onclick=\""+id+".pageIndex(1);"+fun+"\"  class=\"a1\">1..</a>");
		}
		for(i=0;i<maxSize;i++){
			curIndex = startPage + i;
			if(curIndex>totalPage){
				break;
			}
			if(curIndex==index){
				re.push("<span>"+curIndex+"</span>");
			}
			else{
				re.push("<a href=\"javascript:void(0);\" onclick=\""+id+".pageIndex("+curIndex+");"+fun+"\"  class=\"a1\">"+curIndex+"</a>");
			}
		}

		if(index+maxSize < totalPage){
			re.push("<a href=\"javascript:void(0);\" onclick=\""+id+".pageIndex("+totalPage+");"+fun+"\"  class=\"a1\">.."+totalPage+"</a>");
		}
		if(index<totalPage){
			re.push("<a href=\"javascript:void(0);\" onclick=\""+id+".pageIndex("+(index+1)+");"+fun+"\"  class=\"a1\">下一页 »</a>");
		}
		re.push("</div>");
		return re.join("");
	},
	
	lygPagebar:function(id,total,fun,maxSize,className){
		if(total == undefined){
			total = 1;
		}
		var index = this.pageIndex();
		pagenum.length=0;
		var pSize = this.limit();
		// 样式名称
		if(className == null){
			className = "pages";
		}
		// 最大显示多少个分页
		if(maxSize == null){
			maxSize = 6;
		}
		var startPage = 1;// 开始页
		var totalPage = 1;
		if(total%pSize==0){
			totalPage = total/pSize;
		}else{
			totalPage = parseInt((total/pSize))+1;
		}
		if(totalPage<1){
			totalPage = 1;
		}
		if(index<1){
			index= 1;
		}
		if(index>totalPage){
			index = totalPage;
		}
		if(totalPage <2){
			return "";
		}
		var re = [];
		re.push("<nav>");
		re.push("<ul class=\"pager\">");
		re.push("<li>共"+total+"条记录"+index+"/"+totalPage+"页</li>");
		re.push("<li><a href=\"javascript:void(0);\" onclick=\""+id+".pageIndex(1);"+fun+"\">首页</a></li>");
		
		// 结束页处理
//		if(index<=(maxSize/2-1))
//		{
//			startPage =1;
//		}
//		else{
//			startPage = Math.ceil(index- (maxSize/2-1));
//		}
//		if(startPage+maxSize>totalPage){
//			startPage = totalPage-maxSize+1;
//		}
		if(startPage<1){
			startPage = 1;
		}
		if(index>1){
			re.push("<li><a href=\"javascript:void(0);\" onclick=\""+id+".pageIndex("+(index-1)+");"+fun+"\" >上一页</a></li>");
		}else{
			re.push("<li><a href=\"javascript:void(0);\"  >上一页</a></li>");
		}
//		if(startPage>1){
//			re.push("<a  href=\"javascript:void(0);\" onclick=\""+id+".pageIndex(1);"+fun+"\"  class=\"a1\">1..</a>");
//		}
//		for(i=0;i<maxSize;i++){
//			curIndex = startPage + i;
//			if(curIndex>totalPage){
//				break;
//			}
//			if(curIndex==index){
//				re.push("<span>"+curIndex+"</span>");
//			}
//			else{
//				re.push("<a href=\"javascript:void(0);\" onclick=\""+id+".pageIndex("+curIndex+");"+fun+"\"  class=\"a1\">"+curIndex+"</a>");
//			}
//		}

//		if(index+maxSize < totalPage){
//			re.push("<a href=\"javascript:void(0);\" onclick=\""+id+".pageIndex("+totalPage+");"+fun+"\"  class=\"a1\">.."+totalPage+"</a>");
//		}
		if(index<totalPage){
			re.push("<li><a href=\"javascript:void(0);\" onclick=\""+id+".pageIndex("+(index+1)+");"+fun+"\" >下一页</a></li>");
		}else{
			re.push("<li><a href=\"javascript:void(0);\" >下一页</a></li>");
		}
		re.push("<li><a href=\"javascript:void(0);\" onclick=\""+id+".pageIndex("+totalPage+");"+fun+"\">尾页</a></li>")
		re.push("<li>共"+totalPage+"页</li>");
		re.push("</ul>");
		re.push("</nav>");
		return re.join("");
	},
	
	registerPageBar:function(total,maxSize,className){
		if(total == undefined){
			total = 1;
		}
		var index = this.pageIndex();
		pagenum.length=0;
		var pSize = this.limit();
		// 样式名称
		if(className == null){
			className = "pages";
		}
		// 最大显示多少个分页
		if(maxSize == null){
			maxSize = 6;
		}
		var startPage = 1;// 开始页
		var totalPage = 1;
		if(total%pSize==0){
			totalPage = total/pSize;
		}else{
			totalPage = parseInt((total/pSize))+1;
		}
		if(totalPage<1){
			totalPage = 1;
		}
		if(index<1){
			index= 1;
		}
		if(index>totalPage){
			index = totalPage;
		}
		if(totalPage <2){
			return "";
		}
		var re = [];
		re.push("<div class=\""+className+"\">");
		re.push("<a class=\"a1\">"+total+"条</a>")
		
		// 结束页处理
		if(index<=(maxSize/2-1))
		{
			startPage =1;
		}
		else{
			startPage = Math.ceil(index- (maxSize/2-1));
		}
		if(startPage+maxSize>totalPage){
			startPage = totalPage-maxSize+1;
		}
		if(startPage<1){
			startPage = 1;
		}
		if(index>1){
			re.push("<a  href=\"javascript:void(0);\" pageIndex=\""+(index-1)+"\" class=\"a1 page_btn\">«上一页</a>");
		}
		if(startPage>1){
			re.push("<a  href=\"javascript:void(0);\" pageIndex=\"1\" class=\"a1 page_btn\">1..</a>");
		}
		for(i=0;i<maxSize;i++){
			curIndex = startPage + i;
			if(curIndex>totalPage){
				break;
			}
			if(curIndex==index){
				re.push("<span>"+curIndex+"</span>");
			}
			else{
				re.push("<a href=\"javascript:void(0);\" pageIndex=\""+curIndex+"\"  class=\"a1 page_btn\">"+curIndex+"</a>");
			}
		}

		if(index+maxSize < totalPage){
			re.push("<a href=\"javascript:void(0);\" pageIndex=\""+totalPage+"\"  class=\"a1 page_btn\">.."+totalPage+"</a>");
		}
		if(index<totalPage){
			re.push("<a href=\"javascript:void(0);\" pageIndex=\""+(parseInt(index)+1)+"\"  class=\"a1 page_btn\">下一页 »</a>");
		}
		re.push("</div>");
		var obj = re.join("");
		return obj;
	}
}



plugin.add('editor',{path:LEx.webPath+'/public/scripts/kindeditor/kindeditor-min.js',type:'js',charset:'utf-8'});
LEx.Control.editor = function(containerId,fun){
	plugin.ready("editor",function(){
		KindEditor.basePath = LEx.webPath+'public/scripts/kindeditor/';
		var obj =  KindEditor.create('textarea[id=\"'+containerId+'\"]');
		if(fun != undefined){
			var s = eval(fun);
			s.call(s,obj,containerId);
		}
	});
}


plugin.add('fusionCharts', {
	path:  LEx.webPath+'public/scripts/FusionCharts.js',
	type: 'js',
	charset: 'utf-8'
});
LEx.Control.fusionCharts = function(url,id,w,h,fun){
	plugin.ready("fusionCharts",function(){
		var obj = new FusionCharts(url,id,w,h);
		if(fun != undefined){
			var s = eval(fun);
			s.call(s,obj);
		}
	});
};

LEx.Control.Select=function(){}
LEx.Control.Select.prototype = {
	setDefaultSelect:function(selectId,defaultCode){
		$("#"+selectId +" option[value='"+defaultCode+"']").attr("selected" , "selected");
	},
	iterationMethod:function(itemCode,arrDictItem,selObj,mark,keyFieldName,valueFieldName,parentFieldName){
		for(var i in arrDictItem){
			if(arrDictItem[i][parentFieldName] == itemCode && i!="remove" && i!="indexOf" && i!="lastIndexOf"){
				selObj.append("<option value='"+arrDictItem[i][keyFieldName]+"'>"+mark+arrDictItem[i][valueFieldName]+"</option>");
				var ic = arrDictItem[i][keyFieldName]; 
				//delete arrDictItem[i];
				//arrDictItem.splice(i);
				this.iterationMethod(ic,arrDictItem.slice(0),selObj,"&nbsp;&nbsp;&nbsp;&nbsp;"+mark,keyFieldName,valueFieldName,parentFieldName);
			} 
		}
	}
};
//设置select的默认值（selectId为组件id,defaultCode为需要选择的value）
LEx.Control.setDefaultSelect=function(selectId,defaultCode){
	var ucSelect = new LEx.Control.Select();
	ucSelect.setDefaultSelect(selectId,defaultCode);
};	
	
	function obj2string(o){
		var r=[];
		if(typeof o=="string"){
			return "\""+o.replace(/([\'\"\\])/g,"\\$1").replace(/(\n)/g,"\\n").replace(/(\r)/g,"\\r").replace(/(\t)/g,"\\t")+"\"";
		}
		if(typeof o=="object"){
			if(!o.sort){
				for(var i in o){
					r.push(i+":"+obj2string(o[i]));
				}
				if(!!document.all&&!/^\n?function\s*toString\(\)\s*\{\n?\s*\[native code\]\n?\s*\}\n?\s*$/.test(o.toString)){
					r.push("toString:"+o.toString.toString());
				}
				r="{"+r.join()+"}";
			}else{
				for(var i=0;i<o.length;i++){
					r.push(obj2string(o[i]))
				}
				r="["+r.join()+"]";
			} 
			return r;
		} 
		return o.toString();
	}