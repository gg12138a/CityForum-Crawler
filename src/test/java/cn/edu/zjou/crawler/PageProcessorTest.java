package cn.edu.zjou.crawler;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PageProcessorTest {

    @Autowired
    private MessageCrawler.PageProcessor pageProcessor;

    @Test
    public void testGetAllPagesCount() {
        System.out.println(pageProcessor.getAllPagesCount(804167));
        System.out.println(pageProcessor.getAllPagesCount(812000));
    }


    @SneakyThrows
    @Test
    public void testContent() {
        pageProcessor.processPage(812054, 1);

        Thread.sleep(200000);
    }
}
