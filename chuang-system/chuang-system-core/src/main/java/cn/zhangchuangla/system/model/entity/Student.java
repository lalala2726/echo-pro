package cn.zhangchuangla.system.model.entity;

import cn.zhangchuangla.common.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


/**
 * 学生表测试表实体
 *
 * @author Chuang
 * @date 2025-05-24
 */
@Data
@TableName("student")
public class Student {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    @Excel(name = "主键", sort = 0)
    private Long id;

    /**
     * 姓名
     */
    @Excel(name = "姓名", sort = 1)
    private String name;

    /**
     * 年龄
     */
    @Excel(name = "年龄", sort = 2)
    private Integer age;

    /**
     * 性别
     */
    @Excel(name = "性别", sort = 3, dictType = "system_gender")
    private Integer gender;


}
