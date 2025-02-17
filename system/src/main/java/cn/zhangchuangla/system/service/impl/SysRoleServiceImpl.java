package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.system.model.request.SysRoleQueryRequest;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.zhangchuangla.system.model.entity.SysRole;
import cn.zhangchuangla.system.service.SysRoleService;
import cn.zhangchuangla.system.mapper.SysRoleMapper;
import org.springframework.stereotype.Service;

/**
* @author zhangchuang
*/
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole>
    implements SysRoleService{

    /**
     * 角色列表
     * @param request  查询参数
     * @return 分页列表
     */
    @Override
    public Page<SysRole> RoleList(SysRoleQueryRequest request) {
        LambdaQueryWrapper<SysRole> roleLambdaQueryWrapper = new LambdaQueryWrapper<SysRole>()
                .like(request.getName() != null && !request.getName().isEmpty(),
                        SysRole::getName, request.getName());

        return page(new Page<>(request.getPageNum(), request.getPageSize()),roleLambdaQueryWrapper);
    }
}




