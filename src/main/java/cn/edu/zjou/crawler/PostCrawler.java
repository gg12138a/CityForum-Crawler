package cn.edu.zjou.crawler;

import cn.edu.zjou.config.CrawlerConfig;
import cn.edu.zjou.enums.PostStatus;
import cn.edu.zjou.event.PostEvent;
import cn.edu.zjou.po.Dept;
import cn.edu.zjou.po.Post;
import cn.edu.zjou.po.Type;
import cn.edu.zjou.service.impl.PostServiceImpl;
import cn.edu.zjou.util.DeptConverter;
import cn.edu.zjou.util.TypeConverter;
import cn.edu.zjou.util.UrlBuilderUtil;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 负责爬取Post信息（不处理具体的Message信息）
 */
@Component
public class PostCrawler {

    private final Logger logger = LoggerFactory.getLogger(PostCrawler.class);
    private final CrawlerConfig crawlerConfig;
    private final UrlBuilderUtil urlBuilderUtil;

    private final PostServiceImpl postService;

    private final MessageCrawler messageCrawler;

    private final PostProcessor postProcessor;

    private final ApplicationEventPublisher publisher;

    public PostCrawler(CrawlerConfig crawlerConfig, UrlBuilderUtil urlBuilderUtil, PostServiceImpl postService, MessageCrawler messageCrawler, PostProcessor postProcessor, ApplicationEventPublisher publisher) {
        this.crawlerConfig = crawlerConfig;
        this.urlBuilderUtil = urlBuilderUtil;
        this.postService = postService;
        this.messageCrawler = messageCrawler;
        this.postProcessor = postProcessor;
        this.publisher = publisher;
    }

    @Async("proxyTaskExecutor")
    public void doCrawl() {
        int allPagesCount = this.getAllPagesCount();

        for (int i = 1; i <= allPagesCount; i++) {
            this.processPage(i);
        }
    }


    /**
     * 获取Post的总页数（349）
     */
    public int getAllPagesCount() {
        JXDocument jxd = JXDocument.createByUrl(crawlerConfig.getStartUrl());
        JXNode span = jxd.selNOne("""
                //*[@id="fd_page_top"]/div/label/span""");

        String pageCountStr = span.asElement().ownText();
        Pattern pattern = Pattern.compile("(\\d+)");
        Matcher matcher = pattern.matcher(pageCountStr);
        if (matcher.find()) {
            String group = matcher.group();
            return Integer.parseInt(group);
        } else {
            throw new RuntimeException("获取总页数时正则匹配失败");
        }
    }

    /**
     * 处理第page页的所有Post
     * 例如第一页：<a href="http://bbs.zhoushan.cn/forum-143-1.html">...</a>
     */
    @Async("proxyTaskExecutor")
    public void processPage(int page) {
        String url = urlBuilderUtil.getPostPageUrl(page);
        JXDocument jxd = JXDocument.createByUrl(url);

        publisher.publishEvent(new PostEvent(this, jxd, page));
    }

    @EventListener
    @Async("proxyTaskExecutor")
    public void onApplicationEvent(PostEvent event) {
        JXDocument jxd = event.getJxd();
        int page = event.getPage();

        JXNode postsTable = jxd.selNOne("""
                //*[@id="moderate"]/table""");
        List<JXNode> tbodys = postsTable.sel("""
                .//tbody[starts-with(@id, "normalthread")]""");

        List<Post> posts = new ArrayList<>();
        for (int i = 1; i < tbodys.size(); i++) {
            JXNode tbody = tbodys.get(i - 1);

            try {
                Post post = postProcessor.parseTbodyNodeToPost(tbody);
                posts.add(post);
                logger.info("post:第{}页,其标题为：{}", page, post.getTitle());

                messageCrawler.doCrawl(post.getPostId());
            } catch (Exception e) {
                logger.error("解析Post的tbody时出错，第{}页第{}条", page, i, e);
            }
        }
        postService.saveOrUpdateBatch(posts);
    }

    @Component
    static class PostProcessor {
        private final DeptConverter deptConverter;
        private final TypeConverter typeConverter;

        private final Logger logger = LoggerFactory.getLogger(PostProcessor.class);

        PostProcessor(DeptConverter deptConverter, TypeConverter typeConverter) {
            this.deptConverter = deptConverter;
            this.typeConverter = typeConverter;
        }

        public Post parseTbodyNodeToPost(JXNode tbody) {

            int bbsId = this.getBBSId(tbody);

            Type type = this.getType(tbody);
            Dept dept = this.getDept(tbody);
            String title = this.getTitle(tbody);
            int publishUserId = this.getPublishUserID(tbody);
            Date publishDate = this.getPublishDate(tbody);
            int replyCount = this.getReplyCount(tbody);
            int checkCount = this.getCheckCount(tbody);
            PostStatus postStatus = this.getPostStatus(tbody);
            return new Post(bbsId, type.getTypeId(), dept.getDeptId(), title, publishUserId, publishDate, replyCount, checkCount, postStatus);
        }

