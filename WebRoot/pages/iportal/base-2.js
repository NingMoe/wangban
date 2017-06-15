var cname = LEx.urldata.CNAME;  //动态获取cname
var keyword=LEx.urldata.key;
//alert(keyword);
//var cid = "14357153068478211";

var contentLiTable = null;

function init() {
	if(keyword!=""&&keyword!=undefined){
	$('#cname').html("搜索结果");
	}else{
	$('#cname').html(cname);
	}
	if(cname=="县区"||cname=="乡镇"||cname=="社区"||cname=="功能区"){
	contentLiTable = new LEx.Control.Table("contentLi_s", "content_lia");
	}else{
		contentLiTable = new LEx.Control.Table("contentLi_s", "content_li");
	}
	contentLiTable.limit(9);
	onQuery();
}

function onQuery() {
	var cmd = new LEx.Command("app.icity.govservice.GovProjectCmd");
	if(keyword!=""&&keyword!=undefined){
		cmd.setParameter("name",keyword);
		cmd.setParameter("rid", '{{UserInfo.regionId}}');
	}else{
		if(cname=="县区"||cname=="乡镇"||cname=="社区"||cname=="功能区"){
			cmd.setParameter("channel.name@=",'动态要闻');
			cmd.setParameter("isDtyw",'1');
		}else{
		cmd.setParameter("channel.name@=", cname);
		}
	}
	if(cname=="县区"){
		cmd.setParameter("mod(content.rid,1000000)@=",0);
	    cmd.setParameter("mod(content.rid,100000000)@!=",0);
	    cmd.setParameter("content.rid@!=",'330901000000');
	    cmd.setParameter("content.rid not@in",'000901000000,000902000000,000903000000,000904000000,000905000000');
	}else  if(cname=="乡镇"){
		cmd.setParameter("mod(content.rid,10000)@=",0);
	    cmd.setParameter("mod(content.rid,1000000)@!=",0);
	}else if(cname=="社区"){
		cmd.setParameter("mod(content.rid,100)@=",0);
	    cmd.setParameter("mod(content.rid,10000)@!=",0);
	}else if(cname=="功能区"){
		 cmd.setParameter("content.rid@like",'00090');
			cmd.setParameter("mod(content.rid,1000000)@=",0);
	}else if(cname=="法律法规"||cname=="政策文件"||cname=="管理制度"){
		cmd.setParameter("content.rid@=", '{{UserInfo.regionId}}');
	    cmd.setParameter("content.rid@=","330901000000");
	}else if(keyword==""||keyword==undefined){
		
	    cmd.setParameter("content.rid@=", '{{UserInfo.regionId}}');
	}
    
    //默认只显示通过审核的数据
    cmd.setParameter("CHECKS@=", '1');
    //cmd.setParameter("sort"," ID DESC");
    cmd.setParameter("sort"," CTIME DESC");
	cmd.setParameter("start", contentLiTable.start());
	cmd.setParameter("limit", contentLiTable.limit());
	if(keyword!=""&&keyword!=undefined){

		var ret = cmd.execute("search");
		}else{
			var ret = cmd.execute("getList");
		}
        if (!ret.error) {
        	if(ret.total==1){
        		window.location.href="{{PageContext.ContextPath}}iportal/base-1?ID="+ret.data[0].ID;
        	}else{
        		contentLiTable.toBody(ret);
    			// 3.加载分页控件：分页div的id，Table的实例化对象名称（字符串）,总数量，查询方法（字符串）
    			contentLiTable.toPageBar("epager", "contentLiTable", ret.total,"onQuery()");
        	}			
		} else {
			LEx.dialog({
				title : "系统提示",
				content : ret.message,
				icon : 'error',
				lock : true
			});
		}
}



function parseDate(val) {
	if (LEx.isNotNull(val)) {
		return LEx.util.Format.formatDate(val);
	} else {
		return '-';
	}
}

function parseLength(val) {
	if (LEx.isNotNull(val)&&val.length>40) {
		return val.substring(0,40)+"...";
	} else {
		return val;
	}
}