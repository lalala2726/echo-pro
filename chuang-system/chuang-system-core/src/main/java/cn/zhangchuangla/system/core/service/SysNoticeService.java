package cn.zhangchuangla.system.core.service;

import cn.zhangchuangla.system.core.model.entity.SysNotice;
import cn.zhangchuangla.system.core.model.request.notice.SysNoticeAddRequest;
import cn.zhangchuangla.system.core.model.request.notice.SysNoticeQueryRequest;
import cn.zhangchuangla.system.core.model.request.notice.SysNoticeUpdateRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 公告服务接口
 *
 * @author Chuang
 */
public interface SysNoticeService extends IService<SysNotice> {

    /**
     * 分页查询公告列表
     *
     * @param request 查询参数
     * @return 公告分页列表
     */
    Page<SysNotice> listNotice(SysNoticeQueryRequest request);

    /**
     * 根据ID获取公告信息
     *
     * @param id 公告ID
     * @return 公告信息
     */
    SysNotice getNoticeById(Long id);

    /**
     * 新增公告
     *
     * @param request 新增请求
     * @return 是否成功
     */
    boolean addNotice(SysNoticeAddRequest request);

    /**
     * 修改公告
     *
     * @param request 修改请求
     * @return 是否成功
     */
    boolean updateNotice(SysNoticeUpdateRequest request);

    /**
     * 删除公告，支持批量删除
     *
     * @param ids 公告ID集合
     * @return 是否成功
     */
    boolean deleteNotice(List<Long> ids);

    /**
     * 检查公告标题是否存在
     *
     * @param noticeTitle 公告标题
     * @param id          排除的公告ID
     * @return 是否存在
     */
    boolean isNoticeTitleExist(String noticeTitle, Long id);

    /**
     * 导出公告列表
     *
     * @param request 查询参数
     * @return 导出结果
     */
    List<SysNotice> exportNoticeList(SysNoticeQueryRequest request);
}
