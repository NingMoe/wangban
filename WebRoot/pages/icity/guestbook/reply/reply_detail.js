function init(){
		/*var c=  new LEx.Control.Calendar(function(){
			c.setup({id:"time"});
		});
		*/
	}
    $(document).ready(function(e) {
        //初始化时候，触发验证
        LEx.form.init();

        var id = LEx.urldata.id;
        if (LEx.isNotNull(id))
        {
            var command = new LEx.Command("app.icity.guestbook.ReplyCmd");
    		command.setParameter("ID@=",id);
            var ret = command.execute("getList");
            if (ret.state == 1) {
                if (ret.data.length > 0) {
                	var checks = LEx.urldata.checks;
                	var status = LEx.urldata.status;
                    if(status=="0"||(status=="1"&&checks=="2")){
                    	$('#deal_result').html('<textarea rows="4" style="width:558px;margin-right:0px;" tag="DEAL_RESULT" ></textarea>');
                    }else{
                    	$('#deal_result').html('<div style="width:570px;" tag="DEAL_RESULT" ></div>');
                    	$('#open').attr("disabled","disabled");
                    }
                    var d = ret.data[0];
                    LEx.form.set("form", d);
                    $("#time1").val(LEx.util.Format.formatDate(d.WRITE_DATE));
                }
            } else {
                LEx.dialog({ title: "系统提示", content: ret.message, icon: 'error' ,lock:true});
            }
        }
    });
	
      
    function postData1() {
    	//获取表单内容
        var obj = LEx.form.get("form");        
        if(obj["DEAL_RESULT"]==''||obj["DEAL_RESULT"]==null){
    		LEx.dialog({title:"系统提示",content:"回复内容不能为空！",icon: 'error',lock:true});
			return false;
    	}
        
    	//创建Command对象
        var command = new LEx.Command("app.icity.guestbook.ReplyCmd");
        
        for(var property in obj){ 
        	if(property=="OPEN"||property=="DEAL_RESULT"){
        		command.setParameter(property,obj[property]);
        	}
        } 
        
        var id = LEx.urldata.id;
        var replayer = LEx.userInfo.userName;
        if(isNotNull(id)){
        	//如果有主键则修改
        	command.setParameter("ID",id);
        	command.setParameter("CHECKS","1");
        	command.setParameter("REPLAYER",replayer);
          //command.setParameter("DEAL_DATE",new Date());
        	ret = command.execute("reply");
        }else{
        	 LEx.dialog({ title: "系统提示", content: "主键为空", icon: 'error' ,lock:true});
        }
        //结果处理
        if (ret.state == 1) {
            return true;
        } else {
            LEx.dialog({ title: "系统提示", content: ret.message, icon: 'error' ,lock:true});
        }
        return false;
    }
    
  
    function postData2() {
    	//获取表单内容
        var obj = LEx.form.get("form");
        
    	//创建Command对象
        var command = new LEx.Command("app.icity.guestbook.ReplyCmd");
        
        for(var property in obj){ 
        	if(property=="OPEN"||property=="DEAL_RESULT"){
        		command.setParameter(property,obj[property]);
        	}
        } 
        
        var id = LEx.urldata.id;
        if(isNotNull(id)){
        	//如果有主键则修改
        	command.setParameter("ID",id);
        	command.setParameter("CHECKS","2");
        	ret = command.execute("CheckLetter");
        }else{
        	 LEx.dialog({ title: "系统提示", content: "主键为空", icon: 'error' ,lock:true});
        }
        //结果处理
        if (ret.state == 1) {
            return true;
        } else {
            LEx.dialog({ title: "系统提示", content: ret.message, icon: 'error' ,lock:true});
        }
        return false;
    }

    
    function formatCheckFlag(val){
		if(val=="1"){
			return "审核通过";
		}else if(val == "2"){
			return "审核不通过";
		}else{
			return "未审核";
		}
	}
    