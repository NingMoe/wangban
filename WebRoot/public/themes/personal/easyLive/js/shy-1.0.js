$(function(){

    //大菜单加actvie
    $('#menu li').eq(3).addClass('active')

	//菜单移入只为当前元素加Active
	var oMenuiCurActive=null;
	$('.menu_box ul a').each(function(){
		if($(this).hasClass('active')){
			oMenuiCurActive=$(this)
		}		
	})
	$('.menu_box ul a').mouseenter(function(){
		$('.menu_box ul a').removeClass('active');
		$(this).addClass('active')
	})	
	$('.menu_box ul').mouseleave(function(){
		$('.menu_box ul a').removeClass('active');
		oMenuiCurActive.addClass('active')
	})

	if($('.lrmovebox_w850 .lrmovebox').length == 1){
        $('.ltor_btnbox').remove();
    }

    $('.hidshow_btn').each(function(){
        $(this).click(function(){
            var _This=$(this);
            if($(this).hasClass('active')){
                $(this).parent().next().show(200,function(){
                    _This.removeClass('active')
                })
            }else{
                $(this).addClass('active');
                $(this).parent().next().hide(200)
            }
            
        })
    })

    $('.ltor_movebox a').each(function(){
        if($(this).attr('href') == ''){
            $(this).attr({'href':'javascript:;','onclick':'popNotContentInfo()'})
        }
    })

    

    //置顶悬浮
    var IO=document.getElementById('float'),Y=IO,H=0,IE6;
    if(IO == null) return;
    IE6=window.ActiveXObject&&!window.XMLHttpRequest;
    while(Y){H+=Y.offsetTop;Y=Y.offsetParent};
    if(IE6)
    IO.style.cssText="position:absolute;top:expression(this.fix?(document"+
    ".documentElement.scrollTop-(this.javascript||"+H+")):0)";
    window.onscroll=function (){
    var d=document,s=Math.max(d.documentElement.scrollTop,document.body.scrollTop);
    if(s>H&&IO.fix||s<=H&&!IO.fix)return;
    if(!IE6)
    if(IO.style.position=IO.fix){
        IO.style.position='';
        $('.float_box li.goTop').css('display','none')
    }else{
        IO.style.position='fixed';
        $('.float_box li.goTop').css('display','block')
    }
    IO.fix=!IO.fix;
    };
    try{document.execCommand("BackgroundImageCache",false,true)}catch(e){};
    //]]>

})
function popNotContentInfo(){
    LEx.dialog.tips("非常抱歉！此栏目正在建设中...");
}


var iLmoveRboxNow=0;
function leftMove(obj){
    var iParentNode=$(obj).parent().parent().find('.lrmovebox_w850');
    var iBoxWidth=iParentNode.eq(0).outerWidth(true);
    var aLrMovebox=iParentNode.find('.lrmovebox');
    var lrLength=iParentNode.find('.lrmovebox').length;
    $(obj).next().css('display','block')
    if(iLmoveRboxNow == lrLength-2){
        $(obj).css('display','none')
    }
    aLrMovebox.eq(iLmoveRboxNow+1).css({'display':'block','left':iBoxWidth})
    aLrMovebox.eq(iLmoveRboxNow).animate({'left':-iBoxWidth},300,function(){
        $(this).css('display','none')
    })
    aLrMovebox.eq(iLmoveRboxNow+1).animate({'left':'0'},300);
    
    iLmoveRboxNow++;
}
function rightMove(obj){
    var iParentNode=$(obj).parent().parent().find('.lrmovebox_w850');
    var iBoxWidth=iParentNode.eq(0).outerWidth(true);
    var aLrMovebox=iParentNode.find('.lrmovebox');
    var lrLength=iParentNode.find('.lrmovebox').length;
    $(obj).prev().css('display','block')
    if(iLmoveRboxNow == 1){
        $(obj).css('display','none')
    }
    aLrMovebox.eq(iLmoveRboxNow-1).css({'display':'block','left':-iBoxWidth})
    aLrMovebox.eq(iLmoveRboxNow).animate({'left':iBoxWidth},300,function(){
        $(this).css('display','none')
    })
    aLrMovebox.eq(iLmoveRboxNow-1).animate({'left':'0'},300);
    iLmoveRboxNow--;
}

//移到当前内容
function goMoveContent(obj,num){
    var iTop = $('div[tag=listbox'+num+']').offset().top;
    $(obj).parent().parent().find('a').removeClass('active');
    $(obj).addClass('active');
    $("html,body").animate({scrollTop:iTop},1000,function(){
        if($('div[tag=listbox'+num+']').find('.hidshow_btn').hasClass('active')){
            $('div[tag=listbox'+num+']').find('.listinfo').show(400,function(){
                 $('div[tag=listbox'+num+']').find('.hidshow_btn').removeClass('active')
            })
        } 
    })

}
function gotoTop(){
    $("html,body").animate({scrollTop:'0'},1000)
}



