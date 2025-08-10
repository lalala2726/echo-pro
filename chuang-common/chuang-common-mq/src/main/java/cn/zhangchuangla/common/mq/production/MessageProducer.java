package cn.zhangchuangla.common.mq.production;

import cn.zhangchuangla.common.mq.config.RabbitMQConfig;
import cn.zhangchuangla.common.mq.dto.MessageSendDTO;
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
            if (messageSendDTO == null) {
                return;
            }
            // 分批处理用户列表
            List<Long> userIds = messageSendDTO.getUserIds();
            if (userIds == null || userIds.isEmpty()) {
                return;
            }

            int batchSize = normalizeBatchSize(messageSendDTO.getBatchSize());
            for (int i = 0; i < userIds.size(); i += batchSize) {
                int endIndex = Math.min(i + batchSize, userIds.size());
                List<Long> batchUserIds = userIds.subList(i, endIndex);

                // 创建批次消息
                MessageSendDTO batchMessage = copyForUserBatch(messageSendDTO, batchUserIds, batchSize);

                // 发送到队列
                convertAndSendJson(batchMessage);
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
            if (messageSendDTO == null) {
                return;
            }
            List<Long> roleIds = messageSendDTO.getRoleIds();
            if (roleIds == null || roleIds.isEmpty()) {
                log.warn("角色ID列表为空，跳过发送");
                return;
            }

            // 发送到队列
            convertAndSendJson(messageSendDTO);

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
            if (messageSendDTO == null) {
                return;
            }
            List<Long> deptIds = messageSendDTO.getDeptIds();
            if (deptIds == null || deptIds.isEmpty()) {
                return;
            }

            // 发送到队列
            convertAndSendJson(messageSendDTO);

            log.info("发送部门消息到队列，消息ID: {}, 部门数量: {}",
                    messageSendDTO.getMessageId(), deptIds.size());
        } catch (Exception e) {
            throw new RuntimeException("发送部门消息到队列失败", e);
        }
    }

    private int normalizeBatchSize(Integer batchSize) {
        if (batchSize == null || batchSize <= 0) {
            return 500;
        }
        // 上限保护
        return Math.min(batchSize, 5000);
    }

    private MessageSendDTO copyForUserBatch(MessageSendDTO src, List<Long> batchUserIds, int batchSize) {
        return MessageSendDTO.builder()
                .messageId(src.getMessageId())
                .title(src.getTitle())
                .content(src.getContent())
                .messageType(src.getMessageType())
                .sendMethod(src.getSendMethod())
                .userIds(batchUserIds)
                .batchSize(batchSize)
                .build();
    }

    private void convertAndSendJson(Object payload) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.MESSAGE_EXCHANGE, RabbitMQConfig.USER_MESSAGE_ROUTING_KEY, JSON.toJSONString(payload));
    }
}
