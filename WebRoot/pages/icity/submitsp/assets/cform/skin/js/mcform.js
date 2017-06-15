/**
 * 运行时表单静态类
 * 
 */
(function($) {
	if (typeof CForm == "undefined") {
		CForm = {};
	}
	
	/**
	 * 注册事件
	 */
	CForm.on = function(eventName, func) {
		$(CForm).on.apply($(CForm), arguments);
	};
	
	/**
	 * 触发事件
	 */
	CForm.trigger = function(eventName, args) {
		$(CForm).trigger.apply($(CForm), arguments);
	};

	/**
	 * 获得页面上所有的表单数据
	 * 
	 * @returns CForm.List，每一项为一个CForm.Map
	 */
	CForm.getAllFormData = function() {
		var list = new CForm.List();

		$.each($("[cf_elementType=form]"), function() {
			var map = CForm.getFormData(this);
			map.put("formId", $(this).attr("id"));

			list.add(map);
		});

		return list;
	};

	/**
	 * 获得指定表单数据
	 * 
	 * @param form
	 *            表单
	 * @returns CForm.Map key为fieldId,value为fieldValue
	 *          若表单中含有动态行，则key为动态行定义ID，value为CForm.List
	 */
	CForm.getFormData = function(form) {
		var $form = form ? $(form) : $("[cf_elementType=form]").eq(0);
		if ($form.length <= 0) {
			alert("请指定要要获得数据的表单!");
			return;
		}
		var map = new CForm.Map();

		// 具有modelItemId的所有域
		var fields = $form.find("[cf_modelItemId][cf_elementType=field]");

		// 组装主表单数据
		$.each(fields, function(i, field) {
			var fieldId = $(field).attr("id");
			var fieldValue = CForm.getFieldValue(field);
			// 若已有数据，则用","间隔各个数据
			var preFieldValue = map.get(fieldId);
			if (preFieldValue) {
				if ($.trim(fieldValue)) {
					fieldValue = preFieldValue + "," + fieldValue;
				} else {
					fieldValue = preFieldValue;
				}
			}
			map.put(fieldId, fieldValue);
		});

		return map;
	};

	/**
	 * 向指定的表单里设置实际数据
	 * 
	 * @param form
	 *            表单
	 * @param data
	 *            数据
	 * @param byAttr
	 *            赋值属性           
	 */
	CForm.setFormData = function(form, data, byAttr) {
		if (!byAttr) {
			// 默认根据业务模型项赋值
			byAttr = "cf_modelItemId";
		}
		var $form = $(form);
		// 为每个域赋值
		for ( var key in data) {
			var value = data[key];
			// 过滤空值
			if (!value) {
				continue;
			}

			// 主表单
			var $field = $form.find("[" + byAttr + "=" + key + "]");
			$.each($field, function(idx, input) {
				CForm.setFieldValue(input, value);
			});
		}
	};
	
	/**
	 * 根据业务含义向指定的表单里设置实际数据
	 * 
	 * @param form
	 *            表单
	 * @param data
	 *            数据
	 */
	CForm.setFormDataByBizMean = function(form, data) {
		CForm.setFormData(form, data, "cf_bizMean");
	};

	/**
	 * 向指定的表单里，设置数据绑定
	 * 
	 * @param form
	 *            表单
	 * @param data
	 *            数据
	 */
	CForm.setDataBind = function(form, data) {
		var $form = $(form);
		// 为每个域赋值
		for ( var key in data) {
			var value = data[key];
			// 主表单
			var $field = $form.find("[cf_modelItemId=" + key + "]");

			$.each($field, function(i, field) {
				CForm.setFieldDataBind(field, value);
			});
		}
	};

	/**
	 * 向指定的表单里，根据域ID设置数据绑定
	 * 
	 * @param form
	 *            表单
	 * @param data
	 *            数据
	 */
	CForm.setDataBindByFieldId = function(form, data) {
		var $form = $(form);
		// 为每个域赋值
		for ( var key in data) {
			var value = data[key];
			// 主表单
			var $field = $form.find("#" + key);

			$.each($field, function(i, field) {
				CForm.setFieldDataBind(field, value);
			});
		}
	};

	/**
	 * 获得域值
	 * 
	 * @param field
	 *            域
	 */
	CForm.getFieldValue = function(field) {
		var fieldValue = "";

		switch (field.type) {
			// 单选按钮
			case "radio":
				if (!field.checked) {
					break;
				}
			// 复选框
			case "checkbox":
				if (!field.checked) {
					break;
				}
			// 下拉框
			case "select-one":
			// 多行文本框
			case "textarea":
			// 单行文本框
			case "text":
				fieldValue = $.trim($(field).val());
				break;
			case "password":
			case "button":
			case "file":
			case "image":
			case "reset":
			case "submit":
			default:
				break;
		}

		return fieldValue;
	};

	/**
	 * 设置域值
	 * 
	 * @param field
	 *            域
	 * @param value
	 *            域数据
	 */
	CForm.setFieldValue = function(field, value) {
		switch (field.type) {
			case "radio":
				if (value && value == $(field).val()) {
					$(field).attr("checked", true);
				}
				
				break;
			case "checkbox":
				if (value) {
					var valArray = value.split(",");
					for ( var i = 0; i < valArray.length; i++) {
						if (valArray[i] == $(field).val()) {
							$(field).attr("checked", true);
							break;
						}
					}
				}
				
				break;
			case "textarea":
				if (value) {
					// 将textarea中的"[br]"替换为"\r\n"
					$(field).val(value.replace(/\[br\]/g, "\r\n"));
					// 高度自适应
					$(field).trigger("change");
				}
	
				break;
			default:
				if (value) {
					$(field).val(value);
				}
	
				break;
		}
	};

	/**
	 * 设置域绑定
	 * 
	 * @param field
	 *            域
	 * @param value
	 *            值
	 */
	CForm.setFieldDataBind = function(field, value) {
		switch (field.type) {
			case "select-one":
				$(field).append(value);
				break;
			default:
				$(field).val(value);
				break;
		}
	};

	/**
	 * 提交数据时校验
	 */
	CForm.validate = function() {
		// 校验结果
		var isValid = true;

		// 非空校验
		$(".cfIsRequired").each(
				function(i, field) {
					switch (field.type) {
						// 单选按钮
						case "radio":
							// 同一组中至少有一个选中
							$radios = $("input[type=radio][name="
									+ $(field).attr("name") + "]:checked");
							if (!$radios.length) {
								isValid = false;
							}
							
							break;
						// 复选框
						case "checkbox":
							// 同一组中至少有一个选中
							$checkboxes = $("input[type=checkbox][name="
									+ $(field).attr("name") + "]:checked");
							if (!$checkboxes.length) {
								isValid = false;
							}

							break;
						default:
							if (!$.trim($(field).val())) {
								isValid = false;
							}

							break;
					}

					if (!isValid) {
						alert("域[" + $(field).attr("name") + "]不能为空!");
						$(field).focus();
						return false;
					}
				});

		// 数据类型校验
		$('.cfIsInteger').each(function(i, field) {
			var val = $.trim($(this).val());
			if (val && !CForm.isInteger(val)) {
				alert("域[" + $(field).attr("name") + "]只能输入整数!");
				$(field).focus();
				isValid = false;
				return false;
			}
		});

		$('.cfIsFloat').each(function(i, field) {
			var val = $.trim($(this).val());
			if (val) {
				if (!CForm.isFloat(val)) {
					if (!CForm.isInteger(val)) {
						alert("域[" + $(field).attr("name") + "]只能输入小数!");
						$(field).focus();
						isValid = false;
						return false;
					}					
				}else {
					// 精确度校验
					var precision = $(this).attr("cf_fieldPrecision");
					if (precision) {
						var floatLength = val.toString().split(".")[1].length
						if (floatLength > precision) {
							alert("域[" + $(field).attr("name") + "]精确度超过" + precision + "!");
							$(field).focus();
							isValid = false;
							return false;
						}
					}
				}
			}
		});

		$('.cfIsZipCode').each(function(i, field) {
			var val = $.trim($(this).val());
			if (val && !CForm.isZipCode(val)) {
				alert("域[" + $(field).attr("name") + "]的值不符合邮政编码格式!");
				$(field).focus();
				isValid = false;
				return false;
			}
		});

		$('.cfIsEmail').each(function(i, field) {
			var val = $.trim($(this).val());
			if (val && !CForm.isEmail(val)) {
				alert("域[" + $(field).attr("name") + "]的值不符合电子邮件格式!");
				$(field).focus();
				isValid = false;
				return false;
			}
		});

		return isValid;
	};

	/**
	 * 验证值是否为整数
	 */
	CForm.isInteger = function(val) {
		return /^(?:-?|\+?)\d+$/g.test(val);
	};

	/**
	 * 判断值是否为小数
	 */
	CForm.isFloat = function(val) {
		return /^(?:-?|\+?)\d*\.\d+$/g.test(val);
	};

	/**
	 * 判断值是否为邮政编码
	 */
	CForm.isZipCode = function(val) {
		return /^\d{6}$/g.test(val);
	};

	/**
	 * 判断值是否为电子邮件
	 */
	CForm.isEmail = function(val) {
		return /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/g.test(val);
	};

	/**
	 * 将域设置为只读
	 */
	CForm.setFieldReadOnly = function(field) {
		// 输入框，设置为readonly，去除单击、双击事件
		if (field.type == "text" || field.type == "textarea") {
			$(field).attr("readonly", "readonly");
			$(field).unbind().removeAttr("onclick").removeAttr(
					"ondblclick");
		}
		// 其他组件，设置为disabled
		else {
			$(field).attr("disabled", "disabled");
		}
		// 添加只读样式
		$(field).addClass("cfIsReadonly");
	};

	/**
	 * 将表单所有可见域设置为只读
	 */
	CForm.setFormReadOnly = function(form) {
		var $form = form ? $(form) : $("[cf_elementType=form]").eq(0);
		if ($form.length <= 0) {
			alert("未获取到要操作的表单！");
			return;
		}
		var fields = $form.find("[cf_elementType=field]");
		$.each(fields, function() {
			// 设置域只读
			CForm.setFieldReadOnly(this);
		});
	};
	
	/**
	 * 渲染日期（时间）组件
	 */
	CForm.bindDateTime = function(form) {
		var $form = form ? $(form) : $('[cf_elementType=form]').eq(0);
		if ($form.length <= 0) {
			alert('未获取到要操作的表单！');
			return;
		}
		
		var opt = {
				theme : 'android-ics',
				mode : 'scroller',
				display : 'modal',
				dateOrder : 'yyMdd',
				setText : '设置',
				cancelText : '取消',
				monthNames : '1,2,3,4,5,6,7,8,9,10,11,12'.split(','),
				monthNamesShort : '1,2,3,4,5,6,7,8,9,10,11,12'.split(','),
				endYear : 2042
			};

		// 手机日期时间组件
		$form.find('.cfDateTime').each(function() {
			var dt = $(this).attr('format');
			if (!dt) {
				dt = 'yy/mm/dd HH:ii:ss';
			}
			$(this).scroller($.extend(opt, {
				preset : 'datetime',
				headerText : '设置日期时间',
				timeWheels : 'HHii',
				timeFormat : 'HH:ii',
				dateFormat : dt
			}));
		});

		// 手机日期组件
		$form.find('.cfDate').each(function() {
			var d = $(this).attr('format');
			if (!d) {
				d = 'yy/mm/dd';
			}
			$(this).scroller($.extend(opt, {
				preset : 'date',
				headerText : '设置日期',
				dateFormat : d
			}));
		});
	};

	/**
	 * 域长度控制初始化
	 */
	CForm.initFieldLengthCtr = function() {
		// 控制text的长度
		$(".cfText").keyup(function(event) {
			var len = parseInt($(this).attr("cf_fieldLength"));
			if(!len) {
				len = 100;
			}		

			var strVal = $(this).val().toString() + "";
		    //预期计数：中文2字节，英文1字节 
		    var a = 0; 
		    //临时字串
		    var temp = ''; 
		    for (var i=0; i<strVal.length; i++) 
		    { 
		        if (strVal.charCodeAt(i) > 255)  
		        {
		            //按照预期计数增加2 
		             a+=2; 
		        } 
		        else 
		        { 
		             a++; 
		        } 
		        //如果增加计数后长度大于限定长度，就直接返回临时字符串 
		        if(a > len) { $(this).val(temp); return false;} 
		        //将当前内容加到临时字符串 
		        temp += strVal.charAt(i); 
		    } 
		});

		// 控制textarea的长度
		$(".cfTextArea").keyup(function(event) {
			var len = parseInt($(this).attr("cf_fieldLength"));
			if(!len) {
				len = 500;
			}		

			var strVal = $(this).val().toString() + "";
		    //预期计数：中文2字节，英文1字节 
		    var a = 0; 
		    //临时字串
		    var temp = ''; 
		    for (var i=0; i<strVal.length; i++) 
		    {
		        if (strVal.charCodeAt(i) > 255)  
		        {
		            //按照预期计数增加2 
		             a+=2; 
		        } 
		        else 
		        { 
		             a++; 
		        } 
		        //如果增加计数后长度大于限定长度，就直接返回临时字符串 
		        if(a > len) { $(this).val(temp); return false;} 
		        //将当前内容加到临时字符串 
		        temp += strVal.charAt(i); 
		    } 
		});
	};
	
	/**
	 * 多行文本框高度自适应
	 */
	CForm.initAutoHeight = function() {
		// 高度自适应
		var opt = {
				minHeight : 80,
				maxHeight : 200
			}
		$("textarea[cf_elementType=field]").each(function() {
			$(this).textareaAutoHeight(opt);
		});
	};
	
	/**
	 * 云表单加载完毕后需要执行的初始化操作
	 */
	CForm.init = function() {
		// 初始化域长度控制
		CForm.initFieldLengthCtr();
		
		// 多行文本框高度自适应
		CForm.initAutoHeight();
		
		// 初始化日期、日期时间
		if ($(".cfDateTime,.cfDate").length) {
			CForm.bindDateTime();
		}
	};
	
})(this.Zepto||this.jQuery);

