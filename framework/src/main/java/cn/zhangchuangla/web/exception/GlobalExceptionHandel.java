package cn.zhangchuangla.web.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.ServiceException;
import cn.zhangchuangla.common.result.AjaxResult;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

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
    public AjaxResult serviceExceptionHandel(ServiceException exception){
        log.error("业务异常：{}",exception.toString());
        return AjaxResult.error(exception.getMessage());
    }

    /**
     * 请求方法不支持
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public AjaxResult httpRequestMethodNotSupportedExceptionHandel(HttpRequestMethodNotSupportedException exception){
        log.error("请求方法不支持：{}",exception.toString());
        return AjaxResult.error(ResponseCode.NOT_SUPPORT);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public AjaxResult noResourceFoundExceptionHandel(NoResourceFoundException exception, HttpServletRequest request) {
        log.error("资源不存在：{}", exception.toString());
        String message = String.format("资源不存在: %s", request.getRequestURI());
        return AjaxResult.error(ResponseCode.NOT_FOUND, message);
    }

    /**
     * 参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public AjaxResult illegalArgumentExceptionHandel(IllegalArgumentException exception){
        log.error("参数异常：{}",exception.toString());
        return AjaxResult.error(ResponseCode.PARAM_ERROR,"请求参数非法!");
    }

    @ExceptionHandler(NotLoginException.class)
    public AjaxResult notLoginExceptionHandel(NotLoginException exception){
        log.error("未登录异常：{}",exception.toString());
        return AjaxResult.error(ResponseCode.NOT_LOGIN);
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public AjaxResult exceptionHandel(Exception exception){
        log.error("系统异常：{}",exception.toString());
        return AjaxResult.error(ResponseCode.SERVER_ERROR);
    }



}
