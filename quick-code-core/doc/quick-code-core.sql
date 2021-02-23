-- 初始化脚本
-- 首先创建数据库、再初始化Q数据库
CREATE DATABASE if not exists `q_frame_api` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
CREATE DATABASE if not exists `q_frame_log` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
-- 开启定时策略
set global event_scheduler = 1;
-- 创建用户
CREATE USER if not exists 'q_frame'@'%' IDENTIFIED BY 'qFrame@123';

-- 修改用户密码
ALTER USER 'q_frame'@'%' IDENTIFIED WITH MYSQL_NATIVE_PASSWORD BY 'qFrame@123';
flush privileges;

use q_frame_api;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;


-- ----------------------------
-- Table structure for sys_token_info
-- ----------------------------
DROP TABLE IF EXISTS `sys_token_info`;
CREATE TABLE `sys_token_info`
(
    `id`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL,
    `subject`    varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `issuer`     varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL,
    `issued_at`  datetime                                                       NOT NULL COMMENT '产生时间',
    `expiration` datetime                                                       NOT NULL COMMENT '过期时间',
    `token`      varchar(4000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='Token';

-- ----------------------------
-- Table structure for sys_user_info
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_info`;
CREATE TABLE `sys_user_info`
(
    `user_id`     varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `login_name`  varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `password`    varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `user_type`   int(11)                                                       NOT NULL DEFAULT '2' COMMENT '用户类型\r\n0：管理员\r\n1：普通用户\r\n2：其他',
    `status`      int(11)                                                       NOT NULL DEFAULT '1' COMMENT '状态',
    `insert_time` datetime                                                               DEFAULT NULL COMMENT '注册时间',
    `update_time` datetime                                                               DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='系统用户';

SET FOREIGN_KEY_CHECKS = 1;


use q_frame_log;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_interface_request
-- ----------------------------
DROP TABLE IF EXISTS `sys_interface_request`;
CREATE TABLE IF NOT EXISTS `sys_interface_request`
(
    `request_id`      varchar(255) NOT NULL COMMENT '主键',
    `datasource_id`   varchar(255)  DEFAULT NULL COMMENT '数据源Id',
    `config_id`       varchar(255)  DEFAULT NULL COMMENT '配置Id',
    `request_uri`     varchar(255) NOT NULL COMMENT '请求uri',
    `request_type`    varchar(255)  DEFAULT 'GET' COMMENT '请求类型',
    `request_param`   varchar(2255) DEFAULT NULL COMMENT '请求参数',
    `query_sql`       varchar(3000) DEFAULT '' COMMENT '查询SQL',
    `request_time`    double(11, 0) DEFAULT '0' COMMENT '请求耗时',
    `client_ip`       varchar(255)  DEFAULT NULL COMMENT '客户端ip',
    `server_ip`       varchar(255)  DEFAULT NULL COMMENT '服务器ip',
    `browser_name`    varchar(255)  DEFAULT NULL COMMENT '浏览器',
    `browser_version` varchar(255)  DEFAULT NULL COMMENT '浏览器版本',
    `os_name`         varchar(255)  DEFAULT NULL COMMENT '操作系统',
    `success`         char(1)       DEFAULT '0' COMMENT '是否成功',
    `message`         varchar(2255) DEFAULT NULL COMMENT '返回信息',
    `data_size`       double(11, 0) DEFAULT '0' COMMENT '数据条数',
    `begin_time`      datetime      DEFAULT NULL COMMENT '请求时间',
    `end_time`        datetime      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '返回时间',
    PRIMARY KEY (`request_id`) USING BTREE,
    KEY `index_success` (`success`),
    KEY `index_uri` (`request_uri`),
    KEY `index_begin_time` (`begin_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  ROW_FORMAT = DYNAMIC COMMENT ='接口请求日志';

-- ----------------------------
-- Table structure for sys_request_browser_d
-- ----------------------------
DROP TABLE IF EXISTS `sys_request_browser_d`;
CREATE TABLE if not exists `sys_request_browser_d`
(
    `log_id`          int(11)      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `os_name`         varchar(255) NOT NULL COMMENT '操作系统',
    `browser_name`    varchar(255)          DEFAULT NULL COMMENT '浏览器',
    `browser_version` varchar(255)          DEFAULT NULL COMMENT '浏览器版本',
    `request_count`   bigint(21)   NOT NULL DEFAULT '0' COMMENT '请求次数',
    `create_time`     datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '插入时间',
    PRIMARY KEY (`log_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  ROW_FORMAT = DYNAMIC COMMENT ='接口浏览器请求信息';

-- ----------------------------
-- Table structure for sys_request_error_log_d
-- ----------------------------
DROP TABLE IF EXISTS `sys_request_error_log_d`;
CREATE TABLE if not exists `sys_request_error_log_d`
(
    `log_id`        int(11)      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `request_uri`   varchar(255) NOT NULL COMMENT '请求uri',
    `quota_name`    varchar(255)          DEFAULT '' COMMENT '指标名称',
    `request_count` bigint(21)   NOT NULL DEFAULT '0' COMMENT '请求次数',
    `create_time`   datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`log_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  ROW_FORMAT = DYNAMIC COMMENT ='异常接口请求统计';

-- ----------------------------
-- Table structure for sys_request_log_d
-- ----------------------------
DROP TABLE IF EXISTS `sys_request_log_d`;
CREATE TABLE if not exists `sys_request_log_d`
(
    `log_id`        int(11)      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `request_uri`   varchar(255) NOT NULL COMMENT '请求uri',
    `quota_name`    varchar(255)          DEFAULT '' COMMENT '指标名称',
    `request_count` bigint(21)   NOT NULL DEFAULT '0' COMMENT '请求次数',
    `create_time`   datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`log_id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  ROW_FORMAT = DYNAMIC COMMENT ='接口请求统计';

SET FOREIGN_KEY_CHECKS = 1;

use q_frame_api;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
-- ----------------------------
-- View structure for sys_interface_request
-- ----------------------------
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
       `q_frame_log`.`sys_interface_request`.`server_ip`       AS `server_ip`,
       `q_frame_log`.`sys_interface_request`.`browser_name`    AS `browser_name`,
       `q_frame_log`.`sys_interface_request`.`browser_version` AS `browser_version`,
       `q_frame_log`.`sys_interface_request`.`os_name`         AS `os_name`,
       `q_frame_log`.`sys_interface_request`.`success`         AS `success`,
       `q_frame_log`.`sys_interface_request`.`message`         AS `message`,
       `q_frame_log`.`sys_interface_request`.`data_size`       AS `data_size`,
       `q_frame_log`.`sys_interface_request`.`begin_time`      AS `begin_time`,
       `q_frame_log`.`sys_interface_request`.`end_time`        AS `end_time`
from `q_frame_log`.`sys_interface_request`;
SET FOREIGN_KEY_CHECKS = 1;
-- 用户授权 建完再执行
GRANT ALL PRIVILEGES ON q_frame_api.* TO 'q_frame'@'%';
GRANT ALL PRIVILEGES ON q_frame_log.* TO 'q_frame'@'%';
