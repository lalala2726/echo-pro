package cn.zhangchuangla.system.model.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 用于传递文件上传的参数
 *
 * @author Chuang
 * Created on 2025/3/22 23:07
 */
@Data
@Builder
public class FileUploadByByteDto {

    /**
     * 文件数据
     */
    private byte[] data;

    /**
     * 文件类型
     */
    private String contentType;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 是否是压缩文件
     */
    private boolean isCompress;
}
