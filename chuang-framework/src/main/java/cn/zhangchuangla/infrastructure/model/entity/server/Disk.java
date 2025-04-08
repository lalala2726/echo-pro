package cn.zhangchuangla.infrastructure.model.entity.server;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 磁盘信息
 *
 * @author Chuang
 * <p>
 * created on 2025/3/19 19:47
 */
@Data
@Schema(description = "磁盘信息")
public class Disk {

    /**
     * 磁盘名称
     */
    @Schema(description = "磁盘名称")
    private String name;

    /**
     * 磁盘总大小
     */
    @Schema(description = "磁盘总大小")
    private String total;

    /**
     * 已用大小
     */
    @Schema(description = "已用大小")
    private String used;

    /**
     * 剩余大小
     */
    @Schema(description = "剩余大小")
    private String free;

    /**
     * 使用率
     */
    @Schema(description = "使用率")
    private String usage;

}
