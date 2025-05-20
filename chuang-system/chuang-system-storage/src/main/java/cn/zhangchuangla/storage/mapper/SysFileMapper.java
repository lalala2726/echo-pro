package cn.zhangchuangla.storage.mapper;

import cn.zhangchuangla.storage.model.entity.SysFile;
import cn.zhangchuangla.storage.model.request.file.SysFileQueryRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * @author Chuang
 */
public interface SysFileMapper extends BaseMapper<SysFile> {

    /**
     * 文件列表
     *
     * @param page    分页对象
     * @param request 请求参数
     * @return 结果
     */
    Page<SysFile> listFileManage(Page<SysFile> page, @Param("request") SysFileQueryRequest request);

    /**
     * 文件回收列表
     *
     * @param sysFileManagementPage 分页对象
     * @param request               请求参数
     * @return 结果
     */
    Page<SysFile> listFileTrash(Page<SysFile> sysFileManagementPage, @Param("request") SysFileQueryRequest request);
}




