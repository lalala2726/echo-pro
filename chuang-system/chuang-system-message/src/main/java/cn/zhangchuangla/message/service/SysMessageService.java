package cn.zhangchuangla.message.service;

import cn.zhangchuangla.message.model.entity.SysMessage;
import cn.zhangchuangla.message.model.request.*;
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
    SysMessage getSysMessageById(Long id);

    /**
     * 新增系统消息表
     *
     * @param request 新增请求参数
     * @return 结果
     */
    boolean addSysMessage(SysMessageAddRequest request);

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
     * 根据用户ID批量发送消息
     *
     * @param userIds 用户ID列表
     * @param message 消息内容
     * @return 结果
     */
    boolean sendMessageByUserId(List<Long> userIds, SysMessage message);

    /**
     * 用户发送消息
     *
     * @param request 发送消息请求参数
     * @return 结果
     */
    boolean userSendMessage(UserSendMessageRequest request);

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
     * 根据用户ID和消息ID获取当前用户消息
     *
     * @param userId    用户ID
     * @param messageId 消息ID
     * @return 当前用户消息
     */
    SysMessage getCurrentUserMessage(Long userId, Long messageId);

    /**
     * 分页查询用户发送的消息
     *
     * @param page    分页参数
     * @param userId  用户ID
     * @param request 查询参数
     * @return 分页结果
     */
    Page<SysMessage> pageUserSentMessage(Page<SysMessage> page, Long userId, UserMessageListQueryRequest request);

    /**
     * 根据用户ID和消息ID列表获取消息列表
     *
     * @param userId    用户ID
     * @param messageId 消息ID列表
     * @return 消息列表
     */
    List<SysMessage> listMessageWithUserIdAndMessageId(Long userId, List<Long> messageId);
}
