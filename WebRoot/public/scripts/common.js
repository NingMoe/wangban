/*
 * 项目相关类存放在此位置
 * */
//字符串里的标签大写转小写
function lowtag(s) {
	return s.replace(/(<\/?)([a-z\d\:]+)((\s+.+?)?>)/gi, function(s, a, b, c) {
		return a + b.toLowerCase() + c;
	});
}
// 表示全局唯一标识符 (GUID)。
function Guid(g) {
	var arr = new Array(); // 存放32位数值的数组
	if (typeof (g) == "string") { // 如果构造函数的参数为字符串
		initByString(arr, g);
	} else {
		initByOther(arr);
	}
	// 返回一个值，该值指示 Guid 的两个实例是否表示同一个值。
	this.equals = function(o) {
		if (o && o.isGuid) {
			return this.toString() == o.toString();
		} else {
			return false;
		}
	}
	// Guid对象的标记
	this.isGuid = function() {
	}
	// 返回 Guid 类的此实例值的 String 表示形式。
	this.toString = function(format) {
		if (typeof (format) == "string") {
			if (format == "N" || format == "D" || format == "B" || format == "P") {
				return toStringWithFormat(arr, format);
			} else {
				return toStringWithFormat(arr, "D");
			}
		} else {
			return toStringWithFormat(arr, "N");
		}
	}
	// 由字符串加载
	function initByString(arr, g) {
		g = g.replace(/\{|\(|\)|\}|-/g, "");
		g = g.toLowerCase();
		if (g.length != 32 || g.search(/[^0-9,a-f]/i) != -1) {
			initByOther(arr);
		} else {
			for ( var i = 0; i < g.length; i++) {
				arr.push(g[i]);
			}
		}
	}
	// 由其他类型加载
	function initByOther(arr) {
		var i = 32;
		while (i--) {
			arr.push("0");
		}
	}
	/*
	 * 根据所提供的格式说明符，返回此 Guid 实例值的 String 表示形式。 N 32 位：
	 * xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx D 由连字符分隔的 32 位数字
	 * xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx B 括在大括号中、由连字符分隔的 32
	 * 位数字：{xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx} P 括在圆括号中、由连字符分隔的 32
	 * 位数字：(xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx)
	 */
	function toStringWithFormat(arr, format) {
		switch (format) {
		case "N":
			return arr.toString().replace(/,/g, "");
		case "D":
			var str = arr.slice(0, 8) + "-" + arr.slice(8, 12) + "-" + arr.slice(12, 16) + "-" + arr.slice(16, 20) + "-" + arr.slice(20, 32);
			str = str.replace(/,/g, "");
			return str;
		case "B":
			var str = toStringWithFormat(arr, "D");
			str = "{" + str + "}";
			return str;
		case "P":
			var str = toStringWithFormat(arr, "D");
			str = "(" + str + ")";
			return str;
		default:
			return new Guid();
		}
	}
}
// Guid 类的默认实例，其值保证均为零。
Guid.empty = new Guid();
// 初始化 Guid 类的一个新实例。
Guid.newGuid = function() {
	var g = "";
	var i = 32;
	while (i--) {
		g += Math.floor(Math.random() * 16.0).toString(16);
	}
	return new Guid(g) + "";
}

// 模拟点击事件
function likeaClick(fireOnThis, e) {
	// if(e.keyCode != 13) return;
	// var fireOnThis = $(linkId)

	if (typeof (fireOnThis) != "undefined")
		if (document.createEvent) {
			var evObj = document.createEvent('MouseEvents')
			evObj.initEvent('click', true, false)
			fireOnThis.dispatchEvent(evObj);
		} else if (document.createEventObject) {
			fireOnThis.fireEvent('onclick');
		}
	// e.stop();
}
// 模拟change事件
function likeaChange(fireOnThis, e) {
	// if(e.keyCode != 13) return;
	// var fireOnThis = $(linkId)
	if (typeof (fireOnThis) != "undefined")
		if (document.createEvent) {
			var evObj = document.createEvent('MouseEvents')
			evObj.initEvent('change', true, false)
			fireOnThis.dispatchEvent(evObj);
		} else if (document.createEventObject) {
			fireOnThis.fireEvent('onchange');
		}
	// e.stop();
}

// 获取主窗体Iframe对象
LEx.getParent = function() {
	return window.parent.document.getElementById("mainPanel_iframe").contentWindow;
}

// 将页面执行加载到主窗体中
LEx.loadMain = function(url, loadMethod) {
	parent.loadMain(url, loadMethod);
}