        /**
         * 获取BBS数据库中，每条Post的ID
         */
        private int getBBSId(JXNode tbody) {
            JXNode aOfTitleNode = tbody.selOne("./tr/th/a");

            String href = aOfTitleNode.asElement().attr("href");
            Pattern pattern = Pattern.compile("thread-(?<bbsID>\\d+)-");
            Matcher matcher = pattern.matcher(href);
            if (matcher.find()) {
                String bbsID = matcher.group("bbsID");
                return Integer.parseInt(bbsID);
            } else {
                throw new RuntimeException("无法解析出BBS数据库中Post的ID");
            }
        }

        /**
         * 获取PostBody中的Type信息，如”投诉“
         */
        private Type getType(JXNode tbody) {
            JXNode aOfTypeNode = tbody.selOne("./tr/th/em[1]/a");

            String typeName = aOfTypeNode.asElement().ownText();
            int typeId = typeConverter.getTypeId(typeName);

            return new Type(typeId, typeName);
        }

        /**
         * 获取PostBody中的Dept信息，如”市财政局“
         */
        private Dept getDept(JXNode tbody) {
            JXNode aOfDeptNode = tbody.selOne("./tr/th/em[2]/a");
            if (aOfDeptNode == null) {
                int deptId = deptConverter.getDeptId(DeptConverter.UNKNOWN_DEPT_NAME);
                return new Dept(deptId, DeptConverter.UNKNOWN_DEPT_NAME);
            }

            String deptName = aOfDeptNode.asElement().ownText();
            int deptId = deptConverter.getDeptId(deptName);

            return new Dept(deptId, deptName);
        }

        /**
         * 获取PostBody中的Title信息
         */
        private String getTitle(JXNode tbody) {
            JXNode aOfTitleNode = tbody.selOne("./tr/th/a");

            return aOfTitleNode.asElement().ownText();
        }

        /**
         * 获取PostBody中的发表者的UID信息
         */
        private int getPublishUserID(JXNode tbody) {
            JXNode aOfAuthorNode = tbody.selOne("./tr/td[2]/cite/a");

            String userSpaceUrl = aOfAuthorNode.asElement().attr("href");
            Pattern pattern = Pattern.compile("uid-(?<uid>\\d+).html");
            Matcher matcher = pattern.matcher(userSpaceUrl);
            if (matcher.find()) {
                String uid = matcher.group("uid");
                return Integer.parseInt(uid);
            } else {
                throw new RuntimeException("无法解析出BBS数据库中Post发表者的UID");
            }
        }

        /**
         * 获取PostBody中的发布日期信息
         */
        private Date getPublishDate(JXNode tbody) {
            JXNode spanOfPublishDateNode = tbody.selOne("./tr/td[2]/em/span/span");

            String dateStr;
            if (spanOfPublishDateNode != null) {
                // 日期信息存储在span/span的title属性中
                dateStr = spanOfPublishDateNode.asElement().attr("title");
            } else {
                // 日期信息存储在span的text中
                spanOfPublishDateNode = tbody.selOne("./tr/td[2]/em/span");
                dateStr = spanOfPublishDateNode.asElement().ownText();
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                return dateFormat.parse(dateStr);
            } catch (ParseException e) {
                logger.error("发布日期解析出错", e);
                throw new RuntimeException(e);
            }
        }

        /**
         * 获取PostBody中的回复次数
         */
        private int getReplyCount(JXNode tbody) {
            JXNode aOfReplyCountNode = tbody.selOne("./tr/td[3]/a");
            String CountStr = aOfReplyCountNode.asElement().ownText();
            return Integer.parseInt(CountStr);
        }

        /**
         * 获取PostBody中的查看次数
         */
        private int getCheckCount(JXNode tbody) {
            JXNode emOfCheckCountNode = tbody.selOne("./tr/td[3]/em");
            String CountStr = emOfCheckCountNode.asElement().ownText();
            return Integer.parseInt(CountStr);
        }

        /**
         * 获取PostBody中的状态信息
         * 例如：已回复、不受理等。见PostStatus
         */
        private PostStatus getPostStatus(JXNode tbody) {
            JXNode imgOfStatusNode = tbody.selOne("./tr/th/img");

            if (imgOfStatusNode == null) {
                return PostStatus.UNKNOWN;
            } else {
                String altOfStatusNode = imgOfStatusNode.asElement().attr("alt");
                return PostStatus.PostStatusBuilder.getPostStatusByName(altOfStatusNode);
            }
        }
    }
}
