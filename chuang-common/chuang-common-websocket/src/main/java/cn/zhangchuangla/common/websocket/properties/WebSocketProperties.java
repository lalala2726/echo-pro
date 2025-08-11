package cn.zhangchuangla.common.websocket.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * WebSocket 配置属性。
 *
 * <p>通过配置文件进行端点与跨域的灵活配置。</p>
 *
 * @author Chuang
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "app.websocket")
public class WebSocketProperties {

    /**
     * STOMP 握手端点。
     * 示例：/ws
     */
    private String endpoint = "/ws";

    /**
     * 允许的跨域来源模式。
     */
    private List<String> allowedOrigins = new ArrayList<>(List.of("*"));

    /**
     * 是否启用 SockJS 回退。
     */
    private Boolean sockJsEnabled = Boolean.TRUE;

}


