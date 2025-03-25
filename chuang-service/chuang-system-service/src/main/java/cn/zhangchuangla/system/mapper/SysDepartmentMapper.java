package cn.zhangchuangla.system.mapper;

import cn.zhangchuangla.system.model.entity.SysDepartment;
import cn.zhangchuangla.system.model.request.department.SysDepartmentListRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * @author zhangchuang
 */
public interface SysDepartmentMapper extends BaseMapper<SysDepartment> {


    /**
     * 部门列表
     *
     * @param sysDepartmentPage 分页参数
     * @param request           请求参数
     * @return 返回部门分页列表
     */
    Page<SysDepartment> listDepartment(Page<SysDepartment> sysDepartmentPage, @Param("request") SysDepartmentListRequest request);
}




