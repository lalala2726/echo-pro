package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.constant.RedisKeyConstant;
import cn.zhangchuangla.common.core.model.entity.UserPermissions;
import cn.zhangchuangla.common.core.redis.RedisCache;
import cn.zhangchuangla.common.utils.ParamsUtils;
import cn.zhangchuangla.common.utils.StringUtils;
import cn.zhangchuangla.system.mapper.SysPermissionsMapper;
import cn.zhangchuangla.system.model.entity.SysPermissions;
import cn.zhangchuangla.system.service.SysPermissionsService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 包括权限相关操作
 *
 * @author zhangchuang
 */
@Service
@Slf4j
public class SysPermissionsServiceImpl extends ServiceImpl<SysPermissionsMapper, SysPermissions>
        implements SysPermissionsService {

    private final SysPermissionsMapper sysPermissionsMapper;
    private final RedisCache redisCache;
    private final static long permissionExpireTime = 15;

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
        ParamsUtils.minValidParam(id, "用户ID不能小于等于零");
        Object object = redisCache.getCacheObject(RedisKeyConstant.USER_PERMISSIONS + id);
        if (object != null) {
            UserPermissions userPermissions = JSON.parseObject(JSON.toJSONString(object), UserPermissions.class);
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
            throw new RuntimeException(e);
        }
    }
}




