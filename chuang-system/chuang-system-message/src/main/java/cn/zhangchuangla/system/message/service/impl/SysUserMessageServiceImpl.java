package cn.zhangchuangla.system.message.service.impl;

import cn.zhangchuangla.system.message.mapper.SysUserMessageMapper;
import cn.zhangchuangla.system.message.model.entity.SysUserMessage;
import cn.zhangchuangla.system.message.service.SysUserMessageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author Chuang
 * created on 2025/5/25
 */
@Service
@RequiredArgsConstructor
public class SysUserMessageServiceImpl extends ServiceImpl<SysUserMessageMapper, SysUserMessage>
        implements SysUserMessageService {


    /**
     * 获取消息接收相关联的接收者 ID。
     * - 如果是部门消息，返回部门ID列表；
     * - 如果是用户消息，返回用户ID列表；
     * - 如果是全员消息，返回 null。
     *
     * @param messageId 消息ID
     * @return 接收者ID列表（可能为部门ID或用户ID），或 null（表示全员）
     */
    @Override
    public List<Long> getRecipientIdsByMessageId(Long messageId) {
        List<SysUserMessage> messages = list(new LambdaQueryWrapper<SysUserMessage>()
                .eq(SysUserMessage::getMessageId, messageId));

        if (messages.isEmpty()) {
            return List.of();
        }

        return messages.stream()
                .map(this::extractTargetId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    /**
     * 提取消息对应的接收者 ID（部门ID、用户ID 或角色ID），优先顺序为：角色 > 部门 > 用户。
     * 返回 null 表示无有效接收对象（或全员消息）。
     */
    private Long extractTargetId(SysUserMessage msg) {
        if (isValidId(msg.getRoleId())) {
            return msg.getRoleId();
        }
        if (isValidId(msg.getDeptId())) {
            return msg.getDeptId();
        }
        if (isValidId(msg.getUserId())) {
            return msg.getUserId();
        }
        return null;
    }

    /**
     * 判断ID是否为有效值（排除 -1、0 等非法值）。
     */
    private boolean isValidId(Long id) {
        return id != null && id > 0;
    }
}




