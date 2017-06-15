var projectInfo;
var permitIndustry;
var code = $('#code').val();
var itemInfo = {};
var showPage="";

$(function(){	
	// 检查用户是否登录
	checkLogin();
	getItemInfo();
	//绑定
	getInvestDictList();
	/*
	$('#permitIndustry').change(function(){
       // alert($(this).children('option:selected').val());  //弹出select的值
		var html = getXMHYFLML($(this).children('option:selected').val());
		if(html=="undefined"||html==undefined){
			html = "";
		}else{
			$("#permitItemCode").attr("value",html.CODE);
			html = html.NAME;
		}
		$("#permitItemCode").html(html);
    });
	*/
	//var _calendar= new LEx.Control.Calendar(function(){
		//_calendar.setup({id:"startYear"});
		//_calendar.setup({id:"endYear"});
		//_calendar.setup({id:"applyDate"});
	//});
	LEx.dialog({
		id: "note", title: "选择审批阶段",
		url: LEx.webPath+"pro/noteSP",
		width: 600, height: 280,
		lock:true,
		button: [
		         {
		        	 name: '确定',
		        	 focus: true,
		        	 callback: function() {
			        	 var iframe = this.iframe.contentWindow;
			        	 if (!iframe.document.body) {
			        		 LEx.alert('内容还没加载完毕呢');
			        		 return false;
			        	 }
			        	 var select = "";
			        	 if((select=iframe.getData())){
			        		 document.getElementById('projectStage').value=select;
			        		 return true;
			        	 }else{
			        		 return false;
			        	 }
		        	 }
		         }
		         ]
	});
	
	$('#startYear').datetimepicker({autoclose:true,language: 'zh-CN',format:'yyyy', startView:4,minView:4}).on('changeDate', function(ev) {
		var end=$('#endYear').val();
		var start=$('#startYear').val();
		if(end.length>0 && start.length>0){
			if(parseInt(end)<parseInt(start)){
				alert("拟开工时间要小于拟建成时间!");	
				$('#startYear').val("");
				return;
			}
		}
	});
	$('#endYear').datetimepicker({autoclose:true,language: 'zh-CN',format:'yyyy', startView:4,minView:4,minDate:'2013'}).on('changeDate', function(ev) {
		var end=$('#endYear').val();
		var start=$('#startYear').val();
		if(end.length>0 && start.length>0){
			if(parseInt(end)<parseInt(start)){
				alert("拟建成时间大于要拟开工时间!");		
				$('#endYear').val("");
				return;
			}
		}
	});
	$('#applyDate').datetimepicker({autoclose:true,language: 'zh-CN',format:'yyyy-mm-dd',minView: 2});
	//张震永新提需求  分类  目录  类型固定 开发区必填
	$("#isDeArea").change(function(){
		 if($(this).children('option:selected').val()=="1"){
			 $("#kfqmc").show();
		 }else{
			 $("#kfqmc").hide();
		 }
	});
	
	var id;
	
	//if(showPage=="1"){
	//	$("#permitItemCode").html(itemInfo.NAME);
	//	$("#permitItemCode").attr("value",code);
	//}
	/*
	for(var i=0;i<projectInfo.length;i++){
		if(projectInfo[i].CODE == code){
			id = projectInfo[i].PARENT_CODE;
			$("#permitItemCode").html(projectInfo[i].NAME);
			//$("#permitItemCode").attr("value","A00001ML01");
		}
	}	
	*/
	//for(var i=0;i<permitIndustry.length;i++){
	//	if(permitIndustry[i].CODE == itemInfo.PARENT_CODE){
	//		setSelected(document.getElementById('permitIndustry'),permitIndustry[i].NAME);			
	//	}
	//}
	
	//setSelected(document.getElementById('projectType'),"核准类项目");
	$("#divisionName").val("{{region.NAME}}");
	$("#divisionCode").val("{{region.CODE}}");
	
});

function getItemInfo()
{
	var command = new LEx.Command("app.icity.ipro.IproCmd");
	command.setParameter("code", code);
	var ret = command.execute("getItemInfoByCode");
	if(!command.error){	
		itemInfo = ret.data.info[0];
	}
}

