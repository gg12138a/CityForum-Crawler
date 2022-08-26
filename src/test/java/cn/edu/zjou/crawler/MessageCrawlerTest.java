package cn.edu.zjou.crawler;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@SpringBootTest
public class MessageCrawlerTest {

    @Autowired
    private MessageCrawler messageCrawler;





    @Test
    public void testDoCrawl() {
        //http://bbs.zhoushan.cn/thread-804167-1-1.html
        messageCrawler.doCrawl(804167);


        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


}
