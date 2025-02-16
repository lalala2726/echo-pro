package cn.zhangchuangla.admin.controller.system;

import cn.zhangchuangla.common.result.AjaxResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/common")
@RestController
public class CommonController {


    /**
     * oss上传文件
     *
     * @return 文件路径
     */
    @PostMapping("/oss/upload")
    public AjaxResult upload() {
        return null;
    }
}
