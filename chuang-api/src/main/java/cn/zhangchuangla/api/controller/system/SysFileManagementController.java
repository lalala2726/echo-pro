package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.system.service.FileManagementService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhangchuang
 * Created on 2025/3/22 13:11
 */
@RestController
@RequestMapping("/system/file/management")
@Tag(name = "文件管理", description = "管理系统所上传的文件")
public class SysFileManagementController extends BaseController {

    private final FileManagementService fileManagementService;


    @Autowired
    public SysFileManagementController(FileManagementService fileManagementService) {
        this.fileManagementService = fileManagementService;
    }
}
