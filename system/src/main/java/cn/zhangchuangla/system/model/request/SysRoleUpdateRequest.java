package cn.zhangchuangla.system.model.request;

import lombok.Data;

/**
 * 角色表
 */
@Data
public class SysRoleUpdateRequest {

    /**
     * 主键
     */
    private Long id;

    /**
     * 角色名
     */
    private String name;


}
