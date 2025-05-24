package cn.zhangchuangla.mq.config;

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
     * 消息发送队列
     */
    public static final String MESSAGE_QUEUE = "message.send.queue";

    /**
     * 消息发送路由键
     */
    public static final String MESSAGE_ROUTING_KEY = "message.send";

    /**
     * 声明交换机
     */
    @Bean
    public DirectExchange messageExchange() {
        return new DirectExchange(MESSAGE_EXCHANGE, true, false);
    }

    /**
     * 声明队列
     */
    @Bean
    public Queue messageQueue() {
        return QueueBuilder.durable(MESSAGE_QUEUE).build();
    }

    /**
     * 绑定队列到交换机
     */
    @Bean
    public Binding messageBinding() {
        return BindingBuilder.bind(messageQueue())
                .to(messageExchange())
                .with(MESSAGE_ROUTING_KEY);
    }
}
