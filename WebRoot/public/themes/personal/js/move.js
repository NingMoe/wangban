// 弹窗
function popBox(){
	var oDiv = document.createElement('div');
	var oBg=document.createElement('div');
	var oContent=getByClass(document,'pop_warpbox')[0].cloneNode(true);
	var oClose=document.createElement('a');
	oDiv.className ='pop_box';
	oBg.style.width=oDiv.style.width=document.body.scrollWidth+'px';
	oBg.style.height=oDiv.style.height=document.body.scrollHeight+'px';
	oBg.className='bg';
	oClose.className='close';
	oClose.href='javascript:;';
	oClose.innerHTML='关闭';
	document.body.appendChild(oDiv);
	oDiv.appendChild(oBg);
	oDiv.appendChild(oContent);
	oContent.appendChild(oClose);
	oContent.style.left=(document.documentElement.clientWidth-oContent.offsetWidth)/2+'px';
	if(document.body.scrollTop){
		oContent.style.top=document.body.scrollTop+(document.documentElement.clientHeight+-oContent.offsetHeight)/2+'px';			
	}else{
		oContent.style.top=document.documentElement.scrollTop+(document.documentElement.clientHeight+-oContent.offsetHeight)/2+'px';
	}
	oClose.onclick=function(){			
		startMove(oDiv,{opacity:0},150,'linear',function(){
			document.body.removeChild(oDiv);
			
		})
		
	}
}

//向上滑动[滑动新闻]
function scrolNews(obj){
	var oObj=getByClass(document,obj)[0];
	if(oObj == null) return;
	var oScro_ul=oObj.getElementsByTagName('ul')[0];
	var aScro_li=oScro_ul.getElementsByTagName('li');
	var bBtn=true;
	var iGOHig=aScro_li[0].offsetHeight;
	timer=null;
	timer=setInterval(function(){toMove()},3000)
	oObj.onmouseover=function(){clearInterval(timer);}
	oObj.onmouseout=function(){timer=setInterval(function(){toMove()},3000)}
	function toMove(){
		var aNewLi=aScro_li[0].cloneNode(true);
		oScro_ul.appendChild(aNewLi);
		startMove(oScro_ul,{top:-iGOHig},200,'linear',function(){
			oScro_ul.removeChild(aScro_li[0]);
			oScro_ul.style.top=0;
		})		
	}
}


//TAB切换 面对对象
function TabSwich(name,n,click){
	var oTabObj=getByClass(document,name)[n];
	this.aTabBtn=oTabObj.getElementsByTagName('h4')[0].getElementsByTagName('a');
	this.aTabBox=getByClass(oTabObj,'tab_box');
	var i=0;	
	for(i=0;i<this.aTabBtn.length;i++){
		var _This=this;
		this.aTabBtn[i].index=i;
		if(click){
			this.aTabBtn[i].onclick=function(){
				_This.tab(this)
			}
		}else{
			this.aTabBtn[i].onmousemove=function(){
				_This.tab(this)
			}
		}
	}
};
TabSwich.prototype.tab=function(aBtn){
	for(var i=0;i<this.aTabBtn.length;i++)
	{
		this.aTabBtn[i].className='';
		this.aTabBox[i].style.display='none';
	}
	aBtn.className='active';
	this.aTabBox[aBtn.index].style.display='block';
}
// 滑动菜单并切换
function moveMenuTab(obj,num){
	var oObj=getByClass(document,obj)[num];
	if(oObj == null) return;
	var aTabTit=oObj.getElementsByTagName('ul')[0].getElementsByTagName('li');
	var oTabP=oObj.getElementsByTagName('p')[0];
	var oTabWarp=getByClass(oObj,'tab-wrap')[0];
	var oTabBox=getByClass(oObj,'tab_box');
	var iNow=0;
	
	
	for(var i=0; i<aTabTit.length; i++){
		aTabTit[i].index=i;
		aTabTit[i].onmousemove=function(){			
			if(iNow==this.index) return;
			iNow=this.index;
			for(var j=0; j<aTabTit.length; j++){
				aTabTit[j].className='';
				this.className='active';	
				oTabBox[j].style.display='none';
			}
			oTabBox[iNow].style.display='block';
			startMove(oTabP,{left:aTabTit[0].offsetWidth*iNow},200,'backBoth');
		}
	}
}

$(function(){
	scrolNews('roll_news')
	var aTabSwich=getByClass(document,'tab_switch');
	if(aTabSwich == null) return false;
	for(var i=0; i<aTabSwich.length; i++){
		new TabSwich('tab_switch',i,'click');
	}
	var aListmovebox=getByClass(document,'list_movebox');
	if(aListmovebox == null) return false;	
	for(var i=0; i<aListmovebox.length; i++){
		moveMenuTab('list_movebox',i);
	}
	
	//奇偶行
	$(".table_even").each(function(){
    	$(this).find("tr:odd").addClass("odd");
  	});
  	//table移入效果
  	$(".table_even tr").hover(function(){
  		$(this).addClass('active');
  	},function(){
  		$(this).removeClass('active');
  	})
	
})

