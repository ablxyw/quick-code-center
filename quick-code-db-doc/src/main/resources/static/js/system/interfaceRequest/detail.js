/**
 * 详情-接口请求日志js
 * @author weiqiang<weiqiang@ablxyw.cn>
 * @date 2020-02-16 18:11:59
 */
var vm = new Vue({
    el: '#dpCode',
    data: {
        sysInterfaceRequest: {}
    },
    methods: {
        setForm: function () {
            $.SetForm({
                url: '../../interfaceRequest/list?requestId=' + vm.sysInterfaceRequest.requestId + '&_time=' + $.now(),
                type: 'get',
                param: vm.sysInterfaceRequest.requestId,
                success: function (data) {
                    if (data && data.length > 0) {
                        vm.sysInterfaceRequest = data[0];
                        vm.sysInterfaceRequest.beginTime = dateFormatter(data[0].beginTime);
                        vm.sysInterfaceRequest.endTime = dateFormatter(data[0].endTime);
                    }
                }
            });
        },
        acceptClick: function () {
            $.currentIframe().vm.load();
            dialogClose();
        }
    }
});
