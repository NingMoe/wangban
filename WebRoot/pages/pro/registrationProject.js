
$(function(){
	$(".checkit").blur(function(){
		if($(this).val()==""){
			$(this).parent().find("div").html("<font color=\"red\">此项不能为空</font>");
			$(this).parent().find("div").show();
		}		
	}); 
	
	});
function checkprojectContent(){
		if($("#projectContent").val()==""){
			$("#projectContent").parent().find("div").html("<font color=\"red\">此项不能为空</font>");
			$("#projectContent").parent().find("div").show();
			return false;
		}
		if($("#projectContent").val().indexOf("%")>=0){
			$("#projectContent").parent().find("div").html("<font color=\"red\">不允许输入%</font>");
			$("#projectContent").parent().find("div").show();
			return false; 
		}else{
			return true;}
	}
	var checkPhone=/^1\d{10}$/;
	function checkprojectManagerPhone(){
		if($("#projectManagerPhone").val()==""){
			$("#projectManagerPhone").parent().find("div").html("<font color=\"red\">此项不能为空</font>");
			$("#projectManagerPhone").parent().find("div").show();
			return false;
		}
		if(!$("#projectManagerPhone").val().match(checkPhone)) {
			$("#projectManagerPhone").parent().find("div").html("<font color=\"red\">手机格式错误</font>");
			$("#projectManagerPhone").parent().find("div").show();
			return false;
		}else{
			return true;}
	}
	function checklinkPhone(){
		if(!$("#linkPhone").val().match(checkPhone)) {
			$("#linkPhone").parent().find("div").html("<font color=\"red\">手机格式错误</font>");
			$("#linkPhone").parent().find("div").show();
			return false;
		}else{
			return true;}
	}
	var checkEmail=/^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/;
	function checkprojectManagerEmail(){
		
		if($("#projectManagerEmail").val() != ""){
			if(!$("#projectManagerEmail").val().match(checkEmail)) {
				$("#projectManagerEmail").parent().find("div").html("<font color=\"red\">邮箱格式错误</font>");
				$("#projectManagerEmail").parent().find("div").show();
				return false;
			}else{
				return true;
			}
		}
		
		return true;
	}
	function checkcontactEmail(){
		
		if($("#contactEmail").val() != ""){
			if(!$("#contactEmail").val().match(checkEmail)) {
				$("#contactEmail").parent().find("div").html("<font color=\"red\">邮箱格式错误</font>");
				$("#contactEmail").parent().find("div").show();
				return false;
			}else{
				return true;
			}
		}
		
		return true;
	}
	var checkYear=/^[1-9]\d{3}$/;
	function checkstartYear(){
		if($("#startYear").val()==""){
			$("#startYear").parent().find("div").html("<font color=\"red\">此项不能为空</font>");
			$("#startYear").parent().find("div").show();
			 return false;
		}else{
			return true;
		}
		/*if(!$("#startYear").val().match(checkYear)) {
			$("#startYear").parent().find("div").html("<font color=\"red\">请填写正确年份</font>");
			$("#startYear").parent().find("div").show();
			return false;
		}else{
			return true;
		}*/
	}
	function checkendYear(){
		if($("#endYear").val()==""){
			$("#endYear").parent().find("div").html("<font color=\"red\">此项不能为空</font>");
			$("#endYear").parent().find("div").show();
			return false;
		}else{
			return true;
		}
		/*if(!$("#endYear").val().match(checkYear)) {
			$("#endYear").parent().find("div").html("<font color=\"red\">请填写正确年份</font>");
			$("#endYear").parent().find("div").show();
			return false;
		}else{
			return true;
		}*/
	}
	function checkinvestment(){
		if($("#investment").val()==""){
			$("#investment").parent().find("div").html("<font color=\"red\">此项不能为空</font>");
			$("#investment").parent().find("div").show();
			return false;
		}
		var investment=$("#investment").val();
		var inde=investment.indexOf(".");
		if(inde>-1){
			if(inde>8){
				$("#investment").parent().find("div").html("<font color=\"red\">小数点前不能超过八位</font>");
				$("#investment").parent().find("div").show();
				return false;
			}
			if(investment.length-inde-1>6){
				$("#investment").parent().find("div").html("<font color=\"red\">小数点后不能超过六位</font>");
				$("#investment").parent().find("div").show();
				return false;
			}else{
				return true;
			}
		}else{
			if(investment.length>8){
				$("#investment").parent().find("div").html("<font color=\"red\">正整数不能超过八位</font>");
				$("#investment").parent().find("div").show();
				return false;
			}else{
				return true;
			}
		}
	}
	function checkindustry(){
		if($("#industry").attr("code").length!=5){
			$("#industry").parent().find("div").html("<font color=\"red\">请填写到分类的最后一级</font>");
			$("#industry").parent().find("div").show();
			return false;
		}else{
			return true;}
	}
	function checktheindustry(){
		if($("#theIndustry").attr("code").length!=8){
			$("#theIndustry").parent().find("div").html("<font color=\"red\">请填写到分类的最后一级</font>");
			$("#theIndustry").parent().find("div").show();
			return false;
		}else{
			return true;}
	}
	$("input[name='isSubProject']").change(function(){
		if($("input[name='isSubProject']:checked").val()=="1"){
			$("#zxmbmtr").show();
			/*$("#_sfzxm").attr("colspan","1");
			$("#zxmbm").show();
			$("#_zxmbm").show();*/
		}else{
			$("#zxmbmtr").hide();
			/*$("#_sfzxm").attr("colspan","3");
			$("#zxmbm").hide();
			$("#_zxmbm").hide();*/
		}
	});
	$("#isDeArea").change(function(){
		if($("#isDeArea option:selected").val()=="1"){
			$("#kfqmc").show();
		}else{
			$("#kfqmc").hide();
		}
	});
	/*$("#permitIndustry").change(function(){
		initpermitItemCode($("#permitIndustry option:selected").val());
	});*/
	$(".checkit").focus(function(){		
			$(this).parent().find("div").hide();		
	});
	$("#projectType").change(function(){
		if($("#projectType option:selected").val()=="A00002"){
			$("#tzxmhyfl").show();
			$("#hyhzml").show();
		}else{
			$("#tzxmhyfl").hide();
			$("#hyhzml").hide();
		}
	});

