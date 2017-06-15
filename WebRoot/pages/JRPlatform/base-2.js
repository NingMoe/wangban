var cname = LEx.urldata.cname;  

var contentLiTable = null;

function init() {
	$('#cname').html(cname);
	contentLiTable = new LEx.Control.Table("contentLi_s", "content_li");
	contentLiTable.limit(9);
	onQuery(1);
}

function onQuery(start) {
//	var cmd = new LEx.Command("app.icity.govservice.GovProjectCmd");
//	cmd.setParameter("channel.name@=", cname);
//	cmd.setParameter("content.rid@=", '{{UserInfo.regionId}}');
//    //默认只显示通过审核的数据
//    cmd.setParameter("CHECKS@=", '1');
//    cmd.setParameter("sort"," ID DESC");
//	cmd.setParameter("start", contentLiTable.start());
//	cmd.setParameter("limit", contentLiTable.limit());
//	var ret = cmd.execute("getListNew");
	var cmd = new LEx.Command("app.icity.govservice.GovProjectCmd");
	cmd.setParameter("start",contentLiTable.start());
	cmd.setParameter("limit", contentLiTable.limit());
	cmd.setParameter("name", cname);
	cmd.setParameter("open", '0');
	var ret =  cmd.execute("getContentInfo");
    if (!ret.error) {
    	if(ret.total==1){
    		window.location.href="{{PageContext.ContextPath}}JRPlatform/base-1?ID="+ret.data[0].ID;
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