package cn.zhangchuangla.common.mq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 消息发送DTO
 *
 * @author Chuang
 * created on 2025/5/25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageSendDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 消息ID
     */
    private Long messageId;

    /**
     * 消息标题
     */
    private String title;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息类型：system/notice/announcement
     */
    private String messageType;

    /**
     * 发送方式：user/role/dept/all
     */
    private String sendMethod;

    /**
     * 接收用户ID列表（sendMethod=0时使用）
     */
    private List<Long> userIds;

    /**
     * 接收角色ID列表（sendMethod=1时使用）
     */
    private List<Long> roleIds;

    /**
     * 接收部门ID列表（sendMethod=2时使用）
     */
    private List<Long> deptIds;

    /**
     * 批次大小（用于分批处理）
     */
    @Builder.Default
    private Integer batchSize = 500;
}
