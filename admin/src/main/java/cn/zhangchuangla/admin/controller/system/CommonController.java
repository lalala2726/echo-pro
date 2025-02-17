package cn.zhangchuangla.admin.controller.system;

import cn.zhangchuangla.common.enums.FileUploadMethod;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.system.service.FileService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/common")
@RestController
public class CommonController {


    private final FileService fileService;

    public CommonController(FileService fileService) {
        this.fileService = fileService;
    }

    /**
     * oss上传文件
     *
     * @return 文件路径
     */
    @PostMapping("/upload")
    public AjaxResult upload(@RequestParam("file") MultipartFile file) {
        String fileUrl = fileService.specifyUploadFile(file, FileUploadMethod.ALIYUN_OSS);
        AjaxResult ajax = new AjaxResult();
        ajax.put("url", fileUrl);
        return ajax;
    }


}
