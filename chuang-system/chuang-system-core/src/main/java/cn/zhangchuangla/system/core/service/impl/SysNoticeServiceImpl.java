package cn.zhangchuangla.system.core.service.impl;

import cn.zhangchuangla.common.core.exception.ServiceException;
import cn.zhangchuangla.common.core.utils.SecurityUtils;
import cn.zhangchuangla.common.core.utils.XssUtils;
import cn.zhangchuangla.system.core.mapper.SysNoticeMapper;
import cn.zhangchuangla.system.core.model.entity.SysNotice;
import cn.zhangchuangla.system.core.model.request.notice.SysNoticeAddRequest;
import cn.zhangchuangla.system.core.model.request.notice.SysNoticeQueryRequest;
import cn.zhangchuangla.system.core.model.request.notice.SysNoticeUpdateRequest;
import cn.zhangchuangla.system.core.service.SysNoticeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 公告服务实现类
 *
 * @author Chuang
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysNoticeServiceImpl extends ServiceImpl<SysNoticeMapper, SysNotice>
        implements SysNoticeService {

    private final SysNoticeMapper sysNoticeMapper;

    /**
     * 查询公告列表
     *
     * @param request 查询参数
     * @return 公告列表
     */
    @Override
    public Page<SysNotice> listNotice(SysNoticeQueryRequest request) {
        Page<SysNotice> page = new Page<>(request.getPageNum(), request.getPageSize());
        return sysNoticeMapper.listNotice(page, request);
    }

    /**
     * 根据id查询公告信息
     *
     * @param id 公告ID
     * @return 公告信息
     */
    @Override
    public SysNotice getNoticeById(Long id) {
        return getById(id);
    }

    /**
     * 添加公告
     *
     * @param request 添加请求
     * @return 是否添加成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addNotice(SysNoticeAddRequest request) {
        // 检查标题是否存在
        if (isNoticeTitleExist(request.getNoticeTitle(), null)) {
            throw new ServiceException("公告标题已存在");
        }
        // XSS 清洗公告标题与内容
        request.setNoticeTitle(XssUtils.sanitizeHtml(request.getNoticeTitle()));
        request.setNoticeContent(XssUtils.sanitizeHtml(request.getNoticeContent()));

        SysNotice sysNotice = new SysNotice();
        BeanUtils.copyProperties(request, sysNotice);
        // 设置创建者
        sysNotice.setCreateBy(SecurityUtils.getUsername());
        return save(sysNotice);
    }

    /**
     * 修改公告
     *
     * @param request 修改公告请求参数
     * @return 修改结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateNotice(SysNoticeUpdateRequest request) {
        // 检查公告是否存在
        SysNotice existingNotice = getById(request.getId());
        if (existingNotice == null) {
            throw new ServiceException("公告不存在");
        }

        // XSS 清洗公告标题与内容
        request.setNoticeTitle(XssUtils.sanitizeHtml(request.getNoticeTitle()));
        request.setNoticeContent(XssUtils.sanitizeHtml(request.getNoticeContent()));

        // 检查标题是否存在
        if (isNoticeTitleExist(request.getNoticeTitle(), request.getId())) {
            throw new ServiceException("公告标题已存在");
        }

        SysNotice sysNotice = new SysNotice();
        BeanUtils.copyProperties(request, sysNotice);

        // 设置更新者
        sysNotice.setUpdateBy(SecurityUtils.getUsername());

        return updateById(sysNotice);
    }

    /**
     * 删除公告
     *
     * @param ids 公告ID列表
     * @return 删除结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteNotice(List<Long> ids) {
        return removeByIds(ids);
    }

    /**
     * 判断公告标题是否存在
     *
     * @param noticeTitle 公告标题
     * @param id          公告ID
     * @return true:存在 false:不存在
     */
    @Override
    public boolean isNoticeTitleExist(String noticeTitle, Long id) {
        LambdaQueryWrapper<SysNotice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysNotice::getNoticeTitle, noticeTitle);
        if (id != null) {
            queryWrapper.ne(SysNotice::getId, id);
        }
        return count(queryWrapper) > 0;
    }

    /**
     * 导出公告列表
     *
     * @param request 查询参数
     * @return 查询参数
     */
    @Override
    public List<SysNotice> exportNoticeList(SysNoticeQueryRequest request) {
        return sysNoticeMapper.exportNoticeList(request);
    }
}
