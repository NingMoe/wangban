var projectInfo;
var permitIndustry;
var code = $('#code').val();
var itemInfo = {};
var region_code="{{region_code}}";
// add by cys
var types="";// 存放改事项的目录所属行业
var lerepInfoJson=[];// 定义法人信息json数组
var addlerepInfoJson={};
var contrInfoJson=[];// 定义外商信息json数组
var addcontrInfoJson={};
var foreignAbroadFlag="0";// 是否外商投资/境外投资,[0否，1 外商投资项目，2境外投资项目]
// end by cys
var neworold="";//存放新旧信息
$(function(){	
	// 检查用户是否登录
	checkLogin();
	// modify by cys,for:注释
	/*
	 * $("#linkMan").val(loginInfo.realname);
	 * $("#linkPhone").val(loginInfo.mobile);
	 * $("#contractor").val(loginInfo.name);
	 * $("#contactEmail").val(loginInfo.email);
	 */
	// end by cys
	
	getItemInfo();
	// 绑定
	getInvestDictList();
	//判断新建还是修改
	updateorinput();
	//获取修改信息
	getUpdateList();
	/*
	 * $('#permitIndustry').change(function(){ //
	 * alert($(this).children('option:selected').val()); //弹出select的值 var html =
	 * getXMHYFLML($(this).children('option:selected').val());
	 * if(html=="undefined"||html==undefined){ html = ""; }else{
	 * $("#permitItemCode").attr("value",html.CODE); html = html.NAME; }
	 * $("#permitItemCode").html(html); });
	 */
	// var _calendar= new LEx.Control.Calendar(function(){
		// _calendar.setup({id:"startYear"});
		// _calendar.setup({id:"endYear"});
		// _calendar.setup({id:"applyDate"});
	// });
	
	LEx.dialog({
		id: "note", title: "选择审批阶段",
		url: LEx.webPath+"ipro/noteSP",
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
	// 张震永新提需求 分类 目录 类型固定 开发区必填
	$("#isDeArea").change(function(){
		 if($(this).children('option:selected').val()=="1"){
			 $("#kfqmc").show();
		 }else{
			 $("#kfqmc").hide();
		 }
	});
	
	var id;
	
	if(neworold=="新建"){
		$("#permitItemCode").html(itemInfo.NAME);
		$("#permitItemCode").attr("value",code);
	 }
	/*
	 * for(var i=0;i<projectInfo.length;i++){ if(projectInfo[i].CODE == code){
	 * id = projectInfo[i].PARENT_CODE;
	 * $("#permitItemCode").html(projectInfo[i].NAME);
	 * //$("#permitItemCode").attr("value","A00001ML01"); } }
	 */
	for(var i=0;i<permitIndustry.length;i++){
		if(neworold=="新建"){
		if(permitIndustry[i].CODE == itemInfo.PARENT_CODE){
			setSelected(document.getElementById('permitIndustry'),permitIndustry[i].NAME);	
			// add by cys
			types=permitIndustry[i].NAME;
			// end by cys
		}
	}
	}
	setSelected(document.getElementById('projectType'),"审批类项目");
	if(neworold=="新建"){
	$("#divisionName").val(itemInfo.DIVISION_NAME);
	$("#divisionCode").val(itemInfo.DIVISION_CODE);}
	
	// add by cys
	if(types=="外商投资"){
		$(".jw").hide();
		$(".ws").show();
		$("#contrInfo_form").show();
	}
	else if(types=="境外投资"){
		$(".jw").show();
		$(".ws").hide();
		$("#contrInfo_form").hide();
	}else{
		$(".jw").hide();
		$(".ws").hide();	
		$("#contrInfo_form").hide();
		$(".touzi").hide();// 只有境外投資和外商投資，有投資方式選項
	}
	// end by cys
	
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

// 设置select 选中值
function setSelected(title,val){
	for(var i=0;i<title.options.length;i++){
		if(title.options[i].innerHTML == val){
				title.options[i].selected = true;
				break;
		}
	}
}
var XMHYFLML=[];// 获取子类
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
// 获取字典
function getInvestDictList(){
	var command = new LEx.Command("app.icity.ipro.IproCmd");
	var ret = command.execute("getInvestDictList");
	if(!command.error){
		XMHYFLML = ret.data.XMHYFLML;				
		// 项目类型：
		var projectType = ret.data.TZSPXMLX;
		bindOption("projectType",projectType);
		
		// *建设性质：
		var constructPer = ret.data.TZSPJSXZ;
		bindOption("constructPer",constructPer);
		
		// 法人证件类型TZSPXMFRZZLX
		var lerepCerttype = ret.data.TZSPXMFRZZLX;
		bindOption("lerepCerttype",lerepCerttype);
		
		// *所属行业：
		// var industry = ret.data.TZSPSSHY;
		// bindOption("industry",industry);
		
		// 项目阶段：
		var projectStage = ret.data.TZSPXMJD;
		bindOption("projectStage",projectStage);
		
		// 投资项目行业分类：
		// if(showPage=="1"){
			permitIndustry = ret.data.TZSPXMHYFL;
			bindOption("permitIndustry",permitIndustry);
		// }
		// 项目基本信息
		projectInfo = ret.data.XMHYFLML;
		
		// add by cys
		// 投资方式：
		var investmentMode = ret.data.INVESTMENTMODE;
		bindOption("investmentMode",investmentMode);
		
	
		// 项目单位性质
		var enterpriseNature=ret.data.ENTERPRISENATURE;
		bindOption("enterpriseNature",enterpriseNature);
		// 适用产业政策条目类型
		var industrialPolicyType=ret.data.INDUSTRIALPOLICYTYPE;
		bindOption("industrialPolicyType",industrialPolicyType);
		// 土地获取方式
		var getLandMode=ret.data.LANDMODE;
		bindOption("getLandMode",getLandMode);
		
		// 出资方式
		var contributionMode=ret.data.CONTRIBUTIONMODE;
		bindOption("contributionMode",contributionMode);
		// end by cys
		
		return true;
	}else{
		alert("获取字典失败！");
		return false;
	}
}
//判断新建还是修改
function updateorinput(){
	
	var command4 = new LEx.Command("app.icity.ipro.IproCmd");
	command4.setParameter("code", code);
	var ret4 = command4.execute("updateorinput");
	if (ret4==1) { 
		neworold="修改";
		}
	if (ret4==2){
		neworold="新建";
	}
	
}
//获取修改信息
function getUpdateList(){
	if(neworold=="修改"){
		var code='{{code}}';
		var indus;

		var command = new LEx.Command("app.icity.ipro.IproCmd");
		command.setParameter("code", code);
		var ret = command.execute("getProjectModify");
		var divisionCode=ret.data[0].DIVISION_CODE;
			if(divisionCode.length!=12){	
			divisionCode=divisionCode+"000000";
		}
		$("#divisionName").val(divisionCode);
		$("#divisionCode").val(divisionCode);
		$("#projectName").val(ret.data[0].PROJECT_NAME);
		$("#contractor").val(ret.data[0].CONTRACTOR);
		$("#lerepCertno").val(ret.data[0].LEREP_CERTNO);
		$("#startYear").val(ret.data[0].START_YEAR);
		$("#endYear").val(ret.data[0].END_YEAR);
		$("#investment").val(ret.data[0].INVESTMENT);
		$("#placeName").val(ret.data[0].PLACE_NAME);
		$("#placeCode").val(ret.data[0].PLACE_CODE);
		$("#placeDetailName").val(ret.data[0].PLACE_AREA_DETAIL);
		$("#placeDetailCode").val(ret.data[0].PLACE_CODE_DETAIL);
		$("#deAreaName").val(ret.data[0].DE_AREA_NAME);
		$("#applyDate").val(ret.data[0].APPLY_DATE);
		$("#projectContent").val(ret.data[0].PROJECT_CONTENT);
		$("#linkMan").val(ret.data[0].LINK_MAN);
		$("#linkPhone").val(ret.data[0].LINK_PHONE);
		$("#contactEmail").val(ret.data[0].CONTACT_EMAIL);
			$("#isDeArea").val(ret.data[0].IS_DE_AREA);
			indus=ret.data[0].INDUSTRY;
			var ind=indus;
			var first=indus.indexOf('A');
			var last=indus.indexOf('X');
			indus=indus.substring(first+1,last);
			/*for(var i=0;i<sshy.length;i++){
				if(indus==sshy[i].id){
					$("#industryName").val(sshy[i].name);
					$("#industry").val(ind);
					break;
				}
			}*/
			
		var PERMIT_ITEM_CODE=ret.data[0].PERMIT_ITEM_CODE;
		$("#permitItemCode").attr("value",PERMIT_ITEM_CODE);
		var command2 = new LEx.Command("app.icity.ipro.IproCmd");
		command2.setParameter("code",PERMIT_ITEM_CODE);
		var ret2 = command2.execute("checkCode2");
		/*$("#permitItemName").attr("value",ret2.data[0].PERMIT_ITEM_NAME);
		PERMIT_ITEM_NAME=ret2.data[0].PERMIT_ITEM_NAME;
		$("#permitItemCode").val(ret2.data[0].PERMIT_ITEM_NAME);*/
		getInvestDictList();
		for(var i=0;i<permitIndustry.length;i++){
			if(permitIndustry[i].CODE == ret.data[0].PERMIT_INDUSTRY){
				$("#permitIndustry")[0].selectedIndex = i;
				break;
			}
		}
		if($("#permitIndustry")[0].selectedIndex==10){
			types="外商投资";
		}
		else 
			if($("#permitIndustry")[0].selectedIndex==11){
				types="境外投资";
			}
			else{
				types="农业水利";
			}
		
		for(var i=0;i<projectType.length;i++){
			if(projectType[i].CODE == ret.data[0].PROJECT_TYPE){
				$('#projectType')[0].selectedIndex = i;
				break;
			}
		}
		
		for(var i=0;i<constructPer.length;i++){
			if(constructPer[i].CODE == ret.data[0].CONSTRUCT_PER){
				$('#constructPer')[0].selectedIndex = i;
				break;
			}
		}
		
		for(var i=0;i<lerepCerttype.length;i++){
			if(lerepCerttype[i].CODE == ret.data[0].LEREP_CERTTYPE){
				$('#lerepCerttype')[0].selectedIndex = i;
				break;
			}
		}
		}
}
// 绑定select
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
	var divisionCode=$("#divisionCode").val().trim();
	if(divisionCode=="") {
		$("#divisionName").addClass("bordered");}
	else{
		$("#divisionName").removeClass("bordered");
	}
	var permitIndustry=$("#permitIndustry").val().trim();
	if(permitIndustry=="") {
		$("#permitIndustry").addClass("bordered");}
	else{
		$("#permitIndustry").removeClass("bordered");
	}
	var projectName=$("#projectName").val().trim();
	if(projectName=="") {
		$("#projectName").addClass("bordered");}
	else{
		$("#projectName").removeClass("bordered");
	}
	var linkMan=$("#linkMan").val().trim();
	if(linkMan=="") {
		$("#linkMan").addClass("bordered");}
	else{
		$("#linkMan").removeClass("bordered");
	}
	var linkPhone=$("#linkPhone").val().trim();
	if(linkPhone=="") {
		$("#linkPhone").addClass("bordered");}
	else{
		$("#linkPhone").removeClass("bordered");
	}
	var projectType=$("#projectType").val().trim();
	if(projectType=="") {
		$("#projectType").addClass("bordered");}
	else{
		$("#projectType").removeClass("bordered");
	}
	var constructPer=$("#constructPer").find("option:selected").val().trim();
	if(constructPer=="") {
		$("#constructPer").addClass("bordered");}
	else{
		$("#constructPer").removeClass("bordered");
	}
	/*
	 * var contractor=$("#contractor").val(); if(contractor=="") {
	 * $("#contractor").addClass("bordered");} else{
	 * $("#contractor").removeClass("bordered"); }
	 */
	/*
	 * var lerepCerttype=$("#lerepCerttype").find("option:selected").text();
	 * if(lerepCerttype=="") { $("#lerepCerttype").addClass("bordered");} else{
	 * $("#lerepCerttype").removeClass("bordered"); } var
	 * lerepCertno=$("#lerepCertno").val(); if(lerepCertno=="") {
	 * $("#lerepCertno").addClass("bordered");} else{
	 * $("#lerepCertno").removeClass("bordered"); }
	 */
	var endYear=$("#endYear").val().trim();
	if(endYear=="") {
		$("#endYear").addClass("bordered");}
	else{
		$("#endYear").removeClass("bordered");
	}
	var investment=$("#investment").val().trim();// *总投资（万元）：
	// modiy by cys
	if(investment==""||!validateY(investment)) {
		$("#investment").addClass("bordered");}
	else{
		$("#investment").removeClass("bordered");
	}
	// end by cys
	var placeCode=$("#placeCode").val().trim();
	if(placeCode=="") {
		$("#placeCode").addClass("bordered");}
	else{
		$("#placeCode").removeClass("bordered");
	}
	var placeCodeDetail=$("#placeDetailCode").val().trim();
	if(placeCodeDetail=="") {
		$("#placeDetailCode").addClass("bordered");}
	else{
		$("#placeDetailCode").removeClass("bordered");
	}
	var industry=$("#industry").val().trim();
	if(industry=="") {
		$("#industry").addClass("bordered");}
	else{
		$("#industry").removeClass("bordered");
	}
	var applyDate=$("#applyDate").val().trim();
	if(applyDate=="") {
		$("#applyDate").addClass("bordered");}
	else{
		$("#applyDate").removeClass("bordered");
	}
	
/*
 * var linkPhone=$("#linkPhone").val(); var checkPhone=/^1\d{10}$/;
 * if(linkPhone==""||!linkPhone.match(checkPhone)) {
 * $("#linkPhone").addClass("bordered");} else{
 * $("#linkPhone").removeClass("bordered"); } var
 * contactEmail=$("#contactEmail").val(); var
 * checkEmail=/^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/;
 * if(checkEmail==""||!contactEmail.match(checkEmail)) {
 * $("#contactEmail").addClass("bordered");} else{
 * $("#contactEmail").removeClass("bordered"); }
 */
	var startYear=$("#startYear").val().trim();
	if(startYear=="") {
		$("#startYear").addClass("bordered");}
	else{
		$("#startYear").removeClass("bordered");
	}
	var placeName=$("#placeName").val().trim();
	if(placeName=="") {
		$("#placeName").addClass("bordered");}
	else{
		$("#placeName").removeClass("bordered");
	}
	var placeDetailName=$("#placeDetailName").val().trim();
	if(placeDetailName=="") {
		$("#placeDetailName").addClass("bordered");}
	else{
		$("#placeDetailName").removeClass("bordered");
	}
	var industryName=$("#industryName").val().trim();
	if(industryName=="") {
		$("#industryName").addClass("bordered");}
	else{
		$("#industryName").removeClass("bordered");
	}	
	var projectContent=$("#projectContent").val().trim();
	if(projectContent=="") {
		$("#projectContent").addClass("bordered");}
	else{
		$("#projectContent").removeClass("bordered");
	}
	var isDeArea=$("#isDeArea").children('option:selected').val().trim();
	var deAreaName=$("#deAreaName").val();
	if(isDeArea==1&&deAreaName==""){
		$("#deAreaName").addClass("bordered");
	}else{
		$("#deAreaName").removeClass("bordered");
	}
	// add by cys,for：提交时，判断输入框不为空
	var theIndustryName=$("#theIndustryName").val().trim();// 国标行业
	if(theIndustryName=="") {
		$("#theIndustryName").addClass("bordered");}
	else{
		$("#theIndustryName").removeClass("bordered");
	}	
	
	
	// 境外投资
	var projectSite=$("#projectSite").val().trim();// 项目所在地
	var chinaTotalMoney=$("#chinaTotalMoney").val().trim();// 中方投资额（万元）
	
	// 外商投资
	var totalMoneyDollar=$("#totalMoneyDollar").val().trim();// 总投资额折合美元(万元)：
	
	var totalMoneyDollarRate=$("#totalMoneyDollarRate").val().trim();// *总投资额使用的汇率（人民币/美元）：
	var projectCapitalMoney=$("#projectCapitalMoney").val();// 项目资本金
	var projectCapitalMoneyDollar=$("#projectCapitalMoneyDollar").val().trim();// 项目资本金折合美元(万元)
	
	var capitalMoneyDollarRate=$("#capitalMoneyDollarRate").val().trim();// 项目资本金使用的汇率（人民币/美元）
	
	var industrialPolicyName=$("#industrialPolicyName").val().trim();// 适用产业政策条目
	
	var otherInvestmentApplyInfo=$("#otherInvestmentApplyInfo").val().trim();// 其他投资方式需予以申报的情况
	
	var builtArea=$("#builtArea").val().trim();// 总建筑面积
	var landArea=$("#builtArea").val().trim();// 总用地面积
	
	var importDeviceNumberMoney=$("#importDeviceNumberMoney").val().trim();// 其中：拟进口设备数量及金额
	
		
	var businessScope=$("#businessScope").val().trim();// 主要经营范围
	
	var investmentModetext=$("#investmentMode").find("option:selected").text().trim();// 投资方式
	var transactionBothInfo=$("#transactionBothInfo").val().trim();// 提供交易双方情况
	var mergerPlan=$("#mergerPlan").val().trim();// 并购安排
	var mergerManagementModeScope=$("#mergerManagementModeScope").val().trim();// 并购后经营方式及范围
	
	var isCountrySecuritytext=$("#isCountrySecurity").find("option:selected").text().trim();//是否涉及国家安全
	var securityApprovalNumber=$("#securityApprovalNumber").val().trim();// 安全审查决定文号

	
	
	if(types=="外商投资"){
		foreignAbroadFlag="1";
		if(totalMoneyDollar==""||!validateY(totalMoneyDollar)) {
			$("#totalMoneyDollar").addClass("bordered");}
		else{
			$("#totalMoneyDollar").removeClass("bordered");
		}
		
		if(totalMoneyDollarRate==""||!validateY(totalMoneyDollarRate)) {
			$("#totalMoneyDollarRate").addClass("bordered");}
		else{
			$("#totalMoneyDollarRate").removeClass("bordered");
		}
		
		if(projectCapitalMoney==""||!validateY(projectCapitalMoney)) {// 项目资本金
			$("#projectCapitalMoney").addClass("bordered");}
		else{
			$("#projectCapitalMoney").removeClass("bordered");
		}
		
		if(projectCapitalMoneyDollar==""||!validateY(projectCapitalMoneyDollar)) {// 项目资本金折合美元(万元)
			$("#projectCapitalMoneyDollar").addClass("bordered");}
		else{
			$("#projectCapitalMoneyDollar").removeClass("bordered");
		}
		
		if(capitalMoneyDollarRate==""||!validateY(capitalMoneyDollarRate)) {// 项目资本金使用的汇率（人民币/美元）
			$("#capitalMoneyDollarRate").addClass("bordered");}
		else{
			$("#capitalMoneyDollarRate").removeClass("bordered");
		}
		
		if(industrialPolicyName=="") {// 适用产业政策条目
			$("#industrialPolicyName").addClass("bordered");}
		else{
			$("#industrialPolicyName").removeClass("bordered");
		}
		if(otherInvestmentApplyInfo=="") {// 其他投资方式需予以申报的情况
			$("#otherInvestmentApplyInfo").addClass("bordered");}
		else{
			$("#otherInvestmentApplyInfo").removeClass("bordered");
		}
		if(builtArea==""||!validateY(builtArea)) {
			$("#builtArea").addClass("bordered");}
		else{
			$("#builtArea").removeClass("bordered");
		}
		if(landArea==""||!validateY(landArea)) {
			$("#landArea").addClass("bordered");}
		else{
			$("#landArea").removeClass("bordered");
		}
		
		if(importDeviceNumberMoney=="") {// 其中：拟进口设备数量及金额
			$("#importDeviceNumberMoney").addClass("bordered");}
		else{
			$("#importDeviceNumberMoney").removeClass("bordered");
		}
		if(investmentModetext=="并购项目"){
			if(transactionBothInfo==""){				
				$("#transactionBothInfo").addClass("bordered");
			}
			else{
			$("#transactionBothInfo").removeClass("bordered");
			}
			if(mergerPlan==""){				
				$("#mergerPlan").addClass("bordered");
			}
			else{
			$("#mergerPlan").removeClass("bordered");
			}
			if(mergerManagementModeScope==""){				
				$("#mergerManagementModeScope").addClass("bordered");
			}
			else{
			$("#mergerManagementModeScope").removeClass("bordered");
			}
			if(transactionBothInfo==""||mergerPlan==""||mergerManagementModeScope==""){
				
				alert("请将必填项填写之后再进行提交。");
				return false;
			}
		}
		if(isCountrySecuritytext=="是"){//是否国家安全			
			if(securityApprovalNumber==""){				
				$("#securityApprovalNumber").addClass("bordered");
			}
			else{
			$("#securityApprovalNumber").removeClass("bordered");
			}
			if(securityApprovalNumber==""){				
				alert("请将必填项填写之后再进行提交。");
				return false;
			}
		}				
		if(totalMoneyDollar==""||!validateY(totalMoneyDollar)||totalMoneyDollarRate==""||!validateY(totalMoneyDollarRate)||
				projectCapitalMoney==""||!validateY(projectCapitalMoney)||projectCapitalMoneyDollar==""||!validateY(projectCapitalMoneyDollar)
				||capitalMoneyDollarRate==""||!validateY(capitalMoneyDollarRate)
			||otherInvestmentApplyInfo==""||builtArea==""||!validateY(landArea)||landArea==""||!validateY(builtArea)||importDeviceNumberMoney==""||theIndustryName==""||contactTel==""||contactFax==""){
			alert("请将必填项填写之后再进行提交。");
			return false;
		}	
		
	}
	else if(types=="境外投资"){
		foreignAbroadFlag="2";
		if(projectSite=="") {
			$("#projectSite").addClass("bordered");}
		else{
			$("#projectSite").removeClass("bordered");
		}
		if(chinaTotalMoney==""||!validateY(chinaTotalMoney)) {// 中方投资额（万元）
			$("#chinaTotalMoney").addClass("bordered");}
		else{
			$("#chinaTotalMoney").removeClass("bordered");
		}
		if(projectSite==""||chinaTotalMoney==""||!validateY(chinaTotalMoney)||theIndustryName==""||contactTel==""||contactFax==""){
				alert("请将必填项填写之后再进行提交。");
				return false;
			}	
	}else{
		foreignAbroadFlag="0";
		if(theIndustryName==""||contactTel==""||contactFax==""){
			alert("请将必填项填写之后再进行提交。");
			return false;
		}		
	}
	// end by cys
	if(divisionCode==""||permitIndustry==""||projectName==""||linkMan == "" ||linkPhone == ""||projectType==""||constructPer==""||endYear==""||investment==""
		||!validateY(investment)||placeCode==""||placeCodeDetail==""||industry==""||applyDate==""||
		(isDeArea==1&&deAreaName=="")){
		alert("请将必填项填写之后再进行提交。");
	}else{
		// add by cys,for:判断有无添加项目基本信息
		var istrueler="0";
		var istruecon="0";
		  for (var i =0; i <lerepInfoJson.length; i++) {
			
			  if(lerepInfoJson[i]!=undefined){
				  var ids=lerepInfoJson[i].id+"";
				  if(ids.indexOf('del') < 0){
					  istrueler="1";
					  break;
				  }			 				     	           
		    }
		  }
		if(istrueler=="0"){
			alert("请先添加项目单位信息");
			return false;
		}
		// add by cys,for:判断有无添加外商基本信息*************************************
		if(types=="外商投资"){
			for (var i = 0; i <contrInfoJson.length; i++) {
				if(contrInfoJson[i]!=undefined){
				var ids=contrInfoJson[i].id+"";
				 if(ids.indexOf('del') < 0){
					 istruecon="1";
					 break;	
				 }
				}
				
		    }
			if(istruecon=="0"){
				alert("请先添加项目外商单位信息");
				return false;
			}
			
		}
		
		// end by cys*******
		submit();
		alert("提交成功！");
	}
}

function submit(){
	var obj = LEx.form.get("sub_form","name");
	obj.divisionCode = formatLen(obj.divisionCode,0,12);
	var dvisioncode = $("#divisionCode").val();
	if(dvisioncode.length!=12){	
		dvisioncode=dvisioncode+"000000";
  }
	obj.placeCode = formatLen(obj.placeCode,0,6);
	obj.startYear = formatLen(obj.startYear,0,4);
	obj.endYear = formatLen(obj.endYear,0,4);
	obj.placeCodeDetail = obj.placeDetailCode;  // 建设地点详情
	obj.placeAreaDetail = obj.placeDetailName;  // 建设地点详情
	// add by cys,for:添加法人信息和境外投资json串,*******************************************
	 /* for (var i =0; i <lerepInfoJson.length; i++) {
		  if(lerepInfoJson[i]!=undefined){
			  if(lerepInfoJson[i].id){        	
		        	 delete lerepInfoJson[i].id;   // 删除json中的id
		        }	        	           
	    }
	  }*/
	obj.lerepInfo=lerepInfoJson;

	// end by
	// cys******************************************************************
	// add by cys,for:外商投资json串,*************************************
	if(types=="外商投资"){
		/*for (var i = 0; i <contrInfoJson.length; i++) {
			 if(contrInfoJson[i]!=undefined){
	        if(contrInfoJson[i].id){        	
	        	 delete contrInfoJson[i].id;   
	        }	
			 }
			
	    }*/
		obj.contrInfo=contrInfoJson;		
	}
	obj.foreignAbroadFlag=foreignAbroadFlag;
	// end by cys*********************************************************
	
	var dvisioncode = $("#divisionCode").val();
	if(dvisioncode.length!=12){	
		dvisioncode=dvisioncode+"000000";
  }
	var command = new LEx.Command("app.icity.ipro.IproCmd");
	command.setParameter("region_code",dvisioncode);
	command.setParameter("region_name",$("#divisionName").val());
	command.setParameter("baseInfo",obj);
	command.setParameter("userInfo",loginInfo);
	command.setParameter("SXBM",code);
	if(neworold=="新建"){
	command.setParameter("SXMC",itemInfo.NAME);
    }
	
	var command3 = new LEx.Command("app.icity.ipro.IproCmd");
	command3.setParameter("code", code);
	var ret3 = command3.execute("searchsblsh");
	if(neworold=="修改"){
		command.setParameter("SXMC",$("#permitItemName").val());
	command.setParameter("oldInvestId",ret3.data[0].SBLSH);
	command.setParameter("oldseqid",code);
	}
	
	var ret = command.execute("saveInvestInfo");
	
	//add by cys
	$("#lerepInfo_form").hide();
	$("#contrInfo_form").hide();
	//end by cys
	$("#user_info_name").html(loginInfo.name);
	$("#sb_item_name").html(obj.projectName);
	
	if(!command.error){	
		// alert(ret.data.error);
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
	
}
function openRegion(mark){
	LEx.dialog({
		id: "selectRegion", title: "选择区划",
		url: LEx.webPath+"ipro/deptTree?region_code="+region_code,
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

//建设地点详情
function openRegionChecks(mark){
	LEx.dialog({
		id: "selectRegion", title: "选择区划",selectType:"checkbox",
		url: LEx.webPath+"ipro/regionTree",
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
		        				 regionName += "," + regions[i].name;
		        				 regionCode += "," + regions[i].id;
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
// 国标行业
function opengmjjhy(mark){
	LEx.dialog({
		id: "selectGmjjhy", title: "选择行业分类",
		url: LEx.webPath+"ipro/gmjjhyTree?type=guobiaohy",
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
// ====================================添加公共方法BEgin==============================================
String.prototype.trim = function () {  
    return this.replace(/\n|\r|\t|\s|\f|\b/g,"");  
}  
// add by cys,for:判断是否为正数
function validateY(num)
{
  var reg = /^\d+(?=\.{0,1}\d+$|$)/
  if(reg.test(num)) return true;
  return false ;  
}
// add by cys,for:所属行业，注册国别地区，适用产业政策条目
function opencyzctm(mark,type,titles){
	
	LEx.dialog({
		id: "selectGmjjhy", title: titles,
		url: LEx.webPath+"ipro/gmjjhyTree?type="+type,
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
		        			/*
							 * id="A"+region.id; var length=id.length;
							 * while(length<6){ id=id+"X"; length=id.length;
							 *  }
							 */
		        		 id=region.id;
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
// ====================================添加公共方法END==============================================
// ---------------------------------操作【非】外资出资信息Begin--------------------------------------------------
var rowCount=2;  // 行数默认2行
var rowCountcontrInfo =2; 

function delRow(rowIndex){  
    $("#option"+rowIndex).remove();  
       
    for (var i = 0; i < lerepInfoJson.length; i++) {
        var cur_person = lerepInfoJson[i];
        if(cur_person!=undefined){
        if(cur_person.id==rowIndex){
        	if(rowIndex>=3){
        		//delete lerepInfoJson[rowIndex-3];// json数组从0开始，所以要减去3
        		var idval="del"+rowIndex;
        		lerepInfoJson[rowIndex-3].id=idval;// json数组从0开始，所以要减去3
        	}
        }
        	
        }         
    }
   // rowCount--;  
}
function btnoklerepIfo(tab){
	var enterpriseName=$("#enterpriseName").val().trim();
	if(enterpriseName=="") {
		$("#enterpriseName").addClass("bordered");}
	else{
		$("#enterpriseName").removeClass("bordered");
	}
	var lerepCerttype=$("#lerepCerttype").find("option:selected").val().trim();
	var lerepCerttypeName=$("#lerepCerttype").find("option:selected").text().trim();
	if(lerepCerttype=="") {
		$("#lerepCerttype").addClass("bordered");}
	else{
		$("#lerepCerttype").removeClass("bordered");
	}	
	var lerepCertno=$("#lerepCertno").val().trim();
	if(lerepCertno=="") {
		$("#lerepCertno").addClass("bordered");}
	else{
		$("#lerepCertno").removeClass("bordered");
	}   
	var contactName=$("#contactName").val().trim();// 联系人名称
	if(contactName=="") {
		$("#contactName").addClass("bordered");}
	else{
		$("#contactName").removeClass("bordered");
	}	
	var contactTel=$("#contactTel").val().trim();// 联系人电话
	if(contactTel=="") {
		$("#contactTel").addClass("bordered");}
	else{
		$("#contactTel").removeClass("bordered");
	}
	

	
	var contactEmail=$("#contactEmail").val().trim();// 联系人邮箱
	var checkEmail=/^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/;
	if(contactEmail==""||!contactEmail.match(checkEmail)) {
		$("#contactEmail").addClass("bordered");}
	else{
		$("#contactEmail").removeClass("bordered");
	}	
	var contactPhone=$("#contactPhone").val().trim();// 联系人手机
	var checkPhone=/^1\d{10}$/;
	if(contactPhone==""||!contactPhone.match(checkPhone)) {
		$("#contactPhone").addClass("bordered");}
	else{
		$("#contactPhone").removeClass("bordered");
	}
	var contactFax=$("#contactFax").val().trim();// 传真
	if(contactFax=="") {
		$("#contactFax").addClass("bordered");}
	else{
		$("#contactFax").removeClass("bordered");
	}
	var correspondenceAddress=$("#correspondenceAddress").val().trim();// 通讯地Hi
	if(correspondenceAddress=="") {
		$("#correspondenceAddress").addClass("bordered");}
	else{
		$("#correspondenceAddress").removeClass("bordered");
	}
	var enterpriseNature=$("#enterpriseNature").find("option:selected").val().trim();// 项目单位性质
	if(enterpriseNature=="") {
		$("#enterpriseNature").addClass("bordered");}
	else{
		$("#enterpriseNature").removeClass("bordered");
	}
	if(types=="外商投资"){
		var enterprisePlace=$("#enterprisePlace").val().trim();// 项目单位注册地址
		var chinaForeignShareRatio=$("#chinaForeignShareRatio").find("option:selected").val().trim();// 项目单位中、外方各股东及持股比例是否与项目资本金相同
		var businessScope=$("#businessScope").val().trim();// 主要经营范围
		if(enterprisePlace=="") {
			$("#enterprisePlace").addClass("bordered");}
		else{
			$("#enterprisePlace").removeClass("bordered");
		}
		
		if(chinaForeignShareRatio=="") {
			$("#chinaForeignShareRatio").addClass("bordered");}
		else{
			$("#chinaForeignShareRatio").removeClass("bordered");
		}
		
		if(businessScope=="") {
			$("#businessScope").addClass("bordered");}
		else{
			$("#businessScope").removeClass("bordered");
		}
		if(enterpriseName==""||lerepCerttype==""||lerepCertno==""||contactName==""
		    /*||contactTel==""||contactEmail==""||contactPhone==""||contactFax==""*/||correspondenceAddress==""
			||enterpriseNature==""
		    /*||!contactEmail.match(checkEmail)||!contactPhone.match(checkPhone)*/
			||enterprisePlace==""||chinaForeignShareRatio==""||businessScope==""){
			alert("请将必填项填写之后再进行提交。");
			return false;
		}
	}else{
		
		if(enterpriseName==""||lerepCerttype==""||lerepCertno==""||contactName==""
			/*||contactTel==""||contactEmail==""||contactPhone==""||contactFax==""*/||correspondenceAddress==""
			||enterpriseNature=="")
			/*||!contactEmail.match(checkEmail)||!contactPhone.match(checkPhone))*/
			{
			alert("请将必填项填写之后再进行提交。");
			return false;
		}
	}
		
	
	var xuhao=$("#"+tab+" tr").length-1;
	rowCount++; 	   
    var trHtml="<tr id='option"+rowCount+"'>"+
				/* "<th>"+xuhao+"</th>"+ */
				"<th>"+enterpriseName+"</th>"+
				"<th>"+lerepCerttypeName+"</th>"+// 项目法人证照类型
				"<th>"+lerepCertno+"</th>"+// 项目法人证照号码
				"<th>"+contactName+"</th>"+
				"<th>"+contactTel+"</th>"+
				"<th>"+enterpriseNature+"</th>"+
				"<th><a onclick=delRow("+rowCount+")>删除</a></th>"+
				"</tr>";
    $("#"+tab).append(trHtml);
    $("#showdiv").hide();
    $("#cover").hide();
   /* addlerepInfoJson= {"id":rowCount,"enterpriseName":enterpriseName,"lerepCerttype":lerepCerttype,
    		"lerepCertno":lerepCertno,"contactName":contactName,"contactTel":contactTel,"contactEmail":contactEmail,
    		"enterprisePlace":enterprisePlace,"enterpriseNature":enterpriseNature,
    		"chinaForeignShareRatio":chinaForeignShareRatio,"businessScope":businessScope,"contactPhone":contactPhone,
    		"contactFax":contactFax,"correspondenceAddress":correspondenceAddress};*/
    addlerepInfoJson={};
    addlerepInfoJson.id = rowCount;
    addlerepInfoJson.enterpriseName = enterpriseName;
    addlerepInfoJson.lerepCerttype = lerepCerttype;
    addlerepInfoJson.lerepCertno = lerepCertno;
    addlerepInfoJson.contactName = contactName;
    addlerepInfoJson.contactTel = contactTel;
    addlerepInfoJson.contactEmail = contactEmail;
    addlerepInfoJson.enterprisePlace = enterprisePlace;
    addlerepInfoJson.enterpriseNature = enterpriseNature;
    addlerepInfoJson.chinaForeignShareRatio = chinaForeignShareRatio;
    
    addlerepInfoJson.businessScope = businessScope;
    addlerepInfoJson.contactPhone = contactPhone;
    addlerepInfoJson.contactFax = contactFax;
    addlerepInfoJson.correspondenceAddress = correspondenceAddress;
     
    lerepInfoJson.push(addlerepInfoJson);
  
    clearlerepInfo();
  }

function addlerepInfo(){	
	$("#cover").show();
	$("#showdiv").show();
}


function clearlerepInfo(){
	
	$("#enterpriseName").val("");
	$("#lerepCertno").val("");
	$("#enterprisePlace").val("");
	$("#businessScope").val("");
	$("#contactName").val("");
	$("#contactTel").val("");
	
	$("#contactEmail").val("");
	$("#contactPhone").val("");
	$("#contactFax").val("");
	$("#correspondenceAddress").val("");
		
}
// 显示外商投资--------------------------END---------------------------------------------------------------
// 显示外商投资*************************操作外资出资信息Begin****************************************************
function selisCountrySecurity(selval){	// 是否涉及国家安全选中事件
	if(selval=="1"){
		$("#tr_securityApprovalNumber").show();
		
	}else{
		
		$("#tr_securityApprovalNumber").hide();
	}
}
function selinvestmentMode(){// 投资方式选中事件
	var seltext=$("#investmentMode").find("option:selected").text();
	if(seltext=="并购项目"){
		$(".bingou").show();// 显示【提供交易双方情况】，【并购安排】，【并购后经营方式及经营范围】
		$("#tr_otherInvestmentApplyInfo").hide();// 隐藏【其他投资方式需予以申报的情况】
		
	}else{		
		$(".bingou").hide();
		$("#tr_otherInvestmentApplyInfo").show();// 显示【其他投资方式需予以申报的情况】
	}
}
function addcontrInfo(){
	$("#cover").show();
	$("#contrInfodiv").show();
}
function delRowcontrInfo(rowIndex){  
    $("#option_con"+rowIndex).remove();  
   
    for (var i = 0; i < contrInfoJson.length; i++) {
        var cur_person = contrInfoJson[i];
        if(cur_person!=undefined){
        if(cur_person.id==rowIndex){
        	if(rowIndex>=3){
        		/*delete contrInfoJson[rowIndex-3];// json数组从0开始，所以要减去3
        		 
*/    var idval="del"+rowIndex;
        		 contrInfoJson[rowIndex-3].id=idval;
        	}
        	
        }  
        }
    }
  //  rowCount--;  
}
// 保存外商投资出资信息
function btnokcontrInfo(tab){
	var businessType=$("#businessType").find("option:selected").val().trim();// 出资类型
	var businessTypeName=$("#businessType").find("option:selected").text().trim();// 出资类型名称
	
	var investmentName=$("#investmentName").val().trim();
	if(investmentName=="") {// 投资者名称
		$("#investmentName").addClass("bordered");}
	else{
		$("#investmentName").removeClass("bordered");
	}
	var regCountryName=$("#regCountryName").val().trim();// 注册国别地区
	var regCountry=$("#regCountry").val();
	if(regCountryName=="") {
		$("#regCountryName").addClass("bordered");}
	else{
		$("#regCountryName").removeClass("bordered");
	}
	var contributionLimit=$("#contributionLimit").val().trim();
	if(contributionLimit==""||!validateY(contributionLimit)) {// 出资额
		$("#contributionLimit").addClass("bordered");}
	else{
		$("#contributionLimit").removeClass("bordered");
	}
	var contributionRatio=$("#contributionRatio").val().trim();
	if(contributionRatio==""||!validateY(contributionRatio)) {// 出资比例
		$("#contributionRatio").addClass("bordered");}
	else{
		$("#contributionRatio").removeClass("bordered");
	}
	
	var contributionMode=$("#contributionMode").find("option:selected").val().trim();// 出资方式
	var contributionModeName=$("#contributionMode").find("option:selected").text().trim();// 出资方式名称
	if(investmentName==""||regCountryName==""||contributionLimit==""||!validateY(contributionLimit)||
			contributionRatio==""||!validateY(contributionRatio)){
		alert("请将必填项填写之后再进行提交。");
		return false;
	}
			
	var xuhao=$("#"+tab+" tr").length-1;
	rowCountcontrInfo++; 	   
    var trHtml="<tr id='option_con"+rowCountcontrInfo+"'>"+				
				"<th>"+businessTypeName+"</th>"+// 出资类型名称
				"<th>"+investmentName+"</th>"+// 投资者名称
				"<th>"+regCountryName+"</th>"+// 注册国别地区
				"<th>"+contributionLimit+"</th>"+// 出资额（万元）
				"<th>"+contributionRatio+"</th>"+// 出资比例
				"<th>"+contributionModeName+"</th>"+// 出资方式
				"<th><a onclick=delRowcontrInfo("+rowCountcontrInfo+")>删除</a></th>"+
				"</tr>";
    $("#"+tab).append(trHtml);
    $("#contrInfodiv").hide();
    $("#cover").hide();
    addcontrInfoJson={};
  
   /* addcontrInfoJson= {"id":rowCountcontrInfo,"businessType":businessType,"investmentName":investmentName,"regCountry":regCountry,
    		"contributionLimit":contributionLimit,"contributionRatio":contributionRatio,"contributionMode":contributionMode};*/
    addcontrInfoJson.id=rowCountcontrInfo;   
    addcontrInfoJson.businessType=businessType;
    addcontrInfoJson.investmentName=investmentName;
    addcontrInfoJson.regCountry=regCountry;  
    addcontrInfoJson.contributionLimit=contributionLimit;
    addcontrInfoJson.contributionRatio=contributionRatio;
    addcontrInfoJson.contributionMode=contributionMode;  
    contrInfoJson.push(addcontrInfoJson);
    
    
    clearcontrInfo();
  }
function clearcontrInfo(){
	
	$("#investmentName").val("");
	$("#regCountryName").val("");
	$("#regCountry").val("");
	$("#contributionLimit").val("");
	$("#contributionRatio").val("");		
}

$(function(){
	$("#showdiv").css('left', (document.body.clientWidth - 600) / 2);
	$("#contrInfodiv").css('left', (document.body.clientWidth - 600) / 2);
	
});
// 显示外商投资*************************操作外资出资信息END****************************************************

