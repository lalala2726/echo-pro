package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.ParamException;
import cn.zhangchuangla.system.mapper.SysRoleMapper;
import cn.zhangchuangla.system.model.entity.SysRole;
import cn.zhangchuangla.system.model.entity.SysUserRole;
import cn.zhangchuangla.system.model.request.SysRoleQueryRequest;
import cn.zhangchuangla.system.service.SysRoleService;
import cn.zhangchuangla.system.service.SysUserRoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
        if (userId <= 0) {
            throw new ParamException(ResponseCode.PARAM_ERROR, "用户ID不能小于于零");
        }
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
}




