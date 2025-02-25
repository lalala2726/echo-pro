package cn.zhangchuangla.system.model.entity;

import cn.zhangchuangla.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 用户角色关系对应实体类
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_user_role")
@Data
@Schema(name = "用户角色对应关系实体类")
public class SysUserRole extends BaseEntity {


    /**
     * 主键
     */
    @TableId
    @Schema(description = "主键")
    private Long userRoleId;

    /**
     * 用户id
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 角色id
     */
    @Schema(description = "角色ID")
    private Long roleId;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private Date createTime;

}
