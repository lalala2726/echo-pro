package cn.zhangchuangla.system.model.request.student;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * 学生表测试表修改请求参数
 *
 * @author Chuang
 * @date 2025-05-24
 */
@Data
@Schema(description = "学生表测试表修改请求参数")
public class StudentUpdateRequest {

    /**
     * 主键
     */
    @Schema(description = "主键")
    @NotNull(message = "主键不能为空")
    private Long id;

    /**
     * 姓名
     */
    @Schema(description = "姓名")
    private String name;

    /**
     * 年龄
     */
    @Schema(description = "年龄")
    private Integer age;

    /**
     * 性别
     */
    @Schema(description = "性别")
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer gender;
}
