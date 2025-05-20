package cn.zhangchuangla.generator.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 代码预览视图对象
 *
 * @author zhangchuang
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "代码预览视图对象")
public class CodePreviewVo {

    /**
     * 文件名
     */
    @Schema(description = "文件名")
    private String fileName;

    /**
     * 代码内容
     */
    @Schema(description = "代码内容")
    private String content;

    /**
     * 文件类型
     */
    @Schema(description = "文件类型（entity, mapper, service, serviceImpl, controller, mapperXml, vo, request）")
    private String fileType;
} 