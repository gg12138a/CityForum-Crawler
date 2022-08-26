package cn.edu.zjou.crawler;

import cn.edu.zjou.po.Msg;
import cn.edu.zjou.service.impl.MsgServiceImpl;
import cn.edu.zjou.service.impl.UserServiceImpl;
import cn.edu.zjou.util.AsyncPageDownloader;
import cn.edu.zjou.util.UrlBuilderUtil;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author G_xy
 */
@Component
public class MessageCrawler {


    private final PageProcessor pageProcessor;

    public MessageCrawler(PageProcessor pageProcessor) {
        this.pageProcessor = pageProcessor;
    }

    public void doCrawl(int postId) {
/*
        TODO
        int allPagesCount = pageProcessor.getAllPagesCount(postId);
        for (int page = 1; page <= allPagesCount; page++) {
            pageProcessor.processPage(postId, page);
        }
*/

        pageProcessor.processPage(postId, 1);
    }


    /**
     * 负责处理某一页面下的所有Message
     */
    @Component
    static class PageProcessor {

        private Logger logger = LoggerFactory.getLogger(PageProcessor.class);

        private final UrlBuilderUtil urlBuilderUtil;
        private final AsyncPageDownloader downloader;

        private final MsgServiceImpl msgService;
        private final UserServiceImpl userService;

        public PageProcessor(UrlBuilderUtil urlBuilderUtil, AsyncPageDownloader downloader, MsgServiceImpl msgService, UserServiceImpl userService) {
            this.urlBuilderUtil = urlBuilderUtil;
            this.downloader = downloader;
            this.msgService = msgService;
            this.userService = userService;
        }

        public int getAllPagesCount(int postId) {
            String detailPageUrl = urlBuilderUtil.getPostDetailPageUrl(postId, 1);
            JXDocument jxd = JXDocument.createByUrl(detailPageUrl);

            JXNode spanOfPageCountNode = jxd.selNOne("""
                    //*[@id="pgt"]/div/div/label/span""");
            if (spanOfPageCountNode == null) {
                return 1;
            }

            String title = spanOfPageCountNode.asElement().attr("title");

            Pattern pattern = Pattern.compile("(?<count>\\d+)");
            Matcher matcher = pattern.matcher(title);
            if (matcher.find()) {
                String group = matcher.group("count");
                return Integer.parseInt(group);
            } else {
                throw new RuntimeException("获取总页数时正则匹配失败");
            }
        }

        //        public int getAllPagesCountAsync(int postId) {
//            String detailPageUrl = urlBuilderUtil.getPostDetailPageUrl(postId, 1);
//            CompletableFuture<JXDocument> jxdFuture = downloader.downloadPage(detailPageUrl);
//
//            CompletableFuture<Integer> countFuture = jxdFuture.thenApplyAsync(jxd -> {
//                JXNode spanOfPageCountNode = jxd.selNOne("""
//                        //*[@id="pgt"]/div/div/label/span""");
//                if (spanOfPageCountNode == null) {
//                    return 1;
//                }
//
//                String title = spanOfPageCountNode.asElement().attr("title");
//
//                Pattern pattern = Pattern.compile("(?<count>\\d+)");
//                Matcher matcher = pattern.matcher(title);
//                if (matcher.find()) {
//                    String group = matcher.group("count");
//                    return Integer.parseInt(group);
//                } else {
//                    throw new RuntimeException("获取总页数时正则匹配失败");
//                }
//            });
//
//            return countFuture.join();
//        }


        /**
         * 处理posdId帖子的第page页的所有Msg
         */
        public void processPage(int postId, int page) {
            String detailPageUrl = urlBuilderUtil.getPostDetailPageUrl(postId, page);

            CompletableFuture<JXDocument> jxdFuture = downloader.downloadPageAsync(detailPageUrl);
            JXDocument jxd = jxdFuture.join();

            List<JXNode> tableNodes = jxd.selN("""
                    //table[starts-with(@id, "pid") and @class="plhin"]""");

            List<Msg> msgs = new ArrayList<>();

            for (var table : tableNodes) {
                int msgId = this.getMsgId(table);
                int publishUserId = this.getPublishUserId(table);
                String content = this.getContent(table);
                if (!StringUtils.hasText(content)) {// 该用户被封禁
                    continue;
                }
                Date releaseDate = this.getReleaseDate(table);  // end

                logger.info("Msg:第{}页,其内容为{},发表者uid为{}", page, content, publishUserId);

                msgs.add(new Msg(msgId, publishUserId, postId, content, releaseDate, page));
                userService.saveOrCrawlUser(publishUserId);
            }
            msgService.saveOrUpdateBatch(msgs);
        }

        public int getMsgId(JXNode table) {

            String idStr = table.asElement().attr("id");   // pid11802946
            Pattern pattern = Pattern.compile("(?<MsgId>\\d+)");
            Matcher matcher = pattern.matcher(idStr);
            if (matcher.find()) {
                String group = matcher.group("MsgId");
                return Integer.parseInt(group);
            } else {
                throw new RuntimeException("获取MsgId时正则匹配失败");
            }
        }

        public int getPublishUserId(JXNode table) {
            JXNode aOfUserNode = table.selOne("""
                    .//div[@class="authi"]/a""");

            String userSpaceUrl = aOfUserNode.asElement().attr("href");
            Pattern pattern = Pattern.compile("uid-(?<uid>\\d+).html");
            Matcher matcher = pattern.matcher(userSpaceUrl);
            if (matcher.find()) {
                String uid = matcher.group("uid");
                return Integer.parseInt(uid);
            } else {
                throw new RuntimeException("无法解析出BBS数据库中Msg发表者的UID");
            }
        }

        public String getContent(JXNode table) {
            JXNode tdOfContentNode = table.selOne("""
                    .//tbody/tr/td[@class="t_f"]""");

            if (tdOfContentNode == null) {
                JXNode locked = table.selOne("""
                        .//div[@class="pcb"]/div[@class="locked"]""");

                if (locked != null) {
                    return null;        // 该用户被封禁
                }
            }

            String content = tdOfContentNode.asElement().ownText();
            if (!StringUtils.hasText(content)) {     // 内容设置了样式，如字号
                List<JXNode> fontNodes = tdOfContentNode.sel("""
                        //font""");

                StringBuilder stringBuilder = new StringBuilder();
                for (var fontNode : fontNodes) {
                    stringBuilder.append(fontNode.asElement().ownText());
                }
                return stringBuilder.toString();
            }

            return content;
        }

        public Date getReleaseDate(JXNode table) {
            JXNode emOfDateNode = table.selOne("""
                    .//div[@class="authi"]/em[starts-with(@id, "authorposton")]""");

            JXNode spanOfDateNode = emOfDateNode.selOne("./span[@title]");

            String dateStr;
            if (spanOfDateNode != null) {
                dateStr = spanOfDateNode.asElement().attr("title");
            } else {
                String rawDateStr = emOfDateNode.asElement().ownText();
                Pattern pattern = Pattern.compile("发表于 (?<dateStr>.+)");
                Matcher matcher = pattern.matcher(rawDateStr);
                if (matcher.find()) {
                    dateStr = matcher.group("dateStr");
                } else {
                    throw new RuntimeException("获取dateStr时正则匹配失败");
                }
            }

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            try {
                return formatter.parse(dateStr);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        // TODO? 没有必要的功能
//        public int getQuoteMsgId(JXNode table){
//
//        }

    }

}
