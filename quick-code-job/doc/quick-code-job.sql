use q_frame_api;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for quick_blob_triggers
-- ----------------------------
DROP TABLE IF EXISTS `quick_blob_triggers`;
CREATE TABLE `quick_blob_triggers`
(
    `sched_name`    varchar(120) NOT NULL COMMENT '计划名',
    `trigger_name`  varchar(200) NOT NULL COMMENT '触发器名称',
    `trigger_group` varchar(200) NOT NULL COMMENT '触发器组',
    `blob_data`     blob,
    PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`),
    KEY `sched_name` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='以blob 类型存储的触发器';

-- ----------------------------
-- Table structure for quick_calendars
-- ----------------------------
DROP TABLE IF EXISTS `quick_calendars`;
CREATE TABLE `quick_calendars`
(
    `sched_name`    varchar(120) NOT NULL COMMENT '计划名称',
    `calendar_name` varchar(200) NOT NULL,
    `calendar`      blob         NOT NULL,
    PRIMARY KEY (`sched_name`, `calendar_name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='日历信息';

-- ----------------------------
-- Table structure for quick_cron_triggers
-- ----------------------------
DROP TABLE IF EXISTS `quick_cron_triggers`;
CREATE TABLE `quick_cron_triggers`
(
    `sched_name`      varchar(120) NOT NULL COMMENT '计划名称',
    `trigger_name`    varchar(200) NOT NULL COMMENT '触发器名称',
    `trigger_group`   varchar(200) NOT NULL COMMENT '触发器组',
    `cron_expression` varchar(120) NOT NULL COMMENT '时间表达式',
    `time_zone_id`    varchar(80) DEFAULT NULL COMMENT '时区id     nvarchar     80',
    PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='定时触发器';

-- ----------------------------
-- Table structure for quick_fired_triggers
-- ----------------------------
DROP TABLE IF EXISTS `quick_fired_triggers`;
CREATE TABLE `quick_fired_triggers`
(
    `sched_name`        varchar(120) NOT NULL COMMENT '计划名称',
    `entry_id`          varchar(95)  NOT NULL COMMENT '组标识',
    `trigger_name`      varchar(200) NOT NULL COMMENT '触发器名称',
    `trigger_group`     varchar(200) NOT NULL COMMENT '触发器组',
    `instance_name`     varchar(200) NOT NULL COMMENT '当前实例的名称',
    `fired_time`        bigint(13)   NOT NULL COMMENT '当前执行时间',
    `sched_time`        bigint(13)   NOT NULL COMMENT '计划时间',
    `priority`          int(11)      NOT NULL COMMENT '权重',
    `state`             varchar(16)  NOT NULL COMMENT '状态：waiting:等待 \r\npaused:暂停 \r\nacquired:正常执行 \r\nblocked：阻塞 \r\nerror：错误',
    `job_name`          varchar(200) DEFAULT NULL COMMENT '作业名称',
    `job_group`         varchar(200) DEFAULT NULL COMMENT '作业组',
    `is_nonconcurrent`  varchar(1)   DEFAULT NULL COMMENT '是否并行',
    `requests_recovery` varchar(1)   DEFAULT NULL COMMENT '是否要求唤醒',
    PRIMARY KEY (`sched_name`, `entry_id`),
    KEY `idx_qrtz_ft_trig_inst_name` (`sched_name`, `instance_name`),
    KEY `idx_qrtz_ft_inst_job_req_rcvry` (`sched_name`, `instance_name`, `requests_recovery`),
    KEY `idx_qrtz_ft_j_g` (`sched_name`, `job_name`, `job_group`),
    KEY `idx_qrtz_ft_jg` (`sched_name`, `job_group`),
    KEY `idx_qrtz_ft_t_g` (`sched_name`, `trigger_name`, `trigger_group`),
    KEY `idx_qrtz_ft_tg` (`sched_name`, `trigger_group`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='保存已经触发的触发器状态信息';

-- ----------------------------
-- Table structure for quick_job_details
-- ----------------------------
DROP TABLE IF EXISTS `quick_job_details`;
CREATE TABLE `quick_job_details`
(
    `sched_name`        varchar(120) NOT NULL COMMENT '计划名称',
    `job_name`          varchar(200) NOT NULL COMMENT '集群中job的名字',
    `job_group`         varchar(200) NOT NULL COMMENT '集群中job的所属组的名字',
    `description`       varchar(250) DEFAULT NULL COMMENT '描述',
    `job_class_name`    varchar(250) NOT NULL COMMENT '作业程序类名',
    `is_durable`        varchar(1)   NOT NULL COMMENT '是否持久',
    `is_nonconcurrent`  varchar(1)   NOT NULL COMMENT '是否并行',
    `is_update_data`    varchar(1)   NOT NULL COMMENT '是否更新',
    `requests_recovery` varchar(1)   NOT NULL COMMENT '是否要求唤醒',
    `job_data`          blob,
    PRIMARY KEY (`sched_name`, `job_name`, `job_group`),
    KEY `idx_qrtz_j_req_recovery` (`sched_name`, `requests_recovery`),
    KEY `idx_qrtz_j_grp` (`sched_name`, `job_group`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='job 详细信息';

-- ----------------------------
-- Table structure for quick_locks
-- ----------------------------
DROP TABLE IF EXISTS `quick_locks`;
CREATE TABLE `quick_locks`
(
    `sched_name` varchar(120) NOT NULL COMMENT '计划名称',
    `lock_name`  varchar(40)  NOT NULL COMMENT '锁名称',
    PRIMARY KEY (`sched_name`, `lock_name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='存储程序的悲观锁的信息(假如使用了悲观锁) ';

-- ----------------------------
-- Table structure for quick_paused_trigger_grps
-- ----------------------------
DROP TABLE IF EXISTS `quick_paused_trigger_grps`;
CREATE TABLE `quick_paused_trigger_grps`
(
    `sched_name`    varchar(120) NOT NULL COMMENT '计划名称',
    `trigger_group` varchar(200) NOT NULL COMMENT '触发器组',
    PRIMARY KEY (`sched_name`, `trigger_group`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='存放暂停掉的触发器';

-- ----------------------------
-- Table structure for quick_scheduler_state
-- ----------------------------
DROP TABLE IF EXISTS `quick_scheduler_state`;
CREATE TABLE `quick_scheduler_state`
(
    `sched_name`        varchar(120) NOT NULL COMMENT '计划名称',
    `instance_name`     varchar(200) NOT NULL COMMENT '实例名称',
    `last_checkin_time` bigint(13)   NOT NULL COMMENT '最后的检查时间',
    `checkin_interval`  bigint(13)   NOT NULL COMMENT '检查间隔',
    PRIMARY KEY (`sched_name`, `instance_name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='调度器状态';

-- ----------------------------
-- Table structure for quick_simple_triggers
-- ----------------------------
DROP TABLE IF EXISTS `quick_simple_triggers`;
CREATE TABLE `quick_simple_triggers`
(
    `sched_name`      varchar(120) NOT NULL COMMENT '计划名称',
    `trigger_name`    varchar(200) NOT NULL COMMENT '触发器名称',
    `trigger_group`   varchar(200) NOT NULL COMMENT '触发器组',
    `repeat_count`    bigint(7)    NOT NULL COMMENT '重复次数',
    `repeat_interval` bigint(12)   NOT NULL COMMENT '重复间隔',
    `times_triggered` bigint(10)   NOT NULL COMMENT '触发次数',
    PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='简单的触发器';

-- ----------------------------
-- Table structure for quick_simprop_triggers
-- ----------------------------
DROP TABLE IF EXISTS `quick_simprop_triggers`;
CREATE TABLE `quick_simprop_triggers`
(
    `sched_name`    varchar(120) NOT NULL COMMENT '计划名称',
    `trigger_name`  varchar(200) NOT NULL COMMENT '触发器名称',
    `trigger_group` varchar(200) NOT NULL COMMENT '触发器组',
    `str_prop_1`    varchar(512)   DEFAULT NULL,
    `str_prop_2`    varchar(512)   DEFAULT NULL,
    `str_prop_3`    varchar(512)   DEFAULT NULL,
    `int_prop_1`    int(11)        DEFAULT NULL,
    `int_prop_2`    int(11)        DEFAULT NULL,
    `long_prop_1`   bigint(20)     DEFAULT NULL,
    `long_prop_2`   bigint(20)     DEFAULT NULL,
    `dec_prop_1`    decimal(13, 4) DEFAULT NULL,
    `dec_prop_2`    decimal(13, 4) DEFAULT NULL,
    `bool_prop_1`   varchar(1)     DEFAULT NULL,
    `bool_prop_2`   varchar(1)     DEFAULT NULL,
    PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='存储calendarintervaltrigger和dailytimeintervaltrigger两种类型的触发器';

-- ----------------------------
-- Table structure for quick_triggers
-- ----------------------------
DROP TABLE IF EXISTS `quick_triggers`;
CREATE TABLE `quick_triggers`
(
    `sched_name`     varchar(120) NOT NULL COMMENT '计划名称',
    `trigger_name`   varchar(200) NOT NULL COMMENT '触发器名称',
    `trigger_group`  varchar(200) NOT NULL COMMENT '触发器组',
    `job_name`       varchar(200) NOT NULL COMMENT '作业名称',
    `job_group`      varchar(200) NOT NULL COMMENT '作业组',
    `description`    varchar(250) DEFAULT NULL COMMENT '描述',
    `next_fire_time` bigint(13)   DEFAULT NULL COMMENT '下次执行时间',
    `prev_fire_time` bigint(13)   DEFAULT NULL COMMENT '前一次',
    `priority`       int(11)      DEFAULT NULL COMMENT '优先权',
    `trigger_state`  varchar(16)  NOT NULL COMMENT '触发器状态',
    `trigger_type`   varchar(8)   NOT NULL COMMENT '触发器类型',
    `start_time`     bigint(13)   NOT NULL COMMENT '开始时间',
    `end_time`       bigint(13)   DEFAULT NULL COMMENT '结束时间',
    `calendar_name`  varchar(200) DEFAULT NULL COMMENT '日历名称',
    `misfire_instr`  smallint(2)  DEFAULT NULL COMMENT '失败次数',
    `job_data`       blob,
    PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`),
    KEY `idx_qrtz_t_j` (`sched_name`, `job_name`, `job_group`),
    KEY `idx_qrtz_t_jg` (`sched_name`, `job_group`),
    KEY `idx_qrtz_t_c` (`sched_name`, `calendar_name`),
    KEY `idx_qrtz_t_g` (`sched_name`, `trigger_group`),
    KEY `idx_qrtz_t_state` (`sched_name`, `trigger_state`),
    KEY `idx_qrtz_t_n_state` (`sched_name`, `trigger_name`, `trigger_group`, `trigger_state`),
    KEY `idx_qrtz_t_n_g_state` (`sched_name`, `trigger_group`, `trigger_state`),
    KEY `idx_qrtz_t_next_fire_time` (`sched_name`, `next_fire_time`),
    KEY `idx_qrtz_t_nft_st` (`sched_name`, `trigger_state`, `next_fire_time`),
    KEY `idx_qrtz_t_nft_misfire` (`sched_name`, `misfire_instr`, `next_fire_time`),
    KEY `idx_qrtz_t_nft_st_misfire` (`sched_name`, `misfire_instr`, `next_fire_time`, `trigger_state`),
    KEY `idx_qrtz_t_nft_st_misfire_grp` (`sched_name`, `misfire_instr`, `next_fire_time`, `trigger_group`,
                                         `trigger_state`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='触发器';

SET FOREIGN_KEY_CHECKS = 1;
