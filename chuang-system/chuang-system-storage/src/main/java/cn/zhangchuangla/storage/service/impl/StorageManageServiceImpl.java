package cn.zhangchuangla.storage.service.impl;

import cn.zhangchuangla.storage.mapper.SysFileMapper;
import cn.zhangchuangla.storage.model.entity.FileRecord;
import cn.zhangchuangla.storage.service.StorageManageService;
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


    @Override
    public void saveFileInfo(FileRecord fileInfo) {
        save(fileInfo);
    }

}




