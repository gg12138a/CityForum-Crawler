package cn.edu.zjou;


import cn.edu.zjou.mapper.PostMapper;
import cn.edu.zjou.po.Post;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class AppTest 
{

    @Autowired
    private PostMapper postMapper;

    @Test
    public void shouldAnswerWithTrue()
    {
        List<Post> posts = postMapper.selectList(null);
        posts.forEach(System.out::println);
    }
}
