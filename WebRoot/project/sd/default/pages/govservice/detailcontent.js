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
		$("#detailTime").html(ret.data.CT);
	}else{
		LEx.alert(ret.error);
	}
}
function resultprintArea(){
	 $("#printResult").printArea();
}