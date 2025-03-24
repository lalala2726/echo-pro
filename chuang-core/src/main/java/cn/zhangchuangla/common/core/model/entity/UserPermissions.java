package cn.zhangchuangla.common.core.model.entity;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

/**
 * @author zhangchuang
 * Created on 2025/3/1 16:43
 */
@Data
public class UserPermissions implements Serializable {

    @Serial

    private static final long serialVersionUID = 7249028582415001615L;
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 权限列表
     */
    private Set<String> permissions;
}
