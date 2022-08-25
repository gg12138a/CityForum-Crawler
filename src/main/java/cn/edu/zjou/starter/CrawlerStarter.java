package cn.edu.zjou.starter;


import cn.edu.zjou.config.CrawlerConfig;
import cn.edu.zjou.crawler.PostCrawler;
import cn.edu.zjou.crawler.TagCrawler;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 爬取地址：http://bbs.zhoushan.cn/forum-143-1.html
 *
 * @author G_xy
 */
@Component
@NoArgsConstructor
public class CrawlerStarter {

    private final Logger logger = LoggerFactory.getLogger(CrawlerStarter.class);

    private CrawlerConfig crawlerConfig;
    private PostCrawler postCrawler;
    private TagCrawler tagCrawler;

    @Autowired
    public CrawlerStarter(CrawlerConfig crawlerConfig, PostCrawler postCrawler, TagCrawler tagCrawler) {
        this.crawlerConfig = crawlerConfig;
        this.postCrawler = postCrawler;
        this.tagCrawler = tagCrawler;
    }

    /**
     * 1. 更新MySQL的t_tag表
     * 2.
     */
    public void start() {
        tagCrawler.updateTypes(crawlerConfig.getStartUrl());

    }
}