/**
 * 工具类--Map
 */
(function($) {
	/**
	 * 模拟后台的Map，可构造前台Map对象
	 */
	CForm.Map = function(classname, data) {
		this.javaClass = classname ? classname : "HashMap";// short name
		this.map = data ? data : new Object();
		this.length = this.size();
	};

	CForm.Map.prototype = {

		length : null,
		/**
		 * 把值放入map对象中，比如：data.put(key,value);
		 * 
		 * @method put
		 * @param {String}
		 *            key 键值
		 * @param {Oject}
		 *            value 值
		 */
		put : function(key, value) {
			this.map[key] = value;
			this.length++;
		},
		/**
		 * 根据键值从map对象中取值，比如：var val = data.get(key);
		 * 
		 * @method get
		 * @param {String}
		 *            key 键值
		 * @return {Oject} 值
		 */
		get : function(key) {
			return this.map[key];
		},
		/**
		 * 从map对象中移除指定键值的值，比如：data.remove(key);
		 * 
		 * @method remove
		 * @param {String}
		 *            key 键值
		 * @return {Oject} 移除的对象
		 */
		remove : function(key) {
			var ret = this.map[key];
			this.map[key] = null;
			this.length--;
			return ret;
		},
		/**
		 * 获得map对象中值的个数
		 * 
		 * @method size
		 * @return {Int} 长度
		 */
		size : function() {
			if (this.length !== null)
				return this.length;
			this.length = 0;
			for ( var i in this.map) {
				this.length++;
			}
			return this.length;
		},
		/**
		 * 将map对象中所有的值组装为字符串返回
		 * 
		 * @method toString
		 * @return {String} 数据
		 */
		toString : function() {
			var ret = "{";
			var j = 0;
			for ( var i in this.map) {
				ret += i.toString() + ":" + this.get(i).toString();
				if (j++ < this.size() - 1)
					ret += ",";
			}
			ret += "}";
			return ret;
		}
	};
})(this.Zepto||this.jQuery);

