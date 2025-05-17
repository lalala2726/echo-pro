package cn.zhangchuangla.api.controller.generator;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.enums.BusinessType;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.common.result.TableDataResult;
import cn.zhangchuangla.framework.annotation.OperationLog;
import cn.zhangchuangla.generator.config.GenConfig;
import cn.zhangchuangla.generator.model.entity.GenTable;
import cn.zhangchuangla.generator.model.request.*;
import cn.zhangchuangla.generator.model.vo.DbTableVO;
import cn.zhangchuangla.generator.model.vo.PreviewCodeVO;
import cn.zhangchuangla.generator.service.GenTableService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 代码生成控制器
 *
 * @author Chuang
 */
@RestController
@RequestMapping("/tool/gen")
@Tag(name = "代码生成")
@RequiredArgsConstructor
public class GenController extends BaseController {

    private final GenTableService genTableService;

    /**
     * 查询代码生成列表
     */
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermission('tool:gen:list')")
    @Operation(summary = "查询代码生成列表")
    public AjaxResult<TableDataResult> list(
            @Parameter(description = "查询参数") @Validated @ParameterObject GenTableListRequest request) {
        Page<GenTable> page = genTableService.selectGenTableList(request);
        return getTableData(page, page.getRecords());
    }

    /**
     * 获取代码生成表详细信息
     */
    @GetMapping("/{id}")
    @PreAuthorize("@ss.hasPermission('tool:gen:query')")
    @Operation(summary = "获取代码生成表详细信息")
    public AjaxResult<GenTable> getInfo(@Parameter(description = "表ID") @PathVariable("id") Long id) {
        GenTable genTable = genTableService.selectGenTableById(id);
        return AjaxResult.success(genTable);
    }

    /**
     * 查询数据库表列表
     */
    @GetMapping("/dbTables")
    @PreAuthorize("@ss.hasPermission('tool:gen:list')")
    @Operation(summary = "查询数据库表列表")
    public AjaxResult<List<DbTableVO>> dbList(
            @Parameter(description = "表名称") @RequestParam(value = "tableName", required = false) String tableName) {
        List<DbTableVO> list = genTableService.selectDbTableListExcludeGenTable(tableName);
        return AjaxResult.success(list);
    }

    /**
     * 查询数据表字段列表
     */
    @GetMapping("/column/{tableName}")
    @PreAuthorize("@ss.hasPermission('tool:gen:list')")
    @Operation(summary = "查询数据表字段列表")
    public AjaxResult<List<Map<String, Object>>> columnList(
            @Parameter(description = "表名称") @PathVariable("tableName") String tableName) {
        List<Map<String, Object>> list = genTableService.selectTableColumnList(tableName);
        return AjaxResult.success(list);
    }

    /**
     * 导入表结构
     */
    @PostMapping("/importTable")
    @PreAuthorize("@ss.hasPermission('tool:gen:import')")
    @Operation(summary = "导入表结构")
    @OperationLog(title = "代码生成", businessType = BusinessType.IMPORT)
    public AjaxResult<Void> importTable(
            @Parameter(description = "导入表请求") @Validated @RequestBody ImportTableRequest request) {
        boolean result = genTableService.importTable(request);
        return toAjax(result);
    }

    /**
     * 修改保存代码生成业务
     */
    @PutMapping
    @PreAuthorize("@ss.hasPermission('tool:gen:edit')")
    @Operation(summary = "修改保存代码生成业务")
    @OperationLog(title = "代码生成", businessType = BusinessType.UPDATE)
    public AjaxResult<Void> editSave(@Parameter(description = "业务表信息") @Validated @RequestBody GenTableRequest request) {
        boolean result = genTableService.updateGenTable(request);
        return toAjax(result);
    }

    /**
     * 删除代码生成
     */
    @DeleteMapping("/{tableNames}")
    @PreAuthorize("@ss.hasPermission('tool:gen:remove')")
    @Operation(summary = "删除代码生成")
    @OperationLog(title = "代码生成", businessType = BusinessType.DELETE)
    public AjaxResult<Void> remove(
            @Parameter(description = "表名称，多个逗号分隔") @PathVariable("tableNames") String[] tableNames) {
        boolean result = genTableService.deleteGenTable(tableNames);
        return toAjax(result);
    }

    /**
     * 预览代码
     */
    @GetMapping("/preview/{tableName}")
    @PreAuthorize("@ss.hasPermission('tool:gen:preview')")
    @Operation(summary = "预览代码")
    public AjaxResult<List<PreviewCodeVO>> preview(
            @Parameter(description = "表名称") @PathVariable("tableName") String tableName) {
        List<PreviewCodeVO> dataList = genTableService.previewCode(tableName);
        return AjaxResult.success(dataList);
    }

