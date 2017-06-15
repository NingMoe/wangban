$(function() {
	//	Carousel1, Jumbotron
	$('#carousel').carouFredSel({
		
		responsive: true,
		width: '100%',
		items: 1,
		scroll: {
			timeoutDuration: 3000,
			duration: 3000,
			fx: 'crossfade',
			onBefore: function(data) {
 
				
				var currentSlide = $('.item.active', this),
					nextSlide = data.items.visible;
					

				currentSlide.removeClass( 'active' );
  
				data.items.old.add( data.items.visible ).find( '.caption' ).stop().fadeOut();					
			},
			
			onAfter: function(data) {
				
				data.items.visible.last().find( '.caption' ).stop().fadeIn();
			}
		},

		pagination: {
			container: '.slide-indicators',
			deviation: 1
		}
		
	});
	

	$('#carousel2').carouFredSel({
		items: 1,
		scroll: {
			fx: 'crossfade'
		},
		auto: {
			timeoutDuration: 4000,
			duration: 4000
		}
		
	});
	$('#carousel3').carouFredSel({
		items: 1,
		scroll: {
			fx: 'crossfade'
		},
		auto: {
			timeoutDuration: 2500,
			duration: 2500
		}
		
	})
	
});