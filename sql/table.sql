create table user
(
    id          bigint auto_increment primary key comment '主键',
    username    varchar(255) not null comment '用户名',
    password    varchar(255) not null comment '密码',
    nick_name   varchar(255) not null comment '昵称',
    email       varchar(255) not null comment '邮箱',
    create_time timestamp default current_timestamp comment '创建时间',
    update_time timestamp default current_timestamp on update current_timestamp comment '更新时间',
    create_by   varchar(255) comment '创建人',
    update_by   varchar(255) comment '更新人',
    is_deleted  tinyint   default 0 comment '是否删除',
    status      tinyint   default 0 comment '状态',
    remark      varchar(255) comment '备注'
) comment '用户表';
