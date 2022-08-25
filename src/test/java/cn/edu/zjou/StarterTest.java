package cn.edu.zjou;


import cn.edu.zjou.starter.CrawlerStarter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class StarterTest
{

    @Autowired
    private CrawlerStarter crawlerStarter;

    @Test
    public void testStart()
    {
        crawlerStarter.start();
    }
}
