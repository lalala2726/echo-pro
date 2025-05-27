package cn.zhangchuangla.mq.production;

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
 * created on 2025/5/25
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageProducer {

    private final RabbitTemplate rabbitTemplate;

    /**
     * 发送用户消息到队列（仅用于指定用户发送）
     *
     * @param messageSendDTO 消息发送DTO
     */
    public void sendUserMessage(MessageSendDTO messageSendDTO) {
        try {
            // 分批处理用户列表
            List<Long> userIds = messageSendDTO.getUserIds();
            if (userIds == null || userIds.isEmpty()) {
                log.warn("用户ID列表为空，跳过发送");
                return;
            }

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
                        .sendMethod(messageSendDTO.getSendMethod())
                        .userIds(batchUserIds)
                        .batchSize(batchSize)
                        .build();

                // 发送到队列
                rabbitTemplate.convertAndSend(
                        RabbitMQConfig.MESSAGE_EXCHANGE,
                        RabbitMQConfig.USER_MESSAGE_ROUTING_KEY,
                        JSON.toJSONString(batchMessage)
                );

                log.info("发送用户消息批次到队列，消息ID: {}, 用户数量: {}",
                        messageSendDTO.getMessageId(), batchUserIds.size());
            }
        } catch (Exception e) {
            log.error("发送用户消息到队列失败，消息ID: {}", messageSendDTO.getMessageId(), e);
            throw new RuntimeException("发送用户消息到队列失败", e);
        }
    }

    /**
     * 发送角色消息到队列
     *
     * @param messageSendDTO 消息发送DTO
     */
    public void sendRoleMessage(MessageSendDTO messageSendDTO) {
        try {
            List<Long> roleIds = messageSendDTO.getRoleIds();
            if (roleIds == null || roleIds.isEmpty()) {
                log.warn("角色ID列表为空，跳过发送");
                return;
            }

            // 发送到队列
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.MESSAGE_EXCHANGE,
                    RabbitMQConfig.USER_MESSAGE_ROUTING_KEY,
                    JSON.toJSONString(messageSendDTO)
            );

            log.info("发送角色消息到队列，消息ID: {}, 角色数量: {}",
                    messageSendDTO.getMessageId(), roleIds.size());
        } catch (Exception e) {
            log.error("发送角色消息到队列失败，消息ID: {}", messageSendDTO.getMessageId(), e);
            throw new RuntimeException("发送角色消息到队列失败", e);
        }
    }

    /**
     * 发送部门消息到队列
     *
     * @param messageSendDTO 消息发送DTO
     */
    public void sendDeptMessage(MessageSendDTO messageSendDTO) {
        try {
            List<Long> deptIds = messageSendDTO.getDeptIds();
            if (deptIds == null || deptIds.isEmpty()) {
                log.warn("部门ID列表为空，跳过发送");
                return;
            }

            // 发送到队列
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.MESSAGE_EXCHANGE,
                    RabbitMQConfig.USER_MESSAGE_ROUTING_KEY,
                    JSON.toJSONString(messageSendDTO)
            );

            log.info("发送部门消息到队列，消息ID: {}, 部门数量: {}",
                    messageSendDTO.getMessageId(), deptIds.size());
        } catch (Exception e) {
            log.error("发送部门消息到队列失败，消息ID: {}", messageSendDTO.getMessageId(), e);
            throw new RuntimeException("发送部门消息到队列失败", e);
        }
    }
}
