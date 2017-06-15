LEx = {
	version : '1.0'
};

(function() {
	if( __webPath == undefined){
		var paths = document.location.pathname.split("/");
		if (paths[0] == '') {// ie下面有时候paths[0]是空
			LEx.webPath = "/" + paths[1];
		} else {
			LEx.webPath = "/" + paths[0];
		}
	}else{
		LEx.webPath = __webPath;
	}
	var ua = navigator.userAgent.toLowerCase();

	var isOpera = ua.indexOf("opera") > -1, 
	isChrome = ua.indexOf("chrome") > -1, 
	isSafari = !isChrome && (/webkit|khtml/).test(ua),
	isGecko = !isSafari && !isChrome,
	isIE = !isOpera && ua.indexOf("msie") > -1;
	
	/**
	 * 如果浏览器是Internet Explorer则值是true.
	 * 
	 * @type Boolean
	 */
	LEx.isIE = isIE;
	/**
	 * 如果浏览器使用的是Gecko引擎则值是true (例如Mozilla, Firefox).
	 * 
	 * @type Boolean
	 */
	LEx.isGecko = isGecko;
	
	LEx.onReady = function(obj){
		$(document).ready(obj);
	};
})();


/**
 * 获取一些常用系统变量 xss
 */
LEx.getCookies = function() {
	return window.document.cookie;
}
LEx.getLocation = function() {
	return window.location.href;
}
LEx.getDocLocation = function() {
	return document.location.href;
}
LEx.getLocHash = function() {
	return window.location.hash;
}();
LEx.getLocPathname = function() {
	return document.location.pathname;
}();


/**
 * 如果传入的参数是数组，就返回true，否则返回false.
 * 
 * @param {Object}
 *            object 需要测试的对象
 * @return {Boolean}
 */
LEx.isArray = function(v) {
	return v && typeof v.length == 'number' && typeof v.splice == 'function';
};
/**
 * 如果传入的参数是日期类型，就返回true，否则返回false.
 * 
 * @param {Object}
 *            object 需要测试的对象
 * @return {Boolean}
 */
LEx.isDate = function(v) {
	return v && typeof v.getFullYear == 'function';
};

/**
 * 测试是否为数字，如果不是则返回默认值.
 * 
 * @param {Mixed}
 *            value 应为数字，如果不是也会有默认值
 * @param {Number}
 *            defaultValue 如果值不是数字，返回的默认值
 * @public
 * @return {Number} 数字
 */
LEx.num = function(v, defaultValue) {
	if (typeof v != 'number' || isNaN(v)) {
		return defaultValue;
	}
	return v;
};

/**
 * 把对象转换成URL的格式.例如 LEx.urlEncode({foo: 1, bar: 2});将会返回 "foo=1&bar=2".
 * 
 * @param {Object}
 *            o 需要转换的对象
 * @return {String} 转换成的URL字符串
 */
