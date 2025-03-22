package cn.zhangchuangla.system.mapper;

import cn.zhangchuangla.system.model.entity.FileManagement;
import cn.zhangchuangla.system.model.request.file.FileManagementListRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * @author zhangchuang
 */
public interface FileManagementMapper extends BaseMapper<cn.zhangchuangla.system.model.entity.FileManagement> {

    /**
     * 文件列表
     *
     * @param page    分页对象
     * @param request 请求参数
     * @return 结果
     */
     Page<FileManagement> fileList(Page<FileManagement> page, @Param("request") FileManagementListRequest request);
}




