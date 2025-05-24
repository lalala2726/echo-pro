package cn.zhangchuangla.message.service.impl;

import cn.zhangchuangla.message.mapper.SysMessageMapper;
import cn.zhangchuangla.message.model.entity.SysMessage;
import cn.zhangchuangla.message.model.request.SendMessageRequest;
import cn.zhangchuangla.message.model.request.SysMessageAddRequest;
import cn.zhangchuangla.message.model.request.SysMessageQueryRequest;
import cn.zhangchuangla.message.model.request.SysMessageUpdateRequest;
import cn.zhangchuangla.message.service.SysMessageService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统消息表Service实现
 *
 * @author Chuang
 * @date 2025-05-24
 */
@Service
@RequiredArgsConstructor
public class SysMessageServiceImpl extends ServiceImpl<SysMessageMapper, SysMessage> implements SysMessageService {

    private final SysMessageMapper sysMessageMapper;

    /**
     * 分页查询系统消息表
     *
     * @param request 查询参数
     * @return 分页结果
     */
    @Override
    public Page<SysMessage> listSysMessage(SysMessageQueryRequest request) {
        Page<SysMessage> page = new Page<>(request.getPageNum(), request.getPageSize());
        return sysMessageMapper.listSysMessage(page, request);
    }

    /**
     * 根据ID查询系统消息表
     *
     * @param id ID
     * @return 系统消息表
     */
    @Override
    public SysMessage getSysMessageById(Long id) {
        return getById(id);
    }

    /**
     * 新增系统消息表
     *
     * @param request 新增请求参数
     * @return 结果
     */
    @Override
    public boolean addSysMessage(SysMessageAddRequest request) {
        SysMessage sysMessage = new SysMessage();
        BeanUtils.copyProperties(request, sysMessage);
        return save(sysMessage);
    }

    /**
     * 修改系统消息表
     *
     * @param request 修改请求参数
     * @return 结果
     */
    @Override
    public boolean updateSysMessage(SysMessageUpdateRequest request) {
        SysMessage sysMessage = new SysMessage();
        BeanUtils.copyProperties(request, sysMessage);
        return updateById(sysMessage);
    }

    /**
     * 批量删除系统消息表
     *
     * @param ids 需要删除的ID集合
     * @return 结果
     */
    @Override
    public boolean deleteSysMessageByIds(List<Long> ids) {
        return removeByIds(ids);
    }

    /**
     * 发送消息
     *
     * @param request 发送消息请求参数
     * @return 结果
     */
    @Override
    public int sendMessage(SendMessageRequest request) {
        switch (request.getSendMethod()) {
            case 0:
                //TODO 根据ID发送消息
                break;
            case 1:
                //TODO 根据角色发送消息
                break;
            case 2:
                //TODO 根据部门发送消息
                break;
        }
        return 0;
    }
}
