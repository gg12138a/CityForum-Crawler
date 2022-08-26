package cn.edu.zjou.util;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@SpringBootTest
public class AsyncPageDownloaderTest {

    @Autowired
    private AsyncPageDownloader downloader;

    @Test
    public void testAsyc() {


    }

    @Test
    public void testTimeOfAsyncDownloadPage() {

        long start = System.currentTimeMillis();

        List<Future<JXDocument>> futures = new ArrayList<>();
        futures.add(downloader.downloadPageAsync("http://bbs.zhoushan.cn/thread-812000-1-1.html"));
        futures.add(downloader.downloadPageAsync("http://bbs.zhoushan.cn/thread-811815-1-5.html"));
        futures.add(downloader.downloadPageAsync("http://bbs.zhoushan.cn/thread-804167-1-1.html"));
        futures.add(downloader.downloadPageAsync("http://bbs.zhoushan.cn/thread-804167-1-1.html"));
        futures.add(downloader.downloadPageAsync("http://bbs.zhoushan.cn/thread-804167-1-1.html"));
        futures.add(downloader.downloadPageAsync("http://bbs.zhoushan.cn/thread-804167-1-1.html"));

        for (var f : futures) {
            while (true) {
                if (f.isDone()) {
                    break;
                }
            }
        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);

        start = System.currentTimeMillis();
        JXDocument.createByUrl("http://bbs.zhoushan.cn/thread-812000-1-1.html");
        JXDocument.createByUrl("http://bbs.zhoushan.cn/thread-811815-1-5.html");
        JXDocument.createByUrl("http://bbs.zhoushan.cn/thread-804167-1-1.html");
        JXDocument.createByUrl("http://bbs.zhoushan.cn/thread-804167-1-1.html");
        JXDocument.createByUrl("http://bbs.zhoushan.cn/thread-804167-1-1.html");
        JXDocument.createByUrl("http://bbs.zhoushan.cn/thread-804167-1-1.html");
        end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    @SneakyThrows
    @Test
    public void testCallback() {
        List<CompletableFuture<JXDocument>> futures = new ArrayList<>();

        futures.add(downloader.downloadPageAsync("http://bbs.zhoushan.cn/thread-812000-1-1.html"));
        futures.add(downloader.downloadPageAsync("http://bbs.zhoushan.cn/thread-812000-1-1.html"));
        futures.add(downloader.downloadPageAsync("http://bbs.zhoushan.cn/thread-811815-1-5.html"));
        futures.add(downloader.downloadPageAsync("http://bbs.zhoushan.cn/thread-804167-1-1.html"));
        futures.add(downloader.downloadPageAsync("http://bbs.zhoushan.cn/thread-804167-1-1.html"));
        futures.add(downloader.downloadPageAsync("http://bbs.zhoushan.cn/thread-804167-1-1.html"));
        futures.add(downloader.downloadPageAsync("http://bbs.zhoushan.cn/thread-804167-1-1.html"));

        for (var f : futures) {
/*            f.addCallback(new ListenableFutureCallback<JXDocument>() {
                @Override
                public void onFailure(Throwable ex) {
                    System.out.println("下载失败");
                }

                @Override
                public void onSuccess(JXDocument result) {
                    System.out.println(Thread.currentThread().getName() + ":success");
                }
            });*/

            f.thenApplyAsync(jxDocument -> {
                JXNode jxNode = jxDocument.selNOne("""
                        //*[@id="thread_subject"]""");
                return jxNode.asElement().ownText();
            }).thenAcceptAsync(System.out::println);
        }

        Thread.sleep(10000);
    }
}
