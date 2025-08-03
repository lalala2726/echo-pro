package cn.zhangchuangla.api.controller.common;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.entity.base.AjaxResult;
import cn.zhangchuangla.common.core.utils.Assert;
import cn.zhangchuangla.framework.annotation.AccessLimit;
import cn.zhangchuangla.system.core.model.request.CaptchaRequest;
import cn.zhangchuangla.system.core.service.CaptchaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 验证码相关接口
 * 提供获取验证码的功能
 *
 * @author Chuang
 * created on 2025/4/2 14:39
 */
@RestController
@RequestMapping("/captcha")
@Tag(name = "验证码接口", description = "提供获取验证码的功能")
@RequiredArgsConstructor
public class CaptchaController extends BaseController {

    private final CaptchaService captchaService;


    /**
     * 发送邮件验证码
     *
     * @param request 验证码请求参数
     * @return 验证码发送结果
     */
    @PostMapping("/email")
    @Operation(summary = "发送验证码")
    public AjaxResult<Void> sendEmail(@RequestBody CaptchaRequest request) {
        Assert.isNull(request.getEmail(), "邮箱不能为空");
        captchaService.sendEmail(request);
        return success();
    }


    /**
     * 发送手机验证码
     *
     * @param request 发送手机号验证码请求参数
     * @return 验证唯一标识
     */
    @PostMapping("/phone")
    @AccessLimit(message = "验证码发送此处太多了!请稍后再试!")
    @Operation(summary = "发送手机验证码")
    public AjaxResult<Void> sendPhone(@RequestBody CaptchaRequest request) {
        Assert.isNull(request.getPhone(), "手机号不能为空");
        captchaService.sendPhone(request);
        return success();
    }

    /**
     * 验证手机号验证码
     *
     * @param request 验证码请求参数
     * @return 验证结果
     */
    @PostMapping("/verify/phone")
    @AccessLimit(message = "验证码发送此处太多了!请稍后再试!")
    @Operation(summary = "验证手机号验证码")
    public AjaxResult<Void> verifyPhone(@RequestBody CaptchaRequest request) {
        Assert.isNull(request.getCode(), "验证码不能为空");
        Assert.isNull(request.getPhone(), "手机号不能为空");
        boolean verify = captchaService.verifyPhone(request.getPhone(), request.getCode());
        return toAjax(verify);
    }

    /**
     * 验证邮箱验证码
     *
     * @param request 验证码请求参数
     * @return 验证结果
     */
    @PostMapping("/verify/email")
    @Operation(summary = "验证邮箱验证码")
    public AjaxResult<Void> verifyEmail(@RequestBody CaptchaRequest request) {
        Assert.isNull(request.getCode(), "验证码不能为空");
        Assert.isNull(request.getEmail(), "邮箱不能为空");
        boolean verify = captchaService.verifyEmail(request.getEmail(), request.getCode());
        return toAjax(verify);
    }

    /**
     * 验证验证码
     *
     * @param request 验证码请求参数
     * @return 验证结果
     */
    @PostMapping("/verify")
    @Operation(summary = "验证验证码")
    public AjaxResult<Void> verify(@RequestBody CaptchaRequest request) {
        Assert.isNull(request.getCode(), "验证码不能为空");
        Assert.isNull(request.getUid(), "验证码唯一标识不能为空");
        boolean verify = captchaService.verify(request);
        return toAjax(verify);
    }

}
