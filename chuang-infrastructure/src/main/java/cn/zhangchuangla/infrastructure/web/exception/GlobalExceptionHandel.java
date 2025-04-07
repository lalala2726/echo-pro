package cn.zhangchuangla.infrastructure.web.exception;

import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.*;
import cn.zhangchuangla.common.result.AjaxResult;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Objects;

/**
 * 全局异常处理
 *
 * @author Chuang
 * <p>
 * created on 2025/1/11 10:10
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandel {

    /**
     * 业务异常
     */
    @ExceptionHandler(ServiceException.class)
    public AjaxResult serviceExceptionHandel(ServiceException exception) {
        log.error("业务异常：{}", exception.toString());
        return AjaxResult.error(exception.getMessage());
    }

    /**
     * 请求方法不支持
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public AjaxResult httpRequestMethodNotSupportedExceptionHandel(HttpRequestMethodNotSupportedException exception) {
        log.error("请求方法不支持", exception);
        return AjaxResult.error(ResponseCode.NOT_SUPPORT);
    }

    /**
     * 参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public AjaxResult illegalArgumentExceptionHandel(IllegalArgumentException exception) {
        log.error("请求参数非法: ", exception);
        return AjaxResult.error(ResponseCode.PARAM_ERROR, "请求参数非法!");
    }

    /**
     * 认证异常
     */
    @ExceptionHandler(AccountException.class)
    public AjaxResult accountExceptionExceptionHandel(Exception exception) {
        log.error("认证异常", exception);
        return AjaxResult.error(ResponseCode.AUTHORIZED, exception.getMessage());
    }

    /**
     * 认证失败
     */
    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public AjaxResult InternalAuthenticationServiceExceptionHandel(InternalAuthenticationServiceException exception) {
        log.error("认证失败:", exception);
        return AjaxResult.error(ResponseCode.AUTHORIZED, exception.getMessage());
    }

    /**
     * 账号被锁定
     */
    @ExceptionHandler(LockedException.class)
    public AjaxResult lockedExceptionHandel(LockedException exception) {
        log.error("账号被锁定:", exception);
        return AjaxResult.error(ResponseCode.ACCOUNT_LOCKED, exception.getMessage());
    }

    @ExceptionHandler(ParamException.class)
    public AjaxResult paramExceptionHandel(ParamException exception) {
        log.error("参数异常:", exception);
        return AjaxResult.error(ResponseCode.PARAM_ERROR, exception.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public AjaxResult accessDeniedExceptionHandel(AccessDeniedException exception) {
        log.error("权限不足:{}", exception.getMessage());
        return AjaxResult.error(ResponseCode.ACCESS_DENIED);
    }

    /**
     * 参数校验失败
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public AjaxResult methodArgumentNotValidExceptionHandel(MethodArgumentNotValidException exception) {
        log.error("参数校验失败:", exception);
        return AjaxResult.error(ResponseCode.PARAM_ERROR,
                Objects.requireNonNull(exception.getBindingResult().getFieldError()).getDefaultMessage());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public AjaxResult noResourceFoundExceptionHandel(NoResourceFoundException exception, HttpServletRequest request) {
        log.error("资源不存在：{}", exception.toString());
        String message = String.format("资源不存在: %s", request.getRequestURI());
        return AjaxResult.error(ResponseCode.NOT_FOUND, message);
    }

    /**
     * 配置文件异常
     */
    @ExceptionHandler(ProfileException.class)
    public AjaxResult profileExceptionHandel(ProfileException exception) {
        log.error("配置异常:", exception);
        return AjaxResult.error(ResponseCode.PROFILE_ERROR, exception.getMessage());
    }

    /**
     * 访问过于频繁
     */
    @ExceptionHandler(TooManyRequestException.class)
    public AjaxResult tooManyRequestExceptionHandel(TooManyRequestException exception) {
        log.error("请求过于频繁", exception);
        return AjaxResult.error(ResponseCode.TOO_MANY_REQUESTS, exception.getMessage());
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public AjaxResult exceptionHandel(Exception exception) {
        log.error("系统异常", exception);
        return AjaxResult.error(ResponseCode.SERVER_ERROR, exception.getMessage());
    }


}