$(function(){
	initDict();	
	addOption("projectType",TZSPXMLX);//加载类别
	addOption("constructPer",TZSPJSXZ);//加载建设性质
	addOption("provinceCode",TZSPSJBM);//加载省
	addOption("cityCode",TZSPSBM);//加载市
	addOption("countyCode",TZSPXJBM);//加载县
	addOption("industry",TZXMLX);//加载所属行业
	addOption("lerepCerttype",ZSTZXMFRZZLX);//加载项目法人证照类型	
	//addOption("permitIndustry",TZXMHYFL);//投资项目行业分类
	$("#projectStage").parent().parent().show();
	if(LEx.urldata.type=="sp"){
		addOption("projectStage",TZXMSBXMJD);
	}else if(LEx.urldata.type=="hz"){
		addOption("projectStage",TZXMSBHZJD);
	}else if(LEx.urldata.type=="ba"){
		addOption("projectStage",TZXMSBBAJD);
	}
	var type=LEx.urldata.type;
	$("#projectType").attr("disabled","false");
	if(type=="sp"){
		$("#projectType").val("A00001");
	}else if(type=="hz"){
		$("#tzxmhyfl").show();
		$("#hyhzml").show();
		$("#projectType").val("A00002");
	}else if(type=="ba"){
		$("#projectType").val("A00003");
	}
	
	initpermitItemCode($("#permitIndustry").attr("code"));
});
//加载行业核准目录
function initpermitItemCode(code){
	var cmd = new LEx.Command("app.icity.pro.ProCmd");
	cmd.setParameter("regionCode", $("#regionCode").attr("code"));
	cmd.setParameter("permitIndustry", code);
	cmd.setParameter("projectType", $("#projectType option:selected").val());
	var ret = cmd.execute("getCatalogInfo");
	if(!ret.error){
		$("#permitItemCode").empty();
		addOption("permitItemCode",ret.data);
	}else{
		LEx.alert(ret.error);
	}
}
//提交注册
function checkit(){
	if (!checkValidate()) {
		errorDialog("系统提示", "请填写正确的信息！");
		return;
	}
	if(
	!(checkstartYear()&&checkendYear()&&checkinvestment()
			&&checkprojectContent()&&checkprojectManagerPhone()&&
			checklinkPhone()&&checkindustry()&&
			checkprojectManagerEmail()&&
			checkcontactEmail())){
		return;
	}
	if($("#isCommitment").attr("checked")!="checked"){
		alert("是否承诺填写数据的真实性");
		return;
	}
	if(!bool){
		return;
	}
	var project = {};
	project = LEx.form.get("project_body","name");
	project.permitIndustry = $("#permitIndustry").attr("code");
	project.projectType = $("#projectType option:selected").val();//"项目类型：A00001:审批 A00002:核准 A00003:备案"
	project.regionCode = $("#regionCode").attr("code");//"区划代码"
	project.regionName = $("#regionCode").attr("city");//"区划名称"
	project.industry = $("#industry").attr("code");//国标行业编码
	project.industryName = $("#industry").val();//国标行业名称
	project.theIndustry = $("#theIndustry").attr("code");//所属行业编码
	project.theIndustryName = $("#theIndustry").val();//所属行业名称
	project.projectStage = $("#projectStage option:selected").val()==undefined?"":$("#projectStage option:selected").val();
	
	var dcCompany = {};
	dcCompany = LEx.form.get("dcCompany_body","name");	
			
    var contectInformation = {};
    contectInformation = LEx.form.get("contectInformation_body","name");
	
	var command = new LEx.Command("app.icity.pro.ProCmd");
	command.setParameter("project", project);
	command.setParameter("dcCompany", dcCompany);
	command.setParameter("contectInformation", contectInformation);
	var ret = command.execute("submitProjectMessage");
	if(!command.error){	
		save(ret.data.projectId,project,dcCompany,contectInformation);
	}
}
function save(seqid,project,dcCompany,contectInformation){
	var content={};
	content.project=project;
	content.dcCompany=dcCompany;
	content.contectInformation=contectInformation;
	
	var _project = clone(project);
	var _dcCompany = clone(dcCompany);
	
	var radPlace = document.getElementsByName("radPlace"); 
	var placeCode;
	for(var i=0;i<radPlace.length;i++){ 
		if(radPlace[i].checked) { 
			placeCode = radPlace[i].value; 
		}
	}
	
	if(placeCode == "no"){
		placeCode = $("#countyCode").val();
		
	}
	_project.placeCode = placeCode;
	_project.projectType = $("#projectType option:selected").html();
	_project.projectCategory = _project.projectCategory=="0"?"基本建设":"技术改造";//"项目类别：0:基本建设 1：技术改造"
	_project.isSubProject = _project.isSubProject=="0"?"不是":"是";//"是否子项目 0：不是，1是"
	_project.constructPer = selectDict(TZSPJSXZ,_project.constructPer);//"建设性质0:新建1：扩建 2：迁建3：改建”
	_project.provinceCode = selectDict(TZSPSJBM,_project.provinceCode);//省
	_project.cityCode = selectDict(TZSPSBM,_project.cityCode);//市
	_project.countyCode = selectDict(TZSPXJBM,_project.countyCode);//县
	_project.industry = $("#industry").val();//"所属行业-国家统计局国民经济行业分类"
	_project.permitIndustry = $("#permitIndustry").val();//"投资项目行业分类"
	_project.isDeArea = _project.isDeArea=="0"?"非开发区项目":"开发区项目";//"是否开发区项目-标记项目是否为开发区项目，1代表开发区项目；0或者不填写代表非开发区项目。系统默认值为‘0’"
	_project.isApplyAgentService = _project.isApplyAgentService=="0"?"不申请":"申请";//"是否申请审批代理服务0：不申请，1申请"
	_project.isCommitment = _project.isCommitment=="0"?"不承诺":"承诺";//"是否承诺填写数据的真实性1：承诺，0不承诺"  必须选择1
	_project.permitItemCode = $("#permitItemCode option:selected").html();
	_project.projectStage = $("#projectStage option:selected").html()==undefined?"":$("#projectStage option:selected").html();//项目阶段
	_dcCompany.lerepCerttype = $("#lerepCerttype option:selected").html();//“项目法人证照类型”
	var printcontent={};
	printcontent.project=_project;
	printcontent.dcCompany=_dcCompany;
	printcontent.contectInformation=contectInformation;
	var command = new LEx.Command("app.icity.pro.ProCmd");
	command.setParameter("projectname", $("#projectName").val());
	command.setParameter("projecttype", $("#projectType option:selected").val());
	command.setParameter("seqid", seqid);
	command.setParameter("content", content);
	command.setParameter("printcontent", printcontent);
	var ret = command.execute("submitProjectMessageSave");
	if(!command.error){	
		if(ret.state==1){
			alert("项目申报提交成功！");
		}else{
			alert("项目申报提交失败！");
		}		 
	}else{
		alert("项目申报提交失败！");
	}
	this.close();
}
function selectDict(dict,key){
	var len = dict.length;
	for(var i=0;i<len;i++){
		if(dict[i].code == key ){
			return dict[i].name;
		}
	}
}

