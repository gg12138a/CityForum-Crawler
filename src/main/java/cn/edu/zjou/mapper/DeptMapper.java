package cn.edu.zjou.mapper;

import cn.edu.zjou.po.Dept;
import cn.edu.zjou.po.Type;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeptMapper extends BaseMapper<Dept> {
    void saveAllWithUniqueDeptName(@Param("depts") List<Dept> depts);
}
