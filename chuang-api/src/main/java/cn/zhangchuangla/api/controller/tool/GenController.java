package cn.zhangchuangla.api.controller.tool;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.common.result.TableDataResult;
import cn.zhangchuangla.generator.model.entity.GenTable;
import cn.zhangchuangla.generator.model.request.GenTableListRequest;
import cn.zhangchuangla.generator.model.vo.GenTableListVo;
import cn.zhangchuangla.generator.service.GenTableService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

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
    public AjaxResult<TableDataResult> listGenTable(GenTableListRequest request) {
        Page<GenTable> page = genTableService.listGenTable(request);
        List<GenTableListVo> genTableListVos = copyListProperties(page, GenTableListVo.class);
        return getTableData(page, genTableListVos);
    }


    /**
     * 导入数据库表结构
     *
     * @param tableNames 表名称
     * @return 操作结果
     */
    @Operation(summary = "导入数据库表结构")
    @PreAuthorize("@ss.hasPermission('tool:gen:import')")
    @PostMapping("/import")
    public AjaxResult<Void> importTable(@RequestBody List<String> tableNames) {
        return success();
    }

    /**
     * 查询表字段配置
     *
     * @param tableName 表名称
     * @return 表字段配置
     */
    @Operation(summary = "查询表字段配置")
    @PreAuthorize("@ss.hasPermission('tool:gen:query')")
    @GetMapping("/column/list")
    public AjaxResult<Void> columnList(@RequestParam String tableName) {
        return success();
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
    @GetMapping("/preview")
    public AjaxResult<Void> preview(@RequestParam String tableName) {
        return success();
    }

    /**
     * 下载代码
     *
     * @param tableName 表名称
     * @return 压缩包
     */
    @Operation(summary = "下载代码")
    @PreAuthorize("@ss.hasPermission('tool:gen:download')")
    @GetMapping("/download")
    public ResponseEntity<byte[]> download(@RequestParam String tableName) throws IOException {
        return null;
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
