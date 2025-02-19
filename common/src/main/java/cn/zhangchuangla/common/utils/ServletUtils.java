package cn.zhangchuangla.common.utils;

import cn.zhangchuangla.common.entity.ClientInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

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

    public static ClientInfo parseClientInfo(HttpServletRequest request) {
        //获取客户端IP地址,数据形式为：127.0.0.1:8080
        String ip = request.getRemoteAddr();
    }


}
