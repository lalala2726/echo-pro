package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.utils.ParamsUtils;
import cn.zhangchuangla.system.mapper.SysUserRoleMapper;
import cn.zhangchuangla.system.model.entity.SysUserRole;
import cn.zhangchuangla.system.service.SysUserRoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhangchuang
 */
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole>
        implements SysUserRoleService {

    private final SysUserRoleMapper sysUserRoleMapper;

    public SysUserRoleServiceImpl(SysUserRoleMapper sysUserRoleMapper) {
        this.sysUserRoleMapper = sysUserRoleMapper;
    }

    /**
     * 根据用户id获取用户角色
     *
     * @param userId 用户ID
     * @return 用户角色
     */
    @Override
    public List<SysUserRole> getUserRoleByUserId(Long userId) {
        ParamsUtils.minValidParam(userId, "用户ID不能小于等于零");
        LambdaQueryWrapper<SysUserRole> sysUserRoleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        sysUserRoleLambdaQueryWrapper.eq(SysUserRole::getUserId, userId);
        return list(sysUserRoleLambdaQueryWrapper);
    }

    /**
     * 删除用户角色关联角色信息
     *
     * @param userId 用户ID
     * @return 操作结果
     */
    @Override
    public boolean deleteUserRoleAssociation(Long userId) {
        ParamsUtils.minValidParam(userId, "用户ID不能小于等于零");
        int result = sysUserRoleMapper.deleteUserRoleByUserId(userId);
        return result > 0;
    }

    /**
     * 添加用户角色关联
     *
     * @param roleId 角色ID列表
     * @param userId 用户ID
     * @return 操作结果
     */
    @Override
    public boolean addUserRoleAssociation(List<Long> roleId, Long userId) {
        ParamsUtils.minValidParam(userId, "用户ID不能小于等于零");
        roleId.forEach(role -> {
            ParamsUtils.minValidParam(role, "角色ID不能小于等于零");
        });
        roleId.forEach(role -> {
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setRoleId(role);
            sysUserRole.setUserId(userId);
            save(sysUserRole);
        });
        return true;
    }
}




