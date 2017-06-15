/**
 * 表单域控制
 * @param flow_id
 * @param flow_node_id
 * @param form_id
 */
function setFormField(flow_id,flow_node_id,form_id){
		 var data = {"flowId":flow_id,"flowNodeId":flow_node_id,"formId":form_id};
		 var url= CForm.webPath+"/cform/getLocalFormField";		
		$.ajax({
	        url:  url,
			async : true,
			type : "POST",
			data: data,
			dataType:"JSON",
			success : function(data) {	
				if(data){				
					var fieldArray=data.field;
					var zoneArray=data.zone;
					setFieldAttr(fieldArray);
				$.each(zoneArray,function(i,item){
					if(item && item.PARENT_ZONE_PK!=-1){							
							var validateData=item.validateData;							
								if(validateData){
									if(validateData.isReadOnly){
										$("#"+item.ZONE_ID).attr("readonly","readonly");
										$("#"+item.ZONE_ID).addClass("cfIsReadonly");
										if(item.ZONE_TYPE=="Tab"){
											 CForm.setTabReadOnly(item.ZONE_ID);
										}
										//setFieldByZone(id,fieldArray,"readonly");
									}
									if(validateData.isHidden){										
										if(item.ZONE_TYPE=="Tab"){
											$("#"+item.ZONE_ID).hide();
											var tabContent  = $("#"+item.ZONE_ID);
											var tabIndex = tabContent.index()-1;
											tabContent.parent().find(".cfTabHeader>li").eq(tabIndex).addClass("hide");
										}else{
											$("#"+item.ZONE_ID).addClass("cfHidden");
										}
										var id=item.ID;	
										//setFieldByZone(id,fieldArray,"hidden");
									}
							}
						}	
					});
				}
			}
	});
	 }
	
/**
	  * 表单映射
	  * @param itemId
	  * @param formId
	  * @param bsnum
	  */
	function setMappingData(itemId,formId,bsnum,curNodeId){			
		var data = {"formId":formId,"bsnum":bsnum,"itemId":itemId,"flowNodeId":curNodeId};
		 var url= CForm.webPath+"/cform/getFormMappingData";		
		$.ajax({
	        url:  url,
			async : true,
			type : "POST",
			data: data,
			dataType:"JSON",
			success : function(data) {				
				if(data &&  data.fieldList){	
					var fieldList=data.fieldList;				
					$.each(fieldList,function(i,item){						
						if(item.fieldId && item.data){
							$("#"+item.fieldId).val(item.data);
						}
					});				
				}else{
				}
			}
	});
	}

	/**
	 * 根据关联表单获取表单数据
	 * @param formId
	 * @param dataId
	 */
	function setRelationData(formId,dataId){
		$.ajax({
	        url:  CForm.loadDataPath + "?formId=" + formId + "&formDataId=" + dataId,
		async:false,
		type:"POST",
		dataType:"json",
		success:function(options,success,response){	
			if(response) {
				CForm.setFormData("form", response.responseJSON);
			}
		}});
	}
	
	/**
	 * 根据表单Zone设置的属性控制域属性
	 * @param field
	 * @param type
	 */
	function setFieldByZone(id,field,type){
		 if(field){
		        $.each(field,function(j,fieldItem){		        
		        	if(id==fieldItem.ID){
		        	switch(type){
		        	case "hidden":
		        		setHiddenField(fieldItem.FIELD_ID);
						break;
		        	case "readonly":
		        		setReadonlyField(fieldItem.FIELD_ID);
		        		break;
		        	}
		        	}
		        });
		 }
	}
	/**
	 * 设置域属性
	 * @param field
	 */
	function setFieldAttr(field){
		 if(field){
		        $.each(field,function(j,fieldItem){						        
							var validateData=fieldItem.validateData;
							if(validateData){
								if(validateData.isReadOnly){
									setReadonlyField(fieldItem.FIELD_ID);
									//CForm.setFieldReadOnly($("#"+fieldItem.FIELD_ID));
								}
								if(validateData.isRequired){
								    if(!$("#"+fieldItem.FIELD_ID).hasClass("cfIsRequired")){
								    	CForm.setFieldRequired(fieldItem.FIELD_ID);
								    }									
								}
								if(validateData.isHidden){									
									setHiddenField(fieldItem.FIELD_ID);
								}
								if(validateData.isInitialize){
									
								}
							}
		        });
		 }
	}
	
	/**
	 * 设置只读域
	 * @param id
	 */
	function setReadonlyField(id){
		CForm.setFieldNotRequired(id);
		//隐藏域无需处理
        if($("#"+id).hasClass("cfHidden"))
            return; 
		$("#"+id).attr('disabled','disabled').css("background-color","white").css("cursor","default");
	}
	
	/**
	 * 设置隐藏域
	 * @param id
	 */
	function setHiddenField(id){
		CForm.setFieldNotRequired(id);
		$("#"+id).addClass("cfHidden");
		$("#"+id).parent().prev().text("");
	}
	/**
	 * 给表单指定Id 赋值
	 * @param field
	 */
	function setFormFieldValue(Id,value){		
		var obj=document.getElementById(Id);
		if(obj!=undefined)
		CForm.setFieldValue(obj,value);
	}
	
	function setForm(data){
		if(LEx.isNotNull(data) && Object.keys(data).length > 0 ){				
			var fieldArray=data.field;
			var zoneArray=data.zone;
			setFieldAttr(fieldArray);
		$.each(zoneArray,function(i,item){
			if(item && item.PARENT_ZONE_PK!=-1){							
					var validateData=item.validateData;							
						if(validateData){
							if(validateData.isReadOnly){
								$("#"+item.ZONE_ID).attr("readonly","readonly");
								$("#"+item.ZONE_ID).addClass("cfIsReadonly");
								if(item.ZONE_TYPE=="Tab"){
									 CForm.setTabReadOnly(item.ZONE_ID);
								}
							}
							if(validateData.isHidden){										
								if(item.ZONE_TYPE=="Tab"){
									var tabContent  = $("#"+item.ZONE_ID);
									var tabIndex = tabContent.index()-1;
									tabContent.parent().find(".cfTabHeader>li").eq(tabIndex).addClass("hide");
								}else{
									$("#"+item.ZONE_ID).addClass("cfHidden");
								}
								var id=item.ID;	
							}
					}
				}	
			});
		$(".cfTabBody").addClass("hide");
		$(".cfTabHeader>li").each(function() {
			if(!$(this).hasClass("hide")){
				$($(".cfTabBody").get($(this).index())).removeClass("hide");
				return;
			}
		});
		}
	}