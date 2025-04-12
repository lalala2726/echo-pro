package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.constant.RedisKeyConstant;
import cn.zhangchuangla.common.core.model.entity.UserPermissions;
import cn.zhangchuangla.common.core.redis.RedisCache;
import cn.zhangchuangla.common.utils.SecurityUtils;
import cn.zhangchuangla.common.utils.StringUtils;
import cn.zhangchuangla.system.mapper.SysPermissionsMapper;
import cn.zhangchuangla.system.model.entity.SysPermissions;
import cn.zhangchuangla.system.model.request.permissions.SysPermissionsListRequest;
import cn.zhangchuangla.system.service.SysPermissionsService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 用户权限服务实现类
 *
 * @author zhangchuang
 */
@Service
@Slf4j
public class SysPermissionsServiceImpl extends ServiceImpl<SysPermissionsMapper, SysPermissions>
        implements SysPermissionsService {

    // 权限过期时间，单位天
    private final static long permissionExpireTime = 15;
    private final SysPermissionsMapper sysPermissionsMapper;
    private final RedisCache redisCache;

    @Autowired
    public SysPermissionsServiceImpl(SysPermissionsMapper sysPermissionsMapper, RedisCache redisCache) {
        this.sysPermissionsMapper = sysPermissionsMapper;
        this.redisCache = redisCache;
    }

    /**
     * 根据角色名称获取权限
     *
     * @param roleName 角色名称
     * @return 权限集合
     */
    @Override
    public Set<String> getPermissionsByRoleName(String roleName) {
        if (StringUtils.isEmpty(roleName)) {
            return Set.of();
        }
        List<SysPermissions> permissionsList = sysPermissionsMapper.getPermissionsListByRoleName(roleName);
        return permissionsList.stream()
                .map(SysPermissions::getPermissionsKey)
                .collect(Collectors.toSet());
    }

    /**
     * 根据ID获取用户权限集合
     *
     * @param id 用户ID
     * @return 返回权限集合
     */
    @Override
    public Set<String> getPermissionsByUserId(Long id) {
        //如果是管理员将拥有全部权限
        if (SecurityUtils.isSuperAdmin()) {
            return Set.of("*.*.*");
        }
        //优先从Redis中获取权限信息，如果没有，则从数据库中获取并将权限信息缓存到Redis中
        UserPermissions userPermissions = redisCache.getCacheObject(RedisKeyConstant.USER_PERMISSIONS + id);
        if (userPermissions != null) {
            return userPermissions.getPermissions();
        }
        List<SysPermissions> permissionsList = sysPermissionsMapper.getPermissionsByUserId(id);
        Set<String> userPermission = permissionsList.stream()
                .map(SysPermissions::getPermissionsKey)
                .collect(Collectors.toSet());
        UserPermissions userPermissionsRedis = new UserPermissions();
        userPermissionsRedis.setUserId(id);
        userPermissionsRedis.setPermissions(userPermission);
        redisCache.setCacheObject(RedisKeyConstant.USER_PERMISSIONS + id, userPermissionsRedis, permissionExpireTime, TimeUnit.DAYS);
        return userPermission;
    }

    /**
     * 保存用户权限到Redis
     *
     * @param userId     用户ID
     * @param expireTime 过期时间,单位天
     */
    @Override
    public void saveUserPermissionsToRedis(Long userId, long expireTime) {
        try {
            Set<String> permissions = getPermissionsByUserId(userId);
            UserPermissions userPermissions = new UserPermissions();
            userPermissions.setUserId(userId);
            userPermissions.setPermissions(permissions);
            redisCache.setCacheObject(RedisKeyConstant.USER_PERMISSIONS + userId, userPermissions, expireTime, TimeUnit.DAYS);
        } catch (Exception e) {
            log.error("保存用户权限到Redis失败", e);
        }
    }

    /**
     * 分页查询权限列表
     *
     * @param request 请求参数
     * @return 权限列表
     */
    @Override
    public Page<SysPermissions> listPermissions(SysPermissionsListRequest request) {
        Page<SysPermissions> page = new Page<>(request.getPageNum(), request.getPageSize());
        return sysPermissionsMapper.listPermissions(page, request);
    }
}




