/**
 * 运行时表单静态类
 *
 */
(function ($) {
    $.support.cors = true;

    if (typeof CForm == "undefined") {
        window.CForm = {};
    }

    CForm.webPath = ""; //rest接口Root路径
    CForm.getUIPath = "/cform/getFormUI"; //获取UI的rest接口
    CForm.loadDataPath = "/cform/getFormStandardData"; //获取数据的rest接口
    CForm.bindDataPath = "/cform/getDataBind"; //获取数据绑定的rest接口
    CForm.saveDataPath = "/cform/saveData"; //绑定数据的rest接口
    CForm.rootDirectory = "/static/cform"; //存放表单客户端js、css文件的根路径

    CForm.useCustomerTheme = false;

    /**
     * 对影响json序列化的字符进行编码
     * @param str
     * @returns {XML|string}
     */
    var encodeSpecialChar = function(str){
        return str.replace(/\t/g,"[tab]").replace(/(\r\n)|[\r\n]/g,"[br]").replace("%","[percent]");
    };

    /**
     * 对影响json序列化的字符进行解码
     * @param str
     * @returns {XML|string}
     */
    var decodeSpecialChar = function(str){
        str = decodeURI(str); //decode是为了适配部分已encode的老数据
        return str.replace(/\[tab\]/g,"\t").replace(/\[br\]/g,"\r\n").replace(/\[percent\]/g,"%");
    };

    var appendSafetyParam = function(url){
        var token = CForm.getUrlParam("token");
        var appCode = CForm.getUrlParam("appCode");
        if(token) {
            url += "&token=" + token;
        }
        if(appCode) {
            url += "&appCode=" + appCode;
        }
        return url;
    };

    /**
     * 注册事件
     */
    CForm.on = function (eventName, func) {
        $(CForm).on.apply($(CForm), arguments);
    };

    /**
     * 触发事件
     */
    CForm.trigger = function (eventName, args) {
        $(CForm).trigger.apply($(CForm), arguments);
    };

    /**
     * 获得页面上所有的表单数据
     *
     * @returns Array，每一项为一个CForm.Map
     */
    CForm.getAllFormData = function () {
        var list = [];

        $.each($("[cf_elementType=form]"), function () {
            var map = CForm.getFormData(this);
            map["formId"] = $(this).attr("id");

            list.push(map);
        });

        return list;
    };

    /**
     * 获得指定表单数据
     *
     * @param form
     *            表单
     * @returns object，key为fieldId,value为fieldValue
     *          若表单中含有动态行，则key为动态行定义ID，value为CForm.List
     */
    CForm.getFormData = function (form) {
        var $form = form ? $(form) : $("[cf_elementType=form]").eq(0);
        if ($form.length <= 0) {
            alert("请指定要要获得数据的表单!");
            return {};
        }
        var map = {};

        // 动态行
        var $dynRows = $form.find(".cfDynRow[cf_modelId][cf_elementType=zone]");
        // 动态表格
        var $dynTables = $form
            .find('.cfDynTable[cf_modelId][cf_elementType=zone]');
        // 具有modelItemId的所有域
        var fields = $form.find("[cf_modelItemId][cf_elementType=field]");

        var checkBoxMap = {};
        // 组装主表单数据
        $.each(fields, function (i, field) {
            if ($dynRows.find(field).length > 0
                || $dynTables.find(field).length > 0) {
                return;
            }
            var fieldId = field.id;
            var fieldValue = CForm.getFieldValue(field);

            if (field.type == "checkbox" || field.type == "radio") {
                if (checkBoxMap[fieldId]) {
                    if (field.getAttribute("name") != checkBoxMap[fieldId]) {
                        //多余的镜像域checkbox，不用处理
                        return;
                    }
                } else {
                    checkBoxMap[fieldId] = field.getAttribute("name");
                }
            }
            var preFieldValue = map[fieldId];
            if (preFieldValue) {
                //如果是复选框，则连接值
                if (field.type == "checkbox" && fieldValue) {
                    fieldValue = preFieldValue + "," + fieldValue;
                }
                else {
                    fieldValue = preFieldValue;
                }
            }

            if (fieldValue == null) {
                fieldValue = "";
            }
            map[fieldId] = fieldValue;
        });

        // 组装动态行数据
        $.each($dynRows, function (i, dynRow) {
            var $trs = $(dynRow).find("tbody tr");
            // 存放动态行的各行数据
            var dynRowRows = [];
            $.each($trs, function (j, tr) {
                var rowData = {};
                var $fields = $(tr).find(
                    '[cf_modelItemId][cf_elementType=field]');
                $.each($fields, function (k, field) {
                    var fieldId = $(field).attr("id");
                    var fieldValue = CForm.getFieldValue(field);
                    // 若已有数据，则用","间隔各个数据
                    var preFieldValue = rowData[fieldId];
                    if (preFieldValue) {
                        if ($.trim(fieldValue)) {
                            fieldValue = preFieldValue + "," + fieldValue;
                        } else {
                            fieldValue = preFieldValue;
                        }
                    }
                    if (fieldValue == null) {
                        fieldValue = "";
                    }
                    rowData[fieldId] = fieldValue;
                });

                dynRowRows.push(rowData);
            });
            map[$(dynRow).attr("id")] = dynRowRows;
        });

        // 组装动态表格数据
        $.each($dynTables, function (i, dynTable) {
            var $table = $('table', dynTable);
            // 存放动态表格的各表格数据
            var dynTableRows = [];
            var data = map[$(dynTable).attr("id")];
            if (data) {
                dynTableRows = data;
            }
            $.each($table, function (j, table) {
                var rowData = {};
                var $fields = $(table).find(
                    '[cf_modelItemId][cf_elementType=field]');
                $.each($fields, function (k, field) {
                    var fieldId = $(field).attr("id");
                    var fieldValue = CForm.getFieldValue(field);
                    // 若已有数据，则用","间隔各个数据
                    var preFieldValue = rowData[fieldId];
                    if (preFieldValue) {
                        if ($.trim(fieldValue)) {
                            fieldValue = preFieldValue + "," + fieldValue;
                        } else {
                            fieldValue = preFieldValue;
                        }
                    }
                    if (fieldValue == null) {
                        fieldValue = "";
                    }
                    rowData[fieldId] = fieldValue;
                });

                dynTableRows.push(rowData);
            });
            map[$(dynTable).attr("id")] = dynTableRows;
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
    CForm.setFormData = function (form, data, byAttr) {
        if (data.rows) {
            data = data.rows;
        }
        if (!byAttr) {
            // 默认根据业务模型项赋值
            byAttr = "cf_modelItemId";
        }
        var $form = $(form);
        // 为每个域赋值
        for (var key in data) {
            if(!data.hasOwnProperty(key)){
                continue;
            }
            var value = data[key];
            //console.log(key);
            //console.log(value);
            // 过滤空值
            if (!value) {
                continue;
            }

            // 动态行
            var $dynRow = $form.find(".cfDynRow[cf_modelId=" + key + "]");
            if ($dynRow.length) {
                $.each($dynRow,
                    function () {
                        // 设置动态行的第一行数据
                        var $firstTr = $("tbody tr:first [" + byAttr + "]", $(this));
                        if (!value.length) {
                            return false;
                        }
                        var firstRowData = value[0];
                        $.each($firstTr, function (idx, input) {
                            var val = firstRowData[$(input).attr(byAttr)];
                            if (val) {
                                CForm.setFieldValue(input, val);
                            }
                        });

                        // 设置动态行的其他行数据
                        if (value.length < 2) {
                            return;
                        }
                        var currentRow = $("tbody tr", $(this)).eq(0);
                        for (var i = 1; i < value.length; i++) {
                            var $newRow = CForm.addRow($(this), currentRow);
                            currentRow = $("tbody tr", $(this)).eq(i);
                            var $inputs = $("[" + byAttr + "]", $newRow);
                            $.each($inputs, function (idx, input) {
                                var val = value[i][$(input).attr(byAttr)];
                                if (val) {
                                    CForm.setFieldValue(input, val);
                                }
                            });
                        }
                    });
                // 触发列合计单击事件，计算合计值
                $(".cfColSum").trigger("click");
                continue;
            }

            // 动态表格
            var $dynTable = $form.find(".cfDynTable[cf_modelId=" + key + "]");
            if ($dynTable.length) {
                $.each($dynTable, function () {
                    if (!value.length) {
                        return false;
                    }

                    // 设置第一个表格中的所有域
                    var $fieldsOffirstTable = $('table:first [' + byAttr + ']',
                        this);
                    var firstTableData = value[0];
                    $fieldsOffirstTable.each(function (i, field) {
                        var val = firstTableData[$(field).attr(byAttr)];
                        if (val) {
                            CForm.setFieldValue(field, val);
                        }
                    });
                    // 设置动态表格的其他表格数据
                    if (value.length < 2) {
                        return false;
                    }
                    for (var i = 1; i < value.length; i++) {
                        var $newTable = CForm.addTable($(this), false);
                        var $fields = $("[" + byAttr + "]", $newTable);
                        $.each($fields,
                            function (idx, field) {
                                var val = value[i][$(field).attr(byAttr)];
                                if (val) {
                                    CForm.setFieldValue(field, val);
                                }
                            });
                    }
                });
                continue;
            }

            // 主表单
            var $field = $form.find("[" + byAttr + "=" + key + "]");
            $.each($field, function (idx, input) {
                CForm.setFieldValue(input, value);
            });
        }
        CForm.trigger("afterLoadFormData");
    };

    /**
     * 根据业务含义向指定的表单里设置实际数据
     *
     * @param form
     *            表单
     * @param data
     *            数据
     */
    CForm.setFormDataByBizMean = function (form, data) {
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
    CForm.setDataBind = function (form, data) {
        var $form = $(form);
        // 为每个域赋值
        for (var key in data) {
            if(!data.hasOwnProperty(key)){
                continue;
            }
            var value = data[key];
            // 主表单
            var $field = $form.find("[cf_modelItemId=" + key + "]");

            $.each($field, function (i, field) {
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
    CForm.setDataBindByFieldId = function (form, data) {
        var $form = $(form);
        // 为每个域赋值
        for (var key in data) {
            if(!data.hasOwnProperty(key)){
                continue;
            }
            var value = data[key];
            // 主表单
            var $field = $form.find("#" + key);

            $.each($field, function (i, field) {
                CForm.setFieldDataBind(field, value);
            });
        }
    };


    /**
     * 将若干域的值设为同步修改，将域ID传给函数即可,要求同步域的组件类型一致
     * 例如：CForm.synchroField('input1','input2','input3',...);
     * By lichao 2015-4-7
     * @returns {boolean} 成功、失败
     */
    CForm.synchroField = function () {
        var ids = arguments;
        if (ids && ids.length > 0) {
            var controlType = document.getElementById(ids[0]).type;
            var isSpecial = false;
            var event = "blur";

            if (controlType == "radio" || controlType == "checkbox") {
                isSpecial = true;
                event = "click";
            }

            if (controlType == "select-one" || controlType == "select-multiple") {
                event = "change";
            }

            for (var j = 0; j < ids.length; j++) { //循环绑定事件，触发值同步
                $("[cf_modelitemid=" + ids[j] + "]").bind(event, function () {
                    for (var k = 0; k < ids.length; k++) {
                        var to;
                        if (isSpecial) {
                            to = $("input[cf_modelitemid=" + ids[k] + "][value=" + this.value + "]");
                            to.prop("checked", this.checked);
                        } else {
                            to = $("[cf_modelitemid=" + ids[k] + "]");
                            to.val($(this).val());
                        }
                    }
                });
            }
            return true;
        }
        return false;
    };

    /**
     * 获得域值
     *
     * @param field
     *            域
     */
    CForm.getFieldValue = function (field) {
        var fieldValue = "";

        switch (field.type) {
            // 列表框
            case "select-multiple":
                // 选中项
                var $options = $("option:selected", $(field));
                if ($options.length) {
                    var valArray = [];
                    $.each($options, function (i, option) {
                        valArray.push($(option).val());
                    });
                    fieldValue = valArray.join(",");
                }

                break;
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
            // 隐藏域
            case "hidden":
            // 下拉框
            case "select-one":
            // 多行文本框
            case "textarea":
            // 单行文本框
            case "text":
                fieldValue = encodeSpecialChar($.trim(field.value)); //编码是为了处理Tab、回车等不符合json规范的字符
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
    CForm.setFieldValue = function (field, value) {
        var valArray;
        switch (field.type) {
            // 列表框
            case "select-multiple":
                // 实际值
                if (value) {
                    valArray= value.split(",");
                    var $options = $(field).find("option");
                    $.each($options, function (i, option) {
                        // 判断选项值是否在数组中
                        if ($.inArray($(option).val(), valArray) >= 0) {
                            $(option).attr("selected", true);
                        }
                    });
                }
                break;
            case "radio":
                if (value && value == $(field).val()) {
                    $(field).prop("checked", true);
                }
                break;
            case "checkbox":
                if (value) {
                    valArray = value.split(",");
                    for (var i = 0; i < valArray.length; i++) {
                        if (valArray[i] == $(field).val()) {
                            $(field).prop("checked", true);
                            break;
                        }
                    }
                }
                break;
            case "textarea":
                if (value) {
                    $(field).val(decodeSpecialChar(value));
                    // 高度自适应
                    $(field).trigger("change");
                }
                break;
            default:
                if (value) {
                    $(field).val(decodeSpecialChar(value));
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
    CForm.setFieldDataBind = function (field, value) {
        switch (field.type) {
            case "select-one":
            case "select-multiple":
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
    CForm.validate = function () {
        // 校验结果
        var isValid = true;

        // 非空校验
        $(".cfIsRequired").each(
            function (i, field) {
                var $field = $(field);
                switch (field.type) {
                    // 列表框
                    case "select-multiple":
                        // 至少选中一项
                        var $options = $field.find("option:selected");
                        if (!$options.length) {
                            isValid = false;
                        }

                        break;
                    // 单选按钮
                    case "radio":
                        // 同一组中至少有一个选中
                        var $radios = $("input[type=radio][name="
                        + $field.attr("name") + "]:checked");
                        if (!$radios.length) {
                            isValid = false;
                        }

                        break;
                    // 复选框
                    case "checkbox":
                        // 同一组中至少有一个选中
                        var $checkboxes = $("input[type=checkbox][name="
                        + $field.attr("name") + "]:checked");
                        if (!$checkboxes.length) {
                            isValid = false;
                        }

                        break;
                    default:
                        if (!$.trim($field.val())) {
                            isValid = false;
                        }

                        break;
                }

                if (!isValid) {
                    CForm.showTips($field, "cfIsRequired");
                    return false;
                }
            });

        if (!isValid) {
            return false;
        }

        // 数据类型校验
        $('.cfIsInteger').each(function (i, field) {
            var $field = $(field);
            var val = $.trim($field.val());
            if (val && !CForm.isInteger(val)) {
                CForm.showTips($field, "cfIsInteger");
                isValid = false;
                return false;
            }
        });

        if (!isValid) {
            return false;
        }

        // 小数校验
        $('.cfIsFloat').each(function (i, field) {
            var $field = $(field);
            var val = $.trim($field.val());
            if (val) {
                if (!CForm.isFloat(val)) {
                    // 不是小数，校验是否整数
                    if (!CForm.isInteger(val)) {
                        CForm.showTips($field, "cfIsFloat");
                        isValid = false;
                        return false;
                    }
                } else {
                    // 精确度校验
                    var precision = $field.attr("cf_fieldPrecision");
                    if (precision) {
                        var floatLength = val.toString().split(".")[1].length;
                        if (floatLength > precision) {
                            CForm.showTips($field, "cfIsFloat", precision);
                            isValid = false;
                            return false;
                        }
                    }
                }
            }
        });

        if (!isValid) {
            return false;
        }

        // 邮政编码校验
        $('.cfIsZipCode').each(function (i, field) {
            var $field = $(field);
            var val = $.trim($field.val());
            if (val && !CForm.isZipCode(val)) {
                CForm.showTips($field, "cfIsZipCode");
                isValid = false;
                return false;
            }
        });

        if (!isValid) {
            return false;
        }

        // 电子邮件校验
        $('.cfIsEmail').each(function (i, field) {
            var $field = $(field);
            var val = $.trim($field.val());
            if (val && !CForm.isEmail(val)) {
                CForm.showTips($field, "cfIsEmail");
                isValid = false;
                return false;
            }
        });

        if (!isValid) {
            return false;
        }

        // 正则表达式校验
        $('.cfIsRegExp').each(function (i, field) {
            var $field = $(field);
            var reg = $field.attr("cf_regExp");
            if (reg) {
                var val = $.trim($field.val());
                if (val && !eval(reg).test(val)) {
                    CForm.showTips($field, "cfIsRegExp");
                    isValid = false;
                    return false;
                }
            }
        });

        return isValid;
    };


    /**
     * 提示校验信息
     */
    CForm.showTips = function ($field, className, param) {
        var msg = "";

        // 动态行域判断
        var $dynRows = $field.parents(".cfDynRow");
        if ($dynRows.length) {
            var $dynRow = $dynRows.eq(0);
            var dName = $dynRow.attr("name");
            var rowNum = $field.parents("tr").eq(0).index() + 1;
            var colNum = $field.parent("td").index() + 1;
            msg = "动态行[" + dName + "]第" + rowNum + "行，第" + colNum + "列中";
        }

        var fName = $field.attr("name");

        switch (className) {
            // 必填
            case "cfIsRequired":
                msg += fName + "不能为空！";
                break;
            // 整数
            case "cfIsInteger":
                msg += fName + "只能输入整数！";
                break;
            // 小数
            case "cfIsFloat":
                if (param) {
                    msg += fName + "的精确度大于" + param + "！";
                } else {
                    msg += fName + "只能输入小数！";
                }
                break;
            // 邮件编码
            case "cfIsZipCode":
                msg += fName + "的值不符合邮政编码格式！";
                break;
            // 电子邮件
            case "cfIsEmail":
                msg += fName + "的值不符合电子邮件格式！";
                break;
            // 正则表达式
            case "cfIsRegExp":
                msg += fName + "的值不符合规则";
                break;
            default:
                msg += fName + "的值不合法！";
                break;
        }
        // 弹出框
        //如果引入了artdialog，则使用artdialog 的弹出框
        if (window.art) {
            art.dialog.through({
                title: "表单验证",
                content: msg,
                lock: true,
                icon: "error",
                ok: function () { //点确定
                    $field.focus();
                    return true;
                }
            });
        } else {
            alert(msg);
            $field.focus();
        }
    };

    /**
     * 验证值是否为整数
     */
    CForm.isInteger = function (val) {
        return /^(?:-?|\+?)\d+$/g.test(val);
    };

    /**
     * 判断值是否为小数
     */
    CForm.isFloat = function (val) {
        return /^(?:-?|\+?)\d*\.\d+$/g.test(val);
    };

    /**
     * 判断值是否为邮政编码
     */
    CForm.isZipCode = function (val) {
        return /^\d{6}$/g.test(val);
    };

    /**
     * 判断值是否为电子邮件
     */
    CForm.isEmail = function (val) {
        return /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/g.test(val);
    };

    /**
     * 将域设置为只读
     */
    CForm.setFieldReadOnly = function (field) {
        var $field = $(field);
        var fieldValue = "";

        //隐藏域无需处理
        if ($field.hasClass("cfHidden"))
            return;

        //单元格内其它元素处理
        var td = $field.parent("td");
        if (td) {
            // 去掉必填符号
            var requiredTag = td.find("span,font");
            if (requiredTag.length) {
                requiredTag.remove();
            }
        }

        // 根据域类型获取域值
        switch (field.type) {
            // 列表框
            case "select-multiple":
            // 下拉框
            case "select-one":
                // 选中项
                var $options = $("option:selected", $field);
                if ($options.length) {
                    var valArray = [];
                    $.each($options, function () {
                        if ($(this).val()) {
                            valArray.push($(this).text());
                        }
                    });
                    fieldValue = valArray.join(",");
                }
                break;
            // 多行文本框
            case "textarea":
            // 单行文本框
            case "text":
                fieldValue = $.trim($field.val());
                break;
            // 单选按钮
            case "radio":
                if (!field.checked) {
                    // 用""替换后面的文本
                    field.nextSibling.nodeValue = "";
                }
                break;
            // 复选框
            case "checkbox":
                if (!field.checked) {
                    // 用""替换后面的文本
                    field.nextSibling.nodeValue = "";
                } else {
                    var prevs = $field.prevAll();
                    $.each(prevs, function () {
                        if ($(this).is(":checked")) {
                            fieldValue = ",";
                            return false;
                        }
                    });
                }
                break;
            // 按钮
            case "button":
                fieldValue = "";
                break;
        }
        // 正则表达式替换是为了处理多行文本框的换行
        var reg = new RegExp("\n", "g");
        // 隐藏并在后面添加显示文本
        $field.hide().after(fieldValue.replace(reg, "<br/>"));
    };

    /**
     * 将表单所有可见域设置为只读
     */
    CForm.setFormReadOnly = function (form) {
        var $form = form ? $(form) : $("[cf_elementType=form]").eq(0);
        if (!$form.length) {
            alert("未获取到要操作的表单！");
            return;
        }
        // 处理序号列的背景色
        var $trs = $form.find(".cfDynRow tbody tr");
        $.each($trs, function () {
            $("td:first", $(this)).addClass("cfIsReadonly");
        });

        var fields = $form.find("[cf_elementType=field]");
        $.each(fields, function () {
            // 设置域只读
            CForm.setFieldReadOnly(this);
        });
    };


    CForm.setTabReadOnly = function (tab) {
        var $tab = tab;
        if (typeof(tab) == "string") {
            $tab = $("#" + tab);
        }
        var fields = $tab.find("[cf_elementType=field]");
        $.each(fields, function () {
            CForm.setFieldReadOnly(this);
        });
    };


    /**
     * 设置指定字段为必填
     * @param fieldId
     */
    CForm.setFieldRequired = function (fieldId) {
        var $field = $("#" + fieldId);
        $field.addClass("cfIsRequired");
        $field.after('<span style="color:red">*</span>');
    };

    /**
     * 设定指定字段为非必填
     * @param fieldId
     */
    CForm.setFieldNotRequired = function (fieldId) {
        var $field = $("#" + fieldId);
        $field.removeClass("cfIsRequired");
        var $nextP = $("#" + fieldId + "+span");
        $nextP.remove();
    };

    /**
     * 将所有域替换为标签(为兼容保留)
     */
    CForm.setToLabel = CForm.setFormReadOnly;

    /**
     * 获取行（列）合计关联的表单域
     */
    CForm.getSumFieldRefs = function (sumField) {
        // 返回值
        var refFields = [];
        // 合计域
        var $sumField = $(sumField);
        // 如果是行合计
        if ($sumField.hasClass("cfRowSum")) {
            var sumRule = $sumField.attr("cf_sumRule");
            if (sumRule) {
                var sumRuleArr = sumRule.split("#");
                var operation = "+-*/()";
                for (var i = 0; i < sumRuleArr.length; i++) {
                    var sumRuleItem = sumRuleArr[i];
                    // 判断sumRuleItem是否为操作符
                    if (operation.indexOf(sumRuleItem) == -1) {
                        // 域ID放入返回值
                        refFields.push(sumRuleItem);
                    }
                }
            }
        }
        // 列合计
        else if ($sumField.hasClass("cfColSum")) {
            var index = $sumField.parent().prevAll().length;
            var $tbody = $sumField.parents("tfoot").next();
            var $td = $("tr:first td:eq(" + index + ")", $tbody);
            var refFieldId = $("[cf_elementType=field]", $td).attr("id");
            if (refFieldId) {
                refFields.push(refFieldId);
            }
        }
        return refFields;
    };

    /**
     * 动态行动态建行
     */
    CForm.addRow = function (dynRow, curRow) {
        var tr = $("tbody tr:first", dynRow);
        var $clone = tr.clone(true);

        // 清空输入域的值(按钮、单选、复选的值不能清空)
        $(":input[type!=button][type!=radio][type!=checkbox]", $clone).val("");
        // 设置单选按钮为未选中状态，并修改name属性，否则会与其他行的单选按钮成一组
        $(":radio", $clone).attr({
            checked: false,
            //使用时间使name唯一
            name: "r_" + new Date().getTime()
        });
        // 设置复选框为未选中状态
        $(":checkbox", $clone).attr("checked", false);
        if (curRow) {
            curRow.after($clone);
        } else {
            $("tbody", dynRow).append($clone);
        }
        // 序号重排
        var $trs = $("tbody tr", dynRow);
        $.each($trs, function (i, tr) {
            // 序号取行号
            $("td:first label", $(tr)).text(i + 1);
        });

        return $clone;
    };

    /**
     * 动态行动态删除行
     */
    CForm.delRow = function (dynRow, curRow) {
        var $ltr = $("tbody tr:last", dynRow);
        var $ftr = $("tbody tr:first", dynRow);
        if ($ftr.is($ltr)) {
            // 清空输入域的值(按钮、单选、复选的值不能清空)
            $ftr.find(":input[type!=button][type!=radio][type!=checkbox]").val("");
            // 设置单选按钮为未选中状态
            $ftr.find(":radio").attr("checked", false);
            // 设置复选框为未选中状态
            $ftr.find(":checkbox").attr("checked", false);
            return;
        }
        curRow.remove();
        // 序号重排
        var $trs = $("tbody tr", dynRow);
        $.each($trs, function (i, tr) {
            // 序号取行号
            $("td:first label", $(tr)).text(i + 1);
        });
    };

    /**
     * 动态表格增加表格
     */
    CForm.addTable = function (dynTable, isAfter) {
        var $clone = dynTable.clone(true);
        // 清空输入域的值(按钮、单选、复选的值不能清空)
        $(":input[type!=button][type!=radio][type!=checkbox]", $clone).val("");
        // 设置单选按钮为未选中状态，并修改name属性，否则会与其他表格的单选按钮成一组
        $(":radio", $clone).attr({
            checked: false,
            //使用时间使name唯一
            name: "r_" + new Date().getTime()
        });
        // 设置复选框为未选中状态
        $(":checkbox", $clone).attr("checked", false);
        if (isAfter) {
            dynTable.after($clone);
        } else {
            $('[id=' + dynTable.attr("id") + '][class=cfDynTable]:last').after($clone);
        }
        return $clone;
    };

    /**
     * 动态表格删除表格
     */
    CForm.delTable = function (dynTable) {
        var len = $('[id=' + dynTable.attr("id") + '][class=cfDynTable]').length;
        if (len == 1) {
            // 清空输入域的值(按钮、单选、复选的值不能清空)
            dynTable.find(':input[type!=button][type!=radio][type!=checkbox]').val('');
            // 设置单选按钮为未选中状态
            dynTable.find(":radio").attr("checked", false);
            // 设置复选框为未选中状态
            dynTable.find(":checkbox").attr("checked", false);
            return;
        }
        dynTable.remove();
    };

    /**
     * 新增序号列
     */
    CForm.loadSubTblNum = function (form) {
        var $form = form ? $(form) : $('[cf_elementType=form]').eq(0);
        if (!$form.length) {
            alert('未获取到要操作的表单！');
            return;
        }
        // 动态行
        var $drs = $form.find(".cfDynRow[cf_modelId]");
        if ($drs.length) {
            // 添加设置换行框
            $form.append("<div class='cfChangeRowDiv'>"
            + "<label>输入行号&nbsp;(1-<span></span>)：</label>"
            + "<input type='text' id='cfRowNumIn' name='cfRowNumIn'/>"
            + "<label class='cfTipLabel'></label>"
            + "<a href='javascript:;' id='cancelBtn' class='cfCancelBtn'>取消</a>"
            + "<a href='javascript:;' id='confirmBtn' class='cfConfirmBtn'>确定</a>"
            + "</div>");
            $.each($drs, function () {
                // 添加序号和主键隐藏域
                $("<td style='text-align:center;'><input type='hidden' id='SUB_TBL_PK' name='主键' " +
                "cf_modelItemId='SUB_TBL_PK' cf_modelItemName='主键' " +
                "cf_elementType='field' /><label>1</label></td>")
                    .insertBefore($("tbody tr:first td:first", $(this)));
                // 添加序号列标题
                $("<th style='width:50px;'>序号</th>").insertBefore($("thead tr th:first", $(this)));
                // 合计行添加空td
                $("<td></td>").insertAfter($("tfoot tr td:first", $(this)));
            });
        }
        // 动态表格
        var $dts = $form.find(".cfDynTable[cf_modelId]");
        if ($dts.length) {
            $.each($dts, function () {
                // 添加主键隐藏域
                $("table:first tr:first td:first", $(this)).append(
                    "<input type='hidden' id='SUB_TBL_PK' name='主键' " +
                    "cf_modelItemId='SUB_TBL_PK' cf_modelItemName='主键' " +
                    "cf_elementType='field' />");
            });
        }
    };

    /**
     * tab组件初始化
     */
    CForm.initTab = function () {
        // Tab页组件
        $(".cfTab li").click(function () {
            var lis = $(this).parent().children("li");
            var panels = $(".cfTabBody");
            var index = $(this).index();

            if (panels.eq(index)[0]) {
                // 移除所有li的选中
                lis.removeClass("selected");
                // 移除li的左圆角以及右圆角选中状态
                lis.find('span').removeClass('tabLeftSelected').removeClass(
                    'tabRightSelected');

                // 当前选中的li
                var selectedLi = lis.eq(index);
                selectedLi.addClass("selected");

                // 显示当前选中li的左圆角
                selectedLi.find('span').eq(0).addClass('tabLeftSelected');
                // 显示当前选中li的右圆角
                selectedLi.find('span').eq(2).addClass('tabRightSelected');

                // 显示体部
                panels.addClass("hide").eq(index).removeClass("hide");
            }
        });
    };

    /**
     * 日期、日期时间组件初始化
     */
    CForm.initDate = function () {
        // 日期时间框组件
        $(".cfDateTime").each(function () {
            if (window.CFormDateTime) {
                CFormDateTime(this);
            }
        });

        // 日期框组件
        $(".cfDate").each(function () {
            if (window.CFormDate) {
                CFormDate(this);
            }
        });
    };

    /**
     * 动态行初始化
     */
    CForm.initDynRow = function () {
        // 加载序号列
        CForm.loadSubTblNum();

        // 当前动态行
        var $curDynRow = null;
        // 动态行当前行
        var $curTr = null;

        // 监听动态行单击事件，解析行（列）合计关联表单域
        $(".cfDynRow").click(function () {
            /**
             * 解析行（列）合计关联的表单域，绑定表单域change事件
             */
            // 记录已绑定的表单域ID，避免重复绑定
            var bindedField = {};
            // 行合计
            $(".cfRowSum").each(function () {
                var refFields = CForm.getSumFieldRefs(this);
                if (refFields.length) {
                    for (var i = 0; i < refFields.length; i++) {
                        var fieldId = refFields[i];
                        if (!bindedField[fieldId]) {
                            $("#" + fieldId).live("change", function () {
                                $(this).parents("tr")
                                    .find(".cfRowSum").trigger("click");
                            });
                            bindedField[fieldId] = fieldId;
                        }
                    }
                }
            });
            // 列合计
            $(".cfColSum").each(function () {
                var globalObj = this;
                var refFields = CForm.getSumFieldRefs(globalObj);
                if (refFields.length) {
                    for (var i = 0; i < refFields.length; i++) {
                        var fieldId = refFields[i];
                        $("#" + fieldId).bind("keyup", function () {
                            var v = $(this).val();
                            if (v) {
                                // 校验数字
                                if (!$.isNumeric($(this).val())) {
                                    alert("合计域只能输入数字！");
                                    return false;
                                }
                            }
                            // 列合计
                            $(globalObj).trigger("click");

                        });
                    }
                }
            });
            // 取消绑定
            $(".cfDynRow").unbind("click");
        });

        // 增加一行按钮
        $(".cfAddRow").click(
            function () {
                var $dynRow = $(this).parents(
                    ".cfDynRow[cf_elementType=zone]").eq(0);
                var $curRow = $(this).parents("tr").eq(0);
                CForm.addRow($dynRow, $curRow);
            });

        // 删除一行按钮
        $(".cfDelRow").click(
            function () {
                var $dynRow = $(this).parents(
                    ".cfDynRow[cf_elementType=zone]").eq(0);
                var $curRow = $(this).parents("tr").eq(0);
                CForm.delRow($dynRow, $curRow);
            });

        // 换行
        $(".cfChangeRow").click(
            function (e) {
                $curTr = $(this).parents("tr").eq(0);
                $curDynRow = $(this).parents("tbody").eq(0);
                // 清空已有值
                $(".cfChangeRowDiv input").val("");
                // 提示信息
                $(".cfChangeRowDiv span").text($("tr", $curDynRow).length);
                $(".cfChangeRowDiv").css({
                    "left": e.pageX - 175 + "px",
                    "top": e.pageY + "px"
                }).show();
            });

        // 取消换行
        $(".cfCancelBtn").click(
            function () {
                $(".cfChangeRowDiv label:eq(1)").text("");
                $(".cfChangeRowDiv").hide();
            });

        // 确定换行
        $(".cfConfirmBtn").click(
            function () {
                var rowNum = $("#cfRowNumIn").val();
                var totalRow = $(".cfChangeRowDiv span").text();
                // 值必须介于1到动态行行数之间
                var $label = $(".cfChangeRowDiv label:eq(1)");
                if (!rowNum || rowNum < 1 || rowNum > totalRow) {
                    $label.text("请输入1到" + totalRow + "之间的整数！");
                    return false;
                }
                // 清空提示
                $label.text("");
                var curRowNum = $curTr.index() + 1;
                if (rowNum != curRowNum) {
                    // 换行
                    if (rowNum < curRowNum) {
                        $curTr.insertBefore($("tr:eq(" + (rowNum - 1) + ")", $curDynRow));
                    } else {
                        $curTr.insertAfter($("tr:eq(" + (rowNum - 1) + ")", $curDynRow));
                    }
                    // 序号重排
                    var $trs = $("tr", $curDynRow);
                    $.each($trs, function (i, tr) {
                        // 序号取行号
                        $("td:first label", $(tr)).text(i + 1);
                    });
                }
                // 隐藏设置序号框
                $(".cfChangeRowDiv").hide();
            });

        // 行合计
        $(".cfRowSum").click(function () {
            var sumRule = $(this).attr("cf_sumRule");
            if (sumRule) {
                var sumRuleArr = sumRule.split("#");
                var operation = "+-*/()";
                for (var i = 0; i < sumRuleArr.length; i++) {
                    var sumRuleItem = sumRuleArr[i];
                    // sumRuleItem不是操作符
                    if (operation.indexOf(sumRuleItem) == -1) {
                        // 获取同行中该域的值
                        var curTr = $(this).parents("tr").eq(0);
                        // 校验合计域是否存在
                        var $sumF = $("#" + sumRuleItem, curTr);
                        if (!$sumF.length) {
                            alert("合计域[" + sumRuleItem + "]不存在！");
                            sumRuleArr = [];
                            return false;
                        }
                        var fieldValue = CForm.getFieldValue($("#" + sumRuleItem, curTr)[0]);
                        if (!fieldValue) {
                            fieldValue = 0;
                        }
                        sumRuleArr[i] = fieldValue;
                    }
                }
                var valueExp = sumRuleArr.join("");
                var val = null;
                try {
                    val = eval(valueExp);
                } catch (e) {
                    alert("合计发生异常！请检查表达式及域值是否合法！");
                    return false;
                }
                $(this).val(val);
            }
        });

        // 列合计
        $(".cfColSum").click(function () {
            var sumValue = 0;
            var index = $(this).parent().prevAll().length;
            var $tbody = $(this).parents("table").find("tbody");
            $("tr", $tbody).each(function () {
                var $td = $(this).children().eq(index);
                var fieldValue = $("[cf_elementType=field]", $td).val();
                if (fieldValue) {
                    sumValue += parseFloat(fieldValue);
                }
            });
            $(this).val(sumValue);
        });
    };

    /**
     * 动态表格初始化
     */
    CForm.initDynTable = function () {
        // 增加一个表格
        $('.cfAddTable').click(
            function () {
                var $dynTable = $(this).parents(
                    ".cfDynTable[cf_elementType=zone]").eq(0);
                CForm.addTable($dynTable, true);
            });

        // 删除一个表格
        $('.cfDelTable').click(
            function () {
                var $dynTable = $(this).parents(
                    ".cfDynTable[cf_elementType=zone]").eq(0);
                CForm.delTable($dynTable);
            });
    };

    /**
     * 初始化镜像域
     */
    CForm.initMirrorField = function () {
        $("[cf_isMirrorField=true]").each(function () {
            CForm.synchroField(this.id);
        });
    };

    /**
     * 域长度控制初始化
     */
    CForm.initFieldLengthCtr = function () {
        // 控制text的长度
        $(".cfText").keyup(function () {
            var len = parseInt($(this).attr("cf_fieldLength"));
            if (!len) {
                len = 100;
            }

            var strVal = $(this).val().toString() + "";
            //预期计数：中文2字节，英文1字节
            var a = 0;
            //临时字串
            var temp = '';
            for (var i = 0; i < strVal.length; i++) {
                if (strVal.charCodeAt(i) > 255) {
                    //按照预期计数增加2
                    a += 2;
                }
                else {
                    a++;
                }
                //如果增加计数后长度大于限定长度，就直接返回临时字符串
                if (a > len) {
                    $(this).val(temp);
                    return false;
                }
                //将当前内容加到临时字符串
                temp += strVal.charAt(i);
            }
        });

        // 控制textarea的长度
        $(".cfTextArea").keyup(function () {
            var len = parseInt($(this).attr("cf_fieldLength"));
            if (!len) {
                len = 500;
            }

            var strVal = $(this).val().toString() + "";
            //预期计数：中文2字节，英文1字节
            var a = 0;
            //临时字串
            var temp = '';
            for (var i = 0; i < strVal.length; i++) {
                if (strVal.charCodeAt(i) > 255) {
                    //按照预期计数增加2
                    a += 2;
                }
                else {
                    a++;
                }
                //如果增加计数后长度大于限定长度，就直接返回临时字符串
                if (a > len) {
                    $(this).val(temp);
                    return false;
                }
                //将当前内容加到临时字符串
                temp += strVal.charAt(i);
            }
        });
    };

    /**
     * 多行文本框高度自适应
     */
    CForm.initAutoHeight = function () {
        // 高度自适应
        var opt = {
            minHeight: 80,
            maxHeight: 200
        };
        $("textarea[cf_elementType=field]").each(function () {
            $(this).textareaAutoHeight(opt);
        });
    };

    /**
     * 通用帮助初始化
     */
    CForm.initCommonHelp = function () {
        // 组织通用帮助
        $(".cfOrganCommonHelp").click(function () {
            // 选择类型
            var selectType = $(this).attr('cf_selectType');
            // 是否启用数据权限
            var isUseDataPermit = $(this).attr('cf_isUseDataPermit');

            // 调用BSP组织通用帮助
            if (window.selectOrgan) {
                var rtnVal = selectOrgan(selectType, isUseDataPermit);
                if (rtnVal) {
                    var organIdStr = rtnVal.organId.join(',');
                    var organNameStr = rtnVal.organName.join(',');
                    var organIdField = $(this).attr('cf_organIdField');
                    var organNameField = $(this).attr('cf_organNameField');
                    var curTd = $(this).parent();
                    if (!(organIdStr && organIdStr != ',,,,,,,')) {
                        organIdStr = organNameStr = '';
                    }
                    $('#' + organIdField, curTd).val(organIdStr);
                    $('#' + organNameField, curTd).val(organNameStr);
                }
            }
        });
    };

    /**
     * 获取 URL 参数
     * @param name
     * @returns {*}
     */
    CForm.getUrlParam = function (name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
        var r = window.location.search.substr(1).match(reg);
        if (r != null)
            return decodeURIComponent(r[2]);
        return null;
    };

    CForm.setThemes = function (UIContainer) {
        if(CForm.useCustomerTheme){
            return;
        }

        var customSkin = UIContainer.find("form").attr("cf_themeid");
        if (customSkin != null && customSkin.length > 0) {
            var cssUrl = CForm.webPath + CForm.rootDirectory + "/skin/" + customSkin + "/skin.css";
            var link = '<link id="bizLink" rel="stylesheet" type="text/css" href="' + cssUrl + '"/>';
            var $head = $("head");
            $head.append(link);
        }
    };

    //将Json对象转化为路径参数
    var getParamPathFromJson = function (params) {
        var paramPath = "";
        if (params) {
            if(typeof(params)=="string"){
                if(params.indexOf("&") != 0){
                    params="&" + params;
                }
                paramPath = params
            }else {
                for (var itemField in params) {
                    if(params.hasOwnProperty(itemField)) {
                        paramPath = paramPath + "&" + itemField + "=" + params[itemField];
                    }
                }
            }
        }
        return paramPath;
    };

    /**
     * 加载表单UI
     * 表单UI和Data是分别加载的，这里仅加载Html代码，不包含数据
     * @param UIContainer 用于放置表单的Div，传递div对象或其ID均可
     * @param params 用于将额外的参数附加到url地址，接受一个json对象，或一个&拼接的字符串
     */
    CForm.loadUI = function (UIContainer, params) {
        var formId = CForm.getUrlParam("formId");
        if (!formId) {
            alert("该事项未配置具体办理信息，暂不能使用");
            return;
        }
        //加载表单
        $.ajax({
            url: appendSafetyParam(CForm.webPath + CForm.getUIPath + "?formId=" + formId + getParamPathFromJson(params)),
            async: false,
            type: "POST",
            dataType: "text",
            success: function (options, success, response) {
                if (typeof(UIContainer) == "string") {
                    UIContainer = $("#" + UIContainer);
                }
                //加载 UI
                UIContainer.html(response.responseText);
                //装载皮肤
                CForm.setThemes(UIContainer);
                //初始化复控件
                CForm.init();
                CForm.trigger("afterLoadFormUI");
                UIContainer.show();
            }
        });
    };


    /**
     * 加载表单数据
     * @param params 用于将额外的参数附加到url地址，接受一个json对象，或一个&拼接的字符串
     */
    CForm.loadData = function (params) {
        CForm.loadBindData(params,function () {
            CForm.loadMainData(params);
        });
    };


    /**
     * 加载表单主数据
     * @param params 用于将额外的参数附加到url地址，接受一个json对象，或一个&拼接的字符串
     * @param callback 加载完毕的回调函数
     */
    CForm.loadMainData= function(params,callback){
        var formId = CForm.getUrlParam("formId");
        var dataId = CForm.getUrlParam("dataId");
        if (dataId) {
            $.ajax({
                url: appendSafetyParam(CForm.webPath + CForm.loadDataPath + "?formId=" + formId + "&formDataId=" + dataId+ getParamPathFromJson(params)),
                async: false,
                type: "POST",
                dataType: "json",
                success: function (options, success, response) {
                    if (response) {
                        var resutlJson = response.responseJSON;
                        if(!resutlJson){
                            resutlJson = JSON.parse(response.responseText);
                        }
                        CForm.setFormData("form", resutlJson);
                        if(callback) {
                            callback(resutlJson);
                        }
                    }
                }
            });
        }
    };

    /**
     * 加载字段动态绑定的数据
     * @param params 用于将额外的参数附加到url地址，接受一个json对象，或一个&拼接的字符串
     * @param callback 加载完毕的回调函数
     */
    CForm.loadBindData = function (params,callback) {
        var formId = CForm.getUrlParam("formId");
        var url = appendSafetyParam(CForm.webPath + CForm.bindDataPath + "?formId=" + formId+ getParamPathFromJson(params));
        $.getJSON(url, {}, function (data, textStatus, response ) {
            var resutlJson = response.responseJSON;
            if(!resutlJson){
                resutlJson = JSON.parse(response.responseText);
            }
            CForm.setDataBind($("#" + formId), resutlJson);
            if(callback) {
                callback(resutlJson);
            }
        });
    };

    CForm.OUT_FORM_VALIDATE = true;

    /**
     * 保存表单
     * @param UIContainer 用于放置表单的Div，传递div对象或其ID均可
     * @param callbackFunc 保存成功回调函数
     * @param params 用于将额外的参数附加到url地址，接受一个json对象，或一个&拼接的字符串
     * @param isForceCreate 用于标识是否强制新建表单
     */
    CForm.saveData = function (UIContainer, callbackFunc,params,isForceCreate) {
        CForm.OUT_FORM_VALIDATE = true;
        CForm.trigger("beforeSaveFormData");
        if (!CForm.OUT_FORM_VALIDATE) {
            return false;
        }
        var bOk = CForm.validate();
        if (!bOk) {
            return false;
        }
        CForm.saveDataNotValidate(UIContainer, callbackFunc,params,isForceCreate);
    };

    /**
     * 在不验证数据的情况下保存表单，一般用于保存草稿
     * @param UIContainer 用于放置表单的Div，传递div对象或其ID均可
     * @param callbackFunc 保存完毕回调函数
     * @param params 用于将额外的参数附加到url地址，接受一个json对象，或一个&拼接的字符串
     * @param isForceCreate 用于标识是否强制新建表单
     */
    CForm.saveDataNotValidate = function (UIContainer, callbackFunc,params,isForceCreate) {

        if (typeof(UIContainer) == "string") {
            UIContainer = $("#" + UIContainer);
        }
        var data = CForm.getFormData(UIContainer);
        var formId = CForm.getUrlParam("formId");
        var formDataId = CForm.getUrlParam("dataId");
        if(!isForceCreate){
            if (formDataId != null && formDataId != "undefined" && formDataId.length > 0) {
                data["formDataId"] = formDataId;
            }
        }

        $.ajax({
            url: appendSafetyParam(CForm.webPath + CForm.saveDataPath + "?formId=" + formId+ getParamPathFromJson(params)),
            async: false,
            crossDomain: true,
            type: "POST",
            data: JSON.stringify(data),
            dataType: "json",
            contentType: "application/json",
            success: function (options, success, response) {
                var resutlJson = response.responseJSON;
                if(!resutlJson){
                    resutlJson = JSON.parse(response.responseText);
                }
                callbackFunc(resutlJson);
            },
            error: function (e) {
                var resutlJson = e.responseJSON;
                if(!resutlJson){
                    resutlJson = JSON.parse(e.responseText);
                }
                callbackFunc(resutlJson);
            }
        });
        return true;
    };

    /**
     * 初始化表单页面，要求页面请求参数为 xxx?formId=xxx&dataId=xxx
     */
    CForm.init = function () {

        //新版本 Jquery 不支持$.browser,适配浏览器
        if (!$.browser) {
            $.browser = {};
            $.browser.mozilla = /firefox/.test(navigator.userAgent.toLowerCase());
            $.browser.webkit = /webkit/.test(navigator.userAgent.toLowerCase());
            $.browser.opera = /opera/.test(navigator.userAgent.toLowerCase());
            $.browser.msie = /msie/.test(navigator.userAgent.toLowerCase());
        }

        //初始化镜像字段
        CForm.initMirrorField();

        // 初始化域长度控制
        CForm.initFieldLengthCtr();

        // 多行文本框高度自适应
        CForm.initAutoHeight();

        // 初始化tab
        if ($(".cfTab li").length) {
            CForm.initTab();
        }

        // 初始化日期、日期时间
        if ($(".cfDateTime,.cfDate").length) {
            CForm.initDate();
        }

        // 初始化通用帮助
        if ($(".cfOrganCommonHelp").length) {
            CForm.initCommonHelp();
        }

        // 初始化动态行
        if ($(".cfDynRow").length) {
            CForm.initDynRow();
        }

        // 初始化动态表格
        if ($(".cfDynTable").length) {
            CForm.initDynTable();
        }
    };

})(jQuery);


/**
 * 工具类--多行文本框自适应
 */
(function ($) {
    $.fn.extend({
        textareaAutoHeight: function (options) {
            this._options = {
                minHeight: 0,
                maxHeight: 1000
            };

            this.init = function () {
                for (var p in options) {
                    if(options.hasOwnProperty(p)) {
                        this._options[p] = options[p];
                    }
                }
                if (this._options.minHeight == 0) {
                    this._options.minHeight = parseFloat($(this).height());
                }
                for (var q in this._options) {
                    if(this._options.hasOwnProperty(q)) {
                        if ($(this).attr(q) == null) {
                            $(this).attr(q, this._options[q]);
                        }
                    }
                }
                $(this).keyup(this.resetHeight).change(this.resetHeight).focus(
                    this.resetHeight);
            };
            this.resetHeight = function () {
                var _minHeight = parseFloat($(this).attr("minHeight"));
                var _maxHeight = parseFloat($(this).attr("maxHeight"));

                if (!$.browser.msie) {
                    $(this).height(0);
                }
                var h = parseFloat(this.scrollHeight);
                h = h < _minHeight ? _minHeight : h > _maxHeight ? _maxHeight
                    : h;
                // 非ie下高度包括padding、margin、border
                if (!$.browser.msie) {
                    $(this).outerHeight(h);
                }
                // ie下高度不包括padding等
                else {
                    $(this).height(h);
                }

                if (h >= _maxHeight) {
                    $(this).css("overflow-y", "scroll");
                } else {
                    $(this).css("overflow-y", "hidden");
                }
            };
            this.init();
        }
    });


})(jQuery);