package cn.zhangchuangla.system.service;

import cn.zhangchuangla.system.model.entity.SysPost;
import cn.zhangchuangla.system.model.request.post.SysPostAddRequest;
import cn.zhangchuangla.system.model.request.post.SysPostQueryRequest;
import cn.zhangchuangla.system.model.request.post.SysPostUpdateRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

/**
 * 岗位服务接口
 *
 * @author Chuang
 */
public interface SysPostService extends IService<SysPost> {

    /**
     * 岗位列表
     *
     * @param request 请求参数
     * @return 返回岗位列表
     */
    Page<SysPost> listPost(SysPostQueryRequest request);

    /**
     * 新增岗位
     *
     * @param request 请求参数
     * @return 操作结果
     */
    boolean addPost(SysPostAddRequest request);

    /**
     * 删除岗位，支持批量删除
     *
     * @param ids 岗位ID集合
     * @return 操作结果
     */
    boolean deletePost(List<Long> ids);

    /**
     * 根据ID获取岗位信息
     *
     * @param id 请求参数
     * @return 操作结果
     */
    SysPost getPostById(Long id);

    /**
     * 修改岗位
     *
     * @param request 请求参数
     * @return 操作结果
     */
    boolean updatePost(SysPostUpdateRequest request);

    /**
     * 判断岗位编码是否存在
     *
     * @param postCode 岗位编码
     * @return true存在，false不存在
     */
    boolean isPostCodeExist(@NotBlank(message = "岗位编码不能为空") String postCode);

    /**
     * 判断岗位名称是否存在
     *
     * @param postName 岗位名称
     * @return true存在，false不存在
     */
    boolean isPostNameExist(@NotBlank(message = "岗位名称不能为空") String postName);
}
