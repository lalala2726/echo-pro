package cn.zhangchuangla.system.core.service;

import cn.zhangchuangla.system.core.model.request.CaptchaRequest;

/**
 * @author Chuang
 * <p>
 * created on 2025/8/4 03:04
 */
public interface CaptchaService {

    /**
     * 发送邮箱验证码
     *
     * @param request 请求参数
     */
    void sendEmail(CaptchaRequest request);

    /**
     * 发送手机号验证码
     *
     * @param request 请求参数
     */
    void sendPhone(CaptchaRequest request);


    /**
     * 验证验证码是否有效
     *
     * @param request 请求参数
     * @return 验证结果
     */
    boolean verify(CaptchaRequest request);

    /**
     * 验证邮箱验证码是否有效
     *
     * @param email 邮箱
     * @return 验证结果
     */
    boolean verifyEmail(String email, String code);

    /**
     * 验证手机号验证码是否有效
     *
     * @param phone 手机号
     * @return 验证结果
     */
    boolean verifyPhone(String phone, String code);
}
