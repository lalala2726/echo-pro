SELECT permissions.permission_id,
       permissions.permissions_name,
       permissions.permissions_key,
       permissions.create_time,
       permissions.update_time,
       permissions.create_by,
       permissions.update_by,
       permissions.remark
FROM sys_permissions permissions
         JOIN sys_role_permissions role_permissions
              ON permissions.permission_id = role_permissions.permission_id
         JOIN sys_role
              ON role_permissions.role_id = sys_role.role_id
WHERE sys_role.role_key = 'admin';

update sys_user set is_deleted = 0 where 1=1;
