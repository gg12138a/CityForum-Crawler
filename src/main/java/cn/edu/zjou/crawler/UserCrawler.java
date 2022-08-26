package cn.edu.zjou.crawler;

import cn.edu.zjou.util.AsyncPageDownloader;
import cn.edu.zjou.util.UrlBuilderUtil;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class UserCrawler {

    private final Logger logger = LoggerFactory.getLogger(UserCrawler.class);

    private final UrlBuilderUtil urlBuilderUtil;
    private final AsyncPageDownloader downloader;


    public UserCrawler(UrlBuilderUtil urlBuilderUtil, AsyncPageDownloader downloader) {
        this.urlBuilderUtil = urlBuilderUtil;
        this.downloader = downloader;
    }

    public String getUsernameByUserId(int userId) {
        logger.info("user:其uid为{}", userId);

        String userSpaceUrl = urlBuilderUtil.getUserSpaceUrlByUid(userId);
        JXDocument jxd = downloader.downloadPageAsync(userSpaceUrl).join();

        JXNode usernameNode = jxd.selNOne("""
                //*[@id="uhd"]/div[2]/h2""");
        return usernameNode.asElement().ownText();
    }
}
