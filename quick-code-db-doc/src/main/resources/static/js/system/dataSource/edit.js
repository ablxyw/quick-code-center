/**
 * 编辑-数据源配置js
 * @author weiqiang<weiqiang@ablxyw.cn>
 * @date 2020-01-20 13:13:36
 */
var vm = new Vue({
    el: '#dpCode',
    data: {
        sysDatasourceConfig: {},
        dataSourceTypes: [{
            code: 'mysql'
        }, {
            code: 'oracle'
        }]
    },
    methods: {
        setForm: function () {
            $.SetForm({
                url: '../../sysDataSourceConfig/list?datasourceId=' + vm.sysDatasourceConfig.datasourceId + '&databaseType=&_time' + $.now(),
                type: 'get',
                param: vm.sysDatasourceConfig.datasourceId,
                success: function (data) {
                    if (data && data.length > 0) {
                        vm.sysDatasourceConfig = data[0];
                    }
                }
            })
            ;
        },
        acceptClick: function () {
            if (!$('#form').Validform()) {
                return false;
            }
            $.ConfirmForm({
                url: '../../sysDataSourceConfig/update?_' + $.now(),
                param: vm.sysDatasourceConfig,
                success: function (data) {
                    $.currentIframe().vm.load();
                }
            });
        }
    }
});
