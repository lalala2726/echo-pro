package cn.zhangchuangla.system.model.vo.student;

import cn.zhangchuangla.common.excel.annotation.Excel;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 学生表测试表列表视图对象
 *
 * @author Chuang
 * @date 2025-05-24
 */
@Data
@Schema(description = "学生表测试表列表视图对象")
public class StudentListVo {

    /**
     * 主键
     */
    @Schema(description = "主键")
    @Excel(name = "主键", sort = 0)
    private Long id;

    /**
     * 姓名
     */
    @Schema(description = "姓名")
    @Excel(name = "姓名", sort = 1)
    private String name;

    /**
     * 年龄
     */
    @Schema(description = "年龄")
    @Excel(name = "年龄", sort = 2)
    private Integer age;

    /**
     * 性别
     */
    @Schema(description = "性别")
    @Excel(name = "性别", sort = 3, dictType = "system_gender")
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer gender;
}
