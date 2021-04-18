/**
 * 新增-接口请求日志js
 * @author weiqiang<weiqiang@ablxyw.cn>
 * @date 2020-02-16 18:11:59
 */
var vm = new Vue({
    el: '#dpCode',
    data: {
        sysInterfaceRequest: {}
    },
    methods: {
        acceptClick: function () {
            if (!$('#form').Validform()) {
                return false;
            }
            $.SaveForm({
                url: '../../interfaceRequest/insert?_' + $.now(),
                param: vm.sysInterfaceRequest,
                success: function (data) {
                    $.currentIframe().vm.load();
                }
            });
        }
    }
});
