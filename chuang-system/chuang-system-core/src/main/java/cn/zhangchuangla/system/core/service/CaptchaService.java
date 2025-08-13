package cn.zhangchuangla.system.core.service;

import cn.zhangchuangla.system.core.model.request.CaptchaRequest;
import cn.zhangchuangla.system.core.model.vo.captcha.CaptchaImageVo;

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
     * 验证图形验证码是否正确
     *
     * @param uid  验证码唯一标识
     * @param code 验证码
     * @return 验证结果
     */
    boolean verifyImageCode(String uid, String code);


    /**
     * 生成图形验证码（按类型 numeric/alpha/alphanumeric）
     * 返回 uuid 与 Base64 图片
     */
    CaptchaImageVo generateImageCaptcha();

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
