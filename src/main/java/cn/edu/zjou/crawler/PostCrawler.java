package cn.edu.zjou.crawler;

import cn.edu.zjou.config.CrawlerConfig;
import cn.edu.zjou.enums.PostStatus;
import cn.edu.zjou.po.Dept;
import cn.edu.zjou.po.Type;
import cn.edu.zjou.util.DeptConverter;
import cn.edu.zjou.util.TypeConverter;
import cn.edu.zjou.util.UrlBuilderUtil;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 负责
 */
@Component
public class PostCrawler {

    private final Logger logger = LoggerFactory.getLogger(PostCrawler.class);
    private final CrawlerConfig crawlerConfig;
    private final UrlBuilderUtil urlBuilderUtil;

    private final DeptConverter deptConverter;
    private final TypeConverter typeConverter;

    public PostCrawler(CrawlerConfig crawlerConfig, UrlBuilderUtil urlBuilderUtil, DeptConverter deptConverter, TypeConverter typeConverter) {
        this.crawlerConfig = crawlerConfig;
        this.urlBuilderUtil = urlBuilderUtil;
        this.deptConverter = deptConverter;
        this.typeConverter = typeConverter;
    }

    /**
     * 获取Post的总页数（349）
     */
    public int getPagesCount() {
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
            throw new RuntimeException("正则匹配失败");
        }
    }

    /**
     * 处理第page页的所有Post
     * 例如第一页：<a href="http://bbs.zhoushan.cn/forum-143-1.html">...</a>
     */
    public void processPage(int page) {
        String url = urlBuilderUtil.getPostPageUrl(page);
        JXDocument jxd = JXDocument.createByUrl(url);

        JXNode postsTable = jxd.selNOne("""
                //*[@id="moderate"]/table""");
        List<JXNode> tbodys = postsTable.sel("""
                .//tbody[starts-with(@id, "normalthread")]""");

        for (var tbody : tbodys) {
            int bbsId = this.getBBSId(tbody);
            Type type = this.getType(tbody);
            Dept dept = this.getDept(tbody);
            String title = this.getTitle(tbody);

            // TODO

            logger.debug("{},{},{},{}",bbsId,type.getTypeName(),dept.getDeptName(),title);
        }
    }

    /**
     * 获取BBS数据库中，每条Post的ID
     */
    private int getBBSId(JXNode tbody) {
        JXNode aOfTitleNode = tbody.selOne("""
                ./tr/th/a""");

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
        JXNode aOfTypeNode = tbody.selOne("""
                ./tr/th/em[1]/a""");

        String typeName = aOfTypeNode.asElement().ownText();
        int typeId = typeConverter.getTypeId(typeName);

        return new Type(typeId, typeName);
    }

    /**
     * 获取PostBody中的Dept信息，如”市财政局“
     */
    private Dept getDept(JXNode tbody) {
        JXNode aOfDeptNode = tbody.selOne("""
                ./tr/th/em[2]/a""");

        String deptName = aOfDeptNode.asElement().ownText();
        int deptId = deptConverter.getDeptId(deptName);

        return new Dept(deptId, deptName);
    }

    /**
     * 获取PostBody中的Title信息
     */
    private String getTitle(JXNode tbody) {
        JXNode aOfTitleNode = tbody.selOne("""
                ./tr/th/a""");

        return aOfTitleNode.asElement().ownText();
    }


/*
    private int getPublishUserID(JXNode tbody) {
        // TODO
    }

    private Date getPublishDate(JXNode tbody) {
        // TODO
    }

    private int getReplyCount(JXNode tbody) {
        // TODO
    }

    private int getCheckCount(JXNode tbody) {
        // TODO
    }

    private List<PostStatus> getPostStatus(JXNode tbody) {
        // TODO
    }
*/


}
