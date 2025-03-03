package cn.zhangchuangla.system.model.vo.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author zhangchuang
 * Created on 2025/3/1 18:06
 */
@Data
@Schema(name = "用户信息视图对象")
public class UserInfoVo {

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
     * 昵称
     */
    @Schema(description = "昵称")
    private String nickName;

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
     * 状态
     */
    @Schema(description = "状态")
    private Integer status;


}
