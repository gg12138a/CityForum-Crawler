package cn.edu.zjou.mapper;

import cn.edu.zjou.po.User;
import cn.edu.zjou.service.impl.UserServiceImpl;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class MapperTest {

    @Autowired
    private UserServiceImpl userService;

    @Test
    public void test(){
        List<User> users =new ArrayList<>();

        users.add(new User(1));
        users.add(new User(2));
        users.add(new User(3));

        try {
            userService.saveBatch(users);
        }catch (Exception e){
            e.printStackTrace();
        }


    }
}
