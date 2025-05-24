package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.core.core.controller.BaseController;
import cn.zhangchuangla.common.core.enums.BusinessType;
import cn.zhangchuangla.common.core.result.AjaxResult;
import cn.zhangchuangla.common.core.result.TableDataResult;
import cn.zhangchuangla.common.excel.utils.ExcelUtils;
import cn.zhangchuangla.framework.annotation.OperationLog;
import cn.zhangchuangla.system.model.entity.Student;
import cn.zhangchuangla.system.model.request.student.StudentAddRequest;
import cn.zhangchuangla.system.model.request.student.StudentQueryRequest;
import cn.zhangchuangla.system.model.request.student.StudentUpdateRequest;
import cn.zhangchuangla.system.model.vo.student.StudentListVo;
import cn.zhangchuangla.system.model.vo.student.StudentVo;
import cn.zhangchuangla.system.service.StudentService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学生表测试表控制器
 *
 * @author Chuang
 * @date 2025-05-24
 */
@RestController
@RequestMapping("/system/student")
@Tag(name = "学生表测试表管理")
@RequiredArgsConstructor
public class StudentController extends BaseController {

    private final StudentService studentService;
    private final ExcelUtils excelUtils;

    /**
     * 查询学生表测试表列表
     */
    @Operation(summary = "查询学生表测试表列表")
    @PreAuthorize("@ss.hasPermission('system.student:list')")
    @GetMapping("/list")
    public AjaxResult<TableDataResult> list(@Parameter(description = "学生表测试表列表查询参数")
                                            @Validated @ParameterObject StudentQueryRequest request) {
        Page<Student> page = studentService.listStudent(request);
        List<StudentListVo> voList = copyListProperties(page, StudentListVo.class);
        return getTableData(page, voList);
    }

    /**
     * 导出学生表测试表列表
     */
    @Operation(summary = "导出学生表测试表列表")
    @PreAuthorize("@ss.hasPermission('system.student:export')")
    @GetMapping("/export")
    @OperationLog(title = "学生表测试表管理", businessType = BusinessType.EXPORT)
    public void export(HttpServletResponse response) {
        List<Student> list = studentService.list();
        List<StudentListVo> voList = copyListProperties(list, StudentListVo.class);
        excelUtils.exportExcel(response, voList, StudentListVo.class, "学生列表");
    }

    /**
     * 获取学生表测试表详细信息
     */
    @Operation(summary = "获取学生表测试表详细信息")
    @PreAuthorize("@ss.hasPermission('system.student:query')")
    @GetMapping("/{id}")
    public AjaxResult<StudentVo> getInfo(@Parameter(description = "学生表测试表ID")
                                         @PathVariable("id") Long id) {
        checkParam(id == null, "id不能为空");
        Student student = studentService.getStudentById(id);
        StudentVo vo = new StudentVo();
        BeanUtils.copyProperties(student, vo);
        return success(vo);
    }

    /**
     * 新增学生表测试表
     */
    @Operation(summary = "新增学生表测试表")
    @PreAuthorize("@ss.hasPermission('system.student:add')")
    @PostMapping
    @OperationLog(title = "学生表测试表管理", businessType = BusinessType.INSERT)
    public AjaxResult<Void> add(@Parameter(description = "新增学生表测试表请求参数")
                                @Validated @RequestBody StudentAddRequest request) {
        boolean result = studentService.addStudent(request);
        return toAjax(result);
    }

    /**
     * 修改学生表测试表
     */
    @Operation(summary = "修改学生表测试表")
    @PreAuthorize("@ss.hasPermission('system.student:edit')")
    @PutMapping
    @OperationLog(title = "学生表测试表管理", businessType = BusinessType.UPDATE)
    public AjaxResult<Void> edit(@Parameter(description = "修改学生表测试表请求参数")
                                 @Validated @RequestBody StudentUpdateRequest request) {
        boolean result = studentService.updateStudent(request);
        return toAjax(result);
    }

    /**
     * 删除学生表测试表
     */
    @Operation(summary = "删除学生表测试表")
    @PreAuthorize("@ss.hasPermission('system.student:remove')")
    @DeleteMapping("/{ids}")
    @OperationLog(title = "学生表测试表管理", businessType = BusinessType.DELETE)
    public AjaxResult<Void> remove(@Parameter(description = "学生表测试表ID集合，支持批量删除")
                                   @PathVariable("ids") List<Long> ids) {
        boolean result = studentService.deleteStudentByIds(ids);
        return toAjax(result);
    }

}
