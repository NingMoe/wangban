var editor;
function init(){
	LEx.Control.editor("wiki_content",editorFun);
    var id = LEx.urldata.id;
    if (LEx.isNotNull(id))
    {
    	var command = new LEx.Command("app.icity.wiki.BusinessWikiCmd");
		command.setParameter("WT.ID",id);
        var ret = command.execute("getBusinessWiki");
        if (ret.state == 1) {
            if (ret.total > 0) {
            	//LEx.alert(LEx.encode(ret.data));
                var d = ret.data[0];
                LEx.form.set("form", d);
                $("#checks").text(formatCheckFlag(d.CHECKS));
                $("#time").text(LEx.util.Format.formatDate(d.CREATETIME));
            }
        } else {
            LEx.dialog({ title: "系统提示", content: ret.message, icon: 'error' ,lock:true});
        }
    }else{
    	window.location.href=LEx.webPath;
    } 
}
	
function editorFun(obj){
	editor = obj;
}

      
    function postData() {
    	//创建Command对象
        var command = new LEx.Command("app.icity.wiki.BusinessWikiCmd");
        var title = $('#wiki_title').val();
        var content = editor.html();
        if(!LEx.isNotNull(title)){
			LEx.alert("攻略标题不能为空！");
			return false;
		}
		if(!LEx.isNotNull(content)){
			LEx.alert("攻略内容不能为空！");
			return false;
		}
        var id = LEx.urldata.id; 
        if(isNotNull(id)){
        	//如果有主键则修改
        	command.setParameter("ID",id);
        	command.setParameter("NAME" , title);
        	command.setParameter("CONTENT",content);
        	command.setParameter("USERID", LEx.userInfo.uid.toString());
        	ret = command.execute("update");
        }else{
        	 LEx.dialog({ title: "系统提示", content: "主键为空", icon: 'error' ,lock:true});
        }
        //结果处理
        if (ret.state == 1) {
        	LEx.dialog({ title: "系统提示", content: "操作成功", icon: 'succeed' ,lock:true});
	  		return true;
        } else {
            LEx.dialog({ title: "系统提示", content: ret.message, icon: 'error' ,lock:true});
        }
        return false;
    }
    
    
    
    
    function formatCheckFlag(val){
		if(val== '1'){
			return "审核通过";
		}else if(val == '2'){
			return "审核不通过";
		}else{
			return "未审核";
		}
	}
    
    
   
    