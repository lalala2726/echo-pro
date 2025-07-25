package cn.zhangchuangla.framework.security.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Chuang
 * <p>
 * created on 2025/7/25 16:08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginSessionDTO {


    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 刷新令牌会话ID
     */
    private String refreshTokenSessionId;

    /**
     * 访问令牌会话ID
     */
    private String accessTokenSessionId;

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 刷新令牌
     */
    private String refreshToken;


}
