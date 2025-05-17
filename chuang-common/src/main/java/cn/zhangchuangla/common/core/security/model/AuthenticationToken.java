package cn.zhangchuangla.common.core.security.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 认证令牌响应对象
 *
 * @author Ray.Hao
 */
@Schema(name = "认证令牌响应对象", description = "认证令牌响应对象")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationToken implements Serializable {

    @Serial
    private static final long serialVersionUID = -546346567615391164L;

    /**
     * 访问令牌
     */
    @Schema(description = "访问令牌")
    private String accessToken;

    /**
     * 刷新令牌
     */
    @Schema(description = "刷新令牌")
    private String refreshToken;

    /**
     * 过期时间（单位：秒）
     */
    @Schema(description = "过期时间(单位：秒)")
    private Integer expires;

}