// checkbox全选、反选
LEx.doAll = function(id) {
	if ($('#' + id).attr("checked")) {
		$('.' + id + '_checkbox').each(function() {
			$(this).attr("checked", true);
		});
	} else {
		$('.' + id + '_checkbox').each(function() {
			$(this).attr("checked", false);
		});
	}
}
// 获取checkbox的id集合
LEx.getIds = function(id) {
	var ids = [];
	$('.' + id + "_checkbox").each(function() {
		if ($(this).attr("checked") == "checked") {
			ids.push($(this).attr("value"));
		}
	});
	return ids;
}

// 字符串是否为空
function isNotNull(obj) {
	return LEx.isNotNull(obj);
}

// 字符串替换函数
function ReplaceAll(str, sptr, sptr1) {
	while (str.indexOf(sptr) >= 0) {
		str = str.replace(sptr, sptr1);
	}
	return str;
}

String.prototype.trim = function() {
	return this.replace(/(^\s*)|(\s*$)/g, "");
}
String.prototype.ltrim = function() {
	return this.replace(/(^\s*)/g, "");
}
String.prototype.rtrim = function() {
	return this.replace(/(\s*$)/g, "");
}

String.prototype.padLeft = function (padChar, width) { 
	var ret = this; 
	while (ret.length < width) { 
		if (ret.length + padChar.length < width) { 
			ret = padChar + ret; 
		}else { 
			ret = padChar.substring(0, width-ret.length) + ret; 
		} 
	} 
	return ret; 
}; 
String.prototype.padRight = function (padChar, width) { 
	var ret = this; 
	while (ret.length < width) { 
		if (ret.length + padChar.length < width) { 
			ret += padChar; 
		}else { 
			ret += padChar.substring(0, width - ret.length); 
		} 
	} 
	return ret; 
}; 

