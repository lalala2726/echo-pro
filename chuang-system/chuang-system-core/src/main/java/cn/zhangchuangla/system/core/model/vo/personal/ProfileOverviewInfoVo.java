package cn.zhangchuangla.system.core.model.vo.personal;

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
    @Schema(description = "用户名", type = "string", example = "admin")
    private String username;

    /**
     * 昵称
     */
    @Schema(description = "昵称", type = "string", example = "管理员")
    private String nickname;

    /**
     * 头像URL
     */
    @Schema(description = "头像URL", type = "string", example = "https://avatar.zhangchuangla.cn/avatar.png")
    private String avatar;

    /**
     * 性别
     */
    @Schema(description = "性别", type = "string", example = "0")
    private String gender;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱", type = "string", example = "admin@zhangchuangla.cn")
    private String email;

    /**
     * 手机号
     */
    @Schema(description = "手机号", type = "string", example = "13888888888")
    private String phone;

    /**
     * 地区
     */
    @Schema(description = "地区", type = "string", example = "中国")
    private String region;

    /**
     * 个性签名
     */
    @Schema(description = "个性签名", type = "string", example = "这个人很懒，什么都没有写")
    private String signature;

    /**
     * 部门名称
     */
    @Schema(description = "部门名称", type = "string", example = "系统部")
    private String deptName;

    /**
     * 职位
     */
    @Schema(description = "职位", type = "string", example = "管理员")
    private String postName;

    /**
     * 角色信息
     */
    @Schema(description = "角色信息", type = "array", example = "[管理员,超级管理员]")
    private List<String> roles;


}
