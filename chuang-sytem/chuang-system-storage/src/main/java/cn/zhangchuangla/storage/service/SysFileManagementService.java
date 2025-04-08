package cn.zhangchuangla.storage.service;

import cn.zhangchuangla.common.model.dto.FileTransferDto;
import cn.zhangchuangla.storage.model.entity.SysFileManagement;
import cn.zhangchuangla.storage.model.request.manage.SysFileManagementListRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

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

    /**
     * 删除文件
     *
     * @param ids           文件id列表
     * @param isPermanently true代表永久删除文件，false将会转移到回收站
     * @return true: 删除成功, false: 删除失败
     */
    boolean removeFile(List<Long> ids, final Boolean isPermanently);


    /**
     * 根据id查询文件信息
     *
     * @param id 文件id
     * @return 文件信息
     */
    SysFileManagement getFileManageById(Long id);

    /**
     * 查询回收站文件列表
     *
     * @param request 查询参数
     * @return 分页结果
     */
    Page<SysFileManagement> listFileTrash(SysFileManagementListRequest request);

    /**
     * 恢复文件
     *
     * @param id 文件id
     * @return true: 恢复成功, false: 恢复失败
     */
    boolean recoverFile(Long id);
}
