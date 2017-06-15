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