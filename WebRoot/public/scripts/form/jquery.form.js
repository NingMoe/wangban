LEx.form = function(){
	
}
/**
 * 给所有匹配元素集合赋值，跟据元素的propertyName从entity中取值
 * @param containerId
 *		容器Id
 * @param {} entity
 *     传递过来的java实体Bean对应的js对象
 * @param {} [attrName]     
 *     可选参数；
 *     元素的属性名称（例如id，name,tag等）；
 *     默认为tag属性；
 *     元素的该属性值要和entity中的变量名对应
 */
LEx.form.set = function(containerId,entity, attrName){
	if(attrName == null) {   //判断是否传递了第二个参数
		attrName = "tag";   //如果没有默认为name属性
	}
	$("#"+containerId +" ["+attrName+"]").each(function(index, element) {
		var tObj = $(this);
		//获取元素attrName属性的属性值
		var propertyVal = tObj.attr(attrName);
		//如果实体对象entity中包含名称为propertyVal的变量
		if(entity[propertyVal] != undefined) {
			//获取实体对象entity名称为propertyVal的变量的值
			var enValue = entity[propertyVal];
			if(tObj.is("span")){
				tObj.html(enValue); //设置元素的值
			}
			else if(tObj.is("div")){
				tObj.html(enValue); //设置元素的值
			}else if(tObj.is("p")){
				tObj.html(enValue);
			}
			else{
				//如果元素类型是radio或者是checkbox
				if("radio" == tObj.attr("type")){
					if(enValue == tObj.val()){
						tObj.attr("checked", true);
					}
					else{
						tObj.attr("checked", false);
					}
				}else if("checkbox" == tObj.attr("type")) {
					//如果元素值等于enValue
					enValue =","+enValue+",";
					if(enValue.indexOf(tObj.val())>-1) {
						//设置元素被选中
						tObj.attr("checked", true);
					}else{
						tObj.attr("checked", false);
					}
				}
				else {//其他类型的元素（input：text，hidden，password；textarea等）
					tObj.val(enValue); //设置元素的值
				}
			}
		}
    });
};

LEx.form.get = function(containerId,attrName){
	if(attrName == null) {   //判断是否传递了第二个参数
		attrName = "tag";   //如果没有默认为name属性
	}
	var objRet = {};
	$("#"+containerId +" ["+attrName+"]").each(function(index, element) {
		var tObj = $(this);
		//获取元素attrName属性的属性值
		var propertyVal = tObj.attr(attrName);
		if(tObj.is("span")){
			objRet[propertyVal] = tObj.html();
		}
		else{
			//如果元素类型是radio或者是checkbox
			if("radio" == tObj.attr("type")){
				if(tObj.attr("checked") == true){
					objRet[propertyVal] = tObj.val();
				}
			}
			else if("checkbox" == tObj.attr("type")) {
				
				if(tObj.attr("checked") == true){
					var v = tObj.val();
					if(objRet[propertyVal] == undefined){
						objRet[propertyVal] = v;
					}
					else{
						var oldValue = objRet[propertyVal];
						oldValue = ","+oldValue+",";
						if(oldValue.indexOf(tObj.val())<0){
							objRet[propertyVal]= objRet[propertyVal]+","+v;	
						}
					}
				}
			}else {//其他类型的元素（input：text，hidden，password；textarea等）
				objRet[propertyVal] = tObj.val();
			}
		}
    });
	return objRet;
};


/*
 * Copyright (c) 2009 Mustafa OZCAN (http://www.mustafaozcan.net) Dual licensed
 * under the MIT (http://www.opensource.org/licenses/mit-license.php) and GPL
 * (http://www.opensource.org/licenses/gpl-license.php) licenses. Version: 1.0.2
 * Requires: jquery.1.3+
 */
LEx.form.fix = function(table,options) {
	var settings = jQuery.extend({
		headerrowsize : 1,
		highlightrow : false,
		highlightclass : "highlight"
	}, options);
	if(table.substr(0,1)!="#" || table.substr(0,1)!=".")
		table = "#"+table;
	$(table).each(function(i) {
				var $tbl = $(this);
				var $tblhfixed = $tbl.find("tr:lt(" + settings.headerrowsize
						+ ")");
				var headerelement = "th";
				if ($tblhfixed.find(headerelement).length == 0)
					headerelement = "td";
				if ($tblhfixed.find(headerelement).length > 0) {
					$tblhfixed.find(headerelement).each(function() {
						$(this).css("width", $(this).width());
					});
					var $clonedTable = $tbl.clone().empty();
					var tblwidth = GetTblWidth($tbl);
					$clonedTable.attr("id", "fixedtableheader" + i).css({
						"position" : "fixed",
						"top" : "0",
						"left" : $tbl.offset().left
					}).append($tblhfixed.clone()).width(tblwidth).hide()
							.appendTo($("body"));
					if (settings.highlightrow)
						$("tr:gt(" + (settings.headerrowsize - 1) + ")", $tbl)
								.hover(
										function() {
											$(this).addClass(
													settings.highlightclass);
										},
										function() {
											$(this).removeClass(
													settings.highlightclass);
										});
					$(window)
							.scroll(
									function() {
										if (jQuery.browser.msie
												&& jQuery.browser.version == "6.0")
											$clonedTable.css({
												"position" : "absolute",
												"top" : $(window).scrollTop(),
												"left" : $tbl.offset().left
											});
										else
											$clonedTable.css({
												"position" : "fixed",
												"top" : "0",
												"left" : $tbl.offset().left
														- $(window)
																.scrollLeft()
											});
										var sctop = $(window).scrollTop();
										var elmtop = $tblhfixed.offset().top;
										if (sctop > elmtop
												&& sctop <= (elmtop
														+ $tbl.height() - $tblhfixed
														.height()))
											$clonedTable.show();
										else
											$clonedTable.hide();
									});
					$(window).resize(
							function() {
								if ($clonedTable.outerWidth() != $tbl
										.outerWidth()) {
									$tblhfixed.find(headerelement).each(
											function(index) {
												var w = $(this).width();
												$(this).css("width", w);
												$clonedTable
														.find(headerelement)
														.eq(index).css("width",
																w);
											});
									$clonedTable.width($tbl.outerWidth());
								}
								$clonedTable.css("left", $tbl.offset().left);
							});
				}
			});
	function GetTblWidth($tbl) {
		var tblwidth = $tbl.outerWidth();
		return tblwidth;
	}
};

