package cn.zhangchuangla.system.model.entity;

import cn.zhangchuangla.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典表
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "dictionary")
@Data
@Schema(name = "字典", description = "字典表")
public class Dictionary extends BaseEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键")
    private Long id;

    /**
     * 字典名称
     */
    @Schema(description = "字典名称")
    private String name;

    /**
     * 字典状态
     */
    @Schema(description = "字典状态")
    private Integer status;

    /**
     * 描述
     */
    @Schema(description = "描述")
    private String description;
}
