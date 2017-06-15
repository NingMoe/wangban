$(function(){
	$(".business_tab").click(function(){
		$(this).parent().parent().find(".active").removeClass("active");
		$(this).parent().addClass("active");
		var m_folder = $(this).attr("folder");
		var m_tag = $(this).attr("tag");
		$("#center-main").load(LEx.webPath+"src?m=icity/center/"+m_folder+"/"+m_tag+".html", function() {
			contentHandler();window.scrollTo(0,200);
		});
	});	
	var tag = LEx.urldata.tag;
	var folder = LEx.urldata.folder;
	if(tag){//alert($("a[tag='"+tag+"']").attr("tag"));
		$("a[tag='"+tag+"']").parent().parent().find(".active").removeClass("active");
		$("a[tag='"+tag+"']").parent().addClass("active");
		$("#center-main").load(LEx.webPath+"src?m=icity/center/"+folder+"/"+tag+".html", function() {
			contentHandler();window.scrollTo(0,200);
		});
	} else{
		$("#center-main").load(LEx.webPath+"src?m=icity/center/business/business_list.html", function() {
			contentHandler();window.scrollTo(0,200);
		});
	}
});
BusinessDetail=function(id,itemId){
	$("#businessList").hide();
	$("#businessDetail").show();
	$("#businessDetail").load(LEx.webPath+"src?m=icity/center/business/business_detail.html&id="+id+"&type=0&sxid="+itemId, function() {});
};
BusinessAskinfo=function(id){
	$("#business_ask").hide();
	$("#businessAskinfo").show();
	$("#businessAskinfo").load(LEx.webPath+"src?m=icity/center/business/business_askinfo.html&id="+id, function() {});
};
BusinessComplaintinfo=function(id,ly){
	$("#business_complaint").hide();
	$("#businessComplaintinfo").show();
	$("#businessComplaintinfo").load(LEx.webPath+"src?m=icity/center/business/business_complaintinfo.html&id="+id+"&ly="+ly, function() {});
};
BusinessList=function(){
	$("#businessList").show();
	$("#businessDetail").hide();
};
AskinfoList=function(){
	$("#business_ask").show();
	$("#businessAskinfo").hide();
};
ComplaintinfoList=function(){
	$("#business_complaint").show();
	$("#businessComplaintinfo").hide();
};
BusinessDocumentDetail=function(id){
	$("#businessDocument").hide();
	$("#businessDocumentDetail").show();
	$("#businessDocumentDetail").load(LEx.webPath+"src?m=icity/center/active/business_documentinfo.html&id="+id, function() {});
};
BusinessDocument=function(){
	$("#businessDocument").show();
	$("#businessDocumentDetail").hide();
};