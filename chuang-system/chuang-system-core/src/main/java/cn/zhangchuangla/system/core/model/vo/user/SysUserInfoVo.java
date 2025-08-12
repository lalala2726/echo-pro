package cn.zhangchuangla.system.core.model.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Set;

/**
 * @author Chuang
 * Created on 2025/3/1 18:06
 */
@Data
@Schema(name = "用户信息视图对象", description = "用于展示用户的详细信息")
public class SysUserInfoVo {

    /**
     * 头像
     */
    @Schema(description = "头像", type = "string", example = "https://avatar.zhangchuangla.cn/avatar.png")
    private String avatar;

    /**
     * ID
     */
    @Schema(description = "用户ID", type = "number", example = "1")
    private Long userId;

    /**
     * 角色ID集合
     */
    @Schema(description = "角色ID集合", type = "array", example = "[1,2]")
    private Set<Long> roleIds;

    /**
     * 部门ID
     */
    @Schema(description = "部门ID", type = "number", example = "1")
    private Long deptId;

    /**
     * 岗位ID
     */
    @Schema(description = "岗位ID", type = "number", example = "1")
    private Long postId;


    /**
     * 用户名
     */
    @Schema(description = "用户名", type = "string", example = "admin")
    private String username;

    /**
     * 昵称
     */
    @Schema(description = "昵称", type = "string", example = "管理员")
    private String nickname;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱", type = "string", example = "admin@zhangchuangla.cn")
    private String email;

    /**
     * 手机号码
     */
    @Schema(description = "手机号码", type = "string", example = "13888888888")
    private String phone;


}
