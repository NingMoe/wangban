    var oldTop=0,oldLeft=0;
	var oldEventX = 0,oldEventY = 0;
	var ShadeArea;
	var WinArea;
    var canmove=false;
    
    var hover='#000000',normal='#000000';
    
    function BeginDrag(obj){
        if(event.button==1)
        {
            WinArea = obj.parentNode;
            ShadeArea = WinArea.nextSibling;
            
            obj.setCapture();

            oldEventX = event.clientX;
            oldEventY = event.clientY;
            
            oldLeft = parseInt(WinArea.style.left);
            oldTop = parseInt(WinArea.style.top);
            
            ShadeArea.style.width = WinArea.offsetWidth;
            ShadeArea.style.height = WinArea.offsetHeight;
            
            ShadeArea.style.display = "";
            ShadeArea.style.left = oldLeft +5;
            ShadeArea.style.top = oldTop + 5;
            ShadeArea.style.backgroundColor = normal;
            ShadeArea.style.filter = "alpha(opacity=40)";
            canmove = true;
        }
    }
    function EndDrag(obj){
        
        if(canmove)
        {
            ShadeArea.style.display = "none";
            ShadeArea.style.left = oldLeft;
            ShadeArea.style.top = oldTop;
             
            obj.releaseCapture();
            canmove = false;
        }
    }
    function Draging(obj){
        if(canmove){
        
            WinArea.style.left = oldLeft + event.clientX - oldEventX;
            WinArea.style.top = oldTop + event.clientY - oldEventY;
            
            ShadeArea.style.left = parseInt(WinArea.style.left) + 5;
            ShadeArea.style.top = parseInt(WinArea.style.top) + 5;
           
        }
    }
    
  
  (function(){
        SelectsFactory = {
            get_Instance:function(){
                if(Selects.instance == null){
                    return new Selects();
                }
                else{
                    return Selects.instance;
                }
            }
        };
        var Selects = function(){
            this.BuildInput = function(select){
                var textbox = document.createElement("INPUT");
                if(select.options != null  && select.selectedIndex >=0 ){
                    textbox.value = select.options[select.selectedIndex].text;
                }
                textbox.style.width = select.offsetWidth;
                textbox.display = 'none';
                var className = select.className;
                if(className!=null){
                    className = className.toLowerCase();
                    if(className == 'select_norrow'){
                        textbox.className = 'input_narrow';
                    }
                    else{
                        textbox.className = 'input';
                    }
                }
                else{
                    textbox.className = 'input';
                }
                select.insertAdjacentElement("beforeBegin",textbox);
            }      ; 
            this.HiddenSelect =function(){
                var arrSelect = document.getElementsByTagName("SELECT");
                for(var i=0;i<arrSelect.length;i++){
                    var select = arrSelect[i]; 
                    if(select.previousSibling == null){
                       this.BuildInput(select); 
                    }
                    select.style.display = "none";
                    try
                    {
                        select.previousSibling.style.display = "";
                    }catch(ex){}
                }
            };
            
            this.ShowSelect = function(){
                var arrSelect = document.getElementsByTagName("SELECT");
                for(var i=0;i<arrSelect.length;i++){
                    var select = arrSelect[i];
                    if(select.previousSibling == null){
                       this.BuildInput(select); 
                    }
                    select.style.display = "";
                    try
                    {
                        select.previousSibling.style.display = "none";
                    }catch(ex){}
                }
            };
            Selects.instance = this;
        };
    })();
    
    
    
 (function(){
        ShadeFactory = {
            get_Instance:function(backgroundColor,alpha,zindex){
                if(Shade.instance == null){
                    return new Shade(backgroundColor,alpha,zindex);
                }
                else{
                    if(backgroundColor != null){
                        Shade.instance.ShadeDiv.style.backgroundColor = backgroundColor;
                    }
                    else{
                        Shade.instance.ShadeDiv.style.backgroundColor = "#FFFFFF";
                    }
                    if(alpha != null){
                        Shade.instance.ShadeDiv.style.filter = "alpha(opacity="+alpha+")";
                    }
                    else{
                       Shade.instance.ShadeDiv.style.filter = "alpha(opacity=20)"; 
                    }
                    if(zindex != null){
                        Shade.instance.ShadeDiv.style.zIndex = zindex;
                    }
                    else{
                        Shade.instance.ShadeDiv.style.zIndex = 1;
                    }
                    return Shade.instance;
                }
            }
        };
        var Shade = function(backgroundColor,alpha,zindex){
            
            Shade.instance = this;
            this.referenceCount = 0; 
            this.ShadeDiv = document.createElement("DIV");
            if(backgroundColor != null){
                this.ShadeDiv.style.backgroundColor = backgroundColor;
            }
            else{
                this.ShadeDiv.style.backgroundColor = "#FFFFFF";
            }
            if(alpha != null){
                this.ShadeDiv.style.filter = "alpha(opacity="+alpha+")";
            }
            else{
               this.ShadeDiv.style.filter = "alpha(opacity=20)"; 
            }
            if(zindex != null){
                this.ShadeDiv.style.zIndex = zindex;
            }
            else{
                this.ShadeDiv.style.zIndex = 1;
            }
            this.ShadeDiv.style.display = "none";
            this.ShadeDiv.style.position = "absolute";
            document.body.insertAdjacentElement("afterBegin",this.ShadeDiv);
            
            this.selectsObj = SelectsFactory.get_Instance();
            this.Show = function(){
                this.referenceCount = this.referenceCount + 1 ;
                this.ShadeDiv.style.display="";
                this.ShadeDiv.style.left =  this.ShadeDiv.style.top = 0;
                this.ShadeDiv.style.width = document.documentElement.offsetWidth;
                this.ShadeDiv.style.height = document.documentElement.offsetHeight;
                if(isIE && version<7){
                    this.selectsObj.HiddenSelect();
                }
            };
            this.Hidden = function(){
                 this.referenceCount = this.referenceCount - 1 ;
                 if(this.referenceCount <= 0){
                     this.ShadeDiv.style.display="none";
                     this.ShadeDiv.style.left =  this.ShadeDiv.style.top = 0;
                     this.ShadeDiv.style.width = 0;
                     this.ShadeDiv.style.height = 0 ;
                     this.ShadeDiv.style.backgroundColor = "";
                     this.ShadeDiv.style.filter = "";
                     if(isIE && version<7){
                        this.selectsObj.ShowSelect();
                     } 
                 }
            };
             
       };
       
       
    })();
    
  var isConfirmDivBuilded = false;
    function BuildConfirmDiv(){
        var strConfirm = "";
        
        strConfirm += "<div id='ConfirmDiv' style='position:absolute;display:none;z-index:1002; display:none;'>";
        strConfirm +="  <div class='message_width' onmousedown='BeginDrag(this)' onmouseup='EndDrag(this)' onmousemove='Draging(this)'>";
        strConfirm +="    <table class='message_title'>";
        strConfirm +="      <tr>";
        strConfirm +="        <td><div class='message_div'>Please Confirm</div>";
        strConfirm +="          <div class='message_close'><a href='#'><img  id='confirmCloseButton'  style='cursor:pointer;' src='http://www.cnblogs.com/images/cnblogs_com/charles2008/Close.gif' alt='Close' title='Close' width='40' height='40' /></a></div></td>";
        strConfirm +="      </tr>";
        strConfirm +="    </table>";
        strConfirm +="  </div>";
        strConfirm +="  <div class='message_width'>";
        strConfirm +="    <table class='message_contain' cellpadding='0' cellspacing='0'>";
        strConfirm +="      <tr>";
        strConfirm +="        <td colspan='2' height='25'>&nbsp;</td>";
        strConfirm +="      </tr>";
        strConfirm +="      <tr>";
        strConfirm +="        <td class='message_leftcontain'><img src='http://www.cnblogs.com/images/cnblogs_com/charles2008/query.gif' alt='Query' title='Query' width='40' height='40' /></td>";
        strConfirm +="        <td class='message_rightcontain'><div id='confirmMessageDiv'>";
        strConfirm +="                </div>";
        strConfirm +="      </tr>";
        strConfirm +="      <tr>";
        strConfirm +="        <td colspan='2' height='35'>&nbsp;</td>";
        strConfirm +="      </tr>";
        strConfirm +="      <tr>";
        strConfirm +="        <td colspan='2' class='message_button'><input  id='confirmOKButton'  class='button_short' type='button' name='btnOk' value='OK'/><input  id='confirmCancelButton'  class='button_short' type='button' name='btnOk' value='Cancel'/></td>";
        strConfirm +="      </tr>";
        strConfirm +="    </table>";
        strConfirm +="  </div>";
          
        strConfirm +="</div>";
        strConfirm +="<div style='position:absolute;  z-index:1001;  display:none;'></div>";
        
        document.body.insertAdjacentHTML("afterBegin",strConfirm);
        
        isConfirmDivBuilded = true; 
  }
  
  var isIE =  navigator.appName.indexOf("Internet Explorer")!= -1;
    var strVersion = navigator.appVersion.toString();
    var version =parseInt(strVersion.substring(strVersion.indexOf("MSIE")+4,strVersion.indexOf("MSIE")+8));
    
  window.confirm = function(message,okCallBack,cancelCallBack){
        
       
        if(isConfirmDivBuilded == false){
            BuildConfirmDiv();
        }
        
        
        var shadeObj = ShadeFactory.get_Instance("#FFFFFF",0,1000);
        shadeObj.Show();

        var confirmDiv = document.getElementById("ConfirmDiv");
        
        confirmDiv.style.display = "";
         
        confirmDiv.style.top = document.documentElement.scrollTop + ( document.documentElement.clientHeight - parseInt(confirmDiv.offsetHeight))/2 + "px";
        confirmDiv.style.left = document.documentElement.scrollLeft +(  document.documentElement.clientWidth - parseInt(confirmDiv.offsetWidth))/2 + "px";

        var confirmMessageDiv= document.getElementById("confirmMessageDiv");
        confirmMessageDiv.innerHTML = message;
        
        var confirmOKButton = document.getElementById("confirmOKButton");
        confirmOKButton.focus();
        
        var confirmCancelButton = document.getElementById("confirmCancelButton");
        
        var confirmCloseButton = document.getElementById("confirmCloseButton");
        confirmCloseButton.onclick = confirmCancelButton.onclick = function(){   
             shadeObj.Hidden();
             document.getElementById("ConfirmDiv").style.display="none";
             if(cancelCallBack != null && cancelCallBack != ""){
                eval(cancelCallBack);
             }
            
        };
        confirmOKButton.onclick =  function(){   
             shadeObj.Hidden();
             document.getElementById("ConfirmDiv").style.display="none";
              if(okCallBack != null && okCallBack != ""){
                eval(okCallBack);
             }
        };
        
        return false;                

    };