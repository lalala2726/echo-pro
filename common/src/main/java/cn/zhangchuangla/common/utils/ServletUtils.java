package cn.zhangchuangla.common.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * 客户端工具类
 *
 * @author Chuang
 * <p>
 * created on 2025/2/19 17:24
 */
@Slf4j
public class ServletUtils {


    private static final String CONTENT_TYPE_JSON = "application/json;charset=UTF-8";
    private static final String CHARACTER_ENCODING = "UTF-8";

    /**
     * 将字符串渲染到客户端
     *
     * @param response 渲染对象
     * @param string   待渲染的字符串
     */
    public static void renderString(HttpServletResponse response, String string) {
        try {
            response.setContentType(CONTENT_TYPE_JSON);
            response.setCharacterEncoding(CHARACTER_ENCODING);

            PrintWriter writer = response.getWriter();
            writer.print(string);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            log.error("响应渲染失败", e);
            // 当响应已提交时，重置缓冲区避免部分输出
            if (!response.isCommitted()) {
                response.resetBuffer();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }

    public static HttpServletRequest getRequest() {
        return getRequestAttributes().getRequest();
    }

    public static ServletRequestAttributes getRequestAttributes() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return (ServletRequestAttributes) attributes;
    }

}
