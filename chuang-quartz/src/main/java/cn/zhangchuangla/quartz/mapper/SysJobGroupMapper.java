package cn.zhangchuangla.quartz.mapper;

import cn.zhangchuangla.quartz.entity.SysJobGroup;
import cn.zhangchuangla.quartz.model.request.SysJobGroupQueryRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 定时任务组 Mapper 接口
 *
 * @author Chuang
 */
@Mapper
public interface SysJobGroupMapper extends BaseMapper<SysJobGroup> {

    /**
     * 分页查询任务组列表
     *
     * @param page    分页对象
     * @param request 查询条件
     * @return 任务组分页列表
     */
    Page<SysJobGroup> selectJobGroupList(Page<SysJobGroup> page, @Param("request") SysJobGroupQueryRequest request);

    /**
     * 查询所有启用的任务组
     *
     * @return 任务组列表
     */
    List<SysJobGroup> selectEnabledGroups();

    /**
     * 检查任务组名称是否存在
     *
     * @param groupName 任务组名称
     * @param id        排除的任务组ID
     * @return 数量
     */
    int checkGroupNameExists(@Param("groupName") String groupName, @Param("id") Long id);

    /**
     * 检查任务组编码是否存在
     *
     * @param groupCode 任务组编码
     * @param id        排除的任务组ID
     * @return 数量
     */
    int checkGroupCodeExists(@Param("groupCode") String groupCode, @Param("id") Long id);

    /**
     * 检查任务组下是否有任务
     *
     * @param groupId 任务组ID
     * @return 任务数量
     */
    int countJobsByGroupId(@Param("groupId") Long groupId);
}
