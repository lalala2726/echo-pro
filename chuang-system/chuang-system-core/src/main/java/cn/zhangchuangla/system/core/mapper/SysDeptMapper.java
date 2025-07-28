package cn.zhangchuangla.system.core.mapper;

import cn.zhangchuangla.system.core.model.entity.SysDept;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Chuang
 */
public interface SysDeptMapper extends BaseMapper<SysDept> {


    /**
     * 查询部门列表
     *
     * @param sysDept 查询参数
     * @return 部门列表
     */
    List<SysDept> listDepartment(@Param("request") SysDept sysDept);
}




