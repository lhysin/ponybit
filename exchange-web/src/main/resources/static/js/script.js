$(document).on("click", "[href='#']", function(){ return false; });

$(function(){

	// slider
	function slider($swiper, option){
		$swiper.each(function(){
			var $this = $(this);
			var swiper = new Swiper($this.get(0), option);
			$this.data('swiper', swiper);
			$this.data('swiper', swiper.update());
		});
	}

	slider($(".visual .swiper-container"), {
		loop: true,
		autoplay: {
			delay: 2500,
			disableOnInteraction: false,
		},
		speed: 1000
	});

	$('.count .circle').circleProgress({
		emptyFill: 'transparent',
		fill: {gradient: ['#ff656e', '#3670f5']}
	});



});