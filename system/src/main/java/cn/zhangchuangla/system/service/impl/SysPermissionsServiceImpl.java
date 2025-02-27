package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.utils.ParamsUtils;
import cn.zhangchuangla.common.utils.StringUtils;
import cn.zhangchuangla.system.mapper.SysPermissionsMapper;
import cn.zhangchuangla.system.model.entity.SysPermissions;
import cn.zhangchuangla.system.service.SysPermissionsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
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

    public SysPermissionsServiceImpl(SysPermissionsMapper sysPermissionsMapper) {
        this.sysPermissionsMapper = sysPermissionsMapper;
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
        ParamsUtils.paramNotIsZeroOrBlank(id, "用户ID不能小于等于零");
        List<SysPermissions> permissionsList = sysPermissionsMapper.getPermissionsByUserId(id);
        return permissionsList.stream()
                .map(SysPermissions::getPermissionsKey)
                .collect(Collectors.toSet());
    }
}




