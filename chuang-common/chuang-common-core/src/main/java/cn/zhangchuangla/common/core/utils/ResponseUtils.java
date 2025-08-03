package cn.zhangchuangla.common.core.utils;

import cn.zhangchuangla.common.core.entity.base.AjaxResult;
import cn.zhangchuangla.common.core.enums.ResultCode;
import com.alibaba.fastjson.JSON;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * 响应工具类
 *
 * @author Ray.Hao
 */
@Slf4j
public class ResponseUtils {


    /**
     * 异常消息返回(适用过滤器中处理异常响应)
     *
     * @param response   HttpServletResponse
     * @param resultCode 响应结果码
     */
    public static void writeErrMsg(HttpServletResponse response, ResultCode resultCode, HttpStatus httpStatus) {
        response.setStatus(httpStatus.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        try (PrintWriter writer = response.getWriter()) {
            String jsonResponse = JSON.toJSONString(AjaxResult.error(resultCode));
            writer.print(jsonResponse);
            writer.flush(); // 确保将响应内容写入到输出流
        } catch (IOException e) {
            log.error("响应异常处理失败", e);
        }
    }

    public static void writeErrMsg(HttpServletResponse response, HttpStatus httpStatus, String message) {
        response.setStatus(httpStatus.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        try (PrintWriter writer = response.getWriter()) {
            String jsonResponse = JSON.toJSONString(AjaxResult.error(message));
            writer.print(jsonResponse);
            writer.flush(); // 确保将响应内容写入到输出流
        } catch (IOException e) {
            log.error("响应异常处理失败", e);
        }
    }

    /**
     * 异常消息返回(适用过滤器中处理异常响应)
     *
     * @param response   HttpServletResponse
     * @param resultCode 响应结果码
     * @param message    自定义错误消息
     */
    public static void writeErrMsg(HttpServletResponse response, ResultCode resultCode, HttpStatus httpStatus, String message) {
        response.setStatus(httpStatus.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        try (PrintWriter writer = response.getWriter()) {
            String jsonResponse = JSON.toJSONString(AjaxResult.error(resultCode, message));
            writer.print(jsonResponse);
            writer.flush(); // 确保将响应内容写入到输出流
        } catch (IOException e) {
            log.error("响应异常处理失败", e);
        }
    }


}
