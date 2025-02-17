package cn.zhangchuangla.system.service;

import cn.zhangchuangla.common.enums.FileUploadMethod;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Chuang
 * <p>
 * created on 2025/2/17 20:59
 */
public interface FileService {


    /**
     * 自动选择上传文件的方式
     *
     * @param file 文件
     * @return URL
     */
    String autoUploadFile(MultipartFile file);


    /**
     * 指定上传文件的方式
     *
     * @param file 文件
     * @return URL
     */
    String specifyUploadFile(MultipartFile file, FileUploadMethod method);


}
