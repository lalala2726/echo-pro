package cn.zhangchuangla.message.mq;

import cn.zhangchuangla.message.model.entity.SysUserMessage;
import cn.zhangchuangla.message.service.SysUserMessageService;
import cn.zhangchuangla.mq.config.RabbitMQConfig;
import cn.zhangchuangla.mq.dto.MessageSendDTO;
import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 消息业务消费者
 *
 * @author Chuang
 * created on 2025/5/24
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MessageBusinessConsumer {

    private final SysUserMessageService sysUserMessageService;

    /**
     * 消费消息队列中的消息，批量插入用户消息记录
     *
     * @param message 消息内容
     */
    @RabbitListener(queues = RabbitMQConfig.MESSAGE_QUEUE)
    public void handleMessageBatch(String message) {
        long startTime = System.currentTimeMillis();
        try {
            log.info("开始处理消息批次: {}", message);
            MessageSendDTO messageSendDTO = JSON.parseObject(message, MessageSendDTO.class);

            // 批量创建用户消息记录
            List<SysUserMessage> userMessages = messageSendDTO.getUserIds().stream()
                    .map(userId -> SysUserMessage.builder()
                            .messageId(messageSendDTO.getMessageId())
                            .userId(userId)
                            .createTime(new Date())
                            .build())
                    .collect(Collectors.toList());

            // 批量插入
            boolean success = sysUserMessageService.saveBatch(userMessages);

            long endTime = System.currentTimeMillis();
            if (success) {
                log.info("消息批次处理成功，消息ID: {}, 用户数量: {}, 耗时: {}ms",
                        messageSendDTO.getMessageId(),
                        messageSendDTO.getUserIds().size(),
                        endTime - startTime);
            } else {
                log.error("消息批次处理失败，消息ID: {}, 用户数量: {}",
                        messageSendDTO.getMessageId(),
                        messageSendDTO.getUserIds().size());
                throw new RuntimeException("批量插入用户消息记录失败");
            }

        } catch (Exception e) {
            log.error("处理消息批次失败: {}", message, e);
            // 这里可以根据需要实现重试机制或者死信队列
            throw e;
        }
    }
}
