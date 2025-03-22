package cn.zhangchuangla.system.mapper;

import cn.zhangchuangla.system.model.entity.FileUploadRecord;
import cn.zhangchuangla.system.model.request.file.FileUploadRecordRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * @author zhangchuang
 */
public interface FileUploadRecordMapper extends BaseMapper<FileUploadRecord> {

    /**
     * 文件列表
     *
     * @param page    分页对象
     * @param request 请求参数
     * @return 结果
     */
    Page<FileUploadRecord> fileList(Page<FileUploadRecord> page, @Param("request") FileUploadRecordRequest request);
}




