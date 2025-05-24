package cn.zhangchuangla.message.mapper;

import cn.zhangchuangla.message.model.entity.SysMessage;
import cn.zhangchuangla.message.model.request.SysMessageQueryRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统消息表Mapper接口
 *
 * @author Chuang
 * @date 2025-05-24
 */
public interface SysMessageMapper extends BaseMapper<SysMessage> {

    /**
     * 分页查询系统消息表信息
     *
     * @param page    分页对象
     * @param request 查询参数
     * @return 返回分页对象
     */
    Page<SysMessage> listSysMessage(Page<SysMessage> page, @Param("request") SysMessageQueryRequest request);

    /**
     * 根据用户ID分页查询系统消息表信息
     *
     * @param page    分页对象
     * @param userId  用户ID
     * @param request 查询参数
     * @return 列表
     */
    List<SysMessage> listUserMessageByUserId(Page<SysMessage> page, @Param("userId") Long userId, @Param("request") SysMessageQueryRequest request);
}
