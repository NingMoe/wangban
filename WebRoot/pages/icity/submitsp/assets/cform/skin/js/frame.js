(function($, undefined) {
	if (typeof LS == "undefined") {
		LS = {};
		// 从请求中获得上下文路径
		var pathArray = document.location.pathname.split("/");
		if (pathArray.length > 0 && pathArray[0]) {
			LS.webPath = "/" + pathArray[0];
		} else if (pathArray.length > 1) {
			LS.webPath = "/" + pathArray[1];
		}
	}

	/**
	 * 操作Command处理类构造函数
	 * 
	 * @class LS.Command
	 * @param {String}
	 *            className 执行的完整类名
	 */
	LS.Command = function(className) {
		this.className = className;
		this.url = LS.webPath + "/command/ajax";
		// 提交到服务器的请求参数
		this.params = {};
		// 从服务器返回的响应值
		this.returns = new LS.Map("ParameterSet");
	};

	/**
	 * 为操作Command处理类添加方法
	 */
	LS.Command.prototype = {
		/**
		 * 设置提交到服务器的请求参数
		 * 
		 * @method setParameter
		 * @param {String}
		 *            name 参数名称
		 * @param {String}
		 *            value 参数值
		 */
		setParameter : function(name, value) {
			this.params[name] = value;
		},

		/**
		 * 获得服务器返回的响应值
		 * 
		 * @method getReturn
		 * @param {String}
		 *            name 参数名称
		 * @return {String} 参数对应的值
		 */
		getReturn : function(name) {
			return this.returns.get(name);
		},

		/**
		 * 执行Command方法
		 * 
		 * @method execute
		 * @param {String}
		 *            method 执行Command中的方法名
		 * @param {Boolean}
		 *            sync 是否同步执行，默认为true
		 */
		execute : function(method, sync) {
			// 是否同步
			var isSync = true;
			if (sync && (sync == false || sync == "false")) {
				isSync = false;
			}

			var url = this.url + "/" + this.className;
			if (method) {
				url = url + "/" + method;
			}

			var dataStr = '{"params":{"javaClass":"ParameterSet","map":'
					+ $.toJSONString(this.params) + ',"length":1}}';

			var settings = {
				type : "POST",
				contentType : "application/json",
				url : url,
				data : dataStr,
				async : !isSync,// 是否异步
				success : $.proxy(this.successResponse, this),
				error : $.proxy(this.errorResponse, this),
				timeout : 30000
			};

			try {
				$.ajax(settings);
			} catch (e) {
				this.error = e;
			}
		},

		/**
		 * 从服务器端成功获得提交响应
		 */
		successResponse : function(data, status, xhr) {
			if (data) {
				var jo = null
				try {
					jo = $.parseJSON(data);
				} catch (e) {
					this.error = e;
					jo = null;
				}

				if (jo) {
					if (!jo.error) {
						this.returns = LS.serializer.unmarshall(jo);
						if (this.afterExecute) {
							this.afterExecute();
						}
					} else {
						this.error = jo.error.msg;
					}
				}
			}
		},

		/**
		 * 从服务器端获得提交响应出现异常
		 */
		errorResponse : function(xhr, errorType, error) {
			this.error = error;
		}
	};

	/**
	 * 查询Command处理类构造函数
	 * 
	 * @class QueryCommand
	 * @param {String}
	 *            className 执行的完整类名
	 */
	LS.QueryCommand = function(className) {
		this.className = className;
		this.url = LS.webPath + "/command/ajax";
		// 提交到服务器的请求参数
		this.params = {};
		// 从服务器返回的响应值
		this.records = new LS.List();
	};

	/**
	 * 为查询Command处理类添加方法
	 */
	LS.QueryCommand.prototype = {
		/**
		 * 设置提交到服务器的请求参数
		 * 
		 * @method setParameter
		 * @param {String}
		 *            name 参数名称
		 * @param {String}
		 *            value 参数值
		 */
		setParameter : function(name, value) {
			this.params[name] = value;
		},

		/**
		 * 设置起始页
		 */
		setStart : function(value) {
			this.params["start"] = value;
		},

		/**
		 * 设置每页条数
		 */
		setLimit : function(value) {
			this.params["limit"] = value;
		},

		/**
		 * 获得当前记录
		 * 
		 * @method getReturn
		 * @param {String}
		 *            name 参数名称
		 * @return {String} 参数对应的值
		 */
		getCurrent : function() {
			if (this.records.size() > 0) {
				return this.records.get(0);
			}

			return new LS.Record();
		},

		/**
		 * 获得服务器返回的所有记录
		 * 
		 * @method getReturn
		 * @param {String}
		 *            name 参数名称
		 * @return {String} 参数对应的值
		 */
		getAllRecords : function() {
			return this.records;
		},

		/**
		 * 查询Command方法
		 * 
		 * @method execute
		 * @param {String}
		 *            method 查询Command中的方法名
		 * @param {Boolean}
		 *            sync 是否同步执行，默认为true
		 */
		execute : function(method) {
			var url = this.url + "/" + this.className;
			// 执行方法
			if (method) {
				url = url + "/" + method;
			}

			var dataStr = '{"params":{"javaClass":"org.loushang.next.data.ParameterSet","map":'
					+ $.toJSONString(this.params) + ',"length":1}}';

			var settings = {
				type : "POST",
				contentType : "application/json",
				data : dataStr,
				url : url,
				async : false,// 是否异步
				success : $.proxy(this.successResponse, this),
				error : $.proxy(this.errorResponse, this)
			};

			try {
				$.ajax(settings);
			} catch (e) {
				this.error = e;
			}
		},

		/**
		 * 从服务器端成功获得查询响应
		 */
		successResponse : function(data, status, xhr) {
			if (data) {
				var jo = null
				try {
					jo = $.parseJSON(data);
				} catch (e) {
					this.error = e;
					jo = null;
				}

				if (jo) {
					if (!jo.error) {
						this.records = LS.serializer.unmarshall(jo);
					} else {
						this.error = jo.error.msg;
					}
				}
			}
		},

		/**
		 * 从服务器端获得查询响应出现异常
		 */
		errorResponse : function(xhr, errorType, error) {
			this.error = error;
		}
	};

	/**
	 * 前台记录Record构造函数
	 * 
	 * @class LS.Record
	 * @param {String}
	 *            data 要设置的数据
	 */
	LS.Record = function(data) {
		this.javaClass = "Record";
		this.data = data;
		if (!this.data) {
			this.data = {};
		}
	};

	/**
	 * 为前台记录Record添加方法
	 * 
	 * @class QueryCommand
	 * @param {String}
	 *            className 执行的完整类名
	 */
	LS.Record.prototype = {
		/**
		 * 把值设置到Record中
		 * 
		 * @method set
		 * @param {String}
		 *            key 键值
		 * @param {Oject}
		 *            value 值
		 */
		set : function(key, value) {
			this.data[key] = value;
		},

		/**
		 * 根据键值从Record中取值
		 * 
		 * @method get
		 * @param {String}
		 *            key 键值
		 * @return {Oject} 值
		 */
		get : function(key) {
			return this.data[key];
		},

		/**
		 * 将record对象转换为JSON字符串
		 * 
		 * @method toString
		 * @return {String} JSON字符串
		 */
		toJSONString : function() {
			var o = {
				"javaClass" : this.javaClass,
				"data" : this.data
			};

			return $.toJSONString(o);
		},

		/**
		 * 将record数据转换成指定的JavaBean类型
		 * 
		 * @method toBean
		 * @param {String}
		 *            className 完整的JavaBean类名
		 * @return {LS.DataBean} DataBean对象
		 */
		toBean : function(className) {
			var bean = {
				"javaClass" : className
			};

			var data = this.data;

			for (name in data) {
				if (data[name] == "") {
					bean[name] = undefined;
				} else {
					bean[name] = data[name];
				}
			}

			return bean;
		}
	};

	/**
	 * 页面导向
	 * 
	 * @method LS.forward
	 * @param {String}
	 *            url 转向的路径
	 * @param {String}
	 *            caption 转向页面的标题
	 * @param {LS.Map}
	 *            params 转向页面需要传递的参数
	 */
	LS.forward = function(url, caption, params) {
		// 传递参数
		var str = "?";
		if (params) {
			for ( var p in params.map) {
				str += p + "=" + params.get(p) + "&";
			}
		}
		str = str.substring(0, str.lastIndexOf("&"));
		url = url.trim();
		if (url.indexOf("http:") == 0 || url.indexOf("https:") == 0) {
			window.location.href = url + str;
		} else if (url.indexOf("/") == 0) {
			window.location.href = LS.webPath + url + str;
		} else {
			window.location.href = LS.webPath + "/" + url + str;
		}
	};
})(this.Zepto||this.jQuery);

