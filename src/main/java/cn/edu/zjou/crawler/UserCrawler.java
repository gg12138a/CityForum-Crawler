package cn.edu.zjou.crawler;

import cn.edu.zjou.event.UserEvent;
import cn.edu.zjou.po.User;
import cn.edu.zjou.service.impl.UserServiceImpl;
import cn.edu.zjou.util.AsyncPageDownloader;
import cn.edu.zjou.util.UrlBuilderUtil;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class UserCrawler{

    private final Logger logger = LoggerFactory.getLogger(UserCrawler.class);

    private final UrlBuilderUtil urlBuilderUtil;
    private final UserServiceImpl userService;
    private final ApplicationEventPublisher publisher;

    public UserCrawler(UrlBuilderUtil urlBuilderUtil, AsyncPageDownloader downloader, ApplicationEventPublisher publisher, UserServiceImpl userService) {
        this.urlBuilderUtil = urlBuilderUtil;
        this.publisher = publisher;
        this.userService = userService;
    }

    public void processUser(int userId) {
        User selectedUser = userService.getById(userId);

        // 要么没有记录；要么连同ID和name一起保存
        if (selectedUser == null) {
            this.crawlAndSaveUser(userId);
        }
    }

    public void crawlAndSaveUser(int userId) {
        logger.info("user:其uid为{}", userId);

        String userSpaceUrl = urlBuilderUtil.getUserSpaceUrlByUid(userId);
        JXDocument jxd = JXDocument.createByUrl(userSpaceUrl);

        publisher.publishEvent(new UserEvent(this, jxd, userId));
    }

    public String parseJxdToUsername(JXDocument jxd) {
        JXNode usernameNode = jxd.selNOne("""
                //*[@id="uhd"]/div[2]/h2""");
        return usernameNode.asElement().ownText();
    }

    @EventListener
    @Async("proxyTaskExecutor")
    public void onApplicationEvent(UserEvent event) {
        JXDocument jxd = event.getJxd();
        int userId = event.getUserId();

        // 避免重复主键异常
        userService.saveOrUpdate(new User(userId, this.parseJxdToUsername(jxd)));
    }
}
