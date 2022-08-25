package cn.edu.zjou.crawler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest
public class PostCrawlerTest {

    @Autowired
    private PostCrawler postCrawler;

    @Test
    public void testGetPagesCount() {
        System.out.println(postCrawler.getPagesCount());
    }

    @Test
    public void testProcessPage() {
        postCrawler.processPage(1);
    }

}
