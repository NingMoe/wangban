$(function(){
	onQueryDetail(LEx.urldata.id);
	

});
function onQueryDetail(id){
	var cmd = new LEx.Command("app.icity.govservice.GovProjectCmd");	
	cmd.setParameter("id", id);
	var ret =  cmd.execute("getContentDetail");
	if(!ret.error){//document.write(LEx.encode(ret));
		$("#detailTitle").html(ret.data.NAME);
		$("#contentDetail").html(ret.data.CONTENT);
		$("#detailSource").html(ret.data.SOURCE);
		$("#detailTime").html(LEx.util.Format.formatDate(ret.data.CT,'yyyy-MM-dd'));
		for(var i = 0;i<ret.data.upfiles.length;i++){
			if(ret.data.upfiles[i].TYPE=="2"){
				if(i==0){
					$("#files").append("<b>附件：</b><br>");
				}
					var opera = "downloadagent4wp&path="+ret.data.upfiles[i].DOCID+"&name="+encodeURIComponent(ret.data.upfiles[i].YNAME); 
					var url = LEx.webPath + "bsp/uploadify?action="+opera;
					$("#files").append("<a class='text-primary' href='"+url+"'>"+ret.data.upfiles[i].YNAME+"</a><br>");
			}
		}
	}else{
		LEx.alert(ret.error);
	}
}
function resultprintArea(){
	 $("#printResult").printArea();
}