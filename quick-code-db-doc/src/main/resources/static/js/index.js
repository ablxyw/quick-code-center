//生成菜单

// iframe自适应
$(window).on('resize', function () {
    var $content = $('.content');
    $content.height($(this).height() - 146);
    $content.find('iframe').each(function () {
        $(this).height($content.height());
    });
}).resize();

var vm = new Vue({
    el: '#dpCode',
    data: {
        user: {},
        menuList: {},
        main: 'system/dataSource/list.html?_' + $.now(),
        pswd: null,
        newPswd: null,
        navTitle: '数据源配置'
    },
    methods: {
        config: function () {
            vm.main = 'system/dataSource/list.html?_' + $.now();
            vm.navTitle = '数据源配置';
        },
        interfaceRequest: function () {
            vm.main = 'system/interfaceRequest/list.html?_' + $.now();
            vm.navTitle = '请求日志';
        },
        druid: function () {
            vm.main = '././druid/index.html?_' + $.now();
            vm.navTitle = '数据库监控';
        }
    }
});
