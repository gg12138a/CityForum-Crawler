package cn.edu.zjou.crawler;

import cn.edu.zjou.enums.MsgType;
import cn.edu.zjou.event.MsgEvent;
import cn.edu.zjou.po.Msg;
import cn.edu.zjou.service.impl.MsgServiceImpl;
import cn.edu.zjou.service.impl.UserServiceImpl;
import cn.edu.zjou.util.UrlBuilderUtil;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author G_xy
 */
@Component
public class MessageCrawler {

    private final MsgProcessor msgProcessor;

    public MessageCrawler(MsgProcessor msgProcessor) {
        this.msgProcessor = msgProcessor;
    }

    public void doCrawl(int postId) {

        int allPagesCount = msgProcessor.getAllPagesCount(postId);
        for (int page = 1; page <= allPagesCount; page++) {
            msgProcessor.processPage(postId, page);
        }
    }

    /**
     * 负责处理某一页面下的所有Message
     */
    @Component
    static class MsgProcessor {

        private final Logger logger = LoggerFactory.getLogger(MsgProcessor.class);
        private final UrlBuilderUtil urlBuilderUtil;
        private final MsgServiceImpl msgService;
        private final ApplicationEventPublisher publisher;
        private final UserCrawler userCrawler;


        public MsgProcessor(UrlBuilderUtil urlBuilderUtil, MsgServiceImpl msgService, ApplicationEventPublisher publisher, UserCrawler userCrawler) {
            this.urlBuilderUtil = urlBuilderUtil;
            this.msgService = msgService;
            this.publisher = publisher;
            this.userCrawler = userCrawler;
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

        /**
         * 处理帖子的第page页的所有Msg
         */
        @Async("proxyTaskExecutor")
        public void processPage(int postId, int page) {
            String detailPageUrl = urlBuilderUtil.getPostDetailPageUrl(postId, page);

            JXDocument jxd = JXDocument.createByUrl(detailPageUrl);
            publisher.publishEvent(new MsgEvent(this, jxd, postId, page));
        }

        @EventListener
        @Async("proxyTaskExecutor")
        public void onApplicationEvent(MsgEvent event) {
            JXDocument jxd = event.getJxd();
            int page = event.getPage();
            int postId = event.getPostId();

            List<JXNode> tableNodes = jxd.selN("""
                    //table[starts-with(@id, "pid") and @class="plhin"]""");

            List<Msg> msgs = new ArrayList<>();

            for (int i = 1; i <= tableNodes.size(); i++) {
                try {
                    JXNode table = tableNodes.get(i - 1);
                    Msg msg = this.parseTableNodeToMsg(table, postId, page);
                    if (!StringUtils.hasText(msg.getContent())) {// 该用户被封禁
                        continue;
                    }
                    if (i == 1) {
                        msg.setMsgType(MsgType.MAIN);
                    }
                    msgs.add(msg);

                    userCrawler.processUser(msg.getUserId());
                } catch (Exception e) {
                    logger.error("解析Msg的table出错，postId为{},第{}页第{}条", postId, page, i, e);
                }

            }
            msgService.saveOrUpdateBatch(msgs);
        }

        public Msg parseTableNodeToMsg(JXNode table, int postId, int page) {
            int msgId = this.getMsgId(table);
            int publishUserId = this.getPublishUserId(table);
            String content = this.getContent(table);
            Date releaseDate = this.getReleaseDate(table);  // end
            MsgType msgType = this.getMsgType(table);

            logger.info("Msg:第{}页,其内容为:{},postId为:{}", page, content, postId);

            return new Msg(msgId, publishUserId, postId, content, releaseDate, page, msgType);
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

        public MsgType getMsgType(JXNode table) {
            JXNode aOfUserNode = table.selOne("""
                    .//div[@class="authi"]/a""");

            String styleOfUserNode = aOfUserNode.asElement().attr("style");
            if (StringUtils.hasText(styleOfUserNode) && styleOfUserNode.contains("color: #FF0000")) {
                return MsgType.OFFICIAL;
            }

            return MsgType.COMMON;
        }

        // TODO? 没有必要的功能
//        public int getQuoteMsgId(JXNode table){
//
//        }

    }

}
