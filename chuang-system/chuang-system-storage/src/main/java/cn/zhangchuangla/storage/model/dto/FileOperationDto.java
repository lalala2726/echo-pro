package cn.zhangchuangla.storage.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件操作传输对象
 *
 * @author Chuang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileOperationDto {

    /**
     * 文件ID
     */
    private Long fileId;

    /**
     * 原始文件相对路径，存储在服务器上的路径
     */
    private String originalRelativePath;

    /**
     * 原始文件在回收站的相对路径
     */
    private String originalTrashPath;

    /**
     * 预览文件（如果存在）在回收站的相对路径
     */
    private String previewTrashPath;

    /**
     * 预览文件相对路径，存储在服务器上的路径
     */
    private String previewRelativePath;


}
