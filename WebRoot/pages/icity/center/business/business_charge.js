function init(){

var id = LEx.urldata.id;
	if (LEx.isNotNull(id))
	{
		var command = new LEx.Command("app.icity.project.ProjectIndexCmd");
		command.setParameter("CODE",id);
	    var ret = command.execute("getBusinessCharge");
	    if (ret.state == 1) {
	        if (ret.data.length > 0) {
	        	var d = ret.data[0];
	            LEx.form.set("form", d);
	        }
	    } else {
	        LEx.dialog({ title: "系统提示", content: ret.message, icon: 'error' ,lock:true});
	    }
	} 
}
	

      
    
    
   
    