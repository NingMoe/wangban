var json = {};
var data={};
var orgArray={};
$(function(){
	var data = LEx.urldata;	
	var formid = "";
    var _dataId = null;    //表单数据id,null表示表单数据未提交。
    var obj=null;    //表单对象。
    var qymc = null;
    initDict();
    getApplicationInfo();
		//如查状态为12，隐藏暂存按钮		
    var listsp="";
    var listhz="";
    var listba="";
		for(var i=0;i<SPSECTYPE.length;i++){
			if(i<SPSECTYPE.length-1){
			listsp+=SPSECTYPE[i].code+","+SPSECTYPE[i].name+",";}
			else{
				listsp+=SPSECTYPE[i].code+","+SPSECTYPE[i].name;}
			}
		for(var i=0;i<HZSECTYPE.length;i++){
			if(i<HZSECTYPE.length-1){
			listhz+=HZSECTYPE[i].code+","+HZSECTYPE[i].name+",";}
			else{
				listhz+=HZSECTYPE[i].code+","+HZSECTYPE[i].name;}
			}
		for(var i=0;i<BASECTYPE.length;i++){
			if(i<BASECTYPE.length-1){
			listba+=BASECTYPE[i].code+","+BASECTYPE[i].name+",";}
			else{
				listba+=BASECTYPE[i].code+","+BASECTYPE[i].name;}
			}
			if(data.state=="12")
			{
			   $("#save").remove();
			}
			$("#formiframe").attr("src","{{PageContext.ContextPath}}icity/submitsp/view?formId="
						 	+ data.formId 
						 	+ "&flowId=" + data.flowId+ "&num=" + data.num+"&dataId="+data.dataId+"&JRType="+data.JRType+"&SPSECTYPE="+listsp+"&HZSECTYPE"+listhz+"&BASECTYPE"+listba); 
			var obj = parent.frames["formiframe"].window;
			$("#form").show();
			$(".next").val("下一步");
		
	

	$("input[name='isSubProject']").change(function(){
		if($("input[name='isSubProject']:checked").val()=="0"){
			$("#_sfzxm").attr("colspan","1");
			$("#zxmbm").show();
			$("#_zxmbm").show();
		}else{
			$("#_sfzxm").attr("colspan","3");
			$("#zxmbm").hide();
			$("#_zxmbm").hide();
		}
	});
	$("#isDeArea").change(function(){
		if($("#isDeArea option:selected").val()=="1"){
			$("#kfqmc").show();
		}else{
			$("#kfqmc").hide();
		}
	});
	$("#permitIndustry").change(function(){
		initpermitItemCode($("#permitIndustry option:selected").val());
	});
	$(".checkit").focus(function(){		
			$(this).parent().find("div").hide();		
	});
	$("#projectType").change(function(){
		if($("#projectType option:selected").val()=="A00002"){
			
			initpermitItemCode($("#permitIndustry option:selected").val());
			
			$("#hyhzml").show();
		}else{
			$("#hyhzml").hide();
		}
	});
});
function getApplicationInfo(){
	
	
	data.formId = "TouZiZiXunFuWu";
}

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
function submitZx(){
	formiframe.window.bSave();
	var obj = parent.frames["formiframe"].window;
	_dataId = obj.document.getElementById("dataid").value;
	if(obj.document.getElementById("consultSubject").value==""){
		alert("值不能空");
		return;
	}
		buildData();
	var command = new LEx.Command("app.icity.pro.ProCmd");
	command.setParameter("consultSubject", json.consultSubject);
	command.setParameter("projectCode", json.projectCode);
	command.setParameter("projectName", json.projectName);
	command.setParameter("constructPer", json.constructPer);
	command.setParameter("investment", json.investment);
	command.setParameter("placeAreaDetail", json.placeAreaDetail);
	command.setParameter("projectContent", json.projectContent);
	command.setParameter("contractor", json.contractor);
	command.setParameter("lerepNo", json.lerepNo);
	command.setParameter("projectManagerName", json.projectManagerName);
	command.setParameter("projectManagerPhone", json.projectManagerPhone);
	command.setParameter("linkMan", json.linkMan);
	command.setParameter("linkPhone", json.linkPhone);
	command.setParameter("Content", json.Content);
	
	command.setParameter("formId", data.formId);
	command.setParameter("dataId", json.dataId);
	command.setParameter("orgArray",json.orgArray);
	command.setParameter("investType",json.investType);
	
	var ret = command.execute("submitConsult");
	if(!command.error){	
		if(ret.state==1){
			LEx.alert("申请成功");
			window.location.href="{{cp}}public/index";
		}else{
			LEx.alert("申请失败");
		}		 
	}else{
		LEx.alert("申请失败");
	}
	
}

