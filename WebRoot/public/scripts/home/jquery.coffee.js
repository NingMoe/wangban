;(function($){
	$.extend( jQuery.easing ,{
		flyFastSlow: function (x, t, b, c, d) {
			return -c * ((t=t/d-1)*t*t*t  - 1) + b;
		}
	});
	
	$.fn.coffee = function(option){
		var $coffee = $(this);
		var opts = $.extend({},$.fn.coffee.defaults,option);
		var coffeeWidth = $coffee.width() + opts.coffeeHandleWidth  - 100 ;
		$coffee.append('<div class="coffee-steam-box" style="height:'+ opts.steamTopMax+'px; width:'+ (coffeeWidth) +'px; left:0px;top:-'+ opts.steamTopMax+'px;position:absolute;overflow:hidden;z-index:0;"></div>');
		var $coffeeSteamBox = $coffee.find('div.coffee-steam-box');
		var coffeeSteamBoxWidth = $coffeeSteamBox.width();
		
		setInterval(function(){
			steam();
		}, rand( opts.makeSteamInterval / 2 , opts.makeSteamInterval * 2 ) );
		wind();
		return $coffee ;
		
		function steam(){	
			coffeeSteamBoxWidth = $coffeeSteamBox.width();		
			var fontSize = rand( 8 , opts.steamMaxSize  ) ;
			var xPos = rand( coffeeSteamBoxWidth - coffeeWidth , coffeeSteamBoxWidth - opts.coffeeHandleWidth - fontSize );
			var rotate = rand(-5,5);
			var rotateStyle = ' -moz-transform:rotate('+rotate+'deg);-webkit-transform:rotate('+rotate+'deg);-o-transform:rotate('+rotate+'deg);transform:rotate('+rotate+'deg);';
			var color = '#'+ randoms(6 , '0123456789ABCDEF' );
			var steamsFontFamily = randoms( 1, opts.steamsFontFamily )
			var html = '<span class="coffee-steam" style="position:absolute;left:'+xPos+'px;top: '+opts.steamTopMax+'px;font-size:'+fontSize+'px;color:'+ color +';font-family:'+steamsFontFamily +';'+rotateStyle+';-webkit-text-size-adjust:none;display:block; ">'+ randoms( 1, opts.steams ) +'</span>';			
			var $fly = $(html);
			var left = rand( 0 , coffeeSteamBoxWidth - opts.coffeeHandleWidth - fontSize );
			if( left > xPos )
				left = rand( 0 , xPos );
			$fly.appendTo($coffeeSteamBox).animate({
				top		: rand( opts.steamTopMax / 2 , 0 ),
				left	: left  ,
				opacity	: 0
			} , rand( opts.steamFlyTime / 2 , opts.steamFlyTime * 1.2 ) , 'flyFastSlow' ,  function(){
				$fly.remove();
				$fly = null;			
			});
		};
		
		function wind(){
			var left = rand( - coffeeWidth + opts.coffeeHandleWidth , 0 );
			$coffeeSteamBox.animate({
				width : Math.abs(left) + coffeeWidth ,
				left	: left 
			} , rand( 1000 , 3000) , rand( 1 , ['flyFastSlow' ]),  function(){
				 setTimeout(function(){
					wind();	
				}, rand( 100 , 3000 ) ) ;
			});
		};
		
		function randoms( length , chars ) {
			length = length || 1 ;
			var hash = '';
			var maxNum = chars.length - 1;
			var num = 0;
			for( i = 0; i < length; i++ ) {
				num = rand( 0 , maxNum - 1  );
				hash += chars.slice( num , num + 1 );
			}
			return hash;
		};		
		function rand(mi,ma){   
			var range = ma - mi;
			var out = mi + Math.round( Math.random() * range) ;	
			return parseInt(out);
		};		
	};

	$.fn.coffee.defaults = {
		steams				: ['jQuery','HTML5','HTML6','CSS2','CSS3','JS','$.fn()','char','short','if','float','else','type','case','function','travel','return','array()','empty()','eval','C++','JAVA','PHP','JSP','.NET','while','this','$.find();','float','$.ajax()','addClass','width','height','Click','each','animate','cookie','bug','Design','Julying','$(this)','i++','Chrome','Firefox','Firebug','IE6','Guitar' ,'Music' ,'攻城师' ,'旅行' ,'王子墨','啤酒'], 
		steamsFontFamily	: ['宋体','黑体','微软雅黑'],
		steamFlyTime		: 8000 , /*Steam飞行的时间,单位 ms 。（决定steam飞行速度的快慢）*/
		makeSteamInterval	: 500 , /*制造Steam时间间隔,单位 ms.*/
		steamMaxSize		: 30 ,/*制造Steam时间间隔,单位 ms.*/
		steamTopMax			: 150,
		coffeeHandleWidth	: 200 
	};
	$.fn.coffee.version = '1.0.0';
})(jQuery);