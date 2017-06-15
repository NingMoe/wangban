var cname = LEx.urldata.cname;
var region_code = getSecurityValue("WebRegion");
$(function(){	
	$('#position').html(cname);
	$('#title').html(cname);
	onQuery(1);
	
});

function onQuery(start) {
	var limit = 8;
	var cmd = new LEx.Command("app.icity.govservice.GovProjectCmd");
	cmd.setParameter("channel.name@=", cname);
	cmd.setParameter("content.rid@=", region_code);
    cmd.setParameter("CHECKS@=", '1');
    cmd.setParameter("workTime", '1');
    cmd.setParameter("sort"," CTIME DESC");
    cmd.setParameter("start", (start-1)*limit);
	cmd.setParameter("limit", limit);
	var ret = cmd.execute("getList");
	if (!ret.error) {
		$('#wsfblist').html(LEx.processDOMTemplate('contentTemplate', ret));
		var total = ret.total;
		if (total == 0) {
			$("#cpage").hide();
		} else {
			$("#cpage").show();
		}
		$("#totalCount").html(total);
		total = Math.ceil(total / limit);
		$("#PageCount").html(total);
		$("#CurrIndex").html(start > total ? total : start);
		$(function() {
			$('.km-list-group-matter > .list-group-item').hover(function() {
				$(this).addClass('open').siblings().removeClass('open');
			});
		});
	} else {
		$("#cpage").hide();
		LEx.alert(ret.error);
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

//翻页
function gotoPage(type) {
	turnpage(type, "onQuery");
}