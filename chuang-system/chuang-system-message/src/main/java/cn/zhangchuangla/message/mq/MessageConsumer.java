package cn.zhangchuangla.message.mq;

import cn.zhangchuangla.message.service.SysMessageService;
import cn.zhangchuangla.mq.config.RabbitMqConfig;
import cn.zhangchuangla.mq.dto.MessageSendDto;
import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 消息队列消费者
 *
 * @author zhangchuang
 * @since 2024-01-01
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MessageConsumer {

    private final SysMessageService sysMessageService;

    /**
     * 处理消息发送队列
     *
     * @param message 消息内容
     */
    @RabbitListener(queues = RabbitMqConfig.MESSAGE_SEND_QUEUE)
    public void handleMessageSend(String message) {
        try {
            log.info("接收到消息发送队列消息: {}", message);
            
            MessageSendDto messageSendDto = JSON.parseObject(message, MessageSendDto.class);
            
            // 处理消息发送
            sysMessageService.sendMessageImmediately(messageSendDto.getMessageId());
            
            log.info("消息处理完成，消息ID: {}", messageSendDto.getMessageId());
        } catch (Exception e) {
            log.error("处理消息发送队列失败: {}, 错误信息: {}", message, e.getMessage(), e);
            throw e; // 重新抛出异常，触发重试机制
        }
    }

    /**
     * 处理死信队列
     *
     * @param message 消息内容
     */
    @RabbitListener(queues = RabbitMqConfig.MESSAGE_DLX_QUEUE)
    public void handleDeadLetter(String message) {
        log.error("消息进入死信队列，需要人工处理: {}", message);
        // 这里可以发送告警通知或者记录到数据库
    }
} 