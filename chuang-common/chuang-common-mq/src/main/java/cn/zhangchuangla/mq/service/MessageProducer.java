package cn.zhangchuangla.mq.service;

import cn.zhangchuangla.mq.config.RabbitMQConfig;
import cn.zhangchuangla.mq.dto.MessageSendDTO;
import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 消息生产者服务
 *
 * @author Chuang
 * @date 2025-01-20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageProducer {

    private final RabbitTemplate rabbitTemplate;

    /**
     * 发送消息到队列
     *
     * @param messageSendDTO 消息发送DTO
     */
    public void sendMessage(MessageSendDTO messageSendDTO) {
        try {
            // 分批处理用户列表
            List<Long> userIds = messageSendDTO.getUserIds();
            int batchSize = messageSendDTO.getBatchSize();
            
            for (int i = 0; i < userIds.size(); i += batchSize) {
                int endIndex = Math.min(i + batchSize, userIds.size());
                List<Long> batchUserIds = userIds.subList(i, endIndex);
                
                // 创建批次消息
                MessageSendDTO batchMessage = MessageSendDTO.builder()
                        .messageId(messageSendDTO.getMessageId())
                        .title(messageSendDTO.getTitle())
                        .content(messageSendDTO.getContent())
                        .messageType(messageSendDTO.getMessageType())
                        .userIds(batchUserIds)
                        .batchSize(batchSize)
                        .build();
                
                // 发送到队列
                rabbitTemplate.convertAndSend(
                        RabbitMQConfig.MESSAGE_EXCHANGE,
                        RabbitMQConfig.MESSAGE_ROUTING_KEY,
                        JSON.toJSONString(batchMessage)
                );
                
                log.info("发送消息批次到队列，消息ID: {}, 用户数量: {}", 
                        messageSendDTO.getMessageId(), batchUserIds.size());
            }
        } catch (Exception e) {
            log.error("发送消息到队列失败，消息ID: {}", messageSendDTO.getMessageId(), e);
            throw new RuntimeException("发送消息到队列失败", e);
        }
    }
} 