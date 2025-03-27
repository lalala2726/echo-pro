package cn.zhangchuangla.message.service.impl;

import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.ParamException;
import cn.zhangchuangla.common.utils.SecurityUtils;
import cn.zhangchuangla.message.mapper.UserSiteMessageMapper;
import cn.zhangchuangla.message.model.entity.UserSiteMessage;
import cn.zhangchuangla.message.service.UserSiteMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author zhangchuang
 */
@Service
public class UserSiteMessageServiceImpl extends ServiceImpl<UserSiteMessageMapper, UserSiteMessage>
        implements UserSiteMessageService {

    private final UserSiteMessageMapper userSiteMessageMapper;

    public UserSiteMessageServiceImpl(UserSiteMessageMapper userSiteMessageMapper) {
        this.userSiteMessageMapper = userSiteMessageMapper;
    }


    /**
     * 设置站内信为已读
     *
     * @param ids 消息ID
     */
    @Override
    public int isRead(List<Long> ids) {
        // 参数校验
        if (CollectionUtils.isEmpty(ids)) {
            throw new ParamException(ResponseCode.PARAM_NOT_NULL, "参数不能为空");
        }
        Long userId = SecurityUtils.getUserId();

        // 批量更新站内信为已读状态
        return userSiteMessageMapper.batchMarkAsRead(ids, userId);
    }

    /**
     * 设置站内信为已读
     *
     * @return 影响行数
     */
    @Override
    public int isReadAll() {
        Long currentUserId = SecurityUtils.getUserId();
        return userSiteMessageMapper.markAllAsRead(currentUserId);
    }

    /**
     * 删除站内信
     *
     * @param ids 消息ID
     * @return 影响行数
     */
    @Override
    public int deleteSiteMessage(List<Long> ids) {
        // 参数校验
        if (CollectionUtils.isEmpty(ids)) {
            throw new ParamException(ResponseCode.PARAM_NOT_NULL, "参数不能为空");
        }

        Long currentUserId = SecurityUtils.getUserId();

        // 批量删除操作
        return userSiteMessageMapper.batchDeleteByMessageIds(ids, currentUserId);
    }


}




