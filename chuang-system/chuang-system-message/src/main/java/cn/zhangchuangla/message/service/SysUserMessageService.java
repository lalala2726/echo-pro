package cn.zhangchuangla.message.service;

import cn.zhangchuangla.message.model.entity.SysUserMessage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author Chuang
 * created on 2025/5/25
 */
public interface SysUserMessageService extends IService<SysUserMessage> {

    /**
     * 获取消息接收相关联的目标ID,如果是给部门发送的消息,则返回部门ID,如果是给用户发送的消息,则返回用户ID,如果是给全部用户发送的消息,则返回null
     *
     * @param id 消息ID
     * @return 目标ID
     */
    List<Long> getRecipientIdsByMessageId(Long id);
}
