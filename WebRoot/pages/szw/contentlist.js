var contentLiTable = null;
var _calendar= null;
var depts=null;		//部门数组
var region="";
var isout="0";
var cname = LEx.urldata.cid;
var keyword=LEx.urldata.key;

$(function(){
	 $("#er-title").html(cname);
	contentLiTable = new LEx.Control.Table("contentLi_s", "content_li");
	//contentLiTable.limit(contentLiTable.limit());//{{pagelimit}});
	//$("#"+cname).attr("style","background-color:#ffffff");
	onQuery();
	
});

function onQuery() {
	if(cname=="动态要闻") isout = '{{ConfigInfo.Dtyw_isout}}';
	else if(cname=="改革创新") isout = '{{ConfigInfo.Ggcx_isout}}';
	else if(cname=="专题专栏") isout = '{{ConfigInfo.Ztzl_isout}}';
	else if(cname=="公示公告") isout = '{{ConfigInfo.Gsgg_isout}}';
	else if(cname=="考核考评") isout = '{{ConfigInfo.Khkp_isout}}';
	var cmdl = new LEx.Command("app.icity.govservice.GovProjectCmd");
	//var cmd = new LEx.Command("app.pmi.content.ContentCmd");
    cmdl.setParameter("name", cname);
    cmdl.setParameter("open", isout);
    //默认只显示通过审核的数据
    cmdl.setParameter("CHECKS@=", '1');
    cmdl.setParameter("sort"," ID DESC");//cmd.setParameter("sort"," CTIME DESC");
	cmdl.setParameter("start", contentLiTable.start());
	cmdl.setParameter("limit", contentLiTable.limit());
	var ret = null;
	ret=cmdl.execute("getContentInfo");
	debugger;
		if (!ret.error) {//只有一条 新闻时，直接找开内容页
		if(ret.total==1){
			if(ret.data[0].NAME=="新手指南"||ret.data[0].NAME=="网站介绍"||ret.data[0].NAME=="联系我们"){
				window.location.href="content?id="+ret.data[0].ID;
				}else{
			$("#content").html('<div class="" style="padding: 15px 50px;"><h3 id="title" class="text-center" style="color:#005fa7; font-weight:bold">'+ret.data[0].NAME+'</h3>'
					+'<hr width="95%" style="border:solid 1px #999"><div class="" style="margin-left:auto;margin-right:auto;margin-top:5px;text-align:center;width:100%;"><div><div class="" style="float:left;width:25%">&nbsp;</div><div class="" style="float:left;width: 50%;margin-top: 3px;"><span style="margin-left: 10px;">来源:&nbsp;'+ret.data[0].DEPT_NAME+'</span><span style="margin-left: 10px;">发布人:&nbsp;'+ ret.data[0].CREATOR+'</span><br/><span style="margin-left: 10px;">发布时间:&nbsp;'+ret.data[0].CTIME +' </span></div><br/><div id="" style="float:left;width:25%" class="bdshare_t bds_tools get-codes-bdshare"><span class="bds_more">分享到：</span><a href="javascript:shareWebSite(\'http://v.t.qq.com/share/share.php\');"><img style="width:15px;height:15px;" src=\'{{PageContext.ContextPath}}public/assets/img/qq.png\' border=\'0\' alt=\'腾讯微博\' title=\'分享到腾讯微博\'/></a>&nbsp;<a href="javascript:shareWebSite(\'http://v.t.sina.com.cn/share/share.php\');"><img style="width:20px;height:20px;" src=\'{{PageContext.ContextPath}}public/assets/img/xinl.jpg\' alt=\'新浪微博\' title=\'分享到新浪微博\' border=\'0\'></a></div></div></div>'+ret.data[0].CONTENT+'</div>');
		//window.location.href="content?id="+ret.data[0].ID;
				}
			}
		else{
		contentLiTable.toBody(ret);
		// 3.加载分页控件：分页div的id，Table的实例化对象名称（字符串）,总数量，查询方法（字符串）
		contentLiTable.toPageBar("epager", "contentLiTable", ret.total,"onQuery()");
		$("#contentLi_s").html(LEx.processDOMTemplate("content_li",ret));
		$("#epager").html(contentListTable.pagebar(contentListTable,ret.total,onQuery()));
		contentLiTable=null;
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

function formatDate(obj) {
	return LEx.util.Format.formatDate(obj);
}
function shareWebSite(url){
	window.open(url+'?title='+encodeURIComponent(location.href)+'&url='+encodeURIComponent(location.href),"_blank");
}
function initDepart(){
	var cmd=new LEx.Command("app.icity.project.ProjectIndexCmd");
	cmd.setParameter("division_code", {{webrg}});
	var ret=cmd.execute("getRegionList");
	if(ret.state==1&&ret.data&&ret.data.department){
		depts=ret.data.department;
		var option = '<option value="">所有部门</option>';
		if(depts.length>0){
			$.each(depts, function(i,n) {
				var depart = LEx.urldata.depart;
				if((depart!=''||depart!=null)&&n.ID==depart){
					option += '<option value="' + n.ID + '" selected="true">' + n.NAME_SHORT + '</option>';
				}else{
					option += '<option value="' + n.ID + '">' + n.NAME_SHORT + '</option>';
				}				
			});
			$("#selectdept").append(option);
		}	
	}else{
		alert(cmd.error);
	}
}


