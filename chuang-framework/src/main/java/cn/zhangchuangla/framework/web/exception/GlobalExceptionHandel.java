package cn.zhangchuangla.framework.web.exception;

import cn.zhangchuangla.common.core.enums.ResultCode;
import cn.zhangchuangla.common.core.exception.*;
import cn.zhangchuangla.common.core.result.AjaxResult;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
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
@RequiredArgsConstructor
public class GlobalExceptionHandel {


    /**
     * 处理业务逻辑中抛出的自定义业务异常。
     * ServiceException 通常表示在业务流程中检测到的错误，例如非法操作或不符合预期的状态。
     *
     * @param exception 包含错误信息和状态码的异常对象
     * @return 返回包含错误信息和状态码的 AjaxResult 对象
     */
    @ExceptionHandler(ServiceException.class)
    @ResponseStatus(HttpStatus.OK)
    public AjaxResult<Void> serviceExceptionHandel(ServiceException exception) {
        log.error("业务异常", exception);
        return AjaxResult.error(exception.getMessage(), exception.getCode());
    }

    /**
     * 处理请求方法不被支持的情况。
     * HttpRequestMethodNotSupportedException 表示客户端使用了服务器端不支持的 HTTP 方法（如 GET、POST 等）。
     *
     * @param exception 包含错误信息的异常对象
     * @return 返回表示“请求方法不支持”的 AjaxResult 对象
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public AjaxResult<Void> httpRequestMethodNotSupportedExceptionHandel(HttpRequestMethodNotSupportedException exception) {
        log.error("请求方法不支持", exception);
        return AjaxResult.error(ResultCode.NOT_SUPPORT);
    }

    /**
     * 处理请求参数无法解析的情况。
     * HttpMessageNotReadableException 表示 Spring MVC 在反序列化请求体时遇到问题，比如 JSON 格式错误。
     *
     * @param exception 包含错误信息的异常对象
     * @return 返回表示“请求参数非法”的 AjaxResult 对象
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public AjaxResult<Void> httpMessageNotReadableExceptionHandel(HttpMessageNotReadableException exception) {
        log.error("请求参数非法: ", exception);
        return AjaxResult.error(ResultCode.PARAM_ERROR, "请求参数非法!");
    }

    /**
     * 处理请求参数类型不匹配的情况。
     * MethodArgumentTypeMismatchException 表示控制器方法期望的参数类型与实际传入的值不兼容，例如期望整数但收到字符串。
     *
     * @param exception 包含参数名、期望类型及实际值的异常对象
     * @return 返回详细描述类型转换失败原因的 AjaxResult 对象
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public AjaxResult<Void> methodArgumentTypeMismatchExceptionHandel(MethodArgumentTypeMismatchException exception) {
        log.error("请求参数类型不匹配", exception);
        String paramName = exception.getName();
        String errorMessage = null;
        if (exception.getRequiredType() != null) {
            errorMessage = String.format("参数 '%s': 类型转换失败 - 期望类型 %s, 实际值: '%s'",
                    paramName,
                    exception.getRequiredType().getSimpleName(),
                    exception.getValue());
        }
        return AjaxResult.error(ResultCode.PARAM_ERROR, errorMessage);
    }

    /**
     * 处理非法参数导致的 IllegalArgumentException。
     * IllegalArgumentException 表示传递给方法的参数非法或格式不正确。
     *
     * @param exception 包含错误信息的异常对象
     * @return 返回表示“请求参数非法”的 AjaxResult 对象
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public AjaxResult<Void> illegalArgumentExceptionHandel(IllegalArgumentException exception) {
        log.error("请求参数非法: ", exception);
        return AjaxResult.error(ResultCode.PARAM_ERROR, "请求参数非法!");
    }


    /**
     * 处理登录相关的自定义异常。
     * AuthorizationException 表示在身份验证过程中发生错误，如无效凭证或登录失败。
     *
     * @param exception 包含错误信息的异常对象
     * @return 返回表示“登录失败”的 AjaxResult 对象
     */
    @ExceptionHandler(AuthorizationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public AjaxResult<Void> loginExceptionHandel(AuthorizationException exception) {
        log.error("登录异常:", exception);
        return AjaxResult.error(exception.getCode(), exception.getMessage());
    }

    /**
     * 处理参数校验失败的情况。
     * ParamException 是一个通用的参数校验异常类，用于表示参数不满足业务需求。
     *
     * @param exception 包含错误信息的异常对象
     * @return 返回表示“参数异常”的 AjaxResult 对象
     */
    @ExceptionHandler(ParamException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public AjaxResult<Void> paramExceptionHandel(ParamException exception) {
        log.error("参数异常:", exception);
        return AjaxResult.error(ResultCode.PARAM_ERROR, exception.getMessage());
    }


    /**
     * 处理控制器方法参数校验失败的情况。
     * MethodArgumentNotValidException 表示通过 JSR-380 注解（如 @Valid）校验失败。
     *
     * @param exception 包含字段错误信息的异常对象
     * @return 返回具体的字段校验错误信息的 AjaxResult 对象
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public AjaxResult<Void> methodArgumentNotValidExceptionHandel(MethodArgumentNotValidException exception) {
        log.error("参数校验失败:", exception);
        return AjaxResult.error(ResultCode.PARAM_ERROR,
                Objects.requireNonNull(exception.getBindingResult().getFieldError()).getDefaultMessage());
    }

    /**
     * 处理找不到请求的资源的情况。
     * NoResourceFoundException 表示客户端请求了一个不存在的 URL 资源。
     *
     * @param exception 包含错误信息的异常对象
     * @param request   当前的 HttpServletRequest 对象，用于获取请求 URI
     * @return 返回表示“资源不存在”的 AjaxResult 对象，并附带请求的 URI
     */
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public AjaxResult<Void> noResourceFoundExceptionHandel(NoResourceFoundException exception, HttpServletRequest request) {
        log.error("资源不存在：{}", exception.toString());
        String message = String.format("资源不存在: %s", request.getRequestURI());
        return AjaxResult.error(ResultCode.NOT_FOUND, message);
    }

    /**
     * 处理配置文件加载失败的情况。
     * ProfileException 表示在读取或解析配置文件时出现问题。
     *
     * @param exception 包含错误信息的异常对象
     * @return 返回表示“配置异常”的 AjaxResult 对象
     */

    @ExceptionHandler(ProfileException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public AjaxResult<Void> profileExceptionHandel(ProfileException exception) {
        log.error("配置异常:", exception);
        return AjaxResult.error(ResultCode.PROFILE_ERROR, exception.getMessage());
    }

    /**
     * 处理请求过于频繁的情况。
     * TooManyRequestException 表示客户端在短时间内发送了太多请求，触发了速率限制机制。
     *
     * @param exception 包含错误信息的异常对象
     * @return 返回表示“请求过于频繁”的 AjaxResult 对象
     */
    @ExceptionHandler(TooManyRequestException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public AjaxResult<Void> tooManyRequestExceptionHandel(TooManyRequestException exception) {
        log.error("请求过于频繁", exception);
        return AjaxResult.error(ResultCode.TOO_MANY_REQUESTS, exception.getMessage());
    }

    /**
     * 处理缺少必需的请求参数的情况。
     * MissingServletRequestParameterException 表示控制器方法需要某个请求参数，但该参数未提供。
     *
     * @param exception 包含缺失参数名称的异常对象
     * @return 返回表示“缺少请求参数”的 AjaxResult 对象，并指出具体缺失的参数名
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public AjaxResult<Void> missingServletRequestParameterExceptionHandel(MissingServletRequestParameterException exception) {
        log.error("缺少请求参数", exception);
        String message = exception.getMessage();
        String paramName = "";
        if (message.contains("'")) {
            int start = message.indexOf("'") + 1;
            int end = message.indexOf("'", start);
            if (end > start) {
                paramName = message.substring(start, end);
            }
        }
        return AjaxResult.error(ResultCode.PARAM_ERROR, "缺少请求参数: " + paramName);
    }

    /**
     * 处理所有未明确捕获的系统异常。
     * Exception 是通用的捕获所有异常的兜底处理器，用于防止未预料的错误导致服务崩溃。
     *
     * @param exception 包含错误信息的异常对象
     * @return 返回表示“系统异常”的 AjaxResult 对象
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public AjaxResult<Void> exceptionHandel(Exception exception) {
        log.error("系统异常", exception);
        return AjaxResult.error(ResultCode.SERVER_ERROR, exception.getMessage());
    }


}
