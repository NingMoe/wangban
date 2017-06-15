/**
*事项业务状态表
*分组 -> 大类状态 -> 小类状态
*根据下面的方法获取业务状态的转换
*如果关系有变化，在子版本中的plus.js里定义关系
*如：LEx.icityBusiness.statusCategories = [新的关系];
*这里默认深圳版本的关系
*注：如果【小类状态（statusList）】没有变化，不要重新定义
**/
LEx.icityBusiness = {
	defaultValue:"办理中",
	statusList : [//小类状态
		{name:"暂存",value:"00"},
		{name:"已撤回",value:"03"},
		{name:"已提交",value:"11"},//待预审
		{name:"预审通过",value:"16"},//舟山预审通过
		{name:"办理中",value:"01"},//预审时，受理，或者补齐补正后，状态为办理中
		{name:"不予受理",value:"02"},//预审时，不予受理， 最终态 ，处于此状态的业务为办结业务
		{name:"已驳回",value:"14"},//预审时，驳回
		{name:"补齐补正",value:"21"},//预审时，补交告知
		{name:"作废",value:"13"},//作废 ,预审时由办理单位主动将业务进行作废处理最终态 处于此状态的业务为办结业务
		{name:"退件",value:"96"},//退件 ,由办理单位主动将补齐超时的业务退件 处于此状态的业务为办结业务
		{name:"作废",value:"97"},//作废 ,由办理单位主动将业务进行作废处理最终态 处于此状态的业务为办结业务
		{name:"不予许可",value:"98"},
		{name:"准予许可",value:"99"}
	],
	statusCategories : [//大类状态
		  {id:"wtj",name:"暂存",value:"00"},//未提交
		  {id:"ych",name:"已撤回",value:"03"},
		  {id:"ytj",name:"已提交",value:"11"},//已提交
		  {id:"bqbz",name:"补齐补正",value:"21"},//已受理
		  {id:"ysl",name:"办理中",value:"01,16"},//已受理
		  {id:"blcg",name:"办理成功",value:"99"},
		  {id:"blsb",name:"办理失败",value:"02,13,14,96,97,98"}
	],
	statusGroups : [//分组状态
		{name:"已提交",id:"unProcess",value:"ytj"},
		{name:"办理中",id:"processing",value:"ysl"},
		{name:"已办结",id:"processed",value:"blcg,blsb"}
	],
	//根据小类value获取小类名称
	formatStatus : function(val){
		var len = this.statusList.length;
		var result = this.defaultValue;
		if(LEx.isNotNull(val)){
			val=val.replace(/(^\s+|\s+$)/g,"");   // 过滤空格
			for(var i=0;i<len;i++){
				if(val == this.statusList[i].value){
					result = this.statusList[i].name
					break;
				}
			}
		}
		return result;
	},
	//根据大类value获取大类名称
	formatCategory:function(val){
		var len = this.statusCategories.length;
		var result = this.defaultValue;
		if(LEx.isNotNull(val)){
			val=val.replace(/(^\s+|\s+$)/g,"");   // 过滤空格
			for(var i=0;i<len;i++){
				if(val == this.statusCategories[i].id){
					result = this.statusCategories[i].name
					break;
				}
			}
		}
		return result;
	},
	//根据分组value获取分组名称
	formatGroup:function(val){
		var len = this.statusGroups.length;
		var result = this.defaultValue;
		if(LEx.isNotNull(val)){
			val=val.replace(/(^\s+|\s+$)/g,"");   // 过滤空格
			for(var i=0;i<len;i++){
				if(val == this.statusGroups[i].id){
					result = this.statusGroups[i].name
					break;
				}
			}
		}
		return result;
	},
	getIdFromValuesByVal:function(val,queryList){
		var len = queryList.length;
		var result = '';
		var statusStr = '';
		if(LEx.isNotNull(val)){
			val=val.replace(/(^\s+|\s+$)/g,"");   // 过滤空格
			for(var i=0;i<len;i++){
				if(LEx.isNotNull(result)){
					break;
				}
				statusStr = queryList[i].value;
				var tempArr = new Array();
				tempArr = statusStr.split(",");
				for(var j=0;j<tempArr.length;j++){
					if(val == tempArr[j]){
						result = queryList[i].id;
						break;
					}
				}
			}
		}
		if(!LEx.isNotNull(result)){
			result = this.defaultValue;
		}
		return result;
	},
	//在queryList中，根据id获取value字符串
	getValuesById:function(queryList,val){
		var len = queryList.length;
		var result = '';
		if(LEx.isNotNull(val)){
			val=val.replace(/(^\s+|\s+$)/g,"");   // 过滤空格
			for(var i=0;i<len;i++){
				if(val == queryList[i].id){
					result = queryList[i].value;
					break;
				}
			}
		}

		if(!LEx.isNotNull(result)){
			result = this.defaultValue;
		}
		return result;
	},
	//根据大类状态id获取其下的小类value，并将以单引号和分号分开，否则返回默认值
	getStatusStrByCategory:function(val){
		var len = '';
		var result = '';
		var statusStr = '';

		if(LEx.isNotNull(val)){
			statusStr = this.getValuesById(this.statusCategories,val);
			var tempArr = new Array();
			tempArr = statusStr.split(",");
			len = tempArr.length;
			for(var i=0;i<len;i++){
				result += "" + tempArr[i] + ",";
			}
			result = result.substr(0,result.length-1);
		}

		if(!LEx.isNotNull(result)){
			result = this.defaultValue;
		}
		return result;
	},
	//根据分组状态id获取其下的小类value，并将以单引号和分号分开，否则返回默认值
	getStatusStrByGroup:function(val){
		var len = '';
		var result = '';
		var categoryStr = '';
		if(LEx.isNotNull(val)){
			categoryStr = this.getValuesById(this.statusGroups,val);
			var tempArr = new Array();
			tempArr = categoryStr.split(",");
			len = tempArr.length;
			for(var i=0;i<len;i++){
				result += this.getStatusStrByCategory(tempArr[i]) + ",";
			}
			result = result.substr(0,result.length-1);
		}

		if(!LEx.isNotNull(result)){
			result = this.defaultValue;
		}
		return result;
	},
	//根据小类value获取大类id
	getCategoryidByStatus:function(val){
		var result = '';
		if(LEx.isNotNull(val)){
			val=val.replace(/(^\s+|\s+$)/g,"");   // 过滤空格
			result = this.getIdFromValuesByVal(val,this.statusCategories);
		}
		if(!LEx.isNotNull(result)){
			result = this.defaultValue;
		}
		return result;
	},
	//根据小类value获取大类名称
	getCategoryNameByStatus:function(val){
		return this.formatCategory(this.getCategoryidByStatus(val));
	},
	//根据大类id获取分组id
	getGroupIdByCategoryId:function(val){
		var result = '';
		if(LEx.isNotNull(val)){
			val=val.replace(/(^\s+|\s+$)/g,"");   // 过滤空格
			result = this.getIdFromValuesByVal(val,this.statusGroups);
		}
		if(!LEx.isNotNull(result)){
			result = this.defaultValue;
		}
		return result;
	},
	//根据大类id获取分组名称
	getGroupNameByCategoryId:function(val){
		return this.formatGroup(this.getGroupNameByCategoryId(val));
	},
	//根据小类id获取分组id
	getGroupIdByStatus:function(val){
		var categoryId = this.getCategoryidByStatus(val);
		return this.getGroupIdByCategoryId(categoryId);;
	},
	//根据小类id获取分组名称
	getGroupNameByStatus:function(val){
		return this.formatGroup(this.getGroupIdByStatus(val));
	},
	initSelect:function(id){
		if(!LEx.isNotNull(id)){
			LEx.alert("请传入id");
			return false;
		}
		var select = $("#"+id);
		var len = this.statusCategories.length;
		for(var i=0;i<len;i++){
			var categoryId = this.statusCategories[i].id;
			var tempStr = "<option value='" + categoryId+ "'  code=\"" +this.getStatusStrByCategory(categoryId)+ "\">";
			tempStr += this.formatCategory(categoryId) + "</option>"
			select.append(tempStr)
		}

	},
	initSpan:function(id){
		if(!LEx.isNotNull(id)){
			LEx.alert("请传入id");
			return false;
		}
		var oSpan = $("#"+id);
		var len = this.statusCategories.length;
		for(var i=0;i<len;i++){
			var categoryId = this.statusCategories[i].id;

			var tempStr = "|<a href='javascript:;' rel='" + categoryId+ "'  code=\"" +this.getStatusStrByCategory(categoryId)+ "\">";
			tempStr += this.formatCategory(categoryId) + "</a>"


			oSpan.append(tempStr)
		}

	}
}