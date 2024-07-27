/**
 * 新增-数据源配置js
 * @author weiqiang<weiqiang@ablxyw.cn>
 * @date 2020-01-20 13:13:36
 */
var vm = new Vue({
    el: '#dpCode',
    data: {
        sysDatasourceConfig: {
            databaseType: 'mysql',
            appId: '1',
            initialSize: 1,
            maxActive: 10,
            minIdle: 5,
            filters:'wall,stat',
            maxWait: 60000
        },
        dataSourceTypes: [{
            code: 'mysql'
        }, {
            code: 'oracle'
        }]
    },
    methods: {
        acceptClick: function () {
            if (!$('#form').Validform()) {
                return false;
            }
            $.SaveForm({
                url: '../../sysDataSourceConfig/insert?_' + $.now(),
                param: vm.sysDatasourceConfig,
                success: function (data) {
                    $.currentIframe().vm.load();
                }
            });
        }
    }
});
