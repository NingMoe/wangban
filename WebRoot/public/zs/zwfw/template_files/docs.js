

$(function(){
	
    $(".js-textroll").each(function(idx){

        var config=$(this).attr("data-config");
        
            if(!config)return;
        
        var paused=false;
            $(this).mouseenter(function(){
                paused=true;
            });
            $(this).mouseleave(function(){
                paused=false;
            });
        
        var cobj=config.split(",");
            $(this).css({
                "height":cobj[0],
    //				"width":$textroll.innerWidth(),
                "overflow":"hidden",
                "position":"relative"
            });
        
        var $ul=$(this).children("ul");
            $ul.css({
                "position":"absolute",
                "top":0,
                "left":0
            });
        
        var myid=$(this).attr("data-sid");
            if(myid!=null)clearInterval(myid);
        
        var myid=setInterval(function(){
            if(paused)return;
            var $r=$ul.find("li:first");
            var offsety=$r.outerHeight();
            $ul.stop(true,true);
            $ul.animate({"top":"-="+offsety},"slow",function(){
                $ul.css("top",0);
                $r.appendTo($ul);
            });
        },cobj[1]);
        
        $(this).attr("data-sid",myid);
    });
});