//设置select 选中值
function setSelected(title,val){
	for(var i=0;i<title.options.length;i++){
		if(title.options[i].innerHTML == val){
				title.options[i].selected = true;
				break;
		}
	}
}
var XMHYFLML=[];//获取子类
function getXMHYFLML(val){
	var len = XMHYFLML.length;
	if(len>0){
		for(var i=0;i<len;i++){
			if(XMHYFLML[i].PARENT_CODE == val){
				return XMHYFLML[i];
			}
		}
	}
}
//获取字典
function getInvestDictList(){
	var command = new LEx.Command("app.icity.ipro.IproCmd");
	var ret = command.execute("getInvestDictList");
	if(!command.error){
		XMHYFLML = ret.data.XMHYFLML;				
		//项目类型：
		var projectType = ret.data.TZSPXMLX;
		bindOption("projectType",projectType);
		
		//*建设性质：
		var constructPer = ret.data.TZSPJSXZ;
		bindOption("constructPer",constructPer);
		
		//法人证件类型TZSPXMFRZZLX
		var lerepCerttype = ret.data.TZSPXMFRZZLX;
		bindOption("lerepCerttype",lerepCerttype);
		
		//*所属行业：
		//var industry = ret.data.TZSPSSHY;
		//bindOption("industry",industry);
		
		//项目阶段：
		var projectStage = ret.data.TZSPXMJD;
		bindOption("projectStage",projectStage);
		
		//投资项目行业分类：
		//if(showPage=="1"){
		//	permitIndustry = ret.data.TZSPXMHYFL;
		//	bindOption("permitIndustry",permitIndustry);
		//}
		//项目基本信息
		projectInfo = ret.data.XMHYFLML;
		
		return true;
	}else{
		alert("获取字典失败！");
		return false;
	}
}
//绑定select
function bindOption(key,val){
	 var sel=document.getElementById(key);
	 var len = val.length;
	 if(len>0){
		 for(var i=0;i<len;i++){
			 if(val[i].NAME){
				 var opt =new Option(formatLen(val[i].NAME,0,9),val[i].CODE);
				 $(opt).attr("title",val[i].NAME);
				 sel.options.add(opt);
			 }
		 }
	 }
}

