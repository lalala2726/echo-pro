package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.utils.ParamsUtils;
import cn.zhangchuangla.system.mapper.SysRoleMapper;
import cn.zhangchuangla.system.model.entity.SysRole;
import cn.zhangchuangla.system.model.entity.SysUserRole;
import cn.zhangchuangla.system.model.request.role.SysRoleQueryRequest;
import cn.zhangchuangla.system.service.SysRoleService;
import cn.zhangchuangla.system.service.SysUserRoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author zhangchuang
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole>
        implements SysRoleService {

    private final SysUserRoleService sysUserRoleService;

    public SysRoleServiceImpl(SysUserRoleService sysUserRoleService) {
        this.sysUserRoleService = sysUserRoleService;
    }

    //todo 添加一个根据用户ID获取当前用户所拥有的角色,每个用户角色都会缓存到Redis和用户Id进行绑定,方便后续撤销等操作

    /**
     * 角色列表
     *
     * @param request 查询参数
     * @return 分页列表
     */
    @Override
    public Page<SysRole> RoleList(SysRoleQueryRequest request) {
        LambdaQueryWrapper<SysRole> roleLambdaQueryWrapper = new LambdaQueryWrapper<SysRole>()
                .like(request.getName() != null && !request.getName().isEmpty(),
                        SysRole::getRoleName, request.getName());

        return page(new Page<>(request.getPageNum(), request.getPageSize()), roleLambdaQueryWrapper);
    }

    /**
     * 根据用户id获取角色列表
     *
     * @param userId 用户id
     * @return 角色列表
     */
    @Override
    public List<SysRole> getRoleListByUserId(Long userId) {
        //todo 将角色信息缓存到数据中,当用户角色信息发生变化时，更新缓存
        ParamsUtils.minValidParam(userId, "用户ID不能为小于等于零");
        ArrayList<Long> roleIds = new ArrayList<>();
        LambdaQueryWrapper<SysRole> sysRoleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        List<SysUserRole> userRoleList = sysUserRoleService.getUserRoleByUserId(userId);
        userRoleList.forEach(sysUserRole -> {
            Long roleId = sysUserRole.getRoleId();
            roleIds.add(roleId);
        });
        if (!roleIds.isEmpty()) {
            sysRoleLambdaQueryWrapper.in(SysRole::getRoleId, roleIds);
            return list(sysRoleLambdaQueryWrapper);
        }

        return null;
    }

    /**
     * 根据用户id获取角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    @Override
    public Set<String> getUserRoleSetByUserId(Long userId) {
        ParamsUtils.minValidParam(userId, "用户ID不能为小于等于零");
        List<SysRole> roleListByUserId = getRoleListByUserId(userId);
        return roleListByUserId.stream()
                .map(SysRole::getRoleKey)
                .collect(Collectors.toSet());
    }


}




