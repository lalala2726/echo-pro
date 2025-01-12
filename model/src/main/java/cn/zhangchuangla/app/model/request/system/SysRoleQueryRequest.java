package cn.zhangchuangla.app.model.request.system;

import cn.zhangchuangla.app.model.base.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色表
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysRoleQueryRequest extends BasePageRequest {

    /**
     * 角色名
     */
    private String name;

}
