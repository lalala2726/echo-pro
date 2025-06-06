package cn.zhangchuangla.api.controller.tool;

import org.springframework.beans.BeanUtils;
import cn.zhangchuangla.common.core.core.controller.BaseController;
import cn.zhangchuangla.common.core.model.entity.KeyValue;
import cn.zhangchuangla.common.core.result.AjaxResult;
import cn.zhangchuangla.common.core.result.TableDataResult;
import cn.zhangchuangla.framework.annotation.Anonymous;
import cn.zhangchuangla.generator.config.GenConfig;
import cn.zhangchuangla.generator.enums.FileType;
import cn.zhangchuangla.generator.enums.TemplateTypeEnum;
import cn.zhangchuangla.generator.model.entity.DatabaseTable;
import cn.zhangchuangla.generator.model.entity.GenTable;
import cn.zhangchuangla.generator.model.entity.GenTableColumn;
import cn.zhangchuangla.generator.model.request.*;
import cn.zhangchuangla.generator.model.vo.*;
import cn.zhangchuangla.generator.service.GenTableService;
import cn.zhangchuangla.generator.utils.GenUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 代码生成控制器
 *
 * @author Chuang
 */
@Slf4j
@RestController
@RequestMapping("/tool/gen")
@Tag(name = "代码生成", description = "提供前后端代码快速生成相关接口")
@RequiredArgsConstructor
public class GenController extends BaseController {

    private final GenTableService genTableService;

    /**
     * 分页查询低代码表
     *
     * @return 分页结果
     */
    @Operation(summary = "分页查询低代码表")
    @PreAuthorize("@ss.hasPermission('tool:gen:list')")
    @GetMapping("/list")
    public AjaxResult<TableDataResult> listGenTable(@Parameter(description = "列表查询参数") GenTableQueryRequest request) {
        Page<GenTable> page = genTableService.listGenTable(request);
        List<GenTableListVo> genTableListVos = copyListProperties(page, GenTableListVo.class);
        return getTableData(page, genTableListVos);
    }

    /**
     * 根据ID查询低代码详情
     *
     * @param id 表ID
     * @return 表信息
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询低代码详情")
    @PreAuthorize("@ss.hasPermission('tool:gen:query')")
    public AjaxResult<GenTableVo> getGenTableInfo(@Parameter(description = "低代码表ID") @PathVariable("id") Long id) {
        checkParam(id == null, "id不能为空");
        GenTable genTable = genTableService.getGenTableById(id);
        GenTableVo genTableVo = new GenTableVo();
        BeanUtils.copyProperties(genTable, genTableVo);
        return success(genTableVo);
    }

    /**
     * 列出当前数据库中的表信息
     *
     * @param databaseTableQueryRequest 查询参数
     * @return 分页结果
     */
    @GetMapping("/db/list")
    @Operation(summary = "查询数据库表结构")
    @PreAuthorize("@ss.hasPermission('tool:gen:list')")
    public AjaxResult<TableDataResult> listDatabaseTables(
            @Parameter(description = "查询参数") DatabaseTableQueryRequest databaseTableQueryRequest) {
        Page<DatabaseTable> page = genTableService.listDatabaseTables(databaseTableQueryRequest);
        List<DatabaseTableVo> databaseTableVos = copyListProperties(page, DatabaseTableVo.class);
        return getTableData(page, databaseTableVos);
    }

    /**
     * 查询所有表
     *
     * @return 表信息
     */
    @Operation(summary = "查询所有表")
    @GetMapping("/db/list/all")
    @PreAuthorize("@ss.hasPermission('tool:gen:list')")
    public AjaxResult<List<DatabaseTableVo>> listAllTable() {
        List<DatabaseTable> databaseTables = genTableService.listAllTable();
        List<DatabaseTableVo> databaseTableVos = copyListProperties(databaseTables, DatabaseTableVo.class);
        return success(databaseTableVos);
    }

