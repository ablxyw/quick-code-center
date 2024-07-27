/**
 * 数据源配置js
 * @author weiqiang<weiqiang@ablxyw.cn>
 * @date 2020-01-20 13:13:36
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
        url: '../../sysDataSourceConfig/listByPage?_' + $.now(),
        method: 'get',
        height: $(window).height() - 56,
        pageSize: 20,
        queryParams: function (params) {
            params[vm.queryParamKey] = vm.keyword;
            return params;
        },
        columns: [
            {
                radio: true
            }, {
                field: 'id',
                title: "序号",
                width: "30px",
                align: 'center',
                formatter: function (value, row, index) {
                    return index + 1;
                }
            }, {
                field: "datasourceId",
                title: "数据源的id",
                visible: false,
                width: "100px"
            }, {
                field: "datasourceName",
                title: "数据源名称",
                sortable: true,
                width: "100px"
            }, {
                field: "databaseType",
                title: "数据库类型",
                sortable: true,
                width: "100px"
            }, {
                field: "url",
                title: "连接url信息",
                sortable: true,
                width: "100px",
                formatter: substrFormatter
            }, {
                field: "userName",
                title: "用户名",
                sortable: true,
                width: "100px"
            }, {
                field: "passWord",
                title: "密码",
                visible: false,
                width: "100px"
            }, {
                field: "code",
                title: "预留字段",
                visible: false,
                width: "100px"
            }, {
                field: "initialSize",
                title: "初始化数",
                sortable: true,
                width: "100px"
            }, {
                field: "maxActive",
                title: "最大连接数",
                sortable: true,
                width: "100px"
            }, {
                field: "minIdle",
                title: "最小连接数",
                sortable: true,
                width: "100px"
            }, {
                field: "maxWait",
                title: "连接等待时间",
                sortable: true,
                width: "100px"
            }, {
                field: "remark",
                title: "备注",
                sortable: true,
                width: "100px",
                formatter: substrFormatter
            }, {
                field: "updateTime",
                title: "更新时间",
                width: 100,
                sortable: true,
                formatter: dateFormatter
            }
        ]
    })
}

var vm = new Vue({
    el: '#dpCode',
    data: {
        keyword: null,
        queryParamKey: 'datasourceName',
        queryParams: [
            {
                nameEn: "datasourceName",
                nameZh: "数据源名称"
            }, {
                nameEn: "url",
                nameZh: "连接url信息"
            }, {
                nameEn: "userName",
                nameZh: "用户名"
            }, {
                nameEn: "passWord",
                nameZh: "密码"
            }, {
                nameEn: "code",
                nameZh: "预留字段"
            }, {
                nameEn: "initialSize",
                nameZh: "初始化数"
            }, {
                nameEn: "maxActive",
                nameZh: "最大连接池数量"
            }, {
                nameEn: "minIdle",
                nameZh: "最小连接池数量"
            }, {
                nameEn: "maxWait",
                nameZh: "连接等待时间"
            }, {
                nameEn: "databaseType",
                nameZh: "数据库类型"
            }, {
                nameEn: "remark",
                nameZh: "备注"
            }
        ]
    },
    methods: {
        load: function () {
            $('#dataGrid').bootstrapTable('refresh');
        },
        clearSearchValue: function () {
            vm.keyword = '';
        },
        testConnect: function () {
            let ck = $('#dataGrid').bootstrapTable('getSelections');
            if (checkedArray(ck)) {
                $.SaveForm({
                    url: '../../sysDataSourceConfig/testConnect?_' + $.now(),
                    param: ck[0],
                    success: function (data) {
                    }
                });
            }
        },
        updatePassword: function () {
            let ck = $('#dataGrid').bootstrapTable('getSelections');
            if (checkedRow(ck)) {
                dialogOpen({
                    title: '<i class="fa fa-pencil-square-o" aria-hidden="true"></i>&nbsp;修改数据源:<span style="color: red">' + ck[0].datasourceName + '</span> 密码',
                    url: 'system/dataSource/password.html?_' + $.now(),
                    success: function (iframeId) {
                        top.frames[iframeId].vm.sysDatasourceConfig.datasourceId = ck[0].datasourceId;
                    },
                    yes: function (iframeId) {
                        top.frames[iframeId].vm.acceptClick();
                    }
                });
            }
        },
        save: function () {
            dialogOpen({
                title: '<i class="fa fa-plus"></i>&nbsp;新增数据源配置',
                url: 'system/dataSource/add.html?_' + $.now(),
                width: '768px',
                height: '500px',
                scroll: true,
                btn: ['保存', '取消'],
                yes: function (iframeId) {
                    top.frames[iframeId].vm.acceptClick();
                },
            });
        },
        edit: function () {
            let ck = $('#dataGrid').bootstrapTable('getSelections');
            if (checkedRow(ck)) {
                dialogOpen({
                    title: '<i class="fa fa-pencil-square-o" aria-hidden="true"></i>&nbsp;编辑数据源配置',
                    url: 'system/dataSource/edit.html?_' + $.now(),
                    width: '768px',
                    height: '500px',
                    scroll: true,
                    success: function (iframeId) {
                        top.frames[iframeId].vm.sysDatasourceConfig.datasourceId = ck[0].datasourceId;
                        top.frames[iframeId].vm.setForm();
                    },
                    yes: function (iframeId) {
                        top.frames[iframeId].vm.acceptClick();
                    }
                });
            }
        },
        doc: function () {
            let ck = $('#dataGrid').bootstrapTable('getSelections');
            if (checkedArray(ck)) {
                let datasourceId = ck[0].datasourceId;
                window.open('../../sysDocConfig/databaseDoc?datasourceId=' + datasourceId + '&_' + $.now());
            }

        },
        excel: function () {
            let ck = $('#dataGrid').bootstrapTable('getSelections');
            if (checkedArray(ck)) {
                let datasourceId = ck[0].datasourceId;
                window.open('../../sysDocConfig/databaseDocExcel?datasourceId=' + datasourceId + '&_' + $.now());
            }
        },
        remove: function () {
            let ck = $('#dataGrid').bootstrapTable('getSelections'), ids = [];
            if (checkedArray(ck)) {
                $.each(ck, function (idx, item) {
                    ids[idx] = item.datasourceId;
                });
                $.RemoveForm({
                    url: '../../sysDataSourceConfig/deleteByIds?_' + $.now(),
                    param: {ids: ids},
                    success: function (data) {
                        vm.load();
                    }
                });
            }
        }
    }
});
