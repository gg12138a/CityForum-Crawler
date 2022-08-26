package cn.edu.zjou;

import cn.edu.zjou.config.CrawlerConfig;
import cn.edu.zjou.crawler.MessageCrawler;
import cn.edu.zjou.starter.CrawlerStarter;
import cn.edu.zjou.util.AsyncPageDownloader;
import cn.edu.zjou.util.UrlBuilderUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @author G_xy
 */
@EnableConfigurationProperties({CrawlerConfig.class})
@SpringBootApplication
@MapperScan("cn.edu.zjou.mapper")
public class CityForumCrawlerApplication implements CommandLineRunner
{
    /**
     * Main入口
     */
    public static void main(String[] args) {
        SpringApplication.run(CityForumCrawlerApplication.class,args);
    }

    public CityForumCrawlerApplication(CrawlerStarter crawlerStarter) {
        this.crawlerStarter = crawlerStarter;
    }

    private final CrawlerStarter crawlerStarter;

    @Autowired
    private MessageCrawler messageCrawler;

    @Override
    public void run(String... args) throws Exception {
        crawlerStarter.start();
    }
}
