package cn.zhangchuangla.system.message.mapper;

import cn.zhangchuangla.system.message.model.entity.SysUserMessageExt;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author Chuang
 */
public interface UserMessageExtMapper extends BaseMapper<SysUserMessageExt> {

    /**
     * 获取已读消息数量
     *
     * @return 已读消息数量
     */
    long getReadMessageCount(@Param("userId") Long userId);
}




