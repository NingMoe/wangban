

function init(){
	LEx.form.init();
	//如果用户已登录，自动填写相关信息
    if(LEx.isNotNull(LEx.userInfo)){
    	//通过deptId区分一下前后台用户
    	if(!LEx.isNotNull(LEx.userInfo.deptId)){
    		if('{{ConfigInfo.AppId}}'=="chq"){
    			$("input[tag='USERNAME']").val(LEx.userInfo.userName);
    		}else{
    			$("input[tag='USERNAME']").val(LEx.userInfo.userName).addClass("reset-not").attr("disabled","disabled");
    		}
			$("input[tag='PHONE']").val(LEx.userInfo.mobile);
        }
    }		        
	$("button[op]").click(function(event) {
		var container=$(this).attr("container");
		var op=$(this).attr("op");
		var cat=$(this).attr("cat");//10-建议；11-纠错
		if(op=="submit"){
			if(LEx.form.validate(container)){
				submit(cat,container);
			}else{
				LEx.dialog.tips("请正确填写必填项",1.5);
			}
		}
		else{
			$(container+" input,text-area").each(function(){
			$(this).removeClass("input_validation-failed");
			if(!$(this).hasClass("reset-not")){
				$(this).val("");
				$("#CONTENT").val("");
				$("#content").val("");
			}
		});
		}
	});
	bindValid();
}

function submit(cat,container){	 
    var cmd_ins = new LEx.Command("app.icity.guestbook.WriteCmd");
    var obj=LEx.form.get(container.substr(1,container.length-1));
    $.each(obj,function(k,v){
    	cmd_ins.setParameter(k,v);
    });
    if(LEx.isNotNull(LEx.userInfo)&&!LEx.isNotNull(LEx.userInfo.deptId)){
 	   cmd_ins.setParameter("USER_ID",LEx.userInfo.userId);
	   }
    	cmd_ins.setParameter("TYPE",cat);
	      var ret = cmd_ins.execute("insert");
	      if(ret.error){
		         errorDialog("系统提示信息", ret.error);
		        //验证码输入错误则更换验证码
		       // changeimg();
		        return false;
	     }else{
		        var id = ret.data.id;
		        success(id,cat,container);
	} 
}

 //更换验证码
function changeimg(){
	  var myimg = document.getElementById("code"); 
	  now = new Date(); 
	  myimg.src="{{PageContext.ContextPath}}bsp/verifyCode?time="+new Date().getTime();
}  
//绑定验证码发送功能
function bindValid() {
	$(".verify_btn").unbind("click").bind("click", function() {
		var container=$(this).attr("container");
		var receive = $(container+" [tag=PHONE]").val();
		if (!LEx.isNotNull(receive)||$(container+" [tag=PHONE]").hasClass("input_validation-failed")) {
			$(container+" #PHONE").removeClass("input_validation-failed").addClass("input_validation-failed");
			errorDialog("系统提示", "手机号未通过系统校验");
		} 
		else {
			sendValid(container,receive);
		}
	});
}
function errorDialog(t, c) {
	LEx.dialog({
		title : t,
		content : c,
		icon : 'error',
		lock : true
	});
}
// 发送验证码后倒计时，为PHONE绑定发送短信，为EMAIL绑定发送邮件
function waitValid(container,time) {
	if (time > 0) {
		time--;
		$(container+" .verify_btn").html(time + "秒后可以重新发送");
		setTimeout(function() {
			waitValid(container,time);
		}, 1000);
	} else {
		bindValid();// 重新绑定发送短信验证码事件
		$(container+" .verify_btn").removeClass("btn-default").addClass("btn-info");
		$(container+" .verify_btn").html("点击获取短信验证码");
		return;
	}
}

//发送验证码
function sendValid(container,receive) {
	var cmd = new LEx.Command("app.icity.guestbook.SmsCmd");
	var ret = null;
		cmd.setParameter("PHONE", receive);
		ret = cmd.execute("getVerifyCode");
	if (ret.state == 1) {
		$(container+" .verify_btn").unbind("click").removeClass("btn-info").addClass("btn-default").css("cursor", "default");
		waitValid(container,60);// 重新发送短信倒数计时
		return true;
	} else {
		errorDialog("系统提示", ret.message);
		return false;
	}
}


function success(id,type,container){
	$(container).modal('hide');
	$(container+" input").each(function(){
		$(this).removeClass("input_validation-failed");
		$(this).val("");});
	$(".bs-example-modal-result").modal('show');
	$("span#id").html(id);
	var secs = 60;   
          for(var i=secs;i>=0;i--) { 
             window.setTimeout("doUpdate(" + i + ")", (secs-i) * 1000);   
          }
}
//页面倒数计时跳转
 function doUpdate(num){   
           $("#totalSecond").html(num);   
           if (num =='0'){
        	   $(".bs-example-modal-result").modal('hide');
         }  
}  

function redirect() {
    window.location="{{PageContext.ContextPath}}queryletter";
}
function hide() {
	$(".bs-example-modal-result").modal('hide');
}