LEx.urlEncode = function(o) {
	if (!o) {
		return "";
	}
	var buf = [];
	for ( var key in o) {
		var ov = o[key], k = encodeURIComponent(key);
		var type = typeof ov;
		if (type == 'undefined') {
			buf.push(k, "=&");
		} else if (type != "function" && type != "object") {
			buf.push(k, "=", encodeURIComponent(ov), "&");
		} else if (LEx.isDate(ov)) {
			var s = LEx.encode(ov).replace(/"/g, '');
			buf.push(k, "=", s, "&");
		} else if (LEx.isArray(ov)) {
			if (ov.length) {
				for ( var i = 0, len = ov.length; i < len; i++) {
					buf
							.push(k, "=",
									encodeURIComponent(ov[i] === undefined ? ''
											: ov[i]), "&");
				}
			} else {
				buf.push(k, "=&");
			}
		}
	}
	buf.pop();
	return buf.join("");
};

/**
 * 把URL类型的编码转换为对象.例如： LEx.urlDecode("foo=1&bar=2");将会返回对象 {foo: "1", bar: "2"} 或者
 * LEx.urlDecode("foo=1&bar=2&bar=3&bar=4", false);将会返回 {foo: "1", bar: ["2",
 * "3", "4"]}.
 * 
 * @param {String}
 *            string
 * @param {Boolean}
 *            overwrite (可选)(默认是false).
 * @return {Object} 对象
 */
LEx.urlDecode = function(string, overwrite) {
	if (!string || !string.length) {
		return {};
	}
	var obj = {};
	var pairs = string.split('&');
	var pair, name, value;
	for ( var i = 0, len = pairs.length; i < len; i++) {
		pair = pairs[i].split('=');
		name = decodeURIComponent(pair[0]);
		value = decodeURIComponent(pair[1]);
		if (overwrite !== true) {
			if (typeof obj[name] == "undefined") {
				obj[name] = value;
			} else if (typeof obj[name] == "string") {
				obj[name] = [ obj[name] ];
				obj[name].push(value);
			} else {
				obj[name].push(value);
			}
		} else {
			obj[name] = value;
		}
	}
	return obj;
};


LEx.forward = function(url) {
	if(url.indexof("http://")){
		window.location.href = url;
	}else{
		window.location.href = LEx.webPath + "/" + url;
	}
};

LEx.arrayUnique = function(arr){
	var temp = new Array();
	for (i = 0; i < arr.length; i++) {
		bHas = false;
		for(j=0;j<temp.length;j++){
			if(temp[j]==arr[i]){
				bHas = true;
				break;
		  	}
		}
		if(bHas==false){
			temp.push(arr[i]);
		}
  	}
    return temp;
}

//拼装url
LEx.addurldata = function(oldurl){
 	var data = LEx.getLocation().split("?")[1];
 	if(!data){
 		return oldurl;
 	}
 	if(oldurl.indexOf('?')==-1){
 		return oldurl+'?'+data;
 	}else{
 		if(oldurl.lastIndexOf('?')==oldurl.length-1||oldurl.lastIndexOf('&')==oldurl.length-1){
 			return oldurl+data;
 		}else{
 			return oldurl+'&'+data;
 		}
 	}
}

LEx.height = function()
{
    if (window.innerHeight!=window.undefined) return window.innerHeight;
    if (document.compatMode=='CSS1Compat') return document.documentElement.clientHeight;
    if (document.body) return document.body.clientHeight;
    return window.undefined;
}();
LEx.width = function()
{
    var offset = 17;
    var width = null;
    if (window.innerWidth!=window.undefined) return window.innerWidth;
    if (document.compatMode=='CSS1Compat') return document.documentElement.clientWidth;
    if (document.body) return document.body.clientWidth;
}();

//提示框
LEx.alert = function(tip){
	if(tip==undefined){
		tip="";
	}
	LEx.dialog({
	    content: tip,
	    fixed:true,lock:true
	});
}

LEx.htmlEncode = function(str){
	var s = "";
	if(str.length == 0) return "";
	s = str.replace(/&/g,"&amp;");
	s = s.replace(/</g,"&lt;");
	s = s.replace(/>/g,"&gt;");
	s = s.replace(/ /g,"&nbsp;");
	s = s.replace(/\'/g,"&#39;");
	s = s.replace(/\"/g,"&quot;");
	return s;
}
LEx.htmlDecode = function(str)
{
	var s = "";
	if(str.length == 0)   return "";
	s = str.replace(/&amp;/g,"&");
	s = s.replace(/&lt;/g,"<");
	s = s.replace(/&gt;/g,">");
	s = s.replace(/&nbsp;/g," ");
	s = s.replace(/&#39;/g,"\'");
	s = s.replace(/&quot;/g,"\"");
	return s;
}

LEx.htmlBr = function(str){
	var s = "";
	if(str.length == 0)   return "";
	s = str.replace(/\r\n/g,"<br>");
	s = s.replace(/\n/g,"<br>");
	return s;
}

LEx.isNotNull = function (obj) {
    if (obj === undefined || obj === null || obj == "null" || obj === "" || obj == "undefined")
        return false;
    return true;
};

LEx.stop = function(){
	if(!!(window.attachEvent && !window.opera)) 
	{
		document.execCommand("stop");} 
	else 
	{
		window.stop();
	}
};

LEx.getHashcode = function() {                //获取url中的#内容
	    return location.hash;
};
LEx.setHashcode = function(hash) {            //设置url中的#内容
    location.hash = hash;
};

LEx.getLength4CN = function(str){
	return str.replace(/[^\x00-\xff]/g,"aa").length;
};

/*防止XSS漏洞，对用户数据进行过滤--过滤html*/
LEx.filterXss = function(obj) { 
	var result = obj.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g,'&quot;').replace(/'/g,'&apos;');
	return result;
};


//获取url参数，调用LEx.filterXss；解码decodeURIComponent(LEx.getLocation())
LEx.urldata = function(){
 	var url = decodeURIComponent(LEx.getLocation());
 	var data = url.split("?")[1];
 	if(!data){
 		return {};
 	}
 	data = data.split("&");
 	var datastr = "{";
 	for(var i=0;i<data.length;i++){
 		var temp = data[i].split("=");
 		if(!temp[1]){continue;}
 		var v = LEx.filterXss(temp[1].replace(/#$/,'').replace(new RegExp("\"", "g"),"\\\""));
 		datastr += '"'+temp[0]+'":"'+v+'",';//xss
 	}
 	if(datastr.length>2){
 		datastr = datastr.substring(0,datastr.length-1);
 	}
 	datastr += '}';
 	return eval('('+datastr+')');
}();

//获取url参数，调用LEx.filterXss；不解码
LEx.getUrldata = function(){
	var url = LEx.getLocation();
	var data = url.split("?")[1];
	if(!data){
		return {};
	}
	data = data.split("&");
	var datastr = "{";
	for(var i=0;i<data.length;i++){
		var temp = data[i].split("=");
		if(!temp[1]){continue;}
		var v = LEx.filterXss(temp[1].replace(/#$/,'').replace(new RegExp("\"", "g"),"\\\""));
		datastr += '"'+temp[0]+'":"'+v+'",';//xss
	}
	if(datastr.length>2){
		datastr = datastr.substring(0,datastr.length-1);
	}
	datastr += '}';
	return eval('('+datastr+')');
}();

LEx.arrayDecode = function(rows){
	// if(LEx.isArray(rows)){
 //        try{
 //            for(var i=0;i<rows.length;i++){
 //                var tmpObj = rows[i];
 //                for (var tmpKey in tmpObj){
 //                	if(tmpKey == "CONTENT"){
 //                		continue;
 //                	}
 //                    var tmpValue = tmpObj[tmpKey];
 //                    if(typeof(tmpValue)=="string"){
 //                        tmpObj[tmpKey] = LEx.htmlEncode(tmpValue);
 //                    }
 //                }
 //                rows[i] = tmpObj;
 //            }
 //        }
 //        catch(e){
 //        }
 //    }
    return rows;
};

LEx.toHtml = function(newHtml){
	var ObjectType = Object.prototype.toString.call(newHtml);
	var $html;
	if(ObjectType == "[object HTMLAllCollection]"){
		$html = newHtml;
	}else{
		$html = $(newHtml);
	}
    if(LEx.isNotNull($html)){
        var tempHtml = "";
        for(var i=0; i<$html.length ; i++){
            ObjectType = Object.prototype.toString.call($html[i]);
            if(ObjectType == "[object String]"){
                tempHtml += $html[i];
            }else if(ObjectType == "[object Text]"){
                tempHtml += $html[i].wholeText;
            }else if(ObjectType.match("HTML[a-zA-Z0-9]*Element")){
                tempHtml += $html[i].outerHTML;
            }
        }
        return tempHtml;
    }
    return "";
};
(function($){$.toJSON=function(o)
{if(typeof(JSON)=='object'&&JSON.stringify)
return JSON.stringify(o);var type=typeof(o);if(o===null)
return"null";if(type=="undefined")
return undefined;if(type=="number"||type=="boolean")
return o+"";if(type=="string")
return $.quoteString(o);if(type=='object')
{if(typeof o.toJSON=="function")
return $.toJSON(o.toJSON());if(o.constructor===Date)
{var month=o.getUTCMonth()+1;if(month<10)month='0'+month;var day=o.getUTCDate();if(day<10)day='0'+day;var year=o.getUTCFullYear();var hours=o.getUTCHours();if(hours<10)hours='0'+hours;var minutes=o.getUTCMinutes();if(minutes<10)minutes='0'+minutes;var seconds=o.getUTCSeconds();if(seconds<10)seconds='0'+seconds;var milli=o.getUTCMilliseconds();if(milli<100)milli='0'+milli;if(milli<10)milli='0'+milli;return'"'+year+'-'+month+'-'+day+'T'+
hours+':'+minutes+':'+seconds+'.'+milli+'Z"';}
if(o.constructor===Array)
{var ret=[];for(var i=0;i<o.length;i++)
ret.push($.toJSON(o[i])||"null");return"["+ret.join(",")+"]";}
var pairs=[];for(var k in o){var name;var type=typeof k;if(type=="number")
name='"'+k+'"';else if(type=="string")
name=$.quoteString(k);else
continue;if(typeof o[k]=="function")
continue;var val=$.toJSON(o[k]);pairs.push(name+":"+val);}
return"{"+pairs.join(", ")+"}";}};$.evalJSON=function(src)
{if(typeof(JSON)=='object'&&JSON.parse)
return JSON.parse(src);return eval("("+src+")");};$.secureEvalJSON=function(src)
{if(typeof(JSON)=='object'&&JSON.parse)
return JSON.parse(src);var filtered=src;filtered=filtered.replace(/\\["\\\/bfnrtu]/g,'@');filtered=filtered.replace(/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g,']');filtered=filtered.replace(/(?:^|:|,)(?:\s*\[)+/g,'');if(/^[\],:{}\s]*$/.test(filtered))
return eval("("+src+")");else
throw new SyntaxError("Error parsing JSON, source is not valid.");};$.quoteString=function(string)
{if(string.match(_escapeable))
{return'"'+string.replace(_escapeable,function(a)
{var c=_meta[a];if(typeof c==='string')return c;c=a.charCodeAt();return'\\u00'+Math.floor(c/16).toString(16)+(c%16).toString(16);})+'"';}
return'"'+string+'"';};var _escapeable=/["\\\x00-\x1f\x7f-\x9f]/g;var _meta={'\b':'\\b','\t':'\\t','\n':'\\n','\f':'\\f','\r':'\\r','"':'\\"','\\':'\\\\'};})(jQuery);

if(LEx == undefined){
	var LEx = null;
}
LEx.encode = $.toJSON;
LEx.decode = $.evalJSON;
LEx.Command = function(action, targetId, url) {
    this.action = action;
    this.isApiV2 = true;
    if (targetId == null) {
        this.targetId = "Java";
    } else {
        this.targetId = targetId;
    }
    if (url)
        this.url = url;
    else{
        if(!LEx.isNotNull(LEx.cmdPath)){
                this.url = LEx.webPath + "api-v2";
        }else{
            this.url = LEx.cmdPath;
        }
    	
    }
    this.paramsObj = {};
    this.returns = null;
};

LEx.Command.prototype = {
    // IE8
    ithis : this,
    setParameter: function (name, value) {
        /**
        *字符串去掉前置和后置空格
        */
        if(Object.prototype.toString.call(value) == '[object String]'){
            value = value.replace(/(^\s*)|(\s*$)/g,'');
        }
        this.paramsObj[name] = value;
    },
    execute: function (type, sync) {
        if (!LEx.isNotNull(sync)) {
        	sync = true;
        }
        var re = null;
        if (!LEx.isNotNull(this.action)) {
            re = {};
            re.s = "请输入处理action";
            return re;
        }
        if(!LEx.isNotNull(type)){
            type = "execute";
        }
        //var curUrl = this.url + "?i=" + this.targetId + "&a=" + this.action;
        var curUrl = this.url + "/" + this.action+"/"+type;
        if(this.isApiV2){
            var sig = "";
            var chars = "0123456789abcdef";
            if(!LEx.isNotNull(__signature)){
                var curTime = parseInt(Math.random()*(9999-1000+1)+1000)+""+Date.parse(new Date());
                sig = chars.charAt(parseInt(Math.random()*(15-15+1)+10))+chars.charAt(curTime.length)+""+curTime;
            }else{
                sig = __signature;
            }

            var key = "";
            var keyIndex = -1;
            for(var i=0;i<6;i++){
                var c=sig.charAt(keyIndex+1);
                key +=c;
                keyIndex = chars.indexOf(c);
                if(keyIndex<0 || keyIndex>=sig.length){
                    keyIndex = i;
                }
            }

            var timestamp = parseInt(Math.random()*(9999-1000+1)+1000)+"_"+key+"_"+Date.parse(new Date());
            var t = timestamp;//LEx.azdg.encrypt(timestamp,key);
            t = t.replace(/\+/g,"_");
            curUrl+= "?s=" + sig;
            curUrl+= "&t=" +  t;
        }
        // IE8 fix
        var ithis = this.ithis;

        var o = {
            type: "POST",
            async: !sync,
            url: curUrl,
            dataType: "json",
            contentType:"application/json",
            success: function (msg) {
                if (this.target) {
                    this.target.returns = msg;
                    if (this.target.returns != null && this.target.returns.data != null) {
                    	this.target.returns.rows = this.target.returns.data;
                    }
                    if(msg.state==-1){
                    	var tip="<b>系统提示：</b>"+msg.message;
        	        	tip +="，<br/>3秒后系统将为您重新刷新页面，或<a href='javascript:window.location.reload()'><font color='blue'><b>手动刷新</b></font></a>";
                    	LEx.dialog.tips(tip,3,function(){
                    		window.location.reload();
                    	});
                    	return null;
                    }
                    if(msg.state==0){
                    	this.target.error=msg.message;
                    }
                    if (this.target.afterExecute)
                        this.target.afterExecute();
                }
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                if (this.target) {
                    var t = {};
                    t.state = 0;
                    t.message = textStatus;
                    this.target.returns = t;
                    this.target.error=t.message;
                    if (this.target.afterExecute)
                        this.target.afterExecute();
                }
            },
            // IE8 fix
            target: ithis
        };

        if(curUrl.indexOf("http")==0){
            //jsonp模式下只能支持异步，get请求
            o.dataType = "jsonp";
            o.jsonp = "jsonp_callback";
            o.url = o.url+"&jsonp_callback=?";
            if (this.paramsObj != null) {
                o.data = this.paramsObj;
            }
            sync=false;
        }else{
            if (this.paramsObj != null) {
                o.data = LEx.encode(this.paramsObj);
            }
            
        }
        $.ajax(o);
        if(sync){
            // IE8 fix
            this.returns = this.returns || ithis.returns;
            if (this.returns == null) {
	            var t = {};
	            t.state = 0;
	            t.message = "获取服务器端数据为空，请稍后再试！";
	            this.returns = t;
	            this.error = t.message;
	        }
	        if (this.returns != null && this.returns.data != null) {
	            this.returns.rows = this.returns.data;
	        }
	        if(this.returns.state==-1){
	        	var tip="<b>系统提示：</b>"+this.returns.message;
	        	tip +="，<br/>3秒后系统将为您重新刷新页面，或<a href='javascript:window.location.reload()'><font color='blue'><b>手动刷新</b></font></a>";
            	LEx.dialog.tips(tip,3,function(){
            		window.location.reload();
            	});
            	return null;
            }
	        if( this.returns.state==0){
	        	this.returns.error=this.returns.message;
	        }
	        return this.returns;
        }
    }
};
(function() {               // Using a closure to keep global namespace clean.
    if (LEx == null)
        LEx = new Object();
    if (LEx.evalEx == null)
        LEx.evalEx = function(src) { return eval(src); };

    var UNDEFINED;
    if (Array.prototype.pop == null)  // IE 5.x fix from Igor Poteryaev.
        Array.prototype.pop = function() {
            if (this.length === 0) {return UNDEFINED;}
            return this[--this.length];
        };
    if (Array.prototype.push == null) // IE 5.x fix from Igor Poteryaev.
        Array.prototype.push = function() {
            for (var i = 0; i < arguments.length; ++i) {this[this.length] = arguments[i];}
            return this.length;
        };

    LEx.parseTemplate = function(tmplContent, optTmplName, optEtc) {
        if (optEtc == null)
            optEtc = LEx.parseTemplate_etc;
        var funcSrc = parse(tmplContent, optTmplName, optEtc);
        var func = LEx.evalEx(funcSrc, optTmplName, 1);
        if (func != null)
            return new optEtc.Template(optTmplName, tmplContent, funcSrc, func, optEtc);
        return null;
    }
    
    try {
        String.prototype.process = function(context, optFlags) {
            var template = LEx.parseTemplate(this, null);
            if (template != null)
                return template.process(context, optFlags);
            return this;
        }
    } catch (e) { // Swallow exception, such as when String.prototype is sealed.
    }
    
    LEx.parseTemplate_etc = {};            // Exposed for extensibility.
    LEx.parseTemplate_etc.statementTag = "forelse|for|if|elseif|else|var|macro";
    LEx.parseTemplate_etc.statementDef = { // Lookup table for statement tags.
        "if"     : { delta:  1, prefix: "if (", suffix: ") {", paramMin: 1 },
        "else"   : { delta:  0, prefix: "} else {" },
        "elseif" : { delta:  0, prefix: "} else if (", suffix: ") {", paramDefault: "true" },
        "/if"    : { delta: -1, prefix: "}" },
        "for"    : { delta:  1, paramMin: 3, 
                     prefixFunc : function(stmtParts, state, tmplName, etc) {
                        if (stmtParts[2] != "in")
                            throw new etc.ParseError(tmplName, state.line, "bad for loop statement: " + stmtParts.join(' '));
                        var iterVar = stmtParts[1];
                        var listVar = "__LIST__" + iterVar;
                        return [ "var ", listVar, " = ", stmtParts[3], ";",
                             // Fix from Ross Shaull for hash looping, make sure that we have an array of loop lengths to treat like a stack.
                             "var __LENGTH_STACK__;",
                             "if (typeof(__LENGTH_STACK__) == 'undefined' || !__LENGTH_STACK__.length) __LENGTH_STACK__ = new Array();", 
                             "__LENGTH_STACK__[__LENGTH_STACK__.length] = 0;", // Push a new for-loop onto the stack of loop lengths.
                             "if ((", listVar, ") != null) { ",
                             "var ", iterVar, "_ct = 0;",       // iterVar_ct variable, added by B. Bittman     
                             "for (var ", iterVar, "_index in ", listVar, ") { ",
                             iterVar, "_ct++;",
                             "if (typeof(", listVar, "[", iterVar, "_index]) == 'function') {continue;}", // IE 5.x fix from Igor Poteryaev.
                             "__LENGTH_STACK__[__LENGTH_STACK__.length - 1]++;",
                             "var ", iterVar, " = ", listVar, "[", iterVar, "_index];" ].join("");
                     } },
        "forelse" : { delta:  0, prefix: "} } if (__LENGTH_STACK__[__LENGTH_STACK__.length - 1] == 0) { if (", suffix: ") {", paramDefault: "true" },
        "/for"    : { delta: -1, prefix: "} }; delete __LENGTH_STACK__[__LENGTH_STACK__.length - 1];" }, // Remove the just-finished for-loop from the stack of loop lengths.
        "var"     : { delta:  0, prefix: "var ", suffix: ";" },
        "macro"   : { delta:  1, 
                      prefixFunc : function(stmtParts, state, tmplName, etc) {
                          var macroName = stmtParts[1].split('(')[0];
                          return [ "var ", macroName, " = function", 
                                   stmtParts.slice(1).join(' ').substring(macroName.length),
                                   "{ var _OUT_arr = []; var _OUT = { write: function(m) { if (m) _OUT_arr.push(m); } }; " ].join('');
                     } }, 
        "/macro"  : { delta: -1, prefix: " return _OUT_arr.join(''); };" }
    }
    LEx.parseTemplate_etc.modifierDef = {
        "eat"        : function(v)    { return ""; },
        "escape"     : function(s)    { return String(s).replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;"); },
        "capitalize" : function(s)    { return String(s).toUpperCase(); },
        "default"    : function(s, d) { return s != null ? s : d; }
    }
    LEx.parseTemplate_etc.modifierDef.h = LEx.parseTemplate_etc.modifierDef.escape;

    LEx.parseTemplate_etc.Template = function(tmplName, tmplContent, funcSrc, func, etc) {
        this.process = function(context, flags) {
            if (context == null){
                context = {};
            }
            
            if (context._MODIFIERS == null)
                context._MODIFIERS = {};
            if (context.defined == null)
                context.defined = function(str) { return (context[str] != undefined); };
            for (var k in etc.modifierDef) {
                if (context._MODIFIERS[k] == null)
                    context._MODIFIERS[k] = etc.modifierDef[k];
            }
            if (flags == null)
                flags = {};
            var resultArr = [];
            var resultOut = { write: function(m) { resultArr.push(m); } };
            try {
                func(resultOut, context, flags);
            } catch (e) {
                if (flags.throwExceptions == true)
                    throw e;
                var result = new String(resultArr.join("") + "[ERROR: " + e.toString() + (e.message ? '; ' + e.message : '') + "]");
                result["exception"] = e;
                return result;
            }
            return resultArr.join("");
        }
        this.name       = tmplName;
        this.source     = tmplContent; 
        this.sourceFunc = funcSrc;
        this.toString   = function() { return "LEx.Template [" + tmplName + "]"; }
    }
    LEx.parseTemplate_etc.ParseError = function(name, line, message) {
        this.name    = name;
        this.line    = line;
        this.message = message;
    }
    LEx.parseTemplate_etc.ParseError.prototype.toString = function() { 
        return ("LEx template ParseError in " + this.name + ": line " + this.line + ", " + this.message);
    }
    
    var parse = function(body, tmplName, etc) {
        body = cleanWhiteSpace(body);
        var funcText = [ "var LEx_Template_TEMP = function(_OUT, _CONTEXT, _FLAGS) { with (_CONTEXT) {" ];
        var state    = { stack: [], line: 1 };                              // TODO: Fix line number counting.
        var endStmtPrev = -1;
        while (endStmtPrev + 1 < body.length) {
            var begStmt = endStmtPrev;
            // Scan until we find some statement markup.
            begStmt = body.indexOf("{", begStmt + 1);
            while (begStmt >= 0) {
                var endStmt = body.indexOf('}', begStmt + 1);
                var stmt = body.substring(begStmt, endStmt);
                var blockrx = stmt.match(/^\{(cdata|minify|eval)/); // From B. Bittman, minify/eval/cdata implementation.
                if (blockrx) {
                    var blockType = blockrx[1]; 
                    var blockMarkerBeg = begStmt + blockType.length + 1;
                    var blockMarkerEnd = body.indexOf('}', blockMarkerBeg);
                    if (blockMarkerEnd >= 0) {
                        var blockMarker;
                        if( blockMarkerEnd - blockMarkerBeg <= 0 ) {
                            blockMarker = "{/" + blockType + "}";
                        } else {
                            blockMarker = body.substring(blockMarkerBeg + 1, blockMarkerEnd);
                        }                        
                        
                        var blockEnd = body.indexOf(blockMarker, blockMarkerEnd + 1);
                        if (blockEnd >= 0) {                            
                            emitSectionText(body.substring(endStmtPrev + 1, begStmt), funcText);
                            
                            var blockText = body.substring(blockMarkerEnd + 1, blockEnd);
                            if (blockType == 'cdata') {
                                emitText(blockText, funcText);
                            } else if (blockType == 'minify') {
                                emitText(scrubWhiteSpace(blockText), funcText);
                            } else if (blockType == 'eval') {
                                if (blockText != null && blockText.length > 0) // From B. Bittman, eval should not execute until process().
                                    funcText.push('_OUT.write( (function() { ' + blockText + ' })() );');
                            }
                            begStmt = endStmtPrev = blockEnd + blockMarker.length - 1;
                        }
                    }                        
                } else if (body.charAt(begStmt - 1) != '$' &&               // Not an expression or backslashed,
                           body.charAt(begStmt - 1) != '\\') {              // so check if it is a statement tag.
                    var offset = (body.charAt(begStmt + 1) == '/' ? 2 : 1); // Close tags offset of 2 skips '/'.
                                                                            // 10 is larger than maximum statement tag length.
                    if (body.substring(begStmt + offset, begStmt + 10 + offset).search(LEx.parseTemplate_etc.statementTag) == 0) 
                        break;                                              // Found a match.
                }
                begStmt = body.indexOf("{", begStmt + 1);
            }
            if (begStmt < 0)                              // In "a{for}c", begStmt will be 1.
                break;
            var endStmt = body.indexOf("}", begStmt + 1); // In "a{for}c", endStmt will be 5.
            if (endStmt < 0)
                break;
            emitSectionText(body.substring(endStmtPrev + 1, begStmt), funcText);
            emitStatement(body.substring(begStmt, endStmt + 1), state, funcText, tmplName, etc);
            endStmtPrev = endStmt;
        }
        emitSectionText(body.substring(endStmtPrev + 1), funcText);
        if (state.stack.length != 0)
            throw new etc.ParseError(tmplName, state.line, "unclosed, unmatched statement(s): " + state.stack.join(","));
        funcText.push("}}; LEx_Template_TEMP");
        return funcText.join("");
    }
    
    var emitStatement = function(stmtStr, state, funcText, tmplName, etc) {
        var parts = stmtStr.slice(1, -1).split(' ');
        var stmt = etc.statementDef[parts[0]]; // Here, parts[0] == for/if/else/...
        if (stmt == null) {                    // Not a real statement.
            emitSectionText(stmtStr, funcText);
            return;
        }
        if (stmt.delta < 0) {
            if (state.stack.length <= 0)
                throw new etc.ParseError(tmplName, state.line, "close tag does not match any previous statement: " + stmtStr);
            state.stack.pop();
        } 
        if (stmt.delta > 0)
            state.stack.push(stmtStr);

        if (stmt.paramMin != null &&
            stmt.paramMin >= parts.length)
            throw new etc.ParseError(tmplName, state.line, "statement needs more parameters: " + stmtStr);
        if (stmt.prefixFunc != null)
            funcText.push(stmt.prefixFunc(parts, state, tmplName, etc));
        else 
            funcText.push(stmt.prefix);
        if (stmt.suffix != null) {
            if (parts.length <= 1) {
                if (stmt.paramDefault != null)
                    funcText.push(stmt.paramDefault);
            } else {
                for (var i = 1; i < parts.length; i++) {
                    if (i > 1)
                        funcText.push(' ');
                    funcText.push(parts[i]);
                }
            }
            funcText.push(stmt.suffix);
        }
    }

    var emitSectionText = function(text, funcText) {
        if (text.length <= 0)
            return;
        var nlPrefix = 0;               // Index to first non-newline in prefix.
        var nlSuffix = text.length - 1; // Index to first non-space/tab in suffix.
        while (nlPrefix < text.length && (text.charAt(nlPrefix) == '\n'))
            nlPrefix++;
        while (nlSuffix >= 0 && (text.charAt(nlSuffix) == ' ' || text.charAt(nlSuffix) == '\t'))
            nlSuffix--;
        if (nlSuffix < nlPrefix)
            nlSuffix = nlPrefix;
        if (nlPrefix > 0) {
            funcText.push('if (_FLAGS.keepWhitespace == true) _OUT.write("');
            var s = text.substring(0, nlPrefix).replace('\n', '\\n'); // A macro IE fix from BJessen.
            if (s.charAt(s.length - 1) == '\n')
            	s = s.substring(0, s.length - 1);
            funcText.push(s);
            funcText.push('");');
        }
        var lines = text.substring(nlPrefix, nlSuffix + 1).split('\n');
        for (var i = 0; i < lines.length; i++) {
            emitSectionTextLine(lines[i], funcText);
            if (i < lines.length - 1)
                funcText.push('_OUT.write("\\n");\n');
        }
        if (nlSuffix + 1 < text.length) {
            funcText.push('if (_FLAGS.keepWhitespace == true) _OUT.write("');
            var s = text.substring(nlSuffix + 1).replace('\n', '\\n');
            if (s.charAt(s.length - 1) == '\n')
            	s = s.substring(0, s.length - 1);
            funcText.push(s);
            funcText.push('");');
        }
    }
    
    var emitSectionTextLine = function(line, funcText) {
        var endMarkPrev = '}';
        var endExprPrev = -1;
        while (endExprPrev + endMarkPrev.length < line.length) {
            var begMark = "${", endMark = "}";
            var begExpr = line.indexOf(begMark, endExprPrev + endMarkPrev.length); // In "a${b}c", begExpr == 1
            if (begExpr < 0)
                break;
            if (line.charAt(begExpr + 2) == '%') {
                begMark = "${%";
                endMark = "%}";
            }
            var endExpr = line.indexOf(endMark, begExpr + begMark.length);         // In "a${b}c", endExpr == 4;
            if (endExpr < 0)
                break;
            emitText(line.substring(endExprPrev + endMarkPrev.length, begExpr), funcText);                
            // Example: exprs == 'firstName|default:"John Doe"|capitalize'.split('|')
            var exprArr = line.substring(begExpr + begMark.length, endExpr).replace(/\|\|/g, "#@@#").split('|');
            for (var k in exprArr) {
                if (exprArr[k].replace) // IE 5.x fix from Igor Poteryaev.
                    exprArr[k] = exprArr[k].replace(/#@@#/g, '||');
            }
            funcText.push('_OUT.write(');
            emitExpression(exprArr, exprArr.length - 1, funcText); 
            funcText.push(');');
            endExprPrev = endExpr;
            endMarkPrev = endMark;
        }
        emitText(line.substring(endExprPrev + endMarkPrev.length), funcText); 
    }
    
    var emitText = function(text, funcText) {
        if (text == null ||
            text.length <= 0)
            return;
        text = text.replace(/\\/g, '\\\\');
        text = text.replace(/\n/g, '\\n');
        text = text.replace(/"/g,  '\\"');
        funcText.push('_OUT.write("');
        funcText.push(text);
        funcText.push('");');
    }
    
    var emitExpression = function(exprArr, index, funcText) {
        // Ex: foo|a:x|b:y1,y2|c:z1,z2 is emitted as c(b(a(foo,x),y1,y2),z1,z2)
        var expr = exprArr[index]; // Ex: exprArr == [firstName,capitalize,default:"John Doe"]
        if (index <= 0) {          // Ex: expr    == 'default:"John Doe"'
            funcText.push(expr);
            return;
        }
        var parts = expr.split(':');
        funcText.push('_MODIFIERS["');
        funcText.push(parts[0]); // The parts[0] is a modifier function name, like capitalize.
        funcText.push('"](');
        emitExpression(exprArr, index - 1, funcText);
        if (parts.length > 1) {
            funcText.push(',');
            funcText.push(parts[1]);
        }
        funcText.push(')');
    }

    var cleanWhiteSpace = function(result) {
        result = result.replace(/\t/g,   "    ");
        result = result.replace(/\r\n/g, "\n");
        result = result.replace(/\r/g,   "\n");
        result = result.replace(/^(\s*\S*(\s+\S+)*)\s*$/, '$1'); // Right trim by Igor Poteryaev.
        return result;
    }

    var scrubWhiteSpace = function(result) {
        result = result.replace(/^\s+/g,   "");
        result = result.replace(/\s+$/g,   "");
        result = result.replace(/\s+/g,   " ");
        result = result.replace(/^(\s*\S*(\s+\S+)*)\s*$/, '$1'); // Right trim by Igor Poteryaev.
        return result;
    }

    // The DOM helper functions depend on DOM/DHTML, so they only work in a browser.
    // However, these are not considered core to the engine.
    //
    LEx.parseDOMTemplate = function(elementId, optDocument, optEtc) {
        if (optDocument == null)
            optDocument = document;
        var element = optDocument.getElementById(elementId);
        var content = element.value;     // Like textarea.value.
        if (content == null)
            content = element.innerHTML; // Like textarea.innerHTML.
        content = content.replace(/&lt;/g, "<").replace(/&gt;/g, ">");
        return LEx.parseTemplate(content, elementId, optEtc);
    }

    LEx.processDOMTemplate = function(elementId, context, optFlags, optDocument, optEtc) {
        return LEx.parseDOMTemplate(elementId, optDocument, optEtc).process(context, optFlags);
    }
    LEx.processTemplate = function(content,context,optFlags, optEtc) {
    	content = content.replace(/&lt;/g, "<").replace(/&gt;/g, ">");
    	return LEx.parseTemplate(content, Math.random(), optEtc).process(context, optFlags);
    }
}) ();
/*
 * Cookie基本操作，建议使用LEx.cookie包
 * LEx.cookie.get:获取cookie
 * LEx.cookie.set:设置cookie
 * LEx.cookie.del:删除cookie
 * */
var cookie_pre = '';
var cookie_domain = '';
var cookie_path = LEx.webPath;
function getcookie(name) {
    name = cookie_pre+name;
	var arg = name + "=";
	var alen = arg.length;
	var clen = document.cookie.length;
	var i = 0;
	while(i < clen) {
		var j = i + alen;
		if(document.cookie.substring(i, j) == arg) return getcookieval(j);
		i = document.cookie.indexOf(" ", i) + 1;
		if(i == 0) break;
	}
	return null;
}

function setcookie(name, value, days) {
    name = cookie_pre+name;
	var argc = setcookie.arguments.length;
	var argv = setcookie.arguments;
	var secure = (argc > 5) ? argv[5] : false;
	var expire = new Date();
	if(days==null || days==0) days=1;
	expire.setTime(expire.getTime() + 3600000*24*days);
	document.cookie = name + "=" + escape(value) + ("; path=" + cookie_path) + ((cookie_domain == '') ? "" : ("; domain=" + cookie_domain)) + ((secure == true) ? "; secure" : "") + ";expires="+expire.toGMTString();
}

function delcookie(name) {
    var exp = new Date();
	exp.setTime (exp.getTime() - 1);
	var cval = getcookie(name);
    name = cookie_pre+name;
	document.cookie = name+"="+cval+";expires="+exp.toGMTString();
}

function getcookieval(offset) {
	var endstr = document.cookie.indexOf (";", offset);
	if(endstr == -1)
	endstr = document.cookie.length;
	return unescape(document.cookie.substring(offset, endstr));
}
LEx.cookie={};
LEx.cookie.get=getcookie;
LEx.cookie.set=setcookie;
LEx.cookie.del=delcookie;
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
		$("#"+targetId).html(this.pagebar(tempId,total,fun,maxSize,className));
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
if(LEx.util == null){
	LEx.util = {};
}
if(LEx.util.Format == null){
	LEx.util.Format = {};
}

//时间格式化，输出字符串
Date.prototype.pattern=function(fmt) {
    var o = {
    "M+" : this.getMonth()+1, //月份
    "d+" : this.getDate(), //日
    "h+" : this.getHours()%12 == 0 ? 12 : this.getHours()%12, //小时
    "H+" : this.getHours(), //小时
    "m+" : this.getMinutes(), //分
    "s+" : this.getSeconds(), //秒
    "q+" : Math.floor((this.getMonth()+3)/3), //季度
    "S" : this.getMilliseconds() //毫秒
    };
    var week = {
    "0" : "\u65e5",
    "1" : "\u4e00",
    "2" : "\u4e8c",
    "3" : "\u4e09",
    "4" : "\u56db",
    "5" : "\u4e94",
    "6" : "\u516d"
    };
    if(/(y+)/.test(fmt)){
        fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));
    }
    if(/(E+)/.test(fmt)){
        fmt=fmt.replace(RegExp.$1, ((RegExp.$1.length>1) ? (RegExp.$1.length>2 ? "\u661f\u671f" : "\u5468") : "")+week[this.getDay()+""]);
    }
    for(var k in o){
        if(new RegExp("("+ k +")").test(fmt)){
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
        }
    }
    return fmt;
}


LEx.util.Format.formatDate = function(v, dateFormat) {
    try {
        if (dateFormat == undefined || typeof dateFormat != "string") {
            dateFormat = "yyyy-MM-dd";
        }
        if ((typeof v) == "number"){
        	var o = new Date(v*1000);
            return o.pattern(dateFormat);
        }
        if ((typeof v) == "string" && v.indexOf("/Date(") == 0) {
            var date = eval('new ' + eval(v).source);
            return date.pattern(dateFormat);
        }
        if (v.time) {
            var o = new Date(v.time);
            return o.pattern(dateFormat);
        }
        else {
            if (v != "") {
                v = v.replace(/\//g, "-");
                if (v.split(" ")) {
                    var myDate = v.split(" ")[0];
                } else {
                    var myDate = v;
                }
                myDate = myDate.replace("-0", "-").replace("-0", "-");
                var month = myDate.split("-")[1];
                if(month<10)
                	month = "0"+month;
                var day = myDate.split("-")[2];
                if(day<10)
                	day = "0"+day;	
                return myDate.split("-")[0] + "-" + month + "-" + day;
                /* var nowDate = new Date();
                if (myDate.split("-")[0] == nowDate.getFullYear()) {//本年度
                    return myDate.split("-")[1] + "月" + myDate.split("-")[2] + "日";
                } else {//非本年度
                    return myDate.split("-")[0] + "年" + myDate.split("-")[1] + "月" + myDate.split("-")[2] + "日";
                }*/
            }
        }
    }
    catch (e) { }
    return "";
};
LEx.util.Format.formatDateAll = function(v, dateFormat) {
    try {
        if (dateFormat == undefined || typeof dateFormat != "string") {
            dateFormat = "yyyy-MM-dd";
        }
        if ((typeof v) == "number"){
        	var o = new Date(v*1000);
            return o.pattern(dateFormat);
        }
        if ((typeof v) == "string" && v.indexOf("/Date(") == 0) {
            var date = eval('new ' + eval(v).source);
            return date.pattern(dateFormat);
        }
        if (v.time) {
            var o = new Date(v.time);
            return o.pattern(dateFormat);
        }
        else {
            if (v != "") {
                v = v.replace(/\//g, "-");
                if (v.split(" ")) {
                    var myDate = v.split(" ")[0];
                } else {
                    var myDate = v;
                }
                myDate = myDate.replace("-0", "-").replace("-0", "-");
                var month = myDate.split("-")[1];
                if(month<10)
                	month = "0"+month;
                var day = myDate.split("-")[2];
                if(day<10)
                	day = "0"+day;                
                return myDate.split("-")[0] + "年" + myDate.split("-")[1] + "月" + myDate.split("-")[2] + "日";
            }
        }
    }
    catch (e) { }
    return "";
};
LEx.util.Format.formatNumber=function(num){
	var v =(num*100).toString().substring(0,6);
	return "<font size='1'>"+v+"%</font>";
};

LEx.util.Format.formatBytes=function(v){
	 if(v<1024){
	 	return v + "B";
	}
	else if(v<1048576 && v>=1024){
		return parseFloat(v/1024).toFixed(2)+"KB";
	}
	else if(v<1073741824 && v>=1048576){
		return parseFloat(v/1048576).toFixed(2)+"MB";
	}
	else {
		return parseFloat(v/1073741824).toFixed(2)+"GB";
	}
	return v;
};

LEx.util.Format.formatUtil=function(v,d){
	 v = parseFloat(v);
	 if(v<10000){
	 	if(d=="天")return v.toFixed(2)+""+d;
		else return v.toFixed(0)+""+d;
	}
	else if(v<100000000 && v>=10000){
		return parseFloat(v/10000).toFixed(2)+"万"+d;
	}
	/*else if(v<100000000 && v>=1000000){
		return parseFloat(v/1000000).toFixed(2)+"百万"+d;
	}*/
	else{
		return parseFloat(v/100000000).toFixed(2)+"亿"+d;
	}
	return v+""+d;
};
LEx.util.Format.formatDateX=function(xdate){
	if(xdate)
	if(xdate.length>0){
		if(xdate.split(" ")){
			var myDate = xdate.split(" ")[0];
		}else{
			var myDate = xdate;
		}
		myDate = myDate.replace("-0","-").replace("-0","-");
		var nowDate = new Date();
		if(myDate.split("-")[0] == nowDate.getFullYear() ){//本年度
			return myDate.split("-")[1]+"月"+myDate.split("-")[2]+"日";
		}else{//非本年度
			return myDate.split("-")[0]+"年"+myDate.split("-")[1]+"月"+myDate.split("-")[2]+"日";
		}
	}
};

LEx.util.Format.formatMoney = function(num){
	num = num.toString().replace(/\$|\,/g,'');
	if(isNaN(num))
	num = "0";
	sign = (num == (num = Math.abs(num)));
	num = Math.floor(num*100+0.50000000001);
	cents = num%100;
	num = Math.floor(num/100).toString();
	if(cents<10)
	cents = "0" + cents;
	for (var i = 0; i < Math.floor((num.length-(1+i))/3); i++)
	num = num.substring(0,num.length-(4*i+3))+','+
	num.substring(num.length-(4*i+3));
	return (((sign)?'':'-') + '¥' + num + '.' + cents);
};


/*生成总和数据*/
LEx.util.makerTotal = function(obj){
	if(obj.id == null){
		alert("此方法至少需要提供【id】参数");	
		return;
	}
	if(obj.startCol == null){
		obj.startCol = 1;	
	}
	if(obj.startRow == null){
		obj.startRow = 1;	
	}
	//dm小数点后几位
	if(obj.dm == null){
		obj.dm = 2;	
	}
	if(obj.allowFoot == null){
		obj.allowFoot = true;
	}
	if(obj.ignore == null){
		obj.ignore = [];	
	}
	if(obj.align == null){
		obj.align = "center";	
	}
	var table = $("#"+obj.id);
	//多少列及每一列的宽度是多少
	var index = 0;
	
	//多少行记录
	var row = table.find('tr:first');
	var ttf = table.find("tfoot");
	if(ttf != null){
		ttf.html("");
	}
	//多少列记录
	var cols = row.find("td,th");
	
	//合计
	var datas = [];
	//for(var i=0;i<index;i++){
		//datas[i] = 0;	
	//}
	
	cols.each(function(i) {
		var colNum = 1;
		try{
			colNum = $(this).attr("colSpan");
			if(!LEx.isNotNull(colNum)){
				colNum = 1;
			}
		}
		catch(e){
			colNum = 1;
		}
		for(var i=0;i<colNum;i++){
			var obj ={};
			obj.num = 0;
			obj.show = $(this).css("display");
			datas[index+i] = obj;
		}
		index += Number(colNum);
		
	});
	//默认宽度设置
	//if(index ==0){
		//index = 1;
	//}
	
	var trs = table.find('tr');
	trs.each(function(row) {
		if(row>=obj.startRow){
			//获取具体一行的对象
			var tr = $(this);
			var tds = tr.find('td,th');
			tds.each(function(col) {
				if(col>=obj.startCol){
					var td = $(this);
					td.css("text-align",obj.align);
					var ignorFlag = false;
					for(var ti =0;ti<obj.ignore.length;ti++){
						if(col == obj.ignore[ti]){
							ignorFlag = true;
							break;
						}
					}
					if(!ignorFlag){
						var c =0;
						if(td.html() == ""){
							td.html("—");
						}else{
							try{c = parseFloat(td.html());}
							catch(e){}
							if(!isNaN(c)){
								if(c != 0){
									td.html(c.toFixed(obj.dm));
									if(datas[col] == undefined){
										datas[col] = {};
										datas[col].num = 0;
									}
									datas[col].num = datas[col].num+c;
								}else{
									td.html("—");	
								}
							}
						}
					}
				}
			});
		}
	});
	var arrFoot =[];
	arrFoot.push("<tr style='background:#F3F3F3;'>");
	arrFoot.push("<td style='text-align:center;border-top:solid 1px #dddddd;' colspan=\""+obj.startCol+"\">合计</td>");
	for(var i=0;i<datas.length;i++){
		if(i>=obj.startCol){
			var v = "—";
			if(datas[i] != null && datas[i].num !=0){
				v = datas[i].num.toFixed(obj.dm);	
			}
			arrFoot.push("<td style='text-align:"+obj.align+";border-top:solid 1px #dddddd;display:"+datas[i].show+";'>"+v+"</td>");	
		}	
	}
	arrFoot.push("</tr>");
	if(obj.allowFoot){
		var d = document.getElementById(obj.id);
		if(d.tFoot == null){
			d.createTFoot();
		}
		table.find("tfoot").html(arrFoot.join(''));
	}else{
		table.find("tbody").append(arrFoot.join(''));	
	}
}

LEx.util.Format.formatKey = function(serviceName,value,dataType){
	if(dataType == null){dataType = "";}
	return "<span class=\""+serviceName+"_tmplate_depart_id_"+dataType+"\" v=\""+value+"\">.</span>";
}



var now = new Date();
var nowDayOfWeek = now.getDay();//今天本周的第几天
var nowDay = now.getDate(); //当前日
var nowMonth = now.getMonth();//当前月
var nowYear = now.getYear(); //当前年
nowYear += (nowYear < 2000) ? 1900 : 0;
var lastMonthDate = new Date(); //上月日期
lastMonthDate.setDate(1);
lastMonthDate.setMonth(lastMonthDate.getMonth()-1);
var lastYear = lastMonthDate.getYear();
var lastMonth = lastMonthDate.getMonth();
LEx.util.DateTool ={
	//获得某月的天数
	 getMonthDays:function(myMonth){
		var monthStartDate = new Date(nowYear, myMonth, 1);
		var monthEndDate = new Date(nowYear, myMonth + 1, 1);
		var days = (monthEndDate - monthStartDate)/(1000 * 60 * 60 * 24);
		return days;
	} ,
	//格局化日期：yyyy-MM-dd
	 formatDate:function(date) {
		var myyear = date.getFullYear();
		var mymonth = date.getMonth()+1;
		var myweekday = date.getDate();
		var myhour =  date.getHours();
		var myminute = date.getMinutes();
		var mysecond = date.getSeconds();

		if(mymonth < 10){
			mymonth = "0" + mymonth;
		}
		if(myweekday < 10){
			myweekday = "0" + myweekday;
		}
		if(myhour < 10){
			myhour = "0" + myhour;
		}
		if(myminute < 10){
			myminute = "0" + myminute;
		}
		if(mysecond < 10){
			mysecond = "0" + mysecond;
		}
		return (myyear+"-"+mymonth + "-" + myweekday+" "+myhour+":"+myminute+":"+mysecond);
	}
	,
	//获得本周的开端日期
	 getWeekStartDate:function() {
		var weekStartDate = new Date(nowYear, nowMonth, nowDay - nowDayOfWeek);
		return this.formatDate(weekStartDate);
	} ,
	//获得本周的停止日期
	 getWeekEndDate:function() {
		var weekEndDate = new Date(nowYear, nowMonth, nowDay + (6 - nowDayOfWeek));
		return this.formatDate(weekEndDate);
	} ,
	//获得本月的开端日期
	 getMonthStartDate:function(){
		var monthStartDate = new Date(nowYear, nowMonth, 1);
		return this.formatDate(monthStartDate);
	}
	,
	//获得本月的停止日期
	 getMonthEndDate:function(){
		var monthEndDate = new Date(nowYear, nowMonth, this.getMonthDays(nowMonth));
		return this.formatDate(monthEndDate);
	}
	,
	//获得上月开端时候
	 getLastMonthStartDate:function(){
		var lastMonthStartDate = new Date(nowYear, lastMonth, 1);
		return this.formatDate(lastMonthStartDate);
	}
	,
	//获得上月停止时候
	 getLastMonthEndDate:function(){
		var lastMonthEndDate = new Date(nowYear, lastMonth, this.getMonthDays(lastMonth));
		return this.formatDate(lastMonthEndDate);
	}
	,
	//获得本季度的开端月份
	 getQuarterStartMonth:function(){
		var quarterStartMonth = 0;
		if(nowMonth<3){
		quarterStartMonth = 0;
		}
		if(2<nowMonth && nowMonth<6){
		quarterStartMonth = 3;
		}
		if(5<nowMonth && nowMonth<9){
		quarterStartMonth = 6;
		}
		if(nowMonth>8){
		quarterStartMonth = 9;
		}
		return quarterStartMonth;
	}
	,
	//获得本季度的开端日期
	getQuarterStartDate:function(){
		var quarterStartDate = new Date(nowYear, this.getQuarterStartMonth(), 1);
		return this.formatDate(quarterStartDate);
	}
	,
	//或的本季度的停止日期
	getQuarterEndDate:function(){
		var quarterEndMonth = this.getQuarterStartMonth() + 2;
		var quarterStartDate = new Date(nowYear, quarterEndMonth, this.getMonthDays(quarterEndMonth));
		return this.formatDate(quarterStartDate);
	}
	,
	// 输入日期对象和增加的月份数量，返回类似“201204”的日期字符串
	addMonths:function(nowDate, months){  
		var _year = nowDate.getFullYear();
		var _month = nowDate.getMonth();
		
		
		_month = _month + months;
		
		while (_month < 0){  // 0 是一月份
			_year = _year - 1;
			_month = _month + 12;
		}
		while (_month > 11){  // 11是十二月份
			_year = _year + 1;
			_month = _month - 12;
		}
		_month = _month + 1;  // 转换成正常的月份，1代表二月、10代表十一月等
		if (_month < 10){
			_month = "0" + _month;
		}
		return _year + "-" + _month;
	}
};

LEx.util.Format.truncate = function(str,len,defaultStr){
	var length = str.length;
	var result = str;
	if(!LEx.isNotNull(defaultStr)){
		defaultStr = '...';
	}
	if(length > len){
		result = str.substr(0,len).concat(defaultStr);
	}
	return result;
}

Date.prototype.DateAdd = function(strInterval, Number) {       
    var dtTmp = this;    
    switch (strInterval) {     
        case 's' :return new Date(Date.parse(dtTmp) + (1000 * Number));    
        case 'n' :return new Date(Date.parse(dtTmp) + (60000 * Number));    
        case 'h' :return new Date(Date.parse(dtTmp) + (3600000 * Number));    
        case 'd' :return new Date(Date.parse(dtTmp) + (86400000 * Number));    
        case 'w' :return new Date(Date.parse(dtTmp) + ((86400000 * 7) * Number));    
        case 'q' :return new Date(dtTmp.getFullYear(), (dtTmp.getMonth()) + Number*3, dtTmp.getDate(), dtTmp.getHours(), dtTmp.getMinutes(), dtTmp.getSeconds());    
        case 'm' :return new Date(dtTmp.getFullYear(), (dtTmp.getMonth()) + Number, dtTmp.getDate(), dtTmp.getHours(), dtTmp.getMinutes(), dtTmp.getSeconds());    
        case 'y' :return new Date((dtTmp.getFullYear() + Number), dtTmp.getMonth(), dtTmp.getDate(), dtTmp.getHours(), dtTmp.getMinutes(), dtTmp.getSeconds());    
    }    
}
/**
*事项业务状态表
*分组 -> 大类状态 -> 小类状态
*根据下面的方法获取业务状态的转换
*如果关系有变化，在子版本中的plus.js里定义关系
*如：LEx.icityBusiness.statusCategories = [新的关系];
*这里默认深圳版本的关系
*注：如果【小类状态（statusList）】没有变化，不要重新定义
**/
LEx.icityBusiness = {
	defaultValue:"办理中",
	statusList : [//小类状态
		{name:"暂存",value:"00"},
		{name:"已撤回",value:"03"},
		{name:"已提交",value:"11"},//待预审
		{name:"预审通过",value:"16"},//舟山预审通过
		{name:"办理中",value:"01"},//预审时，受理，或者补齐补正后，状态为办理中
		{name:"不予受理",value:"02"},//预审时，不予受理， 最终态 ，处于此状态的业务为办结业务
		{name:"已驳回",value:"14"},//预审时，驳回
		{name:"补齐补正",value:"21"},//预审时，补交告知
		{name:"作废",value:"13"},//作废 ,预审时由办理单位主动将业务进行作废处理最终态 处于此状态的业务为办结业务
		{name:"退件",value:"96"},//退件 ,由办理单位主动将补齐超时的业务退件 处于此状态的业务为办结业务
		{name:"作废",value:"97"},//作废 ,由办理单位主动将业务进行作废处理最终态 处于此状态的业务为办结业务
		{name:"不予许可",value:"98"},
		{name:"准予许可",value:"99"}
	],
	statusCategories : [//大类状态
		  {id:"wtj",name:"暂存",value:"00"},//未提交
		  {id:"ych",name:"已撤回",value:"03"},
		  {id:"ytj",name:"已提交",value:"11"},//已提交
		  {id:"bqbz",name:"补齐补正",value:"21"},//已受理
		  {id:"ysl",name:"办理中",value:"01,16"},//已受理
		  {id:"blcg",name:"办理成功",value:"99"},
		  {id:"blsb",name:"办理失败",value:"02,13,14,96,97,98"}
	],
	statusGroups : [//分组状态
		{name:"已提交",id:"unProcess",value:"ytj"},
		{name:"办理中",id:"processing",value:"ysl"},
		{name:"已办结",id:"processed",value:"blcg,blsb"}
	],
	//根据小类value获取小类名称
	formatStatus : function(val){
		var len = this.statusList.length;
		var result = this.defaultValue;
		if(LEx.isNotNull(val)){
			val=val.replace(/(^\s+|\s+$)/g,"");   // 过滤空格
			for(var i=0;i<len;i++){
				if(val == this.statusList[i].value){
					result = this.statusList[i].name
					break;
				}
			}
		}
		return result;
	},
	//根据大类value获取大类名称
	formatCategory:function(val){
		var len = this.statusCategories.length;
		var result = this.defaultValue;
		if(LEx.isNotNull(val)){
			val=val.replace(/(^\s+|\s+$)/g,"");   // 过滤空格
			for(var i=0;i<len;i++){
				if(val == this.statusCategories[i].id){
					result = this.statusCategories[i].name
					break;
				}
			}
		}
		return result;
	},
	//根据分组value获取分组名称
	formatGroup:function(val){
		var len = this.statusGroups.length;
		var result = this.defaultValue;
		if(LEx.isNotNull(val)){
			val=val.replace(/(^\s+|\s+$)/g,"");   // 过滤空格
			for(var i=0;i<len;i++){
				if(val == this.statusGroups[i].id){
					result = this.statusGroups[i].name
					break;
				}
			}
		}
		return result;
	},
	getIdFromValuesByVal:function(val,queryList){
		var len = queryList.length;
		var result = '';
		var statusStr = '';
		if(LEx.isNotNull(val)){
			val=val.replace(/(^\s+|\s+$)/g,"");   // 过滤空格
			for(var i=0;i<len;i++){
				if(LEx.isNotNull(result)){
					break;
				}
				statusStr = queryList[i].value;
				var tempArr = new Array();
				tempArr = statusStr.split(",");
				for(var j=0;j<tempArr.length;j++){
					if(val == tempArr[j]){
						result = queryList[i].id;
						break;
					}
				}
			}
		}
		if(!LEx.isNotNull(result)){
			result = this.defaultValue;
		}
		return result;
	},
	//在queryList中，根据id获取value字符串
	getValuesById:function(queryList,val){
		var len = queryList.length;
		var result = '';
		if(LEx.isNotNull(val)){
			val=val.replace(/(^\s+|\s+$)/g,"");   // 过滤空格
			for(var i=0;i<len;i++){
				if(val == queryList[i].id){
					result = queryList[i].value;
					break;
				}
			}
		}

		if(!LEx.isNotNull(result)){
			result = this.defaultValue;
		}
		return result;
	},
	//根据大类状态id获取其下的小类value，并将以单引号和分号分开，否则返回默认值
	getStatusStrByCategory:function(val){
		var len = '';
		var result = '';
		var statusStr = '';

		if(LEx.isNotNull(val)){
			statusStr = this.getValuesById(this.statusCategories,val);
			var tempArr = new Array();
			tempArr = statusStr.split(",");
			len = tempArr.length;
			for(var i=0;i<len;i++){
				result += "" + tempArr[i] + ",";
			}
			result = result.substr(0,result.length-1);
		}

		if(!LEx.isNotNull(result)){
			result = this.defaultValue;
		}
		return result;
	},
	//根据分组状态id获取其下的小类value，并将以单引号和分号分开，否则返回默认值
	getStatusStrByGroup:function(val){
		var len = '';
		var result = '';
		var categoryStr = '';
		if(LEx.isNotNull(val)){
			categoryStr = this.getValuesById(this.statusGroups,val);
			var tempArr = new Array();
			tempArr = categoryStr.split(",");
			len = tempArr.length;
			for(var i=0;i<len;i++){
				result += this.getStatusStrByCategory(tempArr[i]) + ",";
			}
			result = result.substr(0,result.length-1);
		}

		if(!LEx.isNotNull(result)){
			result = this.defaultValue;
		}
		return result;
	},
	//根据小类value获取大类id
	getCategoryidByStatus:function(val){
		var result = '';
		if(LEx.isNotNull(val)){
			val=val.replace(/(^\s+|\s+$)/g,"");   // 过滤空格
			result = this.getIdFromValuesByVal(val,this.statusCategories);
		}
		if(!LEx.isNotNull(result)){
			result = this.defaultValue;
		}
		return result;
	},
	//根据小类value获取大类名称
	getCategoryNameByStatus:function(val){
		return this.formatCategory(this.getCategoryidByStatus(val));
	},
	//根据大类id获取分组id
	getGroupIdByCategoryId:function(val){
		var result = '';
		if(LEx.isNotNull(val)){
			val=val.replace(/(^\s+|\s+$)/g,"");   // 过滤空格
			result = this.getIdFromValuesByVal(val,this.statusGroups);
		}
		if(!LEx.isNotNull(result)){
			result = this.defaultValue;
		}
		return result;
	},
	//根据大类id获取分组名称
	getGroupNameByCategoryId:function(val){
		return this.formatGroup(this.getGroupNameByCategoryId(val));
	},
	//根据小类id获取分组id
	getGroupIdByStatus:function(val){
		var categoryId = this.getCategoryidByStatus(val);
		return this.getGroupIdByCategoryId(categoryId);;
	},
	//根据小类id获取分组名称
	getGroupNameByStatus:function(val){
		return this.formatGroup(this.getGroupIdByStatus(val));
	},
	initSelect:function(id){
		if(!LEx.isNotNull(id)){
			LEx.alert("请传入id");
			return false;
		}
		var select = $("#"+id);
		var len = this.statusCategories.length;
		for(var i=0;i<len;i++){
			var categoryId = this.statusCategories[i].id;
			var tempStr = "<option value='" + categoryId+ "'  code=\"" +this.getStatusStrByCategory(categoryId)+ "\">";
			tempStr += this.formatCategory(categoryId) + "</option>"
			select.append(tempStr)
		}

	},
	initSpan:function(id){
		if(!LEx.isNotNull(id)){
			LEx.alert("请传入id");
			return false;
		}
		var oSpan = $("#"+id);
		var len = this.statusCategories.length;
		for(var i=0;i<len;i++){
			var categoryId = this.statusCategories[i].id;

			var tempStr = "|<a href='javascript:;' rel='" + categoryId+ "'  code=\"" +this.getStatusStrByCategory(categoryId)+ "\">";
			tempStr += this.formatCategory(categoryId) + "</a>"


			oSpan.append(tempStr)
		}

	}
}

/*!
 * jQuery Form Plugin
 * version: 3.44.0-2013.09.15
 * Requires jQuery v1.5 or later
 * Copyright (c) 2013 M. Alsup
 * Examples and documentation at: http://malsup.com/jquery/form/
 * Project repository: https://github.com/malsup/form
 * Dual licensed under the MIT and GPL licenses.
 * https://github.com/malsup/form#copyright-and-license
 */
/*global ActiveXObject */
;(function($) {
"use strict";

/*
    Usage Note:
    -----------
    Do not use both ajaxSubmit and ajaxForm on the same form.  These
    functions are mutually exclusive.  Use ajaxSubmit if you want
    to bind your own submit handler to the form.  For example,

    $(document).ready(function() {
        $('#myForm').on('submit', function(e) {
            e.preventDefault(); // <-- important
            $(this).ajaxSubmit({
                target: '#output'
            });
        });
    });

    Use ajaxForm when you want the plugin to manage all the event binding
    for you.  For example,

    $(document).ready(function() {
        $('#myForm').ajaxForm({
            target: '#output'
        });
    });

    You can also use ajaxForm with delegation (requires jQuery v1.7+), so the
    form does not have to exist when you invoke ajaxForm:

    $('#myForm').ajaxForm({
        delegation: true,
        target: '#output'
    });

    When using ajaxForm, the ajaxSubmit function will be invoked for you
    at the appropriate time.
*/

/**
 * Feature detection
 */
var feature = {};
feature.fileapi = $("<input type='file'/>").get(0).files !== undefined;
feature.formdata = window.FormData !== undefined;

var hasProp = !!$.fn.prop;

// attr2 uses prop when it can but checks the return type for
// an expected string.  this accounts for the case where a form 
// contains inputs with names like "action" or "method"; in those
// cases "prop" returns the element
$.fn.attr2 = function() {
    if ( ! hasProp )
        return this.attr.apply(this, arguments);
    var val = this.prop.apply(this, arguments);
    if ( ( val && val.jquery ) || typeof val === 'string' )
        return val;
    return this.attr.apply(this, arguments);
};

/**
 * ajaxSubmit() provides a mechanism for immediately submitting
 * an HTML form using AJAX.
 */
$.fn.ajaxSubmit = function(options) {
    /*jshint scripturl:true */

    // fast fail if nothing selected (http://dev.jquery.com/ticket/2752)
    if (!this.length) {
        log('ajaxSubmit: skipping submit process - no element selected');
        return this;
    }

    var method, action, url, $form = this;

    if (typeof options == 'function') {
        options = { success: options };
    }
    else if ( options === undefined ) {
        options = {};
    }

    method = options.type || this.attr2('method');
    action = options.url  || this.attr2('action');

    url = (typeof action === 'string') ? $.trim(action) : '';
    url = url || window.location.href || '';
    if (url) {
        // clean url (don't include hash vaue)
        url = (url.match(/^([^#]+)/)||[])[1];
    }

    options = $.extend(true, {
        url:  url,
        success: $.ajaxSettings.success,
        type: method || $.ajaxSettings.type,
        iframeSrc: /^https/i.test(window.location.href || '') ? 'javascript:false' : 'about:blank'
    }, options);

    // hook for manipulating the form data before it is extracted;
    // convenient for use with rich editors like tinyMCE or FCKEditor
    var veto = {};
    this.trigger('form-pre-serialize', [this, options, veto]);
    if (veto.veto) {
        log('ajaxSubmit: submit vetoed via form-pre-serialize trigger');
        return this;
    }

    // provide opportunity to alter form data before it is serialized
    if (options.beforeSerialize && options.beforeSerialize(this, options) === false) {
        log('ajaxSubmit: submit aborted via beforeSerialize callback');
        return this;
    }

    var traditional = options.traditional;
    if ( traditional === undefined ) {
        traditional = $.ajaxSettings.traditional;
    }

    var elements = [];
    var qx, a = this.formToArray(options.semantic, elements);
    if (options.data) {
        options.extraData = options.data;
        qx = $.param(options.data, traditional);
    }

    // give pre-submit callback an opportunity to abort the submit
    if (options.beforeSubmit && options.beforeSubmit(a, this, options) === false) {
        log('ajaxSubmit: submit aborted via beforeSubmit callback');
        return this;
    }

    // fire vetoable 'validate' event
    this.trigger('form-submit-validate', [a, this, options, veto]);
    if (veto.veto) {
        log('ajaxSubmit: submit vetoed via form-submit-validate trigger');
        return this;
    }

    var q = $.param(a, traditional);
    if (qx) {
        q = ( q ? (q + '&' + qx) : qx );
    }
    if (options.type.toUpperCase() == 'GET') {
        options.url += (options.url.indexOf('?') >= 0 ? '&' : '?') + q;
        options.data = null;  // data is null for 'get'
    }
    else {
        options.data = q; // data is the query string for 'post'
    }

    var callbacks = [];
    if (options.resetForm) {
        callbacks.push(function() { $form.resetForm(); });
    }
    if (options.clearForm) {
        callbacks.push(function() { $form.clearForm(options.includeHidden); });
    }

    // perform a load on the target only if dataType is not provided
    if (!options.dataType && options.target) {
        var oldSuccess = options.success || function(){};
        callbacks.push(function(data) {
            var fn = options.replaceTarget ? 'replaceWith' : 'html';
            $(options.target)[fn](data).each(oldSuccess, arguments);
        });
    }
    else if (options.success) {
        callbacks.push(options.success);
    }

    options.success = function(data, status, xhr) { // jQuery 1.4+ passes xhr as 3rd arg
        var context = options.context || this ;    // jQuery 1.4+ supports scope context
        for (var i=0, max=callbacks.length; i < max; i++) {
            callbacks[i].apply(context, [data, status, xhr || $form, $form]);
        }
    };

    if (options.error) {
        var oldError = options.error;
        options.error = function(xhr, status, error) {
            var context = options.context || this;
            oldError.apply(context, [xhr, status, error, $form]);
        };
    }

     if (options.complete) {
        var oldComplete = options.complete;
        options.complete = function(xhr, status) {
            var context = options.context || this;
            oldComplete.apply(context, [xhr, status, $form]);
        };
    }

    // are there files to upload?

    // [value] (issue #113), also see comment:
    // https://github.com/malsup/form/commit/588306aedba1de01388032d5f42a60159eea9228#commitcomment-2180219
    var fileInputs = $('input[type=file]:enabled', this).filter(function() { return $(this).val() !== ''; });

    var hasFileInputs = fileInputs.length > 0;
    var mp = 'multipart/form-data';
    var multipart = ($form.attr('enctype') == mp || $form.attr('encoding') == mp);

    var fileAPI = feature.fileapi && feature.formdata;
    log("fileAPI :" + fileAPI);
    var shouldUseFrame = (hasFileInputs || multipart) && !fileAPI;

    var jqxhr;

    // options.iframe allows user to force iframe mode
    // 06-NOV-09: now defaulting to iframe mode if file input is detected
    if (options.iframe !== false && (options.iframe || shouldUseFrame)) {
        // hack to fix Safari hang (thanks to Tim Molendijk for this)
        // see:  http://groups.google.com/group/jquery-dev/browse_thread/thread/36395b7ab510dd5d
        if (options.closeKeepAlive) {
            $.get(options.closeKeepAlive, function() {
                jqxhr = fileUploadIframe(a);
            });
        }
        else {
            jqxhr = fileUploadIframe(a);
        }
    }
    else if ((hasFileInputs || multipart) && fileAPI) {
        jqxhr = fileUploadXhr(a);
    }
    else {
        jqxhr = $.ajax(options);
    }

    $form.removeData('jqxhr').data('jqxhr', jqxhr);

    // clear element array
    for (var k=0; k < elements.length; k++)
        elements[k] = null;

    // fire 'notify' event
    this.trigger('form-submit-notify', [this, options]);
    return this;

    // utility fn for deep serialization
    function deepSerialize(extraData){
        var serialized = $.param(extraData, options.traditional).split('&');
        var len = serialized.length;
        var result = [];
        var i, part;
        for (i=0; i < len; i++) {
            // #252; undo param space replacement
            serialized[i] = serialized[i].replace(/\+/g,' ');
            part = serialized[i].split('=');
            // #278; use array instead of object storage, favoring array serializations
            result.push([decodeURIComponent(part[0]), decodeURIComponent(part[1])]);
        }
        return result;
    }

     // XMLHttpRequest Level 2 file uploads (big hat tip to francois2metz)
    function fileUploadXhr(a) {
        var formdata = new FormData();

        for (var i=0; i < a.length; i++) {
            formdata.append(a[i].name, a[i].value);
        }

        if (options.extraData) {
            var serializedData = deepSerialize(options.extraData);
            for (i=0; i < serializedData.length; i++)
                if (serializedData[i])
                    formdata.append(serializedData[i][0], serializedData[i][1]);
        }

        options.data = null;

        var s = $.extend(true, {}, $.ajaxSettings, options, {
            contentType: false,
            processData: false,
            cache: false,
            type: method || 'POST'
        });

        if (options.uploadProgress) {
            // workaround because jqXHR does not expose upload property
            s.xhr = function() {
                var xhr = $.ajaxSettings.xhr();
                if (xhr.upload) {
                    xhr.upload.addEventListener('progress', function(event) {
                        var percent = 0;
                        var position = event.loaded || event.position; /*event.position is deprecated*/
                        var total = event.total;
                        if (event.lengthComputable) {
                            percent = Math.ceil(position / total * 100);
                        }
                        options.uploadProgress(event, position, total, percent);
                    }, false);
                }
                return xhr;
            };
        }

        s.data = null;
            var beforeSend = s.beforeSend;
            s.beforeSend = function(xhr, o) {
                o.data = formdata;
                if(beforeSend)
                    beforeSend.call(this, xhr, o);
        };
        return $.ajax(s);
    }

    // private function for handling file uploads (hat tip to YAHOO!)
    function fileUploadIframe(a) {
        var form = $form[0], el, i, s, g, id, $io, io, xhr, sub, n, timedOut, timeoutHandle;
        var deferred = $.Deferred();

        // #341
        deferred.abort = function(status) {
            xhr.abort(status);
        };

        if (a) {
            // ensure that every serialized input is still enabled
            for (i=0; i < elements.length; i++) {
                el = $(elements[i]);
                if ( hasProp )
                    el.prop('disabled', false);
                else
                    el.removeAttr('disabled');
            }
        }

        s = $.extend(true, {}, $.ajaxSettings, options);
        s.context = s.context || s;
        id = 'jqFormIO' + (new Date().getTime());
        if (s.iframeTarget) {
            $io = $(s.iframeTarget);
            n = $io.attr2('name');
            if (!n)
                 $io.attr2('name', id);
            else
                id = n;
        }
        else {
            $io = $('<iframe name="' + id + '" src="'+ s.iframeSrc +'" />');
            $io.css({ position: 'absolute', top: '-1000px', left: '-1000px' });
        }
        io = $io[0];


        xhr = { // mock object
            aborted: 0,
            responseText: null,
            responseXML: null,
            status: 0,
            statusText: 'n/a',
            getAllResponseHeaders: function() {},
            getResponseHeader: function() {},
            setRequestHeader: function() {},
            abort: function(status) {
                var e = (status === 'timeout' ? 'timeout' : 'aborted');
                log('aborting upload... ' + e);
                this.aborted = 1;

                try { // #214, #257
                    if (io.contentWindow.document.execCommand) {
                        io.contentWindow.document.execCommand('Stop');
                    }
                }
                catch(ignore) {}

                $io.attr('src', s.iframeSrc); // abort op in progress
                xhr.error = e;
                if (s.error)
                    s.error.call(s.context, xhr, e, status);
                if (g)
                    $.event.trigger("ajaxError", [xhr, s, e]);
                if (s.complete)
                    s.complete.call(s.context, xhr, e);
            }
        };

        g = s.global;
        // trigger ajax global events so that activity/block indicators work like normal
        if (g && 0 === $.active++) {
            $.event.trigger("ajaxStart");
        }
        if (g) {
            $.event.trigger("ajaxSend", [xhr, s]);
        }

        if (s.beforeSend && s.beforeSend.call(s.context, xhr, s) === false) {
            if (s.global) {
                $.active--;
            }
            deferred.reject();
            return deferred;
        }
        if (xhr.aborted) {
            deferred.reject();
            return deferred;
        }

        // add submitting element to data if we know it
        sub = form.clk;
        if (sub) {
            n = sub.name;
            if (n && !sub.disabled) {
                s.extraData = s.extraData || {};
                s.extraData[n] = sub.value;
                if (sub.type == "image") {
                    s.extraData[n+'.x'] = form.clk_x;
                    s.extraData[n+'.y'] = form.clk_y;
                }
            }
        }

        var CLIENT_TIMEOUT_ABORT = 1;
        var SERVER_ABORT = 2;
                
        function getDoc(frame) {
            /* it looks like contentWindow or contentDocument do not
             * carry the protocol property in ie8, when running under ssl
             * frame.document is the only valid response document, since
             * the protocol is know but not on the other two objects. strange?
             * "Same origin policy" http://en.wikipedia.org/wiki/Same_origin_policy
             */
            
            var doc = null;
            
            // IE8 cascading access check
            try {
                if (frame.contentWindow) {
                    doc = frame.contentWindow.document;
                }
            } catch(err) {
                // IE8 access denied under ssl & missing protocol
                log('cannot get iframe.contentWindow document: ' + err);
            }

            if (doc) { // successful getting content
                return doc;
            }

            try { // simply checking may throw in ie8 under ssl or mismatched protocol
                doc = frame.contentDocument ? frame.contentDocument : frame.document;
            } catch(err) {
                // last attempt
                log('cannot get iframe.contentDocument: ' + err);
                doc = frame.document;
            }
            return doc;
        }

        // Rails CSRF hack (thanks to Yvan Barthelemy)
        var csrf_token = $('meta[name=csrf-token]').attr('content');
        var csrf_param = $('meta[name=csrf-param]').attr('content');
        if (csrf_param && csrf_token) {
            s.extraData = s.extraData || {};
            s.extraData[csrf_param] = csrf_token;
        }

        // take a breath so that pending repaints get some cpu time before the upload starts
        function doSubmit() {
            // make sure form attrs are set
            var t = $form.attr2('target'), a = $form.attr2('action');

            // update form attrs in IE friendly way
            form.setAttribute('target',id);
            if (!method || /post/i.test(method) ) {
                form.setAttribute('method', 'POST');
            }
            if (a != s.url) {
                form.setAttribute('action', s.url);
            }

            // ie borks in some cases when setting encoding
            if (! s.skipEncodingOverride && (!method || /post/i.test(method))) {
                $form.attr({
                    encoding: 'multipart/form-data',
                    enctype:  'multipart/form-data'
                });
            }

            // support timout
            if (s.timeout) {
                timeoutHandle = setTimeout(function() { timedOut = true; cb(CLIENT_TIMEOUT_ABORT); }, s.timeout);
            }

            // look for server aborts
            function checkState() {
                try {
                    var state = getDoc(io).readyState;
                    log('state = ' + state);
                    if (state && state.toLowerCase() == 'uninitialized')
                        setTimeout(checkState,50);
                }
                catch(e) {
                    log('Server abort: ' , e, ' (', e.name, ')');
                    cb(SERVER_ABORT);
                    if (timeoutHandle)
                        clearTimeout(timeoutHandle);
                    timeoutHandle = undefined;
                }
            }

            // add "extra" data to form if provided in options
            var extraInputs = [];
            try {
                if (s.extraData) {
                    for (var n in s.extraData) {
                        if (s.extraData.hasOwnProperty(n)) {
                           // if using the $.param format that allows for multiple values with the same name
                           if($.isPlainObject(s.extraData[n]) && s.extraData[n].hasOwnProperty('name') && s.extraData[n].hasOwnProperty('value')) {
                               extraInputs.push(
                               $('<input type="hidden" name="'+s.extraData[n].name+'">').val(s.extraData[n].value)
                                   .appendTo(form)[0]);
                           } else {
                               extraInputs.push(
                               $('<input type="hidden" name="'+n+'">').val(s.extraData[n])
                                   .appendTo(form)[0]);
                           }
                        }
                    }
                }

                if (!s.iframeTarget) {
                    // add iframe to doc and submit the form
                    $io.appendTo('body');
                }
                if (io.attachEvent)
                    io.attachEvent('onload', cb);
                else
                    io.addEventListener('load', cb, false);
                setTimeout(checkState,15);

                try {
                    form.submit();
                } catch(err) {
                    // just in case form has element with name/id of 'submit'
                    var submitFn = document.createElement('form').submit;
                    submitFn.apply(form);
                }
            }
            finally {
                // reset attrs and remove "extra" input elements
                form.setAttribute('action',a);
                if(t) {
                    form.setAttribute('target', t);
                } else {
                    $form.removeAttr('target');
                }
                $(extraInputs).remove();
            }
        }

        if (s.forceSync) {
            doSubmit();
        }
        else {
            setTimeout(doSubmit, 10); // this lets dom updates render
        }

        var data, doc, domCheckCount = 50, callbackProcessed;

        function cb(e) {
            if (xhr.aborted || callbackProcessed) {
                return;
            }
            
            doc = getDoc(io);
            if(!doc) {
                log('cannot access response document');
                e = SERVER_ABORT;
            }
            if (e === CLIENT_TIMEOUT_ABORT && xhr) {
                xhr.abort('timeout');
                deferred.reject(xhr, 'timeout');
                return;
            }
            else if (e == SERVER_ABORT && xhr) {
                xhr.abort('server abort');
                deferred.reject(xhr, 'error', 'server abort');
                return;
            }

            if (!doc || doc.location.href == s.iframeSrc) {
                // response not received yet
                if (!timedOut)
                    return;
            }
            if (io.detachEvent)
                io.detachEvent('onload', cb);
            else
                io.removeEventListener('load', cb, false);

            var status = 'success', errMsg;
            try {
                if (timedOut) {
                    throw 'timeout';
                }

                var isXml = s.dataType == 'xml' || doc.XMLDocument || $.isXMLDoc(doc);
                log('isXml='+isXml);
                if (!isXml && window.opera && (doc.body === null || !doc.body.innerHTML)) {
                    if (--domCheckCount) {
                        // in some browsers (Opera) the iframe DOM is not always traversable when
                        // the onload callback fires, so we loop a bit to accommodate
                        log('requeing onLoad callback, DOM not available');
                        setTimeout(cb, 250);
                        return;
                    }
                    // let this fall through because server response could be an empty document
                    //log('Could not access iframe DOM after mutiple tries.');
                    //throw 'DOMException: not available';
                }

                //log('response detected');
                var docRoot = doc.body ? doc.body : doc.documentElement;
                xhr.responseText = docRoot ? docRoot.innerHTML : null;
                xhr.responseXML = doc.XMLDocument ? doc.XMLDocument : doc;
                if (isXml)
                    s.dataType = 'xml';
                xhr.getResponseHeader = function(header){
                    var headers = {'content-type': s.dataType};
                    return headers[header.toLowerCase()];
                };
                // support for XHR 'status' & 'statusText' emulation :
                if (docRoot) {
                    xhr.status = Number( docRoot.getAttribute('status') ) || xhr.status;
                    xhr.statusText = docRoot.getAttribute('statusText') || xhr.statusText;
                }

                var dt = (s.dataType || '').toLowerCase();
                var scr = /(json|script|text)/.test(dt);
                if (scr || s.textarea) {
                    // see if user embedded response in textarea
                    var ta = doc.getElementsByTagName('textarea')[0];
                    if (ta) {
                        xhr.responseText = ta.value;
                        // support for XHR 'status' & 'statusText' emulation :
                        xhr.status = Number( ta.getAttribute('status') ) || xhr.status;
                        xhr.statusText = ta.getAttribute('statusText') || xhr.statusText;
                    }
                    else if (scr) {
                        // account for browsers injecting pre around json response
                        var pre = doc.getElementsByTagName('pre')[0];
                        var b = doc.getElementsByTagName('body')[0];
                        if (pre) {
                            xhr.responseText = pre.textContent ? pre.textContent : pre.innerText;
                        }
                        else if (b) {
                            xhr.responseText = b.textContent ? b.textContent : b.innerText;
                        }
                    }
                }
                else if (dt == 'xml' && !xhr.responseXML && xhr.responseText) {
                    xhr.responseXML = toXml(xhr.responseText);
                }

                try {
                    data = httpData(xhr, dt, s);
                }
                catch (err) {
                    status = 'parsererror';
                    xhr.error = errMsg = (err || status);
                }
            }
            catch (err) {
                log('error caught: ',err);
                status = 'error';
                xhr.error = errMsg = (err || status);
            }

            if (xhr.aborted) {
                log('upload aborted');
                status = null;
            }

            if (xhr.status) { // we've set xhr.status
                status = (xhr.status >= 200 && xhr.status < 300 || xhr.status === 304) ? 'success' : 'error';
            }

            // ordering of these callbacks/triggers is odd, but that's how $.ajax does it
            if (status === 'success') {
                if (s.success)
                    s.success.call(s.context, data, 'success', xhr);
                deferred.resolve(xhr.responseText, 'success', xhr);
                if (g)
                    $.event.trigger("ajaxSuccess", [xhr, s]);
            }
            else if (status) {
                if (errMsg === undefined)
                    errMsg = xhr.statusText;
                if (s.error)
                    s.error.call(s.context, xhr, status, errMsg);
                deferred.reject(xhr, 'error', errMsg);
                if (g)
                    $.event.trigger("ajaxError", [xhr, s, errMsg]);
            }

            if (g)
                $.event.trigger("ajaxComplete", [xhr, s]);

            if (g && ! --$.active) {
                $.event.trigger("ajaxStop");
            }

            if (s.complete)
                s.complete.call(s.context, xhr, status);

            callbackProcessed = true;
            if (s.timeout)
                clearTimeout(timeoutHandle);

            // clean up
            setTimeout(function() {
                if (!s.iframeTarget)
                    $io.remove();
                else  //adding else to clean up existing iframe response.
                    $io.attr('src', s.iframeSrc);
                xhr.responseXML = null;
            }, 100);
        }

        var toXml = $.parseXML || function(s, doc) { // use parseXML if available (jQuery 1.5+)
            if (window.ActiveXObject) {
                doc = new ActiveXObject('Microsoft.XMLDOM');
                doc.async = 'false';
                doc.loadXML(s);
            }
            else {
                doc = (new DOMParser()).parseFromString(s, 'text/xml');
            }
            return (doc && doc.documentElement && doc.documentElement.nodeName != 'parsererror') ? doc : null;
        };
        var parseJSON = $.parseJSON || function(s) {
            /*jslint evil:true */
            return window['eval']('(' + s + ')');
        };

        var httpData = function( xhr, type, s ) { // mostly lifted from jq1.4.4

            var ct = xhr.getResponseHeader('content-type') || '',
                xml = type === 'xml' || !type && ct.indexOf('xml') >= 0,
                data = xml ? xhr.responseXML : xhr.responseText;

            if (xml && data.documentElement.nodeName === 'parsererror') {
                if ($.error)
                    $.error('parsererror');
            }
            if (s && s.dataFilter) {
                data = s.dataFilter(data, type);
            }
            if (typeof data === 'string') {
                if (type === 'json' || !type && ct.indexOf('json') >= 0) {
                    data = parseJSON(data);
                } else if (type === "script" || !type && ct.indexOf("javascript") >= 0) {
                    $.globalEval(data);
                }
            }
            return data;
        };

        return deferred;
    }
};

/**
 * ajaxForm() provides a mechanism for fully automating form submission.
 *
 * The advantages of using this method instead of ajaxSubmit() are:
 *
 * 1: This method will include coordinates for <input type="image" /> elements (if the element
 *    is used to submit the form).
 * 2. This method will include the submit element's name/value data (for the element that was
 *    used to submit the form).
 * 3. This method binds the submit() method to the form for you.
 *
 * The options argument for ajaxForm works exactly as it does for ajaxSubmit.  ajaxForm merely
 * passes the options argument along after properly binding events for submit elements and
 * the form itself.
 */
$.fn.ajaxForm = function(options) {
    options = options || {};
    options.delegation = options.delegation && $.isFunction($.fn.on);

    // in jQuery 1.3+ we can fix mistakes with the ready state
    if (!options.delegation && this.length === 0) {
        var o = { s: this.selector, c: this.context };
        if (!$.isReady && o.s) {
            log('DOM not ready, queuing ajaxForm');
            $(function() {
                $(o.s,o.c).ajaxForm(options);
            });
            return this;
        }
        // is your DOM ready?  http://docs.jquery.com/Tutorials:Introducing_$(document).ready()
        log('terminating; zero elements found by selector' + ($.isReady ? '' : ' (DOM not ready)'));
        return this;
    }

    if ( options.delegation ) {
        $(document)
            .off('submit.form-plugin', this.selector, doAjaxSubmit)
            .off('click.form-plugin', this.selector, captureSubmittingElement)
            .on('submit.form-plugin', this.selector, options, doAjaxSubmit)
            .on('click.form-plugin', this.selector, options, captureSubmittingElement);
        return this;
    }

    return this.ajaxFormUnbind()
        .bind('submit.form-plugin', options, doAjaxSubmit)
        .bind('click.form-plugin', options, captureSubmittingElement);
};

// private event handlers
function doAjaxSubmit(e) {
    /*jshint validthis:true */
    var options = e.data;
    if (!e.isDefaultPrevented()) { // if event has been canceled, don't proceed
        e.preventDefault();
        $(e.target).ajaxSubmit(options); // #365
    }
}

function captureSubmittingElement(e) {
    /*jshint validthis:true */
    var target = e.target;
    var $el = $(target);
    if (!($el.is("[type=submit],[type=image]"))) {
        // is this a child element of the submit el?  (ex: a span within a button)
        var t = $el.closest('[type=submit]');
        if (t.length === 0) {
            return;
        }
        target = t[0];
    }
    var form = this;
    form.clk = target;
    if (target.type == 'image') {
        if (e.offsetX !== undefined) {
            form.clk_x = e.offsetX;
            form.clk_y = e.offsetY;
        } else if (typeof $.fn.offset == 'function') {
            var offset = $el.offset();
            form.clk_x = e.pageX - offset.left;
            form.clk_y = e.pageY - offset.top;
        } else {
            form.clk_x = e.pageX - target.offsetLeft;
            form.clk_y = e.pageY - target.offsetTop;
        }
    }
    // clear form vars
    setTimeout(function() { form.clk = form.clk_x = form.clk_y = null; }, 100);
}


// ajaxFormUnbind unbinds the event handlers that were bound by ajaxForm
$.fn.ajaxFormUnbind = function() {
    return this.unbind('submit.form-plugin click.form-plugin');
};

/**
 * formToArray() gathers form element data into an array of objects that can
 * be passed to any of the following ajax functions: $.get, $.post, or load.
 * Each object in the array has both a 'name' and 'value' property.  An example of
 * an array for a simple login form might be:
 *
 * [ { name: 'username', value: 'jresig' }, { name: 'password', value: 'secret' } ]
 *
 * It is this array that is passed to pre-submit callback functions provided to the
 * ajaxSubmit() and ajaxForm() methods.
 */
$.fn.formToArray = function(semantic, elements) {
    var a = [];
    if (this.length === 0) {
        return a;
    }

    var form = this[0];
    var els = semantic ? form.getElementsByTagName('*') : form.elements;
    if (!els) {
        return a;
    }

    var i,j,n,v,el,max,jmax;
    for(i=0, max=els.length; i < max; i++) {
        el = els[i];
        n = el.name;
        if (!n || el.disabled) {
            continue;
        }

        if (semantic && form.clk && el.type == "image") {
            // handle image inputs on the fly when semantic == true
            if(form.clk == el) {
                a.push({name: n, value: $(el).val(), type: el.type });
                a.push({name: n+'.x', value: form.clk_x}, {name: n+'.y', value: form.clk_y});
            }
            continue;
        }

        v = $.fieldValue(el, true);
        if (v && v.constructor == Array) {
            if (elements)
                elements.push(el);
            for(j=0, jmax=v.length; j < jmax; j++) {
                a.push({name: n, value: v[j]});
            }
        }
        else if (feature.fileapi && el.type == 'file') {
            if (elements)
                elements.push(el);
            var files = el.files;
            if (files.length) {
                for (j=0; j < files.length; j++) {
                    a.push({name: n, value: files[j], type: el.type});
                }
            }
            else {
                // #180
                a.push({ name: n, value: '', type: el.type });
            }
        }
        else if (v !== null && typeof v != 'undefined') {
            if (elements)
                elements.push(el);
            a.push({name: n, value: v, type: el.type, required: el.required});
        }
    }

    if (!semantic && form.clk) {
        // input type=='image' are not found in elements array! handle it here
        var $input = $(form.clk), input = $input[0];
        n = input.name;
        if (n && !input.disabled && input.type == 'image') {
            a.push({name: n, value: $input.val()});
            a.push({name: n+'.x', value: form.clk_x}, {name: n+'.y', value: form.clk_y});
        }
    }
    return a;
};

/**
 * Serializes form data into a 'submittable' string. This method will return a string
 * in the format: name1=value1&amp;name2=value2
 */
$.fn.formSerialize = function(semantic) {
    //hand off to jQuery.param for proper encoding
    return $.param(this.formToArray(semantic));
};

/**
 * Serializes all field elements in the jQuery object into a query string.
 * This method will return a string in the format: name1=value1&amp;name2=value2
 */
$.fn.fieldSerialize = function(successful) {
    var a = [];
    this.each(function() {
        var n = this.name;
        if (!n) {
            return;
        }
        var v = $.fieldValue(this, successful);
        if (v && v.constructor == Array) {
            for (var i=0,max=v.length; i < max; i++) {
                a.push({name: n, value: v[i]});
            }
        }
        else if (v !== null && typeof v != 'undefined') {
            a.push({name: this.name, value: v});
        }
    });
    //hand off to jQuery.param for proper encoding
    return $.param(a);
};

/**
 * Returns the value(s) of the element in the matched set.  For example, consider the following form:
 *
 *  <form><fieldset>
 *      <input name="A" type="text" />
 *      <input name="A" type="text" />
 *      <input name="B" type="checkbox" value="B1" />
 *      <input name="B" type="checkbox" value="B2"/>
 *      <input name="C" type="radio" value="C1" />
 *      <input name="C" type="radio" value="C2" />
 *  </fieldset></form>
 *
 *  var v = $('input[type=text]').fieldValue();
 *  // if no values are entered into the text inputs
 *  v == ['','']
 *  // if values entered into the text inputs are 'foo' and 'bar'
 *  v == ['foo','bar']
 *
 *  var v = $('input[type=checkbox]').fieldValue();
 *  // if neither checkbox is checked
 *  v === undefined
 *  // if both checkboxes are checked
 *  v == ['B1', 'B2']
 *
 *  var v = $('input[type=radio]').fieldValue();
 *  // if neither radio is checked
 *  v === undefined
 *  // if first radio is checked
 *  v == ['C1']
 *
 * The successful argument controls whether or not the field element must be 'successful'
 * (per http://www.w3.org/TR/html4/interact/forms.html#successful-controls).
 * The default value of the successful argument is true.  If this value is false the value(s)
 * for each element is returned.
 *
 * Note: This method *always* returns an array.  If no valid value can be determined the
 *    array will be empty, otherwise it will contain one or more values.
 */
$.fn.fieldValue = function(successful) {
    for (var val=[], i=0, max=this.length; i < max; i++) {
        var el = this[i];
        var v = $.fieldValue(el, successful);
        if (v === null || typeof v == 'undefined' || (v.constructor == Array && !v.length)) {
            continue;
        }
        if (v.constructor == Array)
            $.merge(val, v);
        else
            val.push(v);
    }
    return val;
};

/**
 * Returns the value of the field element.
 */
$.fieldValue = function(el, successful) {
    var n = el.name, t = el.type, tag = el.tagName.toLowerCase();
    if (successful === undefined) {
        successful = true;
    }

    if (successful && (!n || el.disabled || t == 'reset' || t == 'button' ||
        (t == 'checkbox' || t == 'radio') && !el.checked ||
        (t == 'submit' || t == 'image') && el.form && el.form.clk != el ||
        tag == 'select' && el.selectedIndex == -1)) {
            return null;
    }

    if (tag == 'select') {
        var index = el.selectedIndex;
        if (index < 0) {
            return null;
        }
        var a = [], ops = el.options;
        var one = (t == 'select-one');
        var max = (one ? index+1 : ops.length);
        for(var i=(one ? index : 0); i < max; i++) {
            var op = ops[i];
            if (op.selected) {
                var v = op.value;
                if (!v) { // extra pain for IE...
                    v = (op.attributes && op.attributes['value'] && !(op.attributes['value'].specified)) ? op.text : op.value;
                }
                if (one) {
                    return v;
                }
                a.push(v);
            }
        }
        return a;
    }
    return $(el).val();
};

/**
 * Clears the form data.  Takes the following actions on the form's input fields:
 *  - input text fields will have their 'value' property set to the empty string
 *  - select elements will have their 'selectedIndex' property set to -1
 *  - checkbox and radio inputs will have their 'checked' property set to false
 *  - inputs of type submit, button, reset, and hidden will *not* be effected
 *  - button elements will *not* be effected
 */
$.fn.clearForm = function(includeHidden) {
    return this.each(function() {
        $('input,select,textarea', this).clearFields(includeHidden);
    });
};

/**
 * Clears the selected form elements.
 */
$.fn.clearFields = $.fn.clearInputs = function(includeHidden) {
    var re = /^(?:color|date|datetime|email|month|number|password|range|search|tel|text|time|url|week)$/i; // 'hidden' is not in this list
    return this.each(function() {
        var t = this.type, tag = this.tagName.toLowerCase();
        if (re.test(t) || tag == 'textarea') {
            this.value = '';
        }
        else if (t == 'checkbox' || t == 'radio') {
            this.checked = false;
        }
        else if (tag == 'select') {
            this.selectedIndex = -1;
        }
		else if (t == "file") {
			if (/MSIE/.test(navigator.userAgent)) {
				$(this).replaceWith($(this).clone(true));
			} else {
				$(this).val('');
			}
		}
        else if (includeHidden) {
            // includeHidden can be the value true, or it can be a selector string
            // indicating a special test; for example:
            //  $('#myForm').clearForm('.special:hidden')
            // the above would clean hidden inputs that have the class of 'special'
            if ( (includeHidden === true && /hidden/.test(t)) ||
                 (typeof includeHidden == 'string' && $(this).is(includeHidden)) )
                this.value = '';
        }
    });
};

/**
 * Resets the form data.  Causes all form elements to be reset to their original value.
 */
$.fn.resetForm = function() {
    return this.each(function() {
        // guard against an input with the name of 'reset'
        // note that IE reports the reset function as an 'object'
        if (typeof this.reset == 'function' || (typeof this.reset == 'object' && !this.reset.nodeType)) {
            this.reset();
        }
    });
};

/**
 * Enables or disables any matching elements.
 */
$.fn.enable = function(b) {
    if (b === undefined) {
        b = true;
    }
    return this.each(function() {
        this.disabled = !b;
    });
};

/**
 * Checks/unchecks any matching checkboxes or radio buttons and
 * selects/deselects and matching option elements.
 */
$.fn.selected = function(select) {
    if (select === undefined) {
        select = true;
    }
    return this.each(function() {
        var t = this.type;
        if (t == 'checkbox' || t == 'radio') {
            this.checked = select;
        }
        else if (this.tagName.toLowerCase() == 'option') {
            var $sel = $(this).parent('select');
            if (select && $sel[0] && $sel[0].type == 'select-one') {
                // deselect all other options
                $sel.find('option').selected(false);
            }
            this.selected = select;
        }
    });
};

// expose debug var
$.fn.ajaxSubmit.debug = false;

// helper fn for console logging
function log() {
    if (!$.fn.ajaxSubmit.debug)
        return;
    var msg = '[jquery.form] ' + Array.prototype.join.call(arguments,'');
    if (window.console && window.console.log) {
        window.console.log(msg);
    }
    else if (window.opera && window.opera.postError) {
        window.opera.postError(msg);
    }
}

})( (typeof(jQuery) != 'undefined') ? jQuery : window.Zepto );




 
 
 LEx.form = function(){
 	
 }
 /**
  * 给所有匹配元素集合赋值，跟据元素的propertyName从entity中取值
  * @param containerId
  *		容器Id
  * @param {} entity
  *     传递过来的java实体Bean对应的js对象
  * @param {} [attrName]     
  *     可选参数；
  *     元素的属性名称（例如id，name,tag等）；
  *     默认为tag属性；
  *     元素的该属性值要和entity中的变量名对应
  */
 LEx.form.set = function(containerId,entity, attrName){
 	if(attrName == null) {   //判断是否传递了第二个参数
 		attrName = "tag";   //如果没有默认为name属性
 	}
 	$("#"+containerId +" ["+attrName+"]").each(function(index, element) {
 		var tObj = $(this);
 		//获取元素attrName属性的属性值
 		var propertyVal = tObj.attr(attrName);
 		//如果实体对象entity中包含名称为propertyVal的变量
 		if(entity[propertyVal] != undefined) {
 			//获取实体对象entity名称为propertyVal的变量的值
 			var enValue = entity[propertyVal];

 			//如果是时间类型就转换成字符串 hxk
 			if(LEx.isNotNull(enValue.time)){
 				enValue = LEx.util.Format.formatDate(enValue);
 			}
 			
 			if(tObj.is("span")){
 				tObj.html(enValue); //设置元素的值
 			}
 			else if(tObj.is("div")){
 				tObj.html(enValue); //设置元素的值
 			}else if(tObj.is("p")){
 				tObj.html(enValue);
 			}
 			else{
 				//如果元素类型是radio或者是checkbox
 				if("radio" == tObj.attr("type")){
 					if(enValue == tObj.val()){
 						tObj.attr("checked", true);
 					}
 					else{
 						tObj.attr("checked", false);
 					}
 				}else if("checkbox" == tObj.attr("type")) {
 					//如果元素值等于enValue
 					enValue =","+enValue+",";
 					if(enValue.indexOf(tObj.val())>-1) {
 						//设置元素被选中
 						tObj.attr("checked", true);
 					}else{
 						tObj.attr("checked", false);
 					}
 				}
 				else {//其他类型的元素（input：text，hidden，password；textarea等）
 					tObj.val(enValue); //设置元素的值
 				}
 			}
 		}
     });
 };

 LEx.form.get = function(containerId,attrName){
 	if(attrName == null) {   //判断是否传递了第二个参数
 		attrName = "tag";   //如果没有默认为name属性
 	}
 	var objRet = {};
 	$("#"+containerId +" ["+attrName+"]").each(function(index, element) {
 		var tObj = $(this);
 		//获取元素attrName属性的属性值
 		var propertyVal = tObj.attr(attrName);
 		if(tObj.is("span")){
 			objRet[propertyVal] = tObj.html();
 		}
 		else{
 			//如果元素类型是radio或者是checkbox
 			if("radio" == tObj.attr("type")){
 				if(tObj.attr("checked") == true || tObj.attr("checked") == "checked"){
 					objRet[propertyVal] = tObj.val();
 				}
 			}
 			else if("checkbox" == tObj.attr("type")) {
 				
 				if(tObj.attr("checked") == true || tObj.attr("checked") == "checked"){
 					var v = tObj.val();
 					if(objRet[propertyVal] == undefined){
 						objRet[propertyVal] = v;
 					}
 					else{
 						var oldValue = objRet[propertyVal];
 						oldValue = ","+oldValue+",";
 						if(oldValue.indexOf(tObj.val())<0){
 							objRet[propertyVal]= objRet[propertyVal]+","+v;	
 						}
 					}
 				}
 			}else {//其他类型的元素（input：text，hidden，password；textarea等）
 				objRet[propertyVal] = tObj.val();
 			}
 		}
     });
 	return objRet;
 };


 /*
  * Copyright (c) 2009 Mustafa OZCAN (http://www.mustafaozcan.net) Dual licensed
  * under the MIT (http://www.opensource.org/licenses/mit-license.php) and GPL
  * (http://www.opensource.org/licenses/gpl-license.php) licenses. Version: 1.0.2
  * Requires: jquery.1.3+
  */
 LEx.form.fix = function(table,options) {
 	var settings = jQuery.extend({
 		headerrowsize : 1,
 		highlightrow : false,
 		highlightclass : "highlight"
 	}, options);
 	if(table.substr(0,1)!="#" || table.substr(0,1)!=".")
 		table = "#"+table;
 	$(table).each(function(i) {
 				var $tbl = $(this);
 				var $tblhfixed = $tbl.find("tr:lt(" + settings.headerrowsize
 						+ ")");
 				var headerelement = "th";
 				if ($tblhfixed.find(headerelement).length == 0)
 					headerelement = "td";
 				if ($tblhfixed.find(headerelement).length > 0) {
 					$tblhfixed.find(headerelement).each(function() {
 						$(this).css("width", $(this).width());
 					});
 					var $clonedTable = $tbl.clone().empty();
 					var tblwidth = GetTblWidth($tbl);
 					$clonedTable.attr("id", "fixedtableheader" + i).css({
 						"position" : "fixed",
 						"top" : "0",
 						"left" : $tbl.offset().left
 					}).append($tblhfixed.clone()).width(tblwidth).hide()
 							.appendTo($("body"));
 					if (settings.highlightrow)
 						$("tr:gt(" + (settings.headerrowsize - 1) + ")", $tbl)
 								.hover(
 										function() {
 											$(this).addClass(
 													settings.highlightclass);
 										},
 										function() {
 											$(this).removeClass(
 													settings.highlightclass);
 										});
 					$(window)
 							.scroll(
 									function() {
 										if (jQuery.browser.msie
 												&& jQuery.browser.version == "6.0")
 											$clonedTable.css({
 												"position" : "absolute",
 												"top" : $(window).scrollTop(),
 												"left" : $tbl.offset().left
 											});
 										else
 											$clonedTable.css({
 												"position" : "fixed",
 												"top" : "0",
 												"left" : $tbl.offset().left
 														- $(window)
 																.scrollLeft()
 											});
 										var sctop = $(window).scrollTop();
 										var elmtop = $tblhfixed.offset().top;
 										if (sctop > elmtop
 												&& sctop <= (elmtop
 														+ $tbl.height() - $tblhfixed
 														.height()))
 											$clonedTable.show();
 										else
 											$clonedTable.hide();
 									});
 					$(window).resize(
 							function() {
 								if ($clonedTable.outerWidth() != $tbl
 										.outerWidth()) {
 									$tblhfixed.find(headerelement).each(
 											function(index) {
 												var w = $(this).width();
 												$(this).css("width", w);
 												$clonedTable
 														.find(headerelement)
 														.eq(index).css("width",
 																w);
 											});
 									$clonedTable.width($tbl.outerWidth());
 								}
 								$clonedTable.css("left", $tbl.offset().left);
 							});
 				}
 			});
 	function GetTblWidth($tbl) {
 		var tblwidth = $tbl.outerWidth();
 		return tblwidth;
 	}
 };
	plugin.add('mergeCell', {
			path: LEx.webPath+'public/scripts/form/jquery.mergeCell.js',
			type: 'js',
			charset: 'utf-8'
		});
 //合并单元格
 LEx.form.mergeCell = function(containerId,params){
 	plugin.ready('mergeCell',function() {
 		$('#'+containerId).mergeCell(params); 
 	});
 }; 

 //table排序
 LEx.form.tableSorter = function(containerId){
 	plugin.ready('tableSorter',function() {
 		$("#"+containerId).tablesorter();
 	});
 }; 

 /*
 	初始化表单，进行验证操作
 */
 LEx.form.init = function(control){
 	var xOffset = -20; // x distance from mouse
     var yOffset = 20; // y distance from mouse  
 	if(control == null){
 		control = "body";
 	}
 	//input action
 	$(control+" [reg],[fun]:not([reg]),[tip]").hover(
 		function(e) {
 			if($(this).attr('tip') != undefined){
 				var top = (e.pageY + yOffset);
 				var left = (e.pageX + xOffset);
 				$('body').append( '<p id="vtip"><img id="vtipArrow" src="'+LEx.webPath+'public/images/arrow.png"/>' + $(this).attr('tip') + '</p>' );
 				$('p#vtip').css("top", top+"px").css("left", left+"px");
 				//$('p#vtip').bgiframe();
 			}
 		},
 		function() {
 			if($(this).attr('tip') != undefined){
 				$("p#vtip").remove();
 			}
 		}
 	).mousemove(
 		function(e) {
 			if($(this).attr('tip') != undefined){
 				var top = (e.pageY + yOffset);
 				var left = (e.pageX + xOffset);
 				$("p#vtip").css("top", top+"px").css("left", left+"px");
 			}
 		}
 	).unbind('blur').bind('blur',function(){//LEx.form.init()调用多次时，会出现重复bind，所以先unbind edit by zx 2014-07-15
 		if($(this).attr("reg") != undefined){
 			validate($(this));
 		}else if($(this).attr("fun") != undefined){
 			fun_validate($(this));
 		}
 	});
 }
 /*验证操作*/
 LEx.form.validate = function(control){
 	if(control == null){
 		control = "body";
 	}
 	var isSubmit = true;
 	$(control +" [reg],"+control +" [fun]:not([reg]),"+control +" [tip]").each(function(){
 		
 		if($(this).attr("reg") == undefined){
 			if(!fun_validate($(this))){
 				isSubmit = false;
 			}
 		}else{
 			if(!validate($(this))){
 				isSubmit = false;
 			}
 		}
 	});
 	return isSubmit;
 }
 function validate(obj){
 	var reg = new RegExp(obj.attr("reg"));
 	//清除前后的空格
 	var objValue = $.trim(obj.val());
 	//var objValue = $.trim(obj.attr("value"));
 	if($(obj).get(0).tagName == 'INPUT' && $(obj).attr('type') == 'radio'){
 		var radio = $('input[tag='+$(obj).attr('tag')+']:checked');
 		if(radio.length > 0){
 			objValue=$(radio).val().trim();
 		}else{
 			objValue="";
 		}
 	}
    if($(obj).get(0).tagName == 'INPUT' && $(obj).attr('type') == 'hidden'){
        var radioOjb = $(obj).parent().find('.radio_span');
        if(!reg.test(objValue)){
            // $.each(radioOjb,function(){
                 change_error_style(radioOjb,"add");
                 change_tip(radioOjb,null,"remove");
                radioOjb.addClass('input_radio_validation-failed');
            // });
            return false;
        }else{
            return true;
        }
    }
 	if(!reg.test(objValue)){
 		change_error_style(obj,"add");
 		change_tip(obj,null,"remove");
 		return false;
 	}else{
 		if(obj.attr("fun") == undefined){
 			change_error_style(obj,"remove");
 			change_tip(obj,null,"remove");
 			return true;
 		}else{
 			return fun_validate(obj);
 		}
 	}
 }

 function fun_validate(obj){
 	
 	var fun = obj.attr("fun");
 	
 	var re= "";
 	try{
 		var s = eval(fun);
 		re = s.call(s,obj);
 	}
 	catch(e){
 		re ="请求的验证函数错误。";
 	}
 	re = re.replace(/(^\s*)|(\s*$)/g, "");
 	if(re == 'success'){
 		change_error_style(obj,"remove");
 		change_tip(obj,null,"remove");
 		return true;
 	}else{
 		change_error_style(obj,"add");
 		change_tip(obj,re,"add");
 		return false;
 	}
 }

 function change_tip(obj,msg,action_type){
 	
 	if(obj.attr("tip") == undefined){//初始化判断TIP是否为空
 		obj.attr("is_tip_null","yes");
 	}
 	if(action_type == "add"){
 		if(obj.attr("is_tip_null") == "yes"){
 			obj.attr("tip",msg);
 		}else{
 			if(msg != null){
 				if(obj.attr("tip_bak") == undefined){
 					obj.attr("tip_bak",obj.attr("tip"));
 				}
 				obj.attr("tip",msg);
 			}
 		}
 	}else{
 		if(obj.attr("is_tip_null") == "yes"){
 			obj.removeAttr("tip");
 			obj.removeAttr("tip_bak");
 		}else{
 			obj.attr("tip",obj.attr("tip_bak"));
 			obj.removeAttr("tip_bak");
 		}
 	}
 }

 function change_error_style(obj,action_type){
 	if(action_type == "add"){
 		obj.addClass("input_validation-failed");
 	}else{
 		obj.removeClass("input_validation-failed");
 	}
 }

 $.fn.validate_callback = function(msg,action_type,options){
 	this.each(function(){
 		if(action_type == "failed"){
 			change_error_style($(this),"add");
 			change_tip($(this),msg,"add");
 		}else{
 			change_error_style($(this),"remove");
 			change_tip($(this),null,"remove");
 		}
 	});
 };

 /*验证特殊字符*/
 function regclick(obj){
    	var reg=/[@#\$%\^&\*\_\[\]\\|￥?？]+/gi ;
     	if(reg.test(obj.val())){
        	alert("输入的内容中含有特殊字符！");
            return "输入的内容不可以含有特殊字符";
        }
 	return "success";
 }

 LEx.form.checkComplete = function(control){
 	if(control == null){
 		control == "body";
 	}
 	var isSubmit = true;
 	$(control +" [reg],"+control +" [fun]:not([reg]),"+control +" [tip]").each(function(){
 		
 		if($(this).attr("reg") == undefined){
 			if(!fun_validate_complete($(this))){
 				isSubmit = false;
 			}
 		}else{
 			if(!validate_complete($(this))){
 				isSubmit = false;
 			}
 		}
 	});
 	return isSubmit;
 }

 function validate_complete(obj){
 	var reg = new RegExp(obj.attr("reg"));
// 	var objValue = obj.attr("value").trim();
 	var objValue = $.trim(obj.attr("value"));
 	if(!reg.test(objValue)){
 		return false;
 	}else{
 		if(obj.attr("fun") == undefined){
 			return true;
 		}else{
 			return fun_validate_complete(obj);
 		}
 	}
 }

 function fun_validate_complete(obj){
 	var fun = obj.attr("fun");
 	var re= "";
 	try{
 		var s = eval(fun);
 		re = s.call(s,obj);
 	}
 	catch(e){
 		re ="请求的验证函数错误。";
 	}
 	re = re.replace(/(^\s*)|(\s*$)/g, "");
 	if(re == 'success'){
 		return true;
 	}else{
 		return false;
 	}
 }




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

