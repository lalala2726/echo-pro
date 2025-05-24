package cn.zhangchuangla.message.service;

import cn.zhangchuangla.message.model.entity.SysMessage;
import cn.zhangchuangla.message.model.request.SendMessageRequest;
import cn.zhangchuangla.message.model.request.SysMessageAddRequest;
import cn.zhangchuangla.message.model.request.SysMessageQueryRequest;
import cn.zhangchuangla.message.model.request.SysMessageUpdateRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 系统消息表Service接口
 *
 * @author Chuang
 * @date 2025-05-24
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
    int sendMessage(SendMessageRequest request);
}
