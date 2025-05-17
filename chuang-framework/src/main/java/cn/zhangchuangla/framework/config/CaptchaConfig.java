package cn.zhangchuangla.framework.config;

import cn.zhangchuangla.framework.config.kaptcha.KaptchaTextCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 验证码配置
 */
@Configuration
public class CaptchaConfig {

    /**
     * 创建自定义数学验证码文本生成器
     * 
     * @return 数学验证码文本生成器
     */
    @Bean(name = "captchaTextCreator")
    public KaptchaTextCreator getKaptchaTextCreator() {
        return new KaptchaTextCreator();
    }
    
    /**
     * 数学运算验证码生成器
     *
     * @return 数学运算验证码生成器
     */
    @Bean(name = "captchaProducerMath")
    public KaptchaTextCreator getKaptchaBeanMath() {
        return getKaptchaTextCreator();
    }
}
