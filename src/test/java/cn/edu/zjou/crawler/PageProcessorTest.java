package cn.edu.zjou.crawler;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PageProcessorTest {

    @Autowired
    private MessageCrawler.MsgProcessor msgProcessor;

    @Test
    public void testGetAllPagesCount() {
        System.out.println(msgProcessor.getAllPagesCount(804167));
        System.out.println(msgProcessor.getAllPagesCount(812000));
    }


    @SneakyThrows
    @Test
    public void testContent() {
        msgProcessor.processPage(812054, 1);

        Thread.sleep(200000);
    }
}
