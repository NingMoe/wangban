
//定义系统全局变量
LEx.Const= {
    LOC_HASH_INFO:              "loc_hash_info",
    WINDOW_RESIZE:              "window_resize",
    SHOW_LOADING:               "show_loading",
    HIDE_LOADING:               "hide_loading",
    SHOW_DIALOG:                "show_dialog",
    SHOW_CONFIRM:               "show_confirm",
    LOAD_ERROR:                 "加载页面失败，可能页面不存在！",                           //加载内容出错
    AJAX_CACHE:                 false                                   //默认是否开启缓存
};

//核心库代码
LEx.Library = function($) {
    return {
        randomNum:function(min,max){
            var Range = max - min;
            var Rand = Math.random();
            return(min + Math.round(Rand * Range));
        },
        isNotNull:function(msg){
          if(msg == null || msg == undefined || msg =="undefined"){
              return false;
          }
          return true;
        }
    };
}(jQuery);

LEx.Sandbox = function($) {
    var result = function(coreObj, moduleId,containerObj) {
        this.coreObj = coreObj;
        this.moduleId = moduleId;
        var tid = '';
        if(typeof(containerObj) == "undefined"){
            this.container = $(document.body);
        }else{
            this.container = containerObj;
            tid = containerObj.attr("id");
            if(tid == null || tid == undefined || tid == "undefined" || tid == ''){
            tid = containerObj.attr("class");
        }
        }
        this.instanceId = this.moduleId+"_"+tid;
    };
    result.fn = result.prototype;
    result.fn.listen = function(eventNames, callback, module) {
        this.coreObj.listen(this.instanceId, eventNames, callback, module);
        return this
    };
    result.fn.notify = function(event) {
        event.instanceId = this.instanceId;
        this.coreObj.broadcast(event);
        return this;
    };

    result.fn.showLoading = function() {                //显示加载效果
        this.notify({
            type: LEx.Const.SHOW_LOADING,
            data: ""
        });
        return this;
    };
    result.fn.hideLoading = function() {                //隐藏加载效果
        this.notify({
            type:  LEx.Const.HIDE_LOADING,
            data: ""
        });
        return this;
    };
    result.fn.showDialog = function(data) {             //现实弹出框
        this.notify({
            type:  LEx.Const.SHOW_DIALOG,
            data: data
        });
    };
    result.fn.showConfirm = function(data) {            //现实选择框
        this.notify({
            type:  LEx.Const.SHOW_CONFIRM,
            data: data
        });
    };
    result.fn.stopEvent = function() {                  //取消事件向下触发
        if(window.event)
            window.event.returnValue = false;
        else
            event.preventDefault();//for firefox
    };
    result.fn.getHashcode = function() {                //获取url中的#内容
        return location.hash;
    };
    result.fn.setHashcode = function(hash) {            //设置url中的#内容
        location.hash = hash;
    };
    result.fn.find = function(iname) {                  //查找iname元素
        if(iname != null && iname.length>0){
            if(iname.indexOf(".")>-1){
            }else if(iname.indexOf("#")>-1){

            }else{
                iname = "*[iname='"+iname+"']";
            }
            return this.container.find(iname);
        }else{
            return this.container;
        }
    };
    return result;
}(jQuery);