/* 验证特殊字符 */
function checkSC(val) {
	var reg = /[@#\$%\^&\*\_\[\]\\|￥?？]+/gi;
	if (reg.test(val)) {
		alert("输入的内容中含有特殊字符！");
		return false;
	}
	return true;
}

function initNavigation() {
	$('.search_bar ul').hover(function() {
		if (!$(this).hasClass("expented")) {
			$(this).find("#search_center").animate({
				height : "10px"
			}, 100);
		}
	}, function() {
		if (!$(this).hasClass("expented")) {
			$(this).find("#search_center").animate({
				height : "1px"
			}, 100);
		}
	});

	$('.search_bar ul').click(function() {
		if ($(this).hasClass("expented")) {
			$(this).removeClass('expented');
			$('#advan_search').hide('fast', function() {
				$(".search_bar ul #search_center").animate({
					height : 1
				}, 100);
			});

		} else {
			$(this).addClass('expented');

			var ad_width = $('#advan_search').parent().find('.navigation').width() - 60;
			$('#advan_search').width(ad_width);

			$(this).find("#search_center").animate({
				height : $("#advan_search").height() + 20
			}, 200, 'linear', function() {
				$('#advan_search').show('fast');
			});
		}
	});

	$('.navigation .navigation_subOption li').click(function() {
		$('.navigation .navigation_subOption li').removeClass('on');
		$(this).addClass('on');
	});

	$('.navigation .navigation_viewStyle li').click(function() {
		$('.navigation .navigation_viewStyle li').removeClass('on');
		$(this).addClass('on');
	});

	/**
	 * 根据选中的菜单进行初始化导航栏 nav为完整导航，以都好分割 currNav为当前位置
	 * 
	 * @hxk 2012-10-16
	 */
	var navs = ""// window.top.getPath(LEx.urldata.nav,LEx.urldata.currNav);
	if (navs != "") {
		$('.navigation .navigation_ul').empty();
		$('.navigation .navigation_ul').append($(navs));
		$('.navigation .navigation_ul li:last').addClass('cur');
	}
	
	// 如果设置了goto参数则初始化点击事件
	$(".navigation .navigation_ul li").each(function(){
		if(isNotNull($(this).attr("goto"))){
			$(this).addClass("cl");
			$(this).click(function(){
				window.location = $(this).attr("goto");
			})
		}
	})
}

function getPath(params, currNav) {
	var navs = [];
	if (params != null && params != "") {
		navs = params.split(",");
	} else {
		var s = $(".submenu .selected");
		if (s.attr('nav') != null) {
			navs = s.attr('nav').split(",");
		}
		;
	}
	var lis = "";
	var appCode = getcookie("ZHJC_BUSI_TYPE");

	var appName = "";// LEx.BspCmd.itemName(appCode.toLowerCase(),'svpf_busi_type');
	if (appName != "") {
		lis += "<li title='" + appName + "'>" + appName + "</li>";
	}

	for ( var i = 0; i < navs.length; i++) {
		lis += "<li title='" + navs[i] + "'>" + navs[i] + "</li>";
	}
	if (currNav != null && currNav != "") {
		var t = currNav;
		if (currNav.length > 25) {
			t = currNav.substr(0, 25) + "...";
		}
		lis += "<li title='" + currNav + "'>" + t + "</li>";
	}

	return lis;
}
function closeAdvanceSearch() {
	$('.search_bar ul').removeClass('expented');
	$('#advan_search').hide('fast', function() {
		$(".search_bar ul #search_center").animate({
			height : 1
		}, 100);
	});
}

function mDialog(obj) {
	// if(!isNotNull(obj.id)){
	// obj.id = "operationWindow";
	// }
	if (!isNotNull(obj.title)) {
		obj.title = "操作";
	}
	if (!isNotNull(obj.buttonName)) {// 按钮名称
		obj.buttonName = "保存";
	}
	if (!isNotNull(obj.width)) {// 宽度
		obj.width = 800;
	}
	if (!isNotNull(obj.height)) {// 高度
		obj.height = 280;
	}
	if (!isNotNull(obj.save)) {// 高度
		obj.save = 'true';
	}
	if (!isNotNull(obj.del)) {// 高度
		obj.del = 'true';
	}
	obj.button = [];
	if (obj.button.length == 0) {// 如果页面未定义button，采用默认
		if (!isNotNull(obj.qmName)) {// 回调执行方法
			obj.qmName = "onQuery()";
		}
		if (!isNotNull(obj.pmName)) {// 弹出框执行具体操作的方法
			obj.pmName = "postData()";
		}
		if (obj.save == 'true') {
			obj.button.push({
				name : obj.buttonName,
				callback : function() {
					var iframe = this.iframe.contentWindow;
					if (!iframe.document.body) {
						LEx.alert('内容还没加载完毕呢');
						return false;
					}
					if (iframe.eval(obj.pmName)) {
						eval(obj.qmName);
						return true;
					} else {
						return false;
					}
				},
				focus : true
			});
		}
		if (obj.del == 'true') {
			obj.button.push({
				name : '关闭'
			});
		}
	}
	LEx.dialog(obj);
}


/**
 * 按字典类型查询字典项
 * 
 * @param dic_code
 *            字典类型
 * @param id
 *            需设置的下拉单节点ID
 */
function queryDictItem(dic_code, id, defaultValue, func) {
	// 定义command对象
	var command = new LEx.Command("app.pmi.dictItem.DictItemCmd");
	// 参数设置
	command.setParameter("ITEM.DICT_CODE", dic_code);
	command.setParameter("ITEM.IN_USE", '1');
	// command.setParameter("sort", "ITEM.XH ASC");
	command.execute('getDictItemList');
	
	if (!command.error) {
		ret = command.returns;// 读取后台查询结果数据
		setSelectData(ret.data, id, defaultValue);
		if (func != undefined) {
			var s = eval(func);
			s.call(s, ret.data);
		}
	} else {
		alert(command.error);// 打印错误信息
	}
	
	// command.execute('getDictItemList', false);
}

/**
 * 将字典项设置到下拉单
 * 
 * @param data
 * @param id
 */
function setSelectData(data, id, defaultValue) {
	if(!data){
		return;
	}
	var option = '<option value="">--请选择--</option>';
	$.each(data, function(i, n) {
		if (defaultValue == n.ITEM_CODE) {
			option += '<option value="' + n.ITEM_CODE + '" selected="selected">' + n.ITEM_VALUE + '</option>';
		} else {
			option += '<option value="' + n.ITEM_CODE + '">' + n.ITEM_VALUE + '</option>';
		}
	});
	$('#' + id).append(option);
}

/**
 * 将字典项设置到radio
 * 
 * @param data
 * @param id
 */
function setRadioData(data, id, defaultValue) {
	if(!data){
		return;
	}
	var s = '';
	$.each(data, function(i, n) {
		s += '<input type="radio" name="'+id+'" tag="'+id+'" value="' + n.ITEM_CODE + '"/>&nbsp;' + n.ITEM_VALUE+'&nbsp;&nbsp;&nbsp;';
	});
	$('#span_' + id).append(s);
}

/**
 * 汉字转拼音简写
 * 
 * @param chinese
 *            汉字
 * @param desId
 *            拼音填充的目标节点ID
 */
function pinYIn(chinese, desId) {
	// 定义command对象
	var command = new LEx.Command("core.util.PinYinCmd");
	// 参数设置
	command.setParameter("chinese", chinese);
	command.execute('getPinyin');
	if (!command.error) {
		ret = command.returns;// 读取后台查询结果数据
		$('#' + desId).val(ret.data);
	} else {
		alert(command.error);// 打印错误信息
	}
}


function logger(tar,prefix) {
	var $debug = $("#debug");
	if(typeof(tar) == "undefined") {
		$debug.append(prefix && (" &gt; " + prefix) || "");
		$debug.append(" - [undefined] <br/>");
	} else if(typeof tar  != "object") {
		$debug.append(prefix && (" &gt; " + prefix) || "");
		$debug.append(" - [").append(tar).append("]<br/>")
	} else {
		for (var k in tar) {
			var v = tar[k];
			$debug.append(prefix && (" &gt; " + prefix) || "");
			$debug.append(" - ");
			if (!$.isFunction(v)) {
				if(typeof v == "object") {
					$debug.append(k).append(" : [object]<br/>");

				} else {
					$debug.append(k).append(" : [").append(v).append("]<br/>");
				}
			} else {
				$debug.append(k).append(" : (Function)").append("<br/>");
			}
        }
    }

}
var Security = {};
function getSecurityValue(key,defaultV){
	if(Security && Security.hasOwnProperty(key)){
		return Security[key];
	}
	var command = new LEx.Command("app.pmi.config.cmd.ConfigCmd");
	command.setParameter("key", key);
	command.execute("getConfigInfo");
	if(!command.error){
		var ret = command.returns;
		var value = ret.data;
		if(!LEx.isNotNull(value)){
			value = defaultV;
		}
		Security[key] = value;
	}
	return value;
}

/**
 * 随机生成6位数密码
 * 
 * @author 黄壮辉
 * @return
 */
function newRandomPSWD(){
	var x = 999999;
    var y = 100000;
    var rand = parseInt(Math.random() * (x - y + 1) + y);
    return rand.toString();
}

LEx.Control.Region = function(data){
	if(typeof data == "string"){
		var tmp = data;
		data = {};
		data.id = tmp;
		data.type = "click";
	}else{
		var tmp = data.type;
		if(tmp==null||tmp==""){
			data.type = "click";
		}
	}
	if(data == null || data.id == null){
		alert("LEx.Control.Region中至少包含id参数，格式为：{id:'departList'}");
		return;	
	}
	if(data.dept == null){
		data.dept="dept";
	}
	this.data = data;
	var regionId = $("#"+data.id).attr("code");
	if(!isNotNull(regionId)){
		regionId = $("#"+data.id).val();
	}
	var rootId = data.rootId;
	if(!isNotNull(rootId)){
		rootId = "";
	}
	var regionName = "";
	if(isNotNull(regionId)){
		regionName = formatRegion(regionId);
	}
	$("#"+data.id).val(regionName);
	$("#"+data.id).attr("code",regionId);
	
	$("#"+data.id).attr("readonly","readonly");
	$("#"+data.id).click(function(e) {
		var regionId = $("#"+data.id).attr("code");
		LEx.dialog({
			id: "selectRegion", title: "选择区划",
			url: LEx.webPath+"admin?m=bsp/regionTree.html&regionId="+regionId+"&rootId="+rootId,
			width: 400, height: 450,
			lock:true,
			button: [
			         {
			        	 name: '选择',
			        	 callback: function() {
			        	 var iframe = this.iframe.contentWindow;
			        	 if (!iframe.document.body) {
			        		 LEx.alert('内容还没加载完毕呢');
			        		 return false;
			        	 }
			        	 var region = {};
			        	 if((region=iframe.getData())){
			        		 if(region.ID){
			        			 $("#"+data.id).val(region.NAME);
			        			 $("#"+data.id).attr("code",region.ID);
			        		 }else{
			        			 $("#"+data.id).val("深圳市");
			        			 $("#"+data.id).attr("code","440300");
			        		 }
			        		 var dept = $("#"+data.dept);
			        		 if(dept){
			        			 $("#"+data.dept).val('');
			        			 $("#"+data.dept).attr("code","");
			        		 }
			        		 if (data.func != undefined) {
			        			 var s = eval(data.func);
			        			 s.call(s, region);
			        		 }
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
	});
};

/**
 * js产生随机数
 * 
 * @param l
 * @returns {String}
 */
function randomChar(length) {
	 var x="123456789poiuytrewqasdfghjklmnbvcxzQWERTYUIPLKJHGFDSAZXCVBNM";
	 var tmp="";
	 for(var i=0;i< length;i++) {
		 tmp += x.charAt(Math.ceil(Math.random()*100000000)%x.length);
	 }
	 return tmp;
}


function replaceRntoBr(msg){
	return msg.replace(/\r\n/g,"<br>").replace(/\n/g,"<br>");
}

// 添加运行状态
function showRunning(){
	if($(".tipsDIV").length == 0){
		$("body").append("<div class='tipsDIV'><img src='"+LEx.webPath+"public/themes/lui/images/loading.gif'/>&nbsp;&nbsp;&nbsp;&nbsp;数据保存中...</div>");
	}
	$(".tipsDIV").show();
}

function hideRunning(){
	$(".tipsDIV").hide();
}

/**
 * 文件选择
 */
LEx.resourceManage = function(id,title,isMultiple,type,path,callbackfunc){
	var button = $("<img alt='' style='cursor: pointer;margin-left:10px;text-align:center;vertical-align:middle;' src='"+LEx.webPath+"public/images/img.png' id='resourceBtn'>");			
	// 注册事件
	button.click(function(){
		var option = {};
		if(isNotNull(path))option.path=path;
		if(isNotNull(title))option.title=title;
		if(isNotNull(isMultiple))option.isMultiple=isMultiple;
		if(isNotNull(callbackfunc))option.callbackfunc=callbackfunc;
		openResourceWin(option);
		// openResourceWin({path:path,title:title,type:type,isMultiple:isMultiple,func:callbackfun});
	})
	$('#'+id).after(button);
}

function openResourceWin(options){
	var data={path:"public/images",title:"文件选择",type:"",isMultiple:"false"}
	$.extend(data, options);
	LEx.dialog({
		id:"picSelect",
		title:data.title,
		url: LEx.webPath+"admin?m=bsp/pictures.html&path="+data.path+"&type="+data.type+"&isMultiple="+data.isMultiple,
        width: 800, height: 500,
        lock:true,
        button: [
			{
			    name: '选择',
			    callback: function() {
			        var iframe = this.iframe.contentWindow;
			        if (!iframe.document.body) {
			            LEx.alert('内容还没加载完毕呢');
			            return false;
			        }
			        var arr = [];
			        if((arr=iframe.getData())){
				        // 回调
				        if (data.callbackfunc != undefined) {
							var s = eval(data.callbackfunc);
							s.call(s, arr);
						}
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


var __intervalManager = null;
LEx.IntervalManage = function(option){
	if(__intervalManager == null){
		__intervalManager = this;
	}else{
		LEx.dialog.tips("请不要重复实例化LEx.IntervalManage");
		return null;
	}
	var opt={func:function(){
		readNotice();
	},time:10000};
	$.extend(opt,option);
	this.option = opt;
	var interval = setInterval(function(){
		if(__intervalManager.startFlag){
			readNotice();
		}
	}, this.option.time);
	this.startFlag = false;
}

LEx.IntervalManage.prototype = {
	start:function(){
		this.startFlag = true;
	},
	stop:function(){
		this.startFlag = false;
	}
}




/**
 * * 获取字符串的哈希值 *
 * 
 * @param {String}
 *            str *
 * @param {Boolean}
 *            caseSensitive *
 * @return {Number} hashCode
 */

// 等同于java中的hashCode();
// 效率挺高的
function getHashCode(str){
	var hash = 0;
    if (!LEx.isNotNull(str)) return hash;
    for (i = 0; i < str.length; i++) {
    	var char1 = str.charCodeAt(i);
    	hash = ((hash<<5)-hash)+char1;
    	hash = hash & hash;
    }
    return hash;
}




/**
*清空查询条件
*/
function reset(){
	$('#advan_search input[type=input]').val('');
}

/**
*获取指定对话框
*/
LEx.getDialog =function(id)
{
	try{
		var d = (parent.$.dialog == null) ? $.dialog : parent.$.dialog;
		var list =d.list;
		if(list){
			for (var i in list) {
				if(i == id){
					return list[i];
				}
			}
		}
	}catch(e){
		alert(e.message);
	}
}

function errorDialog(t,c){
	LEx.dialog({
		title : t,
		content : c,
		icon : 'error',
		lock : true
	});
}
/*
 * 截取字符串
 */
function formatLen(val,star,len){
	return val.substr(star,len);
}
/*
 * 截取字符串+...
 */
function formatLenStr(val,star,len){
	var str="";
	if(val.length>len){
		str="...";
	}
	return val.substr(star,len)+str;
}
/**
 * js截取字符串，中英文都能用
 * @param str：需要截取的字符串
 * @param len: 需要截取的长度
 */
function formatLenUtf8(str, len) {
	var str_length = 0;
	var str_len = 0;
	str_cut = new String();
	str_len = str.length;
	for (var i = 0; i < str_len; i++) {
		a = str.charAt(i);
		str_length++;
		if (escape(a).length > 4) {
			//中文字符的长度经编码之后大于4
			str_length++;
		}
		str_cut = str_cut.concat(a);
		if (str_length >= len) {
			return str_cut;
		}
	}
	//如果给定字符串小于指定长度，则返回源字符串；
	if (str_length < len) {
		return str;
	}
}
/**
 * js截取字符串，中英文都能用
 * @param str：需要截取的字符串
 * @param len: 需要截取的长度
 */
function formatLenUtf8Str(str, len) {
	var str_length = 0;
	var str_len = 0;
	str_cut = new String();
	str_len = str.length;
	for (var i = 0; i < str_len; i++) {
		a = str.charAt(i);
		str_length++;
		if (escape(a).length > 4) {
			//中文字符的长度经编码之后大于4
			str_length++;
		}
		str_cut = str_cut.concat(a);
		if (str_length >= len) {
			str_cut = str_cut.concat("...");
			return str_cut;
		}
	}
	//如果给定字符串小于指定长度，则返回源字符串；
	if (str_length < len) {
		return str;
	}
}

function toUtf8(str) {   
    var out, i, len, c;   
    out = "";   
    len = str.length;   
    for(i = 0; i < len; i++) {   
    	c = str.charCodeAt(i);   
    	if ((c >= 0x0001) && (c <= 0x007F)) {   
        	out += str.charAt(i);   
    	} else if (c > 0x07FF) {   
        	out += String.fromCharCode(0xE0 | ((c >> 12) & 0x0F));   
        	out += String.fromCharCode(0x80 | ((c >>  6) & 0x3F));   
        	out += String.fromCharCode(0x80 | ((c >>  0) & 0x3F));   
    	} else {   
        	out += String.fromCharCode(0xC0 | ((c >>  6) & 0x1F));   
        	out += String.fromCharCode(0x80 | ((c >>  0) & 0x3F));   
    	}   
    }   
    return out;   
}
function clone(myObj){
	  if(typeof(myObj) != 'object') return myObj;
	  if(myObj == null) return myObj;
	  
	  var myNewObj = new Object();
	  
	  for(var i in myObj)
	    myNewObj[i] = clone(myObj[i]);
	  
	  return myNewObj;
}
//翻页
function turnpage(type,func){
	var PageCount = $("#PageCount").html();
	var CurrIndex = $("#CurrIndex").html();
	var num = $("#CP").val();
	if(type=="PageFirst"){//首页
		eval(func+"(1)");
	}else if(type=="pagePrevious"){//上一页
		if(CurrIndex=='1'){
			alert("当前为首页。");
			return false;
		}else if(CurrIndex=='0'){
			alert("当前为首页。");
			return false;
		}else{			
			num=CurrIndex*1-1;
			eval(func+"("+num*1+")");
		}		
	}else if(type=="pageNext"){//下一页
		if(CurrIndex==PageCount){
			alert("当前为尾页。");
			return false;
		}else{
			num = CurrIndex*1+1;
			eval(func+"("+num*1+")"); 
		}	
	}else if(type=="pageLast"){//尾页
		eval(func+"("+PageCount*1+")");		
	}else if(type=="pageNum"){//跳转
		if(!(IsAllNumeric(num))){
	       alert("请填写数字。");
	       return false;
		}else{
		   if(PageCount*1<num*1||num*1==0){
			   alert("请填写正确数字。");
		       return false;
		   }else{
			   eval(func+"("+num*1+")"); 
		   }		  
		}	
	}
}
function IsAllNumeric(str) {
	  var l=str.length;
	  var i,s;
	  for(i=0;i<l;i++)
	  { s=str.charAt(i);
	    if(!(s>='0'&&s<='9')) return false;
	  }
	  return true;
}
