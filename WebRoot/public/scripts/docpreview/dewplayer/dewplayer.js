var flashvars = {
			  mp3: docUrl
			};
var params = {
  wmode: "transparent"
};
var attributes = {
  id: "dewplayer"
};
swfobject.embedSWF(CONST.RESOURCE_URL+"/js/docpreview/dewplayer/dewplayer-vinyl-en.swf", "musicContent", "303", "113", "9.0.0", false, flashvars, params, attributes);