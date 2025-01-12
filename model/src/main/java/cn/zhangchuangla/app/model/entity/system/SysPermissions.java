package cn.zhangchuangla.app.model.entity.system;

import cn.zhangchuangla.app.model.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 权限表
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="sys_permissions")
@Data
public class SysPermissions extends BaseEntity {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 权限名称
     */
    private String name;


}
