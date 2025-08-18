package cn.zhangchuangla.framework.model.vo;

import cn.zhangchuangla.common.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;

/**
 * 在线用户信息对象
 *
 * @author Chuang
 * <p>
 * created on 2025/2/27 10:31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OnlineLoginUserVo {

    /**
     * 访问令牌ID
     */
    @Schema(description = "会话ID", type = "string", example = "xxxxxxxxx")
    @Excel(name = "会话ID")
    private String accessTokenId;

    /**
     * 刷新令牌ID
     */
    @Schema(description = "刷新令牌ID", type = "string", example = "xxxxxxxxx")
    @Excel(name = "刷新令牌ID")
    private String refreshTokenId;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID", type = "number", example = "1")
    @Excel(name = "用户ID")
    private Long userId;

    /**
     * 用户名
     */
    @Schema(description = "用户名", type = "string", example = "admin")
    @Excel(name = "用户名")
    private String username;

    /**
     * 角色权限集合
     */
    @Schema(description = "角色权限集合", type = "array", example = "[\"admin\",\"user\"]")
    @Excel(name = "角色权限集合")
    private Set<String> roles;

    /**
     * 登录IP地址
     */
    @Schema(description = "登录IP地址", type = "string", example = "127.0.0.1")
    @Excel(name = "登录IP地址")
    private String ip;

    /**
     * 登录地点
     */
    @Schema(description = "登录地点", type = "string", example = "中国")
    @Excel(name = "登录地点")
    private String location;

    /**
     * 访问时间
     */
    @Schema(description = "最近访问时间", type = "string", example = "2025-02-27 10:31:00")
    @Excel(name = "访问时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date accessTime;

    /**
     * userAgent
     */
    @Schema(description = "userAgent", type = "string", example = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.69 Safari/537.36")
    @Excel(name = "用户代理")
    private String userAgent;


}
