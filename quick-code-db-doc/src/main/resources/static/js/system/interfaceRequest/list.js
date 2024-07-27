/**
 * 接口请求日志js
 * @author weiqiang<weiqiang@ablxyw.cn>
 * @date 2020-02-16 18:11:59
 */

$(function () {
    initialPage();
    getGrid();
});

function initialPage() {
    $(window).resize(function () {
        $('#dataGrid').bootstrapTable('resetView', {height: $(window).height() - 56});
    });
}

function getGrid() {
    $('#dataGrid').bootstrapTableEx({
        url: '../../interfaceRequest/listByPage?_' + $.now(),
        method: 'get',
        height: $(window).height() - 56,
        pageSize: 20,
        queryParams: function (params) {
            if (vm.queryParamKey == 'success') {
                params[vm.queryParamKey] = vm.keyword == '是' ? 1 : 0;
            } else {
                params[vm.queryParamKey] = vm.keyword;
            }
            return params;
        },
        columns: [
            {radio: true},
            {
                field: 'id',
                title: "序号",
                width: "30px",
                align: 'center',
                formatter: function (value, row, index) {
                    return index + 1;
                }
            },
            {
                field: "requestId",
                title: "主键",
                visible: false,
                sortable: true,
                width: "100px"
            },
            {
                field: "datasourceId",
                title: "数据源Id",
                sortable: true,
                visible: false,
                width: "100px"
            },
            {
                field: "configId",
                title: "配置Id",
                sortable: true,
                visible: false,
                width: "100px"
            },
            {
                field: "requestUri",
                title: "请求uri",
                sortable: true,
                width: "100px"
            },
            {
                field: "requestType",
                title: "请求类型",
                sortable: true,
                width: "60px"
            },
            {
                field: "requestParam",
                title: "请求参数",
                visible: false,
                sortable: true,
                width: "100px"
            },
            {
                field: "clientIp",
                title: "客户端ip",
                sortable: true,
                width: "100px"
            },
            {
                field: "success",
                title: "是否成功",
                sortable: true,
                width: "50px",
                formatter: booleanFormatter
            },
            {
                field: "message",
                title: "返回信息",
                sortable: true,
                width: "130px",
                formatter: substrFormatter
            },
            {
                field: "dataSize",
                title: "数据条数",
                align: 'right',
                sortable: true,
                width: "60px"
            }, {
                field: "osName",
                title: "操作系统",
                sortable: true,
                width: "100px"
            }, {
                field: "browserName",
                title: "浏览器(版本)",
                sortable: true,
                width: "130px",
                formatter: function (value, row, index) {
                    if (row.browserVersion) {
                        return value + '(' + row.browserVersion + ')';
                    }
                    return value;
                }
            }, {
                field: "browserVersion",
                title: "浏览器版本",
                sortable: true,
                visible: false,
                width: "100px"
            }, {
                field: "requestTime",
                title: "耗时(ms)",
                align: 'right',
                sortable: true,
                width: "60px"
            },
            {
                field: "beginTime",
                title: "请求时间",
                sortable: true,
                width: "100px",
                formatter: dateFormatter
            }
        ]
    })
}

var vm = new Vue({
    el: '#dpCode',
    data: {
        keyword: null,
        queryParamKey: 'requestUri',
        queryParams: [{
            nameEn: "requestUri",
            nameZh: "请求uri"
        }, {
            nameEn: "requestType",
            nameZh: "请求类型"
        }, {
            nameEn: "requestParam",
            nameZh: "请求参数"
        }, {
            nameEn: "querySql",
            nameZh: "查询SQL"
        }, {
            nameEn: "clientIp",
            nameZh: "客户端ip"
        }, {
            nameEn: "success",
            nameZh: "是否成功"
        }, {
            nameEn: "message",
            nameZh: "返回信息"
        }, {
            nameEn: "osName",
            nameZh: "操作系统",
        }, {
            nameEn: "browserVersion",
            nameZh: "浏览器版本",
        }, {
            nameEn: "browserName",
            nameZh: "浏览器",
        }, {
            nameEn: "requestTime",
            nameZh: "耗时(ms)",
        }, {
            nameEn: "dataSize",
            nameZh: "数据条数"
        }],
        showTable: true,
        apiPrefix: '',
        pieBrowserData: [],
        lineErrorLogData: [],
        lineLogData: []
    },
    methods: {
        load: function () {
            $('#dataGrid').bootstrapTable('refresh');
        },
        clearSearchValue: function () {
            vm.keyword = '';
        },
        save: function () {
            dialogOpen({
                title: '新增接口请求日志',
                url: 'system/interfaceRequest/add.html?_' + $.now(),
                width: '420px',
                height: '350px',
                scroll: true,
                yes: function (iframeId) {
                    top.frames[iframeId].vm.acceptClick();
                },
            });
        },
        edit: function () {
            let ck = $('#dataGrid').bootstrapTable('getSelections');
            if (checkedRow(ck)) {
                dialogOpen({
                    title: '编辑接口请求日志',
                    url: 'system/interfaceRequest/edit.html?_' + $.now(),
                    width: '420px',
                    height: '350px',
                    scroll: true,
                    success: function (iframeId) {
                        top.frames[iframeId].vm.sysInterfaceRequest.requestId = ck[0].requestId;
                        top.frames[iframeId].vm.setForm();
                    },
                    yes: function (iframeId) {
                        top.frames[iframeId].vm.acceptClick();
                    }
                });
            }
        },
        detail: function () {
            let ck = $('#dataGrid').bootstrapTable('getSelections');
            if (checkedRow(ck)) {
                dialogOpen({
                    title: (ck[0].requestUri || '') + ' 接口请求详情',
                    url: 'system/interfaceRequest/detail.html?_' + $.now(),
                    width: '720px',
                    height: '500px',
                    scroll: true,
                    btn: ['关闭'],
                    success: function (iframeId) {
                        top.frames[iframeId].vm.sysInterfaceRequest.requestId = ck[0].requestId;
                        top.frames[iframeId].vm.setForm();
                    },
                    yes: function (iframeId) {
                        top.frames[iframeId].vm.acceptClick();
                    }
                });
            }
        },
        remove: function () {
            let ck = $('#dataGrid').bootstrapTable('getSelections'), ids = [];
            if (checkedArray(ck)) {
                $.each(ck, function (idx, item) {
                    ids[idx] = item.requestId;
                });
                $.RemoveForm({
                    url: '../../interfaceRequest/deleteByIds?_' + $.now(),
                    param: {ids: ids},
                    success: function (data) {
                        vm.load();
                    }
                });
            }
        }
    }
});
