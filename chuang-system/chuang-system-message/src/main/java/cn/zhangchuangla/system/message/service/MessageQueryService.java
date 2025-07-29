package cn.zhangchuangla.system.message.service;

import cn.zhangchuangla.system.message.model.dto.UserMessageDto;
import cn.zhangchuangla.system.message.model.dto.UserMessageReadCountDto;
import cn.zhangchuangla.system.message.model.request.UserMessageListQueryRequest;
import cn.zhangchuangla.system.message.model.vo.user.UserMessageVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 消息查询服务接口
 * 专门负责消息的查询相关操作，包括已读状态的查询
 *
 * @author Chuang
 */
public interface MessageQueryService {

    /**
     * 分页获取用户消息列表
     *
     * @param request 查询参数
     * @return 用户消息分页数据
     */
    Page<UserMessageDto> listUserMessageList(UserMessageListQueryRequest request);

    /**
     * 获取用户消息详情（自动标记为已读）
     *
     * @param messageId 消息ID
     * @return 消息详情
     */
    UserMessageVo getUserMessageDetail(Long messageId);

    /**
     * 获取用户消息已读未读统计
     *
     * @return 统计信息
     */
    UserMessageReadCountDto getUserMessageReadCount();


}
