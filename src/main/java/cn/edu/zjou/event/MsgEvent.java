package cn.edu.zjou.event;

import cn.edu.zjou.crawler.MessageCrawler;
import lombok.Getter;
import lombok.Setter;
import org.seimicrawler.xpath.JXDocument;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class MsgEvent extends ApplicationEvent {

    private JXDocument jxd;

    private int postId;
    private int page;

    public MsgEvent(Object source, JXDocument jxd, int postId, int page) {
        super(source);
        this.jxd = jxd;
        this.postId = postId;
        this.page = page;
    }
}
