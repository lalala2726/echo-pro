package cn.zhangchuangla.storage.model.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 文件移入回收站后的信息传输对象
 *
 * @author Chuang
 */
@Data
@Builder
public class FileTrashInfoDTO {

    /**
     * 原始文件在回收站的相对路径
     */
    private String originalTrashPath;
    /**
     * 预览文件（如果存在）在回收站的相对路径
     */
    private String previewTrashPath;
}
