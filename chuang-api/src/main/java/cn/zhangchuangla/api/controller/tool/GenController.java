package cn.zhangchuangla.api.controller.tool;

import cn.hutool.core.bean.BeanUtil;
import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.common.result.TableDataResult;
import cn.zhangchuangla.generator.config.GenConfig;
import cn.zhangchuangla.generator.enums.FileType;
import cn.zhangchuangla.generator.model.entity.DatabaseTable;
import cn.zhangchuangla.generator.model.entity.GenTable;
import cn.zhangchuangla.generator.model.entity.GenTableColumn;
import cn.zhangchuangla.generator.model.request.DatabaseTableQueryRequest;
import cn.zhangchuangla.generator.model.request.GenConfigUpdateRequest;
import cn.zhangchuangla.generator.model.request.GenTableQueryRequest;
import cn.zhangchuangla.generator.model.request.GenTableUpdateRequest;
import cn.zhangchuangla.generator.model.vo.*;
import cn.zhangchuangla.generator.service.GenTableService;
import cn.zhangchuangla.generator.utils.GenUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
@Tag(name = "代码生成")
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
        BeanUtil.copyProperties(genTable, genTableVo);
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
    public AjaxResult<TableDataResult> listDatabaseTables(
            @Parameter(description = "查询参数") DatabaseTableQueryRequest databaseTableQueryRequest) {
        Page<DatabaseTable> page = genTableService.listDatabaseTables(databaseTableQueryRequest);
        List<DatabaseTableVo> databaseTableVos = copyListProperties(page, DatabaseTableVo.class);
        return getTableData(page, databaseTableVos);
    }

    /**
     * 导入数据库表结构
     *
     * @param tableNames 表名称
     * @return 操作结果
     */
    @Operation(summary = "导入数据库表结构")
    @PreAuthorize("@ss.hasPermission('tool:gen:import')")
    @PostMapping("/importTable/{tableName}")
    public AjaxResult<Void> importTable(
            @Parameter(description = "数据库中表的名称") @PathVariable("tableName") List<String> tableNames) {
        checkParam(tableNames == null, "表名称不能为空！");
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
        boolean result = genTableService.updateGenTable(request);
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
     * 下载代码
     *
     * @param tableName 表名称
     * @return 压缩包
     */
    @Operation(summary = "下载代码")
    @PreAuthorize("@ss.hasPermission('tool:gen:download')")
    @GetMapping("/download/{tableName}")
    public ResponseEntity<byte[]> download(
            @Parameter(description = "需要预览的表名称") @PathVariable("tableName") String tableName) {
        // 参数验证
        checkParam(tableName == null || tableName.isEmpty(), "表名称不能为空");

        // 生成代码
        byte[] data = genTableService.downloadCode(tableName);

        // 设置更完善的响应头
        HttpHeaders headers = new HttpHeaders();

        // 明确指定为ZIP文件类型
        headers.setContentType(MediaType.parseMediaType("application/zip"));

        // 设置文件名，确保使用UTF-8编码避免中文问题
        String fileName = tableName + "_code.zip";
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");

        // 设置内容长度
        headers.setContentLength(data.length);

        // 禁止缓存
        headers.setCacheControl("no-cache, no-store, must-revalidate");
        headers.setPragma("no-cache");
        headers.setExpires(0);

        log.info("正在下载代码，表名：{}，文件大小：{} 字节", tableName, data.length);

        return new ResponseEntity<>(data, headers, HttpStatus.OK);
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

        // 删除旧的表结构
        GenTable table = genTableService.lambdaQuery().eq(GenTable::getTableName, tableName).one();
        if (table == null) {
            return error("表不存在");
        }

        // 删除旧的表记录
        genTableService.removeById(table.getTableId());

        // 重新导入表结构
        List<String> tableNames = new ArrayList<>();
        tableNames.add(tableName);
        boolean importResult = genTableService.importTable(tableNames);

        return toAjax(importResult);
    }
}
