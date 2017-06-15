/**
 * 基于L5的日期时间框
 * 
 * @param dateTime
 */
function CFormDateTime(dateTime) {
	var format = $(dateTime).attr("format");
	if (!$.trim(format)) {
		$(dateTime).attr("format", "Y-m-d H:i:s");
	}

	LoushangDatetime(dateTime);
}

/**
 * 基于L5的日期框
 * 
 * @param date
 */
function CFormDate(date) {
	var format = $(date).attr("format");
	if (!$.trim(format)) {
		$(date).attr("format", "Y-m-d");
	}

	LoushangDate(date);
}

/**
 * 组织通用帮助
 * @param {String} selectType 选择类型
 * @param {String} isUseDataPermit 是否启用数据权限控制
 */
function selectOrgan(selectType, isUseDataPermit) {
	var L5_webPath = L5.webPath + "/jsp/public/help/help.jsp?rootId=1";
	var url = "";
	// 返回值
	var rtnObj = {
		organId : [],
		organName : []
	};
	var currentLoginUserId = '';
	
	switch (selectType) {
	//选择人员(单选)
	case "80":
		if (isUseDataPermit == "1") {
			//进行数据权限控制
			url = L5_webPath
					+ "&helpCode=bsp_organhelp&rootId=rootId&organType=8&isCheckBox=0&returnValueType=array&userId="
					+ currentLoginUserId;
		} else {
			//不进行数据权限控制
			url = L5_webPath
					+ "&helpCode=bsp_organhelp&rootId=rootId&organType=8&isCheckBox=0&returnValueType=array";
		}
		var struData = showModalDialog(url, null,
				"scroll:yes;status:no;dialogWidth:500px;dialogHeight:550px");
		if (!struData) {
			return;
		}
		rtnObj.organId.push(struData[0][0]);
		rtnObj.organName.push(struData[0][1]);
		break;
	//选择人员(复选)
	case "81":
		if (isUseDataPermit == "1") {
			//进行数据权限控制
			url = L5_webPath
					+ "&helpCode=bsp_organhelp&rootId=rootId&organType=8&isCheckBox=1&returnValueType=array&userId="
					+ currentLoginUserId;
		} else {
			//不进行数据权限控制
			url = L5_webPath
					+ "&helpCode=bsp_organhelp&rootId=rootId&organType=8&isCheckBox=1&returnValueType=array";
		}

		var struData = showModalDialog(url, null,
				"scroll:yes;status:no;dialogWidth:500px;dialogHeight:550px");
		if (!struData) {
			return;
		}
		for ( var i = 0; i < struData.length; i++) {
			rtnObj.organId.push(struData[i][0]);
			rtnObj.organName.push(struData[i][1]);
		}
		break;
	//选择部门(单选)
	case "60":

		if (isUseDataPermit == "1") {
			//进行数据权限控制
			url = L5_webPath
					+ "&helpCode=bsp_organhelp&rootId=rootId&organType=1,2&showOrganType=1,2&isCheckBox=0&returnValueType=array&userId="
					+ currentLoginUserId;
		} else {
			//不进行数据权限控制
			url = L5_webPath
					+ "&helpCode=bsp_organhelp&rootId=rootId&organType=1,2&showOrganType=1,2&isCheckBox=0&returnValueType=array";
		}

		var struData = showModalDialog(url, null,
				"scroll:yes;status:no;dialogWidth:500px;dialogHeight:550px");
		if (!struData) {
			return;
		}

		rtnObj.organId.push(struData[0][0]);
		rtnObj.organName.push(struData[0][1]);
		break;
	//选择部门(复选)
	case "61":

		if (isUseDataPermit == "1") {
			//进行数据权限控制
			url = L5_webPath
					+ "&helpCode=bsp_organhelp&rootId=rootId&organType=1,2&showOrganType=1,2&isCheckBox=1&returnValueType=array&userId="
					+ currentLoginUserId;
		} else {
			//不进行数据权限控制
			url = L5_webPath
					+ "&helpCode=bsp_organhelp&rootId=rootId&organType=1,2&showOrganType=1,2&isCheckBox=1&returnValueType=array";
		}

		var struData = showModalDialog(url, null,
				"scroll:yes;status:no;dialogWidth:500px;dialogHeight:550px");
		if (!struData) {
			return;
		}
		for ( var i = 0; i < struData.length; i++) {
			rtnObj.organId.push(struData[i][0]);
			rtnObj.organName.push(struData[i][1]);
		}
		break;
	}

	return rtnObj;
}