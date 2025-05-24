package cn.zhangchuangla.system.service;

import cn.zhangchuangla.system.model.entity.Student;
import cn.zhangchuangla.system.model.request.student.StudentAddRequest;
import cn.zhangchuangla.system.model.request.student.StudentQueryRequest;
import cn.zhangchuangla.system.model.request.student.StudentUpdateRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 学生表测试表Service接口
 *
 * @author Chuang
 * @date 2025-05-24
 */
public interface StudentService extends IService<Student> {

    /**
     * 分页查询学生表测试表
     *
     * @param request 查询参数
     * @return 分页结果
     */
    Page<Student> listStudent(StudentQueryRequest request);

    /**
     * 根据ID查询学生表测试表
     *
     * @param id ID
     * @return 学生表测试表
     */
    Student getStudentById(Long id);

    /**
     * 新增学生表测试表
     *
     * @param request 新增请求参数
     * @return 结果
     */
    boolean addStudent(StudentAddRequest request);

    /**
     * 修改学生表测试表
     *
     * @param request 修改请求参数
     * @return 结果
     */
    boolean updateStudent(StudentUpdateRequest request);

    /**
     * 批量删除学生表测试表
     *
     * @param ids 需要删除的ID集合
     * @return 结果
     */
    boolean deleteStudentByIds(List<Long> ids);

}
