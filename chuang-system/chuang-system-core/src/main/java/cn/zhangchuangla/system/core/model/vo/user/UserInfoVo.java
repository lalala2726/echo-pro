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
public class UserInfoVo {

    /**
     * ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 角色ID集合
     */
    @Schema(description = "角色ID集合")
    private Set<Long> roleIds;


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
     * 邮箱
     */
    @Schema(description = "邮箱")
    private String email;


}
