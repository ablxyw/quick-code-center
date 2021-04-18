// 获取请求参数
// 使用示例
// location.href = http://localhost:8080/index.html?id=123
// T.p('id') --> 123;
var url = function (name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) return unescape(r[2]);
    return null;
};

//全局配置
$.ajaxSetup({
    dataType: "json",
    cache: false,
    complete: function (XMLHttpRequest, textStatus) {
        if (textStatus == "parsererror") {
            top.layer.open({
                title: '系统提示',
                area: '338px',
                icon: 3,
                move: false,
                anim: -1,
                isOutAnim: false,
                content: '注：登录超时,请稍后重新登录.',
                btn: ['立即退出'],
                btnAlign: 'c',
                yes: function () {
                    toUrl('sys/logout');
                }
            });
            setTimeout(function () {
                toUrl("sys/logout");
            }, 2000);
        } else if (textStatus == "error") {
            dialogMsg("请求超时，请稍候重试...", "error");
        }
    }
});

//权限判断
function hasPermission(permission) {
    //TODO 统一返回为true
    if (true) {
        return true;
    }
    if (isNullOrEmpty(window.parent.perms)) {
        return false;
    }
    if (window.parent.perms.indexOf(permission) > -1) {
        return true;
    } else {
        return false;
    }
}

toUrl = function (href) {
    window.location.href = href;
};
/**
 * 格式化显示列数据
 * @param value 列数据
 * @param row 行
 * @param index 列
 * @returns {string}
 */
substrFormatter = function (value, row, index) {
    let oriValue = value, defaultLength = 20;
    if (value) {
        let length = value.length;
        if (length < defaultLength) {
            defaultLength = length;
        }
        value = value.substr(0, defaultLength);
    } else {
        return '';
    }
    return "<span title=" + oriValue + ">" + value + "</span>";
};
/**
 * 格式化显示列数据
 * @param value 列数据
 * @param row 行
 * @param index 列
 * @returns {string}
 */
dateFormatter = function (value, row, index) {
    if (value) {
        return formatDate(new Date(value), 'yyyy-MM-dd hh:mm:ss');
    }
    return value;
};

/**
 * 布尔格式化
 * @param value 列数据
 * @param row 行
 * @param index 列
 * @returns {string}
 */
booleanFormatter = function (value, row, index) {
    if (value) {
        return '是';
    }
    return '否';
};

$.fn.bootstrapTableEx = function (opt) {
    var defaults = {
        url: '',
        dataField: "data",
        method: 'post',
        dataType: 'json',
        selectItemName: 'id',
        clickToSelect: true,
        pagination: true,
        smartDisplay: false,
        pageSize: 10,
        pageList: [10, 20, 30, 40, 50],
        paginationLoop: false,
        sidePagination: 'server',
        queryParamsType: null,
        columns: []
    };
    var option = $.extend({}, defaults, opt);
    $(this).bootstrapTable(option);
};

formatDate = function (v, format) {
    if (!v) return "";
    var d = v;
    if (typeof v === 'string') {
        if (v.indexOf("/Date(") > -1)
            d = new Date(parseInt(v.replace("/Date(", "").replace(")/", ""), 10));
        else
            d = new Date(Date.parse(v.replace(/-/g, "/").replace("T", " ").split(".")[0]));//.split(".")[0] 用来处理出现毫秒的情况，截取掉.xxx，否则会出错
    }
    var o = {
        "M+": d.getMonth() + 1,
        "d+": d.getDate(),
        "h+": d.getHours(),
        "m+": d.getMinutes(),
        "s+": d.getSeconds(),
        "q+": Math.floor((d.getMonth() + 3) / 3),
        "S": d.getMilliseconds()
    };
    if (/(y+)/.test(format)) {
        format = format.replace(RegExp.$1, (d.getFullYear() + "").substr(4 - RegExp.$1.length));
    }
    for (var k in o) {
        if (new RegExp("(" + k + ")").test(format)) {
            format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));
        }
    }
    return format;
};

