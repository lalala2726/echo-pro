package cn.zhangchuangla.system.core.service.impl;

import cn.zhangchuangla.common.core.exception.ServiceException;
import cn.zhangchuangla.common.core.utils.SecurityUtils;
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
public class SysNoticeServiceImpl extends ServiceImpl<SysNoticeMapper, SysNotice> implements SysNoticeService {

    private final SysNoticeMapper sysNoticeMapper;

    @Override
    public Page<SysNotice> listNotice(SysNoticeQueryRequest request) {
        Page<SysNotice> page = new Page<>(request.getPageNum(), request.getPageSize());
        return sysNoticeMapper.listNotice(page, request);
    }

    @Override
    public SysNotice getNoticeById(Long id) {
        return getById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addNotice(SysNoticeAddRequest request) {
        // 检查标题是否存在
        if (isNoticeTitleExist(request.getNoticeTitle(), null)) {
            throw new ServiceException("公告标题已存在");
        }

        SysNotice sysNotice = new SysNotice();
        BeanUtils.copyProperties(request, sysNotice);
        // 设置创建者
        sysNotice.setCreateBy(SecurityUtils.getUsername());
        return save(sysNotice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateNotice(SysNoticeUpdateRequest request) {
        // 检查公告是否存在
        SysNotice existingNotice = getById(request.getId());
        if (existingNotice == null) {
            throw new ServiceException("公告不存在");
        }

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteNotice(List<Long> ids) {
        return removeByIds(ids);
    }

    @Override
    public boolean isNoticeTitleExist(String noticeTitle, Long id) {
        LambdaQueryWrapper<SysNotice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysNotice::getNoticeTitle, noticeTitle);
        if (id != null) {
            queryWrapper.ne(SysNotice::getId, id);
        }
        return count(queryWrapper) > 0;
    }
}