/**
 * 工具类--Map
 */
(function($, undefined) {
	/**
	 * 模拟后台的Map，可构造前台Map对象
	 */
	LS.Map = function(classname, data) {
		this.javaClass = classname ? classname : "HashMap";// short name
		this.map = data ? data : new Object();
		this.length = this.size();
	};

	LS.Map.prototype = {

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
			var val = this.map[key];
			return LS.serializer.unmarshall(val);
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
(function($, undefined) {
	LS.List = function(classname, data) {
		this.javaClass = classname ? classname : "ArrayList";
		this.list = data ? data : new Array();
	};

	LS.List.prototype = {
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
			return LS.serializer.unmarshall(val);
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

(function($, undefined) {
	LS.serializer = {};
	LS.serializer.converters = {};
	LS.serializer.shortnames = {
		// 日期
		"Date" : "java.sql.Date",
		// Map
		"HashMap" : "java.util.HashMap",
		// List
		"ArrayList" : "java.util.ArrayList",
		// 结果集
		"DataSet" : "org.loushang.next.data.DataSet",
		// 记录
		"Record" : "org.loushang.next.data.Record",
		// 参数
		"ParameterSet" : "org.loushang.next.data.ParameterSet"
	};
	/**
	 * 转换对象
	 * 
	 * @method unmarshall
	 * @param {Object}
	 *            val 待转换的对象
	 * @return {Object} 转换后的值
	 */
	LS.serializer.unmarshall = function(val) {
		if (!val) {
			return val;
		}
		if (!val.javaClass) {
			return val;
		}
		var fullname = LS.serializer.shortnames[val.javaClass];
		if (fullname) {
			val.javaClass = fullname;
		}
		var converter = LS.serializer.converters[val.javaClass];
		if (converter) {
			return converter(val);
		}
		return val;
	};
})(this.Zepto||this.jQuery);

(function() {
	/**
	 * Date转换函数
	 */
	function date_convert(o) {
		return new Date(o.time);
	}
	LS.serializer.converters["java.util.Date"] = date_convert;
	LS.serializer.converters["java.sql.Date"] = date_convert;

	/**
	 * Map转换函数
	 */
	function map_convert(o) {
		return new LS.Map(o.javaClass, o.map);
	}

	LS.serializer.converters["java.util.HashMap"] = map_convert;
	LS.serializer.converters["java.util.TreeMap"] = map_convert;
	LS.serializer.converters["java.util.LinkedHashMap"] = map_convert;
	LS.serializer.converters["java.util.WeakHashMap"] = map_convert;
	// 参数
	LS.serializer.converters["org.loushang.next.data.ParameterSet"] = map_convert;

	/**
	 * List转换函数
	 */
	function list_convert(o) {
		return new LS.List(o.javaClass, o.list);
	}
	LS.serializer.converters["java.util.ArrayList"] = list_convert;
	LS.serializer.converters["java.util.LinkedList"] = list_convert;
	LS.serializer.converters["java.util.Vector"] = list_convert;
	LS.serializer.converters["java.util.Vector"] = list_convert;

	/**
	 * DataSet转换函数
	 */
	function dataSet_convert(o) {
		return new LS.List("ArrayList", o.rows);
	}
	LS.serializer.converters["org.loushang.next.data.DataSet"] = dataSet_convert;

	/**
	 * Record转换函数
	 */
	function record_convert(o) {
		return new LS.Record(o);
	}
	LS.serializer.converters["org.loushang.next.data.Record"] = record_convert;
})();

/**
 * 在Zepto命名空间上增加新函数
 */
(function($, undefined) {
	$.extend($, {
		/**
		 * 将对象转换成Json字符串
		 */
		toJSONString : function(o) {
			if (typeof o == "undefined" || o === null) {
				return "null";
			}
			// 已有toJSONString方法，直接返回
			else if (o.toJSONString) {
				return o.toJSONString();
			}
			// 数组
			else if ($.isArray(o)) {
				var results = [];
				for ( var i = 0; i < o.length; i++) {
					var value = $.toJSONString(o[i]);
					if (value !== undefined)
						results.push(value);
				}
				return '[' + results.join(',') + ']';
			}
			// 字符串
			else if (typeof o == "string") {
				return '"'
						+ o.replace(/(\\|\")/g, "\\$1").replace(
								/\n|\r|\t/g,
								function() {
									var a = arguments[0];
									return (a == '\n') ? '\\n'
											: (a == '\r') ? '\\r'
													: (a == '\t') ? '\\t' : ""
								}) + '"';
			}
			// 数字
			else if (typeof o == "number") {
				return isFinite(o) ? String(o) : "null";
			}
			// 布尔
			else if (typeof o == "boolean") {
				return String(o);
			}
			// 对象
			else if (typeof o == "object") {
				if (o === null) {
					return 'null';
				}

				var results = [];
				for ( var property in o) {
					var value = $.toJSONString(o[property]);
					if (value !== undefined) {
						results.push($.toJSONString(property) + ':' + value);
					}
				}
				return '{' + results.join(',') + '}';
			}
		}
	});
})(this.Zepto||this.jQuery);