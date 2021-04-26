CREATE TABLE if not exists `sys_datasource_config`
(
    `datasource_id`   varchar(255) NOT NULL COMMENT '数据源的id',
    `app_id`          varchar(255) NOT NULL COMMENT '应用',
    `datasource_name` varchar(255) NOT NULL DEFAULT '' COMMENT '数据源名称',
    `url`             varchar(400) NOT NULL COMMENT '连接信息',
    `user_name`       varchar(255) NOT NULL COMMENT '用户名',
    `pass_word`       varchar(255)          DEFAULT NULL COMMENT '密码',
    `code`            varchar(255)          DEFAULT NULL COMMENT '暂留字段',
    `initial_size`    int(11)      NOT NULL DEFAULT '1' COMMENT '初始化时建立物理连接的个数',
    `max_active`      int(11)      NOT NULL DEFAULT '10' COMMENT '最大连接池数量',
    `min_idle`        int(11)      NOT NULL DEFAULT '5' COMMENT '最小连接池数量',
    `max_wait`        int(11)      NOT NULL DEFAULT '6000' COMMENT '获取连接时最大等待时间',
    `database_type`   varchar(255) NOT NULL DEFAULT 'mysql' COMMENT '数据库类型',
    `remark`          varchar(500)          DEFAULT NULL COMMENT '备注',
    `filters`         varchar(255) NULL     DEFAULT 'stat,wall' COMMENT '防火墙',
    `create_id`       varchar(255) NULL COMMENT '创建者',
    `update_id`       varchar(255) NULL COMMENT '更新者',
    `create_time`     datetime              DEFAULT NULL COMMENT '创建时间',
    `update_time`     datetime              DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`datasource_id`)
);

CREATE TABLE IF NOT EXISTS `sys_interface_request`
(
    `request_id`      varchar(255) NOT NULL COMMENT '主键',
    `datasource_id`   varchar(255)  DEFAULT '' COMMENT '数据源Id',
    `config_id`       varchar(255)  DEFAULT '' COMMENT '配置Id',
    `request_uri`     varchar(255) NOT NULL COMMENT '请求uri',
    `request_type`    varchar(255)  DEFAULT 'GET' COMMENT '请求类型',
    `request_param`   varchar(2255) DEFAULT NULL COMMENT '请求参数',
    `query_sql`       varchar(3000) DEFAULT '' COMMENT '查询SQL',
    `request_time`    int(11) DEFAULT '0' COMMENT '请求耗时',
    `client_ip`       varchar(255)  DEFAULT '' COMMENT '客户端ip',
    `browser_name`    varchar(255)  DEFAULT '' COMMENT '浏览器',
    `browser_version` varchar(255)  DEFAULT '' COMMENT '浏览器版本',
    `os_name`         varchar(255)  DEFAULT '' COMMENT '操作系统',
    `success`         int(1)        DEFAULT '0' COMMENT '是否成功',
    `message`         varchar(2255) DEFAULT '' COMMENT '返回信息',
    `data_size`       int(11)       DEFAULT '0' COMMENT '数据条数',
    `begin_time`      datetime      DEFAULT null COMMENT '请求时间',
    `end_time`        datetime      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '返回时间',
    PRIMARY KEY (`request_id`)
);