    /**
     * 根据表名称查询表基本字段信息
     *
     * @param tableName 表名称
     * @return 表信息
     */
    @Operation(summary = "根据表名称查询表基本字段信息")
    @GetMapping("/db/{tableName}")
    public AjaxResult<List<KeyValue>> getTableInfo(@PathVariable("tableName") String tableName) {
        List<GenTableColumn> genTableColumns = genTableService.selectGenTableColumnListByTableName(tableName);
        List<KeyValue> keyValues = new ArrayList<>();
        genTableColumns.forEach(genTableColumn -> keyValues.add(new KeyValue(genTableColumn.getColumnName(), genTableColumn.getColumnComment())));
        return success(keyValues);
    }


    /**
     * 导入数据库表结构
     *
     * @param tableNames 表名称
     * @return 操作结果
     */
    @Operation(summary = "导入数据库表结构")
    @PreAuthorize("@ss.hasPermission('tool:gen:import')")
    @PostMapping("/importTable")
    public AjaxResult<Void> importTable(
            @Parameter(description = "数据库中表的名称列表") @RequestBody List<String> tableNames) {
        checkParam(tableNames == null || tableNames.isEmpty(), "表名称不能为空！");
        boolean result = genTableService.importTable(tableNames);
        return toAjax(result);
    }


    /**
     * 查询配置信息
     *
     * @return 配置信息
     */
    @Operation(summary = "查询配置信息")
    @GetMapping("/config")
    public AjaxResult<GenConfig> getConfigInfo() {
        GenConfig genConfig = genTableService.getConfigInfo();
        return success(genConfig);
    }

    /**
     * 修改配置信息
     *
     * @return 操作结果
     */
    @Operation(summary = "修改配置信息")
    @PutMapping("/config")
    public AjaxResult<Void> updateConfigInfo(
            @Parameter(description = "新的配置") @RequestBody @Validated GenConfigUpdateRequest request) {
        boolean result = genTableService.updateConfigInfo(request);
        return toAjax(result);
    }

    /**
     * 查询表字段配置
     *
     * @param tableName 表名称
     * @return 表字段配置
     */
    @Operation(summary = "查询表字段配置")
    @PreAuthorize("@ss.hasPermission('tool:gen:query')")
    @GetMapping("/column/{tableName}")
    public AjaxResult<List<GenTableColumnVo>> columnList(
            @Parameter(description = "低代码中表的名称") @PathVariable("tableName") String tableName) {
        checkParam(tableName == null || tableName.isEmpty(), "表名称不能为空");

        // 查询表字段配置
        List<GenTableColumn> columnList = genTableService.selectGenTableColumnListByTableName(tableName);
        // 转换为VO对象
        List<GenTableColumnVo> columnVoList = copyListProperties(columnList, GenTableColumnVo.class);

        return success(columnVoList);
    }

    /**
     * 修改低代码表配置
     *
     * @param request 更新请求
     * @return 操作结果
     */
    @Operation(summary = "修改低代码表配置")
    @PreAuthorize("@ss.hasPermission('tool:gen:update')")
    @PutMapping
    public AjaxResult<Void> update(
            @Parameter(description = "修改后的参数") @RequestBody @Validated GenTableUpdateRequest request) {
        log.info("开始更新低代码表配置，表ID: {}, 表名: {}", request.getTableId(), request.getTableName());

        // 参数验证
        checkParam(request.getTableId() == null, "表ID不能为空");
        checkParam(request.getTableName() == null || request.getTableName().isEmpty(), "表名称不能为空");

        // 如果包含字段信息，验证字段信息
        if (request.getColumns() != null && !request.getColumns().isEmpty()) {
            for (ColumnUpdateRequest column : request.getColumns()) {
                if (column.getColumnId() == null) {
                    log.warn("字段ID为空，跳过更新: {}", column.getColumnName());
                }
            }
        }

        // 执行更新
        boolean result = genTableService.updateGenTable(request);

        log.info("低代码表配置更新{}, 表ID: {}, 表名: {}",
                result ? "成功" : "失败", request.getTableId(), request.getTableName());

        return toAjax(result);
    }