//合并单元格
LEx.form.mergeCell = function(containerId,params){
	plugin.ready('mergeCell',function() {
		$('#'+containerId).mergeCell(params); 
	});
}; 

//table排序
LEx.form.tableSorter = function(containerId){
	plugin.ready('tableSorter',function() {
		$("#"+containerId).tablesorter();
	});
}; 

/*
	初始化表单，进行验证操作
*/
LEx.form.init = function(control){
	var xOffset = -20; // x distance from mouse
    var yOffset = 20; // y distance from mouse  
	if(control == null){
		control = "body";
	}
	//input action
	$(control+" [reg],[fun]:not([reg]),[tip]").hover(
		function(e) {
			if($(this).attr('tip') != undefined){
				var top = (e.pageY + yOffset);
				var left = (e.pageX + xOffset);
				$('body').append( '<p id="vtip"><img id="vtipArrow" src="'+LEx.webPath+'public/images/arrow.png"/>' + $(this).attr('tip') + '</p>' );
				$('p#vtip').css("top", top+"px").css("left", left+"px");
				//$('p#vtip').bgiframe();
			}
		},
		function() {
			if($(this).attr('tip') != undefined){
				$("p#vtip").remove();
			}
		}
	).mousemove(
		function(e) {
			if($(this).attr('tip') != undefined){
				var top = (e.pageY + yOffset);
				var left = (e.pageX + xOffset);
				$("p#vtip").css("top", top+"px").css("left", left+"px");
			}
		}
	).blur(function(){
		if($(this).attr("reg") != undefined){
			validate($(this));
		}else if($(this).attr("fun") != undefined){
			fun_validate($(this));
		}
	});
}
/*验证操作*/
LEx.form.validate = function(control){
	if(control == null){
		control == "body";
	}
	var isSubmit = true;
	$(control +" [reg],[fun]:not([reg]),[tip]").each(function(){
		
		if($(this).attr("reg") == undefined){
			if(!fun_validate($(this))){
				isSubmit = false;
			}
		}else{
			if(!validate($(this))){
				isSubmit = false;
			}
		}
	});
	return isSubmit;
}
function validate(obj){
	var reg = new RegExp(obj.attr("reg"));
	var objValue = obj.attr("value");
	
	if(!reg.test(objValue)){
		change_error_style(obj,"add");
		change_tip(obj,null,"remove");
		return false;
	}else{
		if(obj.attr("fun") == undefined){
			change_error_style(obj,"remove");
			change_tip(obj,null,"remove");
			return true;
		}else{
			return fun_validate(obj);
		}
	}
}

function fun_validate(obj){
	
	var fun = obj.attr("fun");
	
	var re= "";
	try{
		var s = eval(fun);
		re = s.call(s,obj);
	}
	catch(e){
		re ="请求的验证函数错误。";
	}
	re = re.replace(/(^\s*)|(\s*$)/g, "");
	if(re == 'success'){
		change_error_style(obj,"remove");
		change_tip(obj,null,"remove");
		return true;
	}else{
		change_error_style(obj,"add");
		change_tip(obj,re,"add");
		return false;
	}
}

function change_tip(obj,msg,action_type){
	
	if(obj.attr("tip") == undefined){//初始化判断TIP是否为空
		obj.attr("is_tip_null","yes");
	}
	if(action_type == "add"){
		if(obj.attr("is_tip_null") == "yes"){
			obj.attr("tip",msg);
		}else{
			if(msg != null){
				if(obj.attr("tip_bak") == undefined){
					obj.attr("tip_bak",obj.attr("tip"));
				}
				obj.attr("tip",msg);
			}
		}
	}else{
		if(obj.attr("is_tip_null") == "yes"){
			obj.removeAttr("tip");
			obj.removeAttr("tip_bak");
		}else{
			obj.attr("tip",obj.attr("tip_bak"));
			obj.removeAttr("tip_bak");
		}
	}
}

function change_error_style(obj,action_type){
	if(action_type == "add"){
		obj.addClass("input_validation-failed");
	}else{
		obj.removeClass("input_validation-failed");
	}
}

$.fn.validate_callback = function(msg,action_type,options){
	this.each(function(){
		if(action_type == "failed"){
			change_error_style($(this),"add");
			change_tip($(this),msg,"add");
		}else{
			change_error_style($(this),"remove");
			change_tip($(this),null,"remove");
		}
	});
};

/*验证特殊字符*/
function regclick(obj){
   	var reg=/[@#\$%\^&\*\_\[\]\\|￥?？]+/gi ;
    	if(reg.test(obj.val())){
       	alert("输入的内容中含有特殊字符！");
           return "输入的内容不可以含有特殊字符";
       }
	return "success";
}
