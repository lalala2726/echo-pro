package cn.zhangchuangla.system.service;

import cn.zhangchuangla.common.model.dto.FileTransferDto;
import cn.zhangchuangla.system.model.entity.SysFileManagement;
import cn.zhangchuangla.system.model.request.file.manage.SysFileManagementListRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * author zhangchuang
 */
public interface SysFileManagementService extends IService<SysFileManagement> {

    /**
     * 保存文件信息
     *
     * @param fileTransferDto 文件上传结果
     */
    void saveFileInfo(FileTransferDto fileTransferDto);

    /**
     * 查询文件列表
     *
     * @param request 查询参数
     * @return 分页结果
     */
    Page<SysFileManagement> listFileManage(SysFileManagementListRequest request);
}