    /**
     * 批量设置模板类型
     *
     * @param request 批量设置请求
     * @return 操作结果
     */
    @Operation(summary = "批量设置模板类型")
    @PreAuthorize("@ss.hasPermission('tool:gen:update')")
    @PostMapping("/template/batch")
    public AjaxResult<Void> batchSetTemplateType(@RequestBody BatchTemplateRequest request) {
        checkParam(request.getTableIds() == null || request.getTableIds().isEmpty(), "表ID不能为空");
        checkParam(request.getTemplateType() == null || request.getTemplateType().isEmpty(), "模板类型不能为空");

        // 验证模板类型
        TemplateTypeEnum.getByCode(request.getTemplateType());

        // 批量更新
        boolean result = genTableService.batchSetTemplateType(request.getTableIds(), request.getTemplateType());
        return toAjax(result);
    }

    /**
     * 删除低代码表
     *
     * @param tableIds 表ID数组
     * @return 操作结果
     */
    @Operation(summary = "删除低代码表")
    @PreAuthorize("@ss.hasPermission('tool:gen:remove')")
    @DeleteMapping("/{tableIds}")
    public AjaxResult<Void> deleteGenTable(
            @Parameter(description = "低代码表ID") @PathVariable("tableIds") List<Long> tableIds) {
        checkParam(tableIds == null, "ID不能为空");
        boolean result = genTableService.deleteGenTable(tableIds);
        return toAjax(result);
    }

    /**
     * 预览代码
     *
     * @param tableName 表名称
     * @return 预览数据
     */
    @Operation(summary = "预览代码")
    @PreAuthorize("@ss.hasPermission('tool:gen:preview')")
    @GetMapping("/preview/{tableName}")
    public AjaxResult<List<CodePreviewVo>> preview(
            @Parameter(description = "需要预览的表名称") @PathVariable("tableName") String tableName) {
        checkParam(tableName == null || tableName.isEmpty(), "表名称不能为空");

        // 获取预览代码
        Map<String, String> codeMap = genTableService.previewCode(tableName);

        // 转换为前端需要的格式
        List<CodePreviewVo> previewList = new ArrayList<>();

        codeMap.forEach((fileName, content) -> {
            // 获取文件类型
            FileType fileType = GenUtils.getFileType(fileName);

            // 获取简化文件名
            String simpleFileName = GenUtils.getSimpleFileName(fileName);

            // 添加到预览列表
            CodePreviewVo previewVo = CodePreviewVo.builder()
                    .fileName(simpleFileName)
                    .content(content)
                    .fileType(fileType.getCode())
                    .build();

            previewList.add(previewVo);
        });

        return success(previewList);
    }

    /**
     * 批量预览代码
     *
     * @param request 批量预览请求
     * @return 预览数据
     */
    @Operation(summary = "批量预览代码")
    @PreAuthorize("@ss.hasPermission('tool:gen:preview')")
    @PostMapping("/preview/batch")
    public AjaxResult<Map<String, List<CodePreviewVo>>> batchPreview(@RequestBody BatchPreviewRequest request) {
        checkParam(request.getTableNames() == null || request.getTableNames().isEmpty(), "表名称不能为空");

        Map<String, List<CodePreviewVo>> result = new HashMap<>();

        for (String tableName : request.getTableNames()) {
            // 获取预览代码
            Map<String, String> codeMap = genTableService.previewCode(tableName);

            // 转换为前端需要的格式
            List<CodePreviewVo> previewList = new ArrayList<>();

            codeMap.forEach((fileName, content) -> {
                // 获取文件类型
                FileType fileType = GenUtils.getFileType(fileName);

                // 获取简化文件名
                String simpleFileName = GenUtils.getSimpleFileName(fileName);

                // 添加到预览列表
                CodePreviewVo previewVo = CodePreviewVo.builder()
                        .fileName(simpleFileName)
                        .content(content)
                        .fileType(fileType.getCode())
                        .build();

                previewList.add(previewVo);
            });

            result.put(tableName, previewList);
        }

        return success(result);
    }

