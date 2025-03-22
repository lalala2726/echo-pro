package cn.zhangchuangla.admin.controller.system;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.page.TableDataResult;
import cn.zhangchuangla.system.model.entity.FileUploadRecord;
import cn.zhangchuangla.system.model.request.file.FileUploadRecordRequest;
import cn.zhangchuangla.system.service.FileUploadRecordService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhangchuang
 * Created on 2025/3/22 13:11
 */
@RestController
@RequestMapping("/system/fileManage")
public class FileUploadRecordController extends BaseController {

    private final FileUploadRecordService fileUploadRecordService;

    public FileUploadRecordController(FileUploadRecordService fileUploadRecordService) {
        this.fileUploadRecordService = fileUploadRecordService;
    }


    @GetMapping("/list")
    public TableDataResult fileList(FileUploadRecordRequest request) {
        Page<FileUploadRecord> page = fileUploadRecordService.fileList(request);
        return getTableData(page);
    }


}
