package cn.zhangchuangla.message.service;

import cn.zhangchuangla.message.model.dto.UserMessageDto;
import cn.zhangchuangla.message.model.dto.UserMessageReadCountDto;
import cn.zhangchuangla.message.model.entity.SysMessage;
import cn.zhangchuangla.message.model.request.UserMessageListQueryRequest;
import cn.zhangchuangla.message.model.vo.UserMessageVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

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

  /**
   * 获取用户已发送的消息列表
   *
   * @param request 查询参数
   * @return 已发送消息分页数据
   */
  Page<SysMessage> listUserSentMessageList(UserMessageListQueryRequest request);

  /**
   * 批量查询消息的已读状态
   *
   * @param userId     用户ID
   * @param messageIds 消息ID列表
   * @return 已读的消息ID列表
   */
  List<Long> getReadMessageIds(Long userId, List<Long> messageIds);

  /**
   * 检查用户是否可以访问指定消息
   *
   * @param userId    用户ID
   * @param messageId 消息ID
   * @return 是否可以访问
   */
  boolean canUserAccessMessage(Long userId, Long messageId);
}
