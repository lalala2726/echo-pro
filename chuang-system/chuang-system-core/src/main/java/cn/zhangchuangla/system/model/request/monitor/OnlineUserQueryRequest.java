package cn.zhangchuangla.system.model.request.monitor;

import cn.zhangchuangla.common.core.base.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Chuang
 * <p>
 * created on 2025/3/20 13:09
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "在线用户列表查询请求对象", description = "在线用户列表查询请求对象")
public class OnlineUserQueryRequest extends BasePageRequest {

    /**
     * 会话ID
     */
    @Schema(description = "会话ID", example = "abc123xyz", type = "string")
    private String sessionId;


    /**
     * 用户名
     */
    @Schema(description = "用户名", example = "admin", type = "string")
    private String username;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID", example = "1001", type = "integer", format = "int64")
    private Long userId;

    /**
     * 登录IP
     */
    @Schema(description = "登录IP", example = "192.168.1.1", type = "string")
    private String ip;

    /**
     * 登录地点
     */
    @Schema(description = "登录地点", example = "中国-北京", type = "string")
    private String region;

    /**
     * 浏览器
     */
    @Schema(description = "浏览器", example = "Chrome", type = "string")
    private String browser;

    /**
     * 操作系统
     */
    @Schema(description = "操作系统", example = "Windows 11", type = "string")
    private String os;


}
