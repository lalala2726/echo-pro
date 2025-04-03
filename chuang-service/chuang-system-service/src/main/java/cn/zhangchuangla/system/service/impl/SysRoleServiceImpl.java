package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.constant.RedisKeyConstant;
import cn.zhangchuangla.common.core.redis.RedisCache;
import cn.zhangchuangla.common.exception.ServiceException;
import cn.zhangchuangla.common.utils.ParamsUtils;
import cn.zhangchuangla.system.mapper.SysRoleMapper;
import cn.zhangchuangla.system.model.entity.SysRole;
import cn.zhangchuangla.system.model.request.role.SysRoleAddRequest;
import cn.zhangchuangla.system.model.request.role.SysRoleQueryRequest;
import cn.zhangchuangla.system.model.request.role.SysRoleUpdateRequest;
import cn.zhangchuangla.system.service.SysRoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 角色接口实现类
 *
 * @author zhangchuang
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole>
        implements SysRoleService {

    private final SysRoleMapper sysRoleMapper;

    private final RedisCache redisCache;

    @Autowired
    public SysRoleServiceImpl(SysRoleMapper sysRoleMapper, RedisCache redisCache) {
        this.sysRoleMapper = sysRoleMapper;
        this.redisCache = redisCache;
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
        List<SysRole> cacheRoleCache = redisCache.getCacheObject(RedisKeyConstant.USER_ROLE + userId);
        if (cacheRoleCache != null) {
            return cacheRoleCache;
        }
        List<SysRole> roleListByUserId = sysRoleMapper.getRoleListByUserId(userId);
        redisCache.setCacheObject(RedisKeyConstant.USER_ROLE + userId, roleListByUserId);
        return roleListByUserId;
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
        if (roleListByUserId == null) {
            return null;
        }
        return roleListByUserId.stream()
                .map(SysRole::getRoleKey)
                .collect(Collectors.toSet());
    }

    /**
     * 添加学生信息
     *
     * @param roleAddRequest 请求参数
     */
    @Override
    public void addRoleInfo(SysRoleAddRequest roleAddRequest) {
        if (isRoleNameExist(roleAddRequest.getRoleName())) {
            throw new ServiceException("角色名称已存在");
        }
        if (isRoleKeyExist(roleAddRequest.getRoleKey())) {
            throw new ServiceException("角色权限字符串已存在");
        }
        SysRole sysRole = new SysRole();
        BeanUtils.copyProperties(roleAddRequest, sysRole);
        save(sysRole);
    }

    /**
     * 判断角色名称是否存在
     *
     * @param roleName 角色名称
     * @return true存在，false不存在
     */
    @Override
    public boolean isRoleNameExist(String roleName) {
        if (roleName != null && !roleName.isEmpty()) {
            LambdaQueryWrapper<SysRole> sysRoleLambdaQueryWrapper = new LambdaQueryWrapper<>();
            sysRoleLambdaQueryWrapper.eq(SysRole::getRoleName, roleName);
            return count(sysRoleLambdaQueryWrapper) > 0;
        }
        return false;
    }

    /**
     * 判断角色权限字符串是否存在
     *
     * @param roleKey 角色权限字符串
     * @return true存在，false不存在
     */
    @Override
    public boolean isRoleKeyExist(String roleKey) {
        if (roleKey != null && !roleKey.isEmpty()) {
            LambdaQueryWrapper<SysRole> sysRoleLambdaQueryWrapper = new LambdaQueryWrapper<>();
            sysRoleLambdaQueryWrapper.eq(SysRole::getRoleKey, roleKey);
            return count(sysRoleLambdaQueryWrapper) > 0;
        }
        return false;
    }

    /**
     * 修改角色信息
     *
     * @param request 修改角色信息
     * @return 操作结果
     */
    @Override
    public boolean updateRoleInfo(SysRoleUpdateRequest request) {
        //test 这边需要待测试
        SysRole sysRole = new SysRole();
        BeanUtils.copyProperties(request, sysRole);
        return updateById(sysRole);
    }

}




