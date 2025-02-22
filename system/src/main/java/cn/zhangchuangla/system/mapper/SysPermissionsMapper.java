package cn.zhangchuangla.system.mapper;

import cn.zhangchuangla.system.model.entity.SysPermissions;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author zhangchuang
 */
@Mapper
public interface SysPermissionsMapper extends BaseMapper<SysPermissions> {
    List<SysPermissions> selectPermissionsByUserId(Long userId);
}




