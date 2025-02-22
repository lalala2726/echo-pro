# 用户表
create table sys_user
(
    user_id     bigint auto_increment primary key comment '主键',
    username    varchar(255) not null comment '用户名',
    password    varchar(255) comment '密码',
    nick_name   varchar(255) comment '昵称',
    email       varchar(255) comment '邮箱',
    phone       varchar(255) comment '手机号',
    gender      tinyint   default 0 comment '性别',
    avatar      varchar(255) comment '头像',
    status      tinyint   default 0 comment '状态 0正常 1禁用',
    create_time timestamp default current_timestamp comment '创建时间',
    update_time timestamp default current_timestamp on update current_timestamp comment '更新时间',
    create_by   varchar(255) comment '创建人',
    update_by   varchar(255) comment '更新人',
    is_deleted  tinyint   default 0 comment '是否删除',
    remark      varchar(255) comment '备注'
) comment '用户表';

# 角色表
CREATE TABLE sys_role
(
    role_id     BIGINT AUTO_INCREMENT PRIMARY KEY comment '主键',
    role_name   VARCHAR(50) NOT NULL UNIQUE comment '角色名',
    role_key    VARCHAR(50) NOT NULL UNIQUE comment '角色权限字符串',
    role_sort   INT         NOT NULL comment '角色排序',
    status      TINYINT   DEFAULT 0 comment '状态 0正常 1禁用',
    create_time timestamp default current_timestamp comment '创建时间',
    update_time timestamp default current_timestamp on update current_timestamp comment '更新时间',
    create_by   varchar(255) comment '创建人',
    update_by   varchar(255) comment '更新人',
    remark      varchar(255) comment '备注'
) comment '角色表';

# 权限表
CREATE TABLE sys_permissions
(
    permission_id    BIGINT AUTO_INCREMENT PRIMARY KEY comment '主键',  -- 权限ID
    permissions_name VARCHAR(100) NOT NULL UNIQUE comment '权限名',     -- 权限名
    permissions_key  VARCHAR(100) NOT NULL UNIQUE comment '权限字符串', -- 权限字符串
    create_time      timestamp default current_timestamp comment '创建时间',
    update_time      timestamp default current_timestamp on update current_timestamp comment '更新时间',
    create_by        varchar(255) comment '创建人',
    update_by        varchar(255) comment '更新人',
    remark           varchar(255) comment '备注'
) comment '权限表';

# 用户角色表
create table sys_user_role
(
    user_id     bigint comment '用户id',
    role_id     bigint comment '角色id',
    create_time timestamp default current_timestamp comment '创建时间',
    update_time timestamp default current_timestamp on update current_timestamp comment '更新时间',
    create_by   varchar(255) comment '创建人',
    update_by   varchar(255) comment '更新人',
    remark      varchar(255) comment '备注',
    primary key (user_id, role_id),
    foreign key (user_id) references sys_user (user_id),
    foreign key (role_id) references sys_role (role_id)
) comment '用户角色表';

# 角色权限表
create table sys_role_permissions
(
    role_id       bigint comment '角色id',
    permission_id bigint comment '权限id',
    create_time   timestamp default current_timestamp comment '创建时间',
    update_time   timestamp default current_timestamp on update current_timestamp comment '更新时间',
    create_by     varchar(255) comment '创建人',
    update_by     varchar(255) comment '更新人',
    remark        varchar(255) comment '备注',
    primary key (role_id, permission_id),
    foreign key (role_id) references sys_role (role_id),
    foreign key (permission_id) references sys_permissions (permission_id)
) comment '角色权限表';


CREATE TABLE sys_operation_log
(
    log_id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id        BIGINT NOT NULL COMMENT '操作用户',
    module         VARCHAR(50) COMMENT '操作模块',
    operation_type VARCHAR(20) COMMENT '操作类型（CREATE/UPDATE/DELETE）',
    request_url    VARCHAR(512) COMMENT '请求地址',
    method_name    VARCHAR(255) COMMENT '方法名称',
    params         TEXT COMMENT '请求参数',
    result_code    INT COMMENT '响应状态码',
    error_msg      TEXT COMMENT '错误信息',
    cost_time      BIGINT COMMENT '耗时（毫秒）',
    create_time    DATETIME(6) COMMENT '操作时间'
) COMMENT '操作日志表';