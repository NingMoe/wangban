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