package cn.zhangchuangla.generator.service;

import cn.zhangchuangla.generator.model.entity.GenTable;
import cn.zhangchuangla.generator.model.request.*;
import cn.zhangchuangla.generator.model.vo.DbTableVO;
import cn.zhangchuangla.generator.model.vo.PreviewCodeVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 代码生成业务表 服务接口
 *
 * @author Chuang
 */
public interface GenTableService extends IService<GenTable> {

    /**
     * 查询代码生成业务表列表
     *
     * @param request 查询参数
     * @return 代码生成业务表集合
     */
    Page<GenTable> selectGenTableList(GenTableListRequest request);

    /**
     * 查询数据库表列表
     *
     * @param tableName 表名称
     * @return 数据库表集合
     */
    List<DbTableVO> selectDbTableList(String tableName);

    /**
     * 查询数据库表列表（排除已导入的表）
     *
     * @param tableName 表名称
     * @return 数据库表集合
     */
    List<DbTableVO> selectDbTableListExcludeGenTable(String tableName);

    /**
     * 查询表字段列表
     *
     * @param tableName 表名称
     * @return 表字段集合
     */
    List<Map<String, Object>> selectTableColumnList(String tableName);

    /**
     * 查询表详细信息
     *
     * @param id 表ID
     * @return 表详细信息
     */
    GenTable selectGenTableById(Long id);

    /**
     * 查询表详细信息
     *
     * @param tableName 表名称
     * @return 表详细信息
     */
    GenTable selectGenTableByName(String tableName);

    /**
     * 导入表结构
     *
     * @param request 导入表请求
     * @return 导入结果
     */
    boolean importTable(ImportTableRequest request);

    /**
     * 修改业务表信息
     *
     * @param request 业务表信息
     * @return 结果
     */
    boolean updateGenTable(GenTableRequest request);

    /**
     * 删除业务表
     *
     * @param tableNames 表名称数组
     * @return 结果
     */
    boolean deleteGenTable(String[] tableNames);

    /**
     * 删除业务表
     *
     * @param ids 表ID数组
     * @return 结果
     */
    boolean deleteGenTableByIds(Long[] ids);

    /**
     * 预览代码
     *
     * @param tableName 表名称
     * @return 预览代码列表
     */
    List<PreviewCodeVO> previewCode(String tableName);

    /**
     * 生成代码（下载方式）
     *
     * @param tableName 表名称
     * @return 代码内容
     * @throws IOException IO异常
     */
    byte[] downloadCode(String tableName) throws IOException;

    /**
     * 批量生成代码（下载方式）
     *
     * @param request 批量生成代码请求
     * @return 代码内容
     * @throws IOException IO异常
     */
    byte[] batchGenerateCode(BatchGenCodeRequest request) throws IOException;

    /**
     * 生成代码（自定义路径）
     *
     * @param tableName 表名称
     * @return 结果
     */
    boolean genToPath(String tableName);

    /**
     * 同步数据库
     *
     * @param tableName 表名称
     * @return 结果
     */
    boolean syncDb(String tableName);

    /**
     * 执行SQL脚本
     *
     * @param request SQL请求
     * @return 结果
     */
    boolean executeSql(ExecuteSqlRequest request);
}