LEx.Core = function($, library) {
    //默认是否开启ajax请求缓存
    jQuery.ajaxSetup({cache:LEx.Const.AJAX_CACHE});

    //模块（插件）信息
    var modules = {};
    //监听事件
    var listens = {};

    //触发一个事件
    function triggerEvent(obj) {
        var type = obj.type;
        var data = obj.data;
        var instanceId = obj.instanceId;
        var lisarr = listens[instanceId][type];
        if (lisarr) {
            for (var i = 0, j = lisarr.length; i < j; i++) {
                lisarr[i]["method"](data, type);
            }
        }
    };

    function hashChangeHandler() {
        var hash = location.hash;
        if (hash) {
            //获取网盘id
            var diskinfo = hash.split("|");
            if(diskinfo.length > 1) {
                datamap.diskinfo = diskinfo[1];
            } else {
                datamap.diskinfo = "";
            }

            var key = hash.split("/");
            switch (key[0]) {
                default:
                    break;
            }
        } else {
            triggerEvent({
                type: LEx.Const.LOC_HASH_INFO,
                data: "#disk"
            });
        }
    }
    return {
        currentModule:function(name){
            if(LEx.Library.isNotNull(name)){
                this._currentName = name;
            }
            if(!LEx.Library.isNotNull(this._currentName)){
                this._currentName = LEx.Library.randomNum(100000000,999999999)+"";
            }
            return this._currentName;
        },
        startApp: function() {                      //启动应用
            $(window).resize(function() {
                triggerEvent({
                    type: LEx.Const.WINDOW_RESIZE,
                    data: location.hash
                })
            });
            hashChangeHandler();
        },
        register: function(name,module) {          //注册一个模块，每个模块都必须定义一个唯一的name，建议使用《应用名称.模块名称.功能名称》
            modules[name] = {
                creator: module,
                instance: null
            }
        },
        start: function(name,containerObj,p) {                     //开始实例化一个模块
            
            if(typeof(modules[name]) == 'undefined'){
                modules[name] = {};
            }
            if(modules[name].instance == null){
                modules[name].instance = {};
            }
            var tbox = new LEx.Sandbox(this,name,containerObj);
            modules[name].instance[tbox.instanceId] = modules[name].creator(tbox);
            modules[name].instance[tbox.instanceId].init(p);
            return modules[name].instance[tbox.instanceId];
        },
        stop: function(name) {                      //开始释放一个模块实例
            var module = modules[name];
            if (module.instance) {
                module.instance.destroy();
                module.instance = null
            }
        },
        startAll: function() {                      //实例化所有模块
            for (var m in modules) {
                if (modules.hasOwnProperty(m)) {
                    this.start(m)
                }
            }
        },
        stopAll: function() {                       //开始释放所有实例
            for (var m in modules) {
                if (modules.hasOwnProperty(m)) {
                    this.stop(m)
                }
            }
        },
        listen: function(instanceId, eventNames, callback, module) {
            if (!$.isArray(eventNames)) {
                if(typeof(eventNames)=='object'){
                    var arr = [];
                    for(var p in eventNames){
                        if(typeof(eventNames[p])=="function"){
                        }else{
                            arr.push(eventNames[p]);
                        }
                    }
                    eventNames = arr;
                }else{
                    eventNames = [eventNames]
                }
            }
            if(listens[instanceId]== null){
                listens[instanceId] = {};
            }
            $.each(eventNames, function(i, name) {
                if (listens[instanceId][name]) {
                    listens[instanceId][name].push({
                        method: callback,
                        scope: module
                    })
                } else {
                    listens[instanceId][name] = [{
                        method: callback,
                        scope: module
                    }]
                }
            });
        },
        broadcast: function(event) {                //广播触发一个事件
            triggerEvent(event);
        }
    }
}(jQuery, LEx.Library);
(function($) {
    //加载一个模块，并展示到页面上
    $.fn.loadModule = function(url,p,fun) {
        //定义容器对象
        var _containerObj = $(this);
        //将url转换成name
        var name = url;
        name = name.replace(/\\/g,"").replace(/\//g,"").replace(/\./g,"").replace(/\_/g,"");
        //将url中内容存储在容器中
		var _fun = fun;
        $.ajax({
            url: url,
            type: 'get',
            success: function (responseText) {
                LEx.Core.currentModule(name);
                _containerObj.html(responseText);
                LEx.LUI.init($("#mainDiv"));
                var _Instance = LEx.Core.start(name,_containerObj,p);
                if(_fun != null){
                	var s = eval(_fun);
            		s.call(s,_Instance,true);
				}
            },
            error: function(XMLHttpRequest, textStatus, errorThrown) {
            	var s = eval(_fun);
            	s.call(s,false);
                alert(LEx.Const.LOAD_ERROR);
            }
        });
    }
})(jQuery);