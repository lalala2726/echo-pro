package cn.zhangchuangla.message.service.impl;

import cn.zhangchuangla.common.base.BasePageRequest;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.ParamException;
import cn.zhangchuangla.common.exception.ServiceException;
import cn.zhangchuangla.common.utils.SecurityUtils;
import cn.zhangchuangla.message.mapper.SiteMessagesMapper;
import cn.zhangchuangla.message.model.entity.SiteMessages;
import cn.zhangchuangla.message.model.entity.UserSiteMessage;
import cn.zhangchuangla.message.model.request.SiteMessageListRequest;
import cn.zhangchuangla.message.model.request.SiteMessageRequest;
import cn.zhangchuangla.message.service.SiteMessagesService;
import cn.zhangchuangla.message.service.UserSiteMessageService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * author zhangchuang
 */
@Service
@Slf4j
public class SiteMessagesServiceImpl extends ServiceImpl<SiteMessagesMapper, SiteMessages>
        implements SiteMessagesService {

    private final SiteMessagesMapper siteMessagesMapper;
    private final UserSiteMessageService userSiteMessageService;

    public SiteMessagesServiceImpl(SiteMessagesMapper siteMessagesMapper, UserSiteMessageService userSiteMessageService) {
        this.siteMessagesMapper = siteMessagesMapper;
        this.userSiteMessageService = userSiteMessageService;
    }


    /**
     * 获取当前用户站内信列表
     *
     * @return 返回分页对象
     */
    @Override
    public Page<SiteMessages> getCurrentUserSiteMessagesList(SiteMessageListRequest request) {
        Long userId = SecurityUtils.getUserId();
        Page<SiteMessages> siteMessagesPage = new Page<>(request.getPageNum(), request.getPageSize());
        return siteMessagesMapper.getCurrentUserSiteMessagesList(userId, siteMessagesPage, request.getIsRead());
    }

    /**
     * 发送站内信
     *
     * @param siteMessageRequest 站内信信息
     * @return 操作结果
     */
    @Override
    @Transactional
    public boolean sendSiteMessage(SiteMessageRequest siteMessageRequest) {

        //fixme 使用消息队列重构设计
        siteMessageRequest.getUserId().forEach(id -> {
            if (id == null || id <= 0) {
                throw new ParamException(ResponseCode.PARAM_NOT_NULL, "用户ID不能为空");
            }
        });
        // 1. 创建 SiteMessages 实例并复制属性
        SiteMessages siteMessages = new SiteMessages();
        BeanUtils.copyProperties(siteMessageRequest, siteMessages);

        // 2. 设置发送者ID和用户名
        siteMessages.setSenderId(SecurityUtils.getUserId());
        siteMessages.setSenderName(SecurityUtils.getUsername());

        // 3. 保存站内信消息 - 修改这里
        int rows = siteMessagesMapper.saveSiteMessage(siteMessages);

        // 自增主键会被自动设置到 siteMessages 对象的 id 属性中
        Long messageId = siteMessages.getId();

        // 4. 检查是否保存成功
        if (rows <= 0 || messageId == null) {
            log.error("站内信保存失败！");
            return false;
        }

        // 5. 批量创建 UserSiteMessage 列表
        List<UserSiteMessage> userSiteMessageList = siteMessageRequest.getUserId().stream()
                .map(id -> {
                    UserSiteMessage userSiteMessage = new UserSiteMessage();
                    userSiteMessage.setUserId(id);
                    userSiteMessage.setMessageId(messageId);
                    return userSiteMessage;
                })
                .collect(Collectors.toList());

        // 6. 批量保存用户消息关系
        return userSiteMessageService.saveBatch(userSiteMessageList);
    }

    /**
     * 根据 ID 获取站内信
     *
     * @param messageId 站内信 ID
     * @return 站内信对象
     */
    @Override
    public SiteMessages getSiteMessageById(Long messageId) {
        Long userId = SecurityUtils.getUserId();
        SiteMessages siteMessages = siteMessagesMapper.getCurrentUserSiteMessageById(userId, messageId);
        if (siteMessages == null)
            throw new ServiceException(ResponseCode.RESULT_IS_NULL, "没有ID为 " + messageId + " 的站内信");
        //设置站内信为已读
        List<Long> list = Collections.singletonList(messageId);
        userSiteMessageService.isRead(list);
        return siteMessages;
    }

    /**
     * 获取站内信列表
     *
     * @param request 分页请求对象
     * @return 分页结果
     */
    @Override
    public Page<SiteMessages> listSiteMessages(BasePageRequest request) {
        Page<SiteMessages> page = new Page<>(request.getPageNum(), request.getPageSize());
        return siteMessagesMapper.listSiteMessages(page);
    }


}




