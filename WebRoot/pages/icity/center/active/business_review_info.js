/**
 * 
 */
var editor;
function init(){
    var id = LEx.urldata.id;
    //LEx.alert(LEx.encode(id));
    if (LEx.isNotNull(id))
    {
    	var command = new LEx.Command("app.icity.project.ProjectQueryCmd");
		command.setParameter("SL.ID",id);
        var ret = command.execute("getPY");
        if (ret.state == 1) {
            if (ret.total > 0) {
                var d = ret.data[0];
                LEx.form.set("form", d);
                $("#evaluate_content").text(formatCheckFlag(d.EVALUATE_CONTENT));
                $("#star_level").text(formatCheckFlag(d.STAR_LEVEL));
                $("#creator_date").text(LEx.util.Format.formatDate(d.CREATOR_DATE));
            }
        } else {
            LEx.dialog({ title: "系统提示", content: ret.message, icon: 'error' ,lock:true});
        }
    }else{
    	window.location.href=LEx.webPath;
    } 
}
	
    function postData() {
    	//创建Command对象
        var command = new LEx.Command("app.icity.project.ProjectQueryCmd");
        var id = LEx.urldata.id; 
        if(isNotNull(id)){
        	//如果有主键则修改
        	command.setParameter("ID",id);
        	command.setParameter("EVALUATE_CONTENT",content);
        	command.setParameter("USERID", LEx.userInfo.uid.toString());
        	var ret = command.execute("update");
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
			return "★";
		}else if(val == '2'){
			return "★★";
		}else if(val == '3'){
			return "★★★";
		}else if(val =='4'){
			return "★★★★";
		}else if(val =='5'){
			return "★★★★★"
		}
	}
    
    
   
    