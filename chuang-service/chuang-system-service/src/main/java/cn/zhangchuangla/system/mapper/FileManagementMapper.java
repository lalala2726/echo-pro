package cn.zhangchuangla.system.mapper;

import cn.zhangchuangla.system.model.entity.SysFileManagement;
import cn.zhangchuangla.system.model.request.file.FileManagementListRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * @author zhangchuang
 */
public interface FileManagementMapper extends BaseMapper<SysFileManagement> {

    /**
     * 文件列表
     *
     * @param page    分页对象
     * @param request 请求参数
     * @return 结果
     */
    Page<SysFileManagement> fileList(Page<SysFileManagement> page, @Param("request") FileManagementListRequest request);
}




