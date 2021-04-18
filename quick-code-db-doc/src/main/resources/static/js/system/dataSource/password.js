/**
 * 编辑-数据源配置js
 * @author weiqiang<weiqiang@ablxyw.cn>
 * @date 2020-01-20 13:13:36
 */
var vm = new Vue({
    el: '#dpCode',
    data: {
        sysDatasourceConfig: {
            datasourceId: '',
            oldPassWord: '',
            passWord: '',
        }
    },
    methods: {
        acceptClick: function () {
            if (!$('#form').Validform()) {
                return false;
            }
            $.ConfirmForm({
                url: '../../sysDataSourceConfig/updatePassword?_' + $.now(),
                param: vm.sysDatasourceConfig,
                success: function (data) {
                    $.currentIframe().vm.load();
                }
            });
        }
    }
});
