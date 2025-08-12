package cn.zhangchuangla.system.core.service.impl;

import cn.zhangchuangla.common.core.entity.Option;
import cn.zhangchuangla.common.core.exception.ServiceException;
import cn.zhangchuangla.system.core.mapper.SysPostMapper;
import cn.zhangchuangla.system.core.model.entity.SysPost;
import cn.zhangchuangla.system.core.model.request.post.SysPostAddRequest;
import cn.zhangchuangla.system.core.model.request.post.SysPostQueryRequest;
import cn.zhangchuangla.system.core.model.request.post.SysPostUpdateRequest;
import cn.zhangchuangla.system.core.service.SysPostService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 岗位接口实现类
 *
 * @author Chuang
 */
@Service
@RequiredArgsConstructor
public class SysPostServiceImpl extends ServiceImpl<SysPostMapper, SysPost>
        implements SysPostService {

    private final SysPostMapper sysPostMapper;

    /**
     * 岗位列表
     *
     * @param request 操作结果
     * @return 返回分页列表
     */
    @Override
    public Page<SysPost> listPost(SysPostQueryRequest request) {
        Page<SysPost> page = new Page<>(request.getPageNum(), request.getPageSize());
        return sysPostMapper.listPost(page, request);
    }

    /**
     * 添加岗位
     *
     * @param request 请求参数
     * @return 操作结果
     */
    @Override
    public boolean addPost(SysPostAddRequest request) {
        if (isPostNameExist(request.getPostName())) {
            throw new ServiceException("岗位名称已存在");
        }
        if (isPostCodeExist(request.getPostCode())) {
            throw new ServiceException("岗位编码已存在");
        }
        SysPost sysPost = new SysPost();
        BeanUtils.copyProperties(request, sysPost);
        return save(sysPost);
    }

    /**
     * 删除岗位
     *
     * @param ids 岗位ID集合
     * @return 操作结果
     */
    @Override
    public boolean deletePost(List<Long> ids) {
        return removeByIds(ids);
    }

    /**
     * 根据ID查询岗位信息
     *
     * @param id 请求参数
     * @return 操作结果
     */
    @Override
    public SysPost getPostById(Long id) {
        return getById(id);
    }

    /**
     * 修改岗位信息
     *
     * @param request 请求参数
     * @return 操作结果
     */
    @Override
    public boolean updatePost(SysPostUpdateRequest request) {
        SysPost sysPost = new SysPost();
        BeanUtils.copyProperties(request, sysPost);
        return updateById(sysPost);
    }

    /**
     * 判断岗位编码是否存在
     *
     * @param postCode 岗位编码
     * @return true存在，false不存在
     */
    @Override
    public boolean isPostCodeExist(String postCode) {
        LambdaQueryWrapper<SysPost> eq = new LambdaQueryWrapper<SysPost>().eq(SysPost::getPostCode, postCode);
        return count(eq) > 0;
    }

    /**
     * 判断岗位名称是否存在
     *
     * @param postName 岗位名称
     * @return true存在，false不存在
     */
    @Override
    public boolean isPostNameExist(String postName) {
        LambdaQueryWrapper<SysPost> eq = new LambdaQueryWrapper<SysPost>().eq(SysPost::getPostName, postName);
        return count(eq) > 0;
    }

    /**
     * 导出岗位列表
     *
     * @param request 查询参数
     * @return 岗位列表
     */
    @Override
    public List<SysPost> exportPostList(SysPostQueryRequest request) {
        return sysPostMapper.exportPostList(request);
    }

    /**
     * 获取岗位选项
     *
     * @return 岗位选项
     */
    @Override
    public List<Option<Long>> getPostOptions() {
        LambdaQueryWrapper<SysPost> queryWrapper = new LambdaQueryWrapper<SysPost>()
                .eq(SysPost::getStatus, 0)
                .orderByDesc(SysPost::getCreateTime);
        List<SysPost> list = list(queryWrapper);
        return list.stream()
                .map(item -> new Option<>(item.getId(), item.getPostName()))
                .toList();
    }
}




