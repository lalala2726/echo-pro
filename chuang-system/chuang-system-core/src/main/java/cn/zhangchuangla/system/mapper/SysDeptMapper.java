package cn.zhangchuangla.system.mapper;

import cn.zhangchuangla.system.model.entity.SysDept;
import cn.zhangchuangla.system.model.request.dept.SysDeptListRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangchuang
 */
public interface SysDeptMapper extends BaseMapper<SysDept> {


    List<SysDept> listDepartment(@Param("request") SysDeptListRequest request);
}




