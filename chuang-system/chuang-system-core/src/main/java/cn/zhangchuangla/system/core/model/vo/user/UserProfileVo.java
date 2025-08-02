package cn.zhangchuangla.system.core.model.vo.user;

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
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 所属部门
     */
    @Schema(description = "所属部门")
    private String deptName;


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
     * 头像
     */
    @Schema(description = "头像")
    private String avatar;

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
     * 创建时间
     */
    @Schema(name = "创建时间")
    private Date createTime;


}
