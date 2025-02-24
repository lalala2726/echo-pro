package cn.zhangchuangla.system.model.entity;

import cn.zhangchuangla.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 用户角色表
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_user_role")
@Data
public class SysUserRole extends BaseEntity {


    /**
     * 主键
     */
    @TableId
    private Long userRoleId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 角色id
     */
    private Long roleId;

    /**
     * 创建时间
     */
    private Date createTime;

}
