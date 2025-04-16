package cn.zhangchuangla.system.converter;

import cn.zhangchuangla.system.model.entity.SysPost;
import cn.zhangchuangla.system.model.request.post.SysPostAddRequest;
import cn.zhangchuangla.system.model.request.post.SysPostUpdateRequest;
import org.mapstruct.Mapper;

/**
 * @author Chuang
 * <p>
 * created on 2025/4/16 20:33
 */
@Mapper(componentModel = "spring")
public interface SysPostConverter {

    /**
     * 将岗位添加请求转换为实体类
     *
     * @param request 岗位添加请求
     * @return 岗位实体类
     */
    SysPost toEntity(SysPostAddRequest request);

    /**
     * 将岗位更新请求转换为实体类
     *
     * @param request 岗位更新请求
     * @return 岗位实体类
     */
    SysPost toEntity(SysPostUpdateRequest request);
}
