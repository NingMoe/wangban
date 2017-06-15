function init() {
    onQuery(); // 执行默认的查询方法
}

function onQuery() {
    // 定义command对象
    var command = new LEx.Command("app.pmi.PortalCmd");
    command.setParameter("ALL_FLAG","1");
    var websiteId = LEx.urldata.websiteId;
    if(LEx.isNotNull(websiteId)){
        command.setParameter("WEBSITE_ID",websiteId);
    }
    var websiteType = LEx.urldata.websiteType;
    if(LEx.isNotNull(websiteType)) {
        command.setParameter("WEBSITE_TYPE", websiteType);
    }
    var ret = command.execute("getWebSiteMenu");
    if(!command.error){
        $('#_menu').html(""+LEx.processDOMTemplate('menuTemplate',ret));
    }else{
        LEx.dialog({
            title : "系统提示",
            content : command.error,
            icon : 'error',
            lock : true
        });
    }
}

function formatType(o){
    if(o.TYPE=="url"){
        return "链接地址";
    }else if(o.TYPE=="portal"){
        return "自定义页面";
    }
    return "未知";
}
function formatUrl(o){
    if(o.TYPE=="url"){
        var url = o.VALUE;
        if(url.indexOf("http://")==0){
            return url;
        }else{
            return LEx.webPath+url;
        }
    }else if(o.TYPE=="portal"){
        var portalId = o.VALUE;
        if(portalId.indexOf("/")==0){
            portalId = portalId.substr(1);
        }
        return LEx.webPath+"/portal/design/"+ portalId;
    }
    return "javascript:alert('未知类型，不支持链接操作。');"
}
function formatStatus(v){
    if(v == "1"){
        return "是";
    }
    return "否";
}
function cmd(obj){
    var s = $(obj).attr("class");
    if(s == "selectedOn"){
        $(obj).attr("class","selectedOff");
    }else{
        $(obj).attr("class","selectedOn");
    }
}

function onAdd(pid) {
    var websiteId = LEx.urldata.websiteId;
    if(!LEx.isNotNull(websiteId)){
        websiteId = "";
    }
    var url = LEx.webPath+'/pmi/config/menuInfo?pid=' + pid+"&websiteId="+websiteId;
    LEx.dialog({
        title : "添加菜单",
        url : url,
        lock : true,
        width:800,
        height:400,
        button:[
            {
                name: '保存',
                callback: function() {
                    var iframe = this.iframe.contentWindow;
                    if (!iframe.document.body) {
                        LEx.alert('内容还没加载完毕呢');
                        return false;
                    }
                    var result = iframe.postData();
                    if(result){
                        onQuery();
                        //提示添加成功
                        LEx.dialog.tips("菜单添加成功");
                    }else{
                        return false;
                    }
                },
                focus: true
            }]
    });



}
function onDelete(id,websiteId){
    if(confirm("您是否需要删除此操作？")){
        var cmd = new LEx.Command("app.pmi.PortalCmd");
        cmd.setParameter("ID",id);
        cmd.setParameter("WEBSITE_ID",websiteId);
        var ret = cmd.execute("deleteWebSiteMenu");
        if(ret.state ==1){
            LEx.dialog.tips("删除菜单成功。");
            onQuery();
        }else{

            LEx.dialog.tips("删除菜单失败，错误原因为:\n"+ret.message);
        }
    }
}