function buildData(){
	formInfo=LEx.form.get("guide-top","id");
	obj = document.getElementById("formiframe").contentWindow.window;
	json.info = formInfo;
	json.projectCode = obj.document.getElementById("projectCode").value;
	json.projectName = obj.document.getElementById("projectName").value;
	json.consultSubject = obj.document.getElementById("consultSubject").value;
	json.constructPer=obj.document.getElementById("constructPer").value;
	json.investType = obj.document.getElementById("investType").value;
	json.investment = obj.document.getElementById("investment").value;
	json.placeAreaDetail = obj.document.getElementById("placeAreaDetail").value;
	json.projectContent = obj.document.getElementById("projectContent").value;
	json.contractor = obj.document.getElementById("contractor").value;
	json.lerepNo = obj.document.getElementById("lerepNo").value;
	json.projectManagerName = obj.document.getElementById("projectManagerName").value;
	json.linkMan = obj.document.getElementById("linkMan").value;
	json.projectManagerPhone = obj.document.getElementById("projectManagerPhone").value;
	json.linkPhone = obj.document.getElementById("linkPhone").value;
	json.Content = obj.document.getElementById("Content").value;
	json.dataId=_dataId;
	orgArray.buMen=obj.document.getElementById("buMen").value;
	orgArray.orgCode=obj.document.getElementById("orgCode").value;
	json.orgArray=orgArray;
	
	if(json.consultSubject==null){
		json.consultSubject="";
	}
	if(json.projectCode==null){
		json.projectCode="";
	}
	if(json.projectName==null){
		json.projectName=""; 		
	}
	if(json.investment==null){
		json.investment=""; 		
	}
	if(json.placeAreaDetail==null){
		json.placeAreaDetail=""; 		
	}
	if(json.projectContent==null){
		json.projectContent=""; 		
	}
	if(json.contractor==null){
		json.contractor=""; 		
	}
	if(json.lerepNo==null){
		json.lerepNo=""; 		
	}
	if(json.projectManagerName==null){
		json.projectManagerName=""; 		
	}
	if(json.projectManagerPhone==null){
		json.projectManagerPhone=""; 		
	}
	if(json.linkMan==null){
		json.linkMan=""; 		
	}
	if(json.linkPhone==null){
		json.linkPhone=""; 		
	}
	if(json.Content==null){
		json.Content=""; 		
	}
	
}
function save(seqid,project,dcCompany,contectInformation){
	var content={};
	content.project=project;
	content.dcCompany=dcCompany;
	content.contectInformation=contectInformation;
	
	var _project = clone(project);
	var _dcCompany = clone(dcCompany);
	_project.projectType = $("#projectType option:selected").html();
	_project.projectCategory = _project.projectCategory=="0"?"基本建设":"技术改造";//"项目类别：0:基本建设 1：技术改造"
	_project.isSubProject = _project.isSubProject=="0"?"不是":"是";//"是否子项目 0：不是，1是"
	_project.constructPer = selectDict(TZSPJSXZ,_project.constructPer);//"建设性质0:新建1：扩建 2：迁建3：改建”
	_project.provinceCode = selectDict(TZSPSJBM,_project.provinceCode);//省
	_project.cityCode = selectDict(TZSPSBM,_project.cityCode);//市
	_project.countyCode = selectDict(TZSPXJBM,_project.countyCode);//县
	_project.industry = $("#industry").val();//"所属行业-国家统计局国民经济行业分类"
	_project.permitIndustry = selectDict(TZXMHYFL,_project.permitIndustry);//"投资项目行业分类"
	_project.isDeArea = _project.isDeArea=="0"?"非开发区项目":"开发区项目";//"是否开发区项目-标记项目是否为开发区项目，1代表开发区项目；0或者不填写代表非开发区项目。系统默认值为‘0’"
	_project.isApplyAgentService = _project.isApplyAgentService=="0"?"不申请":"申请";//"是否申请审批代理服务0：不申请，1申请"
	_project.isCommitment = _project.isCommitment=="0"?"不承诺":"承诺";//"是否承诺填写数据的真实性1：承诺，0不承诺"  必须选择1
	_project.permitItemCode = $("#permitItemCode option:selected").html();
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
function selectIndustry(){	
	LEx.dialog({
		id: "selectIndutry", 
		title: "选择所属行业",
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