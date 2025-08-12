package cn.zhangchuangla.system.message.mapper;

import cn.zhangchuangla.system.message.model.dto.AroundMessageIdDto;
import cn.zhangchuangla.system.message.model.entity.SysMessage;
import cn.zhangchuangla.system.message.model.request.SysMessageQueryRequest;
import cn.zhangchuangla.system.message.model.request.UserMessageListQueryRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统消息表Mapper接口
 *
 * @author Chuang
 * created on  2025-05-24
 */
public interface SysMessageMapper extends BaseMapper<SysMessage> {

    /**
     * 分页查询系统消息表信息
     *
     * @param page    分页对象
     * @param request 查询参数
     * @return 返回分页对象
     */
    Page<SysMessage> pageSysMessage(Page<SysMessage> page, @Param("request") SysMessageQueryRequest request);

    /**
     * 根据用户ID分页查询系统消息表信息
     *
     * @param page   分页对象
     * @param userId 用户ID
     * @return 列表
     */
    Page<SysMessage> pageUserMessage(Page<SysMessage> page,
                                     @Param("userId") Long userId,
                                     @Param("request") UserMessageListQueryRequest request);

    /**
     * 根据用户ID分页查询系统消息表信息（已读消息）
     *
     * @param page       分页对象
     * @param userId     用户ID
     * @param request    查询参数
     * @param messageIds 已读消息ID列表
     * @return 列表
     */
    Page<SysMessage> pageUserMessageIsRead(Page<SysMessage> page,
                                           @Param("userId") Long userId,
                                           @Param("request") UserMessageListQueryRequest request,
                                           @Param("messageIds") List<Long> messageIds);

    /**
     * 根据用户ID和消息ID查询系统消息表信息
     *
     * @param userId     用户ID
     * @param messageIds 消息ID列表
     * @return 列表
     */
    List<SysMessage> listMessageWithUserIdAndMessageId(@Param("userId") Long userId,
                                                       @Param("messageIds") List<Long> messageIds);

    /**
     * 根据用户ID查询系统消息表信息
     *
     * @param userId    用户ID
     * @param messageId 消息ID
     * @return 列表
     */
    SysMessage getCurrentUserMessage(@Param("userId") Long userId, @Param("messageId") Long messageId);

    /**
     * 根据用户ID查询消息数量
     *
     * @param userId 用户ID
     * @return 消息数量
     */
    long getUserMessageCount(@Param("userId") Long userId);

    // 删除: 根据用户ID分页查询已发送的系统消息（依赖 senderId），随字段移除一并删除接口


    /**
     * 根据用户ID分页查询未读的系统消息表信息
     *
     * @param page       分页对象
     * @param userId     用户ID
     * @param request    查询参数
     * @param messageIds 已读消息ID列表（用于排除）
     * @return 列表
     */
    Page<SysMessage> pageUserMessageIsUnRead(Page<SysMessage> page,
                                             @Param("userId") Long userId,
                                             @Param("request") UserMessageListQueryRequest request,
                                             @Param("messageIds") List<Long> messageIds);

    /**
     * 获取当前消息的上一条和下一条消息ID（基于 publish_time DESC, id DESC 排序），范围限定为当前用户可见的消息集合。
     *
     * @param userId    当前用户ID
     * @param currentId 当前消息ID
     * @return 包含键 prev_id/next_id 的 Map
     */
    AroundMessageIdDto getPrevAndNextMessageId(@Param("userId") Long userId,
                                               @Param("currentId") Long currentId);
}
