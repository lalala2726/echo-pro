package cn.zhangchuangla.common.mq.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ配置类
 *
 * @author Chuang
 * created on 2025-01-20
 */
@Configuration
public class RabbitMQConfig {

    /**
     * 消息发送交换机
     */
    public static final String MESSAGE_EXCHANGE = "message.exchange";

    /**
     * 用户消息批量插入队列
     */
    public static final String USER_MESSAGE_QUEUE = "user.message.queue";

    /**
     * 用户消息批量插入路由键
     */
    public static final String USER_MESSAGE_ROUTING_KEY = "user.message";

    /**
     * 声明交换机
     */
    @Bean
    public DirectExchange messageExchange() {
        return new DirectExchange(MESSAGE_EXCHANGE, true, false);
    }

    /**
     * 声明用户消息队列
     */
    @Bean
    public Queue userMessageQueue() {
        return QueueBuilder.durable(USER_MESSAGE_QUEUE).build();
    }

    /**
     * 绑定用户消息队列到交换机
     */
    @Bean
    public Binding userMessageBinding() {
        return BindingBuilder.bind(userMessageQueue())
                .to(messageExchange())
                .with(USER_MESSAGE_ROUTING_KEY);
    }
}
