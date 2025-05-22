package cn.zhangchuangla.common.core.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * JWT 工具类
 */
@Component
public class JWTUtils {

    /**
     * 密钥字符串（Base64 编码）
     */
    @Value("${security.secret}")
    private String secret;


}
