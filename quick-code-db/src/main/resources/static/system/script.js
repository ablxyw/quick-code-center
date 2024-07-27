layui.use('table', function () {
    var table = layui.table;
    table.render({
        elem: '#tableScript',
        url: 'sysScriptWorkbench/listByPage',
        request: {
            pageName: 'pageNumber',//页码的参数名称，默认：page
            limitName: 'pageSize' //每页数据量的参数名，默认：limit
        },
        cols: [[
            {type: 'checkbox'},
            {field: 'id', width: 80, title: 'ID', sort: true},
            {field: 'name', width: 200, title: '数据源名称',totalRow: true},
            {field: 'scriptMode', width: 200, title: '脚本类型', sort: true},
            {field: 'curVersion', width: 150, title: '当前版本'},
            {field: 'oriVersion', width: 150, title: '依赖版本'},
            {field: 'createTime', title: '创建时间', minWidth: 200},
            {fixed: 'right', title: '操作', toolbar: '#barScript', width: 200}
        ]],
        id: 'nameReload',
        page: true,
        toolbar: true,
        height: 700,
        totalRow: true,
        response: {
            statusCode: 200 //重新规定成功的状态码为 200，table 组件默认为 0
        },
        //将原始数据解析成 table 组件所规定的数据
        parseData: function (res) {
            console.log(res.data);
            return {
                "code": res.success, //解析接口状态
                "msg": res.message, //解析提示文本
                "count": res.total, //解析数据长度
                "data": res.data //解析数据列表
            };
        }
    });
    //监听工具条
    table.on('tool(tableScript)', function (obj) {
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
