package cn.zhangchuangla.message.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Chuang
 * <p>
 * created on 2025/5/25 00:39
 */
@Data
@Schema(name = "用户消息列表", description = "用户消息列表")
public class UserMessageList {

    /**
     * 消息ID
     */

    private Long id;

    /**
     * 消息标题
     */
    private String title;

    /**
     * 概要内容
     */
    private String content;
}
