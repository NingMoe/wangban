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