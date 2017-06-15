function init() {
	$(".btn-info").click(function(){
		var m_class = $(this).attr("data-target");
		if(m_class==".bs-example-modal-sug"){
		location.href = "{{cp}}icity/interactive/contactDeptList?m_class="+m_class;}
	});
}