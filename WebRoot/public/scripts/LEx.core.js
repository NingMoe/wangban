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