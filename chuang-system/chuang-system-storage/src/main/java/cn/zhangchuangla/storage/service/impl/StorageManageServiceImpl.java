package cn.zhangchuangla.storage.service.impl;

import cn.zhangchuangla.storage.mapper.SysFileMapper;
import cn.zhangchuangla.storage.model.entity.FileRecord;
import cn.zhangchuangla.storage.model.request.file.FileRecordQueryRequest;
import cn.zhangchuangla.storage.service.StorageManageService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 文件管理服务实现类
 *
 * @author Chuang
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class StorageManageServiceImpl extends ServiceImpl<SysFileMapper, FileRecord>
        implements StorageManageService {

    private final SysFileMapper sysFileMapper;

    /**
     * 保存文件信息
     *
     * @param fileInfo 文件信息
     */
    @Override
    public void saveFileInfo(FileRecord fileInfo) {
        save(fileInfo);
    }

    /**
     * 获取文件列表
     *
     * @param request 查询条件
     * @return 文件列表
     */
    @Override
    public Page<FileRecord> listFileManage(FileRecordQueryRequest request) {
        Page<FileRecord> page = new Page<>(request.getPageNum(), request.getPageSize());
        return sysFileMapper.listFileManage(page, request);
    }

    /**
     * 获取回收站文件列表
     *
     * @param request 查询条件
     * @return 文件列表
     */
    @Override
    public Page<FileRecord> listFileTrashManage(FileRecordQueryRequest request) {
        Page<FileRecord> page = new Page<>(request.getPageNum(), request.getPageSize());
        return sysFileMapper.listFileTrashManage(page, request);
    }

}




