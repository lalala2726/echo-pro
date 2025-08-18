package cn.zhangchuangla.system.core.mapper;

import cn.zhangchuangla.system.core.model.entity.SysNotice;
import cn.zhangchuangla.system.core.model.request.notice.SysNoticeQueryRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 公告 Mapper 接口
 *
 * @author Chuang
 */
@Mapper
public interface SysNoticeMapper extends BaseMapper<SysNotice> {

    /**
     * 分页查询公告列表
     *
     * @param page    分页对象
     * @param request 查询参数
     * @return 公告分页列表
     */
    Page<SysNotice> listNotice(Page<SysNotice> page, @Param("request") SysNoticeQueryRequest request);

    /**
     * 导出公告列表
     *
     * @param request 查询参数
     * @return 公告列表
     */
    List<SysNotice> exportNoticeList(@Param("request") SysNoticeQueryRequest request);
}




