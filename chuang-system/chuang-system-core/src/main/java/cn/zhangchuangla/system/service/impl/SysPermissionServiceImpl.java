package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.redis.constant.RedisConstants;
import cn.zhangchuangla.system.model.request.role.SysUpdateRolePermissionRequest;
import cn.zhangchuangla.system.model.vo.role.SysRolePermVo;
import cn.zhangchuangla.system.service.SysPermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @author Chuang
 * <p>
 * created on 2025/5/14 08:23
 */
@Service
@Slf4j
@RequiredArgsConstructor
@CacheConfig(cacheNames = RedisConstants.Auth.PERMISSIONS_PREFIX)
public class SysPermissionServiceImpl implements SysPermissionService {

    @Override
    public Set<String> getUserPermissionByRole(Set<String> roleSet) {
        return Set.of();
    }

    @Override
    public Set<String> getUserPermissionByRole(String role) {
        return Set.of();
    }

    @Override
    public SysRolePermVo getRolePermByRoleId(Long roleId) {
        return null;
    }

    @Override
    public boolean updateRolePermission(SysUpdateRolePermissionRequest request) {
        return false;
    }

    @Override
    public List<Long> getRolePermissionSelectedByRoleId(Long roleId) {
        return List.of();
    }
}
