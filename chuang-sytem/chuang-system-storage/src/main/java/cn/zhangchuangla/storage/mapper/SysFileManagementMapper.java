package cn.zhangchuangla.storage.mapper;

import cn.zhangchuangla.storage.model.entity.SysFileManagement;
import cn.zhangchuangla.storage.model.request.manage.SysFileManagementListRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * @author zhangchuang
 */
public interface SysFileManagementMapper extends BaseMapper<SysFileManagement> {

    /**
     * 文件列表
     *
     * @param page    分页对象
     * @param request 请求参数
     * @return 结果
     */
    Page<SysFileManagement> listFileManage(Page<SysFileManagement> page, @Param("request") SysFileManagementListRequest request);

    /**
     * 文件回收列表
     *
     * @param sysFileManagementPage 分页对象
     * @param request               请求参数
     * @return 结果
     */
    Page<SysFileManagement> listFileTrash(Page<SysFileManagement> sysFileManagementPage, @Param("request") SysFileManagementListRequest request);
}




