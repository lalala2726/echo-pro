package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.constant.RedisKeyConstant;
import cn.zhangchuangla.common.core.redis.RedisCache;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.ParamException;
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
    private final RedisCache redisCache;

    public SysUserRoleServiceImpl(SysUserRoleMapper sysUserRoleMapper, RedisCache redisCache) {
        this.sysUserRoleMapper = sysUserRoleMapper;
        this.redisCache = redisCache;
    }

    /**
     * 根据用户id获取用户角色
     *
     * @param userId 用户ID
     * @return 用户角色
     */
    @Override
    public List<SysUserRole> getUserRoleByUserId(Long userId) {
        redisCache.deleteObject(RedisKeyConstant.USER_ROLE + userId);
        LambdaQueryWrapper<SysUserRole> sysUserRoleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        sysUserRoleLambdaQueryWrapper.eq(SysUserRole::getUserId, userId);
        return list(sysUserRoleLambdaQueryWrapper);
    }

    /**
     * 删除用户角色关联角色信息
     *
     * @param userId 用户ID
     */
    @Override
    public void deleteUserRoleAssociation(Long userId) {
        if (userId == null || userId <= 0) {
            throw new ParamException(ResponseCode.PARAM_ERROR, "用户ID不能小于等于零");
        }
        //删除redis缓存
        redisCache.deleteObject(RedisKeyConstant.USER_ROLE + userId);
        sysUserRoleMapper.deleteUserRoleByUserId(userId);
    }

    /**
     * 添加用户角色关联
     *
     * @param roleId 角色ID列表
     * @param userId 用户ID
     */
    @Override
    public void addUserRoleAssociation(List<Long> roleId, Long userId) {
        redisCache.deleteObject(RedisKeyConstant.USER_ROLE + userId);
        roleId.forEach(role -> {
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setRoleId(role);
            sysUserRole.setUserId(userId);
            save(sysUserRole);
        });
    }
}




