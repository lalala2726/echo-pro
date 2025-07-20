package cn.zhangchuangla.storage.mapper;

import cn.zhangchuangla.storage.model.entity.StorageFile;
import cn.zhangchuangla.storage.model.request.file.FileRecordQueryRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * @author Chuang
 */
public interface StorageFileMapper extends BaseMapper<StorageFile> {

    /**
     * 文件列表
     *
     * @param page    分页对象
     * @param request 请求参数
     * @return 结果
     */
    Page<StorageFile> listFileManage(Page<StorageFile> page, @Param("request") FileRecordQueryRequest request);


    /**
     * 回收站文件列表
     *
     * @param page    分页对象
     * @param request 请求参数
     * @return 结果
     */
    Page<StorageFile> listFileTrashManage(Page<StorageFile> page, @Param("request") FileRecordQueryRequest request);

}