function checkit(){
	var divisionCode=$("#divisionCode").val();
	if(divisionCode=="") {
		$("#divisionName").addClass("bordered");}
	else{
		$("#divisionName").removeClass("bordered");
	}
	//var permitIndustry=$("#permitIndustry").val();
	//if(permitIndustry=="") {
	//	$("#permitIndustry").addClass("bordered");}
	//else{
	//	$("#permitIndustry").removeClass("bordered");
	//}
	var projectName=$("#projectName").val();
	if(projectName=="") {
		$("#projectName").addClass("bordered");}
	else{
		$("#projectName").removeClass("bordered");
	}
	var projectType=$("#projectType").val();
	if(projectType=="") {
		$("#projectType").addClass("bordered");}
	else{
		$("#projectType").removeClass("bordered");
	}
	var constructPer=$("#constructPer").find("option:selected").text();
	if(constructPer=="") {
		$("#constructPer").addClass("bordered");}
	else{
		$("#constructPer").removeClass("bordered");
	}
	var contractor=$("#contractor").val();
	if(contractor=="") {
		$("#contractor").addClass("bordered");}
	else{
		$("#contractor").removeClass("bordered");
	}
	var lerepCerttype=$("#lerepCerttype").find("option:selected").text();
	if(lerepCerttype=="") {
		$("#lerepCerttype").addClass("bordered");}
	else{
		$("#lerepCerttype").removeClass("bordered");
	}
	var lerepCertno=$("#lerepCertno").val();
	if(lerepCertno=="") {
		$("#lerepCertno").addClass("bordered");}
	else{
		$("#lerepCertno").removeClass("bordered");
	}
	var endYear=$("#endYear").val();
	if(endYear=="") {
		$("#endYear").addClass("bordered");}
	else{
		$("#endYear").removeClass("bordered");
	}
	var investment=$("#investment").val();
	if(investment=="") {
		$("#investment").addClass("bordered");}
	else{
		$("#investment").removeClass("bordered");
	}
	var placeCode=$("#placeCode").val();
	if(placeCode=="") {
		$("#placeCode").addClass("bordered");}
	else{
		$("#placeCode").removeClass("bordered");
	}
	var placeCodeDetail=$("#placeDetailCode").val();
	if(placeCodeDetail=="") {
		$("#placeDetailCode").addClass("bordered");}
	else{
		$("#placeDetailCode").removeClass("bordered");
	}
	var industry=$("#industry").val();
	if(industry=="") {
		$("#industry").addClass("bordered");}
	else{
		$("#industry").removeClass("bordered");
	}
	var applyDate=$("#applyDate").val();
	if(applyDate=="") {
		$("#applyDate").addClass("bordered");}
	else{
		$("#applyDate").removeClass("bordered");
	}
	var linkMan=$("#linkMan").val();
	if(linkMan=="") {
		$("#linkMan").addClass("bordered");}
	else{
		$("#linkMan").removeClass("bordered");
	}
	var linkPhone=$("#linkPhone").val();
	var checkPhone=/^1\d{10}$/;
	if(linkPhone==""||!linkPhone.match(checkPhone)) {
		$("#linkPhone").addClass("bordered");}
	else{
		$("#linkPhone").removeClass("bordered");
	}
	var contactEmail=$("#contactEmail").val();
	var checkEmail=/^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/;
	if(checkEmail==""||!contactEmail.match(checkEmail)) {
		$("#contactEmail").addClass("bordered");}
	else{
		$("#contactEmail").removeClass("bordered");
	}
	var startYear=$("#startYear").val();
	if(startYear=="") {
		$("#startYear").addClass("bordered");}
	else{
		$("#startYear").removeClass("bordered");
	}
	var placeName=$("#placeName").val();
	if(placeName=="") {
		$("#placeName").addClass("bordered");}
	else{
		$("#placeName").removeClass("bordered");
	}
	var placeDetailName=$("#placeDetailName").val();
	if(placeDetailName=="") {
		$("#placeDetailName").addClass("bordered");}
	else{
		$("#placeDetailName").removeClass("bordered");
	}
	var industryName=$("#industryName").val();
	if(industryName=="") {
		$("#industryName").addClass("bordered");}
	else{
		$("#industryName").removeClass("bordered");
	}	
	var projectContent=$("#projectContent").val();
	if(projectContent=="") {
		$("#projectContent").addClass("bordered");}
	else{
		$("#projectContent").removeClass("bordered");
	}
	var isDeArea=$("#isDeArea").children('option:selected').val();
	var deAreaName=$("#deAreaName").val();
	if(isDeArea==1&&deAreaName==""){
		$("#deAreaName").addClass("bordered");
	}else{
		$("#deAreaName").removeClass("bordered");
	}
	if(divisionCode==""||projectName==""||projectType==""||constructPer==""||contractor==""||lerepCerttype==""||
		lerepCertno==""||endYear==""||investment==""||placeCode==""||placeCodeDetail==""||industry==""||applyDate==""||
		linkMan==""||linkPhone==""||contactEmail==""||!contactEmail.match(checkEmail)||!linkPhone.match(checkPhone)||(isDeArea==1&&deAreaName=="")){
		alert("请将必填项填写之后再进行提交。");
	}else{
		submit();
		alert("提交成功！");
	}
}

