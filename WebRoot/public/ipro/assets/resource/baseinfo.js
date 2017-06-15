var print_type;

var nameList={ 
		"v000":[["请选择"]],
		"v100":[["证件类型"],["证件编号"],["姓名"],["性别"],["联系电话"],["联系地址" ],["籍贯" ],["邮箱" ],["民族" ],["出生日期" ],["学历" ],["政治面貌" ],["国籍" ],["户口所在地" ],["邮编" ]], 
		"v200":[["项目名称" ],["项目编码" ],["项目审批核准备案文号" ],["联系人" ],["联系电话" ],["总投资" ],["总用地面积" ],["建筑面积" ],["项目地址" ],["建设内容" ],["建设类型"],["建设规模"],["重点项目"],["建设性质"],["联系人手机"]],     
		"v300":[["组织机构名称" ],["组织机构代码" ],["组织机构代码证发证日期" ],["组织机构代码发证机构" ],["组织机构代码证有效期起" ],["组织机构代码证有效期止" ],["机构英文名称" ],["组织机构类型" ],["组织机构现状" ],["企业类别代码" ],["企业类别名称" ],["法定代表人" ],["法定代表人类型" ],["法人证件名称" ],["法人证件号码" ],["联系电话" ],["联系人" ],["登记注册日期" ],["单位注册地址" ],["经营(生产)范围(主营）" ]],
		//投资审批项目-start
		"v400":[["项目所属行政区划"],["投资项目行业分类"],["行业核准目录"],["项目名称"],["项目类型"],["建设性质"],["项目（法人）单位"],["项目法人证照类型"],["项目法人证照号码"],["拟开工时间"],["拟建成时间"],["总投资（万元）"],["建设地点"],["建设地点详情"],["所属行业"],["申报日期"],["建设规模及内容"],["联系人名称"],["联系电话"],["联系人邮箱"],["项目阶段"],["是否开发区项目"],["开发区名称"],["项目编码"],["当前时间"]]
		//投资审批--end
		};

var codeList={
		"v000":[["00"]],
		"v100":[["s_identityType"],["s_idcardNo"],["s_name"],["s_sex"],["s_linkPhone"],["s_linkAddress" ],["s_nativePlace"],["s_email"],["s_nation"],["s_birthday"],["s_education" ],["s_politicalStatus" ],["s_country" ],["s_homeAddress" ],["s_postCode" ]], 
		"v200":[["s_projectName"],["s_projectCode"],["s_projectAllowedNo"],["s_linkMan"],["s_linkPhone" ],["s_investment" ],["s_areaAll"],["s_areaBuild"],["s_location" ],["s_projectContent" ],["s_constructType"],["s_scale"],["s_isImportant"],["s_constructPer"],["s_mobilePhone"]], 
		"v300":[["s_orgName" ],["s_orgCode" ],["s_orgCodeAwardDate_str" ],["s_orgCodeAwardOrg" ],["s_orgCodeValidPeriodStart_str" ],["s_orgCodeValidPeriodEnd_str" ],["s_orgEnglishName" ],["s_orgType" ],["s_orgActuality" ],["s_enterpriseSortCode" ],["s_enterpriseSortName" ],["s_legalPerson" ],["s_legalPersonType" ],["s_certificateName" ],["s_certificateNo" ],["s_contactPhone" ],["s_linkMan" ],["s_registerDate_str" ],["s_registerAddress" ],["s_businessScope" ]],
		//投资审批项目-start
		"v400":[["fm_divisionName"],["fm_permitIndustry"],["fm_permitItemCode"],["fm_projectName"],["fm_projectType"],["fm_constructPer"],["fm_contractor"],["fm_lerepCerttype"],["fm_lerepCertno"],["fm_startYear"],["fm_endYear"],["fm_investment"],["fm_placeName"],["fm_placeCodeDetail"],["fm_industry"],["fm_applyDate"],["fm_projectContent"],["fm_linkMan"],["fm_linkPhone"],["fm_contactEmail"],["fm_projectStage"],["fm_isDeArea"],["fm_deAreaName"],["fm_projectCode"],["DateNow"]]
		//投资审批--end
};
function province_change(v){
	
	var entity = document.getElementById("entity");
	entity.innerHTML = "";
	
	if(v != ""){
		eval("var names = nameList.v"+v+";");
		eval("var codes = codeList.v"+v+";");
		for(var i=0;i<names.length;i++){
		entity.add(new Option(names[i],codes[i]));
		}
	}
	
}