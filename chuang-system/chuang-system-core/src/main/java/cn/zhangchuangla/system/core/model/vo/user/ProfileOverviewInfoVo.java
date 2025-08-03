package cn.zhangchuangla.system.core.model.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 用户概述信息
 *
 * @author Chuang
 * <p>
 * created on 2025/8/3 00:50
 */
@Data
@Schema(name = "用户概述信息", description = "用户概览信息")
public class ProfileOverviewInfoVo {

    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String username;

    /**
     * 昵称
     */
    @Schema(description = "昵称")
    private String nickname;

    /**
     * 头像URL
     */
    @Schema(description = "头像URL")
    private String avatar;

    /**
     * 性别
     */
    @Schema(description = "性别")
    private String gender;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    private String email;

    /**
     * 手机号
     */
    @Schema(description = "手机号")
    private String phone;

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
     * 部门名称
     */
    @Schema(description = "部门名称")
    private String deptName;

    /**
     * 职位
     */
    @Schema(description = "职位")
    private String post;

    /**
     * 角色信息
     */
    @Schema(description = "角色信息")
    private List<String> roles;


}
