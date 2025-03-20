package cn.zhangchuangla.admin.controller.common;

import cn.zhangchuangla.common.enums.FileUploadMethod;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.system.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "通用接口")
@RequestMapping("/common")
@RestController
public class CommonController {


    private final FileService fileService;

    public CommonController(FileService fileService) {
        this.fileService = fileService;
    }

    /**
     * 通用上传文件
     *
     * @return 文件路径
     */
    @PostMapping("/upload")
    @Operation(summary = "文件上传")
    public AjaxResult upload(@Parameter(name = "文件", required = true, description = "需要上传的文件参数")
                             @RequestParam("file")
                             MultipartFile file) {
        String fileUrl = fileService.specifyUploadFile(file, FileUploadMethod.LOCAL);
        AjaxResult ajax = new AjaxResult();
        ajax.put("url", fileUrl);
        return ajax;
    }
}
