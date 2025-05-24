package cn.zhangchuangla.system.mapper;

import cn.zhangchuangla.system.model.entity.Student;
import cn.zhangchuangla.system.model.request.student.StudentQueryRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * 学生表测试表Mapper接口
 *
 * @author Chuang
 * @date 2025-05-24
 */
public interface StudentMapper extends BaseMapper<Student> {

    /**
     * 分页查询学生表测试表信息
     *
     * @param page    分页对象
     * @param request 查询参数
     * @return 返回分页对象
     */
    Page<Student> listStudent(Page<Student> page, @Param("request") StudentQueryRequest request);
}
