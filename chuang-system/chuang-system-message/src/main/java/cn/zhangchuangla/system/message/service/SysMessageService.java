package cn.zhangchuangla.system.message.service;

import cn.zhangchuangla.system.message.model.entity.SysMessage;
import cn.zhangchuangla.system.message.model.request.SysMessageQueryRequest;
import cn.zhangchuangla.system.message.model.request.SysMessageUpdateRequest;
import cn.zhangchuangla.system.message.model.request.SysSendMessageRequest;
import cn.zhangchuangla.system.message.model.request.UserMessageListQueryRequest;
import cn.zhangchuangla.system.message.model.vo.system.SysMessageVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 系统消息服务接口
 * 专门负责消息的CRUD操作和发送
 *
 * @author Chuang
 * created on 2025/5/25
 */
public interface SysMessageService extends IService<SysMessage> {

    /**
     * 分页查询系统消息表（管理员功能）
     *
     * @param request 查询参数
     * @return 分页结果
     */
    Page<SysMessage> listSysMessage(SysMessageQueryRequest request);

    /**
     * 根据ID查询系统消息表
     *
     * @param id ID
     * @return 系统消息表
     */
    SysMessageVo getSysMessageById(Long id);


    /**
     * 修改系统消息表
     *
     * @param request 修改请求参数
     * @return 结果
     */
    boolean updateSysMessage(SysMessageUpdateRequest request);

    /**
     * 批量删除系统消息表
     *
     * @param ids 需要删除的ID集合
     * @return 结果
     */
    boolean deleteSysMessageByIds(List<Long> ids);

    /**
     * 系统管理员发送消息
     *
     * @param request 发送消息请求参数
     * @return 结果
     */
    boolean sysSendMessage(SysSendMessageRequest request);

    /**
     * 系统自动发送消息（无登录用户）
     *
     * @param request 发送消息请求参数
     * @return 发送结果
     */
    boolean systemSendMessage(SysSendMessageRequest request);

    /**
     * 根据用户ID批量发送消息
     *
     * @param userIds 用户ID列表
     * @param message 消息内容
     * @return 结果
     */
    boolean sendMessageByUserId(List<Long> userIds, SysMessage message);


    /**
     * 获取用户消息数量
     *
     * @param userId 用户ID
     * @return 用户消息数量
     */
    long getUserMessageCount(Long userId);

    /**
     * 分页查询用户消息表
     *
     * @param sysMessagePage 分页参数
     * @param userId         用户ID
     * @param request        查询参数
     * @return 分页结果
     */
    Page<SysMessage> pageUserMessage(Page<SysMessage> sysMessagePage, Long userId, UserMessageListQueryRequest request);


    /**
     * 分页查询用户已读消息
     *
     * @param sysMessagePage 分页参数
     * @param userId         用户ID
     * @param request        查询参数
     * @param messageIds     已读消息ID列表
     * @return 分页结果
     */
    Page<SysMessage> pageUserMessageIsRead(Page<SysMessage> sysMessagePage, Long userId, UserMessageListQueryRequest request, List<Long> messageIds);

    /**
     * 根据用户ID和消息ID获取当前用户消息
     *
     * @param userId    用户ID
     * @param messageId 消息ID
     * @return 当前用户消息
     */
    SysMessage getCurrentUserMessage(Long userId, Long messageId);


    /**
     * 根据用户ID和消息ID列表获取消息列表
     *
     * @param userId     用户ID
     * @param messageIds 消息ID列表
     * @return 消息列表
     */
    List<SysMessage> listMessageWithUserIdAndMessageId(Long userId, List<Long> messageIds);

    /**
     * 批量删除消息
     *
     * @param ids 消息ID列表
     * @return 删除结果
     */
    boolean deleteMessages(List<Long> ids);

    /**
     * 分页查询用户未读消息
     *
     * @param sysMessagePage 分页参数
     * @param userId         用户ID
     * @param request        查询参数
     * @param messageIds     已读消息ID列表（用于排除）
     * @return 分页结果
     */
    Page<SysMessage> pageUserMessageIsUnRead(Page<SysMessage> sysMessagePage, Long userId, UserMessageListQueryRequest request, List<Long> messageIds);
}
