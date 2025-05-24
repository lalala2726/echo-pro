package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.system.mapper.StudentMapper;
import cn.zhangchuangla.system.model.entity.Student;
import cn.zhangchuangla.system.model.request.student.StudentAddRequest;
import cn.zhangchuangla.system.model.request.student.StudentQueryRequest;
import cn.zhangchuangla.system.model.request.student.StudentUpdateRequest;
import cn.zhangchuangla.system.service.StudentService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 学生表测试表Service实现
 *
 * @author Chuang
 * @date 2025-05-24
 */
@Service
@RequiredArgsConstructor
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {

    private final StudentMapper studentMapper;

    /**
     * 分页查询学生表测试表
     *
     * @param request 查询参数
     * @return 分页结果
     */
    @Override
    public Page<Student> listStudent(StudentQueryRequest request) {
        Page<Student> page = new Page<>(request.getPageNum(), request.getPageSize());
        return studentMapper.listStudent(page, request);
    }

    /**
     * 根据ID查询学生表测试表
     *
     * @param id ID
     * @return 学生表测试表
     */
    @Override
    public Student getStudentById(Long id) {
        return getById(id);
    }

    /**
     * 新增学生表测试表
     *
     * @param request 新增请求参数
     * @return 结果
     */
    @Override
    public boolean addStudent(StudentAddRequest request) {
        Student student = new Student();
        BeanUtils.copyProperties(request, student);
        return save(student);
    }

    /**
     * 修改学生表测试表
     *
     * @param request 修改请求参数
     * @return 结果
     */
    @Override
    public boolean updateStudent(StudentUpdateRequest request) {
        Student student = new Student();
        BeanUtils.copyProperties(request, student);
        return updateById(student);
    }

    /**
     * 批量删除学生表测试表
     *
     * @param ids 需要删除的ID集合
     * @return 结果
     */
    @Override
    public boolean deleteStudentByIds(List<Long> ids) {
        return removeByIds(ids);
    }
}