/**
 * 工具类--List
 */
(function($) {
	CForm.List = function(classname, data) {
		this.javaClass = classname ? classname : "ArrayList";
		this.list = data ? data : new Array();
	};

	CForm.List.prototype = {
		/**
		 * 把对象添加到list对象中
		 * 
		 * @method add
		 * @param {Oject}
		 *            obj 对象
		 */
		add : function(obj) {
			this.list.push(obj);
		},
		/**
		 * 根据索引得到对象
		 * 
		 * @method get
		 * @param {String}
		 *            index 索引
		 * @return {Oject} 对象
		 */
		get : function(index) {
			var val = this.list[index];
			return val;
		},
		/**
		 * 得到list对象的长度
		 * 
		 * @method size
		 * @return {Int} 长度
		 */
		size : function() {
			return this.list.length
		},
		/**
		 * 将list对象中的数据转换组装成字符串返回
		 * 
		 * @method toString
		 * @return {String} 数据
		 */
		toString : function() {
			var ret = "[";
			for ( var i = 0; i < this.size(); i++) {
				ret += this.get(i).toString();
				if (i < this.size() - 1)
					ret += ",";
			}
			ret += "]";
			return ret;
		}
	};
})(this.Zepto||this.jQuery);

/**
 * 工具类--多行文本框自适应
 */
(function($) {
	$.extend($.fn, {
		textareaAutoHeight : function(options) {
			this._options = {
				minHeight : 0,
				maxHeight : 1000
			}

			this.init = function() {
				for ( var p in options) {
					this._options[p] = options[p];
				}
				if (this._options.minHeight == 0) {
					this._options.minHeight = parseFloat($(this).height());
				}
				for ( var p in this._options) {
					if ($(this).attr(p) == null) {
						$(this).attr(p, this._options[p]);
					}
				}
				$(this).keyup(this.resetHeight).change(this.resetHeight).focus(
						this.resetHeight);
			}

			this.resetHeight = function() {
				var _minHeight = parseFloat($(this).attr("minHeight"));
				var _maxHeight = parseFloat($(this).attr("maxHeight"));

				$(this).height(0);
				
				var h = parseFloat(this.scrollHeight);
				h = h < _minHeight ? _minHeight : h > _maxHeight ? _maxHeight
						: h;

				$(this).height(h);

				if (h >= _maxHeight) {
					$(this).css("overflow-y", "scroll");
				} else {
					$(this).css("overflow-y", "hidden");
				}
			}

			this.init();
		}
	});
})(this.Zepto||this.jQuery);