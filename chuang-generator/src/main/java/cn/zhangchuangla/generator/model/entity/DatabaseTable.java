package cn.zhangchuangla.generator.model.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用于数据库表
 *
 * @author Chuang
 * <p>
 * created on 2025/5/20 13:22
 */
@Data
public class DatabaseTable {

    /**
     * 表名
     */
    private String tableName;

    /**
     * 表注释
     */
    private String tableComment;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

    /**
     * 表目录
     */
    private String tableCatalog;

    /**
     * 表模式
     */
    private String tableSchema;

    /**
     * 表类型
     */
    private String tableType;

    /**
     * 引擎
     */
    private String engine;

    /**
     * 版本
     */
    private Long version;

    /**
     * 行格式
     */
    private String rowFormat;

    /**
     * 表行数
     */
    private Long tableRows;

    /**
     * 平均行长度
     */
    private Long avgRowLength;

    /**
     * 数据长度
     */
    private Long dataLength;

    /**
     * 最大数据长度
     */
    private Long maxDataLength;

    /**
     * 索引长度
     */
    private Long indexLength;

    /**
     * 数据碎片
     */
    private Long dataFree;

    /**
     * 自增ID
     */
    private Long autoIncrement;

    /**
     * 检查时间
     */
    private LocalDateTime checkTime;

    /**
     * 表校对规则
     */
    private String tableCollation;

    /**
     * 校验和
     */
    private Long checksum;

    /**
     * 创建选项
     */
    private String createOptions;
}
