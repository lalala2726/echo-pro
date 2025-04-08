package cn.zhangchuangla.system.service;

import cn.zhangchuangla.common.model.dto.FileTransferDto;
import cn.zhangchuangla.system.model.entity.SysFileManagement;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * author zhangchuang
 */
public interface FileManagementService extends IService<SysFileManagement> {

    /**
     * 保存文件信息
     *
     * @param fileTransferDto 文件上传结果
     */
    void saveFileInfo(FileTransferDto fileTransferDto);
}
