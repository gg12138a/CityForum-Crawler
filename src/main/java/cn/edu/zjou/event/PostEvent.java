package cn.edu.zjou.event;

import cn.edu.zjou.crawler.PostCrawler;
import lombok.Getter;
import lombok.Setter;
import org.seimicrawler.xpath.JXDocument;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;

@Getter
@Setter
public class PostEvent extends ApplicationEvent {

    private JXDocument jxd;
    private int page;

    public PostEvent(Object source, JXDocument jxd, int page) {
        super(source);
        this.jxd = jxd;
        this.page = page;
    }
}
