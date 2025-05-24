package cn.zhangchuangla.system.model.request.student;

import cn.zhangchuangla.common.core.base.BasePageRequest;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 学生表测试表查询请求参数
 *
 * @author Chuang
 * @date 2025-05-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "学生表测试表查询请求参数")
public class StudentQueryRequest extends BasePageRequest {

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
