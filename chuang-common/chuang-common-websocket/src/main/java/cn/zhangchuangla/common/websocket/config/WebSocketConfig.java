package cn.zhangchuangla.common.websocket.config;

import cn.zhangchuangla.common.websocket.properties.WebSocketProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.util.CollectionUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * WebSocket 与 STOMP 的通用配置。
 *
 * @author Chuang
 */
@Configuration
@EnableWebSocketMessageBroker
@EnableConfigurationProperties(WebSocketProperties.class)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketProperties webSocketProperties;

    /**
     * 可选的握手拦截器列表。
     *
     * <p>通常由上层框架提供鉴权拦截器，用于在 HTTP 握手阶段校验令牌、注入用户标识。</p>
     */
    private List<HandshakeInterceptor> handshakeInterceptors = new ArrayList<>();

    /**
     * 可选的自定义握手处理器。
     *
     * <p>用于将握手阶段解析出的用户标识注入到 {@link java.security.Principal}，
     * 使后续 {@code convertAndSendToUser} 能够基于该标识进行点对点推送。</p>
     */
    private HandshakeHandler handshakeHandler;

    /**
     * 可选的 STOMP 入站通道拦截器列表。
     *
     * <p>常用于 STOMP CONNECT 帧鉴权、订阅目的地合法性校验、限流等。</p>
     */
    private List<ChannelInterceptor> inboundInterceptors = new ArrayList<>();

    public WebSocketConfig(WebSocketProperties webSocketProperties) {
        this.webSocketProperties = webSocketProperties;
    }

    @Autowired(required = false)
    public void setHandshakeInterceptors(List<HandshakeInterceptor> interceptors) {
        if (!CollectionUtils.isEmpty(interceptors)) {
            this.handshakeInterceptors = interceptors;
        }
    }

    @Autowired(required = false)
    public void setHandshakeHandler(HandshakeHandler handshakeHandler) {
        this.handshakeHandler = handshakeHandler;
    }

    @Autowired(required = false)
    public void setInboundInterceptors(List<ChannelInterceptor> inboundInterceptors) {
        if (!CollectionUtils.isEmpty(inboundInterceptors)) {
            this.inboundInterceptors = inboundInterceptors;
        }
    }

    /**
     * 注册 STOMP 握手端点。
     *
     * <p>浏览器将通过该端点与服务端完成初始握手，从而建立 STOMP 会话。</p>
     * <p>当启用 SockJS 时，会在该端点下暴露一组回退子端点以适配不支持原生 WebSocket 的环境。</p>
     */
    @Override
    public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
        var registration = registry.addEndpoint(webSocketProperties.getEndpoint())
                .setAllowedOriginPatterns(webSocketProperties.getAllowedOrigins().toArray(String[]::new));

        if (!CollectionUtils.isEmpty(handshakeInterceptors)) {
            registration.addInterceptors(handshakeInterceptors.toArray(new HandshakeInterceptor[0]));
        }
        if (Objects.nonNull(handshakeHandler)) {
            registration.setHandshakeHandler(handshakeHandler);
        }
        if (Boolean.TRUE.equals(webSocketProperties.getSockJsEnabled())) {
            registration.withSockJS();
        }
    }

    /**
     * 配置消息代理与目的地前缀。
     *
     * <ul>
     *   <li>启用简单消息代理，服务端主动向订阅者推送消息</li>
     *   <li>应用前缀：客户端发送到服务端的目标以 /app 开头</li>
     *   <li>用户目的地前缀：用于点对点推送，例如 /user/queue/xxx</li>
     * </ul>
     */
    @Override
    public void configureMessageBroker(@NonNull MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }

    /**
     * 注册入站通道拦截器。
     *
     * <p>拦截客户端发往服务端的 STOMP 帧，常见用途包括：</p>
     * <ul>
     *   <li>在 CONNECT 帧中完成鉴权，注入用户上下文</li>
     *   <li>在 SUBSCRIBE 帧中限制目的地的访问范围</li>
     *   <li>统计与限流</li>
     * </ul>
     */
    @Override
    public void configureClientInboundChannel(@NonNull ChannelRegistration registration) {
        if (!CollectionUtils.isEmpty(inboundInterceptors)) {
            inboundInterceptors.forEach(registration::interceptors);
        }
    }
}