    /**
     * 下载代码
     *
     * @param tableName 表名称
     */
    @Operation(summary = "下载代码")
    @GetMapping("/download/{tableName}")
    @Anonymous
    public void download(HttpServletResponse response,
                         @Parameter(description = "需要下载的表名称") @PathVariable("tableName") String tableName) {
        // 参数验证
        checkParam(tableName == null || tableName.isEmpty(), "表名称不能为空");

        // 生成代码
        byte[] data = genTableService.downloadCode(tableName);

        // 设置响应头
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        try {
            // 设置文件名，确保使用UTF-8编码避免中文问题
            String fileName = tableName + "_code.zip";
            String encodedFileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"");

            // 设置内容长度
            response.setContentLength(data.length);

            // 禁止缓存
            response.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
            response.setHeader(HttpHeaders.PRAGMA, "no-cache");
            response.setDateHeader(HttpHeaders.EXPIRES, 0);

            // 写入响应体
            response.getOutputStream().write(data);
            response.getOutputStream().flush();

            log.info("正在下载代码，表名：{}，文件大小：{} 字节", tableName, data.length);
        } catch (Exception e) {
            log.error("下载代码失败，表名：{}", tableName, e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 批量下载代码
     *
     * @param request 批量下载请求
     */
    @Operation(summary = "批量下载代码")
    @PostMapping("/download/batch")
    @Anonymous
    public void batchDownload(HttpServletResponse response, @RequestBody BatchDownloadRequest request) {
        // 参数验证
        checkParam(request.getTableNames() == null || request.getTableNames().isEmpty(), "表名称不能为空");

        try {
            // 批量生成代码并打包
            byte[] data = genTableService.batchDownloadCode(request.getTableNames());

            // 设置响应头
            response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
            String fileName = "batch_code_" + System.currentTimeMillis() + ".zip";
            String encodedFileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"");

            response.setContentLength(data.length);
            response.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
            response.setHeader(HttpHeaders.PRAGMA, "no-cache");
            response.setDateHeader(HttpHeaders.EXPIRES, 0);

            response.getOutputStream().write(data);
            response.getOutputStream().flush();

            log.info("正在批量下载代码，表数量：{}，文件大小：{} 字节",
                    request.getTableNames().size(), data.length);
        } catch (Exception e) {
            log.error("批量下载代码失败，表名：{}", request.getTableNames(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 同步数据库结构
     *
     * @param tableName 表名称
     * @return 操作结果
     */
    @Operation(summary = "同步数据库结构")
    @PreAuthorize("@ss.hasPermission('tool:gen:sync')")
    @PostMapping("/syncDb/{tableName}")
    public AjaxResult<Void> syncDb(@Parameter(description = "需要同步表的名称") @PathVariable("tableName") String tableName) {
        checkParam(tableName == null || tableName.isEmpty(), "表名称不能为空");

        boolean result = genTableService.syncDb(tableName);
        return toAjax(result);
    }

    /**
     * 批量同步数据库结构
     *
     * @param tableNames 表名称列表
     * @return 操作结果
     */
    @Operation(summary = "批量同步数据库结构")
    @PreAuthorize("@ss.hasPermission('tool:gen:sync')")
    @PostMapping("/syncDb/batch")
    public AjaxResult<Void> batchSyncDb(@Parameter(description = "需要同步的表名称列表") @RequestBody List<String> tableNames) {
        checkParam(tableNames == null || tableNames.isEmpty(), "表名称不能为空");

        boolean result = genTableService.batchSyncDb(tableNames);
        return toAjax(result);
    }
}
