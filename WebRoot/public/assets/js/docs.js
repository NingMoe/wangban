$(function () {
    $('.nav-tabs > li > a').hover(function (e) {
        e.preventDefault()
        $(this).tab('show')
    })
});
$(function() {
    $(".dropdown-menu").on("click", "[data-stopPropagation]", function(e) {
        e.stopPropagation();
    });
});
