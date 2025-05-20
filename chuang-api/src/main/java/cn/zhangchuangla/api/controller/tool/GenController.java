package cn.zhangchuangla.api.controller.tool;

import cn.hutool.core.bean.BeanUtil;
import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.common.result.TableDataResult;
import cn.zhangchuangla.generator.config.GenConfig;
import cn.zhangchuangla.generator.model.entity.DatabaseTable;
import cn.zhangchuangla.generator.model.entity.GenTable;
import cn.zhangchuangla.generator.model.entity.GenTableColumn;
import cn.zhangchuangla.generator.model.request.DatabaseTableQueryRequest;
import cn.zhangchuangla.generator.model.request.GenConfigUpdateRequest;
import cn.zhangchuangla.generator.model.request.GenTableQueryRequest;
import cn.zhangchuangla.generator.model.vo.*;
import cn.zhangchuangla.generator.service.GenTableService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
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
     * 分页查询低代码表
     *
     * @return 分页结果
     */
    @Operation(summary = "分页查询低代码表")
    @PreAuthorize("@ss.hasPermission('tool:gen:list')")
    @GetMapping("/list")
    public AjaxResult<TableDataResult> listGenTable(GenTableQueryRequest request) {
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
    public AjaxResult<GenTableVo> getGenTableInfo(@PathVariable("id") Long id) {
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
    public AjaxResult<TableDataResult> listDatabaseTables(DatabaseTableQueryRequest databaseTableQueryRequest) {
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
    public AjaxResult<Void> importTable(@PathVariable("tableName") List<String> tableNames) {
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
    @PutMapping
    public AjaxResult<Void> updateConfigInfo(@RequestBody @Validated GenConfigUpdateRequest request) {
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
    public AjaxResult<List<GenTableColumnVo>> columnList(@PathVariable("tableName") String tableName) {
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
     * @return 操作结果
     */
    @Operation(summary = "修改低代码表配置")
    @PreAuthorize("@ss.hasPermission('tool:gen:update')")
    @PostMapping("/update")
    public AjaxResult<Void> update() {
        return success();
    }

    /**
     * 删除低代码表
     *
     * @param tableNames 表名称
     * @return 操作结果
     */
    @Operation(summary = "删除低代码表")
    @PreAuthorize("@ss.hasPermission('tool:gen:remove')")
    @PostMapping("/delete")
    public AjaxResult<Void> delete(@RequestBody String[] tableNames) {
        return success();
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
    public AjaxResult<List<CodePreviewVo>> preview(@PathVariable("tableName") String tableName) {
        checkParam(tableName == null || tableName.isEmpty(), "表名称不能为空");

        // 获取预览代码
        Map<String, String> codeMap = genTableService.previewCode(tableName);

        // 转换为前端需要的格式
        List<CodePreviewVo> previewList = new ArrayList<>();

        codeMap.forEach((fileName, content) -> {
            // 获取文件类型
            String fileType = getFileType(fileName);

            // 获取简化文件名
            String simpleFileName = getSimpleFileName(fileName);

            // 添加到预览列表
            CodePreviewVo previewVo = CodePreviewVo.builder()
                    .fileName(simpleFileName)
                    .content(content)
                    .fileType(fileType)
                    .build();

            previewList.add(previewVo);
        });

        return success(previewList);
    }

    /**
     * 获取文件类型
     *
     * @param fileName 文件名
     * @return 文件类型
     */
    private String getFileType(String fileName) {
        if (fileName.contains("entity.java")) {
            return "entity";
        } else if (fileName.contains("mapper.java")) {
            return "mapper";
        } else if (fileName.contains("service.java") && !fileName.contains("serviceImpl")) {
            return "service";
        } else if (fileName.contains("serviceImpl.java")) {
            return "serviceImpl";
        } else if (fileName.contains("controller.java")) {
            return "controller";
        } else if (fileName.contains("mapper.xml")) {
            return "mapperXml";
        } else if (fileName.contains("vo.java")) {
            return "vo";
        } else if (fileName.contains("request.java")) {
            return "request";
        } else {
            return "other";
        }
    }

    /**
     * 获取简化文件名
     *
     * @param fileName 文件名
     * @return 简化文件名
     */
    private String getSimpleFileName(String fileName) {
        int lastIndex = fileName.lastIndexOf("/");
        if (lastIndex > -1) {
            return fileName.substring(lastIndex + 1);
        }
        return fileName;
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
    public ResponseEntity<byte[]> download(@PathVariable("tableName") String tableName) throws IOException {
        checkParam(tableName == null || tableName.isEmpty(), "表名称不能为空");

        // 生成代码
        byte[] data = genTableService.downloadCode(tableName);

        // 设置响应头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", tableName + ".zip");

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
    @PostMapping("/syncDb")
    public AjaxResult<Void> syncDb(@RequestParam String tableName) {
        return success();
    }
}