formatJson = function(json, options){
    let reg = null,
        formatted = '',
        pad = 0,
        PADDING = '    '; // one can also use '\t' or a different number of spaces
    // optional settings
    options = options || {};
    // remove newline where '{' or '[' follows ':'
    options.newlineAfterColonIfBeforeBraceOrBracket = (options.newlineAfterColonIfBeforeBraceOrBracket === true) ? true : false;
    // use a space after a colon
    options.spaceAfterColon = (options.spaceAfterColon === false) ? false : true;

    // begin formatting...

    // make sure we start with the JSON as a string
    if (typeof json !== 'string') {
        json = JSON.stringify(json);
    }
    // parse and stringify in order to remove extra whitespace
    json = JSON.parse(json);
    json = JSON.stringify(json);

    // add newline before and after curly braces
    reg = /([\{\}])/g;
    json = json.replace(reg, '\r\n$1\r\n');

    // add newline before and after square brackets
    reg = /([\[\]])/g;
    json = json.replace(reg, '\r\n$1\r\n');

    // add newline after comma
    reg = /(\,)/g;
    json = json.replace(reg, '$1\r\n');

    // remove multiple newlines
    reg = /(\r\n\r\n)/g;
    json = json.replace(reg, '\r\n');

    // remove newlines before commas
    reg = /\r\n\,/g;
    json = json.replace(reg, ',');

    // optional formatting...
    if (!options.newlineAfterColonIfBeforeBraceOrBracket) {
        reg = /\:\r\n\{/g;
        json = json.replace(reg, ':{');
        reg = /\:\r\n\[/g;
        json = json.replace(reg, ':[');
    }
    if (options.spaceAfterColon) {
        reg = /\:/g;
        json = json.replace(reg, ': ');
    }

    $.each(json.split('\r\n'), function(index, node) {
        var i = 0,
            indent = 0,
            padding = '';

        if (node.match(/\{$/) || node.match(/\[$/)) {
            indent = 1;
        } else if (node.match(/\}/) || node.match(/\]/)) {
            if (pad !== 0) {
                pad -= 1;
            }
        } else {
            indent = 0;
        }

        for (i = 0; i < pad; i++) {
            padding += PADDING;
        }

        formatted += padding + node + '\r\n';
        pad += indent;
    });
    return formatted;
}
isNullOrEmpty = function (obj) {
    if ((typeof (obj) == "string" && obj == "") || obj == null || obj == undefined) {
        return true;
    } else {
        return false;
    }
};

isNotNullOrEmpty = function (obj) {
    if ((typeof (obj) == "string" && obj == "") || obj == null || obj == undefined) {
        return false;
    } else {
        return true;
    }
};

checkedArray = function (id) {
    var isOK = true;
    if (id == undefined || id == "" || id == 'null' || id == 'undefined') {
        isOK = false;
        dialogMsg('您没有选中任何数据项！');
    }
    return isOK;
};

checkedRow = function (id) {
    var isOK = true;
    if (id == undefined || id == "" || id == 'null' || id == 'undefined') {
        isOK = false;
        dialogMsg('您没有选中任何数据项！');
    } else if (id.length > 1) {
        isOK = false;
        dialogMsg('您只能选择一条数据项！');
    }
    return isOK;
};

reload = function () {
    location.reload();
    return false;
};

dialogOpen = function (opt) {
    var defaults = {
        id: 'layerForm',
        title: '',
        width: '',
        height: '',
        url: null,
        scroll: false,
        data: {},
        maxmin: false,
        btn: ['确定', '取消'],
        success: function () {
        },
        yes: function () {
        }
    };
    var option = $.extend({}, defaults, opt), content = null;
    if (option.scroll) {
        content = [option.url]
    } else {
        content = [option.url, 'no']
    }
    top.layer.open({
        type: 2,
        id: option.id,
        title: option.title,
        closeBtn: 1,
        anim: -1,
        maxmin: option.maxmin,
        isOutAnim: false,
        shadeClose: false,
        shade: 0.3,
        area: [option.width, option.height],
        content: content,
        btn: option.btn,
        success: function () {
            option.success(option.id);
        },
        yes: function () {
            option.yes(option.id);
        }
    });
};

dialogContent = function (opt) {
    var defaults = {
        title: '系统窗口',
        width: '',
        height: '',
        content: null,
        data: {},
        btn: ['确定', '取消'],
        success: null,
        yes: null
    };
    var option = $.extend({}, defaults, opt);
    return top.layer.open({
        type: 1,
        title: option.title,
        closeBtn: 1,
        anim: -1,
        isOutAnim: false,
        shadeClose: false,
        shade: 0.3,
        area: [option.width, option.height],
        shift: 5,
        content: option.content,
        btn: option.btn,
        success: option.success,
        yes: option.yes
    });
};

dialogAjax = function (opt) {
    var defaults = {
        title: '系统窗口',
        width: '',
        height: '',
        url: null,
        data: {},
        btn: ['确定', '取消'],
        success: null,
        yes: null
    };
    var option = $.extend({}, defaults, opt);
    $.post(option.url, null, function (content) {
        layer.open({
            type: 1,
            title: option.title,
            closeBtn: 1,
            anim: -1,
            isOutAnim: false,
            shadeClose: false,
            shade: 0.3,
            area: [option.width, option.height],
            shift: 5,
            content: content,
            btn: option.btn,
            success: option.success,
            yes: option.yes
        });
    });
};

dialogAlert = function (content, type) {
    var msgType = {
        success: 1,
        error: 2,
        warn: 3,
        info: 7
    };
    if (isNullOrEmpty(type)) {
        type = 'info';
    }
    top.layer.alert(content, {
        icon: msgType[type],
        title: "系统提示",
        anim: -1,
        btnAlign: 'c',
        isOutAnim: false
    });
};

dialogConfirm = function (content, callBack) {
    top.layer.confirm(content, {
        area: '338px',
        icon: 7,
        anim: -1,
        isOutAnim: false,
        title: "系统提示",
        btn: ['确认', '取消'],
        btnAlign: 'c',
        yes: callBack
    });
};

dialogMsg = function (msg, type) {
    var msgType = {
        success: 1,
        error: 2,
        warn: 3,
        info: 7
    };
    if (isNullOrEmpty(type)) {
        type = 'info';
    }
    top.layer.msg(msg, {
        icon: msgType[type],
        time: 2000
    });
};

dialogClose = function () {
    //先得到当前iframe层的索引
    var index = top.layer.getFrameIndex(window.name);
    //再执行关闭
    top.layer.close(index);
};

dialogLoading = function (flag) {
    if (flag) {
        top.layer.load(0, {
            shade: [0.1, '#fff'],
            time: 2000
        });
    } else {
        top.layer.closeAll('loading');
    }
};

$.fn.GetWebControls = function (keyValue) {
    var reVal = "";
    $(this).find('input,select,textarea').each(function (r) {
        var id = $(this).attr('id');
        var type = $(this).attr('type');
        switch (type) {
            case "checkbox":
                if ($("#" + id).is(":checked")) {
                    reVal += '"' + id + '"' + ':' + '"1",'
                } else {
                    reVal += '"' + id + '"' + ':' + '"0",'
                }
                break;
            default:
                var value = $("#" + id).val();
                if (value == "") {
                    value = "&nbsp;";
                }
                reVal += '"' + id + '"' + ':' + '"' + $.trim(value) + '",'
                break;
        }
    });
    reVal = reVal.substr(0, reVal.length - 1);
    if (!keyValue) {
        reVal = reVal.replace(/&nbsp;/g, '');
    }
    reVal = reVal.replace(/\\/g, '\\\\');
    reVal = reVal.replace(/\n/g, '\\n');
    var postdata = jQuery.parseJSON('{' + reVal + '}');
    return postdata;
};

$.fn.SetWebControls = function (data) {
    var $id = $(this)
    for (var key in data) {
        var id = $id.find('#' + key);
        if (id.attr('id')) {
            var type = id.attr('type');
            var value = $.trim(data[key]).replace(/&nbsp;/g, '');
            switch (type) {
                case "checkbox":
                    if (value == 1) {
                        id.attr("checked", 'checked');
                    } else {
                        id.removeAttr("checked");
                    }
                    break;
                default:
                    id.val(value);
                    break;
            }
        }
    }
};

$.currentIframe = function () {
    return $(window.parent.document).contents().find('#main')[0].contentWindow;
};
