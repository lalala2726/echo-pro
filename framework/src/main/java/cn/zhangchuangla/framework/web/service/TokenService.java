package cn.zhangchuangla.framework.web.service;

import cn.zhangchuangla.common.config.TokenConfig;
import org.springframework.stereotype.Component;

/**
 * @author Chuang
 * <p>
 * created on 2025/2/19 17:51
 */
@Component
public class TokenService {

    private final TokenConfig tokenConfig;

    public TokenService(TokenConfig tokenConfig) {
        this.tokenConfig = tokenConfig;
    }


}
