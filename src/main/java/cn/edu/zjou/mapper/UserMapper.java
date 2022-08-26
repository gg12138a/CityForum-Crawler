package cn.edu.zjou.mapper;

import cn.edu.zjou.po.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMapper extends BaseMapper<User> {

    @Select("""
        select * from `t_user`
        where username is null;
    """)
    List<User> selectUsersByUsernameIsNull();
}