function submit(){
	var obj = LEx.form.get("sub_form","name");
	obj.divisionCode = formatLen(obj.divisionCode,0,6);
	obj.placeCode = formatLen(obj.placeCode,0,6);
	obj.startYear = formatLen(obj.startYear,0,4);
	obj.endYear = formatLen(obj.endYear,0,4);
	obj.placeCodeDetail = obj.placeDetailCode;  //建设地点详情
	obj.placeAreaDetail = obj.placeDetailName;  //建设地点详情
	obj.permitIndustry = '';
	var command = new LEx.Command("app.icity.ipro.IproCmd");
	command.setParameter("region_code",$("#divisionCode").val());
	command.setParameter("region_name",$("#divisionName").val());
	command.setParameter("baseInfo",obj);
	command.setParameter("userInfo",loginInfo);
	command.setParameter("SXBM",code);
	if(showPage=="1") command.setParameter("SXMC",itemInfo.NAME);
	var ret = command.execute("saveInvestInfo");
	
	$("#user_info_name").html(loginInfo.name);
	$("#sb_item_name").html(obj.projectName);
	
	if(!command.error){	
		//alert(ret.data.error);
		$("#sub_form").hide();
		$("#service_step_03").show();
		$("#sub_button").hide();
		if(ret.data.code=="200"){			
			$("#bjsqh").html(ret.data.id);			
			$("#suc").show();
			$("#fa").hide();
		}else{			
			$("#suc").hide();
			$("#fa").show();
		}
	}else{
		alert("办理失败！");		
		$("#sub_form").hide();
		$("#service_step_03").show();
		$("#sub_button").hide();					
		$("#suc").hide();
		$("#fa").show();	
	}
	
	//alert('提交成功');
	//$("#sub_form").hide();
	//$("#service_step_03").show();
	//$("#sub_button").hide();
	//$("#bjsqh").html('2015-370000-82-01-000125');
	//$("#suc").show();
	//$("#fa").hide();
}
function openRegion(mark){
	LEx.dialog({
		id: "selectRegion", title: "选择区划",
		url: LEx.webPath+"pro/deptTree",
		width: 400, height: 450,
		lock:true,
		button: [
		         {
		        	 name: '选择',
		        	 callback: function() {
		        	 var iframe = this.iframe.contentWindow;
		        	 if (!iframe.document.body) {
		        		 LEx.alert('内容还没加载完毕呢');
		        		 return false;
		        	 }
		        	 var region = {};
		        	 if((region=iframe.getData())){		        		
		        		 if(region.CODE){
		        			 $("#"+mark+"Name").val(region.NAME);
		        			 $("#"+mark+"Code").val(region.CODE);
		        		 }
		        		 return true;
		        	 }else{
		        		 return false;
		        	 }
		         },
		         focus: true
		         },
		         {
		        	 name: '关闭'
		         }
		         ]
	});
}

function openRegionChecks(mark){
	LEx.dialog({
		id: "selectRegion", title: "选择区划",
		url: LEx.webPath+"pro/deptTree?checkbox=true",
		width: 400, height: 450,
		lock:true,
		button: [
		         {
		        	 name: '选择',
		        	 callback: function() {
		        	 var iframe = this.iframe.contentWindow;
		        	 if (!iframe.document.body) {
		        		 LEx.alert('内容还没加载完毕呢');
		        		 return false;
		        	 }
		        	 var regions = [];
		        	 if((regions=iframe.getDatas())){
		        		 if(regions.length>0){
		        			 var regionName = "";
		        			 var regionCode = "";
		        			 for(var i=0;i<regions.length;i++){
		        				 regionName += "," + regions[i].NAME;
		        				 regionCode += "," + regions[i].CODE.substring(0,6);
		        			 }
		        			 regionName = regionName.substr(1);
		        			 regionCode = regionCode.substr(1);
		        			 $("#"+mark+"Name").val(regionName);
		        			 $("#"+mark+"Code").val(regionCode);
		        		 }
		        		 return true;
		        	 }else{
		        		 return false;
		        	 }
		         },
		         focus: true
		         },
		         {
		        	 name: '关闭'
		         }
		         ]
	});
}

function opengmjjhy(mark){
	LEx.dialog({
		id: "selectGmjjhy", title: "选择行业分类",
		url: LEx.webPath+"pro/gmjjhyTree",
		width: 400, height: 450,
		lock:true,
		button: [
		         {
		        	 name: '选择',
		        	 callback: function() {
		        	 var iframe = this.iframe.contentWindow;
		        	 if (!iframe.document.body) {
		        		 LEx.alert('内容还没加载完毕呢');
		        		 return false;
		        	 }
		        	 var region = {};
		        	 if((region=iframe.getData())){		        		
		        		 if(region.id){
		        			 $("#"+mark+"Name").val(region.name);
		        			 id="A"+region.id;
		        			 var length=id.length;
		        			 while(length<6){
		        				 id=id+"X";
		        				 length=id.length;
		        			 }
		        			 $("#"+mark).val(id);
		        		 }
		        		 return true;
		        	 }else{
		        		 return false;
		        	 }
		         },
		         focus: true
		         },
		         {
		        	 name: '关闭'
		         }
		         ]
	});
}

function checkPhone(o){
	
}
