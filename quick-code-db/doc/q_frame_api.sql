/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50725
 Source Host           : localhost:3306
 Source Schema         : q_frame_api

 Target Server Type    : MySQL
 Target Server Version : 50725
 File Encoding         : 65001

 Date: 05/04/2021 14:30:08
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_datasource_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_datasource_config`;
CREATE TABLE `sys_datasource_config` (
  `datasource_id` varchar(255) NOT NULL,
  `app_id` varchar(255) NOT NULL DEFAULT '1' COMMENT '应用系统ID',
  `datasource_name` varchar(255) NOT NULL,
  `url` varchar(400) NOT NULL,
  `user_name` varchar(255) NOT NULL,
  `pass_word` varchar(255) DEFAULT NULL,
  `code` varchar(255) DEFAULT NULL,
  `initial_size` int(11) NOT NULL DEFAULT '1' COMMENT '初始化时建立物理连接的个数',
  `max_active` int(11) NOT NULL DEFAULT '10' COMMENT '最大连接池数量',
  `min_idle` int(11) NOT NULL DEFAULT '5' COMMENT '最小连接池数量',
  `max_wait` int(11) NOT NULL DEFAULT '6000' COMMENT '获取连接时最大等待时间',
  `database_type` varchar(255) NOT NULL,
  `remark` varchar(500) DEFAULT NULL,
  `filters` varchar(255) DEFAULT 'stat,wall' COMMENT '防火墙',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_id` varchar(255) DEFAULT NULL COMMENT '创建者',
  `update_id` varchar(255) DEFAULT NULL COMMENT '更新者',
  PRIMARY KEY (`datasource_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据源配置';

-- ----------------------------
-- Table structure for sys_script_workbench
-- ----------------------------
DROP TABLE IF EXISTS `sys_script_workbench`;
CREATE TABLE `sys_script_workbench` (
  `id` varchar(255) NOT NULL COMMENT '主键',
  `name` varchar(255) NOT NULL COMMENT '名称',
  `content` text NOT NULL COMMENT '内容',
  `script_mode` varchar(255) NOT NULL DEFAULT 'javascript' COMMENT '语言脚本',
  `cur_version` int(11) NOT NULL DEFAULT '1' COMMENT '编辑后版本+1',
  `ori_id` varchar(255) DEFAULT '-1' COMMENT '上一版本ID',
  `public_script` char(1) DEFAULT '0' COMMENT '是否公共函数:0:不是 ；1:是',
  `status` char(1) DEFAULT '1' COMMENT '是否可用:0:不可用 ；1:可用',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_id` varchar(255) DEFAULT NULL COMMENT '创建者',
  `update_id` varchar(255) DEFAULT NULL COMMENT '更新者',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='脚本工作台';

-- ----------------------------
-- Table structure for sys_script_workbench_bak
-- ----------------------------
DROP TABLE IF EXISTS `sys_script_workbench_bak`;
CREATE TABLE `sys_script_workbench_bak` (
  `id` varchar(255) NOT NULL COMMENT '主键',
  `name` varchar(255) NOT NULL COMMENT '名称',
  `content` text NOT NULL COMMENT '内容',
  `script_mode` varchar(255) NOT NULL DEFAULT 'javascript' COMMENT '语言脚本',
  `cur_version` int(11) NOT NULL DEFAULT '1' COMMENT '编辑后版本+1',
  `ori_id` varchar(255) DEFAULT '-1' COMMENT '上一版本ID',
  `public_script` char(1) DEFAULT '0' COMMENT '是否公共函数:0:不是 ；1:是',
  `status` char(1) DEFAULT '1' COMMENT '是否可用:0:不可用 ；1:可用',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_id` varchar(255) DEFAULT NULL COMMENT '创建者',
  `update_id` varchar(255) DEFAULT NULL COMMENT '更新者'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='脚本工作台';

-- ----------------------------
-- Triggers structure for table sys_script_workbench
-- ----------------------------
DROP TRIGGER IF EXISTS `script_update`;
delimiter ;;
CREATE TRIGGER `script_update` BEFORE UPDATE ON `sys_script_workbench` FOR EACH ROW insert INTO sys_script_workbench_bak SELECT * from sys_script_workbench where id = old.id
;;
delimiter ;

-- ----------------------------
-- Triggers structure for table sys_script_workbench
-- ----------------------------
DROP TRIGGER IF EXISTS `script_delete`;
delimiter ;;
CREATE TRIGGER `script_delete` BEFORE DELETE ON `sys_script_workbench` FOR EACH ROW insert INTO sys_script_workbench_bak SELECT * from sys_script_workbench where id = old.id
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