//国标行业
function selectIndustry(){	
	LEx.dialog({
		id: "selectIndutry", 
		title: "选择国标行业",
		url: LEx.webPath+"pro/industryTree",
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
		        	 var industry = {};
		        	 if((industry=iframe.getData())){		        		
		        		 if(industry.id){
		        			$("#industry").attr("code",industry.id);
		        			$("#industry").val(industry.name);
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
//所属行业
function selectNewIndustry(){	
	LEx.dialog({
		id: "selectNewIndutry", 
		title: "选择所属行业",
		url: LEx.webPath+"pro/newIndustryTree",
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
		        	 var industry = {};
		        	 if((industry=iframe.getData())){		        		
		        		 if(industry.id){
		        			$("#theIndustry").attr("pcode",industry.pId);
		        			$("#theIndustry").attr("code",industry.id);
		        			$("#theIndustry").val(industry.name);
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
function selectProject(){	
	LEx.dialog({
		id: "selectProject", 
		title: "选择所属项目",
		url: LEx.webPath+"pro/projectTree",
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
		        		 var project = {};
		        		 if((project=iframe.getData())){		        		
		        			 if(project.id){
		        				 $("#mainProjectCode").val(project.id);
		        				 $("#mainProjectName").val(project.name);
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
function selectpermitIndustry(){	
	LEx.dialog({
		id: "selectpermitIndustry",
		title: "选择投资项目行业分类",
		url: LEx.webPath+"pro/permitIndustryTree",
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
		        	 var permitIndustry = {};
		        	 if((permitIndustry=iframe.getData())){		        		
		        		 if(permitIndustry.id){
		        			$("#permitIndustry").attr("code",permitIndustry.id);
		        			$("#permitIndustry").val(permitIndustry.name);
		        			
		        			initpermitItemCode($("#permitIndustry").attr("code"));
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
function checkValidate(){
	bool=true;
	$(".checkit").each(function(){
		if($(this).val()==""){
			$(this).parent().find("div").html("<font color=\"red\">此项不能为空</font>");
			$(this).parent().find("div").show();
			 bool=false;
		}
	});
	return bool;
	/*if($("#"+$("input").attr("id")).val()==""){
		$(this).parent().find("div").html("<font color=\"red\">此项不能为空</font>");
		$(this).parent().find("div").show();
		return false;
}*/
}

function ayncTree(obj)
{
    $t = $(obj).parent().parent().parent();
    initTree();
    var elem = document.getElementById("ztree");
    var d = dialog(
    {
        title: '选择省内区划',
        content: elem,
        quickClose: true,
        width: 350,
        height: 380,
        button: [
        {
            value: '选择',
            callback: function()
            {
                var regions = [];
                debugger;
                if ((regions = getDatas()))
                {
                    if (regions.length > 0)
                    {
                        var regionName = "";
                        var regionCode = "";
                        for (var i = 0; i <
                            regions.length; i++
                        )
                        {
                            regionName += "," +
                                regions[i].NAME;
                            regionCode += "," +
                                regions[i].CODE;
                        }
                        regionName = regionName
                            .substr(1);
                        regionCode = regionCode
                            .substr(1);
                        $t.find(
                            "#province"
                        ).val(regionName);
                        $t.find(
                            "#sn"
                        ).val(regionCode);
                    }
                    d.close().remove();
                }
                else
                {
                    d.close().remove();
                }
            },
            autofocus: true
        },
        {
            value: '关闭',
            callback: function()
            {
                d.close().remove();
            }
        }]
    });
    d.show();
}
function clearSn(obj){
	var id =$(obj).attr("id");
	$("#province").val($("label[for='"+id+"']").text());
}