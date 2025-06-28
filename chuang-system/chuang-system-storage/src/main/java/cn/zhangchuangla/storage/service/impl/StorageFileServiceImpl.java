package cn.zhangchuangla.storage.service.impl;

import cn.zhangchuangla.storage.mapper.SysFileMapper;
import cn.zhangchuangla.storage.model.entity.SysFile;
import cn.zhangchuangla.storage.service.StorageFileService;
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
public class StorageFileServiceImpl extends ServiceImpl<SysFileMapper, SysFile>
        implements StorageFileService {


}




