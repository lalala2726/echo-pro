package cn.zhangchuangla.message.service;

import cn.zhangchuangla.message.model.entity.SysMessage;
import cn.zhangchuangla.message.model.request.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 系统消息表Service接口
 *
 * @author Chuang
 * created on 2025/5/25
 */
public interface SysMessageService extends IService<SysMessage> {

    /**
     * 分页查询系统消息表
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
     * 发送消息
     *
     * @param request 发送消息请求参数
     * @return 结果
     */
    boolean sendMessage(SendMessageRequest request);

    /**
     * 根据用户ID发送消息
     *
     * @param userId  用户ID
     * @param message 消息
     * @return 结果
     */
    boolean sendMessageByUserId(List<Long> userId, SysMessage message);



    /**
     * 查询当前用户的消息列表
     *
     * @param request 查询参数
     * @return 消息分页结果
     */
    Page<SysMessage> listUserMessageList(UserMessageListQueryRequest request);

    /**
     * 根据消息ID查询消息详情
     * @param id 消息ID
     * @return 消息详情
     */
    SysMessage getCurrentUserMessageById(Long id);
}
