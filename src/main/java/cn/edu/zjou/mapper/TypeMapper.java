package cn.edu.zjou.mapper;

import cn.edu.zjou.po.Type;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TypeMapper extends BaseMapper<Type> {
    void saveAllWithUniqueTypeName(@Param("types") List<Type> types);
}
