/**
 * 将form序列化成json字符串,本方法依赖于jquery.js
 * @param id
 * @return
 */
function form2json(id){
	var str = "";
	var temp = new Array();
	//单行文本
	$("#"+id).find("input").filter(":text").each(function(){
		//alert(this.name+":"+this.value);
		str+="\""+this.name+"\":"+"\""+this.value+"\",";
	});
	//单行密码
	$("#"+id).find("input").filter(":password").each(function(){
		//alert(this.name+":"+this.value);
		str+="\""+this.name+"\":"+"\""+this.value+"\",";
	});
	//隐藏
	$("#"+id).find("input").filter(":hidden").each(function(){
		//alert(this.name+":"+this.value);
		str+="\""+this.name+"\":"+"\""+this.value+"\",";
	});
	//下拉列表
	$("#"+id).find("select").each(function(){
		//alert(this.name+":"+this.value);
		str+="\""+this.name+"\":"+"\""+this.value+"\",";
	});
	//单选框
	$("#"+id).find("input").filter(":radio").filter(":checked").each(function(){
		//alert(this.name+":"+this.value);
		str+="\""+this.name+"\":"+"\""+this.value+"\",";
	});
	//复选框,应该写成name=[value1,value2,value3……]这种形式
	var checkboxNames = "";
	$("#"+id).find("input").filter(":checkbox").each(function(){
		if(checkboxNames.indexOf(this.name)==-1){
			checkboxNames+=this.name+",";
		}
	});
	checkboxNames = checkboxNames==""?"":checkboxNames.substring(0, checkboxNames.length-1);
	var arr = checkboxNames.split(",");
	for(var i = 0; i < arr.length; i++){
		var na = arr[i];
		if(na!=""){
			var values = "";
			$("#"+id).find("input").filter(":checkbox").filter(":checked").each(function(){
				if(this.name==na){
					values+=this.value+",";
				}
			});
			values = values==""?"":values.substring(0,values.length-1);
			str+="\""+na+"\":["+values+"],";
		}
	}
	//多行文本
	$("#"+id).find("textarea").each(function(){
		//alert(this.name+":"+this.value);
		str+="\""+this.name+"\":"+"\""+this.value+"\",";
	});
	str = str.replace(/^\s*|\s*$/, "")==""?"":str.substring(0, str.length-1);
	return "{"+str+"}";
}

/**
 * 将json对象中的值赋到form中,本方法依赖于jquery.js
 * @param json
 * @param id
 * @return
 */
function json2form(json,id){
	//单行文本
	$("#"+id).find("input").filter(":text").each(function(i){
		$(this).val(json[this.name]?json[this.name]:"");
	});
	//密码框
	$("#"+id).find("input").filter(":password").each(function(i){
		$(this).val(json[this.name]?json[this.name]:"");
	});
	//隐藏框
	$("#"+id).find("input").filter(":hidden").each(function(i){
		$(this).val(json[this.name]?json[this.name]:"");
	});
	//下拉
	$("#"+id).find("select").each(function(i){
		$(this).val(json[this.name]?json[this.name]:"");
	});
	//多行文本
	$("#"+id).find("textarea").each(function(i){
		$(this).val(json[this.name]?json[this.name]:"");
	});
	//单选框
	var radioNames = "";
	$("#"+id).find("input").filter(":radio").each(function(i){
		radioNames+=radioNames.indexOf(this.name)==-1?this.name+",":"";
	});
	radioNames = radioNames==""?"":radioNames.substring(0, radioNames.length-1);
	var strArr = radioNames.split(",");
	for(var i = 0 ; i < strArr.length;i++){
		var na = strArr[i];
		$("#"+id).find("input").filter(":radio").each(function(m){
			if(this.name==na){
				$(this).attr("checked",this.value==json[na]);
			}
		});
	}
	//复选框
	var checkNames = "";
	$("#"+id).find("input").filter(":checkbox").each(function(i){
		$(this).attr("checked",false);//全不选
		checkNames+=checkNames.indexOf(this.name)==-1?this.name+",":"";
	});
	checkNames = checkNames==""?"":checkNames.substring(0, checkNames.length-1);
	var checkArr = checkNames.split(",");
	for(var i = 0 ; i < checkArr.length;i++){
		var na = checkArr[i];
		if(na!=""){
			$("#"+id).find("input[name='"+na+"']").filter(":checkbox").each(function(m){
				if(this.name==na){
					if(json[na]){
						for(var j = 0; j < json[na].length; j++){
							if(this.value==json[na][j]){
								this.checked=true;
							}
						}
					}
				}
			});
		}
	}
}
//表单验证