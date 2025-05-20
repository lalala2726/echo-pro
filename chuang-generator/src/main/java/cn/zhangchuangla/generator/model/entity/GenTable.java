package cn.zhangchuangla.generator.model.entity;

import cn.zhangchuangla.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 代码生成业务表
 *
 * @author Chuang
 * <p>
 * created on 2025-05-20 11:01
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "gen_table")
@Data
public class GenTable extends BaseEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 表名称
     */
    private String tableName;

    /**
     * 表描述
     */
    private String tableComment;

    /**
     * 表类型（0单表，1主子表,2树表）
     */
    private Integer tableType;

    /**
     * 生成包路径
     */
    private String packageName;

    /**
     * 生成模块名
     */
    private String moduleName;

    /**
     * 生成业务名
     */
    private String businessName;

    /**
     * 生成功能名
     */
    private String functionName;

    /**
     * 生成功能作者
     */
    private String functionAuthor;

    /**
     * 生成代码方式（0 zip压缩包 1 自定义路径）
     */
    private Integer genType;

    /**
     * 生成路径（不填默认项目路径）
     */
    private String genPath;

    /**
     * 创建者
     */
    private String createBy;
}
