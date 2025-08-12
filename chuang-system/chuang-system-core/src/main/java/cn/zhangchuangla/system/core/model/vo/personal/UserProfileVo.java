package cn.zhangchuangla.system.core.model.vo.personal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author Chuang
 * Created on 2025/3/1 18:06
 */
@Data
@Schema(name = "用户简介视图对象", description = "用于展示用户简介的视图对象")
public class UserProfileVo {

    /**
     * ID
     */
    @Schema(description = "用户ID", type = "number", example = "1")
    private Long userId;

    /**
     * 所属部门
     */
    @Schema(description = "所属部门", type = "string", example = "开发部")
    private String deptName;


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
     * 头像
     */
    @Schema(description = "头像", type = "string", example = "https://avatar.zhangchuangla.cn/avatar.png")
    private String avatar;

    /**
     * 性别
     */
    @Schema(description = "性别", type = "number", example = "1")
    private Integer gender;

    /**
     * 手机号
     */
    @Schema(description = "手机号", type = "string", example = "13888888888")
    private String phone;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱", type = "string", example = "admin@zhangchuangla.cn")
    private String email;


    /**
     * 创建时间
     */
    @Schema(name = "创建时间", type = "string", example = "2021-01-01 00:00:00")
    private Date createTime;


}
