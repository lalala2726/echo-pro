package cn.zhangchuangla.message.consumption;

import cn.zhangchuangla.common.core.constant.Constants;
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
            switch (messageSendDTO.getSendMethod()) {
                case Constants.MessageConstants.SEND_METHOD_USER -> handleUserMessage(messageSendDTO, startTime);
                case Constants.MessageConstants.SEND_METHOD_ROLE -> handleRoleMessage(messageSendDTO, startTime);
                case Constants.MessageConstants.SEND_METHOD_DEPT -> handleDeptMessage(messageSendDTO, startTime);
                default -> log.warn("未知的发送方式: {}", messageSendDTO.getSendMethod());
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
     * 处理角色消息
     */
    private void handleRoleMessage(MessageSendDTO messageSendDTO, long startTime) {
        List<Long> roleIds = messageSendDTO.getRoleIds();
        if (roleIds == null || roleIds.isEmpty()) {
            log.warn("角色ID列表为空，跳过处理");
            return;
        }

        // 批量创建角色消息记录
        List<SysUserMessage> roleMessages = roleIds.stream()
                .map(roleId -> SysUserMessage.builder()
                        .messageId(messageSendDTO.getMessageId())
                        .roleId(roleId)
                        .createTime(new Date())
                        .build())
                .collect(Collectors.toList());

        // 批量插入
        boolean success = sysUserMessageService.saveBatch(roleMessages);
        logResult(success, messageSendDTO, roleIds.size(), startTime, "角色");
    }

    /**
     * 处理部门消息
     */
    private void handleDeptMessage(MessageSendDTO messageSendDTO, long startTime) {
        List<Long> deptIds = messageSendDTO.getDeptIds();
        if (deptIds == null || deptIds.isEmpty()) {
            log.warn("部门ID列表为空，跳过处理");
            return;
        }

        // 批量创建部门消息记录
        List<SysUserMessage> deptMessages = deptIds.stream()
                .map(deptId -> SysUserMessage.builder()
                        .messageId(messageSendDTO.getMessageId())
                        .deptId(deptId)
                        .createTime(new Date())
                        .build())
                .collect(Collectors.toList());

        // 批量插入
        boolean success = sysUserMessageService.saveBatch(deptMessages);
        logResult(success, messageSendDTO, deptIds.size(), startTime, "部门");
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
