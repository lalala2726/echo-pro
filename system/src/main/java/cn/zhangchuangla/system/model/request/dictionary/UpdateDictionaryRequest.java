package cn.zhangchuangla.system.model.request.dictionary;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 字典表
 */
@Data
public class UpdateDictionaryRequest {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    /**
     * 字典名称
     */
    @Schema(description = "字典名称")
    private String name;

    /**
     * 描述
     */
    @Schema(description = "描述")
    private String description;
}
