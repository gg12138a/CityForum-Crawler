package cn.edu.zjou.util;

import org.springframework.stereotype.Component;

/**
 * @author G_xy
 */
@Component
public class UrlBuilderUtil {

    public String getPostPageUrl(int page) {
        if (page < 1) {
            throw new IllegalArgumentException("页面应大于等于1");
        }

        return "http://bbs.zhoushan.cn/forum-143-" + page + ".html";
    }

    public String getUserBaseUrlByUid(int uid) {
        return "http://bbs.zhoushan.cn/space-uid-" + uid + ".html";
    }
}
