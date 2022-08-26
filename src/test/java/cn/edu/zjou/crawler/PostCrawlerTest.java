package cn.edu.zjou.crawler;

import cn.edu.zjou.enums.PostStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PostCrawlerTest {

    @Autowired
    private PostCrawler postCrawler;

    @Test
    public void testGetPagesCount() {
        System.out.println(postCrawler.getAllPagesCount());
    }

    @Test
    public void testProcessPage() {
        postCrawler.processPage(1);
    }

    @Test
    public void testGetPostStatusByName() {
        System.out.println(PostStatus.PostStatusBuilder.getPostStatusByName("123"));
        System.out.println(PostStatus.PostStatusBuilder.getPostStatusByName(null));
    }

}
