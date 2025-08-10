package cn.zhangchuangla.system.message.consumer;

import cn.zhangchuangla.common.mq.config.RabbitMQConfig;
import cn.zhangchuangla.common.mq.dto.MessageSendDTO;
import cn.zhangchuangla.system.message.enums.MessageReceiveTypeEnum;
import cn.zhangchuangla.system.message.model.entity.SysUserMessage;
import cn.zhangchuangla.system.message.service.SysUserMessageService;
import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 消息业务消费者。
 *
 * <p>职责：</p>
 * <ul>
 *     <li>消费 MQ 中的消息投递任务，并在库中写入用户/角色/部门消息映射</li>
 *     <li>在落库成功后，使用 WebSocket/STOMP 通知前端（用户定向或分组广播）</li>
 * </ul>
 *
 * <p>为什么需要 MQ：</p>
 * <ul>
 *     <li>消息分发通常涉及大量用户，异步批处理可提升吞吐与可用性</li>
 * </ul>
 *
 * @author Chuang
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MessageBusinessConsumer {

    private final SysUserMessageService sysUserMessageService;
    // WebSocket 推送已迁移至 Service 层，消费者仅负责入库

    /**
     * 消费用户消息队列中的消息，批量插入用户消息记录
     *
     * @param message 消息内容
     */
    @RabbitListener(queues = RabbitMQConfig.USER_MESSAGE_QUEUE)
    public void handleUserMessageBatch(String message) {
        long startTime = System.currentTimeMillis();
        try {
            log.info("开始处理用户消息批次: {}", message);
            MessageSendDTO messageSendDTO = JSON.parseObject(message, MessageSendDTO.class);

            // 根据发送方式处理不同类型的消息
            String method = messageSendDTO.getSendMethod();
            MessageReceiveTypeEnum sendMethod = MessageReceiveTypeEnum.getByValue(method);
            if (sendMethod == null) {
                log.warn("未知的发送方式: {}", method);
                return;
            }
            // 仅保留向指定用户的消费逻辑，其余类型不在消费端处理
            if (sendMethod == MessageReceiveTypeEnum.USER) {
                handleUserMessage(messageSendDTO, startTime);
            } else {
                log.info("消息类型为 {}，不在消费者处理范围，消息ID:{}", sendMethod, messageSendDTO.getMessageId());
            }

        } catch (Exception e) {
            log.error("处理用户消息批次失败: {}", message, e);
            // 这里可以根据需要实现重试机制或者死信队列
            throw e;
        }
    }

    /**
     * 处理指定用户消息
     */
    private void handleUserMessage(MessageSendDTO messageSendDTO, long startTime) {
        List<Long> userIds = messageSendDTO.getUserIds();
        if (userIds == null || userIds.isEmpty()) {
            log.warn("用户ID列表为空，跳过处理");
            return;
        }

        // 批量创建用户消息记录
        List<SysUserMessage> userMessages = userIds.stream()
                .map(userId -> SysUserMessage.builder()
                        .messageId(messageSendDTO.getMessageId())
                        .userId(userId)
                        .createTime(new Date())
                        .build())
                .collect(Collectors.toList());

        // 批量插入
        boolean success = sysUserMessageService.saveBatch(userMessages);
        logResult(success, messageSendDTO, userIds.size(), startTime, "用户");
    }

    /**
     * 记录处理结果
     */
    private void logResult(boolean success, MessageSendDTO messageSendDTO, int count, long startTime, String type) {
        long endTime = System.currentTimeMillis();
        if (success) {
            log.info("{}消息批次处理成功，消息ID: {}, {}数量: {}, 耗时: {}ms",
                    type, messageSendDTO.getMessageId(), type, count, endTime - startTime);
        } else {
            log.error("{}消息批次处理失败，消息ID: {}, {}数量: {}",
                    type, messageSendDTO.getMessageId(), type, count);
            throw new RuntimeException("批量插入" + type + "消息记录失败");
        }
    }

}
