package cn.zhangchuangla.common.core.entity.security;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author Chuang
 * <p>
 * created on 2025/5/17 21:24
 */
@Schema(name = "刷新令牌请求对象", description = "刷新令牌请求对象")
@Data
public class RefreshTokenRequest {

    @Schema(description = "刷新令牌")
    @NotBlank(message = "刷新令牌不能为空")
    private String refreshToken;
}
