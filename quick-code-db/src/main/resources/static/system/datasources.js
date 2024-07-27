//JavaScript代码区域
layui.use('element', function () {
    layui.use('table', function () {
        var table = layui.table;
        let data = '{"data":[{"id":10000,"username":"user-0","sex":"女","city":"城市-0","sign":"签名-0","experience":255,"logins":24,"wealth":82830700,"classify":"作家","score":57},{"id":10001,"username":"user-1","sex":"男","city":"城市-1","sign":"签名-1","experience":884,"logins":58,"wealth":64928690,"classify":"词人","score":27},{"id":10002,"username":"user-2","sex":"女","city":"城市-2","sign":"签名-2","experience":650,"logins":77,"wealth":6298078,"classify":"酱油","score":31},{"id":10003,"username":"user-3","sex":"女","city":"城市-3","sign":"签名-3","experience":362,"logins":157,"wealth":37117017,"classify":"诗人","score":68},{"id":10004,"username":"user-4","sex":"男","city":"城市-4","sign":"签名-4","experience":807,"logins":51,"wealth":76263262,"classify":"作家","score":6},{"id":10005,"username":"user-5","sex":"女","city":"城市-5","sign":"签名-5","experience":173,"logins":68,"wealth":60344147,"classify":"作家","score":87},{"id":10006,"username":"user-6","sex":"女","city":"城市-6","sign":"签名-6","experience":982,"logins":37,"wealth":57768166,"classify":"作家","score":34},{"id":10007,"username":"user-7","sex":"男","city":"城市-7","sign":"签名-7","experience":727,"logins":150,"wealth":82030578,"classify":"作家","score":28},{"id":10008,"username":"user-8","sex":"男","city":"城市-8","sign":"签名-8","experience":951,"logins":133,"wealth":16503371,"classify":"词人","score":14},{"id":10009,"username":"user-9","sex":"女","city":"城市-9","sign":"签名-9","experience":484,"logins":25,"wealth":86801934,"classify":"词人","score":75}]}'
        data = JSON.parse(data).data;
        table.render({
            elem: '#tableDatabase',
            url: 'sysDataSourceConfig/listByPage',
            request: {
                pageName: 'pageNumber',//页码的参数名称，默认：page
                limitName: 'pageSize' //每页数据量的参数名，默认：limit
            },
            cols: [[
                {type: 'checkbox'},
                {field: 'datasourceId', width: 80, title: 'ID', sort: true},
                {field: 'datasourceName', width: 200, title: '数据源名称'},
                {field: 'userName', width: 200, title: '用户名', sort: true},
                {field: 'databaseType', width: 150, title: '数据库类型'},
                {field: 'createTime', title: '创建时间', minWidth: 200},
                {fixed: 'right', title: '操作', toolbar: '#barDatabase', width: 200}
            ]],
            data: data,
            id: 'testReload',
            height: 700,
            page: true,
            toolbar: true,
            //res 即为原始返回的数据
            parseData: function (res) {
                console.log(res.data);
                return {
                    "code": res.success, //解析接口状态
                    "msg": res.message, //解析提示文本
                    "count": res.total, //解析数据长度
                    "dataName": res.data //解析数据列表
                };
            }
        });
        var $ = layui.$, active = {
            reload: function () {
                var demoReload = $('#demoReload');
                //执行重载
                table.reload('testReload', {
                    page: {
                        curr: 1,//重新从第 1 页开始

                    },
                    where: {
                        // datasourceName: demoReload.val() || ''
                    }
                });
            }
        };
        $('.demoTable .layui-btn').on('click', function () {
            var type = $(this).data('type');
            active[type] ? active[type].call(this) : '';
        });
        //监听工具条
        table.on('tool(tableDatabase)', function (obj) {
            var data = obj.data;
            if (obj.event === 'detail') {
                // layer.msg('ID：'+ data.id + ' 的查看操作');
                layer.open({
                    type: 1,
                    title: '弹窗',
                    shadeClose: true,
                    content: $('#dialog'),
                    area: ['500px', '400px'],
                    btn: ['取消', '确定'] //按钮
                });
            } else if (obj.event === 'del') {
                layer.confirm('真的删除行么', function (index) {
                    obj.del();
                    layer.close(index);
                });
            } else if (obj.event === 'edit') {
                layer.alert('编辑行：<br>' + JSON.stringify(data))
            } else if (obj.event === 'test') {
                layer.alert('编辑行：<br>' + JSON.stringify(data))
            }
        });
    });
});
