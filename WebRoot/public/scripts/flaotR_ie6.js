
// IE6 下 左侧浮动窗口
function floatRight(obj){
	var oObj=getByClass(document,obj)[0];
	if(oObj==null) return;
	
	if(document.body.offsetWidth<998+110){
		oObj.style.display='none';
	}else{
		var aSpan=oObj.getElementsByTagName('span');
		var aSpanWidth=aSpan[0].offsetWidth-80;
		var oGotop=getByClass(oObj,'ctrl-btn-5')[0];
		oGotop.style.visibility='hidden';
		var ioObjTop=oObj.offsetTop;
		oObj.style.top=ioObjTop+'px';
		for(var i=0; i<aSpan.length; i++){
			aSpan[i].onmouseover=function(){
				for(var j=0; i<aSpan.length; j++){
					aSpan[j].className='';
				}
				this.className='active';
				startMove(this,{right:80},200,'linear')
			}
			aSpan[i].onmouseout=function(){
				var _This=this;
				startMove(this,{right:-aSpanWidth},200,'linear',function(){
					this.className=''
					startMove(this,{right:-(aSpanWidth-45)},200,'linear')
				})
			}
		}
		window.onscroll=function(){
			var iTop=document.body.scrollTop || document.documentElement.scrollTop;
			if(iTop>200){
				oGotop.style.visibility='visible'
			}else if(iTop<=0){
				oGotop.style.visibility='hidden'
			}
			oObj.style.top=iTop+ioObjTop+"px";
		}
	}
}


