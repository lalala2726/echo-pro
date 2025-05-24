package cn.zhangchuangla.generator.service;

import cn.zhangchuangla.generator.config.GenConfig;
import cn.zhangchuangla.generator.model.entity.DatabaseTable;
import cn.zhangchuangla.generator.model.entity.GenTable;
import cn.zhangchuangla.generator.model.entity.GenTableColumn;
import cn.zhangchuangla.generator.model.request.DatabaseTableQueryRequest;
import cn.zhangchuangla.generator.model.request.GenConfigUpdateRequest;
import cn.zhangchuangla.generator.model.request.GenTableQueryRequest;
import cn.zhangchuangla.generator.model.request.GenTableUpdateRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * @author Chuang
 *         <p>
 *         created on 2025-05-20 11:01
 */
public interface GenTableService extends IService<GenTable> {

    /**
     * 分页查询低代码表
     *
     * @param request 查询参数
     * @return 低代码表列表
     */
    Page<GenTable> listGenTable(GenTableQueryRequest request);

    /**
     * 查询当前数据库表结构
     *
     * @param databaseTableQueryRequest 查询参数
     * @return 数据库表结构
     */
    Page<DatabaseTable> listDatabaseTables(DatabaseTableQueryRequest databaseTableQueryRequest);

    /**
     * 导入当前数据库中的表到低代码表中 （支持批量导入）
     *
     * @param tableNames 表名称集合
     * @return 导入结果
     */
    boolean importTable(List<String> tableNames);

    /**
     * 查询配置信息
     *
     * @return 配置信息
     */
    GenConfig getConfigInfo();

    /**
     * 修改配置信息
     *
     * @param request 配置信息
     * @return 修改结果
     */
    boolean updateConfigInfo(GenConfigUpdateRequest request);

    /**
     * 根据ID查询低代码表信息
     *
     * @param id id
     * @return 返回详情
     */
    GenTable getGenTableById(Long id);

    /**
     * 根据表名查询表字段配置
     *
     * @param tableName 表名
     * @return 表字段配置列表
     */
    List<GenTableColumn> selectGenTableColumnListByTableName(String tableName);

    /**
     * 预览代码
     *
     * @param tableName 表名
     * @return 代码预览列表
     */
    Map<String, String> previewCode(String tableName);

    /**
     * 生成代码（下载方式）
     *
     * @param tableName 表名
     * @return 代码压缩包
     */
    byte[] downloadCode(String tableName);

    /**
     * 批量下载代码
     *
     * @param tableNames 表名列表
     * @return 代码压缩包
     */
    byte[] batchDownloadCode(List<String> tableNames);

    /**
     * 更新低代码表信息
     *
     * @param request 更新请求
     * @return 更新结果
     */
    boolean updateGenTable(GenTableUpdateRequest request);

    /**
     * 批量设置模板类型
     *
     * @param tableIds     表ID列表
     * @param templateType 模板类型
     * @return 操作结果
     */
    boolean batchSetTemplateType(List<Long> tableIds, String templateType);

    /**
     * 删除低代码表，支持批量删除
     *
     * @param tableIds 低代码表ID集合
     * @return 操作结果
     */
    boolean deleteGenTable(List<Long> tableIds);

    /**
     * 同步数据库结构
     *
     * @param tableName 表名
     * @return 操作结果
     */
    boolean syncDb(String tableName);

    /**
     * 批量同步数据库结构
     *
     * @param tableNames 表名列表
     * @return 操作结果
     */
    boolean batchSyncDb(List<String> tableNames);

    /**
     * 查询所有表结构
     *
     * @return 表结构列表
     */
    List<DatabaseTable> listAllTable();

    /**
     * 查询数据库表的字段信息
     *
     * @param tableName 表名
     * @return 字段信息列表
     */
    List<GenTableColumn> selectDbTableColumns(String tableName);

}
