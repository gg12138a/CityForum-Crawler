package cn.edu.zjou.util;


import org.seimicrawler.xpath.JXDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * @author G_xy
 */
@Component
public class AsyncPageDownloader {

    private Logger logger = LoggerFactory.getLogger(AsyncPageDownloader.class);

    @Async("proxyTaskExecutor")
    public CompletableFuture<JXDocument> downloadPageAsync(String url) {
        logger.info("Download：{}", url);
        return CompletableFuture.completedFuture(JXDocument.createByUrl(url))
                .exceptionally(throwable -> {
                    logger.error("下载失败：{}", url, throwable);
                    return null;
                });
    }
}
