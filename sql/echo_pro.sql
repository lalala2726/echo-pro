/*
 Navicat Premium Dump SQL

 Source Server         : 192.168.10.120
 Source Server Type    : MySQL
 Source Server Version : 80403 (8.4.3)
 Source Host           : 192.168.10.120:3306
 Source Schema         : echo_pro

 Target Server Type    : MySQL
 Target Server Version : 80403 (8.4.3)
 File Encoding         : 65001

 Date: 18/08/2025 14:22:34
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for storage_config
-- ----------------------------
DROP TABLE IF EXISTS `storage_config`;
CREATE TABLE `storage_config`
(
    `id`            int           NOT NULL AUTO_INCREMENT COMMENT '主键',
    `storage_name`  varchar(100)  NOT NULL COMMENT '参数名称',
    `storage_key`   varchar(100)  NOT NULL COMMENT '参数键名',
    `storage_value` varchar(4096) NOT NULL COMMENT '存储值',
    `storage_type`  varchar(100)  NOT NULL COMMENT '存储类型',
    `is_primary`    tinyint(1)   DEFAULT '0' COMMENT '是否默认',
    `enable_trash`  tinyint      DEFAULT '0' COMMENT '是否启用回收站(1启用0不启用)',
    `create_by`     varchar(64)  DEFAULT NULL COMMENT '创建者',
    `create_time`   datetime     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`     varchar(64)  DEFAULT NULL COMMENT '更新者',
    `update_time`   datetime     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark`        varchar(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='文件配置表';

-- ----------------------------
-- Records of storage_config
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for storage_file
-- ----------------------------
DROP TABLE IF EXISTS `storage_file`;
CREATE TABLE `storage_file`
(
    `id`                     bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `file_name`              varchar(256) DEFAULT NULL COMMENT '文件名,存储在计算机中的文件名',
    `original_name`          varchar(255) DEFAULT NULL COMMENT '文件名',
    `content_type`           varchar(100) DEFAULT NULL COMMENT '文件类型，如 image/jpeg, application/pdf 等',
    `file_size`              bigint       DEFAULT NULL COMMENT '文件大小，字节为单位',
    `original_file_url`      varchar(500) DEFAULT NULL COMMENT '原始文件URL，直接访问地址',
    `original_relative_path` varchar(500) DEFAULT NULL COMMENT '原始文件相对路径，存储在服务器上的路径',
    `preview_image_url`      varchar(500) DEFAULT NULL COMMENT '预览图片',
    `preview_relative_path`  varchar(500) DEFAULT NULL COMMENT '压缩文件相对路径，存储在服务器上的路径',
    `file_extension`         varchar(20)  DEFAULT NULL COMMENT '文件扩展名',
    `storage_type`           varchar(50)  DEFAULT NULL COMMENT '存储类型 (LOCAL/MINIO/ALIYUN_OSS)',
    `bucket_name`            varchar(100) DEFAULT NULL COMMENT '存储桶名称（OSS/MINIO 使用）',
    `uploader_id`            bigint       DEFAULT NULL COMMENT '上传者ID',
    `uploader_name`          varchar(100) DEFAULT NULL COMMENT '上传者名称',
    `upload_time`            datetime     DEFAULT NULL COMMENT '上传时间',
    `original_trash_path`    varchar(500) DEFAULT NULL COMMENT '原始文件回收站路径',
    `preview_trash_path`     varchar(500) DEFAULT NULL COMMENT '预览图文件回收站路径',
    `update_time`            datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_trash`               tinyint(1)   DEFAULT '0' COMMENT '是否在回收站，1代表在回收站',
    `is_deleted`             tinyint(1)   DEFAULT '0' COMMENT '是否删除(0-未删除, 1-已删除)',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='文件上传记录表';

-- ----------------------------
-- Records of storage_file
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`
(
    `config_id`    int           NOT NULL AUTO_INCREMENT COMMENT '主键',
    `config_name`  varchar(100)  NOT NULL COMMENT '参数名称',
    `config_key`   varchar(100)  NOT NULL COMMENT '参数键名',
    `config_value` varchar(4096) NOT NULL COMMENT '参数键值',
    `create_by`    varchar(64)  DEFAULT NULL COMMENT '创建者',
    `create_time`  datetime     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`    varchar(64)  DEFAULT NULL COMMENT '更新者',
    `update_time`  datetime     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark`       varchar(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`config_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='系统配置表';

-- ----------------------------
-- Records of sys_config
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept`
(
    `dept_id`     bigint       NOT NULL AUTO_INCREMENT COMMENT '部门ID',
    `dept_name`   varchar(100) NOT NULL COMMENT '部门名称',
    `status`      tinyint(1)   DEFAULT '0' COMMENT '状态(0正常1停用)',
    `parent_id`   bigint       DEFAULT NULL COMMENT '父部门ID',
    `manager`     varchar(64)  DEFAULT NULL COMMENT '部门负责人',
    `description` text COMMENT '部门描述',
    `create_time` datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`   varchar(100) DEFAULT NULL COMMENT '创建人',
    `update_by`   varchar(100) DEFAULT NULL COMMENT '更新人',
    `remark`      text COMMENT '备注',
    PRIMARY KEY (`dept_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 9
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='部门表';

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
BEGIN;
INSERT INTO `sys_dept` (`dept_id`, `dept_name`, `status`, `parent_id`, `manager`, `description`, `create_time`,
                        `update_time`, `create_by`, `update_by`, `remark`)
VALUES (1, '西安总公司', 0, 0, '张经理', '西安总公司', '2025-04-13 13:40:28', '2025-08-16 22:21:22', 'DEVELOP',
        'DEVELOP', NULL);
INSERT INTO `sys_dept` (`dept_id`, `dept_name`, `status`, `parent_id`, `manager`, `description`, `create_time`,
                        `update_time`, `create_by`, `update_by`, `remark`)
VALUES (2, '雁塔区分部', 0, 1, NULL, NULL, '2025-04-13 13:40:46', '2025-04-24 10:07:17', 'DEVELOP', 'DEVELOP', NULL);
INSERT INTO `sys_dept` (`dept_id`, `dept_name`, `status`, `parent_id`, `manager`, `description`, `create_time`,
                        `update_time`, `create_by`, `update_by`, `remark`)
VALUES (3, '咸宁路分店', 0, 2, NULL, NULL, '2025-04-13 13:41:07', '2025-04-24 10:07:18', 'DEVELOP', 'DEVELOP', NULL);
INSERT INTO `sys_dept` (`dept_id`, `dept_name`, `status`, `parent_id`, `manager`, `description`, `create_time`,
                        `update_time`, `create_by`, `update_by`, `remark`)
VALUES (4, '兴庆宫分店', 0, 2, NULL, NULL, '2025-04-13 13:42:20', '2025-04-24 10:07:18', 'DEVELOP', 'DEVELOP', NULL);
INSERT INTO `sys_dept` (`dept_id`, `dept_name`, `status`, `parent_id`, `manager`, `description`, `create_time`,
                        `update_time`, `create_by`, `update_by`, `remark`)
VALUES (5, '高新区分部', 0, 1, NULL, NULL, '2025-04-13 13:42:45', '2025-08-16 22:17:32', 'DEVELOP', 'DEVELOP', NULL);
INSERT INTO `sys_dept` (`dept_id`, `dept_name`, `status`, `parent_id`, `manager`, `description`, `create_time`,
                        `update_time`, `create_by`, `update_by`, `remark`)
VALUES (7, '锦业一路分店', 0, 5, '韩经理', '', '2025-04-24 10:55:08', '2025-04-24 10:55:08', NULL, NULL, NULL);
INSERT INTO `sys_dept` (`dept_id`, `dept_name`, `status`, `parent_id`, `manager`, `description`, `create_time`,
                        `update_time`, `create_by`, `update_by`, `remark`)
VALUES (8, '西部大道分店', 0, 5, '产经理', '', '2025-04-24 14:01:28', '2025-04-24 14:01:28', NULL, NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for sys_dict_data
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data`
(
    `id`          bigint       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `dict_type`   varchar(100) NOT NULL COMMENT '字典类型（关联字典类型表dict_type）',
    `dict_label`  varchar(100) NOT NULL COMMENT '字典标签（中文显示）',
    `dict_value`  varchar(100) NOT NULL COMMENT '字典值（业务使用的值）',
    `color`       varchar(32)  DEFAULT NULL COMMENT '颜色',
    `sort`        int          DEFAULT '0' COMMENT '排序（越小越前）',
    `status`      tinyint(1)   DEFAULT '1' COMMENT '状态（1=启用，0=禁用）',
    `remark`      varchar(255) DEFAULT NULL COMMENT '备注',
    `create_by`   varchar(50)  DEFAULT NULL COMMENT '创建人',
    `create_time` datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   varchar(50)  DEFAULT NULL COMMENT '更新人',
    `update_time` datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_dict_type` (`dict_type`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 17
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='字典数据表';

-- ----------------------------
-- Records of sys_dict_data
-- ----------------------------
BEGIN;
INSERT INTO `sys_dict_data` (`id`, `dict_type`, `dict_label`, `dict_value`, `color`, `sort`, `status`, `remark`,
                             `create_by`, `create_time`, `update_by`, `update_time`)
VALUES (1, 'gender', '男', '0', NULL, 1, 1, '男性', 'admin', '2025-07-14 17:35:57', 'zhangchuang',
        '2025-07-18 19:49:54');
INSERT INTO `sys_dict_data` (`id`, `dict_type`, `dict_label`, `dict_value`, `color`, `sort`, `status`, `remark`,
                             `create_by`, `create_time`, `update_by`, `update_time`)
VALUES (2, 'gender', '女', '1', NULL, 2, 1, '女性', 'admin', '2025-07-14 17:35:57', 'zhangchuang',
        '2025-07-18 19:49:58');
INSERT INTO `sys_dict_data` (`id`, `dict_type`, `dict_label`, `dict_value`, `color`, `sort`, `status`, `remark`,
                             `create_by`, `create_time`, `update_by`, `update_time`)
VALUES (3, 'user_status', '启用', '1', NULL, 1, 1, '账号启用', 'admin', '2025-07-14 17:35:57', NULL,
        '2025-07-14 17:35:57');
INSERT INTO `sys_dict_data` (`id`, `dict_type`, `dict_label`, `dict_value`, `color`, `sort`, `status`, `remark`,
                             `create_by`, `create_time`, `update_by`, `update_time`)
VALUES (4, 'user_status', '禁用', '0', NULL, 2, 1, '账号禁用', 'admin', '2025-07-14 17:35:57', NULL,
        '2025-07-14 17:35:57');
INSERT INTO `sys_dict_data` (`id`, `dict_type`, `dict_label`, `dict_value`, `color`, `sort`, `status`, `remark`,
                             `create_by`, `create_time`, `update_by`, `update_time`)
VALUES (11, 'system_status', '正常', '0', NULL, 0, 0, NULL, 'zhangchuang', '2025-07-16 14:25:36', NULL,
        '2025-07-16 14:25:36');
INSERT INTO `sys_dict_data` (`id`, `dict_type`, `dict_label`, `dict_value`, `color`, `sort`, `status`, `remark`,
                             `create_by`, `create_time`, `update_by`, `update_time`)
VALUES (12, 'system_status', '禁用', '1', NULL, 0, 0, NULL, 'zhangchuang', '2025-07-16 14:25:44', 'zhangchuang',
        '2025-08-12 09:26:54');
INSERT INTO `sys_dict_data` (`id`, `dict_type`, `dict_label`, `dict_value`, `color`, `sort`, `status`, `remark`,
                             `create_by`, `create_time`, `update_by`, `update_time`)
VALUES (13, 'notice_type', '系统消息', 'system', NULL, 0, 0, NULL, 'zhangchuang', '2025-08-12 10:08:40', 'zhangchuang',
        '2025-08-12 10:09:03');
INSERT INTO `sys_dict_data` (`id`, `dict_type`, `dict_label`, `dict_value`, `color`, `sort`, `status`, `remark`,
                             `create_by`, `create_time`, `update_by`, `update_time`)
VALUES (14, 'notice_type', '通知消息', 'notice', NULL, 0, 0, NULL, 'zhangchuang', '2025-08-12 10:09:16', 'zhangchuang',
        '2025-08-12 10:09:58');
INSERT INTO `sys_dict_data` (`id`, `dict_type`, `dict_label`, `dict_value`, `color`, `sort`, `status`, `remark`,
                             `create_by`, `create_time`, `update_by`, `update_time`)
VALUES (15, 'notice_type', '公告消息', 'announcement', 'blue', 0, 0, NULL, 'zhangchuang', '2025-08-12 10:09:28',
        'zhangchuang', '2025-08-12 10:21:20');
INSERT INTO `sys_dict_data` (`id`, `dict_type`, `dict_label`, `dict_value`, `color`, `sort`, `status`, `remark`,
                             `create_by`, `create_time`, `update_by`, `update_time`)
VALUES (16, 'test_dice', '正常', 'announcement', 'blue', 0, 0, '测试测试', 'admin', '2025-08-16 22:30:24', 'admin',
        '2025-08-16 22:30:40');
COMMIT;

-- ----------------------------
-- Table structure for sys_dict_type
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type`
(
    `id`          bigint       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `dict_type`   varchar(100) NOT NULL COMMENT '字典类型（唯一）',
    `dict_name`   varchar(100) NOT NULL COMMENT '字典名称',
    `status`      tinyint(1)   DEFAULT '0' COMMENT '状态（0=启用，1=禁用）',
    `remark`      varchar(255) DEFAULT NULL COMMENT '备注',
    `create_by`   varchar(50)  DEFAULT NULL COMMENT '创建人',
    `create_time` datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   varchar(50)  DEFAULT NULL COMMENT '更新人',
    `update_time` datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_dict_type` (`dict_type`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 7
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='字典类型表';

-- ----------------------------
-- Records of sys_dict_type
-- ----------------------------
BEGIN;
INSERT INTO `sys_dict_type` (`id`, `dict_type`, `dict_name`, `status`, `remark`, `create_by`, `create_time`,
                             `update_by`, `update_time`)
VALUES (1, 'gender', '性别', 0, '性别分类', 'admin', '2025-07-14 17:35:50', 'zhangchuang', '2025-07-16 14:13:34');
INSERT INTO `sys_dict_type` (`id`, `dict_type`, `dict_name`, `status`, `remark`, `create_by`, `create_time`,
                             `update_by`, `update_time`)
VALUES (2, 'user_status', '用户状态', 0, '用户启用禁用状态', 'admin', '2025-07-14 17:35:50', 'zhangchuang',
        '2025-07-14 20:25:29');
INSERT INTO `sys_dict_type` (`id`, `dict_type`, `dict_name`, `status`, `remark`, `create_by`, `create_time`,
                             `update_by`, `update_time`)
VALUES (3, 'notice_type', '通知类型', 0, '系统通知类型', 'admin', '2025-07-14 17:35:50', 'zhangchuang',
        '2025-07-14 20:25:31');
INSERT INTO `sys_dict_type` (`id`, `dict_type`, `dict_name`, `status`, `remark`, `create_by`, `create_time`,
                             `update_by`, `update_time`)
VALUES (5, 'system_status', '系统状态', 0, '系统状态', 'zhangchuang', '2025-07-16 14:18:07', 'zhangchuang',
        '2025-07-16 14:22:04');
INSERT INTO `sys_dict_type` (`id`, `dict_type`, `dict_name`, `status`, `remark`, `create_by`, `create_time`,
                             `update_by`, `update_time`)
VALUES (6, 'test_dice', '测试字典', 1, '测试字典', 'admin', '2025-08-16 22:30:09', NULL, '2025-08-16 22:30:09');
COMMIT;

-- ----------------------------
-- Table structure for sys_job
-- ----------------------------
DROP TABLE IF EXISTS `sys_job`;
CREATE TABLE `sys_job`
(
    `job_id`             bigint                                                        NOT NULL AUTO_INCREMENT COMMENT '任务ID',
    `job_name`           varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '任务名称',
    `invoke_target`      varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '调用目标字符串',
    `schedule_type`      int                                                           NOT NULL DEFAULT '0' COMMENT '调度策略（0=Cron表达式 1=固定频率 2=固定延迟 3=一次性执行）',
    `cron_expression`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci          DEFAULT NULL COMMENT 'cron执行表达式',
    `fixed_rate`         bigint                                                                 DEFAULT NULL COMMENT '固定频率间隔（毫秒）',
    `fixed_delay`        bigint                                                                 DEFAULT NULL COMMENT '固定延迟间隔（毫秒）',
    `initial_delay`      bigint                                                                 DEFAULT NULL COMMENT '初始延迟时间（毫秒）',
    `misfire_policy`     int                                                           NOT NULL DEFAULT '0' COMMENT '计划执行错误策略（0=默认 1=立即执行 2=执行一次 3=放弃执行）',
    `concurrent`         int                                                           NOT NULL DEFAULT '1' COMMENT '是否并发执行（0允许 1禁止）',
    `status`             int                                                           NOT NULL DEFAULT '1' COMMENT '任务状态（0正常 1暂停）',
    `priority`           int                                                           NOT NULL DEFAULT '5' COMMENT '任务优先级',
    `description`        varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci          DEFAULT NULL COMMENT '任务描述',
    `job_data`           text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '任务参数',
    `dependent_job_ids`  varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci          DEFAULT NULL COMMENT '依赖任务ID（多个用逗号分隔）',
    `max_retry_count`    int                                                           NOT NULL DEFAULT '0' COMMENT '最大重试次数',
    `retry_interval`     bigint                                                        NOT NULL DEFAULT '0' COMMENT '重试间隔（毫秒）',
    `timeout`            bigint                                                        NOT NULL DEFAULT '0' COMMENT '超时时间（毫秒）',
    `start_time`         datetime                                                               DEFAULT NULL COMMENT '开始时间',
    `end_time`           datetime                                                               DEFAULT NULL COMMENT '结束时间',
    `next_fire_time`     datetime                                                               DEFAULT NULL COMMENT '下次执行时间',
    `previous_fire_time` datetime                                                               DEFAULT NULL COMMENT '上次执行时间',
    `create_time`        datetime                                                               DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`        datetime                                                               DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`          varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci           DEFAULT NULL COMMENT '创建者',
    `update_by`          varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci           DEFAULT NULL COMMENT '更新者',
    `remark`             varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci          DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`job_id`),
    UNIQUE KEY `uk_job_name` (`job_name`),
    KEY `idx_status` (`status`),
    KEY `idx_next_fire_time` (`next_fire_time`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 3
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='定时任务表';

-- ----------------------------
-- Records of sys_job
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for sys_job_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_job_log`;
CREATE TABLE `sys_job_log`
(
    `job_log_id`     bigint                                                        NOT NULL AUTO_INCREMENT COMMENT '任务日志ID',
    `job_id`         bigint                                                        NOT NULL COMMENT '任务ID',
    `job_name`       varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '任务名称',
    `invoke_target`  varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '调用目标字符串',
    `job_data`       text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '任务参数',
    `job_message`    text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '日志信息',
    `status`         int                                                           NOT NULL DEFAULT '0' COMMENT '执行状态（0正常 1失败）',
    `exception_info` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '异常信息',
    `start_time`     datetime                                                               DEFAULT NULL COMMENT '开始时间',
    `end_time`       datetime                                                               DEFAULT NULL COMMENT '结束时间',
    `execute_time`   bigint                                                                 DEFAULT NULL COMMENT '执行耗时（毫秒）',
    `server_ip`      varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci           DEFAULT NULL COMMENT '服务器IP',
    `server_name`    varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci          DEFAULT NULL COMMENT '服务器名称',
    `retry_count`    int                                                           NOT NULL DEFAULT '0' COMMENT '重试次数',
    `trigger_type`   varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci           DEFAULT NULL COMMENT '触发器类型',
    `create_time`    datetime                                                               DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    datetime                                                               DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`      varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci           DEFAULT NULL COMMENT '创建者',
    `update_by`      varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci           DEFAULT NULL COMMENT '更新者',
    `remark`         varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci          DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`job_log_id`),
    KEY `idx_job_id` (`job_id`),
    KEY `idx_job_name` (`job_name`),
    KEY `idx_status` (`status`),
    KEY `idx_start_time` (`start_time`),
    CONSTRAINT `fk_job_log_job_id` FOREIGN KEY (`job_id`) REFERENCES `sys_job` (`job_id`) ON DELETE CASCADE
) ENGINE = InnoDB
  AUTO_INCREMENT = 2
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='定时任务执行日志表';

-- ----------------------------
-- Records of sys_job_log
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for sys_login_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_login_log`;
CREATE TABLE `sys_login_log`
(
    `id`         bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `username`   varchar(256) NOT NULL COMMENT '用户名',
    `status`     tinyint(1)   NOT NULL COMMENT '登录状态（0成功 1失败）',
    `ip`         varchar(64)  DEFAULT NULL COMMENT '登录IP',
    `region`     varchar(255) DEFAULT NULL COMMENT '区域',
    `browser`    varchar(255) DEFAULT NULL COMMENT '浏览器',
    `os`         varchar(255) DEFAULT NULL COMMENT '操作系统',
    `login_time` datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`  varchar(100) DEFAULT NULL COMMENT '创建人',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='系统登录日志';

-- ----------------------------
-- Records of sys_login_log
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`
(
    `id`                    bigint       NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `name`                  varchar(100) NOT NULL COMMENT '名称',
    `title`                 varchar(100) NOT NULL COMMENT '标题',
    `path`                  varchar(200)          DEFAULT NULL COMMENT '路径',
    `type`                  varchar(50)  NOT NULL COMMENT '类型',
    `status`                tinyint      NOT NULL DEFAULT '0' COMMENT '状态',
    `parent_id`             bigint       NOT NULL DEFAULT '0' COMMENT '父级ID',
    `active_path`           varchar(200)          DEFAULT NULL COMMENT '激活路径',
    `active_icon`           varchar(50)           DEFAULT NULL COMMENT '激活图标',
    `icon`                  varchar(50)           DEFAULT NULL COMMENT '图标',
    `component`             varchar(200)          DEFAULT NULL COMMENT '组件',
    `permission`            varchar(200)          DEFAULT NULL COMMENT '权限标识',
    `badge_type`            varchar(128)          DEFAULT NULL COMMENT '徽标类型',
    `badge`                 varchar(50)           DEFAULT NULL COMMENT '徽标',
    `badge_variants`        varchar(128)          DEFAULT NULL COMMENT '徽标颜色',
    `keep_alive`            tinyint               DEFAULT '0' COMMENT '是否缓存',
    `affix_tab`             tinyint               DEFAULT '0' COMMENT '是否固定标签页',
    `hide_in_menu`          tinyint               DEFAULT '0' COMMENT '隐藏菜单',
    `hide_children_in_menu` tinyint               DEFAULT '0' COMMENT '隐藏子菜单',
    `hide_in_breadcrumb`    tinyint               DEFAULT '0' COMMENT '隐藏在面包屑中',
    `hide_in_tab`           tinyint               DEFAULT '0' COMMENT '隐藏在标签页中',
    `link`                  varchar(256)          DEFAULT NULL COMMENT '外部链接地址',
    `sort`                  int          NOT NULL DEFAULT '0' COMMENT '排序',
    `create_time`           datetime              DEFAULT (now()) COMMENT '创建时间',
    `update_time`           datetime              DEFAULT (now()) COMMENT '更新时间',
    `create_by`             varchar(64)           DEFAULT NULL COMMENT '创建者',
    `update_by`             varchar(64)           DEFAULT NULL COMMENT '更新者',
    `remark`                varchar(500)          DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 218
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='菜单表';

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
BEGIN;
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (1, 'SystemManage', '系统管理', '/system', 'catalog', 0, 0, '', '', 'carbon:settings', '', '', '', '',
        'destructive', 0, 0, 0, 0, 0, 0, '', 1, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init', 'admin',
        NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (2, 'Monitor', '系统监控', '/monitor', 'catalog', 0, 0, '', '', 'carbon:cloud-monitoring', NULL, '', '', '', '',
        0, 0, 0, 0, 0, 0, '', 2, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init', 'zhangchuang', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (3, 'SystemTools', '系统工具', '/tool', 'catalog', 0, 0, NULL, NULL, 'carbon:tool-kit', NULL, NULL, NULL, NULL,
        NULL, 0, 0, 0, 0, 0, 0, '', 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init', 'zhangchuang',
        NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (4, 'UserManage', '用户管理', '/system/user', 'menu', 0, 1, '', '', 'carbon:user', '/system/user/index', '', '',
        '', 'default', 0, 0, 0, 0, 0, 0, '', 10, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init', 'admin',
        NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (5, 'RoleManage', '角色管理', '/system/role', 'menu', 0, 1, '', '', 'carbon:user-role', '/system/role/list', '',
        '', '', '', 0, 0, 0, 0, 0, 0, '', 10, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init', 'admin',
        NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (6, 'MenuManage', '菜单管理', '/system/menu', 'menu', 0, 1, NULL, NULL, 'carbon:menu', '/system/menu/list', NULL,
        NULL, NULL, NULL, 1, 0, 0, 0, 0, 0, '', 9, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init',
        'zhangchuang', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (7, 'DeptManage', '部门管理', '/system/dept', 'menu', 0, 1, '', '', 'carbon:column-dependency',
        '/system/dept/list', '', '', '', '', 0, 0, 0, 0, 0, 0, '', 10, '2025-08-07 08:32:15', '2025-08-07 08:32:15',
        'system_init', 'admin', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (8, 'PostManage', '岗位管理', '/system/post', 'menu', 0, 1, NULL, NULL, 'carbon:workspace', '/system/post/index',
        NULL, NULL, NULL, NULL, 1, 0, 0, 0, 0, 0, NULL, 5, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init',
        NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (9, 'DictManage', '字典管理', '/system/dict', 'menu', 0, 1, NULL, NULL, 'carbon:package-text-analysis',
        '/system/dict/type/index', NULL, NULL, NULL, NULL, 1, 0, 0, 0, 0, 0, '', 7, '2025-08-07 08:32:15',
        '2025-08-07 08:32:15', 'system_init', 'zhangchuang', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (10, 'DictData', '字典数据', '/system/dict/data/:id', 'menu', 0, 1, NULL, NULL, 'carbon:report-data',
        '/system/dict/data/index', NULL, NULL, NULL, NULL, 0, 0, 1, 0, 0, 0, '', 7, '2025-08-07 08:32:15',
        '2025-08-07 08:32:15', 'system_init', 'zhangchuang', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (11, 'SystemStorage', '系统存储', '/system/storage', 'catalog', 0, 1, NULL, NULL, 'carbon:block-storage', NULL,
        NULL, NULL, NULL, NULL, 0, 0, 0, 0, 0, 0, '', 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init',
        'zhangchuang', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (12, 'SystemLog', '系统日志', '/system/log', 'catalog', 0, 1, NULL, NULL,
        'carbon:ibm-knowledge-catalog-standard', NULL, NULL, NULL, NULL, NULL, 0, 0, 0, 0, 0, 0, '', 0,
        '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init', 'zhangchuang', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (13, 'OnlineManage', '在线管理', '/system/online', 'catalog', 0, 1, '', '', 'carbon:user-online', '', '', '', '',
        '', 0, 0, 0, 0, 0, 0, '', 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init', 'zhangchuang', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (14, 'StorageConfig', '存储配置', '/system/storage/config', 'menu', 0, 11, NULL, NULL,
        'carbon:ibm-global-storage-architecture', '/system/storage/config/index', NULL, NULL, NULL, NULL, 0, 0, 0, 0, 0,
        0, NULL, 1, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (15, 'StorageFile', '存储文件', '/system/storage/file', 'menu', 0, 11, NULL, NULL,
        'carbon:ibm-cloud-vpc-file-storage', '/system/storage/file/index', NULL, NULL, NULL, NULL, 0, 0, 0, 0, 0, 0,
        NULL, 2, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (16, 'LoginLog', '登录日志', '/system/log/login', 'menu', 0, 12, NULL, NULL, 'carbon:login',
        '/system/log/login/index', NULL, NULL, NULL, NULL, 0, 0, 0, 0, 0, 0, NULL, 1, '2025-08-07 08:32:15',
        '2025-08-07 08:32:15', 'system_init', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (17, 'OperationLog', '操作日志', '/system/log/operation', 'menu', 0, 12, NULL, NULL, 'carbon:operations-field',
        '/system/log/operation/index', NULL, NULL, NULL, NULL, 0, 0, 0, 0, 0, 0, NULL, 2, '2025-08-07 08:32:15',
        '2025-08-07 08:32:15', 'system_init', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (18, 'SystemMetrics', '系统指标', '/monitor/metrics', 'menu', 0, 2, NULL, NULL, 'carbon:business-metrics',
        '/monitor/metrics/index', NULL, NULL, NULL, NULL, 0, 0, 0, 0, 0, 0, NULL, 1, '2025-08-07 08:32:15',
        '2025-08-07 08:32:15', 'system_init', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (19, 'SqlMonitor', '数据监控', '/monitor/database', 'embedded', 0, 2, '', '', 'carbon:db2-database',
        'IFrameView', '', '', '', '', 1, 0, 0, 0, 0, 0, 'https://echo.zhangchuangla.cn/api/druid/login.html', 2,
        '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init', 'admin', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (20, 'Job', '定时任务', '/tool/job', 'catalog', 0, 3, '', '', 'carbon:network-time-protocol', '', '', '', '', '',
        0, 0, 0, 0, 0, 0, '', 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init', 'zhangchuang', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (21, 'OpenApi', '接口文档', '/tool/openapi', 'embedded', 0, 3, '', '', 'carbon:api-1', 'IFrameView', '', '', '',
        '', 1, 0, 0, 0, 0, 0, 'https://echo.zhangchuangla.cn/api/swagger-ui/index.html', 2, '2025-08-07 08:32:15',
        '2025-08-07 08:32:15', 'system_init', 'admin', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (22, 'JobManage', '任务管理', '/tool/job/manage', 'menu', 0, 20, NULL, NULL, 'material-symbols:manage-history',
        '/tool/job/manage/index', NULL, NULL, NULL, NULL, 0, 0, 0, 0, 0, 0, NULL, 1, '2025-08-07 08:32:15',
        '2025-08-07 08:32:15', 'system_init', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (23, 'JobLog', '任务日志', '/tool/job/log', 'menu', 0, 20, '', '', 'carbon:notebook-reference',
        '/tool/job/log/index', '', '', '', '', 0, 0, 0, 0, 0, 0, '', 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15',
        'system_init', 'zhangchuang', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (24, 'DeviceManage', '设备管理', '/system/online/device', 'menu', 0, 13, NULL, NULL, 'carbon:devices',
        '/system/online/device/index', NULL, NULL, NULL, NULL, 0, 0, 0, 0, 0, 0, NULL, 1, '2025-08-07 08:32:15',
        '2025-08-07 08:32:15', 'system_init', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (25, 'SessionManage', '会话管理', '/system/online/session', 'menu', 0, 13, NULL, NULL, 'carbon:user-activity',
        '/system/online/session/index', NULL, NULL, NULL, NULL, 0, 0, 0, 0, 0, 0, NULL, 2, '2025-08-07 08:32:15',
        '2025-08-07 08:32:15', 'system_init', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (101, 'addUser', '新增用户', '', 'button', 0, 4, NULL, NULL, NULL, NULL, 'system:user:add', NULL, NULL, NULL, 0,
        0, 0, 0, 0, 0, NULL, 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (102, 'deleteUser', '删除用户', '', 'button', 0, 4, NULL, NULL, NULL, NULL, 'system:user:delete', NULL, NULL,
        NULL, 0, 0, 0, 0, 0, 0, NULL, 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (103, 'updateUser', '修改用户', '', 'button', 0, 4, NULL, NULL, NULL, NULL, 'system:user:update', NULL, NULL,
        NULL, 0, 0, 0, 0, 0, 0, NULL, 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (104, 'queryUser', '查询用户', '', 'button', 0, 4, NULL, NULL, NULL, NULL, 'system:user:query', NULL, NULL, NULL,
        0, 0, 0, 0, 0, 0, NULL, 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (105, 'listUser', '用户列表', '', 'button', 0, 4, NULL, NULL, NULL, NULL, 'system:user:list', NULL, NULL, NULL,
        0, 0, 0, 0, 0, 0, NULL, 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (106, 'exportUser', '导出用户', '', 'button', 0, 4, NULL, NULL, NULL, NULL, 'system:user:export', NULL, NULL,
        NULL, 0, 0, 0, 0, 0, 0, NULL, 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (107, 'addMenu', '新增菜单', '', 'button', 0, 6, NULL, NULL, NULL, NULL, 'system:menu:add', NULL, NULL, NULL, 0,
        0, 0, 0, 0, 0, NULL, 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (108, 'deleteMenu', '删除菜单', '', 'button', 0, 6, NULL, NULL, NULL, NULL, 'system:menu:delete', NULL, NULL,
        NULL, 0, 0, 0, 0, 0, 0, NULL, 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (109, 'updateMenu', '修改菜单', '', 'button', 0, 6, NULL, NULL, NULL, NULL, 'system:menu:update', NULL, NULL,
        NULL, 0, 0, 0, 0, 0, 0, NULL, 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (110, 'queryMenu', '查询菜单', '', 'button', 0, 6, NULL, NULL, NULL, NULL, 'system:menu:query', NULL, NULL, NULL,
        0, 0, 0, 0, 0, 0, NULL, 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (111, 'listMenu', '菜单列表', '', 'button', 0, 6, NULL, NULL, NULL, NULL, 'system:menu:list', NULL, NULL, NULL,
        0, 0, 0, 0, 0, 0, NULL, 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (112, 'exportMenu', '导出菜单', '', 'button', 0, 6, NULL, NULL, NULL, NULL, 'system:menu:export', NULL, NULL,
        NULL, 0, 0, 0, 0, 0, 0, NULL, 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (113, 'addDept', '新增部门', '', 'button', 0, 7, NULL, NULL, NULL, NULL, 'system:dept:add', NULL, NULL, NULL, 0,
        0, 0, 0, 0, 0, NULL, 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (114, 'deleteDept', '删除部门', '', 'button', 0, 7, NULL, NULL, NULL, NULL, 'system:dept:delete', NULL, NULL,
        NULL, 0, 0, 0, 0, 0, 0, NULL, 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (115, 'updateDept', '修改部门', '', 'button', 0, 7, NULL, NULL, NULL, NULL, 'system:dept:update', NULL, NULL,
        NULL, 0, 0, 0, 0, 0, 0, NULL, 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (116, 'queryDept', '查询部门', '', 'button', 0, 7, NULL, NULL, NULL, NULL, 'system:dept:query', NULL, NULL, NULL,
        0, 0, 0, 0, 0, 0, NULL, 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (117, 'listDept', '部门列表', '', 'button', 0, 7, NULL, NULL, NULL, NULL, 'system:dept:list', NULL, NULL, NULL,
        0, 0, 0, 0, 0, 0, NULL, 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (118, 'exportDept', '导出部门', '', 'button', 0, 7, NULL, NULL, NULL, NULL, 'system:dept:export', NULL, NULL,
        NULL, 0, 0, 0, 0, 0, 0, NULL, 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (119, 'addPost', '新增岗位', '', 'button', 0, 8, NULL, NULL, NULL, NULL, 'system:post:add', NULL, NULL, NULL, 0,
        0, 0, 0, 0, 0, NULL, 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (120, 'deletePost', '删除岗位', '', 'button', 0, 8, NULL, NULL, NULL, NULL, 'system:post:delete', NULL, NULL,
        NULL, 0, 0, 0, 0, 0, 0, NULL, 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (121, 'updatePost', '修改岗位', '', 'button', 0, 8, NULL, NULL, NULL, NULL, 'system:post:update', NULL, NULL,
        NULL, 0, 0, 0, 0, 0, 0, NULL, 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (122, 'queryPost', '查询岗位', '', 'button', 0, 8, NULL, NULL, NULL, NULL, 'system:post:query', NULL, NULL, NULL,
        0, 0, 0, 0, 0, 0, NULL, 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (123, 'listPost', '岗位列表', '', 'button', 0, 8, NULL, NULL, NULL, NULL, 'system:post:list', NULL, NULL, NULL,
        0, 0, 0, 0, 0, 0, NULL, 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (124, 'exportPost', '导出岗位', '', 'button', 0, 8, NULL, NULL, NULL, NULL, 'system:post:export', NULL, NULL,
        NULL, 0, 0, 0, 0, 0, 0, NULL, 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (125, 'addDictType', '新增字典类型', '', 'button', 0, 9, NULL, NULL, NULL, NULL, 'system:dict-type:add', NULL,
        NULL, NULL, 0, 0, 0, 0, 0, 0, '', 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init', 'zhangchuang',
        NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (126, 'deleteDictType', '删除字典类型', '', 'button', 0, 9, NULL, NULL, NULL, NULL, 'system:dict-type:delete',
        NULL, NULL, NULL, 0, 0, 0, 0, 0, 0, '', 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init',
        'zhangchuang', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (127, 'updateDictType', '修改字典类型', '', 'button', 0, 9, NULL, NULL, NULL, NULL, 'system:dict-type:update',
        NULL, NULL, NULL, 0, 0, 0, 0, 0, 0, '', 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init',
        'zhangchuang', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (128, 'queryDictType', '查询字典类型', '', 'button', 0, 9, NULL, NULL, NULL, NULL, 'system:dict-type:query',
        NULL, NULL, NULL, 0, 0, 0, 0, 0, 0, '', 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init',
        'zhangchuang', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (129, 'listDictType', '字典类型列表', '', 'button', 0, 9, NULL, NULL, NULL, NULL, 'system:dict-type:list', NULL,
        NULL, NULL, 0, 0, 0, 0, 0, 0, '', 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init', 'zhangchuang',
        NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (130, 'exportDictType', '导出字典类型', '', 'button', 0, 9, NULL, NULL, NULL, NULL, 'system:dict-type:export',
        NULL, NULL, NULL, 0, 0, 0, 0, 0, 0, '', 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init',
        'zhangchuang', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (131, 'addDictData', '新增字典数据', '', 'button', 0, 10, NULL, NULL, NULL, NULL, 'system:dict-data:add', NULL,
        NULL, NULL, 0, 0, 0, 0, 0, 0, '', 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init', 'zhangchuang',
        NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (132, 'deleteDictData', '删除字典数据', '', 'button', 0, 10, NULL, NULL, NULL, NULL, 'system:dict-data:delete',
        NULL, NULL, NULL, 0, 0, 0, 0, 0, 0, '', 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init',
        'zhangchuang', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (133, 'updateDictData', '修改字典数据', '', 'button', 0, 10, NULL, NULL, NULL, NULL, 'system:dict-data:update',
        NULL, NULL, NULL, 0, 0, 0, 0, 0, 0, '', 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init',
        'zhangchuang', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (134, 'queryDictData', '查询字典数据', '', 'button', 0, 10, NULL, NULL, NULL, NULL, 'system:dict-data:query',
        NULL, NULL, NULL, 0, 0, 0, 0, 0, 0, '', 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init',
        'zhangchuang', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (135, 'listDictData', '字典数据列表', '', 'button', 0, 10, NULL, NULL, NULL, NULL, 'system:dict-data:list', NULL,
        NULL, NULL, 0, 0, 0, 0, 0, 0, '', 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init', 'zhangchuang',
        NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (136, 'exportDictData', '导出字典数据', '', 'button', 0, 10, NULL, NULL, NULL, NULL, 'system:dict-data:export',
        NULL, NULL, NULL, 0, 0, 0, 0, 0, 0, '', 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init',
        'zhangchuang', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (137, 'addStorageConfig', '新增配置', '', 'button', 0, 14, NULL, NULL, NULL, NULL, 'system:storage-config:add',
        NULL, NULL, NULL, 0, 0, 0, 0, 0, 0, '', 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init',
        'zhangchuang', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (138, 'deleteStorageConfig', '删除配置', '', 'button', 0, 14, NULL, NULL, NULL, NULL,
        'system:storage-config:delete', NULL, NULL, NULL, 0, 0, 0, 0, 0, 0, '', 0, '2025-08-07 08:32:15',
        '2025-08-07 08:32:15', 'system_init', 'zhangchuang', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (139, 'updateStorageConfig', '修改配置', '', 'button', 0, 14, NULL, NULL, NULL, NULL,
        'system:storage-config:update', NULL, NULL, NULL, 0, 0, 0, 0, 0, 0, '', 0, '2025-08-07 08:32:15',
        '2025-08-07 08:32:15', 'system_init', 'zhangchuang', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (140, 'queryStorageConfig', '查询配置', '', 'button', 0, 14, NULL, NULL, NULL, NULL,
        'system:storage-config:query', NULL, NULL, NULL, 0, 0, 0, 0, 0, 0, '', 0, '2025-08-07 08:32:15',
        '2025-08-07 08:32:15', 'system_init', 'zhangchuang', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (141, 'listStorageConfig', '配置列表', '', 'button', 0, 14, NULL, NULL, NULL, NULL, 'system:storage-config:list',
        NULL, NULL, NULL, 0, 0, 0, 0, 0, 0, '', 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init',
        'zhangchuang', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (142, 'exportStorageConfig', '导出配置', '', 'button', 0, 14, NULL, NULL, NULL, NULL,
        'system:storage-config:export', NULL, NULL, NULL, 0, 0, 0, 0, 0, 0, '', 0, '2025-08-07 08:32:15',
        '2025-08-07 08:32:15', 'system_init', 'zhangchuang', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (143, 'refreshStorageConfig', '刷新配置', '', 'button', 0, 14, NULL, NULL, NULL, NULL,
        'system:storage-config:refresh', NULL, NULL, NULL, 0, 0, 0, 0, 0, 0, '', 0, '2025-08-07 08:32:15',
        '2025-08-07 08:32:15', 'system_init', 'zhangchuang', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (144, 'queryStorageFile', '查询文件', '', 'button', 0, 15, NULL, NULL, NULL, NULL, 'system:storage-file:query',
        NULL, NULL, NULL, 0, 0, 0, 0, 0, 0, '', 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init',
        'zhangchuang', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (145, 'listStorageFile', '文件列表', '', 'button', 0, 15, NULL, NULL, NULL, NULL, 'system:storage-file:list',
        NULL, NULL, NULL, 0, 0, 0, 0, 0, 0, '', 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init',
        'zhangchuang', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (146, 'deleteStorageFile', '删除文件', '', 'button', 0, 15, NULL, NULL, NULL, NULL, 'system:storage-file:delete',
        NULL, NULL, NULL, 0, 0, 0, 0, 0, 0, '', 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init',
        'zhangchuang', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (147, 'exportStorageFile', '导出文件', '', 'button', 0, 15, NULL, NULL, NULL, NULL, 'system:storage-file:export',
        NULL, NULL, NULL, 0, 0, 0, 0, 0, 0, '', 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init',
        'zhangchuang', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (148, 'restoreStorageFile', '恢复文件', '', 'button', 0, 15, NULL, NULL, NULL, NULL,
        'system:storage-file:restore', NULL, NULL, NULL, 0, 0, 0, 0, 0, 0, '', 0, '2025-08-07 08:32:15',
        '2025-08-07 08:32:15', 'system_init', 'zhangchuang', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (149, 'queryLoginLog', '查询登录日志', '', 'button', 0, 16, NULL, NULL, NULL, NULL, 'system:log-login:query',
        NULL, NULL, NULL, 0, 0, 0, 0, 0, 0, '', 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init',
        'zhangchuang', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (150, 'deleteLoginLog', '删除登录日志', '', 'button', 0, 16, NULL, NULL, NULL, NULL, 'system:log-login:delete',
        NULL, NULL, NULL, 0, 0, 0, 0, 0, 0, '', 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init',
        'zhangchuang', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (151, 'exportLoginLog', '导出登录日志', '', 'button', 0, 16, NULL, NULL, NULL, NULL, 'system:log-login:export',
        NULL, NULL, NULL, 0, 0, 0, 0, 0, 0, '', 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init',
        'zhangchuang', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (152, 'queryOperationLog', '查询操作日志', '', 'button', 0, 17, NULL, NULL, NULL, NULL,
        'system:log-operation:query', NULL, NULL, NULL, 0, 0, 0, 0, 0, 0, '', 0, '2025-08-07 08:32:15',
        '2025-08-07 08:32:15', 'system_init', 'zhangchuang', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (153, 'deleteOperationLog', '删除操作日志', '', 'button', 0, 17, NULL, NULL, NULL, NULL,
        'system:log-operation:delete', NULL, NULL, NULL, 0, 0, 0, 0, 0, 0, '', 0, '2025-08-07 08:32:15',
        '2025-08-07 08:32:15', 'system_init', 'zhangchuang', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (154, 'exportOperationLog', '导出操作日志', '', 'button', 0, 17, NULL, NULL, NULL, NULL,
        'system-log:operation:export', NULL, NULL, NULL, 0, 0, 0, 0, 0, 0, '', 0, '2025-08-07 08:32:15',
        '2025-08-07 08:32:15', 'system_init', 'zhangchuang', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (156, 'listDevice', '设备列表', '', 'button', 0, 24, NULL, NULL, NULL, NULL, 'system:online-device:list', NULL,
        NULL, NULL, 0, 0, 0, 0, 0, 0, '', 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init', 'zhangchuang',
        NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (157, 'exportDevice', '导出设备', '', 'button', 0, 24, NULL, NULL, NULL, NULL, 'system:online-device:export',
        NULL, NULL, NULL, 0, 0, 0, 0, 0, 0, '', 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init',
        'zhangchuang', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (158, 'deleteDevice', '删除设备', '', 'button', 0, 24, NULL, NULL, NULL, NULL, 'system:online-device:delete',
        NULL, NULL, NULL, 0, 0, 0, 0, 0, 0, '', 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init',
        'zhangchuang', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (159, 'querySession', '查询会话', '', 'button', 0, 25, NULL, NULL, NULL, NULL, 'system:online-session:query',
        NULL, NULL, NULL, 0, 0, 0, 0, 0, 0, '', 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init',
        'zhangchuang', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (160, 'listSession', '会话列表', '', 'button', 0, 25, NULL, NULL, NULL, NULL, 'system:online-session:list', NULL,
        NULL, NULL, 0, 0, 0, 0, 0, 0, '', 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init', 'zhangchuang',
        NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (161, 'exportSession', '导出会话', '', 'button', 0, 25, NULL, NULL, NULL, NULL, 'system:online-session:export',
        NULL, NULL, NULL, 0, 0, 0, 0, 0, 0, '', 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init',
        'zhangchuang', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (162, 'deleteSession', '删除会话', '', 'button', 0, 25, NULL, NULL, NULL, NULL, 'system:online-session:delete',
        NULL, NULL, NULL, 0, 0, 0, 0, 0, 0, '', 0, '2025-08-07 08:32:15', '2025-08-07 08:32:15', 'system_init',
        'zhangchuang', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (163, 'addRole', '添加角色', '', 'button', 0, 5, NULL, NULL, NULL, NULL, 'system:role:add', NULL, NULL, NULL, 0,
        0, 0, 0, 0, 0, NULL, 0, '2025-08-07 08:41:51', '2025-08-07 08:41:51', 'system_init', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (164, 'deleteRole', '删除角色', '', 'button', 0, 5, NULL, NULL, NULL, NULL, 'system:role:delete', NULL, NULL,
        NULL, 0, 0, 0, 0, 0, 0, NULL, 0, '2025-08-07 08:41:51', '2025-08-07 08:41:51', 'system_init', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (165, 'updateRole', '修改角色', '', 'button', 0, 5, NULL, NULL, NULL, NULL, 'system:role:update', NULL, NULL,
        NULL, 0, 0, 0, 0, 0, 0, NULL, 0, '2025-08-07 08:41:51', '2025-08-07 08:41:51', 'system_init', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (166, 'listRole', '角色列表', '', 'button', 0, 5, NULL, NULL, NULL, NULL, 'system:role:list', NULL, NULL, NULL,
        0, 0, 0, 0, 0, 0, NULL, 0, '2025-08-07 08:41:51', '2025-08-07 08:41:51', 'system_init', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (167, 'queryRole', '查询角色', '', 'button', 0, 5, NULL, NULL, NULL, NULL, 'system:role:query', NULL, NULL, NULL,
        0, 0, 0, 0, 0, 0, NULL, 0, '2025-08-07 08:41:51', '2025-08-07 08:41:51', 'system_init', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (168, 'exportRole', '导出角色', '', 'button', 0, 5, NULL, NULL, NULL, NULL, 'system:role:export', NULL, NULL,
        NULL, 0, 0, 0, 0, 0, 0, NULL, 0, '2025-08-07 08:41:51', '2025-08-07 08:41:51', 'system_init', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (169, 'queryJobLog', '查询任务日志', '', 'button', 0, 23, NULL, NULL, NULL, NULL, 'tool:job-log:query', NULL,
        NULL, NULL, 0, 0, 0, 0, 0, 0, '', 0, '2025-08-07 08:41:51', '2025-08-07 08:41:51', 'system_init', 'zhangchuang',
        NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (170, 'listJobLog', '任务日志列表', '', 'button', 0, 23, NULL, NULL, NULL, NULL, 'tool:job-log:list', NULL, NULL,
        NULL, 0, 0, 0, 0, 0, 0, '', 0, '2025-08-07 08:41:51', '2025-08-07 08:41:51', 'system_init', 'zhangchuang',
        NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (171, 'exportJobLog', '导出任务日志', '', 'button', 0, 23, NULL, NULL, NULL, NULL, 'tool:job-log:export', NULL,
        NULL, NULL, 0, 0, 0, 0, 0, 0, '', 0, '2025-08-07 08:41:51', '2025-08-07 08:41:51', 'system_init', 'zhangchuang',
        NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (172, 'addJob', '新增任务', '', 'button', 0, 22, NULL, NULL, NULL, NULL, 'tool:job:add', NULL, NULL, NULL, 0, 0,
        0, 0, 0, 0, '', 0, '2025-08-07 08:41:51', '2025-08-07 08:41:51', 'system_init', 'zhangchuang', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (173, 'updateJob', '修改任务', '', 'button', 0, 22, NULL, NULL, NULL, NULL, 'tool:job:update', NULL, NULL, NULL,
        0, 0, 0, 0, 0, 0, '', 0, '2025-08-07 08:41:51', '2025-08-07 08:41:51', 'system_init', 'zhangchuang', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (174, 'deleteJob', '删除任务', '', 'button', 0, 22, NULL, NULL, NULL, NULL, 'tool:job:delete', NULL, NULL, NULL,
        0, 0, 0, 0, 0, 0, '', 0, '2025-08-07 08:41:51', '2025-08-07 08:41:51', 'system_init', 'zhangchuang', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (175, 'exportJob', '导出任务', '', 'button', 0, 22, NULL, NULL, NULL, NULL, 'tool:job:export', NULL, NULL, NULL,
        0, 0, 0, 0, 0, 0, '', 0, '2025-08-07 08:41:51', '2025-08-07 08:41:51', 'system_init', 'zhangchuang', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (176, 'listJob', '任务列表', '', 'button', 0, 22, NULL, NULL, NULL, NULL, 'tool:job:list', NULL, NULL, NULL, 0,
        0, 0, 0, 0, 0, '', 0, '2025-08-07 08:41:51', '2025-08-07 08:41:51', 'system_init', 'zhangchuang', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (177, 'batchJob', '批量操作', '', 'button', 0, 22, NULL, NULL, NULL, NULL, 'tool:job:batch', NULL, NULL, NULL, 0,
        0, 0, 0, 0, 0, '', 0, '2025-08-07 08:41:51', '2025-08-07 08:41:51', 'system_init', 'zhangchuang', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (178, 'execJob', '执行任务', '', 'button', 0, 22, NULL, NULL, NULL, NULL, 'tool:job:run', NULL, NULL, NULL, 0, 0,
        0, 0, 0, 0, '', 0, '2025-08-07 08:41:51', '2025-08-07 08:41:51', 'system_init', 'zhangchuang', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (179, 'refreshJob', '刷新任务', '', 'button', 0, 22, NULL, NULL, NULL, NULL, 'tool:job:refresh', NULL, NULL,
        NULL, 0, 0, 0, 0, 0, 0, '', 0, '2025-08-07 08:41:51', '2025-08-07 08:41:51', 'system_init', 'zhangchuang',
        NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (181, 'assignPermission', '分配权限', NULL, 'button', 0, 5, NULL, NULL, NULL, NULL, 'system:role:assign', NULL,
        NULL, NULL, 0, 0, 0, 0, 0, 0, NULL, 0, '2025-08-07 08:50:35', '2025-08-07 08:50:35', 'zhangchuang', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (182, 'pauseJob', '暂停任务', '', 'button', 0, 22, NULL, NULL, NULL, NULL, 'tool:job:pause', NULL, NULL, NULL, 0,
        0, 0, 0, 0, 0, '', 0, '2025-08-07 09:32:42', '2025-08-07 09:32:42', 'zhangchuang', 'zhangchuang', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (183, 'resumeJob', '恢复任务', NULL, 'button', 0, 22, NULL, NULL, NULL, NULL, 'tool:job:resume', NULL, NULL,
        NULL, 0, 0, 0, 0, 0, 0, NULL, 0, '2025-08-07 09:37:57', '2025-08-07 09:37:57', 'zhangchuang', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (184, 'startJob', '启动任务', NULL, 'button', 0, 22, NULL, NULL, NULL, NULL, 'tool:job:start', NULL, NULL, NULL,
        0, 0, 0, 0, 0, 0, NULL, 0, '2025-08-07 09:46:24', '2025-08-07 09:46:24', 'zhangchuang', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (185, 'cleanJobLog', '清空任务日志', '', 'button', 0, 23, NULL, NULL, NULL, NULL, 'tool:job-log:clean', NULL,
        NULL, NULL, 0, 0, 0, 0, 0, 0, '', 0, '2025-08-07 09:48:42', '2025-08-07 09:48:42', 'zhangchuang', 'zhangchuang',
        NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (186, 'refreshDict', '刷新字典', '', 'button', 0, 9, NULL, NULL, NULL, NULL, 'system:dict-refresh', NULL, NULL,
        NULL, 0, 0, 0, 0, 0, 0, '', 0, '2025-08-07 09:56:18', '2025-08-07 09:56:18', 'zhangchuang', 'zhangchuang',
        NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (189, 'noticeManage', '公告管理', '/system/notice', 'menu', 0, 1, '', '', 'carbon:notification',
        '/system/notice/index', '', '', '', '', 0, 0, 0, 0, 0, 0, '', 1, '2025-08-08 17:03:58', '2025-08-08 17:03:58',
        'zhangchuang', 'zhangchuang', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (191, 'systemMessage', '系统消息', '/system/message', 'catalog', 0, 1, '', '',
        'fluent:comment-multiple-16-regular', '', '', '', '', '', 0, 0, 0, 0, 0, 0, '', 0, '2025-08-10 09:37:59',
        '2025-08-10 09:37:59', 'zhangchuang', 'zhangchuang', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (192, 'systemMessageManage', '消息管理', '/system/message/manage', 'menu', 0, 191, '', '',
        'fluent:comment-text-28-regular', '/system/message/manage/index', '', '', '', '', 0, 0, 0, 0, 0, 0, '', 0,
        '2025-08-10 09:43:25', '2025-08-10 09:43:25', 'zhangchuang', 'zhangchuang', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (193, 'systemMessageSend', '消息发送', '/system/message/send', 'menu', 0, 191, '', '', 'fluent:send-28-regular',
        '/system/message/send/index', '', '', '', '', 0, 0, 0, 0, 0, 0, '', 0, '2025-08-10 09:45:09',
        '2025-08-10 09:45:09', 'zhangchuang', 'zhangchuang', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (194, 'ToolWebSocket', 'WebSocket 测试', '/tool/websocket', 'menu', 0, 3, '', '', 'lucide:radio',
        '/tool/websocket/index', '', '', '', '', 0, 0, 0, 0, 0, 0, '', 1, '2025-08-11 10:54:31', '2025-08-11 10:54:31',
        'zhangchuang', 'zhangchuang', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (195, 'Endpoints', '端点监控', '/monitor/endpoints', 'menu', 0, 2, '', '', 'carbon:ibm-cloud-vpc-endpoints',
        '/monitor/endpoints/index', '', '', '', '', 0, 0, 0, 0, 0, 0, NULL, 0, '2025-08-12 14:13:45',
        '2025-08-12 14:13:45', 'zhangchuang', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (196, 'MessageSend', '消息发送', NULL, 'button', 0, 193, NULL, NULL, NULL, NULL, 'system:message:send', NULL,
        NULL, NULL, 0, 0, 0, 0, 0, 0, NULL, 0, '2025-08-14 09:17:49', '2025-08-14 09:17:49', 'zhangchuang', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (198, 'MessageList', '消息列表', NULL, 'button', 0, 192, NULL, NULL, NULL, NULL, 'system.message:list', NULL,
        NULL, NULL, 0, 0, 0, 0, 0, 0, NULL, 0, '2025-08-14 09:19:58', '2025-08-14 09:19:58', 'zhangchuang', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (199, 'MessageQuery', '消息查询', NULL, 'button', 0, 192, NULL, NULL, NULL, NULL, 'system.message:query', NULL,
        NULL, NULL, 0, 0, 0, 0, 0, 0, NULL, 0, '2025-08-14 09:20:34', '2025-08-14 09:20:34', 'zhangchuang', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (200, 'MessageUpdate', '消息修改', NULL, 'button', 0, 192, NULL, NULL, NULL, NULL, 'system.message:update', NULL,
        NULL, NULL, 0, 0, 0, 0, 0, 0, NULL, 0, '2025-08-14 09:20:58', '2025-08-14 09:20:58', 'zhangchuang', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (201, 'MessageDelete', '消息删除', NULL, 'button', 0, 192, NULL, NULL, NULL, NULL, 'system.message:delete', NULL,
        NULL, NULL, 0, 0, 0, 0, 0, 0, NULL, 0, '2025-08-14 09:21:25', '2025-08-14 09:21:25', 'zhangchuang', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (202, 'NoticeList', '消息列表', NULL, 'button', 0, 189, NULL, NULL, NULL, NULL, 'system:notice:list', NULL, NULL,
        NULL, 0, 0, 0, 0, 0, 0, NULL, 0, '2025-08-14 09:35:19', '2025-08-14 09:35:19', 'zhangchuang', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (203, 'noticeQuery', '消息查询', NULL, 'button', 0, 189, NULL, NULL, NULL, NULL, 'system:notice:query', NULL,
        NULL, NULL, 0, 0, 0, 0, 0, 0, NULL, 0, '2025-08-14 09:35:47', '2025-08-14 09:35:47', 'zhangchuang', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (204, 'AddNotice', '添加公告', NULL, 'button', 0, 189, NULL, NULL, NULL, NULL, 'system:notice:add', NULL, NULL,
        NULL, 0, 0, 0, 0, 0, 0, NULL, 0, '2025-08-14 09:36:28', '2025-08-14 09:36:28', 'zhangchuang', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (205, 'updateNotice', '修改公告', NULL, 'button', 0, 189, NULL, NULL, NULL, NULL, 'system:notice:update', NULL,
        NULL, NULL, 0, 0, 0, 0, 0, 0, NULL, 0, '2025-08-14 09:36:58', '2025-08-14 09:36:58', 'zhangchuang', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (206, 'DeleteNotice', '删除公告', NULL, 'button', 0, 189, NULL, NULL, NULL, NULL, 'system:notice:delete', NULL,
        NULL, NULL, 0, 0, 0, 0, 0, 0, NULL, 0, '2025-08-14 09:37:59', '2025-08-14 09:37:59', 'zhangchuang', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (207, 'ExportNotice', '导出公告', NULL, 'button', 0, 189, NULL, NULL, NULL, NULL, 'system:notice:export', NULL,
        NULL, NULL, 0, 0, 0, 0, 0, 0, NULL, 0, '2025-08-14 09:38:31', '2025-08-14 09:38:31', 'zhangchuang', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (208, 'EndpointsList', '端点列表', NULL, 'button', 0, 195, NULL, NULL, NULL, NULL, 'monitor:endpoints:list',
        NULL, NULL, NULL, 0, 0, 0, 0, 0, 0, NULL, 0, '2025-08-14 09:40:08', '2025-08-14 09:40:08', 'zhangchuang', NULL,
        NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (209, 'MonitorMetrics', '监控查询', NULL, 'button', 0, 18, NULL, NULL, NULL, NULL, 'monitor:metrics:query', NULL,
        NULL, NULL, 0, 0, 0, 0, 0, 0, NULL, 0, '2025-08-14 09:42:00', '2025-08-14 09:42:00', 'zhangchuang', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (210, 'MessageExport', '消息导出', NULL, 'button', 0, 192, NULL, NULL, NULL, NULL, 'system:message:export', NULL,
        NULL, NULL, 0, 0, 0, 0, 0, 0, NULL, 0, '2025-08-14 09:54:43', '2025-08-14 09:54:43', 'zhangchuang', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (211, 'PersonaCenter', '个人中心', '/personal', 'catalog', 0, 0, '', '', 'fluent:home-empty-28-regular', '', '',
        '', '', '', 0, 0, 0, 0, 0, 0, NULL, 10, '2025-08-16 13:51:01', '2025-08-16 13:51:01', 'admin', 'admin', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (212, 'Profile', '个人资料', '/personal/profile', 'menu', 0, 211, '', '', 'fluent:slide-text-person-24-regular',
        '/personal/profile/index', '', '', '', '', 0, 0, 0, 0, 0, 0, NULL, 0, '2025-08-16 13:53:15',
        '2025-08-16 13:53:15', 'admin', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (213, 'MyMessage', '我的消息', '/personal/message', 'menu', 0, 211, '', '', 'fluent:comment-24-regular',
        '/personal/message/index', '', '', '', '', 0, 0, 0, 0, 0, 0, NULL, 0, '2025-08-16 13:56:37',
        '2025-08-16 13:56:37', 'admin', 'admin', NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (214, 'updateProfile', '修改资料', NULL, 'button', 0, 212, NULL, NULL, NULL, NULL, 'personal:profile:updatee',
        NULL, NULL, NULL, 0, 0, 0, 0, 0, 0, NULL, 0, '2025-08-16 14:08:40', '2025-08-16 14:08:40', 'admin', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (215, 'UpdatePassword', '修改密码', NULL, 'button', 0, 212, NULL, NULL, NULL, NULL, 'personal:profile:password',
        NULL, NULL, NULL, 0, 0, 0, 0, 0, 0, NULL, 0, '2025-08-16 14:09:15', '2025-08-16 14:09:15', 'admin', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (216, 'ResetUserPassword', '重置密码', NULL, 'button', 0, 4, NULL, NULL, NULL, NULL, 'system:user:resetPassword',
        NULL, NULL, NULL, 0, 0, 0, 0, 0, 0, NULL, 0, '2025-08-16 14:42:16', '2025-08-16 14:42:16', 'admin', NULL, NULL);
INSERT INTO `sys_menu` (`id`, `name`, `title`, `path`, `type`, `status`, `parent_id`, `active_path`, `active_icon`,
                        `icon`, `component`, `permission`, `badge_type`, `badge`, `badge_variants`, `keep_alive`,
                        `affix_tab`, `hide_in_menu`, `hide_children_in_menu`, `hide_in_breadcrumb`, `hide_in_tab`,
                        `link`, `sort`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`)
VALUES (217, 'PersonalMessageDetail', '消息详情', '/personal/message/detail', 'menu', 0, 211, '/personal/message',
        'fluent:comment-28-filled', 'fluent:comment-28-filled', '/personal/message/detail', '', '', '', '', 0, 0, 1, 0,
        0, 0, NULL, 0, '2025-08-16 15:40:58', '2025-08-16 15:40:58', 'admin', NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for sys_message
-- ----------------------------
DROP TABLE IF EXISTS `sys_message`;
CREATE TABLE `sys_message`
(
    `id`           bigint                                                         NOT NULL AUTO_INCREMENT COMMENT '消息ID',
    `title`        varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息标题',
    `content`      text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci          NOT NULL COMMENT '消息内容',
    `type`         varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci   NOT NULL COMMENT '消息类型',
    `level`        varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci   NOT NULL COMMENT '消息级别',
    `sender_name`  varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci            DEFAULT NULL COMMENT '发送者姓名',
    `target_type`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci   NOT NULL COMMENT '目标类型',
    `publish_time` datetime                                                                DEFAULT NULL COMMENT '发布时间',
    `is_deleted`   tinyint(1)                                                     NOT NULL DEFAULT '0' COMMENT '是否删除：0-未删除 1-已删除',
    `create_time`  datetime                                                       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  datetime                                                       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`    varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci            DEFAULT NULL COMMENT '创建人',
    `update_by`    varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci            DEFAULT NULL COMMENT '更新人',
    PRIMARY KEY (`id`),
    KEY `idx_type` (`type`),
    KEY `idx_level` (`level`),
    KEY `idx_target_type` (`target_type`),
    KEY `idx_publish_time` (`publish_time`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_is_deleted` (`is_deleted`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 3
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='系统消息表';

-- ----------------------------
-- Records of sys_message
-- ----------------------------
BEGIN;
INSERT INTO `sys_message` (`id`, `title`, `content`, `type`, `level`, `sender_name`, `target_type`, `publish_time`,
                           `is_deleted`, `create_time`, `update_time`, `create_by`, `update_by`)
VALUES (1, '11', '<p>111</p>', 'system', 'normal', 'admin', 'all', '2025-08-16 15:41:13', 0, '2025-08-16 15:41:13',
        '2025-08-16 15:41:13', NULL, NULL);
INSERT INTO `sys_message` (`id`, `title`, `content`, `type`, `level`, `sender_name`, `target_type`, `publish_time`,
                           `is_deleted`, `create_time`, `update_time`, `create_by`, `update_by`)
VALUES (2, '测试发送消息',
        '<div>\n <img src=\"https://minio.zhangchuangla.cn/echopro/resource/2025/08/file/0f19c67892084fc79ab9a133a09b9e73.jpg\" width=\"592\" height=\"auto\">\n</div>\n<p></p>',
        'system', 'normal', 'admin', 'all', '2025-08-16 15:57:55', 0, '2025-08-16 15:57:55', '2025-08-16 15:57:54',
        NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for sys_notice
-- ----------------------------
DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice`
(
    `id`             bigint       NOT NULL AUTO_INCREMENT COMMENT '公告ID',
    `notice_title`   varchar(255) NOT NULL COMMENT '公告标题',
    `notice_content` text         NOT NULL COMMENT '公告内容',
    `notice_type`    varchar(255) NOT NULL COMMENT '公告类型',
    `create_time`    datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`      varchar(64)  DEFAULT NULL COMMENT '创建者',
    `update_by`      varchar(64)  DEFAULT NULL COMMENT '更新者',
    `remark`         varchar(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 2
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='公告表';

-- ----------------------------
-- Records of sys_notice
-- ----------------------------
BEGIN;
INSERT INTO `sys_notice` (`id`, `notice_title`, `notice_content`, `notice_type`, `create_time`, `update_time`,
                          `create_by`, `update_by`, `remark`)
VALUES (1, '测试测试',
        '<p>很显然！这是一个测试公告哈哈哈！</p>\n<video controls=\"true\" width=\"350\">\n <source src=\"https://minio.zhangchuangla.cn/echopro/resource/2025/08/file/cb202e2f4aba484da57754cf0bcf769a.mov\">\n</video>',
        '1', '2025-08-16 22:38:22', '2025-08-16 22:38:22', 'admin', NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for sys_operation_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_operation_log`;
CREATE TABLE `sys_operation_log`
(
    `id`               bigint      NOT NULL AUTO_INCREMENT,
    `user_id`          bigint      NOT NULL COMMENT '用户ID',
    `user_name`        varchar(50) NOT NULL COMMENT '用户名',
    `module`           varchar(50)          DEFAULT NULL COMMENT '操作模块',
    `operation_status` tinyint(1)  NOT NULL DEFAULT '2' COMMENT '操作状态 0成功 1失败 2未知',
    `request_method`   varchar(64)          DEFAULT NULL COMMENT '请求方法',
    `operation_ip`     varchar(64)          DEFAULT NULL COMMENT '操作IP',
    `operation_region` varchar(64)          DEFAULT NULL COMMENT '操作地区',
    `response_result`  text COMMENT '操作结果',
    `operation_type`   varchar(20)          DEFAULT NULL COMMENT '操作类型（CREATE/UPDATE/DELETE）',
    `request_url`      varchar(512)         DEFAULT NULL COMMENT '请求地址',
    `method_name`      varchar(255)         DEFAULT NULL COMMENT '方法名称',
    `request_params`   text COMMENT '请求参数',
    `error_msg`        text COMMENT '错误信息',
    `cost_time`        bigint               DEFAULT NULL COMMENT '耗时（毫秒）',
    `create_time`      datetime(6)          DEFAULT NULL COMMENT '操作时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='操作日志表';

-- ----------------------------
-- Records of sys_operation_log
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for sys_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_post`;
CREATE TABLE `sys_post`
(
    `id`          bigint      NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
    `post_code`   varchar(64) NOT NULL COMMENT '岗位编码',
    `post_name`   varchar(50) NOT NULL COMMENT '岗位名称',
    `sort`        int          DEFAULT '0' COMMENT '排序',
    `status`      tinyint(1)   DEFAULT '0' COMMENT '状态(0-正常,1-停用)',
    `create_time` datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`   varchar(100) DEFAULT NULL COMMENT '创建人',
    `update_by`   varchar(100) DEFAULT NULL COMMENT '更新人',
    `remark`      text COMMENT '备注',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 8
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='岗位表';

-- ----------------------------
-- Records of sys_post
-- ----------------------------
BEGIN;
INSERT INTO `sys_post` (`id`, `post_code`, `post_name`, `sort`, `status`, `create_time`, `update_time`, `create_by`,
                        `update_by`, `remark`)
VALUES (6, 'PM', '产品经理', 0, 0, '2025-08-14 09:55:16', '2025-08-14 09:55:16', NULL, NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`
(
    `id`          bigint      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `role_name`   varchar(50) NOT NULL COMMENT '角色名',
    `role_key`    varchar(50)          DEFAULT NULL COMMENT '角色权限字符串',
    `sort`        int         NOT NULL DEFAULT '0' COMMENT '角色排序',
    `status`      tinyint(1)           DEFAULT '0' COMMENT '状态 0正常 1禁用',
    `create_time` timestamp   NULL     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` timestamp   NULL     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`   varchar(255)         DEFAULT NULL COMMENT '创建人',
    `update_by`   varchar(255)         DEFAULT NULL COMMENT '更新人',
    `remark`      varchar(2048)        DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `role_name` (`role_name`),
    UNIQUE KEY `role_key` (`role_key`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 30
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='角色表';

-- ----------------------------
-- Records of sys_role
-- ----------------------------
BEGIN;
INSERT INTO `sys_role` (`id`, `role_name`, `role_key`, `sort`, `status`, `create_time`, `update_time`, `create_by`,
                        `update_by`, `remark`)
VALUES (1, '超级管理员', 'super_admin', 0, 0, '2025-02-23 21:55:13', '2025-08-16 14:21:32', NULL, NULL,
        '超级管理员拥有系统全部权限');
INSERT INTO `sys_role` (`id`, `role_name`, `role_key`, `sort`, `status`, `create_time`, `update_time`, `create_by`,
                        `update_by`, `remark`)
VALUES (23, '演示角色', 'demo', 0, 0, '2025-08-16 14:17:13', '2025-08-16 14:17:13', 'admin', NULL,
        '演示角色包拥有系统绝大数权限信息');
INSERT INTO `sys_role` (`id`, `role_name`, `role_key`, `sort`, `status`, `create_time`, `update_time`, `create_by`,
                        `update_by`, `remark`)
VALUES (29, '测试用户', 'test_user', 0, 0, '2025-08-16 22:15:09', '2025-08-16 22:15:09', 'admin', NULL,
        '这是一个测试用户');
COMMIT;

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`
(
    `role_id` bigint NOT NULL COMMENT '角色ID',
    `menu_id` bigint NOT NULL COMMENT '菜单ID',
    UNIQUE KEY `uk_roleid_menuid` (`role_id`, `menu_id`) USING BTREE COMMENT '角色菜单唯一索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='角色和菜单关联表';

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
BEGIN;
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 1);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 2);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 3);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 4);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 5);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 6);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 7);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 8);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 9);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 10);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 11);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 12);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 13);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 14);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 15);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 16);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 17);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 18);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 19);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 20);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 21);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 22);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 23);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 24);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 104);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 105);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 110);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 111);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 116);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 117);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 122);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 123);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 128);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 129);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 134);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 135);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 138);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 139);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 143);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 144);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 145);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 149);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 153);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 154);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 157);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 166);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 167);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 169);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 170);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 171);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 172);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 173);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 174);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 175);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 176);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 177);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 178);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 179);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 182);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 183);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 184);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 185);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 186);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 189);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 194);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 195);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 204);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 205);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 206);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 207);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 208);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (22, 209);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 1);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 2);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 3);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 4);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 5);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 6);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 7);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 8);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 9);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 10);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 11);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 12);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 13);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 14);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 15);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 16);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 17);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 18);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 19);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 20);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 21);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 22);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 23);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 24);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 25);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 101);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 102);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 103);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 104);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 105);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 106);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 107);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 108);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 109);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 110);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 111);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 112);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 113);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 114);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 115);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 116);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 117);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 118);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 119);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 120);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 121);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 122);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 123);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 124);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 125);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 126);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 127);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 128);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 129);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 130);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 131);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 132);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 133);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 134);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 135);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 136);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 137);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 138);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 139);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 140);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 141);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 142);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 143);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 144);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 145);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 146);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 147);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 148);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 149);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 150);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 151);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 152);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 153);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 154);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 156);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 157);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 158);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 159);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 160);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 161);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 162);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 163);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 164);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 165);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 166);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 167);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 168);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 169);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 170);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 171);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 172);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 173);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 174);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 175);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 176);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 177);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 178);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 179);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 181);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 182);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 183);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 184);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 185);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 186);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 189);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 191);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 192);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 193);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 194);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 195);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 196);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 198);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 199);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 200);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 201);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 202);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 203);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 204);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 205);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 206);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 207);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 208);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 209);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 210);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 211);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 212);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 213);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 214);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 215);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 216);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (23, 217);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (29, 211);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (29, 212);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (29, 215);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (29, 217);
COMMIT;

-- ----------------------------
-- Table structure for sys_security_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_security_log`;
CREATE TABLE `sys_security_log`
(
    `id`               bigint       NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `user_id`          bigint       NOT NULL COMMENT '用户ID',
    `title`            varchar(255) NOT NULL COMMENT '日志标题',
    `operation_type`   varchar(50)  NOT NULL COMMENT '操作类型',
    `operation_region` varchar(255) NOT NULL COMMENT '操作区域',
    `operation_ip`     varchar(50)  NOT NULL COMMENT '操作IP',
    `operation_time`   datetime     NOT NULL COMMENT '操作时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='安全日志表';

-- ----------------------------
-- Records of sys_security_log
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`
(
    `user_id`     bigint                                                  NOT NULL AUTO_INCREMENT COMMENT '主键',
    `username`    varchar(255) CHARACTER SET ucs2 COLLATE ucs2_general_ci NOT NULL COMMENT '用户名',
    `password`    varchar(255)                                                 DEFAULT NULL COMMENT '密码',
    `nickname`    varchar(255)                                                 DEFAULT NULL COMMENT '昵称',
    `avatar`      varchar(256)                                                 DEFAULT NULL COMMENT '头像',
    `email`       varchar(255)                                                 DEFAULT NULL COMMENT '邮箱',
    `dept_id`     bigint                                                       DEFAULT NULL COMMENT '部门ID',
    `post_id`     bigint                                                       DEFAULT NULL COMMENT '职位ID',
    `phone`       varchar(255)                                                 DEFAULT NULL COMMENT '手机号',
    `gender`      tinyint(1)                                                   DEFAULT '0' COMMENT '性别',
    `region`      varchar(128)                                                 DEFAULT NULL COMMENT '地区',
    `signature`   varchar(256)                                                 DEFAULT NULL COMMENT '个性签名',
    `status`      tinyint                                                      DEFAULT '0' COMMENT '状态 0正常 1禁用',
    `create_time` timestamp                                               NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` timestamp                                               NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`   varchar(255)                                                 DEFAULT NULL COMMENT '创建人',
    `update_by`   varchar(255)                                                 DEFAULT NULL COMMENT '更新人',
    `is_deleted`  tinyint                                                      DEFAULT '0' COMMENT '是否删除',
    `remark`      varchar(255)                                                 DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`user_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 3
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='用户表';

-- ----------------------------
-- Records of sys_user
-- ----------------------------
BEGIN;
INSERT INTO `sys_user` (`user_id`, `username`, `password`, `nickname`, `avatar`, `email`, `dept_id`, `post_id`, `phone`,
                        `gender`, `region`, `signature`, `status`, `create_time`, `update_time`, `create_by`,
                        `update_by`, `is_deleted`, `remark`)
VALUES (1, 'admin', '$2a$10$w99techP3aEIvtujc3i6s.yY0gkLjb.u4RgpiAGhQXHa3T2jRbKiq', 'admin',
        'https://minio.zhangchuangla.cn/echopro/resource/2025/08/image/original/c16438e613b444b2a15e2f1fa8cd7aae.jpg',
        NULL, 1, 6, NULL, 0, '陕西-西安', '唯有热爱可抵岁月漫长', 0, '2025-08-15 12:41:34', '2025-08-16 16:29:08', NULL,
        NULL, 0, NULL);
INSERT INTO `sys_user` (`user_id`, `username`, `password`, `nickname`, `avatar`, `email`, `dept_id`, `post_id`, `phone`,
                        `gender`, `region`, `signature`, `status`, `create_time`, `update_time`, `create_by`,
                        `update_by`, `is_deleted`, `remark`)
VALUES (2, 'demo', '$2a$10$M/wHbN5ENWNUqAj.URs.GOQjmM6I/9pzbZL4zpUm8MQ21AI.5/lgC', '演示用户',
        'https://minio.zhangchuangla.cn/echopro/resource/2025/08/image/original/05bd63fe553544388eeb33f63c7e262f.jpg',
        'admin@qq.com', 1, 6, '18888888888', 0, NULL, NULL, 0, '2025-08-15 17:24:26', '2025-08-16 18:37:28', NULL, NULL,
        0, NULL);
COMMIT;

-- ----------------------------
-- Table structure for sys_user_message
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_message`;
CREATE TABLE `sys_user_message`
(
    `id`           bigint     NOT NULL AUTO_INCREMENT COMMENT '关联ID',
    `message_id`   bigint     NOT NULL COMMENT '消息ID',
    `user_id`      bigint     NOT NULL DEFAULT (-(1)) COMMENT '用户ID',
    `role_id`      bigint              DEFAULT (-(1)) COMMENT '角色ID',
    `dept_id`      bigint              DEFAULT (-(1)) COMMENT '部门ID',
    `is_read`      tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否已读：0-未读 1-已读',
    `read_time`    datetime            DEFAULT NULL COMMENT '阅读时间',
    `is_starred`   tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否收藏：0-未收藏 1-已收藏',
    `starred_time` datetime            DEFAULT NULL COMMENT '收藏时间',
    `is_deleted`   tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除：0-未删除 1-已删除',
    `delete_time`  datetime            DEFAULT NULL COMMENT '删除时间',
    `create_time`  datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_message_user` (`message_id`, `user_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_message_id` (`message_id`),
    KEY `idx_is_read` (`is_read`),
    KEY `idx_is_starred` (`is_starred`),
    KEY `idx_is_deleted` (`is_deleted`),
    KEY `idx_create_time` (`create_time`),
    CONSTRAINT `fk_user_message_message` FOREIGN KEY (`message_id`) REFERENCES `sys_message` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='用户消息关联表';

-- ----------------------------
-- Records of sys_user_message
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`
(
    `id`          bigint    NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`     bigint         DEFAULT NULL COMMENT '用户id',
    `role_id`     bigint         DEFAULT NULL COMMENT '角色id',
    `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`   varchar(255)   DEFAULT NULL COMMENT '创建人',
    `update_by`   varchar(255)   DEFAULT NULL COMMENT '更新人',
    `remark`      varchar(255)   DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `user_id` (`user_id`),
    KEY `role_id` (`role_id`),
    CONSTRAINT `sys_user_role_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`),
    CONSTRAINT `sys_user_role_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1897100379522416693
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='用户角色表';

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
BEGIN;
INSERT INTO `sys_user_role` (`id`, `user_id`, `role_id`, `create_time`, `update_time`, `create_by`, `update_by`,
                             `remark`)
VALUES (1, 1, 1, '2025-04-15 08:08:31', '2025-04-15 08:08:31', NULL, NULL, NULL);
INSERT INTO `sys_user_role` (`id`, `user_id`, `role_id`, `create_time`, `update_time`, `create_by`, `update_by`,
                             `remark`)
VALUES (1897100379522416692, 2, 23, '2025-08-16 18:37:28', '2025-08-16 18:37:28', NULL, NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for user_message_ext
-- ----------------------------
DROP TABLE IF EXISTS `user_message_ext`;
CREATE TABLE `user_message_ext`
(
    `id`              bigint    NOT NULL COMMENT '主键ID',
    `user_id`         bigint    NOT NULL COMMENT '用户ID',
    `message_id`      bigint    NOT NULL COMMENT '消息ID',
    `is_read`         tinyint        DEFAULT '0' COMMENT '0代表未读 1代表已读',
    `first_read_time` datetime       DEFAULT NULL COMMENT '首次阅读时间',
    `last_read_time`  datetime       DEFAULT NULL COMMENT '最近一次阅读时间 ',
    `create_time`     timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='用户-消息扩展表 ';

-- ----------------------------
-- Records of user_message_ext
-- ----------------------------
BEGIN;
INSERT INTO `user_message_ext` (`id`, `user_id`, `message_id`, `is_read`, `first_read_time`, `last_read_time`,
                                `create_time`, `update_time`)
VALUES (1956626335045779457, 1, 1, 1, '2025-08-16 15:57:25', '2025-08-16 15:57:25', '2025-08-16 15:57:25',
        '2025-08-16 15:57:25');
INSERT INTO `user_message_ext` (`id`, `user_id`, `message_id`, `is_read`, `first_read_time`, `last_read_time`,
                                `create_time`, `update_time`)
VALUES (1956626502851493890, 1, 2, 1, '2025-08-16 15:58:08', '2025-08-17 13:17:33', '2025-08-16 15:58:05',
        '2025-08-17 13:17:33');
INSERT INTO `user_message_ext` (`id`, `user_id`, `message_id`, `is_read`, `first_read_time`, `last_read_time`,
                                `create_time`, `update_time`)
VALUES (1956636878873980930, 2, 2, 1, '2025-08-16 16:39:19', '2025-08-16 16:39:19', '2025-08-16 16:39:19',
        '2025-08-16 16:39:19');
INSERT INTO `user_message_ext` (`id`, `user_id`, `message_id`, `is_read`, `first_read_time`, `last_read_time`,
                                `create_time`, `update_time`)
VALUES (1956948910819143682, 2, 1, 1, '2025-08-17 13:19:13', '2025-08-17 13:19:13', '2025-08-17 13:19:13',
        '2025-08-17 13:19:13');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
