/**
 * 在线预览，flexpaper
 */
 (function($) {
	var flashContainer = $("#flash");
	
	if(docType == "ppt" || docType == "pptx") {
		flashContainer.css("height", 520);
	} else {
		var height = Math.min(Math.max($(window).height(), 500), 700);
		flashContainer.css("height", height);
		$(window).resize(function() {
			var height = Math.min(Math.max($(window).height(), 500), 700);
			flashContainer.css("height", height);
		});
	}
	
	var swfVersionStr = "10.0.0";
	var xiSwfUrlStr = LEx.webPath
			+ "/public/scripts/docpreview/playerProductInstall.swf";
	var flashvars = {
		SwfFile : escape(docUrl),
		Scale : 1.0,
		ZoomTransition : "easeOut",
		ZoomTime : 0.5,
		ZoomInterval : 0.1,
		StartAtPage :read_page,
		FitPageOnLoad : false,
		FitWidthOnLoad : true,
		PrintEnabled : false,
        TextSelectEnabled:false,
        ReadOnly:true,
		FullScreenAsMaxWindow : false,
		ProgressiveLoading : true,
		PrintToolsVisible : false,
		ViewModeToolsVisible : true,
		ZoomToolsVisible : true,
		FullScreenVisible : true,
		NavToolsVisible : true,
		CursorToolsVisible : true,
		SearchToolsVisible : true,
		SearchMatchAll:true,
		InitViewMode : 'Portrait',
		localeChain : "zh_CN"
	};

	var params = {};
	params.quality = "high";
	params.bgcolor = "#F2F2F2";
	params.allowscriptaccess = "always";
	params.allowfullscreen = "true";
	params.wmode = "window";
	var attributes = {};
	attributes.id = "FlexPaperViewer";
	attributes.name = "FlexPaperViewer";
	swfobject.embedSWF(LEx.webPath + "public/scripts/docpreview/DocViewerFlex.swf",
			"flashContent", "100%", "100%", swfVersionStr, xiSwfUrlStr,
			flashvars, params, attributes);
	swfobject.createCSS("#flashContent", "display:block;");
})(jQuery);
 
function FlexPaperComplete() {
	if($("#FlexPaperViewer")[0]) {
		$("#FlexPaperViewer")[0].flashAddEventListener("onExternalLinkClicked");
	}
}

function FlexPaperEvent(event) {
	switch(event.type) {
	case "onExternalLinkClicked":
		window.open(event.link, "_blank");
		break;
	}
}
