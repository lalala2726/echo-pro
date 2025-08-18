package cn.zhangchuangla.system.core.mapper;

import cn.zhangchuangla.system.core.model.entity.SysPost;
import cn.zhangchuangla.system.core.model.request.post.SysPostQueryRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Chuang
 */
public interface SysPostMapper extends BaseMapper<SysPost> {

    /**
     * 分页查询岗位信息
     *
     * @param page    分页对象
     * @param request 查询参数
     * @return 返回分页对象
     */
    Page<SysPost> listPost(Page<SysPost> page, @Param("request") SysPostQueryRequest request);

    /**
     * 导出岗位列表
     *
     * @param request 查询参数
     * @return 岗位列表
     */
    List<SysPost> exportPostList(@Param("request") SysPostQueryRequest request);
}




