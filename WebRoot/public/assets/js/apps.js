
$(function(){
    $(".dropdown-hover").hover(
        function() {
            $('.dropdown-menu', this).stop( true, true ).fadeIn("fast");
            $(this).toggleClass('open');
//            $('mdicon', this).toggleClass("mdicon-expand-less mdicon-expand-more");                
        },function() {
            $('.dropdown-menu', this).stop( true, true ).fadeOut("fast");
            $(this).toggleClass('open');
//            $('mdicon', this).toggleClass("mdicon-expand-less mdicon-expand-more");                
        }
    );
});
$(function(){
	$('#js-list-group-l2 > .list-group-item').hover(function(){
		$(this).addClass('open').siblings().removeClass('open');
	});	
});