    /**
     * 生成代码（下载方式）
     */
    @GetMapping("/download/{tableName}")
    @PreAuthorize("@ss.hasPermission('tool:gen:code')")
    @Operation(summary = "生成代码（下载方式）")
    public ResponseEntity<byte[]> download(@Parameter(description = "表名称") @PathVariable("tableName") String tableName)
            throws IOException {
        byte[] data = genTableService.downloadCode(tableName);
        String fileName = tableName + ".zip";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(data.length)
                .body(data);
    }

    /**
     * 批量生成代码
     */
    @PostMapping("/batchGenCode")
    @PreAuthorize("@ss.hasPermission('tool:gen:code')")
    @Operation(summary = "批量生成代码")
    @OperationLog(title = "代码生成", businessType = BusinessType.GENERATE)
    public ResponseEntity<byte[]> batchGenCode(
            @Parameter(description = "批量生成代码请求") @Validated @RequestBody BatchGenCodeRequest request) throws IOException {
        byte[] data = genTableService.batchGenerateCode(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=code.zip")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(data.length)
                .body(data);
    }

    /**
     * 生成代码（自定义路径）
     */
    @PostMapping("/genToPath/{tableName}")
    @PreAuthorize("@ss.hasPermission('tool:gen:code')")
    @Operation(summary = "生成代码（自定义路径）")
    @OperationLog(title = "代码生成", businessType = BusinessType.GENERATE)
    public AjaxResult<Void> genToPath(@Parameter(description = "表名称") @PathVariable("tableName") String tableName) {
        boolean result = genTableService.genToPath(tableName);
        return toAjax(result);
    }

    /**
     * 同步数据库
     */
    @PutMapping("/sync/{tableName}")
    @PreAuthorize("@ss.hasPermission('tool:gen:edit')")
    @Operation(summary = "同步数据库")
    @OperationLog(title = "代码生成", businessType = BusinessType.UPDATE)
    public AjaxResult<Void> syncDb(@Parameter(description = "表名称") @PathVariable("tableName") String tableName) {
        boolean result = genTableService.syncDb(tableName);
        return toAjax(result);
    }

    /**
     * 执行SQL脚本
     */
    @PostMapping("/executeSql")
    @PreAuthorize("@ss.hasPermission('tool:gen:edit')")
    @Operation(summary = "执行SQL脚本")
    @OperationLog(title = "代码生成", businessType = BusinessType.EXECUTE)
    public AjaxResult<Void> executeSql(
            @Parameter(description = "SQL请求") @Validated @RequestBody ExecuteSqlRequest request) {
        boolean result = genTableService.executeSql(request);
        return toAjax(result);
    }

    /**
     * 获取代码生成全局配置
     */
    @GetMapping("/config")
    @PreAuthorize("@ss.hasPermission('tool:gen:query')")
    @Operation(summary = "获取代码生成全局配置")
    public AjaxResult<GenConfig> getConfig() {
        // 这里应该从配置文件或数据库中获取全局配置
        GenConfig config = new GenConfig();
        config.setAuthor("Chuang");
        config.setPackageName("cn.zhangchuangla");
        config.setModuleName("system");
        config.setTablePrefix("sys_");
        config.setOutputDir(System.getProperty("user.dir"));
        return AjaxResult.success(config);
    }

    /**
     * 修改代码生成全局配置
     */
    @PutMapping("/config")
    @PreAuthorize("@ss.hasPermission('tool:gen:edit')")
    @Operation(summary = "修改代码生成全局配置")
    @OperationLog(title = "代码生成", businessType = BusinessType.UPDATE)
    public AjaxResult<Void> updateConfig(@Parameter(description = "全局配置") @Validated @RequestBody GenConfig config) {
        // 这里应该保存全局配置到配置文件或数据库
        return AjaxResult.success();
    }

    /**
     * 获取可用模板列表
     */
    @GetMapping("/templates")
    @PreAuthorize("@ss.hasPermission('tool:gen:query')")
    @Operation(summary = "获取可用模板列表")
    public AjaxResult<List<String>> getTemplates() {
        // 这里应该从模板目录中获取所有可用模板
        List<String> templates = List.of(
                "entity.java.vm",
                "mapper.java.vm",
                "mapper.xml.vm",
                "service.java.vm",
                "serviceImpl.java.vm",
                "controller.java.vm",
                "request/add.java.vm",
                "request/update.java.vm",
                "request/list.java.vm");
        return AjaxResult.success(templates);
    }
}