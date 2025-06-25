package cn.zhangchuangla.message.model.bo;

import lombok.Data;

/**
 * @author Chuang
 * <p>
 * created on 2025/6/26 04:09
 */
@Data
public class MessageReadStatusBo {

    /**
     * 消息ID
     */
    private Long messageId;

    /**
     * 是否已读
     */
    private Integer isRead;


}
