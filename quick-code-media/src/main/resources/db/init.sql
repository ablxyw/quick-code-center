-- 初始化脚本
-- 首先创建数据库、再初始化Q数据库
CREATE DATABASE if not exists `q_doc` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
CREATE DATABASE if not exists `q_frame_log` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
-- 创建用户
CREATE USER IF NOT EXISTS 'q_doc'@'%' IDENTIFIED BY 'qDoc123@';

use q_doc;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE IF NOT EXISTS `sys_datasource_config`
(
    `datasource_id`   varchar(255) NOT NULL COMMENT '数据源的id',
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
    PRIMARY KEY (`datasource_id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='数据源配置';

CREATE TABLE IF NOT EXISTS `sys_screenshot`
(
    `shot_id`       varchar(255)  NOT NULL COMMENT '主键',
    `name`          varchar(255)  NOT NULL COMMENT '名称',
    `url`           varchar(1000) NOT NULL COMMENT '链接地址',
    `enable`        bit(1)       DEFAULT '0' COMMENT '是否可用',
    `driver_path`   varchar(255)  NOT NULL COMMENT '驱动地址',
    `driver_type`   varchar(255) DEFAULT 'chrome' COMMENT '驱动类型',
    `width`         int(11)      DEFAULT NULL COMMENT '宽度',
    `height`        int(11)      DEFAULT NULL COMMENT '高度',
    `fullscreen`    bit(1)       DEFAULT '0' COMMENT '是否全屏',
    `sleep_timeout` int(11)      DEFAULT '1000' COMMENT '打开链接等待时长(ms)',
    `file_type`     varchar(255) DEFAULT 'png' COMMENT '保存文件类型',
    `file_url`      varchar(255) DEFAULT '' COMMENT '保存文件地址',
    `create_id`     varchar(255)  NULL COMMENT '创建者',
    `update_id`     varchar(255)  NULL COMMENT '更新者',
    `create_time`   datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time`   datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`shot_id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='html转图配置';
CREATE TABLE IF NOT EXISTS `sys_screenshot_log`
(
    `log_id`      varchar(255)  NOT NULL COMMENT '主键',
    `shot_id`     varchar(255)  NOT NULL COMMENT '配置Id',
    `name`        varchar(255)  NOT NULL COMMENT '名称',
    `url`         varchar(1000) NOT NULL COMMENT '链接地址',
    `file_url`    varchar(255) DEFAULT '' COMMENT '保存文件地址',
    `create_id`   varchar(255)  NULL COMMENT '创建者',
    `update_id`   varchar(255)  NULL COMMENT '更新者',
    `create_time` datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`log_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='html转图配置日志';


ALTER TABLE `sys_datasource_config`
    ADD COLUMN `filters` varchar(255) NULL DEFAULT 'stat,wall' COMMENT '防火墙' AFTER `remark`;
ALTER TABLE `sys_datasource_config`
    ADD COLUMN `create_id` varchar(255) NULL COMMENT '创建者' AFTER `update_time`,
    ADD COLUMN `update_id` varchar(255) NULL COMMENT '更新者' AFTER `create_id`;

SET FOREIGN_KEY_CHECKS = 1;



use q_frame_log;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
CREATE TABLE IF NOT EXISTS `sys_interface_request`
(
    `request_id`      varchar(255) NOT NULL COMMENT '主键',
    `datasource_id`   varchar(255)  DEFAULT '' COMMENT '数据源Id',
    `config_id`       varchar(255)  DEFAULT '' COMMENT '配置Id',
    `request_uri`     varchar(255) NOT NULL COMMENT '请求uri',
    `request_type`    varchar(255)  DEFAULT 'GET' COMMENT '请求类型',
    `request_param`   varchar(2255) DEFAULT NULL COMMENT '请求参数',
    `query_sql`       varchar(3000) DEFAULT '' COMMENT '查询SQL',
    `request_time`    double(11, 0) DEFAULT '0' COMMENT '请求耗时',
    `client_ip`       varchar(255)  DEFAULT '' COMMENT '客户端ip',
    `browser_name`    varchar(255)  DEFAULT '' COMMENT '浏览器',
    `browser_version` varchar(255)  DEFAULT '' COMMENT '浏览器版本',
    `os_name`         varchar(255)  DEFAULT '' COMMENT '操作系统',
    `success`         char(1)       DEFAULT '0' COMMENT '是否成功',
    `message`         varchar(2255) DEFAULT '' COMMENT '返回信息',
    `data_size`       double(11, 0) DEFAULT '0' COMMENT '数据条数',
    `begin_time`      datetime      DEFAULT '' COMMENT '请求时间',
    `end_time`        datetime      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '返回时间',
    PRIMARY KEY (`request_id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  ROW_FORMAT = DYNAMIC COMMENT ='请求日志';
SET FOREIGN_KEY_CHECKS = 1;

use `q_doc`;
DROP VIEW IF EXISTS `sys_interface_request`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `sys_interface_request` AS
select `q_frame_log`.`sys_interface_request`.`request_id`      AS `request_id`,
       `q_frame_log`.`sys_interface_request`.`datasource_id`   AS `datasource_id`,
       `q_frame_log`.`sys_interface_request`.`config_id`       AS `config_id`,
       `q_frame_log`.`sys_interface_request`.`request_uri`     AS `request_uri`,
       `q_frame_log`.`sys_interface_request`.`request_type`    AS `request_type`,
       `q_frame_log`.`sys_interface_request`.`request_param`   AS `request_param`,
       `q_frame_log`.`sys_interface_request`.`query_sql`       AS `query_sql`,
       `q_frame_log`.`sys_interface_request`.`request_time`    AS `request_time`,
       `q_frame_log`.`sys_interface_request`.`client_ip`       AS `client_ip`,
       `q_frame_log`.`sys_interface_request`.`browser_name`    AS `browser_name`,
       `q_frame_log`.`sys_interface_request`.`browser_version` AS `browser_version`,
       `q_frame_log`.`sys_interface_request`.`os_name`         AS `os_name`,
       `q_frame_log`.`sys_interface_request`.`success`         AS `success`,
       `q_frame_log`.`sys_interface_request`.`message`         AS `message`,
       `q_frame_log`.`sys_interface_request`.`data_size`       AS `data_size`,
       `q_frame_log`.`sys_interface_request`.`begin_time`      AS `begin_time`,
       `q_frame_log`.`sys_interface_request`.`end_time`        AS `end_time`
from `q_frame_log`.`sys_interface_request`;
-- 用户授权 建完再执行
GRANT ALL PRIVILEGES ON q_doc.* TO 'q_doc'@'%';
GRANT ALL PRIVILEGES ON q_frame_log.* TO 'q_doc'@'%';
