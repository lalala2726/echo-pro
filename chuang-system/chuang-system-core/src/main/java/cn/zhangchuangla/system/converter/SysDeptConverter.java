package cn.zhangchuangla.system.converter;

import cn.zhangchuangla.system.model.entity.SysDept;
import cn.zhangchuangla.system.model.request.dept.SysDeptAddRequest;
import cn.zhangchuangla.system.model.request.dept.SysDeptUpdateRequest;
import cn.zhangchuangla.system.model.vo.dept.SysDeptVo;
import org.mapstruct.Mapper;

/**
 * @author Chuang
 * <p>
 * created on 2025/4/16 20:32
 */
@Mapper(componentModel = "spring")
public interface SysDeptConverter {

    /**
     * 将部门添加请求转换为实体类
     *
     * @param request 部门添加请求
     * @return 部门实体类
     */
    SysDept toEntity(SysDeptAddRequest request);

    /**
     * 将部门请求转换为实体类
     *
     * @param request 部门请求
     * @return 部门实体类
     */
    SysDept toEntity(SysDeptUpdateRequest request);

    /**
     * 将部门实体类转换为部门视图对象
     *
     * @param dept 部门实体类
     * @return 部门视图对象
     */
    SysDeptVo toSysDeptVo(SysDept dept);
}
