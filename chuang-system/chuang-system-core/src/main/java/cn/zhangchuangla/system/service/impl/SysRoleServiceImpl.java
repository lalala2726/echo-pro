package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.constant.RedisConstants;
import cn.zhangchuangla.common.constant.SysRolesConstant;
import cn.zhangchuangla.common.core.redis.RedisCache;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.ParamException;
import cn.zhangchuangla.common.exception.ServiceException;
import cn.zhangchuangla.common.model.entity.Option;
import cn.zhangchuangla.system.mapper.SysRoleMapper;
import cn.zhangchuangla.system.model.entity.SysRole;
import cn.zhangchuangla.system.model.entity.SysRoleMenu;
import cn.zhangchuangla.system.model.request.role.SysRoleAddRequest;
import cn.zhangchuangla.system.model.request.role.SysRoleListRequest;
import cn.zhangchuangla.system.model.request.role.SysRoleUpdateRequest;
import cn.zhangchuangla.system.service.SysRoleMenuService;
import cn.zhangchuangla.system.service.SysRoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 角色接口实现类
 *
 * @author Chuang
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole>
        implements SysRoleService {

    private final SysRoleMapper sysRoleMapper;
    private final SysRoleMenuService sysRoleMenuService;
    private final RedisCache redisCache;


    /**
     * 角色列表
     *
     * @param request 查询参数
     * @return 分页列表
     */
    @Override
    public Page<SysRole> roleList(SysRoleListRequest request) {
        LambdaQueryWrapper<SysRole> roleLambdaQueryWrapper = new LambdaQueryWrapper<SysRole>()
                .like(request.getRoleName() != null && !request.getRoleName().isEmpty(),
                        SysRole::getRoleName, request.getRoleName());
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
        return sysRoleMapper.getRoleListByUserId(userId);
    }

    /**
     * 根据用户id获取角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    @Override
    public Set<String> getRoleSetByUserId(Long userId) {
        if (userId <= 0) {
            throw new ParamException(ResponseCode.INVALID_ROLE_ID, "用户ID无效");
        }

        String cacheKey = RedisConstants.Auth.ROLE_KEY + userId;

        // 从缓存获取角色权限集合
        Set<String> roleSet = redisCache.getCacheObject(cacheKey);
        if (roleSet == null || roleSet.isEmpty()) {
            List<SysRole> roleList = getRoleListByUserId(userId);
            roleSet = roleList.stream()
                    .map(SysRole::getRoleKey)
                    .collect(Collectors.toSet());
            redisCache.setCacheObject(cacheKey, roleSet);
        }
        return roleSet;
    }

    /**
     * 添加角色信息
     *
     * @param roleAddRequest 请求参数
     */
    @Override
    public boolean addRoleInfo(SysRoleAddRequest roleAddRequest) {
        SysRole sysRole = new SysRole();
        BeanUtils.copyProperties(roleAddRequest, sysRole);
        return save(sysRole);
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
        SysRole role = getById(request.getRoleId());
        if (role == null) {
            throw new ServiceException(ResponseCode.RESULT_IS_NULL, "角色不存在");
        }
        // 检查是否包含超级管理员角色
        boolean contains = SysRolesConstant.SUPER_ADMIN.equals(role.getRoleKey());
        if (contains) {
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "超级管理员角色不允许修改");
        }
        SysRole sysRole = new SysRole();
        BeanUtils.copyProperties(request, sysRole);
        return updateById(sysRole);
    }

    /**
     * 获取部门下拉列表
     *
     * @return 下拉列表
     */
    @Override
    public List<Option<Long>> getRoleOptions() {
        List<SysRole> roleList = list();
        if (roleList != null && !roleList.isEmpty()) {
            return roleList.stream()
                    .map(role -> new Option<>(role.getRoleId(), role.getRoleName()))
                    .collect(Collectors.toList());
        }
        return null;
    }

    /**
     * 根据用户id获取角色id集合
     *
     * @param userId 用户ID
     * @return 角色ID集合
     */
    @Override
    public Set<Long> getUserRoleIdByUserId(Long userId) {
        if (userId <= 0) throw new ParamException(ResponseCode.PARAM_ERROR, "用户ID不能小于等于0");
        List<SysRole> roleList = getRoleListByUserId(userId);
        if (roleList == null) {
            return null;
        }
        return roleList.stream()
                .map(SysRole::getRoleId)
                .collect(Collectors.toSet());
    }

    /**
     * 删除角色信息，支持批量删除
     *
     * @param ids 角色ID
     * @return 删除结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRoleInfo(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new ServiceException(ResponseCode.PARAM_ERROR, "角色ID不能为空");
        }

        // 检查是否包含超级管理员角色
        List<SysRole> roles = listByIds(ids);
        if (roles.stream().anyMatch(role -> SysRolesConstant.SUPER_ADMIN.equals(role.getRoleKey()))) {
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "超级管理员角色不允许删除");
        }

        // 检查角色是否已分配菜单
        LambdaQueryWrapper<SysRoleMenu> eq = new LambdaQueryWrapper<SysRoleMenu>().in(SysRoleMenu::getRoleId, ids);
        List<SysRoleMenu> list = sysRoleMenuService.list(eq);
        if (list != null && !list.isEmpty()) {
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "角色已分配用户，不能删除");
        }

        return removeByIds(ids);
    }

    /**
     * 根据角色ID集合获取角色权限字符串集合
     *
     * @param roleId 角色ID集合
     * @return 角色权限字符串集合
     */
    @Override
    public Set<String> getRoleSetByRoleId(List<Long> roleId) {
        if (roleId == null || roleId.isEmpty()) {
            throw new ParamException(ResponseCode.PARAM_ERROR, "角色ID集合不能为空");
        }
        LambdaQueryWrapper<SysRole> sysRoleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        sysRoleLambdaQueryWrapper.in(SysRole::getRoleId, roleId);
        List<SysRole> roles = list(sysRoleLambdaQueryWrapper);
        return roles.stream()
                .map(SysRole::getRoleKey)
                .collect(Collectors.toSet());
    }

    /**
     * 根据角色ID集合获取角色权限字符串集合
     *
     * @param roleId 角色ID集合
     * @return 角色权限字符串集合
     */
    @Override
    public Set<String> getRoleSetByRoleId(Long roleId) {
        LambdaQueryWrapper<SysRole> sysRoleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        sysRoleLambdaQueryWrapper.in(SysRole::getRoleId, roleId);
        List<SysRole> roles = list(sysRoleLambdaQueryWrapper);
        return roles.stream()
                .map(SysRole::getRoleKey)
                .collect(Collectors.toSet());
    }

    /**
     * 根据用户ID查询角色信息
     *
     * @param id 角色ID
     * @return 返回角色信息
     */
    @Override
    public SysRole getRoleInfoById(Long id) {
        SysRole sysRole = getById(id);
        if (sysRole == null) {
            throw new ServiceException(ResponseCode.RESULT_IS_NULL, String.format("ID:【%s】的角色不存在", id));
        }
        return sysRole;
    }

}




