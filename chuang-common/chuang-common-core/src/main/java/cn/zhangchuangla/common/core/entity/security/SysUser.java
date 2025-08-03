package cn.zhangchuangla.common.core.entity.security;

import cn.zhangchuangla.common.core.entity.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * @author Chuang
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_user")
@Data
@Schema(name = "用户实体类")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SysUser extends BaseEntity {

    /**
     * ID
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String username;

    /**
     * 部门ID
     */
    @Schema(description = "部门ID")
    private Long deptId;

    /**
     * 职位ID
     */
    @Schema(description = "职位ID")
    private Long postId;

    /**
     * 密码
     */
    @Schema(description = "密码")
    private String password;

    /**
     * 昵称
     */
    @Schema(description = "昵称")
    private String nickname;

    /**
     * 头像
     */
    @Schema(description = "头像")
    private String avatar;

    /**
     * 地区
     */
    @Schema(description = "地区")
    private String region;

    /**
     * 个性签名
     */
    @Schema(description = "个性签名")
    private String signature;

    /**
     * 性别
     */
    @Schema(description = "性别")
    private Integer gender;

    /**
     * 手机号
     */
    @Schema(description = "手机号")
    private String phone;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    private String email;

    /**
     * 状态
     */
    @Schema(description = "状态")
    private Integer status;

    /**
     * 逻辑删除
     */
    @TableLogic
    @Schema(description = "逻辑删除")
    private String isDeleted;


}
